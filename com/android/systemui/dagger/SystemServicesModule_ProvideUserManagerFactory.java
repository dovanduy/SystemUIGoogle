// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.os.UserManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideUserManagerFactory implements Factory<UserManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideUserManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideUserManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideUserManagerFactory(provider);
    }
    
    public static UserManager provideInstance(final Provider<Context> provider) {
        return proxyProvideUserManager(provider.get());
    }
    
    public static UserManager proxyProvideUserManager(final Context context) {
        final UserManager provideUserManager = SystemServicesModule.provideUserManager(context);
        Preconditions.checkNotNull(provideUserManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideUserManager;
    }
    
    @Override
    public UserManager get() {
        return provideInstance(this.contextProvider);
    }
}
