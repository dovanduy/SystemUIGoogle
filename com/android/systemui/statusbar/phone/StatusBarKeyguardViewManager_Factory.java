// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.dock.DockManager;
import android.content.Context;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.keyguard.ViewMediatorCallback;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class StatusBarKeyguardViewManager_Factory implements Factory<StatusBarKeyguardViewManager>
{
    private final Provider<ViewMediatorCallback> callbackProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<LockPatternUtils> lockPatternUtilsProvider;
    private final Provider<NavigationModeController> navigationModeControllerProvider;
    private final Provider<NotificationMediaManager> notificationMediaManagerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<SysuiStatusBarStateController> sysuiStatusBarStateControllerProvider;
    
    public StatusBarKeyguardViewManager_Factory(final Provider<Context> contextProvider, final Provider<ViewMediatorCallback> callbackProvider, final Provider<LockPatternUtils> lockPatternUtilsProvider, final Provider<SysuiStatusBarStateController> sysuiStatusBarStateControllerProvider, final Provider<ConfigurationController> configurationControllerProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<NavigationModeController> navigationModeControllerProvider, final Provider<DockManager> dockManagerProvider, final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<NotificationMediaManager> notificationMediaManagerProvider) {
        this.contextProvider = contextProvider;
        this.callbackProvider = callbackProvider;
        this.lockPatternUtilsProvider = lockPatternUtilsProvider;
        this.sysuiStatusBarStateControllerProvider = sysuiStatusBarStateControllerProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.navigationModeControllerProvider = navigationModeControllerProvider;
        this.dockManagerProvider = dockManagerProvider;
        this.notificationShadeWindowControllerProvider = notificationShadeWindowControllerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.notificationMediaManagerProvider = notificationMediaManagerProvider;
    }
    
    public static StatusBarKeyguardViewManager_Factory create(final Provider<Context> provider, final Provider<ViewMediatorCallback> provider2, final Provider<LockPatternUtils> provider3, final Provider<SysuiStatusBarStateController> provider4, final Provider<ConfigurationController> provider5, final Provider<KeyguardUpdateMonitor> provider6, final Provider<NavigationModeController> provider7, final Provider<DockManager> provider8, final Provider<NotificationShadeWindowController> provider9, final Provider<KeyguardStateController> provider10, final Provider<NotificationMediaManager> provider11) {
        return new StatusBarKeyguardViewManager_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11);
    }
    
    public static StatusBarKeyguardViewManager provideInstance(final Provider<Context> provider, final Provider<ViewMediatorCallback> provider2, final Provider<LockPatternUtils> provider3, final Provider<SysuiStatusBarStateController> provider4, final Provider<ConfigurationController> provider5, final Provider<KeyguardUpdateMonitor> provider6, final Provider<NavigationModeController> provider7, final Provider<DockManager> provider8, final Provider<NotificationShadeWindowController> provider9, final Provider<KeyguardStateController> provider10, final Provider<NotificationMediaManager> provider11) {
        return new StatusBarKeyguardViewManager(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get());
    }
    
    @Override
    public StatusBarKeyguardViewManager get() {
        return provideInstance(this.contextProvider, this.callbackProvider, this.lockPatternUtilsProvider, this.sysuiStatusBarStateControllerProvider, this.configurationControllerProvider, this.keyguardUpdateMonitorProvider, this.navigationModeControllerProvider, this.dockManagerProvider, this.notificationShadeWindowControllerProvider, this.keyguardStateControllerProvider, this.notificationMediaManagerProvider);
    }
}
