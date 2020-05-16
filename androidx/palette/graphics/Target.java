// 
// Decompiled by Procyon v0.5.36
// 

package androidx.palette.graphics;

public final class Target
{
    public static final Target DARK_MUTED;
    public static final Target DARK_VIBRANT;
    public static final Target LIGHT_MUTED;
    public static final Target LIGHT_VIBRANT;
    public static final Target MUTED;
    public static final Target VIBRANT;
    boolean mIsExclusive;
    final float[] mLightnessTargets;
    final float[] mSaturationTargets;
    final float[] mWeights;
    
    static {
        setDefaultLightLightnessValues(LIGHT_VIBRANT = new Target());
        setDefaultVibrantSaturationValues(Target.LIGHT_VIBRANT);
        setDefaultNormalLightnessValues(VIBRANT = new Target());
        setDefaultVibrantSaturationValues(Target.VIBRANT);
        setDefaultDarkLightnessValues(DARK_VIBRANT = new Target());
        setDefaultVibrantSaturationValues(Target.DARK_VIBRANT);
        setDefaultLightLightnessValues(LIGHT_MUTED = new Target());
        setDefaultMutedSaturationValues(Target.LIGHT_MUTED);
        setDefaultNormalLightnessValues(MUTED = new Target());
        setDefaultMutedSaturationValues(Target.MUTED);
        setDefaultDarkLightnessValues(DARK_MUTED = new Target());
        setDefaultMutedSaturationValues(Target.DARK_MUTED);
    }
    
    Target() {
        final float[] array = new float[3];
        this.mSaturationTargets = array;
        this.mLightnessTargets = new float[3];
        this.mWeights = new float[3];
        this.mIsExclusive = true;
        setTargetDefaultValues(array);
        setTargetDefaultValues(this.mLightnessTargets);
        this.setDefaultWeights();
    }
    
    private static void setDefaultDarkLightnessValues(final Target target) {
        final float[] mLightnessTargets = target.mLightnessTargets;
        mLightnessTargets[1] = 0.26f;
        mLightnessTargets[2] = 0.45f;
    }
    
    private static void setDefaultLightLightnessValues(final Target target) {
        final float[] mLightnessTargets = target.mLightnessTargets;
        mLightnessTargets[0] = 0.55f;
        mLightnessTargets[1] = 0.74f;
    }
    
    private static void setDefaultMutedSaturationValues(final Target target) {
        final float[] mSaturationTargets = target.mSaturationTargets;
        mSaturationTargets[1] = 0.3f;
        mSaturationTargets[2] = 0.4f;
    }
    
    private static void setDefaultNormalLightnessValues(final Target target) {
        final float[] mLightnessTargets = target.mLightnessTargets;
        mLightnessTargets[0] = 0.3f;
        mLightnessTargets[1] = 0.5f;
        mLightnessTargets[2] = 0.7f;
    }
    
    private static void setDefaultVibrantSaturationValues(final Target target) {
        final float[] mSaturationTargets = target.mSaturationTargets;
        mSaturationTargets[0] = 0.35f;
        mSaturationTargets[1] = 1.0f;
    }
    
    private void setDefaultWeights() {
        final float[] mWeights = this.mWeights;
        mWeights[0] = 0.24f;
        mWeights[1] = 0.52f;
        mWeights[2] = 0.24f;
    }
    
    private static void setTargetDefaultValues(final float[] array) {
        array[0] = 0.0f;
        array[1] = 0.5f;
        array[2] = 1.0f;
    }
    
    public float getLightnessWeight() {
        return this.mWeights[1];
    }
    
    public float getMaximumLightness() {
        return this.mLightnessTargets[2];
    }
    
    public float getMaximumSaturation() {
        return this.mSaturationTargets[2];
    }
    
    public float getMinimumLightness() {
        return this.mLightnessTargets[0];
    }
    
    public float getMinimumSaturation() {
        return this.mSaturationTargets[0];
    }
    
    public float getPopulationWeight() {
        return this.mWeights[2];
    }
    
    public float getSaturationWeight() {
        return this.mWeights[0];
    }
    
    public float getTargetLightness() {
        return this.mLightnessTargets[1];
    }
    
    public float getTargetSaturation() {
        return this.mSaturationTargets[1];
    }
    
    public boolean isExclusive() {
        return this.mIsExclusive;
    }
    
    void normalizeWeights() {
        final int length = this.mWeights.length;
        final int n = 0;
        int i = 0;
        float n2 = 0.0f;
        while (i < length) {
            final float n3 = this.mWeights[i];
            float n4 = n2;
            if (n3 > 0.0f) {
                n4 = n2 + n3;
            }
            ++i;
            n2 = n4;
        }
        if (n2 != 0.0f) {
            for (int length2 = this.mWeights.length, j = n; j < length2; ++j) {
                final float[] mWeights = this.mWeights;
                if (mWeights[j] > 0.0f) {
                    mWeights[j] /= n2;
                }
            }
        }
    }
}
