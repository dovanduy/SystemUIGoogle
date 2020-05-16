// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

import kotlin.jvm.internal.Intrinsics;

public final class GestureController
{
    private GestureSensor.Listener gestureListener;
    private int gestureStage;
    
    private final void sendGestureProgress(final GestureSensor gestureSensor, final int n, final GestureSensor.DetectionProperties detectionProperties) {
        final GestureSensor.Listener gestureListener = this.gestureListener;
        if (gestureListener != null) {
            gestureListener.onGestureProgress(gestureSensor, n, detectionProperties);
        }
    }
    
    public final void onGestureProgress(final GestureSensor gestureSensor, final int n, final GestureSensor.DetectionProperties detectionProperties) {
        Intrinsics.checkParameterIsNotNull(gestureSensor, "sensor");
        if (n != this.gestureStage) {
            int gestureStage;
            if (n == 3) {
                gestureStage = 0;
            }
            else {
                gestureStage = n;
            }
            this.gestureStage = gestureStage;
            this.sendGestureProgress(gestureSensor, n, detectionProperties);
        }
    }
    
    public final void setGestureListener(final GestureSensor.Listener gestureListener) {
        this.gestureListener = gestureListener;
    }
}
