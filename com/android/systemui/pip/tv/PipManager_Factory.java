// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.tv;

import com.android.systemui.pip.PipSurfaceTransactionHelper;
import com.android.systemui.pip.PipBoundsHandler;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PipManager_Factory implements Factory<PipManager>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<PipBoundsHandler> pipBoundsHandlerProvider;
    private final Provider<PipSurfaceTransactionHelper> surfaceTransactionHelperProvider;
    
    public PipManager_Factory(final Provider<Context> contextProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<PipBoundsHandler> pipBoundsHandlerProvider, final Provider<PipSurfaceTransactionHelper> surfaceTransactionHelperProvider) {
        this.contextProvider = contextProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.pipBoundsHandlerProvider = pipBoundsHandlerProvider;
        this.surfaceTransactionHelperProvider = surfaceTransactionHelperProvider;
    }
    
    public static PipManager_Factory create(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<PipBoundsHandler> provider3, final Provider<PipSurfaceTransactionHelper> provider4) {
        return new PipManager_Factory(provider, provider2, provider3, provider4);
    }
    
    public static PipManager provideInstance(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<PipBoundsHandler> provider3, final Provider<PipSurfaceTransactionHelper> provider4) {
        return new PipManager(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public PipManager get() {
        return provideInstance(this.contextProvider, this.broadcastDispatcherProvider, this.pipBoundsHandlerProvider, this.surfaceTransactionHelperProvider);
    }
}
