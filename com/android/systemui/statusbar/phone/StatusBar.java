// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.os.Message;
import android.graphics.Rect;
import com.android.systemui.shared.system.WindowManagerWrapper;
import android.app.IWallpaperManager;
import com.android.systemui.plugins.Plugin;
import android.util.ArraySet;
import com.android.systemui.plugins.OverlayPlugin;
import com.android.systemui.plugins.PluginListener;
import android.app.IWallpaperManager$Stub;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.IStatusBarService$Stub;
import android.view.accessibility.AccessibilityManager;
import com.android.systemui.R$bool;
import android.service.dreams.IDreamManager$Stub;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.charging.WirelessChargingAnimation;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.AutoReinflateContainer;
import com.android.internal.view.AppearanceRegion;
import android.content.res.Configuration;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.systemui.R$string;
import android.view.ThreadedRenderer;
import android.content.IntentFilter;
import com.android.systemui.R$array;
import com.android.systemui.fragments.ExtensionFragmentListener;
import java.util.function.Supplier;
import android.os.UserManager;
import android.widget.ImageView;
import com.android.systemui.statusbar.BackDropView;
import com.android.systemui.statusbar.ScrimView;
import com.android.systemui.statusbar.AutoHideUiElement;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.SystemUIFactory;
import android.view.WindowManagerGlobal;
import android.util.Slog;
import android.view.KeyEvent;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.view.View$OnTouchListener;
import java.util.Iterator;
import java.util.Map;
import com.android.systemui.Prefs;
import com.android.systemui.R$style;
import com.android.systemui.Dumpable;
import android.app.StatusBarManager;
import java.io.FileDescriptor;
import android.provider.Settings$Global;
import android.view.ViewStub;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.internal.statusbar.RegisterStatusBarResult;
import java.util.function.Consumer;
import android.content.ComponentName;
import android.view.InsetsState;
import android.os.Trace;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.EventLogTags;
import android.app.PendingIntent$OnFinished;
import android.app.ProfilerInfo;
import android.os.IBinder;
import android.app.IApplicationThread;
import android.app.ActivityTaskManager;
import android.app.PendingIntent;
import android.os.Debug;
import android.app.Notification;
import android.service.notification.StatusBarNotification;
import android.app.PendingIntent$CanceledException;
import android.util.EventLog;
import android.net.Uri;
import android.os.Parcelable;
import com.android.systemui.classifier.FalsingLog;
import java.io.Writer;
import android.os.SystemProperties;
import java.io.StringWriter;
import com.android.systemui.qs.QSFragment;
import com.android.systemui.plugins.qs.QS;
import com.android.systemui.R$id;
import android.app.Fragment;
import android.view.MotionEvent;
import android.os.SystemClock;
import java.util.Objects;
import android.os.AsyncTask;
import android.app.ActivityManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.os.UserHandle;
import android.content.pm.PackageManager;
import android.app.ActivityOptions;
import android.view.RemoteAnimationAdapter;
import java.io.PrintWriter;
import android.widget.DateTimeView;
import android.provider.Settings$Secure;
import android.app.NotificationManager;
import com.android.systemui.DejankUtils;
import android.os.Bundle;
import com.android.systemui.statusbar.KeyboardShortcuts;
import android.os.Looper;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Intent;
import android.util.Log;
import com.android.systemui.statusbar.notification.interruption.NotificationAlertingManager;
import android.content.Context;
import android.os.RemoteException;
import android.content.pm.IPackageManager$Stub;
import android.os.ServiceManager;
import android.media.AudioAttributes$Builder;
import android.view.WindowManager;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import android.graphics.PointF;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.volume.VolumeComponent;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.VibratorHelper;
import android.os.Vibrator;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import android.app.UiModeManager;
import java.util.concurrent.Executor;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;
import android.metrics.LogMaker;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.phone.dagger.StatusBarComponent;
import javax.inject.Provider;
import android.view.ViewGroup;
import com.android.systemui.recents.ScreenPinningRequest;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.recents.Recents;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.statusbar.PulseExpansionHandler;
import android.os.PowerManager;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.statusbar.notification.init.NotificationsController;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.statusbar.NotificationMediaManager;
import android.os.Handler;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcher;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.app.KeyguardManager;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.InitController;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import android.view.View$OnClickListener;
import android.os.PowerManager$WakeLock;
import com.android.systemui.statusbar.GestureRecorder;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import android.service.dreams.IDreamManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.stackdivider.Divider;
import java.util.Optional;
import android.util.DisplayMetrics;
import android.view.Display;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.app.admin.DevicePolicyManager;
import com.android.systemui.plugins.DarkIconDispatcher;
import android.graphics.Point;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.statusbar.notification.interruption.BypassHeadsUpNotifier;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.policy.BrightnessMirrorController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.internal.statusbar.IStatusBarService;
import android.content.BroadcastReceiver;
import com.android.systemui.assist.AssistManager;
import dagger.Lazy;
import android.view.View;
import com.android.systemui.ActivityIntentHelper;
import android.media.AudioAttributes;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.internal.colorextraction.ColorExtractor$OnColorsChangedListener;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.DemoMode;
import com.android.systemui.SystemUI;

public class StatusBar extends SystemUI implements DemoMode, ActivityStarter, KeyguardStateController.Callback, OnHeadsUpChangedListener, Callbacks, ColorExtractor$OnColorsChangedListener, ConfigurationListener, StateListener, ActivityLaunchAnimator.Callback
{
    public static final boolean ONLY_CORE_APPS;
    private static final AudioAttributes VIBRATION_ATTRIBUTES;
    private ActivityIntentHelper mActivityIntentHelper;
    private ActivityLaunchAnimator mActivityLaunchAnimator;
    private View mAmbientIndicationContainer;
    private boolean mAppFullscreen;
    private boolean mAppImmersive;
    private int mAppearance;
    private final Lazy<AssistManager> mAssistManagerLazy;
    private final AutoHideController mAutoHideController;
    private final BroadcastReceiver mBannerActionBroadcastReceiver;
    protected IStatusBarService mBarService;
    private final BatteryController mBatteryController;
    private BiometricUnlockController mBiometricUnlockController;
    private final Lazy<BiometricUnlockController> mBiometricUnlockControllerLazy;
    protected boolean mBouncerShowing;
    private boolean mBouncerWasShowingWhenHidden;
    private BrightnessMirrorController mBrightnessMirrorController;
    private boolean mBrightnessMirrorVisible;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final BroadcastReceiver mBroadcastReceiver;
    private final BubbleController mBubbleController;
    private final BubbleController.BubbleExpandListener mBubbleExpandListener;
    private final BypassHeadsUpNotifier mBypassHeadsUpNotifier;
    private long[] mCameraLaunchGestureVibePattern;
    private final Runnable mCheckBarModes;
    private final SysuiColorExtractor mColorExtractor;
    protected final CommandQueue mCommandQueue;
    private final ConfigurationController mConfigurationController;
    private final Point mCurrentDisplaySize;
    private final DarkIconDispatcher mDarkIconDispatcher;
    private boolean mDemoMode;
    private boolean mDemoModeAllowed;
    private final BroadcastReceiver mDemoReceiver;
    protected boolean mDeviceInteractive;
    protected DevicePolicyManager mDevicePolicyManager;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private int mDisabled1;
    private int mDisabled2;
    private final DismissCallbackRegistry mDismissCallbackRegistry;
    protected Display mDisplay;
    private int mDisplayId;
    private final DisplayMetrics mDisplayMetrics;
    private final Optional<Divider> mDividerOptional;
    private final DozeParameters mDozeParameters;
    protected DozeScrimController mDozeScrimController;
    @VisibleForTesting
    DozeServiceHost mDozeServiceHost;
    protected boolean mDozing;
    private NotificationEntry mDraggedDownEntry;
    private IDreamManager mDreamManager;
    private final DynamicPrivacyController mDynamicPrivacyController;
    private boolean mExpandedVisible;
    private final ExtensionController mExtensionController;
    private final FalsingManager mFalsingManager;
    private final GestureRecorder mGestureRec;
    protected PowerManager$WakeLock mGestureWakeLock;
    private final View$OnClickListener mGoToLockedShadeListener;
    private final NotificationGroupManager mGroupManager;
    private final NotificationGutsManager mGutsManager;
    protected final H mHandler;
    private HeadsUpAppearanceController mHeadsUpAppearanceController;
    private final HeadsUpManagerPhone mHeadsUpManager;
    private boolean mHideIconsForBouncer;
    private final StatusBarIconController mIconController;
    private PhoneStatusBarPolicy mIconPolicy;
    private final InitController mInitController;
    private int mInteractingWindows;
    protected boolean mIsKeyguard;
    private boolean mIsOccluded;
    private final KeyguardBypassController mKeyguardBypassController;
    private final KeyguardDismissUtil mKeyguardDismissUtil;
    KeyguardIndicationController mKeyguardIndicationController;
    protected KeyguardManager mKeyguardManager;
    private final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private KeyguardUserSwitcher mKeyguardUserSwitcher;
    private final KeyguardViewMediator mKeyguardViewMediator;
    private ViewMediatorCallback mKeyguardViewMediatorCallback;
    private int mLastCameraLaunchSource;
    private int mLastLoggedStateFingerprint;
    private boolean mLaunchCameraOnFinishedGoingToSleep;
    private boolean mLaunchCameraWhenFinishedWaking;
    private Runnable mLaunchTransitionEndRunnable;
    private final LightBarController mLightBarController;
    private final LightsOutNotifController mLightsOutNotifController;
    private final LockscreenLockIconController mLockscreenLockIconController;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    protected LockscreenWallpaper mLockscreenWallpaper;
    private final Lazy<LockscreenWallpaper> mLockscreenWallpaperLazy;
    private final Handler mMainThreadHandler;
    private final NotificationMediaManager mMediaManager;
    private final MetricsLogger mMetricsLogger;
    private final NavigationBarController mNavigationBarController;
    private final NetworkController mNetworkController;
    private boolean mNoAnimationOnNextBarModeChange;
    private NotificationActivityStarter mNotificationActivityStarter;
    protected NotificationIconAreaController mNotificationIconAreaController;
    protected final NotificationInterruptStateProvider mNotificationInterruptStateProvider;
    private final NotificationLogger mNotificationLogger;
    protected NotificationPanelViewController mNotificationPanelViewController;
    private Lazy<NotificationShadeDepthController> mNotificationShadeDepthControllerLazy;
    protected NotificationShadeWindowController mNotificationShadeWindowController;
    protected NotificationShadeWindowView mNotificationShadeWindowView;
    protected NotificationShadeWindowViewController mNotificationShadeWindowViewController;
    protected NotificationShelf mNotificationShelf;
    private NotificationsController mNotificationsController;
    protected boolean mPanelExpanded;
    protected StatusBarWindowView mPhoneStatusBarWindow;
    private final PluginDependencyProvider mPluginDependencyProvider;
    private final PluginManager mPluginManager;
    private final PowerManager mPowerManager;
    protected StatusBarNotificationPresenter mPresenter;
    private final PulseExpansionHandler mPulseExpansionHandler;
    private QSPanel mQSPanel;
    private final Object mQueueLock;
    private final Optional<Recents> mRecentsOptional;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final RemoteInputQuickSettingsDisabler mRemoteInputQuickSettingsDisabler;
    private View mReportRejectedTouch;
    private final ScreenLifecycle mScreenLifecycle;
    final ScreenLifecycle.Observer mScreenObserver;
    private final ScreenPinningRequest mScreenPinningRequest;
    private final ScrimController mScrimController;
    private final ShadeController mShadeController;
    protected ViewGroup mStackScroller;
    protected int mState;
    private final Provider<StatusBarComponent.Builder> mStatusBarComponentBuilder;
    protected StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private int mStatusBarMode;
    private final StatusBarNotificationActivityStarter.Builder mStatusBarNotificationActivityStarterBuilder;
    private final SysuiStatusBarStateController mStatusBarStateController;
    private LogMaker mStatusBarStateLog;
    private final StatusBarTouchableRegionManager mStatusBarTouchableRegionManager;
    protected PhoneStatusBarView mStatusBarView;
    protected StatusBarWindowController mStatusBarWindowController;
    private boolean mStatusBarWindowHidden;
    private int mStatusBarWindowState;
    final Runnable mStopTracing;
    private final SuperStatusBarViewFactory mSuperStatusBarViewFactory;
    private final int[] mTmpInt2;
    private boolean mTopHidesStatusBar;
    private boolean mTransientShown;
    private final Executor mUiBgExecutor;
    private UiModeManager mUiModeManager;
    private final ScrimController.Callback mUnlockScrimCallback;
    private final KeyguardUpdateMonitorCallback mUpdateCallback;
    private final UserInfoControllerImpl mUserInfoControllerImpl;
    @VisibleForTesting
    protected boolean mUserSetup;
    private final DeviceProvisionedController.DeviceProvisionedListener mUserSetupObserver;
    private final UserSwitcherController mUserSwitcherController;
    private boolean mVibrateOnOpening;
    private Vibrator mVibrator;
    private final VibratorHelper mVibratorHelper;
    private final NotificationViewHierarchyManager mViewHierarchyManager;
    protected boolean mVisible;
    private boolean mVisibleToUser;
    private final VisualStabilityManager mVisualStabilityManager;
    private final VolumeComponent mVolumeComponent;
    private boolean mWakeUpComingFromTouch;
    private final NotificationWakeUpCoordinator mWakeUpCoordinator;
    private PointF mWakeUpTouchLocation;
    private final WakefulnessLifecycle mWakefulnessLifecycle;
    @VisibleForTesting
    final WakefulnessLifecycle.Observer mWakefulnessObserver;
    private final BroadcastReceiver mWallpaperChangedReceiver;
    private boolean mWallpaperSupported;
    private boolean mWereIconsJustHidden;
    protected WindowManager mWindowManager;
    
    static {
        VIBRATION_ATTRIBUTES = new AudioAttributes$Builder().setContentType(4).setUsage(13).build();
        boolean onlyCoreApps;
        try {
            onlyCoreApps = IPackageManager$Stub.asInterface(ServiceManager.getService("package")).isOnlyCoreApps();
        }
        catch (RemoteException ex) {
            onlyCoreApps = false;
        }
        ONLY_CORE_APPS = onlyCoreApps;
    }
    
