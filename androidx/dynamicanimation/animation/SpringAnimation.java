// 
// Decompiled by Procyon v0.5.36
// 

package androidx.dynamicanimation.animation;

import android.util.AndroidRuntimeException;
import android.os.Looper;

public final class SpringAnimation extends DynamicAnimation<SpringAnimation>
{
    private boolean mEndRequested;
    private float mPendingPosition;
    private SpringForce mSpring;
    
    public <K> SpringAnimation(final K k, final FloatPropertyCompat<K> floatPropertyCompat) {
        super(k, floatPropertyCompat);
        this.mSpring = null;
        this.mPendingPosition = Float.MAX_VALUE;
        this.mEndRequested = false;
    }
    
    private void sanityCheck() {
        final SpringForce mSpring = this.mSpring;
        if (mSpring == null) {
            throw new UnsupportedOperationException("Incomplete SpringAnimation: Either final position or a spring force needs to be set.");
        }
        final double n = mSpring.getFinalPosition();
        if (n > super.mMaxValue) {
            throw new UnsupportedOperationException("Final position of the spring cannot be greater than the max value.");
        }
        if (n >= super.mMinValue) {
            return;
        }
        throw new UnsupportedOperationException("Final position of the spring cannot be less than the min value.");
    }
    
    public void animateToFinalPosition(final float n) {
        if (this.isRunning()) {
            this.mPendingPosition = n;
        }
        else {
            if (this.mSpring == null) {
                this.mSpring = new SpringForce(n);
            }
            this.mSpring.setFinalPosition(n);
            this.start();
        }
    }
    
    public boolean canSkipToEnd() {
        return this.mSpring.mDampingRatio > 0.0;
    }
    
    @Override
    public void cancel() {
        super.cancel();
        final float mPendingPosition = this.mPendingPosition;
        if (mPendingPosition != Float.MAX_VALUE) {
            final SpringForce mSpring = this.mSpring;
            if (mSpring == null) {
                this.mSpring = new SpringForce(mPendingPosition);
            }
            else {
                mSpring.setFinalPosition(mPendingPosition);
            }
            this.mPendingPosition = Float.MAX_VALUE;
        }
    }
    
    public SpringForce getSpring() {
        return this.mSpring;
    }
    
    boolean isAtEquilibrium(final float n, final float n2) {
        return this.mSpring.isAtEquilibrium(n, n2);
    }
    
    public SpringAnimation setSpring(final SpringForce mSpring) {
        this.mSpring = mSpring;
        return this;
    }
    
    public void skipToEnd() {
        if (!this.canSkipToEnd()) {
            throw new UnsupportedOperationException("Spring animations can only come to an end when there is damping");
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (super.mRunning) {
                this.mEndRequested = true;
            }
            return;
        }
        throw new AndroidRuntimeException("Animations may only be started on the main thread");
    }
    
    @Override
    public void start() {
        this.sanityCheck();
        this.mSpring.setValueThreshold(this.getValueThreshold());
        super.start();
    }
    
    @Override
    boolean updateValueAndVelocity(long n) {
        if (this.mEndRequested) {
            final float mPendingPosition = this.mPendingPosition;
            if (mPendingPosition != Float.MAX_VALUE) {
                this.mSpring.setFinalPosition(mPendingPosition);
                this.mPendingPosition = Float.MAX_VALUE;
            }
            super.mValue = this.mSpring.getFinalPosition();
            super.mVelocity = 0.0f;
            this.mEndRequested = false;
            return true;
        }
        if (this.mPendingPosition != Float.MAX_VALUE) {
            final SpringForce mSpring = this.mSpring;
            final double n2 = super.mValue;
            final double n3 = super.mVelocity;
            n /= 2L;
            final MassState updateValues = mSpring.updateValues(n2, n3, n);
            this.mSpring.setFinalPosition(this.mPendingPosition);
            this.mPendingPosition = Float.MAX_VALUE;
            final MassState updateValues2 = this.mSpring.updateValues(updateValues.mValue, updateValues.mVelocity, n);
            super.mValue = updateValues2.mValue;
            super.mVelocity = updateValues2.mVelocity;
        }
        else {
            final MassState updateValues3 = this.mSpring.updateValues(super.mValue, super.mVelocity, n);
            super.mValue = updateValues3.mValue;
            super.mVelocity = updateValues3.mVelocity;
        }
        final float max = Math.max(super.mValue, super.mMinValue);
        super.mValue = max;
        final float min = Math.min(max, super.mMaxValue);
        super.mValue = min;
        if (this.isAtEquilibrium(min, super.mVelocity)) {
            super.mValue = this.mSpring.getFinalPosition();
            super.mVelocity = 0.0f;
            return true;
        }
        return false;
    }
}
