// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.sensors;

import java.util.Random;

public interface GestureSensor extends Sensor
{
    void setGestureListener(final Listener p0);
    
    public static class DetectionProperties
    {
        final long mActionId;
        final boolean mHapticConsumed;
        final boolean mHostSuspended;
        
        public DetectionProperties(final boolean mHapticConsumed, final boolean mHostSuspended) {
            this.mHapticConsumed = mHapticConsumed;
            this.mHostSuspended = mHostSuspended;
            this.mActionId = new Random().nextLong();
        }
        
        public long getActionId() {
            return this.mActionId;
        }
        
        public boolean isHapticConsumed() {
            return this.mHapticConsumed;
        }
        
        public boolean isHostSuspended() {
            return this.mHostSuspended;
        }
    }
    
    public interface Listener
    {
        void onGestureDetected(final GestureSensor p0, final DetectionProperties p1);
        
        void onGestureProgress(final GestureSensor p0, final float p1, final int p2);
    }
}
