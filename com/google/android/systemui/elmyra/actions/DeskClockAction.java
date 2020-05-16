// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.actions;

import android.content.ActivityNotFoundException;
import android.util.Log;
import android.os.Parcelable;
import android.app.ActivityOptions;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import android.os.Handler;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.net.Uri;
import java.util.function.Consumer;
import android.provider.Settings$Secure;
import com.google.android.systemui.elmyra.UserContentObserver;
import android.content.Intent;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import android.content.BroadcastReceiver;

abstract class DeskClockAction extends Action
{
    private boolean mAlertFiring;
    private final BroadcastReceiver mAlertReceiver;
    private boolean mReceiverRegistered;
    
    DeskClockAction(final Context context) {
        super(context, null);
        this.mAlertReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (intent.getAction().equals(DeskClockAction.this.getAlertAction())) {
                    DeskClockAction.this.mAlertFiring = true;
                }
                else if (intent.getAction().equals(DeskClockAction.this.getDoneAction())) {
                    DeskClockAction.this.mAlertFiring = false;
                }
                DeskClockAction.this.notifyListener();
            }
        };
        this.updateBroadcastReceiver();
        new UserContentObserver(this.getContext(), Settings$Secure.getUriFor("assist_gesture_silence_alerts_enabled"), new _$$Lambda$DeskClockAction$dyH9jy2GURTsOoYs4WoZlKMC29A(this));
    }
    
    private void updateBroadcastReceiver() {
        boolean b = false;
        this.mAlertFiring = false;
        if (this.mReceiverRegistered) {
            this.getContext().unregisterReceiver(this.mAlertReceiver);
            this.mReceiverRegistered = false;
        }
        if (Settings$Secure.getIntForUser(this.getContext().getContentResolver(), "assist_gesture_silence_alerts_enabled", 1, -2) != 0) {
            b = true;
        }
        if (b) {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(this.getAlertAction());
            intentFilter.addAction(this.getDoneAction());
            this.getContext().registerReceiverAsUser(this.mAlertReceiver, UserHandle.CURRENT, intentFilter, "com.android.systemui.permission.SEND_ALERT_BROADCASTS", (Handler)null);
            this.mReceiverRegistered = true;
        }
        this.notifyListener();
    }
    
    protected abstract Intent createDismissIntent();
    
    protected abstract String getAlertAction();
    
    protected abstract String getDoneAction();
    
    @Override
    public boolean isAvailable() {
        return this.mAlertFiring;
    }
    
    @Override
    public void onTrigger(final GestureSensor.DetectionProperties detectionProperties) {
        try {
            final Intent dismissIntent = this.createDismissIntent();
            final ActivityOptions basic = ActivityOptions.makeBasic();
            basic.setDisallowEnterPictureInPictureWhileLaunching(true);
            dismissIntent.setFlags(268435456);
            final StringBuilder sb = new StringBuilder();
            sb.append("android-app://");
            sb.append(this.getContext().getPackageName());
            dismissIntent.putExtra("android.intent.extra.REFERRER", (Parcelable)Uri.parse(sb.toString()));
            this.getContext().startActivityAsUser(dismissIntent, basic.toBundle(), UserHandle.CURRENT);
        }
        catch (ActivityNotFoundException ex) {
            Log.e("Elmyra/DeskClockAction", "Failed to dismiss alert", (Throwable)ex);
        }
        this.mAlertFiring = false;
        this.notifyListener();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mReceiverRegistered -> ");
        sb.append(this.mReceiverRegistered);
        sb.append("]");
        return sb.toString();
    }
}
