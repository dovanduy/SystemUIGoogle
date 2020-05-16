// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.app.IActivityManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideIActivityManagerFactory implements Factory<IActivityManager>
{
    private static final SystemServicesModule_ProvideIActivityManagerFactory INSTANCE;
    
    static {
        INSTANCE = new SystemServicesModule_ProvideIActivityManagerFactory();
    }
    
    public static SystemServicesModule_ProvideIActivityManagerFactory create() {
        return SystemServicesModule_ProvideIActivityManagerFactory.INSTANCE;
    }
    
    public static IActivityManager provideInstance() {
        return proxyProvideIActivityManager();
    }
    
    public static IActivityManager proxyProvideIActivityManager() {
        final IActivityManager provideIActivityManager = SystemServicesModule.provideIActivityManager();
        Preconditions.checkNotNull(provideIActivityManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideIActivityManager;
    }
    
    @Override
    public IActivityManager get() {
        return provideInstance();
    }
}
