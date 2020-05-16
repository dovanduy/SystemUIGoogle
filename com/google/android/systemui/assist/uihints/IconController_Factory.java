// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.android.systemui.statusbar.policy.ConfigurationController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class IconController_Factory implements Factory<IconController>
{
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<LayoutInflater> inflaterProvider;
    private final Provider<ViewGroup> parentProvider;
    
    public IconController_Factory(final Provider<LayoutInflater> inflaterProvider, final Provider<ViewGroup> parentProvider, final Provider<ConfigurationController> configurationControllerProvider) {
        this.inflaterProvider = inflaterProvider;
        this.parentProvider = parentProvider;
        this.configurationControllerProvider = configurationControllerProvider;
    }
    
    public static IconController_Factory create(final Provider<LayoutInflater> provider, final Provider<ViewGroup> provider2, final Provider<ConfigurationController> provider3) {
        return new IconController_Factory(provider, provider2, provider3);
    }
    
    public static IconController provideInstance(final Provider<LayoutInflater> provider, final Provider<ViewGroup> provider2, final Provider<ConfigurationController> provider3) {
        return new IconController(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public IconController get() {
        return provideInstance(this.inflaterProvider, this.parentProvider, this.configurationControllerProvider);
    }
}
