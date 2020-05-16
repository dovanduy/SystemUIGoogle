// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.feedback;

import com.google.android.systemui.columbus.sensors.GestureSensor;
import android.animation.Animator$AnimatorListener;
import android.view.animation.LinearInterpolator;
import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.animation.ValueAnimator;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.assist.AssistManager;
import java.util.concurrent.TimeUnit;
import com.google.android.systemui.assist.AssistManagerGoogle;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.AnimatorSet;

public final class AssistInvocationEffect implements FeedbackEffect
{
    private static final long DECAY_DURATION;
    private AnimatorSet animation;
    private final ValueAnimator$AnimatorUpdateListener animatorUpdateListener;
    private final AssistManagerGoogle assistManager;
    private float progress;
    
    static {
        DECAY_DURATION = TimeUnit.SECONDS.toMillis(5L);
    }
    
    public AssistInvocationEffect(final AssistManager assistManager) {
        Intrinsics.checkParameterIsNotNull(assistManager, "assistManager");
        AssistManager assistManager2 = assistManager;
        if (!(assistManager instanceof AssistManagerGoogle)) {
            assistManager2 = null;
        }
        this.assistManager = (AssistManagerGoogle)assistManager2;
        this.animatorUpdateListener = (ValueAnimator$AnimatorUpdateListener)new AssistInvocationEffect$animatorUpdateListener.AssistInvocationEffect$animatorUpdateListener$1(this);
        this.animation = new AnimatorSet();
    }
    
    private final void setProgress(final float progress, final boolean b, final boolean b2) {
        if (this.animation.isRunning()) {
            this.animation.cancel();
        }
        if (b) {
            this.animation = new AnimatorSet();
            final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { this.progress, progress });
            Intrinsics.checkExpressionValueIsNotNull(ofFloat, "toValueAnimator");
            ofFloat.setDuration(200L);
            ofFloat.setInterpolator((TimeInterpolator)new DecelerateInterpolator());
            ofFloat.addUpdateListener(this.animatorUpdateListener);
            this.animation.play((Animator)ofFloat);
            if (progress > 0.0f && b2) {
                final ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[] { progress, 0.0f });
                Intrinsics.checkExpressionValueIsNotNull(ofFloat2, "decayAnimator");
                ofFloat2.setDuration((long)(AssistInvocationEffect.DECAY_DURATION * progress));
                ofFloat2.setInterpolator((TimeInterpolator)new LinearInterpolator());
                ofFloat2.addUpdateListener(this.animatorUpdateListener);
                this.animation.play((Animator)ofFloat2).after((Animator)ofFloat);
            }
            else if (progress >= 1.0f) {
                this.animation.addListener((Animator$AnimatorListener)new AssistInvocationEffect$setProgress.AssistInvocationEffect$setProgress$1(this));
            }
            this.animation.start();
        }
        else {
            this.progress = progress;
            this.updateAssistManager();
        }
    }
    
    private final void updateAssistManager() {
        final AssistManagerGoogle assistManager = this.assistManager;
        if (assistManager != null) {
            assistManager.onInvocationProgress(2, this.progress);
        }
    }
    
    @Override
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        final AssistManagerGoogle assistManager = this.assistManager;
        if (assistManager != null && assistManager.shouldUseHomeButtonAnimations()) {
            return;
        }
        if (n != 1) {
            if (n != 3) {
                this.setProgress(0.0f, false, false);
            }
            else {
                this.setProgress(1.0f, true, false);
            }
        }
        else {
            this.setProgress(0.99f, true, true);
        }
    }
}
