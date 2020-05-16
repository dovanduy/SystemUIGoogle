// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.concurrency;

import dagger.internal.Preconditions;
import android.os.Looper;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ConcurrencyModule_ProvideMainDelayableExecutorFactory implements Factory<DelayableExecutor>
{
    private final Provider<Looper> looperProvider;
    
    public ConcurrencyModule_ProvideMainDelayableExecutorFactory(final Provider<Looper> looperProvider) {
        this.looperProvider = looperProvider;
    }
    
    public static ConcurrencyModule_ProvideMainDelayableExecutorFactory create(final Provider<Looper> provider) {
        return new ConcurrencyModule_ProvideMainDelayableExecutorFactory(provider);
    }
    
    public static DelayableExecutor provideInstance(final Provider<Looper> provider) {
        return proxyProvideMainDelayableExecutor(provider.get());
    }
    
    public static DelayableExecutor proxyProvideMainDelayableExecutor(final Looper looper) {
        final DelayableExecutor provideMainDelayableExecutor = ConcurrencyModule.provideMainDelayableExecutor(looper);
        Preconditions.checkNotNull(provideMainDelayableExecutor, "Cannot return null from a non-@Nullable @Provides method");
        return provideMainDelayableExecutor;
    }
    
    @Override
    public DelayableExecutor get() {
        return provideInstance(this.looperProvider);
    }
}
