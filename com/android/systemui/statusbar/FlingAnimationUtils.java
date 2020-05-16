// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.animation.TimeInterpolator;
import android.animation.Animator;
import android.util.Log;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.notification.NotificationUtils;
import android.util.DisplayMetrics;
import android.view.animation.PathInterpolator;

public class FlingAnimationUtils
{
    private AnimatorProperties mAnimatorProperties;
    private float mCachedStartGradient;
    private float mCachedVelocityFactor;
    private float mHighVelocityPxPerSecond;
    private PathInterpolator mInterpolator;
    private float mLinearOutSlowInX2;
    private float mMaxLengthSeconds;
    private float mMinVelocityPxPerSecond;
    private final float mSpeedUpFactor;
    private final float mY2;
    
    public FlingAnimationUtils(final DisplayMetrics displayMetrics, final float n) {
        this(displayMetrics, n, 0.0f);
    }
    
    public FlingAnimationUtils(final DisplayMetrics displayMetrics, final float n, final float n2) {
        this(displayMetrics, n, n2, -1.0f, 1.0f);
    }
    
    public FlingAnimationUtils(final DisplayMetrics displayMetrics, float density, final float mSpeedUpFactor, final float mLinearOutSlowInX2, final float my2) {
        this.mAnimatorProperties = new AnimatorProperties();
        this.mCachedStartGradient = -1.0f;
        this.mCachedVelocityFactor = -1.0f;
        this.mMaxLengthSeconds = density;
        this.mSpeedUpFactor = mSpeedUpFactor;
        if (mLinearOutSlowInX2 < 0.0f) {
            this.mLinearOutSlowInX2 = NotificationUtils.interpolate(0.35f, 0.68f, mSpeedUpFactor);
        }
        else {
            this.mLinearOutSlowInX2 = mLinearOutSlowInX2;
        }
        this.mY2 = my2;
        density = displayMetrics.density;
        this.mMinVelocityPxPerSecond = 250.0f * density;
        this.mHighVelocityPxPerSecond = density * 3000.0f;
    }
    
    private float calculateLinearOutFasterInY2(float max) {
        final float mMinVelocityPxPerSecond = this.mMinVelocityPxPerSecond;
        max = Math.max(0.0f, Math.min(1.0f, (max - mMinVelocityPxPerSecond) / (this.mHighVelocityPxPerSecond - mMinVelocityPxPerSecond)));
        return (1.0f - max) * 0.4f + max * 0.5f;
    }
    
    private AnimatorProperties getDismissingProperties(float n, float n2, float abs, float abs2) {
        final double n3 = this.mMaxLengthSeconds;
        n2 -= n;
        n = (float)(n3 * Math.pow(Math.abs(n2) / abs2, 0.5));
        abs2 = Math.abs(n2);
        abs = Math.abs(abs);
        final float calculateLinearOutFasterInY2 = this.calculateLinearOutFasterInY2(abs);
        n2 = calculateLinearOutFasterInY2 / 0.5f;
        final PathInterpolator interpolator = new PathInterpolator(0.0f, 0.0f, 0.5f, calculateLinearOutFasterInY2);
        n2 = n2 * abs2 / abs;
        if (n2 <= n) {
            this.mAnimatorProperties.interpolator = (Interpolator)interpolator;
            n = n2;
        }
        else if (abs >= this.mMinVelocityPxPerSecond) {
            this.mAnimatorProperties.interpolator = (Interpolator)new InterpolatorInterpolator((Interpolator)new VelocityInterpolator(n, abs, abs2), (Interpolator)interpolator, Interpolators.LINEAR_OUT_SLOW_IN);
        }
        else {
            this.mAnimatorProperties.interpolator = Interpolators.FAST_OUT_LINEAR_IN;
        }
        final AnimatorProperties mAnimatorProperties = this.mAnimatorProperties;
        mAnimatorProperties.duration = (long)(n * 1000.0f);
        return mAnimatorProperties;
    }
    
