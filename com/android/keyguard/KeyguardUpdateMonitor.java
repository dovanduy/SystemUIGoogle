// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import java.util.function.Predicate;
import android.os.Bundle;
import java.util.Collection;
import android.telephony.CarrierConfigManager;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.content.pm.ResolveInfo;
import com.android.settingslib.WirelessUtils;
import android.hardware.fingerprint.FingerprintManager$CryptoObject;
import android.hardware.biometrics.CryptoObject;
import java.util.function.Supplier;
import android.content.ContentResolver;
import android.provider.Settings$Global;
import java.util.TimeZone;
import com.android.systemui.DejankUtils;
import com.android.systemui.R$string;
import com.android.systemui.util.Assert;
import java.util.Iterator;
import android.telephony.TelephonyManager;
import android.content.pm.UserInfo;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import android.service.dreams.IDreamManager$Stub;
import com.android.internal.widget.LockPatternUtils$StrongAuthTracker;
import android.app.IUserSwitchObserver;
import android.app.ActivityManager;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.os.Message;
import java.util.function.Consumer;
import android.app.ActivityManager$StackInfo;
import android.app.ActivityTaskManager;
import android.os.IRemoteCallback;
import android.hardware.face.FaceManager$AuthenticationResult;
import android.hardware.fingerprint.FingerprintManager$AuthenticationResult;
import android.os.Trace;
import android.hardware.biometrics.BiometricSourceType;
import android.hardware.biometrics.IBiometricEnabledOnKeyguardCallback$Stub;
import android.util.Log;
import com.google.android.collect.Lists;
import com.android.systemui.dump.DumpManager;
import android.os.Looper;
import android.os.RemoteException;
import android.content.pm.IPackageManager$Stub;
import android.os.ServiceManager;
import android.app.UserSwitchObserver;
import android.os.UserManager;
import android.util.SparseArray;
import android.app.trust.TrustManager;
import com.android.systemui.shared.system.TaskStackChangeListener;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionManager$OnSubscriptionsChangedListener;
import android.telephony.SubscriptionInfo;
import java.util.List;
import android.telephony.ServiceState;
import java.util.HashMap;
import android.content.Intent;
import java.util.Map;
import android.telephony.PhoneStateListener;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import android.os.Handler;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager$LockoutResetCallback;
import android.hardware.fingerprint.FingerprintManager$AuthenticationCallback;
import android.util.SparseBooleanArray;
import android.hardware.face.FaceManager;
import android.hardware.face.FaceManager$LockoutResetCallback;
import android.os.CancellationSignal;
import android.hardware.face.FaceManager$AuthenticationCallback;
import android.service.dreams.IDreamManager;
import android.database.ContentObserver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.internal.annotations.VisibleForTesting;
import android.content.BroadcastReceiver;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.IBiometricEnabledOnKeyguardCallback;
import com.android.settingslib.fuelgauge.BatteryStatus;
import java.util.concurrent.Executor;
import android.content.ComponentName;
import com.android.systemui.Dumpable;
import android.app.trust.TrustManager$TrustListener;

public class KeyguardUpdateMonitor implements TrustManager$TrustListener, Dumpable
{
    public static final boolean CORE_APPS_ONLY;
    private static final ComponentName FALLBACK_HOME_COMPONENT;
    private static int sCurrentUser;
    private int mActiveMobileDataSubscription;
    private boolean mAssistantVisible;
    private boolean mAuthInterruptActive;
    private final Executor mBackgroundExecutor;
    private BatteryStatus mBatteryStatus;
    private IBiometricEnabledOnKeyguardCallback mBiometricEnabledCallback;
    private BiometricManager mBiometricManager;
    private boolean mBouncer;
    @VisibleForTesting
    protected final BroadcastReceiver mBroadcastAllReceiver;
    private final BroadcastDispatcher mBroadcastDispatcher;
    @VisibleForTesting
    protected final BroadcastReceiver mBroadcastReceiver;
    private final ArrayList<WeakReference<KeyguardUpdateMonitorCallback>> mCallbacks;
    private final Runnable mCancelNotReceived;
    private final Context mContext;
    private boolean mCredentialAttempted;
    private boolean mDeviceInteractive;
    private final DevicePolicyManager mDevicePolicyManager;
    private boolean mDeviceProvisioned;
    private ContentObserver mDeviceProvisionedObserver;
    private final IDreamManager mDreamManager;
    @VisibleForTesting
    FaceManager$AuthenticationCallback mFaceAuthenticationCallback;
    private CancellationSignal mFaceCancelSignal;
    private final FaceManager$LockoutResetCallback mFaceLockoutResetCallback;
    private FaceManager mFaceManager;
    private int mFaceRunningState;
    private SparseBooleanArray mFaceSettingEnabledForUser;
    private FingerprintManager$AuthenticationCallback mFingerprintAuthenticationCallback;
    private CancellationSignal mFingerprintCancelSignal;
    private boolean mFingerprintLockedOut;
    private final FingerprintManager$LockoutResetCallback mFingerprintLockoutResetCallback;
    private int mFingerprintRunningState;
    private FingerprintManager mFpm;
    private boolean mGoingToSleep;
    private final Handler mHandler;
    private int mHardwareFaceUnavailableRetryCount;
    private int mHardwareFingerprintUnavailableRetryCount;
    private boolean mHasLockscreenWallpaper;
    private boolean mIsDreaming;
    private final boolean mIsPrimaryUser;
    private KeyguardBypassController mKeyguardBypassController;
    private boolean mKeyguardGoingAway;
    private boolean mKeyguardIsVisible;
    private boolean mKeyguardOccluded;
    private boolean mLockIconPressed;
    private LockPatternUtils mLockPatternUtils;
    private boolean mLogoutEnabled;
    private boolean mNeedsSlowUnlockTransition;
    private int mPhoneState;
    @VisibleForTesting
    public PhoneStateListener mPhoneStateListener;
    private Runnable mRetryFaceAuthentication;
    private Runnable mRetryFingerprintAuthentication;
    private int mRingMode;
    private boolean mScreenOn;
    private Map<Integer, Intent> mSecondaryLockscreenRequirement;
    private boolean mSecureCameraLaunched;
    HashMap<Integer, ServiceState> mServiceStates;
    HashMap<Integer, SimData> mSimDatas;
    @VisibleForTesting
    protected StrongAuthTracker mStrongAuthTracker;
    private List<SubscriptionInfo> mSubscriptionInfo;
    private SubscriptionManager$OnSubscriptionsChangedListener mSubscriptionListener;
    private SubscriptionManager mSubscriptionManager;
    private boolean mSwitchingUser;
    private final TaskStackChangeListener mTaskStackListener;
    @VisibleForTesting
    protected boolean mTelephonyCapable;
    private TrustManager mTrustManager;
    private Runnable mUpdateBiometricListeningState;
    @VisibleForTesting
    SparseArray<BiometricAuthenticated> mUserFaceAuthenticated;
    private SparseBooleanArray mUserFaceUnlockRunning;
    @VisibleForTesting
    SparseArray<BiometricAuthenticated> mUserFingerprintAuthenticated;
    private SparseBooleanArray mUserHasTrust;
    private SparseBooleanArray mUserIsUnlocked;
    private UserManager mUserManager;
    private final UserSwitchObserver mUserSwitchObserver;
    private SparseBooleanArray mUserTrustIsManaged;
    private SparseBooleanArray mUserTrustIsUsuallyManaged;
    
    static {
        FALLBACK_HOME_COMPONENT = new ComponentName("com.android.settings", "com.android.settings.FallbackHome");
        try {
            CORE_APPS_ONLY = IPackageManager$Stub.asInterface(ServiceManager.getService("package")).isOnlyCoreApps();
        }
        catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }
    
