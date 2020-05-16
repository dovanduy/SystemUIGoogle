// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import com.android.systemui.controls.ui.ControlsUiController;
import java.util.Optional;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.dump.DumpManager;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ControlsControllerImpl_Factory implements Factory<ControlsControllerImpl>
{
    private final Provider<ControlsBindingController> bindingControllerProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<DelayableExecutor> executorProvider;
    private final Provider<ControlsListingController> listingControllerProvider;
    private final Provider<Optional<ControlsFavoritePersistenceWrapper>> optionalWrapperProvider;
    private final Provider<ControlsUiController> uiControllerProvider;
    
    public ControlsControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<DelayableExecutor> executorProvider, final Provider<ControlsUiController> uiControllerProvider, final Provider<ControlsBindingController> bindingControllerProvider, final Provider<ControlsListingController> listingControllerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<Optional<ControlsFavoritePersistenceWrapper>> optionalWrapperProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.contextProvider = contextProvider;
        this.executorProvider = executorProvider;
        this.uiControllerProvider = uiControllerProvider;
        this.bindingControllerProvider = bindingControllerProvider;
        this.listingControllerProvider = listingControllerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.optionalWrapperProvider = optionalWrapperProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static ControlsControllerImpl_Factory create(final Provider<Context> provider, final Provider<DelayableExecutor> provider2, final Provider<ControlsUiController> provider3, final Provider<ControlsBindingController> provider4, final Provider<ControlsListingController> provider5, final Provider<BroadcastDispatcher> provider6, final Provider<Optional<ControlsFavoritePersistenceWrapper>> provider7, final Provider<DumpManager> provider8) {
        return new ControlsControllerImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
    }
    
    public static ControlsControllerImpl provideInstance(final Provider<Context> provider, final Provider<DelayableExecutor> provider2, final Provider<ControlsUiController> provider3, final Provider<ControlsBindingController> provider4, final Provider<ControlsListingController> provider5, final Provider<BroadcastDispatcher> provider6, final Provider<Optional<ControlsFavoritePersistenceWrapper>> provider7, final Provider<DumpManager> provider8) {
        return new ControlsControllerImpl(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get());
    }
    
    @Override
    public ControlsControllerImpl get() {
        return provideInstance(this.contextProvider, this.executorProvider, this.uiControllerProvider, this.bindingControllerProvider, this.listingControllerProvider, this.broadcastDispatcherProvider, this.optionalWrapperProvider, this.dumpManagerProvider);
    }
}
