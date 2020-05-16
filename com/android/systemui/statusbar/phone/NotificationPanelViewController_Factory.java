// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.PulseExpansionHandler;
import android.os.PowerManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.LatencyTracker;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.ConversationNotificationManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.CommandQueue;
import android.app.ActivityManager;
import android.view.accessibility.AccessibilityManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationPanelViewController_Factory implements Factory<NotificationPanelViewController>
{
    private final Provider<AccessibilityManager> accessibilityManagerProvider;
    private final Provider<ActivityManager> activityManagerProvider;
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<ConversationNotificationManager> conversationNotificationManagerProvider;
    private final Provider<NotificationWakeUpCoordinator> coordinatorProvider;
    private final Provider<Integer> displayIdProvider;
    private final Provider<DozeLog> dozeLogProvider;
    private final Provider<DozeParameters> dozeParametersProvider;
    private final Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<FlingAnimationUtils.Builder> flingAnimationUtilsBuilderProvider;
    private final Provider<InjectionInflationController> injectionInflationControllerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<LatencyTracker> latencyTrackerProvider;
    private final Provider<MetricsLogger> metricsLoggerProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider;
    private final Provider<PowerManager> powerManagerProvider;
    private final Provider<PulseExpansionHandler> pulseExpansionHandlerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<StatusBarTouchableRegionManager> statusBarTouchableRegionManagerProvider;
    private final Provider<VibratorHelper> vibratorHelperProvider;
    private final Provider<NotificationPanelView> viewProvider;
    private final Provider<ZenModeController> zenModeControllerProvider;
    
    public NotificationPanelViewController_Factory(final Provider<NotificationPanelView> viewProvider, final Provider<InjectionInflationController> injectionInflationControllerProvider, final Provider<NotificationWakeUpCoordinator> coordinatorProvider, final Provider<PulseExpansionHandler> pulseExpansionHandlerProvider, final Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider, final Provider<KeyguardBypassController> bypassControllerProvider, final Provider<FalsingManager> falsingManagerProvider, final Provider<ShadeController> shadeControllerProvider, final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, final Provider<NotificationEntryManager> notificationEntryManagerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<DozeLog> dozeLogProvider, final Provider<DozeParameters> dozeParametersProvider, final Provider<CommandQueue> commandQueueProvider, final Provider<VibratorHelper> vibratorHelperProvider, final Provider<LatencyTracker> latencyTrackerProvider, final Provider<PowerManager> powerManagerProvider, final Provider<AccessibilityManager> accessibilityManagerProvider, final Provider<Integer> displayIdProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<MetricsLogger> metricsLoggerProvider, final Provider<ActivityManager> activityManagerProvider, final Provider<ZenModeController> zenModeControllerProvider, final Provider<ConfigurationController> configurationControllerProvider, final Provider<FlingAnimationUtils.Builder> flingAnimationUtilsBuilderProvider, final Provider<StatusBarTouchableRegionManager> statusBarTouchableRegionManagerProvider, final Provider<ConversationNotificationManager> conversationNotificationManagerProvider) {
        this.viewProvider = viewProvider;
        this.injectionInflationControllerProvider = injectionInflationControllerProvider;
        this.coordinatorProvider = coordinatorProvider;
        this.pulseExpansionHandlerProvider = pulseExpansionHandlerProvider;
        this.dynamicPrivacyControllerProvider = dynamicPrivacyControllerProvider;
        this.bypassControllerProvider = bypassControllerProvider;
        this.falsingManagerProvider = falsingManagerProvider;
        this.shadeControllerProvider = shadeControllerProvider;
        this.notificationLockscreenUserManagerProvider = notificationLockscreenUserManagerProvider;
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.dozeLogProvider = dozeLogProvider;
        this.dozeParametersProvider = dozeParametersProvider;
        this.commandQueueProvider = commandQueueProvider;
        this.vibratorHelperProvider = vibratorHelperProvider;
        this.latencyTrackerProvider = latencyTrackerProvider;
        this.powerManagerProvider = powerManagerProvider;
        this.accessibilityManagerProvider = accessibilityManagerProvider;
        this.displayIdProvider = displayIdProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.metricsLoggerProvider = metricsLoggerProvider;
        this.activityManagerProvider = activityManagerProvider;
        this.zenModeControllerProvider = zenModeControllerProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.flingAnimationUtilsBuilderProvider = flingAnimationUtilsBuilderProvider;
        this.statusBarTouchableRegionManagerProvider = statusBarTouchableRegionManagerProvider;
        this.conversationNotificationManagerProvider = conversationNotificationManagerProvider;
    }
    
    public static NotificationPanelViewController_Factory create(final Provider<NotificationPanelView> provider, final Provider<InjectionInflationController> provider2, final Provider<NotificationWakeUpCoordinator> provider3, final Provider<PulseExpansionHandler> provider4, final Provider<DynamicPrivacyController> provider5, final Provider<KeyguardBypassController> provider6, final Provider<FalsingManager> provider7, final Provider<ShadeController> provider8, final Provider<NotificationLockscreenUserManager> provider9, final Provider<NotificationEntryManager> provider10, final Provider<KeyguardStateController> provider11, final Provider<StatusBarStateController> provider12, final Provider<DozeLog> provider13, final Provider<DozeParameters> provider14, final Provider<CommandQueue> provider15, final Provider<VibratorHelper> provider16, final Provider<LatencyTracker> provider17, final Provider<PowerManager> provider18, final Provider<AccessibilityManager> provider19, final Provider<Integer> provider20, final Provider<KeyguardUpdateMonitor> provider21, final Provider<MetricsLogger> provider22, final Provider<ActivityManager> provider23, final Provider<ZenModeController> provider24, final Provider<ConfigurationController> provider25, final Provider<FlingAnimationUtils.Builder> provider26, final Provider<StatusBarTouchableRegionManager> provider27, final Provider<ConversationNotificationManager> provider28) {
        return new NotificationPanelViewController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20, provider21, provider22, provider23, provider24, provider25, provider26, provider27, provider28);
    }
    
    public static NotificationPanelViewController provideInstance(final Provider<NotificationPanelView> provider, final Provider<InjectionInflationController> provider2, final Provider<NotificationWakeUpCoordinator> provider3, final Provider<PulseExpansionHandler> provider4, final Provider<DynamicPrivacyController> provider5, final Provider<KeyguardBypassController> provider6, final Provider<FalsingManager> provider7, final Provider<ShadeController> provider8, final Provider<NotificationLockscreenUserManager> provider9, final Provider<NotificationEntryManager> provider10, final Provider<KeyguardStateController> provider11, final Provider<StatusBarStateController> provider12, final Provider<DozeLog> provider13, final Provider<DozeParameters> provider14, final Provider<CommandQueue> provider15, final Provider<VibratorHelper> provider16, final Provider<LatencyTracker> provider17, final Provider<PowerManager> provider18, final Provider<AccessibilityManager> provider19, final Provider<Integer> provider20, final Provider<KeyguardUpdateMonitor> provider21, final Provider<MetricsLogger> provider22, final Provider<ActivityManager> provider23, final Provider<ZenModeController> provider24, final Provider<ConfigurationController> provider25, final Provider<FlingAnimationUtils.Builder> provider26, final Provider<StatusBarTouchableRegionManager> provider27, final Provider<ConversationNotificationManager> provider28) {
        return new NotificationPanelViewController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get(), provider14.get(), provider15.get(), provider16.get(), provider17.get(), provider18.get(), provider19.get(), provider20.get(), provider21.get(), provider22.get(), provider23.get(), provider24.get(), provider25.get(), provider26.get(), provider27.get(), provider28.get());
    }
    
    @Override
    public NotificationPanelViewController get() {
        return provideInstance(this.viewProvider, this.injectionInflationControllerProvider, this.coordinatorProvider, this.pulseExpansionHandlerProvider, this.dynamicPrivacyControllerProvider, this.bypassControllerProvider, this.falsingManagerProvider, this.shadeControllerProvider, this.notificationLockscreenUserManagerProvider, this.notificationEntryManagerProvider, this.keyguardStateControllerProvider, this.statusBarStateControllerProvider, this.dozeLogProvider, this.dozeParametersProvider, this.commandQueueProvider, this.vibratorHelperProvider, this.latencyTrackerProvider, this.powerManagerProvider, this.accessibilityManagerProvider, this.displayIdProvider, this.keyguardUpdateMonitorProvider, this.metricsLoggerProvider, this.activityManagerProvider, this.zenModeControllerProvider, this.configurationControllerProvider, this.flingAnimationUtilsBuilderProvider, this.statusBarTouchableRegionManagerProvider, this.conversationNotificationManagerProvider);
    }
}
