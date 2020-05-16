// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.power;

import com.android.settingslib.fuelgauge.Estimate;

public class EnhancedEstimatesImpl implements EnhancedEstimates
{
    @Override
    public Estimate getEstimate() {
        return null;
    }
    
    @Override
    public boolean getLowWarningEnabled() {
        return true;
    }
    
    @Override
    public long getLowWarningThreshold() {
        return 0L;
    }
    
    @Override
    public long getSevereWarningThreshold() {
        return 0L;
    }
    
    @Override
    public boolean isHybridNotificationEnabled() {
        return false;
    }
}
