// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.content.pm.PackageManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvidePackageManagerFactory implements Factory<PackageManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvidePackageManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvidePackageManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvidePackageManagerFactory(provider);
    }
    
    public static PackageManager provideInstance(final Provider<Context> provider) {
        return proxyProvidePackageManager(provider.get());
    }
    
    public static PackageManager proxyProvidePackageManager(final Context context) {
        final PackageManager providePackageManager = SystemServicesModule.providePackageManager(context);
        Preconditions.checkNotNull(providePackageManager, "Cannot return null from a non-@Nullable @Provides method");
        return providePackageManager;
    }
    
    @Override
    public PackageManager get() {
        return provideInstance(this.contextProvider);
    }
}
