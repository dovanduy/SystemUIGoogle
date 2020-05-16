// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.power.PowerUI;
import android.app.IWallpaperManager;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.VolumeDialogController;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunablePadding;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.wm.SystemWindows;
import com.android.systemui.model.SysUiState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.policy.SmartReplyConstants;
import com.android.systemui.statusbar.phone.ShadeController;
import android.hardware.SensorPrivacyManager;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.SecurityController;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.recents.Recents;
import com.android.systemui.tracing.ProtoTracer;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.notification.row.NotificationBlockingHelperManager;
import com.android.systemui.statusbar.notification.interruption.NotificationAlertingManager;
import android.hardware.display.NightDisplayListener;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.util.leak.LeakReporter;
import com.android.systemui.util.leak.LeakDetector;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import android.view.IWindowManager;
import com.android.internal.statusbar.IStatusBarService;
import android.app.INotificationManager;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.util.leak.GarbageMonitor;
import com.android.systemui.fragments.FragmentService;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.power.EnhancedEstimates;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.dock.DockManager;
import com.android.systemui.stackdivider.Divider;
import android.util.DisplayMetrics;
import com.android.systemui.wm.DisplayImeController;
import com.android.systemui.wm.DisplayController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.shared.system.DevicePolicyManagerWrapper;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.keyguard.clock.ClockManager;
import com.android.systemui.statusbar.notification.row.ChannelEditorDialogController;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.policy.BluetoothController;
import android.os.Looper;
import android.os.Handler;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.appops.AppOpsController;
import android.app.AlarmManager;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.AccessibilityController;
import dagger.Lazy;

public final class Dependency_MembersInjector implements Object<Dependency>
{
    public static void injectMAccessibilityController(final Dependency dependency, final Lazy<AccessibilityController> mAccessibilityController) {
        dependency.mAccessibilityController = mAccessibilityController;
    }
    
    public static void injectMAccessibilityManagerWrapper(final Dependency dependency, final Lazy<AccessibilityManagerWrapper> mAccessibilityManagerWrapper) {
        dependency.mAccessibilityManagerWrapper = mAccessibilityManagerWrapper;
    }
    
    public static void injectMActivityManagerWrapper(final Dependency dependency, final Lazy<ActivityManagerWrapper> mActivityManagerWrapper) {
        dependency.mActivityManagerWrapper = mActivityManagerWrapper;
    }
    
    public static void injectMActivityStarter(final Dependency dependency, final Lazy<ActivityStarter> mActivityStarter) {
        dependency.mActivityStarter = mActivityStarter;
    }
    
    public static void injectMAlarmManager(final Dependency dependency, final Lazy<AlarmManager> mAlarmManager) {
        dependency.mAlarmManager = mAlarmManager;
    }
    
    public static void injectMAppOpsController(final Dependency dependency, final Lazy<AppOpsController> mAppOpsController) {
        dependency.mAppOpsController = mAppOpsController;
    }
    
    public static void injectMAssistManager(final Dependency dependency, final Lazy<AssistManager> mAssistManager) {
        dependency.mAssistManager = mAssistManager;
    }
    
    public static void injectMAsyncSensorManager(final Dependency dependency, final Lazy<AsyncSensorManager> mAsyncSensorManager) {
        dependency.mAsyncSensorManager = mAsyncSensorManager;
    }
    
    public static void injectMAutoHideController(final Dependency dependency, final Lazy<AutoHideController> mAutoHideController) {
        dependency.mAutoHideController = mAutoHideController;
    }
    
    public static void injectMBatteryController(final Dependency dependency, final Lazy<BatteryController> mBatteryController) {
        dependency.mBatteryController = mBatteryController;
    }
    
    public static void injectMBgHandler(final Dependency dependency, final Lazy<Handler> mBgHandler) {
        dependency.mBgHandler = mBgHandler;
    }
    
    public static void injectMBgLooper(final Dependency dependency, final Lazy<Looper> mBgLooper) {
        dependency.mBgLooper = mBgLooper;
    }
    
    public static void injectMBluetoothController(final Dependency dependency, final Lazy<BluetoothController> mBluetoothController) {
        dependency.mBluetoothController = mBluetoothController;
    }
    
