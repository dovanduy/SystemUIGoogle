// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.os.RemoteException;
import android.util.Log;
import com.android.settingslib.fuelgauge.BatteryStatus;
import android.hardware.biometrics.BiometricSourceType;
import com.android.settingslib.Utils;
import com.android.systemui.R$id;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.internal.annotations.VisibleForTesting;
import java.util.IllegalFormatConversionException;
import android.text.format.Formatter;
import java.text.NumberFormat;
import android.text.TextUtils;
import com.android.systemui.R$string;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import com.android.internal.widget.ViewClippingUtil;
import com.android.systemui.R$integer;
import android.os.Message;
import android.view.View;
import com.android.systemui.util.wakelock.WakeLock;
import com.android.systemui.util.wakelock.SettableWakeLock;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.statusbar.phone.KeyguardIndicationTextView;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.LockscreenLockIconController;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.content.res.ColorStateList;
import android.view.ViewGroup;
import android.os.Handler;
import com.android.systemui.dock.DockManager;
import android.content.Context;
import com.android.internal.widget.ViewClippingUtil$ClippingParameters;
import com.android.internal.app.IBatteryStats;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;

public class KeyguardIndicationController implements StateListener, Callback
{
    private String mAlignmentIndication;
    private final IBatteryStats mBatteryInfo;
    private int mBatteryLevel;
    private int mChargingSpeed;
    private long mChargingTimeRemaining;
    private int mChargingWattage;
    private final ViewClippingUtil$ClippingParameters mClippingParams;
    private final Context mContext;
    private final DockManager mDockManager;
    private boolean mDozing;
    private final Handler mHandler;
    private boolean mHideTransientMessageOnScreenOff;
    private ViewGroup mIndicationArea;
    private ColorStateList mInitialTextColorState;
    private final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private LockscreenLockIconController mLockIconController;
    private String mMessageToShowOnScreenOn;
    private boolean mPowerCharged;
    private boolean mPowerPluggedIn;
    private boolean mPowerPluggedInWired;
    private String mRestingIndication;
    private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final StatusBarStateController mStatusBarStateController;
    private KeyguardIndicationTextView mTextView;
    private final KeyguardUpdateMonitorCallback mTickReceiver;
    private CharSequence mTransientIndication;
    private boolean mTransientTextIsError;
    private KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    private boolean mVisible;
    private final SettableWakeLock mWakeLock;
    
