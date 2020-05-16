// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import java.util.Objects;
import com.android.internal.util.Preconditions;
import com.android.internal.annotations.VisibleForTesting;
import java.util.function.Consumer;
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
import android.util.ArrayMap;
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
import android.os.Handler;
import android.os.Looper;

public class Dependency
{
    public static final DependencyKey<Looper> BG_LOOPER;
    public static final DependencyKey<String> LEAK_REPORT_EMAIL;
    public static final DependencyKey<Handler> MAIN_HANDLER;
    public static final DependencyKey<Looper> MAIN_LOOPER;
    public static final DependencyKey<Handler> TIME_TICK_HANDLER;
    private static Dependency sDependency;
    Lazy<AccessibilityController> mAccessibilityController;
    Lazy<AccessibilityManagerWrapper> mAccessibilityManagerWrapper;
    Lazy<ActivityManagerWrapper> mActivityManagerWrapper;
    Lazy<ActivityStarter> mActivityStarter;
    Lazy<AlarmManager> mAlarmManager;
    Lazy<AppOpsController> mAppOpsController;
    Lazy<AssistManager> mAssistManager;
    Lazy<AsyncSensorManager> mAsyncSensorManager;
    Lazy<AutoHideController> mAutoHideController;
    Lazy<BatteryController> mBatteryController;
    Lazy<Handler> mBgHandler;
    Lazy<Looper> mBgLooper;
    Lazy<BluetoothController> mBluetoothController;
    Lazy<BroadcastDispatcher> mBroadcastDispatcher;
    Lazy<BubbleController> mBubbleController;
    Lazy<CastController> mCastController;
    Lazy<ChannelEditorDialogController> mChannelEditorDialogController;
    Lazy<ClockManager> mClockManager;
    Lazy<CommandQueue> mCommandQueue;
    Lazy<ConfigurationController> mConfigurationController;
    Lazy<DarkIconDispatcher> mDarkIconDispatcher;
    Lazy<DataSaverController> mDataSaverController;
    private final ArrayMap<Object, Object> mDependencies;
    Lazy<DevicePolicyManagerWrapper> mDevicePolicyManagerWrapper;
    Lazy<DeviceProvisionedController> mDeviceProvisionedController;
    Lazy<DisplayController> mDisplayController;
    Lazy<DisplayImeController> mDisplayImeController;
    Lazy<DisplayMetrics> mDisplayMetrics;
    Lazy<Divider> mDivider;
    Lazy<DockManager> mDockManager;
    Lazy<DozeParameters> mDozeParameters;
    DumpManager mDumpManager;
    Lazy<EnhancedEstimates> mEnhancedEstimates;
    Lazy<ExtensionController> mExtensionController;
    Lazy<FlashlightController> mFlashlightController;
    Lazy<ForegroundServiceController> mForegroundServiceController;
    Lazy<ForegroundServiceNotificationListener> mForegroundServiceNotificationListener;
    Lazy<FragmentService> mFragmentService;
    Lazy<GarbageMonitor> mGarbageMonitor;
    Lazy<HotspotController> mHotspotController;
    Lazy<INotificationManager> mINotificationManager;
    Lazy<IStatusBarService> mIStatusBarService;
    Lazy<IWindowManager> mIWindowManager;
    Lazy<KeyguardDismissUtil> mKeyguardDismissUtil;
    Lazy<NotificationEntryManager.KeyguardEnvironment> mKeyguardEnvironment;
    Lazy<KeyguardStateController> mKeyguardMonitor;
    Lazy<KeyguardSecurityModel> mKeyguardSecurityModel;
    Lazy<KeyguardUpdateMonitor> mKeyguardUpdateMonitor;
    Lazy<LeakDetector> mLeakDetector;
    Lazy<String> mLeakReportEmail;
    Lazy<LeakReporter> mLeakReporter;
    Lazy<LightBarController> mLightBarController;
    Lazy<LocalBluetoothManager> mLocalBluetoothManager;
    Lazy<LocationController> mLocationController;
    Lazy<LockscreenGestureLogger> mLockscreenGestureLogger;
    Lazy<Handler> mMainHandler;
    Lazy<Looper> mMainLooper;
    Lazy<ManagedProfileController> mManagedProfileController;
    Lazy<MetricsLogger> mMetricsLogger;
    Lazy<NavigationModeController> mNavBarModeController;
    Lazy<NavigationBarController> mNavigationBarController;
    Lazy<NetworkController> mNetworkController;
    Lazy<NextAlarmController> mNextAlarmController;
    Lazy<NightDisplayListener> mNightDisplayListener;
    Lazy<NotificationAlertingManager> mNotificationAlertingManager;
    Lazy<NotificationBlockingHelperManager> mNotificationBlockingHelperManager;
    Lazy<NotificationEntryManager> mNotificationEntryManager;
    Lazy<NotificationFilter> mNotificationFilter;
    Lazy<NotificationGroupAlertTransferHelper> mNotificationGroupAlertTransferHelper;
    Lazy<NotificationGroupManager> mNotificationGroupManager;
    Lazy<NotificationGutsManager> mNotificationGutsManager;
    Lazy<NotificationListener> mNotificationListener;
    Lazy<NotificationLockscreenUserManager> mNotificationLockscreenUserManager;
    Lazy<NotificationLogger> mNotificationLogger;
    Lazy<NotificationMediaManager> mNotificationMediaManager;
    Lazy<NotificationRemoteInputManager> mNotificationRemoteInputManager;
    Lazy<NotificationRemoteInputManager.Callback> mNotificationRemoteInputManagerCallback;
    Lazy<NotificationShadeWindowController> mNotificationShadeWindowController;
    Lazy<NotificationViewHierarchyManager> mNotificationViewHierarchyManager;
    Lazy<OverviewProxyService> mOverviewProxyService;
    Lazy<PackageManagerWrapper> mPackageManagerWrapper;
    Lazy<PluginDependencyProvider> mPluginDependencyProvider;
    Lazy<PluginManager> mPluginManager;
    Lazy<ProtoTracer> mProtoTracer;
    private final ArrayMap<Object, LazyDependencyCreator> mProviders;
    Lazy<Recents> mRecents;
    Lazy<RecordingController> mRecordingController;
    Lazy<RemoteInputQuickSettingsDisabler> mRemoteInputQuickSettingsDisabler;
    Lazy<RotationLockController> mRotationLockController;
    Lazy<ScreenLifecycle> mScreenLifecycle;
    Lazy<SecurityController> mSecurityController;
    Lazy<SensorPrivacyController> mSensorPrivacyController;
    Lazy<SensorPrivacyManager> mSensorPrivacyManager;
    Lazy<ShadeController> mShadeController;
    Lazy<SmartReplyConstants> mSmartReplyConstants;
    Lazy<SmartReplyController> mSmartReplyController;
    Lazy<StatusBar> mStatusBar;
    Lazy<StatusBarIconController> mStatusBarIconController;
    Lazy<StatusBarStateController> mStatusBarStateController;
    Lazy<SysUiState> mSysUiStateFlagsContainer;
    Lazy<SystemWindows> mSystemWindows;
    Lazy<SysuiColorExtractor> mSysuiColorExtractor;
    Lazy<StatusBarWindowController> mTempStatusBarWindowController;
    Lazy<Handler> mTimeTickHandler;
    Lazy<TunablePadding.TunablePaddingService> mTunablePaddingService;
    Lazy<TunerService> mTunerService;
    Lazy<UiOffloadThread> mUiOffloadThread;
    Lazy<UserInfoController> mUserInfoController;
    Lazy<UserSwitcherController> mUserSwitcherController;
    Lazy<VibratorHelper> mVibratorHelper;
    Lazy<VisualStabilityManager> mVisualStabilityManager;
    Lazy<VolumeDialogController> mVolumeDialogController;
    Lazy<WakefulnessLifecycle> mWakefulnessLifecycle;
    Lazy<IWallpaperManager> mWallpaperManager;
    Lazy<PowerUI.WarningsUI> mWarningsUI;
    Lazy<ZenModeController> mZenModeController;
    
