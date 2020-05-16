// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.os.Handler;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideHandlerFactory implements Factory<Handler>
{
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideHandlerFactory(final DependencyProvider module) {
        this.module = module;
    }
    
    public static DependencyProvider_ProvideHandlerFactory create(final DependencyProvider dependencyProvider) {
        return new DependencyProvider_ProvideHandlerFactory(dependencyProvider);
    }
    
    public static Handler provideInstance(final DependencyProvider dependencyProvider) {
        return proxyProvideHandler(dependencyProvider);
    }
    
    public static Handler proxyProvideHandler(final DependencyProvider dependencyProvider) {
        final Handler provideHandler = dependencyProvider.provideHandler();
        Preconditions.checkNotNull(provideHandler, "Cannot return null from a non-@Nullable @Provides method");
        return provideHandler;
    }
    
    @Override
    public Handler get() {
        return provideInstance(this.module);
    }
}
