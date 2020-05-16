// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.colorextraction;

import android.content.Context;
import com.android.systemui.statusbar.policy.ConfigurationController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SysuiColorExtractor_Factory implements Factory<SysuiColorExtractor>
{
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    
    public SysuiColorExtractor_Factory(final Provider<Context> contextProvider, final Provider<ConfigurationController> configurationControllerProvider) {
        this.contextProvider = contextProvider;
        this.configurationControllerProvider = configurationControllerProvider;
    }
    
    public static SysuiColorExtractor_Factory create(final Provider<Context> provider, final Provider<ConfigurationController> provider2) {
        return new SysuiColorExtractor_Factory(provider, provider2);
    }
    
    public static SysuiColorExtractor provideInstance(final Provider<Context> provider, final Provider<ConfigurationController> provider2) {
        return new SysuiColorExtractor(provider.get(), provider2.get());
    }
    
    @Override
    public SysuiColorExtractor get() {
        return provideInstance(this.contextProvider, this.configurationControllerProvider);
    }
}
