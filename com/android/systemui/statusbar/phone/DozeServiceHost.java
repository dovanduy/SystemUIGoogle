// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.os.Bundle;
import android.view.MotionEvent;
import android.os.SystemClock;
import android.util.Log;
import java.util.Iterator;
import com.android.systemui.doze.DozeReceiver;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.os.SystemProperties;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.PulseExpansionHandler;
import android.os.PowerManager;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import java.util.ArrayList;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.assist.AssistManager;
import dagger.Lazy;
import android.view.View;
import com.android.systemui.doze.DozeHost;

public final class DozeServiceHost implements DozeHost
{
    private View mAmbientIndicationContainer;
    private boolean mAnimateScreenOff;
    private boolean mAnimateWakeup;
    private final Lazy<AssistManager> mAssistManagerLazy;
    private final BatteryController mBatteryController;
    private BiometricUnlockController mBiometricUnlockController;
    private final Lazy<BiometricUnlockController> mBiometricUnlockControllerLazy;
    private final ArrayList<Callback> mCallbacks;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private final DozeLog mDozeLog;
    private final DozeScrimController mDozeScrimController;
    private boolean mDozingRequested;
    private final HeadsUpManagerPhone mHeadsUpManagerPhone;
    private boolean mIgnoreTouchWhilePulsing;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final KeyguardViewMediator mKeyguardViewMediator;
    private final LockscreenLockIconController mLockscreenLockIconController;
    private NotificationIconAreaController mNotificationIconAreaController;
    private NotificationPanelViewController mNotificationPanel;
    private final NotificationShadeWindowController mNotificationShadeWindowController;
    private NotificationShadeWindowViewController mNotificationShadeWindowViewController;
    private final NotificationWakeUpCoordinator mNotificationWakeUpCoordinator;
    private Runnable mPendingScreenOffCallback;
    private final PowerManager mPowerManager;
    private final PulseExpansionHandler mPulseExpansionHandler;
    private boolean mPulsing;
    private final ScrimController mScrimController;
    private StatusBar mStatusBar;
    private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final SysuiStatusBarStateController mStatusBarStateController;
    private boolean mSuppressed;
    private final VisualStabilityManager mVisualStabilityManager;
    @VisibleForTesting
    boolean mWakeLockScreenPerformsAuth;
    private WakefulnessLifecycle mWakefulnessLifecycle;
    
    public DozeServiceHost(final DozeLog mDozeLog, final PowerManager mPowerManager, final WakefulnessLifecycle mWakefulnessLifecycle, final SysuiStatusBarStateController mStatusBarStateController, final DeviceProvisionedController mDeviceProvisionedController, final HeadsUpManagerPhone mHeadsUpManagerPhone, final BatteryController mBatteryController, final ScrimController mScrimController, final Lazy<BiometricUnlockController> mBiometricUnlockControllerLazy, final KeyguardViewMediator mKeyguardViewMediator, final Lazy<AssistManager> mAssistManagerLazy, final DozeScrimController mDozeScrimController, final KeyguardUpdateMonitor mKeyguardUpdateMonitor, final VisualStabilityManager mVisualStabilityManager, final PulseExpansionHandler mPulseExpansionHandler, final NotificationShadeWindowController mNotificationShadeWindowController, final NotificationWakeUpCoordinator mNotificationWakeUpCoordinator, final LockscreenLockIconController mLockscreenLockIconController) {
        this.mCallbacks = new ArrayList<Callback>();
        this.mWakeLockScreenPerformsAuth = SystemProperties.getBoolean("persist.sysui.wake_performs_auth", true);
        this.mDozeLog = mDozeLog;
        this.mPowerManager = mPowerManager;
        this.mWakefulnessLifecycle = mWakefulnessLifecycle;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mDeviceProvisionedController = mDeviceProvisionedController;
        this.mHeadsUpManagerPhone = mHeadsUpManagerPhone;
        this.mBatteryController = mBatteryController;
        this.mScrimController = mScrimController;
        this.mBiometricUnlockControllerLazy = mBiometricUnlockControllerLazy;
        this.mKeyguardViewMediator = mKeyguardViewMediator;
        this.mAssistManagerLazy = mAssistManagerLazy;
        this.mDozeScrimController = mDozeScrimController;
        this.mKeyguardUpdateMonitor = mKeyguardUpdateMonitor;
        this.mVisualStabilityManager = mVisualStabilityManager;
        this.mPulseExpansionHandler = mPulseExpansionHandler;
        this.mNotificationShadeWindowController = mNotificationShadeWindowController;
        this.mNotificationWakeUpCoordinator = mNotificationWakeUpCoordinator;
        this.mLockscreenLockIconController = mLockscreenLockIconController;
    }
    
