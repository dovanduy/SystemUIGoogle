// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SliceBroadcastRelayHandler_Factory implements Factory<SliceBroadcastRelayHandler>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    
    public SliceBroadcastRelayHandler_Factory(final Provider<Context> contextProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.contextProvider = contextProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static SliceBroadcastRelayHandler_Factory create(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2) {
        return new SliceBroadcastRelayHandler_Factory(provider, provider2);
    }
    
    public static SliceBroadcastRelayHandler provideInstance(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2) {
        return new SliceBroadcastRelayHandler(provider.get(), provider2.get());
    }
    
    @Override
    public SliceBroadcastRelayHandler get() {
        return provideInstance(this.contextProvider, this.broadcastDispatcherProvider);
    }
}
