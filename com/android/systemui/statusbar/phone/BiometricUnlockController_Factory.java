// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.content.res.Resources;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.os.Handler;
import com.android.systemui.dump.DumpManager;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class BiometricUnlockController_Factory implements Factory<BiometricUnlockController>
{
    private final Provider<Context> contextProvider;
    private final Provider<DozeParameters> dozeParametersProvider;
    private final Provider<DozeScrimController> dozeScrimControllerProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<KeyguardViewMediator> keyguardViewMediatorProvider;
    private final Provider<MetricsLogger> metricsLoggerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<Resources> resourcesProvider;
    private final Provider<ScrimController> scrimControllerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<StatusBar> statusBarProvider;
    
    public BiometricUnlockController_Factory(final Provider<Context> contextProvider, final Provider<DozeScrimController> dozeScrimControllerProvider, final Provider<KeyguardViewMediator> keyguardViewMediatorProvider, final Provider<ScrimController> scrimControllerProvider, final Provider<StatusBar> statusBarProvider, final Provider<ShadeController> shadeControllerProvider, final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<Handler> handlerProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<Resources> resourcesProvider, final Provider<KeyguardBypassController> keyguardBypassControllerProvider, final Provider<DozeParameters> dozeParametersProvider, final Provider<MetricsLogger> metricsLoggerProvider, final Provider<DumpManager> dumpManagerProvider) {
        this.contextProvider = contextProvider;
        this.dozeScrimControllerProvider = dozeScrimControllerProvider;
        this.keyguardViewMediatorProvider = keyguardViewMediatorProvider;
        this.scrimControllerProvider = scrimControllerProvider;
        this.statusBarProvider = statusBarProvider;
        this.shadeControllerProvider = shadeControllerProvider;
        this.notificationShadeWindowControllerProvider = notificationShadeWindowControllerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.handlerProvider = handlerProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.resourcesProvider = resourcesProvider;
        this.keyguardBypassControllerProvider = keyguardBypassControllerProvider;
        this.dozeParametersProvider = dozeParametersProvider;
        this.metricsLoggerProvider = metricsLoggerProvider;
        this.dumpManagerProvider = dumpManagerProvider;
    }
    
    public static BiometricUnlockController_Factory create(final Provider<Context> provider, final Provider<DozeScrimController> provider2, final Provider<KeyguardViewMediator> provider3, final Provider<ScrimController> provider4, final Provider<StatusBar> provider5, final Provider<ShadeController> provider6, final Provider<NotificationShadeWindowController> provider7, final Provider<KeyguardStateController> provider8, final Provider<Handler> provider9, final Provider<KeyguardUpdateMonitor> provider10, final Provider<Resources> provider11, final Provider<KeyguardBypassController> provider12, final Provider<DozeParameters> provider13, final Provider<MetricsLogger> provider14, final Provider<DumpManager> provider15) {
        return new BiometricUnlockController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15);
    }
    
    public static BiometricUnlockController provideInstance(final Provider<Context> provider, final Provider<DozeScrimController> provider2, final Provider<KeyguardViewMediator> provider3, final Provider<ScrimController> provider4, final Provider<StatusBar> provider5, final Provider<ShadeController> provider6, final Provider<NotificationShadeWindowController> provider7, final Provider<KeyguardStateController> provider8, final Provider<Handler> provider9, final Provider<KeyguardUpdateMonitor> provider10, final Provider<Resources> provider11, final Provider<KeyguardBypassController> provider12, final Provider<DozeParameters> provider13, final Provider<MetricsLogger> provider14, final Provider<DumpManager> provider15) {
        return new BiometricUnlockController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get(), provider14.get(), provider15.get());
    }
    
    @Override
    public BiometricUnlockController get() {
        return provideInstance(this.contextProvider, this.dozeScrimControllerProvider, this.keyguardViewMediatorProvider, this.scrimControllerProvider, this.statusBarProvider, this.shadeControllerProvider, this.notificationShadeWindowControllerProvider, this.keyguardStateControllerProvider, this.handlerProvider, this.keyguardUpdateMonitorProvider, this.resourcesProvider, this.keyguardBypassControllerProvider, this.dozeParametersProvider, this.metricsLoggerProvider, this.dumpManagerProvider);
    }
}
