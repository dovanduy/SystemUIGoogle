// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.fragments;

import com.android.systemui.dagger.SystemUIRootComponent;
import com.android.systemui.statusbar.policy.ConfigurationController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class FragmentService_Factory implements Factory<FragmentService>
{
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<SystemUIRootComponent> rootComponentProvider;
    
    public FragmentService_Factory(final Provider<SystemUIRootComponent> rootComponentProvider, final Provider<ConfigurationController> configurationControllerProvider) {
        this.rootComponentProvider = rootComponentProvider;
        this.configurationControllerProvider = configurationControllerProvider;
    }
    
    public static FragmentService_Factory create(final Provider<SystemUIRootComponent> provider, final Provider<ConfigurationController> provider2) {
        return new FragmentService_Factory(provider, provider2);
    }
    
    public static FragmentService provideInstance(final Provider<SystemUIRootComponent> provider, final Provider<ConfigurationController> provider2) {
        return new FragmentService(provider.get(), provider2.get());
    }
    
    @Override
    public FragmentService get() {
        return provideInstance(this.rootComponentProvider, this.configurationControllerProvider);
    }
}
