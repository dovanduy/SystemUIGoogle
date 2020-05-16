// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.applications;

import android.content.res.Resources;
import android.content.res.Configuration;

public class InterestingConfigChanges
{
    private final int mFlags;
    private final Configuration mLastConfiguration;
    private int mLastDensity;
    
    public InterestingConfigChanges() {
        this(-2147482876);
    }
    
    public InterestingConfigChanges(final int mFlags) {
        this.mLastConfiguration = new Configuration();
        this.mFlags = mFlags;
    }
    
    public boolean applyNewConfig(final Resources resources) {
        final Configuration mLastConfiguration = this.mLastConfiguration;
        final int update = mLastConfiguration.updateFrom(Configuration.generateDelta(mLastConfiguration, resources.getConfiguration()));
        if (this.mLastDensity == resources.getDisplayMetrics().densityDpi && (update & this.mFlags) == 0x0) {
            return false;
        }
        this.mLastDensity = resources.getDisplayMetrics().densityDpi;
        return true;
    }
}
