// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class FlashlightControllerImpl_Factory implements Factory<FlashlightControllerImpl>
{
    private final Provider<Context> contextProvider;
    
    public FlashlightControllerImpl_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static FlashlightControllerImpl_Factory create(final Provider<Context> provider) {
        return new FlashlightControllerImpl_Factory(provider);
    }
    
    public static FlashlightControllerImpl provideInstance(final Provider<Context> provider) {
        return new FlashlightControllerImpl(provider.get());
    }
    
    @Override
    public FlashlightControllerImpl get() {
        return provideInstance(this.contextProvider);
    }
}
