// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.power;

import com.android.settingslib.fuelgauge.Estimate;

public interface EnhancedEstimates
{
    Estimate getEstimate();
    
    boolean getLowWarningEnabled();
    
    long getLowWarningThreshold();
    
    long getSevereWarningThreshold();
    
    boolean isHybridNotificationEnabled();
}
