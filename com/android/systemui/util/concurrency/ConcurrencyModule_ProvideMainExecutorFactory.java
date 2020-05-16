// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.concurrency;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import java.util.concurrent.Executor;
import dagger.internal.Factory;

public final class ConcurrencyModule_ProvideMainExecutorFactory implements Factory<Executor>
{
    private final Provider<Context> contextProvider;
    
    public ConcurrencyModule_ProvideMainExecutorFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static ConcurrencyModule_ProvideMainExecutorFactory create(final Provider<Context> provider) {
        return new ConcurrencyModule_ProvideMainExecutorFactory(provider);
    }
    
    public static Executor provideInstance(final Provider<Context> provider) {
        return proxyProvideMainExecutor(provider.get());
    }
    
    public static Executor proxyProvideMainExecutor(final Context context) {
        final Executor provideMainExecutor = ConcurrencyModule.provideMainExecutor(context);
        Preconditions.checkNotNull(provideMainExecutor, "Cannot return null from a non-@Nullable @Provides method");
        return provideMainExecutor;
    }
    
    @Override
    public Executor get() {
        return provideInstance(this.contextProvider);
    }
}
