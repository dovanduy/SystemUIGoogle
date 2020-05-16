// 
// Decompiled by Procyon v0.5.36
// 

package androidx.dynamicanimation.animation;

public final class SpringForce
{
    private double mDampedFreq;
    double mDampingRatio;
    private double mFinalPosition;
    private double mGammaMinus;
    private double mGammaPlus;
    private boolean mInitialized;
    private final DynamicAnimation.MassState mMassState;
    double mNaturalFreq;
    private double mValueThreshold;
    private double mVelocityThreshold;
    
    public SpringForce() {
        this.mNaturalFreq = Math.sqrt(1500.0);
        this.mDampingRatio = 0.5;
        this.mInitialized = false;
        this.mFinalPosition = Double.MAX_VALUE;
        this.mMassState = new DynamicAnimation.MassState();
    }
    
    public SpringForce(final float n) {
        this.mNaturalFreq = Math.sqrt(1500.0);
        this.mDampingRatio = 0.5;
        this.mInitialized = false;
        this.mFinalPosition = Double.MAX_VALUE;
        this.mMassState = new DynamicAnimation.MassState();
        this.mFinalPosition = n;
    }
    
    private void init() {
        if (this.mInitialized) {
            return;
        }
        if (this.mFinalPosition != Double.MAX_VALUE) {
            final double mDampingRatio = this.mDampingRatio;
            if (mDampingRatio > 1.0) {
                final double n = -mDampingRatio;
                final double mNaturalFreq = this.mNaturalFreq;
                this.mGammaPlus = n * mNaturalFreq + mNaturalFreq * Math.sqrt(mDampingRatio * mDampingRatio - 1.0);
                final double mDampingRatio2 = this.mDampingRatio;
                final double n2 = -mDampingRatio2;
                final double mNaturalFreq2 = this.mNaturalFreq;
                this.mGammaMinus = n2 * mNaturalFreq2 - mNaturalFreq2 * Math.sqrt(mDampingRatio2 * mDampingRatio2 - 1.0);
            }
            else if (mDampingRatio >= 0.0 && mDampingRatio < 1.0) {
                this.mDampedFreq = this.mNaturalFreq * Math.sqrt(1.0 - mDampingRatio * mDampingRatio);
            }
            this.mInitialized = true;
            return;
        }
        throw new IllegalStateException("Error: Final position of the spring must be set before the animation starts");
    }
    
    public float getDampingRatio() {
        return (float)this.mDampingRatio;
    }
    
    public float getFinalPosition() {
        return (float)this.mFinalPosition;
    }
    
    public float getStiffness() {
        final double mNaturalFreq = this.mNaturalFreq;
        return (float)(mNaturalFreq * mNaturalFreq);
    }
    
    public boolean isAtEquilibrium(final float n, final float a) {
        return Math.abs(a) < this.mVelocityThreshold && Math.abs(n - this.getFinalPosition()) < this.mValueThreshold;
    }
    
    public SpringForce setDampingRatio(final float n) {
        if (n >= 0.0f) {
            this.mDampingRatio = n;
            this.mInitialized = false;
            return this;
        }
        throw new IllegalArgumentException("Damping ratio must be non-negative");
    }
    
    public SpringForce setFinalPosition(final float n) {
        this.mFinalPosition = n;
        return this;
    }
    
    public SpringForce setStiffness(final float n) {
        if (n > 0.0f) {
            this.mNaturalFreq = Math.sqrt(n);
            this.mInitialized = false;
            return this;
        }
        throw new IllegalArgumentException("Spring stiffness constant must be positive.");
    }
    
    void setValueThreshold(double abs) {
        abs = Math.abs(abs);
        this.mValueThreshold = abs;
        this.mVelocityThreshold = abs * 62.5;
    }
    
    DynamicAnimation.MassState updateValues(double n, double n2, final long n3) {
        this.init();
        final double n4 = n3 / 1000.0;
        final double n5 = n - this.mFinalPosition;
        n = this.mDampingRatio;
        if (n > 1.0) {
            n = this.mGammaMinus;
            final double mGammaPlus = this.mGammaPlus;
            final double n6 = n5 - (n * n5 - n2) / (n - mGammaPlus);
            n2 = (n5 * n - n2) / (n - mGammaPlus);
            n = Math.pow(2.718281828459045, n * n4) * n6 + Math.pow(2.718281828459045, this.mGammaPlus * n4) * n2;
            final double mGammaMinus = this.mGammaMinus;
            final double pow = Math.pow(2.718281828459045, mGammaMinus * n4);
            final double mGammaPlus2 = this.mGammaPlus;
            n2 = n6 * mGammaMinus * pow + n2 * mGammaPlus2 * Math.pow(2.718281828459045, mGammaPlus2 * n4);
        }
        else if (n == 1.0) {
            n = this.mNaturalFreq;
            n2 += n * n5;
            final double n7 = n5 + n2 * n4;
            n = Math.pow(2.718281828459045, -n * n4) * n7;
            final double pow2 = Math.pow(2.718281828459045, -this.mNaturalFreq * n4);
            final double mNaturalFreq = this.mNaturalFreq;
            n2 = n2 * Math.pow(2.718281828459045, -mNaturalFreq * n4) + n7 * pow2 * -mNaturalFreq;
        }
        else {
            final double n8 = 1.0 / this.mDampedFreq;
            final double mNaturalFreq2 = this.mNaturalFreq;
            n2 = n8 * (n * mNaturalFreq2 * n5 + n2);
            n = Math.pow(2.718281828459045, -n * mNaturalFreq2 * n4) * (Math.cos(this.mDampedFreq * n4) * n5 + Math.sin(this.mDampedFreq * n4) * n2);
            final double mNaturalFreq3 = this.mNaturalFreq;
            final double n9 = -mNaturalFreq3;
            final double mDampingRatio = this.mDampingRatio;
            final double pow3 = Math.pow(2.718281828459045, -mDampingRatio * mNaturalFreq3 * n4);
            final double mDampedFreq = this.mDampedFreq;
            final double n10 = -mDampedFreq;
            final double sin = Math.sin(mDampedFreq * n4);
            final double mDampedFreq2 = this.mDampedFreq;
            n2 = n9 * n * mDampingRatio + pow3 * (n10 * n5 * sin + n2 * mDampedFreq2 * Math.cos(mDampedFreq2 * n4));
        }
        final DynamicAnimation.MassState mMassState = this.mMassState;
        mMassState.mValue = (float)(n + this.mFinalPosition);
        mMassState.mVelocity = (float)n2;
        return mMassState;
    }
}
