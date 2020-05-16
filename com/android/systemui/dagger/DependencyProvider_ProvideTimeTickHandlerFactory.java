// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.os.Handler;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideTimeTickHandlerFactory implements Factory<Handler>
{
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideTimeTickHandlerFactory(final DependencyProvider module) {
        this.module = module;
    }
    
    public static DependencyProvider_ProvideTimeTickHandlerFactory create(final DependencyProvider dependencyProvider) {
        return new DependencyProvider_ProvideTimeTickHandlerFactory(dependencyProvider);
    }
    
    public static Handler provideInstance(final DependencyProvider dependencyProvider) {
        return proxyProvideTimeTickHandler(dependencyProvider);
    }
    
    public static Handler proxyProvideTimeTickHandler(final DependencyProvider dependencyProvider) {
        final Handler provideTimeTickHandler = dependencyProvider.provideTimeTickHandler();
        Preconditions.checkNotNull(provideTimeTickHandler, "Cannot return null from a non-@Nullable @Provides method");
        return provideTimeTickHandler;
    }
    
    @Override
    public Handler get() {
        return provideInstance(this.module);
    }
}
