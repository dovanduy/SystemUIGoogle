// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class EndPointRatioClassifier extends StrokeClassifier
{
    public EndPointRatioClassifier(final ClassifierData mClassifierData) {
        super.mClassifierData = mClassifierData;
    }
    
    @Override
    public float getFalseTouchEvaluation(final int n, final Stroke stroke) {
        float n2;
        if (stroke.getTotalLength() == 0.0f) {
            n2 = 1.0f;
        }
        else {
            n2 = stroke.getEndPointLength() / stroke.getTotalLength();
        }
        return EndPointRatioEvaluator.evaluate(n2);
    }
    
    @Override
    public String getTag() {
        return "END_RTIO";
    }
}
