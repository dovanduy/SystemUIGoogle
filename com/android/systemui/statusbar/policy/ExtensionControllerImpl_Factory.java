// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.systemui.tuner.TunerService;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.util.leak.LeakDetector;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ExtensionControllerImpl_Factory implements Factory<ExtensionControllerImpl>
{
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<LeakDetector> leakDetectorProvider;
    private final Provider<PluginManager> pluginManagerProvider;
    private final Provider<TunerService> tunerServiceProvider;
    
    public ExtensionControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<LeakDetector> leakDetectorProvider, final Provider<PluginManager> pluginManagerProvider, final Provider<TunerService> tunerServiceProvider, final Provider<ConfigurationController> configurationControllerProvider) {
        this.contextProvider = contextProvider;
        this.leakDetectorProvider = leakDetectorProvider;
        this.pluginManagerProvider = pluginManagerProvider;
        this.tunerServiceProvider = tunerServiceProvider;
        this.configurationControllerProvider = configurationControllerProvider;
    }
    
    public static ExtensionControllerImpl_Factory create(final Provider<Context> provider, final Provider<LeakDetector> provider2, final Provider<PluginManager> provider3, final Provider<TunerService> provider4, final Provider<ConfigurationController> provider5) {
        return new ExtensionControllerImpl_Factory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static ExtensionControllerImpl provideInstance(final Provider<Context> provider, final Provider<LeakDetector> provider2, final Provider<PluginManager> provider3, final Provider<TunerService> provider4, final Provider<ConfigurationController> provider5) {
        return new ExtensionControllerImpl(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    @Override
    public ExtensionControllerImpl get() {
        return provideInstance(this.contextProvider, this.leakDetectorProvider, this.pluginManagerProvider, this.tunerServiceProvider, this.configurationControllerProvider);
    }
}
