// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.metrics.LogMaker;
import com.android.internal.util.LatencyTracker;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.Trace;
import android.os.SystemClock;
import android.hardware.biometrics.BiometricSourceType;
import com.android.systemui.Dependency;
import android.util.Log;
import com.android.systemui.dump.DumpManager;
import android.content.res.Resources;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import android.os.PowerManager$WakeLock;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.keyguard.ScreenLifecycle;
import android.os.PowerManager;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.os.Handler;
import android.content.Context;
import com.android.systemui.Dumpable;
import com.android.keyguard.KeyguardUpdateMonitorCallback;

public class BiometricUnlockController extends KeyguardUpdateMonitorCallback implements Dumpable
{
    private final Context mContext;
    private final DozeParameters mDozeParameters;
    private DozeScrimController mDozeScrimController;
    private boolean mFadedAwayAfterWakeAndUnlock;
    private final Handler mHandler;
    private boolean mHasScreenTurnedOnSinceAuthenticating;
    private final KeyguardBypassController mKeyguardBypassController;
    private final KeyguardStateController mKeyguardStateController;
    private KeyguardViewMediator mKeyguardViewMediator;
    private final NotificationMediaManager mMediaManager;
    private final MetricsLogger mMetricsLogger;
    private int mMode;
    private final NotificationShadeWindowController mNotificationShadeWindowController;
    private PendingAuthenticated mPendingAuthenticated;
    private boolean mPendingShowBouncer;
    private final PowerManager mPowerManager;
    private final Runnable mReleaseBiometricWakeLockRunnable;
    private final ScreenLifecycle.Observer mScreenObserver;
    private ScrimController mScrimController;
    private final ShadeController mShadeController;
    private StatusBar mStatusBar;
    private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final KeyguardUpdateMonitor mUpdateMonitor;
    private PowerManager$WakeLock mWakeLock;
    private final int mWakeUpDelay;
    @VisibleForTesting
    final WakefulnessLifecycle.Observer mWakefulnessObserver;
    
