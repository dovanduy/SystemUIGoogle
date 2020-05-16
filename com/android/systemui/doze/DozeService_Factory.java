// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import com.android.systemui.shared.plugins.PluginManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DozeService_Factory implements Factory<DozeService>
{
    private final Provider<DozeFactory> dozeFactoryProvider;
    private final Provider<PluginManager> pluginManagerProvider;
    
    public DozeService_Factory(final Provider<DozeFactory> dozeFactoryProvider, final Provider<PluginManager> pluginManagerProvider) {
        this.dozeFactoryProvider = dozeFactoryProvider;
        this.pluginManagerProvider = pluginManagerProvider;
    }
    
    public static DozeService_Factory create(final Provider<DozeFactory> provider, final Provider<PluginManager> provider2) {
        return new DozeService_Factory(provider, provider2);
    }
    
    public static DozeService provideInstance(final Provider<DozeFactory> provider, final Provider<PluginManager> provider2) {
        return new DozeService(provider.get(), provider2.get());
    }
    
    @Override
    public DozeService get() {
        return provideInstance(this.dozeFactoryProvider, this.pluginManagerProvider);
    }
}
