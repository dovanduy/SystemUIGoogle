// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

import android.view.MotionEvent;
import java.util.HashMap;

public class AccelerationClassifier extends StrokeClassifier
{
    private final HashMap<Stroke, Data> mStrokeMap;
    
    public AccelerationClassifier(final ClassifierData mClassifierData) {
        this.mStrokeMap = new HashMap<Stroke, Data>();
        super.mClassifierData = mClassifierData;
    }
    
    @Override
    public float getFalseTouchEvaluation(final int n, final Stroke key) {
        return SpeedRatioEvaluator.evaluate(this.mStrokeMap.get(key).maxSpeedRatio) * 2.0f;
    }
    
    @Override
    public String getTag() {
        return "ACC";
    }
    
    @Override
    public void onTouchEvent(final MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            this.mStrokeMap.clear();
        }
        for (int i = 0; i < motionEvent.getPointerCount(); ++i) {
            final Stroke stroke = super.mClassifierData.getStroke(motionEvent.getPointerId(i));
            final Point point = stroke.getPoints().get(stroke.getPoints().size() - 1);
            if (this.mStrokeMap.get(stroke) == null) {
                this.mStrokeMap.put(stroke, new Data(point));
            }
            else {
                this.mStrokeMap.get(stroke).addPoint(point);
            }
        }
    }
    
    private static class Data
    {
        float maxSpeedRatio;
        Point previousPoint;
        float previousSpeed;
        
        public Data(final Point previousPoint) {
            this.previousSpeed = 0.0f;
            this.maxSpeedRatio = 0.0f;
            this.previousPoint = previousPoint;
        }
        
        public void addPoint(final Point point) {
            final float dist = this.previousPoint.dist(point);
            final float n = (float)(point.timeOffsetNano - this.previousPoint.timeOffsetNano + 1L);
            final float previousSpeed = dist / n;
            if (n <= 2.0E7f && n >= 5000000.0f) {
                final float previousSpeed2 = this.previousSpeed;
                if (previousSpeed2 != 0.0f) {
                    this.maxSpeedRatio = Math.max(this.maxSpeedRatio, previousSpeed / previousSpeed2);
                }
                this.previousSpeed = previousSpeed;
                this.previousPoint = point;
                return;
            }
            this.previousSpeed = 0.0f;
            this.previousPoint = point;
        }
    }
}
