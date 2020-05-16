// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.net.wifi.WifiManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideWifiManagerFactory implements Factory<WifiManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideWifiManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideWifiManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideWifiManagerFactory(provider);
    }
    
    public static WifiManager provideInstance(final Provider<Context> provider) {
        return proxyProvideWifiManager(provider.get());
    }
    
    public static WifiManager proxyProvideWifiManager(final Context context) {
        final WifiManager provideWifiManager = SystemServicesModule.provideWifiManager(context);
        Preconditions.checkNotNull(provideWifiManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideWifiManager;
    }
    
    @Override
    public WifiManager get() {
        return provideInstance(this.contextProvider);
    }
}
