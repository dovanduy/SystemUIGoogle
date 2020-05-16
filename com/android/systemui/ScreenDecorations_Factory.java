// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import com.android.systemui.tuner.TunerService;
import android.os.Handler;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ScreenDecorations_Factory implements Factory<ScreenDecorations>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<TunerService> tunerServiceProvider;
    
    public ScreenDecorations_Factory(final Provider<Context> contextProvider, final Provider<Handler> handlerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<TunerService> tunerServiceProvider) {
        this.contextProvider = contextProvider;
        this.handlerProvider = handlerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.tunerServiceProvider = tunerServiceProvider;
    }
    
    public static ScreenDecorations_Factory create(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<BroadcastDispatcher> provider3, final Provider<TunerService> provider4) {
        return new ScreenDecorations_Factory(provider, provider2, provider3, provider4);
    }
    
    public static ScreenDecorations provideInstance(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<BroadcastDispatcher> provider3, final Provider<TunerService> provider4) {
        return new ScreenDecorations(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public ScreenDecorations get() {
        return provideInstance(this.contextProvider, this.handlerProvider, this.broadcastDispatcherProvider, this.tunerServiceProvider);
    }
}
