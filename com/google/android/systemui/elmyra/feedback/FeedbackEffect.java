// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.feedback;

import com.google.android.systemui.elmyra.sensors.GestureSensor;

public interface FeedbackEffect
{
    void onProgress(final float p0, final int p1);
    
    void onRelease();
    
    void onResolve(final GestureSensor.DetectionProperties p0);
}
