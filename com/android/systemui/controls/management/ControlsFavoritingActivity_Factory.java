// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import java.util.concurrent.Executor;
import com.android.systemui.controls.controller.ControlsControllerImpl;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ControlsFavoritingActivity_Factory implements Factory<ControlsFavoritingActivity>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<ControlsControllerImpl> controllerProvider;
    private final Provider<Executor> executorProvider;
    private final Provider<ControlsListingController> listingControllerProvider;
    
    public ControlsFavoritingActivity_Factory(final Provider<Executor> executorProvider, final Provider<ControlsControllerImpl> controllerProvider, final Provider<ControlsListingController> listingControllerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.executorProvider = executorProvider;
        this.controllerProvider = controllerProvider;
        this.listingControllerProvider = listingControllerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static ControlsFavoritingActivity_Factory create(final Provider<Executor> provider, final Provider<ControlsControllerImpl> provider2, final Provider<ControlsListingController> provider3, final Provider<BroadcastDispatcher> provider4) {
        return new ControlsFavoritingActivity_Factory(provider, provider2, provider3, provider4);
    }
    
    public static ControlsFavoritingActivity provideInstance(final Provider<Executor> provider, final Provider<ControlsControllerImpl> provider2, final Provider<ControlsListingController> provider3, final Provider<BroadcastDispatcher> provider4) {
        return new ControlsFavoritingActivity(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public ControlsFavoritingActivity get() {
        return provideInstance(this.executorProvider, this.controllerProvider, this.listingControllerProvider, this.broadcastDispatcherProvider);
    }
}
