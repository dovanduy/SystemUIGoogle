// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.concurrency;

import dagger.internal.Preconditions;
import android.os.Looper;
import javax.inject.Provider;
import java.util.concurrent.Executor;
import dagger.internal.Factory;

public final class ConcurrencyModule_ProvideExecutorFactory implements Factory<Executor>
{
    private final Provider<Looper> looperProvider;
    
    public ConcurrencyModule_ProvideExecutorFactory(final Provider<Looper> looperProvider) {
        this.looperProvider = looperProvider;
    }
    
    public static ConcurrencyModule_ProvideExecutorFactory create(final Provider<Looper> provider) {
        return new ConcurrencyModule_ProvideExecutorFactory(provider);
    }
    
    public static Executor provideInstance(final Provider<Looper> provider) {
        return proxyProvideExecutor(provider.get());
    }
    
    public static Executor proxyProvideExecutor(final Looper looper) {
        final Executor provideExecutor = ConcurrencyModule.provideExecutor(looper);
        Preconditions.checkNotNull(provideExecutor, "Cannot return null from a non-@Nullable @Provides method");
        return provideExecutor;
    }
    
    @Override
    public Executor get() {
        return provideInstance(this.looperProvider);
    }
}
