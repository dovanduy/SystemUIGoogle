// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import com.android.systemui.shared.plugins.PluginManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PluginDependencyProvider_Factory implements Factory<PluginDependencyProvider>
{
    private final Provider<PluginManager> managerProvider;
    
    public PluginDependencyProvider_Factory(final Provider<PluginManager> managerProvider) {
        this.managerProvider = managerProvider;
    }
    
    public static PluginDependencyProvider_Factory create(final Provider<PluginManager> provider) {
        return new PluginDependencyProvider_Factory(provider);
    }
    
    public static PluginDependencyProvider newPluginDependencyProvider(final PluginManager pluginManager) {
        return new PluginDependencyProvider(pluginManager);
    }
    
    public static PluginDependencyProvider provideInstance(final Provider<PluginManager> provider) {
        return new PluginDependencyProvider(provider.get());
    }
    
    @Override
    public PluginDependencyProvider get() {
        return provideInstance(this.managerProvider);
    }
}
