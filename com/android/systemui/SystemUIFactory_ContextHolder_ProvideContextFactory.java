// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import dagger.internal.Preconditions;
import android.content.Context;
import dagger.internal.Factory;

public final class SystemUIFactory_ContextHolder_ProvideContextFactory implements Factory<Context>
{
    private final SystemUIFactory.ContextHolder module;
    
    public SystemUIFactory_ContextHolder_ProvideContextFactory(final SystemUIFactory.ContextHolder module) {
        this.module = module;
    }
    
    public static SystemUIFactory_ContextHolder_ProvideContextFactory create(final SystemUIFactory.ContextHolder contextHolder) {
        return new SystemUIFactory_ContextHolder_ProvideContextFactory(contextHolder);
    }
    
    public static Context provideInstance(final SystemUIFactory.ContextHolder contextHolder) {
        return proxyProvideContext(contextHolder);
    }
    
    public static Context proxyProvideContext(final SystemUIFactory.ContextHolder contextHolder) {
        final Context provideContext = contextHolder.provideContext();
        Preconditions.checkNotNull(provideContext, "Cannot return null from a non-@Nullable @Provides method");
        return provideContext;
    }
    
    @Override
    public Context get() {
        return provideInstance(this.module);
    }
}
