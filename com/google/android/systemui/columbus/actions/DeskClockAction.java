// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import android.content.ActivityNotFoundException;
import android.util.Log;
import android.os.Parcelable;
import android.app.ActivityOptions;
import com.google.android.systemui.columbus.sensors.GestureSensor;
import android.content.Intent;
import android.os.Handler;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import kotlin.Unit;
import android.net.Uri;
import kotlin.jvm.functions.Function1;
import android.provider.Settings$Secure;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import com.google.android.systemui.columbus.ColumbusContentObserver;

public abstract class DeskClockAction extends Action
{
    private boolean alertFiring;
    private final DeskClockAction$alertReceiver.DeskClockAction$alertReceiver$1 alertReceiver;
    private boolean receiverRegistered;
    private final ColumbusContentObserver settingsObserver;
    
    public DeskClockAction(final Context context, final ColumbusContentObserver.Factory factory) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        super(context, null);
        this.alertReceiver = new DeskClockAction$alertReceiver.DeskClockAction$alertReceiver$1(this);
        final Uri uri = Settings$Secure.getUriFor("assist_gesture_silence_alerts_enabled");
        Intrinsics.checkExpressionValueIsNotNull(uri, "Settings.Secure.getUriFo\u2026E_SILENCE_ALERTS_ENABLED)");
        (this.settingsObserver = factory.create(uri, (Function1<? super Uri, Unit>)new DeskClockAction$settingsObserver.DeskClockAction$settingsObserver$1(this))).activate();
        this.updateBroadcastReceiver();
    }
    
    private final void updateBroadcastReceiver() {
        boolean b = false;
        this.alertFiring = false;
        if (this.receiverRegistered) {
            this.getContext().unregisterReceiver((BroadcastReceiver)this.alertReceiver);
            this.receiverRegistered = false;
        }
        if (Settings$Secure.getIntForUser(this.getContext().getContentResolver(), "assist_gesture_silence_alerts_enabled", 1, -2) != 0) {
            b = true;
        }
        if (b) {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(this.getAlertAction());
            intentFilter.addAction(this.getDoneAction());
            this.getContext().registerReceiverAsUser((BroadcastReceiver)this.alertReceiver, UserHandle.CURRENT, intentFilter, "com.android.systemui.permission.SEND_ALERT_BROADCASTS", (Handler)null);
            this.receiverRegistered = true;
        }
        this.notifyListener();
    }
    
    protected abstract Intent createDismissIntent();
    
    protected abstract String getAlertAction();
    
    protected abstract String getDoneAction();
    
    @Override
    public boolean isAvailable() {
        return this.alertFiring;
    }
    
    @Override
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        if (n == 3) {
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
                Log.e("Columbus/DeskClockAction", "Failed to dismiss alert", (Throwable)ex);
            }
            this.alertFiring = false;
            this.notifyListener();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [receiverRegistered -> ");
        sb.append(this.receiverRegistered);
        sb.append("]");
        return sb.toString();
    }
}
