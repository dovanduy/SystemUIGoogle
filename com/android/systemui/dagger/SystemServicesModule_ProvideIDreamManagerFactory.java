// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.service.dreams.IDreamManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideIDreamManagerFactory implements Factory<IDreamManager>
{
    private static final SystemServicesModule_ProvideIDreamManagerFactory INSTANCE;
    
    static {
        INSTANCE = new SystemServicesModule_ProvideIDreamManagerFactory();
    }
    
    public static SystemServicesModule_ProvideIDreamManagerFactory create() {
        return SystemServicesModule_ProvideIDreamManagerFactory.INSTANCE;
    }
    
    public static IDreamManager provideInstance() {
        return proxyProvideIDreamManager();
    }
    
    public static IDreamManager proxyProvideIDreamManager() {
        final IDreamManager provideIDreamManager = SystemServicesModule.provideIDreamManager();
        Preconditions.checkNotNull(provideIDreamManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideIDreamManager;
    }
    
    @Override
    public IDreamManager get() {
        return provideInstance();
    }
}
