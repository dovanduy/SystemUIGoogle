// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.face;

import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

public class FaceNotificationBroadcastReceiver extends BroadcastReceiver
{
    private final Context mContext;
    
    FaceNotificationBroadcastReceiver(final Context mContext) {
        this.mContext = mContext;
    }
    
    public void onReceive(final Context context, final Intent intent) {
        final String action = intent.getAction();
        if (action == null) {
            Log.e("FaceNotificationBCR", "Received broadcast with null action.");
            this.mContext.unregisterReceiver((BroadcastReceiver)this);
            return;
        }
        int n = -1;
        if (action.hashCode() == -244988429) {
            if (action.equals("face_action_show_reenroll_dialog")) {
                n = 0;
            }
        }
        if (n == 0) {
            FaceNotificationDialogFactory.createReenrollDialog(this.mContext).show();
        }
        this.mContext.unregisterReceiver((BroadcastReceiver)this);
    }
}
