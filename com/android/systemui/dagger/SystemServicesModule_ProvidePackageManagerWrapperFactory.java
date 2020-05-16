// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.shared.system.PackageManagerWrapper;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvidePackageManagerWrapperFactory implements Factory<PackageManagerWrapper>
{
    private static final SystemServicesModule_ProvidePackageManagerWrapperFactory INSTANCE;
    
    static {
        INSTANCE = new SystemServicesModule_ProvidePackageManagerWrapperFactory();
    }
    
    public static SystemServicesModule_ProvidePackageManagerWrapperFactory create() {
        return SystemServicesModule_ProvidePackageManagerWrapperFactory.INSTANCE;
    }
    
    public static PackageManagerWrapper provideInstance() {
        return proxyProvidePackageManagerWrapper();
    }
    
    public static PackageManagerWrapper proxyProvidePackageManagerWrapper() {
        final PackageManagerWrapper providePackageManagerWrapper = SystemServicesModule.providePackageManagerWrapper();
        Preconditions.checkNotNull(providePackageManagerWrapper, "Cannot return null from a non-@Nullable @Provides method");
        return providePackageManagerWrapper;
    }
    
    @Override
    public PackageManagerWrapper get() {
        return provideInstance();
    }
}
