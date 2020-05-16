// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.tuner.TunerService;
import android.content.res.Resources;
import android.os.PowerManager;
import android.hardware.display.AmbientDisplayConfiguration;
import com.android.systemui.doze.AlwaysOnDisplayPolicy;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DozeParameters_Factory implements Factory<DozeParameters>
{
    private final Provider<AlwaysOnDisplayPolicy> alwaysOnDisplayPolicyProvider;
    private final Provider<AmbientDisplayConfiguration> ambientDisplayConfigurationProvider;
    private final Provider<PowerManager> powerManagerProvider;
    private final Provider<Resources> resourcesProvider;
    private final Provider<TunerService> tunerServiceProvider;
    
    public DozeParameters_Factory(final Provider<Resources> resourcesProvider, final Provider<AmbientDisplayConfiguration> ambientDisplayConfigurationProvider, final Provider<AlwaysOnDisplayPolicy> alwaysOnDisplayPolicyProvider, final Provider<PowerManager> powerManagerProvider, final Provider<TunerService> tunerServiceProvider) {
        this.resourcesProvider = resourcesProvider;
        this.ambientDisplayConfigurationProvider = ambientDisplayConfigurationProvider;
        this.alwaysOnDisplayPolicyProvider = alwaysOnDisplayPolicyProvider;
        this.powerManagerProvider = powerManagerProvider;
        this.tunerServiceProvider = tunerServiceProvider;
    }
    
    public static DozeParameters_Factory create(final Provider<Resources> provider, final Provider<AmbientDisplayConfiguration> provider2, final Provider<AlwaysOnDisplayPolicy> provider3, final Provider<PowerManager> provider4, final Provider<TunerService> provider5) {
        return new DozeParameters_Factory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static DozeParameters provideInstance(final Provider<Resources> provider, final Provider<AmbientDisplayConfiguration> provider2, final Provider<AlwaysOnDisplayPolicy> provider3, final Provider<PowerManager> provider4, final Provider<TunerService> provider5) {
        return new DozeParameters(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    @Override
    public DozeParameters get() {
        return provideInstance(this.resourcesProvider, this.ambientDisplayConfigurationProvider, this.alwaysOnDisplayPolicyProvider, this.powerManagerProvider, this.tunerServiceProvider);
    }
}
