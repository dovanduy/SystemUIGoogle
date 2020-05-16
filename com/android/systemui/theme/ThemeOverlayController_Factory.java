// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.theme;

import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.os.Handler;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ThemeOverlayController_Factory implements Factory<ThemeOverlayController>
{
    private final Provider<Handler> bgHandlerProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    
    public ThemeOverlayController_Factory(final Provider<Context> contextProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<Handler> bgHandlerProvider) {
        this.contextProvider = contextProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.bgHandlerProvider = bgHandlerProvider;
    }
    
    public static ThemeOverlayController_Factory create(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<Handler> provider3) {
        return new ThemeOverlayController_Factory(provider, provider2, provider3);
    }
    
    public static ThemeOverlayController provideInstance(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<Handler> provider3) {
        return new ThemeOverlayController(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public ThemeOverlayController get() {
        return provideInstance(this.contextProvider, this.broadcastDispatcherProvider, this.bgHandlerProvider);
    }
}
