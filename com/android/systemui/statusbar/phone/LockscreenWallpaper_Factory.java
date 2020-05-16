// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.app.WallpaperManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import android.os.Handler;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.app.IWallpaperManager;
import com.android.systemui.dump.DumpManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class LockscreenWallpaper_Factory implements Factory<LockscreenWallpaper>
{
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<IWallpaperManager> iWallpaperManagerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<NotificationMediaManager> mediaManagerProvider;
    private final Provider<WallpaperManager> wallpaperManagerProvider;
    
    public LockscreenWallpaper_Factory(final Provider<WallpaperManager> wallpaperManagerProvider, final Provider<IWallpaperManager> iWallpaperManagerProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<DumpManager> dumpManagerProvider, final Provider<NotificationMediaManager> mediaManagerProvider, final Provider<Handler> mainHandlerProvider) {
        this.wallpaperManagerProvider = wallpaperManagerProvider;
        this.iWallpaperManagerProvider = iWallpaperManagerProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.dumpManagerProvider = dumpManagerProvider;
        this.mediaManagerProvider = mediaManagerProvider;
        this.mainHandlerProvider = mainHandlerProvider;
    }
    
    public static LockscreenWallpaper_Factory create(final Provider<WallpaperManager> provider, final Provider<IWallpaperManager> provider2, final Provider<KeyguardUpdateMonitor> provider3, final Provider<DumpManager> provider4, final Provider<NotificationMediaManager> provider5, final Provider<Handler> provider6) {
        return new LockscreenWallpaper_Factory(provider, provider2, provider3, provider4, provider5, provider6);
    }
    
    public static LockscreenWallpaper provideInstance(final Provider<WallpaperManager> provider, final Provider<IWallpaperManager> provider2, final Provider<KeyguardUpdateMonitor> provider3, final Provider<DumpManager> provider4, final Provider<NotificationMediaManager> provider5, final Provider<Handler> provider6) {
        return new LockscreenWallpaper(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get());
    }
    
    @Override
    public LockscreenWallpaper get() {
        return provideInstance(this.wallpaperManagerProvider, this.iWallpaperManagerProvider, this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, this.mediaManagerProvider, this.mainHandlerProvider);
    }
}