    @VisibleForTesting
    protected KeyguardUpdateMonitor(final Context mContext, final Looper looper, final BroadcastDispatcher mBroadcastDispatcher, final DumpManager dumpManager, final Executor mBackgroundExecutor) {
        this.mSimDatas = new HashMap<Integer, SimData>();
        this.mServiceStates = new HashMap<Integer, ServiceState>();
        this.mCallbacks = (ArrayList<WeakReference<KeyguardUpdateMonitorCallback>>)Lists.newArrayList();
        this.mFingerprintRunningState = 0;
        this.mFaceRunningState = 0;
        this.mActiveMobileDataSubscription = -1;
        this.mHardwareFingerprintUnavailableRetryCount = 0;
        this.mHardwareFaceUnavailableRetryCount = 0;
        this.mCancelNotReceived = new Runnable() {
            @Override
            public void run() {
                Log.w("KeyguardUpdateMonitor", "Cancel not received, transitioning to STOPPED");
                final KeyguardUpdateMonitor this$0 = KeyguardUpdateMonitor.this;
                this$0.mFaceRunningState = 0;
                this$0.mFingerprintRunningState = 0;
                KeyguardUpdateMonitor.this.updateBiometricListeningState();
            }
        };
        this.mFaceSettingEnabledForUser = new SparseBooleanArray();
        this.mBiometricEnabledCallback = (IBiometricEnabledOnKeyguardCallback)new IBiometricEnabledOnKeyguardCallback$Stub() {
            public void onChanged(final BiometricSourceType biometricSourceType, final boolean b, final int n) throws RemoteException {
                if (biometricSourceType == BiometricSourceType.FACE) {
                    KeyguardUpdateMonitor.this.mFaceSettingEnabledForUser.put(n, b);
                    KeyguardUpdateMonitor.this.updateFaceListeningState();
                }
            }
        };
        this.mPhoneStateListener = new PhoneStateListener() {
            public void onActiveDataSubscriptionIdChanged(final int n) {
                KeyguardUpdateMonitor.this.mActiveMobileDataSubscription = n;
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(328);
            }
        };
        this.mSubscriptionListener = new SubscriptionManager$OnSubscriptionsChangedListener() {
            public void onSubscriptionsChanged() {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(328);
            }
        };
        this.mUserIsUnlocked = new SparseBooleanArray();
        this.mUserHasTrust = new SparseBooleanArray();
        this.mUserTrustIsManaged = new SparseBooleanArray();
        this.mUserTrustIsUsuallyManaged = new SparseBooleanArray();
        this.mUserFaceUnlockRunning = new SparseBooleanArray();
        this.mSecondaryLockscreenRequirement = new HashMap<Integer, Intent>();
        this.mUserFingerprintAuthenticated = (SparseArray<BiometricAuthenticated>)new SparseArray();
        this.mUserFaceAuthenticated = (SparseArray<BiometricAuthenticated>)new SparseArray();
        this.mUpdateBiometricListeningState = new _$$Lambda$KeyguardUpdateMonitor$w3Onnt26KGuFqBxQaSJgQd6Y_G4(this);
        this.mRetryFingerprintAuthentication = new Runnable() {
            @Override
            public void run() {
                final StringBuilder sb = new StringBuilder();
                sb.append("Retrying fingerprint after HW unavailable, attempt ");
                sb.append(KeyguardUpdateMonitor.this.mHardwareFingerprintUnavailableRetryCount);
                Log.w("KeyguardUpdateMonitor", sb.toString());
                KeyguardUpdateMonitor.this.updateFingerprintListeningState();
            }
        };
        this.mRetryFaceAuthentication = new Runnable() {
            @Override
            public void run() {
                final StringBuilder sb = new StringBuilder();
                sb.append("Retrying face after HW unavailable, attempt ");
                sb.append(KeyguardUpdateMonitor.this.mHardwareFaceUnavailableRetryCount);
                Log.w("KeyguardUpdateMonitor", sb.toString());
                KeyguardUpdateMonitor.this.updateFaceListeningState();
            }
        };
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if (!"android.intent.action.TIME_TICK".equals(action) && !"android.intent.action.TIME_SET".equals(action)) {
                    if ("android.intent.action.TIMEZONE_CHANGED".equals(action)) {
                        KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(339, (Object)intent.getStringExtra("time-zone")));
                    }
                    else if ("android.intent.action.BATTERY_CHANGED".equals(action)) {
                        KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(302, (Object)new BatteryStatus(intent)));
                    }
                    else if ("android.intent.action.SIM_STATE_CHANGED".equals(action)) {
                        final SimData fromIntent = SimData.fromIntent(intent);
                        if (intent.getBooleanExtra("rebroadcastOnUnlock", false)) {
                            if (fromIntent.simState == 1) {
                                KeyguardUpdateMonitor.this.mHandler.obtainMessage(338, (Object)Boolean.TRUE).sendToTarget();
                            }
                            return;
                        }
                        final StringBuilder sb = new StringBuilder();
                        sb.append("action ");
                        sb.append(action);
                        sb.append(" state: ");
                        sb.append(intent.getStringExtra("ss"));
                        sb.append(" slotId: ");
                        sb.append(fromIntent.slotId);
                        sb.append(" subid: ");
                        sb.append(fromIntent.subId);
                        Log.v("KeyguardUpdateMonitor", sb.toString());
                        KeyguardUpdateMonitor.this.mHandler.obtainMessage(304, fromIntent.subId, fromIntent.slotId, (Object)fromIntent.simState).sendToTarget();
                    }
                    else if ("android.media.RINGER_MODE_CHANGED".equals(action)) {
                        KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(305, intent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1), 0));
                    }
                    else if ("android.intent.action.PHONE_STATE".equals(action)) {
                        KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(306, (Object)intent.getStringExtra("state")));
                    }
                    else if ("android.intent.action.AIRPLANE_MODE".equals(action)) {
                        KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(329);
                    }
                    else if ("android.intent.action.SERVICE_STATE".equals(action)) {
                        KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(330, intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1), 0, (Object)ServiceState.newFromBundle(intent.getExtras())));
                    }
                    else if ("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED".equals(action)) {
                        KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(328);
                    }
                    else if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(action)) {
                        KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(337);
                    }
                }
                else {
                    KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(301);
                }
            }
        };
        this.mBroadcastAllReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if ("android.app.action.NEXT_ALARM_CLOCK_CHANGED".equals(action)) {
                    KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(301);
                }
                else if ("android.intent.action.USER_INFO_CHANGED".equals(action)) {
                    KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(317, intent.getIntExtra("android.intent.extra.user_handle", this.getSendingUserId()), 0));
                }
                else if ("com.android.facelock.FACE_UNLOCK_STARTED".equals(action)) {
                    Trace.beginSection("KeyguardUpdateMonitor.mBroadcastAllReceiver#onReceive ACTION_FACE_UNLOCK_STARTED");
                    KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(327, 1, this.getSendingUserId()));
                    Trace.endSection();
                }
                else if ("com.android.facelock.FACE_UNLOCK_STOPPED".equals(action)) {
                    KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(327, 0, this.getSendingUserId()));
                }
                else if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(action)) {
                    KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(309, (Object)this.getSendingUserId()));
                }
                else if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                    KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(334, this.getSendingUserId(), 0));
                }
                else if ("android.intent.action.USER_STOPPED".equals(action)) {
                    KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(340, intent.getIntExtra("android.intent.extra.user_handle", -1), 0));
                }
                else if ("android.intent.action.USER_REMOVED".equals(action)) {
                    KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(341, intent.getIntExtra("android.intent.extra.user_handle", -1), 0));
                }
            }
        };
        this.mFingerprintLockoutResetCallback = new FingerprintManager$LockoutResetCallback() {
            public void onLockoutReset() {
                KeyguardUpdateMonitor.this.handleFingerprintLockoutReset();
            }
        };
        this.mFaceLockoutResetCallback = new FaceManager$LockoutResetCallback() {
            public void onLockoutReset() {
                KeyguardUpdateMonitor.this.handleFaceLockoutReset();
            }
        };
        this.mFingerprintAuthenticationCallback = new FingerprintManager$AuthenticationCallback() {
            public void onAuthenticationAcquired(final int n) {
                KeyguardUpdateMonitor.this.handleFingerprintAcquired(n);
            }
            
            public void onAuthenticationError(final int n, final CharSequence charSequence) {
                KeyguardUpdateMonitor.this.handleFingerprintError(n, charSequence.toString());
            }
            
            public void onAuthenticationFailed() {
                KeyguardUpdateMonitor.this.handleFingerprintAuthFailed();
            }
            
            public void onAuthenticationHelp(final int n, final CharSequence charSequence) {
                KeyguardUpdateMonitor.this.handleFingerprintHelp(n, charSequence.toString());
            }
            
            public void onAuthenticationSucceeded(final FingerprintManager$AuthenticationResult fingerprintManager$AuthenticationResult) {
                Trace.beginSection("KeyguardUpdateMonitor#onAuthenticationSucceeded");
                KeyguardUpdateMonitor.this.handleFingerprintAuthenticated(fingerprintManager$AuthenticationResult.getUserId(), fingerprintManager$AuthenticationResult.isStrongBiometric());
                Trace.endSection();
            }
        };
        this.mFaceAuthenticationCallback = new FaceManager$AuthenticationCallback() {
            public void onAuthenticationAcquired(final int n) {
                KeyguardUpdateMonitor.this.handleFaceAcquired(n);
            }
            
            public void onAuthenticationError(final int n, final CharSequence charSequence) {
                KeyguardUpdateMonitor.this.handleFaceError(n, charSequence.toString());
            }
            
            public void onAuthenticationFailed() {
                KeyguardUpdateMonitor.this.handleFaceAuthFailed();
            }
            
            public void onAuthenticationHelp(final int n, final CharSequence charSequence) {
                KeyguardUpdateMonitor.this.handleFaceHelp(n, charSequence.toString());
            }
            
            public void onAuthenticationSucceeded(final FaceManager$AuthenticationResult faceManager$AuthenticationResult) {
                Trace.beginSection("KeyguardUpdateMonitor#onAuthenticationSucceeded");
                KeyguardUpdateMonitor.this.handleFaceAuthenticated(faceManager$AuthenticationResult.getUserId(), faceManager$AuthenticationResult.isStrongBiometric());
                Trace.endSection();
            }
        };
        this.mUserSwitchObserver = new UserSwitchObserver() {
            public void onUserSwitchComplete(final int n) throws RemoteException {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(314, n, 0));
            }
            
            public void onUserSwitching(final int n, final IRemoteCallback remoteCallback) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(310, n, 0, (Object)remoteCallback));
            }
        };
        this.mTaskStackListener = new TaskStackChangeListener() {
            @Override
            public void onTaskStackChangedBackground() {
                try {
                    final ActivityManager$StackInfo stackInfo = ActivityTaskManager.getService().getStackInfo(0, 4);
                    if (stackInfo == null) {
                        return;
                    }
                    KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(335, (Object)stackInfo.visible));
                }
                catch (RemoteException ex) {
                    Log.e("KeyguardUpdateMonitor", "unable to check task stack", (Throwable)ex);
                }
            }
        };
        this.mContext = mContext;
        this.mSubscriptionManager = SubscriptionManager.from(mContext);
        this.mDeviceProvisioned = this.isDeviceProvisionedInSettingsDb();
        this.mStrongAuthTracker = new StrongAuthTracker(mContext, new _$$Lambda$KeyguardUpdateMonitor$_GZaxeQabrHzh5b8rORPTQGQVD8(this));
        this.mBackgroundExecutor = mBackgroundExecutor;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        dumpManager.registerDumpable(KeyguardUpdateMonitor.class.getName(), this);
        this.mHandler = new Handler(looper) {
            public void handleMessage(final Message message) {
                switch (message.what) {
                    default: {
                        super.handleMessage(message);
                        break;
                    }
                    case 341: {
                        KeyguardUpdateMonitor.this.handleUserRemoved(message.arg1);
                        break;
                    }
                    case 340: {
                        KeyguardUpdateMonitor.this.handleUserStopped(message.arg1);
                        break;
                    }
                    case 339: {
                        KeyguardUpdateMonitor.this.handleTimeZoneUpdate((String)message.obj);
                        break;
                    }
                    case 338: {
                        KeyguardUpdateMonitor.this.updateTelephonyCapable((boolean)message.obj);
                        break;
                    }
                    case 337: {
                        KeyguardUpdateMonitor.this.updateLogoutEnabled();
                        break;
                    }
                    case 336: {
                        KeyguardUpdateMonitor.this.updateBiometricListeningState();
                        break;
                    }
                    case 335: {
                        KeyguardUpdateMonitor.this.setAssistantVisible((boolean)message.obj);
                        break;
                    }
                    case 334: {
                        KeyguardUpdateMonitor.this.handleUserUnlocked(message.arg1);
                        break;
                    }
                    case 333: {
                        KeyguardUpdateMonitor.this.handleDreamingStateChanged(message.arg1);
                        break;
                    }
                    case 332: {
                        Trace.beginSection("KeyguardUpdateMonitor#handler MSG_SCREEN_TURNED_ON");
                        KeyguardUpdateMonitor.this.handleScreenTurnedOff();
                        Trace.endSection();
                        break;
                    }
                    case 331: {
                        KeyguardUpdateMonitor.this.handleScreenTurnedOn();
                        break;
                    }
                    case 330: {
                        KeyguardUpdateMonitor.this.handleServiceStateChange(message.arg1, (ServiceState)message.obj);
                        break;
                    }
                    case 329: {
                        KeyguardUpdateMonitor.this.handleAirplaneModeChanged();
                        break;
                    }
                    case 328: {
                        KeyguardUpdateMonitor.this.handleSimSubscriptionInfoChanged();
                        break;
                    }
                    case 327: {
                        Trace.beginSection("KeyguardUpdateMonitor#handler MSG_FACE_UNLOCK_STATE_CHANGED");
                        KeyguardUpdateMonitor.this.handleFaceUnlockStateChanged(message.arg1 != 0, message.arg2);
                        Trace.endSection();
                        break;
                    }
                    case 322: {
                        KeyguardUpdateMonitor.this.handleKeyguardBouncerChanged(message.arg1);
                        break;
                    }
                    case 321: {
                        KeyguardUpdateMonitor.this.handleStartedGoingToSleep(message.arg1);
                        break;
                    }
                    case 320: {
                        KeyguardUpdateMonitor.this.handleFinishedGoingToSleep(message.arg1);
                        break;
                    }
                    case 319: {
                        Trace.beginSection("KeyguardUpdateMonitor#handler MSG_STARTED_WAKING_UP");
                        KeyguardUpdateMonitor.this.handleStartedWakingUp();
                        Trace.endSection();
                        break;
                    }
                    case 318: {
                        KeyguardUpdateMonitor.this.handleReportEmergencyCallAction();
                        break;
                    }
                    case 317: {
                        KeyguardUpdateMonitor.this.handleUserInfoChanged(message.arg1);
                        break;
                    }
                    case 314: {
                        KeyguardUpdateMonitor.this.handleUserSwitchComplete(message.arg1);
                        break;
                    }
                    case 312: {
                        KeyguardUpdateMonitor.this.handleKeyguardReset();
                        break;
                    }
                    case 310: {
                        KeyguardUpdateMonitor.this.handleUserSwitching(message.arg1, (IRemoteCallback)message.obj);
                        break;
                    }
                    case 309: {
                        KeyguardUpdateMonitor.this.handleDevicePolicyManagerStateChanged(message.arg1);
                        break;
                    }
                    case 308: {
                        KeyguardUpdateMonitor.this.handleDeviceProvisioned();
                        break;
                    }
                    case 306: {
                        KeyguardUpdateMonitor.this.handlePhoneStateChanged((String)message.obj);
                        break;
                    }
                    case 305: {
                        KeyguardUpdateMonitor.this.handleRingerModeChange(message.arg1);
                        break;
                    }
                    case 304: {
                        KeyguardUpdateMonitor.this.handleSimStateChange(message.arg1, message.arg2, (int)message.obj);
                        break;
                    }
                    case 302: {
                        KeyguardUpdateMonitor.this.handleBatteryUpdate((BatteryStatus)message.obj);
                        break;
                    }
                    case 301: {
                        KeyguardUpdateMonitor.this.handleTimeUpdate();
                        break;
                    }
                }
            }
        };
        if (!this.mDeviceProvisioned) {
            this.watchForDeviceProvisioning();
        }
        this.mBatteryStatus = new BatteryStatus(1, 100, 0, 0, 0);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_TICK");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.SERVICE_STATE");
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction("android.media.RINGER_MODE_CHANGED");
        intentFilter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this.mBroadcastReceiver, intentFilter, this.mHandler);
        final IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.USER_INFO_CHANGED");
        intentFilter2.addAction("android.app.action.NEXT_ALARM_CLOCK_CHANGED");
        intentFilter2.addAction("com.android.facelock.FACE_UNLOCK_STARTED");
        intentFilter2.addAction("com.android.facelock.FACE_UNLOCK_STOPPED");
        intentFilter2.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        intentFilter2.addAction("android.intent.action.USER_UNLOCKED");
        intentFilter2.addAction("android.intent.action.USER_STOPPED");
        intentFilter2.addAction("android.intent.action.USER_REMOVED");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this.mBroadcastAllReceiver, intentFilter2, this.mHandler, UserHandle.ALL);
        this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mSubscriptionListener);
        try {
            ActivityManager.getService().registerUserSwitchObserver((IUserSwitchObserver)this.mUserSwitchObserver, "KeyguardUpdateMonitor");
        }
        catch (RemoteException ex) {
            ex.rethrowAsRuntimeException();
        }
        (this.mTrustManager = (TrustManager)mContext.getSystemService((Class)TrustManager.class)).registerTrustListener((TrustManager$TrustListener)this);
        (this.mLockPatternUtils = new LockPatternUtils(mContext)).registerStrongAuthTracker((LockPatternUtils$StrongAuthTracker)this.mStrongAuthTracker);
        this.mDreamManager = IDreamManager$Stub.asInterface(ServiceManager.getService("dreams"));
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.fingerprint")) {
            this.mFpm = (FingerprintManager)mContext.getSystemService("fingerprint");
        }
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.biometrics.face")) {
            this.mFaceManager = (FaceManager)mContext.getSystemService("face");
        }
        if (this.mFpm != null || this.mFaceManager != null) {
            (this.mBiometricManager = (BiometricManager)mContext.getSystemService((Class)BiometricManager.class)).registerEnabledOnKeyguardCallback(this.mBiometricEnabledCallback);
        }
        this.updateBiometricListeningState();
        final FingerprintManager mFpm = this.mFpm;
        if (mFpm != null) {
            mFpm.addLockoutResetCallback(this.mFingerprintLockoutResetCallback);
        }
        final FaceManager mFaceManager = this.mFaceManager;
        if (mFaceManager != null) {
            mFaceManager.addLockoutResetCallback(this.mFaceLockoutResetCallback);
        }
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
        final UserManager mUserManager = (UserManager)mContext.getSystemService((Class)UserManager.class);
        this.mUserManager = mUserManager;
        this.mIsPrimaryUser = mUserManager.isPrimaryUser();
        final int currentUser = ActivityManager.getCurrentUser();
        this.mUserIsUnlocked.put(currentUser, this.mUserManager.isUserUnlocked(currentUser));
        final DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager)mContext.getSystemService((Class)DevicePolicyManager.class);
        this.mDevicePolicyManager = mDevicePolicyManager;
        this.mLogoutEnabled = mDevicePolicyManager.isLogoutEnabled();
        this.updateSecondaryLockscreenRequirement(currentUser);
        for (final UserInfo userInfo : this.mUserManager.getUsers()) {
            final SparseBooleanArray mUserTrustIsUsuallyManaged = this.mUserTrustIsUsuallyManaged;
            final int id = userInfo.id;
            mUserTrustIsUsuallyManaged.put(id, this.mTrustManager.isTrustUsuallyManaged(id));
        }
        this.updateAirplaneModeState();
        final TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService("phone");
        if (telephonyManager != null) {
            telephonyManager.listen(this.mPhoneStateListener, 4194304);
        }
    }
    
    private void callbacksRefreshCarrierInfo() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onRefreshCarrierInfo();
            }
        }
    }
    
    private boolean containsFlag(final int n, final int n2) {
        return (n & n2) != 0x0;
    }
    
    private void dispatchErrorMessage(final CharSequence charSequence) {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTrustAgentErrorMessage(charSequence);
            }
        }
    }
    
    public static int getCurrentUser() {
        synchronized (KeyguardUpdateMonitor.class) {
            return KeyguardUpdateMonitor.sCurrentUser;
        }
    }
    
    private int getSlotId(final int n) {
        if (!this.mSimDatas.containsKey(n)) {
            this.refreshSimState(n, SubscriptionManager.getSlotIndex(n));
        }
        return this.mSimDatas.get(n).slotId;
    }
    
    private void handleAirplaneModeChanged() {
        this.callbacksRefreshCarrierInfo();
    }
    
    private void handleBatteryUpdate(final BatteryStatus mBatteryStatus) {
        Assert.isMainThread();
        final boolean batteryUpdateInteresting = this.isBatteryUpdateInteresting(this.mBatteryStatus, mBatteryStatus);
        this.mBatteryStatus = mBatteryStatus;
        if (batteryUpdateInteresting) {
            for (int i = 0; i < this.mCallbacks.size(); ++i) {
                final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onRefreshBatteryInfo(mBatteryStatus);
                }
            }
        }
    }
    
    private void handleDevicePolicyManagerStateChanged(int i) {
        Assert.isMainThread();
        this.updateFingerprintListeningState();
        this.updateSecondaryLockscreenRequirement(i);
        KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback;
        for (i = 0; i < this.mCallbacks.size(); ++i) {
            keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onDevicePolicyManagerStateChanged();
            }
        }
    }
    
    private void handleDeviceProvisioned() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onDeviceProvisioned();
            }
        }
        if (this.mDeviceProvisionedObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mDeviceProvisionedObserver);
            this.mDeviceProvisionedObserver = null;
        }
    }
    
    private void handleDreamingStateChanged(int i) {
        Assert.isMainThread();
        final int n = 0;
        boolean mIsDreaming = true;
        if (i != 1) {
            mIsDreaming = false;
        }
        this.mIsDreaming = mIsDreaming;
        KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback;
        for (i = n; i < this.mCallbacks.size(); ++i) {
            keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onDreamingStateChanged(this.mIsDreaming);
            }
        }
        this.updateBiometricListeningState();
    }
    
    private void handleFaceAcquired(int i) {
        Assert.isMainThread();
        if (i != 0) {
            return;
        }
        Log.d("KeyguardUpdateMonitor", "Face acquired");
        KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback;
        for (i = 0; i < this.mCallbacks.size(); ++i) {
            keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricAcquired(BiometricSourceType.FACE);
            }
        }
    }
    
    private void handleFaceAuthFailed() {
        Assert.isMainThread();
        int i = 0;
        this.setFaceRunningState(0);
        while (i < this.mCallbacks.size()) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricAuthFailed(BiometricSourceType.FACE);
            }
            ++i;
        }
        this.handleFaceHelp(-2, this.mContext.getString(R$string.kg_face_not_recognized));
    }
    
    private void handleFaceAuthenticated(final int i, final boolean b) {
        Trace.beginSection("KeyGuardUpdateMonitor#handlerFaceAuthenticated");
        try {
            if (this.mGoingToSleep) {
                Log.d("KeyguardUpdateMonitor", "Aborted successful auth because device is going to sleep.");
                return;
            }
            try {
                final int id = ActivityManager.getService().getCurrentUser().id;
                if (id != i) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Face authenticated for wrong user: ");
                    sb.append(i);
                    Log.d("KeyguardUpdateMonitor", sb.toString());
                    return;
                }
                if (this.isFaceDisabled(id)) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Face authentication disabled by DPM for userId: ");
                    sb2.append(id);
                    Log.d("KeyguardUpdateMonitor", sb2.toString());
                    return;
                }
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("Face auth succeeded for user ");
                sb3.append(id);
                Log.d("KeyguardUpdateMonitor", sb3.toString());
                this.onFaceAuthenticated(id, b);
                Trace.endSection();
            }
            catch (RemoteException ex) {
                Log.e("KeyguardUpdateMonitor", "Failed to get current user id: ", (Throwable)ex);
            }
        }
        finally {
            this.setFaceRunningState(0);
        }
    }
    
    private void handleFaceError(final int n, final String str) {
        Assert.isMainThread();
        final StringBuilder sb = new StringBuilder();
        sb.append("Face error received: ");
        sb.append(str);
        Log.d("KeyguardUpdateMonitor", sb.toString());
        if (n == 5 && this.mHandler.hasCallbacks(this.mCancelNotReceived)) {
            this.mHandler.removeCallbacks(this.mCancelNotReceived);
        }
        final int n2 = 0;
        if (n == 5 && this.mFaceRunningState == 3) {
            this.setFaceRunningState(0);
            this.updateFaceListeningState();
        }
        else {
            this.setFaceRunningState(0);
        }
        if (n == 1 || n == 2) {
            final int mHardwareFaceUnavailableRetryCount = this.mHardwareFaceUnavailableRetryCount;
            if (mHardwareFaceUnavailableRetryCount < 10) {
                this.mHardwareFaceUnavailableRetryCount = mHardwareFaceUnavailableRetryCount + 1;
                this.mHandler.removeCallbacks(this.mRetryFaceAuthentication);
                this.mHandler.postDelayed(this.mRetryFaceAuthentication, 500L);
            }
        }
        int i = n2;
        if (n == 9) {
            this.mLockPatternUtils.requireStrongAuth(8, getCurrentUser());
            i = n2;
        }
        while (i < this.mCallbacks.size()) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricError(n, str, BiometricSourceType.FACE);
            }
            ++i;
        }
    }
    
    private void handleFaceHelp(final int n, final String str) {
        Assert.isMainThread();
        final StringBuilder sb = new StringBuilder();
        sb.append("Face help received: ");
        sb.append(str);
        Log.d("KeyguardUpdateMonitor", sb.toString());
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricHelp(n, str, BiometricSourceType.FACE);
            }
        }
    }
    
    private void handleFaceLockoutReset() {
        this.updateFaceListeningState();
    }
    
    private void handleFaceUnlockStateChanged(final boolean b, final int n) {
        Assert.isMainThread();
        this.mUserFaceUnlockRunning.put(n, b);
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onFaceUnlockStateChanged(b, n);
            }
        }
    }
    
    private void handleFingerprintAcquired(int i) {
        Assert.isMainThread();
        if (i != 0) {
            return;
        }
        KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback;
        for (i = 0; i < this.mCallbacks.size(); ++i) {
            keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricAcquired(BiometricSourceType.FINGERPRINT);
            }
        }
    }
    
    private void handleFingerprintAuthFailed() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricAuthFailed(BiometricSourceType.FINGERPRINT);
            }
        }
        this.handleFingerprintHelp(-1, this.mContext.getString(R$string.kg_fingerprint_not_recognized));
    }
    
    private void handleFingerprintAuthenticated(final int i, final boolean b) {
        Trace.beginSection("KeyGuardUpdateMonitor#handlerFingerPrintAuthenticated");
        try {
            try {
                final int id = ActivityManager.getService().getCurrentUser().id;
                if (id != i) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Fingerprint authenticated for wrong user: ");
                    sb.append(i);
                    Log.d("KeyguardUpdateMonitor", sb.toString());
                    this.setFingerprintRunningState(0);
                    return;
                }
                if (this.isFingerprintDisabled(id)) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Fingerprint disabled by DPM for userId: ");
                    sb2.append(id);
                    Log.d("KeyguardUpdateMonitor", sb2.toString());
                    this.setFingerprintRunningState(0);
                    return;
                }
                this.onFingerprintAuthenticated(id, b);
                this.setFingerprintRunningState(0);
                Trace.endSection();
                return;
            }
            finally {}
        }
        catch (RemoteException ex) {
            Log.e("KeyguardUpdateMonitor", "Failed to get current user id: ", (Throwable)ex);
            this.setFingerprintRunningState(0);
            return;
        }
        this.setFingerprintRunningState(0);
    }
    
    private void handleFingerprintError(final int n, final String s) {
        Assert.isMainThread();
        if (n == 5 && this.mHandler.hasCallbacks(this.mCancelNotReceived)) {
            this.mHandler.removeCallbacks(this.mCancelNotReceived);
        }
        final int n2 = 0;
        if (n == 5 && this.mFingerprintRunningState == 3) {
            this.setFingerprintRunningState(0);
            this.updateFingerprintListeningState();
        }
        else {
            this.setFingerprintRunningState(0);
            this.mFingerprintCancelSignal = null;
            this.mFaceCancelSignal = null;
        }
        if (n == 1) {
            final int mHardwareFingerprintUnavailableRetryCount = this.mHardwareFingerprintUnavailableRetryCount;
            if (mHardwareFingerprintUnavailableRetryCount < 10) {
                this.mHardwareFingerprintUnavailableRetryCount = mHardwareFingerprintUnavailableRetryCount + 1;
                this.mHandler.removeCallbacks(this.mRetryFingerprintAuthentication);
                this.mHandler.postDelayed(this.mRetryFingerprintAuthentication, 500L);
            }
        }
        if (n == 9) {
            this.mLockPatternUtils.requireStrongAuth(8, getCurrentUser());
        }
        while (true) {
            Label_0160: {
                if (n == 7) {
                    break Label_0160;
                }
                int i = n2;
                if (n == 9) {
                    break Label_0160;
                }
                while (i < this.mCallbacks.size()) {
                    final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
                    if (keyguardUpdateMonitorCallback != null) {
                        keyguardUpdateMonitorCallback.onBiometricError(n, s, BiometricSourceType.FINGERPRINT);
                    }
                    ++i;
                }
                return;
            }
            this.mFingerprintLockedOut = true;
            int i = n2;
            continue;
        }
    }
    
    private void handleFingerprintHelp(final int n, final String s) {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricHelp(n, s, BiometricSourceType.FINGERPRINT);
            }
        }
    }
    
    private void handleFingerprintLockoutReset() {
        this.mFingerprintLockedOut = false;
        this.updateFingerprintListeningState();
    }
    
    private void handleKeyguardBouncerChanged(int i) {
        Assert.isMainThread();
        boolean mBouncer = true;
        final int n = 0;
        if (i != 1) {
            mBouncer = false;
        }
        this.mBouncer = mBouncer;
        if (mBouncer) {
            this.mSecureCameraLaunched = false;
            i = n;
        }
        else {
            this.mCredentialAttempted = false;
            i = n;
        }
        while (i < this.mCallbacks.size()) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onKeyguardBouncerChanged(mBouncer);
            }
            ++i;
        }
        this.updateBiometricListeningState();
    }
    
    private void handleKeyguardReset() {
        this.updateBiometricListeningState();
        this.mNeedsSlowUnlockTransition = this.resolveNeedsSlowUnlockTransition();
    }
    
    private void handlePhoneStateChanged(final String anObject) {
        Assert.isMainThread();
        final boolean equals = TelephonyManager.EXTRA_STATE_IDLE.equals(anObject);
        final int n = 0;
        int i;
        if (equals) {
            this.mPhoneState = 0;
            i = n;
        }
        else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(anObject)) {
            this.mPhoneState = 2;
            i = n;
        }
        else {
            i = n;
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(anObject)) {
                this.mPhoneState = 1;
                i = n;
            }
        }
        while (i < this.mCallbacks.size()) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onPhoneStateChanged(this.mPhoneState);
            }
            ++i;
        }
    }
    
    private void handleReportEmergencyCallAction() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onEmergencyCallAction();
            }
        }
    }
    
    private void handleRingerModeChange(final int mRingMode) {
        Assert.isMainThread();
        this.mRingMode = mRingMode;
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onRingerModeChanged(mRingMode);
            }
        }
    }
    
    private void handleScreenTurnedOff() {
        DejankUtils.startDetectingBlockingIpcs("KeyguardUpdateMonitor#handleScreenTurnedOff");
        Assert.isMainThread();
        int i = 0;
        this.mHardwareFingerprintUnavailableRetryCount = 0;
        this.mHardwareFaceUnavailableRetryCount = 0;
        while (i < this.mCallbacks.size()) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onScreenTurnedOff();
            }
            ++i;
        }
        DejankUtils.stopDetectingBlockingIpcs("KeyguardUpdateMonitor#handleScreenTurnedOff");
    }
    
    private void handleScreenTurnedOn() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onScreenTurnedOn();
            }
        }
    }
    
    private void handleSimSubscriptionInfoChanged() {
        Assert.isMainThread();
        Log.v("KeyguardUpdateMonitor", "onSubscriptionInfoChanged()");
        final List completeActiveSubscriptionInfoList = this.mSubscriptionManager.getCompleteActiveSubscriptionInfoList();
        if (completeActiveSubscriptionInfoList != null) {
            for (final SubscriptionInfo obj : completeActiveSubscriptionInfoList) {
                final StringBuilder sb = new StringBuilder();
                sb.append("SubInfo:");
                sb.append(obj);
                Log.v("KeyguardUpdateMonitor", sb.toString());
            }
        }
        else {
            Log.v("KeyguardUpdateMonitor", "onSubscriptionInfoChanged: list is null");
        }
        final List<SubscriptionInfo> subscriptionInfo = this.getSubscriptionInfo(true);
        final ArrayList<SubscriptionInfo> list = new ArrayList<SubscriptionInfo>();
        for (int i = 0; i < subscriptionInfo.size(); ++i) {
            final SubscriptionInfo e = subscriptionInfo.get(i);
            if (this.refreshSimState(e.getSubscriptionId(), e.getSimSlotIndex())) {
                list.add(e);
            }
        }
        for (int j = 0; j < list.size(); ++j) {
            final SimData simData = this.mSimDatas.get(list.get(j).getSubscriptionId());
            for (int k = 0; k < this.mCallbacks.size(); ++k) {
                final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(k).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onSimStateChanged(simData.subId, simData.slotId, simData.simState);
                }
            }
        }
        this.callbacksRefreshCarrierInfo();
    }
    
    private void handleTimeUpdate() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTimeChanged();
            }
        }
    }
    
    private void handleTimeZoneUpdate(final String id) {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTimeZoneChanged(TimeZone.getTimeZone(id));
                keyguardUpdateMonitorCallback.onTimeChanged();
            }
        }
    }
    
    private void handleUserInfoChanged(final int n) {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onUserInfoChanged(n);
            }
        }
    }
    
    private void handleUserStopped(final int n) {
        Assert.isMainThread();
        this.mUserIsUnlocked.put(n, this.mUserManager.isUserUnlocked(n));
    }
    
    private void handleUserSwitchComplete(final int n) {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onUserSwitchComplete(n);
            }
        }
    }
    
    private void handleUserUnlocked(int i) {
        Assert.isMainThread();
        this.mUserIsUnlocked.put(i, true);
        this.mNeedsSlowUnlockTransition = this.resolveNeedsSlowUnlockTransition();
        KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback;
        for (i = 0; i < this.mCallbacks.size(); ++i) {
            keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onUserUnlocked();
            }
        }
    }
    
    private boolean isBatteryUpdateInteresting(final BatteryStatus batteryStatus, final BatteryStatus batteryStatus2) {
        final boolean pluggedIn = batteryStatus2.isPluggedIn();
        final boolean pluggedIn2 = batteryStatus.isPluggedIn();
        final boolean b = pluggedIn2 && pluggedIn && batteryStatus.status != batteryStatus2.status;
        return pluggedIn2 != pluggedIn || b || batteryStatus.level != batteryStatus2.level || (pluggedIn && batteryStatus2.maxChargingWattage != batteryStatus.maxChargingWattage);
    }
    
    private boolean isDeviceProvisionedInSettingsDb() {
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean b = false;
        if (Settings$Global.getInt(contentResolver, "device_provisioned", 0) != 0) {
            b = true;
        }
        return b;
    }
    
    private boolean isFaceDisabled(final int n) {
        return DejankUtils.whitelistIpcs((Supplier<Boolean>)new _$$Lambda$KeyguardUpdateMonitor$N2Cyv6mYvgookTnpPTeaGdzNtxk(this, (DevicePolicyManager)this.mContext.getSystemService("device_policy"), n));
    }
    
    private boolean isFingerprintDisabled(final int n) {
        final DevicePolicyManager devicePolicyManager = (DevicePolicyManager)this.mContext.getSystemService("device_policy");
        return (devicePolicyManager != null && (devicePolicyManager.getKeyguardDisabledFeatures((ComponentName)null, n) & 0x20) != 0x0) || this.isSimPinSecure();
    }
    
    public static boolean isSimPinSecure(final int n) {
        return n == 2 || n == 3 || n == 7;
    }
    
    private boolean isTrustDisabled(final int n) {
        return this.isSimPinSecure();
    }
    
    private boolean isUnlockWithFacePossible(final int n) {
        return this.isFaceAuthEnabledForUser(n) && !this.isFaceDisabled(n);
    }
    
    private boolean isUnlockWithFingerprintPossible(final int n) {
        final FingerprintManager mFpm = this.mFpm;
        return mFpm != null && mFpm.isHardwareDetected() && !this.isFingerprintDisabled(n) && this.mFpm.getEnrolledFingerprints(n).size() > 0;
    }
    
    private void notifyFaceRunningStateChanged() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricRunningStateChanged(this.isFaceDetectionRunning(), BiometricSourceType.FACE);
            }
        }
    }
    
    private void notifyFingerprintRunningStateChanged() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricRunningStateChanged(this.isFingerprintDetectionRunning(), BiometricSourceType.FINGERPRINT);
            }
        }
    }
    
    private void notifyStrongAuthStateChanged(final int n) {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onStrongAuthStateChanged(n);
            }
        }
    }
    
    private boolean refreshSimState(final int n, final int n2) {
        final TelephonyManager telephonyManager = (TelephonyManager)this.mContext.getSystemService("phone");
        final boolean b = false;
        int simState;
        if (telephonyManager != null) {
            simState = telephonyManager.getSimState(n2);
        }
        else {
            simState = 0;
        }
        final SimData simData = this.mSimDatas.get(n);
        boolean b2 = true;
        if (simData == null) {
            this.mSimDatas.put(n, new SimData(simState, n2, n));
        }
        else {
            b2 = b;
            if (simData.simState != simState) {
                b2 = true;
            }
            simData.simState = simState;
        }
        return b2;
    }
    
    private void reportSuccessfulBiometricUnlock(final boolean b, final int n) {
        this.mBackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                KeyguardUpdateMonitor.this.mLockPatternUtils.reportSuccessfulBiometricUnlock(b, n);
            }
        });
    }
    
    private boolean resolveNeedsSlowUnlockTransition() {
        return !this.isUserUnlocked(getCurrentUser()) && KeyguardUpdateMonitor.FALLBACK_HOME_COMPONENT.equals((Object)this.mContext.getPackageManager().resolveActivity(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME"), 0).getComponentInfo().getComponentName());
    }
    
    private void sendUpdates(final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback) {
        keyguardUpdateMonitorCallback.onRefreshBatteryInfo(this.mBatteryStatus);
        keyguardUpdateMonitorCallback.onTimeChanged();
        keyguardUpdateMonitorCallback.onRingerModeChanged(this.mRingMode);
        keyguardUpdateMonitorCallback.onPhoneStateChanged(this.mPhoneState);
        keyguardUpdateMonitorCallback.onRefreshCarrierInfo();
        keyguardUpdateMonitorCallback.onClockVisibilityChanged();
        keyguardUpdateMonitorCallback.onKeyguardVisibilityChangedRaw(this.mKeyguardIsVisible);
        keyguardUpdateMonitorCallback.onTelephonyCapable(this.mTelephonyCapable);
        final Iterator<Map.Entry<Integer, SimData>> iterator = this.mSimDatas.entrySet().iterator();
        while (iterator.hasNext()) {
            final SimData simData = iterator.next().getValue();
            keyguardUpdateMonitorCallback.onSimStateChanged(simData.subId, simData.slotId, simData.simState);
        }
    }
    
    public static void setCurrentUser(final int sCurrentUser) {
        synchronized (KeyguardUpdateMonitor.class) {
            KeyguardUpdateMonitor.sCurrentUser = sCurrentUser;
        }
    }
    
    private void setFaceRunningState(final int mFaceRunningState) {
        final int mFaceRunningState2 = this.mFaceRunningState;
        int n = false ? 1 : 0;
        final boolean b = mFaceRunningState2 == 1;
        if (mFaceRunningState == 1) {
            n = (true ? 1 : 0);
        }
        this.mFaceRunningState = mFaceRunningState;
        final StringBuilder sb = new StringBuilder();
        sb.append("faceRunningState: ");
        sb.append(this.mFaceRunningState);
        Log.d("KeyguardUpdateMonitor", sb.toString());
        if ((b ? 1 : 0) != n) {
            this.notifyFaceRunningStateChanged();
        }
    }
    
    private void setFingerprintRunningState(final int mFingerprintRunningState) {
        final int mFingerprintRunningState2 = this.mFingerprintRunningState;
        int n = false ? 1 : 0;
        final boolean b = mFingerprintRunningState2 == 1;
        if (mFingerprintRunningState == 1) {
            n = (true ? 1 : 0);
        }
        this.mFingerprintRunningState = mFingerprintRunningState;
        final StringBuilder sb = new StringBuilder();
        sb.append("fingerprintRunningState: ");
        sb.append(this.mFingerprintRunningState);
        Log.d("KeyguardUpdateMonitor", sb.toString());
        if ((b ? 1 : 0) != n) {
            this.notifyFingerprintRunningStateChanged();
        }
    }
    
    private boolean shouldListenForFaceAssistant() {
        final BiometricAuthenticated biometricAuthenticated = (BiometricAuthenticated)this.mUserFaceAuthenticated.get(getCurrentUser());
        final boolean mAssistantVisible = this.mAssistantVisible;
        boolean b2;
        final boolean b = b2 = false;
        if (mAssistantVisible) {
            b2 = b;
            if (this.mKeyguardOccluded) {
                if (biometricAuthenticated != null) {
                    b2 = b;
                    if (biometricAuthenticated.mAuthenticated) {
                        return b2;
                    }
                }
                b2 = b;
                if (!this.mUserHasTrust.get(getCurrentUser(), false)) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    private boolean shouldListenForFingerprint() {
        final boolean mFingerprintLockedOut = this.mFingerprintLockedOut;
        final boolean b = false;
        final boolean b2 = !mFingerprintLockedOut || !this.mBouncer || !this.mCredentialAttempted;
        if (!this.mKeyguardIsVisible && this.mDeviceInteractive && (!this.mBouncer || this.mKeyguardGoingAway) && !this.mGoingToSleep && !this.shouldListenForFingerprintAssistant()) {
            boolean b3 = b;
            if (!this.mKeyguardOccluded) {
                return b3;
            }
            b3 = b;
            if (!this.mIsDreaming) {
                return b3;
            }
        }
        boolean b3 = b;
        if (!this.mSwitchingUser) {
            b3 = b;
            if (!this.isFingerprintDisabled(getCurrentUser())) {
                if (this.mKeyguardGoingAway) {
                    b3 = b;
                    if (this.mDeviceInteractive) {
                        return b3;
                    }
                }
                b3 = b;
                if (this.mIsPrimaryUser) {
                    b3 = b;
                    if (b2) {
                        b3 = true;
                    }
                }
            }
        }
        return b3;
    }
    
    private boolean shouldListenForFingerprintAssistant() {
        final BiometricAuthenticated biometricAuthenticated = (BiometricAuthenticated)this.mUserFingerprintAuthenticated.get(getCurrentUser());
        final boolean mAssistantVisible = this.mAssistantVisible;
        boolean b2;
        final boolean b = b2 = false;
        if (mAssistantVisible) {
            b2 = b;
            if (this.mKeyguardOccluded) {
                if (biometricAuthenticated != null) {
                    b2 = b;
                    if (biometricAuthenticated.mAuthenticated) {
                        return b2;
                    }
                }
                b2 = b;
                if (!this.mUserHasTrust.get(getCurrentUser(), false)) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    private void startListeningForFace() {
        if (this.mFaceRunningState == 2) {
            this.setFaceRunningState(3);
            return;
        }
        final int currentUser = getCurrentUser();
        if (this.isUnlockWithFacePossible(currentUser)) {
            final CancellationSignal mFaceCancelSignal = this.mFaceCancelSignal;
            if (mFaceCancelSignal != null) {
                mFaceCancelSignal.cancel();
            }
            final CancellationSignal mFaceCancelSignal2 = new CancellationSignal();
            this.mFaceCancelSignal = mFaceCancelSignal2;
            this.mFaceManager.authenticate((CryptoObject)null, mFaceCancelSignal2, 0, this.mFaceAuthenticationCallback, (Handler)null, currentUser);
            this.setFaceRunningState(1);
        }
    }
    
    private void startListeningForFingerprint() {
        final int mFingerprintRunningState = this.mFingerprintRunningState;
        if (mFingerprintRunningState == 2) {
            this.setFingerprintRunningState(3);
            return;
        }
        if (mFingerprintRunningState == 3) {
            return;
        }
        final int currentUser = getCurrentUser();
        if (this.isUnlockWithFingerprintPossible(currentUser)) {
            final CancellationSignal mFingerprintCancelSignal = this.mFingerprintCancelSignal;
            if (mFingerprintCancelSignal != null) {
                mFingerprintCancelSignal.cancel();
            }
            final CancellationSignal mFingerprintCancelSignal2 = new CancellationSignal();
            this.mFingerprintCancelSignal = mFingerprintCancelSignal2;
            this.mFpm.authenticate((FingerprintManager$CryptoObject)null, mFingerprintCancelSignal2, 0, this.mFingerprintAuthenticationCallback, (Handler)null, currentUser);
            this.setFingerprintRunningState(1);
        }
    }
    
    private void stopListeningForFace() {
        if (this.mFaceRunningState == 1) {
            final CancellationSignal mFaceCancelSignal = this.mFaceCancelSignal;
            if (mFaceCancelSignal != null) {
                mFaceCancelSignal.cancel();
                this.mFaceCancelSignal = null;
                if (!this.mHandler.hasCallbacks(this.mCancelNotReceived)) {
                    this.mHandler.postDelayed(this.mCancelNotReceived, 3000L);
                }
            }
            this.setFaceRunningState(2);
        }
        if (this.mFaceRunningState == 3) {
            this.setFaceRunningState(2);
        }
    }
    
    private void stopListeningForFingerprint() {
        if (this.mFingerprintRunningState == 1) {
            final CancellationSignal mFingerprintCancelSignal = this.mFingerprintCancelSignal;
            if (mFingerprintCancelSignal != null) {
                mFingerprintCancelSignal.cancel();
                this.mFingerprintCancelSignal = null;
                if (!this.mHandler.hasCallbacks(this.mCancelNotReceived)) {
                    this.mHandler.postDelayed(this.mCancelNotReceived, 3000L);
                }
            }
            this.setFingerprintRunningState(2);
        }
        if (this.mFingerprintRunningState == 3) {
            this.setFingerprintRunningState(2);
        }
    }
    
    private void updateAirplaneModeState() {
        if (WirelessUtils.isAirplaneModeOn(this.mContext)) {
            if (!this.mHandler.hasMessages(329)) {
                this.mHandler.sendEmptyMessage(329);
            }
        }
    }
    
    private void updateBiometricListeningState() {
        this.updateFingerprintListeningState();
        this.updateFaceListeningState();
    }
    
    private void updateFaceListeningState() {
        if (this.mHandler.hasMessages(336)) {
            return;
        }
        this.mHandler.removeCallbacks(this.mRetryFaceAuthentication);
        final boolean shouldListenForFace = this.shouldListenForFace();
        if (this.mFaceRunningState == 1 && !shouldListenForFace) {
            this.stopListeningForFace();
        }
        else if (this.mFaceRunningState != 1 && shouldListenForFace) {
            this.startListeningForFace();
        }
    }
    
    private void updateFingerprintListeningState() {
        if (this.mHandler.hasMessages(336)) {
            return;
        }
        this.mHandler.removeCallbacks(this.mRetryFingerprintAuthentication);
        final boolean shouldListenForFingerprint = this.shouldListenForFingerprint();
        final int mFingerprintRunningState = this.mFingerprintRunningState;
        int n2;
        final int n = n2 = 1;
        if (mFingerprintRunningState != 1) {
            if (mFingerprintRunningState == 3) {
                n2 = n;
            }
            else {
                n2 = 0;
            }
        }
        if (n2 != 0 && !shouldListenForFingerprint) {
            this.stopListeningForFingerprint();
        }
        else if (n2 == 0 && shouldListenForFingerprint) {
            this.startListeningForFingerprint();
        }
    }
    
    private void updateLogoutEnabled() {
        Assert.isMainThread();
        final boolean logoutEnabled = this.mDevicePolicyManager.isLogoutEnabled();
        if (this.mLogoutEnabled != logoutEnabled) {
            this.mLogoutEnabled = logoutEnabled;
            for (int i = 0; i < this.mCallbacks.size(); ++i) {
                final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onLogoutEnabledChanged();
                }
            }
        }
    }
    
    private void updateSecondaryLockscreenRequirement(final int n) {
        final Intent intent = this.mSecondaryLockscreenRequirement.get(n);
        final boolean secondaryLockscreenEnabled = this.mDevicePolicyManager.isSecondaryLockscreenEnabled(UserHandle.of(n));
        boolean b = true;
        final int n2 = 0;
        Label_0202: {
            if (secondaryLockscreenEnabled && intent == null) {
                final ComponentName profileOwnerOrDeviceOwnerSupervisionComponent = this.mDevicePolicyManager.getProfileOwnerOrDeviceOwnerSupervisionComponent(UserHandle.of(n));
                if (profileOwnerOrDeviceOwnerSupervisionComponent == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("No Profile Owner or Device Owner supervision app found for User ");
                    sb.append(n);
                    Log.e("KeyguardUpdateMonitor", sb.toString());
                }
                else {
                    final ResolveInfo resolveService = this.mContext.getPackageManager().resolveService(new Intent("android.app.action.BIND_SECONDARY_LOCKSCREEN_SERVICE").setPackage(profileOwnerOrDeviceOwnerSupervisionComponent.getPackageName()), 0);
                    if (resolveService != null && resolveService.serviceInfo != null) {
                        this.mSecondaryLockscreenRequirement.put(n, new Intent().setComponent(resolveService.serviceInfo.getComponentName()));
                        break Label_0202;
                    }
                }
            }
            else if (!secondaryLockscreenEnabled && intent != null) {
                this.mSecondaryLockscreenRequirement.put(n, null);
                break Label_0202;
            }
            b = false;
        }
        if (b) {
            for (int i = n2; i < this.mCallbacks.size(); ++i) {
                final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onSecondaryLockscreenRequirementChanged(n);
                }
            }
        }
    }
    
    private void watchForDeviceProvisioning() {
        this.mDeviceProvisionedObserver = new ContentObserver(this.mHandler) {
            public void onChange(final boolean b) {
                super.onChange(b);
                final KeyguardUpdateMonitor this$0 = KeyguardUpdateMonitor.this;
                this$0.mDeviceProvisioned = this$0.isDeviceProvisionedInSettingsDb();
                if (KeyguardUpdateMonitor.this.mDeviceProvisioned) {
                    KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(308);
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings$Global.getUriFor("device_provisioned"), false, this.mDeviceProvisionedObserver);
        final boolean deviceProvisionedInSettingsDb = this.isDeviceProvisionedInSettingsDb();
        if (deviceProvisionedInSettingsDb != this.mDeviceProvisioned && (this.mDeviceProvisioned = deviceProvisionedInSettingsDb)) {
            this.mHandler.sendEmptyMessage(308);
        }
    }
    
    public void awakenFromDream() {
        if (this.mIsDreaming) {
            final IDreamManager mDreamManager = this.mDreamManager;
            if (mDreamManager != null) {
                try {
                    mDreamManager.awaken();
                }
                catch (RemoteException ex) {
                    Log.e("KeyguardUpdateMonitor", "Unable to awaken from dream");
                }
            }
        }
    }
    
    public void cancelFaceAuth() {
        this.stopListeningForFace();
    }
    
    public void clearBiometricRecognized() {
        Assert.isMainThread();
        this.mUserFingerprintAuthenticated.clear();
        this.mUserFaceAuthenticated.clear();
        this.mTrustManager.clearAllBiometricRecognized(BiometricSourceType.FINGERPRINT);
        this.mTrustManager.clearAllBiometricRecognized(BiometricSourceType.FACE);
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricsCleared();
            }
        }
    }
    
    public void dispatchDreamingStarted() {
        final Handler mHandler = this.mHandler;
        mHandler.sendMessage(mHandler.obtainMessage(333, 1, 0));
    }
    
    public void dispatchDreamingStopped() {
        final Handler mHandler = this.mHandler;
        mHandler.sendMessage(mHandler.obtainMessage(333, 0, 0));
    }
    
    public void dispatchFinishedGoingToSleep(final int n) {
        synchronized (this) {
            this.mDeviceInteractive = false;
            // monitorexit(this)
            final Handler mHandler = this.mHandler;
            mHandler.sendMessage(mHandler.obtainMessage(320, n, 0));
        }
    }
    
    public void dispatchScreenTurnedOff() {
        synchronized (this) {
            this.mScreenOn = false;
            // monitorexit(this)
            this.mHandler.sendEmptyMessage(332);
        }
    }
    
    public void dispatchScreenTurnedOn() {
        synchronized (this) {
            this.mScreenOn = true;
            // monitorexit(this)
            this.mHandler.sendEmptyMessage(331);
        }
    }
    
    public void dispatchStartedGoingToSleep(final int n) {
        final Handler mHandler = this.mHandler;
        mHandler.sendMessage(mHandler.obtainMessage(321, n, 0));
    }
    
    public void dispatchStartedWakingUp() {
        synchronized (this) {
            this.mDeviceInteractive = true;
            // monitorexit(this)
            this.mHandler.sendEmptyMessage(319);
        }
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("KeyguardUpdateMonitor state:");
        printWriter.println("  SIM States:");
        for (final SimData simData : this.mSimDatas.values()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("    ");
            sb.append(simData.toString());
            printWriter.println(sb.toString());
        }
        printWriter.println("  Subs:");
        if (this.mSubscriptionInfo != null) {
            for (int i = 0; i < this.mSubscriptionInfo.size(); ++i) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("    ");
                sb2.append(this.mSubscriptionInfo.get(i));
                printWriter.println(sb2.toString());
            }
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  Current active data subId=");
        sb3.append(this.mActiveMobileDataSubscription);
        printWriter.println(sb3.toString());
        printWriter.println("  Service states:");
        for (final int intValue : this.mServiceStates.keySet()) {
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("    ");
            sb4.append(intValue);
            sb4.append("=");
            sb4.append(this.mServiceStates.get(intValue));
            printWriter.println(sb4.toString());
        }
        final FingerprintManager mFpm = this.mFpm;
        final boolean b = true;
        if (mFpm != null && mFpm.isHardwareDetected()) {
            final int currentUser = ActivityManager.getCurrentUser();
            final int strongAuthForUser = this.mStrongAuthTracker.getStrongAuthForUser(currentUser);
            final BiometricAuthenticated biometricAuthenticated = (BiometricAuthenticated)this.mUserFingerprintAuthenticated.get(currentUser);
            final StringBuilder sb5 = new StringBuilder();
            sb5.append("  Fingerprint state (user=");
            sb5.append(currentUser);
            sb5.append(")");
            printWriter.println(sb5.toString());
            final StringBuilder sb6 = new StringBuilder();
            sb6.append("    allowed=");
            sb6.append(biometricAuthenticated != null && this.isUnlockingWithBiometricAllowed(biometricAuthenticated.mIsStrongBiometric));
            printWriter.println(sb6.toString());
            final StringBuilder sb7 = new StringBuilder();
            sb7.append("    auth'd=");
            sb7.append(biometricAuthenticated != null && biometricAuthenticated.mAuthenticated);
            printWriter.println(sb7.toString());
            final StringBuilder sb8 = new StringBuilder();
            sb8.append("    authSinceBoot=");
            sb8.append(this.getStrongAuthTracker().hasUserAuthenticatedSinceBoot());
            printWriter.println(sb8.toString());
            final StringBuilder sb9 = new StringBuilder();
            sb9.append("    disabled(DPM)=");
            sb9.append(this.isFingerprintDisabled(currentUser));
            printWriter.println(sb9.toString());
            final StringBuilder sb10 = new StringBuilder();
            sb10.append("    possible=");
            sb10.append(this.isUnlockWithFingerprintPossible(currentUser));
            printWriter.println(sb10.toString());
            final StringBuilder sb11 = new StringBuilder();
            sb11.append("    listening: actual=");
            sb11.append(this.mFingerprintRunningState);
            sb11.append(" expected=");
            sb11.append(this.shouldListenForFingerprint() ? 1 : 0);
            printWriter.println(sb11.toString());
            final StringBuilder sb12 = new StringBuilder();
            sb12.append("    strongAuthFlags=");
            sb12.append(Integer.toHexString(strongAuthForUser));
            printWriter.println(sb12.toString());
            final StringBuilder sb13 = new StringBuilder();
            sb13.append("    trustManaged=");
            sb13.append(this.getUserTrustIsManaged(currentUser));
            printWriter.println(sb13.toString());
        }
        final FaceManager mFaceManager = this.mFaceManager;
        if (mFaceManager != null && mFaceManager.isHardwareDetected()) {
            final int currentUser2 = ActivityManager.getCurrentUser();
            final int strongAuthForUser2 = this.mStrongAuthTracker.getStrongAuthForUser(currentUser2);
            final BiometricAuthenticated biometricAuthenticated2 = (BiometricAuthenticated)this.mUserFaceAuthenticated.get(currentUser2);
            final StringBuilder sb14 = new StringBuilder();
            sb14.append("  Face authentication state (user=");
            sb14.append(currentUser2);
            sb14.append(")");
            printWriter.println(sb14.toString());
            final StringBuilder sb15 = new StringBuilder();
            sb15.append("    allowed=");
            sb15.append(biometricAuthenticated2 != null && this.isUnlockingWithBiometricAllowed(biometricAuthenticated2.mIsStrongBiometric));
            printWriter.println(sb15.toString());
            final StringBuilder sb16 = new StringBuilder();
            sb16.append("    auth'd=");
            sb16.append(biometricAuthenticated2 != null && biometricAuthenticated2.mAuthenticated && b);
            printWriter.println(sb16.toString());
            final StringBuilder sb17 = new StringBuilder();
            sb17.append("    authSinceBoot=");
            sb17.append(this.getStrongAuthTracker().hasUserAuthenticatedSinceBoot());
            printWriter.println(sb17.toString());
            final StringBuilder sb18 = new StringBuilder();
            sb18.append("    disabled(DPM)=");
            sb18.append(this.isFaceDisabled(currentUser2));
            printWriter.println(sb18.toString());
            final StringBuilder sb19 = new StringBuilder();
            sb19.append("    possible=");
            sb19.append(this.isUnlockWithFacePossible(currentUser2));
            printWriter.println(sb19.toString());
            final StringBuilder sb20 = new StringBuilder();
            sb20.append("    strongAuthFlags=");
            sb20.append(Integer.toHexString(strongAuthForUser2));
            printWriter.println(sb20.toString());
            final StringBuilder sb21 = new StringBuilder();
            sb21.append("    trustManaged=");
            sb21.append(this.getUserTrustIsManaged(currentUser2));
            printWriter.println(sb21.toString());
            final StringBuilder sb22 = new StringBuilder();
            sb22.append("    enabledByUser=");
            sb22.append(this.mFaceSettingEnabledForUser.get(currentUser2));
            printWriter.println(sb22.toString());
            final StringBuilder sb23 = new StringBuilder();
            sb23.append("    mSecureCameraLaunched=");
            sb23.append(this.mSecureCameraLaunched);
            printWriter.println(sb23.toString());
        }
    }
    
    public List<SubscriptionInfo> getFilteredSubscriptionInfo(final boolean b) {
        final List<SubscriptionInfo> subscriptionInfo = this.getSubscriptionInfo(false);
        if (subscriptionInfo.size() == 2) {
            final SubscriptionInfo subscriptionInfo2 = subscriptionInfo.get(0);
            SubscriptionInfo subscriptionInfo3 = subscriptionInfo.get(1);
            if (subscriptionInfo2.getGroupUuid() != null && subscriptionInfo2.getGroupUuid().equals((Object)subscriptionInfo3.getGroupUuid())) {
                if (!subscriptionInfo2.isOpportunistic() && !subscriptionInfo3.isOpportunistic()) {
                    return subscriptionInfo;
                }
                if (CarrierConfigManager.getDefaultConfig().getBoolean("always_show_primary_signal_bar_in_opportunistic_network_boolean")) {
                    if (subscriptionInfo2.isOpportunistic()) {
                        subscriptionInfo3 = subscriptionInfo2;
                    }
                    subscriptionInfo.remove(subscriptionInfo3);
                }
                else {
                    SubscriptionInfo subscriptionInfo4 = subscriptionInfo2;
                    if (subscriptionInfo2.getSubscriptionId() == this.mActiveMobileDataSubscription) {
                        subscriptionInfo4 = subscriptionInfo3;
                    }
                    subscriptionInfo.remove(subscriptionInfo4);
                }
            }
        }
        return subscriptionInfo;
    }
    
    public int getNextSubIdForState(final int n) {
        int i = 0;
        final List<SubscriptionInfo> subscriptionInfo = this.getSubscriptionInfo(false);
        int n2 = -1;
        int n3 = Integer.MAX_VALUE;
        while (i < subscriptionInfo.size()) {
            final int subscriptionId = subscriptionInfo.get(i).getSubscriptionId();
            final int slotId = this.getSlotId(subscriptionId);
            int n4 = n2;
            int n5 = n3;
            if (n == this.getSimState(subscriptionId)) {
                n4 = n2;
                if ((n5 = n3) > slotId) {
                    n4 = subscriptionId;
                    n5 = slotId;
                }
            }
            ++i;
            n2 = n4;
            n3 = n5;
        }
        return n2;
    }
    
    public Intent getSecondaryLockscreenRequirement(final int i) {
        return this.mSecondaryLockscreenRequirement.get(i);
    }
    
    public ServiceState getServiceState(final int i) {
        return this.mServiceStates.get(i);
    }
    
    public int getSimState(final int n) {
        if (this.mSimDatas.containsKey(n)) {
            return this.mSimDatas.get(n).simState;
        }
        return 0;
    }
    
    public StrongAuthTracker getStrongAuthTracker() {
        return this.mStrongAuthTracker;
    }
    
    public List<SubscriptionInfo> getSubscriptionInfo(final boolean b) {
        List<SubscriptionInfo> mSubscriptionInfo = this.mSubscriptionInfo;
        if (mSubscriptionInfo == null || b) {
            mSubscriptionInfo = (List<SubscriptionInfo>)this.mSubscriptionManager.getCompleteActiveSubscriptionInfoList();
        }
        if (mSubscriptionInfo == null) {
            this.mSubscriptionInfo = new ArrayList<SubscriptionInfo>();
        }
        else {
            this.mSubscriptionInfo = mSubscriptionInfo;
        }
        return new ArrayList<SubscriptionInfo>(this.mSubscriptionInfo);
    }
    
    public SubscriptionInfo getSubscriptionInfoForSubId(final int n) {
        int i = 0;
        for (List<SubscriptionInfo> subscriptionInfo = this.getSubscriptionInfo(false); i < subscriptionInfo.size(); ++i) {
            final SubscriptionInfo subscriptionInfo2 = subscriptionInfo.get(i);
            if (n == subscriptionInfo2.getSubscriptionId()) {
                return subscriptionInfo2;
            }
        }
        return null;
    }
    
    public boolean getUserCanSkipBouncer(final int n) {
        return this.getUserHasTrust(n) || this.getUserUnlockedWithBiometric(n);
    }
    
    public boolean getUserHasTrust(final int n) {
        return !this.isTrustDisabled(n) && this.mUserHasTrust.get(n);
    }
    
    public boolean getUserTrustIsManaged(final int n) {
        return this.mUserTrustIsManaged.get(n) && !this.isTrustDisabled(n);
    }
    
    public boolean getUserUnlockedWithBiometric(int n) {
        final BiometricAuthenticated biometricAuthenticated = (BiometricAuthenticated)this.mUserFingerprintAuthenticated.get(n);
        final BiometricAuthenticated biometricAuthenticated2 = (BiometricAuthenticated)this.mUserFaceAuthenticated.get(n);
        final boolean b = true;
        if (biometricAuthenticated != null && biometricAuthenticated.mAuthenticated && this.isUnlockingWithBiometricAllowed(biometricAuthenticated.mIsStrongBiometric)) {
            n = 1;
        }
        else {
            n = 0;
        }
        final boolean b2 = biometricAuthenticated2 != null && biometricAuthenticated2.mAuthenticated && this.isUnlockingWithBiometricAllowed(biometricAuthenticated2.mIsStrongBiometric);
        boolean b3 = b;
        if (n == 0) {
            b3 = (b2 && b);
        }
        return b3;
    }
    
    protected void handleFinishedGoingToSleep(final int n) {
        Assert.isMainThread();
        int i = 0;
        this.mGoingToSleep = false;
        while (i < this.mCallbacks.size()) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onFinishedGoingToSleep(n);
            }
            ++i;
        }
        this.updateBiometricListeningState();
    }
    
    @VisibleForTesting
    void handleServiceStateChange(final int i, final ServiceState value) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            Log.w("KeyguardUpdateMonitor", "invalid subId in handleServiceStateChange()");
            return;
        }
        this.updateTelephonyCapable(true);
        this.mServiceStates.put(i, value);
        this.callbacksRefreshCarrierInfo();
    }
    
    @VisibleForTesting
    void handleSimStateChange(final int n, final int n2, final int n3) {
        Assert.isMainThread();
        final StringBuilder sb = new StringBuilder();
        sb.append("handleSimStateChange(subId=");
        sb.append(n);
        sb.append(", slotId=");
        sb.append(n2);
        sb.append(", state=");
        sb.append(n3);
        sb.append(")");
        Log.d("KeyguardUpdateMonitor", sb.toString());
        final boolean validSubscriptionId = SubscriptionManager.isValidSubscriptionId(n);
        final int n4 = 0;
        int n5 = 1;
        final int n6 = 1;
        boolean b = false;
        Label_0199: {
            if (!validSubscriptionId) {
                Log.w("KeyguardUpdateMonitor", "invalid subId in handleSimStateChange()");
                if (n3 == 1) {
                    this.updateTelephonyCapable(true);
                    for (final SimData simData : this.mSimDatas.values()) {
                        if (simData.slotId == n2) {
                            simData.simState = 1;
                        }
                    }
                    b = true;
                    break Label_0199;
                }
                if (n3 != 8) {
                    return;
                }
                this.updateTelephonyCapable(true);
            }
            b = false;
        }
        final SimData simData2 = this.mSimDatas.get(n);
        if (simData2 == null) {
            this.mSimDatas.put(n, new SimData(n3, n2, n));
        }
        else {
            n5 = n6;
            if (simData2.simState == n3) {
                n5 = n6;
                if (simData2.subId == n) {
                    if (simData2.slotId != n2) {
                        n5 = n6;
                    }
                    else {
                        n5 = 0;
                    }
                }
            }
            simData2.simState = n3;
            simData2.subId = n;
            simData2.slotId = n2;
        }
        if ((n5 != 0 || b) && n3 != 0) {
            for (int i = n4; i < this.mCallbacks.size(); ++i) {
                final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onSimStateChanged(n, n2, n3);
                }
            }
        }
    }
    
    protected void handleStartedGoingToSleep(final int n) {
        Assert.isMainThread();
        int i = 0;
        this.mLockIconPressed = false;
        this.clearBiometricRecognized();
        while (i < this.mCallbacks.size()) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onStartedGoingToSleep(n);
            }
            ++i;
        }
        this.mGoingToSleep = true;
        this.updateBiometricListeningState();
    }
    
    protected void handleStartedWakingUp() {
        Trace.beginSection("KeyguardUpdateMonitor#handleStartedWakingUp");
        Assert.isMainThread();
        this.updateBiometricListeningState();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onStartedWakingUp();
            }
        }
        Trace.endSection();
    }
    
    @VisibleForTesting
    void handleUserRemoved(final int n) {
        Assert.isMainThread();
        this.mUserIsUnlocked.delete(n);
        this.mUserTrustIsUsuallyManaged.delete(n);
    }
    
    @VisibleForTesting
    void handleUserSwitching(final int n, final IRemoteCallback remoteCallback) {
        Assert.isMainThread();
        this.clearBiometricRecognized();
        this.mUserTrustIsUsuallyManaged.put(n, this.mTrustManager.isTrustUsuallyManaged(n));
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onUserSwitching(n);
            }
        }
        try {
            remoteCallback.sendResult((Bundle)null);
        }
        catch (RemoteException ex) {}
    }
    
    public boolean hasLockscreenWallpaper() {
        return this.mHasLockscreenWallpaper;
    }
    
    public boolean isDeviceInteractive() {
        return this.mDeviceInteractive;
    }
    
    public boolean isDeviceProvisioned() {
        return this.mDeviceProvisioned;
    }
    
    public boolean isDreaming() {
        return this.mIsDreaming;
    }
    
    public boolean isFaceAuthEnabledForUser(final int n) {
        return DejankUtils.whitelistIpcs((Supplier<Boolean>)new _$$Lambda$KeyguardUpdateMonitor$0UXHOkJpmpZlHpxHcUFHVsSURJU(this, n));
    }
    
    public boolean isFaceDetectionRunning() {
        final int mFaceRunningState = this.mFaceRunningState;
        boolean b = true;
        if (mFaceRunningState != 1) {
            b = false;
        }
        return b;
    }
    
    public boolean isFingerprintDetectionRunning() {
        final int mFingerprintRunningState = this.mFingerprintRunningState;
        boolean b = true;
        if (mFingerprintRunningState != 1) {
            b = false;
        }
        return b;
    }
    
    public boolean isGoingToSleep() {
        return this.mGoingToSleep;
    }
    
    public boolean isKeyguardVisible() {
        return this.mKeyguardIsVisible;
    }
    
    public boolean isLogoutEnabled() {
        return this.mLogoutEnabled;
    }
    
    public boolean isScreenOn() {
        return this.mScreenOn;
    }
    
    public boolean isSimPinSecure() {
        final Iterator<SubscriptionInfo> iterator = this.getSubscriptionInfo(false).iterator();
        while (iterator.hasNext()) {
            if (isSimPinSecure(this.getSimState(iterator.next().getSubscriptionId()))) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isSimPinVoiceSecure() {
        return this.isSimPinSecure();
    }
    
    public boolean isSwitchingUser() {
        return this.mSwitchingUser;
    }
    
    public boolean isTrustUsuallyManaged(final int n) {
        Assert.isMainThread();
        return this.mUserTrustIsUsuallyManaged.get(n);
    }
    
    public boolean isUnlockingWithBiometricAllowed(final boolean b) {
        return this.mStrongAuthTracker.isUnlockingWithBiometricAllowed(b);
    }
    
    public boolean isUnlockingWithBiometricsPossible(final int n) {
        return this.isUnlockWithFacePossible(n) || this.isUnlockWithFingerprintPossible(n);
    }
    
    public boolean isUserInLockdown(final int n) {
        return this.containsFlag(this.mStrongAuthTracker.getStrongAuthForUser(n), 32);
    }
    
    public boolean isUserUnlocked(final int n) {
        return this.mUserIsUnlocked.get(n);
    }
    
    public boolean needsSlowUnlockTransition() {
        return this.mNeedsSlowUnlockTransition;
    }
    
    public void onAuthInterruptDetected(final boolean mAuthInterruptActive) {
        if (this.mAuthInterruptActive == mAuthInterruptActive) {
            return;
        }
        this.mAuthInterruptActive = mAuthInterruptActive;
        this.updateFaceListeningState();
    }
    
    public void onCameraLaunched() {
        this.mSecureCameraLaunched = true;
        this.updateBiometricListeningState();
    }
    
    @VisibleForTesting
    protected void onFaceAuthenticated(final int n, final boolean b) {
        Trace.beginSection("KeyGuardUpdateMonitor#onFaceAuthenticated");
        Assert.isMainThread();
        this.mUserFaceAuthenticated.put(n, (Object)new BiometricAuthenticated(true, b));
        if (this.getUserCanSkipBouncer(n)) {
            this.mTrustManager.unlockedByBiometricForUser(n, BiometricSourceType.FACE);
        }
        this.mFaceCancelSignal = null;
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricAuthenticated(n, BiometricSourceType.FACE, b);
            }
        }
        final Handler mHandler = this.mHandler;
        mHandler.sendMessageDelayed(mHandler.obtainMessage(336), 500L);
        this.mAssistantVisible = false;
        this.reportSuccessfulBiometricUnlock(b, n);
        Trace.endSection();
    }
    
    @VisibleForTesting
    protected void onFingerprintAuthenticated(final int n, final boolean b) {
        Assert.isMainThread();
        Trace.beginSection("KeyGuardUpdateMonitor#onFingerPrintAuthenticated");
        this.mUserFingerprintAuthenticated.put(n, (Object)new BiometricAuthenticated(true, b));
        if (this.getUserCanSkipBouncer(n)) {
            this.mTrustManager.unlockedByBiometricForUser(n, BiometricSourceType.FINGERPRINT);
        }
        this.mFingerprintCancelSignal = null;
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricAuthenticated(n, BiometricSourceType.FINGERPRINT, b);
            }
        }
        final Handler mHandler = this.mHandler;
        mHandler.sendMessageDelayed(mHandler.obtainMessage(336), 500L);
        this.mAssistantVisible = false;
        this.reportSuccessfulBiometricUnlock(b, n);
        Trace.endSection();
    }
    
    public void onKeyguardVisibilityChanged(final boolean b) {
        Assert.isMainThread();
        final StringBuilder sb = new StringBuilder();
        sb.append("onKeyguardVisibilityChanged(");
        sb.append(b);
        sb.append(")");
        Log.d("KeyguardUpdateMonitor", sb.toString());
        this.mKeyguardIsVisible = b;
        int i;
        final int n = i = 0;
        if (b) {
            this.mSecureCameraLaunched = false;
            i = n;
        }
        while (i < this.mCallbacks.size()) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onKeyguardVisibilityChangedRaw(b);
            }
            ++i;
        }
        this.updateBiometricListeningState();
    }
    
    public void onLockIconPressed() {
        this.mLockIconPressed = true;
        final int currentUser = getCurrentUser();
        this.mUserFaceAuthenticated.put(currentUser, (Object)null);
        this.updateFaceListeningState();
        this.mStrongAuthTracker.onStrongAuthRequiredChanged(currentUser);
    }
    
    public void onTrustChanged(final boolean b, final int n, final int n2) {
        Assert.isMainThread();
        this.mUserHasTrust.put(n, b);
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTrustChanged(n);
                if (b && n2 != 0) {
                    keyguardUpdateMonitorCallback.onTrustGrantedWithFlags(n2, n);
                }
            }
        }
    }
    
    public void onTrustError(final CharSequence charSequence) {
        this.dispatchErrorMessage(charSequence);
    }
    
    public void onTrustManagedChanged(final boolean b, final int n) {
        Assert.isMainThread();
        this.mUserTrustIsManaged.put(n, b);
        this.mUserTrustIsUsuallyManaged.put(n, this.mTrustManager.isTrustUsuallyManaged(n));
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTrustManagedChanged(n);
            }
        }
    }
    
    public void registerCallback(final KeyguardUpdateMonitorCallback referent) {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            if (this.mCallbacks.get(i).get() == referent) {
                return;
            }
        }
        this.mCallbacks.add(new WeakReference<KeyguardUpdateMonitorCallback>(referent));
        this.removeCallback(null);
        this.sendUpdates(referent);
    }
    
    public void removeCallback(final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback) {
        Assert.isMainThread();
        this.mCallbacks.removeIf(new _$$Lambda$KeyguardUpdateMonitor$shh0D49FvbBEewtE2q5FtYu8WmE(keyguardUpdateMonitorCallback));
    }
    
    public void reportEmergencyCallAction(final boolean b) {
        if (!b) {
            this.mHandler.obtainMessage(318).sendToTarget();
        }
        else {
            Assert.isMainThread();
            this.handleReportEmergencyCallAction();
        }
    }
    
    public void reportSimUnlocked(final int i) {
        final StringBuilder sb = new StringBuilder();
        sb.append("reportSimUnlocked(subId=");
        sb.append(i);
        sb.append(")");
        Log.v("KeyguardUpdateMonitor", sb.toString());
        this.handleSimStateChange(i, this.getSlotId(i), 5);
    }
    
    public void requestFaceAuth() {
        this.updateFaceListeningState();
    }
    
    public void sendKeyguardBouncerChanged(final boolean arg1) {
        final Message obtainMessage = this.mHandler.obtainMessage(322);
        obtainMessage.arg1 = (arg1 ? 1 : 0);
        obtainMessage.sendToTarget();
    }
    
    public void sendKeyguardReset() {
        this.mHandler.obtainMessage(312).sendToTarget();
    }
    
    @VisibleForTesting
    void setAssistantVisible(final boolean mAssistantVisible) {
        this.mAssistantVisible = mAssistantVisible;
        this.updateBiometricListeningState();
    }
    
    public void setCredentialAttempted() {
        this.mCredentialAttempted = true;
        this.updateBiometricListeningState();
    }
    
    public void setHasLockscreenWallpaper(final boolean mHasLockscreenWallpaper) {
        Assert.isMainThread();
        if (mHasLockscreenWallpaper != this.mHasLockscreenWallpaper) {
            this.mHasLockscreenWallpaper = mHasLockscreenWallpaper;
            for (int i = 0; i < this.mCallbacks.size(); ++i) {
                final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onHasLockscreenWallpaperChanged(mHasLockscreenWallpaper);
                }
            }
        }
    }
    
    public void setKeyguardBypassController(final KeyguardBypassController mKeyguardBypassController) {
        this.mKeyguardBypassController = mKeyguardBypassController;
    }
    
    public void setKeyguardGoingAway(final boolean mKeyguardGoingAway) {
        this.mKeyguardGoingAway = mKeyguardGoingAway;
        this.updateBiometricListeningState();
    }
    
    public void setKeyguardOccluded(final boolean mKeyguardOccluded) {
        this.mKeyguardOccluded = mKeyguardOccluded;
        this.updateBiometricListeningState();
    }
    
    public void setSwitchingUser(final boolean mSwitchingUser) {
        this.mSwitchingUser = mSwitchingUser;
        this.mHandler.post(this.mUpdateBiometricListeningState);
    }
    
    public boolean shouldListenForFace() {
        final boolean mKeyguardIsVisible = this.mKeyguardIsVisible;
        final boolean b = false;
        final boolean b2 = mKeyguardIsVisible && this.mDeviceInteractive && !this.mGoingToSleep;
        final int currentUser = getCurrentUser();
        final int strongAuthForUser = this.mStrongAuthTracker.getStrongAuthForUser(currentUser);
        final boolean b3 = this.containsFlag(strongAuthForUser, 2) || this.containsFlag(strongAuthForUser, 32);
        final boolean b4 = this.containsFlag(strongAuthForUser, 1) || this.containsFlag(strongAuthForUser, 16);
        final KeyguardBypassController mKeyguardBypassController = this.mKeyguardBypassController;
        final boolean b5 = mKeyguardBypassController != null && mKeyguardBypassController.canBypass();
        final boolean b6 = !this.getUserCanSkipBouncer(currentUser) || b5;
        final boolean b7 = (!b4 || (b5 && !this.mBouncer)) && !b3;
        if (!this.mBouncer && !this.mAuthInterruptActive && !b2) {
            final boolean b8 = b;
            if (!this.shouldListenForFaceAssistant()) {
                return b8;
            }
        }
        boolean b8 = b;
        if (!this.mSwitchingUser) {
            b8 = b;
            if (!this.isFaceDisabled(currentUser)) {
                b8 = b;
                if (b6) {
                    b8 = b;
                    if (!this.mKeyguardGoingAway) {
                        b8 = b;
                        if (this.mFaceSettingEnabledForUser.get(currentUser)) {
                            b8 = b;
                            if (!this.mLockIconPressed) {
                                b8 = b;
                                if (b7) {
                                    b8 = b;
                                    if (this.mIsPrimaryUser) {
                                        b8 = b;
                                        if (!this.mSecureCameraLaunched) {
                                            b8 = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return b8;
    }
    
    @VisibleForTesting
    void updateTelephonyCapable(final boolean mTelephonyCapable) {
        Assert.isMainThread();
        if (mTelephonyCapable == this.mTelephonyCapable) {
            return;
        }
        this.mTelephonyCapable = mTelephonyCapable;
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTelephonyCapable(this.mTelephonyCapable);
            }
        }
    }
    
    public boolean userNeedsStrongAuth() {
        return this.mStrongAuthTracker.getStrongAuthForUser(getCurrentUser()) != 0;
    }
    
    @VisibleForTesting
    static class BiometricAuthenticated
    {
        private final boolean mAuthenticated;
        private final boolean mIsStrongBiometric;
        
        BiometricAuthenticated(final boolean mAuthenticated, final boolean mIsStrongBiometric) {
            this.mAuthenticated = mAuthenticated;
            this.mIsStrongBiometric = mIsStrongBiometric;
        }
    }
    
    private static class SimData
    {
        public int simState;
        public int slotId;
        public int subId;
        
        SimData(final int simState, final int slotId, final int subId) {
            this.simState = simState;
            this.slotId = slotId;
            this.subId = subId;
        }
        
        static SimData fromIntent(final Intent intent) {
            if ("android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction())) {
                final String stringExtra = intent.getStringExtra("ss");
                int n = 0;
                final int intExtra = intent.getIntExtra("android.telephony.extra.SLOT_INDEX", 0);
                final int intExtra2 = intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1);
                if ("ABSENT".equals(stringExtra)) {
                    if ("PERM_DISABLED".equals(intent.getStringExtra("reason"))) {
                        n = 7;
                    }
                    else {
                        n = 1;
                    }
                }
                else {
                    if (!"READY".equals(stringExtra)) {
                        if ("LOCKED".equals(stringExtra)) {
                            final String stringExtra2 = intent.getStringExtra("reason");
                            if ("PIN".equals(stringExtra2)) {
                                n = 2;
                                return new SimData(n, intExtra, intExtra2);
                            }
                            if ("PUK".equals(stringExtra2)) {
                                n = 3;
                                return new SimData(n, intExtra, intExtra2);
                            }
                            return new SimData(n, intExtra, intExtra2);
                        }
                        else {
                            if ("NETWORK".equals(stringExtra)) {
                                n = 4;
                                return new SimData(n, intExtra, intExtra2);
                            }
                            if ("CARD_IO_ERROR".equals(stringExtra)) {
                                n = 8;
                                return new SimData(n, intExtra, intExtra2);
                            }
                            if (!"LOADED".equals(stringExtra)) {
                                if (!"IMSI".equals(stringExtra)) {
                                    return new SimData(n, intExtra, intExtra2);
                                }
                            }
                        }
                    }
                    n = 5;
                }
                return new SimData(n, intExtra, intExtra2);
            }
            throw new IllegalArgumentException("only handles intent ACTION_SIM_STATE_CHANGED");
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("SimData{state=");
            sb.append(this.simState);
            sb.append(",slotId=");
            sb.append(this.slotId);
            sb.append(",subId=");
            sb.append(this.subId);
            sb.append("}");
            return sb.toString();
        }
    }
    
    public static class StrongAuthTracker extends LockPatternUtils$StrongAuthTracker
    {
        private final Consumer<Integer> mStrongAuthRequiredChangedCallback;
        
        public StrongAuthTracker(final Context context, final Consumer<Integer> mStrongAuthRequiredChangedCallback) {
            super(context);
            this.mStrongAuthRequiredChangedCallback = mStrongAuthRequiredChangedCallback;
        }
        
        public boolean hasUserAuthenticatedSinceBoot() {
            final int strongAuthForUser = this.getStrongAuthForUser(KeyguardUpdateMonitor.getCurrentUser());
            boolean b = true;
            if ((strongAuthForUser & 0x1) != 0x0) {
                b = false;
            }
            return b;
        }
        
        public boolean isUnlockingWithBiometricAllowed(final boolean b) {
            return this.isBiometricAllowedForUser(b, KeyguardUpdateMonitor.getCurrentUser());
        }
        
        public void onStrongAuthRequiredChanged(final int i) {
            this.mStrongAuthRequiredChangedCallback.accept(i);
        }
    }
}
