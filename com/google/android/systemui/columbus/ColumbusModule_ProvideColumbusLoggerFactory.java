// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import dagger.internal.Preconditions;
import com.android.internal.logging.MetricsLogger;
import dagger.internal.Factory;

public final class ColumbusModule_ProvideColumbusLoggerFactory implements Factory<MetricsLogger>
{
    private static final ColumbusModule_ProvideColumbusLoggerFactory INSTANCE;
    
    static {
        INSTANCE = new ColumbusModule_ProvideColumbusLoggerFactory();
    }
    
    public static ColumbusModule_ProvideColumbusLoggerFactory create() {
        return ColumbusModule_ProvideColumbusLoggerFactory.INSTANCE;
    }
    
    public static MetricsLogger provideInstance() {
        return proxyProvideColumbusLogger();
    }
    
    public static MetricsLogger proxyProvideColumbusLogger() {
        final MetricsLogger provideColumbusLogger = ColumbusModule.provideColumbusLogger();
        Preconditions.checkNotNull(provideColumbusLogger, "Cannot return null from a non-@Nullable @Provides method");
        return provideColumbusLogger;
    }
    
    @Override
    public MetricsLogger get() {
        return provideInstance();
    }
}
