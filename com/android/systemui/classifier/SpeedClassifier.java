// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class SpeedClassifier extends StrokeClassifier
{
    public SpeedClassifier(final ClassifierData classifierData) {
    }
    
    @Override
    public float getFalseTouchEvaluation(final int n, final Stroke stroke) {
        final float n2 = stroke.getDurationNanos() / 1.0E9f;
        if (n2 == 0.0f) {
            return SpeedEvaluator.evaluate(0.0f);
        }
        return SpeedEvaluator.evaluate(stroke.getTotalLength() / n2);
    }
    
    @Override
    public String getTag() {
        return "SPD";
    }
}
