// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import android.animation.TimeInterpolator;

public final class SystemUIInterpolators$LogDecelerateInterpolator implements TimeInterpolator
{
    private final float mBase;
    private final float mDrift;
    private final float mOutputScale;
    private final float mTimeScale;
    
    public SystemUIInterpolators$LogDecelerateInterpolator() {
        this(400.0f, 1.4f, 0.0f);
    }
    
    private SystemUIInterpolators$LogDecelerateInterpolator(final float mBase, final float n, final float mDrift) {
        this.mBase = mBase;
        this.mDrift = mDrift;
        this.mTimeScale = 1.0f / n;
        this.mOutputScale = 1.0f / this.computeLog(1.0f);
    }
    
    private float computeLog(final float n) {
        return 1.0f - (float)Math.pow(this.mBase, -n * this.mTimeScale) + this.mDrift * n;
    }
    
    public float getInterpolation(final float n) {
        return this.computeLog(n) * this.mOutputScale;
    }
}
