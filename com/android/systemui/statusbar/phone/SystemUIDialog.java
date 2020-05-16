// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.util.concurrent.Executor;
import android.os.UserHandle;
import android.content.Intent;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.DialogInterface$OnClickListener;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.content.DialogInterface$OnDismissListener;
import android.content.DialogInterface;
import android.view.Window;
import android.view.WindowInsets$Type;
import android.view.WindowManager$LayoutParams;
import android.app.Dialog;
import com.android.systemui.R$style;
import android.content.Context;
import android.app.AlertDialog;

public class SystemUIDialog extends AlertDialog
{
    private final Context mContext;
    private final DismissReceiver mDismissReceiver;
    
    public SystemUIDialog(final Context context) {
        this(context, R$style.Theme_SystemUI_Dialog);
    }
    
    public SystemUIDialog(final Context mContext, final int n) {
        super(mContext, n);
        this.mContext = mContext;
        applyFlags(this);
        final WindowManager$LayoutParams attributes = this.getWindow().getAttributes();
        attributes.setTitle((CharSequence)this.getClass().getSimpleName());
        this.getWindow().setAttributes(attributes);
        this.mDismissReceiver = new DismissReceiver((Dialog)this);
    }
    
    public static AlertDialog applyFlags(final AlertDialog alertDialog) {
        final Window window = alertDialog.getWindow();
        window.setType(2017);
        window.addFlags(655360);
        window.getAttributes().setFitInsetsTypes(window.getAttributes().getFitInsetsTypes() & WindowInsets$Type.statusBars());
        return alertDialog;
    }
    
    public static void registerDismissListener(final Dialog dialog) {
        final DismissReceiver dismissReceiver = new DismissReceiver(dialog);
        dialog.setOnDismissListener((DialogInterface$OnDismissListener)new _$$Lambda$SystemUIDialog$aJwQFxZ3HhCkUHQluqH61p2yCMc(dismissReceiver));
        dismissReceiver.register();
    }
    
    public static void setShowForAllUsers(final Dialog dialog, final boolean b) {
        if (b) {
            final WindowManager$LayoutParams attributes = dialog.getWindow().getAttributes();
            attributes.privateFlags |= 0x10;
        }
        else {
            final WindowManager$LayoutParams attributes2 = dialog.getWindow().getAttributes();
            attributes2.privateFlags &= 0xFFFFFFEF;
        }
    }
    
    public static void setWindowOnTop(final Dialog dialog) {
        final Window window = dialog.getWindow();
        window.setType(2017);
        if (Dependency.get(KeyguardStateController.class).isShowing()) {
            window.getAttributes().setFitInsetsTypes(window.getAttributes().getFitInsetsTypes() & WindowInsets$Type.statusBars());
        }
    }
    
    protected void onStart() {
        super.onStart();
        this.mDismissReceiver.register();
    }
    
    protected void onStop() {
        super.onStop();
        this.mDismissReceiver.unregister();
    }
    
    public void setMessage(final int n) {
        this.setMessage((CharSequence)this.mContext.getString(n));
    }
    
    public void setNegativeButton(final int n, final DialogInterface$OnClickListener dialogInterface$OnClickListener) {
        this.setButton(-2, (CharSequence)this.mContext.getString(n), dialogInterface$OnClickListener);
    }
    
    public void setPositiveButton(final int n, final DialogInterface$OnClickListener dialogInterface$OnClickListener) {
        this.setButton(-1, (CharSequence)this.mContext.getString(n), dialogInterface$OnClickListener);
    }
    
    public void setShowForAllUsers(final boolean b) {
        setShowForAllUsers((Dialog)this, b);
    }
    
    private static class DismissReceiver extends BroadcastReceiver
    {
        private static final IntentFilter INTENT_FILTER;
        private final BroadcastDispatcher mBroadcastDispatcher;
        private final Dialog mDialog;
        private boolean mRegistered;
        
        static {
            (INTENT_FILTER = new IntentFilter()).addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
            DismissReceiver.INTENT_FILTER.addAction("android.intent.action.SCREEN_OFF");
        }
        
        DismissReceiver(final Dialog mDialog) {
            this.mDialog = mDialog;
            this.mBroadcastDispatcher = Dependency.get(BroadcastDispatcher.class);
        }
        
        public void onReceive(final Context context, final Intent intent) {
            this.mDialog.dismiss();
        }
        
        void register() {
            this.mBroadcastDispatcher.registerReceiver(this, DismissReceiver.INTENT_FILTER, null, UserHandle.CURRENT);
            this.mRegistered = true;
        }
        
        void unregister() {
            if (this.mRegistered) {
                this.mBroadcastDispatcher.unregisterReceiver(this);
                this.mRegistered = false;
            }
        }
    }
}
