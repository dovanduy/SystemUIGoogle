// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.view.ViewAnimationUtils;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.graphics.drawable.TransitionDrawable;
import android.animation.Animator;

public class QSDetailClipper
{
    private Animator mAnimator;
    private final TransitionDrawable mBackground;
    private final View mDetail;
    private final AnimatorListenerAdapter mGoneOnEnd;
    private final Runnable mReverseBackground;
    private final AnimatorListenerAdapter mVisibleOnStart;
    
    public QSDetailClipper(final View mDetail) {
        this.mReverseBackground = new Runnable() {
            @Override
            public void run() {
                if (QSDetailClipper.this.mAnimator != null) {
                    QSDetailClipper.this.mBackground.reverseTransition((int)(QSDetailClipper.this.mAnimator.getDuration() * 0.35));
                }
            }
        };
        this.mVisibleOnStart = new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                QSDetailClipper.this.mAnimator = null;
            }
            
            public void onAnimationStart(final Animator animator) {
                QSDetailClipper.this.mDetail.setVisibility(0);
            }
        };
        this.mGoneOnEnd = new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                QSDetailClipper.this.mDetail.setVisibility(8);
                QSDetailClipper.this.mBackground.resetTransition();
                QSDetailClipper.this.mAnimator = null;
            }
        };
        this.mDetail = mDetail;
        this.mBackground = (TransitionDrawable)mDetail.getBackground();
    }
    
    public void animateCircularClip(final int a, final int a2, final boolean b, final Animator$AnimatorListener animator$AnimatorListener) {
        final Animator mAnimator = this.mAnimator;
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        final int a3 = this.mDetail.getWidth() - a;
        final int a4 = this.mDetail.getHeight() - a2;
        int min = 0;
        if (a < 0 || a3 < 0 || a2 < 0 || a4 < 0) {
            min = Math.min(Math.min(Math.min(Math.abs(a), Math.abs(a2)), Math.abs(a3)), Math.abs(a4));
        }
        final int n = a * a;
        final int n2 = a2 * a2;
        final double a5 = (int)Math.ceil(Math.sqrt(n + n2));
        final int n3 = a3 * a3;
        final double a6 = (int)Math.max(a5, Math.ceil(Math.sqrt(n2 + n3)));
        final int n4 = a4 * a4;
        final int n5 = (int)Math.max((int)Math.max(a6, Math.ceil(Math.sqrt(n3 + n4))), Math.ceil(Math.sqrt(n + n4)));
        if (b) {
            this.mAnimator = ViewAnimationUtils.createCircularReveal(this.mDetail, a, a2, (float)min, (float)n5);
        }
        else {
            this.mAnimator = ViewAnimationUtils.createCircularReveal(this.mDetail, a, a2, (float)n5, (float)min);
        }
        final Animator mAnimator2 = this.mAnimator;
        mAnimator2.setDuration((long)(mAnimator2.getDuration() * 1.5));
        if (animator$AnimatorListener != null) {
            this.mAnimator.addListener(animator$AnimatorListener);
        }
        if (b) {
            this.mBackground.startTransition((int)(this.mAnimator.getDuration() * 0.6));
            this.mAnimator.addListener((Animator$AnimatorListener)this.mVisibleOnStart);
        }
        else {
            this.mDetail.postDelayed(this.mReverseBackground, (long)(this.mAnimator.getDuration() * 0.65));
            this.mAnimator.addListener((Animator$AnimatorListener)this.mGoneOnEnd);
        }
        this.mAnimator.start();
    }
    
    public void showBackground() {
        this.mBackground.showSecondLayer();
    }
}
