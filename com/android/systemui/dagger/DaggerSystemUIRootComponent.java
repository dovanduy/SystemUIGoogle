// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import com.android.systemui.qs.QuickQSPanel;
import com.android.systemui.qs.QuickStatusBarHeader;
import com.android.systemui.qs.QSFooterImpl;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.qs.customize.QSCustomizer;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.keyguard.KeyguardSliceView;
import com.android.keyguard.KeyguardMessageArea;
import com.android.keyguard.KeyguardClockSwitch;
import com.android.systemui.util.InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory;
import com.android.systemui.util.InjectionInflationController_ViewAttributeProvider_ProvideContextFactory;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.qs.customize.TileQueryHelper;
import com.android.systemui.statusbar.notification.stack.NotificationSectionsManager_Factory;
import com.android.systemui.statusbar.notification.people.PeopleHubViewAdapter;
import com.android.systemui.statusbar.notification.stack.NotificationSectionsManager;
import com.android.systemui.statusbar.phone.NotificationShadeWindowViewController;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.statusbar.phone.NotificationPanelViewController_Factory;
import com.android.systemui.statusbar.phone.dagger.StatusBarViewModule_GetNotificationPanelViewFactory;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.statusbar.phone.NotificationPanelView;
import com.android.systemui.statusbar.FlingAnimationUtils_Builder_Factory;
import com.android.systemui.statusbar.phone.NotificationShadeWindowView;
import com.android.systemui.qs.QSFragment;
import com.android.systemui.statusbar.phone.NavigationBarFragment;
import com.android.keyguard.CarrierTextController;
import com.android.systemui.qs.carrier.QSCarrierGroupController;
import com.android.systemui.qs.QuickStatusBarHeaderController;
import com.android.systemui.qs.QSContainerImplController;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationViewController;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRowController_Factory;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.row.ExpandableOutlineViewController;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableViewController;
import com.android.systemui.statusbar.notification.row.ExpandableOutlineView;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideStatusBarNotificationFactory;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideNotificationKeyFactory;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideAppNameFactory;
import com.android.systemui.statusbar.notification.row.ExpandableViewController_Factory;
import com.android.systemui.statusbar.notification.row.ExpandableOutlineViewController_Factory;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRowController;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationViewController_Factory;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.Dependency_MembersInjector;
import dagger.internal.Preconditions;
import android.content.ContentProvider;
import com.android.systemui.Dependency;
import com.android.systemui.SystemUIAppComponentFactory_MembersInjector;
import com.android.systemui.SystemUIAppComponentFactory;
import com.android.systemui.keyguard.KeyguardSliceProvider_MembersInjector;
import com.android.systemui.keyguard.KeyguardSliceProvider;
import com.android.systemui.statusbar.notification.people.PeopleHubViewAdapterImpl_Factory;
import com.android.systemui.statusbar.notification.people.DataSource;
import com.android.systemui.statusbar.notification.people.PeopleHubViewModelFactoryDataSourceImpl_Factory;
import com.android.systemui.statusbar.notification.people.PeopleHubDataSourceImpl_Factory;
import com.android.systemui.statusbar.phone.AutoTileManager;
import com.android.systemui.plugins.qs.QSFactory;
import com.android.systemui.qs.QSTileHost_Factory;
import com.android.systemui.log.dagger.LogModule_ProvideQuickSettingsLogBufferFactory;
import com.android.systemui.qs.AutoAddTracker;
import com.android.systemui.qs.tiles.ScreenRecordTile;
import com.android.systemui.qs.tiles.UiModeNightTile;
import com.android.systemui.qs.tiles.NfcTile;
import com.android.systemui.qs.tiles.NightDisplayTile;
import com.android.systemui.qs.tiles.DataSaverTile;
import com.android.systemui.qs.tiles.BatterySaverTile;
import com.android.systemui.qs.tiles.UserTile;
import com.android.systemui.qs.tiles.HotspotTile;
import com.android.systemui.qs.tiles.CastTile;
import com.android.systemui.qs.tiles.LocationTile;
import com.android.systemui.qs.tiles.FlashlightTile;
import com.android.systemui.qs.tiles.RotationLockTile;
import com.android.systemui.qs.tiles.WorkModeTile;
import com.android.systemui.qs.tiles.AirplaneModeTile;
import com.android.systemui.qs.tiles.ColorInversionTile;
import com.android.systemui.qs.tiles.DndTile;
import com.android.systemui.qs.tiles.CellularTile;
import com.android.systemui.qs.tiles.BluetoothTile;
import com.android.systemui.qs.tiles.WifiTile;
import com.android.systemui.qs.tileimpl.QSFactoryImpl_Factory;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import com.android.systemui.qs.QSHost;
import com.android.keyguard.KeyguardSecurityModel_Factory;
import com.android.systemui.statusbar.notification.row.ChannelEditorDialogController_Factory;
import com.android.keyguard.clock.ClockManager_Factory;
import com.android.systemui.ForegroundServiceNotificationListener_Factory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationBlockingHelperManagerFactory;
import com.android.systemui.power.PowerNotificationWarnings_Factory;
import com.android.systemui.UiOffloadThread_Factory;
import com.android.systemui.tuner.TunablePadding_TunablePaddingService_Factory;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper_Factory;
import com.android.systemui.fragments.FragmentService_Factory;
import com.android.systemui.statusbar.phone.StatusBarWindowController_Factory;
import com.android.systemui.statusbar.policy.SecurityControllerImpl_Factory;
import com.android.systemui.statusbar.phone.ManagedProfileControllerImpl_Factory;
import com.android.systemui.statusbar.policy.FlashlightControllerImpl_Factory;
import com.android.systemui.recents.OverviewProxyRecentsImpl_Factory;
import com.android.systemui.accessibility.WindowMagnification_Factory;
import com.android.systemui.volume.VolumeUI_Factory;
import com.android.systemui.statusbar.tv.TvStatusBar_Factory;
import com.android.systemui.toast.ToastUI_Factory;
import com.android.systemui.theme.ThemeOverlayController_Factory;
import com.android.systemui.accessibility.SystemActions_Factory;
import com.android.systemui.SliceBroadcastRelayHandler_Factory;
import com.android.systemui.SizeCompatModeActivityController_Factory;
import com.android.systemui.shortcut.ShortcutKeyDispatcher_Factory;
import com.android.systemui.ScreenDecorations_Factory;
import com.android.systemui.power.PowerUI_Factory;
import com.android.systemui.LatencyTester_Factory;
import com.android.systemui.statusbar.notification.InstantAppNotifier_Factory;
import com.android.systemui.globalactions.GlobalActionsComponent_Factory;
import com.android.systemui.globalactions.GlobalActionsDialog;
import com.android.systemui.plugins.GlobalActions;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideUiEventLoggerFactory;
import dagger.internal.Factory;
import com.android.systemui.util.leak.GarbageMonitor_Service_Factory;
import com.android.systemui.util.leak.GarbageMonitor_Factory;
import com.android.systemui.util.leak.LeakReporter_Factory;
import com.android.systemui.biometrics.AuthController_Factory;
import com.android.systemui.screenrecord.RecordingService;
import com.android.systemui.screenshot.TakeScreenshotService;
import com.android.systemui.dump.SystemUIAuxiliaryDumpService;
import com.android.systemui.SystemUIService;
import com.android.systemui.keyguard.KeyguardService;
import com.android.systemui.ImageWallpaper;
import com.android.systemui.doze.DozeService;
import com.android.systemui.screenshot.GlobalScreenshotLegacy_Factory;
import com.android.systemui.screenshot.ScreenshotNotificationsController;
import com.android.systemui.screenshot.GlobalScreenshot_Factory;
import com.android.systemui.keyguard.KeyguardLifecyclesDispatcher_Factory;
import com.android.systemui.doze.DozeFactory;
import com.android.systemui.controls.management.ControlsRequestDialog;
import com.android.systemui.controls.management.ControlsFavoritingActivity;
import com.android.systemui.controls.management.ControlsProviderSelectorActivity;
import com.android.systemui.bubbles.BubbleOverflowActivity;
import com.android.systemui.screenrecord.ScreenRecordDialog;
import com.android.systemui.settings.BrightnessDialog;
import com.android.systemui.keyguard.WorkLockActivity;
import com.android.systemui.ForegroundServicesDialog_Factory;
import com.android.systemui.ForegroundServicesDialog;
import com.android.systemui.tuner.TunerActivity_Factory;
import com.android.systemui.tuner.TunerActivity;
import dagger.internal.MapProviderFactory;
import com.android.systemui.controls.controller.ControlsBindingController;
import com.android.systemui.controls.ui.ControlsUiController;
import com.android.systemui.controls.controller.ControlsControllerImpl_Factory;
import com.android.systemui.controls.controller.ControlsBindingControllerImpl_Factory;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.controls.ui.ControlsUiControllerImpl_Factory;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideBackgroundDelayableExecutorFactory;
import com.android.systemui.controls.management.ControlsListingControllerImpl_Factory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideExecutorFactory;
import com.android.systemui.bubbles.dagger.BubbleModule_NewBubbleControllerFactory;
import com.android.systemui.bubbles.BubbleData_Factory;
import com.android.systemui.statusbar.phone.ShadeControllerImpl_Factory;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager_Factory;
import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideNotificationMediaManagerFactory;
import com.android.systemui.media.MediaControllerFactory;
import com.android.keyguard.KeyguardMediaPlayer_Factory;
import com.android.systemui.statusbar.MediaArtworkProcessor_Factory;
import com.android.systemui.statusbar.phone.PhoneStatusBarPolicy;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.volume.VolumeComponent;
import com.android.systemui.recents.ScreenPinningRequest;
import com.android.systemui.statusbar.phone.dagger.StatusBarPhoneModule_ProvideStatusBarFactory;
import com.android.systemui.statusbar.phone.StatusBarTouchableRegionManager_Factory;
import com.android.systemui.statusbar.NotificationShadeDepthController_Factory;
import com.android.systemui.util.time.DateFormatUtil;
import android.content.SharedPreferences;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.SensorPrivacyControllerImpl_Factory;
import com.android.systemui.statusbar.policy.LocationControllerImpl_Factory;
import com.android.systemui.statusbar.policy.ZenModeControllerImpl_Factory;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.RotationLockControllerImpl_Factory;
import com.android.systemui.statusbar.policy.NextAlarmControllerImpl_Factory;
import com.android.systemui.statusbar.policy.BluetoothControllerImpl_Factory;
import com.android.systemui.statusbar.policy.HotspotControllerImpl_Factory;
import com.android.systemui.statusbar.policy.CastControllerImpl_Factory;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl_Factory;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil_Factory;
import com.android.systemui.plugins.PluginDependencyProvider_Factory;
import com.android.systemui.InitController_Factory;
import com.android.systemui.statusbar.SuperStatusBarViewFactory_Factory;
import com.android.systemui.util.InjectionInflationController_Factory;
import com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter_Builder_Factory;
import com.android.systemui.ActivityIntentHelper_Factory;
import com.android.systemui.statusbar.phone.StatusBarRemoteInputCallback_Factory;
import com.android.systemui.statusbar.phone.LightsOutNotifController_Factory;
import com.android.systemui.volume.VolumeDialogComponent_Factory;
import com.android.systemui.volume.VolumeDialogControllerImpl_Factory;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.phone.DozeServiceHost_Factory;
import com.android.systemui.statusbar.phone.BiometricUnlockController_Factory;
import com.android.systemui.statusbar.phone.DozeScrimController_Factory;
import com.android.systemui.doze.DozeLogger;
import com.android.systemui.doze.DozeLog_Factory;
import com.android.systemui.log.dagger.LogModule_ProvideDozeLogBufferFactory;
import android.app.IWallpaperManager;
import android.app.WallpaperManager;
import com.android.systemui.statusbar.phone.LockscreenWallpaper_Factory;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import com.android.systemui.statusbar.phone.ScrimController_Factory;
import com.android.systemui.statusbar.BlurUtils_Factory;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.LockscreenLockIconController_Factory;
import com.android.systemui.util.wakelock.WakeLock;
import com.android.systemui.statusbar.KeyguardIndicationController_Factory;
import com.android.systemui.statusbar.policy.AccessibilityController_Factory;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger_Factory;
import com.android.systemui.assist.AssistManager_Factory;
import com.android.systemui.assist.PhoneStateMonitor_Factory;
import com.android.systemui.pip.BasePipManager;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.assist.AssistHandleBehavior;
import com.android.systemui.assist.AssistHandleViewController;
import com.android.systemui.assist.AssistHandleBehaviorController_Factory;
import com.android.systemui.assist.AssistModule_ProvideAssistHandleBehaviorControllerMapFactory;
import com.android.systemui.BootCompleteCache;
import com.android.systemui.assist.AssistHandleReminderExpBehavior_Factory;
import com.android.systemui.recents.OverviewProxyService_Factory;
import com.android.systemui.stackdivider.DividerModule_ProvideDividerFactory;
import com.android.systemui.wm.DisplayImeController_Factory;
import com.android.systemui.TransactionPool_Factory;
import com.android.systemui.wm.SystemWindows_Factory;
import com.android.systemui.pip.PipUI_Factory;
import com.android.systemui.pip.PipBoundsHandler;
import com.android.systemui.pip.phone.PipManager_Factory;
import com.android.systemui.pip.PipSurfaceTransactionHelper_Factory;
import com.android.systemui.pip.PipSnapAlgorithm;
import com.android.systemui.util.FloatingContentCoordinator_Factory;
import com.android.systemui.wm.DisplayController_Factory;
import com.android.systemui.assist.AssistModule_ProvideSystemClockFactory;
import com.android.systemui.assist.AssistHandleLikeHomeBehavior_Factory;
import com.android.systemui.assist.AssistHandleOffBehavior_Factory;
import com.android.systemui.assist.DeviceConfigHelper_Factory;
import com.android.systemui.assist.AssistModule_ProvideBackgroundHandlerFactory;
import com.android.systemui.assist.AssistModule_ProvideAssistUtilsFactory;
import com.android.systemui.statusbar.VibratorHelper_Factory;
import com.android.systemui.keyguard.WakefulnessLifecycle_Factory;
import com.android.systemui.keyguard.ScreenLifecycle_Factory;
import com.android.systemui.statusbar.policy.NetworkControllerImpl_Factory;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.policy.UserSwitcherController_Factory;
import com.android.systemui.ActivityStarterDelegate_Factory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationAlertingManagerFactory;
import com.android.systemui.statusbar.notification.DynamicChildBindController;
import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideNotificationViewHierarchyManagerFactory;
import com.android.systemui.statusbar.notification.stack.ForegroundServiceSectionController_Factory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationLoggerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationPanelLoggerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationGutsManagerFactory;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler_Factory;
import com.android.systemui.statusbar.notification.interruption.BypassHeadsUpNotifier_Factory;
import com.android.systemui.statusbar.notification.DynamicPrivacyController_Factory;
import com.android.systemui.statusbar.PulseExpansionHandler_Factory;
import com.android.systemui.statusbar.notification.stack.NotificationRoundnessManager_Factory;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator_Factory;
import com.android.systemui.statusbar.phone.StatusBarIconControllerImpl_Factory;
import com.android.systemui.statusbar.phone.LightBarController_Factory;
import com.android.systemui.statusbar.phone.DarkIconDispatcherImpl_Factory;
import com.android.systemui.statusbar.notification.init.NotificationsControllerStub;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationsControllerFactory;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineInitializer;
import com.android.systemui.statusbar.notification.init.NotificationsControllerImpl_Factory;
import com.android.systemui.statusbar.notification.headsup.HeadsUpBindController_Factory;
import com.android.systemui.statusbar.phone.dagger.StatusBarPhoneDependenciesModule_ProvideNotificationGroupAlertTransferHelperFactory;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescer;
import com.android.systemui.statusbar.notification.collection.init.NotifPipelineInitializer_Factory;
import com.android.systemui.statusbar.notification.collection.NotifViewManager_Factory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideVisualStabilityManagerFactory;
import com.android.systemui.statusbar.notification.collection.coordinator.HideNotifsForOtherUsersCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.NotifCoordinators_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.PreparationCoordinatorLogger;
import com.android.systemui.statusbar.notification.collection.coordinator.PreparationCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.NotifViewBarn_Factory;
import com.android.systemui.statusbar.notification.collection.NotifInflaterImpl_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.ConversationCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.HeadsUpCoordinator_Factory;
import com.android.systemui.statusbar.notification.headsup.HeadsUpViewBinder_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.BubbleCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.DeviceProvisionedCoordinator_Factory;
import com.android.systemui.appops.AppOpsController;
import com.android.systemui.statusbar.notification.collection.coordinator.ForegroundCoordinator_Factory;
import com.android.systemui.ForegroundServiceController_Factory;
import com.android.systemui.appops.AppOpsControllerImpl_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.RankingCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.KeyguardCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescerLogger;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideMainDelayableExecutorFactory;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinder;
import com.android.systemui.statusbar.notification.collection.NotificationRankingManager;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationEntryManagerFactory;
import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController_Factory;
import com.android.systemui.statusbar.notification.icon.IconManager;
import com.android.systemui.statusbar.notification.row.RowInflaterTask;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.internal.util.NotificationMessagingUtil;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl_Factory;
import com.android.systemui.statusbar.notification.row.RowInflaterTask_Factory;
import com.android.systemui.statusbar.notification.icon.IconBuilder;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProviderImpl_Factory;
import com.android.systemui.power.EnhancedEstimates;
import com.android.systemui.statusbar.policy.BatteryControllerImpl_Factory;
import com.android.systemui.power.EnhancedEstimatesImpl_Factory;
import com.android.systemui.statusbar.notification.row.RowContentBindStageLogger;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder;
import com.android.systemui.statusbar.notification.row.RowContentBindStage_Factory;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager_Factory;
import com.android.systemui.statusbar.notification.ConversationNotificationProcessor;
import com.android.systemui.statusbar.notification.row.NotificationContentInflater_Factory;
import com.android.systemui.statusbar.notification.ConversationNotificationManager_Factory;
import com.android.systemui.statusbar.policy.SmartReplyConstants_Factory;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineLogger;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline_Factory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideCommonNotifCollectionFactory;
import com.android.systemui.statusbar.notification.collection.NotifPipeline_Factory;
import com.android.systemui.statusbar.notification.collection.listbuilder.ShadeListBuilderLogger;
import com.android.systemui.statusbar.notification.collection.ShadeListBuilder_Factory;
import com.android.systemui.util.time.SystemClockImpl_Factory;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionLogger;
import com.android.systemui.statusbar.notification.collection.NotifCollection_Factory;
import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideNotificationRemoteInputManagerFactory;
import com.android.systemui.statusbar.policy.RemoteInputUriController_Factory;
import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideSmartReplyControllerFactory;
import com.android.systemui.statusbar.phone.KeyguardEnvironmentImpl_Factory;
import com.android.systemui.statusbar.notification.NotificationEntryManagerLogger;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider_Factory;
import com.android.systemui.statusbar.notification.people.NotificationPersonExtractor;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifierImpl_Factory;
import com.android.systemui.statusbar.notification.people.NotificationPersonExtractorPluginBoundary_Factory;
import com.android.systemui.statusbar.policy.ExtensionControllerImpl_Factory;
import com.android.systemui.statusbar.notification.NotificationFilter_Factory;
import com.android.systemui.statusbar.phone.NotificationGroupManager_Factory;
import com.android.systemui.log.dagger.LogModule_ProvideNotificationsLogBufferFactory;
import com.android.systemui.log.dagger.LogModule_ProvideLogcatEchoTrackerFactory;
import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideNotificationListenerFactory;
import com.android.systemui.statusbar.FeatureFlags_Factory;
import com.android.keyguard.KeyguardViewController;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.keyguard.dagger.KeyguardModule_NewKeyguardViewMediatorFactory;
import com.android.systemui.statusbar.phone.NavigationModeController_Factory;
import com.android.systemui.keyguard.DismissCallbackRegistry_Factory;
import dagger.internal.DelegateFactory;
import com.android.systemui.dock.DockManager;
import com.android.systemui.util.sensors.ProximitySensor;
import com.android.systemui.classifier.FalsingManagerProxy_Factory;
import com.android.systemui.util.DeviceConfigProxy_Factory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideUiBackgroundExecutorFactory;
import com.android.systemui.dock.DockManagerImpl_Factory;
import com.android.systemui.util.sensors.AsyncSensorManager_Factory;
import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideCommandQueueFactory;
import com.android.systemui.tracing.ProtoTracer_Factory;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController_Factory;
import com.android.systemui.colorextraction.SysuiColorExtractor_Factory;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.phone.KeyguardBypassController_Factory;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl_Factory;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.statusbar.policy.KeyguardStateControllerImpl_Factory;
import com.android.keyguard.KeyguardUpdateMonitor_Factory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideBackgroundExecutorFactory;
import com.android.systemui.statusbar.policy.DeviceProvisionedControllerImpl_Factory;
import com.android.systemui.statusbar.StatusBarStateControllerImpl_Factory;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.doze.AlwaysOnDisplayPolicy;
import android.hardware.display.AmbientDisplayConfiguration;
import com.android.systemui.statusbar.phone.DozeParameters_Factory;
import com.android.systemui.tuner.TunerServiceImpl_Factory;
import com.android.systemui.screenrecord.RecordingController_Factory;
import com.android.systemui.broadcast.BroadcastDispatcher_Factory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideBgLooperFactory;
import com.android.systemui.BootCompleteCacheImpl_Factory;
import dagger.internal.DoubleCheck;
import android.content.Context;
import com.android.systemui.dump.DumpManager_Factory;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideMainLooperFactory;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import android.content.res.Resources;
import dagger.internal.InstanceFactory;
import com.android.systemui.statusbar.policy.ZenModeControllerImpl;
import com.android.systemui.qs.tiles.WorkModeTile_Factory;
import com.android.systemui.keyguard.WorkLockActivity_Factory;
import com.android.systemui.accessibility.WindowMagnification;
import com.android.systemui.qs.tiles.WifiTile_Factory;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.volume.VolumeUI;
import com.android.systemui.volume.VolumeDialogControllerImpl;
import com.android.systemui.volume.VolumeDialogComponent;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.qs.tiles.UserTile_Factory;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.qs.tiles.UiModeNightTile_Factory;
import com.android.systemui.statusbar.tv.TvStatusBar;
import com.android.systemui.tuner.TunerServiceImpl;
import com.android.systemui.tuner.TunablePadding;
import com.android.systemui.TransactionPool;
import com.android.systemui.toast.ToastUI;
import com.android.systemui.theme.ThemeOverlayController;
import com.android.systemui.screenshot.TakeScreenshotService_Factory;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.wm.SystemWindows;
import com.android.systemui.SystemUIService_Factory;
import com.android.systemui.dump.SystemUIAuxiliaryDumpService_Factory;
import com.android.systemui.accessibility.SystemActions;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.phone.StatusBarTouchableRegionManager;
import com.android.systemui.statusbar.StatusBarStateControllerImpl;
import com.android.systemui.statusbar.phone.StatusBarRemoteInputCallback;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.StatusBarIconControllerImpl;
import com.android.systemui.statusbar.phone.dagger.StatusBarComponent;
import com.android.systemui.statusbar.policy.SmartReplyConstants;
import com.android.systemui.SliceBroadcastRelayHandler;
import com.android.systemui.SizeCompatModeActivityController;
import com.android.systemui.shortcut.ShortcutKeyDispatcher;
import com.android.systemui.statusbar.notification.collection.ShadeListBuilder;
import com.android.systemui.statusbar.notification.collection.listbuilder.ShadeListBuilderLogger_Factory;
import com.android.systemui.statusbar.phone.ShadeControllerImpl;
import com.android.systemui.statusbar.policy.SensorPrivacyControllerImpl;
import com.android.systemui.statusbar.policy.SecurityControllerImpl;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.screenshot.ScreenshotNotificationsController_Factory;
import com.android.systemui.qs.tiles.ScreenRecordTile_Factory;
import com.android.systemui.screenrecord.ScreenRecordDialog_Factory;
import com.android.systemui.recents.ScreenPinningRequest_Factory;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import com.android.systemui.statusbar.notification.row.RowContentBindStageLogger_Factory;
import com.android.systemui.qs.tiles.RotationLockTile_Factory;
import com.android.systemui.statusbar.policy.RotationLockControllerImpl;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.screenrecord.RecordingService_Factory;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.statusbar.notification.collection.coordinator.RankingCoordinator;
import com.android.systemui.qs.QSTileHost;
import com.android.systemui.qs.logging.QSLogger_Factory;
import com.android.systemui.qs.tileimpl.QSFactoryImpl;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.util.sensors.ProximitySensor_Factory;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.net.wifi.WifiManager;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import android.os.Vibrator;
import android.os.UserManager;
import com.android.internal.logging.UiEventLogger;
import android.app.trust.TrustManager;
import android.telephony.TelephonyManager;
import android.telecom.TelecomManager;
import androidx.slice.Clock;
import com.android.systemui.model.SysUiState;
import com.android.systemui.statusbar.SmartReplyController;
import android.content.pm.ShortcutManager;
import android.hardware.SensorPrivacyManager;
import com.android.systemui.recents.RecentsModule_ProvideRecentsImplFactory;
import android.os.PowerManager;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.system.PackageManagerWrapper;
import android.content.pm.PackageManager;
import com.android.systemui.statusbar.notification.init.NotificationsController;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.logging.NotificationPanelLogger;
import com.android.systemui.statusbar.NotificationMediaManager;
import android.app.NotificationManager;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.row.NotificationBlockingHelperManager;
import com.android.systemui.statusbar.notification.interruption.NotificationAlertingManager;
import com.android.systemui.statusbar.notification.row.NotifRemoteViewCache;
import android.hardware.display.NightDisplayListener;
import android.net.NetworkScoreManager;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideMainHandlerFactory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideMainExecutorFactory;
import com.android.systemui.log.LogcatEchoTracker;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.systemui.util.leak.LeakDetector;
import android.content.pm.LauncherApps;
import com.android.internal.util.LatencyTracker;
import android.app.KeyguardManager;
import com.android.systemui.statusbar.phone.KeyguardLiftController;
import android.view.IWindowManager;
import com.android.internal.statusbar.IStatusBarService;
import android.content.pm.IPackageManager;
import android.app.INotificationManager;
import android.service.dreams.IDreamManager;
import com.android.internal.app.IBatteryStats;
import android.app.IActivityManager;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.log.LogBuffer;
import android.util.DisplayMetrics;
import com.android.systemui.shared.system.DevicePolicyManagerWrapper;
import android.app.admin.DevicePolicyManager;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.SystemUIFactory_ContextHolder_ProvideContextFactory;
import android.content.ContentResolver;
import android.net.ConnectivityManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import com.android.systemui.statusbar.CommandQueue;
import android.os.Looper;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideBgHandlerFactory;
import android.os.Handler;
import java.util.concurrent.Executor;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.statusbar.phone.AutoHideController;
import android.media.AudioManager;
import com.android.internal.app.AssistUtils;
import com.android.systemui.assist.AssistModule_ProvideAssistHandleViewControllerFactory;
import android.app.AlarmManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import android.app.ActivityManager;
import android.view.accessibility.AccessibilityManager;
import com.android.systemui.tracing.ProtoTracer;
import com.android.systemui.statusbar.notification.collection.coordinator.PreparationCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.PreparationCoordinatorLogger_Factory;
import com.android.systemui.power.PowerUI;
import com.android.systemui.power.PowerNotificationWarnings;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.pip.PipUI;
import com.android.systemui.pip.PipSurfaceTransactionHelper;
import com.android.systemui.pip.PipSnapAlgorithm_Factory;
import com.android.systemui.pip.phone.PipManager;
import com.android.systemui.pip.PipBoundsHandler_Factory;
import com.android.systemui.statusbar.phone.PhoneStatusBarPolicy_Factory;
import com.android.systemui.assist.PhoneStateMonitor;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifierImpl;
import com.android.systemui.statusbar.notification.people.PeopleHubViewModelFactoryDataSourceImpl;
import com.android.systemui.statusbar.notification.people.PeopleHubViewAdapterImpl;
import com.android.systemui.statusbar.notification.people.PeopleHubDataSourceImpl;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.recents.OverviewProxyRecentsImpl;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.recents.Recents;
import dagger.Lazy;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.controls.controller.ControlsFavoritePersistenceWrapper;
import java.util.Optional;
import com.android.systemui.statusbar.notification.init.NotificationsControllerStub_Factory;
import com.android.systemui.statusbar.notification.init.NotificationsControllerImpl;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager_Factory;
import com.android.systemui.statusbar.notification.row.dagger.NotificationRowComponent;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.notification.stack.NotificationRoundnessManager;
import com.android.systemui.statusbar.notification.collection.NotificationRankingManager_Factory;
import com.android.systemui.statusbar.notification.people.NotificationPersonExtractorPluginBoundary;
import com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProviderImpl;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.notification.NotificationEntryManagerLogger_Factory;
import com.android.systemui.statusbar.notification.row.NotificationContentInflater;
import com.android.systemui.statusbar.notification.collection.NotifViewManager;
import com.android.systemui.statusbar.notification.collection.NotifViewBarn;
import com.android.systemui.statusbar.notification.row.NotifRemoteViewCacheImpl_Factory;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.init.NotifPipelineInitializer;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager;
import com.android.systemui.statusbar.notification.collection.NotifInflaterImpl;
import com.android.systemui.statusbar.notification.collection.coordinator.NotifCoordinators;
import com.android.systemui.statusbar.notification.collection.NotifCollection;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionLogger_Factory;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineLogger_Factory;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineInitializer_Factory;
import com.android.systemui.qs.tiles.NightDisplayTile_Factory;
import com.android.systemui.qs.tiles.NfcTile_Factory;
import com.android.systemui.statusbar.policy.NextAlarmControllerImpl;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.statusbar.policy.NetworkControllerImpl;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.util.leak.GarbageMonitor_MemoryTile_Factory;
import com.android.systemui.media.MediaControllerFactory_Factory;
import com.android.systemui.statusbar.MediaArtworkProcessor;
import com.android.systemui.SystemUI;
import android.app.Service;
import com.android.systemui.recents.RecentsImplementation;
import android.content.BroadcastReceiver;
import android.app.Activity;
import java.util.Map;
import com.android.systemui.statusbar.phone.ManagedProfileControllerImpl;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import com.android.systemui.statusbar.phone.LockscreenLockIconController;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger;
import com.android.systemui.qs.tiles.LocationTile_Factory;
import com.android.systemui.statusbar.policy.LocationControllerImpl;
import com.android.systemui.statusbar.phone.LightsOutNotifController;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.util.leak.LeakReporter;
import com.android.systemui.LatencyTester;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.policy.KeyguardStateControllerImpl;
import com.android.systemui.keyguard.KeyguardService_Factory;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.keyguard.KeyguardMediaPlayer;
import com.android.systemui.keyguard.KeyguardLifecyclesDispatcher;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.phone.KeyguardEnvironmentImpl;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import com.android.systemui.statusbar.notification.collection.coordinator.KeyguardCoordinator;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.notification.InstantAppNotifier;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.InitController;
import com.android.systemui.ImageWallpaper_Factory;
import com.android.systemui.statusbar.notification.icon.IconManager_Factory;
import com.android.systemui.statusbar.notification.icon.IconBuilder_Factory;
import com.android.systemui.qs.tiles.HotspotTile_Factory;
import com.android.systemui.statusbar.policy.HotspotControllerImpl;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.notification.collection.coordinator.HideNotifsForOtherUsersCoordinator_Factory;
import com.android.systemui.statusbar.notification.headsup.HeadsUpViewBinder;
import com.android.systemui.statusbar.notification.collection.coordinator.HeadsUpCoordinator;
import com.android.systemui.statusbar.notification.headsup.HeadsUpBindController;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescer_Factory;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescerLogger_Factory;
import com.android.systemui.screenshot.GlobalScreenshot;
import com.android.systemui.screenshot.GlobalScreenshotLegacy;
import com.android.systemui.globalactions.GlobalActionsImpl_Factory;
import com.android.systemui.globalactions.GlobalActionsDialog_Factory;
import com.android.systemui.globalactions.GlobalActionsComponent;
import com.android.systemui.util.leak.GarbageMonitor;
import com.android.systemui.fragments.FragmentService;
import com.android.systemui.statusbar.notification.stack.ForegroundServiceSectionController;
import com.android.systemui.ForegroundServiceNotificationListener;
import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController;
import com.android.systemui.ForegroundServiceController;
import com.android.systemui.statusbar.notification.collection.coordinator.ForegroundCoordinator;
import com.android.systemui.util.FloatingContentCoordinator;
import com.android.systemui.qs.tiles.FlashlightTile_Factory;
import com.android.systemui.statusbar.policy.FlashlightControllerImpl;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.classifier.FalsingManagerProxy;
import com.android.systemui.statusbar.policy.ExtensionControllerImpl;
import com.android.systemui.statusbar.notification.logging.NotificationLogger_ExpansionStateLogger_Factory;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent;
import com.android.systemui.power.EnhancedEstimatesImpl;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.DynamicChildBindController_Factory;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.doze.DozeService_Factory;
import com.android.systemui.statusbar.phone.DozeServiceHost;
import com.android.systemui.statusbar.phone.DozeScrimController;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.doze.DozeLogger_Factory;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.doze.DozeFactory_Factory;
import com.android.systemui.dock.DockManagerImpl;
import com.android.systemui.qs.tiles.DndTile_Factory;
import com.android.systemui.wm.DisplayImeController;
import com.android.systemui.wm.DisplayController;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.statusbar.notification.collection.coordinator.DeviceProvisionedCoordinator;
import com.android.systemui.statusbar.policy.DeviceProvisionedControllerImpl;
import com.android.systemui.assist.DeviceConfigHelper;
import com.android.systemui.util.time.DateFormatUtil_Factory;
import com.android.systemui.qs.tiles.DataSaverTile_Factory;
import com.android.systemui.statusbar.phone.DarkIconDispatcherImpl;
import com.android.systemui.statusbar.notification.ConversationNotificationProcessor_Factory;
import com.android.systemui.statusbar.notification.ConversationNotificationManager;
import com.android.systemui.statusbar.notification.collection.coordinator.ConversationCoordinator;
import com.android.systemui.controls.ui.ControlsUiControllerImpl;
import com.android.systemui.controls.management.ControlsRequestDialog_Factory;
import com.android.systemui.controls.management.ControlsProviderSelectorActivity_Factory;
import com.android.systemui.controls.management.ControlsListingControllerImpl;
import com.android.systemui.controls.management.ControlsFavoritingActivity_Factory;
import com.android.systemui.controls.controller.ControlsControllerImpl;
import com.android.systemui.controls.controller.ControlsBindingControllerImpl;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.qs.tiles.ColorInversionTile_Factory;
import com.android.keyguard.clock.ClockManager;
import com.android.systemui.statusbar.notification.row.ChannelEditorDialogController;
import com.android.systemui.qs.tiles.CellularTile_Factory;
import com.android.systemui.qs.tiles.CastTile_Factory;
import com.android.systemui.statusbar.policy.CastControllerImpl;
import com.android.systemui.statusbar.notification.interruption.BypassHeadsUpNotifier;
import com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter;
import com.android.systemui.util.wakelock.DelayedWakeLock_Builder_Factory;
import com.android.systemui.util.wakelock.WakeLock_Builder_Factory;
import com.android.systemui.bubbles.BubbleOverflowActivity_Factory;
import com.android.systemui.bubbles.BubbleData;
import com.android.systemui.statusbar.notification.collection.coordinator.BubbleCoordinator;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.settings.BrightnessDialog_Factory;
import com.android.systemui.BootCompleteCacheImpl;
import com.android.systemui.statusbar.BlurUtils;
import com.android.systemui.qs.tiles.BluetoothTile_Factory;
import com.android.systemui.statusbar.policy.BluetoothControllerImpl;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.util.time.SystemClock;
import com.android.systemui.qs.tiles.BatterySaverTile_Factory;
import com.android.systemui.statusbar.policy.BatteryControllerImpl;
import com.android.systemui.statusbar.phone.AutoTileManager_Factory;
import com.android.systemui.qs.AutoAddTracker_Factory;
import com.android.systemui.biometrics.AuthController;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.assist.AssistHandleBehaviorController;
import com.android.systemui.appops.AppOpsControllerImpl;
import com.android.systemui.qs.tiles.AirplaneModeTile_Factory;
import com.android.systemui.ActivityStarterDelegate;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.screenshot.GlobalScreenshot_ActionProxyReceiver_Factory;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.AccessibilityController;
import javax.inject.Provider;