    public StatusBar(final Context context, final NotificationsController mNotificationsController, final LightBarController mLightBarController, final AutoHideController mAutoHideController, final KeyguardUpdateMonitor mKeyguardUpdateMonitor, final StatusBarIconController mIconController, final PulseExpansionHandler mPulseExpansionHandler, final NotificationWakeUpCoordinator mWakeUpCoordinator, final KeyguardBypassController mKeyguardBypassController, final KeyguardStateController mKeyguardStateController, final HeadsUpManagerPhone mHeadsUpManager, final DynamicPrivacyController mDynamicPrivacyController, final BypassHeadsUpNotifier mBypassHeadsUpNotifier, final FalsingManager mFalsingManager, final BroadcastDispatcher mBroadcastDispatcher, final RemoteInputQuickSettingsDisabler mRemoteInputQuickSettingsDisabler, final NotificationGutsManager mGutsManager, final NotificationLogger mNotificationLogger, final NotificationInterruptStateProvider mNotificationInterruptStateProvider, final NotificationViewHierarchyManager mViewHierarchyManager, final KeyguardViewMediator mKeyguardViewMediator, final NotificationAlertingManager notificationAlertingManager, final DisplayMetrics mDisplayMetrics, final MetricsLogger mMetricsLogger, final Executor mUiBgExecutor, final NotificationMediaManager mMediaManager, final NotificationLockscreenUserManager mLockscreenUserManager, final NotificationRemoteInputManager mRemoteInputManager, final UserSwitcherController mUserSwitcherController, final NetworkController mNetworkController, final BatteryController mBatteryController, final SysuiColorExtractor mColorExtractor, final ScreenLifecycle mScreenLifecycle, final WakefulnessLifecycle mWakefulnessLifecycle, final SysuiStatusBarStateController mStatusBarStateController, final VibratorHelper mVibratorHelper, final BubbleController mBubbleController, final NotificationGroupManager mGroupManager, final VisualStabilityManager mVisualStabilityManager, final DeviceProvisionedController mDeviceProvisionedController, final NavigationBarController mNavigationBarController, final Lazy<AssistManager> mAssistManagerLazy, final ConfigurationController mConfigurationController, final NotificationShadeWindowController mNotificationShadeWindowController, final LockscreenLockIconController mLockscreenLockIconController, final DozeParameters mDozeParameters, final ScrimController mScrimController, final KeyguardLiftController keyguardLiftController, final Lazy<LockscreenWallpaper> mLockscreenWallpaperLazy, final Lazy<BiometricUnlockController> mBiometricUnlockControllerLazy, final DozeServiceHost mDozeServiceHost, final PowerManager mPowerManager, final ScreenPinningRequest mScreenPinningRequest, final DozeScrimController mDozeScrimController, final VolumeComponent mVolumeComponent, final CommandQueue mCommandQueue, final Optional<Recents> mRecentsOptional, final Provider<StatusBarComponent.Builder> mStatusBarComponentBuilder, final PluginManager mPluginManager, final Optional<Divider> mDividerOptional, final LightsOutNotifController mLightsOutNotifController, final StatusBarNotificationActivityStarter.Builder mStatusBarNotificationActivityStarterBuilder, final ShadeController mShadeController, final SuperStatusBarViewFactory mSuperStatusBarViewFactory, final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager, final ViewMediatorCallback mKeyguardViewMediatorCallback, final InitController mInitController, final DarkIconDispatcher mDarkIconDispatcher, final Handler receiverHandler, final PluginDependencyProvider mPluginDependencyProvider, final KeyguardDismissUtil mKeyguardDismissUtil, final ExtensionController mExtensionController, final UserInfoControllerImpl mUserInfoControllerImpl, final PhoneStatusBarPolicy mIconPolicy, final KeyguardIndicationController mKeyguardIndicationController, final DismissCallbackRegistry mDismissCallbackRegistry, final Lazy<NotificationShadeDepthController> mNotificationShadeDepthControllerLazy, final StatusBarTouchableRegionManager mStatusBarTouchableRegionManager) {
        super(context);
        this.mCurrentDisplaySize = new Point();
        this.mStatusBarWindowState = 0;
        this.mQueueLock = new Object();
        this.mDisabled1 = 0;
        this.mDisabled2 = 0;
        this.mGestureRec = null;
        this.mUserSetup = false;
        this.mUserSetupObserver = new DeviceProvisionedController.DeviceProvisionedListener() {
            @Override
            public void onUserSetupChanged() {
                final boolean userSetup = StatusBar.this.mDeviceProvisionedController.isUserSetup(StatusBar.this.mDeviceProvisionedController.getCurrentUser());
                final StringBuilder sb = new StringBuilder();
                sb.append("mUserSetupObserver - DeviceProvisionedListener called for user ");
                sb.append(StatusBar.this.mDeviceProvisionedController.getCurrentUser());
                Log.d("StatusBar", sb.toString());
                final StatusBar this$0 = StatusBar.this;
                if (userSetup != this$0.mUserSetup) {
                    this$0.mUserSetup = userSetup;
                    if (!userSetup && this$0.mStatusBarView != null) {
                        this$0.animateCollapseQuickSettings();
                    }
                    final StatusBar this$2 = StatusBar.this;
                    final NotificationPanelViewController mNotificationPanelViewController = this$2.mNotificationPanelViewController;
                    if (mNotificationPanelViewController != null) {
                        mNotificationPanelViewController.setUserSetupComplete(this$2.mUserSetup);
                    }
                    StatusBar.this.updateQsExpansionEnabled();
                }
            }
        };
        this.mHandler = this.createHandler();
        this.mWallpaperChangedReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (!StatusBar.this.mWallpaperSupported) {
                    Log.wtf("StatusBar", "WallpaperManager not supported");
                    return;
                }
                final WallpaperInfo wallpaperInfo = ((WallpaperManager)context.getSystemService((Class)WallpaperManager.class)).getWallpaperInfo(-2);
                final boolean boolean1 = StatusBar.this.mContext.getResources().getBoolean(17891425);
                final boolean displayNeedsBlanking = StatusBar.this.mDozeParameters.getDisplayNeedsBlanking();
                final boolean b = true;
                boolean b2 = false;
                Label_0102: {
                    if (boolean1) {
                        if (wallpaperInfo == null) {
                            b2 = b;
                            if (displayNeedsBlanking ^ true) {
                                break Label_0102;
                            }
                        }
                        if (wallpaperInfo != null && wallpaperInfo.supportsAmbientMode()) {
                            b2 = b;
                            break Label_0102;
                        }
                    }
                    b2 = false;
                }
                StatusBar.this.mNotificationShadeWindowController.setWallpaperSupportsAmbientMode(b2);
                StatusBar.this.mScrimController.setWallpaperSupportsAmbientMode(b2);
            }
        };
        this.mTmpInt2 = new int[2];
        this.mUnlockScrimCallback = new ScrimController.Callback() {
            @Override
            public void onCancelled() {
                this.onFinished();
            }
            
            @Override
            public void onFinished() {
                final StatusBar this$0 = StatusBar.this;
                if (this$0.mStatusBarKeyguardViewManager == null) {
                    Log.w("StatusBar", "Tried to notify keyguard visibility when mStatusBarKeyguardViewManager was null");
                    return;
                }
                if (this$0.mKeyguardStateController.isKeyguardFadingAway()) {
                    StatusBar.this.mStatusBarKeyguardViewManager.onKeyguardFadedAway();
                }
            }
        };
        this.mGoToLockedShadeListener = (View$OnClickListener)new _$$Lambda$StatusBar$yGW3L_liHoPrdVSisJBkD7OsnTE(this);
        this.mUpdateCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onDreamingStateChanged(final boolean b) {
                if (b) {
                    StatusBar.this.maybeEscalateHeadsUp();
                }
            }
            
            @Override
            public void onStrongAuthStateChanged(final int n) {
                super.onStrongAuthStateChanged(n);
                StatusBar.this.mNotificationsController.requestNotificationUpdate("onStrongAuthStateChanged");
            }
        };
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        this.mCheckBarModes = new _$$Lambda$KBnY14rlKZ6x8gvk_goBuFrr5eE(this);
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                final boolean equals = "android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(action);
                final int n = 0;
                if (equals) {
                    KeyboardShortcuts.dismiss();
                    if (StatusBar.this.mRemoteInputManager.getController() != null) {
                        StatusBar.this.mRemoteInputManager.getController().closeRemoteInputs();
                    }
                    if (StatusBar.this.mBubbleController.isStackExpanded()) {
                        StatusBar.this.mBubbleController.collapseStack();
                    }
                    if (StatusBar.this.mLockscreenUserManager.isCurrentProfile(this.getSendingUserId())) {
                        final String stringExtra = intent.getStringExtra("reason");
                        int n2 = n;
                        if (stringExtra != null) {
                            n2 = n;
                            if (stringExtra.equals("recentapps")) {
                                n2 = 2;
                            }
                        }
                        StatusBar.this.mShadeController.animateCollapsePanels(n2);
                    }
                }
                else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                    final NotificationShadeWindowController mNotificationShadeWindowController = StatusBar.this.mNotificationShadeWindowController;
                    if (mNotificationShadeWindowController != null) {
                        mNotificationShadeWindowController.setNotTouchable(false);
                    }
                    if (StatusBar.this.mBubbleController.isStackExpanded()) {
                        StatusBar.this.mBubbleController.collapseStack();
                    }
                    StatusBar.this.finishBarAnimations();
                    StatusBar.this.resetUserExpandedStates();
                }
                else if ("android.app.action.SHOW_DEVICE_MONITORING_DIALOG".equals(action)) {
                    StatusBar.this.mQSPanel.showDeviceMonitoringDialog();
                }
            }
        };
        this.mDemoReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent obj) {
                final String action = obj.getAction();
                if ("com.android.systemui.demo".equals(action)) {
                    final Bundle extras = obj.getExtras();
                    if (extras != null) {
                        final String lowerCase = extras.getString("command", "").trim().toLowerCase();
                        if (lowerCase.length() > 0) {
                            try {
                                StatusBar.this.dispatchDemoCommand(lowerCase, extras);
                            }
                            finally {
                                final StringBuilder sb = new StringBuilder();
                                sb.append("Error running demo command, intent=");
                                sb.append(obj);
                                final Throwable t;
                                Log.w("StatusBar", sb.toString(), t);
                            }
                        }
                    }
                }
                else {
                    "fake_artwork".equals(action);
                }
            }
        };
        this.mStopTracing = new _$$Lambda$StatusBar$_fO_jShMXABz_MmDk7j6MlDaRW0(this);
        this.mWakefulnessObserver = new WakefulnessLifecycle.Observer() {
            @Override
            public void onFinishedGoingToSleep() {
                StatusBar.this.mNotificationPanelViewController.onAffordanceLaunchEnded();
                StatusBar.this.releaseGestureWakeLock();
                StatusBar.this.mLaunchCameraWhenFinishedWaking = false;
                final StatusBar this$0 = StatusBar.this;
                this$0.mWakeUpComingFromTouch = (this$0.mDeviceInteractive = false);
                StatusBar.this.mWakeUpTouchLocation = null;
                StatusBar.this.mVisualStabilityManager.setScreenOn(false);
                StatusBar.this.updateVisibleToUser();
                StatusBar.this.updateNotificationPanelTouchState();
                StatusBar.this.mNotificationShadeWindowViewController.cancelCurrentTouch();
                if (StatusBar.this.mLaunchCameraOnFinishedGoingToSleep) {
                    StatusBar.this.mLaunchCameraOnFinishedGoingToSleep = false;
                    StatusBar.this.mHandler.post((Runnable)new _$$Lambda$StatusBar$12$y9_RRyD4rDeCN3cFnbhrxNLuI7g(this));
                }
                StatusBar.this.updateIsKeyguard();
            }
            
            @Override
            public void onFinishedWakingUp() {
                StatusBar.this.mWakeUpCoordinator.setFullyAwake(true);
                StatusBar.this.mBypassHeadsUpNotifier.setFullyAwake(true);
                StatusBar.this.mWakeUpCoordinator.setWakingUp(false);
                if (StatusBar.this.mLaunchCameraWhenFinishedWaking) {
                    final StatusBar this$0 = StatusBar.this;
                    this$0.mNotificationPanelViewController.launchCamera(false, this$0.mLastCameraLaunchSource);
                    StatusBar.this.mLaunchCameraWhenFinishedWaking = false;
                }
                StatusBar.this.updateScrimController();
            }
            
            @Override
            public void onStartedGoingToSleep() {
                DejankUtils.startDetectingBlockingIpcs("StatusBar#onStartedGoingToSleep");
                StatusBar.this.updateNotificationPanelTouchState();
                StatusBar.this.notifyHeadsUpGoingToSleep();
                StatusBar.this.dismissVolumeDialog();
                StatusBar.this.mWakeUpCoordinator.setFullyAwake(false);
                StatusBar.this.mBypassHeadsUpNotifier.setFullyAwake(false);
                StatusBar.this.mKeyguardBypassController.onStartedGoingToSleep();
                DejankUtils.stopDetectingBlockingIpcs("StatusBar#onStartedGoingToSleep");
            }
            
            @Override
            public void onStartedWakingUp() {
                DejankUtils.startDetectingBlockingIpcs("StatusBar#onStartedWakingUp");
                final StatusBar this$0 = StatusBar.this;
                this$0.mDeviceInteractive = true;
                this$0.mWakeUpCoordinator.setWakingUp(true);
                if (!StatusBar.this.mKeyguardBypassController.getBypassEnabled()) {
                    StatusBar.this.mHeadsUpManager.releaseAllImmediately();
                }
                StatusBar.this.mVisualStabilityManager.setScreenOn(true);
                StatusBar.this.updateVisibleToUser();
                StatusBar.this.updateIsKeyguard();
                StatusBar.this.mDozeServiceHost.stopDozing();
                StatusBar.this.updateNotificationPanelTouchState();
                StatusBar.this.mPulseExpansionHandler.onStartedWakingUp();
                DejankUtils.stopDetectingBlockingIpcs("StatusBar#onStartedWakingUp");
            }
        };
        this.mScreenObserver = new ScreenLifecycle.Observer() {
            @Override
            public void onScreenTurnedOff() {
                StatusBar.this.mFalsingManager.onScreenOff();
                StatusBar.this.mScrimController.onScreenTurnedOff();
                StatusBar.this.updateIsKeyguard();
            }
            
            @Override
            public void onScreenTurnedOn() {
                StatusBar.this.mScrimController.onScreenTurnedOn();
            }
            
            @Override
            public void onScreenTurningOn() {
                StatusBar.this.mFalsingManager.onScreenTurningOn();
                StatusBar.this.mNotificationPanelViewController.onScreenTurningOn();
            }
        };
        this.mBannerActionBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if ("com.android.systemui.statusbar.banner_action_cancel".equals(action) || "com.android.systemui.statusbar.banner_action_setup".equals(action)) {
                    ((NotificationManager)StatusBar.this.mContext.getSystemService("notification")).cancel(5);
                    Settings$Secure.putInt(StatusBar.this.mContext.getContentResolver(), "show_note_about_notification_hiding", 0);
                    if ("com.android.systemui.statusbar.banner_action_setup".equals(action)) {
                        StatusBar.this.mShadeController.animateCollapsePanels(2, true);
                        StatusBar.this.mContext.startActivity(new Intent("android.settings.ACTION_APP_NOTIFICATION_REDACTION").addFlags(268435456));
                    }
                }
            }
        };
        this.mNotificationsController = mNotificationsController;
        this.mLightBarController = mLightBarController;
        this.mAutoHideController = mAutoHideController;
        this.mKeyguardUpdateMonitor = mKeyguardUpdateMonitor;
        this.mIconController = mIconController;
        this.mPulseExpansionHandler = mPulseExpansionHandler;
        this.mWakeUpCoordinator = mWakeUpCoordinator;
        this.mKeyguardBypassController = mKeyguardBypassController;
        this.mKeyguardStateController = mKeyguardStateController;
        this.mHeadsUpManager = mHeadsUpManager;
        this.mKeyguardIndicationController = mKeyguardIndicationController;
        this.mStatusBarTouchableRegionManager = mStatusBarTouchableRegionManager;
        this.mDynamicPrivacyController = mDynamicPrivacyController;
        this.mBypassHeadsUpNotifier = mBypassHeadsUpNotifier;
        this.mFalsingManager = mFalsingManager;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mRemoteInputQuickSettingsDisabler = mRemoteInputQuickSettingsDisabler;
        this.mGutsManager = mGutsManager;
        this.mNotificationLogger = mNotificationLogger;
        this.mNotificationInterruptStateProvider = mNotificationInterruptStateProvider;
        this.mViewHierarchyManager = mViewHierarchyManager;
        this.mKeyguardViewMediator = mKeyguardViewMediator;
        this.mDisplayMetrics = mDisplayMetrics;
        this.mMetricsLogger = mMetricsLogger;
        this.mUiBgExecutor = mUiBgExecutor;
        this.mMediaManager = mMediaManager;
        this.mLockscreenUserManager = mLockscreenUserManager;
        this.mRemoteInputManager = mRemoteInputManager;
        this.mUserSwitcherController = mUserSwitcherController;
        this.mNetworkController = mNetworkController;
        this.mBatteryController = mBatteryController;
        this.mColorExtractor = mColorExtractor;
        this.mScreenLifecycle = mScreenLifecycle;
        this.mWakefulnessLifecycle = mWakefulnessLifecycle;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mVibratorHelper = mVibratorHelper;
        this.mBubbleController = mBubbleController;
        this.mGroupManager = mGroupManager;
        this.mVisualStabilityManager = mVisualStabilityManager;
        this.mDeviceProvisionedController = mDeviceProvisionedController;
        this.mNavigationBarController = mNavigationBarController;
        this.mAssistManagerLazy = mAssistManagerLazy;
        this.mConfigurationController = mConfigurationController;
        this.mNotificationShadeWindowController = mNotificationShadeWindowController;
        this.mLockscreenLockIconController = mLockscreenLockIconController;
        this.mDozeServiceHost = mDozeServiceHost;
        this.mPowerManager = mPowerManager;
        this.mDozeParameters = mDozeParameters;
        this.mScrimController = mScrimController;
        this.mLockscreenWallpaperLazy = mLockscreenWallpaperLazy;
        this.mScreenPinningRequest = mScreenPinningRequest;
        this.mDozeScrimController = mDozeScrimController;
        this.mBiometricUnlockControllerLazy = mBiometricUnlockControllerLazy;
        this.mNotificationShadeDepthControllerLazy = mNotificationShadeDepthControllerLazy;
        this.mVolumeComponent = mVolumeComponent;
        this.mCommandQueue = mCommandQueue;
        this.mRecentsOptional = mRecentsOptional;
        this.mStatusBarComponentBuilder = mStatusBarComponentBuilder;
        this.mPluginManager = mPluginManager;
        this.mDividerOptional = mDividerOptional;
        this.mStatusBarNotificationActivityStarterBuilder = mStatusBarNotificationActivityStarterBuilder;
        this.mShadeController = mShadeController;
        this.mSuperStatusBarViewFactory = mSuperStatusBarViewFactory;
        this.mLightsOutNotifController = mLightsOutNotifController;
        this.mStatusBarKeyguardViewManager = mStatusBarKeyguardViewManager;
        this.mKeyguardViewMediatorCallback = mKeyguardViewMediatorCallback;
        this.mInitController = mInitController;
        this.mDarkIconDispatcher = mDarkIconDispatcher;
        this.mPluginDependencyProvider = mPluginDependencyProvider;
        this.mKeyguardDismissUtil = mKeyguardDismissUtil;
        this.mExtensionController = mExtensionController;
        this.mUserInfoControllerImpl = mUserInfoControllerImpl;
        this.mIconPolicy = mIconPolicy;
        this.mDismissCallbackRegistry = mDismissCallbackRegistry;
        this.mBubbleExpandListener = new _$$Lambda$StatusBar$be2UvXBqvJVkeR4_MOL5Z579OFk(this);
        DateTimeView.setReceiverHandler(receiverHandler);
    }
    
    private static int barMode(final boolean b, final int n) {
        if (b) {
            return 1;
        }
        if ((n & 0x5) == 0x5) {
            return 3;
        }
        if ((n & 0x4) != 0x0) {
            return 6;
        }
        if ((n & 0x1) != 0x0) {
            return 4;
        }
        return 0;
    }
    
    private void clearTransient() {
        if (this.mTransientShown) {
            this.mTransientShown = false;
            this.handleTransientChanged();
        }
    }
    
    private void dismissVolumeDialog() {
        final VolumeComponent mVolumeComponent = this.mVolumeComponent;
        if (mVolumeComponent != null) {
            mVolumeComponent.dismissNow();
        }
    }
    
    private void dispatchDemoCommandToView(final String s, final Bundle bundle, final int n) {
        final PhoneStatusBarView mStatusBarView = this.mStatusBarView;
        if (mStatusBarView == null) {
            return;
        }
        final View viewById = mStatusBarView.findViewById(n);
        if (viewById instanceof DemoMode) {
            ((DemoMode)viewById).dispatchDemoCommand(s, bundle);
        }
    }
    
    static void dumpBarTransitions(final PrintWriter printWriter, final String s, final BarTransitions barTransitions) {
        printWriter.print("  ");
        printWriter.print(s);
        printWriter.print(".BarTransitions.mMode=");
        printWriter.println(BarTransitions.modeToString(barTransitions.getMode()));
    }
    
    private void executeWhenUnlocked(final OnDismissAction onDismissAction, final boolean b) {
        if (this.mStatusBarKeyguardViewManager.isShowing() && b) {
            this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
        }
        this.dismissKeyguardThenExecute(onDismissAction, null, false);
    }
    
    private void finishBarAnimations() {
        if (this.mNotificationShadeWindowController != null && this.mNotificationShadeWindowViewController.getBarTransitions() != null) {
            this.mNotificationShadeWindowViewController.getBarTransitions().finishAnimations();
        }
        this.mNavigationBarController.finishBarAnimations(this.mDisplayId);
    }
    
    public static Bundle getActivityOptions(final RemoteAnimationAdapter remoteAnimationAdapter) {
        ActivityOptions activityOptions;
        if (remoteAnimationAdapter != null) {
            activityOptions = ActivityOptions.makeRemoteAnimation(remoteAnimationAdapter);
        }
        else {
            activityOptions = ActivityOptions.makeBasic();
        }
        activityOptions.setLaunchWindowingMode(4);
        return activityOptions.toBundle();
    }
    
    private static int getLoggingFingerprint(final int n, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5) {
        return (n & 0xFF) | (b ? 1 : 0) << 8 | (b2 ? 1 : 0) << 9 | (b3 ? 1 : 0) << 10 | (b4 ? 1 : 0) << 11 | (b5 ? 1 : 0) << 12;
    }
    
    public static PackageManager getPackageManagerForUser(final Context context, final int n) {
        Context packageContextAsUser = context;
        Label_0028: {
            if (n < 0) {
                break Label_0028;
            }
            try {
                packageContextAsUser = context.createPackageContextAsUser(context.getPackageName(), 4, new UserHandle(n));
                return packageContextAsUser.getPackageManager();
            }
            catch (PackageManager$NameNotFoundException ex) {
                packageContextAsUser = context;
                return packageContextAsUser.getPackageManager();
            }
        }
    }
    
    private void handleStartActivityDismissingKeyguard(final Intent intent, final boolean b) {
        this.startActivityDismissingKeyguard(intent, b, true);
    }
    
    private void handleTransientChanged() {
        final int barMode = barMode(this.mTransientShown, this.mAppearance);
        if (this.updateBarMode(barMode)) {
            this.mLightBarController.onStatusBarModeChanged(barMode);
        }
    }
    
    private void handleVisibleToUserChangedImpl(final boolean b) {
        if (b) {
            final boolean hasPinnedHeadsUp = this.mHeadsUpManager.hasPinnedHeadsUp();
            final boolean presenterFullyCollapsed = this.mPresenter.isPresenterFullyCollapsed();
            int n = 1;
            boolean b2 = false;
            Label_0050: {
                if (!presenterFullyCollapsed) {
                    final int mState = this.mState;
                    if (mState == 0 || mState == 2) {
                        b2 = true;
                        break Label_0050;
                    }
                }
                b2 = false;
            }
            final int activeNotificationsCount = this.mNotificationsController.getActiveNotificationsCount();
            if (!hasPinnedHeadsUp || !this.mPresenter.isPresenterFullyCollapsed()) {
                n = activeNotificationsCount;
            }
            this.mUiBgExecutor.execute(new _$$Lambda$StatusBar$CZvoL2x6jbn0LuvEHTO6I5m8x7c(this, b2, n));
        }
        else {
            this.mUiBgExecutor.execute(new _$$Lambda$StatusBar$9Wf6G4QZESrYpTijjquPUgybXFk(this));
        }
    }
    
    private void inflateShelf() {
        (this.mNotificationShelf = this.mSuperStatusBarViewFactory.getNotificationShelf(this.mStackScroller)).setOnClickListener(this.mGoToLockedShadeListener);
    }
    
    private void inflateStatusBarWindow() {
        this.mNotificationShadeWindowView = this.mSuperStatusBarViewFactory.getNotificationShadeWindowView();
        final StatusBarComponent build = this.mStatusBarComponentBuilder.get().statusBarWindowView(this.mNotificationShadeWindowView).build();
        this.mNotificationShadeWindowViewController = build.getNotificationShadeWindowViewController();
        this.mNotificationShadeWindowController.setNotificationShadeView((ViewGroup)this.mNotificationShadeWindowView);
        this.mNotificationShadeWindowViewController.setupExpandedStatusBar();
        this.mStatusBarWindowController = build.getStatusBarWindowController();
        this.mPhoneStatusBarWindow = this.mSuperStatusBarViewFactory.getStatusBarWindowView();
        this.mNotificationPanelViewController = build.getNotificationPanelViewController();
    }
    
    private boolean isGoingToSleep() {
        return this.mWakefulnessLifecycle.getWakefulness() == 3;
    }
    
    private boolean isTransientShown() {
        return this.mTransientShown;
    }
    
    private boolean isWakingUpOrAwake() {
        final int wakefulness = this.mWakefulnessLifecycle.getWakefulness();
        boolean b = true;
        if (wakefulness != 2) {
            b = (this.mWakefulnessLifecycle.getWakefulness() == 1 && b);
        }
        return b;
    }
    
    private void logStateToEventlog() {
        final int showing = this.mStatusBarKeyguardViewManager.isShowing() ? 1 : 0;
        final int occluded = this.mStatusBarKeyguardViewManager.isOccluded() ? 1 : 0;
        final int bouncerShowing = this.mStatusBarKeyguardViewManager.isBouncerShowing() ? 1 : 0;
        final int methodSecure = this.mKeyguardStateController.isMethodSecure() ? 1 : 0;
        final int canDismissLockScreen = this.mKeyguardStateController.canDismissLockScreen() ? 1 : 0;
        final int loggingFingerprint = getLoggingFingerprint(this.mState, (boolean)(showing != 0), (boolean)(occluded != 0), (boolean)(bouncerShowing != 0), (boolean)(methodSecure != 0), (boolean)(canDismissLockScreen != 0));
        if (loggingFingerprint != this.mLastLoggedStateFingerprint) {
            if (this.mStatusBarStateLog == null) {
                this.mStatusBarStateLog = new LogMaker(0);
            }
            final MetricsLogger mMetricsLogger = this.mMetricsLogger;
            final LogMaker mStatusBarStateLog = this.mStatusBarStateLog;
            int category;
            if (bouncerShowing != 0) {
                category = 197;
            }
            else {
                category = 196;
            }
            final LogMaker setCategory = mStatusBarStateLog.setCategory(category);
            int type;
            if (showing != 0) {
                type = 1;
            }
            else {
                type = 2;
            }
            mMetricsLogger.write(setCategory.setType(type).setSubtype(methodSecure));
            EventLogTags.writeSysuiStatusBarState(this.mState, showing, occluded, bouncerShowing, methodSecure, canDismissLockScreen);
            this.mLastLoggedStateFingerprint = loggingFingerprint;
        }
    }
    
    private void onLaunchTransitionFadingEnded() {
        this.mNotificationPanelViewController.setAlpha(1.0f);
        this.mNotificationPanelViewController.onAffordanceLaunchEnded();
        this.releaseGestureWakeLock();
        this.runLaunchTransitionEndRunnable();
        this.mKeyguardStateController.setLaunchTransitionFadingAway(false);
        this.mPresenter.updateMediaMetaData(true, true);
    }
    
    private void onLaunchTransitionTimeout() {
        Log.w("StatusBar", "Launch transition: Timeout!");
        this.mNotificationPanelViewController.onAffordanceLaunchEnded();
        this.releaseGestureWakeLock();
        this.mNotificationPanelViewController.resetViews(false);
    }
    
    private void postOnUiThread(final Runnable runnable) {
        this.mMainThreadHandler.post(runnable);
    }
    
    private void releaseGestureWakeLock() {
        if (this.mGestureWakeLock.isHeld()) {
            this.mGestureWakeLock.release();
        }
    }
    
    private void runLaunchTransitionEndRunnable() {
        final Runnable mLaunchTransitionEndRunnable = this.mLaunchTransitionEndRunnable;
        if (mLaunchTransitionEndRunnable != null) {
            this.mLaunchTransitionEndRunnable = null;
            mLaunchTransitionEndRunnable.run();
        }
    }
    
    private void setUpPresenter() {
        final ActivityLaunchAnimator mActivityLaunchAnimator = new ActivityLaunchAnimator(this.mNotificationShadeWindowViewController, (ActivityLaunchAnimator.Callback)this, this.mNotificationPanelViewController, this.mNotificationShadeDepthControllerLazy.get(), (NotificationListContainer)this.mStackScroller);
        this.mActivityLaunchAnimator = mActivityLaunchAnimator;
        final StatusBarNotificationPresenter statusBarNotificationPresenter = new StatusBarNotificationPresenter(super.mContext, this.mNotificationPanelViewController, this.mHeadsUpManager, this.mNotificationShadeWindowView, this.mStackScroller, this.mDozeScrimController, this.mScrimController, mActivityLaunchAnimator, this.mDynamicPrivacyController, this.mKeyguardStateController, this.mKeyguardIndicationController, this, this.mShadeController, this.mCommandQueue, this.mInitController, this.mNotificationInterruptStateProvider);
        this.mPresenter = statusBarNotificationPresenter;
        this.mNotificationShelf.setOnActivatedListener((ActivatableNotificationView.OnActivatedListener)statusBarNotificationPresenter);
        this.mRemoteInputManager.getController().addCallback((RemoteInputController.Callback)this.mNotificationShadeWindowController);
        final StatusBarNotificationActivityStarter.Builder mStatusBarNotificationActivityStarterBuilder = this.mStatusBarNotificationActivityStarterBuilder;
        mStatusBarNotificationActivityStarterBuilder.setStatusBar(this);
        mStatusBarNotificationActivityStarterBuilder.setActivityLaunchAnimator(this.mActivityLaunchAnimator);
        mStatusBarNotificationActivityStarterBuilder.setNotificationPresenter(this.mPresenter);
        mStatusBarNotificationActivityStarterBuilder.setNotificationPanelViewController(this.mNotificationPanelViewController);
        final StatusBarNotificationActivityStarter build = mStatusBarNotificationActivityStarterBuilder.build();
        this.mNotificationActivityStarter = build;
        this.mGutsManager.setNotificationActivityStarter(build);
        final NotificationsController mNotificationsController = this.mNotificationsController;
        final StatusBarNotificationPresenter mPresenter = this.mPresenter;
        mNotificationsController.initialize(this, mPresenter, (NotificationListContainer)this.mStackScroller, this.mNotificationActivityStarter, mPresenter);
    }
    
    private void showBouncerIfKeyguard() {
        final int mState = this.mState;
        if ((mState == 1 || mState == 2) && !this.mKeyguardViewMediator.isHiding()) {
            this.mStatusBarKeyguardViewManager.showBouncer(true);
        }
    }
    
    private void showTransientUnchecked() {
        if (!this.mTransientShown) {
            this.mTransientShown = true;
            this.mNoAnimationOnNextBarModeChange = true;
            this.handleTransientChanged();
        }
    }
    
    private boolean updateBarMode(final int mStatusBarMode) {
        if (this.mStatusBarMode != mStatusBarMode) {
            this.mStatusBarMode = mStatusBarMode;
            this.checkBarModes();
            this.mAutoHideController.touchAutoHide();
            return true;
        }
        return false;
    }
    
    private void updateDozingState() {
        Trace.traceCounter(4096L, "dozing", (int)(this.mDozing ? 1 : 0));
        Trace.beginSection("StatusBar#updateDozingState");
        final boolean showing = this.mStatusBarKeyguardViewManager.isShowing();
        final boolean b = false;
        final boolean b2 = showing && !this.mStatusBarKeyguardViewManager.isOccluded();
        final boolean b3 = this.mBiometricUnlockController.getMode() == 1;
        boolean b4 = false;
        Label_0121: {
            if (this.mDozing || !this.mDozeServiceHost.shouldAnimateWakeup() || b3) {
                b4 = b;
                if (!this.mDozing) {
                    break Label_0121;
                }
                b4 = b;
                if (!this.mDozeServiceHost.shouldAnimateScreenOff()) {
                    break Label_0121;
                }
                b4 = b;
                if (!b2) {
                    break Label_0121;
                }
            }
            b4 = true;
        }
        this.mNotificationPanelViewController.setDozing(this.mDozing, b4, this.mWakeUpTouchLocation);
        this.updateQsExpansionEnabled();
        Trace.endSection();
    }
    
    private void updateHideIconsForBouncer(final boolean b) {
        final boolean mTopHidesStatusBar = this.mTopHidesStatusBar;
        boolean mHideIconsForBouncer = false;
        final boolean b2 = mTopHidesStatusBar && this.mIsOccluded && (this.mStatusBarWindowHidden || this.mBouncerShowing);
        final boolean b3 = !this.mPanelExpanded && !this.mIsOccluded && this.mBouncerShowing;
        if (b2 || b3) {
            mHideIconsForBouncer = true;
        }
        if (this.mHideIconsForBouncer != mHideIconsForBouncer) {
            this.mHideIconsForBouncer = mHideIconsForBouncer;
            if (!mHideIconsForBouncer && this.mBouncerWasShowingWhenHidden) {
                this.mWereIconsJustHidden = true;
                this.mHandler.postDelayed((Runnable)new _$$Lambda$StatusBar$7K_uOTSmIW_UQzwxpe9Dmc05CLQ(this), 500L);
            }
            else {
                this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, b);
            }
        }
        if (mHideIconsForBouncer) {
            this.mBouncerWasShowingWhenHidden = this.mBouncerShowing;
        }
    }
    
    private void updateKeyguardState() {
        this.mKeyguardStateController.notifyKeyguardState(this.mStatusBarKeyguardViewManager.isShowing(), this.mStatusBarKeyguardViewManager.isOccluded());
    }
    
    private void updatePanelExpansionForKeyguard() {
        if (this.mState == 1 && this.mBiometricUnlockController.getMode() != 1 && !this.mBouncerShowing) {
            this.mShadeController.instantExpandNotificationsPanel();
        }
        else if (this.mState == 3) {
            this.instantCollapseNotificationPanel();
        }
    }
    
    private void updateQsExpansionEnabled() {
        final boolean deviceProvisioned = this.mDeviceProvisionedController.isDeviceProvisioned();
        boolean b = true;
        Label_0077: {
            Label_0075: {
                if (deviceProvisioned) {
                    if (!this.mUserSetup) {
                        final UserSwitcherController mUserSwitcherController = this.mUserSwitcherController;
                        if (mUserSwitcherController != null && mUserSwitcherController.isSimpleUserSwitcher()) {
                            break Label_0075;
                        }
                    }
                    final int mDisabled2 = this.mDisabled2;
                    if ((mDisabled2 & 0x4) == 0x0 && (mDisabled2 & 0x1) == 0x0 && !this.mDozing && !StatusBar.ONLY_CORE_APPS) {
                        break Label_0077;
                    }
                }
            }
            b = false;
        }
        this.mNotificationPanelViewController.setQsExpansionEnabled(b);
        final StringBuilder sb = new StringBuilder();
        sb.append("updateQsExpansionEnabled - QS Expand enabled: ");
        sb.append(b);
        Log.d("StatusBar", sb.toString());
    }
    
    private void updateReportRejectedTouchVisibility() {
        final View mReportRejectedTouch = this.mReportRejectedTouch;
        if (mReportRejectedTouch == null) {
            return;
        }
        int visibility;
        if (this.mState == 1 && !this.mDozing && this.mFalsingManager.isReportingEnabled()) {
            visibility = 0;
        }
        else {
            visibility = 4;
        }
        mReportRejectedTouch.setVisibility(visibility);
    }
    
    private void vibrateForCameraGesture() {
        this.mVibrator.vibrate(this.mCameraLaunchGestureVibePattern, -1);
    }
    
    public static String viewInfo(final View view) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[(");
        sb.append(view.getLeft());
        sb.append(",");
        sb.append(view.getTop());
        sb.append(")(");
        sb.append(view.getRight());
        sb.append(",");
        sb.append(view.getBottom());
        sb.append(") ");
        sb.append(view.getWidth());
        sb.append("x");
        sb.append(view.getHeight());
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public void abortTransient(final int n, final int[] array) {
        if (n != this.mDisplayId) {
            return;
        }
        if (!InsetsState.containsType(array, 0)) {
            return;
        }
        this.clearTransient();
    }
    
    @Override
    public void addQsTile(final ComponentName componentName) {
        final QSPanel mqsPanel = this.mQSPanel;
        if (mqsPanel != null && mqsPanel.getHost() != null) {
            this.mQSPanel.getHost().addTile(componentName);
        }
    }
    
    @Override
    public void animateCollapsePanels(final int n, final boolean b) {
        this.mShadeController.animateCollapsePanels(n, b, false, 1.0f);
    }
    
    public void animateCollapseQuickSettings() {
        if (this.mState == 0) {
            this.mStatusBarView.collapsePanel(true, false, 1.0f);
        }
    }
    
    @Override
    public void animateExpandNotificationsPanel() {
        if (!this.mCommandQueue.panelsEnabled()) {
            return;
        }
        this.mNotificationPanelViewController.expandWithoutQs();
    }
    
    @Override
    public void animateExpandSettingsPanel(final String s) {
        if (!this.mCommandQueue.panelsEnabled()) {
            return;
        }
        if (!this.mUserSetup) {
            return;
        }
        if (s != null) {
            this.mQSPanel.openDetails(s);
        }
        this.mNotificationPanelViewController.expandWithQs();
    }
    
    public void animateKeyguardUnoccluding() {
        this.mNotificationPanelViewController.setExpandedFraction(0.0f);
        this.animateExpandNotificationsPanel();
    }
    
    @Override
    public void appTransitionCancelled(final int n) {
        if (n == this.mDisplayId) {
            this.mDividerOptional.ifPresent((Consumer<? super Divider>)_$$Lambda$0LwwxILcL3cgEtrSMW_qhRkAhLc.INSTANCE);
        }
    }
    
    @Override
    public void appTransitionFinished(final int n) {
        if (n == this.mDisplayId) {
            this.mDividerOptional.ifPresent((Consumer<? super Divider>)_$$Lambda$0LwwxILcL3cgEtrSMW_qhRkAhLc.INSTANCE);
        }
    }
    
    public boolean areLaunchAnimationsEnabled() {
        return this.mState == 0;
    }
    
    boolean areNotificationAlertsDisabled() {
        return (this.mDisabled1 & 0x40000) != 0x0;
    }
    
    void awakenDreams() {
        this.mUiBgExecutor.execute(new _$$Lambda$StatusBar$VT97TmDhKlLj_VDOo_bU8X_zi3w(this));
    }
    
    @Override
    public void cancelPreloadRecentApps() {
        this.mHandler.removeMessages(1023);
        this.mHandler.sendEmptyMessage(1023);
    }
    
    void checkBarMode(final int n, final int n2, final BarTransitions barTransitions) {
        barTransitions.transitionTo(n, !this.mNoAnimationOnNextBarModeChange && this.mDeviceInteractive && n2 != 2);
    }
    
    void checkBarModes() {
        if (this.mDemoMode) {
            return;
        }
        if (this.mNotificationShadeWindowViewController != null && this.getStatusBarTransitions() != null) {
            this.checkBarMode(this.mStatusBarMode, this.mStatusBarWindowState, this.getStatusBarTransitions());
        }
        this.mNavigationBarController.checkNavBarModes(this.mDisplayId);
        this.mNoAnimationOnNextBarModeChange = false;
    }
    
    public void clearNotificationEffects() {
        try {
            this.mBarService.clearNotificationEffects();
        }
        catch (RemoteException ex) {}
    }
    
    @Override
    public void clickTile(final ComponentName componentName) {
        this.mQSPanel.clickTile(componentName);
    }
    
    public void collapseShade() {
        if (this.mNotificationPanelViewController.isTracking()) {
            this.mNotificationShadeWindowViewController.cancelCurrentTouch();
        }
        if (this.mPanelExpanded && this.mState == 0) {
            this.mShadeController.animateCollapsePanels();
        }
    }
    
    public void createAndAddWindows(final RegisterStatusBarResult registerStatusBarResult) {
        this.makeStatusBarView(registerStatusBarResult);
        this.mNotificationShadeWindowController.attach();
        this.mStatusBarWindowController.attach();
    }
    
    protected QS createDefaultQSFragment() {
        return FragmentHostManager.get((View)this.mNotificationShadeWindowView).create(QSFragment.class);
    }
    
    protected H createHandler() {
        return new H();
    }
    
    protected void createNavigationBar(final RegisterStatusBarResult registerStatusBarResult) {
        this.mNavigationBarController.createNavigationBars(true, registerStatusBarResult);
    }
    
    protected void createUserSwitcher() {
        this.mKeyguardUserSwitcher = new KeyguardUserSwitcher(super.mContext, (ViewStub)this.mNotificationShadeWindowView.findViewById(R$id.keyguard_user_switcher), (KeyguardStatusBarView)this.mNotificationShadeWindowView.findViewById(R$id.keyguard_header), this.mNotificationPanelViewController);
    }
    
    @Override
    public void disable(int mStatusBarWindowState, final int mDisabled1, int n, final boolean b) {
        if (mStatusBarWindowState != this.mDisplayId) {
            return;
        }
        final int adjustDisableFlags = this.mRemoteInputQuickSettingsDisabler.adjustDisableFlags(n);
        mStatusBarWindowState = this.mStatusBarWindowState;
        final int n2 = this.mDisabled1 ^ mDisabled1;
        this.mDisabled1 = mDisabled1;
        final int n3 = this.mDisabled2 ^ adjustDisableFlags;
        this.mDisabled2 = adjustDisableFlags;
        final StringBuilder sb = new StringBuilder();
        sb.append("disable<");
        mStatusBarWindowState = (mDisabled1 & 0x10000);
        char c;
        if (mStatusBarWindowState != 0) {
            n = (c = 'E');
        }
        else {
            n = (c = 'e');
        }
        sb.append(c);
        n = (0x10000 & n2);
        char c2;
        if (n != 0) {
            c2 = '!';
        }
        else {
            c2 = ' ';
        }
        sb.append(c2);
        final char c3 = 'I';
        char c4;
        if ((mDisabled1 & 0x20000) != 0x0) {
            c4 = 'I';
        }
        else {
            c4 = 'i';
        }
        sb.append(c4);
        char c5;
        if ((0x20000 & n2) != 0x0) {
            c5 = '!';
        }
        else {
            c5 = ' ';
        }
        sb.append(c5);
        char c6;
        if ((mDisabled1 & 0x40000) != 0x0) {
            c6 = 'A';
        }
        else {
            c6 = 'a';
        }
        sb.append(c6);
        final int n4 = 0x40000 & n2;
        char c7;
        if (n4 != 0) {
            c7 = '!';
        }
        else {
            c7 = ' ';
        }
        sb.append(c7);
        final char c8 = 'S';
        char c9;
        if ((mDisabled1 & 0x100000) != 0x0) {
            c9 = 'S';
        }
        else {
            c9 = 's';
        }
        sb.append(c9);
        char c10;
        if ((0x100000 & n2) != 0x0) {
            c10 = '!';
        }
        else {
            c10 = ' ';
        }
        sb.append(c10);
        char c11;
        if ((mDisabled1 & 0x400000) != 0x0) {
            c11 = 'B';
        }
        else {
            c11 = 'b';
        }
        sb.append(c11);
        char c12;
        if ((0x400000 & n2) != 0x0) {
            c12 = '!';
        }
        else {
            c12 = ' ';
        }
        sb.append(c12);
        char c13;
        if ((mDisabled1 & 0x200000) != 0x0) {
            c13 = 'H';
        }
        else {
            c13 = 'h';
        }
        sb.append(c13);
        char c14;
        if ((0x200000 & n2) != 0x0) {
            c14 = '!';
        }
        else {
            c14 = ' ';
        }
        sb.append(c14);
        final int n5 = mDisabled1 & 0x1000000;
        char c15;
        if (n5 != 0) {
            c15 = 'R';
        }
        else {
            c15 = 'r';
        }
        sb.append(c15);
        final int n6 = n2 & 0x1000000;
        char c16;
        if (n6 != 0) {
            c16 = '!';
        }
        else {
            c16 = ' ';
        }
        sb.append(c16);
        char c17;
        if ((mDisabled1 & 0x800000) != 0x0) {
            c17 = 'C';
        }
        else {
            c17 = 'c';
        }
        sb.append(c17);
        char c18;
        if ((n2 & 0x800000) != 0x0) {
            c18 = '!';
        }
        else {
            c18 = ' ';
        }
        sb.append(c18);
        char c19;
        if ((mDisabled1 & 0x2000000) != 0x0) {
            c19 = c8;
        }
        else {
            c19 = 's';
        }
        sb.append(c19);
        char c20;
        if ((n2 & 0x2000000) != 0x0) {
            c20 = '!';
        }
        else {
            c20 = ' ';
        }
        sb.append(c20);
        sb.append("> disable2<");
        char c21;
        if ((adjustDisableFlags & 0x1) != 0x0) {
            c21 = 'Q';
        }
        else {
            c21 = 'q';
        }
        sb.append(c21);
        final int n7 = n3 & 0x1;
        char c22;
        if (n7 != 0) {
            c22 = '!';
        }
        else {
            c22 = ' ';
        }
        sb.append(c22);
        char c23;
        if ((adjustDisableFlags & 0x2) != 0x0) {
            c23 = c3;
        }
        else {
            c23 = 'i';
        }
        sb.append(c23);
        char c24;
        if ((n3 & 0x2) != 0x0) {
            c24 = '!';
        }
        else {
            c24 = ' ';
        }
        sb.append(c24);
        char c25;
        if ((adjustDisableFlags & 0x4) != 0x0) {
            c25 = 'N';
        }
        else {
            c25 = 'n';
        }
        sb.append(c25);
        final int n8 = n3 & 0x4;
        char c26;
        if (n8 != 0) {
            c26 = '!';
        }
        else {
            c26 = ' ';
        }
        sb.append(c26);
        sb.append('>');
        Log.d("StatusBar", sb.toString());
        if (n != 0 && mStatusBarWindowState != 0) {
            this.mShadeController.animateCollapsePanels();
        }
        if (n6 != 0 && n5 != 0) {
            this.mHandler.removeMessages(1020);
            this.mHandler.sendEmptyMessage(1020);
        }
        if (n4 != 0 && this.areNotificationAlertsDisabled()) {
            this.mHeadsUpManager.releaseAllImmediately();
        }
        if (n7 != 0) {
            this.updateQsExpansionEnabled();
        }
        if (n8 != 0) {
            this.updateQsExpansionEnabled();
            if ((mDisabled1 & 0x4) != 0x0) {
                this.mShadeController.animateCollapsePanels();
            }
        }
    }
    
    protected void dismissKeyboardShortcuts() {
        KeyboardShortcuts.dismiss();
    }
    
    @Override
    public void dismissKeyboardShortcutsMenu() {
        this.mHandler.removeMessages(1027);
        this.mHandler.sendEmptyMessage(1027);
    }
    
    @Override
    public void dismissKeyguardThenExecute(final OnDismissAction onDismissAction, final Runnable runnable, final boolean b) {
        if (this.mWakefulnessLifecycle.getWakefulness() == 0 && this.mKeyguardStateController.canDismissLockScreen() && !this.mStatusBarStateController.leaveOpenOnKeyguardHide() && this.mDozeServiceHost.isPulsing()) {
            this.mBiometricUnlockController.startWakeAndUnlock(2);
        }
        if (this.mStatusBarKeyguardViewManager.isShowing()) {
            this.mStatusBarKeyguardViewManager.dismissWithAction(onDismissAction, runnable, b);
        }
        else {
            onDismissAction.onDismiss();
        }
    }
    
    protected void dismissKeyguardThenExecute(final OnDismissAction onDismissAction, final boolean b) {
        this.dismissKeyguardThenExecute(onDismissAction, null, b);
    }
    
    @Override
    public void dispatchDemoCommand(final String s, final Bundle bundle) {
        final boolean mDemoModeAllowed = this.mDemoModeAllowed;
        final int n = 0;
        if (!mDemoModeAllowed) {
            this.mDemoModeAllowed = (Settings$Global.getInt(super.mContext.getContentResolver(), "sysui_demo_allowed", 0) != 0);
        }
        if (!this.mDemoModeAllowed) {
            return;
        }
        if (s.equals("enter")) {
            this.mDemoMode = true;
        }
        else if (s.equals("exit")) {
            this.mDemoMode = false;
            this.checkBarModes();
        }
        else if (!this.mDemoMode) {
            this.dispatchDemoCommand("enter", new Bundle());
        }
        final boolean b = s.equals("enter") || s.equals("exit");
        if (b || s.equals("volume")) {
            final VolumeComponent mVolumeComponent = this.mVolumeComponent;
            if (mVolumeComponent != null) {
                mVolumeComponent.dispatchDemoCommand(s, bundle);
            }
        }
        if (b || s.equals("clock")) {
            this.dispatchDemoCommandToView(s, bundle, R$id.clock);
        }
        if (b || s.equals("battery")) {
            this.mBatteryController.dispatchDemoCommand(s, bundle);
        }
        if (b || s.equals("status")) {
            ((StatusBarIconControllerImpl)this.mIconController).dispatchDemoCommand(s, bundle);
        }
        if (this.mNetworkController != null && (b || s.equals("network"))) {
            this.mNetworkController.dispatchDemoCommand(s, bundle);
        }
        if (b || s.equals("notifications")) {
            final PhoneStatusBarView mStatusBarView = this.mStatusBarView;
            View viewById;
            if (mStatusBarView == null) {
                viewById = null;
            }
            else {
                viewById = mStatusBarView.findViewById(R$id.notification_icon_area);
            }
            if (viewById != null) {
                final String string = bundle.getString("visible");
                int visibility;
                if (this.mDemoMode && "false".equals(string)) {
                    visibility = 4;
                }
                else {
                    visibility = 0;
                }
                viewById.setVisibility(visibility);
            }
        }
        if (s.equals("bars")) {
            final String string2 = bundle.getString("mode");
            int n2;
            if ("opaque".equals(string2)) {
                n2 = 4;
            }
            else if ("translucent".equals(string2)) {
                n2 = 2;
            }
            else if ("semi-transparent".equals(string2)) {
                n2 = 1;
            }
            else if ("transparent".equals(string2)) {
                n2 = n;
            }
            else if ("warning".equals(string2)) {
                n2 = 5;
            }
            else {
                n2 = -1;
            }
            if (n2 != -1) {
                if (this.mNotificationShadeWindowController != null && this.mNotificationShadeWindowViewController.getBarTransitions() != null) {
                    this.mNotificationShadeWindowViewController.getBarTransitions().transitionTo(n2, true);
                }
                this.mNavigationBarController.transitionTo(this.mDisplayId, n2, true);
            }
        }
        if (b || s.equals("operator")) {
            this.dispatchDemoCommandToView(s, bundle, R$id.operator_name);
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        Object str = this.mQueueLock;
        synchronized (str) {
            printWriter.println("Current Status Bar state:");
            final StringBuilder sb = new StringBuilder();
            sb.append("  mExpandedVisible=");
            sb.append(this.mExpandedVisible);
            printWriter.println(sb.toString());
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("  mDisplayMetrics=");
            sb2.append(this.mDisplayMetrics);
            printWriter.println(sb2.toString());
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("  mStackScroller: ");
            sb3.append(viewInfo((View)this.mStackScroller));
            printWriter.println(sb3.toString());
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("  mStackScroller: ");
            sb4.append(viewInfo((View)this.mStackScroller));
            sb4.append(" scroll ");
            sb4.append(this.mStackScroller.getScrollX());
            sb4.append(",");
            sb4.append(this.mStackScroller.getScrollY());
            printWriter.println(sb4.toString());
            // monitorexit(str)
            printWriter.print("  mInteractingWindows=");
            printWriter.println(this.mInteractingWindows);
            printWriter.print("  mStatusBarWindowState=");
            printWriter.println(StatusBarManager.windowStateToString(this.mStatusBarWindowState));
            printWriter.print("  mStatusBarMode=");
            printWriter.println(BarTransitions.modeToString(this.mStatusBarMode));
            printWriter.print("  mDozing=");
            printWriter.println(this.mDozing);
            printWriter.print("  mWallpaperSupported= ");
            printWriter.println(this.mWallpaperSupported);
            printWriter.println("  StatusBarWindowView: ");
            str = this.mNotificationShadeWindowViewController;
            if (str != null) {
                ((NotificationShadeWindowViewController)str).dump(fileDescriptor, printWriter, array);
                dumpBarTransitions(printWriter, "PhoneStatusBarTransitions", this.mNotificationShadeWindowViewController.getBarTransitions());
            }
            printWriter.println("  mMediaManager: ");
            str = this.mMediaManager;
            if (str != null) {
                ((NotificationMediaManager)str).dump(fileDescriptor, printWriter, array);
            }
            printWriter.println("  Panels: ");
            if (this.mNotificationPanelViewController != null) {
                str = new StringBuilder();
                ((StringBuilder)str).append("    mNotificationPanel=");
                ((StringBuilder)str).append(this.mNotificationPanelViewController.getView());
                ((StringBuilder)str).append(" params=");
                ((StringBuilder)str).append(this.mNotificationPanelViewController.getView().getLayoutParams().debug(""));
                printWriter.println(((StringBuilder)str).toString());
                printWriter.print("      ");
                this.mNotificationPanelViewController.dump(fileDescriptor, printWriter, array);
            }
            printWriter.println("  mStackScroller: ");
            if (this.mStackScroller instanceof Dumpable) {
                printWriter.print("      ");
                ((Dumpable)this.mStackScroller).dump(fileDescriptor, printWriter, array);
            }
            printWriter.println("  Theme:");
            if (this.mUiModeManager == null) {
                str = "null";
            }
            else {
                str = new StringBuilder();
                ((StringBuilder)str).append(this.mUiModeManager.getNightMode());
                ((StringBuilder)str).append("");
                str = ((StringBuilder)str).toString();
            }
            final StringBuilder sb5 = new StringBuilder();
            sb5.append("    dark theme: ");
            sb5.append((String)str);
            sb5.append(" (auto: ");
            boolean b = false;
            sb5.append(0);
            sb5.append(", yes: ");
            sb5.append(2);
            sb5.append(", no: ");
            sb5.append(1);
            sb5.append(")");
            printWriter.println(sb5.toString());
            if (super.mContext.getThemeResId() == R$style.Theme_SystemUI_Light) {
                b = true;
            }
            str = new StringBuilder();
            ((StringBuilder)str).append("    light wallpaper theme: ");
            ((StringBuilder)str).append(b);
            printWriter.println(((StringBuilder)str).toString());
            str = this.mKeyguardIndicationController;
            if (str != null) {
                ((KeyguardIndicationController)str).dump(fileDescriptor, printWriter, array);
            }
            str = this.mScrimController;
            if (str != null) {
                ((ScrimController)str).dump(fileDescriptor, printWriter, array);
            }
            str = this.mStatusBarKeyguardViewManager;
            if (str != null) {
                ((StatusBarKeyguardViewManager)str).dump(printWriter);
            }
            this.mNotificationsController.dump(fileDescriptor, printWriter, array, true);
            str = this.mHeadsUpManager;
            if (str != null) {
                ((HeadsUpManagerPhone)str).dump(fileDescriptor, printWriter, array);
            }
            else {
                printWriter.println("  mHeadsUpManager: null");
            }
            str = this.mStatusBarTouchableRegionManager;
            if (str != null) {
                ((StatusBarTouchableRegionManager)str).dump(fileDescriptor, printWriter, array);
            }
            else {
                printWriter.println("  mStatusBarTouchableRegionManager: null");
            }
            str = this.mLightBarController;
            if (str != null) {
                ((LightBarController)str).dump(fileDescriptor, printWriter, array);
            }
            this.mFalsingManager.dump(printWriter);
            FalsingLog.dump(printWriter);
            printWriter.println("SharedPreferences:");
            for (final Map.Entry<String, V> entry : Prefs.getAll(super.mContext).entrySet()) {
                printWriter.print("  ");
                printWriter.print(entry.getKey());
                printWriter.print("=");
                printWriter.println(entry.getValue());
            }
        }
    }
    
    public void endAffordanceLaunch() {
        this.releaseGestureWakeLock();
        this.mNotificationPanelViewController.onAffordanceLaunchEnded();
    }
    
    public void executeActionDismissingKeyguard(final Runnable runnable, final boolean b) {
        if (!this.mDeviceProvisionedController.isDeviceProvisioned()) {
            return;
        }
        this.dismissKeyguardThenExecute(new _$$Lambda$StatusBar$k2Z8hkD0RBskzAKYWfDbADG_fwk(this, runnable), b);
    }
    
    public void executeRunnableDismissingKeyguard(final Runnable runnable, final Runnable runnable2, final boolean b, final boolean b2, final boolean b3) {
        this.dismissKeyguardThenExecute(new _$$Lambda$StatusBar$WT5CJNEVDKfmLz43emscOEa28Rc(this, runnable, b, b3), runnable2, b2);
    }
    
    public void fadeKeyguardAfterLaunchTransition(final Runnable runnable, final Runnable mLaunchTransitionEndRunnable) {
        this.mHandler.removeMessages(1003);
        this.mLaunchTransitionEndRunnable = mLaunchTransitionEndRunnable;
        final _$$Lambda$StatusBar$qLHpBuX_xlYSZlt7wd3GF8ThptU launchTransitionEndRunnable = new _$$Lambda$StatusBar$qLHpBuX_xlYSZlt7wd3GF8ThptU(this, runnable);
        if (this.mNotificationPanelViewController.isLaunchTransitionRunning()) {
            this.mNotificationPanelViewController.setLaunchTransitionEndRunnable(launchTransitionEndRunnable);
        }
        else {
            launchTransitionEndRunnable.run();
        }
    }
    
    public void fadeKeyguardWhilePulsing() {
        this.mNotificationPanelViewController.fadeOut(0L, 96L, new _$$Lambda$StatusBar$ylp5ePNGXoDc5jay88dVi46VH2Y(this)).start();
    }
    
    public void finishKeyguardFadingAway() {
        this.mKeyguardStateController.notifyKeyguardDoneFading();
        this.mScrimController.setExpansionAffectsAlpha(true);
    }
    
    public View getAmbientIndicationContainer() {
        return this.mAmbientIndicationContainer;
    }
    
    protected ViewGroup getBouncerContainer() {
        return (ViewGroup)this.mNotificationShadeWindowView;
    }
    
    float getDisplayDensity() {
        return this.mDisplayMetrics.density;
    }
    
    float getDisplayHeight() {
        return (float)this.mDisplayMetrics.heightPixels;
    }
    
    float getDisplayWidth() {
        return (float)this.mDisplayMetrics.widthPixels;
    }
    
    public GestureRecorder getGestureRecorder() {
        return this.mGestureRec;
    }
    
    public NotificationGutsManager getGutsManager() {
        return this.mGutsManager;
    }
    
    public KeyguardBottomAreaView getKeyguardBottomAreaView() {
        return this.mNotificationPanelViewController.getKeyguardBottomAreaView();
    }
    
    public NavigationBarView getNavigationBarView() {
        return this.mNavigationBarController.getNavigationBarView(this.mDisplayId);
    }
    
    public ViewGroup getNotificationScrollLayout() {
        return this.mStackScroller;
    }
    
    public NotificationShadeWindowView getNotificationShadeWindowView() {
        return this.mNotificationShadeWindowView;
    }
    
    public NotificationShadeWindowViewController getNotificationShadeWindowViewController() {
        return this.mNotificationShadeWindowViewController;
    }
    
    public NotificationPanelViewController getPanelController() {
        return this.mNotificationPanelViewController;
    }
    
    public NotificationPresenter getPresenter() {
        return this.mPresenter;
    }
    
    int getRotation() {
        return this.mDisplay.getRotation();
    }
    
    public int getStatusBarHeight() {
        return this.mStatusBarWindowController.getStatusBarHeight();
    }
    
    protected BarTransitions getStatusBarTransitions() {
        return this.mNotificationShadeWindowViewController.getBarTransitions();
    }
    
    protected View getStatusBarView() {
        return (View)this.mStatusBarView;
    }
    
    protected View$OnTouchListener getStatusBarWindowTouchListener() {
        return (View$OnTouchListener)new _$$Lambda$StatusBar$Lo2b_uuijn2v_KFsJCc85164BF0(this);
    }
    
    public int getWakefulnessState() {
        return this.mWakefulnessLifecycle.getWakefulness();
    }
    
    void goToLockedShade(final View view) {
        if ((this.mDisabled2 & 0x4) != 0x0) {
            return;
        }
        int n = this.mLockscreenUserManager.getCurrentUserId();
        NotificationEntry entry = null;
        if (view instanceof ExpandableNotificationRow) {
            entry = ((ExpandableNotificationRow)view).getEntry();
            entry.setUserExpanded(true, true);
            entry.setGroupExpansionChanging(true);
            n = entry.getSbn().getUserId();
        }
        final NotificationLockscreenUserManager mLockscreenUserManager = this.mLockscreenUserManager;
        final boolean userAllowsPrivateNotificationsInPublic = mLockscreenUserManager.userAllowsPrivateNotificationsInPublic(mLockscreenUserManager.getCurrentUserId());
        final int n2 = 0;
        int n3;
        if (userAllowsPrivateNotificationsInPublic && this.mLockscreenUserManager.shouldShowLockscreenNotifications() && !this.mFalsingManager.shouldEnforceBouncer()) {
            n3 = 0;
        }
        else {
            n3 = 1;
        }
        if (this.mKeyguardBypassController.getBypassEnabled()) {
            n3 = n2;
        }
        if (this.mLockscreenUserManager.isLockscreenPublicMode(n) && n3 != 0) {
            this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
            this.showBouncerIfKeyguard();
            this.mDraggedDownEntry = entry;
        }
        else {
            this.mNotificationPanelViewController.animateToFullShade(0L);
            this.mStatusBarStateController.setState(2);
        }
    }
    
    void handlePeekToExpandTransistion() {
        try {
            this.mBarService.onPanelRevealed(false, this.mNotificationsController.getActiveNotificationsCount());
        }
        catch (RemoteException ex) {}
    }
    
    @Override
    public void handleSystemKey(final int n) {
        if (this.mCommandQueue.panelsEnabled() && this.mKeyguardUpdateMonitor.isDeviceInteractive()) {
            if (!this.mKeyguardStateController.isShowing() || this.mKeyguardStateController.isOccluded()) {
                if (!this.mUserSetup) {
                    return;
                }
                if (280 == n) {
                    this.mMetricsLogger.action(493);
                    this.mNotificationPanelViewController.collapse(false, 1.0f);
                }
                else if (281 == n) {
                    this.mMetricsLogger.action(494);
                    if (this.mNotificationPanelViewController.isFullyCollapsed()) {
                        if (this.mVibrateOnOpening) {
                            this.mVibratorHelper.vibrate(2);
                        }
                        this.mNotificationPanelViewController.expand(true);
                        ((NotificationListContainer)this.mStackScroller).setWillExpand(true);
                        this.mHeadsUpManager.unpinAll(true);
                        this.mMetricsLogger.count("panel_open", 1);
                    }
                    else if (!this.mNotificationPanelViewController.isInSettings() && !this.mNotificationPanelViewController.isExpanding()) {
                        this.mNotificationPanelViewController.flingSettings(0.0f, 0);
                        this.mMetricsLogger.count("panel_open_qs", 1);
                    }
                }
            }
        }
    }
    
    protected void handleVisibleToUserChanged(final boolean b) {
        if (b) {
            this.handleVisibleToUserChangedImpl(b);
            this.mNotificationLogger.startNotificationLogging();
        }
        else {
            this.mNotificationLogger.stopNotificationLogging();
            this.handleVisibleToUserChangedImpl(b);
        }
    }
    
    public boolean headsUpShouldBeVisible() {
        return this.mHeadsUpAppearanceController.shouldBeVisible();
    }
    
    public boolean hideKeyguard() {
        this.mStatusBarStateController.setKeyguardRequested(false);
        return this.updateIsKeyguard();
    }
    
    public boolean hideKeyguardImpl() {
        this.mIsKeyguard = false;
        Trace.beginSection("StatusBar#hideKeyguard");
        final boolean leaveOpenOnKeyguardHide = this.mStatusBarStateController.leaveOpenOnKeyguardHide();
        if (!this.mStatusBarStateController.setState(0)) {
            this.mLockscreenUserManager.updatePublicMode();
        }
        if (this.mStatusBarStateController.leaveOpenOnKeyguardHide()) {
            if (!this.mStatusBarStateController.isKeyguardRequested()) {
                this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(false);
            }
            final long calculateGoingToFullShadeDelay = this.mKeyguardStateController.calculateGoingToFullShadeDelay();
            this.mNotificationPanelViewController.animateToFullShade(calculateGoingToFullShadeDelay);
            final NotificationEntry mDraggedDownEntry = this.mDraggedDownEntry;
            if (mDraggedDownEntry != null) {
                mDraggedDownEntry.setUserLocked(false);
                this.mDraggedDownEntry = null;
            }
            this.mNavigationBarController.disableAnimationsDuringHide(this.mDisplayId, calculateGoingToFullShadeDelay);
        }
        else if (!this.mNotificationPanelViewController.isCollapsing()) {
            this.instantCollapseNotificationPanel();
        }
        final QSPanel mqsPanel = this.mQSPanel;
        if (mqsPanel != null) {
            mqsPanel.refreshAllTiles();
        }
        this.mHandler.removeMessages(1003);
        this.releaseGestureWakeLock();
        this.mNotificationPanelViewController.onAffordanceLaunchEnded();
        this.mNotificationPanelViewController.cancelAnimation();
        this.mNotificationPanelViewController.setAlpha(1.0f);
        this.mNotificationPanelViewController.resetViewGroupFade();
        this.updateScrimController();
        Trace.endSection();
        return leaveOpenOnKeyguardHide;
    }
    
    public boolean hideStatusBarIconsForBouncer() {
        return this.mHideIconsForBouncer || this.mWereIconsJustHidden;
    }
    
    public boolean hideStatusBarIconsWhenExpanded() {
        return this.mNotificationPanelViewController.hideStatusBarIconsWhenExpanded();
    }
    
    public boolean inFullscreenMode() {
        return this.mAppFullscreen;
    }
    
    public boolean inImmersiveMode() {
        return this.mAppImmersive;
    }
    
    void instantCollapseNotificationPanel() {
        this.mNotificationPanelViewController.instantCollapse();
        this.mShadeController.runPostCollapseRunnables();
    }
    
    public boolean interceptMediaKey(final KeyEvent keyEvent) {
        final int mState = this.mState;
        boolean b = true;
        if (mState != 1 || !this.mStatusBarKeyguardViewManager.interceptMediaKey(keyEvent)) {
            b = false;
        }
        return b;
    }
    
    public boolean interceptTouchEvent(final MotionEvent motionEvent) {
        if (this.mStatusBarWindowState == 0) {
            if ((motionEvent.getAction() == 1 || motionEvent.getAction() == 3) && !this.mExpandedVisible) {
                this.setInteracting(1, false);
            }
            else {
                this.setInteracting(1, true);
            }
        }
        return false;
    }
    
    public boolean isBouncerShowing() {
        return this.mBouncerShowing;
    }
    
    public boolean isBouncerShowingScrimmed() {
        return this.isBouncerShowing() && this.mStatusBarKeyguardViewManager.bouncerNeedsScrimming();
    }
    
    boolean isCameraAllowedByAdmin() {
        final boolean cameraDisabled = this.mDevicePolicyManager.getCameraDisabled((ComponentName)null, this.mLockscreenUserManager.getCurrentUserId());
        boolean b = false;
        if (cameraDisabled) {
            return false;
        }
        if (this.mStatusBarKeyguardViewManager != null && (!this.isKeyguardShowing() || !this.isKeyguardSecure())) {
            return true;
        }
        if ((this.mDevicePolicyManager.getKeyguardDisabledFeatures((ComponentName)null, this.mLockscreenUserManager.getCurrentUserId()) & 0x2) == 0x0) {
            b = true;
        }
        return b;
    }
    
    public boolean isDeviceInVrMode() {
        return this.mPresenter.isDeviceInVrMode();
    }
    
    public boolean isDeviceInteractive() {
        return this.mDeviceInteractive;
    }
    
    public boolean isFalsingThresholdNeeded() {
        final int state = this.mStatusBarStateController.getState();
        boolean b = true;
        if (state != 1) {
            b = false;
        }
        return b;
    }
    
    public boolean isFullScreenUserSwitcherState() {
        return this.mState == 3;
    }
    
    public boolean isInLaunchTransition() {
        return this.mNotificationPanelViewController.isLaunchTransitionRunning() || this.mNotificationPanelViewController.isLaunchTransitionFinished();
    }
    
    public boolean isKeyguardSecure() {
        final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager = this.mStatusBarKeyguardViewManager;
        if (mStatusBarKeyguardViewManager == null) {
            Slog.w("StatusBar", "isKeyguardSecure() called before startKeyguard(), returning false", new Throwable());
            return false;
        }
        return mStatusBarKeyguardViewManager.isSecure();
    }
    
    public boolean isKeyguardShowing() {
        final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager = this.mStatusBarKeyguardViewManager;
        if (mStatusBarKeyguardViewManager == null) {
            Slog.i("StatusBar", "isKeyguardShowing() called before startKeyguard(), returning true");
            return true;
        }
        return mStatusBarKeyguardViewManager.isShowing();
    }
    
    public boolean isOccluded() {
        return this.mIsOccluded;
    }
    
    public boolean isPulsing() {
        return this.mDozeServiceHost.isPulsing();
    }
    
    boolean isSameStatusBarState(final int n) {
        return this.mStatusBarWindowState == n;
    }
    
    public boolean isScreenFullyOff() {
        return this.mScreenLifecycle.getScreenState() == 0;
    }
    
    public boolean isWakeUpComingFromTouch() {
        return this.mWakeUpComingFromTouch;
    }
    
    public void keyguardGoingAway() {
        this.mKeyguardStateController.notifyKeyguardGoingAway(true);
        this.mCommandQueue.appTransitionPending(this.mDisplayId, true);
    }
    
    void makeExpandedInvisible() {
        if (this.mExpandedVisible) {
            if (this.mNotificationShadeWindowView != null) {
                this.mStatusBarView.collapsePanel(false, false, 1.0f);
                this.mNotificationPanelViewController.closeQs();
                this.visibilityChanged(this.mExpandedVisible = false);
                this.mNotificationShadeWindowController.setPanelVisible(false);
                this.mStatusBarWindowController.setForceStatusBarVisible(false);
                this.mGutsManager.closeAndSaveGuts(true, true, true, -1, -1, true);
                this.mShadeController.runPostCollapseRunnables();
                this.setInteracting(1, false);
                if (!this.mNotificationActivityStarter.isCollapsingToShowActivityOverLockscreen()) {
                    this.showBouncerIfKeyguard();
                }
                this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, this.mNotificationPanelViewController.hideStatusBarIconsWhenExpanded());
                if (!this.mStatusBarKeyguardViewManager.isShowing()) {
                    WindowManagerGlobal.getInstance().trimMemory(20);
                }
            }
        }
    }
    
    void makeExpandedVisible(final boolean b) {
        if (!b && (this.mExpandedVisible || !this.mCommandQueue.panelsEnabled())) {
            return;
        }
        this.mExpandedVisible = true;
        this.mNotificationShadeWindowController.setPanelVisible(true);
        this.visibilityChanged(true);
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, b ^ true);
        this.setInteracting(1, true);
    }
    
    protected void makeStatusBarView(final RegisterStatusBarResult registerStatusBarResult) {
        final Context mContext = super.mContext;
        this.updateDisplaySize();
        this.updateResources();
        this.updateTheme();
        this.inflateStatusBarWindow();
        this.mNotificationShadeWindowViewController.setService(this);
        this.mNotificationShadeWindowView.setOnTouchListener(this.getStatusBarWindowTouchListener());
        final ViewGroup mStackScroller = (ViewGroup)this.mNotificationShadeWindowView.findViewById(R$id.notification_stack_scroller);
        this.mStackScroller = mStackScroller;
        this.mNotificationLogger.setUpWithContainer((NotificationListContainer)mStackScroller);
        final NotificationIconAreaController notificationIconAreaController = SystemUIFactory.getInstance().createNotificationIconAreaController(mContext, this, this.mWakeUpCoordinator, this.mKeyguardBypassController, this.mStatusBarStateController);
        this.mNotificationIconAreaController = notificationIconAreaController;
        this.mWakeUpCoordinator.setIconAreaController(notificationIconAreaController);
        this.inflateShelf();
        this.mNotificationIconAreaController.setupShelf(this.mNotificationShelf);
        final NotificationPanelViewController mNotificationPanelViewController = this.mNotificationPanelViewController;
        final NotificationIconAreaController mNotificationIconAreaController = this.mNotificationIconAreaController;
        Objects.requireNonNull(mNotificationIconAreaController);
        mNotificationPanelViewController.setOnReinflationListener(new _$$Lambda$LyXF2jzAv77MElAagmeOMv__4xQ(mNotificationIconAreaController));
        this.mNotificationPanelViewController.addExpansionListener(this.mWakeUpCoordinator);
        this.mDarkIconDispatcher.addDarkReceiver((DarkIconDispatcher.DarkReceiver)this.mNotificationIconAreaController);
        this.mPluginDependencyProvider.allowPluginDependency(DarkIconDispatcher.class);
        this.mPluginDependencyProvider.allowPluginDependency(StatusBarStateController.class);
        final FragmentHostManager value = FragmentHostManager.get((View)this.mPhoneStatusBarWindow);
        value.addTagListener("CollapsedStatusBarFragment", (FragmentHostManager.FragmentListener)new _$$Lambda$StatusBar$TPJyILujZ88K3rKFmgzHGHpbtLo(this));
        value.getFragmentManager().beginTransaction().replace(R$id.status_bar_container, (Fragment)new CollapsedStatusBarFragment(), "CollapsedStatusBarFragment").commit();
        this.mHeadsUpManager.setup(this.mVisualStabilityManager);
        this.mStatusBarTouchableRegionManager.setup(this, (View)this.mNotificationShadeWindowView);
        this.mHeadsUpManager.addListener(this);
        this.mHeadsUpManager.addListener(this.mNotificationPanelViewController.getOnHeadsUpChangedListener());
        this.mHeadsUpManager.addListener(this.mVisualStabilityManager);
        this.mNotificationPanelViewController.setHeadsUpManager(this.mHeadsUpManager);
        this.mNotificationLogger.setHeadsUpManager(this.mHeadsUpManager);
        this.createNavigationBar(registerStatusBarResult);
        if (this.mWallpaperSupported) {
            this.mLockscreenWallpaper = this.mLockscreenWallpaperLazy.get();
        }
        this.mKeyguardIndicationController.setIndicationArea((ViewGroup)this.mNotificationShadeWindowView.findViewById(R$id.keyguard_indication_area));
        this.mNotificationPanelViewController.setKeyguardIndicationController(this.mKeyguardIndicationController);
        this.mAmbientIndicationContainer = this.mNotificationShadeWindowView.findViewById(R$id.ambient_indication_container);
        this.mBatteryController.addCallback((BatteryController.BatteryStateChangeCallback)new BatteryController.BatteryStateChangeCallback() {
            @Override
            public void onBatteryLevelChanged(final int n, final boolean b, final boolean b2) {
            }
            
            @Override
            public void onPowerSaveChanged(final boolean b) {
                final StatusBar this$0 = StatusBar.this;
                this$0.mHandler.post(this$0.mCheckBarModes);
                final DozeServiceHost mDozeServiceHost = StatusBar.this.mDozeServiceHost;
                if (mDozeServiceHost != null) {
                    mDozeServiceHost.firePowerSaveChanged(b);
                }
            }
        });
        this.mAutoHideController.setStatusBar(new AutoHideUiElement() {
            @Override
            public void hide() {
                StatusBar.this.clearTransient();
            }
            
            @Override
            public boolean isVisible() {
                return StatusBar.this.isTransientShown();
            }
            
            @Override
            public boolean shouldHideOnTouch() {
                return StatusBar.this.mRemoteInputManager.getController().isRemoteInputActive() ^ true;
            }
            
            @Override
            public void synchronizeState() {
                StatusBar.this.checkBarModes();
            }
        });
        final ScrimView scrimView = (ScrimView)this.mNotificationShadeWindowView.findViewById(R$id.scrim_behind);
        final ScrimView scrimView2 = (ScrimView)this.mNotificationShadeWindowView.findViewById(R$id.scrim_in_front);
        final ScrimView scrimView3 = (ScrimView)this.mNotificationShadeWindowView.findViewById(R$id.scrim_for_bubble);
        this.mScrimController.setScrimVisibleListener(new _$$Lambda$StatusBar$FJ09N4w98W1tToxpLlffdr7H_Fk(this));
        this.mScrimController.attachViews(scrimView, scrimView2, scrimView3);
        this.mNotificationPanelViewController.initDependencies(this, this.mGroupManager, this.mNotificationShelf, this.mNotificationIconAreaController, this.mScrimController);
        final BackDropView backDropView = (BackDropView)this.mNotificationShadeWindowView.findViewById(R$id.backdrop);
        this.mMediaManager.setup(backDropView, (ImageView)backDropView.findViewById(R$id.backdrop_front), (ImageView)backDropView.findViewById(R$id.backdrop_back), this.mScrimController, this.mLockscreenWallpaper);
        this.mNotificationPanelViewController.setUserSetupComplete(this.mUserSetup);
        if (UserManager.get(super.mContext).isUserSwitcherEnabled()) {
            this.createUserSwitcher();
        }
        final NotificationPanelViewController mNotificationPanelViewController2 = this.mNotificationPanelViewController;
        final LockscreenLockIconController mLockscreenLockIconController = this.mLockscreenLockIconController;
        Objects.requireNonNull(mLockscreenLockIconController);
        mNotificationPanelViewController2.setLaunchAffordanceListener(new _$$Lambda$sBkty3XIL7r37AAUJ1Bk1mVwNfA(mLockscreenLockIconController));
        final View viewById = this.mNotificationShadeWindowView.findViewById(R$id.qs_frame);
        if (viewById != null) {
            final FragmentHostManager value2 = FragmentHostManager.get(viewById);
            final int qs_frame = R$id.qs_frame;
            final ExtensionController.ExtensionBuilder<QS> extension = this.mExtensionController.newExtension(QS.class);
            extension.withPlugin(QS.class);
            extension.withDefault(new _$$Lambda$Zqmz5npIKuMPJHZWVxICwxzCPwk(this));
            ExtensionFragmentListener.attachExtensonToFragment(viewById, "QS", qs_frame, extension.build());
            this.mBrightnessMirrorController = new BrightnessMirrorController(this.mNotificationShadeWindowView, this.mNotificationPanelViewController, this.mNotificationShadeDepthControllerLazy.get(), new _$$Lambda$StatusBar$bHczCH98mucX3KqZT66dgnU9dJ8(this));
            value2.addTagListener("QS", (FragmentHostManager.FragmentListener)new _$$Lambda$StatusBar$iLE125wCFniZRETLh08RVM8bnLc(this));
        }
        if ((this.mReportRejectedTouch = this.mNotificationShadeWindowView.findViewById(R$id.report_rejected_touch)) != null) {
            this.updateReportRejectedTouchVisibility();
            this.mReportRejectedTouch.setOnClickListener((View$OnClickListener)new _$$Lambda$StatusBar$XQOpWl97Dmi1_PDOREwJc80t2Z4(this));
        }
        if (!this.mPowerManager.isScreenOn()) {
            this.mBroadcastReceiver.onReceive(super.mContext, new Intent("android.intent.action.SCREEN_OFF"));
        }
        this.mGestureWakeLock = this.mPowerManager.newWakeLock(10, "GestureWakeLock");
        this.mVibrator = (Vibrator)super.mContext.getSystemService((Class)Vibrator.class);
        final int[] intArray = super.mContext.getResources().getIntArray(R$array.config_cameraLaunchGestureVibePattern);
        this.mCameraLaunchGestureVibePattern = new long[intArray.length];
        for (int i = 0; i < intArray.length; ++i) {
            this.mCameraLaunchGestureVibePattern[i] = intArray[i];
        }
        this.registerBroadcastReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.systemui.demo");
        mContext.registerReceiverAsUser(this.mDemoReceiver, UserHandle.ALL, intentFilter, "android.permission.DUMP", (Handler)null);
        this.mDeviceProvisionedController.addCallback(this.mUserSetupObserver);
        this.mUserSetupObserver.onUserSetupChanged();
        ThreadedRenderer.overrideProperty("disableProfileBars", "true");
        ThreadedRenderer.overrideProperty("ambientRatio", String.valueOf(1.5f));
    }
    
    public void maybeEscalateHeadsUp() {
        this.mHeadsUpManager.getAllEntries().forEach((Consumer<? super NotificationEntry>)_$$Lambda$StatusBar$00BUzvXeXCM5N0MdSF4qFI3_BMY.INSTANCE);
        this.mHeadsUpManager.releaseAllImmediately();
    }
    
    public void notifyBiometricAuthModeChanged() {
        this.mDozeServiceHost.updateDozing();
        this.updateScrimController();
        this.mLockscreenLockIconController.onBiometricAuthModeChanged(this.mBiometricUnlockController.isWakeAndUnlock(), this.mBiometricUnlockController.isBiometricUnlock());
    }
    
    protected void notifyHeadsUpGoingToSleep() {
        this.maybeEscalateHeadsUp();
    }
    
    public boolean onBackPressed() {
        final boolean b = this.mScrimController.getState() == ScrimState.BOUNCER_SCRIMMED;
        if (this.mStatusBarKeyguardViewManager.onBackPressed(b)) {
            if (!b) {
                this.mNotificationPanelViewController.expandWithoutQs();
            }
            return true;
        }
        if (this.mNotificationPanelViewController.isQsExpanded()) {
            if (this.mNotificationPanelViewController.isQsDetailShowing()) {
                this.mNotificationPanelViewController.closeQsDetail();
            }
            else {
                this.mNotificationPanelViewController.animateCloseQs(false);
            }
            return true;
        }
        final int mState = this.mState;
        if (mState != 1 && mState != 2) {
            if (this.mNotificationPanelViewController.canPanelBeCollapsed()) {
                this.mShadeController.animateCollapsePanels();
            }
            else {
                this.mBubbleController.performBackPressIfNeeded();
            }
            return true;
        }
        final KeyguardUserSwitcher mKeyguardUserSwitcher = this.mKeyguardUserSwitcher;
        return mKeyguardUserSwitcher != null && mKeyguardUserSwitcher.hideIfNotSimple(true);
    }
    
    public void onBouncerPreHideAnimation() {
        this.mNotificationPanelViewController.onBouncerPreHideAnimation();
        this.mLockscreenLockIconController.onBouncerPreHideAnimation();
    }
    
    public void onCameraHintStarted() {
        this.mFalsingManager.onCameraHintStarted();
        this.mKeyguardIndicationController.showTransientIndication(R$string.camera_hint);
    }
    
    @Override
    public void onCameraLaunchGestureDetected(final int mLastCameraLaunchSource) {
        this.mLastCameraLaunchSource = mLastCameraLaunchSource;
        if (this.isGoingToSleep()) {
            this.mLaunchCameraOnFinishedGoingToSleep = true;
            return;
        }
        if (!this.mNotificationPanelViewController.canCameraGestureBeLaunched()) {
            return;
        }
        if (!this.mDeviceInteractive) {
            this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 5, "com.android.systemui:CAMERA_GESTURE");
        }
        this.vibrateForCameraGesture();
        if (mLastCameraLaunchSource == 1) {
            Log.v("StatusBar", "Camera launch");
            this.mKeyguardUpdateMonitor.onCameraLaunched();
        }
        if (!this.mStatusBarKeyguardViewManager.isShowing()) {
            this.startActivityDismissingKeyguard(KeyguardBottomAreaView.INSECURE_CAMERA_INTENT, false, true, true, null, 0);
        }
        else {
            if (!this.mDeviceInteractive) {
                this.mGestureWakeLock.acquire(6000L);
            }
            if (this.isWakingUpOrAwake()) {
                if (this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                    this.mStatusBarKeyguardViewManager.reset(true);
                }
                this.mNotificationPanelViewController.launchCamera(this.mDeviceInteractive, mLastCameraLaunchSource);
                this.updateScrimController();
            }
            else {
                this.mLaunchCameraWhenFinishedWaking = true;
            }
        }
    }
    
    public void onClosingFinished() {
        this.mShadeController.runPostCollapseRunnables();
        if (!this.mPresenter.isPresenterFullyCollapsed()) {
            this.mNotificationShadeWindowController.setNotificationShadeFocusable(true);
        }
    }
    
    public void onColorsChanged(final ColorExtractor colorExtractor, final int n) {
        this.updateTheme();
    }
    
    public void onConfigChanged(final Configuration configuration) {
        this.updateResources();
        this.updateDisplaySize();
        this.mViewHierarchyManager.updateRowStates();
        this.mScreenPinningRequest.onConfigurationChanged();
    }
    
    public void onDensityOrFontScaleChanged() {
        final BrightnessMirrorController mBrightnessMirrorController = this.mBrightnessMirrorController;
        if (mBrightnessMirrorController != null) {
            mBrightnessMirrorController.onDensityOrFontScaleChanged();
        }
        this.mUserInfoControllerImpl.onDensityOrFontScaleChanged();
        this.mUserSwitcherController.onDensityOrFontScaleChanged();
        final KeyguardUserSwitcher mKeyguardUserSwitcher = this.mKeyguardUserSwitcher;
        if (mKeyguardUserSwitcher != null) {
            mKeyguardUserSwitcher.onDensityOrFontScaleChanged();
        }
        this.mNotificationIconAreaController.onDensityOrFontScaleChanged(super.mContext);
        this.mHeadsUpManager.onDensityOrFontScaleChanged();
    }
    
    public void onDozingChanged(final boolean mDozing) {
        Trace.beginSection("StatusBar#updateDozing");
        this.mDozing = mDozing;
        this.mNotificationPanelViewController.resetViews(this.mDozeServiceHost.getDozingRequested() && this.mDozeParameters.shouldControlScreenOff());
        this.updateQsExpansionEnabled();
        this.mKeyguardViewMediator.setDozing(this.mDozing);
        this.mNotificationsController.requestNotificationUpdate("onDozingChanged");
        this.updateDozingState();
        this.mDozeServiceHost.updateDozing();
        this.updateScrimController();
        this.updateReportRejectedTouchVisibility();
        Trace.endSection();
    }
    
    public void onExpandAnimationFinished(final boolean b) {
        if (!this.mPresenter.isCollapsing()) {
            this.onClosingFinished();
        }
        if (b) {
            this.instantCollapseNotificationPanel();
        }
    }
    
    public void onExpandAnimationTimedOut() {
        if (this.mPresenter.isPresenterFullyCollapsed() && !this.mPresenter.isCollapsing()) {
            final ActivityLaunchAnimator mActivityLaunchAnimator = this.mActivityLaunchAnimator;
            if (mActivityLaunchAnimator != null && !mActivityLaunchAnimator.isLaunchForActivity()) {
                this.onClosingFinished();
                return;
            }
        }
        this.mShadeController.collapsePanel(true);
    }
    
    @Override
    public void onHeadsUpPinnedModeChanged(final boolean b) {
        if (b) {
            this.mNotificationShadeWindowController.setHeadsUpShowing(true);
            this.mStatusBarWindowController.setForceStatusBarVisible(true);
            if (this.mNotificationPanelViewController.isFullyCollapsed()) {
                this.mNotificationPanelViewController.getView().requestLayout();
                this.mNotificationShadeWindowController.setForceWindowCollapsed(true);
                this.mNotificationPanelViewController.getView().post((Runnable)new _$$Lambda$StatusBar$O_yMnz6iTAsryy7QzcJdGyCPRLY(this));
            }
        }
        else {
            final boolean b2 = this.mKeyguardBypassController.getBypassEnabled() && this.mState == 1;
            if (this.mNotificationPanelViewController.isFullyCollapsed() && !this.mNotificationPanelViewController.isTracking() && !b2) {
                this.mHeadsUpManager.setHeadsUpGoingAway(true);
                this.mNotificationPanelViewController.runAfterAnimationFinished(new _$$Lambda$StatusBar$Qj9ts5qNgL9tKpQUFYjsLd9tYDs(this));
            }
            else {
                this.mNotificationShadeWindowController.setHeadsUpShowing(false);
                if (b2) {
                    this.mStatusBarWindowController.setForceStatusBarVisible(false);
                }
            }
        }
    }
    
    @Override
    public void onHeadsUpStateChanged(final NotificationEntry notificationEntry, final boolean b) {
        this.mNotificationsController.requestNotificationUpdate("onHeadsUpStateChanged");
        if (this.mStatusBarStateController.isDozing() && b) {
            notificationEntry.setPulseSuppressed(false);
            this.mDozeServiceHost.fireNotificationPulse(notificationEntry);
            if (this.mDozeServiceHost.isPulsing()) {
                this.mDozeScrimController.cancelPendingPulseTimeout();
            }
        }
        if (!b && !this.mHeadsUpManager.hasNotifications()) {
            this.mDozeScrimController.pulseOutNow();
        }
    }
    
    public void onHintFinished() {
        this.mKeyguardIndicationController.hideTransientIndicationDelayed(1200L);
    }
    
    public void onInputFocusTransfer(final boolean b, final float n) {
        if (!this.mCommandQueue.panelsEnabled()) {
            return;
        }
        if (b) {
            this.mNotificationPanelViewController.startWaitingForOpenPanelGesture();
        }
        else {
            this.mNotificationPanelViewController.stopWaitingForOpenPanelGesture(n);
        }
    }
    
    public void onKeyguardViewManagerStatesUpdated() {
        this.logStateToEventlog();
    }
    
    public void onLaunchAnimationCancelled() {
        if (!this.mPresenter.isCollapsing()) {
            this.onClosingFinished();
        }
    }
    
    public boolean onMenuPressed() {
        if (this.shouldUnlockOnMenuPressed()) {
            this.mShadeController.animateCollapsePanels(2, true);
            return true;
        }
        return false;
    }
    
    public void onOverlayChanged() {
        final BrightnessMirrorController mBrightnessMirrorController = this.mBrightnessMirrorController;
        if (mBrightnessMirrorController != null) {
            mBrightnessMirrorController.onOverlayChanged();
        }
        this.mNotificationPanelViewController.onThemeChanged();
        this.onThemeChanged();
    }
    
    public void onPanelLaidOut() {
        this.updateKeyguardMaxNotifications();
    }
    
    public void onPhoneHintStarted() {
        this.mFalsingManager.onLeftAffordanceHintStarted();
        this.mKeyguardIndicationController.showTransientIndication(R$string.phone_hint);
    }
    
    @Override
    public void onRecentsAnimationStateChanged(final boolean b) {
        this.setInteracting(2, b);
    }
    
    public boolean onSpacePressed() {
        if (this.mDeviceInteractive && this.mState != 0) {
            this.mShadeController.animateCollapsePanels(2, true);
            return true;
        }
        return false;
    }
    
    public void onStateChanged(int mState) {
        this.mState = mState;
        this.updateReportRejectedTouchVisibility();
        this.mDozeServiceHost.updateDozing();
        this.updateTheme();
        this.mNavigationBarController.touchAutoDim(this.mDisplayId);
        Trace.beginSection("StatusBar#updateKeyguardState");
        mState = this.mState;
        final boolean b = true;
        if (mState == 1) {
            this.mKeyguardIndicationController.setVisible(true);
            final KeyguardUserSwitcher mKeyguardUserSwitcher = this.mKeyguardUserSwitcher;
            if (mKeyguardUserSwitcher != null) {
                mKeyguardUserSwitcher.setKeyguard(true, this.mStatusBarStateController.fromShadeLocked());
            }
            final PhoneStatusBarView mStatusBarView = this.mStatusBarView;
            if (mStatusBarView != null) {
                mStatusBarView.removePendingHideExpandedRunnables();
            }
            final View mAmbientIndicationContainer = this.mAmbientIndicationContainer;
            if (mAmbientIndicationContainer != null) {
                mAmbientIndicationContainer.setVisibility(0);
            }
        }
        else {
            this.mKeyguardIndicationController.setVisible(false);
            final KeyguardUserSwitcher mKeyguardUserSwitcher2 = this.mKeyguardUserSwitcher;
            if (mKeyguardUserSwitcher2 != null) {
                mKeyguardUserSwitcher2.setKeyguard(false, this.mStatusBarStateController.goingToFullShade() || this.mState == 2 || this.mStatusBarStateController.fromShadeLocked());
            }
            final View mAmbientIndicationContainer2 = this.mAmbientIndicationContainer;
            if (mAmbientIndicationContainer2 != null) {
                mAmbientIndicationContainer2.setVisibility(4);
            }
        }
        this.updateDozingState();
        this.checkBarModes();
        this.updateScrimController();
        this.mPresenter.updateMediaMetaData(false, this.mState != 1 && b);
        this.updateKeyguardState();
        Trace.endSection();
    }
    
    public void onStatePreChange(final int n, final int n2) {
        if (this.mVisible && (n2 == 2 || this.mStatusBarStateController.goingToFullShade())) {
            this.clearNotificationEffects();
        }
        if (n2 == 1) {
            this.mRemoteInputManager.onPanelCollapsed();
            this.maybeEscalateHeadsUp();
        }
    }
    
    @Override
    public void onSystemBarAppearanceChanged(final int n, final int mAppearance, final AppearanceRegion[] array, final boolean b) {
        if (n != this.mDisplayId) {
            return;
        }
        boolean updateBarMode = false;
        if (this.mAppearance != mAppearance) {
            this.mAppearance = mAppearance;
            updateBarMode = this.updateBarMode(barMode(this.mTransientShown, mAppearance));
        }
        this.mLightBarController.onStatusBarAppearanceChanged(array, updateBarMode, this.mStatusBarMode, b);
    }
    
    public void onThemeChanged() {
        final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager = this.mStatusBarKeyguardViewManager;
        if (mStatusBarKeyguardViewManager != null) {
            mStatusBarKeyguardViewManager.onThemeChanged();
        }
        final View mAmbientIndicationContainer = this.mAmbientIndicationContainer;
        if (mAmbientIndicationContainer instanceof AutoReinflateContainer) {
            ((AutoReinflateContainer)mAmbientIndicationContainer).inflateLayout();
        }
        this.mNotificationIconAreaController.onThemeChanged();
    }
    
    public void onTrackingStarted() {
        this.mShadeController.runPostCollapseRunnables();
    }
    
    public void onTrackingStopped(final boolean b) {
        final int mState = this.mState;
        if ((mState == 1 || mState == 2) && !b && !this.mKeyguardStateController.canDismissLockScreen()) {
            this.mStatusBarKeyguardViewManager.showBouncer(false);
        }
    }
    
    public void onUiModeChanged() {
        final BrightnessMirrorController mBrightnessMirrorController = this.mBrightnessMirrorController;
        if (mBrightnessMirrorController != null) {
            mBrightnessMirrorController.onUiModeChanged();
        }
    }
    
    public void onUnlockHintStarted() {
        this.mFalsingManager.onUnlockHintStarted();
        this.mKeyguardIndicationController.showTransientIndication(R$string.keyguard_unlock);
    }
    
    @Override
    public void onUnlockedChanged() {
        this.updateKeyguardState();
        this.logStateToEventlog();
    }
    
    public void onVoiceAssistHintStarted() {
        this.mFalsingManager.onLeftAffordanceHintStarted();
        this.mKeyguardIndicationController.showTransientIndication(R$string.voice_hint);
    }
    
    public void postAnimateCollapsePanels() {
        final H mHandler = this.mHandler;
        final ShadeController mShadeController = this.mShadeController;
        Objects.requireNonNull(mShadeController);
        mHandler.post((Runnable)new _$$Lambda$XCWkmsWO8Vw7cZeQUx0r8bL0Lus(mShadeController));
    }
    
    public void postAnimateForceCollapsePanels() {
        this.mHandler.post((Runnable)new _$$Lambda$StatusBar$jxuGhEth_AUZXwOkxxLeo3OCwfQ(this));
    }
    
    public void postAnimateOpenPanels() {
        this.mHandler.sendEmptyMessage(1002);
    }
    
    void postHideRecentApps() {
        if (!this.mHandler.hasMessages(1020)) {
            this.mHandler.removeMessages(1020);
            this.mHandler.sendEmptyMessage(1020);
        }
    }
    
    @Override
    public void postQSRunnableDismissingKeyguard(final Runnable runnable) {
        this.mHandler.post((Runnable)new _$$Lambda$StatusBar$aYUC_5EdgVqg4WjHOuSqtFlaBrg(this, runnable));
    }
    
    @Override
    public void postStartActivityDismissingKeyguard(final PendingIntent pendingIntent) {
        this.mHandler.post((Runnable)new _$$Lambda$StatusBar$FSCUW817Sah6o31V0csstTMMbnA(this, pendingIntent));
    }
    
    @Override
    public void postStartActivityDismissingKeyguard(final Intent intent, final int n) {
        this.mHandler.postDelayed((Runnable)new _$$Lambda$StatusBar$QYfNUZE9eWqxETCXEbiVx13NSWQ(this, intent), (long)n);
    }
    
    @Override
    public void preloadRecentApps() {
        this.mHandler.removeMessages(1022);
        this.mHandler.sendEmptyMessage(1022);
    }
    
    public void readyForKeyguardDone() {
        this.mStatusBarKeyguardViewManager.readyForKeyguardDone();
    }
    
    @VisibleForTesting
    protected void registerBroadcastReceiver() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.app.action.SHOW_DEVICE_MONITORING_DIALOG");
        this.mBroadcastDispatcher.registerReceiver(this.mBroadcastReceiver, intentFilter, null, UserHandle.ALL);
    }
    
    @Override
    public void remQsTile(final ComponentName componentName) {
        final QSPanel mqsPanel = this.mQSPanel;
        if (mqsPanel != null && mqsPanel.getHost() != null) {
            this.mQSPanel.getHost().removeTile(componentName);
        }
    }
    
    public void requestFaceAuth() {
        if (!this.mKeyguardStateController.canDismissLockScreen()) {
            this.mKeyguardUpdateMonitor.requestFaceAuth();
        }
    }
    
    public void requestNotificationUpdate(final String s) {
        this.mNotificationsController.requestNotificationUpdate(s);
    }
    
    public void resetUserExpandedStates() {
        this.mNotificationsController.resetUserExpandedStates();
    }
    
    @VisibleForTesting
    void setBarStateForTest(final int mState) {
        this.mState = mState;
    }
    
    public void setBouncerShowing(final boolean b) {
        this.mBouncerShowing = b;
        this.mKeyguardBypassController.setBouncerShowing(b);
        this.mPulseExpansionHandler.setBouncerShowing(b);
        this.mLockscreenLockIconController.setBouncerShowingScrimmed(this.isBouncerShowingScrimmed());
        final PhoneStatusBarView mStatusBarView = this.mStatusBarView;
        if (mStatusBarView != null) {
            mStatusBarView.setBouncerShowing(b);
        }
        this.updateHideIconsForBouncer(true);
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, true);
        this.updateScrimController();
        if (!this.mBouncerShowing) {
            this.updatePanelExpansionForKeyguard();
        }
    }
    
    public void setInteracting(final int n, final boolean b) {
        final int mInteractingWindows = this.mInteractingWindows;
        boolean b2 = true;
        if ((mInteractingWindows & n) != 0x0 == b) {
            b2 = false;
        }
        int mInteractingWindows2;
        if (b) {
            mInteractingWindows2 = (this.mInteractingWindows | n);
        }
        else {
            mInteractingWindows2 = (this.mInteractingWindows & n);
        }
        this.mInteractingWindows = mInteractingWindows2;
        if (mInteractingWindows2 != 0) {
            this.mAutoHideController.suspendAutoHide();
        }
        else {
            this.mAutoHideController.resumeSuspendedAutoHide();
        }
        if (b2 && b && n == 2) {
            this.mNavigationBarController.touchAutoDim(this.mDisplayId);
            this.dismissVolumeDialog();
        }
        this.checkBarModes();
    }
    
    public void setKeyguardFadingAway(final long n, final long n2, final long n3, final boolean b) {
        this.mCommandQueue.appTransitionStarting(this.mDisplayId, n + n3 - 120L, 120L, true);
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, n3 > 0L);
        this.mCommandQueue.appTransitionStarting(this.mDisplayId, n - 120L, 120L, true);
        this.mKeyguardStateController.notifyKeyguardFadingAway(n2, n3, b);
    }
    
    public void setLockscreenUser(final int n) {
        final LockscreenWallpaper mLockscreenWallpaper = this.mLockscreenWallpaper;
        if (mLockscreenWallpaper != null) {
            mLockscreenWallpaper.setCurrentUser(n);
        }
        this.mScrimController.setCurrentUser(n);
        if (this.mWallpaperSupported) {
            this.mWallpaperChangedReceiver.onReceive(super.mContext, (Intent)null);
        }
    }
    
    public void setNotificationSnoozed(final StatusBarNotification statusBarNotification, final int n) {
        this.mNotificationsController.setNotificationSnoozed(statusBarNotification, n);
    }
    
    public void setNotificationSnoozed(final StatusBarNotification statusBarNotification, final NotificationSwipeActionHelper.SnoozeOption snoozeOption) {
        this.mNotificationsController.setNotificationSnoozed(statusBarNotification, snoozeOption);
    }
    
    public void setOccluded(final boolean b) {
        this.mIsOccluded = b;
        this.mScrimController.setKeyguardOccluded(b);
        this.updateHideIconsForBouncer(false);
    }
    
    public void setPanelExpanded(final boolean panelExpanded) {
        this.mPanelExpanded = panelExpanded;
        this.updateHideIconsForBouncer(false);
        this.mNotificationShadeWindowController.setPanelExpanded(panelExpanded);
        this.mVisualStabilityManager.setPanelExpanded(panelExpanded);
        if (panelExpanded && this.mStatusBarStateController.getState() != 1) {
            this.clearNotificationEffects();
        }
        if (!panelExpanded) {
            this.mRemoteInputManager.onPanelCollapsed();
        }
    }
    
    public void setQsExpanded(final boolean qsExpanded) {
        this.mNotificationShadeWindowController.setQsExpanded(qsExpanded);
        final NotificationPanelViewController mNotificationPanelViewController = this.mNotificationPanelViewController;
        int statusAccessibilityImportance;
        if (qsExpanded) {
            statusAccessibilityImportance = 4;
        }
        else {
            statusAccessibilityImportance = 0;
        }
        mNotificationPanelViewController.setStatusAccessibilityImportance(statusAccessibilityImportance);
        if (this.getNavigationBarView() != null) {
            this.getNavigationBarView().onStatusBarPanelStateChanged();
        }
    }
    
    void setQsScrimEnabled(final boolean qsScrimEnabled) {
        this.mNotificationPanelViewController.setQsScrimEnabled(qsScrimEnabled);
    }
    
    @Override
    public void setTopAppHidesStatusBar(final boolean mTopHidesStatusBar) {
        this.mTopHidesStatusBar = mTopHidesStatusBar;
        if (!mTopHidesStatusBar && this.mWereIconsJustHidden) {
            this.mWereIconsJustHidden = false;
            this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, true);
        }
        this.updateHideIconsForBouncer(true);
    }
    
    protected void setUpDisableFlags(final int n, final int n2) {
        this.mCommandQueue.disable(this.mDisplayId, n, n2, false);
    }
    
    @VisibleForTesting
    void setUserSetupForTest(final boolean mUserSetup) {
        this.mUserSetup = mUserSetup;
    }
    
    @Override
    public void setWindowState(int n, final int n2, final int mStatusBarWindowState) {
        if (n != this.mDisplayId) {
            return;
        }
        boolean mStatusBarWindowHidden = true;
        if (mStatusBarWindowState == 0) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (this.mNotificationShadeWindowView != null && n2 == 1 && this.mStatusBarWindowState != mStatusBarWindowState) {
            this.mStatusBarWindowState = mStatusBarWindowState;
            if (n == 0 && this.mState == 0) {
                this.mStatusBarView.collapsePanel(false, false, 1.0f);
            }
            if (this.mStatusBarView != null) {
                if (mStatusBarWindowState != 2) {
                    mStatusBarWindowHidden = false;
                }
                this.mStatusBarWindowHidden = mStatusBarWindowHidden;
                this.updateHideIconsForBouncer(false);
            }
        }
    }
    
    public boolean shouldIgnoreTouch() {
        return this.mStatusBarStateController.isDozing() && this.mDozeServiceHost.getIgnoreTouchWhilePulsing();
    }
    
    protected boolean shouldUnlockOnMenuPressed() {
        return this.mDeviceInteractive && this.mState != 0 && this.mStatusBarKeyguardViewManager.shouldDismissOnMenuPressed();
    }
    
    @Override
    public void showAssistDisclosure() {
        this.mAssistManagerLazy.get().showDisclosure();
    }
    
    public void showKeyguard() {
        this.mStatusBarStateController.setKeyguardRequested(true);
        this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(false);
        this.updateIsKeyguard();
        this.mAssistManagerLazy.get().onLockscreenShown();
    }
    
    public void showKeyguardImpl() {
        this.mIsKeyguard = true;
        if (this.mKeyguardStateController.isLaunchTransitionFadingAway()) {
            this.mNotificationPanelViewController.cancelAnimation();
            this.onLaunchTransitionFadingEnded();
        }
        this.mHandler.removeMessages(1003);
        final UserSwitcherController mUserSwitcherController = this.mUserSwitcherController;
        if (mUserSwitcherController != null && mUserSwitcherController.useFullscreenUserSwitcher()) {
            this.mStatusBarStateController.setState(3);
        }
        else if (!this.mPulseExpansionHandler.isWakingToShadeLocked()) {
            this.mStatusBarStateController.setState(1);
        }
        this.updatePanelExpansionForKeyguard();
        final NotificationEntry mDraggedDownEntry = this.mDraggedDownEntry;
        if (mDraggedDownEntry != null) {
            mDraggedDownEntry.setUserLocked(false);
            this.mDraggedDownEntry.notifyHeightChanged(false);
            this.mDraggedDownEntry = null;
        }
    }
    
    @Override
    public void showPinningEnterExitToast(final boolean b) {
        if (this.getNavigationBarView() != null) {
            this.getNavigationBarView().showPinningEnterExitToast(b);
        }
    }
    
    @Override
    public void showPinningEscapeToast() {
        if (this.getNavigationBarView() != null) {
            this.getNavigationBarView().showPinningEscapeToast();
        }
    }
    
    @Override
    public void showScreenPinningRequest(final int n) {
        if (this.mKeyguardStateController.isShowing()) {
            return;
        }
        this.showScreenPinningRequest(n, true);
    }
    
    public void showScreenPinningRequest(final int n, final boolean b) {
        this.mScreenPinningRequest.showPrompt(n, b);
    }
    
    @Override
    public void showTransient(final int n, final int[] array) {
        if (n != this.mDisplayId) {
            return;
        }
        if (!InsetsState.containsType(array, 0)) {
            return;
        }
        this.showTransientUnchecked();
    }
    
    @Override
    public void showWirelessChargingAnimation(final int n) {
        if (!this.mDozing && !this.mKeyguardManager.isKeyguardLocked()) {
            WirelessChargingAnimation.makeWirelessChargingAnimation(super.mContext, null, n, null, false).show();
        }
        else {
            WirelessChargingAnimation.makeWirelessChargingAnimation(super.mContext, null, n, (WirelessChargingAnimation.Callback)new WirelessChargingAnimation.Callback() {
                @Override
                public void onAnimationEnded() {
                    CrossFadeHelper.fadeIn((View)StatusBar.this.mNotificationPanelViewController.getView());
                }
                
                @Override
                public void onAnimationStarting() {
                    CrossFadeHelper.fadeOut((View)StatusBar.this.mNotificationPanelViewController.getView(), 1.0f);
                }
            }, this.mDozing).show();
        }
    }
    
    @Override
    public void start() {
        this.mScreenLifecycle.addObserver(this.mScreenObserver);
        this.mWakefulnessLifecycle.addObserver(this.mWakefulnessObserver);
        this.mUiModeManager = (UiModeManager)super.mContext.getSystemService((Class)UiModeManager.class);
        this.mBypassHeadsUpNotifier.setUp();
        this.mBubbleController.setExpandListener(this.mBubbleExpandListener);
        this.mActivityIntentHelper = new ActivityIntentHelper(super.mContext);
        this.mColorExtractor.addOnColorsChangedListener((ColorExtractor$OnColorsChangedListener)this);
        this.mStatusBarStateController.addCallback(this, 0);
        this.mWindowManager = (WindowManager)super.mContext.getSystemService("window");
        this.mDreamManager = IDreamManager$Stub.asInterface(ServiceManager.checkService("dreams"));
        final Display defaultDisplay = this.mWindowManager.getDefaultDisplay();
        this.mDisplay = defaultDisplay;
        this.mDisplayId = defaultDisplay.getDisplayId();
        this.updateDisplaySize();
        this.mVibrateOnOpening = super.mContext.getResources().getBoolean(R$bool.config_vibrateOnIconAnimation);
        WindowManagerGlobal.getWindowManagerService();
        this.mDevicePolicyManager = (DevicePolicyManager)super.mContext.getSystemService("device_policy");
        final AccessibilityManager accessibilityManager = (AccessibilityManager)super.mContext.getSystemService("accessibility");
        this.mKeyguardUpdateMonitor.setKeyguardBypassController(this.mKeyguardBypassController);
        this.mBarService = IStatusBarService$Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mKeyguardManager = (KeyguardManager)super.mContext.getSystemService("keyguard");
        this.mWallpaperSupported = ((WallpaperManager)super.mContext.getSystemService((Class)WallpaperManager.class)).isWallpaperSupported();
        this.mCommandQueue.addCallback((CommandQueue.Callbacks)this);
        RegisterStatusBarResult registerStatusBar;
        try {
            registerStatusBar = this.mBarService.registerStatusBar((IStatusBar)this.mCommandQueue);
        }
        catch (RemoteException ex) {
            ex.rethrowFromSystemServer();
            registerStatusBar = null;
        }
        this.createAndAddWindows(registerStatusBar);
        if (this.mWallpaperSupported) {
            this.mBroadcastDispatcher.registerReceiver(this.mWallpaperChangedReceiver, new IntentFilter("android.intent.action.WALLPAPER_CHANGED"), null, UserHandle.ALL);
            this.mWallpaperChangedReceiver.onReceive(super.mContext, (Intent)null);
        }
        this.setUpPresenter();
        if (InsetsState.containsType(registerStatusBar.mTransientBarTypes, 0)) {
            this.showTransientUnchecked();
        }
        this.onSystemBarAppearanceChanged(this.mDisplayId, registerStatusBar.mAppearance, registerStatusBar.mAppearanceRegions, registerStatusBar.mNavbarColorManagedByIme);
        this.mAppFullscreen = registerStatusBar.mAppFullscreen;
        this.mAppImmersive = registerStatusBar.mAppImmersive;
        ((CommandQueue.Callbacks)this).setImeWindowStatus(this.mDisplayId, registerStatusBar.mImeToken, registerStatusBar.mImeWindowVis, registerStatusBar.mImeBackDisposition, registerStatusBar.mShowImeSwitcher);
        for (int size = registerStatusBar.mIcons.size(), i = 0; i < size; ++i) {
            this.mCommandQueue.setIcon((String)registerStatusBar.mIcons.keyAt(i), (StatusBarIcon)registerStatusBar.mIcons.valueAt(i));
        }
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.systemui.statusbar.banner_action_cancel");
        intentFilter.addAction("com.android.systemui.statusbar.banner_action_setup");
        super.mContext.registerReceiver(this.mBannerActionBroadcastReceiver, intentFilter, "com.android.systemui.permission.SELF", (Handler)null);
        while (true) {
            if (!this.mWallpaperSupported) {
                break Label_0544;
            }
            final IWallpaperManager interface1 = IWallpaperManager$Stub.asInterface(ServiceManager.getService("wallpaper"));
            try {
                interface1.setInAmbientMode(false, 0L);
                this.mIconPolicy.init();
                new StatusBarSignalPolicy(super.mContext, this.mIconController);
                this.mKeyguardStateController.addCallback((KeyguardStateController.Callback)this);
                this.startKeyguard();
                this.mKeyguardUpdateMonitor.registerCallback(this.mUpdateCallback);
                this.mDozeServiceHost.initialize(this, this.mNotificationIconAreaController, this.mStatusBarKeyguardViewManager, this.mNotificationShadeWindowViewController, this.mNotificationPanelViewController, this.mAmbientIndicationContainer);
                this.mConfigurationController.addCallback((ConfigurationController.ConfigurationListener)this);
                this.mInitController.addPostInitTask(new _$$Lambda$StatusBar$JvvAhae_7rLxiigih01CvipegIM(this, registerStatusBar.mDisabledFlags1, registerStatusBar.mDisabledFlags2));
                this.mPluginManager.addPluginListener((PluginListener<Plugin>)new PluginListener<OverlayPlugin>() {
                    private ArraySet<OverlayPlugin> mOverlays = new ArraySet();
                    
                    @Override
                    public void onPluginConnected(final OverlayPlugin overlayPlugin, final Context context) {
                        StatusBar.this.mMainThreadHandler.post((Runnable)new _$$Lambda$StatusBar$5$Qb_zhFNCkzRUWOzTJgRH72E70q0(this, overlayPlugin));
                    }
                    
                    @Override
                    public void onPluginDisconnected(final OverlayPlugin overlayPlugin) {
                        StatusBar.this.mMainThreadHandler.post((Runnable)new _$$Lambda$StatusBar$5$KN25JTGIyA1c8HpRGz8WZDvwP0Y(this, overlayPlugin));
                    }
                    
                    class Callback implements OverlayPlugin.Callback
                    {
                        private final OverlayPlugin mPlugin;
                        
                        Callback(final OverlayPlugin mPlugin) {
                            this.mPlugin = mPlugin;
                        }
                        
                        @Override
                        public void onHoldStatusBarOpenChange() {
                            if (this.mPlugin.holdStatusBarOpen()) {
                                PluginListener.this.mOverlays.add((Object)this.mPlugin);
                            }
                            else {
                                PluginListener.this.mOverlays.remove((Object)this.mPlugin);
                            }
                            StatusBar.this.mMainThreadHandler.post((Runnable)new _$$Lambda$StatusBar$5$Callback$U2F2_aeucZtrnZrV13H_iSFQwOM(this));
                        }
                    }
                }, OverlayPlugin.class, true);
            }
            catch (RemoteException ex2) {
                continue;
            }
            break;
        }
    }
    
    @Override
    public void startActivity(final Intent intent, final boolean b) {
        this.startActivityDismissingKeyguard(intent, false, b);
    }
    
    @Override
    public void startActivity(final Intent intent, final boolean b, final ActivityStarter.Callback callback) {
        this.startActivityDismissingKeyguard(intent, false, b, false, callback, 0);
    }
    
    @Override
    public void startActivity(final Intent intent, final boolean b, final boolean b2) {
        this.startActivityDismissingKeyguard(intent, b, b2);
    }
    
    @Override
    public void startActivity(final Intent intent, final boolean b, final boolean b2, final int n) {
        this.startActivityDismissingKeyguard(intent, b, b2, n);
    }
    
    public void startActivityDismissingKeyguard(final Intent intent, final boolean b, final boolean b2) {
        this.startActivityDismissingKeyguard(intent, b, b2, 0);
    }
    
    public void startActivityDismissingKeyguard(final Intent intent, final boolean b, final boolean b2, final int n) {
        this.startActivityDismissingKeyguard(intent, b, b2, false, null, n);
    }
    
    public void startActivityDismissingKeyguard(final Intent intent, final boolean b, final boolean b2, final boolean b3, final ActivityStarter.Callback callback, final int n) {
        if (b && !this.mDeviceProvisionedController.isDeviceProvisioned()) {
            return;
        }
        this.executeRunnableDismissingKeyguard(new _$$Lambda$StatusBar$b3hghl7W3pt7f8AJV_PUvyhb3Dg(this, intent, n, b3, callback), new _$$Lambda$StatusBar$zj3_DK5ImD3954b2E55XoNAEEno(callback), b2, this.mActivityIntentHelper.wouldLaunchResolverActivity(intent, this.mLockscreenUserManager.getCurrentUserId()), true);
    }
    
    @Override
    public void startAssist(final Bundle bundle) {
        this.mAssistManagerLazy.get().startAssist(bundle);
    }
    
    protected void startKeyguard() {
        Trace.beginSection("StatusBar#startKeyguard");
        this.mBiometricUnlockController = this.mBiometricUnlockControllerLazy.get();
        this.mStatusBarKeyguardViewManager.registerStatusBar(this, this.getBouncerContainer(), this.mNotificationPanelViewController, this.mBiometricUnlockController, this.mDismissCallbackRegistry, (ViewGroup)this.mNotificationShadeWindowView.findViewById(R$id.lock_icon_container), (View)this.mStackScroller, this.mKeyguardBypassController, this.mFalsingManager);
        this.mKeyguardIndicationController.setStatusBarKeyguardViewManager(this.mStatusBarKeyguardViewManager);
        this.mBiometricUnlockController.setStatusBarKeyguardViewManager(this.mStatusBarKeyguardViewManager);
        this.mRemoteInputManager.getController().addCallback((RemoteInputController.Callback)this.mStatusBarKeyguardViewManager);
        this.mDynamicPrivacyController.setStatusBarKeyguardViewManager(this.mStatusBarKeyguardViewManager);
        this.mLightBarController.setBiometricUnlockController(this.mBiometricUnlockController);
        this.mMediaManager.setBiometricUnlockController(this.mBiometricUnlockController);
        this.mKeyguardDismissUtil.setDismissHandler(new _$$Lambda$StatusBar$SBKxeejWdiPVIq__MxMI8pU8ipA(this));
        Trace.endSection();
    }
    
    public void startLaunchTransitionTimeout() {
        this.mHandler.sendEmptyMessageDelayed(1003, 5000L);
    }
    
    @Override
    public void startPendingIntentDismissingKeyguard(final PendingIntent pendingIntent) {
        this.startPendingIntentDismissingKeyguard(pendingIntent, null);
    }
    
    @Override
    public void startPendingIntentDismissingKeyguard(final PendingIntent pendingIntent, final Runnable runnable) {
        this.startPendingIntentDismissingKeyguard(pendingIntent, runnable, null);
    }
    
    @Override
    public void startPendingIntentDismissingKeyguard(final PendingIntent pendingIntent, final Runnable runnable, final View view) {
        this.executeActionDismissingKeyguard(new _$$Lambda$StatusBar$G92BXER_W8UnFfV7mBvrfVJTKQY(this, pendingIntent, view, runnable), pendingIntent.isActivity() && this.mActivityIntentHelper.wouldLaunchResolverActivity(pendingIntent.getIntent(), this.mLockscreenUserManager.getCurrentUserId()));
    }
    
    @Override
    public void suppressAmbientDisplay(final boolean dozeSuppressed) {
        this.mDozeServiceHost.setDozeSuppressed(dozeSuppressed);
    }
    
    protected void toggleKeyboardShortcuts(final int n) {
        KeyboardShortcuts.toggle(super.mContext, n);
    }
    
    @Override
    public void toggleKeyboardShortcutsMenu(final int n) {
        this.mHandler.removeMessages(1026);
        this.mHandler.obtainMessage(1026, n, 0).sendToTarget();
    }
    
    @Override
    public void togglePanel() {
        if (this.mPanelExpanded) {
            this.mShadeController.animateCollapsePanels();
        }
        else {
            this.animateExpandNotificationsPanel();
        }
    }
    
    @Override
    public void toggleSplitScreen() {
        this.toggleSplitScreenMode(-1, -1);
    }
    
    protected boolean toggleSplitScreenMode(final int n, int n2) {
        final boolean present = this.mRecentsOptional.isPresent();
        final int n3 = 0;
        if (!present) {
            return false;
        }
        Divider divider;
        if (this.mDividerOptional.isPresent()) {
            divider = this.mDividerOptional.get();
        }
        else {
            divider = null;
        }
        if (divider != null && divider.inSplitMode()) {
            if (divider.isMinimized() && !divider.isHomeStackResizable()) {
                return false;
            }
            divider.onUndockingTask();
            if (n2 != -1) {
                this.mMetricsLogger.action(n2);
            }
            return true;
        }
        else {
            final int navBarPosition = WindowManagerWrapper.getInstance().getNavBarPosition(this.mDisplayId);
            if (navBarPosition == -1) {
                return false;
            }
            n2 = n3;
            if (navBarPosition == 1) {
                n2 = 1;
            }
            return this.mRecentsOptional.get().splitPrimaryTask(n2, null, n);
        }
    }
    
    @Override
    public void topAppWindowChanged(final int n, final boolean mAppFullscreen, final boolean mAppImmersive) {
        if (n != this.mDisplayId) {
            return;
        }
        this.mAppFullscreen = mAppFullscreen;
        this.mAppImmersive = mAppImmersive;
        this.mStatusBarStateController.setFullscreenState(mAppFullscreen, mAppImmersive);
    }
    
    void updateDisplaySize() {
        this.mDisplay.getMetrics(this.mDisplayMetrics);
        this.mDisplay.getSize(this.mCurrentDisplaySize);
    }
    
    boolean updateIsKeyguard() {
        final int mode = this.mBiometricUnlockController.getMode();
        final int n = 1;
        final boolean b = mode == 1;
        final boolean b2 = this.mDozeServiceHost.getDozingRequested() && (!this.mDeviceInteractive || (this.isGoingToSleep() && (this.isScreenFullyOff() || this.mIsKeyguard)));
        int n2;
        if ((this.mStatusBarStateController.isKeyguardRequested() || b2) && !b) {
            n2 = n;
        }
        else {
            n2 = 0;
        }
        if (b2) {
            this.updatePanelExpansionForKeyguard();
        }
        if (n2 != 0) {
            if (!this.isGoingToSleep() || this.mScreenLifecycle.getScreenState() != 3) {
                this.showKeyguardImpl();
            }
            return false;
        }
        return this.hideKeyguardImpl();
    }
    
    public void updateKeyguardMaxNotifications() {
        if (this.mState == 1 && this.mPresenter.getMaxNotificationsWhileLocked(false) != this.mPresenter.getMaxNotificationsWhileLocked(true)) {
            this.mViewHierarchyManager.updateRowStates();
        }
    }
    
    void updateNotificationPanelTouchState() {
        final boolean goingToSleep = this.isGoingToSleep();
        boolean touchAndAnimationDisabled = false;
        final boolean b = goingToSleep && !this.mDozeParameters.shouldControlScreenOff();
        if ((!this.mDeviceInteractive && !this.mDozeServiceHost.isPulsing()) || b) {
            touchAndAnimationDisabled = true;
        }
        this.mNotificationPanelViewController.setTouchAndAnimationDisabled(touchAndAnimationDisabled);
        this.mNotificationIconAreaController.setAnimationsEnabled(touchAndAnimationDisabled ^ true);
    }
    
    void updateResources() {
        final QSPanel mqsPanel = this.mQSPanel;
        if (mqsPanel != null) {
            mqsPanel.updateResources();
        }
        final StatusBarWindowController mStatusBarWindowController = this.mStatusBarWindowController;
        if (mStatusBarWindowController != null) {
            mStatusBarWindowController.refreshStatusBarHeight();
        }
        final PhoneStatusBarView mStatusBarView = this.mStatusBarView;
        if (mStatusBarView != null) {
            mStatusBarView.updateResources();
        }
        final NotificationPanelViewController mNotificationPanelViewController = this.mNotificationPanelViewController;
        if (mNotificationPanelViewController != null) {
            mNotificationPanelViewController.updateResources();
        }
        final BrightnessMirrorController mBrightnessMirrorController = this.mBrightnessMirrorController;
        if (mBrightnessMirrorController != null) {
            mBrightnessMirrorController.updateResources();
        }
    }
    
    @VisibleForTesting
    void updateScrimController() {
        Trace.beginSection("StatusBar#updateScrimController");
        final boolean b = this.mBiometricUnlockController.isWakeAndUnlock() || this.mKeyguardStateController.isKeyguardFadingAway();
        this.mScrimController.setExpansionAffectsAlpha(true ^ this.mBiometricUnlockController.isBiometricUnlock());
        final boolean launchingAffordanceWithPreview = this.mNotificationPanelViewController.isLaunchingAffordanceWithPreview();
        this.mScrimController.setLaunchingAffordanceWithPreview(launchingAffordanceWithPreview);
        if (this.mBouncerShowing) {
            ScrimState scrimState;
            if (this.mStatusBarKeyguardViewManager.bouncerNeedsScrimming()) {
                scrimState = ScrimState.BOUNCER_SCRIMMED;
            }
            else {
                scrimState = ScrimState.BOUNCER;
            }
            this.mScrimController.transitionTo(scrimState);
        }
        else if (!this.isInLaunchTransition() && !this.mLaunchCameraWhenFinishedWaking && !launchingAffordanceWithPreview) {
            if (this.mBrightnessMirrorVisible) {
                this.mScrimController.transitionTo(ScrimState.BRIGHTNESS_MIRROR);
            }
            else if (this.mDozeServiceHost.isPulsing()) {
                this.mScrimController.transitionTo(ScrimState.PULSING, this.mDozeScrimController.getScrimCallback());
            }
            else if (this.mDozeServiceHost.hasPendingScreenOffCallback()) {
                this.mScrimController.transitionTo(ScrimState.OFF, (ScrimController.Callback)new ScrimController.Callback() {
                    @Override
                    public void onFinished() {
                        StatusBar.this.mDozeServiceHost.executePendingScreenOffCallback();
                    }
                });
            }
            else if (this.mDozing && !b) {
                this.mScrimController.transitionTo(ScrimState.AOD);
            }
            else if (this.mIsKeyguard && !b) {
                this.mScrimController.transitionTo(ScrimState.KEYGUARD);
            }
            else if (this.mBubbleController.isStackExpanded()) {
                this.mScrimController.transitionTo(ScrimState.BUBBLE_EXPANDED);
            }
            else {
                this.mScrimController.transitionTo(ScrimState.UNLOCKED, this.mUnlockScrimCallback);
            }
        }
        else {
            this.mScrimController.transitionTo(ScrimState.UNLOCKED, this.mUnlockScrimCallback);
        }
        Trace.endSection();
    }
    
    protected void updateTheme() {
        int theme;
        if (this.mColorExtractor.getNeutralColors().supportsDarkText()) {
            theme = R$style.Theme_SystemUI_Light;
        }
        else {
            theme = R$style.Theme_SystemUI;
        }
        if (super.mContext.getThemeResId() != theme) {
            super.mContext.setTheme(theme);
            this.mConfigurationController.notifyThemeChanged();
        }
    }
    
    protected void updateVisibleToUser() {
        final boolean mVisibleToUser = this.mVisibleToUser;
        int mVisibleToUser2;
        if (this.mVisible && this.mDeviceInteractive) {
            mVisibleToUser2 = 1;
        }
        else {
            mVisibleToUser2 = 0;
        }
        this.mVisibleToUser = (mVisibleToUser2 != 0);
        if ((mVisibleToUser ? 1 : 0) != mVisibleToUser2) {
            this.handleVisibleToUserChanged((boolean)(mVisibleToUser2 != 0));
        }
    }
    
    public void userActivity() {
        if (this.mState == 1) {
            this.mKeyguardViewMediatorCallback.userActivity();
        }
    }
    
    void vibrate() {
        ((Vibrator)super.mContext.getSystemService("vibrator")).vibrate(250L, StatusBar.VIBRATION_ATTRIBUTES);
    }
    
    void visibilityChanged(final boolean mVisible) {
        if (this.mVisible != mVisible && !(this.mVisible = mVisible)) {
            this.mGutsManager.closeAndSaveGuts(true, true, true, -1, -1, true);
        }
        this.updateVisibleToUser();
    }
    
    public void wakeUpIfDozing(final long n, final View view, final String str) {
        if (this.mDozing) {
            final PowerManager mPowerManager = this.mPowerManager;
            final StringBuilder sb = new StringBuilder();
            sb.append("com.android.systemui:");
            sb.append(str);
            mPowerManager.wakeUp(n, 4, sb.toString());
            this.mWakeUpComingFromTouch = true;
            view.getLocationInWindow(this.mTmpInt2);
            this.mWakeUpTouchLocation = new PointF((float)(this.mTmpInt2[0] + view.getWidth() / 2), (float)(this.mTmpInt2[1] + view.getHeight() / 2));
            this.mFalsingManager.onScreenOnFromTouch();
        }
    }
    
    protected class H extends Handler
    {
        public void handleMessage(final Message message) {
            final int what = message.what;
            if (what != 1026) {
                if (what != 1027) {
                    switch (what) {
                        case 1003: {
                            StatusBar.this.onLaunchTransitionTimeout();
                            break;
                        }
                        case 1002: {
                            StatusBar.this.animateExpandSettingsPanel((String)message.obj);
                            break;
                        }
                        case 1001: {
                            StatusBar.this.mShadeController.animateCollapsePanels();
                            break;
                        }
                        case 1000: {
                            StatusBar.this.animateExpandNotificationsPanel();
                            break;
                        }
                    }
                }
                else {
                    StatusBar.this.dismissKeyboardShortcuts();
                }
            }
            else {
                StatusBar.this.toggleKeyboardShortcuts(message.arg1);
            }
        }
    }
}
