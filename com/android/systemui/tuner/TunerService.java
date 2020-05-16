// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import com.android.systemui.Dependency;
import android.content.BroadcastReceiver;
import android.content.pm.PackageManager$NameNotFoundException;
import android.os.UserHandle;
import android.app.ActivityManager;
import android.provider.Settings$Secure;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import com.android.systemui.R$string;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import android.content.pm.PackageManager;
import android.content.ComponentName;
import android.content.Context;

public abstract class TunerService
{
    public static final boolean isTunerEnabled(final Context context) {
        final int componentEnabledSetting = userContext(context).getPackageManager().getComponentEnabledSetting(new ComponentName(context, (Class)TunerActivity.class));
        boolean b = true;
        if (componentEnabledSetting != 1) {
            b = false;
        }
        return b;
    }
    
    public static boolean parseIntegerSwitch(final String s, final boolean b) {
        boolean b2 = b;
        if (s == null) {
            return b2;
        }
        try {
            b2 = (Integer.parseInt(s) != 0);
            return b2;
        }
        catch (NumberFormatException ex) {
            b2 = b;
            return b2;
        }
    }
    
    public static final void setTunerEnabled(final Context context, final boolean b) {
        final PackageManager packageManager = userContext(context).getPackageManager();
        final ComponentName componentName = new ComponentName(context, (Class)TunerActivity.class);
        int n;
        if (b) {
            n = 1;
        }
        else {
            n = 2;
        }
        packageManager.setComponentEnabledSetting(componentName, n, 1);
    }
    
    public static final void showResetRequest(final Context context, final Runnable runnable) {
        final SystemUIDialog systemUIDialog = new SystemUIDialog(context);
        systemUIDialog.setShowForAllUsers(true);
        systemUIDialog.setMessage(R$string.remove_from_settings_prompt);
        systemUIDialog.setButton(-2, (CharSequence)context.getString(R$string.cancel), (DialogInterface$OnClickListener)null);
        systemUIDialog.setButton(-1, (CharSequence)context.getString(R$string.guest_exit_guest_dialog_remove), (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                context.sendBroadcast(new Intent("com.android.systemui.action.CLEAR_TUNER"));
                TunerService.setTunerEnabled(context, false);
                Settings$Secure.putInt(context.getContentResolver(), "seen_tuner_warning", 0);
                final Runnable val$onDisabled = runnable;
                if (val$onDisabled != null) {
                    val$onDisabled.run();
                }
            }
        });
        systemUIDialog.show();
    }
    
    private static Context userContext(Context packageContextAsUser) {
        try {
            packageContextAsUser = packageContextAsUser.createPackageContextAsUser(packageContextAsUser.getPackageName(), 0, new UserHandle(ActivityManager.getCurrentUser()));
            return packageContextAsUser;
        }
        catch (PackageManager$NameNotFoundException ex) {
            return packageContextAsUser;
        }
    }
    
    public abstract void addTunable(final Tunable p0, final String... p1);
    
    public abstract void clearAll();
    
    public abstract int getValue(final String p0, final int p1);
    
    public abstract String getValue(final String p0);
    
    public abstract void removeTunable(final Tunable p0);
    
    public abstract void setValue(final String p0, final int p1);
    
    public abstract void setValue(final String p0, final String p1);
    
    public static class ClearReceiver extends BroadcastReceiver
    {
        public void onReceive(final Context context, final Intent intent) {
            if ("com.android.systemui.action.CLEAR_TUNER".equals(intent.getAction())) {
                Dependency.get(TunerService.class).clearAll();
            }
        }
    }
    
    public interface Tunable
    {
        void onTuningChanged(final String p0, final String p1);
    }
}
