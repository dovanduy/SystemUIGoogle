// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import com.android.systemui.shared.plugins.PluginManager;
import dagger.internal.Factory;

public final class DependencyProvider_ProvidePluginManagerFactory implements Factory<PluginManager>
{
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;
    
    public DependencyProvider_ProvidePluginManagerFactory(final DependencyProvider module, final Provider<Context> contextProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
    }
    
    public static DependencyProvider_ProvidePluginManagerFactory create(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return new DependencyProvider_ProvidePluginManagerFactory(dependencyProvider, provider);
    }
    
    public static PluginManager provideInstance(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return proxyProvidePluginManager(dependencyProvider, provider.get());
    }
    
    public static PluginManager proxyProvidePluginManager(final DependencyProvider dependencyProvider, final Context context) {
        final PluginManager providePluginManager = dependencyProvider.providePluginManager(context);
        Preconditions.checkNotNull(providePluginManager, "Cannot return null from a non-@Nullable @Provides method");
        return providePluginManager;
    }
    
    @Override
    public PluginManager get() {
        return provideInstance(this.module, this.contextProvider);
    }
}
