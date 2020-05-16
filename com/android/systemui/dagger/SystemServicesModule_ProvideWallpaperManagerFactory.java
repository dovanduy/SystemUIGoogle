// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.app.WallpaperManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideWallpaperManagerFactory implements Factory<WallpaperManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideWallpaperManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideWallpaperManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideWallpaperManagerFactory(provider);
    }
    
    public static WallpaperManager provideInstance(final Provider<Context> provider) {
        return proxyProvideWallpaperManager(provider.get());
    }
    
    public static WallpaperManager proxyProvideWallpaperManager(final Context context) {
        final WallpaperManager provideWallpaperManager = SystemServicesModule.provideWallpaperManager(context);
        Preconditions.checkNotNull(provideWallpaperManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideWallpaperManager;
    }
    
    @Override
    public WallpaperManager get() {
        return provideInstance(this.contextProvider);
    }
}
