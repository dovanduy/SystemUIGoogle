// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.content.res.Resources;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.dock.DockManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.AccessibilityController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class LockscreenLockIconController_Factory implements Factory<LockscreenLockIconController>
{
    private final Provider<AccessibilityController> accessibilityControllerProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerPhoneProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<KeyguardIndicationController> keyguardIndicationControllerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<LockPatternUtils> lockPatternUtilsProvider;
    private final Provider<LockscreenGestureLogger> lockscreenGestureLoggerProvider;
    private final Provider<NotificationWakeUpCoordinator> notificationWakeUpCoordinatorProvider;
    private final Provider<Resources> resourcesProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    
    public LockscreenLockIconController_Factory(final Provider<LockscreenGestureLogger> lockscreenGestureLoggerProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<LockPatternUtils> lockPatternUtilsProvider, final Provider<ShadeController> shadeControllerProvider, final Provider<AccessibilityController> accessibilityControllerProvider, final Provider<KeyguardIndicationController> keyguardIndicationControllerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<ConfigurationController> configurationControllerProvider, final Provider<NotificationWakeUpCoordinator> notificationWakeUpCoordinatorProvider, final Provider<KeyguardBypassController> keyguardBypassControllerProvider, final Provider<DockManager> dockManagerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<Resources> resourcesProvider, final Provider<HeadsUpManagerPhone> headsUpManagerPhoneProvider) {
        this.lockscreenGestureLoggerProvider = lockscreenGestureLoggerProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.lockPatternUtilsProvider = lockPatternUtilsProvider;
        this.shadeControllerProvider = shadeControllerProvider;
        this.accessibilityControllerProvider = accessibilityControllerProvider;
        this.keyguardIndicationControllerProvider = keyguardIndicationControllerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.notificationWakeUpCoordinatorProvider = notificationWakeUpCoordinatorProvider;
        this.keyguardBypassControllerProvider = keyguardBypassControllerProvider;
        this.dockManagerProvider = dockManagerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.resourcesProvider = resourcesProvider;
        this.headsUpManagerPhoneProvider = headsUpManagerPhoneProvider;
    }
    
    public static LockscreenLockIconController_Factory create(final Provider<LockscreenGestureLogger> provider, final Provider<KeyguardUpdateMonitor> provider2, final Provider<LockPatternUtils> provider3, final Provider<ShadeController> provider4, final Provider<AccessibilityController> provider5, final Provider<KeyguardIndicationController> provider6, final Provider<StatusBarStateController> provider7, final Provider<ConfigurationController> provider8, final Provider<NotificationWakeUpCoordinator> provider9, final Provider<KeyguardBypassController> provider10, final Provider<DockManager> provider11, final Provider<KeyguardStateController> provider12, final Provider<Resources> provider13, final Provider<HeadsUpManagerPhone> provider14) {
        return new LockscreenLockIconController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14);
    }
    
    public static LockscreenLockIconController provideInstance(final Provider<LockscreenGestureLogger> provider, final Provider<KeyguardUpdateMonitor> provider2, final Provider<LockPatternUtils> provider3, final Provider<ShadeController> provider4, final Provider<AccessibilityController> provider5, final Provider<KeyguardIndicationController> provider6, final Provider<StatusBarStateController> provider7, final Provider<ConfigurationController> provider8, final Provider<NotificationWakeUpCoordinator> provider9, final Provider<KeyguardBypassController> provider10, final Provider<DockManager> provider11, final Provider<KeyguardStateController> provider12, final Provider<Resources> provider13, final Provider<HeadsUpManagerPhone> provider14) {
        return new LockscreenLockIconController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get(), provider14.get());
    }
    
    @Override
    public LockscreenLockIconController get() {
        return provideInstance(this.lockscreenGestureLoggerProvider, this.keyguardUpdateMonitorProvider, this.lockPatternUtilsProvider, this.shadeControllerProvider, this.accessibilityControllerProvider, this.keyguardIndicationControllerProvider, this.statusBarStateControllerProvider, this.configurationControllerProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, this.dockManagerProvider, this.keyguardStateControllerProvider, this.resourcesProvider, this.headsUpManagerPhoneProvider);
    }
}