    public BiometricUnlockController(final Context mContext, final DozeScrimController mDozeScrimController, final KeyguardViewMediator mKeyguardViewMediator, final ScrimController mScrimController, final StatusBar mStatusBar, final ShadeController mShadeController, final NotificationShadeWindowController mNotificationShadeWindowController, final KeyguardStateController mKeyguardStateController, final Handler mHandler, final KeyguardUpdateMonitor mUpdateMonitor, final Resources resources, final KeyguardBypassController mKeyguardBypassController, final DozeParameters mDozeParameters, final MetricsLogger mMetricsLogger, final DumpManager dumpManager) {
        this.mPendingAuthenticated = null;
        this.mReleaseBiometricWakeLockRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i("BiometricUnlockCtrl", "biometric wakelock: TIMEOUT!!");
                BiometricUnlockController.this.releaseBiometricWakeLock();
            }
        };
        this.mWakefulnessObserver = new WakefulnessLifecycle.Observer() {
            @Override
            public void onFinishedWakingUp() {
                if (BiometricUnlockController.this.mPendingShowBouncer) {
                    BiometricUnlockController.this.showBouncer();
                }
            }
        };
        this.mScreenObserver = new ScreenLifecycle.Observer() {
            @Override
            public void onScreenTurnedOn() {
                BiometricUnlockController.this.mHasScreenTurnedOnSinceAuthenticating = true;
            }
        };
        this.mContext = mContext;
        this.mPowerManager = (PowerManager)mContext.getSystemService((Class)PowerManager.class);
        this.mShadeController = mShadeController;
        this.mUpdateMonitor = mUpdateMonitor;
        this.mDozeParameters = mDozeParameters;
        mUpdateMonitor.registerCallback(this);
        this.mMediaManager = Dependency.get(NotificationMediaManager.class);
        Dependency.get(WakefulnessLifecycle.class).addObserver(this.mWakefulnessObserver);
        Dependency.get(ScreenLifecycle.class).addObserver(this.mScreenObserver);
        this.mNotificationShadeWindowController = mNotificationShadeWindowController;
        this.mDozeScrimController = mDozeScrimController;
        this.mKeyguardViewMediator = mKeyguardViewMediator;
        this.mScrimController = mScrimController;
        this.mStatusBar = mStatusBar;
        this.mKeyguardStateController = mKeyguardStateController;
        this.mHandler = mHandler;
        this.mWakeUpDelay = resources.getInteger(17694918);
        (this.mKeyguardBypassController = mKeyguardBypassController).setUnlockController(this);
        this.mMetricsLogger = mMetricsLogger;
        dumpManager.registerDumpable(BiometricUnlockController.class.getName(), this);
    }
    
    private int calculateMode(final BiometricSourceType biometricSourceType, final boolean b) {
        if (biometricSourceType != BiometricSourceType.FACE && biometricSourceType != BiometricSourceType.IRIS) {
            return this.calculateModeForFingerprint(b);
        }
        return this.calculateModeForPassiveAuth(b);
    }
    
    private int calculateModeForFingerprint(final boolean b) {
        final boolean unlockingWithBiometricAllowed = this.mUpdateMonitor.isUnlockingWithBiometricAllowed(b);
        final boolean dreaming = this.mUpdateMonitor.isDreaming();
        if (!this.mUpdateMonitor.isDeviceInteractive()) {
            if (!this.mStatusBarKeyguardViewManager.isShowing()) {
                return 4;
            }
            if (this.mDozeScrimController.isPulsing() && unlockingWithBiometricAllowed) {
                return 2;
            }
            if (!unlockingWithBiometricAllowed && this.mKeyguardStateController.isMethodSecure()) {
                return 3;
            }
            return 1;
        }
        else {
            if (unlockingWithBiometricAllowed && dreaming) {
                return 6;
            }
            if (this.mStatusBarKeyguardViewManager.isShowing()) {
                if (this.mStatusBarKeyguardViewManager.bouncerIsOrWillBeShowing() && unlockingWithBiometricAllowed) {
                    return 8;
                }
                if (unlockingWithBiometricAllowed) {
                    return 5;
                }
                if (!this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                    return 3;
                }
            }
            return 0;
        }
    }
    
    private int calculateModeForPassiveAuth(final boolean b) {
        final boolean unlockingWithBiometricAllowed = this.mUpdateMonitor.isUnlockingWithBiometricAllowed(b);
        final boolean dreaming = this.mUpdateMonitor.isDreaming();
        final boolean bypassEnabled = this.mKeyguardBypassController.getBypassEnabled();
        final boolean deviceInteractive = this.mUpdateMonitor.isDeviceInteractive();
        int n = 3;
        int n2 = 4;
        final int n3 = 0;
        if (!deviceInteractive) {
            if (!this.mStatusBarKeyguardViewManager.isShowing()) {
                if (bypassEnabled) {
                    n2 = 1;
                }
                return n2;
            }
            if (!unlockingWithBiometricAllowed) {
                if (!bypassEnabled) {
                    n = 0;
                }
                return n;
            }
            if (this.mDozeScrimController.isPulsing()) {
                if (bypassEnabled) {
                    n2 = 2;
                }
                return n2;
            }
            if (bypassEnabled) {
                return 2;
            }
            return 4;
        }
        else {
            if (unlockingWithBiometricAllowed && dreaming) {
                if (bypassEnabled) {
                    n2 = 6;
                }
                return n2;
            }
            if (!this.mStatusBarKeyguardViewManager.isShowing()) {
                return 0;
            }
            if (this.mStatusBarKeyguardViewManager.bouncerIsOrWillBeShowing() && unlockingWithBiometricAllowed) {
                if (bypassEnabled && this.mKeyguardBypassController.canPlaySubtleWindowAnimations()) {
                    return 7;
                }
                return 8;
            }
            else {
                if (unlockingWithBiometricAllowed) {
                    int n4 = n3;
                    if (bypassEnabled) {
                        n4 = 7;
                    }
                    return n4;
                }
                if (!bypassEnabled) {
                    n = 0;
                }
                return n;
            }
        }
    }
    
    private void cleanup() {
        this.releaseBiometricWakeLock();
    }
    
    private boolean pulsingOrAod() {
        final ScrimState state = this.mScrimController.getState();
        return state == ScrimState.AOD || state == ScrimState.PULSING;
    }
    
    private void releaseBiometricWakeLock() {
        if (this.mWakeLock != null) {
            this.mHandler.removeCallbacks(this.mReleaseBiometricWakeLockRunnable);
            Log.i("BiometricUnlockCtrl", "releasing biometric wakelock");
            this.mWakeLock.release();
            this.mWakeLock = null;
        }
    }
    
    private void resetMode() {
        this.mMode = 0;
        this.mNotificationShadeWindowController.setForceDozeBrightness(false);
        if (this.mStatusBar.getNavigationBarView() != null) {
            this.mStatusBar.getNavigationBarView().setWakeAndUnlocking(false);
        }
        this.mStatusBar.notifyBiometricAuthModeChanged();
    }
    
    private void showBouncer() {
        if (this.mMode == 3) {
            this.mStatusBarKeyguardViewManager.showBouncer(false);
        }
        this.mShadeController.animateCollapsePanels(0, true, false, 1.1f);
        this.mPendingShowBouncer = false;
    }
    
    private int toSubtype(final BiometricSourceType biometricSourceType) {
        final int n = BiometricUnlockController$5.$SwitchMap$android$hardware$biometrics$BiometricSourceType[biometricSourceType.ordinal()];
        if (n == 1) {
            return 0;
        }
        if (n == 2) {
            return 1;
        }
        if (n != 3) {
            return 3;
        }
        return 2;
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println(" BiometricUnlockController:");
        printWriter.print("   mMode=");
        printWriter.println(this.mMode);
        printWriter.print("   mWakeLock=");
        printWriter.println(this.mWakeLock);
    }
    
    public void finishKeyguardFadingAway() {
        if (this.isWakeAndUnlock()) {
            this.mFadedAwayAfterWakeAndUnlock = true;
        }
        this.resetMode();
    }
    
    public int getMode() {
        return this.mMode;
    }
    
    public boolean hasPendingAuthentication() {
        final PendingAuthenticated mPendingAuthenticated = this.mPendingAuthenticated;
        return mPendingAuthenticated != null && this.mUpdateMonitor.isUnlockingWithBiometricAllowed(mPendingAuthenticated.isStrongBiometric) && this.mPendingAuthenticated.userId == KeyguardUpdateMonitor.getCurrentUser();
    }
    
    public boolean isBiometricUnlock() {
        if (!this.isWakeAndUnlock()) {
            final int mMode = this.mMode;
            if (mMode != 5) {
                if (mMode != 7) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isWakeAndUnlock() {
        final int mMode = this.mMode;
        boolean b2;
        final boolean b = b2 = true;
        if (mMode != 1) {
            b2 = b;
            if (mMode != 2) {
                b2 = (mMode == 6 && b);
            }
        }
        return b2;
    }
    
    @Override
    public void onBiometricAcquired(final BiometricSourceType biometricSourceType) {
        Trace.beginSection("BiometricUnlockController#onBiometricAcquired");
        this.releaseBiometricWakeLock();
        if (!this.mUpdateMonitor.isDeviceInteractive()) {
            if (LatencyTracker.isEnabled(this.mContext)) {
                int n = 2;
                if (biometricSourceType == BiometricSourceType.FACE) {
                    n = 6;
                }
                LatencyTracker.getInstance(this.mContext).onActionStart(n);
            }
            this.mWakeLock = this.mPowerManager.newWakeLock(1, "wake-and-unlock:wakelock");
            Trace.beginSection("acquiring wake-and-unlock");
            this.mWakeLock.acquire();
            Trace.endSection();
            Log.i("BiometricUnlockCtrl", "biometric acquired, grabbing biometric wakelock");
            this.mHandler.postDelayed(this.mReleaseBiometricWakeLockRunnable, 15000L);
        }
        Trace.endSection();
    }
    
    @Override
    public void onBiometricAuthFailed(final BiometricSourceType biometricSourceType) {
        this.mMetricsLogger.write(new LogMaker(1697).setType(11).setSubtype(this.toSubtype(biometricSourceType)));
        this.cleanup();
    }
    
    @Override
    public void onBiometricAuthenticated(final int n, final BiometricSourceType biometricSourceType, final boolean b) {
        Trace.beginSection("BiometricUnlockController#onBiometricAuthenticated");
        if (this.mUpdateMonitor.isGoingToSleep()) {
            this.mPendingAuthenticated = new PendingAuthenticated(n, biometricSourceType, b);
            Trace.endSection();
            return;
        }
        this.mMetricsLogger.write(new LogMaker(1697).setType(10).setSubtype(this.toSubtype(biometricSourceType)));
        if (this.mKeyguardBypassController.onBiometricAuthenticated(biometricSourceType, b)) {
            this.mKeyguardViewMediator.userActivity();
            this.startWakeAndUnlock(biometricSourceType, b);
        }
        else {
            Log.d("BiometricUnlockCtrl", "onBiometricAuthenticated aborted by bypass controller");
        }
    }
    
    @Override
    public void onBiometricError(final int i, final String s, final BiometricSourceType biometricSourceType) {
        this.mMetricsLogger.write(new LogMaker(1697).setType(15).setSubtype(this.toSubtype(biometricSourceType)).addTaggedData(1741, (Object)i));
        this.cleanup();
    }
    
    @Override
    public void onFinishedGoingToSleep(final int n) {
        Trace.beginSection("BiometricUnlockController#onFinishedGoingToSleep");
        final PendingAuthenticated mPendingAuthenticated = this.mPendingAuthenticated;
        if (mPendingAuthenticated != null) {
            this.mHandler.post((Runnable)new _$$Lambda$BiometricUnlockController$WXzEzz1fr3GrmjWXzyYSNPAnvmA(this, mPendingAuthenticated));
            this.mPendingAuthenticated = null;
        }
        Trace.endSection();
    }
    
    @Override
    public void onStartedGoingToSleep(final int n) {
        this.resetMode();
        this.mFadedAwayAfterWakeAndUnlock = false;
        this.mPendingAuthenticated = null;
    }
    
    public void setStatusBarKeyguardViewManager(final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager) {
        this.mStatusBarKeyguardViewManager = mStatusBarKeyguardViewManager;
    }
    
    public void startKeyguardFadingAway() {
        this.mHandler.postDelayed((Runnable)new Runnable() {
            @Override
            public void run() {
                BiometricUnlockController.this.mNotificationShadeWindowController.setForceDozeBrightness(false);
            }
        }, 96L);
    }
    
    public void startWakeAndUnlock(int mMode) {
        final StringBuilder sb = new StringBuilder();
        sb.append("startWakeAndUnlock(");
        sb.append(mMode);
        sb.append(")");
        Log.v("BiometricUnlockCtrl", sb.toString());
        final boolean deviceInteractive = this.mUpdateMonitor.isDeviceInteractive();
        this.mMode = mMode;
        if (mMode == 2 && this.pulsingOrAod()) {
            this.mNotificationShadeWindowController.setForceDozeBrightness(true);
        }
        final boolean alwaysOn = this.mDozeParameters.getAlwaysOn();
        final boolean b = mMode == 1 && alwaysOn && this.mWakeUpDelay > 0;
        final _$$Lambda$BiometricUnlockController$eARUOiIHQidy4dPvrf3UVu6gsv0 $$Lambda$BiometricUnlockController$eARUOiIHQidy4dPvrf3UVu6gsv0 = new _$$Lambda$BiometricUnlockController$eARUOiIHQidy4dPvrf3UVu6gsv0(this, deviceInteractive, b);
        if (!b && this.mMode != 0) {
            $$Lambda$BiometricUnlockController$eARUOiIHQidy4dPvrf3UVu6gsv0.run();
        }
        mMode = this.mMode;
        switch (mMode) {
            case 7:
            case 8: {
                Trace.beginSection("MODE_DISMISS_BOUNCER or MODE_UNLOCK_FADING");
                this.mStatusBarKeyguardViewManager.notifyKeyguardAuthenticated(false);
                Trace.endSection();
                break;
            }
            case 3:
            case 5: {
                Trace.beginSection("MODE_UNLOCK_COLLAPSING or MODE_SHOW_BOUNCER");
                if (!deviceInteractive) {
                    this.mPendingShowBouncer = true;
                }
                else {
                    this.showBouncer();
                }
                Trace.endSection();
                break;
            }
            case 1:
            case 2:
            case 6: {
                if (mMode == 2) {
                    Trace.beginSection("MODE_WAKE_AND_UNLOCK_PULSING");
                    this.mMediaManager.updateMediaMetaData(false, true);
                }
                else if (mMode == 1) {
                    Trace.beginSection("MODE_WAKE_AND_UNLOCK");
                }
                else {
                    Trace.beginSection("MODE_WAKE_AND_UNLOCK_FROM_DREAM");
                    this.mUpdateMonitor.awakenFromDream();
                }
                this.mNotificationShadeWindowController.setNotificationShadeFocusable(false);
                if (b) {
                    this.mHandler.postDelayed((Runnable)$$Lambda$BiometricUnlockController$eARUOiIHQidy4dPvrf3UVu6gsv0, (long)this.mWakeUpDelay);
                }
                else {
                    this.mKeyguardViewMediator.onWakeAndUnlocking();
                }
                if (this.mStatusBar.getNavigationBarView() != null) {
                    this.mStatusBar.getNavigationBarView().setWakeAndUnlocking(true);
                }
                Trace.endSection();
                break;
            }
        }
        this.mStatusBar.notifyBiometricAuthModeChanged();
        Trace.endSection();
    }
    
    public void startWakeAndUnlock(final BiometricSourceType biometricSourceType, final boolean b) {
        this.startWakeAndUnlock(this.calculateMode(biometricSourceType, b));
    }
    
    public boolean unlockedByWakeAndUnlock() {
        return this.isWakeAndUnlock() || this.mFadedAwayAfterWakeAndUnlock;
    }
    
    private static final class PendingAuthenticated
    {
        public final BiometricSourceType biometricSourceType;
        public final boolean isStrongBiometric;
        public final int userId;
        
        PendingAuthenticated(final int userId, final BiometricSourceType biometricSourceType, final boolean isStrongBiometric) {
            this.userId = userId;
            this.biometricSourceType = biometricSourceType;
            this.isStrongBiometric = isStrongBiometric;
        }
    }
}