public final class DaggerSystemUIRootComponent implements SystemUIRootComponent
{
    private static final Provider ABSENT_JDK_OPTIONAL_PROVIDER;
    private Provider<AccessibilityController> accessibilityControllerProvider;
    private Provider<AccessibilityManagerWrapper> accessibilityManagerWrapperProvider;
    private GlobalScreenshot_ActionProxyReceiver_Factory actionProxyReceiverProvider;
    private Provider<ActivityIntentHelper> activityIntentHelperProvider;
    private Provider<ActivityStarterDelegate> activityStarterDelegateProvider;
    private AirplaneModeTile_Factory airplaneModeTileProvider;
    private Provider<AppOpsControllerImpl> appOpsControllerImplProvider;
    private Provider<AssistHandleBehaviorController> assistHandleBehaviorControllerProvider;
    private Provider assistHandleLikeHomeBehaviorProvider;
    private Provider assistHandleOffBehaviorProvider;
    private Provider assistHandleReminderExpBehaviorProvider;
    private Provider<AssistManager> assistManagerProvider;
    private Provider<AsyncSensorManager> asyncSensorManagerProvider;
    private Provider<AuthController> authControllerProvider;
    private AutoAddTracker_Factory autoAddTrackerProvider;
    private AutoTileManager_Factory autoTileManagerProvider;
    private Provider<BatteryControllerImpl> batteryControllerImplProvider;
    private BatterySaverTile_Factory batterySaverTileProvider;
    private Provider<SystemClock> bindSystemClockProvider;
    private Provider<BiometricUnlockController> biometricUnlockControllerProvider;
    private Provider<BluetoothControllerImpl> bluetoothControllerImplProvider;
    private BluetoothTile_Factory bluetoothTileProvider;
    private Provider<BlurUtils> blurUtilsProvider;
    private Provider<BootCompleteCacheImpl> bootCompleteCacheImplProvider;
    private BrightnessDialog_Factory brightnessDialogProvider;
    private Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private Provider<BubbleCoordinator> bubbleCoordinatorProvider;
    private Provider<BubbleData> bubbleDataProvider;
    private BubbleOverflowActivity_Factory bubbleOverflowActivityProvider;
    private WakeLock_Builder_Factory builderProvider;
    private DelayedWakeLock_Builder_Factory builderProvider2;
    private Provider<StatusBarNotificationActivityStarter.Builder> builderProvider3;
    private Provider<BypassHeadsUpNotifier> bypassHeadsUpNotifierProvider;
    private Provider<CastControllerImpl> castControllerImplProvider;
    private CastTile_Factory castTileProvider;
    private CellularTile_Factory cellularTileProvider;
    private Provider<ChannelEditorDialogController> channelEditorDialogControllerProvider;
    private Provider<ClockManager> clockManagerProvider;
    private ColorInversionTile_Factory colorInversionTileProvider;
    private Provider<ContextComponentResolver> contextComponentResolverProvider;
    private SystemUIFactory.ContextHolder contextHolder;
    private Provider<ControlsBindingControllerImpl> controlsBindingControllerImplProvider;
    private Provider<ControlsControllerImpl> controlsControllerImplProvider;
    private ControlsFavoritingActivity_Factory controlsFavoritingActivityProvider;
    private Provider<ControlsListingControllerImpl> controlsListingControllerImplProvider;
    private ControlsProviderSelectorActivity_Factory controlsProviderSelectorActivityProvider;
    private ControlsRequestDialog_Factory controlsRequestDialogProvider;
    private Provider<ControlsUiControllerImpl> controlsUiControllerImplProvider;
    private Provider<ConversationCoordinator> conversationCoordinatorProvider;
    private Provider<ConversationNotificationManager> conversationNotificationManagerProvider;
    private ConversationNotificationProcessor_Factory conversationNotificationProcessorProvider;
    private Provider<DarkIconDispatcherImpl> darkIconDispatcherImplProvider;
    private DataSaverTile_Factory dataSaverTileProvider;
    private DateFormatUtil_Factory dateFormatUtilProvider;
    private Provider<DeviceConfigHelper> deviceConfigHelperProvider;
    private Provider<DeviceProvisionedControllerImpl> deviceProvisionedControllerImplProvider;
    private Provider<DeviceProvisionedCoordinator> deviceProvisionedCoordinatorProvider;
    private Provider<DismissCallbackRegistry> dismissCallbackRegistryProvider;
    private Provider<DisplayController> displayControllerProvider;
    private Provider<DisplayImeController> displayImeControllerProvider;
    private DndTile_Factory dndTileProvider;
    private Provider<DockManagerImpl> dockManagerImplProvider;
    private DozeFactory_Factory dozeFactoryProvider;
    private Provider<DozeLog> dozeLogProvider;
    private DozeLogger_Factory dozeLoggerProvider;
    private Provider<DozeParameters> dozeParametersProvider;
    private Provider<DozeScrimController> dozeScrimControllerProvider;
    private Provider<DozeServiceHost> dozeServiceHostProvider;
    private DozeService_Factory dozeServiceProvider;
    private Provider<DumpManager> dumpManagerProvider;
    private DynamicChildBindController_Factory dynamicChildBindControllerProvider;
    private Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider;
    private Provider<EnhancedEstimatesImpl> enhancedEstimatesImplProvider;
    private Provider<ExpandableNotificationRowComponent.Builder> expandableNotificationRowComponentBuilderProvider;
    private NotificationLogger_ExpansionStateLogger_Factory expansionStateLoggerProvider;
    private Provider<ExtensionControllerImpl> extensionControllerImplProvider;
    private Provider<FalsingManagerProxy> falsingManagerProxyProvider;
    private Provider<FeatureFlags> featureFlagsProvider;
    private Provider<FlashlightControllerImpl> flashlightControllerImplProvider;
    private FlashlightTile_Factory flashlightTileProvider;
    private Provider<FloatingContentCoordinator> floatingContentCoordinatorProvider;
    private Provider<ForegroundCoordinator> foregroundCoordinatorProvider;
    private Provider<ForegroundServiceController> foregroundServiceControllerProvider;
    private Provider<ForegroundServiceDismissalFeatureController> foregroundServiceDismissalFeatureControllerProvider;
    private Provider<ForegroundServiceNotificationListener> foregroundServiceNotificationListenerProvider;
    private Provider<ForegroundServiceSectionController> foregroundServiceSectionControllerProvider;
    private Provider<FragmentService> fragmentServiceProvider;
    private Provider<GarbageMonitor> garbageMonitorProvider;
    private Provider<GlobalActionsComponent> globalActionsComponentProvider;
    private GlobalActionsDialog_Factory globalActionsDialogProvider;
    private GlobalActionsImpl_Factory globalActionsImplProvider;
    private Provider<GlobalScreenshotLegacy> globalScreenshotLegacyProvider;
    private Provider<GlobalScreenshot> globalScreenshotProvider;
    private GroupCoalescerLogger_Factory groupCoalescerLoggerProvider;
    private GroupCoalescer_Factory groupCoalescerProvider;
    private Provider<HeadsUpBindController> headsUpBindControllerProvider;
    private Provider<HeadsUpCoordinator> headsUpCoordinatorProvider;
    private Provider<HeadsUpViewBinder> headsUpViewBinderProvider;
    private HideNotifsForOtherUsersCoordinator_Factory hideNotifsForOtherUsersCoordinatorProvider;
    private Provider<HighPriorityProvider> highPriorityProvider;
    private Provider<HotspotControllerImpl> hotspotControllerImplProvider;
    private HotspotTile_Factory hotspotTileProvider;
    private IconBuilder_Factory iconBuilderProvider;
    private IconManager_Factory iconManagerProvider;
    private ImageWallpaper_Factory imageWallpaperProvider;
    private Provider<InitController> initControllerProvider;
    private Provider<InjectionInflationController> injectionInflationControllerProvider;
    private Provider<InstantAppNotifier> instantAppNotifierProvider;
    private Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private Provider<KeyguardCoordinator> keyguardCoordinatorProvider;
    private Provider<KeyguardDismissUtil> keyguardDismissUtilProvider;
    private Provider<KeyguardEnvironmentImpl> keyguardEnvironmentImplProvider;
    private Provider<KeyguardIndicationController> keyguardIndicationControllerProvider;
    private Provider<KeyguardLifecyclesDispatcher> keyguardLifecyclesDispatcherProvider;
    private Provider<KeyguardMediaPlayer> keyguardMediaPlayerProvider;
    private Provider<KeyguardSecurityModel> keyguardSecurityModelProvider;
    private KeyguardService_Factory keyguardServiceProvider;
    private Provider<KeyguardStateControllerImpl> keyguardStateControllerImplProvider;
    private Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private Provider<LatencyTester> latencyTesterProvider;
    private Provider<LeakReporter> leakReporterProvider;
    private Provider<LightBarController> lightBarControllerProvider;
    private Provider<LightsOutNotifController> lightsOutNotifControllerProvider;
    private Provider<LocationControllerImpl> locationControllerImplProvider;
    private LocationTile_Factory locationTileProvider;
    private Provider<LockscreenGestureLogger> lockscreenGestureLoggerProvider;
    private Provider<LockscreenLockIconController> lockscreenLockIconControllerProvider;
    private Provider<LockscreenWallpaper> lockscreenWallpaperProvider;
    private Provider<ManagedProfileControllerImpl> managedProfileControllerImplProvider;
    private Provider<Map<Class<?>, Provider<Activity>>> mapOfClassOfAndProviderOfActivityProvider;
    private Provider<Map<Class<?>, Provider<BroadcastReceiver>>> mapOfClassOfAndProviderOfBroadcastReceiverProvider;
    private Provider<Map<Class<?>, Provider<RecentsImplementation>>> mapOfClassOfAndProviderOfRecentsImplementationProvider;
    private Provider<Map<Class<?>, Provider<Service>>> mapOfClassOfAndProviderOfServiceProvider;
    private Provider<Map<Class<?>, Provider<SystemUI>>> mapOfClassOfAndProviderOfSystemUIProvider;
    private Provider<MediaArtworkProcessor> mediaArtworkProcessorProvider;
    private MediaControllerFactory_Factory mediaControllerFactoryProvider;
    private GarbageMonitor_MemoryTile_Factory memoryTileProvider;
    private Provider<NavigationModeController> navigationModeControllerProvider;
    private Provider<NetworkControllerImpl> networkControllerImplProvider;
    private Provider<BubbleController> newBubbleControllerProvider;
    private Provider<KeyguardViewMediator> newKeyguardViewMediatorProvider;
    private Provider<NextAlarmControllerImpl> nextAlarmControllerImplProvider;
    private NfcTile_Factory nfcTileProvider;
    private NightDisplayTile_Factory nightDisplayTileProvider;
    private NotifBindPipelineInitializer_Factory notifBindPipelineInitializerProvider;
    private NotifBindPipelineLogger_Factory notifBindPipelineLoggerProvider;
    private Provider<NotifBindPipeline> notifBindPipelineProvider;
    private NotifCollectionLogger_Factory notifCollectionLoggerProvider;
    private Provider<NotifCollection> notifCollectionProvider;
    private Provider<NotifCoordinators> notifCoordinatorsProvider;
    private Provider<NotifInflaterImpl> notifInflaterImplProvider;
    private Provider<NotifInflationErrorManager> notifInflationErrorManagerProvider;
    private Provider<NotifPipelineInitializer> notifPipelineInitializerProvider;
    private Provider<NotifPipeline> notifPipelineProvider;
    private NotifRemoteViewCacheImpl_Factory notifRemoteViewCacheImplProvider;
    private Provider<NotifViewBarn> notifViewBarnProvider;
    private Provider<NotifViewManager> notifViewManagerProvider;
    private Provider<NotificationContentInflater> notificationContentInflaterProvider;
    private NotificationEntryManagerLogger_Factory notificationEntryManagerLoggerProvider;
    private Provider<NotificationFilter> notificationFilterProvider;
    private Provider<NotificationGroupManager> notificationGroupManagerProvider;
    private Provider<NotificationInterruptStateProviderImpl> notificationInterruptStateProviderImplProvider;
    private Provider<NotificationLockscreenUserManagerImpl> notificationLockscreenUserManagerImplProvider;
    private Provider<NotificationPersonExtractorPluginBoundary> notificationPersonExtractorPluginBoundaryProvider;
    private NotificationRankingManager_Factory notificationRankingManagerProvider;
    private Provider<NotificationRoundnessManager> notificationRoundnessManagerProvider;
    private Provider<NotificationRowBinderImpl> notificationRowBinderImplProvider;
    private Provider<NotificationRowComponent.Builder> notificationRowComponentBuilderProvider;
    private NotificationSectionsFeatureManager_Factory notificationSectionsFeatureManagerProvider;
    private Provider<NotificationShadeDepthController> notificationShadeDepthControllerProvider;
    private Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private Provider<NotificationWakeUpCoordinator> notificationWakeUpCoordinatorProvider;
    private Provider<NotificationsControllerImpl> notificationsControllerImplProvider;
    private NotificationsControllerStub_Factory notificationsControllerStubProvider;
    private Provider<Optional<ControlsFavoritePersistenceWrapper>> optionalOfControlsFavoritePersistenceWrapperProvider;
    private Provider<Optional<Divider>> optionalOfDividerProvider;
    private Provider<Optional<Lazy<Recents>>> optionalOfLazyOfRecentsProvider;
    private Provider<Optional<Lazy<StatusBar>>> optionalOfLazyOfStatusBarProvider;
    private Provider<Optional<Recents>> optionalOfRecentsProvider;
    private Provider<Optional<StatusBar>> optionalOfStatusBarProvider;
    private Provider<OverviewProxyRecentsImpl> overviewProxyRecentsImplProvider;
    private Provider<OverviewProxyService> overviewProxyServiceProvider;
    private Provider<PeopleHubDataSourceImpl> peopleHubDataSourceImplProvider;
    private Provider<PeopleHubViewAdapterImpl> peopleHubViewAdapterImplProvider;
    private Provider<PeopleHubViewModelFactoryDataSourceImpl> peopleHubViewModelFactoryDataSourceImplProvider;
    private Provider<PeopleNotificationIdentifierImpl> peopleNotificationIdentifierImplProvider;
    private Provider<PhoneStateMonitor> phoneStateMonitorProvider;
    private PhoneStatusBarPolicy_Factory phoneStatusBarPolicyProvider;
    private PipBoundsHandler_Factory pipBoundsHandlerProvider;
    private Provider<PipManager> pipManagerProvider;
    private PipSnapAlgorithm_Factory pipSnapAlgorithmProvider;
    private Provider<PipSurfaceTransactionHelper> pipSurfaceTransactionHelperProvider;
    private Provider<PipUI> pipUIProvider;
    private Provider<PluginDependencyProvider> pluginDependencyProvider;
    private Provider<PowerNotificationWarnings> powerNotificationWarningsProvider;
    private Provider<PowerUI> powerUIProvider;
    private PreparationCoordinatorLogger_Factory preparationCoordinatorLoggerProvider;
    private Provider<PreparationCoordinator> preparationCoordinatorProvider;
    private Provider<ProtoTracer> protoTracerProvider;
    private Provider<AccessibilityManager> provideAccessibilityManagerProvider;
    private Provider<ActivityManager> provideActivityManagerProvider;
    private Provider<ActivityManagerWrapper> provideActivityManagerWrapperProvider;
    private Provider<AlarmManager> provideAlarmManagerProvider;
    private Provider<Boolean> provideAllowNotificationLongPressProvider;
    private DependencyProvider_ProvideAlwaysOnDisplayPolicyFactory provideAlwaysOnDisplayPolicyProvider;
    private DependencyProvider_ProvideAmbientDisplayConfigurationFactory provideAmbientDisplayConfigurationProvider;
    private Provider provideAssistHandleBehaviorControllerMapProvider;
    private AssistModule_ProvideAssistHandleViewControllerFactory provideAssistHandleViewControllerProvider;
    private Provider<AssistUtils> provideAssistUtilsProvider;
    private Provider<AudioManager> provideAudioManagerProvider;
    private Provider<AutoHideController> provideAutoHideControllerProvider;
    private Provider<DelayableExecutor> provideBackgroundDelayableExecutorProvider;
    private Provider<Executor> provideBackgroundExecutorProvider;
    private Provider<Handler> provideBackgroundHandlerProvider;
    private ConcurrencyModule_ProvideBgHandlerFactory provideBgHandlerProvider;
    private Provider<Looper> provideBgLooperProvider;
    private Provider<CommandQueue> provideCommandQueueProvider;
    private Provider<CommonNotifCollection> provideCommonNotifCollectionProvider;
    private Provider<ConfigurationController> provideConfigurationControllerProvider;
    private Provider<ConnectivityManager> provideConnectivityManagagerProvider;
    private Provider<ContentResolver> provideContentResolverProvider;
    private SystemUIFactory_ContextHolder_ProvideContextFactory provideContextProvider;
    private Provider<DataSaverController> provideDataSaverControllerProvider;
    private Provider<DevicePolicyManager> provideDevicePolicyManagerProvider;
    private Provider<DevicePolicyManagerWrapper> provideDevicePolicyManagerWrapperProvider;
    private SystemServicesModule_ProvideDisplayIdFactory provideDisplayIdProvider;
    private Provider<DisplayMetrics> provideDisplayMetricsProvider;
    private Provider<Divider> provideDividerProvider;
    private Provider<LogBuffer> provideDozeLogBufferProvider;
    private Provider<Executor> provideExecutorProvider;
    private DependencyProvider_ProvideHandlerFactory provideHandlerProvider;
    private Provider<HeadsUpManagerPhone> provideHeadsUpManagerPhoneProvider;
    private Provider<IActivityManager> provideIActivityManagerProvider;
    private Provider<IBatteryStats> provideIBatteryStatsProvider;
    private Provider<IDreamManager> provideIDreamManagerProvider;
    private Provider<INotificationManager> provideINotificationManagerProvider;
    private Provider<IPackageManager> provideIPackageManagerProvider;
    private Provider<IStatusBarService> provideIStatusBarServiceProvider;
    private Provider<IWindowManager> provideIWindowManagerProvider;
    private Provider<KeyguardLiftController> provideKeyguardLiftControllerProvider;
    private Provider<KeyguardManager> provideKeyguardManagerProvider;
    private Provider<LatencyTracker> provideLatencyTrackerProvider;
    private Provider<LauncherApps> provideLauncherAppsProvider;
    private Provider<LeakDetector> provideLeakDetectorProvider;
    private Provider<String> provideLeakReportEmailProvider;
    private Provider<LocalBluetoothManager> provideLocalBluetoothControllerProvider;
    private DependencyProvider_ProvideLockPatternUtilsFactory provideLockPatternUtilsProvider;
    private Provider<LogcatEchoTracker> provideLogcatEchoTrackerProvider;
    private Provider<DelayableExecutor> provideMainDelayableExecutorProvider;
    private ConcurrencyModule_ProvideMainExecutorFactory provideMainExecutorProvider;
    private ConcurrencyModule_ProvideMainHandlerFactory provideMainHandlerProvider;
    private Provider<MetricsLogger> provideMetricsLoggerProvider;
    private Provider<NavigationBarController> provideNavigationBarControllerProvider;
    private Provider<NetworkScoreManager> provideNetworkScoreManagerProvider;
    private Provider<NightDisplayListener> provideNightDisplayListenerProvider;
    private Provider<NotifRemoteViewCache> provideNotifRemoteViewCacheProvider;
    private Provider<NotificationAlertingManager> provideNotificationAlertingManagerProvider;
    private Provider<NotificationBlockingHelperManager> provideNotificationBlockingHelperManagerProvider;
    private Provider<NotificationEntryManager> provideNotificationEntryManagerProvider;
    private Provider<NotificationGroupAlertTransferHelper> provideNotificationGroupAlertTransferHelperProvider;
    private Provider<NotificationGutsManager> provideNotificationGutsManagerProvider;
    private Provider<NotificationListener> provideNotificationListenerProvider;
    private Provider<NotificationLogger> provideNotificationLoggerProvider;
    private Provider<NotificationManager> provideNotificationManagerProvider;
    private Provider<NotificationMediaManager> provideNotificationMediaManagerProvider;
    private DependencyProvider_ProvideNotificationMessagingUtilFactory provideNotificationMessagingUtilProvider;
    private Provider<NotificationPanelLogger> provideNotificationPanelLoggerProvider;
    private Provider<NotificationRemoteInputManager> provideNotificationRemoteInputManagerProvider;
    private Provider<NotificationViewHierarchyManager> provideNotificationViewHierarchyManagerProvider;
    private Provider<NotificationsController> provideNotificationsControllerProvider;
    private Provider<LogBuffer> provideNotificationsLogBufferProvider;
    private Provider<PackageManager> providePackageManagerProvider;
    private Provider<PackageManagerWrapper> providePackageManagerWrapperProvider;
    private Provider<PluginManager> providePluginManagerProvider;
    private Provider<PowerManager> providePowerManagerProvider;
    private Provider<LogBuffer> provideQuickSettingsLogBufferProvider;
    private RecentsModule_ProvideRecentsImplFactory provideRecentsImplProvider;
    private Provider<Recents> provideRecentsProvider;
    private SystemServicesModule_ProvideResourcesFactory provideResourcesProvider;
    private Provider<SensorPrivacyManager> provideSensorPrivacyManagerProvider;
    private DependencyProvider_ProvideSharePreferencesFactory provideSharePreferencesProvider;
    private Provider<ShortcutManager> provideShortcutManagerProvider;
    private Provider<SmartReplyController> provideSmartReplyControllerProvider;
    private Provider<StatusBar> provideStatusBarProvider;
    private Provider<SysUiState> provideSysUiStateProvider;
    private Provider<Clock> provideSystemClockProvider;
    private Provider<TelecomManager> provideTelecomManagerProvider;
    private Provider<TelephonyManager> provideTelephonyManagerProvider;
    private Provider<Handler> provideTimeTickHandlerProvider;
    private Provider<TrustManager> provideTrustManagerProvider;
    private Provider<Executor> provideUiBackgroundExecutorProvider;
    private Provider<UiEventLogger> provideUiEventLoggerProvider;
    private Provider<UserManager> provideUserManagerProvider;
    private Provider<Vibrator> provideVibratorProvider;
    private Provider<VisualStabilityManager> provideVisualStabilityManagerProvider;
    private SystemServicesModule_ProvideWallpaperManagerFactory provideWallpaperManagerProvider;
    private Provider<WifiManager> provideWifiManagerProvider;
    private Provider<WindowManager> provideWindowManagerProvider;
    private Provider<LayoutInflater> providerLayoutInflaterProvider;
    private Provider<Choreographer> providesChoreographerProvider;
    private DependencyProvider_ProvidesViewMediatorCallbackFactory providesViewMediatorCallbackProvider;
    private ProximitySensor_Factory proximitySensorProvider;
    private Provider<PulseExpansionHandler> pulseExpansionHandlerProvider;
    private Provider<QSFactoryImpl> qSFactoryImplProvider;
    private QSLogger_Factory qSLoggerProvider;
    private Provider<QSTileHost> qSTileHostProvider;
    private Provider<RankingCoordinator> rankingCoordinatorProvider;
    private Provider<RecordingController> recordingControllerProvider;
    private RecordingService_Factory recordingServiceProvider;
    private Provider<RemoteInputQuickSettingsDisabler> remoteInputQuickSettingsDisablerProvider;
    private Provider<RemoteInputUriController> remoteInputUriControllerProvider;
    private Provider<RotationLockControllerImpl> rotationLockControllerImplProvider;
    private RotationLockTile_Factory rotationLockTileProvider;
    private RowContentBindStageLogger_Factory rowContentBindStageLoggerProvider;
    private Provider<RowContentBindStage> rowContentBindStageProvider;
    private Provider<ScreenDecorations> screenDecorationsProvider;
    private Provider<ScreenLifecycle> screenLifecycleProvider;
    private ScreenPinningRequest_Factory screenPinningRequestProvider;
    private ScreenRecordDialog_Factory screenRecordDialogProvider;
    private ScreenRecordTile_Factory screenRecordTileProvider;
    private ScreenshotNotificationsController_Factory screenshotNotificationsControllerProvider;
    private Provider<ScrimController> scrimControllerProvider;
    private Provider<SecurityControllerImpl> securityControllerImplProvider;
    private Provider<SensorPrivacyControllerImpl> sensorPrivacyControllerImplProvider;
    private Provider<GarbageMonitor.Service> serviceProvider;
    private Provider<ShadeControllerImpl> shadeControllerImplProvider;
    private ShadeListBuilderLogger_Factory shadeListBuilderLoggerProvider;
    private Provider<ShadeListBuilder> shadeListBuilderProvider;
    private Provider<ShortcutKeyDispatcher> shortcutKeyDispatcherProvider;
    private Provider<SizeCompatModeActivityController> sizeCompatModeActivityControllerProvider;
    private Provider<SliceBroadcastRelayHandler> sliceBroadcastRelayHandlerProvider;
    private Provider<SmartReplyConstants> smartReplyConstantsProvider;
    private Provider<StatusBarComponent.Builder> statusBarComponentBuilderProvider;
    private Provider<StatusBarIconControllerImpl> statusBarIconControllerImplProvider;
    private Provider<StatusBarKeyguardViewManager> statusBarKeyguardViewManagerProvider;
    private Provider<StatusBarRemoteInputCallback> statusBarRemoteInputCallbackProvider;
    private Provider<StatusBarStateControllerImpl> statusBarStateControllerImplProvider;
    private Provider<StatusBarTouchableRegionManager> statusBarTouchableRegionManagerProvider;
    private Provider<StatusBarWindowController> statusBarWindowControllerProvider;
    private Provider<SuperStatusBarViewFactory> superStatusBarViewFactoryProvider;
    private Provider<SystemActions> systemActionsProvider;
    private SystemUIAuxiliaryDumpService_Factory systemUIAuxiliaryDumpServiceProvider;
    private Provider<SystemUIRootComponent> systemUIRootComponentProvider;
    private SystemUIService_Factory systemUIServiceProvider;
    private Provider<SystemWindows> systemWindowsProvider;
    private Provider<SysuiColorExtractor> sysuiColorExtractorProvider;
    private TakeScreenshotService_Factory takeScreenshotServiceProvider;
    private Provider<ThemeOverlayController> themeOverlayControllerProvider;
    private Provider<ToastUI> toastUIProvider;
    private Provider<TransactionPool> transactionPoolProvider;
    private Provider<TunablePadding.TunablePaddingService> tunablePaddingServiceProvider;
    private Provider<TunerServiceImpl> tunerServiceImplProvider;
    private Provider<TvStatusBar> tvStatusBarProvider;
    private UiModeNightTile_Factory uiModeNightTileProvider;
    private Provider<UiOffloadThread> uiOffloadThreadProvider;
    private Provider<UserInfoControllerImpl> userInfoControllerImplProvider;
    private Provider<UserSwitcherController> userSwitcherControllerProvider;
    private UserTile_Factory userTileProvider;
    private Provider<VibratorHelper> vibratorHelperProvider;
    private Provider<VolumeDialogComponent> volumeDialogComponentProvider;
    private Provider<VolumeDialogControllerImpl> volumeDialogControllerImplProvider;
    private Provider<VolumeUI> volumeUIProvider;
    private Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;
    private WifiTile_Factory wifiTileProvider;
    private Provider<WindowMagnification> windowMagnificationProvider;
    private WorkLockActivity_Factory workLockActivityProvider;
    private WorkModeTile_Factory workModeTileProvider;
    private Provider<ZenModeControllerImpl> zenModeControllerImplProvider;
    
