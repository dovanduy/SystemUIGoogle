// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.os.Handler;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ZenModeControllerImpl_Factory implements Factory<ZenModeControllerImpl>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Handler> handlerProvider;
    
    public ZenModeControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<Handler> handlerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.contextProvider = contextProvider;
        this.handlerProvider = handlerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static ZenModeControllerImpl_Factory create(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<BroadcastDispatcher> provider3) {
        return new ZenModeControllerImpl_Factory(provider, provider2, provider3);
    }
    
    public static ZenModeControllerImpl provideInstance(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<BroadcastDispatcher> provider3) {
        return new ZenModeControllerImpl(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public ZenModeControllerImpl get() {
        return provideInstance(this.contextProvider, this.handlerProvider, this.broadcastDispatcherProvider);
    }
}
