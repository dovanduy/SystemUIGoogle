// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_DOZE", version = 1)
public interface DozeServicePlugin extends Plugin
{
    public static final String ACTION = "com.android.systemui.action.PLUGIN_DOZE";
    public static final int VERSION = 1;
    
    void onDreamingStarted();
    
    void onDreamingStopped();
    
    void setDozeRequester(final RequestDoze p0);
    
    public interface RequestDoze
    {
        void onRequestHideDoze();
        
        void onRequestShowDoze();
    }
}
