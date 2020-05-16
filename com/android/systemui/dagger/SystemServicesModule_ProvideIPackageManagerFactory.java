// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.pm.IPackageManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideIPackageManagerFactory implements Factory<IPackageManager>
{
    private static final SystemServicesModule_ProvideIPackageManagerFactory INSTANCE;
    
    static {
        INSTANCE = new SystemServicesModule_ProvideIPackageManagerFactory();
    }
    
    public static SystemServicesModule_ProvideIPackageManagerFactory create() {
        return SystemServicesModule_ProvideIPackageManagerFactory.INSTANCE;
    }
    
    public static IPackageManager provideInstance() {
        return proxyProvideIPackageManager();
    }
    
    public static IPackageManager proxyProvideIPackageManager() {
        final IPackageManager provideIPackageManager = SystemServicesModule.provideIPackageManager();
        Preconditions.checkNotNull(provideIPackageManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideIPackageManager;
    }
    
    @Override
    public IPackageManager get() {
        return provideInstance();
    }
}
