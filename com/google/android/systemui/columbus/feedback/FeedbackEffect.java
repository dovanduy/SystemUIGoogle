// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.feedback;

import com.google.android.systemui.columbus.sensors.GestureSensor;

public interface FeedbackEffect
{
    void onProgress(final int p0, final GestureSensor.DetectionProperties p1);
}
