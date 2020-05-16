// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.net.NetworkScoreManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideNetworkScoreManagerFactory implements Factory<NetworkScoreManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideNetworkScoreManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideNetworkScoreManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideNetworkScoreManagerFactory(provider);
    }
    
    public static NetworkScoreManager provideInstance(final Provider<Context> provider) {
        return proxyProvideNetworkScoreManager(provider.get());
    }
    
    public static NetworkScoreManager proxyProvideNetworkScoreManager(final Context context) {
        final NetworkScoreManager provideNetworkScoreManager = SystemServicesModule.provideNetworkScoreManager(context);
        Preconditions.checkNotNull(provideNetworkScoreManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNetworkScoreManager;
    }
    
    @Override
    public NetworkScoreManager get() {
        return provideInstance(this.contextProvider);
    }
}
