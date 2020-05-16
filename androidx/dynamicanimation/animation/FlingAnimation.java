// 
// Decompiled by Procyon v0.5.36
// 

package androidx.dynamicanimation.animation;

public final class FlingAnimation extends DynamicAnimation<FlingAnimation>
{
    private final DragForce mFlingForce;
    
    public <K> FlingAnimation(final K k, final FloatPropertyCompat<K> floatPropertyCompat) {
        super(k, floatPropertyCompat);
        (this.mFlingForce = new DragForce()).setValueThreshold(this.getValueThreshold());
    }
    
    boolean isAtEquilibrium(final float n, final float n2) {
        return n >= super.mMaxValue || n <= super.mMinValue || this.mFlingForce.isAtEquilibrium(n, n2);
    }
    
    public FlingAnimation setFriction(final float frictionScalar) {
        if (frictionScalar > 0.0f) {
            this.mFlingForce.setFrictionScalar(frictionScalar);
            return this;
        }
        throw new IllegalArgumentException("Friction must be positive");
    }
    
    @Override
    public FlingAnimation setMaxValue(final float maxValue) {
        super.setMaxValue(maxValue);
        return this;
    }
    
    @Override
    public FlingAnimation setMinValue(final float minValue) {
        super.setMinValue(minValue);
        return this;
    }
    
    @Override
    public FlingAnimation setStartVelocity(final float startVelocity) {
        super.setStartVelocity(startVelocity);
        return this;
    }
    
    @Override
    boolean updateValueAndVelocity(final long n) {
        final MassState updateValueAndVelocity = this.mFlingForce.updateValueAndVelocity(super.mValue, super.mVelocity, n);
        final float mValue = updateValueAndVelocity.mValue;
        super.mValue = mValue;
        final float mVelocity = updateValueAndVelocity.mVelocity;
        super.mVelocity = mVelocity;
        final float mMinValue = super.mMinValue;
        if (mValue < mMinValue) {
            super.mValue = mMinValue;
            return true;
        }
        final float mMaxValue = super.mMaxValue;
        if (mValue > mMaxValue) {
            super.mValue = mMaxValue;
            return true;
        }
        return this.isAtEquilibrium(mValue, mVelocity);
    }
    
    static final class DragForce
    {
        private float mFriction;
        private final MassState mMassState;
        private float mVelocityThreshold;
        
        DragForce() {
            this.mFriction = -4.2f;
            this.mMassState = new MassState();
        }
        
        public boolean isAtEquilibrium(final float n, final float a) {
            return Math.abs(a) < this.mVelocityThreshold;
        }
        
        void setFrictionScalar(final float n) {
            this.mFriction = n * -4.2f;
        }
        
        void setValueThreshold(final float n) {
            this.mVelocityThreshold = n * 62.5f;
        }
        
        MassState updateValueAndVelocity(final float n, final float n2, final long n3) {
            final MassState mMassState = this.mMassState;
            final double n4 = n2;
            final float n5 = (float)n3;
            mMassState.mVelocity = (float)(n4 * Math.exp(n5 / 1000.0f * this.mFriction));
            final MassState mMassState2 = this.mMassState;
            final float mFriction = this.mFriction;
            mMassState2.mValue = (float)(n - n2 / mFriction + n2 / mFriction * Math.exp(mFriction * n5 / 1000.0f));
            final MassState mMassState3 = this.mMassState;
            if (this.isAtEquilibrium(mMassState3.mValue, mMassState3.mVelocity)) {
                this.mMassState.mVelocity = 0.0f;
            }
            return this.mMassState;
        }
    }
}