    static {
        ABSENT_JDK_OPTIONAL_PROVIDER = InstanceFactory.create(Optional.empty());
    }
    
    private DaggerSystemUIRootComponent(final Builder builder) {
        this.initialize(builder);
        this.initialize2(builder);
        this.initialize3(builder);
        this.initialize4(builder);
    }
    
    private static <T> Provider<Optional<T>> absentJdkOptionalProvider() {
        return (Provider<Optional<T>>)DaggerSystemUIRootComponent.ABSENT_JDK_OPTIONAL_PROVIDER;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    private Handler getBackgroundHandler() {
        return ConcurrencyModule_ProvideBgHandlerFactory.proxyProvideBgHandler(this.provideBgLooperProvider.get());
    }
    
    private Executor getMainExecutor() {
        return ConcurrencyModule_ProvideMainExecutorFactory.proxyProvideMainExecutor(SystemUIFactory_ContextHolder_ProvideContextFactory.proxyProvideContext(this.contextHolder));
    }
    
    private Handler getMainHandler() {
        return ConcurrencyModule_ProvideMainHandlerFactory.proxyProvideMainHandler(ConcurrencyModule_ProvideMainLooperFactory.proxyProvideMainLooper());
    }
    
    private Resources getMainResources() {
        return SystemServicesModule_ProvideResourcesFactory.proxyProvideResources(SystemUIFactory_ContextHolder_ProvideContextFactory.proxyProvideContext(this.contextHolder));
    }
    
    private NotificationSectionsFeatureManager getNotificationSectionsFeatureManager() {
        return new NotificationSectionsFeatureManager(new DeviceConfigProxy(), SystemUIFactory_ContextHolder_ProvideContextFactory.proxyProvideContext(this.contextHolder));
    }
    
    private QSLogger getQSLogger() {
        return new QSLogger(this.provideQuickSettingsLogBufferProvider.get());
    }
    
    private void initialize(final Builder builder) {
        final SystemUIFactory_ContextHolder_ProvideContextFactory create = SystemUIFactory_ContextHolder_ProvideContextFactory.create(builder.contextHolder);
        this.provideContextProvider = create;
        final Provider<Object> provider = DoubleCheck.provider(DumpManager_Factory.create(create));
        this.dumpManagerProvider = (Provider<DumpManager>)provider;
        this.bootCompleteCacheImplProvider = (Provider<BootCompleteCacheImpl>)DoubleCheck.provider(BootCompleteCacheImpl_Factory.create((Provider<DumpManager>)provider));
        this.provideConfigurationControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideConfigurationControllerFactory.create(builder.dependencyProvider, this.provideContextProvider));
        this.provideMainHandlerProvider = ConcurrencyModule_ProvideMainHandlerFactory.create(ConcurrencyModule_ProvideMainLooperFactory.create());
        final Provider<Looper> provider2 = DoubleCheck.provider(ConcurrencyModule_ProvideBgLooperFactory.create());
        this.provideBgLooperProvider = provider2;
        final Provider<Object> provider3 = DoubleCheck.provider(BroadcastDispatcher_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, provider2, this.dumpManagerProvider));
        this.broadcastDispatcherProvider = (Provider<BroadcastDispatcher>)provider3;
        this.workLockActivityProvider = WorkLockActivity_Factory.create((Provider<BroadcastDispatcher>)provider3);
        this.brightnessDialogProvider = BrightnessDialog_Factory.create(this.broadcastDispatcherProvider);
        final Provider<RecordingController> provider4 = DoubleCheck.provider(RecordingController_Factory.create(this.provideContextProvider));
        this.recordingControllerProvider = provider4;
        this.screenRecordDialogProvider = ScreenRecordDialog_Factory.create(provider4);
        this.provideWindowManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideWindowManagerFactory.create(this.provideContextProvider));
        this.provideIActivityManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideIActivityManagerFactory.create());
        this.provideResourcesProvider = SystemServicesModule_ProvideResourcesFactory.create(this.provideContextProvider);
        this.provideAmbientDisplayConfigurationProvider = DependencyProvider_ProvideAmbientDisplayConfigurationFactory.create(builder.dependencyProvider, this.provideContextProvider);
        this.provideAlwaysOnDisplayPolicyProvider = DependencyProvider_ProvideAlwaysOnDisplayPolicyFactory.create(builder.dependencyProvider, this.provideContextProvider);
        this.providePowerManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvidePowerManagerFactory.create(this.provideContextProvider));
        final Provider<LeakDetector> provider5 = DoubleCheck.provider(DependencyProvider_ProvideLeakDetectorFactory.create(builder.dependencyProvider));
        this.provideLeakDetectorProvider = provider5;
        final Provider<Object> provider6 = DoubleCheck.provider(TunerServiceImpl_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, provider5, this.broadcastDispatcherProvider));
        this.tunerServiceImplProvider = (Provider<TunerServiceImpl>)provider6;
        this.dozeParametersProvider = (Provider<DozeParameters>)DoubleCheck.provider(DozeParameters_Factory.create(this.provideResourcesProvider, this.provideAmbientDisplayConfigurationProvider, this.provideAlwaysOnDisplayPolicyProvider, this.providePowerManagerProvider, (Provider<TunerService>)provider6));
        this.statusBarStateControllerImplProvider = DoubleCheck.provider(StatusBarStateControllerImpl_Factory.create());
        this.provideDevicePolicyManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideDevicePolicyManagerFactory.create(this.provideContextProvider));
        this.provideUserManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideUserManagerFactory.create(this.provideContextProvider));
        this.provideIStatusBarServiceProvider = DoubleCheck.provider(SystemServicesModule_ProvideIStatusBarServiceFactory.create());
        this.provideKeyguardManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideKeyguardManagerFactory.create(this.provideContextProvider));
        this.deviceProvisionedControllerImplProvider = DoubleCheck.provider(DeviceProvisionedControllerImpl_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, this.broadcastDispatcherProvider));
        this.provideBackgroundExecutorProvider = DoubleCheck.provider(ConcurrencyModule_ProvideBackgroundExecutorFactory.create(this.provideBgLooperProvider));
        this.keyguardUpdateMonitorProvider = DoubleCheck.provider(KeyguardUpdateMonitor_Factory.create(this.provideContextProvider, ConcurrencyModule_ProvideMainLooperFactory.create(), this.broadcastDispatcherProvider, this.dumpManagerProvider, this.provideBackgroundExecutorProvider));
        final DependencyProvider_ProvideLockPatternUtilsFactory create2 = DependencyProvider_ProvideLockPatternUtilsFactory.create(builder.dependencyProvider, this.provideContextProvider);
        this.provideLockPatternUtilsProvider = create2;
        final Provider<Object> provider7 = DoubleCheck.provider(KeyguardStateControllerImpl_Factory.create(this.provideContextProvider, this.keyguardUpdateMonitorProvider, create2));
        this.keyguardStateControllerImplProvider = (Provider<KeyguardStateControllerImpl>)provider7;
        final Provider<Object> provider8 = DoubleCheck.provider(NotificationLockscreenUserManagerImpl_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider, this.provideDevicePolicyManagerProvider, this.provideUserManagerProvider, this.provideIStatusBarServiceProvider, this.provideKeyguardManagerProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.provideMainHandlerProvider, (Provider<DeviceProvisionedController>)this.deviceProvisionedControllerImplProvider, (Provider<KeyguardStateController>)provider7));
        this.notificationLockscreenUserManagerImplProvider = (Provider<NotificationLockscreenUserManagerImpl>)provider8;
        this.keyguardBypassControllerProvider = (Provider<KeyguardBypassController>)DoubleCheck.provider(KeyguardBypassController_Factory.create(this.provideContextProvider, (Provider<TunerService>)this.tunerServiceImplProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, (Provider<NotificationLockscreenUserManager>)provider8, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, this.dumpManagerProvider));
        final Provider<SysuiColorExtractor> provider9 = DoubleCheck.provider(SysuiColorExtractor_Factory.create(this.provideContextProvider, this.provideConfigurationControllerProvider));
        this.sysuiColorExtractorProvider = provider9;
        this.notificationShadeWindowControllerProvider = (Provider<NotificationShadeWindowController>)DoubleCheck.provider(NotificationShadeWindowController_Factory.create(this.provideContextProvider, this.provideWindowManagerProvider, this.provideIActivityManagerProvider, this.dozeParametersProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.provideConfigurationControllerProvider, this.keyguardBypassControllerProvider, provider9, this.dumpManagerProvider));
        final Provider<ProtoTracer> provider10 = DoubleCheck.provider(ProtoTracer_Factory.create(this.provideContextProvider, this.dumpManagerProvider));
        this.protoTracerProvider = provider10;
        this.provideCommandQueueProvider = (Provider<CommandQueue>)DoubleCheck.provider(StatusBarDependenciesModule_ProvideCommandQueueFactory.create(this.provideContextProvider, provider10));
        this.providePluginManagerProvider = DoubleCheck.provider(DependencyProvider_ProvidePluginManagerFactory.create(builder.dependencyProvider, this.provideContextProvider));
        this.provideMainExecutorProvider = ConcurrencyModule_ProvideMainExecutorFactory.create(this.provideContextProvider);
        this.provideDisplayMetricsProvider = DoubleCheck.provider(DependencyProvider_ProvideDisplayMetricsFactory.create(builder.dependencyProvider, this.provideContextProvider, this.provideWindowManagerProvider));
        final Provider<AsyncSensorManager> provider11 = DoubleCheck.provider(AsyncSensorManager_Factory.create(this.provideContextProvider, this.providePluginManagerProvider));
        this.asyncSensorManagerProvider = provider11;
        this.proximitySensorProvider = ProximitySensor_Factory.create(this.provideResourcesProvider, provider11);
        this.dockManagerImplProvider = DoubleCheck.provider(DockManagerImpl_Factory.create());
        this.provideUiBackgroundExecutorProvider = DoubleCheck.provider(ConcurrencyModule_ProvideUiBackgroundExecutorFactory.create());
        this.falsingManagerProxyProvider = DoubleCheck.provider(FalsingManagerProxy_Factory.create(this.provideContextProvider, this.providePluginManagerProvider, this.provideMainExecutorProvider, this.provideDisplayMetricsProvider, this.proximitySensorProvider, DeviceConfigProxy_Factory.create(), (Provider<DockManager>)this.dockManagerImplProvider, this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, this.provideUiBackgroundExecutorProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider));
        this.statusBarKeyguardViewManagerProvider = new DelegateFactory<StatusBarKeyguardViewManager>();
        this.dismissCallbackRegistryProvider = DoubleCheck.provider(DismissCallbackRegistry_Factory.create(this.provideUiBackgroundExecutorProvider));
        this.provideTrustManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideTrustManagerFactory.create(this.provideContextProvider));
        this.navigationModeControllerProvider = DoubleCheck.provider(NavigationModeController_Factory.create(this.provideContextProvider, (Provider<DeviceProvisionedController>)this.deviceProvisionedControllerImplProvider, this.provideUiBackgroundExecutorProvider));
        this.newKeyguardViewMediatorProvider = DoubleCheck.provider(KeyguardModule_NewKeyguardViewMediatorFactory.create(this.provideContextProvider, (Provider<FalsingManager>)this.falsingManagerProxyProvider, this.provideLockPatternUtilsProvider, this.broadcastDispatcherProvider, this.notificationShadeWindowControllerProvider, (Provider<KeyguardViewController>)this.statusBarKeyguardViewManagerProvider, this.dismissCallbackRegistryProvider, this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, this.providePowerManagerProvider, this.provideTrustManagerProvider, this.provideUiBackgroundExecutorProvider, DeviceConfigProxy_Factory.create(), this.navigationModeControllerProvider));
        this.providesViewMediatorCallbackProvider = DependencyProvider_ProvidesViewMediatorCallbackFactory.create(builder.dependencyProvider, this.newKeyguardViewMediatorProvider);
        this.featureFlagsProvider = DoubleCheck.provider(FeatureFlags_Factory.create(this.provideBackgroundExecutorProvider));
        final Provider<NotificationManager> provider12 = DoubleCheck.provider(SystemServicesModule_ProvideNotificationManagerFactory.create(this.provideContextProvider));
        this.provideNotificationManagerProvider = provider12;
        this.provideNotificationListenerProvider = (Provider<NotificationListener>)DoubleCheck.provider(StatusBarDependenciesModule_ProvideNotificationListenerFactory.create(this.provideContextProvider, provider12, this.provideMainHandlerProvider));
        final Provider<ContentResolver> provider13 = DoubleCheck.provider(SystemServicesModule_ProvideContentResolverFactory.create(this.provideContextProvider));
        this.provideContentResolverProvider = provider13;
        final Provider<Object> provider14 = DoubleCheck.provider(LogModule_ProvideLogcatEchoTrackerFactory.create(provider13, ConcurrencyModule_ProvideMainLooperFactory.create()));
        this.provideLogcatEchoTrackerProvider = (Provider<LogcatEchoTracker>)provider14;
        final Provider<Object> provider15 = DoubleCheck.provider(LogModule_ProvideNotificationsLogBufferFactory.create((Provider<LogcatEchoTracker>)provider14, this.dumpManagerProvider));
        this.provideNotificationsLogBufferProvider = (Provider<LogBuffer>)provider15;
        this.notificationEntryManagerLoggerProvider = NotificationEntryManagerLogger_Factory.create((Provider<LogBuffer>)provider15);
        this.notificationGroupManagerProvider = DoubleCheck.provider(NotificationGroupManager_Factory.create((Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider));
        this.provideNotificationMediaManagerProvider = new DelegateFactory<NotificationMediaManager>();
        this.provideHeadsUpManagerPhoneProvider = DoubleCheck.provider(SystemUIDefaultModule_ProvideHeadsUpManagerPhoneFactory.create(this.provideContextProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.keyguardBypassControllerProvider, this.notificationGroupManagerProvider, this.provideConfigurationControllerProvider));
        this.notificationFilterProvider = DoubleCheck.provider(NotificationFilter_Factory.create((Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider));
        this.notificationSectionsFeatureManagerProvider = NotificationSectionsFeatureManager_Factory.create(DeviceConfigProxy_Factory.create(), this.provideContextProvider);
        final Provider<ExtensionController> provider16 = DoubleCheck.provider(ExtensionControllerImpl_Factory.create(this.provideContextProvider, this.provideLeakDetectorProvider, this.providePluginManagerProvider, (Provider<TunerService>)this.tunerServiceImplProvider, this.provideConfigurationControllerProvider));
        this.extensionControllerImplProvider = (Provider<ExtensionControllerImpl>)provider16;
        final Provider<Object> provider17 = DoubleCheck.provider(NotificationPersonExtractorPluginBoundary_Factory.create(provider16));
        this.notificationPersonExtractorPluginBoundaryProvider = (Provider<NotificationPersonExtractorPluginBoundary>)provider17;
        final Provider<Object> provider18 = DoubleCheck.provider(PeopleNotificationIdentifierImpl_Factory.create((Provider<NotificationPersonExtractor>)provider17, this.notificationGroupManagerProvider));
        this.peopleNotificationIdentifierImplProvider = (Provider<PeopleNotificationIdentifierImpl>)provider18;
        final Provider<Object> provider19 = DoubleCheck.provider(HighPriorityProvider_Factory.create((Provider<PeopleNotificationIdentifier>)provider18));
        this.highPriorityProvider = (Provider<HighPriorityProvider>)provider19;
        this.notificationRankingManagerProvider = NotificationRankingManager_Factory.create(this.provideNotificationMediaManagerProvider, this.notificationGroupManagerProvider, (Provider<HeadsUpManager>)this.provideHeadsUpManagerPhoneProvider, this.notificationFilterProvider, this.notificationEntryManagerLoggerProvider, this.notificationSectionsFeatureManagerProvider, (Provider<PeopleNotificationIdentifier>)this.peopleNotificationIdentifierImplProvider, (Provider<HighPriorityProvider>)provider19);
        this.keyguardEnvironmentImplProvider = DoubleCheck.provider(KeyguardEnvironmentImpl_Factory.create());
        this.provideNotificationMessagingUtilProvider = DependencyProvider_ProvideNotificationMessagingUtilFactory.create(builder.dependencyProvider, this.provideContextProvider);
        final DelegateFactory<NotificationEntryManager> provideNotificationEntryManagerProvider = new DelegateFactory<NotificationEntryManager>();
        this.provideNotificationEntryManagerProvider = provideNotificationEntryManagerProvider;
        this.provideSmartReplyControllerProvider = (Provider<SmartReplyController>)DoubleCheck.provider(StatusBarDependenciesModule_ProvideSmartReplyControllerFactory.create(provideNotificationEntryManagerProvider, this.provideIStatusBarServiceProvider));
        this.provideStatusBarProvider = new DelegateFactory<StatusBar>();
        this.provideHandlerProvider = DependencyProvider_ProvideHandlerFactory.create(builder.dependencyProvider);
        final Provider<RemoteInputUriController> provider20 = DoubleCheck.provider(RemoteInputUriController_Factory.create(this.provideIStatusBarServiceProvider));
        this.remoteInputUriControllerProvider = provider20;
        this.provideNotificationRemoteInputManagerProvider = (Provider<NotificationRemoteInputManager>)DoubleCheck.provider(StatusBarDependenciesModule_ProvideNotificationRemoteInputManagerFactory.create(this.provideContextProvider, (Provider<NotificationLockscreenUserManager>)this.notificationLockscreenUserManagerImplProvider, this.provideSmartReplyControllerProvider, this.provideNotificationEntryManagerProvider, this.provideStatusBarProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.provideHandlerProvider, provider20));
        final NotifCollectionLogger_Factory create3 = NotifCollectionLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.notifCollectionLoggerProvider = create3;
        this.notifCollectionProvider = (Provider<NotifCollection>)DoubleCheck.provider(NotifCollection_Factory.create(this.provideIStatusBarServiceProvider, this.dumpManagerProvider, this.featureFlagsProvider, create3));
        this.bindSystemClockProvider = DoubleCheck.provider(SystemClockImpl_Factory.create());
        final ShadeListBuilderLogger_Factory create4 = ShadeListBuilderLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.shadeListBuilderLoggerProvider = create4;
        final Provider<Object> provider21 = DoubleCheck.provider(ShadeListBuilder_Factory.create(this.bindSystemClockProvider, create4, this.dumpManagerProvider));
        this.shadeListBuilderProvider = (Provider<ShadeListBuilder>)provider21;
        final Provider<Object> provider22 = DoubleCheck.provider(NotifPipeline_Factory.create(this.notifCollectionProvider, (Provider<ShadeListBuilder>)provider21));
        this.notifPipelineProvider = (Provider<NotifPipeline>)provider22;
        this.provideCommonNotifCollectionProvider = (Provider<CommonNotifCollection>)DoubleCheck.provider(NotificationsModule_ProvideCommonNotifCollectionFactory.create(this.featureFlagsProvider, (Provider<NotifPipeline>)provider22, this.provideNotificationEntryManagerProvider));
        final NotifBindPipelineLogger_Factory create5 = NotifBindPipelineLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.notifBindPipelineLoggerProvider = create5;
        this.notifBindPipelineProvider = (Provider<NotifBindPipeline>)DoubleCheck.provider(NotifBindPipeline_Factory.create(this.provideCommonNotifCollectionProvider, create5, ConcurrencyModule_ProvideMainLooperFactory.create()));
        final NotifRemoteViewCacheImpl_Factory create6 = NotifRemoteViewCacheImpl_Factory.create(this.provideCommonNotifCollectionProvider);
        this.notifRemoteViewCacheImplProvider = create6;
        this.provideNotifRemoteViewCacheProvider = (Provider<NotifRemoteViewCache>)DoubleCheck.provider(create6);
        this.smartReplyConstantsProvider = DoubleCheck.provider(SmartReplyConstants_Factory.create(this.provideMainHandlerProvider, this.provideContextProvider, DeviceConfigProxy_Factory.create()));
        this.provideLauncherAppsProvider = DoubleCheck.provider(SystemServicesModule_ProvideLauncherAppsFactory.create(this.provideContextProvider));
        final Provider<ConversationNotificationManager> provider23 = DoubleCheck.provider(ConversationNotificationManager_Factory.create(this.provideNotificationEntryManagerProvider, this.notificationGroupManagerProvider, this.provideContextProvider));
        this.conversationNotificationManagerProvider = provider23;
        final ConversationNotificationProcessor_Factory create7 = ConversationNotificationProcessor_Factory.create(this.provideLauncherAppsProvider, provider23);
        this.conversationNotificationProcessorProvider = create7;
        this.notificationContentInflaterProvider = (Provider<NotificationContentInflater>)DoubleCheck.provider(NotificationContentInflater_Factory.create(this.provideNotifRemoteViewCacheProvider, this.provideNotificationRemoteInputManagerProvider, this.smartReplyConstantsProvider, this.provideSmartReplyControllerProvider, create7, this.provideBackgroundExecutorProvider));
        this.notifInflationErrorManagerProvider = DoubleCheck.provider(NotifInflationErrorManager_Factory.create());
        final RowContentBindStageLogger_Factory create8 = RowContentBindStageLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.rowContentBindStageLoggerProvider = create8;
        this.rowContentBindStageProvider = (Provider<RowContentBindStage>)DoubleCheck.provider(RowContentBindStage_Factory.create((Provider<NotificationRowContentBinder>)this.notificationContentInflaterProvider, this.notifInflationErrorManagerProvider, create8));
        this.provideIDreamManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideIDreamManagerFactory.create());
        this.enhancedEstimatesImplProvider = DoubleCheck.provider(EnhancedEstimatesImpl_Factory.create());
        final ConcurrencyModule_ProvideBgHandlerFactory create9 = ConcurrencyModule_ProvideBgHandlerFactory.create(this.provideBgLooperProvider);
        this.provideBgHandlerProvider = create9;
        final Provider<Object> provider24 = DoubleCheck.provider(BatteryControllerImpl_Factory.create(this.provideContextProvider, (Provider<EnhancedEstimates>)this.enhancedEstimatesImplProvider, this.providePowerManagerProvider, this.broadcastDispatcherProvider, this.provideMainHandlerProvider, create9));
        this.batteryControllerImplProvider = (Provider<BatteryControllerImpl>)provider24;
        this.notificationInterruptStateProviderImplProvider = (Provider<NotificationInterruptStateProviderImpl>)DoubleCheck.provider(NotificationInterruptStateProviderImpl_Factory.create(this.provideContentResolverProvider, this.providePowerManagerProvider, this.provideIDreamManagerProvider, this.provideAmbientDisplayConfigurationProvider, this.notificationFilterProvider, (Provider<BatteryController>)provider24, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, (Provider<HeadsUpManager>)this.provideHeadsUpManagerPhoneProvider, this.provideMainHandlerProvider));
        this.expandableNotificationRowComponentBuilderProvider = new Provider<ExpandableNotificationRowComponent.Builder>() {
            @Override
            public ExpandableNotificationRowComponent.Builder get() {
                return new ExpandableNotificationRowComponentBuilder();
            }
        };
    }
    
    private void initialize2(final Builder builder) {
        final IconBuilder_Factory create = IconBuilder_Factory.create(this.provideContextProvider);
        this.iconBuilderProvider = create;
        this.iconManagerProvider = IconManager_Factory.create(this.provideCommonNotifCollectionProvider, this.provideLauncherAppsProvider, create);
        this.notificationRowBinderImplProvider = DoubleCheck.provider(NotificationRowBinderImpl_Factory.create(this.provideContextProvider, this.provideNotificationMessagingUtilProvider, this.provideNotificationRemoteInputManagerProvider, (Provider<NotificationLockscreenUserManager>)this.notificationLockscreenUserManagerImplProvider, this.notifBindPipelineProvider, this.rowContentBindStageProvider, (Provider<NotificationInterruptStateProvider>)this.notificationInterruptStateProviderImplProvider, RowInflaterTask_Factory.create(), this.expandableNotificationRowComponentBuilderProvider, this.iconManagerProvider));
        final Provider<ForegroundServiceDismissalFeatureController> provider = DoubleCheck.provider(ForegroundServiceDismissalFeatureController_Factory.create(DeviceConfigProxy_Factory.create(), this.provideContextProvider));
        this.foregroundServiceDismissalFeatureControllerProvider = provider;
        ((DelegateFactory)this.provideNotificationEntryManagerProvider).setDelegatedProvider(this.provideNotificationEntryManagerProvider = (Provider<NotificationEntryManager>)DoubleCheck.provider(NotificationsModule_ProvideNotificationEntryManagerFactory.create(this.notificationEntryManagerLoggerProvider, this.notificationGroupManagerProvider, this.notificationRankingManagerProvider, (Provider<NotificationEntryManager.KeyguardEnvironment>)this.keyguardEnvironmentImplProvider, this.featureFlagsProvider, (Provider<NotificationRowBinder>)this.notificationRowBinderImplProvider, this.provideNotificationRemoteInputManagerProvider, this.provideLeakDetectorProvider, provider)));
        this.provideMainDelayableExecutorProvider = DoubleCheck.provider(ConcurrencyModule_ProvideMainDelayableExecutorFactory.create(ConcurrencyModule_ProvideMainLooperFactory.create()));
        final GroupCoalescerLogger_Factory create2 = GroupCoalescerLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.groupCoalescerLoggerProvider = create2;
        this.groupCoalescerProvider = GroupCoalescer_Factory.create(this.provideMainDelayableExecutorProvider, this.bindSystemClockProvider, create2);
        this.hideNotifsForOtherUsersCoordinatorProvider = HideNotifsForOtherUsersCoordinator_Factory.create((Provider<NotificationLockscreenUserManager>)this.notificationLockscreenUserManagerImplProvider);
        this.keyguardCoordinatorProvider = DoubleCheck.provider(KeyguardCoordinator_Factory.create(this.provideContextProvider, this.provideHandlerProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, (Provider<NotificationLockscreenUserManager>)this.notificationLockscreenUserManagerImplProvider, this.broadcastDispatcherProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.keyguardUpdateMonitorProvider, this.highPriorityProvider));
        this.rankingCoordinatorProvider = DoubleCheck.provider(RankingCoordinator_Factory.create((Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider));
        final Provider<AppOpsController> provider2 = DoubleCheck.provider(AppOpsControllerImpl_Factory.create(this.provideContextProvider, this.provideBgLooperProvider, this.dumpManagerProvider));
        this.appOpsControllerImplProvider = (Provider<AppOpsControllerImpl>)provider2;
        final Provider<Object> provider3 = DoubleCheck.provider(ForegroundServiceController_Factory.create(this.provideNotificationEntryManagerProvider, provider2, this.provideMainHandlerProvider));
        this.foregroundServiceControllerProvider = (Provider<ForegroundServiceController>)provider3;
        this.foregroundCoordinatorProvider = (Provider<ForegroundCoordinator>)DoubleCheck.provider(ForegroundCoordinator_Factory.create((Provider<ForegroundServiceController>)provider3, (Provider<AppOpsController>)this.appOpsControllerImplProvider, this.provideMainDelayableExecutorProvider));
        final Provider<IPackageManager> provider4 = DoubleCheck.provider(SystemServicesModule_ProvideIPackageManagerFactory.create());
        this.provideIPackageManagerProvider = provider4;
        this.deviceProvisionedCoordinatorProvider = (Provider<DeviceProvisionedCoordinator>)DoubleCheck.provider(DeviceProvisionedCoordinator_Factory.create((Provider<DeviceProvisionedController>)this.deviceProvisionedControllerImplProvider, provider4));
        final DelegateFactory<BubbleController> newBubbleControllerProvider = new DelegateFactory<BubbleController>();
        this.newBubbleControllerProvider = newBubbleControllerProvider;
        this.bubbleCoordinatorProvider = (Provider<BubbleCoordinator>)DoubleCheck.provider(BubbleCoordinator_Factory.create(newBubbleControllerProvider, this.notifCollectionProvider));
        final Provider<HeadsUpViewBinder> provider5 = DoubleCheck.provider(HeadsUpViewBinder_Factory.create(this.provideNotificationMessagingUtilProvider, this.rowContentBindStageProvider));
        this.headsUpViewBinderProvider = provider5;
        this.headsUpCoordinatorProvider = (Provider<HeadsUpCoordinator>)DoubleCheck.provider(HeadsUpCoordinator_Factory.create((Provider<HeadsUpManager>)this.provideHeadsUpManagerPhoneProvider, provider5, (Provider<NotificationInterruptStateProvider>)this.notificationInterruptStateProviderImplProvider, this.provideNotificationRemoteInputManagerProvider));
        this.conversationCoordinatorProvider = DoubleCheck.provider(ConversationCoordinator_Factory.create());
        this.preparationCoordinatorLoggerProvider = PreparationCoordinatorLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.notifInflaterImplProvider = DoubleCheck.provider(NotifInflaterImpl_Factory.create(this.provideIStatusBarServiceProvider, this.notifCollectionProvider, this.notifInflationErrorManagerProvider, this.notifPipelineProvider));
        final Provider<NotifViewBarn> provider6 = DoubleCheck.provider(NotifViewBarn_Factory.create());
        this.notifViewBarnProvider = provider6;
        final Provider<Object> provider7 = DoubleCheck.provider(PreparationCoordinator_Factory.create(this.preparationCoordinatorLoggerProvider, this.notifInflaterImplProvider, this.notifInflationErrorManagerProvider, provider6, this.provideIStatusBarServiceProvider));
        this.preparationCoordinatorProvider = (Provider<PreparationCoordinator>)provider7;
        this.notifCoordinatorsProvider = (Provider<NotifCoordinators>)DoubleCheck.provider(NotifCoordinators_Factory.create(this.dumpManagerProvider, this.featureFlagsProvider, this.hideNotifsForOtherUsersCoordinatorProvider, this.keyguardCoordinatorProvider, this.rankingCoordinatorProvider, this.foregroundCoordinatorProvider, this.deviceProvisionedCoordinatorProvider, this.bubbleCoordinatorProvider, this.headsUpCoordinatorProvider, this.conversationCoordinatorProvider, (Provider<PreparationCoordinator>)provider7));
        final Provider<VisualStabilityManager> provider8 = DoubleCheck.provider(NotificationsModule_ProvideVisualStabilityManagerFactory.create(this.provideNotificationEntryManagerProvider, this.provideHandlerProvider));
        this.provideVisualStabilityManagerProvider = provider8;
        final Provider<Object> provider9 = DoubleCheck.provider(NotifViewManager_Factory.create(this.notifViewBarnProvider, provider8, this.featureFlagsProvider));
        this.notifViewManagerProvider = (Provider<NotifViewManager>)provider9;
        this.notifPipelineInitializerProvider = (Provider<NotifPipelineInitializer>)DoubleCheck.provider(NotifPipelineInitializer_Factory.create(this.notifPipelineProvider, this.groupCoalescerProvider, this.notifCollectionProvider, this.shadeListBuilderProvider, this.notifCoordinatorsProvider, this.notifInflaterImplProvider, this.dumpManagerProvider, this.featureFlagsProvider, (Provider<NotifViewManager>)provider9));
        this.notifBindPipelineInitializerProvider = NotifBindPipelineInitializer_Factory.create(this.notifBindPipelineProvider, this.rowContentBindStageProvider);
        this.provideNotificationGroupAlertTransferHelperProvider = DoubleCheck.provider(StatusBarPhoneDependenciesModule_ProvideNotificationGroupAlertTransferHelperFactory.create(this.rowContentBindStageProvider));
        final Provider<HeadsUpBindController> provider10 = DoubleCheck.provider(HeadsUpBindController_Factory.create(this.headsUpViewBinderProvider, (Provider<NotificationInterruptStateProvider>)this.notificationInterruptStateProviderImplProvider));
        this.headsUpBindControllerProvider = provider10;
        this.notificationsControllerImplProvider = (Provider<NotificationsControllerImpl>)DoubleCheck.provider(NotificationsControllerImpl_Factory.create(this.featureFlagsProvider, this.provideNotificationListenerProvider, this.provideNotificationEntryManagerProvider, this.notifPipelineInitializerProvider, this.notifBindPipelineInitializerProvider, (Provider<DeviceProvisionedController>)this.deviceProvisionedControllerImplProvider, this.notificationRowBinderImplProvider, this.remoteInputUriControllerProvider, this.newBubbleControllerProvider, this.notificationGroupManagerProvider, this.provideNotificationGroupAlertTransferHelperProvider, (Provider<HeadsUpManager>)this.provideHeadsUpManagerPhoneProvider, provider10, this.headsUpViewBinderProvider));
        final NotificationsControllerStub_Factory create3 = NotificationsControllerStub_Factory.create(this.provideNotificationListenerProvider);
        this.notificationsControllerStubProvider = create3;
        this.provideNotificationsControllerProvider = (Provider<NotificationsController>)DoubleCheck.provider(NotificationsModule_ProvideNotificationsControllerFactory.create(this.provideContextProvider, this.notificationsControllerImplProvider, create3));
        final Provider<DarkIconDispatcher> provider11 = DoubleCheck.provider(DarkIconDispatcherImpl_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider));
        this.darkIconDispatcherImplProvider = (Provider<DarkIconDispatcherImpl>)provider11;
        this.lightBarControllerProvider = (Provider<LightBarController>)DoubleCheck.provider(LightBarController_Factory.create(this.provideContextProvider, provider11, (Provider<BatteryController>)this.batteryControllerImplProvider));
        this.provideIWindowManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideIWindowManagerFactory.create());
        this.provideAutoHideControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideAutoHideControllerFactory.create(builder.dependencyProvider, this.provideContextProvider, this.provideMainHandlerProvider, this.provideIWindowManagerProvider));
        this.statusBarIconControllerImplProvider = DoubleCheck.provider(StatusBarIconControllerImpl_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider));
        this.notificationWakeUpCoordinatorProvider = DoubleCheck.provider(NotificationWakeUpCoordinator_Factory.create((Provider<HeadsUpManager>)this.provideHeadsUpManagerPhoneProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.keyguardBypassControllerProvider, this.dozeParametersProvider));
        final Provider<NotificationRoundnessManager> provider12 = DoubleCheck.provider(NotificationRoundnessManager_Factory.create(this.keyguardBypassControllerProvider, this.notificationSectionsFeatureManagerProvider));
        this.notificationRoundnessManagerProvider = provider12;
        this.pulseExpansionHandlerProvider = (Provider<PulseExpansionHandler>)DoubleCheck.provider(PulseExpansionHandler_Factory.create(this.provideContextProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, this.provideHeadsUpManagerPhoneProvider, provider12, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, (Provider<FalsingManager>)this.falsingManagerProxyProvider));
        this.dynamicPrivacyControllerProvider = DoubleCheck.provider(DynamicPrivacyController_Factory.create((Provider<NotificationLockscreenUserManager>)this.notificationLockscreenUserManagerImplProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider));
        this.bypassHeadsUpNotifierProvider = DoubleCheck.provider(BypassHeadsUpNotifier_Factory.create(this.provideContextProvider, this.keyguardBypassControllerProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.provideHeadsUpManagerPhoneProvider, (Provider<NotificationLockscreenUserManager>)this.notificationLockscreenUserManagerImplProvider, this.provideNotificationMediaManagerProvider, this.provideNotificationEntryManagerProvider, (Provider<TunerService>)this.tunerServiceImplProvider));
        this.remoteInputQuickSettingsDisablerProvider = DoubleCheck.provider(RemoteInputQuickSettingsDisabler_Factory.create(this.provideContextProvider, this.provideConfigurationControllerProvider, this.provideCommandQueueProvider));
        this.provideAccessibilityManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideAccessibilityManagerFactory.create(this.provideContextProvider));
        this.provideINotificationManagerProvider = DoubleCheck.provider(DependencyProvider_ProvideINotificationManagerFactory.create(builder.dependencyProvider));
        final Provider<ShortcutManager> provider13 = DoubleCheck.provider(SystemServicesModule_ProvideShortcutManagerFactory.create(this.provideContextProvider));
        this.provideShortcutManagerProvider = provider13;
        this.provideNotificationGutsManagerProvider = (Provider<NotificationGutsManager>)DoubleCheck.provider(NotificationsModule_ProvideNotificationGutsManagerFactory.create(this.provideContextProvider, this.provideVisualStabilityManagerProvider, this.provideStatusBarProvider, this.provideMainHandlerProvider, this.provideAccessibilityManagerProvider, this.highPriorityProvider, this.provideINotificationManagerProvider, this.provideLauncherAppsProvider, provider13));
        this.expansionStateLoggerProvider = NotificationLogger_ExpansionStateLogger_Factory.create(this.provideUiBackgroundExecutorProvider);
        final Provider<NotificationPanelLogger> provider14 = DoubleCheck.provider(NotificationsModule_ProvideNotificationPanelLoggerFactory.create());
        this.provideNotificationPanelLoggerProvider = provider14;
        this.provideNotificationLoggerProvider = (Provider<NotificationLogger>)DoubleCheck.provider(NotificationsModule_ProvideNotificationLoggerFactory.create(this.provideNotificationListenerProvider, this.provideUiBackgroundExecutorProvider, this.provideNotificationEntryManagerProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.expansionStateLoggerProvider, provider14));
        this.foregroundServiceSectionControllerProvider = DoubleCheck.provider(ForegroundServiceSectionController_Factory.create(this.provideNotificationEntryManagerProvider, this.foregroundServiceDismissalFeatureControllerProvider));
        final DynamicChildBindController_Factory create4 = DynamicChildBindController_Factory.create(this.rowContentBindStageProvider);
        this.dynamicChildBindControllerProvider = create4;
        this.provideNotificationViewHierarchyManagerProvider = (Provider<NotificationViewHierarchyManager>)DoubleCheck.provider(StatusBarDependenciesModule_ProvideNotificationViewHierarchyManagerFactory.create(this.provideContextProvider, this.provideMainHandlerProvider, (Provider<NotificationLockscreenUserManager>)this.notificationLockscreenUserManagerImplProvider, this.notificationGroupManagerProvider, this.provideVisualStabilityManagerProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.provideNotificationEntryManagerProvider, this.keyguardBypassControllerProvider, this.newBubbleControllerProvider, this.dynamicPrivacyControllerProvider, this.foregroundServiceSectionControllerProvider, create4));
        this.provideNotificationAlertingManagerProvider = DoubleCheck.provider(NotificationsModule_ProvideNotificationAlertingManagerFactory.create(this.provideNotificationEntryManagerProvider, this.provideNotificationRemoteInputManagerProvider, this.provideVisualStabilityManagerProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, (Provider<NotificationInterruptStateProvider>)this.notificationInterruptStateProviderImplProvider, this.provideNotificationListenerProvider, (Provider<HeadsUpManager>)this.provideHeadsUpManagerPhoneProvider));
        this.provideMetricsLoggerProvider = DoubleCheck.provider(DependencyProvider_ProvideMetricsLoggerFactory.create(builder.dependencyProvider));
        final Provider access$400 = of((Provider<Object>)this.provideStatusBarProvider);
        this.optionalOfLazyOfStatusBarProvider = (Provider<Optional<Lazy<StatusBar>>>)access$400;
        final Provider<Object> provider15 = DoubleCheck.provider(ActivityStarterDelegate_Factory.create(access$400));
        this.activityStarterDelegateProvider = (Provider<ActivityStarterDelegate>)provider15;
        this.userSwitcherControllerProvider = (Provider<UserSwitcherController>)DoubleCheck.provider(UserSwitcherController_Factory.create(this.provideContextProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, this.provideMainHandlerProvider, (Provider<ActivityStarter>)provider15, this.broadcastDispatcherProvider));
        this.provideConnectivityManagagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideConnectivityManagagerFactory.create(this.provideContextProvider));
        this.provideTelephonyManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideTelephonyManagerFactory.create(this.provideContextProvider));
        this.provideWifiManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideWifiManagerFactory.create(this.provideContextProvider));
        final Provider<NetworkScoreManager> provider16 = DoubleCheck.provider(SystemServicesModule_ProvideNetworkScoreManagerFactory.create(this.provideContextProvider));
        this.provideNetworkScoreManagerProvider = provider16;
        this.networkControllerImplProvider = (Provider<NetworkControllerImpl>)DoubleCheck.provider(NetworkControllerImpl_Factory.create(this.provideContextProvider, this.provideBgLooperProvider, (Provider<DeviceProvisionedController>)this.deviceProvisionedControllerImplProvider, this.broadcastDispatcherProvider, this.provideConnectivityManagagerProvider, this.provideTelephonyManagerProvider, this.provideWifiManagerProvider, provider16));
        this.screenLifecycleProvider = DoubleCheck.provider(ScreenLifecycle_Factory.create());
        this.wakefulnessLifecycleProvider = DoubleCheck.provider(WakefulnessLifecycle_Factory.create());
        this.vibratorHelperProvider = DoubleCheck.provider(VibratorHelper_Factory.create(this.provideContextProvider));
        this.provideNavigationBarControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideNavigationBarControllerFactory.create(builder.dependencyProvider, this.provideContextProvider, this.provideMainHandlerProvider, this.provideCommandQueueProvider));
        this.provideAssistUtilsProvider = DoubleCheck.provider(AssistModule_ProvideAssistUtilsFactory.create(this.provideContextProvider));
        this.provideBackgroundHandlerProvider = DoubleCheck.provider(AssistModule_ProvideBackgroundHandlerFactory.create());
        this.provideAssistHandleViewControllerProvider = AssistModule_ProvideAssistHandleViewControllerFactory.create(this.provideNavigationBarControllerProvider);
        this.deviceConfigHelperProvider = DoubleCheck.provider(DeviceConfigHelper_Factory.create());
        this.assistHandleOffBehaviorProvider = DoubleCheck.provider(AssistHandleOffBehavior_Factory.create());
        final Provider<SysUiState> provider17 = DoubleCheck.provider(SystemUIModule_ProvideSysUiStateFactory.create());
        this.provideSysUiStateProvider = provider17;
        this.assistHandleLikeHomeBehaviorProvider = DoubleCheck.provider(AssistHandleLikeHomeBehavior_Factory.create((Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.wakefulnessLifecycleProvider, provider17));
        this.provideSystemClockProvider = DoubleCheck.provider(AssistModule_ProvideSystemClockFactory.create());
        this.provideActivityManagerWrapperProvider = DoubleCheck.provider(DependencyProvider_ProvideActivityManagerWrapperFactory.create(builder.dependencyProvider));
        this.displayControllerProvider = DoubleCheck.provider(DisplayController_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, this.provideIWindowManagerProvider));
        this.floatingContentCoordinatorProvider = DoubleCheck.provider(FloatingContentCoordinator_Factory.create());
        final PipSnapAlgorithm_Factory create5 = PipSnapAlgorithm_Factory.create(this.provideContextProvider);
        this.pipSnapAlgorithmProvider = create5;
        this.pipBoundsHandlerProvider = PipBoundsHandler_Factory.create(this.provideContextProvider, create5);
        this.pipSurfaceTransactionHelperProvider = DoubleCheck.provider(PipSurfaceTransactionHelper_Factory.create(this.provideContextProvider));
        final Provider<BasePipManager> provider18 = DoubleCheck.provider(PipManager_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider, this.displayControllerProvider, this.floatingContentCoordinatorProvider, DeviceConfigProxy_Factory.create(), this.pipBoundsHandlerProvider, this.pipSnapAlgorithmProvider, this.pipSurfaceTransactionHelperProvider));
        this.pipManagerProvider = (Provider<PipManager>)provider18;
        this.pipUIProvider = (Provider<PipUI>)DoubleCheck.provider(PipUI_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider, provider18));
        final DelegateFactory<ContextComponentHelper> contextComponentResolverProvider = new DelegateFactory<ContextComponentHelper>();
        this.contextComponentResolverProvider = (Provider<ContextComponentResolver>)contextComponentResolverProvider;
        final RecentsModule_ProvideRecentsImplFactory create6 = RecentsModule_ProvideRecentsImplFactory.create(this.provideContextProvider, contextComponentResolverProvider);
        this.provideRecentsImplProvider = create6;
        final Provider<Object> provider19 = DoubleCheck.provider(SystemUIDefaultModule_ProvideRecentsFactory.create(this.provideContextProvider, create6, this.provideCommandQueueProvider));
        this.provideRecentsProvider = (Provider<Recents>)provider19;
        this.optionalOfLazyOfRecentsProvider = (Provider<Optional<Lazy<Recents>>>)of(provider19);
        this.systemWindowsProvider = DoubleCheck.provider(SystemWindows_Factory.create(this.provideContextProvider, this.displayControllerProvider, this.provideIWindowManagerProvider));
        final Provider<TransactionPool> provider20 = DoubleCheck.provider(TransactionPool_Factory.create());
        this.transactionPoolProvider = provider20;
        final Provider<Object> provider21 = DoubleCheck.provider(DisplayImeController_Factory.create(this.systemWindowsProvider, this.displayControllerProvider, this.provideMainHandlerProvider, provider20));
        this.displayImeControllerProvider = (Provider<DisplayImeController>)provider21;
        final Provider<Object> provider22 = DoubleCheck.provider(DividerModule_ProvideDividerFactory.create(this.provideContextProvider, this.optionalOfLazyOfRecentsProvider, this.displayControllerProvider, this.systemWindowsProvider, (Provider<DisplayImeController>)provider21, this.provideMainHandlerProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, this.transactionPoolProvider));
        this.provideDividerProvider = (Provider<Divider>)provider22;
        final Provider access$401 = of(provider22);
        this.optionalOfDividerProvider = (Provider<Optional<Divider>>)access$401;
        this.overviewProxyServiceProvider = (Provider<OverviewProxyService>)DoubleCheck.provider(OverviewProxyService_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider, (Provider<DeviceProvisionedController>)this.deviceProvisionedControllerImplProvider, this.provideNavigationBarControllerProvider, this.navigationModeControllerProvider, this.notificationShadeWindowControllerProvider, this.provideSysUiStateProvider, this.pipUIProvider, access$401, this.optionalOfLazyOfStatusBarProvider));
        final Provider<PackageManagerWrapper> provider23 = DoubleCheck.provider(SystemServicesModule_ProvidePackageManagerWrapperFactory.create());
        this.providePackageManagerWrapperProvider = provider23;
        final Provider<Object> provider24 = DoubleCheck.provider(AssistHandleReminderExpBehavior_Factory.create(this.provideSystemClockProvider, this.provideBackgroundHandlerProvider, this.deviceConfigHelperProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.provideActivityManagerWrapperProvider, this.overviewProxyServiceProvider, this.provideSysUiStateProvider, this.wakefulnessLifecycleProvider, provider23, this.broadcastDispatcherProvider, (Provider<BootCompleteCache>)this.bootCompleteCacheImplProvider));
        this.assistHandleReminderExpBehaviorProvider = provider24;
        final Provider<Object> provider25 = DoubleCheck.provider(AssistModule_ProvideAssistHandleBehaviorControllerMapFactory.create(this.assistHandleOffBehaviorProvider, this.assistHandleLikeHomeBehaviorProvider, (Provider<AssistHandleReminderExpBehavior>)provider24));
        this.provideAssistHandleBehaviorControllerMapProvider = provider25;
        this.assistHandleBehaviorControllerProvider = (Provider<AssistHandleBehaviorController>)DoubleCheck.provider(AssistHandleBehaviorController_Factory.create(this.provideContextProvider, this.provideAssistUtilsProvider, this.provideBackgroundHandlerProvider, this.provideAssistHandleViewControllerProvider, this.deviceConfigHelperProvider, (Provider<Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController>>)provider25, this.navigationModeControllerProvider, this.provideAccessibilityManagerProvider, this.dumpManagerProvider));
    }
    
    private void initialize3(final Builder builder) {
        final Provider<PhoneStateMonitor> provider = DoubleCheck.provider(PhoneStateMonitor_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider, this.optionalOfLazyOfStatusBarProvider, (Provider<BootCompleteCache>)this.bootCompleteCacheImplProvider));
        this.phoneStateMonitorProvider = provider;
        this.assistManagerProvider = (Provider<AssistManager>)DoubleCheck.provider(AssistManager_Factory.create((Provider<DeviceProvisionedController>)this.deviceProvisionedControllerImplProvider, this.provideContextProvider, this.provideAssistUtilsProvider, this.assistHandleBehaviorControllerProvider, this.provideCommandQueueProvider, provider, this.overviewProxyServiceProvider, this.provideConfigurationControllerProvider, this.provideSysUiStateProvider));
        this.lockscreenGestureLoggerProvider = DoubleCheck.provider(LockscreenGestureLogger_Factory.create());
        this.shadeControllerImplProvider = new DelegateFactory<ShadeControllerImpl>();
        this.accessibilityControllerProvider = DoubleCheck.provider(AccessibilityController_Factory.create(this.provideContextProvider));
        this.builderProvider = WakeLock_Builder_Factory.create(this.provideContextProvider);
        final Provider<IBatteryStats> provider2 = DoubleCheck.provider(SystemServicesModule_ProvideIBatteryStatsFactory.create());
        this.provideIBatteryStatsProvider = provider2;
        final Provider<Object> provider3 = DoubleCheck.provider(KeyguardIndicationController_Factory.create(this.provideContextProvider, this.builderProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.keyguardUpdateMonitorProvider, (Provider<DockManager>)this.dockManagerImplProvider, provider2));
        this.keyguardIndicationControllerProvider = (Provider<KeyguardIndicationController>)provider3;
        this.lockscreenLockIconControllerProvider = (Provider<LockscreenLockIconController>)DoubleCheck.provider(LockscreenLockIconController_Factory.create(this.lockscreenGestureLoggerProvider, this.keyguardUpdateMonitorProvider, this.provideLockPatternUtilsProvider, (Provider<ShadeController>)this.shadeControllerImplProvider, this.accessibilityControllerProvider, (Provider<KeyguardIndicationController>)provider3, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.provideConfigurationControllerProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, (Provider<DockManager>)this.dockManagerImplProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, this.provideResourcesProvider, this.provideHeadsUpManagerPhoneProvider));
        this.provideAlarmManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideAlarmManagerFactory.create(this.provideContextProvider));
        this.builderProvider2 = DelayedWakeLock_Builder_Factory.create(this.provideContextProvider);
        final Provider<BlurUtils> provider4 = DoubleCheck.provider(BlurUtils_Factory.create(this.provideResourcesProvider, this.dumpManagerProvider));
        this.blurUtilsProvider = provider4;
        this.scrimControllerProvider = (Provider<ScrimController>)DoubleCheck.provider(ScrimController_Factory.create(this.lightBarControllerProvider, this.dozeParametersProvider, this.provideAlarmManagerProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, this.builderProvider2, this.provideHandlerProvider, this.keyguardUpdateMonitorProvider, this.sysuiColorExtractorProvider, (Provider<DockManager>)this.dockManagerImplProvider, provider4));
        this.provideKeyguardLiftControllerProvider = DoubleCheck.provider(SystemUIModule_ProvideKeyguardLiftControllerFactory.create(this.provideContextProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.asyncSensorManagerProvider, this.keyguardUpdateMonitorProvider, this.dumpManagerProvider));
        final SystemServicesModule_ProvideWallpaperManagerFactory create = SystemServicesModule_ProvideWallpaperManagerFactory.create(this.provideContextProvider);
        this.provideWallpaperManagerProvider = create;
        this.lockscreenWallpaperProvider = (Provider<LockscreenWallpaper>)DoubleCheck.provider(LockscreenWallpaper_Factory.create(create, SystemServicesModule_ProvideIWallPaperManagerFactory.create(), this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, this.provideNotificationMediaManagerProvider, this.provideMainHandlerProvider));
        final Provider<LogBuffer> provider5 = DoubleCheck.provider(LogModule_ProvideDozeLogBufferFactory.create(this.provideLogcatEchoTrackerProvider, this.dumpManagerProvider));
        this.provideDozeLogBufferProvider = provider5;
        final DozeLogger_Factory create2 = DozeLogger_Factory.create(provider5);
        this.dozeLoggerProvider = create2;
        final Provider<Object> provider6 = DoubleCheck.provider(DozeLog_Factory.create(this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, create2));
        this.dozeLogProvider = (Provider<DozeLog>)provider6;
        final Provider<Object> provider7 = DoubleCheck.provider(DozeScrimController_Factory.create(this.dozeParametersProvider, (Provider<DozeLog>)provider6));
        this.dozeScrimControllerProvider = (Provider<DozeScrimController>)provider7;
        final Provider<Object> provider8 = DoubleCheck.provider(BiometricUnlockController_Factory.create(this.provideContextProvider, (Provider<DozeScrimController>)provider7, this.newKeyguardViewMediatorProvider, this.scrimControllerProvider, this.provideStatusBarProvider, (Provider<ShadeController>)this.shadeControllerImplProvider, this.notificationShadeWindowControllerProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, this.provideHandlerProvider, this.keyguardUpdateMonitorProvider, this.provideResourcesProvider, this.keyguardBypassControllerProvider, this.dozeParametersProvider, this.provideMetricsLoggerProvider, this.dumpManagerProvider));
        this.biometricUnlockControllerProvider = (Provider<BiometricUnlockController>)provider8;
        this.dozeServiceHostProvider = (Provider<DozeServiceHost>)DoubleCheck.provider(DozeServiceHost_Factory.create(this.dozeLogProvider, this.providePowerManagerProvider, this.wakefulnessLifecycleProvider, (Provider<SysuiStatusBarStateController>)this.statusBarStateControllerImplProvider, (Provider<DeviceProvisionedController>)this.deviceProvisionedControllerImplProvider, this.provideHeadsUpManagerPhoneProvider, (Provider<BatteryController>)this.batteryControllerImplProvider, this.scrimControllerProvider, (Provider<BiometricUnlockController>)provider8, this.newKeyguardViewMediatorProvider, this.assistManagerProvider, this.dozeScrimControllerProvider, this.keyguardUpdateMonitorProvider, this.provideVisualStabilityManagerProvider, this.pulseExpansionHandlerProvider, this.notificationShadeWindowControllerProvider, this.notificationWakeUpCoordinatorProvider, this.lockscreenLockIconControllerProvider));
        this.screenPinningRequestProvider = ScreenPinningRequest_Factory.create(this.provideContextProvider, this.optionalOfLazyOfStatusBarProvider);
        final Provider<VolumeDialogControllerImpl> provider9 = DoubleCheck.provider(VolumeDialogControllerImpl_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider, this.optionalOfLazyOfStatusBarProvider));
        this.volumeDialogControllerImplProvider = provider9;
        this.volumeDialogComponentProvider = (Provider<VolumeDialogComponent>)DoubleCheck.provider(VolumeDialogComponent_Factory.create(this.provideContextProvider, this.newKeyguardViewMediatorProvider, provider9));
        this.optionalOfRecentsProvider = (Provider<Optional<Recents>>)of((Provider<Object>)this.provideRecentsProvider);
        this.statusBarComponentBuilderProvider = new Provider<StatusBarComponent.Builder>() {
            @Override
            public StatusBarComponent.Builder get() {
                return new StatusBarComponentBuilder();
            }
        };
        this.lightsOutNotifControllerProvider = DoubleCheck.provider(LightsOutNotifController_Factory.create(this.provideWindowManagerProvider, this.provideNotificationEntryManagerProvider, this.provideCommandQueueProvider));
        this.statusBarRemoteInputCallbackProvider = DoubleCheck.provider(StatusBarRemoteInputCallback_Factory.create(this.provideContextProvider, this.notificationGroupManagerProvider, (Provider<NotificationLockscreenUserManager>)this.notificationLockscreenUserManagerImplProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.statusBarKeyguardViewManagerProvider, (Provider<ActivityStarter>)this.activityStarterDelegateProvider, (Provider<ShadeController>)this.shadeControllerImplProvider, this.provideCommandQueueProvider));
        final Provider<ActivityIntentHelper> provider10 = DoubleCheck.provider(ActivityIntentHelper_Factory.create(this.provideContextProvider));
        this.activityIntentHelperProvider = provider10;
        this.builderProvider3 = (Provider<StatusBarNotificationActivityStarter.Builder>)DoubleCheck.provider(StatusBarNotificationActivityStarter_Builder_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider, this.assistManagerProvider, this.provideNotificationEntryManagerProvider, this.provideHeadsUpManagerPhoneProvider, (Provider<ActivityStarter>)this.activityStarterDelegateProvider, this.provideIStatusBarServiceProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.statusBarKeyguardViewManagerProvider, this.provideKeyguardManagerProvider, this.provideIDreamManagerProvider, this.provideNotificationRemoteInputManagerProvider, this.statusBarRemoteInputCallbackProvider, this.notificationGroupManagerProvider, (Provider<NotificationLockscreenUserManager>)this.notificationLockscreenUserManagerImplProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, (Provider<NotificationInterruptStateProvider>)this.notificationInterruptStateProviderImplProvider, this.provideMetricsLoggerProvider, this.provideLockPatternUtilsProvider, this.provideMainHandlerProvider, this.provideBgHandlerProvider, this.provideUiBackgroundExecutorProvider, provider10, this.newBubbleControllerProvider, (Provider<ShadeController>)this.shadeControllerImplProvider, this.featureFlagsProvider, this.notifPipelineProvider, this.notifCollectionProvider));
        final Factory<DaggerSystemUIRootComponent> create3 = InstanceFactory.create(this);
        this.systemUIRootComponentProvider = (Provider<SystemUIRootComponent>)create3;
        this.injectionInflationControllerProvider = (Provider<InjectionInflationController>)DoubleCheck.provider(InjectionInflationController_Factory.create((Provider<SystemUIRootComponent>)create3));
        final Provider<NotificationRowComponent.Builder> notificationRowComponentBuilderProvider = new Provider<NotificationRowComponent.Builder>() {
            @Override
            public NotificationRowComponent.Builder get() {
                return new NotificationRowComponentBuilder();
            }
        };
        this.notificationRowComponentBuilderProvider = notificationRowComponentBuilderProvider;
        this.superStatusBarViewFactoryProvider = (Provider<SuperStatusBarViewFactory>)DoubleCheck.provider(SuperStatusBarViewFactory_Factory.create(this.provideContextProvider, this.injectionInflationControllerProvider, notificationRowComponentBuilderProvider, this.lockscreenLockIconControllerProvider));
        this.initControllerProvider = DoubleCheck.provider(InitController_Factory.create());
        this.provideTimeTickHandlerProvider = DoubleCheck.provider(DependencyProvider_ProvideTimeTickHandlerFactory.create(builder.dependencyProvider));
        this.pluginDependencyProvider = DoubleCheck.provider(PluginDependencyProvider_Factory.create(this.providePluginManagerProvider));
        this.keyguardDismissUtilProvider = DoubleCheck.provider(KeyguardDismissUtil_Factory.create());
        this.userInfoControllerImplProvider = DoubleCheck.provider(UserInfoControllerImpl_Factory.create(this.provideContextProvider));
        this.castControllerImplProvider = DoubleCheck.provider(CastControllerImpl_Factory.create(this.provideContextProvider));
        this.hotspotControllerImplProvider = DoubleCheck.provider(HotspotControllerImpl_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, this.provideBgHandlerProvider));
        this.provideLocalBluetoothControllerProvider = DoubleCheck.provider(SystemServicesModule_ProvideLocalBluetoothControllerFactory.create(this.provideContextProvider, this.provideBgHandlerProvider));
        this.bluetoothControllerImplProvider = DoubleCheck.provider(BluetoothControllerImpl_Factory.create(this.provideContextProvider, this.provideBgLooperProvider, ConcurrencyModule_ProvideMainLooperFactory.create(), this.provideLocalBluetoothControllerProvider));
        this.nextAlarmControllerImplProvider = DoubleCheck.provider(NextAlarmControllerImpl_Factory.create(this.provideContextProvider));
        this.rotationLockControllerImplProvider = DoubleCheck.provider(RotationLockControllerImpl_Factory.create(this.provideContextProvider));
        this.provideDataSaverControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideDataSaverControllerFactory.create(builder.dependencyProvider, (Provider<NetworkController>)this.networkControllerImplProvider));
        this.zenModeControllerImplProvider = DoubleCheck.provider(ZenModeControllerImpl_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, this.broadcastDispatcherProvider));
        this.locationControllerImplProvider = DoubleCheck.provider(LocationControllerImpl_Factory.create(this.provideContextProvider, this.provideBgLooperProvider, this.broadcastDispatcherProvider, (Provider<BootCompleteCache>)this.bootCompleteCacheImplProvider));
        this.sensorPrivacyControllerImplProvider = DoubleCheck.provider(SensorPrivacyControllerImpl_Factory.create(this.provideContextProvider));
        this.provideAudioManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideAudioManagerFactory.create(this.provideContextProvider));
        this.provideTelecomManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideTelecomManagerFactory.create(this.provideContextProvider));
        this.provideDisplayIdProvider = SystemServicesModule_ProvideDisplayIdFactory.create(this.provideContextProvider);
        this.provideSharePreferencesProvider = DependencyProvider_ProvideSharePreferencesFactory.create(builder.dependencyProvider, this.provideContextProvider);
        final DateFormatUtil_Factory create4 = DateFormatUtil_Factory.create(this.provideContextProvider);
        this.dateFormatUtilProvider = create4;
        this.phoneStatusBarPolicyProvider = PhoneStatusBarPolicy_Factory.create((Provider<StatusBarIconController>)this.statusBarIconControllerImplProvider, this.provideCommandQueueProvider, this.broadcastDispatcherProvider, this.provideUiBackgroundExecutorProvider, this.provideResourcesProvider, (Provider<CastController>)this.castControllerImplProvider, (Provider<HotspotController>)this.hotspotControllerImplProvider, (Provider<BluetoothController>)this.bluetoothControllerImplProvider, (Provider<NextAlarmController>)this.nextAlarmControllerImplProvider, (Provider<UserInfoController>)this.userInfoControllerImplProvider, (Provider<RotationLockController>)this.rotationLockControllerImplProvider, this.provideDataSaverControllerProvider, (Provider<ZenModeController>)this.zenModeControllerImplProvider, (Provider<DeviceProvisionedController>)this.deviceProvisionedControllerImplProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, (Provider<LocationController>)this.locationControllerImplProvider, (Provider<SensorPrivacyController>)this.sensorPrivacyControllerImplProvider, this.provideIActivityManagerProvider, this.provideAlarmManagerProvider, this.provideUserManagerProvider, this.provideAudioManagerProvider, this.recordingControllerProvider, this.provideTelecomManagerProvider, this.provideDisplayIdProvider, this.provideSharePreferencesProvider, create4);
        final Provider<Choreographer> provider11 = DoubleCheck.provider(DependencyProvider_ProvidesChoreographerFactory.create(builder.dependencyProvider));
        this.providesChoreographerProvider = provider11;
        this.notificationShadeDepthControllerProvider = (Provider<NotificationShadeDepthController>)DoubleCheck.provider(NotificationShadeDepthController_Factory.create((Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.blurUtilsProvider, this.biometricUnlockControllerProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, provider11, this.provideWallpaperManagerProvider, this.notificationShadeWindowControllerProvider, this.dumpManagerProvider));
        final Provider<StatusBarTouchableRegionManager> provider12 = DoubleCheck.provider(StatusBarTouchableRegionManager_Factory.create(this.provideContextProvider, this.notificationShadeWindowControllerProvider, this.provideConfigurationControllerProvider, this.provideHeadsUpManagerPhoneProvider, this.newBubbleControllerProvider));
        this.statusBarTouchableRegionManagerProvider = provider12;
        ((DelegateFactory)this.provideStatusBarProvider).setDelegatedProvider(this.provideStatusBarProvider = (Provider<StatusBar>)DoubleCheck.provider(StatusBarPhoneModule_ProvideStatusBarFactory.create(this.provideContextProvider, this.provideNotificationsControllerProvider, this.lightBarControllerProvider, this.provideAutoHideControllerProvider, this.keyguardUpdateMonitorProvider, (Provider<StatusBarIconController>)this.statusBarIconControllerImplProvider, this.pulseExpansionHandlerProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, this.provideHeadsUpManagerPhoneProvider, this.dynamicPrivacyControllerProvider, this.bypassHeadsUpNotifierProvider, (Provider<FalsingManager>)this.falsingManagerProxyProvider, this.broadcastDispatcherProvider, this.remoteInputQuickSettingsDisablerProvider, this.provideNotificationGutsManagerProvider, this.provideNotificationLoggerProvider, (Provider<NotificationInterruptStateProvider>)this.notificationInterruptStateProviderImplProvider, this.provideNotificationViewHierarchyManagerProvider, this.newKeyguardViewMediatorProvider, this.provideNotificationAlertingManagerProvider, this.provideDisplayMetricsProvider, this.provideMetricsLoggerProvider, this.provideUiBackgroundExecutorProvider, this.provideNotificationMediaManagerProvider, (Provider<NotificationLockscreenUserManager>)this.notificationLockscreenUserManagerImplProvider, this.provideNotificationRemoteInputManagerProvider, this.userSwitcherControllerProvider, (Provider<NetworkController>)this.networkControllerImplProvider, (Provider<BatteryController>)this.batteryControllerImplProvider, this.sysuiColorExtractorProvider, this.screenLifecycleProvider, this.wakefulnessLifecycleProvider, (Provider<SysuiStatusBarStateController>)this.statusBarStateControllerImplProvider, this.vibratorHelperProvider, this.newBubbleControllerProvider, this.notificationGroupManagerProvider, this.provideVisualStabilityManagerProvider, (Provider<DeviceProvisionedController>)this.deviceProvisionedControllerImplProvider, this.provideNavigationBarControllerProvider, this.assistManagerProvider, this.provideConfigurationControllerProvider, this.notificationShadeWindowControllerProvider, this.lockscreenLockIconControllerProvider, this.dozeParametersProvider, this.scrimControllerProvider, this.provideKeyguardLiftControllerProvider, this.lockscreenWallpaperProvider, this.biometricUnlockControllerProvider, this.dozeServiceHostProvider, this.providePowerManagerProvider, this.screenPinningRequestProvider, this.dozeScrimControllerProvider, (Provider<VolumeComponent>)this.volumeDialogComponentProvider, this.provideCommandQueueProvider, this.optionalOfRecentsProvider, this.statusBarComponentBuilderProvider, this.providePluginManagerProvider, this.optionalOfDividerProvider, this.lightsOutNotifControllerProvider, this.builderProvider3, (Provider<ShadeController>)this.shadeControllerImplProvider, this.superStatusBarViewFactoryProvider, this.statusBarKeyguardViewManagerProvider, this.providesViewMediatorCallbackProvider, this.initControllerProvider, (Provider<DarkIconDispatcher>)this.darkIconDispatcherImplProvider, this.provideTimeTickHandlerProvider, this.pluginDependencyProvider, this.keyguardDismissUtilProvider, (Provider<ExtensionController>)this.extensionControllerImplProvider, this.userInfoControllerImplProvider, this.phoneStatusBarPolicyProvider, this.keyguardIndicationControllerProvider, this.notificationShadeDepthControllerProvider, this.dismissCallbackRegistryProvider, provider12)));
        this.mediaArtworkProcessorProvider = DoubleCheck.provider(MediaArtworkProcessor_Factory.create());
        final MediaControllerFactory_Factory create5 = MediaControllerFactory_Factory.create(this.provideContextProvider);
        this.mediaControllerFactoryProvider = create5;
        final Provider<Object> provider13 = DoubleCheck.provider(KeyguardMediaPlayer_Factory.create(this.provideContextProvider, create5, this.provideBackgroundExecutorProvider));
        this.keyguardMediaPlayerProvider = (Provider<KeyguardMediaPlayer>)provider13;
        ((DelegateFactory)this.provideNotificationMediaManagerProvider).setDelegatedProvider(this.provideNotificationMediaManagerProvider = (Provider<NotificationMediaManager>)DoubleCheck.provider(StatusBarDependenciesModule_ProvideNotificationMediaManagerFactory.create(this.provideContextProvider, this.provideStatusBarProvider, this.notificationShadeWindowControllerProvider, this.provideNotificationEntryManagerProvider, this.mediaArtworkProcessorProvider, this.keyguardBypassControllerProvider, (Provider<KeyguardMediaPlayer>)provider13, this.provideMainExecutorProvider, DeviceConfigProxy_Factory.create())));
        ((DelegateFactory)this.statusBarKeyguardViewManagerProvider).setDelegatedProvider(this.statusBarKeyguardViewManagerProvider = DoubleCheck.provider(StatusBarKeyguardViewManager_Factory.create(this.provideContextProvider, this.providesViewMediatorCallbackProvider, this.provideLockPatternUtilsProvider, (Provider<SysuiStatusBarStateController>)this.statusBarStateControllerImplProvider, this.provideConfigurationControllerProvider, this.keyguardUpdateMonitorProvider, this.navigationModeControllerProvider, (Provider<DockManager>)this.dockManagerImplProvider, this.notificationShadeWindowControllerProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, this.provideNotificationMediaManagerProvider)));
        ((DelegateFactory)this.shadeControllerImplProvider).setDelegatedProvider(this.shadeControllerImplProvider = DoubleCheck.provider(ShadeControllerImpl_Factory.create(this.provideCommandQueueProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, this.notificationShadeWindowControllerProvider, this.statusBarKeyguardViewManagerProvider, this.provideWindowManagerProvider, this.provideStatusBarProvider, this.assistManagerProvider, this.newBubbleControllerProvider)));
        final Provider<BubbleData> provider14 = DoubleCheck.provider(BubbleData_Factory.create(this.provideContextProvider));
        this.bubbleDataProvider = provider14;
        ((DelegateFactory)this.newBubbleControllerProvider).setDelegatedProvider(this.newBubbleControllerProvider = (Provider<BubbleController>)DoubleCheck.provider(BubbleModule_NewBubbleControllerFactory.create(this.provideContextProvider, this.notificationShadeWindowControllerProvider, (Provider<StatusBarStateController>)this.statusBarStateControllerImplProvider, (Provider<ShadeController>)this.shadeControllerImplProvider, provider14, this.provideConfigurationControllerProvider, (Provider<NotificationInterruptStateProvider>)this.notificationInterruptStateProviderImplProvider, (Provider<ZenModeController>)this.zenModeControllerImplProvider, (Provider<NotificationLockscreenUserManager>)this.notificationLockscreenUserManagerImplProvider, this.notificationGroupManagerProvider, this.provideNotificationEntryManagerProvider, this.notifPipelineProvider, this.featureFlagsProvider, this.dumpManagerProvider, this.floatingContentCoordinatorProvider, this.provideSysUiStateProvider)));
        this.bubbleOverflowActivityProvider = BubbleOverflowActivity_Factory.create(this.newBubbleControllerProvider);
        final Provider<Executor> provider15 = DoubleCheck.provider(ConcurrencyModule_ProvideExecutorFactory.create(this.provideBgLooperProvider));
        this.provideExecutorProvider = provider15;
        this.controlsListingControllerImplProvider = (Provider<ControlsListingControllerImpl>)DoubleCheck.provider(ControlsListingControllerImpl_Factory.create(this.provideContextProvider, provider15));
        this.provideBackgroundDelayableExecutorProvider = DoubleCheck.provider(ConcurrencyModule_ProvideBackgroundDelayableExecutorFactory.create(this.provideBgLooperProvider));
        final DelegateFactory<ControlsController> controlsControllerImplProvider = new DelegateFactory<ControlsController>();
        this.controlsControllerImplProvider = (Provider<ControlsControllerImpl>)controlsControllerImplProvider;
        this.controlsUiControllerImplProvider = (Provider<ControlsUiControllerImpl>)DoubleCheck.provider(ControlsUiControllerImpl_Factory.create(controlsControllerImplProvider, this.provideContextProvider, this.provideMainDelayableExecutorProvider, this.provideBackgroundDelayableExecutorProvider, (Provider<ControlsListingController>)this.controlsListingControllerImplProvider, this.provideSharePreferencesProvider));
        this.controlsBindingControllerImplProvider = DoubleCheck.provider(ControlsBindingControllerImpl_Factory.create(this.provideContextProvider, this.provideBackgroundDelayableExecutorProvider, (Provider<ControlsController>)this.controlsControllerImplProvider));
        final Provider<Optional<ControlsFavoritePersistenceWrapper>> absentJdkOptionalProvider = absentJdkOptionalProvider();
        this.optionalOfControlsFavoritePersistenceWrapperProvider = absentJdkOptionalProvider;
        ((DelegateFactory)this.controlsControllerImplProvider).setDelegatedProvider(this.controlsControllerImplProvider = (Provider<ControlsControllerImpl>)DoubleCheck.provider(ControlsControllerImpl_Factory.create(this.provideContextProvider, this.provideBackgroundDelayableExecutorProvider, (Provider<ControlsUiController>)this.controlsUiControllerImplProvider, (Provider<ControlsBindingController>)this.controlsBindingControllerImplProvider, (Provider<ControlsListingController>)this.controlsListingControllerImplProvider, this.broadcastDispatcherProvider, absentJdkOptionalProvider, this.dumpManagerProvider)));
        this.controlsProviderSelectorActivityProvider = ControlsProviderSelectorActivity_Factory.create(this.provideMainExecutorProvider, this.provideBackgroundExecutorProvider, (Provider<ControlsListingController>)this.controlsListingControllerImplProvider, (Provider<ControlsController>)this.controlsControllerImplProvider, this.broadcastDispatcherProvider);
        this.controlsFavoritingActivityProvider = ControlsFavoritingActivity_Factory.create(this.provideMainExecutorProvider, this.controlsControllerImplProvider, (Provider<ControlsListingController>)this.controlsListingControllerImplProvider, this.broadcastDispatcherProvider);
        this.controlsRequestDialogProvider = ControlsRequestDialog_Factory.create((Provider<ControlsController>)this.controlsControllerImplProvider, this.broadcastDispatcherProvider, (Provider<ControlsListingController>)this.controlsListingControllerImplProvider);
        final MapProviderFactory.Builder<Class<TunerActivity>, Object> builder2 = MapProviderFactory.builder(9);
        builder2.put(TunerActivity.class, (Provider<Object>)TunerActivity_Factory.create());
        builder2.put((Class<TunerActivity>)ForegroundServicesDialog.class, (Provider<Object>)ForegroundServicesDialog_Factory.create());
        builder2.put((Class<TunerActivity>)WorkLockActivity.class, (Provider<Object>)this.workLockActivityProvider);
        builder2.put((Class<TunerActivity>)BrightnessDialog.class, (Provider<Object>)this.brightnessDialogProvider);
        builder2.put((Class<TunerActivity>)ScreenRecordDialog.class, (Provider<Object>)this.screenRecordDialogProvider);
        builder2.put((Class<TunerActivity>)BubbleOverflowActivity.class, (Provider<Object>)this.bubbleOverflowActivityProvider);
        builder2.put((Class<TunerActivity>)ControlsProviderSelectorActivity.class, (Provider<Object>)this.controlsProviderSelectorActivityProvider);
        builder2.put((Class<TunerActivity>)ControlsFavoritingActivity.class, (Provider<Object>)this.controlsFavoritingActivityProvider);
        builder2.put((Class<TunerActivity>)ControlsRequestDialog.class, (Provider<Object>)this.controlsRequestDialogProvider);
        this.mapOfClassOfAndProviderOfActivityProvider = (Provider<Map<Class<?>, Provider<Activity>>>)builder2.build();
        final DozeFactory_Factory create6 = DozeFactory_Factory.create((Provider<FalsingManager>)this.falsingManagerProxyProvider, this.dozeLogProvider, this.dozeParametersProvider, (Provider<BatteryController>)this.batteryControllerImplProvider, this.asyncSensorManagerProvider, this.provideAlarmManagerProvider, this.wakefulnessLifecycleProvider, this.keyguardUpdateMonitorProvider, (Provider<DockManager>)this.dockManagerImplProvider, SystemServicesModule_ProvideIWallPaperManagerFactory.create(), this.proximitySensorProvider, this.builderProvider2, this.provideHandlerProvider, this.biometricUnlockControllerProvider, this.broadcastDispatcherProvider, this.dozeServiceHostProvider);
        this.dozeFactoryProvider = create6;
        this.dozeServiceProvider = DozeService_Factory.create(create6, this.providePluginManagerProvider);
        this.imageWallpaperProvider = ImageWallpaper_Factory.create(this.dozeParametersProvider);
        final Provider<KeyguardLifecyclesDispatcher> provider16 = DoubleCheck.provider(KeyguardLifecyclesDispatcher_Factory.create(this.screenLifecycleProvider, this.wakefulnessLifecycleProvider));
        this.keyguardLifecyclesDispatcherProvider = provider16;
        this.keyguardServiceProvider = KeyguardService_Factory.create(this.newKeyguardViewMediatorProvider, provider16);
        this.systemUIServiceProvider = SystemUIService_Factory.create(this.provideMainHandlerProvider, this.dumpManagerProvider);
        this.systemUIAuxiliaryDumpServiceProvider = SystemUIAuxiliaryDumpService_Factory.create(this.dumpManagerProvider);
        this.providerLayoutInflaterProvider = DoubleCheck.provider(DependencyProvider_ProviderLayoutInflaterFactory.create(builder.dependencyProvider, this.provideContextProvider));
        final ScreenshotNotificationsController_Factory create7 = ScreenshotNotificationsController_Factory.create(this.provideContextProvider, this.provideWindowManagerProvider);
        this.screenshotNotificationsControllerProvider = create7;
        this.globalScreenshotProvider = (Provider<GlobalScreenshot>)DoubleCheck.provider(GlobalScreenshot_Factory.create(this.provideContextProvider, this.provideResourcesProvider, this.providerLayoutInflaterProvider, create7));
        final Provider<GlobalScreenshotLegacy> provider17 = DoubleCheck.provider(GlobalScreenshotLegacy_Factory.create(this.provideContextProvider, this.provideResourcesProvider, this.providerLayoutInflaterProvider, this.screenshotNotificationsControllerProvider));
        this.globalScreenshotLegacyProvider = provider17;
        this.takeScreenshotServiceProvider = TakeScreenshotService_Factory.create(this.globalScreenshotProvider, provider17, this.provideUserManagerProvider);
        this.recordingServiceProvider = RecordingService_Factory.create(this.recordingControllerProvider);
        final MapProviderFactory.Builder<Class<DozeService>, Object> builder3 = MapProviderFactory.builder(7);
        builder3.put(DozeService.class, (Provider<Object>)this.dozeServiceProvider);
        builder3.put((Class<DozeService>)ImageWallpaper.class, (Provider<Object>)this.imageWallpaperProvider);
        builder3.put((Class<DozeService>)KeyguardService.class, (Provider<Object>)this.keyguardServiceProvider);
        builder3.put((Class<DozeService>)SystemUIService.class, (Provider<Object>)this.systemUIServiceProvider);
        builder3.put((Class<DozeService>)SystemUIAuxiliaryDumpService.class, (Provider<Object>)this.systemUIAuxiliaryDumpServiceProvider);
        builder3.put((Class<DozeService>)TakeScreenshotService.class, (Provider<Object>)this.takeScreenshotServiceProvider);
        builder3.put((Class<DozeService>)RecordingService.class, (Provider<Object>)this.recordingServiceProvider);
        this.mapOfClassOfAndProviderOfServiceProvider = (Provider<Map<Class<?>, Provider<Service>>>)builder3.build();
        this.authControllerProvider = DoubleCheck.provider(AuthController_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider));
        final Provider<String> provider18 = DoubleCheck.provider(SystemUIDefaultModule_ProvideLeakReportEmailFactory.create());
        this.provideLeakReportEmailProvider = provider18;
        final Provider<Object> provider19 = DoubleCheck.provider(LeakReporter_Factory.create(this.provideContextProvider, this.provideLeakDetectorProvider, provider18));
        this.leakReporterProvider = (Provider<LeakReporter>)provider19;
        final Provider<Object> provider20 = DoubleCheck.provider(GarbageMonitor_Factory.create(this.provideContextProvider, this.provideBgLooperProvider, this.provideLeakDetectorProvider, (Provider<LeakReporter>)provider19));
        this.garbageMonitorProvider = (Provider<GarbageMonitor>)provider20;
        this.serviceProvider = (Provider<GarbageMonitor.Service>)DoubleCheck.provider(GarbageMonitor_Service_Factory.create(this.provideContextProvider, (Provider<GarbageMonitor>)provider20));
    }
    
    private void initialize4(final Builder builder) {
        this.globalActionsComponentProvider = new DelegateFactory<GlobalActionsComponent>();
        this.provideVibratorProvider = DoubleCheck.provider(SystemServicesModule_ProvideVibratorFactory.create(this.provideContextProvider));
        final Provider<UiEventLogger> provider = DoubleCheck.provider(NotificationsModule_ProvideUiEventLoggerFactory.create());
        this.provideUiEventLoggerProvider = provider;
        final GlobalActionsDialog_Factory create = GlobalActionsDialog_Factory.create(this.provideContextProvider, (Provider<GlobalActions.GlobalActionsManager>)this.globalActionsComponentProvider, this.provideAudioManagerProvider, this.provideIDreamManagerProvider, this.provideDevicePolicyManagerProvider, this.provideLockPatternUtilsProvider, this.broadcastDispatcherProvider, this.provideConnectivityManagagerProvider, this.provideTelephonyManagerProvider, this.provideContentResolverProvider, this.provideVibratorProvider, this.provideResourcesProvider, this.provideConfigurationControllerProvider, (Provider<ActivityStarter>)this.activityStarterDelegateProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, this.provideUserManagerProvider, this.provideTrustManagerProvider, this.provideIActivityManagerProvider, this.provideTelecomManagerProvider, this.provideMetricsLoggerProvider, this.notificationShadeDepthControllerProvider, this.sysuiColorExtractorProvider, this.provideIStatusBarServiceProvider, this.blurUtilsProvider, this.notificationShadeWindowControllerProvider, (Provider<ControlsUiController>)this.controlsUiControllerImplProvider, this.provideIWindowManagerProvider, this.provideBackgroundExecutorProvider, (Provider<ControlsListingController>)this.controlsListingControllerImplProvider, (Provider<ControlsController>)this.controlsControllerImplProvider, provider);
        this.globalActionsDialogProvider = create;
        final GlobalActionsImpl_Factory create2 = GlobalActionsImpl_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider, create, this.blurUtilsProvider);
        this.globalActionsImplProvider = create2;
        ((DelegateFactory)this.globalActionsComponentProvider).setDelegatedProvider(this.globalActionsComponentProvider = (Provider<GlobalActionsComponent>)DoubleCheck.provider(GlobalActionsComponent_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider, (Provider<ExtensionController>)this.extensionControllerImplProvider, (Provider<GlobalActions>)create2)));
        this.instantAppNotifierProvider = DoubleCheck.provider(InstantAppNotifier_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider, this.provideUiBackgroundExecutorProvider, this.provideDividerProvider));
        this.latencyTesterProvider = DoubleCheck.provider(LatencyTester_Factory.create(this.provideContextProvider, this.biometricUnlockControllerProvider, this.providePowerManagerProvider, this.broadcastDispatcherProvider));
        this.powerUIProvider = DoubleCheck.provider(PowerUI_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider, this.provideCommandQueueProvider, this.provideStatusBarProvider));
        this.screenDecorationsProvider = DoubleCheck.provider(ScreenDecorations_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, this.broadcastDispatcherProvider, (Provider<TunerService>)this.tunerServiceImplProvider));
        this.shortcutKeyDispatcherProvider = DoubleCheck.provider(ShortcutKeyDispatcher_Factory.create(this.provideContextProvider, this.provideDividerProvider, this.provideRecentsProvider));
        this.sizeCompatModeActivityControllerProvider = DoubleCheck.provider(SizeCompatModeActivityController_Factory.create(this.provideContextProvider, this.provideActivityManagerWrapperProvider, this.provideCommandQueueProvider));
        this.sliceBroadcastRelayHandlerProvider = DoubleCheck.provider(SliceBroadcastRelayHandler_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider));
        this.systemActionsProvider = DoubleCheck.provider(SystemActions_Factory.create(this.provideContextProvider));
        this.themeOverlayControllerProvider = DoubleCheck.provider(ThemeOverlayController_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider, this.provideBgHandlerProvider));
        this.toastUIProvider = DoubleCheck.provider(ToastUI_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider));
        this.tvStatusBarProvider = DoubleCheck.provider(TvStatusBar_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider));
        this.volumeUIProvider = DoubleCheck.provider(VolumeUI_Factory.create(this.provideContextProvider, this.volumeDialogComponentProvider));
        this.windowMagnificationProvider = DoubleCheck.provider(WindowMagnification_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider));
        final MapProviderFactory.Builder<Class<AuthController>, AuthController> builder2 = MapProviderFactory.builder(21);
        builder2.put(AuthController.class, this.authControllerProvider);
        builder2.put((Class<AuthController>)Divider.class, (Provider<AuthController>)this.provideDividerProvider);
        builder2.put((Class<AuthController>)GarbageMonitor.Service.class, (Provider<AuthController>)this.serviceProvider);
        builder2.put((Class<AuthController>)GlobalActionsComponent.class, (Provider<AuthController>)this.globalActionsComponentProvider);
        builder2.put((Class<AuthController>)InstantAppNotifier.class, (Provider<AuthController>)this.instantAppNotifierProvider);
        builder2.put((Class<AuthController>)KeyguardViewMediator.class, (Provider<AuthController>)this.newKeyguardViewMediatorProvider);
        builder2.put((Class<AuthController>)LatencyTester.class, (Provider<AuthController>)this.latencyTesterProvider);
        builder2.put((Class<AuthController>)PipUI.class, (Provider<AuthController>)this.pipUIProvider);
        builder2.put((Class<AuthController>)PowerUI.class, (Provider<AuthController>)this.powerUIProvider);
        builder2.put((Class<AuthController>)Recents.class, (Provider<AuthController>)this.provideRecentsProvider);
        builder2.put((Class<AuthController>)ScreenDecorations.class, (Provider<AuthController>)this.screenDecorationsProvider);
        builder2.put((Class<AuthController>)ShortcutKeyDispatcher.class, (Provider<AuthController>)this.shortcutKeyDispatcherProvider);
        builder2.put((Class<AuthController>)SizeCompatModeActivityController.class, (Provider<AuthController>)this.sizeCompatModeActivityControllerProvider);
        builder2.put((Class<AuthController>)SliceBroadcastRelayHandler.class, (Provider<AuthController>)this.sliceBroadcastRelayHandlerProvider);
        builder2.put((Class<AuthController>)StatusBar.class, (Provider<AuthController>)this.provideStatusBarProvider);
        builder2.put((Class<AuthController>)SystemActions.class, (Provider<AuthController>)this.systemActionsProvider);
        builder2.put((Class<AuthController>)ThemeOverlayController.class, (Provider<AuthController>)this.themeOverlayControllerProvider);
        builder2.put((Class<AuthController>)ToastUI.class, (Provider<AuthController>)this.toastUIProvider);
        builder2.put((Class<AuthController>)TvStatusBar.class, (Provider<AuthController>)this.tvStatusBarProvider);
        builder2.put((Class<AuthController>)VolumeUI.class, (Provider<AuthController>)this.volumeUIProvider);
        builder2.put((Class<AuthController>)WindowMagnification.class, (Provider<AuthController>)this.windowMagnificationProvider);
        this.mapOfClassOfAndProviderOfSystemUIProvider = (Provider<Map<Class<?>, Provider<SystemUI>>>)builder2.build();
        this.overviewProxyRecentsImplProvider = DoubleCheck.provider(OverviewProxyRecentsImpl_Factory.create(this.optionalOfLazyOfStatusBarProvider, this.optionalOfDividerProvider));
        final MapProviderFactory.Builder<Class<OverviewProxyRecentsImpl>, OverviewProxyRecentsImpl> builder3 = MapProviderFactory.builder(1);
        builder3.put(OverviewProxyRecentsImpl.class, this.overviewProxyRecentsImplProvider);
        this.mapOfClassOfAndProviderOfRecentsImplementationProvider = (Provider<Map<Class<?>, Provider<RecentsImplementation>>>)builder3.build();
        this.actionProxyReceiverProvider = GlobalScreenshot_ActionProxyReceiver_Factory.create(this.optionalOfLazyOfStatusBarProvider);
        final MapProviderFactory.Builder<Class<GlobalScreenshot.ActionProxyReceiver>, BroadcastReceiver> builder4 = MapProviderFactory.builder(1);
        builder4.put(GlobalScreenshot.ActionProxyReceiver.class, (Provider<BroadcastReceiver>)this.actionProxyReceiverProvider);
        final MapProviderFactory<Class<GlobalScreenshot.ActionProxyReceiver>, BroadcastReceiver> build = builder4.build();
        this.mapOfClassOfAndProviderOfBroadcastReceiverProvider = (Provider<Map<Class<?>, Provider<BroadcastReceiver>>>)build;
        ((DelegateFactory)this.contextComponentResolverProvider).setDelegatedProvider(this.contextComponentResolverProvider = (Provider<ContextComponentResolver>)DoubleCheck.provider(ContextComponentResolver_Factory.create(this.mapOfClassOfAndProviderOfActivityProvider, this.mapOfClassOfAndProviderOfServiceProvider, this.mapOfClassOfAndProviderOfSystemUIProvider, this.mapOfClassOfAndProviderOfRecentsImplementationProvider, (Provider<Map<Class<?>, Provider<BroadcastReceiver>>>)build)));
        this.provideAllowNotificationLongPressProvider = DoubleCheck.provider(SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory.create());
        this.flashlightControllerImplProvider = DoubleCheck.provider(FlashlightControllerImpl_Factory.create(this.provideContextProvider));
        this.provideNightDisplayListenerProvider = DoubleCheck.provider(DependencyProvider_ProvideNightDisplayListenerFactory.create(builder.dependencyProvider, this.provideContextProvider, this.provideBgHandlerProvider));
        this.managedProfileControllerImplProvider = DoubleCheck.provider(ManagedProfileControllerImpl_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider));
        this.securityControllerImplProvider = DoubleCheck.provider(SecurityControllerImpl_Factory.create(this.provideContextProvider, this.provideBgHandlerProvider, this.broadcastDispatcherProvider, this.provideBackgroundExecutorProvider));
        this.statusBarWindowControllerProvider = DoubleCheck.provider(StatusBarWindowController_Factory.create(this.provideContextProvider, this.provideWindowManagerProvider, this.superStatusBarViewFactoryProvider, this.provideResourcesProvider));
        this.fragmentServiceProvider = DoubleCheck.provider(FragmentService_Factory.create(this.systemUIRootComponentProvider, this.provideConfigurationControllerProvider));
        this.accessibilityManagerWrapperProvider = DoubleCheck.provider(AccessibilityManagerWrapper_Factory.create(this.provideContextProvider));
        this.tunablePaddingServiceProvider = DoubleCheck.provider(TunablePadding_TunablePaddingService_Factory.create((Provider<TunerService>)this.tunerServiceImplProvider));
        this.uiOffloadThreadProvider = DoubleCheck.provider(UiOffloadThread_Factory.create());
        this.powerNotificationWarningsProvider = DoubleCheck.provider(PowerNotificationWarnings_Factory.create(this.provideContextProvider, (Provider<ActivityStarter>)this.activityStarterDelegateProvider));
        this.provideNotificationBlockingHelperManagerProvider = DoubleCheck.provider(NotificationsModule_ProvideNotificationBlockingHelperManagerFactory.create(this.provideContextProvider, this.provideNotificationGutsManagerProvider, this.provideNotificationEntryManagerProvider, this.provideMetricsLoggerProvider));
        this.provideSensorPrivacyManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideSensorPrivacyManagerFactory.create(this.provideContextProvider));
        this.foregroundServiceNotificationListenerProvider = DoubleCheck.provider(ForegroundServiceNotificationListener_Factory.create(this.provideContextProvider, this.foregroundServiceControllerProvider, this.provideNotificationEntryManagerProvider, this.notifPipelineProvider));
        this.clockManagerProvider = DoubleCheck.provider(ClockManager_Factory.create(this.provideContextProvider, this.injectionInflationControllerProvider, this.providePluginManagerProvider, this.sysuiColorExtractorProvider, (Provider<DockManager>)this.dockManagerImplProvider, this.broadcastDispatcherProvider));
        this.provideDevicePolicyManagerWrapperProvider = DoubleCheck.provider(DependencyProvider_ProvideDevicePolicyManagerWrapperFactory.create(builder.dependencyProvider));
        this.channelEditorDialogControllerProvider = DoubleCheck.provider(ChannelEditorDialogController_Factory.create(this.provideContextProvider, this.provideINotificationManagerProvider));
        this.keyguardSecurityModelProvider = DoubleCheck.provider(KeyguardSecurityModel_Factory.create(this.provideContextProvider));
        final DelegateFactory<QSHost> qsTileHostProvider = new DelegateFactory<QSHost>();
        this.qSTileHostProvider = (Provider<QSTileHost>)qsTileHostProvider;
        this.wifiTileProvider = WifiTile_Factory.create(qsTileHostProvider, (Provider<NetworkController>)this.networkControllerImplProvider, (Provider<ActivityStarter>)this.activityStarterDelegateProvider);
        this.bluetoothTileProvider = BluetoothTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, (Provider<BluetoothController>)this.bluetoothControllerImplProvider, (Provider<ActivityStarter>)this.activityStarterDelegateProvider);
        this.cellularTileProvider = CellularTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, (Provider<NetworkController>)this.networkControllerImplProvider, (Provider<ActivityStarter>)this.activityStarterDelegateProvider);
        this.dndTileProvider = DndTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, (Provider<ZenModeController>)this.zenModeControllerImplProvider, (Provider<ActivityStarter>)this.activityStarterDelegateProvider, this.broadcastDispatcherProvider, this.provideSharePreferencesProvider);
        this.colorInversionTileProvider = ColorInversionTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider);
        this.airplaneModeTileProvider = AirplaneModeTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, (Provider<ActivityStarter>)this.activityStarterDelegateProvider, this.broadcastDispatcherProvider);
        this.workModeTileProvider = WorkModeTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, (Provider<ManagedProfileController>)this.managedProfileControllerImplProvider);
        this.rotationLockTileProvider = RotationLockTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, (Provider<RotationLockController>)this.rotationLockControllerImplProvider);
        this.flashlightTileProvider = FlashlightTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, (Provider<FlashlightController>)this.flashlightControllerImplProvider);
        this.locationTileProvider = LocationTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, (Provider<LocationController>)this.locationControllerImplProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, (Provider<ActivityStarter>)this.activityStarterDelegateProvider);
        this.castTileProvider = CastTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, (Provider<CastController>)this.castControllerImplProvider, (Provider<KeyguardStateController>)this.keyguardStateControllerImplProvider, (Provider<NetworkController>)this.networkControllerImplProvider, (Provider<ActivityStarter>)this.activityStarterDelegateProvider);
        this.hotspotTileProvider = HotspotTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, (Provider<HotspotController>)this.hotspotControllerImplProvider, this.provideDataSaverControllerProvider);
        this.userTileProvider = UserTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, this.userSwitcherControllerProvider, (Provider<UserInfoController>)this.userInfoControllerImplProvider);
        this.batterySaverTileProvider = BatterySaverTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, (Provider<BatteryController>)this.batteryControllerImplProvider);
        this.dataSaverTileProvider = DataSaverTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, (Provider<NetworkController>)this.networkControllerImplProvider);
        this.nightDisplayTileProvider = NightDisplayTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider);
        this.nfcTileProvider = NfcTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, this.broadcastDispatcherProvider);
        this.memoryTileProvider = GarbageMonitor_MemoryTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, this.garbageMonitorProvider, (Provider<ActivityStarter>)this.activityStarterDelegateProvider);
        this.uiModeNightTileProvider = UiModeNightTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, this.provideConfigurationControllerProvider, (Provider<BatteryController>)this.batteryControllerImplProvider);
        final ScreenRecordTile_Factory create3 = ScreenRecordTile_Factory.create((Provider<QSHost>)this.qSTileHostProvider, this.recordingControllerProvider);
        this.screenRecordTileProvider = create3;
        this.qSFactoryImplProvider = (Provider<QSFactoryImpl>)DoubleCheck.provider(QSFactoryImpl_Factory.create((Provider<QSHost>)this.qSTileHostProvider, this.wifiTileProvider, this.bluetoothTileProvider, this.cellularTileProvider, this.dndTileProvider, this.colorInversionTileProvider, this.airplaneModeTileProvider, this.workModeTileProvider, this.rotationLockTileProvider, this.flashlightTileProvider, this.locationTileProvider, this.castTileProvider, this.hotspotTileProvider, this.userTileProvider, this.batterySaverTileProvider, this.dataSaverTileProvider, this.nightDisplayTileProvider, this.nfcTileProvider, this.memoryTileProvider, this.uiModeNightTileProvider, create3));
        final AutoAddTracker_Factory create4 = AutoAddTracker_Factory.create(this.provideContextProvider);
        this.autoAddTrackerProvider = create4;
        this.autoTileManagerProvider = AutoTileManager_Factory.create(this.provideContextProvider, create4, this.qSTileHostProvider, this.provideBgHandlerProvider, (Provider<HotspotController>)this.hotspotControllerImplProvider, this.provideDataSaverControllerProvider, (Provider<ManagedProfileController>)this.managedProfileControllerImplProvider, this.provideNightDisplayListenerProvider, (Provider<CastController>)this.castControllerImplProvider);
        this.optionalOfStatusBarProvider = (Provider<Optional<StatusBar>>)of((Provider<Object>)this.provideStatusBarProvider);
        final Provider<LogBuffer> provider2 = DoubleCheck.provider(LogModule_ProvideQuickSettingsLogBufferFactory.create(this.provideLogcatEchoTrackerProvider, this.dumpManagerProvider));
        this.provideQuickSettingsLogBufferProvider = provider2;
        final QSLogger_Factory create5 = QSLogger_Factory.create(provider2);
        this.qSLoggerProvider = create5;
        ((DelegateFactory)this.qSTileHostProvider).setDelegatedProvider(this.qSTileHostProvider = (Provider<QSTileHost>)DoubleCheck.provider(QSTileHost_Factory.create(this.provideContextProvider, (Provider<StatusBarIconController>)this.statusBarIconControllerImplProvider, (Provider<QSFactory>)this.qSFactoryImplProvider, this.provideMainHandlerProvider, this.provideBgLooperProvider, this.providePluginManagerProvider, (Provider<TunerService>)this.tunerServiceImplProvider, this.autoTileManagerProvider, this.dumpManagerProvider, this.broadcastDispatcherProvider, this.optionalOfStatusBarProvider, create5)));
        this.contextHolder = builder.contextHolder;
        final Provider<PackageManager> provider3 = DoubleCheck.provider(SystemServicesModule_ProvidePackageManagerFactory.create(this.provideContextProvider));
        this.providePackageManagerProvider = provider3;
        final Provider<Object> provider4 = DoubleCheck.provider(PeopleHubDataSourceImpl_Factory.create(this.provideNotificationEntryManagerProvider, (Provider<NotificationPersonExtractor>)this.notificationPersonExtractorPluginBoundaryProvider, this.provideUserManagerProvider, this.provideLauncherAppsProvider, provider3, this.provideContextProvider, this.provideNotificationListenerProvider, this.provideBackgroundExecutorProvider, this.provideMainExecutorProvider, (Provider<NotificationLockscreenUserManager>)this.notificationLockscreenUserManagerImplProvider, (Provider<PeopleNotificationIdentifier>)this.peopleNotificationIdentifierImplProvider));
        this.peopleHubDataSourceImplProvider = (Provider<PeopleHubDataSourceImpl>)provider4;
        final Provider<Object> provider5 = DoubleCheck.provider(PeopleHubViewModelFactoryDataSourceImpl_Factory.create((Provider<ActivityStarter>)this.activityStarterDelegateProvider, (Provider<DataSource<Object>>)provider4));
        this.peopleHubViewModelFactoryDataSourceImplProvider = (Provider<PeopleHubViewModelFactoryDataSourceImpl>)provider5;
        this.peopleHubViewAdapterImplProvider = (Provider<PeopleHubViewAdapterImpl>)DoubleCheck.provider(PeopleHubViewAdapterImpl_Factory.create((Provider<DataSource<Object>>)provider5));
        this.provideLatencyTrackerProvider = DoubleCheck.provider(SystemServicesModule_ProvideLatencyTrackerFactory.create(this.provideContextProvider));
        this.provideActivityManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideActivityManagerFactory.create(this.provideContextProvider));
    }
    
    private KeyguardSliceProvider injectKeyguardSliceProvider(final KeyguardSliceProvider keyguardSliceProvider) {
        KeyguardSliceProvider_MembersInjector.injectMDozeParameters(keyguardSliceProvider, this.dozeParametersProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMZenModeController(keyguardSliceProvider, this.zenModeControllerImplProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMNextAlarmController(keyguardSliceProvider, this.nextAlarmControllerImplProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMAlarmManager(keyguardSliceProvider, this.provideAlarmManagerProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMContentResolver(keyguardSliceProvider, this.provideContentResolverProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMMediaManager(keyguardSliceProvider, this.provideNotificationMediaManagerProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMStatusBarStateController(keyguardSliceProvider, this.statusBarStateControllerImplProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMKeyguardBypassController(keyguardSliceProvider, this.keyguardBypassControllerProvider.get());
        return keyguardSliceProvider;
    }
    
    private SystemUIAppComponentFactory injectSystemUIAppComponentFactory(final SystemUIAppComponentFactory systemUIAppComponentFactory) {
        SystemUIAppComponentFactory_MembersInjector.injectMComponentHelper(systemUIAppComponentFactory, this.contextComponentResolverProvider.get());
        return systemUIAppComponentFactory;
    }
    
    @Override
    public Dependency.DependencyInjector createDependency() {
        return new DependencyInjectorImpl();
    }
    
    @Override
    public DumpManager createDumpManager() {
        return this.dumpManagerProvider.get();
    }
    
    @Override
    public FragmentService.FragmentCreator createFragmentCreator() {
        return new FragmentCreatorImpl();
    }
    
    @Override
    public InjectionInflationController.ViewCreator createViewCreator() {
        return new ViewCreatorImpl();
    }
    
    @Override
    public ConfigurationController getConfigurationController() {
        return this.provideConfigurationControllerProvider.get();
    }
    
    @Override
    public ContextComponentHelper getContextComponentHelper() {
        return this.contextComponentResolverProvider.get();
    }
    
    @Override
    public InitController getInitController() {
        return this.initControllerProvider.get();
    }
    
    @Override
    public void inject(final ContentProvider contentProvider) {
    }
    
    @Override
    public void inject(final SystemUIAppComponentFactory systemUIAppComponentFactory) {
        this.injectSystemUIAppComponentFactory(systemUIAppComponentFactory);
    }
    
    @Override
    public void inject(final KeyguardSliceProvider keyguardSliceProvider) {
        this.injectKeyguardSliceProvider(keyguardSliceProvider);
    }
    
    @Override
    public BootCompleteCacheImpl provideBootCacheImpl() {
        return this.bootCompleteCacheImplProvider.get();
    }
    
    public static final class Builder
    {
        private SystemUIFactory.ContextHolder contextHolder;
        private DependencyProvider dependencyProvider;
        
        private Builder() {
        }
        
        public SystemUIRootComponent build() {
            if (this.contextHolder != null) {
                if (this.dependencyProvider == null) {
                    this.dependencyProvider = new DependencyProvider();
                }
                return new DaggerSystemUIRootComponent(this, null);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(SystemUIFactory.ContextHolder.class.getCanonicalName());
            sb.append(" must be set");
            throw new IllegalStateException(sb.toString());
        }
        
        public Builder contextHolder(final SystemUIFactory.ContextHolder contextHolder) {
            Preconditions.checkNotNull(contextHolder);
            this.contextHolder = contextHolder;
            return this;
        }
        
        public Builder dependencyProvider(final DependencyProvider dependencyProvider) {
            Preconditions.checkNotNull(dependencyProvider);
            this.dependencyProvider = dependencyProvider;
            return this;
        }
    }
    
    private final class DependencyInjectorImpl implements DependencyInjector
    {
        private Dependency injectDependency(final Dependency dependency) {
            Dependency_MembersInjector.injectMDumpManager(dependency, DaggerSystemUIRootComponent.this.dumpManagerProvider.get());
            Dependency_MembersInjector.injectMActivityStarter(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.activityStarterDelegateProvider));
            Dependency_MembersInjector.injectMBroadcastDispatcher(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.broadcastDispatcherProvider));
            Dependency_MembersInjector.injectMAsyncSensorManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.asyncSensorManagerProvider));
            Dependency_MembersInjector.injectMBluetoothController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.bluetoothControllerImplProvider));
            Dependency_MembersInjector.injectMLocationController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.locationControllerImplProvider));
            Dependency_MembersInjector.injectMRotationLockController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.rotationLockControllerImplProvider));
            Dependency_MembersInjector.injectMNetworkController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.networkControllerImplProvider));
            Dependency_MembersInjector.injectMZenModeController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.zenModeControllerImplProvider));
            Dependency_MembersInjector.injectMHotspotController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.hotspotControllerImplProvider));
            Dependency_MembersInjector.injectMCastController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.castControllerImplProvider));
            Dependency_MembersInjector.injectMFlashlightController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.flashlightControllerImplProvider));
            Dependency_MembersInjector.injectMUserSwitcherController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.userSwitcherControllerProvider));
            Dependency_MembersInjector.injectMUserInfoController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.userInfoControllerImplProvider));
            Dependency_MembersInjector.injectMKeyguardMonitor(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.keyguardStateControllerImplProvider));
            Dependency_MembersInjector.injectMKeyguardUpdateMonitor(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.keyguardUpdateMonitorProvider));
            Dependency_MembersInjector.injectMBatteryController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.batteryControllerImplProvider));
            Dependency_MembersInjector.injectMNightDisplayListener(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideNightDisplayListenerProvider));
            Dependency_MembersInjector.injectMManagedProfileController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.managedProfileControllerImplProvider));
            Dependency_MembersInjector.injectMNextAlarmController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.nextAlarmControllerImplProvider));
            Dependency_MembersInjector.injectMDataSaverController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideDataSaverControllerProvider));
            Dependency_MembersInjector.injectMAccessibilityController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.accessibilityControllerProvider));
            Dependency_MembersInjector.injectMDeviceProvisionedController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.deviceProvisionedControllerImplProvider));
            Dependency_MembersInjector.injectMPluginManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.providePluginManagerProvider));
            Dependency_MembersInjector.injectMAssistManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.assistManagerProvider));
            Dependency_MembersInjector.injectMSecurityController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.securityControllerImplProvider));
            Dependency_MembersInjector.injectMLeakDetector(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideLeakDetectorProvider));
            Dependency_MembersInjector.injectMLeakReporter(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.leakReporterProvider));
            Dependency_MembersInjector.injectMGarbageMonitor(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.garbageMonitorProvider));
            Dependency_MembersInjector.injectMTunerService(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.tunerServiceImplProvider));
            Dependency_MembersInjector.injectMNotificationShadeWindowController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationShadeWindowControllerProvider));
            Dependency_MembersInjector.injectMTempStatusBarWindowController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.statusBarWindowControllerProvider));
            Dependency_MembersInjector.injectMDarkIconDispatcher(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.darkIconDispatcherImplProvider));
            Dependency_MembersInjector.injectMConfigurationController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideConfigurationControllerProvider));
            Dependency_MembersInjector.injectMStatusBarIconController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.statusBarIconControllerImplProvider));
            Dependency_MembersInjector.injectMScreenLifecycle(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.screenLifecycleProvider));
            Dependency_MembersInjector.injectMWakefulnessLifecycle(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.wakefulnessLifecycleProvider));
            Dependency_MembersInjector.injectMFragmentService(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.fragmentServiceProvider));
            Dependency_MembersInjector.injectMExtensionController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.extensionControllerImplProvider));
            Dependency_MembersInjector.injectMPluginDependencyProvider(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.pluginDependencyProvider));
            Dependency_MembersInjector.injectMLocalBluetoothManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideLocalBluetoothControllerProvider));
            Dependency_MembersInjector.injectMVolumeDialogController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.volumeDialogControllerImplProvider));
            Dependency_MembersInjector.injectMMetricsLogger(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideMetricsLoggerProvider));
            Dependency_MembersInjector.injectMAccessibilityManagerWrapper(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.accessibilityManagerWrapperProvider));
            Dependency_MembersInjector.injectMSysuiColorExtractor(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.sysuiColorExtractorProvider));
            Dependency_MembersInjector.injectMTunablePaddingService(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.tunablePaddingServiceProvider));
            Dependency_MembersInjector.injectMForegroundServiceController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.foregroundServiceControllerProvider));
            Dependency_MembersInjector.injectMUiOffloadThread(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.uiOffloadThreadProvider));
            Dependency_MembersInjector.injectMWarningsUI(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.powerNotificationWarningsProvider));
            Dependency_MembersInjector.injectMLightBarController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.lightBarControllerProvider));
            Dependency_MembersInjector.injectMIWindowManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideIWindowManagerProvider));
            Dependency_MembersInjector.injectMOverviewProxyService(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.overviewProxyServiceProvider));
            Dependency_MembersInjector.injectMNavBarModeController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.navigationModeControllerProvider));
            Dependency_MembersInjector.injectMEnhancedEstimates(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.enhancedEstimatesImplProvider));
            Dependency_MembersInjector.injectMVibratorHelper(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.vibratorHelperProvider));
            Dependency_MembersInjector.injectMIStatusBarService(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideIStatusBarServiceProvider));
            Dependency_MembersInjector.injectMDisplayMetrics(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideDisplayMetricsProvider));
            Dependency_MembersInjector.injectMLockscreenGestureLogger(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.lockscreenGestureLoggerProvider));
            Dependency_MembersInjector.injectMKeyguardEnvironment(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.keyguardEnvironmentImplProvider));
            Dependency_MembersInjector.injectMShadeController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.shadeControllerImplProvider));
            Dependency_MembersInjector.injectMNotificationRemoteInputManagerCallback(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.statusBarRemoteInputCallbackProvider));
            Dependency_MembersInjector.injectMAppOpsController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.appOpsControllerImplProvider));
            Dependency_MembersInjector.injectMNavigationBarController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideNavigationBarControllerProvider));
            Dependency_MembersInjector.injectMStatusBarStateController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider));
            Dependency_MembersInjector.injectMNotificationLockscreenUserManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationLockscreenUserManagerImplProvider));
            Dependency_MembersInjector.injectMNotificationGroupAlertTransferHelper(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideNotificationGroupAlertTransferHelperProvider));
            Dependency_MembersInjector.injectMNotificationGroupManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationGroupManagerProvider));
            Dependency_MembersInjector.injectMVisualStabilityManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideVisualStabilityManagerProvider));
            Dependency_MembersInjector.injectMNotificationGutsManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideNotificationGutsManagerProvider));
            Dependency_MembersInjector.injectMNotificationMediaManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideNotificationMediaManagerProvider));
            Dependency_MembersInjector.injectMNotificationBlockingHelperManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideNotificationBlockingHelperManagerProvider));
            Dependency_MembersInjector.injectMNotificationRemoteInputManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideNotificationRemoteInputManagerProvider));
            Dependency_MembersInjector.injectMSmartReplyConstants(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.smartReplyConstantsProvider));
            Dependency_MembersInjector.injectMNotificationListener(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideNotificationListenerProvider));
            Dependency_MembersInjector.injectMNotificationLogger(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideNotificationLoggerProvider));
            Dependency_MembersInjector.injectMNotificationViewHierarchyManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideNotificationViewHierarchyManagerProvider));
            Dependency_MembersInjector.injectMNotificationFilter(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationFilterProvider));
            Dependency_MembersInjector.injectMKeyguardDismissUtil(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.keyguardDismissUtilProvider));
            Dependency_MembersInjector.injectMSmartReplyController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideSmartReplyControllerProvider));
            Dependency_MembersInjector.injectMRemoteInputQuickSettingsDisabler(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.remoteInputQuickSettingsDisablerProvider));
            Dependency_MembersInjector.injectMBubbleController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.newBubbleControllerProvider));
            Dependency_MembersInjector.injectMNotificationEntryManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideNotificationEntryManagerProvider));
            Dependency_MembersInjector.injectMNotificationAlertingManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideNotificationAlertingManagerProvider));
            Dependency_MembersInjector.injectMSensorPrivacyManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideSensorPrivacyManagerProvider));
            Dependency_MembersInjector.injectMAutoHideController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideAutoHideControllerProvider));
            Dependency_MembersInjector.injectMForegroundServiceNotificationListener(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.foregroundServiceNotificationListenerProvider));
            Dependency_MembersInjector.injectMBgLooper(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideBgLooperProvider));
            Dependency_MembersInjector.injectMBgHandler(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideBgHandlerProvider));
            Dependency_MembersInjector.injectMMainLooper(dependency, DoubleCheck.lazy(ConcurrencyModule_ProvideMainLooperFactory.create()));
            Dependency_MembersInjector.injectMMainHandler(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideMainHandlerProvider));
            Dependency_MembersInjector.injectMTimeTickHandler(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideTimeTickHandlerProvider));
            Dependency_MembersInjector.injectMLeakReportEmail(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideLeakReportEmailProvider));
            Dependency_MembersInjector.injectMClockManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.clockManagerProvider));
            Dependency_MembersInjector.injectMActivityManagerWrapper(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideActivityManagerWrapperProvider));
            Dependency_MembersInjector.injectMDevicePolicyManagerWrapper(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideDevicePolicyManagerWrapperProvider));
            Dependency_MembersInjector.injectMPackageManagerWrapper(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.providePackageManagerWrapperProvider));
            Dependency_MembersInjector.injectMSensorPrivacyController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.sensorPrivacyControllerImplProvider));
            Dependency_MembersInjector.injectMDockManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.dockManagerImplProvider));
            Dependency_MembersInjector.injectMChannelEditorDialogController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.channelEditorDialogControllerProvider));
            Dependency_MembersInjector.injectMINotificationManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideINotificationManagerProvider));
            Dependency_MembersInjector.injectMSysUiStateFlagsContainer(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideSysUiStateProvider));
            Dependency_MembersInjector.injectMAlarmManager(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideAlarmManagerProvider));
            Dependency_MembersInjector.injectMKeyguardSecurityModel(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.keyguardSecurityModelProvider));
            Dependency_MembersInjector.injectMDozeParameters(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.dozeParametersProvider));
            Dependency_MembersInjector.injectMWallpaperManager(dependency, DoubleCheck.lazy(SystemServicesModule_ProvideIWallPaperManagerFactory.create()));
            Dependency_MembersInjector.injectMCommandQueue(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideCommandQueueProvider));
            Dependency_MembersInjector.injectMRecents(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideRecentsProvider));
            Dependency_MembersInjector.injectMStatusBar(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideStatusBarProvider));
            Dependency_MembersInjector.injectMDisplayController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.displayControllerProvider));
            Dependency_MembersInjector.injectMSystemWindows(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.systemWindowsProvider));
            Dependency_MembersInjector.injectMDisplayImeController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.displayImeControllerProvider));
            Dependency_MembersInjector.injectMRecordingController(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.recordingControllerProvider));
            Dependency_MembersInjector.injectMProtoTracer(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.protoTracerProvider));
            Dependency_MembersInjector.injectMDivider(dependency, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideDividerProvider));
            return dependency;
        }
        
        @Override
        public void createSystemUI(final Dependency dependency) {
            this.injectDependency(dependency);
        }
    }
    
    private final class ExpandableNotificationRowComponentBuilder implements ExpandableNotificationRowComponent.Builder
    {
        private ExpandableNotificationRow expandableNotificationRow;
        private NotificationRowContentBinder.InflationCallback inflationCallback;
        private NotificationEntry notificationEntry;
        private Runnable onDismissRunnable;
        private ExpandableNotificationRow.OnExpandClickListener onExpandClickListener;
        private RowContentBindStage rowContentBindStage;
        
        @Override
        public ExpandableNotificationRowComponent build() {
            if (this.expandableNotificationRow == null) {
                final StringBuilder sb = new StringBuilder();
                sb.append(ExpandableNotificationRow.class.getCanonicalName());
                sb.append(" must be set");
                throw new IllegalStateException(sb.toString());
            }
            if (this.notificationEntry == null) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(NotificationEntry.class.getCanonicalName());
                sb2.append(" must be set");
                throw new IllegalStateException(sb2.toString());
            }
            if (this.onDismissRunnable == null) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append(Runnable.class.getCanonicalName());
                sb3.append(" must be set");
                throw new IllegalStateException(sb3.toString());
            }
            if (this.rowContentBindStage == null) {
                final StringBuilder sb4 = new StringBuilder();
                sb4.append(RowContentBindStage.class.getCanonicalName());
                sb4.append(" must be set");
                throw new IllegalStateException(sb4.toString());
            }
            if (this.inflationCallback == null) {
                final StringBuilder sb5 = new StringBuilder();
                sb5.append(NotificationRowContentBinder.InflationCallback.class.getCanonicalName());
                sb5.append(" must be set");
                throw new IllegalStateException(sb5.toString());
            }
            if (this.onExpandClickListener != null) {
                return new ExpandableNotificationRowComponentImpl(this);
            }
            final StringBuilder sb6 = new StringBuilder();
            sb6.append(ExpandableNotificationRow.OnExpandClickListener.class.getCanonicalName());
            sb6.append(" must be set");
            throw new IllegalStateException(sb6.toString());
        }
        
        public ExpandableNotificationRowComponentBuilder expandableNotificationRow(final ExpandableNotificationRow expandableNotificationRow) {
            Preconditions.checkNotNull(expandableNotificationRow);
            this.expandableNotificationRow = expandableNotificationRow;
            return this;
        }
        
        public ExpandableNotificationRowComponentBuilder inflationCallback(final NotificationRowContentBinder.InflationCallback inflationCallback) {
            Preconditions.checkNotNull(inflationCallback);
            this.inflationCallback = inflationCallback;
            return this;
        }
        
        public ExpandableNotificationRowComponentBuilder notificationEntry(final NotificationEntry notificationEntry) {
            Preconditions.checkNotNull(notificationEntry);
            this.notificationEntry = notificationEntry;
            return this;
        }
        
        public ExpandableNotificationRowComponentBuilder onDismissRunnable(final Runnable runnable) {
            Preconditions.checkNotNull(runnable);
            this.onDismissRunnable = runnable;
            return this;
        }
        
        public ExpandableNotificationRowComponentBuilder onExpandClickListener(final ExpandableNotificationRow.OnExpandClickListener onExpandClickListener) {
            Preconditions.checkNotNull(onExpandClickListener);
            this.onExpandClickListener = onExpandClickListener;
            return this;
        }
        
        public ExpandableNotificationRowComponentBuilder rowContentBindStage(final RowContentBindStage rowContentBindStage) {
            Preconditions.checkNotNull(rowContentBindStage);
            this.rowContentBindStage = rowContentBindStage;
            return this;
        }
    }
    
    private final class ExpandableNotificationRowComponentImpl implements ExpandableNotificationRowComponent
    {
        private ActivatableNotificationViewController_Factory activatableNotificationViewControllerProvider;
        private Provider<ExpandableNotificationRowController> expandableNotificationRowControllerProvider;
        private Provider<ExpandableNotificationRow> expandableNotificationRowProvider;
        private ExpandableOutlineViewController_Factory expandableOutlineViewControllerProvider;
        private ExpandableViewController_Factory expandableViewControllerProvider;
        private Provider<NotificationRowContentBinder.InflationCallback> inflationCallbackProvider;
        private Provider<NotificationEntry> notificationEntryProvider;
        private Provider<Runnable> onDismissRunnableProvider;
        private Provider<ExpandableNotificationRow.OnExpandClickListener> onExpandClickListenerProvider;
        private ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideAppNameFactory provideAppNameProvider;
        private ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideNotificationKeyFactory provideNotificationKeyProvider;
        private ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideStatusBarNotificationFactory provideStatusBarNotificationProvider;
        private Provider<RowContentBindStage> rowContentBindStageProvider;
        
        private ExpandableNotificationRowComponentImpl(final ExpandableNotificationRowComponentBuilder expandableNotificationRowComponentBuilder) {
            this.initialize(expandableNotificationRowComponentBuilder);
        }
        
        private void initialize(final ExpandableNotificationRowComponentBuilder expandableNotificationRowComponentBuilder) {
            final Factory<ExpandableNotificationRow> create = InstanceFactory.create(expandableNotificationRowComponentBuilder.expandableNotificationRow);
            this.expandableNotificationRowProvider = create;
            final ExpandableViewController_Factory create2 = ExpandableViewController_Factory.create((Provider<ExpandableView>)create);
            this.expandableViewControllerProvider = create2;
            final ExpandableOutlineViewController_Factory create3 = ExpandableOutlineViewController_Factory.create((Provider<ExpandableOutlineView>)this.expandableNotificationRowProvider, create2);
            this.expandableOutlineViewControllerProvider = create3;
            this.activatableNotificationViewControllerProvider = ActivatableNotificationViewController_Factory.create((Provider<ActivatableNotificationView>)this.expandableNotificationRowProvider, create3, DaggerSystemUIRootComponent.this.provideAccessibilityManagerProvider, DaggerSystemUIRootComponent.this.falsingManagerProxyProvider);
            final Factory<NotificationEntry> create4 = InstanceFactory.create(expandableNotificationRowComponentBuilder.notificationEntry);
            this.notificationEntryProvider = create4;
            this.provideStatusBarNotificationProvider = ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideStatusBarNotificationFactory.create(create4);
            this.provideAppNameProvider = ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideAppNameFactory.create(DaggerSystemUIRootComponent.this.provideContextProvider, this.provideStatusBarNotificationProvider);
            this.provideNotificationKeyProvider = ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideNotificationKeyFactory.create(this.provideStatusBarNotificationProvider);
            this.rowContentBindStageProvider = InstanceFactory.create(expandableNotificationRowComponentBuilder.rowContentBindStage);
            this.onExpandClickListenerProvider = InstanceFactory.create(expandableNotificationRowComponentBuilder.onExpandClickListener);
            this.inflationCallbackProvider = InstanceFactory.create(expandableNotificationRowComponentBuilder.inflationCallback);
            this.onDismissRunnableProvider = InstanceFactory.create(expandableNotificationRowComponentBuilder.onDismissRunnable);
            this.expandableNotificationRowControllerProvider = DoubleCheck.provider(ExpandableNotificationRowController_Factory.create(this.expandableNotificationRowProvider, this.activatableNotificationViewControllerProvider, DaggerSystemUIRootComponent.this.provideNotificationMediaManagerProvider, DaggerSystemUIRootComponent.this.providePluginManagerProvider, DaggerSystemUIRootComponent.this.bindSystemClockProvider, this.provideAppNameProvider, this.provideNotificationKeyProvider, DaggerSystemUIRootComponent.this.keyguardBypassControllerProvider, DaggerSystemUIRootComponent.this.notificationGroupManagerProvider, this.rowContentBindStageProvider, DaggerSystemUIRootComponent.this.provideNotificationLoggerProvider, DaggerSystemUIRootComponent.this.provideHeadsUpManagerPhoneProvider, this.onExpandClickListenerProvider, DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider, this.inflationCallbackProvider, DaggerSystemUIRootComponent.this.provideNotificationGutsManagerProvider, DaggerSystemUIRootComponent.this.provideAllowNotificationLongPressProvider, this.onDismissRunnableProvider, DaggerSystemUIRootComponent.this.falsingManagerProxyProvider, DaggerSystemUIRootComponent.this.peopleNotificationIdentifierImplProvider));
        }
        
        @Override
        public ExpandableNotificationRowController getExpandableNotificationRowController() {
            return this.expandableNotificationRowControllerProvider.get();
        }
    }
    
    private final class FragmentCreatorImpl implements FragmentCreator
    {
        private QSContainerImplController.Builder getBuilder() {
            return new QSContainerImplController.Builder(this.getBuilder2());
        }
        
        private QuickStatusBarHeaderController.Builder getBuilder2() {
            return new QuickStatusBarHeaderController.Builder(this.getBuilder3());
        }
        
        private QSCarrierGroupController.Builder getBuilder3() {
            return new QSCarrierGroupController.Builder(DaggerSystemUIRootComponent.this.activityStarterDelegateProvider.get(), DaggerSystemUIRootComponent.this.getBackgroundHandler(), ConcurrencyModule_ProvideMainLooperFactory.proxyProvideMainLooper(), DaggerSystemUIRootComponent.this.networkControllerImplProvider.get(), this.getBuilder4());
        }
        
        private CarrierTextController.Builder getBuilder4() {
            return new CarrierTextController.Builder(SystemUIFactory_ContextHolder_ProvideContextFactory.proxyProvideContext(DaggerSystemUIRootComponent.this.contextHolder), DaggerSystemUIRootComponent.this.getMainResources());
        }
        
        @Override
        public NavigationBarFragment createNavigationBarFragment() {
            return new NavigationBarFragment(DaggerSystemUIRootComponent.this.accessibilityManagerWrapperProvider.get(), DaggerSystemUIRootComponent.this.deviceProvisionedControllerImplProvider.get(), DaggerSystemUIRootComponent.this.provideMetricsLoggerProvider.get(), DaggerSystemUIRootComponent.this.assistManagerProvider.get(), DaggerSystemUIRootComponent.this.overviewProxyServiceProvider.get(), DaggerSystemUIRootComponent.this.navigationModeControllerProvider.get(), DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), DaggerSystemUIRootComponent.this.provideSysUiStateProvider.get(), DaggerSystemUIRootComponent.this.broadcastDispatcherProvider.get(), DaggerSystemUIRootComponent.this.provideCommandQueueProvider.get(), DaggerSystemUIRootComponent.this.provideDividerProvider.get(), Optional.of(DaggerSystemUIRootComponent.this.provideRecentsProvider.get()), DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideStatusBarProvider), DaggerSystemUIRootComponent.this.shadeControllerImplProvider.get(), DaggerSystemUIRootComponent.this.provideNotificationRemoteInputManagerProvider.get(), DaggerSystemUIRootComponent.this.getMainHandler());
        }
        
        @Override
        public QSFragment createQSFragment() {
            return new QSFragment(DaggerSystemUIRootComponent.this.remoteInputQuickSettingsDisablerProvider.get(), DaggerSystemUIRootComponent.this.injectionInflationControllerProvider.get(), DaggerSystemUIRootComponent.this.qSTileHostProvider.get(), DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), DaggerSystemUIRootComponent.this.provideCommandQueueProvider.get(), this.getBuilder());
        }
    }
    
    private final class NotificationRowComponentBuilder implements NotificationRowComponent.Builder
    {
        private ActivatableNotificationView activatableNotificationView;
        
        public NotificationRowComponentBuilder activatableNotificationView(final ActivatableNotificationView activatableNotificationView) {
            Preconditions.checkNotNull(activatableNotificationView);
            this.activatableNotificationView = activatableNotificationView;
            return this;
        }
        
        @Override
        public NotificationRowComponent build() {
            if (this.activatableNotificationView != null) {
                return new NotificationRowComponentImpl(this);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(ActivatableNotificationView.class.getCanonicalName());
            sb.append(" must be set");
            throw new IllegalStateException(sb.toString());
        }
    }
    
    private final class NotificationRowComponentImpl implements NotificationRowComponent
    {
        private ActivatableNotificationView activatableNotificationView;
        
        private NotificationRowComponentImpl(final NotificationRowComponentBuilder notificationRowComponentBuilder) {
            this.initialize(notificationRowComponentBuilder);
        }
        
        private ExpandableOutlineViewController getExpandableOutlineViewController() {
            return new ExpandableOutlineViewController(this.activatableNotificationView, this.getExpandableViewController());
        }
        
        private ExpandableViewController getExpandableViewController() {
            return new ExpandableViewController(this.activatableNotificationView);
        }
        
        private void initialize(final NotificationRowComponentBuilder notificationRowComponentBuilder) {
            this.activatableNotificationView = notificationRowComponentBuilder.activatableNotificationView;
        }
        
        @Override
        public ActivatableNotificationViewController getActivatableNotificationViewController() {
            return new ActivatableNotificationViewController(this.activatableNotificationView, this.getExpandableOutlineViewController(), DaggerSystemUIRootComponent.this.provideAccessibilityManagerProvider.get(), DaggerSystemUIRootComponent.this.falsingManagerProxyProvider.get());
        }
    }
    
    private static final class PresentJdkOptionalInstanceProvider<T> implements Provider<Optional<T>>
    {
        private final Provider<T> delegate;
        
        private PresentJdkOptionalInstanceProvider(final Provider<T> provider) {
            Preconditions.checkNotNull(provider);
            this.delegate = provider;
        }
        
        private static <T> Provider<Optional<T>> of(final Provider<T> provider) {
            return new PresentJdkOptionalInstanceProvider<T>(provider);
        }
        
        @Override
        public Optional<T> get() {
            return Optional.of(this.delegate.get());
        }
    }
    
    private static final class PresentJdkOptionalLazyProvider<T> implements Provider<Optional<Lazy<T>>>
    {
        private final Provider<T> delegate;
        
        private PresentJdkOptionalLazyProvider(final Provider<T> provider) {
            Preconditions.checkNotNull(provider);
            this.delegate = provider;
        }
        
        private static <T> Provider<Optional<Lazy<T>>> of(final Provider<T> provider) {
            return new PresentJdkOptionalLazyProvider<T>(provider);
        }
        
        @Override
        public Optional<Lazy<T>> get() {
            return Optional.of(DoubleCheck.lazy(this.delegate));
        }
    }
    
    private final class StatusBarComponentBuilder implements StatusBarComponent.Builder
    {
        private NotificationShadeWindowView statusBarWindowView;
        
        @Override
        public StatusBarComponent build() {
            if (this.statusBarWindowView != null) {
                return new StatusBarComponentImpl(this);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(NotificationShadeWindowView.class.getCanonicalName());
            sb.append(" must be set");
            throw new IllegalStateException(sb.toString());
        }
        
        public StatusBarComponentBuilder statusBarWindowView(final NotificationShadeWindowView notificationShadeWindowView) {
            Preconditions.checkNotNull(notificationShadeWindowView);
            this.statusBarWindowView = notificationShadeWindowView;
            return this;
        }
    }
    
    private final class StatusBarComponentImpl implements StatusBarComponent
    {
        private FlingAnimationUtils_Builder_Factory builderProvider;
        private Provider<NotificationPanelView> getNotificationPanelViewProvider;
        private Provider<NotificationPanelViewController> notificationPanelViewControllerProvider;
        private NotificationShadeWindowView statusBarWindowView;
        private Provider<NotificationShadeWindowView> statusBarWindowViewProvider;
        
        private StatusBarComponentImpl(final StatusBarComponentBuilder statusBarComponentBuilder) {
            this.initialize(statusBarComponentBuilder);
        }
        
        private void initialize(final StatusBarComponentBuilder statusBarComponentBuilder) {
            this.statusBarWindowView = statusBarComponentBuilder.statusBarWindowView;
            final Factory<NotificationShadeWindowView> create = InstanceFactory.create(statusBarComponentBuilder.statusBarWindowView);
            this.statusBarWindowViewProvider = create;
            this.getNotificationPanelViewProvider = (Provider<NotificationPanelView>)DoubleCheck.provider(StatusBarViewModule_GetNotificationPanelViewFactory.create(create));
            this.builderProvider = FlingAnimationUtils_Builder_Factory.create(DaggerSystemUIRootComponent.this.provideDisplayMetricsProvider);
            this.notificationPanelViewControllerProvider = DoubleCheck.provider(NotificationPanelViewController_Factory.create(this.getNotificationPanelViewProvider, DaggerSystemUIRootComponent.this.injectionInflationControllerProvider, DaggerSystemUIRootComponent.this.notificationWakeUpCoordinatorProvider, DaggerSystemUIRootComponent.this.pulseExpansionHandlerProvider, DaggerSystemUIRootComponent.this.dynamicPrivacyControllerProvider, DaggerSystemUIRootComponent.this.keyguardBypassControllerProvider, DaggerSystemUIRootComponent.this.falsingManagerProxyProvider, DaggerSystemUIRootComponent.this.shadeControllerImplProvider, DaggerSystemUIRootComponent.this.notificationLockscreenUserManagerImplProvider, DaggerSystemUIRootComponent.this.provideNotificationEntryManagerProvider, DaggerSystemUIRootComponent.this.keyguardStateControllerImplProvider, DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider, DaggerSystemUIRootComponent.this.dozeLogProvider, DaggerSystemUIRootComponent.this.dozeParametersProvider, DaggerSystemUIRootComponent.this.provideCommandQueueProvider, DaggerSystemUIRootComponent.this.vibratorHelperProvider, DaggerSystemUIRootComponent.this.provideLatencyTrackerProvider, DaggerSystemUIRootComponent.this.providePowerManagerProvider, DaggerSystemUIRootComponent.this.provideAccessibilityManagerProvider, DaggerSystemUIRootComponent.this.provideDisplayIdProvider, DaggerSystemUIRootComponent.this.keyguardUpdateMonitorProvider, DaggerSystemUIRootComponent.this.provideMetricsLoggerProvider, DaggerSystemUIRootComponent.this.provideActivityManagerProvider, DaggerSystemUIRootComponent.this.zenModeControllerImplProvider, DaggerSystemUIRootComponent.this.provideConfigurationControllerProvider, this.builderProvider, DaggerSystemUIRootComponent.this.statusBarTouchableRegionManagerProvider, DaggerSystemUIRootComponent.this.conversationNotificationManagerProvider));
        }
        
        @Override
        public NotificationPanelViewController getNotificationPanelViewController() {
            return this.notificationPanelViewControllerProvider.get();
        }
        
        @Override
        public NotificationShadeWindowViewController getNotificationShadeWindowViewController() {
            return new NotificationShadeWindowViewController(DaggerSystemUIRootComponent.this.injectionInflationControllerProvider.get(), DaggerSystemUIRootComponent.this.notificationWakeUpCoordinatorProvider.get(), DaggerSystemUIRootComponent.this.pulseExpansionHandlerProvider.get(), DaggerSystemUIRootComponent.this.dynamicPrivacyControllerProvider.get(), DaggerSystemUIRootComponent.this.keyguardBypassControllerProvider.get(), DaggerSystemUIRootComponent.this.falsingManagerProxyProvider.get(), DaggerSystemUIRootComponent.this.providePluginManagerProvider.get(), DaggerSystemUIRootComponent.this.tunerServiceImplProvider.get(), DaggerSystemUIRootComponent.this.notificationLockscreenUserManagerImplProvider.get(), DaggerSystemUIRootComponent.this.provideNotificationEntryManagerProvider.get(), DaggerSystemUIRootComponent.this.keyguardStateControllerImplProvider.get(), DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), DaggerSystemUIRootComponent.this.dozeLogProvider.get(), DaggerSystemUIRootComponent.this.dozeParametersProvider.get(), DaggerSystemUIRootComponent.this.provideCommandQueueProvider.get(), DaggerSystemUIRootComponent.this.shadeControllerImplProvider.get(), DaggerSystemUIRootComponent.this.dockManagerImplProvider.get(), DaggerSystemUIRootComponent.this.notificationShadeDepthControllerProvider.get(), this.statusBarWindowView, this.notificationPanelViewControllerProvider.get(), DaggerSystemUIRootComponent.this.superStatusBarViewFactoryProvider.get());
        }
        
        @Override
        public StatusBarWindowController getStatusBarWindowController() {
            return DaggerSystemUIRootComponent.this.statusBarWindowControllerProvider.get();
        }
    }
    
    private final class ViewCreatorImpl implements ViewCreator
    {
        @Override
        public ViewInstanceCreator createInstanceCreator(final ViewAttributeProvider viewAttributeProvider) {
            return new ViewInstanceCreatorImpl(viewAttributeProvider);
        }
        
        private final class ViewInstanceCreatorImpl implements ViewInstanceCreator
        {
            private ViewAttributeProvider viewAttributeProvider;
            
            private ViewInstanceCreatorImpl(final ViewAttributeProvider viewAttributeProvider) {
                this.initialize(viewAttributeProvider);
            }
            
            private NotificationSectionsManager getNotificationSectionsManager() {
                return NotificationSectionsManager_Factory.newNotificationSectionsManager(DaggerSystemUIRootComponent.this.activityStarterDelegateProvider.get(), DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), DaggerSystemUIRootComponent.this.provideConfigurationControllerProvider.get(), DaggerSystemUIRootComponent.this.peopleHubViewAdapterImplProvider.get(), DaggerSystemUIRootComponent.this.keyguardMediaPlayerProvider.get(), DaggerSystemUIRootComponent.this.getNotificationSectionsFeatureManager());
            }
            
            private TileQueryHelper getTileQueryHelper() {
                return new TileQueryHelper(SystemUIFactory_ContextHolder_ProvideContextFactory.proxyProvideContext(DaggerSystemUIRootComponent.this.contextHolder), DaggerSystemUIRootComponent.this.getMainExecutor(), DaggerSystemUIRootComponent.this.provideBackgroundExecutorProvider.get());
            }
            
            private void initialize(final ViewAttributeProvider viewAttributeProvider) {
                Preconditions.checkNotNull(viewAttributeProvider);
                this.viewAttributeProvider = viewAttributeProvider;
            }
            
            @Override
            public NotificationShelf creatNotificationShelf() {
                return new NotificationShelf(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), DaggerSystemUIRootComponent.this.keyguardBypassControllerProvider.get());
            }
            
            @Override
            public KeyguardClockSwitch createKeyguardClockSwitch() {
                return new KeyguardClockSwitch(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), DaggerSystemUIRootComponent.this.sysuiColorExtractorProvider.get(), DaggerSystemUIRootComponent.this.clockManagerProvider.get());
            }
            
            @Override
            public KeyguardMessageArea createKeyguardMessageArea() {
                return new KeyguardMessageArea(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), DaggerSystemUIRootComponent.this.provideConfigurationControllerProvider.get());
            }
            
            @Override
            public KeyguardSliceView createKeyguardSliceView() {
                return new KeyguardSliceView(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), DaggerSystemUIRootComponent.this.activityStarterDelegateProvider.get(), DaggerSystemUIRootComponent.this.provideConfigurationControllerProvider.get(), DaggerSystemUIRootComponent.this.tunerServiceImplProvider.get(), DaggerSystemUIRootComponent.this.getMainResources());
            }
            
            @Override
            public NotificationStackScrollLayout createNotificationStackScrollLayout() {
                return new NotificationStackScrollLayout(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), DaggerSystemUIRootComponent.this.provideAllowNotificationLongPressProvider.get(), DaggerSystemUIRootComponent.this.notificationRoundnessManagerProvider.get(), DaggerSystemUIRootComponent.this.dynamicPrivacyControllerProvider.get(), DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), DaggerSystemUIRootComponent.this.provideHeadsUpManagerPhoneProvider.get(), DaggerSystemUIRootComponent.this.keyguardBypassControllerProvider.get(), DaggerSystemUIRootComponent.this.falsingManagerProxyProvider.get(), DaggerSystemUIRootComponent.this.notificationLockscreenUserManagerImplProvider.get(), DaggerSystemUIRootComponent.this.provideNotificationGutsManagerProvider.get(), DaggerSystemUIRootComponent.this.zenModeControllerImplProvider.get(), this.getNotificationSectionsManager(), DaggerSystemUIRootComponent.this.foregroundServiceSectionControllerProvider.get(), DaggerSystemUIRootComponent.this.foregroundServiceDismissalFeatureControllerProvider.get(), DaggerSystemUIRootComponent.this.featureFlagsProvider.get(), DaggerSystemUIRootComponent.this.notifPipelineProvider.get(), DaggerSystemUIRootComponent.this.provideNotificationEntryManagerProvider.get(), DaggerSystemUIRootComponent.this.notifCollectionProvider.get(), DaggerSystemUIRootComponent.this.provideUiEventLoggerProvider.get());
            }
            
            @Override
            public QSCustomizer createQSCustomizer() {
                return new QSCustomizer(SystemUIFactory_ContextHolder_ProvideContextFactory.proxyProvideContext(DaggerSystemUIRootComponent.this.contextHolder), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), DaggerSystemUIRootComponent.this.lightBarControllerProvider.get(), DaggerSystemUIRootComponent.this.keyguardStateControllerImplProvider.get(), DaggerSystemUIRootComponent.this.screenLifecycleProvider.get(), this.getTileQueryHelper());
            }
            
            @Override
            public QSPanel createQSPanel() {
                return new QSPanel(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), DaggerSystemUIRootComponent.this.dumpManagerProvider.get(), DaggerSystemUIRootComponent.this.broadcastDispatcherProvider.get(), DaggerSystemUIRootComponent.this.getQSLogger(), DaggerSystemUIRootComponent.this.provideNotificationMediaManagerProvider.get(), DaggerSystemUIRootComponent.this.getMainExecutor(), DaggerSystemUIRootComponent.this.provideBackgroundDelayableExecutorProvider.get(), DaggerSystemUIRootComponent.this.provideLocalBluetoothControllerProvider.get());
            }
            
            @Override
            public QSFooterImpl createQsFooter() {
                return new QSFooterImpl(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), DaggerSystemUIRootComponent.this.activityStarterDelegateProvider.get(), DaggerSystemUIRootComponent.this.userInfoControllerImplProvider.get(), DaggerSystemUIRootComponent.this.deviceProvisionedControllerImplProvider.get());
            }
            
            @Override
            public QuickStatusBarHeader createQsHeader() {
                return new QuickStatusBarHeader(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), DaggerSystemUIRootComponent.this.nextAlarmControllerImplProvider.get(), DaggerSystemUIRootComponent.this.zenModeControllerImplProvider.get(), DaggerSystemUIRootComponent.this.statusBarIconControllerImplProvider.get(), DaggerSystemUIRootComponent.this.activityStarterDelegateProvider.get(), DaggerSystemUIRootComponent.this.provideCommandQueueProvider.get(), DaggerSystemUIRootComponent.this.broadcastDispatcherProvider.get());
            }
            
            @Override
            public QuickQSPanel createQuickQSPanel() {
                return new QuickQSPanel(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), DaggerSystemUIRootComponent.this.dumpManagerProvider.get(), DaggerSystemUIRootComponent.this.broadcastDispatcherProvider.get(), DaggerSystemUIRootComponent.this.getQSLogger(), DaggerSystemUIRootComponent.this.provideNotificationMediaManagerProvider.get(), DaggerSystemUIRootComponent.this.getMainExecutor(), DaggerSystemUIRootComponent.this.provideBackgroundDelayableExecutorProvider.get(), DaggerSystemUIRootComponent.this.provideLocalBluetoothControllerProvider.get());
            }
        }
    }
}
