// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ControlsRequestDialog_Factory implements Factory<ControlsRequestDialog>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<ControlsController> controllerProvider;
    private final Provider<ControlsListingController> controlsListingControllerProvider;
    
    public ControlsRequestDialog_Factory(final Provider<ControlsController> controllerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<ControlsListingController> controlsListingControllerProvider) {
        this.controllerProvider = controllerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.controlsListingControllerProvider = controlsListingControllerProvider;
    }
    
    public static ControlsRequestDialog_Factory create(final Provider<ControlsController> provider, final Provider<BroadcastDispatcher> provider2, final Provider<ControlsListingController> provider3) {
        return new ControlsRequestDialog_Factory(provider, provider2, provider3);
    }
    
    public static ControlsRequestDialog provideInstance(final Provider<ControlsController> provider, final Provider<BroadcastDispatcher> provider2, final Provider<ControlsListingController> provider3) {
        return new ControlsRequestDialog(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public ControlsRequestDialog get() {
        return provideInstance(this.controllerProvider, this.broadcastDispatcherProvider, this.controlsListingControllerProvider);
    }
}