    @Override
    public void addCallback(final Callback e) {
        this.mCallbacks.add(e);
    }
    
    @Override
    public void cancelGentleSleep() {
        this.mPendingScreenOffCallback = null;
        if (this.mScrimController.getState() == ScrimState.OFF) {
            this.mStatusBar.updateScrimController();
        }
    }
    
    @Override
    public void dozeTimeTick() {
        this.mNotificationPanel.dozeTimeTick();
        final View mAmbientIndicationContainer = this.mAmbientIndicationContainer;
        if (mAmbientIndicationContainer instanceof DozeReceiver) {
            ((DozeReceiver)mAmbientIndicationContainer).dozeTimeTick();
        }
    }
    
    void executePendingScreenOffCallback() {
        final Runnable mPendingScreenOffCallback = this.mPendingScreenOffCallback;
        if (mPendingScreenOffCallback == null) {
            return;
        }
        mPendingScreenOffCallback.run();
        this.mPendingScreenOffCallback = null;
    }
    
    @Override
    public void extendPulse(final int n) {
        if (n == 8) {
            this.mScrimController.setWakeLockScreenSensorActive(true);
        }
        if (this.mDozeScrimController.isPulsing() && this.mHeadsUpManagerPhone.hasNotifications()) {
            this.mHeadsUpManagerPhone.extendHeadsUp();
        }
        else {
            this.mDozeScrimController.extendPulse();
        }
    }
    
