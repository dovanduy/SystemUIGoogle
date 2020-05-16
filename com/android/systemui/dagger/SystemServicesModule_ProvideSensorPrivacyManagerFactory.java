// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.hardware.SensorPrivacyManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideSensorPrivacyManagerFactory implements Factory<SensorPrivacyManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideSensorPrivacyManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideSensorPrivacyManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideSensorPrivacyManagerFactory(provider);
    }
    
    public static SensorPrivacyManager provideInstance(final Provider<Context> provider) {
        return proxyProvideSensorPrivacyManager(provider.get());
    }
    
    public static SensorPrivacyManager proxyProvideSensorPrivacyManager(final Context context) {
        final SensorPrivacyManager provideSensorPrivacyManager = SystemServicesModule.provideSensorPrivacyManager(context);
        Preconditions.checkNotNull(provideSensorPrivacyManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideSensorPrivacyManager;
    }
    
    @Override
    public SensorPrivacyManager get() {
        return provideInstance(this.contextProvider);
    }
}
