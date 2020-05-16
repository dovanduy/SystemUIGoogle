// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.concurrency;

import dagger.internal.Preconditions;
import java.util.concurrent.Executor;
import dagger.internal.Factory;

public final class ConcurrencyModule_ProvideUiBackgroundExecutorFactory implements Factory<Executor>
{
    private static final ConcurrencyModule_ProvideUiBackgroundExecutorFactory INSTANCE;
    
    static {
        INSTANCE = new ConcurrencyModule_ProvideUiBackgroundExecutorFactory();
    }
    
    public static ConcurrencyModule_ProvideUiBackgroundExecutorFactory create() {
        return ConcurrencyModule_ProvideUiBackgroundExecutorFactory.INSTANCE;
    }
    
    public static Executor provideInstance() {
        return proxyProvideUiBackgroundExecutor();
    }
    
    public static Executor proxyProvideUiBackgroundExecutor() {
        final Executor provideUiBackgroundExecutor = ConcurrencyModule.provideUiBackgroundExecutor();
        Preconditions.checkNotNull(provideUiBackgroundExecutor, "Cannot return null from a non-@Nullable @Provides method");
        return provideUiBackgroundExecutor;
    }
    
    @Override
    public Executor get() {
        return provideInstance();
    }
}
