// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class DurationCountEvaluator
{
    public static float evaluate(float n) {
        final double n2 = n;
        float n3;
        if (n2 < 0.0105) {
            n3 = 1.0f;
        }
        else {
            n3 = 0.0f;
        }
        n = n3;
        if (n2 < 0.00909) {
            n = n3 + 1.0f;
        }
        float n4 = n;
        if (n2 < 0.00667) {
            n4 = n + 1.0f;
        }
        n = n4;
        if (n2 > 0.0333) {
            n = n4 + 1.0f;
        }
        float n5 = n;
        if (n2 > 0.05) {
            n5 = n + 1.0f;
        }
        return n5;
    }
}
