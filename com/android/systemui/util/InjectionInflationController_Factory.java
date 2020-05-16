// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import com.android.systemui.dagger.SystemUIRootComponent;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class InjectionInflationController_Factory implements Factory<InjectionInflationController>
{
    private final Provider<SystemUIRootComponent> rootComponentProvider;
    
    public InjectionInflationController_Factory(final Provider<SystemUIRootComponent> rootComponentProvider) {
        this.rootComponentProvider = rootComponentProvider;
    }
    
    public static InjectionInflationController_Factory create(final Provider<SystemUIRootComponent> provider) {
        return new InjectionInflationController_Factory(provider);
    }
    
    public static InjectionInflationController provideInstance(final Provider<SystemUIRootComponent> provider) {
        return new InjectionInflationController(provider.get());
    }
    
    @Override
    public InjectionInflationController get() {
        return provideInstance(this.rootComponentProvider);
    }
}
