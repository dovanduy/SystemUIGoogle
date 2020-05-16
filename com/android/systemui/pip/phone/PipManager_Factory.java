// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.phone;

import com.android.systemui.pip.PipSurfaceTransactionHelper;
import com.android.systemui.pip.PipSnapAlgorithm;
import com.android.systemui.pip.PipBoundsHandler;
import com.android.systemui.util.FloatingContentCoordinator;
import com.android.systemui.wm.DisplayController;
import com.android.systemui.util.DeviceConfigProxy;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PipManager_Factory implements Factory<PipManager>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigProxy> deviceConfigProvider;
    private final Provider<DisplayController> displayControllerProvider;
    private final Provider<FloatingContentCoordinator> floatingContentCoordinatorProvider;
    private final Provider<PipBoundsHandler> pipBoundsHandlerProvider;
    private final Provider<PipSnapAlgorithm> pipSnapAlgorithmProvider;
    private final Provider<PipSurfaceTransactionHelper> surfaceTransactionHelperProvider;
    
    public PipManager_Factory(final Provider<Context> contextProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<DisplayController> displayControllerProvider, final Provider<FloatingContentCoordinator> floatingContentCoordinatorProvider, final Provider<DeviceConfigProxy> deviceConfigProvider, final Provider<PipBoundsHandler> pipBoundsHandlerProvider, final Provider<PipSnapAlgorithm> pipSnapAlgorithmProvider, final Provider<PipSurfaceTransactionHelper> surfaceTransactionHelperProvider) {
        this.contextProvider = contextProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.displayControllerProvider = displayControllerProvider;
        this.floatingContentCoordinatorProvider = floatingContentCoordinatorProvider;
        this.deviceConfigProvider = deviceConfigProvider;
        this.pipBoundsHandlerProvider = pipBoundsHandlerProvider;
        this.pipSnapAlgorithmProvider = pipSnapAlgorithmProvider;
        this.surfaceTransactionHelperProvider = surfaceTransactionHelperProvider;
    }
    
    public static PipManager_Factory create(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<DisplayController> provider3, final Provider<FloatingContentCoordinator> provider4, final Provider<DeviceConfigProxy> provider5, final Provider<PipBoundsHandler> provider6, final Provider<PipSnapAlgorithm> provider7, final Provider<PipSurfaceTransactionHelper> provider8) {
        return new PipManager_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
    }
    
    public static PipManager provideInstance(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<DisplayController> provider3, final Provider<FloatingContentCoordinator> provider4, final Provider<DeviceConfigProxy> provider5, final Provider<PipBoundsHandler> provider6, final Provider<PipSnapAlgorithm> provider7, final Provider<PipSurfaceTransactionHelper> provider8) {
        return new PipManager(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get());
    }
    
    @Override
    public PipManager get() {
        return provideInstance(this.contextProvider, this.broadcastDispatcherProvider, this.displayControllerProvider, this.floatingContentCoordinatorProvider, this.deviceConfigProvider, this.pipBoundsHandlerProvider, this.pipSnapAlgorithmProvider, this.surfaceTransactionHelperProvider);
    }
}
