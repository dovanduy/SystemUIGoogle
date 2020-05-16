// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.concurrency;

import dagger.internal.Preconditions;
import android.os.Looper;
import javax.inject.Provider;
import java.util.concurrent.Executor;
import dagger.internal.Factory;

public final class ConcurrencyModule_ProvideBackgroundExecutorFactory implements Factory<Executor>
{
    private final Provider<Looper> looperProvider;
    
    public ConcurrencyModule_ProvideBackgroundExecutorFactory(final Provider<Looper> looperProvider) {
        this.looperProvider = looperProvider;
    }
    
    public static ConcurrencyModule_ProvideBackgroundExecutorFactory create(final Provider<Looper> provider) {
        return new ConcurrencyModule_ProvideBackgroundExecutorFactory(provider);
    }
    
    public static Executor provideInstance(final Provider<Looper> provider) {
        return proxyProvideBackgroundExecutor(provider.get());
    }
    
    public static Executor proxyProvideBackgroundExecutor(final Looper looper) {
        final Executor provideBackgroundExecutor = ConcurrencyModule.provideBackgroundExecutor(looper);
        Preconditions.checkNotNull(provideBackgroundExecutor, "Cannot return null from a non-@Nullable @Provides method");
        return provideBackgroundExecutor;
    }
    
    @Override
    public Executor get() {
        return provideInstance(this.looperProvider);
    }
}
