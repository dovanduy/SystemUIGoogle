// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import dagger.internal.DoubleCheck;
import android.content.Context;
import com.android.systemui.util.concurrency.DelayableExecutor;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ControlsBindingControllerImpl_Factory implements Factory<ControlsBindingControllerImpl>
{
    private final Provider<DelayableExecutor> backgroundExecutorProvider;
    private final Provider<Context> contextProvider;
    private final Provider<ControlsController> controllerProvider;
    
    public ControlsBindingControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<DelayableExecutor> backgroundExecutorProvider, final Provider<ControlsController> controllerProvider) {
        this.contextProvider = contextProvider;
        this.backgroundExecutorProvider = backgroundExecutorProvider;
        this.controllerProvider = controllerProvider;
    }
    
    public static ControlsBindingControllerImpl_Factory create(final Provider<Context> provider, final Provider<DelayableExecutor> provider2, final Provider<ControlsController> provider3) {
        return new ControlsBindingControllerImpl_Factory(provider, provider2, provider3);
    }
    
    public static ControlsBindingControllerImpl provideInstance(final Provider<Context> provider, final Provider<DelayableExecutor> provider2, final Provider<ControlsController> provider3) {
        return new ControlsBindingControllerImpl(provider.get(), provider2.get(), DoubleCheck.lazy(provider3));
    }
    
    @Override
    public ControlsBindingControllerImpl get() {
        return provideInstance(this.contextProvider, this.backgroundExecutorProvider, this.controllerProvider);
    }
}
