// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import android.app.ActivityManager;
import android.os.UserHandle;
import android.content.ComponentName;
import kotlin.jvm.internal.Intrinsics;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

public final class ControlsRequestReceiver extends BroadcastReceiver
{
    public static final Companion Companion;
    
    static {
        Companion = new Companion(null);
    }
    
    public void onReceive(final Context context, final Intent intent) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        final ComponentName componentName = (ComponentName)intent.getParcelableExtra("android.intent.extra.COMPONENT_NAME");
        String packageName;
        if (componentName != null) {
            packageName = componentName.getPackageName();
        }
        else {
            packageName = null;
        }
        if (packageName != null) {
            if (ControlsRequestReceiver.Companion.isPackageInForeground(context, packageName)) {
                final Intent intent2 = new Intent(context, (Class)ControlsRequestDialog.class);
                intent2.putExtra("android.intent.extra.COMPONENT_NAME", intent.getParcelableExtra("android.intent.extra.COMPONENT_NAME"));
                intent2.putExtra("android.service.controls.extra.CONTROL", intent.getParcelableExtra("android.service.controls.extra.CONTROL"));
                intent2.putExtra("android.intent.extra.USER_ID", context.getUserId());
                context.startActivityAsUser(intent2, UserHandle.SYSTEM);
            }
        }
    }
    
    public static final class Companion
    {
        private Companion() {
        }
        
        public final boolean isPackageInForeground(final Context context, final String str) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(str, "packageName");
            try {
                final int packageUid = context.getPackageManager().getPackageUid(str, 0);
                final ActivityManager activityManager = (ActivityManager)context.getSystemService((Class)ActivityManager.class);
                int uidImportance;
                if (activityManager != null) {
                    uidImportance = activityManager.getUidImportance(packageUid);
                }
                else {
                    uidImportance = 1000;
                }
                if (uidImportance != 100) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Uid ");
                    sb.append(packageUid);
                    sb.append(" not in foreground");
                    Log.w("ControlsRequestReceiver", sb.toString());
                    return false;
                }
                return true;
            }
            catch (PackageManager$NameNotFoundException ex) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Package ");
                sb2.append(str);
                sb2.append(" not found");
                Log.w("ControlsRequestReceiver", sb2.toString());
                return false;
            }
        }
    }
}