    public static void injectMBroadcastDispatcher(final Dependency dependency, final Lazy<BroadcastDispatcher> mBroadcastDispatcher) {
        dependency.mBroadcastDispatcher = mBroadcastDispatcher;
    }
    
    public static void injectMBubbleController(final Dependency dependency, final Lazy<BubbleController> mBubbleController) {
        dependency.mBubbleController = mBubbleController;
    }
    
    public static void injectMCastController(final Dependency dependency, final Lazy<CastController> mCastController) {
        dependency.mCastController = mCastController;
    }
    
    public static void injectMChannelEditorDialogController(final Dependency dependency, final Lazy<ChannelEditorDialogController> mChannelEditorDialogController) {
        dependency.mChannelEditorDialogController = mChannelEditorDialogController;
    }
    
    public static void injectMClockManager(final Dependency dependency, final Lazy<ClockManager> mClockManager) {
        dependency.mClockManager = mClockManager;
    }
    
    public static void injectMCommandQueue(final Dependency dependency, final Lazy<CommandQueue> mCommandQueue) {
        dependency.mCommandQueue = mCommandQueue;
    }
    
    public static void injectMConfigurationController(final Dependency dependency, final Lazy<ConfigurationController> mConfigurationController) {
        dependency.mConfigurationController = mConfigurationController;
    }
    
    public static void injectMDarkIconDispatcher(final Dependency dependency, final Lazy<DarkIconDispatcher> mDarkIconDispatcher) {
        dependency.mDarkIconDispatcher = mDarkIconDispatcher;
    }
    
    public static void injectMDataSaverController(final Dependency dependency, final Lazy<DataSaverController> mDataSaverController) {
        dependency.mDataSaverController = mDataSaverController;
    }
    
    public static void injectMDevicePolicyManagerWrapper(final Dependency dependency, final Lazy<DevicePolicyManagerWrapper> mDevicePolicyManagerWrapper) {
        dependency.mDevicePolicyManagerWrapper = mDevicePolicyManagerWrapper;
    }
    
    public static void injectMDeviceProvisionedController(final Dependency dependency, final Lazy<DeviceProvisionedController> mDeviceProvisionedController) {
        dependency.mDeviceProvisionedController = mDeviceProvisionedController;
    }
    
    public static void injectMDisplayController(final Dependency dependency, final Lazy<DisplayController> mDisplayController) {
        dependency.mDisplayController = mDisplayController;
    }
    
    public static void injectMDisplayImeController(final Dependency dependency, final Lazy<DisplayImeController> mDisplayImeController) {
        dependency.mDisplayImeController = mDisplayImeController;
    }
    
    public static void injectMDisplayMetrics(final Dependency dependency, final Lazy<DisplayMetrics> mDisplayMetrics) {
        dependency.mDisplayMetrics = mDisplayMetrics;
    }
    
    public static void injectMDivider(final Dependency dependency, final Lazy<Divider> mDivider) {
        dependency.mDivider = mDivider;
    }
    
    public static void injectMDockManager(final Dependency dependency, final Lazy<DockManager> mDockManager) {
        dependency.mDockManager = mDockManager;
    }
    
    public static void injectMDozeParameters(final Dependency dependency, final Lazy<DozeParameters> mDozeParameters) {
        dependency.mDozeParameters = mDozeParameters;
    }
    
    public static void injectMDumpManager(final Dependency dependency, final DumpManager mDumpManager) {
        dependency.mDumpManager = mDumpManager;
    }
    
    public static void injectMEnhancedEstimates(final Dependency dependency, final Lazy<EnhancedEstimates> mEnhancedEstimates) {
        dependency.mEnhancedEstimates = mEnhancedEstimates;
    }
    
    public static void injectMExtensionController(final Dependency dependency, final Lazy<ExtensionController> mExtensionController) {
        dependency.mExtensionController = mExtensionController;
    }
    
    public static void injectMFlashlightController(final Dependency dependency, final Lazy<FlashlightController> mFlashlightController) {
        dependency.mFlashlightController = mFlashlightController;
    }
    
    public static void injectMForegroundServiceController(final Dependency dependency, final Lazy<ForegroundServiceController> mForegroundServiceController) {
        dependency.mForegroundServiceController = mForegroundServiceController;
    }
    
