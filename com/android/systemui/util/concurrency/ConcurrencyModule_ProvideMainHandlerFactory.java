// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.concurrency;

import dagger.internal.Preconditions;
import android.os.Looper;
import javax.inject.Provider;
import android.os.Handler;
import dagger.internal.Factory;

public final class ConcurrencyModule_ProvideMainHandlerFactory implements Factory<Handler>
{
    private final Provider<Looper> mainLooperProvider;
    
    public ConcurrencyModule_ProvideMainHandlerFactory(final Provider<Looper> mainLooperProvider) {
        this.mainLooperProvider = mainLooperProvider;
    }
    
    public static ConcurrencyModule_ProvideMainHandlerFactory create(final Provider<Looper> provider) {
        return new ConcurrencyModule_ProvideMainHandlerFactory(provider);
    }
    
    public static Handler provideInstance(final Provider<Looper> provider) {
        return proxyProvideMainHandler(provider.get());
    }
    
    public static Handler proxyProvideMainHandler(final Looper looper) {
        final Handler provideMainHandler = ConcurrencyModule.provideMainHandler(looper);
        Preconditions.checkNotNull(provideMainHandler, "Cannot return null from a non-@Nullable @Provides method");
        return provideMainHandler;
    }
    
    @Override
    public Handler get() {
        return provideInstance(this.mainLooperProvider);
    }
}
