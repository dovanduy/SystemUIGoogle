// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.systemui.Dumpable;

public interface HotspotController extends CallbackController<Callback>, Dumpable
{
    int getNumConnectedDevices();
    
    boolean isHotspotEnabled();
    
    boolean isHotspotSupported();
    
    boolean isHotspotTransient();
    
    void setHotspotEnabled(final boolean p0);
    
    public interface Callback
    {
        default void onHotspotAvailabilityChanged(final boolean b) {
        }
        
        void onHotspotChanged(final boolean p0, final int p1);
    }
}
