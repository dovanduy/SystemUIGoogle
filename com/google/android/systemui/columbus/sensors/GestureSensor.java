// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

import java.util.Random;

public interface GestureSensor extends Sensor
{
    void setGestureListener(final Listener p0);
    
    public static final class DetectionProperties
    {
        private final long actionId;
        private final boolean isHapticConsumed;
        private final boolean isHostSuspended;
        
        public DetectionProperties(final boolean isHostSuspended, final boolean isHapticConsumed) {
            this.isHostSuspended = isHostSuspended;
            this.isHapticConsumed = isHapticConsumed;
            this.actionId = new Random().nextLong();
        }
        
        public final long getActionId() {
            return this.actionId;
        }
        
        public final boolean isHapticConsumed() {
            return this.isHapticConsumed;
        }
        
        public final boolean isHostSuspended() {
            return this.isHostSuspended;
        }
    }
    
    public interface Listener
    {
        void onGestureProgress(final GestureSensor p0, final int p1, final DetectionProperties p2);
        
        public static final class DefaultImpls
        {
        }
    }
}
