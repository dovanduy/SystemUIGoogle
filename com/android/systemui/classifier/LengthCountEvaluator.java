// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class LengthCountEvaluator
{
    public static float evaluate(float n) {
        final double n2 = n;
        if (n2 < 0.09) {
            n = 1.0f;
        }
        else {
            n = 0.0f;
        }
        float n3 = n;
        if (n2 < 0.05) {
            n3 = n + 1.0f;
        }
        n = n3;
        if (n2 < 0.02) {
            n = n3 + 1.0f;
        }
        float n4 = n;
        if (n2 > 0.6) {
            n4 = n + 1.0f;
        }
        n = n4;
        if (n2 > 0.9) {
            n = n4 + 1.0f;
        }
        float n5 = n;
        if (n2 > 1.2) {
            n5 = n + 1.0f;
        }
        return n5;
    }
}
