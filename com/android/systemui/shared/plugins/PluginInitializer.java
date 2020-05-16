// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.plugins;

import android.content.Context;
import android.os.Looper;

public interface PluginInitializer
{
    Looper getBgLooper();
    
    PluginEnabler getPluginEnabler(final Context p0);
    
    String[] getWhitelistedPlugins(final Context p0);
    
    void handleWtfs();
    
    void onPluginManagerInit();
}
