// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.actions;

import com.google.android.systemui.elmyra.sensors.GestureSensor;
import android.content.ContentResolver;
import android.net.Uri;
import java.util.function.Consumer;
import android.provider.Settings$Secure;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;

public class SilenceCall extends Action
{
    private boolean mIsPhoneRinging;
    private final PhoneStateListener mPhoneStateListener;
    private boolean mSilenceSettingEnabled;
    private final TelecomManager mTelecomManager;
    private final TelephonyManager mTelephonyManager;
    
    public SilenceCall(final Context context) {
        super(context, null);
        this.mPhoneStateListener = new PhoneStateListener() {
            public void onCallStateChanged(final int n, final String s) {
                final boolean access$000 = SilenceCall.this.isPhoneRinging(n);
                if (SilenceCall.this.mIsPhoneRinging != access$000) {
                    SilenceCall.this.mIsPhoneRinging = access$000;
                    SilenceCall.this.notifyListener();
                }
            }
        };
        this.mTelecomManager = (TelecomManager)context.getSystemService((Class)TelecomManager.class);
        this.mTelephonyManager = (TelephonyManager)context.getSystemService((Class)TelephonyManager.class);
        this.updatePhoneStateListener();
        new UserContentObserver(this.getContext(), Settings$Secure.getUriFor("assist_gesture_silence_alerts_enabled"), new _$$Lambda$SilenceCall$P91IyaoSIoRZpeDIyPp8173JrBg(this));
    }
    
    private boolean isPhoneRinging(final int n) {
        boolean b = true;
        if (n != 1) {
            b = false;
        }
        return b;
    }
    
    private void updatePhoneStateListener() {
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean mSilenceSettingEnabled = true;
        final int intForUser = Settings$Secure.getIntForUser(contentResolver, "assist_gesture_silence_alerts_enabled", 1, -2);
        int n = 0;
        if (intForUser == 0) {
            mSilenceSettingEnabled = false;
        }
        if (mSilenceSettingEnabled != this.mSilenceSettingEnabled) {
            this.mSilenceSettingEnabled = mSilenceSettingEnabled;
            if (mSilenceSettingEnabled) {
                n = 32;
            }
            this.mTelephonyManager.listen(this.mPhoneStateListener, n);
            this.mIsPhoneRinging = this.isPhoneRinging(this.mTelephonyManager.getCallState());
            this.notifyListener();
        }
    }
    
    @Override
    public boolean isAvailable() {
        return this.mSilenceSettingEnabled && this.mIsPhoneRinging;
    }
    
    @Override
    public void onTrigger(final GestureSensor.DetectionProperties detectionProperties) {
        this.mTelecomManager.silenceRinger();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mSilenceSettingEnabled -> ");
        sb.append(this.mSilenceSettingEnabled);
        sb.append("]");
        return sb.toString();
    }
}
