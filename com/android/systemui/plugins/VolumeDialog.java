// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;

@DependsOn(target = Callback.class)
@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_VOLUME", version = 1)
public interface VolumeDialog extends Plugin
{
    public static final String ACTION = "com.android.systemui.action.PLUGIN_VOLUME";
    public static final int VERSION = 1;
    
    void destroy();
    
    void init(final int p0, final Callback p1);
    
    @ProvidesInterface(version = 1)
    public interface Callback
    {
        public static final int VERSION = 1;
        
        void onZenPrioritySettingsClicked();
        
        void onZenSettingsClicked();
    }
}
