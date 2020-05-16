// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(version = 1)
public class PluginDependency
{
    public static final int VERSION = 1;
    static DependencyProvider sProvider;
    
    public static <T> T get(final Plugin plugin, final Class<T> clazz) {
        return PluginDependency.sProvider.get(plugin, clazz);
    }
    
    abstract static class DependencyProvider
    {
        abstract <T> T get(final Plugin p0, final Class<T> p1);
    }
}