    static {
        BG_LOOPER = new DependencyKey<Looper>("background_looper");
        MAIN_LOOPER = new DependencyKey<Looper>("main_looper");
        TIME_TICK_HANDLER = new DependencyKey<Handler>("time_tick_handler");
        MAIN_HANDLER = new DependencyKey<Handler>("main_handler");
        LEAK_REPORT_EMAIL = new DependencyKey<String>("leak_report_email");
    }
    
    public Dependency() {
        this.mDependencies = (ArrayMap<Object, Object>)new ArrayMap();
        this.mProviders = (ArrayMap<Object, LazyDependencyCreator>)new ArrayMap();
    }
    
    public static <T> void destroy(final Class<T> clazz, final Consumer<T> consumer) {
        Dependency.sDependency.destroyDependency((Class<Object>)clazz, (Consumer<Object>)consumer);
    }
    
    private <T> void destroyDependency(final Class<T> clazz, final Consumer<T> consumer) {
        final Object remove = this.mDependencies.remove((Object)clazz);
        if (remove instanceof Dumpable) {
            this.mDumpManager.unregisterDumpable(remove.getClass().getName());
        }
        if (remove != null && consumer != null) {
            consumer.accept((T)remove);
        }
    }
    
    @Deprecated
    public static <T> T get(final DependencyKey<T> dependencyKey) {
        return (T)Dependency.sDependency.getDependency((DependencyKey<Object>)dependencyKey);
    }
    
    @Deprecated
    public static <T> T get(final Class<T> clazz) {
        return (T)Dependency.sDependency.getDependency((Class<Object>)clazz);
    }
    
    private <T> T getDependencyInner(final Object o) {
        synchronized (this) {
            Object value;
            if ((value = this.mDependencies.get(o)) == null) {
                final Object dependency = this.createDependency(o);
                this.mDependencies.put(o, dependency);
                value = dependency;
                if (this.autoRegisterModulesForDump()) {
                    value = dependency;
                    if (dependency instanceof Dumpable) {
                        this.mDumpManager.registerDumpable(((Dumpable)dependency).getClass().getName(), (Dumpable)dependency);
                        value = dependency;
                    }
                }
            }
            return (T)value;
        }
    }
    
    @VisibleForTesting
    protected boolean autoRegisterModulesForDump() {
        return true;
    }
    
