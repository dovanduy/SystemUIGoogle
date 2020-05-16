// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.hardware.biometrics.BiometricSourceType;
import android.os.SystemClock;
import com.android.internal.util.LatencyTracker;
import android.content.Context;
import android.os.PowerManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.phone.BiometricUnlockController;

public class LatencyTester extends SystemUI
{
    private final BiometricUnlockController mBiometricUnlockController;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final PowerManager mPowerManager;
    
    public LatencyTester(final Context context, final BiometricUnlockController mBiometricUnlockController, final PowerManager mPowerManager, final BroadcastDispatcher mBroadcastDispatcher) {
        super(context);
        this.mBiometricUnlockController = mBiometricUnlockController;
        this.mPowerManager = mPowerManager;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
    }
    
    private void fakeTurnOnScreen() {
        if (LatencyTracker.isEnabled(super.mContext)) {
            LatencyTracker.getInstance(super.mContext).onActionStart(5);
        }
        this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 0, "android.policy:LATENCY_TESTS");
    }
    
    private void fakeWakeAndUnlock() {
        this.mBiometricUnlockController.onBiometricAcquired(BiometricSourceType.FINGERPRINT);
        this.mBiometricUnlockController.onBiometricAuthenticated(KeyguardUpdateMonitor.getCurrentUser(), BiometricSourceType.FINGERPRINT, true);
    }
    
    @Override
    public void start() {
        if (!Build.IS_DEBUGGABLE) {
            return;
        }
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.systemui.latency.ACTION_FINGERPRINT_WAKE");
        intentFilter.addAction("com.android.systemui.latency.ACTION_TURN_ON_SCREEN");
        this.mBroadcastDispatcher.registerReceiver(new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if ("com.android.systemui.latency.ACTION_FINGERPRINT_WAKE".equals(action)) {
                    LatencyTester.this.fakeWakeAndUnlock();
                }
                else if ("com.android.systemui.latency.ACTION_TURN_ON_SCREEN".equals(action)) {
                    LatencyTester.this.fakeTurnOnScreen();
                }
            }
        }, intentFilter);
    }
}
