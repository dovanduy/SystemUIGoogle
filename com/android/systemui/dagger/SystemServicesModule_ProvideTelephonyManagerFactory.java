// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.telephony.TelephonyManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideTelephonyManagerFactory implements Factory<TelephonyManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideTelephonyManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideTelephonyManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideTelephonyManagerFactory(provider);
    }
    
    public static TelephonyManager provideInstance(final Provider<Context> provider) {
        return proxyProvideTelephonyManager(provider.get());
    }
    
    public static TelephonyManager proxyProvideTelephonyManager(final Context context) {
        final TelephonyManager provideTelephonyManager = SystemServicesModule.provideTelephonyManager(context);
        Preconditions.checkNotNull(provideTelephonyManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideTelephonyManager;
    }
    
    @Override
    public TelephonyManager get() {
        return provideInstance(this.contextProvider);
    }
}
