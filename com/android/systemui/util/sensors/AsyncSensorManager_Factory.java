// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.sensors;

import com.android.systemui.shared.plugins.PluginManager;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AsyncSensorManager_Factory implements Factory<AsyncSensorManager>
{
    private final Provider<Context> contextProvider;
    private final Provider<PluginManager> pluginManagerProvider;
    
    public AsyncSensorManager_Factory(final Provider<Context> contextProvider, final Provider<PluginManager> pluginManagerProvider) {
        this.contextProvider = contextProvider;
        this.pluginManagerProvider = pluginManagerProvider;
    }
    
    public static AsyncSensorManager_Factory create(final Provider<Context> provider, final Provider<PluginManager> provider2) {
        return new AsyncSensorManager_Factory(provider, provider2);
    }
    
    public static AsyncSensorManager provideInstance(final Provider<Context> provider, final Provider<PluginManager> provider2) {
        return new AsyncSensorManager(provider.get(), provider2.get());
    }
    
    @Override
    public AsyncSensorManager get() {
        return provideInstance(this.contextProvider, this.pluginManagerProvider);
    }
}
