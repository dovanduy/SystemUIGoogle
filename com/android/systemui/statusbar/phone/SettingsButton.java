// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.Interpolators;
import android.view.MotionEvent;
import android.animation.Animator;
import android.animation.Animator$AnimatorListener;
import android.animation.TimeInterpolator;
import android.view.animation.AnimationUtils;
import android.view.View;
import android.view.ViewConfiguration;
import android.util.AttributeSet;
import android.content.Context;
import android.animation.ObjectAnimator;
import com.android.keyguard.AlphaOptimizedImageButton;

public class SettingsButton extends AlphaOptimizedImageButton
{
    private ObjectAnimator mAnimator;
    private final Runnable mLongPressCallback;
    private float mSlop;
    private boolean mUpToSpeed;
    
    public SettingsButton(final Context context, final AttributeSet set) {
        super(context, set);
        this.mLongPressCallback = new Runnable() {
            @Override
            public void run() {
                SettingsButton.this.startAccelSpin();
            }
        };
        this.mSlop = (float)ViewConfiguration.get(this.getContext()).getScaledTouchSlop();
    }
    
    private void cancelAnimation() {
        final ObjectAnimator mAnimator = this.mAnimator;
        if (mAnimator != null) {
            mAnimator.removeAllListeners();
            this.mAnimator.cancel();
            this.mAnimator = null;
        }
    }
    
    private void cancelLongClick() {
        this.cancelAnimation();
        this.mUpToSpeed = false;
        this.removeCallbacks(this.mLongPressCallback);
    }
    
    private void startExitAnimation() {
        this.animate().translationX(((View)this.getParent().getParent()).getWidth() - this.getX()).alpha(0.0f).setDuration(350L).setInterpolator((TimeInterpolator)AnimationUtils.loadInterpolator(super.mContext, 17563650)).setListener((Animator$AnimatorListener)new Animator$AnimatorListener() {
            public void onAnimationCancel(final Animator animator) {
            }
            
            public void onAnimationEnd(final Animator animator) {
                SettingsButton.this.setAlpha(1.0f);
                SettingsButton.this.setTranslationX(0.0f);
                SettingsButton.this.cancelLongClick();
            }
            
            public void onAnimationRepeat(final Animator animator) {
            }
            
            public void onAnimationStart(final Animator animator) {
            }
        }).start();
    }
    
    public boolean isTunerClick() {
        return this.mUpToSpeed;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 1) {
            if (actionMasked != 2) {
                if (actionMasked == 3) {
                    this.cancelLongClick();
                }
            }
            else {
                final float x = motionEvent.getX();
                final float y = motionEvent.getY();
                final float mSlop = this.mSlop;
                if (x < -mSlop || y < -mSlop || x > this.getWidth() + this.mSlop || y > this.getHeight() + this.mSlop) {
                    this.cancelLongClick();
                }
            }
        }
        else if (this.mUpToSpeed) {
            this.startExitAnimation();
        }
        else {
            this.cancelLongClick();
        }
        return super.onTouchEvent(motionEvent);
    }
    
    protected void startAccelSpin() {
        this.cancelAnimation();
        (this.mAnimator = ObjectAnimator.ofFloat((Object)this, View.ROTATION, new float[] { 0.0f, 360.0f })).setInterpolator((TimeInterpolator)AnimationUtils.loadInterpolator(super.mContext, 17563648));
        this.mAnimator.setDuration(750L);
        this.mAnimator.addListener((Animator$AnimatorListener)new Animator$AnimatorListener() {
            public void onAnimationCancel(final Animator animator) {
            }
            
            public void onAnimationEnd(final Animator animator) {
                SettingsButton.this.startContinuousSpin();
            }
            
            public void onAnimationRepeat(final Animator animator) {
            }
            
            public void onAnimationStart(final Animator animator) {
            }
        });
        this.mAnimator.start();
    }
    
    protected void startContinuousSpin() {
        this.cancelAnimation();
        this.performHapticFeedback(0);
        this.mUpToSpeed = true;
        (this.mAnimator = ObjectAnimator.ofFloat((Object)this, View.ROTATION, new float[] { 0.0f, 360.0f })).setInterpolator((TimeInterpolator)Interpolators.LINEAR);
        this.mAnimator.setDuration(375L);
        this.mAnimator.setRepeatCount(-1);
        this.mAnimator.start();
    }
}
