// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.feedback;

import android.os.SystemClock;
import com.google.android.systemui.columbus.sensors.GestureSensor;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.os.PowerManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;

public final class UserActivity implements FeedbackEffect
{
    private final KeyguardStateController keyguardStateController;
    private int lastStage;
    private final PowerManager powerManager;
    private int triggerCount;
    
    public UserActivity(final Context context, final KeyguardStateController keyguardStateController) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(keyguardStateController, "keyguardStateController");
        this.keyguardStateController = keyguardStateController;
        this.powerManager = (PowerManager)context.getSystemService((Class)PowerManager.class);
    }
    
    @Override
    public void onProgress(final int lastStage, final GestureSensor.DetectionProperties detectionProperties) {
        Label_0071: {
            if (lastStage != this.lastStage && lastStage == 1 && !this.keyguardStateController.isShowing()) {
                final PowerManager powerManager = this.powerManager;
                if (powerManager != null) {
                    powerManager.userActivity(SystemClock.uptimeMillis(), 0, 0);
                    ++this.triggerCount;
                    break Label_0071;
                }
            }
            if (lastStage == 3) {
                --this.triggerCount;
            }
        }
        this.lastStage = lastStage;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [triggerCount -> ");
        sb.append(this.triggerCount);
        sb.append("]");
        return sb.toString();
    }
}