    public static void injectMForegroundServiceNotificationListener(final Dependency dependency, final Lazy<ForegroundServiceNotificationListener> mForegroundServiceNotificationListener) {
        dependency.mForegroundServiceNotificationListener = mForegroundServiceNotificationListener;
    }
    
    public static void injectMFragmentService(final Dependency dependency, final Lazy<FragmentService> mFragmentService) {
        dependency.mFragmentService = mFragmentService;
    }
    
    public static void injectMGarbageMonitor(final Dependency dependency, final Lazy<GarbageMonitor> mGarbageMonitor) {
        dependency.mGarbageMonitor = mGarbageMonitor;
    }
    
    public static void injectMHotspotController(final Dependency dependency, final Lazy<HotspotController> mHotspotController) {
        dependency.mHotspotController = mHotspotController;
    }
    
    public static void injectMINotificationManager(final Dependency dependency, final Lazy<INotificationManager> miNotificationManager) {
        dependency.mINotificationManager = miNotificationManager;
    }
    
    public static void injectMIStatusBarService(final Dependency dependency, final Lazy<IStatusBarService> miStatusBarService) {
        dependency.mIStatusBarService = miStatusBarService;
    }
    
    public static void injectMIWindowManager(final Dependency dependency, final Lazy<IWindowManager> miWindowManager) {
        dependency.mIWindowManager = miWindowManager;
    }
    
    public static void injectMKeyguardDismissUtil(final Dependency dependency, final Lazy<KeyguardDismissUtil> mKeyguardDismissUtil) {
        dependency.mKeyguardDismissUtil = mKeyguardDismissUtil;
    }
    
    public static void injectMKeyguardEnvironment(final Dependency dependency, final Lazy<NotificationEntryManager.KeyguardEnvironment> mKeyguardEnvironment) {
        dependency.mKeyguardEnvironment = mKeyguardEnvironment;
    }
    
    public static void injectMKeyguardMonitor(final Dependency dependency, final Lazy<KeyguardStateController> mKeyguardMonitor) {
        dependency.mKeyguardMonitor = mKeyguardMonitor;
    }
    
    public static void injectMKeyguardSecurityModel(final Dependency dependency, final Lazy<KeyguardSecurityModel> mKeyguardSecurityModel) {
        dependency.mKeyguardSecurityModel = mKeyguardSecurityModel;
    }
    
    public static void injectMKeyguardUpdateMonitor(final Dependency dependency, final Lazy<KeyguardUpdateMonitor> mKeyguardUpdateMonitor) {
        dependency.mKeyguardUpdateMonitor = mKeyguardUpdateMonitor;
    }
    
    public static void injectMLeakDetector(final Dependency dependency, final Lazy<LeakDetector> mLeakDetector) {
        dependency.mLeakDetector = mLeakDetector;
    }
    
    public static void injectMLeakReportEmail(final Dependency dependency, final Lazy<String> mLeakReportEmail) {
        dependency.mLeakReportEmail = mLeakReportEmail;
    }
    
    public static void injectMLeakReporter(final Dependency dependency, final Lazy<LeakReporter> mLeakReporter) {
        dependency.mLeakReporter = mLeakReporter;
    }
    
    public static void injectMLightBarController(final Dependency dependency, final Lazy<LightBarController> mLightBarController) {
        dependency.mLightBarController = mLightBarController;
    }
    
    public static void injectMLocalBluetoothManager(final Dependency dependency, final Lazy<LocalBluetoothManager> mLocalBluetoothManager) {
        dependency.mLocalBluetoothManager = mLocalBluetoothManager;
    }
    
    public static void injectMLocationController(final Dependency dependency, final Lazy<LocationController> mLocationController) {
        dependency.mLocationController = mLocationController;
    }
    
    public static void injectMLockscreenGestureLogger(final Dependency dependency, final Lazy<LockscreenGestureLogger> mLockscreenGestureLogger) {
        dependency.mLockscreenGestureLogger = mLockscreenGestureLogger;
    }
    
    public static void injectMMainHandler(final Dependency dependency, final Lazy<Handler> mMainHandler) {
        dependency.mMainHandler = mMainHandler;
    }
    
