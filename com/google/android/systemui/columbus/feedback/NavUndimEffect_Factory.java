// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.feedback;

import com.android.systemui.statusbar.NavigationBarController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NavUndimEffect_Factory implements Factory<NavUndimEffect>
{
    private final Provider<NavigationBarController> navBarControllerProvider;
    
    public NavUndimEffect_Factory(final Provider<NavigationBarController> navBarControllerProvider) {
        this.navBarControllerProvider = navBarControllerProvider;
    }
    
    public static NavUndimEffect_Factory create(final Provider<NavigationBarController> provider) {
        return new NavUndimEffect_Factory(provider);
    }
    
    public static NavUndimEffect provideInstance(final Provider<NavigationBarController> provider) {
        return new NavUndimEffect(provider.get());
    }
    
    @Override
    public NavUndimEffect get() {
        return provideInstance(this.navBarControllerProvider);
    }
}
