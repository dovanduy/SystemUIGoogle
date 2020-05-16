// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class SpeedRatioEvaluator
{
    public static float evaluate(float n) {
        float n2 = 0.0f;
        if (n == 0.0f) {
            return 0.0f;
        }
        final double n3 = n;
        if (n3 <= 1.0) {
            n2 = 1.0f;
        }
        n = n2;
        if (n3 <= 0.5) {
            n = n2 + 1.0f;
        }
        float n4 = n;
        if (n3 > 9.0) {
            n4 = n + 1.0f;
        }
        n = n4;
        if (n3 > 18.0) {
            n = n4 + 1.0f;
        }
        return n;
    }
}