    public static void injectMMainLooper(final Dependency dependency, final Lazy<Looper> mMainLooper) {
        dependency.mMainLooper = mMainLooper;
    }
    
    public static void injectMManagedProfileController(final Dependency dependency, final Lazy<ManagedProfileController> mManagedProfileController) {
        dependency.mManagedProfileController = mManagedProfileController;
    }
    
    public static void injectMMetricsLogger(final Dependency dependency, final Lazy<MetricsLogger> mMetricsLogger) {
        dependency.mMetricsLogger = mMetricsLogger;
    }
    
    public static void injectMNavBarModeController(final Dependency dependency, final Lazy<NavigationModeController> mNavBarModeController) {
        dependency.mNavBarModeController = mNavBarModeController;
    }
    
    public static void injectMNavigationBarController(final Dependency dependency, final Lazy<NavigationBarController> mNavigationBarController) {
        dependency.mNavigationBarController = mNavigationBarController;
    }
    
    public static void injectMNetworkController(final Dependency dependency, final Lazy<NetworkController> mNetworkController) {
        dependency.mNetworkController = mNetworkController;
    }
    
    public static void injectMNextAlarmController(final Dependency dependency, final Lazy<NextAlarmController> mNextAlarmController) {
        dependency.mNextAlarmController = mNextAlarmController;
    }
    
    public static void injectMNightDisplayListener(final Dependency dependency, final Lazy<NightDisplayListener> mNightDisplayListener) {
        dependency.mNightDisplayListener = mNightDisplayListener;
    }
    
    public static void injectMNotificationAlertingManager(final Dependency dependency, final Lazy<NotificationAlertingManager> mNotificationAlertingManager) {
        dependency.mNotificationAlertingManager = mNotificationAlertingManager;
    }
    
    public static void injectMNotificationBlockingHelperManager(final Dependency dependency, final Lazy<NotificationBlockingHelperManager> mNotificationBlockingHelperManager) {
        dependency.mNotificationBlockingHelperManager = mNotificationBlockingHelperManager;
    }
    
    public static void injectMNotificationEntryManager(final Dependency dependency, final Lazy<NotificationEntryManager> mNotificationEntryManager) {
        dependency.mNotificationEntryManager = mNotificationEntryManager;
    }
    
    public static void injectMNotificationFilter(final Dependency dependency, final Lazy<NotificationFilter> mNotificationFilter) {
        dependency.mNotificationFilter = mNotificationFilter;
    }
    
    public static void injectMNotificationGroupAlertTransferHelper(final Dependency dependency, final Lazy<NotificationGroupAlertTransferHelper> mNotificationGroupAlertTransferHelper) {
        dependency.mNotificationGroupAlertTransferHelper = mNotificationGroupAlertTransferHelper;
    }
    
    public static void injectMNotificationGroupManager(final Dependency dependency, final Lazy<NotificationGroupManager> mNotificationGroupManager) {
        dependency.mNotificationGroupManager = mNotificationGroupManager;
    }
    
    public static void injectMNotificationGutsManager(final Dependency dependency, final Lazy<NotificationGutsManager> mNotificationGutsManager) {
        dependency.mNotificationGutsManager = mNotificationGutsManager;
    }
    
    public static void injectMNotificationListener(final Dependency dependency, final Lazy<NotificationListener> mNotificationListener) {
        dependency.mNotificationListener = mNotificationListener;
    }
    
    public static void injectMNotificationLockscreenUserManager(final Dependency dependency, final Lazy<NotificationLockscreenUserManager> mNotificationLockscreenUserManager) {
        dependency.mNotificationLockscreenUserManager = mNotificationLockscreenUserManager;
    }
    
    public static void injectMNotificationLogger(final Dependency dependency, final Lazy<NotificationLogger> mNotificationLogger) {
        dependency.mNotificationLogger = mNotificationLogger;
    }
    
    public static void injectMNotificationMediaManager(final Dependency dependency, final Lazy<NotificationMediaManager> mNotificationMediaManager) {
        dependency.mNotificationMediaManager = mNotificationMediaManager;
    }
    
