// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import android.util.Property;
import android.animation.ObjectAnimator;
import android.util.MathUtils;
import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.animation.AnimatorSet;
import com.android.systemui.R$id;
import android.view.View;
import android.os.Handler;
import com.android.systemui.CornerHandleView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.phone.NavigationBarTransitions;

public class AssistHandleViewController implements DarkIntensityListener
{
    @VisibleForTesting
    boolean mAssistHintBlocked;
    private CornerHandleView mAssistHintLeft;
    private CornerHandleView mAssistHintRight;
    @VisibleForTesting
    boolean mAssistHintVisible;
    private int mBottomOffset;
    private Handler mHandler;
    
    public AssistHandleViewController(final Handler mHandler, final View view) {
        this.mAssistHintBlocked = false;
        this.mHandler = mHandler;
        this.mAssistHintLeft = (CornerHandleView)view.findViewById(R$id.assist_hint_left);
        this.mAssistHintRight = (CornerHandleView)view.findViewById(R$id.assist_hint_right);
    }
    
    private void fade(final View view, final boolean b, final boolean b2) {
        if (b) {
            view.animate().cancel();
            view.setAlpha(1.0f);
            view.setVisibility(0);
            final AnimatorSet set = new AnimatorSet();
            final Animator handleAnimator = this.getHandleAnimator(view, 0.0f, 1.1f, b2, 750L, (Interpolator)new PathInterpolator(0.0f, 0.45f, 0.67f, 1.0f));
            final PathInterpolator pathInterpolator = new PathInterpolator(0.33f, 0.0f, 0.67f, 1.0f);
            final Animator handleAnimator2 = this.getHandleAnimator(view, 1.1f, 0.97f, b2, 400L, (Interpolator)pathInterpolator);
            final Animator handleAnimator3 = this.getHandleAnimator(view, 0.97f, 1.02f, b2, 400L, (Interpolator)pathInterpolator);
            final Animator handleAnimator4 = this.getHandleAnimator(view, 1.02f, 1.0f, b2, 400L, (Interpolator)pathInterpolator);
            set.play(handleAnimator).before(handleAnimator2);
            set.play(handleAnimator2).before(handleAnimator3);
            set.play(handleAnimator3).before(handleAnimator4);
            set.start();
        }
        else {
            view.animate().cancel();
            view.animate().setInterpolator((TimeInterpolator)new AccelerateInterpolator(1.5f)).setDuration(250L).alpha(0.0f);
        }
    }
    
    private void hideAssistHandles() {
        this.mAssistHintLeft.setVisibility(8);
        this.mAssistHintRight.setVisibility(8);
        this.mAssistHintVisible = false;
    }
    
    Animator getHandleAnimator(final View view, float lerp, float n, final boolean b, final long duration, final Interpolator interpolator) {
        final float lerp2 = MathUtils.lerp(2.0f, 1.0f, lerp);
        final float lerp3 = MathUtils.lerp(2.0f, 1.0f, n);
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)view, View.SCALE_X, new float[] { lerp2, lerp3 });
        final ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat((Object)view, View.SCALE_Y, new float[] { lerp2, lerp3 });
        lerp = MathUtils.lerp(0.2f, 0.0f, lerp);
        final float lerp4 = MathUtils.lerp(0.2f, 0.0f, n);
        int n2;
        if (b) {
            n2 = -1;
        }
        else {
            n2 = 1;
        }
        final Property translation_X = View.TRANSLATION_X;
        n = (float)n2;
        final ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat((Object)view, translation_X, new float[] { n * lerp * view.getWidth(), n * lerp4 * view.getWidth() });
        final ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat((Object)view, View.TRANSLATION_Y, new float[] { lerp * view.getHeight() + this.mBottomOffset, lerp4 * view.getHeight() + this.mBottomOffset });
        final AnimatorSet set = new AnimatorSet();
        set.play((Animator)ofFloat).with((Animator)ofFloat2);
        set.play((Animator)ofFloat).with((Animator)ofFloat3);
        set.play((Animator)ofFloat).with((Animator)ofFloat4);
        set.setDuration(duration);
        set.setInterpolator((TimeInterpolator)interpolator);
        return (Animator)set;
    }
    
    @Override
    public void onDarkIntensity(final float n) {
        this.mAssistHintLeft.updateDarkness(n);
        this.mAssistHintRight.updateDarkness(n);
    }
    
    public void setAssistHintBlocked(final boolean mAssistHintBlocked) {
        if (!this.mHandler.getLooper().isCurrentThread()) {
            this.mHandler.post((Runnable)new _$$Lambda$AssistHandleViewController$ai_ftoeNqQkXVTgh21US20St6_A(this, mAssistHintBlocked));
            return;
        }
        this.mAssistHintBlocked = mAssistHintBlocked;
        if (this.mAssistHintVisible && mAssistHintBlocked) {
            this.hideAssistHandles();
        }
    }
    
    public void setAssistHintVisible(final boolean mAssistHintVisible) {
        if (!this.mHandler.getLooper().isCurrentThread()) {
            this.mHandler.post((Runnable)new _$$Lambda$AssistHandleViewController$UwVsLlCfHOnxZGhueV8hx6rT9Z4(this, mAssistHintVisible));
            return;
        }
        if (this.mAssistHintBlocked && mAssistHintVisible) {
            return;
        }
        if (this.mAssistHintVisible != mAssistHintVisible) {
            this.mAssistHintVisible = mAssistHintVisible;
            this.fade(this.mAssistHintLeft, mAssistHintVisible, true);
            this.fade(this.mAssistHintRight, this.mAssistHintVisible, false);
        }
    }
    
    public void setBottomOffset(final int mBottomOffset) {
        if (this.mBottomOffset != mBottomOffset) {
            this.mBottomOffset = mBottomOffset;
            if (this.mAssistHintVisible) {
                this.hideAssistHandles();
                this.setAssistHintVisible(true);
            }
        }
    }
}
