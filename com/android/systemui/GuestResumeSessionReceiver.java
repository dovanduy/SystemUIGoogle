// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import java.util.concurrent.Executor;
import android.os.UserHandle;
import android.content.IntentFilter;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.content.ContentResolver;
import android.provider.Settings$System;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.RemoteException;
import android.os.Bundle;
import android.view.WindowManagerGlobal;
import android.util.Log;
import android.app.ActivityManager;
import android.os.UserManager;
import android.content.Context;
import android.app.Dialog;
import android.content.BroadcastReceiver;

public class GuestResumeSessionReceiver extends BroadcastReceiver
{
    private Dialog mNewSessionDialog;
    
    private void cancelDialog() {
        final Dialog mNewSessionDialog = this.mNewSessionDialog;
        if (mNewSessionDialog != null && mNewSessionDialog.isShowing()) {
            this.mNewSessionDialog.cancel();
            this.mNewSessionDialog = null;
        }
    }
    
    private static void wipeGuestSession(final Context context, final int i) {
        final UserManager userManager = (UserManager)context.getSystemService("user");
        try {
            final UserInfo currentUser = ActivityManager.getService().getCurrentUser();
            if (currentUser.id != i) {
                final StringBuilder sb = new StringBuilder();
                sb.append("User requesting to start a new session (");
                sb.append(i);
                sb.append(") is not current user (");
                sb.append(currentUser.id);
                sb.append(")");
                Log.w("GuestResumeSessionReceiver", sb.toString());
                return;
            }
            if (!currentUser.isGuest()) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("User requesting to start a new session (");
                sb2.append(i);
                sb2.append(") is not a guest");
                Log.w("GuestResumeSessionReceiver", sb2.toString());
                return;
            }
            if (!userManager.markGuestForDeletion(currentUser.id)) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("Couldn't mark the guest for deletion for user ");
                sb3.append(i);
                Log.w("GuestResumeSessionReceiver", sb3.toString());
                return;
            }
            final UserInfo guest = userManager.createGuest(context, currentUser.name);
            Label_0222: {
                if (guest != null) {
                    break Label_0222;
                }
                try {
                    Log.e("GuestResumeSessionReceiver", "Could not create new guest, switching back to system user");
                    ActivityManager.getService().switchUser(0);
                    userManager.removeUser(currentUser.id);
                    WindowManagerGlobal.getWindowManagerService().lockNow((Bundle)null);
                    return;
                    ActivityManager.getService().switchUser(guest.id);
                    userManager.removeUser(currentUser.id);
                }
                catch (RemoteException ex) {
                    Log.e("GuestResumeSessionReceiver", "Couldn't wipe session because ActivityManager or WindowManager is dead");
                }
            }
        }
        catch (RemoteException ex2) {
            Log.e("GuestResumeSessionReceiver", "Couldn't wipe session because ActivityManager is dead");
        }
    }
    
    public void onReceive(final Context context, final Intent obj) {
        if (!"android.intent.action.USER_SWITCHED".equals(obj.getAction())) {
            return;
        }
        this.cancelDialog();
        final int intExtra = obj.getIntExtra("android.intent.extra.user_handle", -10000);
        if (intExtra == -10000) {
            final StringBuilder sb = new StringBuilder();
            sb.append(obj);
            sb.append(" sent to ");
            sb.append("GuestResumeSessionReceiver");
            sb.append(" without EXTRA_USER_HANDLE");
            Log.e("GuestResumeSessionReceiver", sb.toString());
            return;
        }
        try {
            if (!ActivityManager.getService().getCurrentUser().isGuest()) {
                return;
            }
            final ContentResolver contentResolver = context.getContentResolver();
            if (Settings$System.getIntForUser(contentResolver, "systemui.guest_has_logged_in", 0, intExtra) != 0) {
                (this.mNewSessionDialog = (Dialog)new ResetSessionDialog(context, intExtra)).show();
            }
            else {
                Settings$System.putIntForUser(contentResolver, "systemui.guest_has_logged_in", 1, intExtra);
            }
        }
        catch (RemoteException ex) {}
    }
    
    public void register(final BroadcastDispatcher broadcastDispatcher) {
        broadcastDispatcher.registerReceiver(this, new IntentFilter("android.intent.action.USER_SWITCHED"), null, UserHandle.SYSTEM);
    }
    
    private static class ResetSessionDialog extends SystemUIDialog implements DialogInterface$OnClickListener
    {
        private final int mUserId;
        
        public ResetSessionDialog(final Context context, final int mUserId) {
            super(context);
            this.setTitle((CharSequence)context.getString(R$string.guest_wipe_session_title));
            this.setMessage((CharSequence)context.getString(R$string.guest_wipe_session_message));
            this.setCanceledOnTouchOutside(false);
            this.setButton(-2, (CharSequence)context.getString(R$string.guest_wipe_session_wipe), (DialogInterface$OnClickListener)this);
            this.setButton(-1, (CharSequence)context.getString(R$string.guest_wipe_session_dontwipe), (DialogInterface$OnClickListener)this);
            this.mUserId = mUserId;
        }
        
        public void onClick(final DialogInterface dialogInterface, final int n) {
            if (n == -2) {
                wipeGuestSession(this.getContext(), this.mUserId);
                this.dismiss();
            }
            else if (n == -1) {
                this.cancel();
            }
        }
    }
}
