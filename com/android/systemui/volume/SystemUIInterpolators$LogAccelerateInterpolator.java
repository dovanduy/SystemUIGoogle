// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import android.animation.TimeInterpolator;

public final class SystemUIInterpolators$LogAccelerateInterpolator implements TimeInterpolator
{
    private final int mBase;
    private final int mDrift;
    private final float mLogScale;
    
    public SystemUIInterpolators$LogAccelerateInterpolator() {
        this(100, 0);
    }
    
    private SystemUIInterpolators$LogAccelerateInterpolator(final int mBase, final int mDrift) {
        this.mBase = mBase;
        this.mDrift = mDrift;
        this.mLogScale = 1.0f / computeLog(1.0f, mBase, mDrift);
    }
    
    private static float computeLog(final float n, final int n2, final int n3) {
        return (float)(-Math.pow(n2, -n)) + 1.0f + n3 * n;
    }
    
    public float getInterpolation(final float n) {
        return 1.0f - computeLog(1.0f - n, this.mBase, this.mDrift) * this.mLogScale;
    }
}
