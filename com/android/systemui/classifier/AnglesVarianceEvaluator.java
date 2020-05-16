// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class AnglesVarianceEvaluator
{
    public static float evaluate(float n, final int n2) {
        final double n3 = n;
        float n4;
        if (n3 > 0.2) {
            n4 = 1.0f;
        }
        else {
            n4 = 0.0f;
        }
        n = n4;
        if (n3 > 0.4) {
            n = n4 + 1.0f;
        }
        float n5 = n;
        if (n3 > 0.8) {
            n5 = n + 1.0f;
        }
        n = n5;
        if (n3 > 1.5) {
            n = n5 + 1.0f;
        }
        return n;
    }
}
