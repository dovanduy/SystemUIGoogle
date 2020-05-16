// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.content.Context;

public interface PluginListener<T extends Plugin>
{
    void onPluginConnected(final T p0, final Context p1);
    
    default void onPluginDisconnected(final T t) {
    }
}
