// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import dagger.internal.DoubleCheck;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.PulseExpansionHandler;
import android.os.PowerManager;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.assist.AssistManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DozeServiceHost_Factory implements Factory<DozeServiceHost>
{
    private final Provider<AssistManager> assistManagerLazyProvider;
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<BiometricUnlockController> biometricUnlockControllerLazyProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<DozeLog> dozeLogProvider;
    private final Provider<DozeScrimController> dozeScrimControllerProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerPhoneProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<KeyguardViewMediator> keyguardViewMediatorProvider;
    private final Provider<LockscreenLockIconController> lockscreenLockIconControllerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<NotificationWakeUpCoordinator> notificationWakeUpCoordinatorProvider;
    private final Provider<PowerManager> powerManagerProvider;
    private final Provider<PulseExpansionHandler> pulseExpansionHandlerProvider;
    private final Provider<ScrimController> scrimControllerProvider;
    private final Provider<SysuiStatusBarStateController> statusBarStateControllerProvider;
    private final Provider<VisualStabilityManager> visualStabilityManagerProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;
    
    public DozeServiceHost_Factory(final Provider<DozeLog> dozeLogProvider, final Provider<PowerManager> powerManagerProvider, final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider, final Provider<SysuiStatusBarStateController> statusBarStateControllerProvider, final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider, final Provider<HeadsUpManagerPhone> headsUpManagerPhoneProvider, final Provider<BatteryController> batteryControllerProvider, final Provider<ScrimController> scrimControllerProvider, final Provider<BiometricUnlockController> biometricUnlockControllerLazyProvider, final Provider<KeyguardViewMediator> keyguardViewMediatorProvider, final Provider<AssistManager> assistManagerLazyProvider, final Provider<DozeScrimController> dozeScrimControllerProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<VisualStabilityManager> visualStabilityManagerProvider, final Provider<PulseExpansionHandler> pulseExpansionHandlerProvider, final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider, final Provider<NotificationWakeUpCoordinator> notificationWakeUpCoordinatorProvider, final Provider<LockscreenLockIconController> lockscreenLockIconControllerProvider) {
        this.dozeLogProvider = dozeLogProvider;
        this.powerManagerProvider = powerManagerProvider;
        this.wakefulnessLifecycleProvider = wakefulnessLifecycleProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.deviceProvisionedControllerProvider = deviceProvisionedControllerProvider;
        this.headsUpManagerPhoneProvider = headsUpManagerPhoneProvider;
        this.batteryControllerProvider = batteryControllerProvider;
        this.scrimControllerProvider = scrimControllerProvider;
        this.biometricUnlockControllerLazyProvider = biometricUnlockControllerLazyProvider;
        this.keyguardViewMediatorProvider = keyguardViewMediatorProvider;
        this.assistManagerLazyProvider = assistManagerLazyProvider;
        this.dozeScrimControllerProvider = dozeScrimControllerProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.visualStabilityManagerProvider = visualStabilityManagerProvider;
        this.pulseExpansionHandlerProvider = pulseExpansionHandlerProvider;
        this.notificationShadeWindowControllerProvider = notificationShadeWindowControllerProvider;
        this.notificationWakeUpCoordinatorProvider = notificationWakeUpCoordinatorProvider;
        this.lockscreenLockIconControllerProvider = lockscreenLockIconControllerProvider;
    }
    
    public static DozeServiceHost_Factory create(final Provider<DozeLog> provider, final Provider<PowerManager> provider2, final Provider<WakefulnessLifecycle> provider3, final Provider<SysuiStatusBarStateController> provider4, final Provider<DeviceProvisionedController> provider5, final Provider<HeadsUpManagerPhone> provider6, final Provider<BatteryController> provider7, final Provider<ScrimController> provider8, final Provider<BiometricUnlockController> provider9, final Provider<KeyguardViewMediator> provider10, final Provider<AssistManager> provider11, final Provider<DozeScrimController> provider12, final Provider<KeyguardUpdateMonitor> provider13, final Provider<VisualStabilityManager> provider14, final Provider<PulseExpansionHandler> provider15, final Provider<NotificationShadeWindowController> provider16, final Provider<NotificationWakeUpCoordinator> provider17, final Provider<LockscreenLockIconController> provider18) {
        return new DozeServiceHost_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18);
    }
    
    public static DozeServiceHost provideInstance(final Provider<DozeLog> provider, final Provider<PowerManager> provider2, final Provider<WakefulnessLifecycle> provider3, final Provider<SysuiStatusBarStateController> provider4, final Provider<DeviceProvisionedController> provider5, final Provider<HeadsUpManagerPhone> provider6, final Provider<BatteryController> provider7, final Provider<ScrimController> provider8, final Provider<BiometricUnlockController> provider9, final Provider<KeyguardViewMediator> provider10, final Provider<AssistManager> provider11, final Provider<DozeScrimController> provider12, final Provider<KeyguardUpdateMonitor> provider13, final Provider<VisualStabilityManager> provider14, final Provider<PulseExpansionHandler> provider15, final Provider<NotificationShadeWindowController> provider16, final Provider<NotificationWakeUpCoordinator> provider17, final Provider<LockscreenLockIconController> provider18) {
        return new DozeServiceHost(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), DoubleCheck.lazy(provider9), provider10.get(), DoubleCheck.lazy(provider11), provider12.get(), provider13.get(), provider14.get(), provider15.get(), provider16.get(), provider17.get(), provider18.get());
    }
    
    @Override
    public DozeServiceHost get() {
        return provideInstance(this.dozeLogProvider, this.powerManagerProvider, this.wakefulnessLifecycleProvider, this.statusBarStateControllerProvider, this.deviceProvisionedControllerProvider, this.headsUpManagerPhoneProvider, this.batteryControllerProvider, this.scrimControllerProvider, this.biometricUnlockControllerLazyProvider, this.keyguardViewMediatorProvider, this.assistManagerLazyProvider, this.dozeScrimControllerProvider, this.keyguardUpdateMonitorProvider, this.visualStabilityManagerProvider, this.pulseExpansionHandlerProvider, this.notificationShadeWindowControllerProvider, this.notificationWakeUpCoordinatorProvider, this.lockscreenLockIconControllerProvider);
    }
}
