// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class DirectionClassifier extends StrokeClassifier
{
    public DirectionClassifier(final ClassifierData classifierData) {
    }
    
    @Override
    public float getFalseTouchEvaluation(final int n, final Stroke stroke) {
        final Point point = stroke.getPoints().get(0);
        final Point point2 = stroke.getPoints().get(stroke.getPoints().size() - 1);
        return DirectionEvaluator.evaluate(point2.x - point.x, point2.y - point.y, n);
    }
    
    @Override
    public String getTag() {
        return "DIR";
    }
}
