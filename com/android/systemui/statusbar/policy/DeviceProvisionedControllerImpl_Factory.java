// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.os.Handler;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DeviceProvisionedControllerImpl_Factory implements Factory<DeviceProvisionedControllerImpl>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Handler> mainHandlerProvider;
    
    public DeviceProvisionedControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<Handler> mainHandlerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.contextProvider = contextProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static DeviceProvisionedControllerImpl_Factory create(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<BroadcastDispatcher> provider3) {
        return new DeviceProvisionedControllerImpl_Factory(provider, provider2, provider3);
    }
    
    public static DeviceProvisionedControllerImpl provideInstance(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<BroadcastDispatcher> provider3) {
        return new DeviceProvisionedControllerImpl(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public DeviceProvisionedControllerImpl get() {
        return provideInstance(this.contextProvider, this.mainHandlerProvider, this.broadcastDispatcherProvider);
    }
}
