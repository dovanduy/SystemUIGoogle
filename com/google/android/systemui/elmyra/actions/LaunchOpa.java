// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.actions;

import java.util.ArrayList;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import android.os.Bundle;
import android.content.ContentResolver;
import com.google.android.systemui.assist.AssistManagerGoogle;
import android.net.Uri;
import java.util.function.Consumer;
import android.provider.Settings$Secure;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.android.systemui.Dependency;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.assist.OpaEnabledListener;
import android.app.KeyguardManager;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.tuner.TunerService;

public class LaunchOpa extends Action implements Tunable
{
    private final AssistManager mAssistManager;
    private boolean mEnableForAnyAssistant;
    private boolean mIsGestureEnabled;
    private boolean mIsOpaEnabled;
    private final KeyguardManager mKeyguardManager;
    private final OpaEnabledListener mOpaEnabledListener;
    private final StatusBar mStatusBar;
    
    private LaunchOpa(final Context context, final StatusBar mStatusBar, final List<FeedbackEffect> list) {
        super(context, list);
        this.mOpaEnabledListener = new OpaEnabledListener() {
            @Override
            public void onOpaEnabledReceived(final Context context, final boolean b, final boolean b2, final boolean b3) {
                final boolean b4 = false;
                final boolean b5 = b2 || LaunchOpa.this.mEnableForAnyAssistant;
                boolean b6 = b4;
                if (b) {
                    b6 = b4;
                    if (b5) {
                        b6 = b4;
                        if (b3) {
                            b6 = true;
                        }
                    }
                }
                if (LaunchOpa.this.mIsOpaEnabled != b6) {
                    LaunchOpa.this.mIsOpaEnabled = b6;
                    LaunchOpa.this.notifyListener();
                }
            }
        };
        this.mStatusBar = mStatusBar;
        this.mAssistManager = Dependency.get(AssistManager.class);
        this.mKeyguardManager = (KeyguardManager)this.getContext().getSystemService("keyguard");
        this.mIsGestureEnabled = this.isGestureEnabled();
        new UserContentObserver(this.getContext(), Settings$Secure.getUriFor("assist_gesture_enabled"), new _$$Lambda$LaunchOpa$Z0JaMSicRwfMwFAmiKhALeNwbbw(this));
        Dependency.get(TunerService.class).addTunable((TunerService.Tunable)this, "assist_gesture_any_assistant");
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean mEnableForAnyAssistant = false;
        if (Settings$Secure.getInt(contentResolver, "assist_gesture_any_assistant", 0) == 1) {
            mEnableForAnyAssistant = true;
        }
        this.mEnableForAnyAssistant = mEnableForAnyAssistant;
        ((AssistManagerGoogle)this.mAssistManager).addOpaEnabledListener(this.mOpaEnabledListener);
    }
    
    private boolean isGestureEnabled() {
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean b = true;
        if (Settings$Secure.getIntForUser(contentResolver, "assist_gesture_enabled", 1, -2) == 0) {
            b = false;
        }
        return b;
    }
    
    private void updateGestureEnabled() {
        final boolean gestureEnabled = this.isGestureEnabled();
        if (this.mIsGestureEnabled != gestureEnabled) {
            this.mIsGestureEnabled = gestureEnabled;
            this.notifyListener();
        }
    }
    
    @Override
    public boolean isAvailable() {
        return this.mIsGestureEnabled && this.mIsOpaEnabled;
    }
    
    public void launchOpa() {
        this.launchOpa(0L);
    }
    
    public void launchOpa(final long n) {
        final Bundle bundle = new Bundle();
        int n2;
        if (this.mKeyguardManager.isKeyguardLocked()) {
            n2 = 14;
        }
        else {
            n2 = 13;
        }
        bundle.putInt("triggered_by", n2);
        bundle.putLong("latency_id", n);
        bundle.putInt("invocation_type", 2);
        this.mAssistManager.startAssist(bundle);
    }
    
    @Override
    public void onProgress(final float n, final int n2) {
        this.updateFeedbackEffects(n, n2);
    }
    
    @Override
    public void onTrigger(final GestureSensor.DetectionProperties detectionProperties) {
        this.mStatusBar.collapseShade();
        this.triggerFeedbackEffects(detectionProperties);
        long actionId;
        if (detectionProperties != null) {
            actionId = detectionProperties.getActionId();
        }
        else {
            actionId = 0L;
        }
        this.launchOpa(actionId);
    }
    
    @Override
    public void onTuningChanged(final String anObject, final String anObject2) {
        if ("assist_gesture_any_assistant".equals(anObject)) {
            this.mEnableForAnyAssistant = "1".equals(anObject2);
            ((AssistManagerGoogle)this.mAssistManager).dispatchOpaEnabledState();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mIsGestureEnabled -> ");
        sb.append(this.mIsGestureEnabled);
        sb.append("; mIsOpaEnabled -> ");
        sb.append(this.mIsOpaEnabled);
        sb.append("]");
        return sb.toString();
    }
    
    public static class Builder
    {
        private final Context mContext;
        List<FeedbackEffect> mFeedbackEffects;
        private final StatusBar mStatusBar;
        
        public Builder(final Context mContext, final StatusBar mStatusBar) {
            this.mFeedbackEffects = new ArrayList<FeedbackEffect>();
            this.mContext = mContext;
            this.mStatusBar = mStatusBar;
        }
        
        public Builder addFeedbackEffect(final FeedbackEffect feedbackEffect) {
            this.mFeedbackEffects.add(feedbackEffect);
            return this;
        }
        
        public LaunchOpa build() {
            return new LaunchOpa(this.mContext, this.mStatusBar, this.mFeedbackEffects, null);
        }
    }
}
