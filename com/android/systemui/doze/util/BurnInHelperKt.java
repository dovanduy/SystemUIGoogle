// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze.util;

import android.util.MathUtils;

public final class BurnInHelperKt
{
    public static final int getBurnInOffset(final int n, final boolean b) {
        final float n2 = System.currentTimeMillis() / 60000.0f;
        final float n3 = (float)n;
        float n4;
        if (b) {
            n4 = 83.0f;
        }
        else {
            n4 = 521.0f;
        }
        return (int)zigzag(n2, n3, n4);
    }
    
    private static final float zigzag(float n, final float n2, final float n3) {
        final float n4 = 2;
        n = n % n3 / (n3 / n4);
        if (n > 1) {
            n = n4 - n;
        }
        return MathUtils.lerp(0.0f, n2, n);
    }
}
