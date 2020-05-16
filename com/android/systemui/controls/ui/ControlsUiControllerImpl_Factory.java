// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import dagger.internal.DoubleCheck;
import android.content.SharedPreferences;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.controls.controller.ControlsController;
import android.content.Context;
import com.android.systemui.util.concurrency.DelayableExecutor;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ControlsUiControllerImpl_Factory implements Factory<ControlsUiControllerImpl>
{
    private final Provider<DelayableExecutor> bgExecutorProvider;
    private final Provider<Context> contextProvider;
    private final Provider<ControlsController> controlsControllerProvider;
    private final Provider<ControlsListingController> controlsListingControllerProvider;
    private final Provider<SharedPreferences> sharedPreferencesProvider;
    private final Provider<DelayableExecutor> uiExecutorProvider;
    
    public ControlsUiControllerImpl_Factory(final Provider<ControlsController> controlsControllerProvider, final Provider<Context> contextProvider, final Provider<DelayableExecutor> uiExecutorProvider, final Provider<DelayableExecutor> bgExecutorProvider, final Provider<ControlsListingController> controlsListingControllerProvider, final Provider<SharedPreferences> sharedPreferencesProvider) {
        this.controlsControllerProvider = controlsControllerProvider;
        this.contextProvider = contextProvider;
        this.uiExecutorProvider = uiExecutorProvider;
        this.bgExecutorProvider = bgExecutorProvider;
        this.controlsListingControllerProvider = controlsListingControllerProvider;
        this.sharedPreferencesProvider = sharedPreferencesProvider;
    }
    
    public static ControlsUiControllerImpl_Factory create(final Provider<ControlsController> provider, final Provider<Context> provider2, final Provider<DelayableExecutor> provider3, final Provider<DelayableExecutor> provider4, final Provider<ControlsListingController> provider5, final Provider<SharedPreferences> provider6) {
        return new ControlsUiControllerImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6);
    }
    
    public static ControlsUiControllerImpl provideInstance(final Provider<ControlsController> provider, final Provider<Context> provider2, final Provider<DelayableExecutor> provider3, final Provider<DelayableExecutor> provider4, final Provider<ControlsListingController> provider5, final Provider<SharedPreferences> provider6) {
        return new ControlsUiControllerImpl(DoubleCheck.lazy(provider), provider2.get(), provider3.get(), provider4.get(), DoubleCheck.lazy(provider5), provider6.get());
    }
    
    @Override
    public ControlsUiControllerImpl get() {
        return provideInstance(this.controlsControllerProvider, this.contextProvider, this.uiExecutorProvider, this.bgExecutorProvider, this.controlsListingControllerProvider, this.sharedPreferencesProvider);
    }
}
