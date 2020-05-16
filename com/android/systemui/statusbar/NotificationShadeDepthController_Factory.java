// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.app.WallpaperManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.dump.DumpManager;
import android.view.Choreographer;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationShadeDepthController_Factory implements Factory<NotificationShadeDepthController>
{
    private final Provider<BiometricUnlockController> biometricUnlockControllerProvider;
    private final Provider<BlurUtils> blurUtilsProvider;
    private final Provider<Choreographer> choreographerProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<WallpaperManager> wallpaperManagerProvider;
    
    public NotificationShadeDepthController_Factory(final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<BlurUtils> blurUtilsProvider, final Provider<BiometricUnlockController> biometricUnlockControllerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<Choreographer> choreographerProvider, final Provider<WallpaperManager> wallpaperManagerProvider, final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.blurUtilsProvider = blurUtilsProvider;
        this.biometricUnlockControllerProvider = biometricUnlockControllerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.choreographerProvider = choreographerProvider;
        this.wallpaperManagerProvider = wallpaperManagerProvider;
        this.notificationShadeWindowControllerProvider = notificationShadeWindowControllerProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static NotificationShadeDepthController_Factory create(final Provider<StatusBarStateController> provider, final Provider<BlurUtils> provider2, final Provider<BiometricUnlockController> provider3, final Provider<KeyguardStateController> provider4, final Provider<Choreographer> provider5, final Provider<WallpaperManager> provider6, final Provider<NotificationShadeWindowController> provider7, final Provider<DumpManager> provider8) {
        return new NotificationShadeDepthController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
    }
    
    public static NotificationShadeDepthController provideInstance(final Provider<StatusBarStateController> provider, final Provider<BlurUtils> provider2, final Provider<BiometricUnlockController> provider3, final Provider<KeyguardStateController> provider4, final Provider<Choreographer> provider5, final Provider<WallpaperManager> provider6, final Provider<NotificationShadeWindowController> provider7, final Provider<DumpManager> provider8) {
        return new NotificationShadeDepthController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get());
    }
    
    @Override
    public NotificationShadeDepthController get() {
        return provideInstance(this.statusBarStateControllerProvider, this.blurUtilsProvider, this.biometricUnlockControllerProvider, this.keyguardStateControllerProvider, this.choreographerProvider, this.wallpaperManagerProvider, this.notificationShadeWindowControllerProvider, this.dumpManagerProvider);
    }
}
