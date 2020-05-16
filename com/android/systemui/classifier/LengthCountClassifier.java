// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class LengthCountClassifier extends StrokeClassifier
{
    public LengthCountClassifier(final ClassifierData classifierData) {
    }
    
    @Override
    public float getFalseTouchEvaluation(final int n, final Stroke stroke) {
        return LengthCountEvaluator.evaluate(stroke.getTotalLength() / Math.max(1.0f, (float)(stroke.getCount() - 2)));
    }
    
    @Override
    public String getTag() {
        return "LEN_CNT";
    }
}
