// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.app.trust.TrustManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideTrustManagerFactory implements Factory<TrustManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideTrustManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideTrustManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideTrustManagerFactory(provider);
    }
    
    public static TrustManager provideInstance(final Provider<Context> provider) {
        return proxyProvideTrustManager(provider.get());
    }
    
    public static TrustManager proxyProvideTrustManager(final Context context) {
        final TrustManager provideTrustManager = SystemServicesModule.provideTrustManager(context);
        Preconditions.checkNotNull(provideTrustManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideTrustManager;
    }
    
    @Override
    public TrustManager get() {
        return provideInstance(this.contextProvider);
    }
}
