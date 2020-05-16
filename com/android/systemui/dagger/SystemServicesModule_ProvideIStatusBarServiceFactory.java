// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import com.android.internal.statusbar.IStatusBarService;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideIStatusBarServiceFactory implements Factory<IStatusBarService>
{
    private static final SystemServicesModule_ProvideIStatusBarServiceFactory INSTANCE;
    
    static {
        INSTANCE = new SystemServicesModule_ProvideIStatusBarServiceFactory();
    }
    
    public static SystemServicesModule_ProvideIStatusBarServiceFactory create() {
        return SystemServicesModule_ProvideIStatusBarServiceFactory.INSTANCE;
    }
    
    public static IStatusBarService provideInstance() {
        return proxyProvideIStatusBarService();
    }
    
    public static IStatusBarService proxyProvideIStatusBarService() {
        final IStatusBarService provideIStatusBarService = SystemServicesModule.provideIStatusBarService();
        Preconditions.checkNotNull(provideIStatusBarService, "Cannot return null from a non-@Nullable @Provides method");
        return provideIStatusBarService;
    }
    
    @Override
    public IStatusBarService get() {
        return provideInstance();
    }
}
