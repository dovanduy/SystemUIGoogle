// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class DurationCountClassifier extends StrokeClassifier
{
    public DurationCountClassifier(final ClassifierData classifierData) {
    }
    
    @Override
    public float getFalseTouchEvaluation(final int n, final Stroke stroke) {
        return DurationCountEvaluator.evaluate(stroke.getDurationSeconds() / stroke.getCount());
    }
    
    @Override
    public String getTag() {
        return "DUR";
    }
}
