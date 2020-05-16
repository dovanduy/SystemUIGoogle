// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.hardware.display.AmbientDisplayConfiguration;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideAmbientDisplayConfigurationFactory implements Factory<AmbientDisplayConfiguration>
{
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideAmbientDisplayConfigurationFactory(final DependencyProvider module, final Provider<Context> contextProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
    }
    
    public static DependencyProvider_ProvideAmbientDisplayConfigurationFactory create(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return new DependencyProvider_ProvideAmbientDisplayConfigurationFactory(dependencyProvider, provider);
    }
    
    public static AmbientDisplayConfiguration provideInstance(final DependencyProvider dependencyProvider, final Provider<Context> provider) {
        return proxyProvideAmbientDisplayConfiguration(dependencyProvider, provider.get());
    }
    
    public static AmbientDisplayConfiguration proxyProvideAmbientDisplayConfiguration(final DependencyProvider dependencyProvider, final Context context) {
        final AmbientDisplayConfiguration provideAmbientDisplayConfiguration = dependencyProvider.provideAmbientDisplayConfiguration(context);
        Preconditions.checkNotNull(provideAmbientDisplayConfiguration, "Cannot return null from a non-@Nullable @Provides method");
        return provideAmbientDisplayConfiguration;
    }
    
    @Override
    public AmbientDisplayConfiguration get() {
        return provideInstance(this.module, this.contextProvider);
    }
}
