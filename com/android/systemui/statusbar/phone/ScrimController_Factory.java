// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.os.Handler;
import com.android.systemui.dock.DockManager;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import com.android.systemui.statusbar.BlurUtils;
import android.app.AlarmManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ScrimController_Factory implements Factory<ScrimController>
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
    private final Provider<SysuiColorExtractor> sysuiColorExtractorProvider;
    
    public ScrimController_Factory(final Provider<LightBarController> lightBarControllerProvider, final Provider<DozeParameters> dozeParametersProvider, final Provider<AlarmManager> alarmManagerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<DelayedWakeLock.Builder> delayedWakeLockBuilderProvider, final Provider<Handler> handlerProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<SysuiColorExtractor> sysuiColorExtractorProvider, final Provider<DockManager> dockManagerProvider, final Provider<BlurUtils> blurUtilsProvider) {
        this.lightBarControllerProvider = lightBarControllerProvider;
        this.dozeParametersProvider = dozeParametersProvider;
        this.alarmManagerProvider = alarmManagerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.delayedWakeLockBuilderProvider = delayedWakeLockBuilderProvider;
        this.handlerProvider = handlerProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.sysuiColorExtractorProvider = sysuiColorExtractorProvider;
        this.dockManagerProvider = dockManagerProvider;
        this.blurUtilsProvider = blurUtilsProvider;
    }
    
    public static ScrimController_Factory create(final Provider<LightBarController> provider, final Provider<DozeParameters> provider2, final Provider<AlarmManager> provider3, final Provider<KeyguardStateController> provider4, final Provider<DelayedWakeLock.Builder> provider5, final Provider<Handler> provider6, final Provider<KeyguardUpdateMonitor> provider7, final Provider<SysuiColorExtractor> provider8, final Provider<DockManager> provider9, final Provider<BlurUtils> provider10) {
        return new ScrimController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10);
    }
    
    public static ScrimController provideInstance(final Provider<LightBarController> provider, final Provider<DozeParameters> provider2, final Provider<AlarmManager> provider3, final Provider<KeyguardStateController> provider4, final Provider<DelayedWakeLock.Builder> provider5, final Provider<Handler> provider6, final Provider<KeyguardUpdateMonitor> provider7, final Provider<SysuiColorExtractor> provider8, final Provider<DockManager> provider9, final Provider<BlurUtils> provider10) {
        return new ScrimController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get());
    }
    
    @Override
    public ScrimController get() {
        return provideInstance(this.lightBarControllerProvider, this.dozeParametersProvider, this.alarmManagerProvider, this.keyguardStateControllerProvider, this.delayedWakeLockBuilderProvider, this.handlerProvider, this.keyguardUpdateMonitorProvider, this.sysuiColorExtractorProvider, this.dockManagerProvider, this.blurUtilsProvider);
    }
}
