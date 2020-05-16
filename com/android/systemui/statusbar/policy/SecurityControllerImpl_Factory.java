// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.os.Handler;
import java.util.concurrent.Executor;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SecurityControllerImpl_Factory implements Factory<SecurityControllerImpl>
{
    private final Provider<Executor> bgExecutorProvider;
    private final Provider<Handler> bgHandlerProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    
    public SecurityControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<Handler> bgHandlerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<Executor> bgExecutorProvider) {
        this.contextProvider = contextProvider;
        this.bgHandlerProvider = bgHandlerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.bgExecutorProvider = bgExecutorProvider;
    }
    
    public static SecurityControllerImpl_Factory create(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<BroadcastDispatcher> provider3, final Provider<Executor> provider4) {
        return new SecurityControllerImpl_Factory(provider, provider2, provider3, provider4);
    }
    
    public static SecurityControllerImpl provideInstance(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<BroadcastDispatcher> provider3, final Provider<Executor> provider4) {
        return new SecurityControllerImpl(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public SecurityControllerImpl get() {
        return provideInstance(this.contextProvider, this.bgHandlerProvider, this.broadcastDispatcherProvider, this.bgExecutorProvider);
    }
}
