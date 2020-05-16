// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone.dagger;

import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarTouchableRegionManager;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.phone.PhoneStatusBarPolicy;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import com.android.systemui.plugins.PluginDependencyProvider;
import android.os.Handler;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.InitController;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter;
import com.android.systemui.statusbar.phone.LightsOutNotifController;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.shared.plugins.PluginManager;
import javax.inject.Provider;
import com.android.systemui.recents.Recents;
import java.util.Optional;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.volume.VolumeComponent;
import com.android.systemui.statusbar.phone.DozeScrimController;
import com.android.systemui.recents.ScreenPinningRequest;
import android.os.PowerManager;
import com.android.systemui.statusbar.phone.DozeServiceHost;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import com.android.systemui.statusbar.phone.KeyguardLiftController;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.LockscreenLockIconController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.assist.AssistManager;
import dagger.Lazy;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import java.util.concurrent.Executor;
import com.android.internal.logging.MetricsLogger;
import android.util.DisplayMetrics;
import com.android.systemui.statusbar.notification.interruption.NotificationAlertingManager;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.notification.interruption.BypassHeadsUpNotifier;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.notification.init.NotificationsController;
import android.content.Context;

public interface StatusBarPhoneModule
{
    default StatusBar provideStatusBar(final Context context, final NotificationsController notificationsController, final LightBarController lightBarController, final AutoHideController autoHideController, final KeyguardUpdateMonitor keyguardUpdateMonitor, final StatusBarIconController statusBarIconController, final PulseExpansionHandler pulseExpansionHandler, final NotificationWakeUpCoordinator notificationWakeUpCoordinator, final KeyguardBypassController keyguardBypassController, final KeyguardStateController keyguardStateController, final HeadsUpManagerPhone headsUpManagerPhone, final DynamicPrivacyController dynamicPrivacyController, final BypassHeadsUpNotifier bypassHeadsUpNotifier, final FalsingManager falsingManager, final BroadcastDispatcher broadcastDispatcher, final RemoteInputQuickSettingsDisabler remoteInputQuickSettingsDisabler, final NotificationGutsManager notificationGutsManager, final NotificationLogger notificationLogger, final NotificationInterruptStateProvider notificationInterruptStateProvider, final NotificationViewHierarchyManager notificationViewHierarchyManager, final KeyguardViewMediator keyguardViewMediator, final NotificationAlertingManager notificationAlertingManager, final DisplayMetrics displayMetrics, final MetricsLogger metricsLogger, final Executor executor, final NotificationMediaManager notificationMediaManager, final NotificationLockscreenUserManager notificationLockscreenUserManager, final NotificationRemoteInputManager notificationRemoteInputManager, final UserSwitcherController userSwitcherController, final NetworkController networkController, final BatteryController batteryController, final SysuiColorExtractor sysuiColorExtractor, final ScreenLifecycle screenLifecycle, final WakefulnessLifecycle wakefulnessLifecycle, final SysuiStatusBarStateController sysuiStatusBarStateController, final VibratorHelper vibratorHelper, final BubbleController bubbleController, final NotificationGroupManager notificationGroupManager, final VisualStabilityManager visualStabilityManager, final DeviceProvisionedController deviceProvisionedController, final NavigationBarController navigationBarController, final Lazy<AssistManager> lazy, final ConfigurationController configurationController, final NotificationShadeWindowController notificationShadeWindowController, final LockscreenLockIconController lockscreenLockIconController, final DozeParameters dozeParameters, final ScrimController scrimController, final KeyguardLiftController keyguardLiftController, final Lazy<LockscreenWallpaper> lazy2, final Lazy<BiometricUnlockController> lazy3, final DozeServiceHost dozeServiceHost, final PowerManager powerManager, final ScreenPinningRequest screenPinningRequest, final DozeScrimController dozeScrimController, final VolumeComponent volumeComponent, final CommandQueue commandQueue, final Optional<Recents> optional, final Provider<StatusBarComponent.Builder> provider, final PluginManager pluginManager, final Optional<Divider> optional2, final LightsOutNotifController lightsOutNotifController, final StatusBarNotificationActivityStarter.Builder builder, final ShadeController shadeController, final SuperStatusBarViewFactory superStatusBarViewFactory, final StatusBarKeyguardViewManager statusBarKeyguardViewManager, final ViewMediatorCallback viewMediatorCallback, final InitController initController, final DarkIconDispatcher darkIconDispatcher, final Handler handler, final PluginDependencyProvider pluginDependencyProvider, final KeyguardDismissUtil keyguardDismissUtil, final ExtensionController extensionController, final UserInfoControllerImpl userInfoControllerImpl, final PhoneStatusBarPolicy phoneStatusBarPolicy, final KeyguardIndicationController keyguardIndicationController, final Lazy<NotificationShadeDepthController> lazy4, final DismissCallbackRegistry dismissCallbackRegistry, final StatusBarTouchableRegionManager statusBarTouchableRegionManager) {
        return new StatusBar(context, notificationsController, lightBarController, autoHideController, keyguardUpdateMonitor, statusBarIconController, pulseExpansionHandler, notificationWakeUpCoordinator, keyguardBypassController, keyguardStateController, headsUpManagerPhone, dynamicPrivacyController, bypassHeadsUpNotifier, falsingManager, broadcastDispatcher, remoteInputQuickSettingsDisabler, notificationGutsManager, notificationLogger, notificationInterruptStateProvider, notificationViewHierarchyManager, keyguardViewMediator, notificationAlertingManager, displayMetrics, metricsLogger, executor, notificationMediaManager, notificationLockscreenUserManager, notificationRemoteInputManager, userSwitcherController, networkController, batteryController, sysuiColorExtractor, screenLifecycle, wakefulnessLifecycle, sysuiStatusBarStateController, vibratorHelper, bubbleController, notificationGroupManager, visualStabilityManager, deviceProvisionedController, navigationBarController, lazy, configurationController, notificationShadeWindowController, lockscreenLockIconController, dozeParameters, scrimController, keyguardLiftController, lazy2, lazy3, dozeServiceHost, powerManager, screenPinningRequest, dozeScrimController, volumeComponent, commandQueue, optional, provider, pluginManager, optional2, lightsOutNotifController, builder, shadeController, superStatusBarViewFactory, statusBarKeyguardViewManager, viewMediatorCallback, initController, darkIconDispatcher, handler, pluginDependencyProvider, keyguardDismissUtil, extensionController, userInfoControllerImpl, phoneStatusBarPolicy, keyguardIndicationController, dismissCallbackRegistry, lazy4, statusBarTouchableRegionManager);
    }
}
