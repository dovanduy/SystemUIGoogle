// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import java.util.TimeZone;
import com.android.settingslib.fuelgauge.BatteryStatus;
import android.os.SystemClock;
import android.hardware.biometrics.BiometricSourceType;

public class KeyguardUpdateMonitorCallback
{
    private boolean mShowing;
    private long mVisibilityChangedCalled;
    
    public void onBiometricAcquired(final BiometricSourceType biometricSourceType) {
    }
    
    public void onBiometricAuthFailed(final BiometricSourceType biometricSourceType) {
    }
    
    public void onBiometricAuthenticated(final int n, final BiometricSourceType biometricSourceType, final boolean b) {
    }
    
    public void onBiometricError(final int n, final String s, final BiometricSourceType biometricSourceType) {
    }
    
    public void onBiometricHelp(final int n, final String s, final BiometricSourceType biometricSourceType) {
    }
    
    public void onBiometricRunningStateChanged(final boolean b, final BiometricSourceType biometricSourceType) {
    }
    
    public void onBiometricsCleared() {
    }
    
    public void onClockVisibilityChanged() {
    }
    
    public void onDevicePolicyManagerStateChanged() {
    }
    
    public void onDeviceProvisioned() {
    }
    
    public void onDreamingStateChanged(final boolean b) {
    }
    
    public void onEmergencyCallAction() {
    }
    
    public void onFaceUnlockStateChanged(final boolean b, final int n) {
    }
    
    @Deprecated
    public void onFinishedGoingToSleep(final int n) {
    }
    
    public void onHasLockscreenWallpaperChanged(final boolean b) {
    }
    
    public void onKeyguardBouncerChanged(final boolean b) {
    }
    
    public void onKeyguardVisibilityChanged(final boolean b) {
    }
    
    public void onKeyguardVisibilityChangedRaw(final boolean mShowing) {
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        if (mShowing == this.mShowing && elapsedRealtime - this.mVisibilityChangedCalled < 1000L) {
            return;
        }
        this.onKeyguardVisibilityChanged(mShowing);
        this.mVisibilityChangedCalled = elapsedRealtime;
        this.mShowing = mShowing;
    }
    
    public void onLogoutEnabledChanged() {
    }
    
    public void onPhoneStateChanged(final int n) {
    }
    
    public void onRefreshBatteryInfo(final BatteryStatus batteryStatus) {
    }
    
    public void onRefreshCarrierInfo() {
    }
    
    public void onRingerModeChanged(final int n) {
    }
    
    @Deprecated
    public void onScreenTurnedOff() {
    }
    
    @Deprecated
    public void onScreenTurnedOn() {
    }
    
    public void onSecondaryLockscreenRequirementChanged(final int n) {
    }
    
    public void onSimStateChanged(final int n, final int n2, final int n3) {
    }
    
    @Deprecated
    public void onStartedGoingToSleep(final int n) {
    }
    
    @Deprecated
    public void onStartedWakingUp() {
    }
    
    public void onStrongAuthStateChanged(final int n) {
    }
    
    public void onTelephonyCapable(final boolean b) {
    }
    
    public void onTimeChanged() {
    }
    
    public void onTimeZoneChanged(final TimeZone timeZone) {
    }
    
    public void onTrustAgentErrorMessage(final CharSequence charSequence) {
    }
    
    public void onTrustChanged(final int n) {
    }
    
    public void onTrustGrantedWithFlags(final int n, final int n2) {
    }
    
    public void onTrustManagedChanged(final int n) {
    }
    
    public void onUserInfoChanged(final int n) {
    }
    
    public void onUserSwitchComplete(final int n) {
    }
    
    public void onUserSwitching(final int n) {
    }
    
    public void onUserUnlocked() {
    }
}
