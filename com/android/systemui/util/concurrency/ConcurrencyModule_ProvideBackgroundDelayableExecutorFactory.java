// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.concurrency;

import dagger.internal.Preconditions;
import android.os.Looper;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ConcurrencyModule_ProvideBackgroundDelayableExecutorFactory implements Factory<DelayableExecutor>
{
    private final Provider<Looper> looperProvider;
    
    public ConcurrencyModule_ProvideBackgroundDelayableExecutorFactory(final Provider<Looper> looperProvider) {
        this.looperProvider = looperProvider;
    }
    
    public static ConcurrencyModule_ProvideBackgroundDelayableExecutorFactory create(final Provider<Looper> provider) {
        return new ConcurrencyModule_ProvideBackgroundDelayableExecutorFactory(provider);
    }
    
    public static DelayableExecutor provideInstance(final Provider<Looper> provider) {
        return proxyProvideBackgroundDelayableExecutor(provider.get());
    }
    
    public static DelayableExecutor proxyProvideBackgroundDelayableExecutor(final Looper looper) {
        final DelayableExecutor provideBackgroundDelayableExecutor = ConcurrencyModule.provideBackgroundDelayableExecutor(looper);
        Preconditions.checkNotNull(provideBackgroundDelayableExecutor, "Cannot return null from a non-@Nullable @Provides method");
        return provideBackgroundDelayableExecutor;
    }
    
    @Override
    public DelayableExecutor get() {
        return provideInstance(this.looperProvider);
    }
}
