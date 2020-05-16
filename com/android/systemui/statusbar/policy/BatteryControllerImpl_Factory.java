// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.os.PowerManager;
import com.android.systemui.power.EnhancedEstimates;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.os.Handler;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class BatteryControllerImpl_Factory implements Factory<BatteryControllerImpl>
{
    private final Provider<Handler> bgHandlerProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<EnhancedEstimates> enhancedEstimatesProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<PowerManager> powerManagerProvider;
    
    public BatteryControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<EnhancedEstimates> enhancedEstimatesProvider, final Provider<PowerManager> powerManagerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<Handler> mainHandlerProvider, final Provider<Handler> bgHandlerProvider) {
        this.contextProvider = contextProvider;
        this.enhancedEstimatesProvider = enhancedEstimatesProvider;
        this.powerManagerProvider = powerManagerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.bgHandlerProvider = bgHandlerProvider;
    }
    
    public static BatteryControllerImpl_Factory create(final Provider<Context> provider, final Provider<EnhancedEstimates> provider2, final Provider<PowerManager> provider3, final Provider<BroadcastDispatcher> provider4, final Provider<Handler> provider5, final Provider<Handler> provider6) {
        return new BatteryControllerImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6);
    }
    
    public static BatteryControllerImpl provideInstance(final Provider<Context> provider, final Provider<EnhancedEstimates> provider2, final Provider<PowerManager> provider3, final Provider<BroadcastDispatcher> provider4, final Provider<Handler> provider5, final Provider<Handler> provider6) {
        return new BatteryControllerImpl(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get());
    }
    
    @Override
    public BatteryControllerImpl get() {
        return provideInstance(this.contextProvider, this.enhancedEstimatesProvider, this.powerManagerProvider, this.broadcastDispatcherProvider, this.mainHandlerProvider, this.bgHandlerProvider);
    }
}
