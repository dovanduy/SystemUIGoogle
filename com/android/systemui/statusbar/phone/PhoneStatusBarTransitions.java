// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import com.android.systemui.R$id;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import android.animation.Animator;
import android.view.View;

public final class PhoneStatusBarTransitions extends BarTransitions
{
    private View mBattery;
    private Animator mCurrentAnimation;
    private final float mIconAlphaWhenOpaque;
    private View mLeftSide;
    private View mStatusIcons;
    
    public PhoneStatusBarTransitions(final PhoneStatusBarView phoneStatusBarView, final View view) {
        super(view, R$drawable.status_background);
        this.mIconAlphaWhenOpaque = phoneStatusBarView.getContext().getResources().getFraction(R$dimen.status_bar_icon_drawing_alpha, 1, 1);
        this.mLeftSide = phoneStatusBarView.findViewById(R$id.status_bar_left_side);
        this.mStatusIcons = phoneStatusBarView.findViewById(R$id.statusIcons);
        this.mBattery = phoneStatusBarView.findViewById(R$id.battery);
        this.applyModeBackground(-1, this.getMode(), false);
        this.applyMode(this.getMode(), false);
    }
    
    private void applyMode(final int n, final boolean b) {
        if (this.mLeftSide == null) {
            return;
        }
        final float nonBatteryClockAlpha = this.getNonBatteryClockAlphaFor(n);
        final float batteryClockAlpha = this.getBatteryClockAlpha(n);
        final Animator mCurrentAnimation = this.mCurrentAnimation;
        if (mCurrentAnimation != null) {
            mCurrentAnimation.cancel();
        }
        if (b) {
            final AnimatorSet mCurrentAnimation2 = new AnimatorSet();
            mCurrentAnimation2.playTogether(new Animator[] { (Animator)this.animateTransitionTo(this.mLeftSide, nonBatteryClockAlpha), (Animator)this.animateTransitionTo(this.mStatusIcons, nonBatteryClockAlpha), (Animator)this.animateTransitionTo(this.mBattery, batteryClockAlpha) });
            if (this.isLightsOut(n)) {
                mCurrentAnimation2.setDuration(1500L);
            }
            mCurrentAnimation2.start();
            this.mCurrentAnimation = (Animator)mCurrentAnimation2;
        }
        else {
            this.mLeftSide.setAlpha(nonBatteryClockAlpha);
            this.mStatusIcons.setAlpha(nonBatteryClockAlpha);
            this.mBattery.setAlpha(batteryClockAlpha);
        }
    }
    
    private float getBatteryClockAlpha(final int n) {
        float nonBatteryClockAlpha;
        if (this.isLightsOut(n)) {
            nonBatteryClockAlpha = 0.5f;
        }
        else {
            nonBatteryClockAlpha = this.getNonBatteryClockAlphaFor(n);
        }
        return nonBatteryClockAlpha;
    }
    
    private float getNonBatteryClockAlphaFor(final int n) {
        float mIconAlphaWhenOpaque;
        if (this.isLightsOut(n)) {
            mIconAlphaWhenOpaque = 0.0f;
        }
        else if (!this.isOpaque(n)) {
            mIconAlphaWhenOpaque = 1.0f;
        }
        else {
            mIconAlphaWhenOpaque = this.mIconAlphaWhenOpaque;
        }
        return mIconAlphaWhenOpaque;
    }
    
    private boolean isOpaque(final int n) {
        boolean b = true;
        if (n == 1 || n == 2 || n == 0 || n == 6) {
            b = false;
        }
        return b;
    }
    
    public ObjectAnimator animateTransitionTo(final View view, final float n) {
        return ObjectAnimator.ofFloat((Object)view, "alpha", new float[] { view.getAlpha(), n });
    }
    
    @Override
    protected void onTransition(final int n, final int n2, final boolean b) {
        super.onTransition(n, n2, b);
        this.applyMode(n2, b);
    }
}
