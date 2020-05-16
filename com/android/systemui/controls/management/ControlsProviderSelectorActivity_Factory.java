// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.broadcast.BroadcastDispatcher;
import java.util.concurrent.Executor;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ControlsProviderSelectorActivity_Factory implements Factory<ControlsProviderSelectorActivity>
{
    private final Provider<Executor> backExecutorProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<ControlsController> controlsControllerProvider;
    private final Provider<Executor> executorProvider;
    private final Provider<ControlsListingController> listingControllerProvider;
    
    public ControlsProviderSelectorActivity_Factory(final Provider<Executor> executorProvider, final Provider<Executor> backExecutorProvider, final Provider<ControlsListingController> listingControllerProvider, final Provider<ControlsController> controlsControllerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider) {
        this.executorProvider = executorProvider;
        this.backExecutorProvider = backExecutorProvider;
        this.listingControllerProvider = listingControllerProvider;
        this.controlsControllerProvider = controlsControllerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
    }
    
    public static ControlsProviderSelectorActivity_Factory create(final Provider<Executor> provider, final Provider<Executor> provider2, final Provider<ControlsListingController> provider3, final Provider<ControlsController> provider4, final Provider<BroadcastDispatcher> provider5) {
        return new ControlsProviderSelectorActivity_Factory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static ControlsProviderSelectorActivity provideInstance(final Provider<Executor> provider, final Provider<Executor> provider2, final Provider<ControlsListingController> provider3, final Provider<ControlsController> provider4, final Provider<BroadcastDispatcher> provider5) {
        return new ControlsProviderSelectorActivity(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    @Override
    public ControlsProviderSelectorActivity get() {
        return provideInstance(this.executorProvider, this.backExecutorProvider, this.listingControllerProvider, this.controlsControllerProvider, this.broadcastDispatcherProvider);
    }
}
