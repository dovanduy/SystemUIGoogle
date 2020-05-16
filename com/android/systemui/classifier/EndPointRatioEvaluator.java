// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class EndPointRatioEvaluator
{
    public static float evaluate(float n) {
        final double n2 = n;
        float n3;
        if (n2 < 0.85) {
            n3 = 1.0f;
        }
        else {
            n3 = 0.0f;
        }
        n = n3;
        if (n2 < 0.75) {
            n = n3 + 1.0f;
        }
        float n4 = n;
        if (n2 < 0.65) {
            n4 = n + 1.0f;
        }
        float n5 = n4;
        if (n2 < 0.55) {
            n5 = n4 + 1.0f;
        }
        n = n5;
        if (n2 < 0.45) {
            n = n5 + 1.0f;
        }
        float n6 = n;
        if (n2 < 0.35) {
            n6 = n + 1.0f;
        }
        return n6;
    }
}
