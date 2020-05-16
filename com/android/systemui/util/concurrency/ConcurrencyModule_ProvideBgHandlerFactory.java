// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.concurrency;

import dagger.internal.Preconditions;
import android.os.Looper;
import javax.inject.Provider;
import android.os.Handler;
import dagger.internal.Factory;

public final class ConcurrencyModule_ProvideBgHandlerFactory implements Factory<Handler>
{
    private final Provider<Looper> bgLooperProvider;
    
    public ConcurrencyModule_ProvideBgHandlerFactory(final Provider<Looper> bgLooperProvider) {
        this.bgLooperProvider = bgLooperProvider;
    }
    
    public static ConcurrencyModule_ProvideBgHandlerFactory create(final Provider<Looper> provider) {
        return new ConcurrencyModule_ProvideBgHandlerFactory(provider);
    }
    
    public static Handler provideInstance(final Provider<Looper> provider) {
        return proxyProvideBgHandler(provider.get());
    }
    
    public static Handler proxyProvideBgHandler(final Looper looper) {
        final Handler provideBgHandler = ConcurrencyModule.provideBgHandler(looper);
        Preconditions.checkNotNull(provideBgHandler, "Cannot return null from a non-@Nullable @Provides method");
        return provideBgHandler;
    }
    
    @Override
    public Handler get() {
        return provideInstance(this.bgLooperProvider);
    }
}
