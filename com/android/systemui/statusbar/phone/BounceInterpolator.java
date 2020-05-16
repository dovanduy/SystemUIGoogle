// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.animation.Interpolator;

public class BounceInterpolator implements Interpolator
{
    public float getInterpolation(float n) {
        n *= 1.1f;
        if (n < 0.36363637f) {
            return 7.5625f * n * n;
        }
        if (n < 0.72727275f) {
            n -= 0.54545456f;
            return 7.5625f * n * n + 0.75f;
        }
        if (n < 0.90909094f) {
            n -= 0.8181818f;
            return 7.5625f * n * n + 0.9375f;
        }
        return 1.0f;
    }
}
