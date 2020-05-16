// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class EndPointLengthClassifier extends StrokeClassifier
{
    public EndPointLengthClassifier(final ClassifierData classifierData) {
    }
    
    @Override
    public float getFalseTouchEvaluation(final int n, final Stroke stroke) {
        return EndPointLengthEvaluator.evaluate(stroke.getEndPointLength());
    }
    
    @Override
    public String getTag() {
        return "END_LNGTH";
    }
}
