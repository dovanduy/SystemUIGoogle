// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.feedback;

import com.google.android.systemui.elmyra.sensors.GestureSensor;
import android.os.SystemClock;
import com.android.systemui.Dependency;
import android.content.Context;
import android.os.PowerManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;

public class UserActivity implements FeedbackEffect
{
    private final KeyguardStateController mKeyguardStateController;
    private int mLastStage;
    private final PowerManager mPowerManager;
    private int mTriggerCount;
    
    public UserActivity(final Context context) {
        this.mTriggerCount = 0;
        this.mLastStage = 0;
        this.mKeyguardStateController = Dependency.get(KeyguardStateController.class);
        this.mPowerManager = (PowerManager)context.getSystemService((Class)PowerManager.class);
    }
    
    @Override
    public void onProgress(final float n, final int mLastStage) {
        if (mLastStage != this.mLastStage && mLastStage == 2 && !this.mKeyguardStateController.isShowing()) {
            final PowerManager mPowerManager = this.mPowerManager;
            if (mPowerManager != null) {
                mPowerManager.userActivity(SystemClock.uptimeMillis(), 0, 0);
                ++this.mTriggerCount;
            }
        }
        this.mLastStage = mLastStage;
    }
    
    @Override
    public void onRelease() {
    }
    
    @Override
    public void onResolve(final GestureSensor.DetectionProperties detectionProperties) {
        --this.mTriggerCount;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mTriggerCount -> ");
        sb.append(this.mTriggerCount);
        sb.append("]");
        return sb.toString();
    }
}
