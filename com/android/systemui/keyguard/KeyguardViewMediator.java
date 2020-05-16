// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import android.util.EventLog;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.view.animation.AnimationUtils;
import android.provider.Settings$Global;
import android.media.AudioAttributes$Builder;
import android.media.SoundPool$Builder;
import com.android.systemui.R$bool;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.SystemUIFactory;
import android.content.IntentFilter;
import com.android.systemui.DejankUtils;
import com.android.internal.util.LatencyTracker;
import android.content.ContentResolver;
import android.provider.Settings$System;
import android.content.ComponentName;
import android.provider.Settings$Secure;
import android.telephony.SubscriptionManager;
import android.os.SystemProperties;
import android.app.PendingIntent;
import android.os.SystemClock;
import android.os.UserHandle;
import com.android.systemui.shared.system.QuickStepContract;
import java.util.Objects;
import android.app.ActivityTaskManager;
import android.os.Message;
import android.os.Handler$Callback;
import android.os.Looper;
import android.os.Trace;
import android.app.ActivityManager;
import android.content.pm.UserInfo;
import com.android.internal.policy.IKeyguardDismissCallback;
import android.os.UserManager;
import android.os.RemoteException;
import android.os.DeadObjectException;
import android.util.Slog;
import android.util.Log;
import android.os.Bundle;
import android.hardware.biometrics.BiometricSourceType;
import android.provider.DeviceConfig$Properties;
import android.telephony.TelephonyManager;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.dump.DumpManager;
import android.content.Context;
import com.android.keyguard.ViewMediatorCallback;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import java.util.concurrent.Executor;
import android.app.trust.TrustManager;
import android.app.StatusBarManager;
import android.os.PowerManager$WakeLock;
import android.os.PowerManager;
import android.provider.DeviceConfig$OnPropertiesChangedListener;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import android.media.SoundPool;
import com.android.internal.widget.LockPatternUtils;
import android.util.SparseIntArray;
import com.android.keyguard.KeyguardViewController;
import dagger.Lazy;
import com.android.internal.policy.IKeyguardStateCallback;
import java.util.ArrayList;
import com.android.keyguard.KeyguardDisplayManager;
import android.view.animation.Animation;
import android.os.Handler;
import com.android.systemui.plugins.FalsingManager;
import com.android.internal.policy.IKeyguardExitCallback;
import com.android.internal.policy.IKeyguardDrawnCallback;
import com.android.systemui.util.DeviceConfigProxy;
import android.content.BroadcastReceiver;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.media.AudioManager;
import android.app.AlarmManager;
import android.content.Intent;
import com.android.systemui.Dumpable;
import com.android.systemui.SystemUI;

public class KeyguardViewMediator extends SystemUI implements Dumpable
{
    private static final Intent USER_PRESENT_INTENT;
    private AlarmManager mAlarmManager;
    private boolean mAodShowing;
    private AudioManager mAudioManager;
    private boolean mBootCompleted;
    private boolean mBootSendUserPresent;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final BroadcastReceiver mBroadcastReceiver;
    private CharSequence mCustomMessage;
    private final BroadcastReceiver mDelayedLockBroadcastReceiver;
    private int mDelayedProfileShowingSequence;
    private int mDelayedShowingSequence;
    private DeviceConfigProxy mDeviceConfig;
    private boolean mDeviceInteractive;
    private final DismissCallbackRegistry mDismissCallbackRegistry;
    private boolean mDozing;
    private IKeyguardDrawnCallback mDrawnCallback;
    private IKeyguardExitCallback mExitSecureCallback;
    private boolean mExternallyEnabled;
    private final FalsingManager mFalsingManager;
    private boolean mGoingToSleep;
    private Handler mHandler;
    private Animation mHideAnimation;
    private final Runnable mHideAnimationFinishedRunnable;
    private boolean mHideAnimationRun;
    private boolean mHideAnimationRunning;
    private boolean mHiding;
    private boolean mInGestureNavigationMode;
    private boolean mInputRestricted;
    private KeyguardDisplayManager mKeyguardDisplayManager;
    private boolean mKeyguardDonePending;
    private final Runnable mKeyguardGoingAwayRunnable;
    private final ArrayList<IKeyguardStateCallback> mKeyguardStateCallbacks;
    private final Lazy<KeyguardViewController> mKeyguardViewControllerLazy;
    private final SparseIntArray mLastSimStates;
    private boolean mLockLater;
    private final LockPatternUtils mLockPatternUtils;
    private int mLockSoundId;
    private int mLockSoundStreamId;
    private float mLockSoundVolume;
    private SoundPool mLockSounds;
    private boolean mNeedToReshowWhenReenabled;
    private final NotificationShadeWindowController mNotificationShadeWindowController;
    private boolean mOccluded;
    private final DeviceConfig$OnPropertiesChangedListener mOnPropertiesChangedListener;
    private final PowerManager mPM;
    private boolean mPendingLock;
    private boolean mPendingReset;
    private String mPhoneState;
    private boolean mPulsing;
    private boolean mShowHomeOverLockscreen;
    private PowerManager$WakeLock mShowKeyguardWakeLock;
    private boolean mShowing;
    private boolean mShuttingDown;
    private StatusBarManager mStatusBarManager;
    private boolean mSystemReady;
    private final TrustManager mTrustManager;
    private int mTrustedSoundId;
    private final Executor mUiBgExecutor;
    private int mUiSoundsStreamType;
    private int mUnlockSoundId;
    KeyguardUpdateMonitorCallback mUpdateCallback;
    private final KeyguardUpdateMonitor mUpdateMonitor;
    ViewMediatorCallback mViewMediatorCallback;
    private boolean mWaitingUntilKeyguardVisible;
    private boolean mWakeAndUnlocking;
    
    static {
        USER_PRESENT_INTENT = new Intent("android.intent.action.USER_PRESENT").addFlags(606076928);
    }
    
