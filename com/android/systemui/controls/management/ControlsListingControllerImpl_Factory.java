// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import java.util.concurrent.Executor;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ControlsListingControllerImpl_Factory implements Factory<ControlsListingControllerImpl>
{
    private final Provider<Context> contextProvider;
    private final Provider<Executor> executorProvider;
    
    public ControlsListingControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<Executor> executorProvider) {
        this.contextProvider = contextProvider;
        this.executorProvider = executorProvider;
    }
    
    public static ControlsListingControllerImpl_Factory create(final Provider<Context> provider, final Provider<Executor> provider2) {
        return new ControlsListingControllerImpl_Factory(provider, provider2);
    }
    
    public static ControlsListingControllerImpl provideInstance(final Provider<Context> provider, final Provider<Executor> provider2) {
        return new ControlsListingControllerImpl(provider.get(), provider2.get());
    }
    
    @Override
    public ControlsListingControllerImpl get() {
        return provideInstance(this.contextProvider, this.executorProvider);
    }
}
