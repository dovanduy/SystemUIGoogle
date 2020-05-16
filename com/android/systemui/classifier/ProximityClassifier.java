// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

import android.view.MotionEvent;
import android.hardware.SensorEvent;

public class ProximityClassifier extends GestureClassifier
{
    private float mAverageNear;
    private long mGestureStartTimeNano;
    private boolean mNear;
    private long mNearDuration;
    private long mNearStartTimeNano;
    
    public ProximityClassifier(final ClassifierData classifierData) {
    }
    
    private void update(final boolean mNear, final long mNearStartTimeNano) {
        final long mNearStartTimeNano2 = this.mNearStartTimeNano;
        if (mNearStartTimeNano > mNearStartTimeNano2) {
            if (this.mNear) {
                this.mNearDuration += mNearStartTimeNano - mNearStartTimeNano2;
            }
            if (mNear) {
                this.mNearStartTimeNano = mNearStartTimeNano;
            }
        }
        this.mNear = mNear;
    }
    
    @Override
    public float getFalseTouchEvaluation(final int n) {
        return ProximityEvaluator.evaluate(this.mAverageNear, n);
    }
    
    @Override
    public String getTag() {
        return "PROX";
    }
    
    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8) {
            final float[] values = sensorEvent.values;
            boolean b = false;
            if (values[0] < sensorEvent.sensor.getMaximumRange()) {
                b = true;
            }
            this.update(b, sensorEvent.timestamp);
        }
    }
    
    @Override
    public void onTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mGestureStartTimeNano = motionEvent.getEventTimeNano();
            this.mNearStartTimeNano = motionEvent.getEventTimeNano();
            this.mNearDuration = 0L;
        }
        if (actionMasked == 1 || actionMasked == 3) {
            this.update(this.mNear, motionEvent.getEventTimeNano());
            final long n = motionEvent.getEventTimeNano() - this.mGestureStartTimeNano;
            if (n == 0L) {
                float mAverageNear;
                if (this.mNear) {
                    mAverageNear = 1.0f;
                }
                else {
                    mAverageNear = 0.0f;
                }
                this.mAverageNear = mAverageNear;
            }
            else {
                this.mAverageNear = this.mNearDuration / (float)n;
            }
        }
    }
}