    public KeyguardViewMediator(final Context context, final FalsingManager mFalsingManager, final LockPatternUtils mLockPatternUtils, final BroadcastDispatcher mBroadcastDispatcher, final NotificationShadeWindowController mNotificationShadeWindowController, final Lazy<KeyguardViewController> mKeyguardViewControllerLazy, final DismissCallbackRegistry mDismissCallbackRegistry, final KeyguardUpdateMonitor mUpdateMonitor, final DumpManager dumpManager, final Executor mUiBgExecutor, final PowerManager mpm, final TrustManager mTrustManager, final DeviceConfigProxy mDeviceConfig, final NavigationModeController navigationModeController) {
        super(context);
        this.mExternallyEnabled = true;
        this.mNeedToReshowWhenReenabled = false;
        this.mOccluded = false;
        this.mLastSimStates = new SparseIntArray();
        this.mPhoneState = TelephonyManager.EXTRA_STATE_IDLE;
        this.mWaitingUntilKeyguardVisible = false;
        this.mKeyguardDonePending = false;
        this.mHideAnimationRun = false;
        this.mHideAnimationRunning = false;
        this.mKeyguardStateCallbacks = new ArrayList<IKeyguardStateCallback>();
        this.mOnPropertiesChangedListener = (DeviceConfig$OnPropertiesChangedListener)new DeviceConfig$OnPropertiesChangedListener() {
            public void onPropertiesChanged(final DeviceConfig$Properties deviceConfig$Properties) {
                if (deviceConfig$Properties.getKeyset().contains("nav_bar_handle_show_over_lockscreen")) {
                    KeyguardViewMediator.this.mShowHomeOverLockscreen = deviceConfig$Properties.getBoolean("nav_bar_handle_show_over_lockscreen", true);
                }
            }
        };
        this.mUpdateCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onBiometricAuthFailed(final BiometricSourceType biometricSourceType) {
                final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
                if (KeyguardViewMediator.this.mLockPatternUtils.isSecure(currentUser)) {
                    KeyguardViewMediator.this.mLockPatternUtils.getDevicePolicyManager().reportFailedBiometricAttempt(currentUser);
                }
            }
            
            @Override
            public void onBiometricAuthenticated(final int n, final BiometricSourceType biometricSourceType, final boolean b) {
                if (KeyguardViewMediator.this.mLockPatternUtils.isSecure(n)) {
                    KeyguardViewMediator.this.mLockPatternUtils.getDevicePolicyManager().reportSuccessfulBiometricAttempt(n);
                }
            }
            
            @Override
            public void onClockVisibilityChanged() {
                KeyguardViewMediator.this.adjustStatusBarLocked();
            }
            
            @Override
            public void onDeviceProvisioned() {
                KeyguardViewMediator.this.sendUserPresentBroadcast();
                synchronized (KeyguardViewMediator.this) {
                    if (KeyguardViewMediator.this.mustNotUnlockCurrentUser()) {
                        KeyguardViewMediator.this.doKeyguardLocked(null);
                    }
                }
            }
            
            @Override
            public void onHasLockscreenWallpaperChanged(final boolean b) {
                synchronized (KeyguardViewMediator.this) {
                    KeyguardViewMediator.this.notifyHasLockscreenWallpaperChanged(b);
                }
            }
            
            @Override
            public void onSimStateChanged(int i, final int j, final int n) {
                final StringBuilder sb = new StringBuilder();
                sb.append("onSimStateChanged(subId=");
                sb.append(i);
                sb.append(", slotId=");
                sb.append(j);
                sb.append(",state=");
                sb.append(n);
                sb.append(")");
                Log.d("KeyguardViewMediator", sb.toString());
                i = KeyguardViewMediator.this.mKeyguardStateCallbacks.size();
                final boolean simPinSecure = KeyguardViewMediator.this.mUpdateMonitor.isSimPinSecure();
                --i;
                while (i >= 0) {
                    try {
                        KeyguardViewMediator.this.mKeyguardStateCallbacks.get(i).onSimSecureStateChanged(simPinSecure);
                    }
                    catch (RemoteException ex) {
                        Slog.w("KeyguardViewMediator", "Failed to call onSimSecureStateChanged", (Throwable)ex);
                        if (ex instanceof DeadObjectException) {
                            KeyguardViewMediator.this.mKeyguardStateCallbacks.remove(i);
                        }
                    }
                    --i;
                }
                synchronized (KeyguardViewMediator.this) {
                    i = KeyguardViewMediator.this.mLastSimStates.get(j);
                    if (i != 2 && i != 3) {
                        i = 0;
                    }
                    else {
                        i = 1;
                    }
                    KeyguardViewMediator.this.mLastSimStates.append(j, n);
                    // monitorexit(this.this$0)
                    Label_0511: {
                        if (n != 1) {
                            if (n != 2 && n != 3) {
                                if (n != 5) {
                                    if (n == 6) {
                                        break Label_0511;
                                    }
                                    if (n != 7) {
                                        final StringBuilder sb2 = new StringBuilder();
                                        sb2.append("Unspecific state: ");
                                        sb2.append(n);
                                        Log.v("KeyguardViewMediator", sb2.toString());
                                        return;
                                    }
                                    synchronized (KeyguardViewMediator.this) {
                                        if (!KeyguardViewMediator.this.mShowing) {
                                            Log.d("KeyguardViewMediator", "PERM_DISABLED and keygaurd isn't showing.");
                                            KeyguardViewMediator.this.doKeyguardLocked(null);
                                            return;
                                        }
                                        Log.d("KeyguardViewMediator", "PERM_DISABLED, resetStateLocked toshow permanently disabled message in lockscreen.");
                                        KeyguardViewMediator.this.resetStateLocked();
                                        return;
                                    }
                                }
                                synchronized (KeyguardViewMediator.this) {
                                    final StringBuilder sb3 = new StringBuilder();
                                    sb3.append("READY, reset state? ");
                                    sb3.append(KeyguardViewMediator.this.mShowing);
                                    Log.d("KeyguardViewMediator", sb3.toString());
                                    if (KeyguardViewMediator.this.mShowing && i != 0) {
                                        Log.d("KeyguardViewMediator", "SIM moved to READY when the previous state was locked. Reset the state.");
                                        KeyguardViewMediator.this.resetStateLocked();
                                    }
                                    return;
                                }
                            }
                            synchronized (KeyguardViewMediator.this) {
                                if (!KeyguardViewMediator.this.mShowing) {
                                    Log.d("KeyguardViewMediator", "INTENT_VALUE_ICC_LOCKED and keygaurd isn't showing; need to show keyguard so user can enter sim pin");
                                    KeyguardViewMediator.this.doKeyguardLocked(null);
                                    return;
                                }
                                KeyguardViewMediator.this.resetStateLocked();
                                return;
                            }
                        }
                    }
                    synchronized (KeyguardViewMediator.this) {
                        if (KeyguardViewMediator.this.shouldWaitForProvisioning()) {
                            if (!KeyguardViewMediator.this.mShowing) {
                                Log.d("KeyguardViewMediator", "ICC_ABSENT isn't showing, we need to show the keyguard since the device isn't provisioned yet.");
                                KeyguardViewMediator.this.doKeyguardLocked(null);
                            }
                            else {
                                KeyguardViewMediator.this.resetStateLocked();
                            }
                        }
                        if (n == 1 && i != 0) {
                            Log.d("KeyguardViewMediator", "SIM moved to ABSENT when the previous state was locked. Reset the state.");
                            KeyguardViewMediator.this.resetStateLocked();
                        }
                    }
                }
            }
            
            @Override
            public void onTrustChanged(final int n) {
                if (n == KeyguardUpdateMonitor.getCurrentUser()) {
                    synchronized (KeyguardViewMediator.this) {
                        KeyguardViewMediator.this.notifyTrustedChangedLocked(KeyguardViewMediator.this.mUpdateMonitor.getUserHasTrust(n));
                    }
                }
            }
            
            @Override
            public void onUserInfoChanged(final int n) {
            }
            
            @Override
            public void onUserSwitchComplete(final int n) {
                if (n != 0) {
                    final UserInfo userInfo = UserManager.get(KeyguardViewMediator.this.mContext).getUserInfo(n);
                    if (userInfo != null) {
                        if (!KeyguardViewMediator.this.mLockPatternUtils.isSecure(n)) {
                            if (userInfo.isGuest() || userInfo.isDemo()) {
                                KeyguardViewMediator.this.dismiss(null, null);
                            }
                        }
                    }
                }
            }
            
            @Override
            public void onUserSwitching(final int n) {
                synchronized (KeyguardViewMediator.this) {
                    KeyguardViewMediator.this.resetKeyguardDonePendingLocked();
                    if (KeyguardViewMediator.this.mLockPatternUtils.isLockScreenDisabled(n)) {
                        KeyguardViewMediator.this.dismiss(null, null);
                    }
                    else {
                        KeyguardViewMediator.this.resetStateLocked();
                    }
                    KeyguardViewMediator.this.adjustStatusBarLocked();
                }
            }
        };
        this.mViewMediatorCallback = new ViewMediatorCallback() {
            @Override
            public CharSequence consumeCustomMessage() {
                final CharSequence access$2700 = KeyguardViewMediator.this.mCustomMessage;
                KeyguardViewMediator.this.mCustomMessage = null;
                return access$2700;
            }
            
            @Override
            public int getBouncerPromptReason() {
                final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
                final boolean trustUsuallyManaged = KeyguardViewMediator.this.mUpdateMonitor.isTrustUsuallyManaged(currentUser);
                final boolean unlockingWithBiometricsPossible = KeyguardViewMediator.this.mUpdateMonitor.isUnlockingWithBiometricsPossible(currentUser);
                final boolean b = trustUsuallyManaged || unlockingWithBiometricsPossible;
                final KeyguardUpdateMonitor.StrongAuthTracker strongAuthTracker = KeyguardViewMediator.this.mUpdateMonitor.getStrongAuthTracker();
                final int strongAuthForUser = strongAuthTracker.getStrongAuthForUser(currentUser);
                if (b && !strongAuthTracker.hasUserAuthenticatedSinceBoot()) {
                    return 1;
                }
                if (b && (strongAuthForUser & 0x10) != 0x0) {
                    return 2;
                }
                if (b && (strongAuthForUser & 0x2) != 0x0) {
                    return 3;
                }
                if (trustUsuallyManaged && (strongAuthForUser & 0x4) != 0x0) {
                    return 4;
                }
                if (b && (strongAuthForUser & 0x8) != 0x0) {
                    return 5;
                }
                if (b && (strongAuthForUser & 0x40) != 0x0) {
                    return 6;
                }
                if (b && (strongAuthForUser & 0x80) != 0x0) {
                    return 7;
                }
                return 0;
            }
            
            @Override
            public boolean isScreenOn() {
                return KeyguardViewMediator.this.mDeviceInteractive;
            }
            
            @Override
            public void keyguardDone(final boolean b, final int n) {
                if (n != ActivityManager.getCurrentUser()) {
                    return;
                }
                KeyguardViewMediator.this.tryKeyguardDone();
            }
            
            @Override
            public void keyguardDoneDrawing() {
                Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardDoneDrawing");
                KeyguardViewMediator.this.mHandler.sendEmptyMessage(8);
                Trace.endSection();
            }
            
            @Override
            public void keyguardDonePending(final boolean b, final int n) {
                Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardDonePending");
                if (n != ActivityManager.getCurrentUser()) {
                    Trace.endSection();
                    return;
                }
                KeyguardViewMediator.this.mKeyguardDonePending = true;
                KeyguardViewMediator.this.mHideAnimationRun = true;
                KeyguardViewMediator.this.mHideAnimationRunning = true;
                KeyguardViewMediator.this.mKeyguardViewControllerLazy.get().startPreHideAnimation(KeyguardViewMediator.this.mHideAnimationFinishedRunnable);
                KeyguardViewMediator.this.mHandler.sendEmptyMessageDelayed(13, 3000L);
                Trace.endSection();
            }
            
            @Override
            public void keyguardGone() {
                Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardGone");
                KeyguardViewMediator.this.mNotificationShadeWindowController.setKeyguardGoingAway(false);
                KeyguardViewMediator.this.mKeyguardDisplayManager.hide();
                Trace.endSection();
            }
            
            @Override
            public void onBouncerVisiblityChanged(final boolean b) {
                synchronized (KeyguardViewMediator.this) {
                    KeyguardViewMediator.this.adjustStatusBarLocked(b);
                }
            }
            
            @Override
            public void onCancelClicked() {
                KeyguardViewMediator.this.mKeyguardViewControllerLazy.get().onCancelClicked();
            }
            
            @Override
            public void playTrustedSound() {
                KeyguardViewMediator.this.playTrustedSound();
            }
            
            @Override
            public void readyForKeyguardDone() {
                Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#readyForKeyguardDone");
                if (KeyguardViewMediator.this.mKeyguardDonePending) {
                    KeyguardViewMediator.this.mKeyguardDonePending = false;
                    KeyguardViewMediator.this.tryKeyguardDone();
                }
                Trace.endSection();
            }
            
            @Override
            public void resetKeyguard() {
                KeyguardViewMediator.this.resetStateLocked();
            }
            
            @Override
            public void setNeedsInput(final boolean needsInput) {
                KeyguardViewMediator.this.mKeyguardViewControllerLazy.get().setNeedsInput(needsInput);
            }
            
            @Override
            public void userActivity() {
                KeyguardViewMediator.this.userActivity();
            }
        };
        this.mDelayedLockBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if ("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD".equals(intent.getAction())) {
                    final int intExtra = intent.getIntExtra("seq", 0);
                    final StringBuilder sb = new StringBuilder();
                    sb.append("received DELAYED_KEYGUARD_ACTION with seq = ");
                    sb.append(intExtra);
                    sb.append(", mDelayedShowingSequence = ");
                    sb.append(KeyguardViewMediator.this.mDelayedShowingSequence);
                    Log.d("KeyguardViewMediator", sb.toString());
                    synchronized (KeyguardViewMediator.this) {
                        if (KeyguardViewMediator.this.mDelayedShowingSequence == intExtra) {
                            KeyguardViewMediator.this.doKeyguardLocked(null);
                        }
                        return;
                    }
                }
                if ("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_LOCK".equals(intent.getAction())) {
                    final int intExtra2 = intent.getIntExtra("seq", 0);
                    final int intExtra3 = intent.getIntExtra("android.intent.extra.USER_ID", 0);
                    if (intExtra3 != 0) {
                        synchronized (KeyguardViewMediator.this) {
                            if (KeyguardViewMediator.this.mDelayedProfileShowingSequence == intExtra2) {
                                KeyguardViewMediator.this.lockProfile(intExtra3);
                            }
                        }
                    }
                }
            }
        };
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if ("android.intent.action.ACTION_SHUTDOWN".equals(intent.getAction())) {
                    synchronized (KeyguardViewMediator.this) {
                        KeyguardViewMediator.this.mShuttingDown = true;
                    }
                }
            }
        };
        this.mHandler = new Handler(Looper.myLooper(), (Handler$Callback)null, true) {
            public void handleMessage(final Message message) {
                switch (message.what) {
                    case 18: {
                        KeyguardViewMediator.this.handleSystemReady();
                        break;
                    }
                    case 17: {
                        KeyguardViewMediator.this.handleNotifyStartedGoingToSleep();
                        break;
                    }
                    case 16: {
                        KeyguardViewMediator.this.handleNotifyScreenTurnedOff();
                        break;
                    }
                    case 15: {
                        Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_SCREEN_TURNED_ON");
                        KeyguardViewMediator.this.handleNotifyScreenTurnedOn();
                        Trace.endSection();
                        break;
                    }
                    case 14: {
                        Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_STARTED_WAKING_UP");
                        KeyguardViewMediator.this.handleNotifyStartedWakingUp();
                        Trace.endSection();
                        break;
                    }
                    case 13: {
                        Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE_PENDING_TIMEOUT");
                        Log.w("KeyguardViewMediator", "Timeout while waiting for activity drawn!");
                        Trace.endSection();
                        break;
                    }
                    case 12: {
                        Trace.beginSection("KeyguardViewMediator#handleMessage START_KEYGUARD_EXIT_ANIM");
                        final StartKeyguardExitAnimParams startKeyguardExitAnimParams = (StartKeyguardExitAnimParams)message.obj;
                        KeyguardViewMediator.this.handleStartKeyguardExitAnimation(startKeyguardExitAnimParams.startTime, startKeyguardExitAnimParams.fadeoutDuration);
                        KeyguardViewMediator.this.mFalsingManager.onSuccessfulUnlock();
                        Trace.endSection();
                        break;
                    }
                    case 11: {
                        final DismissMessage dismissMessage = (DismissMessage)message.obj;
                        KeyguardViewMediator.this.handleDismiss(dismissMessage.getCallback(), dismissMessage.getMessage());
                        break;
                    }
                    case 10: {
                        synchronized (KeyguardViewMediator.this) {
                            KeyguardViewMediator.this.doKeyguardLocked((Bundle)message.obj);
                            break;
                        }
                    }
                    case 9: {
                        Trace.beginSection("KeyguardViewMediator#handleMessage SET_OCCLUDED");
                        final KeyguardViewMediator this$0 = KeyguardViewMediator.this;
                        final int arg1 = message.arg1;
                        boolean b = true;
                        final boolean b2 = arg1 != 0;
                        if (message.arg2 == 0) {
                            b = false;
                        }
                        this$0.handleSetOccluded(b2, b);
                        Trace.endSection();
                        break;
                    }
                    case 8: {
                        Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE_DRAWING");
                        KeyguardViewMediator.this.handleKeyguardDoneDrawing();
                        Trace.endSection();
                        break;
                    }
                    case 7: {
                        Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE");
                        KeyguardViewMediator.this.handleKeyguardDone();
                        Trace.endSection();
                        break;
                    }
                    case 6: {
                        Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_SCREEN_TURNING_ON");
                        KeyguardViewMediator.this.handleNotifyScreenTurningOn((IKeyguardDrawnCallback)message.obj);
                        Trace.endSection();
                        break;
                    }
                    case 5: {
                        KeyguardViewMediator.this.handleNotifyFinishedGoingToSleep();
                        break;
                    }
                    case 4: {
                        Trace.beginSection("KeyguardViewMediator#handleMessage VERIFY_UNLOCK");
                        KeyguardViewMediator.this.handleVerifyUnlock();
                        Trace.endSection();
                        break;
                    }
                    case 3: {
                        KeyguardViewMediator.this.handleReset();
                        break;
                    }
                    case 2: {
                        KeyguardViewMediator.this.handleHide();
                        break;
                    }
                    case 1: {
                        KeyguardViewMediator.this.handleShow((Bundle)message.obj);
                        break;
                    }
                }
            }
        };
        this.mKeyguardGoingAwayRunnable = new Runnable() {
            @Override
            public void run() {
                Trace.beginSection("KeyguardViewMediator.mKeyGuardGoingAwayRunnable");
                Log.d("KeyguardViewMediator", "keyguardGoingAway");
                KeyguardViewMediator.this.mKeyguardViewControllerLazy.get().keyguardGoingAway();
                int n;
                if (!KeyguardViewMediator.this.mKeyguardViewControllerLazy.get().shouldDisableWindowAnimationsForUnlock() && (!KeyguardViewMediator.this.mWakeAndUnlocking || KeyguardViewMediator.this.mPulsing)) {
                    n = 0;
                }
                else {
                    n = 2;
                }
                int n2 = 0;
                Label_0137: {
                    if (!KeyguardViewMediator.this.mKeyguardViewControllerLazy.get().isGoingToNotificationShade()) {
                        n2 = n;
                        if (!KeyguardViewMediator.this.mWakeAndUnlocking) {
                            break Label_0137;
                        }
                        n2 = n;
                        if (!KeyguardViewMediator.this.mPulsing) {
                            break Label_0137;
                        }
                    }
                    n2 = (n | 0x1);
                }
                int n3 = n2;
                if (KeyguardViewMediator.this.mKeyguardViewControllerLazy.get().isUnlockWithWallpaper()) {
                    n3 = (n2 | 0x4);
                }
                int n4 = n3;
                if (KeyguardViewMediator.this.mKeyguardViewControllerLazy.get().shouldSubtleWindowAnimationsForUnlock()) {
                    n4 = (n3 | 0x8);
                }
                KeyguardViewMediator.this.mUpdateMonitor.setKeyguardGoingAway(true);
                KeyguardViewMediator.this.mNotificationShadeWindowController.setKeyguardGoingAway(true);
                KeyguardViewMediator.this.mUiBgExecutor.execute(new _$$Lambda$KeyguardViewMediator$7$KooXr448I10DQHquWhJeRDn4i_w(n4));
                Trace.endSection();
            }
        };
        this.mHideAnimationFinishedRunnable = new _$$Lambda$KeyguardViewMediator$QIsTwFYGBxDSDLEE3WQkiYwdaXA(this);
        this.mFalsingManager = mFalsingManager;
        this.mLockPatternUtils = mLockPatternUtils;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mNotificationShadeWindowController = mNotificationShadeWindowController;
        this.mKeyguardViewControllerLazy = mKeyguardViewControllerLazy;
        this.mDismissCallbackRegistry = mDismissCallbackRegistry;
        this.mUiBgExecutor = mUiBgExecutor;
        this.mUpdateMonitor = mUpdateMonitor;
        this.mPM = mpm;
        this.mTrustManager = mTrustManager;
        dumpManager.registerDumpable(KeyguardViewMediator.class.getName(), this);
        this.mDeviceConfig = mDeviceConfig;
        this.mShowHomeOverLockscreen = mDeviceConfig.getBoolean("systemui", "nav_bar_handle_show_over_lockscreen", true);
        final DeviceConfigProxy mDeviceConfig2 = this.mDeviceConfig;
        final Handler mHandler = this.mHandler;
        Objects.requireNonNull(mHandler);
        mDeviceConfig2.addOnPropertiesChangedListener("systemui", new _$$Lambda$LfzJt661qZfn2w_6SYHFbD3aMy0(mHandler), this.mOnPropertiesChangedListener);
        this.mInGestureNavigationMode = QuickStepContract.isGesturalMode(navigationModeController.addListener((NavigationModeController.ModeChangedListener)new _$$Lambda$KeyguardViewMediator$me7csJcL_HvRFR46jFg_Qy6MBGw(this)));
    }
    
    private void adjustStatusBarLocked() {
        this.adjustStatusBarLocked(false);
    }
    
    private void adjustStatusBarLocked(final boolean b) {
        if (this.mStatusBarManager == null) {
            this.mStatusBarManager = (StatusBarManager)super.mContext.getSystemService("statusbar");
        }
        if (this.mStatusBarManager == null) {
            Log.w("KeyguardViewMediator", "Could not get status bar manager");
        }
        else {
            int i = 0;
            final int n = 0;
            if (b || this.isShowingAndNotOccluded()) {
                int n2 = 0;
                Label_0079: {
                    if (this.mShowHomeOverLockscreen) {
                        n2 = n;
                        if (this.mInGestureNavigationMode) {
                            break Label_0079;
                        }
                    }
                    n2 = 2097152;
                }
                i = (n2 | 0x1000000);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("adjustStatusBarLocked: mShowing=");
            sb.append(this.mShowing);
            sb.append(" mOccluded=");
            sb.append(this.mOccluded);
            sb.append(" isSecure=");
            sb.append(this.isSecure());
            sb.append(" force=");
            sb.append(b);
            sb.append(" --> flags=0x");
            sb.append(Integer.toHexString(i));
            Log.d("KeyguardViewMediator", sb.toString());
            this.mStatusBarManager.disable(i);
        }
    }
    
    private void cancelDoKeyguardForChildProfilesLocked() {
        ++this.mDelayedProfileShowingSequence;
    }
    
    private void cancelDoKeyguardLaterLocked() {
        ++this.mDelayedShowingSequence;
    }
    
    private void doKeyguardForChildProfilesLocked() {
        for (final int n : UserManager.get(super.mContext).getEnabledProfileIds(UserHandle.myUserId())) {
            if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(n)) {
                this.lockProfile(n);
            }
        }
    }
    
    private void doKeyguardLaterForChildProfilesLocked() {
        for (final int n : UserManager.get(super.mContext).getEnabledProfileIds(UserHandle.myUserId())) {
            if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(n)) {
                final long lockTimeout = this.getLockTimeout(n);
                if (lockTimeout == 0L) {
                    this.doKeyguardForChildProfilesLocked();
                }
                else {
                    final long elapsedRealtime = SystemClock.elapsedRealtime();
                    final Intent intent = new Intent("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_LOCK");
                    intent.putExtra("seq", this.mDelayedProfileShowingSequence);
                    intent.putExtra("android.intent.extra.USER_ID", n);
                    intent.addFlags(268435456);
                    this.mAlarmManager.setExactAndAllowWhileIdle(2, elapsedRealtime + lockTimeout, PendingIntent.getBroadcast(super.mContext, 0, intent, 268435456));
                }
            }
        }
    }
    
    private void doKeyguardLaterLocked() {
        final long lockTimeout = this.getLockTimeout(KeyguardUpdateMonitor.getCurrentUser());
        if (lockTimeout == 0L) {
            this.doKeyguardLocked(null);
        }
        else {
            this.doKeyguardLaterLocked(lockTimeout);
        }
    }
    
    private void doKeyguardLaterLocked(final long n) {
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        final Intent intent = new Intent("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD");
        intent.putExtra("seq", this.mDelayedShowingSequence);
        intent.addFlags(268435456);
        this.mAlarmManager.setExactAndAllowWhileIdle(2, elapsedRealtime + n, PendingIntent.getBroadcast(super.mContext, 0, intent, 268435456));
        final StringBuilder sb = new StringBuilder();
        sb.append("setting alarm to turn off keyguard, seq = ");
        sb.append(this.mDelayedShowingSequence);
        Log.d("KeyguardViewMediator", sb.toString());
        this.doKeyguardLaterForChildProfilesLocked();
    }
    
    private void doKeyguardLocked(final Bundle bundle) {
        if (KeyguardUpdateMonitor.CORE_APPS_ONLY) {
            Log.d("KeyguardViewMediator", "doKeyguard: not showing because booting to cryptkeeper");
            return;
        }
        final boolean mExternallyEnabled = this.mExternallyEnabled;
        boolean b = true;
        if (!mExternallyEnabled) {
            Log.d("KeyguardViewMediator", "doKeyguard: not showing because externally disabled");
            this.mNeedToReshowWhenReenabled = true;
            return;
        }
        if (this.mKeyguardViewControllerLazy.get().isShowing()) {
            Log.d("KeyguardViewMediator", "doKeyguard: not showing because it is already showing");
            this.resetStateLocked();
            return;
        }
        if (!this.mustNotUnlockCurrentUser() || !this.mUpdateMonitor.isDeviceProvisioned()) {
            final boolean boolean1 = SystemProperties.getBoolean("keyguard.no_require_sim", false);
            final boolean validSubscriptionId = SubscriptionManager.isValidSubscriptionId(this.mUpdateMonitor.getNextSubIdForState(1));
            final boolean validSubscriptionId2 = SubscriptionManager.isValidSubscriptionId(this.mUpdateMonitor.getNextSubIdForState(7));
            final boolean b2 = this.mUpdateMonitor.isSimPinSecure() || ((validSubscriptionId || validSubscriptionId2) && (boolean1 ^ true));
            if (!b2 && this.shouldWaitForProvisioning()) {
                Log.d("KeyguardViewMediator", "doKeyguard: not showing because device isn't provisioned and the sim is not locked or missing");
                return;
            }
            if (bundle == null || !bundle.getBoolean("force_show", false)) {
                b = false;
            }
            if (this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser()) && !b2 && !b) {
                Log.d("KeyguardViewMediator", "doKeyguard: not showing because lockscreen is off");
                return;
            }
            if (this.mLockPatternUtils.checkVoldPassword(KeyguardUpdateMonitor.getCurrentUser())) {
                Log.d("KeyguardViewMediator", "Not showing lock screen since just decrypted");
                this.setShowingLocked(false);
                this.hideLocked();
                return;
            }
        }
        Log.d("KeyguardViewMediator", "doKeyguard: showing the lock screen");
        this.showLocked(bundle);
    }
    
    private long getLockTimeout(final int n) {
        final ContentResolver contentResolver = super.mContext.getContentResolver();
        long max = Settings$Secure.getInt(contentResolver, "lock_screen_lock_after_timeout", 5000);
        final long maximumTimeToLock = this.mLockPatternUtils.getDevicePolicyManager().getMaximumTimeToLock((ComponentName)null, n);
        if (maximumTimeToLock > 0L) {
            max = Math.max(Math.min(maximumTimeToLock - Math.max(Settings$System.getInt(contentResolver, "screen_off_timeout", 30000), 0L), max), 0L);
        }
        return max;
    }
    
    private void handleDismiss(final IKeyguardDismissCallback keyguardDismissCallback, final CharSequence mCustomMessage) {
        if (this.mShowing) {
            if (keyguardDismissCallback != null) {
                this.mDismissCallbackRegistry.addCallback(keyguardDismissCallback);
            }
            this.mCustomMessage = mCustomMessage;
            this.mKeyguardViewControllerLazy.get().dismissAndCollapse();
        }
        else if (keyguardDismissCallback != null) {
            new DismissCallbackWrapper(keyguardDismissCallback).notifyDismissError();
        }
    }
    
    private void handleHide() {
        Trace.beginSection("KeyguardViewMediator#handleHide");
        if (this.mAodShowing) {
            ((PowerManager)super.mContext.getSystemService((Class)PowerManager.class)).wakeUp(SystemClock.uptimeMillis(), 4, "com.android.systemui:BOUNCER_DOZING");
        }
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleHide");
            if (this.mustNotUnlockCurrentUser()) {
                Log.d("KeyguardViewMediator", "Split system user, quit unlocking.");
                return;
            }
            this.mHiding = true;
            if (this.mShowing && !this.mOccluded) {
                this.mKeyguardGoingAwayRunnable.run();
            }
            else {
                this.handleStartKeyguardExitAnimation(SystemClock.uptimeMillis() + this.mHideAnimation.getStartOffset(), this.mHideAnimation.getDuration());
            }
            Trace.endSection();
        }
    }
    
    private void handleKeyguardDone() {
        Trace.beginSection("KeyguardViewMediator#handleKeyguardDone");
        this.mUiBgExecutor.execute(new _$$Lambda$KeyguardViewMediator$b2tJFTYOy_ClYJYmXz8VxCEUdb0(this, KeyguardUpdateMonitor.getCurrentUser()));
        Log.d("KeyguardViewMediator", "handleKeyguardDone");
        synchronized (this) {
            this.resetKeyguardDonePendingLocked();
            // monitorexit(this)
            this.mUpdateMonitor.clearBiometricRecognized();
            if (this.mGoingToSleep) {
                Log.i("KeyguardViewMediator", "Device is going to sleep, aborting keyguardDone");
                return;
            }
            final IKeyguardExitCallback mExitSecureCallback = this.mExitSecureCallback;
            if (mExitSecureCallback != null) {
                try {
                    mExitSecureCallback.onKeyguardExitResult(true);
                }
                catch (RemoteException ex) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult()", (Throwable)ex);
                }
                this.mExitSecureCallback = null;
                this.mExternallyEnabled = true;
                this.mNeedToReshowWhenReenabled = false;
                this.updateInputRestricted();
            }
            this.handleHide();
            Trace.endSection();
        }
    }
    
    private void handleKeyguardDoneDrawing() {
        Trace.beginSection("KeyguardViewMediator#handleKeyguardDoneDrawing");
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleKeyguardDoneDrawing");
            if (this.mWaitingUntilKeyguardVisible) {
                Log.d("KeyguardViewMediator", "handleKeyguardDoneDrawing: notifying mWaitingUntilKeyguardVisible");
                this.mWaitingUntilKeyguardVisible = false;
                this.notifyAll();
                this.mHandler.removeMessages(8);
            }
            Trace.endSection();
        }
    }
    
    private void handleNotifyFinishedGoingToSleep() {
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleNotifyFinishedGoingToSleep");
            this.mKeyguardViewControllerLazy.get().onFinishedGoingToSleep();
        }
    }
    
    private void handleNotifyScreenTurnedOff() {
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleNotifyScreenTurnedOff");
            this.mDrawnCallback = null;
        }
    }
    
    private void handleNotifyScreenTurnedOn() {
        Trace.beginSection("KeyguardViewMediator#handleNotifyScreenTurnedOn");
        if (LatencyTracker.isEnabled(super.mContext)) {
            LatencyTracker.getInstance(super.mContext).onActionEnd(5);
        }
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleNotifyScreenTurnedOn");
            this.mKeyguardViewControllerLazy.get().onScreenTurnedOn();
            Trace.endSection();
        }
    }
    
    private void handleNotifyScreenTurningOn(final IKeyguardDrawnCallback mDrawnCallback) {
        Trace.beginSection("KeyguardViewMediator#handleNotifyScreenTurningOn");
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleNotifyScreenTurningOn");
            this.mKeyguardViewControllerLazy.get().onScreenTurningOn();
            if (mDrawnCallback != null) {
                if (this.mWakeAndUnlocking) {
                    this.mDrawnCallback = mDrawnCallback;
                }
                else {
                    this.notifyDrawn(mDrawnCallback);
                }
            }
            Trace.endSection();
        }
    }
    
    private void handleNotifyStartedGoingToSleep() {
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleNotifyStartedGoingToSleep");
            this.mKeyguardViewControllerLazy.get().onStartedGoingToSleep();
        }
    }
    
    private void handleNotifyStartedWakingUp() {
        Trace.beginSection("KeyguardViewMediator#handleMotifyStartedWakingUp");
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleNotifyWakingUp");
            this.mKeyguardViewControllerLazy.get().onStartedWakingUp();
            Trace.endSection();
        }
    }
    
    private void handleReset() {
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleReset");
            this.mKeyguardViewControllerLazy.get().reset(true);
        }
    }
    
    private void handleSetOccluded(final boolean b, final boolean b2) {
        Trace.beginSection("KeyguardViewMediator#handleSetOccluded");
        synchronized (this) {
            if (this.mHiding && b) {
                this.startKeyguardExitAnimation(0L, 0L);
            }
            if (this.mOccluded != b) {
                this.mOccluded = b;
                this.mUpdateMonitor.setKeyguardOccluded(b);
                this.mKeyguardViewControllerLazy.get().setOccluded(b, b2 && this.mDeviceInteractive);
                this.adjustStatusBarLocked();
            }
            Trace.endSection();
        }
    }
    
    private void handleShow(final Bundle bundle) {
        Trace.beginSection("KeyguardViewMediator#handleShow");
        final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        if (this.mLockPatternUtils.isSecure(currentUser)) {
            this.mLockPatternUtils.getDevicePolicyManager().reportKeyguardSecured(currentUser);
        }
        synchronized (this) {
            if (!this.mSystemReady) {
                Log.d("KeyguardViewMediator", "ignoring handleShow because system is not ready.");
                return;
            }
            Log.d("KeyguardViewMediator", "handleShow");
            this.mHiding = false;
            this.mWakeAndUnlocking = false;
            this.setShowingLocked(true);
            this.mKeyguardViewControllerLazy.get().show(bundle);
            this.resetKeyguardDonePendingLocked();
            this.mHideAnimationRun = false;
            this.adjustStatusBarLocked();
            this.userActivity();
            this.mUpdateMonitor.setKeyguardGoingAway(false);
            this.mNotificationShadeWindowController.setKeyguardGoingAway(false);
            this.mShowKeyguardWakeLock.release();
            // monitorexit(this)
            this.mKeyguardDisplayManager.show();
            this.mLockPatternUtils.scheduleNonStrongBiometricIdleTimeout(KeyguardUpdateMonitor.getCurrentUser());
            Trace.endSection();
        }
    }
    
    private void handleStartKeyguardExitAnimation(final long lng, final long lng2) {
        Trace.beginSection("KeyguardViewMediator#handleStartKeyguardExitAnimation");
        final StringBuilder sb = new StringBuilder();
        sb.append("handleStartKeyguardExitAnimation startTime=");
        sb.append(lng);
        sb.append(" fadeoutDuration=");
        sb.append(lng2);
        Log.d("KeyguardViewMediator", sb.toString());
        synchronized (this) {
            if (!this.mHiding) {
                this.setShowingLocked(this.mShowing, true);
                return;
            }
            this.mHiding = false;
            if (this.mWakeAndUnlocking && this.mDrawnCallback != null) {
                this.mKeyguardViewControllerLazy.get().getViewRootImpl().setReportNextDraw();
                this.notifyDrawn(this.mDrawnCallback);
                this.mDrawnCallback = null;
            }
            if (TelephonyManager.EXTRA_STATE_IDLE.equals(this.mPhoneState)) {
                this.playSounds(false);
            }
            this.setShowingLocked(false);
            this.mWakeAndUnlocking = false;
            this.mDismissCallbackRegistry.notifyDismissSucceeded();
            this.mKeyguardViewControllerLazy.get().hide(lng, lng2);
            this.resetKeyguardDonePendingLocked();
            this.mHideAnimationRun = false;
            this.adjustStatusBarLocked();
            this.sendUserPresentBroadcast();
            Trace.endSection();
        }
    }
    
    private void handleSystemReady() {
        synchronized (this) {
            Log.d("KeyguardViewMediator", "onSystemReady");
            this.mSystemReady = true;
            this.doKeyguardLocked(null);
            this.mUpdateMonitor.registerCallback(this.mUpdateCallback);
            // monitorexit(this)
            this.maybeSendUserPresentBroadcast();
        }
    }
    
    private void handleVerifyUnlock() {
        Trace.beginSection("KeyguardViewMediator#handleVerifyUnlock");
        synchronized (this) {
            Log.d("KeyguardViewMediator", "handleVerifyUnlock");
            this.setShowingLocked(true);
            this.mKeyguardViewControllerLazy.get().dismissAndCollapse();
            Trace.endSection();
        }
    }
    
    private void hideLocked() {
        Trace.beginSection("KeyguardViewMediator#hideLocked");
        Log.d("KeyguardViewMediator", "hideLocked");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(2));
        Trace.endSection();
    }
    
    private void lockProfile(final int n) {
        this.mTrustManager.setDeviceLockedForUser(n, true);
    }
    
    private void maybeSendUserPresentBroadcast() {
        if (this.mSystemReady && this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser())) {
            this.sendUserPresentBroadcast();
        }
        else if (this.mSystemReady && this.shouldWaitForProvisioning()) {
            this.getLockPatternUtils().userPresent(KeyguardUpdateMonitor.getCurrentUser());
        }
    }
    
    private void notifyDefaultDisplayCallbacks(final boolean b) {
        DejankUtils.whitelistIpcs(new _$$Lambda$KeyguardViewMediator$3B_VNkKi8KUJMzu6MYBDrWf_mz0(this, b));
        this.updateInputRestrictedLocked();
        this.mUiBgExecutor.execute(new _$$Lambda$KeyguardViewMediator$L7F4kyZ4mS1wNEut_ZcHqmyp_w4(this));
    }
    
    private void notifyDrawn(final IKeyguardDrawnCallback keyguardDrawnCallback) {
        Trace.beginSection("KeyguardViewMediator#notifyDrawn");
        try {
            keyguardDrawnCallback.onDrawn();
        }
        catch (RemoteException ex) {
            Slog.w("KeyguardViewMediator", "Exception calling onDrawn():", (Throwable)ex);
        }
        Trace.endSection();
    }
    
    private void notifyFinishedGoingToSleep() {
        Log.d("KeyguardViewMediator", "notifyFinishedGoingToSleep");
        this.mHandler.sendEmptyMessage(5);
    }
    
    private void notifyHasLockscreenWallpaperChanged(final boolean b) {
        for (int i = this.mKeyguardStateCallbacks.size() - 1; i >= 0; --i) {
            try {
                this.mKeyguardStateCallbacks.get(i).onHasLockscreenWallpaperChanged(b);
            }
            catch (RemoteException ex) {
                Slog.w("KeyguardViewMediator", "Failed to call onHasLockscreenWallpaperChanged", (Throwable)ex);
                if (ex instanceof DeadObjectException) {
                    this.mKeyguardStateCallbacks.remove(i);
                }
            }
        }
    }
    
    private void notifyScreenOn(final IKeyguardDrawnCallback keyguardDrawnCallback) {
        Log.d("KeyguardViewMediator", "notifyScreenOn");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6, (Object)keyguardDrawnCallback));
    }
    
    private void notifyScreenTurnedOff() {
        Log.d("KeyguardViewMediator", "notifyScreenTurnedOff");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(16));
    }
    
    private void notifyScreenTurnedOn() {
        Log.d("KeyguardViewMediator", "notifyScreenTurnedOn");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(15));
    }
    
    private void notifyStartedGoingToSleep() {
        Log.d("KeyguardViewMediator", "notifyStartedGoingToSleep");
        this.mHandler.sendEmptyMessage(17);
    }
    
    private void notifyStartedWakingUp() {
        Log.d("KeyguardViewMediator", "notifyStartedWakingUp");
        this.mHandler.sendEmptyMessage(14);
    }
    
    private void notifyTrustedChangedLocked(final boolean b) {
        for (int i = this.mKeyguardStateCallbacks.size() - 1; i >= 0; --i) {
            try {
                this.mKeyguardStateCallbacks.get(i).onTrustedChanged(b);
            }
            catch (RemoteException ex) {
                Slog.w("KeyguardViewMediator", "Failed to call notifyTrustedChangedLocked", (Throwable)ex);
                if (ex instanceof DeadObjectException) {
                    this.mKeyguardStateCallbacks.remove(i);
                }
            }
        }
    }
    
    private void playSound(final int n) {
        if (n == 0) {
            return;
        }
        if (Settings$System.getInt(super.mContext.getContentResolver(), "lockscreen_sounds_enabled", 1) == 1) {
            this.mLockSounds.stop(this.mLockSoundStreamId);
            if (this.mAudioManager == null) {
                final AudioManager mAudioManager = (AudioManager)super.mContext.getSystemService("audio");
                if ((this.mAudioManager = mAudioManager) == null) {
                    return;
                }
                this.mUiSoundsStreamType = mAudioManager.getUiSoundsStreamType();
            }
            this.mUiBgExecutor.execute(new _$$Lambda$KeyguardViewMediator$T300gxG_Qx_25Zq_PA4hcV7d_Ns(this, n));
        }
    }
    
    private void playSounds(final boolean b) {
        int n;
        if (b) {
            n = this.mLockSoundId;
        }
        else {
            n = this.mUnlockSoundId;
        }
        this.playSound(n);
    }
    
    private void playTrustedSound() {
        this.playSound(this.mTrustedSoundId);
    }
    
    private void resetKeyguardDonePendingLocked() {
        this.mKeyguardDonePending = false;
        this.mHandler.removeMessages(13);
    }
    
    private void resetStateLocked() {
        Log.e("KeyguardViewMediator", "resetStateLocked");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3));
    }
    
    private void sendUserPresentBroadcast() {
        synchronized (this) {
            if (this.mBootCompleted) {
                final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
                this.mUiBgExecutor.execute(new _$$Lambda$KeyguardViewMediator$D0hsCCxTp4AHvBc22_UzBpCE55U(this, (UserManager)super.mContext.getSystemService("user"), new UserHandle(currentUser), currentUser));
            }
            else {
                this.mBootSendUserPresent = true;
            }
        }
    }
    
    private void setShowingLocked(final boolean b) {
        this.setShowingLocked(b, false);
    }
    
    private void setShowingLocked(final boolean mShowing, final boolean b) {
        final boolean mDozing = this.mDozing;
        final boolean b2 = true;
        final boolean mAodShowing = mDozing && !this.mWakeAndUnlocking;
        int n = b2 ? 1 : 0;
        if (mShowing == this.mShowing) {
            n = (b2 ? 1 : 0);
            if (mAodShowing == this.mAodShowing) {
                if (b) {
                    n = (b2 ? 1 : 0);
                }
                else {
                    n = 0;
                }
            }
        }
        this.mShowing = mShowing;
        this.mAodShowing = mAodShowing;
        if (n != 0) {
            this.notifyDefaultDisplayCallbacks(mShowing);
            this.updateActivityLockScreenState(mShowing, mAodShowing);
        }
    }
    
    private void setupLocked() {
        final PowerManager$WakeLock wakeLock = this.mPM.newWakeLock(1, "show keyguard");
        this.mShowKeyguardWakeLock = wakeLock;
        final boolean b = false;
        wakeLock.setReferenceCounted(false);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
        this.mBroadcastDispatcher.registerReceiver(this.mBroadcastReceiver, intentFilter);
        final IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD");
        intentFilter2.addAction("com.android.internal.policy.impl.PhoneWindowManager.DELAYED_LOCK");
        super.mContext.registerReceiver(this.mDelayedLockBroadcastReceiver, intentFilter2, "com.android.systemui.permission.SELF", (Handler)null);
        this.mKeyguardDisplayManager = new KeyguardDisplayManager(super.mContext, new InjectionInflationController(SystemUIFactory.getInstance().getRootComponent()));
        this.mAlarmManager = (AlarmManager)super.mContext.getSystemService("alarm");
        KeyguardUpdateMonitor.setCurrentUser(ActivityManager.getCurrentUser());
        if (super.mContext.getResources().getBoolean(R$bool.config_enableKeyguardService)) {
            boolean b2 = b;
            if (!this.shouldWaitForProvisioning()) {
                b2 = b;
                if (!this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser())) {
                    b2 = true;
                }
            }
            this.setShowingLocked(b2, true);
        }
        else {
            this.setShowingLocked(false, true);
        }
        final ContentResolver contentResolver = super.mContext.getContentResolver();
        this.mDeviceInteractive = this.mPM.isInteractive();
        this.mLockSounds = new SoundPool$Builder().setMaxStreams(1).setAudioAttributes(new AudioAttributes$Builder().setUsage(13).setContentType(4).build()).build();
        final String string = Settings$Global.getString(contentResolver, "lock_sound");
        if (string != null) {
            this.mLockSoundId = this.mLockSounds.load(string, 1);
        }
        if (string == null || this.mLockSoundId == 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append("failed to load lock sound from ");
            sb.append(string);
            Log.w("KeyguardViewMediator", sb.toString());
        }
        final String string2 = Settings$Global.getString(contentResolver, "unlock_sound");
        if (string2 != null) {
            this.mUnlockSoundId = this.mLockSounds.load(string2, 1);
        }
        if (string2 == null || this.mUnlockSoundId == 0) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("failed to load unlock sound from ");
            sb2.append(string2);
            Log.w("KeyguardViewMediator", sb2.toString());
        }
        final String string3 = Settings$Global.getString(contentResolver, "trusted_sound");
        if (string3 != null) {
            this.mTrustedSoundId = this.mLockSounds.load(string3, 1);
        }
        if (string3 == null || this.mTrustedSoundId == 0) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("failed to load trusted sound from ");
            sb3.append(string3);
            Log.w("KeyguardViewMediator", sb3.toString());
        }
        this.mLockSoundVolume = (float)Math.pow(10.0, super.mContext.getResources().getInteger(17694823) / 20.0f);
        this.mHideAnimation = AnimationUtils.loadAnimation(super.mContext, 17432680);
        new WorkLockActivityController(super.mContext);
    }
    
    private boolean shouldWaitForProvisioning() {
        return !this.mUpdateMonitor.isDeviceProvisioned() && !this.isSecure();
    }
    
    private void showLocked(final Bundle bundle) {
        Trace.beginSection("KeyguardViewMediator#showLocked aqcuiring mShowKeyguardWakeLock");
        Log.d("KeyguardViewMediator", "showLocked");
        this.mShowKeyguardWakeLock.acquire();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1, (Object)bundle));
        Trace.endSection();
    }
    
    private void tryKeyguardDone() {
        if (!this.mKeyguardDonePending && this.mHideAnimationRun && !this.mHideAnimationRunning) {
            this.handleKeyguardDone();
        }
        else if (!this.mHideAnimationRun) {
            this.mHideAnimationRun = true;
            this.mHideAnimationRunning = true;
            this.mKeyguardViewControllerLazy.get().startPreHideAnimation(this.mHideAnimationFinishedRunnable);
        }
    }
    
    private void updateActivityLockScreenState(final boolean b, final boolean b2) {
        this.mUiBgExecutor.execute(new _$$Lambda$KeyguardViewMediator$qlYq268j2Y_mVXrIHOuE1hVcz_A(b, b2));
    }
    
    private void updateInputRestricted() {
        synchronized (this) {
            this.updateInputRestrictedLocked();
        }
    }
    
    private void updateInputRestrictedLocked() {
        final boolean inputRestricted = this.isInputRestricted();
        if (this.mInputRestricted != inputRestricted) {
            this.mInputRestricted = inputRestricted;
            for (int i = this.mKeyguardStateCallbacks.size() - 1; i >= 0; --i) {
                final IKeyguardStateCallback o = this.mKeyguardStateCallbacks.get(i);
                try {
                    o.onInputRestrictedStateChanged(inputRestricted);
                }
                catch (RemoteException ex) {
                    Slog.w("KeyguardViewMediator", "Failed to call onDeviceProvisioned", (Throwable)ex);
                    if (ex instanceof DeadObjectException) {
                        this.mKeyguardStateCallbacks.remove(o);
                    }
                }
            }
        }
    }
    
    public void addStateMonitorCallback(final IKeyguardStateCallback e) {
        synchronized (this) {
            this.mKeyguardStateCallbacks.add(e);
            try {
                e.onSimSecureStateChanged(this.mUpdateMonitor.isSimPinSecure());
                e.onShowingStateChanged(this.mShowing);
                e.onInputRestrictedStateChanged(this.mInputRestricted);
                e.onTrustedChanged(this.mUpdateMonitor.getUserHasTrust(KeyguardUpdateMonitor.getCurrentUser()));
                e.onHasLockscreenWallpaperChanged(this.mUpdateMonitor.hasLockscreenWallpaper());
            }
            catch (RemoteException ex) {
                Slog.w("KeyguardViewMediator", "Failed to call to IKeyguardStateCallback", (Throwable)ex);
            }
        }
    }
    
    public void dismiss(final IKeyguardDismissCallback keyguardDismissCallback, final CharSequence charSequence) {
        this.mHandler.obtainMessage(11, (Object)new DismissMessage(keyguardDismissCallback, charSequence)).sendToTarget();
    }
    
    public void doKeyguardTimeout(final Bundle bundle) {
        this.mHandler.removeMessages(10);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(10, (Object)bundle));
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.print("  mSystemReady: ");
        printWriter.println(this.mSystemReady);
        printWriter.print("  mBootCompleted: ");
        printWriter.println(this.mBootCompleted);
        printWriter.print("  mBootSendUserPresent: ");
        printWriter.println(this.mBootSendUserPresent);
        printWriter.print("  mExternallyEnabled: ");
        printWriter.println(this.mExternallyEnabled);
        printWriter.print("  mShuttingDown: ");
        printWriter.println(this.mShuttingDown);
        printWriter.print("  mNeedToReshowWhenReenabled: ");
        printWriter.println(this.mNeedToReshowWhenReenabled);
        printWriter.print("  mShowing: ");
        printWriter.println(this.mShowing);
        printWriter.print("  mInputRestricted: ");
        printWriter.println(this.mInputRestricted);
        printWriter.print("  mOccluded: ");
        printWriter.println(this.mOccluded);
        printWriter.print("  mDelayedShowingSequence: ");
        printWriter.println(this.mDelayedShowingSequence);
        printWriter.print("  mExitSecureCallback: ");
        printWriter.println(this.mExitSecureCallback);
        printWriter.print("  mDeviceInteractive: ");
        printWriter.println(this.mDeviceInteractive);
        printWriter.print("  mGoingToSleep: ");
        printWriter.println(this.mGoingToSleep);
        printWriter.print("  mHiding: ");
        printWriter.println(this.mHiding);
        printWriter.print("  mDozing: ");
        printWriter.println(this.mDozing);
        printWriter.print("  mAodShowing: ");
        printWriter.println(this.mAodShowing);
        printWriter.print("  mWaitingUntilKeyguardVisible: ");
        printWriter.println(this.mWaitingUntilKeyguardVisible);
        printWriter.print("  mKeyguardDonePending: ");
        printWriter.println(this.mKeyguardDonePending);
        printWriter.print("  mHideAnimationRun: ");
        printWriter.println(this.mHideAnimationRun);
        printWriter.print("  mPendingReset: ");
        printWriter.println(this.mPendingReset);
        printWriter.print("  mPendingLock: ");
        printWriter.println(this.mPendingLock);
        printWriter.print("  mWakeAndUnlocking: ");
        printWriter.println(this.mWakeAndUnlocking);
        printWriter.print("  mDrawnCallback: ");
        printWriter.println(this.mDrawnCallback);
    }
    
    public LockPatternUtils getLockPatternUtils() {
        return this.mLockPatternUtils;
    }
    
    public ViewMediatorCallback getViewMediatorCallback() {
        return this.mViewMediatorCallback;
    }
    
    public boolean isHiding() {
        return this.mHiding;
    }
    
    public boolean isInputRestricted() {
        return this.mShowing || this.mNeedToReshowWhenReenabled;
    }
    
    public boolean isSecure() {
        return this.isSecure(KeyguardUpdateMonitor.getCurrentUser());
    }
    
    public boolean isSecure(final int n) {
        return this.mLockPatternUtils.isSecure(n) || this.mUpdateMonitor.isSimPinSecure();
    }
    
    public boolean isShowingAndNotOccluded() {
        return this.mShowing && !this.mOccluded;
    }
    
    public void keyguardDone() {
        Trace.beginSection("KeyguardViewMediator#keyguardDone");
        Log.d("KeyguardViewMediator", "keyguardDone()");
        this.userActivity();
        EventLog.writeEvent(70000, 2);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(7));
        Trace.endSection();
    }
    
    boolean mustNotUnlockCurrentUser() {
        return UserManager.isSplitSystemUser() && KeyguardUpdateMonitor.getCurrentUser() == 0;
    }
    
    public void onBootCompleted() {
        synchronized (this) {
            this.mBootCompleted = true;
            if (this.mBootSendUserPresent) {
                this.sendUserPresentBroadcast();
            }
        }
    }
    
    public void onDreamingStarted() {
        this.mUpdateMonitor.dispatchDreamingStarted();
        synchronized (this) {
            if (this.mDeviceInteractive && this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser())) {
                this.doKeyguardLaterLocked();
            }
        }
    }
    
    public void onDreamingStopped() {
        this.mUpdateMonitor.dispatchDreamingStopped();
        synchronized (this) {
            if (this.mDeviceInteractive) {
                this.cancelDoKeyguardLaterLocked();
            }
        }
    }
    
    public void onFinishedGoingToSleep(final int i, final boolean b) {
        final StringBuilder sb = new StringBuilder();
        sb.append("onFinishedGoingToSleep(");
        sb.append(i);
        sb.append(")");
        Log.d("KeyguardViewMediator", sb.toString());
        synchronized (this) {
            this.mDeviceInteractive = false;
            this.mGoingToSleep = false;
            this.mWakeAndUnlocking = false;
            this.resetKeyguardDonePendingLocked();
            this.mHideAnimationRun = false;
            this.notifyFinishedGoingToSleep();
            if (b) {
                Log.i("KeyguardViewMediator", "Camera gesture was triggered, preventing Keyguard locking.");
                ((PowerManager)super.mContext.getSystemService((Class)PowerManager.class)).wakeUp(SystemClock.uptimeMillis(), 5, "com.android.systemui:CAMERA_GESTURE_PREVENT_LOCK");
                this.mPendingLock = false;
                this.mPendingReset = false;
            }
            if (this.mPendingReset) {
                this.resetStateLocked();
                this.mPendingReset = false;
            }
            if (this.mPendingLock) {
                this.doKeyguardLocked(null);
                this.mPendingLock = false;
            }
            if (!this.mLockLater && !b) {
                this.doKeyguardForChildProfilesLocked();
            }
            // monitorexit(this)
            this.mUpdateMonitor.dispatchFinishedGoingToSleep(i);
        }
    }
    
    public void onScreenTurnedOff() {
        this.notifyScreenTurnedOff();
        this.mUpdateMonitor.dispatchScreenTurnedOff();
    }
    
    public void onScreenTurnedOn() {
        Trace.beginSection("KeyguardViewMediator#onScreenTurnedOn");
        this.notifyScreenTurnedOn();
        this.mUpdateMonitor.dispatchScreenTurnedOn();
        Trace.endSection();
    }
    
    public void onScreenTurningOn(final IKeyguardDrawnCallback keyguardDrawnCallback) {
        Trace.beginSection("KeyguardViewMediator#onScreenTurningOn");
        this.notifyScreenOn(keyguardDrawnCallback);
        Trace.endSection();
    }
    
    public void onShortPowerPressedGoHome() {
    }
    
    public void onStartedGoingToSleep(final int i) {
        final StringBuilder sb = new StringBuilder();
        sb.append("onStartedGoingToSleep(");
        sb.append(i);
        sb.append(")");
        Log.d("KeyguardViewMediator", sb.toString());
        synchronized (this) {
            this.mDeviceInteractive = false;
            this.mGoingToSleep = true;
            this.mUpdateMonitor.setKeyguardGoingAway(false);
            final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            final boolean b = this.mLockPatternUtils.getPowerButtonInstantlyLocks(currentUser) || !this.mLockPatternUtils.isSecure(currentUser);
            final long lockTimeout = this.getLockTimeout(KeyguardUpdateMonitor.getCurrentUser());
            this.mLockLater = false;
            if (this.mExitSecureCallback != null) {
                Log.d("KeyguardViewMediator", "pending exit secure callback cancelled");
                try {
                    this.mExitSecureCallback.onKeyguardExitResult(false);
                }
                catch (RemoteException ex) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", (Throwable)ex);
                }
                this.mExitSecureCallback = null;
                if (!this.mExternallyEnabled) {
                    this.hideLocked();
                }
            }
            else if (this.mShowing) {
                this.mPendingReset = true;
            }
            else if ((i == 3 && lockTimeout > 0L) || (i == 2 && !b)) {
                this.doKeyguardLaterLocked(lockTimeout);
                this.mLockLater = true;
            }
            else if (!this.mLockPatternUtils.isLockScreenDisabled(currentUser)) {
                this.mPendingLock = true;
            }
            if (this.mPendingLock) {
                this.playSounds(true);
            }
            // monitorexit(this)
            this.mUpdateMonitor.dispatchStartedGoingToSleep(i);
            this.notifyStartedGoingToSleep();
        }
    }
    
    public void onStartedWakingUp() {
        Trace.beginSection("KeyguardViewMediator#onStartedWakingUp");
        synchronized (this) {
            this.mDeviceInteractive = true;
            this.cancelDoKeyguardLaterLocked();
            this.cancelDoKeyguardForChildProfilesLocked();
            final StringBuilder sb = new StringBuilder();
            sb.append("onStartedWakingUp, seq = ");
            sb.append(this.mDelayedShowingSequence);
            Log.d("KeyguardViewMediator", sb.toString());
            this.notifyStartedWakingUp();
            // monitorexit(this)
            this.mUpdateMonitor.dispatchStartedWakingUp();
            this.maybeSendUserPresentBroadcast();
            Trace.endSection();
        }
    }
    
    public void onSystemReady() {
        this.mHandler.obtainMessage(18).sendToTarget();
    }
    
    public void onWakeAndUnlocking() {
        Trace.beginSection("KeyguardViewMediator#onWakeAndUnlocking");
        this.mWakeAndUnlocking = true;
        this.keyguardDone();
        Trace.endSection();
    }
    
    public void setCurrentUser(final int currentUser) {
        KeyguardUpdateMonitor.setCurrentUser(currentUser);
        synchronized (this) {
            this.notifyTrustedChangedLocked(this.mUpdateMonitor.getUserHasTrust(currentUser));
        }
    }
    
    public void setDozing(final boolean mDozing) {
        if (mDozing == this.mDozing) {
            return;
        }
        this.mDozing = mDozing;
        this.setShowingLocked(this.mShowing);
    }
    
    public void setKeyguardEnabled(final boolean b) {
        synchronized (this) {
            final StringBuilder sb = new StringBuilder();
            sb.append("setKeyguardEnabled(");
            sb.append(b);
            sb.append(")");
            Log.d("KeyguardViewMediator", sb.toString());
            this.mExternallyEnabled = b;
            if (!b && this.mShowing) {
                if (this.mExitSecureCallback != null) {
                    Log.d("KeyguardViewMediator", "in process of verifyUnlock request, ignoring");
                    return;
                }
                Log.d("KeyguardViewMediator", "remembering to reshow, hiding keyguard, disabling status bar expansion");
                this.mNeedToReshowWhenReenabled = true;
                this.updateInputRestrictedLocked();
                this.hideLocked();
            }
            else if (b && this.mNeedToReshowWhenReenabled) {
                Log.d("KeyguardViewMediator", "previously hidden, reshowing, reenabling status bar expansion");
                this.mNeedToReshowWhenReenabled = false;
                this.updateInputRestrictedLocked();
                if (this.mExitSecureCallback != null) {
                    Log.d("KeyguardViewMediator", "onKeyguardExitResult(false), resetting");
                    try {
                        this.mExitSecureCallback.onKeyguardExitResult(false);
                    }
                    catch (RemoteException ex) {
                        Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", (Throwable)ex);
                    }
                    this.mExitSecureCallback = null;
                    this.resetStateLocked();
                }
                else {
                    this.showLocked(null);
                    this.mWaitingUntilKeyguardVisible = true;
                    this.mHandler.sendEmptyMessageDelayed(8, 2000L);
                    Log.d("KeyguardViewMediator", "waiting until mWaitingUntilKeyguardVisible is false");
                    while (this.mWaitingUntilKeyguardVisible) {
                        try {
                            this.wait();
                        }
                        catch (InterruptedException ex2) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    Log.d("KeyguardViewMediator", "done waiting for mWaitingUntilKeyguardVisible");
                }
            }
        }
    }
    
    public void setOccluded(final boolean b, final boolean b2) {
        Trace.beginSection("KeyguardViewMediator#setOccluded");
        final StringBuilder sb = new StringBuilder();
        sb.append("setOccluded ");
        sb.append(b);
        Log.d("KeyguardViewMediator", sb.toString());
        this.mHandler.removeMessages(9);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(9, (int)(b ? 1 : 0), (int)(b2 ? 1 : 0)));
        Trace.endSection();
    }
    
    public void setPulsing(final boolean mPulsing) {
        this.mPulsing = mPulsing;
    }
    
    public void setSwitchingUser(final boolean switchingUser) {
        this.mUpdateMonitor.setSwitchingUser(switchingUser);
    }
    
    @Override
    public void start() {
        synchronized (this) {
            this.setupLocked();
        }
    }
    
    public void startKeyguardExitAnimation(final long n, final long n2) {
        Trace.beginSection("KeyguardViewMediator#startKeyguardExitAnimation");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(12, (Object)new StartKeyguardExitAnimParams(n, n2)));
        Trace.endSection();
    }
    
    public void userActivity() {
        this.mPM.userActivity(SystemClock.uptimeMillis(), false);
    }
    
    public void verifyUnlock(final IKeyguardExitCallback keyguardExitCallback) {
        Trace.beginSection("KeyguardViewMediator#verifyUnlock");
        synchronized (this) {
            Log.d("KeyguardViewMediator", "verifyUnlock");
            if (this.shouldWaitForProvisioning()) {
                Log.d("KeyguardViewMediator", "ignoring because device isn't provisioned");
                try {
                    keyguardExitCallback.onKeyguardExitResult(false);
                }
                catch (RemoteException ex) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", (Throwable)ex);
                }
            }
            else if (this.mExternallyEnabled) {
                Log.w("KeyguardViewMediator", "verifyUnlock called when not externally disabled");
                try {
                    keyguardExitCallback.onKeyguardExitResult(false);
                }
                catch (RemoteException ex2) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", (Throwable)ex2);
                }
            }
            else if (this.mExitSecureCallback != null) {
                try {
                    keyguardExitCallback.onKeyguardExitResult(false);
                }
                catch (RemoteException ex3) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", (Throwable)ex3);
                }
            }
            else if (!this.isSecure()) {
                this.mExternallyEnabled = true;
                this.mNeedToReshowWhenReenabled = false;
                this.updateInputRestricted();
                try {
                    keyguardExitCallback.onKeyguardExitResult(true);
                }
                catch (RemoteException ex4) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", (Throwable)ex4);
                }
            }
            else {
                try {
                    keyguardExitCallback.onKeyguardExitResult(false);
                }
                catch (RemoteException ex5) {
                    Slog.w("KeyguardViewMediator", "Failed to call onKeyguardExitResult(false)", (Throwable)ex5);
                }
            }
            Trace.endSection();
        }
    }
    
    private static class DismissMessage
    {
        private final IKeyguardDismissCallback mCallback;
        private final CharSequence mMessage;
        
        DismissMessage(final IKeyguardDismissCallback mCallback, final CharSequence mMessage) {
            this.mCallback = mCallback;
            this.mMessage = mMessage;
        }
        
        public IKeyguardDismissCallback getCallback() {
            return this.mCallback;
        }
        
        public CharSequence getMessage() {
            return this.mMessage;
        }
    }
    
    private static class StartKeyguardExitAnimParams
    {
        long fadeoutDuration;
        long startTime;
        
        private StartKeyguardExitAnimParams(final long startTime, final long fadeoutDuration) {
            this.startTime = startTime;
            this.fadeoutDuration = fadeoutDuration;
        }
    }
}
