// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.systemui.Dumpable;

public interface FlashlightController extends CallbackController<FlashlightListener>, Dumpable
{
    boolean hasFlashlight();
    
    boolean isAvailable();
    
    boolean isEnabled();
    
    void setFlashlight(final boolean p0);
    
    public interface FlashlightListener
    {
        void onFlashlightAvailabilityChanged(final boolean p0);
        
        void onFlashlightChanged(final boolean p0);
        
        void onFlashlightError();
    }
}
