// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.app.admin.DevicePolicyManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideDevicePolicyManagerFactory implements Factory<DevicePolicyManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideDevicePolicyManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideDevicePolicyManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideDevicePolicyManagerFactory(provider);
    }
    
    public static DevicePolicyManager provideInstance(final Provider<Context> provider) {
        return proxyProvideDevicePolicyManager(provider.get());
    }
    
    public static DevicePolicyManager proxyProvideDevicePolicyManager(final Context context) {
        final DevicePolicyManager provideDevicePolicyManager = SystemServicesModule.provideDevicePolicyManager(context);
        Preconditions.checkNotNull(provideDevicePolicyManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideDevicePolicyManager;
    }
    
    @Override
    public DevicePolicyManager get() {
        return provideInstance(this.contextProvider);
    }
}
