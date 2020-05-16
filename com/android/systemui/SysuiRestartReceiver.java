// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.os.Process;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

public class SysuiRestartReceiver extends BroadcastReceiver
{
    public static String ACTION = "com.android.systemui.action.RESTART";
    
    public void onReceive(final Context context, final Intent intent) {
        if (SysuiRestartReceiver.ACTION.equals(intent.getAction())) {
            NotificationManager.from(context).cancel(intent.getData().toString().substring(10), 6);
            Process.killProcess(Process.myPid());
        }
    }
}
