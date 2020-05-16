// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.display;

import android.util.MathUtils;

public class BrightnessUtils
{
    public static final float convertGammaToLinearFloat(final int n, final float n2, final float n3) {
        final float norm = MathUtils.norm(0.0f, 65535.0f, (float)n);
        float sq;
        if (norm <= 0.5f) {
            sq = MathUtils.sq(norm / 0.5f);
        }
        else {
            sq = MathUtils.exp((norm - 0.5599107f) / 0.17883277f) + 0.28466892f;
        }
        return MathUtils.lerp(n2, n3, sq / 12.0f);
    }
    
    public static final int convertLinearToGammaFloat(float n, final float n2, final float n3) {
        n = MathUtils.norm(n2, n3, n) * 12.0f;
        if (n <= 1.0f) {
            n = MathUtils.sqrt(n) * 0.5f;
        }
        else {
            n = MathUtils.log(n - 0.28466892f) * 0.17883277f + 0.5599107f;
        }
        return Math.round(MathUtils.lerp(0.0f, 65535.0f, n));
    }
}
