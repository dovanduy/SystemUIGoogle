// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import com.android.systemui.Dependency;
import com.android.systemui.shared.plugins.PluginManager;
import android.util.ArrayMap;

public class PluginDependencyProvider extends DependencyProvider
{
    private final ArrayMap<Class<?>, Object> mDependencies;
    private final PluginManager mManager;
    
    public PluginDependencyProvider(final PluginManager mManager) {
        this.mDependencies = (ArrayMap<Class<?>, Object>)new ArrayMap();
        this.mManager = mManager;
        PluginDependency.sProvider = (PluginDependency.DependencyProvider)this;
    }
    
    public <T> void allowPluginDependency(final Class<T> clazz) {
        this.allowPluginDependency(clazz, (T)Dependency.get((Class<T>)clazz));
    }
    
    public <T> void allowPluginDependency(final Class<T> clazz, final T t) {
        synchronized (this.mDependencies) {
            this.mDependencies.put((Object)clazz, (Object)t);
        }
    }
    
    @Override
     <T> T get(final Plugin plugin, final Class<T> clazz) {
        if (this.mManager.dependsOn(plugin, clazz)) {
            synchronized (this.mDependencies) {
                if (this.mDependencies.containsKey((Object)clazz)) {
                    return (T)this.mDependencies.get((Object)clazz);
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Unknown dependency ");
                sb.append(clazz);
                throw new IllegalArgumentException(sb.toString());
            }
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(plugin.getClass());
        sb2.append(" does not depend on ");
        sb2.append(clazz);
        throw new IllegalArgumentException(sb2.toString());
    }
}