    public static void injectMNotificationRemoteInputManager(final Dependency dependency, final Lazy<NotificationRemoteInputManager> mNotificationRemoteInputManager) {
        dependency.mNotificationRemoteInputManager = mNotificationRemoteInputManager;
    }
    
    public static void injectMNotificationRemoteInputManagerCallback(final Dependency dependency, final Lazy<NotificationRemoteInputManager.Callback> mNotificationRemoteInputManagerCallback) {
        dependency.mNotificationRemoteInputManagerCallback = mNotificationRemoteInputManagerCallback;
    }
    
    public static void injectMNotificationShadeWindowController(final Dependency dependency, final Lazy<NotificationShadeWindowController> mNotificationShadeWindowController) {
        dependency.mNotificationShadeWindowController = mNotificationShadeWindowController;
    }
    
    public static void injectMNotificationViewHierarchyManager(final Dependency dependency, final Lazy<NotificationViewHierarchyManager> mNotificationViewHierarchyManager) {
        dependency.mNotificationViewHierarchyManager = mNotificationViewHierarchyManager;
    }
    
    public static void injectMOverviewProxyService(final Dependency dependency, final Lazy<OverviewProxyService> mOverviewProxyService) {
        dependency.mOverviewProxyService = mOverviewProxyService;
    }
    
    public static void injectMPackageManagerWrapper(final Dependency dependency, final Lazy<PackageManagerWrapper> mPackageManagerWrapper) {
        dependency.mPackageManagerWrapper = mPackageManagerWrapper;
    }
    
    public static void injectMPluginDependencyProvider(final Dependency dependency, final Lazy<PluginDependencyProvider> mPluginDependencyProvider) {
        dependency.mPluginDependencyProvider = mPluginDependencyProvider;
    }
    
    public static void injectMPluginManager(final Dependency dependency, final Lazy<PluginManager> mPluginManager) {
        dependency.mPluginManager = mPluginManager;
    }
    
    public static void injectMProtoTracer(final Dependency dependency, final Lazy<ProtoTracer> mProtoTracer) {
        dependency.mProtoTracer = mProtoTracer;
    }
    
    public static void injectMRecents(final Dependency dependency, final Lazy<Recents> mRecents) {
        dependency.mRecents = mRecents;
    }
    
    public static void injectMRecordingController(final Dependency dependency, final Lazy<RecordingController> mRecordingController) {
        dependency.mRecordingController = mRecordingController;
    }
    
    public static void injectMRemoteInputQuickSettingsDisabler(final Dependency dependency, final Lazy<RemoteInputQuickSettingsDisabler> mRemoteInputQuickSettingsDisabler) {
        dependency.mRemoteInputQuickSettingsDisabler = mRemoteInputQuickSettingsDisabler;
    }
    
    public static void injectMRotationLockController(final Dependency dependency, final Lazy<RotationLockController> mRotationLockController) {
        dependency.mRotationLockController = mRotationLockController;
    }
    
    public static void injectMScreenLifecycle(final Dependency dependency, final Lazy<ScreenLifecycle> mScreenLifecycle) {
        dependency.mScreenLifecycle = mScreenLifecycle;
    }
    
    public static void injectMSecurityController(final Dependency dependency, final Lazy<SecurityController> mSecurityController) {
        dependency.mSecurityController = mSecurityController;
    }
    
    public static void injectMSensorPrivacyController(final Dependency dependency, final Lazy<SensorPrivacyController> mSensorPrivacyController) {
        dependency.mSensorPrivacyController = mSensorPrivacyController;
    }
    
    public static void injectMSensorPrivacyManager(final Dependency dependency, final Lazy<SensorPrivacyManager> mSensorPrivacyManager) {
        dependency.mSensorPrivacyManager = mSensorPrivacyManager;
    }
    
    public static void injectMShadeController(final Dependency dependency, final Lazy<ShadeController> mShadeController) {
        dependency.mShadeController = mShadeController;
    }
    
    public static void injectMSmartReplyConstants(final Dependency dependency, final Lazy<SmartReplyConstants> mSmartReplyConstants) {
        dependency.mSmartReplyConstants = mSmartReplyConstants;
    }
    
    public static void injectMSmartReplyController(final Dependency dependency, final Lazy<SmartReplyController> mSmartReplyController) {
        dependency.mSmartReplyController = mSmartReplyController;
    }
    
