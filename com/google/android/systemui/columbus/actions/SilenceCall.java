// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.google.android.systemui.columbus.sensors.GestureSensor;
import android.content.ContentResolver;
import android.telephony.PhoneStateListener;
import kotlin.Unit;
import android.net.Uri;
import kotlin.jvm.functions.Function1;
import android.provider.Settings$Secure;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.telecom.TelecomManager;
import com.google.android.systemui.columbus.ColumbusContentObserver;

public final class SilenceCall extends Action
{
    private boolean isPhoneRinging;
    private final SilenceCall$phoneStateListener.SilenceCall$phoneStateListener$1 phoneStateListener;
    private final ColumbusContentObserver settingsObserver;
    private boolean silenceSettingEnabled;
    private final TelecomManager telecomManager;
    private final TelephonyManager telephonyManager;
    
    public SilenceCall(final Context context, final ColumbusContentObserver.Factory factory) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        super(context, null);
        this.telecomManager = (TelecomManager)context.getSystemService((Class)TelecomManager.class);
        this.telephonyManager = (TelephonyManager)context.getSystemService((Class)TelephonyManager.class);
        this.phoneStateListener = new SilenceCall$phoneStateListener.SilenceCall$phoneStateListener$1(this);
        final Uri uri = Settings$Secure.getUriFor("assist_gesture_silence_alerts_enabled");
        Intrinsics.checkExpressionValueIsNotNull(uri, "Settings.Secure.getUriFo\u2026E_SILENCE_ALERTS_ENABLED)");
        (this.settingsObserver = factory.create(uri, (Function1<? super Uri, Unit>)new SilenceCall$settingsObserver.SilenceCall$settingsObserver$1(this))).activate();
        this.updatePhoneStateListener();
    }
    
    private final boolean isPhoneRinging(final int n) {
        boolean b = true;
        if (n != 1) {
            b = false;
        }
        return b;
    }
    
    private final void updatePhoneStateListener() {
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean silenceSettingEnabled = true;
        final int intForUser = Settings$Secure.getIntForUser(contentResolver, "assist_gesture_silence_alerts_enabled", 1, -2);
        int n = 0;
        if (intForUser == 0) {
            silenceSettingEnabled = false;
        }
        if (silenceSettingEnabled != this.silenceSettingEnabled) {
            this.silenceSettingEnabled = silenceSettingEnabled;
            if (silenceSettingEnabled) {
                n = 32;
            }
            final TelephonyManager telephonyManager = this.telephonyManager;
            if (telephonyManager != null) {
                telephonyManager.listen((PhoneStateListener)this.phoneStateListener, n);
                this.isPhoneRinging = this.isPhoneRinging(telephonyManager.getCallState());
            }
            this.notifyListener();
        }
    }
    
    @Override
    public boolean isAvailable() {
        return this.silenceSettingEnabled && this.isPhoneRinging;
    }
    
    @Override
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        if (n == 3) {
            final TelecomManager telecomManager = this.telecomManager;
            if (telecomManager != null) {
                telecomManager.silenceRinger();
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [silenceSettingEnabled -> ");
        sb.append(this.silenceSettingEnabled);
        sb.append("]");
        return sb.toString();
    }
}