    private Interpolator getInterpolator(final float mCachedStartGradient, final float n) {
        if (Float.isNaN(n)) {
            Log.e("FlingAnimationUtils", "Invalid velocity factor", new Throwable());
            return Interpolators.LINEAR_OUT_SLOW_IN;
        }
        Label_0103: {
            if (mCachedStartGradient == this.mCachedStartGradient && n == this.mCachedVelocityFactor) {
                break Label_0103;
            }
            final float f = this.mSpeedUpFactor * (1.0f - n);
            final float f2 = f * mCachedStartGradient;
            final float mLinearOutSlowInX2 = this.mLinearOutSlowInX2;
            final float my2 = this.mY2;
            try {
                this.mInterpolator = new PathInterpolator(f, f2, mLinearOutSlowInX2, my2);
                this.mCachedStartGradient = mCachedStartGradient;
                this.mCachedVelocityFactor = n;
                return (Interpolator)this.mInterpolator;
            }
            catch (IllegalArgumentException cause) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Illegal path with x1=");
                sb.append(f);
                sb.append(" y1=");
                sb.append(f2);
                sb.append(" x2=");
                sb.append(mLinearOutSlowInX2);
                sb.append(" y2=");
                sb.append(my2);
                throw new IllegalArgumentException(sb.toString(), cause);
            }
        }
    }
    
    private AnimatorProperties getProperties(float min, float n, float mSpeedUpFactor, float abs) {
        final double n2 = this.mMaxLengthSeconds;
        min = n - min;
        n = (float)(n2 * Math.sqrt(Math.abs(min) / abs));
        abs = Math.abs(min);
        final float abs2 = Math.abs(mSpeedUpFactor);
        mSpeedUpFactor = this.mSpeedUpFactor;
        min = 1.0f;
        if (mSpeedUpFactor != 0.0f) {
            min = Math.min(abs2 / 3000.0f, 1.0f);
        }
        final float interpolate = NotificationUtils.interpolate(0.75f, this.mY2 / this.mLinearOutSlowInX2, min);
        mSpeedUpFactor = interpolate * abs / abs2;
        final Interpolator interpolator = this.getInterpolator(interpolate, min);
        if (mSpeedUpFactor <= n) {
            this.mAnimatorProperties.interpolator = interpolator;
            min = mSpeedUpFactor;
        }
        else if (abs2 >= this.mMinVelocityPxPerSecond) {
            this.mAnimatorProperties.interpolator = (Interpolator)new InterpolatorInterpolator((Interpolator)new VelocityInterpolator(n, abs2, abs), interpolator, Interpolators.LINEAR_OUT_SLOW_IN);
            min = n;
        }
        else {
            this.mAnimatorProperties.interpolator = Interpolators.FAST_OUT_SLOW_IN;
            min = n;
        }
        final AnimatorProperties mAnimatorProperties = this.mAnimatorProperties;
        mAnimatorProperties.duration = (long)(min * 1000.0f);
        return mAnimatorProperties;
    }
    
    public void apply(final Animator animator, final float n, final float n2, final float n3) {
        this.apply(animator, n, n2, n3, Math.abs(n2 - n));
    }
    
    public void apply(final Animator animator, final float n, final float n2, final float n3, final float n4) {
        final AnimatorProperties properties = this.getProperties(n, n2, n3, n4);
        animator.setDuration(properties.duration);
        animator.setInterpolator((TimeInterpolator)properties.interpolator);
    }
    
    public void applyDismissing(final Animator animator, final float n, final float n2, final float n3, final float n4) {
        final AnimatorProperties dismissingProperties = this.getDismissingProperties(n, n2, n3, n4);
        animator.setDuration(dismissingProperties.duration);
        animator.setInterpolator((TimeInterpolator)dismissingProperties.interpolator);
    }
    
    public float getMinVelocityPxPerSecond() {
        return this.mMinVelocityPxPerSecond;
    }
    
    private static class AnimatorProperties
    {
        long duration;
        Interpolator interpolator;
    }
    
    public static class Builder
    {
        private final DisplayMetrics mDisplayMetrics;
        float mMaxLengthSeconds;
        float mSpeedUpFactor;
        float mX2;
        float mY2;
        
        public Builder(final DisplayMetrics mDisplayMetrics) {
            this.mDisplayMetrics = mDisplayMetrics;
            this.reset();
        }
        
        public FlingAnimationUtils build() {
            return new FlingAnimationUtils(this.mDisplayMetrics, this.mMaxLengthSeconds, this.mSpeedUpFactor, this.mX2, this.mY2);
        }
        
        public Builder reset() {
            this.mMaxLengthSeconds = 0.0f;
            this.mSpeedUpFactor = 0.0f;
            this.mX2 = -1.0f;
            this.mY2 = 1.0f;
            return this;
        }
        
        public Builder setMaxLengthSeconds(final float mMaxLengthSeconds) {
            this.mMaxLengthSeconds = mMaxLengthSeconds;
            return this;
        }
        
        public Builder setSpeedUpFactor(final float mSpeedUpFactor) {
            this.mSpeedUpFactor = mSpeedUpFactor;
            return this;
        }
        
        public Builder setX2(final float mx2) {
            this.mX2 = mx2;
            return this;
        }
        
        public Builder setY2(final float my2) {
            this.mY2 = my2;
            return this;
        }
    }
    
    private static final class InterpolatorInterpolator implements Interpolator
    {
        private Interpolator mCrossfader;
        private Interpolator mInterpolator1;
        private Interpolator mInterpolator2;
        
        InterpolatorInterpolator(final Interpolator mInterpolator1, final Interpolator mInterpolator2, final Interpolator mCrossfader) {
            this.mInterpolator1 = mInterpolator1;
            this.mInterpolator2 = mInterpolator2;
            this.mCrossfader = mCrossfader;
        }
        
        public float getInterpolation(final float n) {
            final float interpolation = this.mCrossfader.getInterpolation(n);
            return (1.0f - interpolation) * this.mInterpolator1.getInterpolation(n) + interpolation * this.mInterpolator2.getInterpolation(n);
        }
    }
    
    private static final class VelocityInterpolator implements Interpolator
    {
        private float mDiff;
        private float mDurationSeconds;
        private float mVelocity;
        
        private VelocityInterpolator(final float mDurationSeconds, final float mVelocity, final float mDiff) {
            this.mDurationSeconds = mDurationSeconds;
            this.mVelocity = mVelocity;
            this.mDiff = mDiff;
        }
        
        public float getInterpolation(final float n) {
            return n * this.mDurationSeconds * this.mVelocity / this.mDiff;
        }
    }
}
