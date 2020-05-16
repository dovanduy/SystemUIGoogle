// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import com.android.internal.logging.MetricsLogger;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideMetricsLoggerFactory implements Factory<MetricsLogger>
{
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideMetricsLoggerFactory(final DependencyProvider module) {
        this.module = module;
    }
    
    public static DependencyProvider_ProvideMetricsLoggerFactory create(final DependencyProvider dependencyProvider) {
        return new DependencyProvider_ProvideMetricsLoggerFactory(dependencyProvider);
    }
    
    public static MetricsLogger provideInstance(final DependencyProvider dependencyProvider) {
        return proxyProvideMetricsLogger(dependencyProvider);
    }
    
    public static MetricsLogger proxyProvideMetricsLogger(final DependencyProvider dependencyProvider) {
        final MetricsLogger provideMetricsLogger = dependencyProvider.provideMetricsLogger();
        Preconditions.checkNotNull(provideMetricsLogger, "Cannot return null from a non-@Nullable @Provides method");
        return provideMetricsLogger;
    }
    
    @Override
    public MetricsLogger get() {
        return provideInstance(this.module);
    }
}
