// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class SpeedEvaluator
{
    public static float evaluate(float n) {
        final double n2 = n;
        if (n2 < 4.0) {
            n = 1.0f;
        }
        else {
            n = 0.0f;
        }
        float n3 = n;
        if (n2 < 2.2) {
            n3 = n + 1.0f;
        }
        n = n3;
        if (n2 > 35.0) {
            n = n3 + 1.0f;
        }
        float n4 = n;
        if (n2 > 50.0) {
            n4 = n + 1.0f;
        }
        return n4;
    }
}
