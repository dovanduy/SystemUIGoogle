// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.glwallpaper;

import android.animation.Animator$AnimatorListener;
import android.util.Log;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.ValueAnimator;

class ImageRevealHelper
{
    private static final String TAG = "ImageRevealHelper";
    private final ValueAnimator mAnimator;
    private boolean mAwake;
    private float mReveal;
    private final RevealStateListener mRevealListener;
    
    ImageRevealHelper(final RevealStateListener mRevealListener) {
        this.mReveal = 0.0f;
        this.mAwake = false;
        this.mRevealListener = mRevealListener;
        (this.mAnimator = ValueAnimator.ofFloat(new float[0])).setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        this.mAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$ImageRevealHelper$F24215Snv58_ZInLQsaNs5JLH9M(this));
        this.mAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            private boolean mIsCanceled;
            
            public void onAnimationCancel(final Animator animator) {
                this.mIsCanceled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                if (!this.mIsCanceled && ImageRevealHelper.this.mRevealListener != null) {
                    Log.d(ImageRevealHelper.TAG, "transition end");
                    ImageRevealHelper.this.mRevealListener.onRevealEnd();
                }
                this.mIsCanceled = false;
            }
            
            public void onAnimationStart(final Animator animator) {
                if (ImageRevealHelper.this.mRevealListener != null) {
                    Log.d(ImageRevealHelper.TAG, "transition start");
                    ImageRevealHelper.this.mRevealListener.onRevealStart(true);
                }
            }
        });
    }
    
    public float getReveal() {
        return this.mReveal;
    }
    
    void updateAwake(final boolean b, final long n) {
        final String tag = ImageRevealHelper.TAG;
        final StringBuilder sb = new StringBuilder();
        sb.append("updateAwake: awake=");
        sb.append(b);
        sb.append(", duration=");
        sb.append(n);
        Log.d(tag, sb.toString());
        this.mAnimator.cancel();
        this.mAwake = b;
        float n2 = 0.0f;
        final float n3 = 0.0f;
        if (n == 0L) {
            float mReveal;
            if (b) {
                mReveal = n3;
            }
            else {
                mReveal = 1.0f;
            }
            this.mReveal = mReveal;
            this.mRevealListener.onRevealStart(false);
            this.mRevealListener.onRevealStateChanged();
            this.mRevealListener.onRevealEnd();
        }
        else {
            this.mAnimator.setDuration(n);
            final ValueAnimator mAnimator = this.mAnimator;
            final float mReveal2 = this.mReveal;
            if (!this.mAwake) {
                n2 = 1.0f;
            }
            mAnimator.setFloatValues(new float[] { mReveal2, n2 });
            this.mAnimator.start();
        }
    }
    
    public interface RevealStateListener
    {
        void onRevealEnd();
        
        void onRevealStart(final boolean p0);
        
        void onRevealStateChanged();
    }
}
