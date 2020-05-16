// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class AnglesPercentageEvaluator
{
    public static float evaluate(float n, int n2) {
        if (n2 == 8) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        final float n3 = 0.0f;
        final double n4 = n;
        n = n3;
        if (n4 < 1.0) {
            n = n3;
            if (n2 == 0) {
                n = 1.0f;
            }
        }
        float n5 = n;
        if (n4 < 0.9) {
            n5 = n;
            if (n2 == 0) {
                n5 = n + 1.0f;
            }
        }
        n = n5;
        if (n4 < 0.7) {
            n = n5 + 1.0f;
        }
        return n;
    }
}
