// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import android.app.IWallpaperManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideIWallPaperManagerFactory implements Factory<IWallpaperManager>
{
    private static final SystemServicesModule_ProvideIWallPaperManagerFactory INSTANCE;
    
    static {
        INSTANCE = new SystemServicesModule_ProvideIWallPaperManagerFactory();
    }
    
    public static SystemServicesModule_ProvideIWallPaperManagerFactory create() {
        return SystemServicesModule_ProvideIWallPaperManagerFactory.INSTANCE;
    }
    
    public static IWallpaperManager provideInstance() {
        return proxyProvideIWallPaperManager();
    }
    
    public static IWallpaperManager proxyProvideIWallPaperManager() {
        return SystemServicesModule.provideIWallPaperManager();
    }
    
    @Override
    public IWallpaperManager get() {
        return provideInstance();
    }
}
