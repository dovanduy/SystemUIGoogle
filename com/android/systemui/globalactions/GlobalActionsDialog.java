// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.globalactions;

import android.widget.ImageView$ScaleType;
import android.util.FeatureFlagUtils;
import android.view.View$OnLongClickListener;
import android.app.IStopUserCallback;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R$drawable;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R$color;
import android.app.ActivityManager;
import android.net.Uri;
import android.sysprop.TelephonyProperties;
import com.android.systemui.Interpolators;
import com.android.internal.colorextraction.ColorExtractor;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import com.android.systemui.volume.SystemUIInterpolators$LogAccelerateInterpolator;
import com.android.internal.colorextraction.ColorExtractor$GradientColors;
import android.animation.ValueAnimator;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.widget.FrameLayout;
import com.android.internal.view.RotationPolicy;
import com.android.internal.colorextraction.drawable.ScrimDrawable;
import android.view.accessibility.AccessibilityEvent;
import android.view.View$AccessibilityDelegate;
import android.view.View$OnClickListener;
import com.android.systemui.R$id;
import com.android.systemui.util.leak.RotationUtils;
import com.android.systemui.R$layout;
import android.view.Window;
import android.os.Binder;
import android.os.IBinder;
import com.android.systemui.MultiListLayout;
import com.android.internal.colorextraction.ColorExtractor$OnColorsChangedListener;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.app.PendingIntent;
import com.android.internal.logging.UiEventLogger$UiEventEnum;
import android.content.DialogInterface;
import android.content.ComponentName;
import java.util.function.Consumer;
import com.android.systemui.controls.ControlsServiceInfo;
import android.content.SharedPreferences;
import android.view.WindowManager$LayoutParams;
import android.os.Bundle;
import android.provider.Settings$Secure;
import android.os.SystemProperties;
import android.util.ArraySet;
import android.os.UserHandle;
import java.util.Iterator;
import java.util.List;
import android.os.RemoteException;
import android.util.Log;
import android.graphics.drawable.Drawable;
import android.content.pm.UserInfo;
import com.android.systemui.R$string;
import android.provider.Settings$Global;
import android.content.IntentFilter;
import android.view.ContextThemeWrapper;
import com.android.systemui.R$style;
import android.os.Message;
import android.telephony.ServiceState;
import android.content.Intent;
import com.android.systemui.controls.controller.ControlsController;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.net.ConnectivityManager;
import com.android.systemui.plugins.GlobalActions;
import android.os.UserManager;
import com.android.internal.logging.UiEventLogger;
import android.app.trust.TrustManager;
import android.telecom.TelecomManager;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.util.ScreenshotHelper;
import com.android.internal.util.ScreenRecordHelper;
import android.content.res.Resources;
import android.telephony.PhoneStateListener;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.util.ArrayList;
import android.view.IWindowManager;
import android.app.IActivityManager;
import android.os.Handler;
import com.android.internal.util.EmergencyAffordanceManager;
import android.service.dreams.IDreamManager;
import android.app.admin.DevicePolicyManager;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.controls.ui.ControlsUiController;
import com.android.systemui.controls.management.ControlsListingController;
import android.content.Context;
import android.content.ContentResolver;
import android.content.BroadcastReceiver;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.BlurUtils;
import java.util.concurrent.Executor;
import android.media.AudioManager;
import android.database.ContentObserver;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.GlobalActionsPanelPlugin;
import com.android.systemui.statusbar.policy.ConfigurationController;
import android.content.DialogInterface$OnShowListener;
import android.content.DialogInterface$OnDismissListener;

public class GlobalActionsDialog implements DialogInterface$OnDismissListener, DialogInterface$OnShowListener, ConfigurationListener, Callbacks
{
    private final ActivityStarter mActivityStarter;
    private MyAdapter mAdapter;
    private ContentObserver mAirplaneModeObserver;
    private ToggleAction mAirplaneModeOn;
    private State mAirplaneState;
    private boolean mAnyControlsProviders;
    private final AudioManager mAudioManager;
    private final Executor mBackgroundExecutor;
    private final BlurUtils mBlurUtils;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private BroadcastReceiver mBroadcastReceiver;
    private final ConfigurationController mConfigurationController;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private final ControlsListingController mControlsListingController;
    private ControlsUiController mControlsUiController;
    private final NotificationShadeDepthController mDepthController;
    private final DevicePolicyManager mDevicePolicyManager;
    private boolean mDeviceProvisioned;
    private ActionsDialog mDialog;
    private final IDreamManager mDreamManager;
    private final EmergencyAffordanceManager mEmergencyAffordanceManager;
    private Handler mHandler;
    private boolean mHasTelephony;
    private boolean mHasVibrator;
    private final IActivityManager mIActivityManager;
    private final IWindowManager mIWindowManager;
    private boolean mIsWaitingForEcmExit;
    private ArrayList<Action> mItems;
    private boolean mKeyguardShowing;
    private final KeyguardStateController mKeyguardStateController;
    private final LockPatternUtils mLockPatternUtils;
    private final MetricsLogger mMetricsLogger;
    private final NotificationShadeWindowController mNotificationShadeWindowController;
    private GlobalActionsPanelPlugin mPanelPlugin;
    PhoneStateListener mPhoneStateListener;
    private final Resources mResources;
    private BroadcastReceiver mRingerModeReceiver;
    private final ScreenRecordHelper mScreenRecordHelper;
    private final ScreenshotHelper mScreenshotHelper;
    private final boolean mShowSilentToggle;
    private Action mSilentModeAction;
    private final IStatusBarService mStatusBarService;
    private final SysuiColorExtractor mSysuiColorExtractor;
    private final TelecomManager mTelecomManager;
    private final TrustManager mTrustManager;
    private final UiEventLogger mUiEventLogger;
    private final UserManager mUserManager;
    private final GlobalActions.GlobalActionsManager mWindowManagerFuncs;
    
