// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideConfigurationControllerFactory implements Factory<ConfigurationController>
{
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideConfigurationControllerFactory(final DependencyProvider module, final Provider<Context> contextProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
    }
    
    public static DependencyProvider_ProvideConfigurationControllerFactory create(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return new DependencyProvider_ProvideConfigurationControllerFactory(dependencyProvider, provider);
    }
    
    public static ConfigurationController provideInstance(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return proxyProvideConfigurationController(dependencyProvider, provider.get());
    }
    
    public static ConfigurationController proxyProvideConfigurationController(final DependencyProvider dependencyProvider, final Context context) {
        final ConfigurationController provideConfigurationController = dependencyProvider.provideConfigurationController(context);
        Preconditions.checkNotNull(provideConfigurationController, "Cannot return null from a non-@Nullable @Provides method");
        return provideConfigurationController;
    }
    
    @Override
    public ConfigurationController get() {
        return provideInstance(this.module, this.contextProvider);
    }
}