    KeyguardIndicationController(final Context mContext, final WakeLock.Builder builder, final KeyguardStateController mKeyguardStateController, final StatusBarStateController mStatusBarStateController, final KeyguardUpdateMonitor mKeyguardUpdateMonitor, final DockManager mDockManager, final IBatteryStats mBatteryInfo) {
        this.mClippingParams = (ViewClippingUtil$ClippingParameters)new ViewClippingUtil$ClippingParameters() {
            public boolean shouldFinish(final View view) {
                return view == KeyguardIndicationController.this.mIndicationArea;
            }
        };
        this.mTickReceiver = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onTimeChanged() {
                if (KeyguardIndicationController.this.mVisible) {
                    KeyguardIndicationController.this.updateIndication(false);
                }
            }
        };
        this.mHandler = new Handler() {
            public void handleMessage(final Message message) {
                final int what = message.what;
                if (what == 1) {
                    KeyguardIndicationController.this.hideTransientIndication();
                }
                else if (what == 2) {
                    if (KeyguardIndicationController.this.mLockIconController != null) {
                        KeyguardIndicationController.this.mLockIconController.setTransientBiometricsError(false);
                    }
                }
                else if (what == 3) {
                    KeyguardIndicationController.this.showSwipeUpToUnlock();
                }
            }
        };
        this.mContext = mContext;
        this.mKeyguardStateController = mKeyguardStateController;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mKeyguardUpdateMonitor = mKeyguardUpdateMonitor;
        (this.mDockManager = mDockManager).addAlignmentStateListener((DockManager.AlignmentStateListener)new _$$Lambda$KeyguardIndicationController$MNRKvB1L0H3Iaik26PzOwQaf05I(this));
        builder.setTag("Doze:KeyguardIndication");
        this.mWakeLock = new SettableWakeLock(builder.build(), "KeyguardIndication");
        this.mBatteryInfo = mBatteryInfo;
        this.mKeyguardUpdateMonitor.registerCallback(this.getKeyguardCallback());
        this.mKeyguardUpdateMonitor.registerCallback(this.mTickReceiver);
        this.mStatusBarStateController.addCallback((StatusBarStateController.StateListener)this);
        this.mKeyguardStateController.addCallback((KeyguardStateController.Callback)this);
    }
    
    private void animateText(final KeyguardIndicationTextView keyguardIndicationTextView, final String s) {
        final int integer = this.mContext.getResources().getInteger(R$integer.wired_charging_keyguard_text_animation_distance);
        final int integer2 = this.mContext.getResources().getInteger(R$integer.wired_charging_keyguard_text_animation_duration_up);
        final int integer3 = this.mContext.getResources().getInteger(R$integer.wired_charging_keyguard_text_animation_duration_down);
        keyguardIndicationTextView.animate().cancel();
        ViewClippingUtil.setClippingDeactivated((View)keyguardIndicationTextView, true, this.mClippingParams);
        keyguardIndicationTextView.animate().translationYBy((float)integer).setInterpolator((TimeInterpolator)Interpolators.LINEAR).setDuration((long)integer2).setListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            private boolean mCancelled;
            final /* synthetic */ KeyguardIndicationController this$0;
            
            public void onAnimationCancel(final Animator animator) {
                keyguardIndicationTextView.setTranslationY(0.0f);
                this.mCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                if (this.mCancelled) {
                    ViewClippingUtil.setClippingDeactivated((View)keyguardIndicationTextView, false, KeyguardIndicationController.this.mClippingParams);
                    return;
                }
                keyguardIndicationTextView.animate().setDuration((long)integer3).setInterpolator((TimeInterpolator)Interpolators.BOUNCE).translationY(0.0f).setListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                    public void onAnimationEnd(final Animator animator) {
                        keyguardIndicationTextView.setTranslationY(0.0f);
                        final AnimatorListenerAdapter this$1 = AnimatorListenerAdapter.this;
                        ViewClippingUtil.setClippingDeactivated((View)keyguardIndicationTextView, false, this$1.this$0.mClippingParams);
                    }
                });
            }
            
            public void onAnimationStart(final Animator animator) {
                keyguardIndicationTextView.switchIndication(s);
            }
        });
    }
    
    private String getTrustManagedIndication() {
        return null;
    }
    
    private void handleAlignStateChanged(final int n) {
        String mAlignmentIndication;
        if (n == 1) {
            mAlignmentIndication = this.mContext.getResources().getString(R$string.dock_alignment_slow_charging);
        }
        else if (n == 2) {
            mAlignmentIndication = this.mContext.getResources().getString(R$string.dock_alignment_not_charging);
        }
        else {
            mAlignmentIndication = "";
        }
        if (!mAlignmentIndication.equals(this.mAlignmentIndication)) {
            this.mAlignmentIndication = mAlignmentIndication;
            this.updateIndication(false);
        }
    }
    
    private void showSwipeUpToUnlock() {
        if (this.mDozing) {
            return;
        }
        if (this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
            this.mStatusBarKeyguardViewManager.showBouncerMessage(this.mContext.getString(R$string.keyguard_retry), this.mInitialTextColorState);
        }
        else if (this.mKeyguardUpdateMonitor.isScreenOn()) {
            this.showTransientIndication(this.mContext.getString(R$string.keyguard_unlock), false, true);
            this.hideTransientIndicationDelayed(5000L);
        }
    }
    
    private void showTransientIndication(final CharSequence mTransientIndication, final boolean mTransientTextIsError, final boolean b) {
        this.mTransientIndication = mTransientIndication;
        this.mHideTransientMessageOnScreenOff = (b && mTransientIndication != null);
        this.mTransientTextIsError = mTransientTextIsError;
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(3);
        if (this.mDozing && !TextUtils.isEmpty(this.mTransientIndication)) {
            this.mWakeLock.setAcquired(true);
            this.hideTransientIndicationDelayed(5000L);
        }
        this.updateIndication(false);
    }
    
    @VisibleForTesting
    String computePowerIndication() {
        if (this.mPowerCharged) {
            return this.mContext.getResources().getString(R$string.keyguard_charged);
        }
        final boolean b = this.mChargingTimeRemaining > 0L;
        int n;
        if (this.mPowerPluggedInWired) {
            final int mChargingSpeed = this.mChargingSpeed;
            if (mChargingSpeed != 0) {
                if (mChargingSpeed != 2) {
                    if (b) {
                        n = R$string.keyguard_indication_charging_time;
                    }
                    else {
                        n = R$string.keyguard_plugged_in;
                    }
                }
                else if (b) {
                    n = R$string.keyguard_indication_charging_time_fast;
                }
                else {
                    n = R$string.keyguard_plugged_in_charging_fast;
                }
            }
            else if (b) {
                n = R$string.keyguard_indication_charging_time_slowly;
            }
            else {
                n = R$string.keyguard_plugged_in_charging_slowly;
            }
        }
        else if (b) {
            n = R$string.keyguard_indication_charging_time_wireless;
        }
        else {
            n = R$string.keyguard_plugged_in_wireless;
        }
        final String format = NumberFormat.getPercentInstance().format(this.mBatteryLevel / 100.0f);
        if (b) {
            final String formatShortElapsedTimeRoundingUpToMinutes = Formatter.formatShortElapsedTimeRoundingUpToMinutes(this.mContext, this.mChargingTimeRemaining);
            try {
                return this.mContext.getResources().getString(n, new Object[] { formatShortElapsedTimeRoundingUpToMinutes, format });
            }
            catch (IllegalFormatConversionException ex) {
                return this.mContext.getResources().getString(n, new Object[] { formatShortElapsedTimeRoundingUpToMinutes });
            }
        }
        try {
            return this.mContext.getResources().getString(n, new Object[] { format });
        }
        catch (IllegalFormatConversionException ex2) {
            return this.mContext.getResources().getString(n);
        }
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("KeyguardIndicationController:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mTransientTextIsError: ");
        sb.append(this.mTransientTextIsError);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  mInitialTextColorState: ");
        sb2.append(this.mInitialTextColorState);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mPowerPluggedInWired: ");
        sb3.append(this.mPowerPluggedInWired);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  mPowerPluggedIn: ");
        sb4.append(this.mPowerPluggedIn);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("  mPowerCharged: ");
        sb5.append(this.mPowerCharged);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("  mChargingSpeed: ");
        sb6.append(this.mChargingSpeed);
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append("  mChargingWattage: ");
        sb7.append(this.mChargingWattage);
        printWriter.println(sb7.toString());
        final StringBuilder sb8 = new StringBuilder();
        sb8.append("  mMessageToShowOnScreenOn: ");
        sb8.append(this.mMessageToShowOnScreenOn);
        printWriter.println(sb8.toString());
        final StringBuilder sb9 = new StringBuilder();
        sb9.append("  mDozing: ");
        sb9.append(this.mDozing);
        printWriter.println(sb9.toString());
        final StringBuilder sb10 = new StringBuilder();
        sb10.append("  mBatteryLevel: ");
        sb10.append(this.mBatteryLevel);
        printWriter.println(sb10.toString());
        final StringBuilder sb11 = new StringBuilder();
        sb11.append("  mTextView.getText(): ");
        final KeyguardIndicationTextView mTextView = this.mTextView;
        Object text;
        if (mTextView == null) {
            text = null;
        }
        else {
            text = mTextView.getText();
        }
        sb11.append(text);
        printWriter.println(sb11.toString());
        final StringBuilder sb12 = new StringBuilder();
        sb12.append("  computePowerIndication(): ");
        sb12.append(this.computePowerIndication());
        printWriter.println(sb12.toString());
    }
    
    protected KeyguardUpdateMonitorCallback getKeyguardCallback() {
        if (this.mUpdateMonitorCallback == null) {
            this.mUpdateMonitorCallback = new BaseKeyguardCallback();
        }
        return this.mUpdateMonitorCallback;
    }
    
    @VisibleForTesting
    String getTrustGrantedIndication() {
        return this.mContext.getString(R$string.keyguard_indication_trust_unlocked);
    }
    
    public void hideTransientIndication() {
        if (this.mTransientIndication != null) {
            this.mTransientIndication = null;
            this.mHideTransientMessageOnScreenOff = false;
            this.mHandler.removeMessages(1);
            this.updateIndication(false);
        }
    }
    
    public void hideTransientIndicationDelayed(final long n) {
        final Handler mHandler = this.mHandler;
        mHandler.sendMessageDelayed(mHandler.obtainMessage(1), n);
    }
    
    @Override
    public void onDozingChanged(final boolean dozing) {
        this.setDozing(dozing);
    }
    
    @Override
    public void onStateChanged(final int n) {
    }
    
    @Override
    public void onUnlockedChanged() {
        this.updateIndication(this.mDozing ^ true);
    }
    
    public void setDozing(final boolean mDozing) {
        if (this.mDozing == mDozing) {
            return;
        }
        this.mDozing = mDozing;
        if (this.mHideTransientMessageOnScreenOff && mDozing) {
            this.hideTransientIndication();
        }
        else {
            this.updateIndication(false);
        }
    }
    
    public void setIndicationArea(final ViewGroup mIndicationArea) {
        this.mIndicationArea = mIndicationArea;
        final KeyguardIndicationTextView mTextView = (KeyguardIndicationTextView)mIndicationArea.findViewById(R$id.keyguard_indication_text);
        this.mTextView = mTextView;
        ColorStateList mInitialTextColorState;
        if (mTextView != null) {
            mInitialTextColorState = mTextView.getTextColors();
        }
        else {
            mInitialTextColorState = ColorStateList.valueOf(-1);
        }
        this.mInitialTextColorState = mInitialTextColorState;
        this.updateIndication(false);
    }
    
    public void setLockIconController(final LockscreenLockIconController mLockIconController) {
        this.mLockIconController = mLockIconController;
    }
    
    @VisibleForTesting
    void setPowerPluggedIn(final boolean mPowerPluggedIn) {
        this.mPowerPluggedIn = mPowerPluggedIn;
    }
    
    public void setStatusBarKeyguardViewManager(final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager) {
        this.mStatusBarKeyguardViewManager = mStatusBarKeyguardViewManager;
    }
    
    public void setVisible(final boolean mVisible) {
        this.mVisible = mVisible;
        final ViewGroup mIndicationArea = this.mIndicationArea;
        int visibility;
        if (mVisible) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mIndicationArea.setVisibility(visibility);
        if (mVisible) {
            if (!this.mHandler.hasMessages(1)) {
                this.hideTransientIndication();
            }
            this.updateIndication(false);
        }
        else if (!mVisible) {
            this.hideTransientIndication();
        }
    }
    
    public void showTransientIndication(final int n) {
        this.showTransientIndication(this.mContext.getResources().getString(n));
    }
    
    public void showTransientIndication(final CharSequence charSequence) {
        this.showTransientIndication(charSequence, false, false);
    }
    
    protected final void updateIndication(final boolean b) {
        final boolean empty = TextUtils.isEmpty(this.mTransientIndication);
        final boolean b2 = false;
        if (empty) {
            this.mWakeLock.setAcquired(false);
        }
        if (this.mVisible) {
            if (this.mDozing) {
                this.mTextView.setTextColor(-1);
                if (!TextUtils.isEmpty(this.mTransientIndication)) {
                    this.mTextView.switchIndication(this.mTransientIndication);
                }
                else if (!TextUtils.isEmpty((CharSequence)this.mAlignmentIndication)) {
                    this.mTextView.switchIndication(this.mAlignmentIndication);
                }
                else if (this.mPowerPluggedIn) {
                    final String computePowerIndication = this.computePowerIndication();
                    if (b) {
                        this.animateText(this.mTextView, computePowerIndication);
                    }
                    else {
                        this.mTextView.switchIndication(computePowerIndication);
                    }
                }
                else {
                    this.mTextView.switchIndication(NumberFormat.getPercentInstance().format(this.mBatteryLevel / 100.0f));
                }
                return;
            }
            final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            final String trustGrantedIndication = this.getTrustGrantedIndication();
            final String trustManagedIndication = this.getTrustManagedIndication();
            String computePowerIndication2 = null;
            if (this.mPowerPluggedIn) {
                computePowerIndication2 = this.computePowerIndication();
            }
            int mTransientTextIsError;
            if (!this.mKeyguardUpdateMonitor.isUserUnlocked(currentUser)) {
                this.mTextView.switchIndication(17040477);
                mTransientTextIsError = (b2 ? 1 : 0);
            }
            else if (!TextUtils.isEmpty(this.mTransientIndication)) {
                if (computePowerIndication2 != null && !this.mTransientIndication.equals(computePowerIndication2)) {
                    this.mTextView.switchIndication(this.mContext.getResources().getString(R$string.keyguard_indication_trust_unlocked_plugged_in, new Object[] { this.mTransientIndication, computePowerIndication2 }));
                }
                else {
                    this.mTextView.switchIndication(this.mTransientIndication);
                }
                mTransientTextIsError = (this.mTransientTextIsError ? 1 : 0);
            }
            else if (!TextUtils.isEmpty((CharSequence)trustGrantedIndication) && this.mKeyguardUpdateMonitor.getUserHasTrust(currentUser)) {
                if (computePowerIndication2 != null) {
                    this.mTextView.switchIndication(this.mContext.getResources().getString(R$string.keyguard_indication_trust_unlocked_plugged_in, new Object[] { trustGrantedIndication, computePowerIndication2 }));
                    mTransientTextIsError = (b2 ? 1 : 0);
                }
                else {
                    this.mTextView.switchIndication(trustGrantedIndication);
                    mTransientTextIsError = (b2 ? 1 : 0);
                }
            }
            else if (!TextUtils.isEmpty((CharSequence)this.mAlignmentIndication)) {
                this.mTextView.switchIndication(this.mAlignmentIndication);
                mTransientTextIsError = 1;
            }
            else if (this.mPowerPluggedIn) {
                if (b) {
                    this.animateText(this.mTextView, computePowerIndication2);
                    mTransientTextIsError = (b2 ? 1 : 0);
                }
                else {
                    this.mTextView.switchIndication(computePowerIndication2);
                    mTransientTextIsError = (b2 ? 1 : 0);
                }
            }
            else if (!TextUtils.isEmpty((CharSequence)trustManagedIndication) && this.mKeyguardUpdateMonitor.getUserTrustIsManaged(currentUser) && !this.mKeyguardUpdateMonitor.getUserHasTrust(currentUser)) {
                this.mTextView.switchIndication(trustManagedIndication);
                mTransientTextIsError = (b2 ? 1 : 0);
            }
            else {
                this.mTextView.switchIndication(this.mRestingIndication);
                mTransientTextIsError = (b2 ? 1 : 0);
            }
            final KeyguardIndicationTextView mTextView = this.mTextView;
            ColorStateList textColor;
            if (mTransientTextIsError != 0) {
                textColor = Utils.getColorError(this.mContext);
            }
            else {
                textColor = this.mInitialTextColorState;
            }
            mTextView.setTextColor(textColor);
        }
    }
    
    protected class BaseKeyguardCallback extends KeyguardUpdateMonitorCallback
    {
        private void animatePadlockError() {
            if (KeyguardIndicationController.this.mLockIconController != null) {
                KeyguardIndicationController.this.mLockIconController.setTransientBiometricsError(true);
            }
            KeyguardIndicationController.this.mHandler.removeMessages(2);
            KeyguardIndicationController.this.mHandler.sendMessageDelayed(KeyguardIndicationController.this.mHandler.obtainMessage(2), 1300L);
        }
        
        private boolean shouldSuppressBiometricError(final int n, final BiometricSourceType biometricSourceType, final KeyguardUpdateMonitor keyguardUpdateMonitor) {
            if (biometricSourceType == BiometricSourceType.FINGERPRINT) {
                return this.shouldSuppressFingerprintError(n, keyguardUpdateMonitor);
            }
            return biometricSourceType == BiometricSourceType.FACE && this.shouldSuppressFaceError(n, keyguardUpdateMonitor);
        }
        
        private boolean shouldSuppressFaceError(final int n, final KeyguardUpdateMonitor keyguardUpdateMonitor) {
            final boolean b = true;
            if (!keyguardUpdateMonitor.isUnlockingWithBiometricAllowed(true)) {
                final boolean b2 = b;
                if (n != 9) {
                    return b2;
                }
            }
            return n == 5 && b;
        }
        
        private boolean shouldSuppressFingerprintError(final int n, final KeyguardUpdateMonitor keyguardUpdateMonitor) {
            final boolean b = true;
            if (!keyguardUpdateMonitor.isUnlockingWithBiometricAllowed(true)) {
                final boolean b2 = b;
                if (n != 9) {
                    return b2;
                }
            }
            return n == 5 && b;
        }
        
        @Override
        public void onBiometricAuthenticated(final int n, final BiometricSourceType biometricSourceType, final boolean b) {
            super.onBiometricAuthenticated(n, biometricSourceType, b);
            KeyguardIndicationController.this.mHandler.sendEmptyMessage(1);
        }
        
        @Override
        public void onBiometricError(final int n, final String s, final BiometricSourceType biometricSourceType) {
            if (this.shouldSuppressBiometricError(n, biometricSourceType, KeyguardIndicationController.this.mKeyguardUpdateMonitor)) {
                return;
            }
            this.animatePadlockError();
            if (n == 3) {
                KeyguardIndicationController.this.showSwipeUpToUnlock();
            }
            else if (KeyguardIndicationController.this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                KeyguardIndicationController.this.mStatusBarKeyguardViewManager.showBouncerMessage(s, KeyguardIndicationController.this.mInitialTextColorState);
            }
            else if (KeyguardIndicationController.this.mKeyguardUpdateMonitor.isScreenOn()) {
                KeyguardIndicationController.this.showTransientIndication(s);
                KeyguardIndicationController.this.hideTransientIndicationDelayed(5000L);
            }
            else {
                KeyguardIndicationController.this.mMessageToShowOnScreenOn = s;
            }
        }
        
        @Override
        public void onBiometricHelp(final int n, final String s, final BiometricSourceType biometricSourceType) {
            final KeyguardUpdateMonitor access$1500 = KeyguardIndicationController.this.mKeyguardUpdateMonitor;
            boolean b = true;
            if (!access$1500.isUnlockingWithBiometricAllowed(true)) {
                return;
            }
            if (n != -2) {
                b = false;
            }
            if (KeyguardIndicationController.this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                KeyguardIndicationController.this.mStatusBarKeyguardViewManager.showBouncerMessage(s, KeyguardIndicationController.this.mInitialTextColorState);
            }
            else if (KeyguardIndicationController.this.mKeyguardUpdateMonitor.isScreenOn()) {
                KeyguardIndicationController.this.showTransientIndication(s, false, b);
                if (!b) {
                    KeyguardIndicationController.this.hideTransientIndicationDelayed(1300L);
                }
            }
            if (b) {
                KeyguardIndicationController.this.mHandler.sendMessageDelayed(KeyguardIndicationController.this.mHandler.obtainMessage(3), 1300L);
            }
        }
        
        @Override
        public void onBiometricRunningStateChanged(final boolean b, final BiometricSourceType biometricSourceType) {
            if (b) {
                KeyguardIndicationController.this.hideTransientIndication();
                KeyguardIndicationController.this.mMessageToShowOnScreenOn = null;
            }
        }
        
        @Override
        public void onRefreshBatteryInfo(final BatteryStatus batteryStatus) {
            final int status = batteryStatus.status;
            final boolean b = false;
            final boolean b2 = status == 2 || status == 5;
            final boolean access$500 = KeyguardIndicationController.this.mPowerPluggedIn;
            KeyguardIndicationController.this.mPowerPluggedInWired = (batteryStatus.isPluggedInWired() && b2);
            KeyguardIndicationController.this.mPowerPluggedIn = (batteryStatus.isPluggedIn() && b2);
            KeyguardIndicationController.this.mPowerCharged = batteryStatus.isCharged();
            KeyguardIndicationController.this.mChargingWattage = batteryStatus.maxChargingWattage;
            final KeyguardIndicationController this$0 = KeyguardIndicationController.this;
            this$0.mChargingSpeed = batteryStatus.getChargingSpeed(this$0.mContext);
            KeyguardIndicationController.this.mBatteryLevel = batteryStatus.level;
            try {
                final KeyguardIndicationController this$2 = KeyguardIndicationController.this;
                long computeChargeTimeRemaining;
                if (KeyguardIndicationController.this.mPowerPluggedIn) {
                    computeChargeTimeRemaining = KeyguardIndicationController.this.mBatteryInfo.computeChargeTimeRemaining();
                }
                else {
                    computeChargeTimeRemaining = -1L;
                }
                this$2.mChargingTimeRemaining = computeChargeTimeRemaining;
            }
            catch (RemoteException ex) {
                Log.e("KeyguardIndication", "Error calling IBatteryStats: ", (Throwable)ex);
                KeyguardIndicationController.this.mChargingTimeRemaining = -1L;
            }
            final KeyguardIndicationController this$3 = KeyguardIndicationController.this;
            boolean b3 = b;
            if (!access$500) {
                b3 = b;
                if (this$3.mPowerPluggedInWired) {
                    b3 = true;
                }
            }
            this$3.updateIndication(b3);
            if (KeyguardIndicationController.this.mDozing) {
                if (!access$500 && KeyguardIndicationController.this.mPowerPluggedIn) {
                    final KeyguardIndicationController this$4 = KeyguardIndicationController.this;
                    this$4.showTransientIndication(this$4.computePowerIndication());
                    KeyguardIndicationController.this.hideTransientIndicationDelayed(5000L);
                }
                else if (access$500 && !KeyguardIndicationController.this.mPowerPluggedIn) {
                    KeyguardIndicationController.this.hideTransientIndication();
                }
            }
        }
        
        @Override
        public void onScreenTurnedOn() {
            if (KeyguardIndicationController.this.mMessageToShowOnScreenOn != null) {
                final KeyguardIndicationController this$0 = KeyguardIndicationController.this;
                this$0.showTransientIndication(this$0.mMessageToShowOnScreenOn, true, false);
                KeyguardIndicationController.this.hideTransientIndicationDelayed(5000L);
                KeyguardIndicationController.this.mMessageToShowOnScreenOn = null;
            }
        }
        
        @Override
        public void onTrustAgentErrorMessage(final CharSequence charSequence) {
            KeyguardIndicationController.this.showTransientIndication(charSequence, true, false);
        }
        
        @Override
        public void onUserSwitchComplete(final int n) {
            if (KeyguardIndicationController.this.mVisible) {
                KeyguardIndicationController.this.updateIndication(false);
            }
        }
        
        @Override
        public void onUserUnlocked() {
            if (KeyguardIndicationController.this.mVisible) {
                KeyguardIndicationController.this.updateIndication(false);
            }
        }
    }
}