    public GlobalActionsDialog(final Context context, final GlobalActions.GlobalActionsManager mWindowManagerFuncs, final AudioManager mAudioManager, final IDreamManager mDreamManager, final DevicePolicyManager mDevicePolicyManager, final LockPatternUtils mLockPatternUtils, final BroadcastDispatcher mBroadcastDispatcher, final ConnectivityManager connectivityManager, final TelephonyManager telephonyManager, final ContentResolver mContentResolver, final Vibrator vibrator, final Resources mResources, final ConfigurationController mConfigurationController, final ActivityStarter mActivityStarter, final KeyguardStateController mKeyguardStateController, final UserManager mUserManager, final TrustManager mTrustManager, final IActivityManager miActivityManager, final TelecomManager mTelecomManager, final MetricsLogger mMetricsLogger, final NotificationShadeDepthController mDepthController, final SysuiColorExtractor mSysuiColorExtractor, final IStatusBarService mStatusBarService, final BlurUtils mBlurUtils, final NotificationShadeWindowController mNotificationShadeWindowController, final ControlsUiController mControlsUiController, final IWindowManager miWindowManager, final Executor mBackgroundExecutor, final ControlsListingController mControlsListingController, final ControlsController controlsController, final UiEventLogger mUiEventLogger) {
        final boolean b = false;
        this.mKeyguardShowing = false;
        this.mDeviceProvisioned = false;
        this.mAirplaneState = State.Off;
        this.mIsWaitingForEcmExit = false;
        this.mAnyControlsProviders = false;
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if (!"android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(action) && !"android.intent.action.SCREEN_OFF".equals(action)) {
                    if ("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED".equals(action) && !intent.getBooleanExtra("android.telephony.extra.PHONE_IN_ECM_STATE", false) && GlobalActionsDialog.this.mIsWaitingForEcmExit) {
                        GlobalActionsDialog.this.mIsWaitingForEcmExit = false;
                        GlobalActionsDialog.this.changeAirplaneModeSystemSetting(true);
                    }
                }
                else {
                    final String stringExtra = intent.getStringExtra("reason");
                    if (!"globalactions".equals(stringExtra)) {
                        GlobalActionsDialog.this.mHandler.sendMessage(GlobalActionsDialog.this.mHandler.obtainMessage(0, (Object)stringExtra));
                    }
                }
            }
        };
        this.mPhoneStateListener = new PhoneStateListener() {
            public void onServiceStateChanged(final ServiceState serviceState) {
                if (!GlobalActionsDialog.this.mHasTelephony) {
                    return;
                }
                final boolean b = serviceState.getState() == 3;
                final GlobalActionsDialog this$0 = GlobalActionsDialog.this;
                State state;
                if (b) {
                    state = State.On;
                }
                else {
                    state = State.Off;
                }
                this$0.mAirplaneState = state;
                GlobalActionsDialog.this.mAirplaneModeOn.updateState(GlobalActionsDialog.this.mAirplaneState);
                GlobalActionsDialog.this.mAdapter.notifyDataSetChanged();
            }
        };
        this.mRingerModeReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (intent.getAction().equals("android.media.RINGER_MODE_CHANGED")) {
                    GlobalActionsDialog.this.mHandler.sendEmptyMessage(1);
                }
            }
        };
        this.mAirplaneModeObserver = new ContentObserver(new Handler()) {
            public void onChange(final boolean b) {
                GlobalActionsDialog.this.onAirplaneModeChanged();
            }
        };
        this.mHandler = new Handler() {
            public void handleMessage(final Message message) {
                final int what = message.what;
                if (what != 0) {
                    if (what != 1) {
                        if (what == 2) {
                            GlobalActionsDialog.this.handleShow();
                        }
                    }
                    else {
                        GlobalActionsDialog.this.refreshSilentMode();
                        GlobalActionsDialog.this.mAdapter.notifyDataSetChanged();
                    }
                }
                else if (GlobalActionsDialog.this.mDialog != null) {
                    if ("dream".equals(message.obj)) {
                        GlobalActionsDialog.this.mDialog.dismissImmediately();
                    }
                    else {
                        GlobalActionsDialog.this.mDialog.dismiss();
                    }
                    GlobalActionsDialog.this.mDialog = null;
                }
            }
        };
        this.mContext = (Context)new ContextThemeWrapper(context, R$style.qs_theme);
        this.mWindowManagerFuncs = mWindowManagerFuncs;
        this.mAudioManager = mAudioManager;
        this.mDreamManager = mDreamManager;
        this.mDevicePolicyManager = mDevicePolicyManager;
        this.mLockPatternUtils = mLockPatternUtils;
        this.mKeyguardStateController = mKeyguardStateController;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mContentResolver = mContentResolver;
        this.mResources = mResources;
        this.mConfigurationController = mConfigurationController;
        this.mUserManager = mUserManager;
        this.mTrustManager = mTrustManager;
        this.mIActivityManager = miActivityManager;
        this.mTelecomManager = mTelecomManager;
        this.mMetricsLogger = mMetricsLogger;
        this.mUiEventLogger = mUiEventLogger;
        this.mDepthController = mDepthController;
        this.mSysuiColorExtractor = mSysuiColorExtractor;
        this.mStatusBarService = mStatusBarService;
        this.mNotificationShadeWindowController = mNotificationShadeWindowController;
        this.mControlsUiController = mControlsUiController;
        this.mIWindowManager = miWindowManager;
        this.mBackgroundExecutor = mBackgroundExecutor;
        this.mControlsListingController = mControlsListingController;
        this.mBlurUtils = mBlurUtils;
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED");
        this.mBroadcastDispatcher.registerReceiver(this.mBroadcastReceiver, intentFilter);
        this.mHasTelephony = connectivityManager.isNetworkSupported(0);
        telephonyManager.listen(this.mPhoneStateListener, 1);
        mContentResolver.registerContentObserver(Settings$Global.getUriFor("airplane_mode_on"), true, this.mAirplaneModeObserver);
        boolean mHasVibrator = b;
        if (vibrator != null) {
            mHasVibrator = b;
            if (vibrator.hasVibrator()) {
                mHasVibrator = true;
            }
        }
        this.mHasVibrator = mHasVibrator;
        this.mShowSilentToggle = (mResources.getBoolean(17891570) ^ true);
        this.mEmergencyAffordanceManager = new EmergencyAffordanceManager(context);
        this.mScreenshotHelper = new ScreenshotHelper(context);
        this.mScreenRecordHelper = new ScreenRecordHelper(context);
        this.mConfigurationController.addCallback((ConfigurationController.ConfigurationListener)this);
        this.mActivityStarter = mActivityStarter;
        mKeyguardStateController.addCallback((KeyguardStateController.Callback)new KeyguardStateController.Callback() {
            @Override
            public void onUnlockedChanged() {
                if (GlobalActionsDialog.this.mDialog != null && GlobalActionsDialog.this.mDialog.mPanelController != null) {
                    GlobalActionsDialog.this.mDialog.mPanelController.onDeviceLockStateChanged(mKeyguardStateController.isUnlocked() || mKeyguardStateController.canDismissLockScreen());
                }
            }
        });
        this.mControlsListingController.addCallback((ControlsListingController.ControlsListingCallback)new _$$Lambda$GlobalActionsDialog$O_Ah89_G6hEoZ_9KRVsxNAMfaq4(this, this.mContext.getResources().getString(R$string.config_controlsPreferredPackage), context, controlsController));
    }
    
    private void addUsersToMenu(final ArrayList<Action> list) {
        if (this.mUserManager.isUserSwitcherEnabled()) {
            final List users = this.mUserManager.getUsers();
            final UserInfo currentUser = this.getCurrentUser();
            for (final UserInfo userInfo : users) {
                if (userInfo.supportsSwitchToByUser()) {
                    boolean b = true;
                    Label_0097: {
                        if (currentUser == null) {
                            if (userInfo.id == 0) {
                                break Label_0097;
                            }
                        }
                        else if (currentUser.id == userInfo.id) {
                            break Label_0097;
                        }
                        b = false;
                    }
                    final String iconPath = userInfo.iconPath;
                    Drawable fromPath;
                    if (iconPath != null) {
                        fromPath = Drawable.createFromPath(iconPath);
                    }
                    else {
                        fromPath = null;
                    }
                    final StringBuilder sb = new StringBuilder();
                    String name = userInfo.name;
                    if (name == null) {
                        name = "Primary";
                    }
                    sb.append(name);
                    String str;
                    if (b) {
                        str = " \u2714";
                    }
                    else {
                        str = "";
                    }
                    sb.append(str);
                    list.add((Action)new SinglePressAction(17302684, fromPath, sb.toString()) {
                        @Override
                        public void onPress() {
                            try {
                                GlobalActionsDialog.this.mIActivityManager.switchUser(userInfo.id);
                            }
                            catch (RemoteException obj) {
                                final StringBuilder sb = new StringBuilder();
                                sb.append("Couldn't switch user ");
                                sb.append(obj);
                                Log.e("GlobalActionsDialog", sb.toString());
                            }
                        }
                        
                        @Override
                        public boolean showBeforeProvisioning() {
                            return false;
                        }
                        
                        @Override
                        public boolean showDuringKeyguard() {
                            return true;
                        }
                    });
                }
            }
        }
    }
    
    private void awakenIfNecessary() {
        final IDreamManager mDreamManager = this.mDreamManager;
        if (mDreamManager == null) {
            return;
        }
        try {
            if (mDreamManager.isDreaming()) {
                this.mDreamManager.awaken();
            }
        }
        catch (RemoteException ex) {}
    }
    
    private void changeAirplaneModeSystemSetting(final boolean b) {
        Settings$Global.putInt(this.mContentResolver, "airplane_mode_on", (int)(b ? 1 : 0));
        final Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.addFlags(536870912);
        intent.putExtra("state", b);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        if (!this.mHasTelephony) {
            State mAirplaneState;
            if ((b ? 1 : 0) != 0) {
                mAirplaneState = State.On;
            }
            else {
                mAirplaneState = State.Off;
            }
            this.mAirplaneState = mAirplaneState;
        }
    }
    
    private ActionsDialog createDialog() {
        if (!this.mHasVibrator) {
            this.mSilentModeAction = (Action)new SilentModeToggleAction();
        }
        else {
            this.mSilentModeAction = (Action)new SilentModeTriStateAction(this.mAudioManager, this.mHandler);
        }
        this.mAirplaneModeOn = (ToggleAction)new AirplaneModeAction();
        this.onAirplaneModeChanged();
        this.mItems = new ArrayList<Action>();
        final String[] stringArray = this.mResources.getStringArray(17236040);
        final ArraySet set = new ArraySet();
        int n = 0;
        ControlsUiController mControlsUiController;
        while (true) {
            final int length = stringArray.length;
            mControlsUiController = null;
            if (n >= length) {
                break;
            }
            final String s = stringArray[n];
            if (!set.contains((Object)s)) {
                if ("power".equals(s)) {
                    this.mItems.add((Action)new PowerAction());
                }
                else if ("airplane".equals(s)) {
                    this.mItems.add((Action)this.mAirplaneModeOn);
                }
                else if ("bugreport".equals(s)) {
                    if (Settings$Global.getInt(this.mContentResolver, "bugreport_in_power_menu", 0) != 0 && this.isCurrentUserOwner()) {
                        this.mItems.add((Action)new BugReportAction());
                    }
                }
                else if ("silent".equals(s)) {
                    if (this.mShowSilentToggle) {
                        this.mItems.add(this.mSilentModeAction);
                    }
                }
                else if ("users".equals(s)) {
                    if (SystemProperties.getBoolean("fw.power_user_switcher", false)) {
                        this.addUsersToMenu(this.mItems);
                    }
                }
                else if ("settings".equals(s)) {
                    this.mItems.add(this.getSettingsAction());
                }
                else if ("lockdown".equals(s)) {
                    if (Settings$Secure.getIntForUser(this.mContentResolver, "lockdown_in_power_menu", 0, this.getCurrentUser().id) != 0 && this.shouldDisplayLockdown()) {
                        this.mItems.add(this.getLockdownAction());
                    }
                }
                else if ("voiceassist".equals(s)) {
                    this.mItems.add(this.getVoiceAssistAction());
                }
                else if ("assist".equals(s)) {
                    this.mItems.add(this.getAssistAction());
                }
                else if ("restart".equals(s)) {
                    this.mItems.add((Action)new RestartAction());
                }
                else if ("screenshot".equals(s)) {
                    this.mItems.add((Action)new ScreenshotAction());
                }
                else if ("logout".equals(s)) {
                    if (this.mDevicePolicyManager.isLogoutEnabled() && this.getCurrentUser().id != 0) {
                        this.mItems.add((Action)new LogoutAction());
                    }
                }
                else if ("emergency".equals(s)) {
                    if (!this.mEmergencyAffordanceManager.needsEmergencyAffordance()) {
                        this.mItems.add((Action)new EmergencyDialerAction());
                    }
                }
                else {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Invalid global action key ");
                    sb.append(s);
                    Log.e("GlobalActionsDialog", sb.toString());
                }
                set.add((Object)s);
            }
            ++n;
        }
        if (this.mEmergencyAffordanceManager.needsEmergencyAffordance()) {
            this.mItems.add((Action)new EmergencyAffordanceAction());
        }
        this.mAdapter = new MyAdapter();
        this.mDepthController.setShowingHomeControls(this.shouldShowControls());
        final Context mContext = this.mContext;
        final MyAdapter mAdapter = this.mAdapter;
        final PanelViewController walletPanelViewController = this.getWalletPanelViewController();
        final NotificationShadeDepthController mDepthController = this.mDepthController;
        final SysuiColorExtractor mSysuiColorExtractor = this.mSysuiColorExtractor;
        final IStatusBarService mStatusBarService = this.mStatusBarService;
        final NotificationShadeWindowController mNotificationShadeWindowController = this.mNotificationShadeWindowController;
        if (this.shouldShowControls()) {
            mControlsUiController = this.mControlsUiController;
        }
        final ActionsDialog actionsDialog = new ActionsDialog(mContext, mAdapter, walletPanelViewController, mDepthController, mSysuiColorExtractor, mStatusBarService, mNotificationShadeWindowController, mControlsUiController, this.mBlurUtils);
        actionsDialog.setCanceledOnTouchOutside(false);
        actionsDialog.setKeyguardShowing(this.mKeyguardShowing);
        actionsDialog.setOnDismissListener((DialogInterface$OnDismissListener)this);
        actionsDialog.setOnShowListener((DialogInterface$OnShowListener)this);
        return actionsDialog;
    }
    
    private Action getAssistAction() {
        return (Action)new SinglePressAction(17302296, 17040240) {
            @Override
            public void onPress() {
                final Intent intent = new Intent("android.intent.action.ASSIST");
                intent.addFlags(335544320);
                GlobalActionsDialog.this.mContext.startActivity(intent);
            }
            
            @Override
            public boolean showBeforeProvisioning() {
                return true;
            }
            
            @Override
            public boolean showDuringKeyguard() {
                return true;
            }
        };
    }
    
    private UserInfo getCurrentUser() {
        try {
            return this.mIActivityManager.getCurrentUser();
        }
        catch (RemoteException ex) {
            return null;
        }
    }
    
    private Action getLockdownAction() {
        return (Action)new SinglePressAction(17302468, 17040244) {
            @Override
            public void onPress() {
                GlobalActionsDialog.this.mLockPatternUtils.requireStrongAuth(32, -1);
                try {
                    GlobalActionsDialog.this.mIWindowManager.lockNow((Bundle)null);
                    GlobalActionsDialog.this.mBackgroundExecutor.execute(new _$$Lambda$GlobalActionsDialog$5$u5Ns0wVEJav3TRu44tbisdIWku0(this));
                }
                catch (RemoteException ex) {
                    Log.e("GlobalActionsDialog", "Error while trying to lock device.", (Throwable)ex);
                }
            }
            
            @Override
            public boolean showBeforeProvisioning() {
                return false;
            }
            
            @Override
            public boolean showDuringKeyguard() {
                return true;
            }
        };
    }
    
    private Action getSettingsAction() {
        return (Action)new SinglePressAction(17302811, 17040250) {
            @Override
            public void onPress() {
                final Intent intent = new Intent("android.settings.SETTINGS");
                intent.addFlags(335544320);
                GlobalActionsDialog.this.mContext.startActivity(intent);
            }
            
            @Override
            public boolean showBeforeProvisioning() {
                return true;
            }
            
            @Override
            public boolean showDuringKeyguard() {
                return true;
            }
        };
    }
    
    private Action getVoiceAssistAction() {
        return (Action)new SinglePressAction(17302853, 17040254) {
            @Override
            public void onPress() {
                final Intent intent = new Intent("android.intent.action.VOICE_ASSIST");
                intent.addFlags(335544320);
                GlobalActionsDialog.this.mContext.startActivity(intent);
            }
            
            @Override
            public boolean showBeforeProvisioning() {
                return true;
            }
            
            @Override
            public boolean showDuringKeyguard() {
                return true;
            }
        };
    }
    
    private PanelViewController getWalletPanelViewController() {
        final GlobalActionsPanelPlugin mPanelPlugin = this.mPanelPlugin;
        if (mPanelPlugin == null) {
            return null;
        }
        return mPanelPlugin.onPanelShown((GlobalActionsPanelPlugin.Callbacks)this, this.mKeyguardStateController.isUnlocked() ^ true);
    }
    
    private void handleShow() {
        this.awakenIfNecessary();
        this.mDialog = this.createDialog();
        this.prepareDialog();
        if (this.mAdapter.getCount() == 1 && this.mAdapter.getItem(0) instanceof SinglePressAction && !(this.mAdapter.getItem(0) instanceof LongPressAction)) {
            ((SinglePressAction)this.mAdapter.getItem(0)).onPress();
        }
        else {
            final WindowManager$LayoutParams attributes = this.mDialog.getWindow().getAttributes();
            attributes.setTitle((CharSequence)"ActionsDialog");
            attributes.layoutInDisplayCutoutMode = 3;
            this.mDialog.getWindow().setAttributes(attributes);
            this.mDialog.show();
            this.mWindowManagerFuncs.onGlobalActionsShown();
        }
    }
    
    private boolean isCurrentUserOwner() {
        final UserInfo currentUser = this.getCurrentUser();
        return currentUser == null || currentUser.isPrimary();
    }
    
    private static boolean isForceGridEnabled(final Context context) {
        return isPanelDebugModeEnabled(context);
    }
    
    private static boolean isPanelDebugModeEnabled(final Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        boolean b = false;
        if (Settings$Secure.getInt(contentResolver, "global_actions_panel_debug_enabled", 0) == 1) {
            b = true;
        }
        return b;
    }
    
    private void lockProfiles() {
        final int id = this.getCurrentUser().id;
        for (final int n : this.mUserManager.getEnabledProfileIds(id)) {
            if (n != id) {
                this.mTrustManager.setDeviceLockedForUser(n, true);
            }
        }
    }
    
    private void onAirplaneModeChanged() {
        if (this.mHasTelephony) {
            return;
        }
        final ContentResolver mContentResolver = this.mContentResolver;
        boolean b = false;
        if (Settings$Global.getInt(mContentResolver, "airplane_mode_on", 0) == 1) {
            b = true;
        }
        State mAirplaneState;
        if (b) {
            mAirplaneState = State.On;
        }
        else {
            mAirplaneState = State.Off;
        }
        this.mAirplaneState = mAirplaneState;
        this.mAirplaneModeOn.updateState(mAirplaneState);
    }
    
    private void prepareDialog() {
        this.refreshSilentMode();
        this.mAirplaneModeOn.updateState(this.mAirplaneState);
        this.mAdapter.notifyDataSetChanged();
        if (this.mShowSilentToggle) {
            this.mBroadcastDispatcher.registerReceiver(this.mRingerModeReceiver, new IntentFilter("android.media.RINGER_MODE_CHANGED"));
        }
    }
    
    private void refreshSilentMode() {
        if (!this.mHasVibrator) {
            final boolean b = this.mAudioManager.getRingerMode() != 2;
            final ToggleAction toggleAction = (ToggleAction)this.mSilentModeAction;
            State state;
            if (b) {
                state = State.On;
            }
            else {
                state = State.Off;
            }
            toggleAction.updateState(state);
        }
    }
    
    private boolean shouldDisplayLockdown() {
        final boolean methodSecure = this.mKeyguardStateController.isMethodSecure();
        boolean b = false;
        if (!methodSecure) {
            return false;
        }
        final int strongAuthForUser = this.mLockPatternUtils.getStrongAuthForUser(this.getCurrentUser().id);
        if (strongAuthForUser == 0 || strongAuthForUser == 4) {
            b = true;
        }
        return b;
    }
    
    private boolean shouldShowControls() {
        return this.mKeyguardStateController.isUnlocked() && this.mControlsUiController.getAvailable() && this.mAnyControlsProviders;
    }
    
    public void destroy() {
        this.mConfigurationController.removeCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    public void dismissDialog() {
        this.mHandler.removeMessages(0);
        this.mHandler.sendEmptyMessage(0);
    }
    
    public void dismissGlobalActionsMenu() {
        this.dismissDialog();
    }
    
    public void onDismiss(final DialogInterface dialogInterface) {
        if (this.mDialog == dialogInterface) {
            this.mDialog = null;
        }
        this.mWindowManagerFuncs.onGlobalActionsHidden();
        if (this.mShowSilentToggle) {
            try {
                this.mBroadcastDispatcher.unregisterReceiver(this.mRingerModeReceiver);
            }
            catch (IllegalArgumentException ex) {
                Log.w("GlobalActionsDialog", (Throwable)ex);
            }
        }
    }
    
    public void onShow(final DialogInterface dialogInterface) {
        this.mMetricsLogger.visible(1568);
        this.mUiEventLogger.log((UiEventLogger$UiEventEnum)GlobalActionsEvent.GA_POWER_MENU_OPEN);
    }
    
    public void onUiModeChanged() {
        this.mContext.getTheme().applyStyle(this.mContext.getThemeResId(), true);
        final ActionsDialog mDialog = this.mDialog;
        if (mDialog != null && mDialog.isShowing()) {
            this.mDialog.refreshDialog();
        }
    }
    
    public void showDialog(final boolean mKeyguardShowing, final boolean mDeviceProvisioned, final GlobalActionsPanelPlugin mPanelPlugin) {
        this.mKeyguardShowing = mKeyguardShowing;
        this.mDeviceProvisioned = mDeviceProvisioned;
        this.mPanelPlugin = mPanelPlugin;
        final ActionsDialog mDialog = this.mDialog;
        if (mDialog != null) {
            mDialog.dismiss();
            this.mDialog = null;
            this.mHandler.sendEmptyMessage(2);
        }
        else {
            this.handleShow();
        }
    }
    
    public void startPendingIntentDismissingKeyguard(final PendingIntent pendingIntent) {
        this.mActivityStarter.startPendingIntentDismissingKeyguard(pendingIntent);
    }
    
    public interface Action
    {
        View create(final Context p0, final View p1, final ViewGroup p2, final LayoutInflater p3);
        
        boolean isEnabled();
        
        void onPress();
        
        default boolean shouldBeSeparated() {
            return false;
        }
        
        boolean showBeforeProvisioning();
        
        boolean showDuringKeyguard();
    }
    
    private static final class ActionsDialog extends Dialog implements DialogInterface, ColorExtractor$OnColorsChangedListener
    {
        private final MyAdapter mAdapter;
        private Drawable mBackgroundDrawable;
        private final BlurUtils mBlurUtils;
        private final SysuiColorExtractor mColorExtractor;
        private final Context mContext;
        private ControlsUiController mControlsUiController;
        private ViewGroup mControlsView;
        private final NotificationShadeDepthController mDepthController;
        private MultiListLayout mGlobalActionsLayout;
        private boolean mHadTopUi;
        private boolean mKeyguardShowing;
        private final NotificationShadeWindowController mNotificationShadeWindowController;
        private final PanelViewController mPanelController;
        private ResetOrientationData mResetOrientationData;
        private float mScrimAlpha;
        private boolean mShowing;
        private final IStatusBarService mStatusBarService;
        private final IBinder mToken;
        
        ActionsDialog(final Context mContext, final MyAdapter mAdapter, final PanelViewController mPanelController, final NotificationShadeDepthController mDepthController, final SysuiColorExtractor mColorExtractor, final IStatusBarService mStatusBarService, final NotificationShadeWindowController mNotificationShadeWindowController, final ControlsUiController mControlsUiController, final BlurUtils mBlurUtils) {
            super(mContext, R$style.Theme_SystemUI_Dialog_GlobalActions);
            this.mToken = (IBinder)new Binder();
            this.mContext = mContext;
            this.mAdapter = mAdapter;
            this.mDepthController = mDepthController;
            this.mColorExtractor = mColorExtractor;
            this.mStatusBarService = mStatusBarService;
            this.mNotificationShadeWindowController = mNotificationShadeWindowController;
            this.mControlsUiController = mControlsUiController;
            this.mBlurUtils = mBlurUtils;
            final Window window = this.getWindow();
            window.requestFeature(1);
            window.getDecorView();
            final WindowManager$LayoutParams attributes = window.getAttributes();
            attributes.systemUiVisibility |= 0x700;
            window.setLayout(-1, -1);
            window.clearFlags(2);
            window.addFlags(17629472);
            window.setType(2020);
            window.getAttributes().setFitInsetsTypes(0);
            this.setTitle(17040255);
            this.mPanelController = mPanelController;
            this.initializeLayout();
        }
        
        private void completeDismiss() {
            this.mNotificationShadeWindowController.setForceHasTopUi(this.mHadTopUi);
            this.mDepthController.updateGlobalDialogVisibility(0.0f, null);
            super.dismiss();
        }
        
        private void dismissPanel() {
            final PanelViewController mPanelController = this.mPanelController;
            if (mPanelController != null) {
                mPanelController.onDismissed();
            }
        }
        
        private void fixNavBarClipping() {
            final ViewGroup viewGroup = (ViewGroup)this.findViewById(16908290);
            viewGroup.setClipChildren(false);
            viewGroup.setClipToPadding(false);
            final ViewGroup viewGroup2 = (ViewGroup)viewGroup.getParent();
            viewGroup2.setClipChildren(false);
            viewGroup2.setClipToPadding(false);
        }
        
        private int getGlobalActionsLayoutId(final Context context) {
            if (this.mControlsUiController != null) {
                return R$layout.global_actions_grid_v2;
            }
            final int rotation = RotationUtils.getRotation(context);
            final boolean b = isForceGridEnabled(context) || (this.shouldUsePanel() && rotation == 0);
            if (rotation == 2) {
                if (b) {
                    return R$layout.global_actions_grid_seascape;
                }
                return R$layout.global_actions_column_seascape;
            }
            else {
                if (b) {
                    return R$layout.global_actions_grid;
                }
                return R$layout.global_actions_column;
            }
        }
        
        private void initializeLayout() {
            this.setContentView(this.getGlobalActionsLayoutId(this.mContext));
            this.fixNavBarClipping();
            this.mControlsView = (ViewGroup)this.findViewById(R$id.global_actions_controls);
            (this.mGlobalActionsLayout = (MultiListLayout)this.findViewById(R$id.global_actions_view)).setOutsideTouchListener((View$OnClickListener)new _$$Lambda$GlobalActionsDialog$ActionsDialog$dNZefhFQEiKyxgSvmP1LBM0gtx4(this));
            this.mGlobalActionsLayout.setListViewAccessibilityDelegate(new View$AccessibilityDelegate() {
                public boolean dispatchPopulateAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
                    accessibilityEvent.getText().add(ActionsDialog.this.mContext.getString(17040255));
                    return true;
                }
            });
            this.mGlobalActionsLayout.setRotationListener((MultiListLayout.RotationListener)new _$$Lambda$yTIuIImgAFK3eAYSmNsa3QUABJI(this));
            this.mGlobalActionsLayout.setAdapter((MultiListLayout.MultiListAdapter)this.mAdapter);
            ((View)this.mGlobalActionsLayout.getParent()).setOnClickListener((View$OnClickListener)new _$$Lambda$GlobalActionsDialog$ActionsDialog$qLnbwfmuMw_GJ7JUyo3Qt6_cEh4(this));
            final View viewById = this.findViewById(R$id.global_actions_grid_root);
            if (viewById != null) {
                viewById.setOnClickListener((View$OnClickListener)new _$$Lambda$GlobalActionsDialog$ActionsDialog$56DLCMO5mz26TtbyNqA0WIFSkog(this));
            }
            if (this.shouldUsePanel()) {
                this.initializePanel();
            }
            if (this.mBackgroundDrawable == null) {
                this.mBackgroundDrawable = (Drawable)new ScrimDrawable();
                if (this.mControlsUiController != null) {
                    this.mScrimAlpha = 1.0f;
                }
                else {
                    float mScrimAlpha;
                    if (this.mBlurUtils.supportsBlursOnWindows()) {
                        mScrimAlpha = 0.54f;
                    }
                    else {
                        mScrimAlpha = 0.75f;
                    }
                    this.mScrimAlpha = mScrimAlpha;
                }
            }
            this.getWindow().setBackgroundDrawable(this.mBackgroundDrawable);
        }
        
        private void initializePanel() {
            final int rotation = RotationUtils.getRotation(this.mContext);
            final boolean rotationLocked = RotationPolicy.isRotationLocked(this.mContext);
            if (rotation != 0) {
                if (rotationLocked) {
                    if (this.mResetOrientationData == null) {
                        final ResetOrientationData mResetOrientationData = new ResetOrientationData();
                        this.mResetOrientationData = mResetOrientationData;
                        mResetOrientationData.locked = true;
                        mResetOrientationData.rotation = rotation;
                    }
                    this.mGlobalActionsLayout.post((Runnable)new _$$Lambda$GlobalActionsDialog$ActionsDialog$KOOsXb68KZ6uVivL8nC_5NKKiBk(this));
                }
            }
            else {
                if (!rotationLocked) {
                    if (this.mResetOrientationData == null) {
                        final ResetOrientationData mResetOrientationData2 = new ResetOrientationData();
                        this.mResetOrientationData = mResetOrientationData2;
                        mResetOrientationData2.locked = false;
                    }
                    this.mGlobalActionsLayout.post((Runnable)new _$$Lambda$GlobalActionsDialog$ActionsDialog$RJgtbpfP8gfKx4bDDYXf9gg3qxs(this));
                }
                this.setRotationSuggestionsEnabled(false);
                ((FrameLayout)this.findViewById(R$id.global_actions_panel_container)).addView(this.mPanelController.getPanelContent(), (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
            }
        }
        
        private void resetOrientation() {
            final ResetOrientationData mResetOrientationData = this.mResetOrientationData;
            if (mResetOrientationData != null) {
                RotationPolicy.setRotationLockAtAngle(this.mContext, mResetOrientationData.locked, mResetOrientationData.rotation);
            }
            this.setRotationSuggestionsEnabled(true);
        }
        
        private void setRotationSuggestionsEnabled(final boolean b) {
            try {
                final int identifier = Binder.getCallingUserHandle().getIdentifier();
                int n;
                if (b) {
                    n = 0;
                }
                else {
                    n = 16;
                }
                this.mStatusBarService.disable2ForUser(n, this.mToken, this.mContext.getPackageName(), identifier);
            }
            catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }
        
        private boolean shouldUsePanel() {
            final PanelViewController mPanelController = this.mPanelController;
            return mPanelController != null && mPanelController.getPanelContent() != null;
        }
        
        private void updateColors(final ColorExtractor$GradientColors colorExtractor$GradientColors, final boolean b) {
            if (!(this.mBackgroundDrawable instanceof ScrimDrawable)) {
                return;
            }
            final boolean b2 = this.mControlsUiController != null;
            final ScrimDrawable scrimDrawable = (ScrimDrawable)this.mBackgroundDrawable;
            int n;
            if (!b2 && colorExtractor$GradientColors.supportsDarkText()) {
                n = -1;
            }
            else {
                n = -16777216;
            }
            scrimDrawable.setColor(n, b);
            final View decorView = this.getWindow().getDecorView();
            if (colorExtractor$GradientColors.supportsDarkText()) {
                decorView.setSystemUiVisibility(8208);
            }
            else {
                decorView.setSystemUiVisibility(0);
            }
        }
        
        public void dismiss() {
            if (!this.mShowing) {
                return;
            }
            this.mShowing = false;
            final ControlsUiController mControlsUiController = this.mControlsUiController;
            if (mControlsUiController != null) {
                mControlsUiController.hide();
            }
            this.mGlobalActionsLayout.setTranslationX(0.0f);
            this.mGlobalActionsLayout.setTranslationY(0.0f);
            this.mGlobalActionsLayout.setAlpha(1.0f);
            this.mGlobalActionsLayout.animate().alpha(0.0f).translationX(this.mGlobalActionsLayout.getAnimationOffsetX()).translationY(this.mGlobalActionsLayout.getAnimationOffsetY()).setDuration(550L).withEndAction((Runnable)new _$$Lambda$GlobalActionsDialog$ActionsDialog$b7BjyiDlA1YYZd2S_4WLEfoJbac(this)).setInterpolator((TimeInterpolator)new SystemUIInterpolators$LogAccelerateInterpolator()).setUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$GlobalActionsDialog$ActionsDialog$_0WJKduv0QvmLhPuj3fXKKiMDpo(this)).start();
            this.dismissPanel();
            this.resetOrientation();
        }
        
        void dismissImmediately() {
            this.mShowing = false;
            final ControlsUiController mControlsUiController = this.mControlsUiController;
            if (mControlsUiController != null) {
                mControlsUiController.hide();
            }
            this.dismissPanel();
            this.resetOrientation();
            this.completeDismiss();
        }
        
        public void onColorsChanged(final ColorExtractor colorExtractor, final int n) {
            if (this.mKeyguardShowing) {
                if ((n & 0x2) != 0x0) {
                    this.updateColors(colorExtractor.getColors(2), true);
                }
            }
            else if ((n & 0x1) != 0x0) {
                this.updateColors(colorExtractor.getColors(1), true);
            }
        }
        
        public void onRotate(final int n, final int n2) {
            if (this.mShowing) {
                this.refreshDialog();
            }
        }
        
        protected void onStart() {
            super.setCanceledOnTouchOutside(true);
            super.onStart();
            this.mGlobalActionsLayout.updateList();
            if (this.mBackgroundDrawable instanceof ScrimDrawable) {
                this.mColorExtractor.addOnColorsChangedListener((ColorExtractor$OnColorsChangedListener)this);
                this.updateColors(this.mColorExtractor.getNeutralColors(), false);
            }
        }
        
        protected void onStop() {
            super.onStop();
            this.mColorExtractor.removeOnColorsChangedListener((ColorExtractor$OnColorsChangedListener)this);
        }
        
        public void refreshDialog() {
            this.initializeLayout();
            this.mGlobalActionsLayout.updateList();
        }
        
        public void setKeyguardShowing(final boolean mKeyguardShowing) {
            this.mKeyguardShowing = mKeyguardShowing;
        }
        
        public void show() {
            super.show();
            this.mShowing = true;
            this.mHadTopUi = this.mNotificationShadeWindowController.getForceHasTopUi();
            this.mNotificationShadeWindowController.setForceHasTopUi(true);
            this.mBackgroundDrawable.setAlpha(0);
            final MultiListLayout mGlobalActionsLayout = this.mGlobalActionsLayout;
            mGlobalActionsLayout.setTranslationX(mGlobalActionsLayout.getAnimationOffsetX());
            final MultiListLayout mGlobalActionsLayout2 = this.mGlobalActionsLayout;
            mGlobalActionsLayout2.setTranslationY(mGlobalActionsLayout2.getAnimationOffsetY());
            this.mGlobalActionsLayout.setAlpha(0.0f);
            this.mGlobalActionsLayout.animate().alpha(1.0f).translationX(0.0f).translationY(0.0f).setDuration(450L).setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN).setUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$GlobalActionsDialog$ActionsDialog$C9bHmWE3unC7OleIlzCLrtXN5ig(this)).start();
            final ControlsUiController mControlsUiController = this.mControlsUiController;
            if (mControlsUiController != null) {
                mControlsUiController.show(this.mControlsView);
            }
        }
        
        private static class ResetOrientationData
        {
            public boolean locked;
            public int rotation;
        }
    }
    
    private class AirplaneModeAction extends ToggleAction
    {
        AirplaneModeAction() {
            super(17302461, 17302463, 17040258, 17040257, 17040256);
        }
        
        @Override
        protected void changeStateFromPress(final boolean b) {
            if (!GlobalActionsDialog.this.mHasTelephony) {
                return;
            }
            if (!TelephonyProperties.in_ecm_mode().orElse(Boolean.FALSE)) {
                State mState;
                if (b) {
                    mState = State.TurningOn;
                }
                else {
                    mState = State.TurningOff;
                }
                super.mState = mState;
                GlobalActionsDialog.this.mAirplaneState = mState;
            }
        }
        
        @Override
        void onToggle(final boolean b) {
            if (GlobalActionsDialog.this.mHasTelephony && TelephonyProperties.in_ecm_mode().orElse(Boolean.FALSE)) {
                GlobalActionsDialog.this.mIsWaitingForEcmExit = true;
                final Intent intent = new Intent("android.telephony.action.SHOW_NOTICE_ECM_BLOCK_OTHERS", (Uri)null);
                intent.addFlags(268435456);
                GlobalActionsDialog.this.mContext.startActivity(intent);
            }
            else {
                GlobalActionsDialog.this.changeAirplaneModeSystemSetting(b);
            }
        }
        
        @Override
        public boolean showBeforeProvisioning() {
            return false;
        }
        
        @Override
        public boolean showDuringKeyguard() {
            return true;
        }
    }
    
    private class BugReportAction extends SinglePressAction implements LongPressAction
    {
        public BugReportAction() {
            super(17302465, 17039780);
        }
        
        @Override
        public boolean onLongPress() {
            if (ActivityManager.isUserAMonkey()) {
                return false;
            }
            try {
                GlobalActionsDialog.this.mMetricsLogger.action(293);
                GlobalActionsDialog.this.mIActivityManager.requestFullBugReport();
                return false;
            }
            catch (RemoteException ex) {
                return false;
            }
        }
        
        @Override
        public void onPress() {
            if (ActivityManager.isUserAMonkey()) {
                return;
            }
            GlobalActionsDialog.this.mHandler.postDelayed((Runnable)new Runnable() {
                @Override
                public void run() {
                    try {
                        GlobalActionsDialog.this.mMetricsLogger.action(292);
                        if (!GlobalActionsDialog.this.mIActivityManager.launchBugReportHandlerApp()) {
                            Log.w("GlobalActionsDialog", "Bugreport handler could not be launched");
                            GlobalActionsDialog.this.mIActivityManager.requestInteractiveBugReport();
                        }
                    }
                    catch (RemoteException ex) {}
                }
            }, 500L);
        }
        
        @Override
        public boolean showBeforeProvisioning() {
            return false;
        }
        
        @Override
        public boolean showDuringKeyguard() {
            return true;
        }
    }
    
    private abstract class EmergencyAction extends SinglePressAction
    {
        EmergencyAction(final GlobalActionsDialog globalActionsDialog, final int n, final int n2) {
            globalActionsDialog.super(n, n2);
        }
        
        @Override
        public View create(final Context context, View create, final ViewGroup viewGroup, final LayoutInflater layoutInflater) {
            create = super.create(context, create, viewGroup, layoutInflater);
            int n;
            if (this.shouldBeSeparated()) {
                n = create.getResources().getColor(R$color.global_actions_alert_text);
            }
            else {
                n = create.getResources().getColor(R$color.global_actions_text);
            }
            final TextView textView = (TextView)create.findViewById(16908299);
            textView.setTextColor(n);
            textView.setSelected(true);
            ((ImageView)create.findViewById(16908294)).getDrawable().setTint(n);
            return create;
        }
        
        @Override
        public boolean shouldBeSeparated() {
            return true;
        }
        
        @Override
        public boolean showBeforeProvisioning() {
            return true;
        }
        
        @Override
        public boolean showDuringKeyguard() {
            return true;
        }
    }
    
    private class EmergencyAffordanceAction extends EmergencyAction
    {
        EmergencyAffordanceAction() {
            super(17302213, 17040242);
        }
        
        @Override
        public void onPress() {
            GlobalActionsDialog.this.mEmergencyAffordanceManager.performEmergencyCall();
        }
    }
    
    private class EmergencyDialerAction extends EmergencyAction
    {
        private EmergencyDialerAction() {
            super(R$drawable.ic_emergency_star, 17040242);
        }
        
        @Override
        public void onPress() {
            GlobalActionsDialog.this.mMetricsLogger.action(1569);
            if (GlobalActionsDialog.this.mTelecomManager != null) {
                final Intent launchEmergencyDialerIntent = GlobalActionsDialog.this.mTelecomManager.createLaunchEmergencyDialerIntent((String)null);
                launchEmergencyDialerIntent.addFlags(343932928);
                launchEmergencyDialerIntent.putExtra("com.android.phone.EmergencyDialer.extra.ENTRY_TYPE", 2);
                GlobalActionsDialog.this.mContext.startActivityAsUser(launchEmergencyDialerIntent, UserHandle.CURRENT);
            }
        }
    }
    
    @VisibleForTesting
    public enum GlobalActionsEvent implements UiEventLogger$UiEventEnum
    {
        GA_POWER_MENU_OPEN(337);
        
        private final int mId;
        
        private GlobalActionsEvent(final int mId) {
            this.mId = mId;
        }
        
        public int getId() {
            return this.mId;
        }
    }
    
    private final class LogoutAction extends SinglePressAction
    {
        private LogoutAction() {
            super(17302515, 17040245);
        }
        
        @Override
        public void onPress() {
            GlobalActionsDialog.this.mHandler.postDelayed((Runnable)new _$$Lambda$GlobalActionsDialog$LogoutAction$3H17sX2I_BqMu2dZ5Dekk1AEv_U(this), 500L);
        }
        
        @Override
        public boolean showBeforeProvisioning() {
            return false;
        }
        
        @Override
        public boolean showDuringKeyguard() {
            return true;
        }
    }
    
    private interface LongPressAction extends Action
    {
        boolean onLongPress();
    }
    
    public class MyAdapter extends MultiListAdapter
    {
        private int countItems(final boolean b) {
            int i = 0;
            int n = 0;
            while (i < GlobalActionsDialog.this.mItems.size()) {
                final Action action = GlobalActionsDialog.this.mItems.get(i);
                int n2 = n;
                if (this.shouldBeShown(action)) {
                    n2 = n;
                    if (action.shouldBeSeparated() == b) {
                        n2 = n + 1;
                    }
                }
                ++i;
                n = n2;
            }
            return n;
        }
        
        private boolean shouldBeShown(final Action action) {
            return (!GlobalActionsDialog.this.mKeyguardShowing || action.showDuringKeyguard()) && (GlobalActionsDialog.this.mDeviceProvisioned || action.showBeforeProvisioning());
        }
        
        public boolean areAllItemsEnabled() {
            return false;
        }
        
        @Override
        public int countListItems() {
            return this.countItems(false);
        }
        
        @Override
        public int countSeparatedItems() {
            return this.countItems(true);
        }
        
        public int getCount() {
            return this.countSeparatedItems() + this.countListItems();
        }
        
        public Action getItem(final int i) {
            int j = 0;
            int n = 0;
            while (j < GlobalActionsDialog.this.mItems.size()) {
                final Action action = GlobalActionsDialog.this.mItems.get(j);
                if (this.shouldBeShown(action)) {
                    if (n == i) {
                        return action;
                    }
                    ++n;
                }
                ++j;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("position ");
            sb.append(i);
            sb.append(" out of range of showable actions, filtered count=");
            sb.append(this.getCount());
            sb.append(", keyguardshowing=");
            sb.append(GlobalActionsDialog.this.mKeyguardShowing);
            sb.append(", provisioned=");
            sb.append(GlobalActionsDialog.this.mDeviceProvisioned);
            throw new IllegalArgumentException(sb.toString());
        }
        
        public long getItemId(final int n) {
            return n;
        }
        
        public View getView(final int n, View create, final ViewGroup viewGroup) {
            final Action item = this.getItem(n);
            create = item.create(GlobalActionsDialog.this.mContext, create, viewGroup, LayoutInflater.from(GlobalActionsDialog.this.mContext));
            create.setOnClickListener((View$OnClickListener)new _$$Lambda$GlobalActionsDialog$MyAdapter$mHwNDdvU6gX4bdQUg9ucB10QA0w(this, n));
            if (item instanceof LongPressAction) {
                create.setOnLongClickListener((View$OnLongClickListener)new _$$Lambda$GlobalActionsDialog$MyAdapter$VSUDyewgk86XHamZik1hS11jzxk(this, n));
            }
            return create;
        }
        
        public boolean isEnabled(final int n) {
            return this.getItem(n).isEnabled();
        }
        
        public void onClickItem(final int n) {
            final Action item = GlobalActionsDialog.this.mAdapter.getItem(n);
            if (!(item instanceof SilentModeTriStateAction)) {
                if (GlobalActionsDialog.this.mDialog != null) {
                    GlobalActionsDialog.this.mDialog.dismiss();
                }
                else {
                    Log.w("GlobalActionsDialog", "Action clicked while mDialog is null.");
                }
                item.onPress();
            }
        }
        
        public boolean onLongClickItem(final int n) {
            final Action item = GlobalActionsDialog.this.mAdapter.getItem(n);
            if (item instanceof LongPressAction) {
                if (GlobalActionsDialog.this.mDialog != null) {
                    GlobalActionsDialog.this.mDialog.dismiss();
                }
                else {
                    Log.w("GlobalActionsDialog", "Action long-clicked while mDialog is null.");
                }
                return ((LongPressAction)item).onLongPress();
            }
            return false;
        }
        
        @Override
        public boolean shouldBeSeparated(final int n) {
            return this.getItem(n).shouldBeSeparated();
        }
    }
    
    private final class PowerAction extends SinglePressAction implements LongPressAction
    {
        private PowerAction() {
            super(17301552, 17040246);
        }
        
        @Override
        public boolean onLongPress() {
            if (!GlobalActionsDialog.this.mUserManager.hasUserRestriction("no_safe_boot")) {
                GlobalActionsDialog.this.mWindowManagerFuncs.reboot(true);
                return true;
            }
            return false;
        }
        
        @Override
        public void onPress() {
            GlobalActionsDialog.this.mWindowManagerFuncs.shutdown();
        }
        
        @Override
        public boolean showBeforeProvisioning() {
            return true;
        }
        
        @Override
        public boolean showDuringKeyguard() {
            return true;
        }
    }
    
    private final class RestartAction extends SinglePressAction implements LongPressAction
    {
        private RestartAction() {
            super(17302803, 17040248);
        }
        
        @Override
        public boolean onLongPress() {
            if (!GlobalActionsDialog.this.mUserManager.hasUserRestriction("no_safe_boot")) {
                GlobalActionsDialog.this.mWindowManagerFuncs.reboot(true);
                return true;
            }
            return false;
        }
        
        @Override
        public void onPress() {
            GlobalActionsDialog.this.mWindowManagerFuncs.reboot(false);
        }
        
        @Override
        public boolean showBeforeProvisioning() {
            return true;
        }
        
        @Override
        public boolean showDuringKeyguard() {
            return true;
        }
    }
    
    private class ScreenshotAction extends SinglePressAction implements LongPressAction
    {
        public ScreenshotAction() {
            super(17302805, 17040249);
        }
        
        @Override
        public boolean onLongPress() {
            if (FeatureFlagUtils.isEnabled(GlobalActionsDialog.this.mContext, "settings_screenrecord_long_press")) {
                GlobalActionsDialog.this.mScreenRecordHelper.launchRecordPrompt();
            }
            else {
                this.onPress();
            }
            return true;
        }
        
        @Override
        public void onPress() {
            GlobalActionsDialog.this.mHandler.postDelayed((Runnable)new Runnable() {
                @Override
                public void run() {
                    GlobalActionsDialog.this.mScreenshotHelper.takeScreenshot(1, true, true, GlobalActionsDialog.this.mHandler, (Consumer)null);
                    GlobalActionsDialog.this.mMetricsLogger.action(1282);
                }
            }, 500L);
        }
        
        @Override
        public boolean showBeforeProvisioning() {
            return false;
        }
        
        @Override
        public boolean showDuringKeyguard() {
            return true;
        }
    }
    
    private class SilentModeToggleAction extends ToggleAction
    {
        public SilentModeToggleAction() {
            super(17302314, 17302313, 17040253, 17040252, 17040251);
        }
        
        @Override
        void onToggle(final boolean b) {
            if (b) {
                GlobalActionsDialog.this.mAudioManager.setRingerMode(0);
            }
            else {
                GlobalActionsDialog.this.mAudioManager.setRingerMode(2);
            }
        }
        
        @Override
        public boolean showBeforeProvisioning() {
            return false;
        }
        
        @Override
        public boolean showDuringKeyguard() {
            return true;
        }
    }
    
    private static class SilentModeTriStateAction implements Action, View$OnClickListener
    {
        private final int[] ITEM_IDS;
        private final AudioManager mAudioManager;
        private final Handler mHandler;
        
        SilentModeTriStateAction(final AudioManager mAudioManager, final Handler mHandler) {
            this.ITEM_IDS = new int[] { 16909243, 16909244, 16909245 };
            this.mAudioManager = mAudioManager;
            this.mHandler = mHandler;
        }
        
        private int indexToRingerMode(final int n) {
            return n;
        }
        
        private int ringerModeToIndex(final int n) {
            return n;
        }
        
        @Override
        public View create(final Context context, View inflate, final ViewGroup viewGroup, final LayoutInflater layoutInflater) {
            inflate = layoutInflater.inflate(17367162, viewGroup, false);
            final int ringerMode = this.mAudioManager.getRingerMode();
            this.ringerModeToIndex(ringerMode);
            for (int i = 0; i < 3; ++i) {
                final View viewById = inflate.findViewById(this.ITEM_IDS[i]);
                viewById.setSelected(ringerMode == i);
                viewById.setTag((Object)i);
                viewById.setOnClickListener((View$OnClickListener)this);
            }
            return inflate;
        }
        
        @Override
        public boolean isEnabled() {
            return true;
        }
        
        public void onClick(final View view) {
            if (!(view.getTag() instanceof Integer)) {
                return;
            }
            final int intValue = (int)view.getTag();
            final AudioManager mAudioManager = this.mAudioManager;
            this.indexToRingerMode(intValue);
            mAudioManager.setRingerMode(intValue);
            this.mHandler.sendEmptyMessageDelayed(0, 300L);
        }
        
        @Override
        public void onPress() {
        }
        
        @Override
        public boolean showBeforeProvisioning() {
            return false;
        }
        
        @Override
        public boolean showDuringKeyguard() {
            return true;
        }
    }
    
    private abstract class SinglePressAction implements Action
    {
        private final Drawable mIcon;
        private final int mIconResId;
        private final CharSequence mMessage;
        private final int mMessageResId;
        
        protected SinglePressAction(final int mIconResId, final int mMessageResId) {
            this.mIconResId = mIconResId;
            this.mMessageResId = mMessageResId;
            this.mMessage = null;
            this.mIcon = null;
        }
        
        protected SinglePressAction(final int mIconResId, final Drawable mIcon, final CharSequence mMessage) {
            this.mIconResId = mIconResId;
            this.mMessageResId = 0;
            this.mMessage = mMessage;
            this.mIcon = mIcon;
        }
        
        @Override
        public View create(final Context context, final View view, final ViewGroup viewGroup, final LayoutInflater layoutInflater) {
            final View inflate = layoutInflater.inflate(this.getActionLayoutId(context), viewGroup, false);
            final ImageView imageView = (ImageView)inflate.findViewById(16908294);
            final TextView textView = (TextView)inflate.findViewById(16908299);
            textView.setSelected(true);
            final Drawable mIcon = this.mIcon;
            if (mIcon != null) {
                imageView.setImageDrawable(mIcon);
                imageView.setScaleType(ImageView$ScaleType.CENTER_CROP);
            }
            else {
                final int mIconResId = this.mIconResId;
                if (mIconResId != 0) {
                    imageView.setImageDrawable(context.getDrawable(mIconResId));
                }
            }
            final CharSequence mMessage = this.mMessage;
            if (mMessage != null) {
                textView.setText(mMessage);
            }
            else {
                textView.setText(this.mMessageResId);
            }
            return inflate;
        }
        
        protected int getActionLayoutId(final Context context) {
            if (GlobalActionsDialog.this.shouldShowControls()) {
                return R$layout.global_actions_grid_item_v2;
            }
            return R$layout.global_actions_grid_item;
        }
        
        @Override
        public boolean isEnabled() {
            return true;
        }
        
        @Override
        public abstract void onPress();
    }
    
    private abstract static class ToggleAction implements Action
    {
        protected int mDisabledIconResid;
        protected int mDisabledStatusMessageResId;
        protected int mEnabledIconResId;
        protected int mEnabledStatusMessageResId;
        protected State mState;
        
        public ToggleAction(final int mEnabledIconResId, final int mDisabledIconResid, final int n, final int mEnabledStatusMessageResId, final int mDisabledStatusMessageResId) {
            this.mState = State.Off;
            this.mEnabledIconResId = mEnabledIconResId;
            this.mDisabledIconResid = mDisabledIconResid;
            this.mEnabledStatusMessageResId = mEnabledStatusMessageResId;
            this.mDisabledStatusMessageResId = mDisabledStatusMessageResId;
        }
        
        protected void changeStateFromPress(final boolean b) {
            State mState;
            if (b) {
                mState = State.On;
            }
            else {
                mState = State.Off;
            }
            this.mState = mState;
        }
        
        @Override
        public View create(final Context context, final View view, final ViewGroup viewGroup, final LayoutInflater layoutInflater) {
            this.willCreate();
            final int global_actions_grid_item = R$layout.global_actions_grid_item;
            boolean b = false;
            final View inflate = layoutInflater.inflate(global_actions_grid_item, viewGroup, false);
            final ImageView imageView = (ImageView)inflate.findViewById(16908294);
            final TextView textView = (TextView)inflate.findViewById(16908299);
            final boolean enabled = this.isEnabled();
            final State mState = this.mState;
            if (mState == State.On || mState == State.TurningOn) {
                b = true;
            }
            if (textView != null) {
                int text;
                if (b) {
                    text = this.mEnabledStatusMessageResId;
                }
                else {
                    text = this.mDisabledStatusMessageResId;
                }
                textView.setText(text);
                textView.setEnabled(enabled);
                textView.setSelected(true);
            }
            if (imageView != null) {
                int n;
                if (b) {
                    n = this.mEnabledIconResId;
                }
                else {
                    n = this.mDisabledIconResid;
                }
                imageView.setImageDrawable(context.getDrawable(n));
                imageView.setEnabled(enabled);
            }
            inflate.setEnabled(enabled);
            return inflate;
        }
        
        @Override
        public boolean isEnabled() {
            return this.mState.inTransition() ^ true;
        }
        
        @Override
        public final void onPress() {
            if (this.mState.inTransition()) {
                Log.w("GlobalActionsDialog", "shouldn't be able to toggle when in transition");
                return;
            }
            final boolean b = this.mState != State.On;
            this.onToggle(b);
            this.changeStateFromPress(b);
        }
        
        abstract void onToggle(final boolean p0);
        
        public void updateState(final State mState) {
            this.mState = mState;
        }
        
        void willCreate() {
        }
        
        enum State
        {
            Off(false), 
            On(false), 
            TurningOff(true), 
            TurningOn(true);
            
            private final boolean inTransition;
            
            private State(final boolean inTransition) {
                this.inTransition = inTransition;
            }
            
            public boolean inTransition() {
                return this.inTransition;
            }
        }
    }
}
