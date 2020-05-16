// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.net.ConnectivityManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideConnectivityManagagerFactory implements Factory<ConnectivityManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideConnectivityManagagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideConnectivityManagagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideConnectivityManagagerFactory(provider);
    }
    
    public static ConnectivityManager provideInstance(final Provider<Context> provider) {
        return proxyProvideConnectivityManagager(provider.get());
    }
    
    public static ConnectivityManager proxyProvideConnectivityManagager(final Context context) {
        final ConnectivityManager provideConnectivityManagager = SystemServicesModule.provideConnectivityManagager(context);
        Preconditions.checkNotNull(provideConnectivityManagager, "Cannot return null from a non-@Nullable @Provides method");
        return provideConnectivityManagager;
    }
    
    @Override
    public ConnectivityManager get() {
        return provideInstance(this.contextProvider);
    }
}
