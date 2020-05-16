// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

import android.view.MotionEvent;
import android.hardware.SensorEvent;

public abstract class Classifier
{
    protected ClassifierData mClassifierData;
    
    public abstract String getTag();
    
    public void onSensorChanged(final SensorEvent sensorEvent) {
    }
    
    public void onTouchEvent(final MotionEvent motionEvent) {
    }
}
