// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.util.DeviceConfigProxy;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ForegroundServiceDismissalFeatureController_Factory implements Factory<ForegroundServiceDismissalFeatureController>
{
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigProxy> proxyProvider;
    
    public ForegroundServiceDismissalFeatureController_Factory(final Provider<DeviceConfigProxy> proxyProvider, final Provider<Context> contextProvider) {
        this.proxyProvider = proxyProvider;
        this.contextProvider = contextProvider;
    }
    
    public static ForegroundServiceDismissalFeatureController_Factory create(final Provider<DeviceConfigProxy> provider, final Provider<Context> provider2) {
        return new ForegroundServiceDismissalFeatureController_Factory(provider, provider2);
    }
    
    public static ForegroundServiceDismissalFeatureController provideInstance(final Provider<DeviceConfigProxy> provider, final Provider<Context> provider2) {
        return new ForegroundServiceDismissalFeatureController(provider.get(), provider2.get());
    }
    
    @Override
    public ForegroundServiceDismissalFeatureController get() {
        return provideInstance(this.proxyProvider, this.contextProvider);
    }
}