    @VisibleForTesting
    protected <T> T createDependency(final Object obj) {
        Preconditions.checkArgument(obj instanceof DependencyKey || obj instanceof Class);
        final LazyDependencyCreator lazyDependencyCreator = (LazyDependencyCreator)this.mProviders.get(obj);
        if (lazyDependencyCreator != null) {
            return lazyDependencyCreator.createDependency();
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Unsupported dependency ");
        sb.append(obj);
        sb.append(". ");
        sb.append(this.mProviders.size());
        sb.append(" providers known.");
        throw new IllegalArgumentException(sb.toString());
    }
    
    protected final <T> T getDependency(final DependencyKey<T> dependencyKey) {
        return this.getDependencyInner(dependencyKey);
    }
    
    protected final <T> T getDependency(final Class<T> clazz) {
        return this.getDependencyInner(clazz);
    }
    
    protected void start() {
        final ArrayMap<Object, LazyDependencyCreator> mProviders = this.mProviders;
        final DependencyKey<Handler> time_TICK_HANDLER = Dependency.TIME_TICK_HANDLER;
        final Lazy<Handler> mTimeTickHandler = this.mTimeTickHandler;
        Objects.requireNonNull(mTimeTickHandler);
        mProviders.put((Object)time_TICK_HANDLER, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mTimeTickHandler));
        final ArrayMap<Object, LazyDependencyCreator> mProviders2 = this.mProviders;
        final DependencyKey<Looper> bg_LOOPER = Dependency.BG_LOOPER;
        final Lazy<Looper> mBgLooper = this.mBgLooper;
        Objects.requireNonNull(mBgLooper);
        mProviders2.put((Object)bg_LOOPER, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mBgLooper));
        final ArrayMap<Object, LazyDependencyCreator> mProviders3 = this.mProviders;
        final DependencyKey<Looper> main_LOOPER = Dependency.MAIN_LOOPER;
        final Lazy<Looper> mMainLooper = this.mMainLooper;
        Objects.requireNonNull(mMainLooper);
        mProviders3.put((Object)main_LOOPER, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mMainLooper));
        final ArrayMap<Object, LazyDependencyCreator> mProviders4 = this.mProviders;
        final DependencyKey<Handler> main_HANDLER = Dependency.MAIN_HANDLER;
        final Lazy<Handler> mMainHandler = this.mMainHandler;
        Objects.requireNonNull(mMainHandler);
        mProviders4.put((Object)main_HANDLER, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mMainHandler));
        final ArrayMap<Object, LazyDependencyCreator> mProviders5 = this.mProviders;
        final Lazy<ActivityStarter> mActivityStarter = this.mActivityStarter;
        Objects.requireNonNull(mActivityStarter);
        mProviders5.put((Object)ActivityStarter.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mActivityStarter));
        final ArrayMap<Object, LazyDependencyCreator> mProviders6 = this.mProviders;
        final Lazy<BroadcastDispatcher> mBroadcastDispatcher = this.mBroadcastDispatcher;
        Objects.requireNonNull(mBroadcastDispatcher);
        mProviders6.put((Object)BroadcastDispatcher.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mBroadcastDispatcher));
        final ArrayMap<Object, LazyDependencyCreator> mProviders7 = this.mProviders;
        final Lazy<AsyncSensorManager> mAsyncSensorManager = this.mAsyncSensorManager;
        Objects.requireNonNull(mAsyncSensorManager);
        mProviders7.put((Object)AsyncSensorManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mAsyncSensorManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders8 = this.mProviders;
        final Lazy<BluetoothController> mBluetoothController = this.mBluetoothController;
        Objects.requireNonNull(mBluetoothController);
        mProviders8.put((Object)BluetoothController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mBluetoothController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders9 = this.mProviders;
        final Lazy<SensorPrivacyManager> mSensorPrivacyManager = this.mSensorPrivacyManager;
        Objects.requireNonNull(mSensorPrivacyManager);
        mProviders9.put((Object)SensorPrivacyManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mSensorPrivacyManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders10 = this.mProviders;
        final Lazy<LocationController> mLocationController = this.mLocationController;
        Objects.requireNonNull(mLocationController);
        mProviders10.put((Object)LocationController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mLocationController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders11 = this.mProviders;
        final Lazy<RotationLockController> mRotationLockController = this.mRotationLockController;
        Objects.requireNonNull(mRotationLockController);
        mProviders11.put((Object)RotationLockController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mRotationLockController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders12 = this.mProviders;
        final Lazy<NetworkController> mNetworkController = this.mNetworkController;
        Objects.requireNonNull(mNetworkController);
        mProviders12.put((Object)NetworkController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNetworkController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders13 = this.mProviders;
        final Lazy<ZenModeController> mZenModeController = this.mZenModeController;
        Objects.requireNonNull(mZenModeController);
        mProviders13.put((Object)ZenModeController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mZenModeController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders14 = this.mProviders;
        final Lazy<HotspotController> mHotspotController = this.mHotspotController;
        Objects.requireNonNull(mHotspotController);
        mProviders14.put((Object)HotspotController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mHotspotController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders15 = this.mProviders;
        final Lazy<CastController> mCastController = this.mCastController;
        Objects.requireNonNull(mCastController);
        mProviders15.put((Object)CastController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mCastController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders16 = this.mProviders;
        final Lazy<FlashlightController> mFlashlightController = this.mFlashlightController;
        Objects.requireNonNull(mFlashlightController);
        mProviders16.put((Object)FlashlightController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mFlashlightController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders17 = this.mProviders;
        final Lazy<KeyguardStateController> mKeyguardMonitor = this.mKeyguardMonitor;
        Objects.requireNonNull(mKeyguardMonitor);
        mProviders17.put((Object)KeyguardStateController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mKeyguardMonitor));
        final ArrayMap<Object, LazyDependencyCreator> mProviders18 = this.mProviders;
        final Lazy<KeyguardUpdateMonitor> mKeyguardUpdateMonitor = this.mKeyguardUpdateMonitor;
        Objects.requireNonNull(mKeyguardUpdateMonitor);
        mProviders18.put((Object)KeyguardUpdateMonitor.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mKeyguardUpdateMonitor));
        final ArrayMap<Object, LazyDependencyCreator> mProviders19 = this.mProviders;
        final Lazy<UserSwitcherController> mUserSwitcherController = this.mUserSwitcherController;
        Objects.requireNonNull(mUserSwitcherController);
        mProviders19.put((Object)UserSwitcherController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mUserSwitcherController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders20 = this.mProviders;
        final Lazy<UserInfoController> mUserInfoController = this.mUserInfoController;
        Objects.requireNonNull(mUserInfoController);
        mProviders20.put((Object)UserInfoController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mUserInfoController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders21 = this.mProviders;
        final Lazy<BatteryController> mBatteryController = this.mBatteryController;
        Objects.requireNonNull(mBatteryController);
        mProviders21.put((Object)BatteryController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mBatteryController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders22 = this.mProviders;
        final Lazy<NightDisplayListener> mNightDisplayListener = this.mNightDisplayListener;
        Objects.requireNonNull(mNightDisplayListener);
        mProviders22.put((Object)NightDisplayListener.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNightDisplayListener));
        final ArrayMap<Object, LazyDependencyCreator> mProviders23 = this.mProviders;
        final Lazy<ManagedProfileController> mManagedProfileController = this.mManagedProfileController;
        Objects.requireNonNull(mManagedProfileController);
        mProviders23.put((Object)ManagedProfileController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mManagedProfileController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders24 = this.mProviders;
        final Lazy<NextAlarmController> mNextAlarmController = this.mNextAlarmController;
        Objects.requireNonNull(mNextAlarmController);
        mProviders24.put((Object)NextAlarmController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNextAlarmController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders25 = this.mProviders;
        final Lazy<DataSaverController> mDataSaverController = this.mDataSaverController;
        Objects.requireNonNull(mDataSaverController);
        mProviders25.put((Object)DataSaverController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mDataSaverController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders26 = this.mProviders;
        final Lazy<AccessibilityController> mAccessibilityController = this.mAccessibilityController;
        Objects.requireNonNull(mAccessibilityController);
        mProviders26.put((Object)AccessibilityController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mAccessibilityController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders27 = this.mProviders;
        final Lazy<DeviceProvisionedController> mDeviceProvisionedController = this.mDeviceProvisionedController;
        Objects.requireNonNull(mDeviceProvisionedController);
        mProviders27.put((Object)DeviceProvisionedController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mDeviceProvisionedController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders28 = this.mProviders;
        final Lazy<PluginManager> mPluginManager = this.mPluginManager;
        Objects.requireNonNull(mPluginManager);
        mProviders28.put((Object)PluginManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mPluginManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders29 = this.mProviders;
        final Lazy<AssistManager> mAssistManager = this.mAssistManager;
        Objects.requireNonNull(mAssistManager);
        mProviders29.put((Object)AssistManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mAssistManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders30 = this.mProviders;
        final Lazy<SecurityController> mSecurityController = this.mSecurityController;
        Objects.requireNonNull(mSecurityController);
        mProviders30.put((Object)SecurityController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mSecurityController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders31 = this.mProviders;
        final Lazy<LeakDetector> mLeakDetector = this.mLeakDetector;
        Objects.requireNonNull(mLeakDetector);
        mProviders31.put((Object)LeakDetector.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mLeakDetector));
        final ArrayMap<Object, LazyDependencyCreator> mProviders32 = this.mProviders;
        final DependencyKey<String> leak_REPORT_EMAIL = Dependency.LEAK_REPORT_EMAIL;
        final Lazy<String> mLeakReportEmail = this.mLeakReportEmail;
        Objects.requireNonNull(mLeakReportEmail);
        mProviders32.put((Object)leak_REPORT_EMAIL, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mLeakReportEmail));
        final ArrayMap<Object, LazyDependencyCreator> mProviders33 = this.mProviders;
        final Lazy<LeakReporter> mLeakReporter = this.mLeakReporter;
        Objects.requireNonNull(mLeakReporter);
        mProviders33.put((Object)LeakReporter.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mLeakReporter));
        final ArrayMap<Object, LazyDependencyCreator> mProviders34 = this.mProviders;
        final Lazy<GarbageMonitor> mGarbageMonitor = this.mGarbageMonitor;
        Objects.requireNonNull(mGarbageMonitor);
        mProviders34.put((Object)GarbageMonitor.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mGarbageMonitor));
        final ArrayMap<Object, LazyDependencyCreator> mProviders35 = this.mProviders;
        final Lazy<TunerService> mTunerService = this.mTunerService;
        Objects.requireNonNull(mTunerService);
        mProviders35.put((Object)TunerService.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mTunerService));
        final ArrayMap<Object, LazyDependencyCreator> mProviders36 = this.mProviders;
        final Lazy<NotificationShadeWindowController> mNotificationShadeWindowController = this.mNotificationShadeWindowController;
        Objects.requireNonNull(mNotificationShadeWindowController);
        mProviders36.put((Object)NotificationShadeWindowController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationShadeWindowController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders37 = this.mProviders;
        final Lazy<StatusBarWindowController> mTempStatusBarWindowController = this.mTempStatusBarWindowController;
        Objects.requireNonNull(mTempStatusBarWindowController);
        mProviders37.put((Object)StatusBarWindowController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mTempStatusBarWindowController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders38 = this.mProviders;
        final Lazy<DarkIconDispatcher> mDarkIconDispatcher = this.mDarkIconDispatcher;
        Objects.requireNonNull(mDarkIconDispatcher);
        mProviders38.put((Object)DarkIconDispatcher.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mDarkIconDispatcher));
        final ArrayMap<Object, LazyDependencyCreator> mProviders39 = this.mProviders;
        final Lazy<ConfigurationController> mConfigurationController = this.mConfigurationController;
        Objects.requireNonNull(mConfigurationController);
        mProviders39.put((Object)ConfigurationController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mConfigurationController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders40 = this.mProviders;
        final Lazy<StatusBarIconController> mStatusBarIconController = this.mStatusBarIconController;
        Objects.requireNonNull(mStatusBarIconController);
        mProviders40.put((Object)StatusBarIconController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mStatusBarIconController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders41 = this.mProviders;
        final Lazy<ScreenLifecycle> mScreenLifecycle = this.mScreenLifecycle;
        Objects.requireNonNull(mScreenLifecycle);
        mProviders41.put((Object)ScreenLifecycle.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mScreenLifecycle));
        final ArrayMap<Object, LazyDependencyCreator> mProviders42 = this.mProviders;
        final Lazy<WakefulnessLifecycle> mWakefulnessLifecycle = this.mWakefulnessLifecycle;
        Objects.requireNonNull(mWakefulnessLifecycle);
        mProviders42.put((Object)WakefulnessLifecycle.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mWakefulnessLifecycle));
        final ArrayMap<Object, LazyDependencyCreator> mProviders43 = this.mProviders;
        final Lazy<FragmentService> mFragmentService = this.mFragmentService;
        Objects.requireNonNull(mFragmentService);
        mProviders43.put((Object)FragmentService.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mFragmentService));
        final ArrayMap<Object, LazyDependencyCreator> mProviders44 = this.mProviders;
        final Lazy<ExtensionController> mExtensionController = this.mExtensionController;
        Objects.requireNonNull(mExtensionController);
        mProviders44.put((Object)ExtensionController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mExtensionController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders45 = this.mProviders;
        final Lazy<PluginDependencyProvider> mPluginDependencyProvider = this.mPluginDependencyProvider;
        Objects.requireNonNull(mPluginDependencyProvider);
        mProviders45.put((Object)PluginDependencyProvider.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mPluginDependencyProvider));
        final ArrayMap<Object, LazyDependencyCreator> mProviders46 = this.mProviders;
        final Lazy<LocalBluetoothManager> mLocalBluetoothManager = this.mLocalBluetoothManager;
        Objects.requireNonNull(mLocalBluetoothManager);
        mProviders46.put((Object)LocalBluetoothManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mLocalBluetoothManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders47 = this.mProviders;
        final Lazy<VolumeDialogController> mVolumeDialogController = this.mVolumeDialogController;
        Objects.requireNonNull(mVolumeDialogController);
        mProviders47.put((Object)VolumeDialogController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mVolumeDialogController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders48 = this.mProviders;
        final Lazy<MetricsLogger> mMetricsLogger = this.mMetricsLogger;
        Objects.requireNonNull(mMetricsLogger);
        mProviders48.put((Object)MetricsLogger.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mMetricsLogger));
        final ArrayMap<Object, LazyDependencyCreator> mProviders49 = this.mProviders;
        final Lazy<AccessibilityManagerWrapper> mAccessibilityManagerWrapper = this.mAccessibilityManagerWrapper;
        Objects.requireNonNull(mAccessibilityManagerWrapper);
        mProviders49.put((Object)AccessibilityManagerWrapper.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mAccessibilityManagerWrapper));
        final ArrayMap<Object, LazyDependencyCreator> mProviders50 = this.mProviders;
        final Lazy<SysuiColorExtractor> mSysuiColorExtractor = this.mSysuiColorExtractor;
        Objects.requireNonNull(mSysuiColorExtractor);
        mProviders50.put((Object)SysuiColorExtractor.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mSysuiColorExtractor));
        final ArrayMap<Object, LazyDependencyCreator> mProviders51 = this.mProviders;
        final Lazy<TunablePadding.TunablePaddingService> mTunablePaddingService = this.mTunablePaddingService;
        Objects.requireNonNull(mTunablePaddingService);
        mProviders51.put((Object)TunablePadding.TunablePaddingService.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mTunablePaddingService));
        final ArrayMap<Object, LazyDependencyCreator> mProviders52 = this.mProviders;
        final Lazy<ForegroundServiceController> mForegroundServiceController = this.mForegroundServiceController;
        Objects.requireNonNull(mForegroundServiceController);
        mProviders52.put((Object)ForegroundServiceController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mForegroundServiceController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders53 = this.mProviders;
        final Lazy<UiOffloadThread> mUiOffloadThread = this.mUiOffloadThread;
        Objects.requireNonNull(mUiOffloadThread);
        mProviders53.put((Object)UiOffloadThread.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mUiOffloadThread));
        final ArrayMap<Object, LazyDependencyCreator> mProviders54 = this.mProviders;
        final Lazy<PowerUI.WarningsUI> mWarningsUI = this.mWarningsUI;
        Objects.requireNonNull(mWarningsUI);
        mProviders54.put((Object)PowerUI.WarningsUI.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mWarningsUI));
        final ArrayMap<Object, LazyDependencyCreator> mProviders55 = this.mProviders;
        final Lazy<LightBarController> mLightBarController = this.mLightBarController;
        Objects.requireNonNull(mLightBarController);
        mProviders55.put((Object)LightBarController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mLightBarController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders56 = this.mProviders;
        final Lazy<IWindowManager> miWindowManager = this.mIWindowManager;
        Objects.requireNonNull(miWindowManager);
        mProviders56.put((Object)IWindowManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(miWindowManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders57 = this.mProviders;
        final Lazy<OverviewProxyService> mOverviewProxyService = this.mOverviewProxyService;
        Objects.requireNonNull(mOverviewProxyService);
        mProviders57.put((Object)OverviewProxyService.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mOverviewProxyService));
        final ArrayMap<Object, LazyDependencyCreator> mProviders58 = this.mProviders;
        final Lazy<NavigationModeController> mNavBarModeController = this.mNavBarModeController;
        Objects.requireNonNull(mNavBarModeController);
        mProviders58.put((Object)NavigationModeController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNavBarModeController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders59 = this.mProviders;
        final Lazy<EnhancedEstimates> mEnhancedEstimates = this.mEnhancedEstimates;
        Objects.requireNonNull(mEnhancedEstimates);
        mProviders59.put((Object)EnhancedEstimates.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mEnhancedEstimates));
        final ArrayMap<Object, LazyDependencyCreator> mProviders60 = this.mProviders;
        final Lazy<VibratorHelper> mVibratorHelper = this.mVibratorHelper;
        Objects.requireNonNull(mVibratorHelper);
        mProviders60.put((Object)VibratorHelper.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mVibratorHelper));
        final ArrayMap<Object, LazyDependencyCreator> mProviders61 = this.mProviders;
        final Lazy<IStatusBarService> miStatusBarService = this.mIStatusBarService;
        Objects.requireNonNull(miStatusBarService);
        mProviders61.put((Object)IStatusBarService.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(miStatusBarService));
        final ArrayMap<Object, LazyDependencyCreator> mProviders62 = this.mProviders;
        final Lazy<DisplayMetrics> mDisplayMetrics = this.mDisplayMetrics;
        Objects.requireNonNull(mDisplayMetrics);
        mProviders62.put((Object)DisplayMetrics.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mDisplayMetrics));
        final ArrayMap<Object, LazyDependencyCreator> mProviders63 = this.mProviders;
        final Lazy<LockscreenGestureLogger> mLockscreenGestureLogger = this.mLockscreenGestureLogger;
        Objects.requireNonNull(mLockscreenGestureLogger);
        mProviders63.put((Object)LockscreenGestureLogger.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mLockscreenGestureLogger));
        final ArrayMap<Object, LazyDependencyCreator> mProviders64 = this.mProviders;
        final Lazy<NotificationEntryManager.KeyguardEnvironment> mKeyguardEnvironment = this.mKeyguardEnvironment;
        Objects.requireNonNull(mKeyguardEnvironment);
        mProviders64.put((Object)NotificationEntryManager.KeyguardEnvironment.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mKeyguardEnvironment));
        final ArrayMap<Object, LazyDependencyCreator> mProviders65 = this.mProviders;
        final Lazy<ShadeController> mShadeController = this.mShadeController;
        Objects.requireNonNull(mShadeController);
        mProviders65.put((Object)ShadeController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mShadeController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders66 = this.mProviders;
        final Lazy<NotificationRemoteInputManager.Callback> mNotificationRemoteInputManagerCallback = this.mNotificationRemoteInputManagerCallback;
        Objects.requireNonNull(mNotificationRemoteInputManagerCallback);
        mProviders66.put((Object)NotificationRemoteInputManager.Callback.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationRemoteInputManagerCallback));
        final ArrayMap<Object, LazyDependencyCreator> mProviders67 = this.mProviders;
        final Lazy<AppOpsController> mAppOpsController = this.mAppOpsController;
        Objects.requireNonNull(mAppOpsController);
        mProviders67.put((Object)AppOpsController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mAppOpsController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders68 = this.mProviders;
        final Lazy<NavigationBarController> mNavigationBarController = this.mNavigationBarController;
        Objects.requireNonNull(mNavigationBarController);
        mProviders68.put((Object)NavigationBarController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNavigationBarController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders69 = this.mProviders;
        final Lazy<StatusBarStateController> mStatusBarStateController = this.mStatusBarStateController;
        Objects.requireNonNull(mStatusBarStateController);
        mProviders69.put((Object)StatusBarStateController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mStatusBarStateController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders70 = this.mProviders;
        final Lazy<NotificationLockscreenUserManager> mNotificationLockscreenUserManager = this.mNotificationLockscreenUserManager;
        Objects.requireNonNull(mNotificationLockscreenUserManager);
        mProviders70.put((Object)NotificationLockscreenUserManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationLockscreenUserManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders71 = this.mProviders;
        final Lazy<VisualStabilityManager> mVisualStabilityManager = this.mVisualStabilityManager;
        Objects.requireNonNull(mVisualStabilityManager);
        mProviders71.put((Object)VisualStabilityManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mVisualStabilityManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders72 = this.mProviders;
        final Lazy<NotificationGroupManager> mNotificationGroupManager = this.mNotificationGroupManager;
        Objects.requireNonNull(mNotificationGroupManager);
        mProviders72.put((Object)NotificationGroupManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationGroupManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders73 = this.mProviders;
        final Lazy<NotificationGroupAlertTransferHelper> mNotificationGroupAlertTransferHelper = this.mNotificationGroupAlertTransferHelper;
        Objects.requireNonNull(mNotificationGroupAlertTransferHelper);
        mProviders73.put((Object)NotificationGroupAlertTransferHelper.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationGroupAlertTransferHelper));
        final ArrayMap<Object, LazyDependencyCreator> mProviders74 = this.mProviders;
        final Lazy<NotificationMediaManager> mNotificationMediaManager = this.mNotificationMediaManager;
        Objects.requireNonNull(mNotificationMediaManager);
        mProviders74.put((Object)NotificationMediaManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationMediaManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders75 = this.mProviders;
        final Lazy<NotificationGutsManager> mNotificationGutsManager = this.mNotificationGutsManager;
        Objects.requireNonNull(mNotificationGutsManager);
        mProviders75.put((Object)NotificationGutsManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationGutsManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders76 = this.mProviders;
        final Lazy<NotificationBlockingHelperManager> mNotificationBlockingHelperManager = this.mNotificationBlockingHelperManager;
        Objects.requireNonNull(mNotificationBlockingHelperManager);
        mProviders76.put((Object)NotificationBlockingHelperManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationBlockingHelperManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders77 = this.mProviders;
        final Lazy<NotificationRemoteInputManager> mNotificationRemoteInputManager = this.mNotificationRemoteInputManager;
        Objects.requireNonNull(mNotificationRemoteInputManager);
        mProviders77.put((Object)NotificationRemoteInputManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationRemoteInputManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders78 = this.mProviders;
        final Lazy<SmartReplyConstants> mSmartReplyConstants = this.mSmartReplyConstants;
        Objects.requireNonNull(mSmartReplyConstants);
        mProviders78.put((Object)SmartReplyConstants.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mSmartReplyConstants));
        final ArrayMap<Object, LazyDependencyCreator> mProviders79 = this.mProviders;
        final Lazy<NotificationListener> mNotificationListener = this.mNotificationListener;
        Objects.requireNonNull(mNotificationListener);
        mProviders79.put((Object)NotificationListener.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationListener));
        final ArrayMap<Object, LazyDependencyCreator> mProviders80 = this.mProviders;
        final Lazy<NotificationLogger> mNotificationLogger = this.mNotificationLogger;
        Objects.requireNonNull(mNotificationLogger);
        mProviders80.put((Object)NotificationLogger.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationLogger));
        final ArrayMap<Object, LazyDependencyCreator> mProviders81 = this.mProviders;
        final Lazy<NotificationViewHierarchyManager> mNotificationViewHierarchyManager = this.mNotificationViewHierarchyManager;
        Objects.requireNonNull(mNotificationViewHierarchyManager);
        mProviders81.put((Object)NotificationViewHierarchyManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationViewHierarchyManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders82 = this.mProviders;
        final Lazy<NotificationFilter> mNotificationFilter = this.mNotificationFilter;
        Objects.requireNonNull(mNotificationFilter);
        mProviders82.put((Object)NotificationFilter.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationFilter));
        final ArrayMap<Object, LazyDependencyCreator> mProviders83 = this.mProviders;
        final Lazy<KeyguardDismissUtil> mKeyguardDismissUtil = this.mKeyguardDismissUtil;
        Objects.requireNonNull(mKeyguardDismissUtil);
        mProviders83.put((Object)KeyguardDismissUtil.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mKeyguardDismissUtil));
        final ArrayMap<Object, LazyDependencyCreator> mProviders84 = this.mProviders;
        final Lazy<SmartReplyController> mSmartReplyController = this.mSmartReplyController;
        Objects.requireNonNull(mSmartReplyController);
        mProviders84.put((Object)SmartReplyController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mSmartReplyController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders85 = this.mProviders;
        final Lazy<RemoteInputQuickSettingsDisabler> mRemoteInputQuickSettingsDisabler = this.mRemoteInputQuickSettingsDisabler;
        Objects.requireNonNull(mRemoteInputQuickSettingsDisabler);
        mProviders85.put((Object)RemoteInputQuickSettingsDisabler.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mRemoteInputQuickSettingsDisabler));
        final ArrayMap<Object, LazyDependencyCreator> mProviders86 = this.mProviders;
        final Lazy<BubbleController> mBubbleController = this.mBubbleController;
        Objects.requireNonNull(mBubbleController);
        mProviders86.put((Object)BubbleController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mBubbleController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders87 = this.mProviders;
        final Lazy<NotificationEntryManager> mNotificationEntryManager = this.mNotificationEntryManager;
        Objects.requireNonNull(mNotificationEntryManager);
        mProviders87.put((Object)NotificationEntryManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationEntryManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders88 = this.mProviders;
        final Lazy<NotificationAlertingManager> mNotificationAlertingManager = this.mNotificationAlertingManager;
        Objects.requireNonNull(mNotificationAlertingManager);
        mProviders88.put((Object)NotificationAlertingManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mNotificationAlertingManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders89 = this.mProviders;
        final Lazy<ForegroundServiceNotificationListener> mForegroundServiceNotificationListener = this.mForegroundServiceNotificationListener;
        Objects.requireNonNull(mForegroundServiceNotificationListener);
        mProviders89.put((Object)ForegroundServiceNotificationListener.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mForegroundServiceNotificationListener));
        final ArrayMap<Object, LazyDependencyCreator> mProviders90 = this.mProviders;
        final Lazy<ClockManager> mClockManager = this.mClockManager;
        Objects.requireNonNull(mClockManager);
        mProviders90.put((Object)ClockManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mClockManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders91 = this.mProviders;
        final Lazy<ActivityManagerWrapper> mActivityManagerWrapper = this.mActivityManagerWrapper;
        Objects.requireNonNull(mActivityManagerWrapper);
        mProviders91.put((Object)ActivityManagerWrapper.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mActivityManagerWrapper));
        final ArrayMap<Object, LazyDependencyCreator> mProviders92 = this.mProviders;
        final Lazy<DevicePolicyManagerWrapper> mDevicePolicyManagerWrapper = this.mDevicePolicyManagerWrapper;
        Objects.requireNonNull(mDevicePolicyManagerWrapper);
        mProviders92.put((Object)DevicePolicyManagerWrapper.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mDevicePolicyManagerWrapper));
        final ArrayMap<Object, LazyDependencyCreator> mProviders93 = this.mProviders;
        final Lazy<PackageManagerWrapper> mPackageManagerWrapper = this.mPackageManagerWrapper;
        Objects.requireNonNull(mPackageManagerWrapper);
        mProviders93.put((Object)PackageManagerWrapper.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mPackageManagerWrapper));
        final ArrayMap<Object, LazyDependencyCreator> mProviders94 = this.mProviders;
        final Lazy<SensorPrivacyController> mSensorPrivacyController = this.mSensorPrivacyController;
        Objects.requireNonNull(mSensorPrivacyController);
        mProviders94.put((Object)SensorPrivacyController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mSensorPrivacyController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders95 = this.mProviders;
        final Lazy<DockManager> mDockManager = this.mDockManager;
        Objects.requireNonNull(mDockManager);
        mProviders95.put((Object)DockManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mDockManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders96 = this.mProviders;
        final Lazy<ChannelEditorDialogController> mChannelEditorDialogController = this.mChannelEditorDialogController;
        Objects.requireNonNull(mChannelEditorDialogController);
        mProviders96.put((Object)ChannelEditorDialogController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mChannelEditorDialogController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders97 = this.mProviders;
        final Lazy<INotificationManager> miNotificationManager = this.mINotificationManager;
        Objects.requireNonNull(miNotificationManager);
        mProviders97.put((Object)INotificationManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(miNotificationManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders98 = this.mProviders;
        final Lazy<SysUiState> mSysUiStateFlagsContainer = this.mSysUiStateFlagsContainer;
        Objects.requireNonNull(mSysUiStateFlagsContainer);
        mProviders98.put((Object)SysUiState.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mSysUiStateFlagsContainer));
        final ArrayMap<Object, LazyDependencyCreator> mProviders99 = this.mProviders;
        final Lazy<AlarmManager> mAlarmManager = this.mAlarmManager;
        Objects.requireNonNull(mAlarmManager);
        mProviders99.put((Object)AlarmManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mAlarmManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders100 = this.mProviders;
        final Lazy<KeyguardSecurityModel> mKeyguardSecurityModel = this.mKeyguardSecurityModel;
        Objects.requireNonNull(mKeyguardSecurityModel);
        mProviders100.put((Object)KeyguardSecurityModel.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mKeyguardSecurityModel));
        final ArrayMap<Object, LazyDependencyCreator> mProviders101 = this.mProviders;
        final Lazy<DozeParameters> mDozeParameters = this.mDozeParameters;
        Objects.requireNonNull(mDozeParameters);
        mProviders101.put((Object)DozeParameters.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mDozeParameters));
        final ArrayMap<Object, LazyDependencyCreator> mProviders102 = this.mProviders;
        final Lazy<IWallpaperManager> mWallpaperManager = this.mWallpaperManager;
        Objects.requireNonNull(mWallpaperManager);
        mProviders102.put((Object)IWallpaperManager.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mWallpaperManager));
        final ArrayMap<Object, LazyDependencyCreator> mProviders103 = this.mProviders;
        final Lazy<CommandQueue> mCommandQueue = this.mCommandQueue;
        Objects.requireNonNull(mCommandQueue);
        mProviders103.put((Object)CommandQueue.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mCommandQueue));
        final ArrayMap<Object, LazyDependencyCreator> mProviders104 = this.mProviders;
        final Lazy<Recents> mRecents = this.mRecents;
        Objects.requireNonNull(mRecents);
        mProviders104.put((Object)Recents.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mRecents));
        final ArrayMap<Object, LazyDependencyCreator> mProviders105 = this.mProviders;
        final Lazy<StatusBar> mStatusBar = this.mStatusBar;
        Objects.requireNonNull(mStatusBar);
        mProviders105.put((Object)StatusBar.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mStatusBar));
        final ArrayMap<Object, LazyDependencyCreator> mProviders106 = this.mProviders;
        final Lazy<DisplayController> mDisplayController = this.mDisplayController;
        Objects.requireNonNull(mDisplayController);
        mProviders106.put((Object)DisplayController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mDisplayController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders107 = this.mProviders;
        final Lazy<SystemWindows> mSystemWindows = this.mSystemWindows;
        Objects.requireNonNull(mSystemWindows);
        mProviders107.put((Object)SystemWindows.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mSystemWindows));
        final ArrayMap<Object, LazyDependencyCreator> mProviders108 = this.mProviders;
        final Lazy<DisplayImeController> mDisplayImeController = this.mDisplayImeController;
        Objects.requireNonNull(mDisplayImeController);
        mProviders108.put((Object)DisplayImeController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mDisplayImeController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders109 = this.mProviders;
        final Lazy<ProtoTracer> mProtoTracer = this.mProtoTracer;
        Objects.requireNonNull(mProtoTracer);
        mProviders109.put((Object)ProtoTracer.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mProtoTracer));
        final ArrayMap<Object, LazyDependencyCreator> mProviders110 = this.mProviders;
        final Lazy<AutoHideController> mAutoHideController = this.mAutoHideController;
        Objects.requireNonNull(mAutoHideController);
        mProviders110.put((Object)AutoHideController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mAutoHideController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders111 = this.mProviders;
        final Lazy<RecordingController> mRecordingController = this.mRecordingController;
        Objects.requireNonNull(mRecordingController);
        mProviders111.put((Object)RecordingController.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mRecordingController));
        final ArrayMap<Object, LazyDependencyCreator> mProviders112 = this.mProviders;
        final Lazy<Divider> mDivider = this.mDivider;
        Objects.requireNonNull(mDivider);
        mProviders112.put((Object)Divider.class, (Object)new _$$Lambda$Vs_MsjQwuYhfrxzUr7AqZvcfoH4(mDivider));
        Dependency.sDependency = this;
    }
    
    public interface DependencyInjector
    {
        void createSystemUI(final Dependency p0);
    }
    
    public static final class DependencyKey<V>
    {
        private final String mDisplayName;
        
        public DependencyKey(final String mDisplayName) {
            this.mDisplayName = mDisplayName;
        }
        
        @Override
        public String toString() {
            return this.mDisplayName;
        }
    }
    
    private interface LazyDependencyCreator<T>
    {
        T createDependency();
    }
}
