// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.actions;

import com.google.android.systemui.elmyra.sensors.GestureSensor;
import android.content.ContentResolver;
import android.util.Log;
import android.net.Uri;
import java.util.function.Consumer;
import android.provider.Settings$Secure;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.Optional;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;

public class UnpinNotifications extends Action
{
    private boolean mHasPinnedHeadsUp;
    private final OnHeadsUpChangedListener mHeadsUpChangedListener;
    private final Optional<HeadsUpManager> mHeadsUpManagerOptional;
    private boolean mSilenceSettingEnabled;
    
    public UnpinNotifications(final Context context, final Optional<HeadsUpManager> mHeadsUpManagerOptional) {
        super(context, null);
        this.mHeadsUpChangedListener = new OnHeadsUpChangedListener() {
            @Override
            public void onHeadsUpPinnedModeChanged(final boolean b) {
                if (UnpinNotifications.this.mHasPinnedHeadsUp != b) {
                    UnpinNotifications.this.mHasPinnedHeadsUp = b;
                    UnpinNotifications.this.notifyListener();
                }
            }
        };
        this.mHeadsUpManagerOptional = mHeadsUpManagerOptional;
        if (mHeadsUpManagerOptional.isPresent()) {
            this.updateHeadsUpListener();
            new UserContentObserver(this.getContext(), Settings$Secure.getUriFor("assist_gesture_silence_alerts_enabled"), new _$$Lambda$UnpinNotifications$Coju1I9MwFJHZmrlRAr_VaZtdE4(this));
        }
        else {
            Log.w("Elmyra/UnpinNotifications", "No HeadsUpManager");
        }
    }
    
    private void updateHeadsUpListener() {
        if (!this.mHeadsUpManagerOptional.isPresent()) {
            return;
        }
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean mSilenceSettingEnabled = true;
        if (Settings$Secure.getIntForUser(contentResolver, "assist_gesture_silence_alerts_enabled", 1, -2) == 0) {
            mSilenceSettingEnabled = false;
        }
        if (this.mSilenceSettingEnabled != mSilenceSettingEnabled) {
            this.mSilenceSettingEnabled = mSilenceSettingEnabled;
            if (mSilenceSettingEnabled) {
                this.mHasPinnedHeadsUp = this.mHeadsUpManagerOptional.get().hasPinnedHeadsUp();
                this.mHeadsUpManagerOptional.get().addListener(this.mHeadsUpChangedListener);
            }
            else {
                this.mHasPinnedHeadsUp = false;
                this.mHeadsUpManagerOptional.get().removeListener(this.mHeadsUpChangedListener);
            }
            this.notifyListener();
        }
    }
    
    @Override
    public boolean isAvailable() {
        return this.mSilenceSettingEnabled && this.mHasPinnedHeadsUp;
    }
    
    @Override
    public void onTrigger(final GestureSensor.DetectionProperties detectionProperties) {
        this.mHeadsUpManagerOptional.ifPresent((Consumer<? super HeadsUpManager>)_$$Lambda$UnpinNotifications$2AIQLXUnga6EFVuQA5J5PzBLz_w.INSTANCE);
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
}
