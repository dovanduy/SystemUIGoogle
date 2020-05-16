// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.plugins;

import android.content.ComponentName;

public interface PluginEnabler
{
    int getDisableReason(final ComponentName p0);
    
    boolean isEnabled(final ComponentName p0);
    
    void setDisabled(final ComponentName p0, final int p1);
    
    void setEnabled(final ComponentName p0);
}
