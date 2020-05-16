// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.phone.NavigationModeController;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NonGesturalNavigation_Factory implements Factory<NonGesturalNavigation>
{
    private final Provider<Context> contextProvider;
    private final Provider<NavigationModeController> modeControllerProvider;
    
    public NonGesturalNavigation_Factory(final Provider<Context> contextProvider, final Provider<NavigationModeController> modeControllerProvider) {
        this.contextProvider = contextProvider;
        this.modeControllerProvider = modeControllerProvider;
    }
    
    public static NonGesturalNavigation_Factory create(final Provider<Context> provider, final Provider<NavigationModeController> provider2) {
        return new NonGesturalNavigation_Factory(provider, provider2);
    }
    
    public static NonGesturalNavigation provideInstance(final Provider<Context> provider, final Provider<NavigationModeController> provider2) {
        return new NonGesturalNavigation(provider.get(), DoubleCheck.lazy(provider2));
    }
    
    @Override
    public NonGesturalNavigation get() {
        return provideInstance(this.contextProvider, this.modeControllerProvider);
    }
}
