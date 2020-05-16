// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui;

import android.app.IWallpaperManager;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.os.Handler;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.dock.DockManager;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import com.android.systemui.statusbar.BlurUtils;
import android.app.AlarmManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class LiveWallpaperScrimController_Factory implements Factory<LiveWallpaperScrimController>
{
    private final Provider<AlarmManager> alarmManagerProvider;
    private final Provider<BlurUtils> blurUtilsProvider;
    private final Provider<DelayedWakeLock.Builder> delayedWakeLockBuilderProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<DozeParameters> dozeParametersProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<LightBarController> lightBarControllerProvider;
    private final Provider<LockscreenWallpaper> lockscreenWallpaperProvider;
    private final Provider<SysuiColorExtractor> sysuiColorExtractorProvider;
    private final Provider<IWallpaperManager> wallpaperManagerProvider;
    
    public LiveWallpaperScrimController_Factory(final Provider<LightBarController> lightBarControllerProvider, final Provider<DozeParameters> dozeParametersProvider, final Provider<AlarmManager> alarmManagerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<DelayedWakeLock.Builder> delayedWakeLockBuilderProvider, final Provider<Handler> handlerProvider, final Provider<IWallpaperManager> wallpaperManagerProvider, final Provider<LockscreenWallpaper> lockscreenWallpaperProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<SysuiColorExtractor> sysuiColorExtractorProvider, final Provider<DockManager> dockManagerProvider, final Provider<BlurUtils> blurUtilsProvider) {
        this.lightBarControllerProvider = lightBarControllerProvider;
        this.dozeParametersProvider = dozeParametersProvider;
        this.alarmManagerProvider = alarmManagerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.delayedWakeLockBuilderProvider = delayedWakeLockBuilderProvider;
        this.handlerProvider = handlerProvider;
        this.wallpaperManagerProvider = wallpaperManagerProvider;
        this.lockscreenWallpaperProvider = lockscreenWallpaperProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.sysuiColorExtractorProvider = sysuiColorExtractorProvider;
        this.dockManagerProvider = dockManagerProvider;
        this.blurUtilsProvider = blurUtilsProvider;
    }
    
    public static LiveWallpaperScrimController_Factory create(final Provider<LightBarController> provider, final Provider<DozeParameters> provider2, final Provider<AlarmManager> provider3, final Provider<KeyguardStateController> provider4, final Provider<DelayedWakeLock.Builder> provider5, final Provider<Handler> provider6, final Provider<IWallpaperManager> provider7, final Provider<LockscreenWallpaper> provider8, final Provider<KeyguardUpdateMonitor> provider9, final Provider<SysuiColorExtractor> provider10, final Provider<DockManager> provider11, final Provider<BlurUtils> provider12) {
        return new LiveWallpaperScrimController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12);
    }
    
    public static LiveWallpaperScrimController provideInstance(final Provider<LightBarController> provider, final Provider<DozeParameters> provider2, final Provider<AlarmManager> provider3, final Provider<KeyguardStateController> provider4, final Provider<DelayedWakeLock.Builder> provider5, final Provider<Handler> provider6, final Provider<IWallpaperManager> provider7, final Provider<LockscreenWallpaper> provider8, final Provider<KeyguardUpdateMonitor> provider9, final Provider<SysuiColorExtractor> provider10, final Provider<DockManager> provider11, final Provider<BlurUtils> provider12) {
        return new LiveWallpaperScrimController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get());
    }
    
    @Override
    public LiveWallpaperScrimController get() {
        return provideInstance(this.lightBarControllerProvider, this.dozeParametersProvider, this.alarmManagerProvider, this.keyguardStateControllerProvider, this.delayedWakeLockBuilderProvider, this.handlerProvider, this.wallpaperManagerProvider, this.lockscreenWallpaperProvider, this.keyguardUpdateMonitorProvider, this.sysuiColorExtractorProvider, this.dockManagerProvider, this.blurUtilsProvider);
    }
}