    void fireNotificationPulse(final NotificationEntry notificationEntry) {
        final _$$Lambda$DozeServiceHost$Xc4SX99X8IZoMaU0MD3jJJv7A3I $$Lambda$DozeServiceHost$Xc4SX99X8IZoMaU0MD3jJJv7A3I = new _$$Lambda$DozeServiceHost$Xc4SX99X8IZoMaU0MD3jJJv7A3I(this, notificationEntry);
        final Iterator<Callback> iterator = this.mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onNotificationAlerted($$Lambda$DozeServiceHost$Xc4SX99X8IZoMaU0MD3jJJv7A3I);
        }
    }
    
    void firePowerSaveChanged(final boolean b) {
        final Iterator<Callback> iterator = this.mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onPowerSaveChanged(b);
        }
    }
    
    boolean getDozingRequested() {
        return this.mDozingRequested;
    }
    
    boolean getIgnoreTouchWhilePulsing() {
        return this.mIgnoreTouchWhilePulsing;
    }
    
    boolean hasPendingScreenOffCallback() {
        return this.mPendingScreenOffCallback != null;
    }
    
    public void initialize(final StatusBar mStatusBar, final NotificationIconAreaController mNotificationIconAreaController, final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager, final NotificationShadeWindowViewController mNotificationShadeWindowViewController, final NotificationPanelViewController mNotificationPanel, final View mAmbientIndicationContainer) {
        this.mStatusBar = mStatusBar;
        this.mNotificationIconAreaController = mNotificationIconAreaController;
        this.mStatusBarKeyguardViewManager = mStatusBarKeyguardViewManager;
        this.mNotificationPanel = mNotificationPanel;
        this.mNotificationShadeWindowViewController = mNotificationShadeWindowViewController;
        this.mAmbientIndicationContainer = mAmbientIndicationContainer;
        this.mBiometricUnlockController = this.mBiometricUnlockControllerLazy.get();
    }
    
    @Override
    public boolean isBlockingDoze() {
        if (this.mBiometricUnlockController.hasPendingAuthentication()) {
            Log.i("StatusBar", "Blocking AOD because fingerprint has authenticated");
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isDozeSuppressed() {
        return this.mSuppressed;
    }
    
    @Override
    public boolean isPowerSaveActive() {
        return this.mBatteryController.isAodPowerSave();
    }
    
    @Override
    public boolean isProvisioned() {
        return this.mDeviceProvisionedController.isDeviceProvisioned() && this.mDeviceProvisionedController.isCurrentUserSetup();
    }
    
    boolean isPulsing() {
        return this.mPulsing;
    }
    
    @Override
    public boolean isPulsingBlocked() {
        final int mode = this.mBiometricUnlockController.getMode();
        boolean b = true;
        if (mode != 1) {
            b = false;
        }
        return b;
    }
    
    @Override
    public void onIgnoreTouchWhilePulsing(final boolean mIgnoreTouchWhilePulsing) {
        if (mIgnoreTouchWhilePulsing != this.mIgnoreTouchWhilePulsing) {
            this.mDozeLog.tracePulseTouchDisabledByProx(mIgnoreTouchWhilePulsing);
        }
        this.mIgnoreTouchWhilePulsing = mIgnoreTouchWhilePulsing;
        if (this.mStatusBarStateController.isDozing() && mIgnoreTouchWhilePulsing) {
            this.mNotificationShadeWindowViewController.cancelCurrentTouch();
        }
    }
    
    @Override
    public void onSlpiTap(final float n, final float n2) {
        if (n > 0.0f && n2 > 0.0f) {
            final View mAmbientIndicationContainer = this.mAmbientIndicationContainer;
            if (mAmbientIndicationContainer != null && mAmbientIndicationContainer.getVisibility() == 0) {
                final int[] array = new int[2];
                this.mAmbientIndicationContainer.getLocationOnScreen(array);
                final float n3 = n - array[0];
                final float n4 = n2 - array[1];
                if (0.0f <= n3 && n3 <= this.mAmbientIndicationContainer.getWidth() && 0.0f <= n4 && n4 <= this.mAmbientIndicationContainer.getHeight()) {
                    final long elapsedRealtime = SystemClock.elapsedRealtime();
                    final MotionEvent obtain = MotionEvent.obtain(elapsedRealtime, elapsedRealtime, 0, n, n2, 0);
                    this.mAmbientIndicationContainer.dispatchTouchEvent(obtain);
                    obtain.recycle();
                    final MotionEvent obtain2 = MotionEvent.obtain(elapsedRealtime, elapsedRealtime, 1, n, n2, 0);
                    this.mAmbientIndicationContainer.dispatchTouchEvent(obtain2);
                    obtain2.recycle();
                }
            }
        }
    }
    
    @Override
    public void prepareForGentleSleep(final Runnable mPendingScreenOffCallback) {
        if (this.mPendingScreenOffCallback != null) {
            Log.w("DozeServiceHost", "Overlapping onDisplayOffCallback. Ignoring previous one.");
        }
        this.mPendingScreenOffCallback = mPendingScreenOffCallback;
        this.mStatusBar.updateScrimController();
    }
    
    @Override
    public void pulseWhileDozing(final PulseCallback pulseCallback, final int n) {
        if (n == 5) {
            this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 4, "com.android.systemui:LONG_PRESS");
            this.mAssistManagerLazy.get().startAssist(new Bundle());
            return;
        }
        if (n == 8) {
            this.mScrimController.setWakeLockScreenSensorActive(true);
        }
        final boolean b = n == 8 && this.mWakeLockScreenPerformsAuth;
        this.mPulsing = true;
        this.mDozeScrimController.pulse(new PulseCallback() {
            private void setPulsing(final boolean b) {
                DozeServiceHost.this.mStatusBarStateController.setPulsing(b);
                DozeServiceHost.this.mStatusBarKeyguardViewManager.setPulsing(b);
                DozeServiceHost.this.mKeyguardViewMediator.setPulsing(b);
                DozeServiceHost.this.mNotificationPanel.setPulsing(b);
                DozeServiceHost.this.mVisualStabilityManager.setPulsing(b);
                DozeServiceHost.this.mLockscreenLockIconController.setPulsing(b);
                DozeServiceHost.this.mIgnoreTouchWhilePulsing = false;
                if (DozeServiceHost.this.mKeyguardUpdateMonitor != null && b) {
                    DozeServiceHost.this.mKeyguardUpdateMonitor.onAuthInterruptDetected(b);
                }
                DozeServiceHost.this.mStatusBar.updateScrimController();
                DozeServiceHost.this.mPulseExpansionHandler.setPulsing(b);
                DozeServiceHost.this.mNotificationWakeUpCoordinator.setPulsing(b);
            }
            
            @Override
            public void onPulseFinished() {
                DozeServiceHost.this.mPulsing = false;
                pulseCallback.onPulseFinished();
                DozeServiceHost.this.mStatusBar.updateNotificationPanelTouchState();
                DozeServiceHost.this.mScrimController.setWakeLockScreenSensorActive(false);
                this.setPulsing(false);
            }
            
            @Override
            public void onPulseStarted() {
                pulseCallback.onPulseStarted();
                DozeServiceHost.this.mStatusBar.updateNotificationPanelTouchState();
                this.setPulsing(true);
            }
        }, n);
        this.mStatusBar.updateScrimController();
    }
    
    @Override
    public void removeCallback(final Callback o) {
        this.mCallbacks.remove(o);
    }
    
    @Override
    public void setAnimateScreenOff(final boolean mAnimateScreenOff) {
        this.mAnimateScreenOff = mAnimateScreenOff;
    }
    
    @Override
    public void setAnimateWakeup(final boolean mAnimateWakeup) {
        if (this.mWakefulnessLifecycle.getWakefulness() != 2) {
            if (this.mWakefulnessLifecycle.getWakefulness() != 1) {
                this.mAnimateWakeup = mAnimateWakeup;
            }
        }
    }
    
    @Override
    public void setAodDimmingScrim(final float aodFrontScrimAlpha) {
        this.mScrimController.setAodFrontScrimAlpha(aodFrontScrimAlpha);
    }
    
    @Override
    public void setDozeScreenBrightness(final int dozeScreenBrightness) {
        this.mNotificationShadeWindowController.setDozeScreenBrightness(dozeScreenBrightness);
    }
    
    void setDozeSuppressed(final boolean mSuppressed) {
        if (mSuppressed == this.mSuppressed) {
            return;
        }
        this.mSuppressed = mSuppressed;
        final Iterator<Callback> iterator = this.mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onDozeSuppressedChanged(mSuppressed);
        }
    }
    
    boolean shouldAnimateScreenOff() {
        return this.mAnimateScreenOff;
    }
    
    boolean shouldAnimateWakeup() {
        return this.mAnimateWakeup;
    }
    
    @Override
    public void startDozing() {
        if (!this.mDozingRequested) {
            this.mDozingRequested = true;
            this.updateDozing();
            this.mDozeLog.traceDozing(this.mStatusBarStateController.isDozing());
            this.mStatusBar.updateIsKeyguard();
        }
    }
    
    @Override
    public void stopDozing() {
        if (this.mDozingRequested) {
            this.mDozingRequested = false;
            this.updateDozing();
            this.mDozeLog.traceDozing(this.mStatusBarStateController.isDozing());
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PSB.DozeServiceHost[mCallbacks=");
        sb.append(this.mCallbacks.size());
        sb.append("]");
        return sb.toString();
    }
    
    void updateDozing() {
        final boolean mDozingRequested = this.mDozingRequested;
        final boolean b = false;
        boolean isDozing = (mDozingRequested && this.mStatusBarStateController.getState() == 1) || this.mBiometricUnlockController.getMode() == 2;
        if (this.mBiometricUnlockController.getMode() == 1) {
            isDozing = b;
        }
        this.mStatusBarStateController.setIsDozing(isDozing);
    }
}
