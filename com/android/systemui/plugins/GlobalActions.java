// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;

@DependsOn(target = GlobalActionsManager.class)
@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_GLOBAL_ACTIONS", version = 1)
public interface GlobalActions extends Plugin
{
    public static final String ACTION = "com.android.systemui.action.PLUGIN_GLOBAL_ACTIONS";
    public static final int VERSION = 1;
    
    default void destroy() {
    }
    
    void showGlobalActions(final GlobalActionsManager p0);
    
    default void showShutdownUi(final boolean b, final String s) {
    }
    
    @ProvidesInterface(version = 1)
    public interface GlobalActionsManager
    {
        public static final int VERSION = 1;
        
        void onGlobalActionsHidden();
        
        void onGlobalActionsShown();
        
        void reboot(final boolean p0);
        
        void shutdown();
    }
}
