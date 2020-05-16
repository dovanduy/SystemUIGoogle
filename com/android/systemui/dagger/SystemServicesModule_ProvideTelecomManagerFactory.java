// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import android.content.Context;
import javax.inject.Provider;
import android.telecom.TelecomManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideTelecomManagerFactory implements Factory<TelecomManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideTelecomManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideTelecomManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideTelecomManagerFactory(provider);
    }
    
    public static TelecomManager provideInstance(final Provider<Context> provider) {
        return proxyProvideTelecomManager(provider.get());
    }
    
    public static TelecomManager proxyProvideTelecomManager(final Context context) {
        return SystemServicesModule.provideTelecomManager(context);
    }
    
    @Override
    public TelecomManager get() {
        return provideInstance(this.contextProvider);
    }
}
