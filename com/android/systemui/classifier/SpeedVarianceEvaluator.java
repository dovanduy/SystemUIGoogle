// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class SpeedVarianceEvaluator
{
    public static float evaluate(float n) {
        final double n2 = n;
        float n3;
        if (n2 > 0.06) {
            n3 = 1.0f;
        }
        else {
            n3 = 0.0f;
        }
        n = n3;
        if (n2 > 0.15) {
            n = n3 + 1.0f;
        }
        float n4 = n;
        if (n2 > 0.3) {
            n4 = n + 1.0f;
        }
        n = n4;
        if (n2 > 0.6) {
            n = n4 + 1.0f;
        }
        return n;
    }
}
