// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.view.IWindowManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideIWindowManagerFactory implements Factory<IWindowManager>
{
    private static final SystemServicesModule_ProvideIWindowManagerFactory INSTANCE;
    
    static {
        INSTANCE = new SystemServicesModule_ProvideIWindowManagerFactory();
    }
    
    public static SystemServicesModule_ProvideIWindowManagerFactory create() {
        return SystemServicesModule_ProvideIWindowManagerFactory.INSTANCE;
    }
    
    public static IWindowManager provideInstance() {
        return proxyProvideIWindowManager();
    }
    
    public static IWindowManager proxyProvideIWindowManager() {
        final IWindowManager provideIWindowManager = SystemServicesModule.provideIWindowManager();
        Preconditions.checkNotNull(provideIWindowManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideIWindowManager;
    }
    
    @Override
    public IWindowManager get() {
        return provideInstance();
    }
}