    public static void injectMStatusBar(final Dependency dependency, final Lazy<StatusBar> mStatusBar) {
        dependency.mStatusBar = mStatusBar;
    }
    
    public static void injectMStatusBarIconController(final Dependency dependency, final Lazy<StatusBarIconController> mStatusBarIconController) {
        dependency.mStatusBarIconController = mStatusBarIconController;
    }
    
    public static void injectMStatusBarStateController(final Dependency dependency, final Lazy<StatusBarStateController> mStatusBarStateController) {
        dependency.mStatusBarStateController = mStatusBarStateController;
    }
    
    public static void injectMSysUiStateFlagsContainer(final Dependency dependency, final Lazy<SysUiState> mSysUiStateFlagsContainer) {
        dependency.mSysUiStateFlagsContainer = mSysUiStateFlagsContainer;
    }
    
    public static void injectMSystemWindows(final Dependency dependency, final Lazy<SystemWindows> mSystemWindows) {
        dependency.mSystemWindows = mSystemWindows;
    }
    
    public static void injectMSysuiColorExtractor(final Dependency dependency, final Lazy<SysuiColorExtractor> mSysuiColorExtractor) {
        dependency.mSysuiColorExtractor = mSysuiColorExtractor;
    }
    
    public static void injectMTempStatusBarWindowController(final Dependency dependency, final Lazy<StatusBarWindowController> mTempStatusBarWindowController) {
        dependency.mTempStatusBarWindowController = mTempStatusBarWindowController;
    }
    
    public static void injectMTimeTickHandler(final Dependency dependency, final Lazy<Handler> mTimeTickHandler) {
        dependency.mTimeTickHandler = mTimeTickHandler;
    }
    
    public static void injectMTunablePaddingService(final Dependency dependency, final Lazy<TunablePadding.TunablePaddingService> mTunablePaddingService) {
        dependency.mTunablePaddingService = mTunablePaddingService;
    }
    
    public static void injectMTunerService(final Dependency dependency, final Lazy<TunerService> mTunerService) {
        dependency.mTunerService = mTunerService;
    }
    
    public static void injectMUiOffloadThread(final Dependency dependency, final Lazy<UiOffloadThread> mUiOffloadThread) {
        dependency.mUiOffloadThread = mUiOffloadThread;
    }
    
    public static void injectMUserInfoController(final Dependency dependency, final Lazy<UserInfoController> mUserInfoController) {
        dependency.mUserInfoController = mUserInfoController;
    }
    
    public static void injectMUserSwitcherController(final Dependency dependency, final Lazy<UserSwitcherController> mUserSwitcherController) {
        dependency.mUserSwitcherController = mUserSwitcherController;
    }
    
    public static void injectMVibratorHelper(final Dependency dependency, final Lazy<VibratorHelper> mVibratorHelper) {
        dependency.mVibratorHelper = mVibratorHelper;
    }
    
    public static void injectMVisualStabilityManager(final Dependency dependency, final Lazy<VisualStabilityManager> mVisualStabilityManager) {
        dependency.mVisualStabilityManager = mVisualStabilityManager;
    }
    
    public static void injectMVolumeDialogController(final Dependency dependency, final Lazy<VolumeDialogController> mVolumeDialogController) {
        dependency.mVolumeDialogController = mVolumeDialogController;
    }
    
    public static void injectMWakefulnessLifecycle(final Dependency dependency, final Lazy<WakefulnessLifecycle> mWakefulnessLifecycle) {
        dependency.mWakefulnessLifecycle = mWakefulnessLifecycle;
    }
    
    public static void injectMWallpaperManager(final Dependency dependency, final Lazy<IWallpaperManager> mWallpaperManager) {
        dependency.mWallpaperManager = mWallpaperManager;
    }
    
    public static void injectMWarningsUI(final Dependency dependency, final Lazy<PowerUI.WarningsUI> mWarningsUI) {
        dependency.mWarningsUI = mWarningsUI;
    }
    
    public static void injectMZenModeController(final Dependency dependency, final Lazy<ZenModeController> mZenModeController) {
        dependency.mZenModeController = mZenModeController;
    }
}
