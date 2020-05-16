// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.hardware.biometrics.BiometricSourceType;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.Collection;
import android.os.Trace;
import android.os.Build;
import android.content.Context;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.KeyguardUpdateMonitor;
import java.util.ArrayList;
import com.android.systemui.Dumpable;

public class KeyguardStateControllerImpl implements KeyguardStateController, Dumpable
{
    private boolean mBypassFadingAnimation;
    private final ArrayList<Callback> mCallbacks;
    private boolean mCanDismissLockScreen;
    private boolean mDebugUnlocked;
    private boolean mFaceAuthEnabled;
    private boolean mKeyguardFadingAway;
    private long mKeyguardFadingAwayDelay;
    private long mKeyguardFadingAwayDuration;
    private boolean mKeyguardGoingAway;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback;
    private boolean mLaunchTransitionFadingAway;
    private final LockPatternUtils mLockPatternUtils;
    private boolean mOccluded;
    private boolean mSecure;
    private boolean mShowing;
    private boolean mTrustManaged;
    private boolean mTrusted;
    
    public KeyguardStateControllerImpl(final Context context, final KeyguardUpdateMonitor mKeyguardUpdateMonitor, final LockPatternUtils mLockPatternUtils) {
        this.mCallbacks = new ArrayList<Callback>();
        final UpdateMonitorCallback mKeyguardUpdateMonitorCallback = new UpdateMonitorCallback();
        this.mKeyguardUpdateMonitorCallback = mKeyguardUpdateMonitorCallback;
        this.mDebugUnlocked = false;
        this.mKeyguardUpdateMonitor = mKeyguardUpdateMonitor;
        this.mLockPatternUtils = mLockPatternUtils;
        mKeyguardUpdateMonitor.registerCallback(mKeyguardUpdateMonitorCallback);
        this.update(true);
        final boolean is_DEBUGGABLE = Build.IS_DEBUGGABLE;
    }
    
    private void notifyKeyguardChanged() {
        Trace.beginSection("KeyguardStateController#notifyKeyguardChanged");
        new ArrayList(this.mCallbacks).forEach((Consumer)_$$Lambda$AiBS48IhN8ohBVmcN3CRVHIBtQ8.INSTANCE);
        Trace.endSection();
    }
    
    private void notifyUnlockedChanged() {
        Trace.beginSection("KeyguardStateController#notifyUnlockedChanged");
        new ArrayList(this.mCallbacks).forEach((Consumer)_$$Lambda$6_7ujqA_9Wm5PTpKC6v1UcUnDTY.INSTANCE);
        Trace.endSection();
    }
    
    private void setKeyguardFadingAway(final boolean mKeyguardFadingAway) {
        if (this.mKeyguardFadingAway != mKeyguardFadingAway) {
            this.mKeyguardFadingAway = mKeyguardFadingAway;
            final ArrayList<Callback> list = new ArrayList<Callback>(this.mCallbacks);
            for (int i = 0; i < list.size(); ++i) {
                list.get(i).onKeyguardFadingAwayChanged();
            }
        }
    }
    
    @Override
    public void addCallback(final Callback e) {
        Objects.requireNonNull(e, "Callback must not be null. b/128895449");
        if (!this.mCallbacks.contains(e)) {
            this.mCallbacks.add(e);
        }
    }
    
    @Override
    public long calculateGoingToFullShadeDelay() {
        return this.mKeyguardFadingAwayDelay + this.mKeyguardFadingAwayDuration;
    }
    
    @Override
    public boolean canDismissLockScreen() {
        return this.mCanDismissLockScreen;
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("KeyguardStateController:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mSecure: ");
        sb.append(this.mSecure);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  mCanDismissLockScreen: ");
        sb2.append(this.mCanDismissLockScreen);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mTrustManaged: ");
        sb3.append(this.mTrustManaged);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  mTrusted: ");
        sb4.append(this.mTrusted);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("  mDebugUnlocked: ");
        sb5.append(this.mDebugUnlocked);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("  mFaceAuthEnabled: ");
        sb6.append(this.mFaceAuthEnabled);
        printWriter.println(sb6.toString());
    }
    
    @Override
    public long getKeyguardFadingAwayDelay() {
        return this.mKeyguardFadingAwayDelay;
    }
    
    @Override
    public long getKeyguardFadingAwayDuration() {
        return this.mKeyguardFadingAwayDuration;
    }
    
    @Override
    public boolean isBypassFadingAnimation() {
        return this.mBypassFadingAnimation;
    }
    
    @Override
    public boolean isFaceAuthEnabled() {
        return this.mFaceAuthEnabled;
    }
    
    @Override
    public boolean isKeyguardFadingAway() {
        return this.mKeyguardFadingAway;
    }
    
    @Override
    public boolean isKeyguardGoingAway() {
        return this.mKeyguardGoingAway;
    }
    
    @Override
    public boolean isLaunchTransitionFadingAway() {
        return this.mLaunchTransitionFadingAway;
    }
    
    @Override
    public boolean isMethodSecure() {
        return this.mSecure;
    }
    
    @Override
    public boolean isOccluded() {
        return this.mOccluded;
    }
    
    @Override
    public boolean isShowing() {
        return this.mShowing;
    }
    
    @Override
    public void notifyKeyguardDoneFading() {
        this.setKeyguardFadingAway(this.mKeyguardGoingAway = false);
    }
    
    @Override
    public void notifyKeyguardFadingAway(final long mKeyguardFadingAwayDelay, final long mKeyguardFadingAwayDuration, final boolean mBypassFadingAnimation) {
        this.mKeyguardFadingAwayDelay = mKeyguardFadingAwayDelay;
        this.mKeyguardFadingAwayDuration = mKeyguardFadingAwayDuration;
        this.mBypassFadingAnimation = mBypassFadingAnimation;
        this.setKeyguardFadingAway(true);
    }
    
    @Override
    public void notifyKeyguardGoingAway(final boolean mKeyguardGoingAway) {
        this.mKeyguardGoingAway = mKeyguardGoingAway;
    }
    
    @Override
    public void notifyKeyguardState(final boolean mShowing, final boolean mOccluded) {
        if (this.mShowing == mShowing && this.mOccluded == mOccluded) {
            return;
        }
        this.mShowing = mShowing;
        this.mOccluded = mOccluded;
        this.notifyKeyguardChanged();
    }
    
    @Override
    public void removeCallback(final Callback callback) {
        Objects.requireNonNull(callback, "Callback must not be null. b/128895449");
        this.mCallbacks.remove(callback);
    }
    
    @Override
    public void setLaunchTransitionFadingAway(final boolean mLaunchTransitionFadingAway) {
        this.mLaunchTransitionFadingAway = mLaunchTransitionFadingAway;
    }
    
    void update(final boolean b) {
        Trace.beginSection("KeyguardStateController#update");
        final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        final boolean secure = this.mLockPatternUtils.isSecure(currentUser);
        boolean b2 = false;
        boolean mCanDismissLockScreen;
        if (secure && !this.mKeyguardUpdateMonitor.getUserCanSkipBouncer(currentUser)) {
            final boolean is_DEBUGGABLE = Build.IS_DEBUGGABLE;
            mCanDismissLockScreen = false;
        }
        else {
            mCanDismissLockScreen = true;
        }
        final boolean userTrustIsManaged = this.mKeyguardUpdateMonitor.getUserTrustIsManaged(currentUser);
        final boolean userHasTrust = this.mKeyguardUpdateMonitor.getUserHasTrust(currentUser);
        final boolean faceAuthEnabledForUser = this.mKeyguardUpdateMonitor.isFaceAuthEnabledForUser(currentUser);
        if (secure != this.mSecure || mCanDismissLockScreen != this.mCanDismissLockScreen || userTrustIsManaged != this.mTrustManaged || this.mTrusted != userHasTrust || this.mFaceAuthEnabled != faceAuthEnabledForUser) {
            b2 = true;
        }
        if (b2 || b) {
            this.mSecure = secure;
            this.mCanDismissLockScreen = mCanDismissLockScreen;
            this.mTrusted = userHasTrust;
            this.mTrustManaged = userTrustIsManaged;
            this.mFaceAuthEnabled = faceAuthEnabledForUser;
            this.notifyUnlockedChanged();
        }
        Trace.endSection();
    }
    
    private class UpdateMonitorCallback extends KeyguardUpdateMonitorCallback
    {
        @Override
        public void onBiometricAuthenticated(final int n, final BiometricSourceType biometricSourceType, final boolean b) {
            Trace.beginSection("KeyguardUpdateMonitorCallback#onBiometricAuthenticated");
            if (KeyguardStateControllerImpl.this.mKeyguardUpdateMonitor.isUnlockingWithBiometricAllowed(b)) {
                KeyguardStateControllerImpl.this.update(false);
            }
            Trace.endSection();
        }
        
        @Override
        public void onBiometricsCleared() {
            KeyguardStateControllerImpl.this.update(false);
        }
        
        @Override
        public void onFaceUnlockStateChanged(final boolean b, final int n) {
            KeyguardStateControllerImpl.this.update(false);
        }
        
        @Override
        public void onKeyguardVisibilityChanged(final boolean b) {
            KeyguardStateControllerImpl.this.update(false);
        }
        
        @Override
        public void onStartedWakingUp() {
            KeyguardStateControllerImpl.this.update(false);
        }
        
        @Override
        public void onStrongAuthStateChanged(final int n) {
            KeyguardStateControllerImpl.this.update(false);
        }
        
        @Override
        public void onTrustChanged(final int n) {
            KeyguardStateControllerImpl.this.update(false);
            KeyguardStateControllerImpl.this.notifyKeyguardChanged();
        }
        
        @Override
        public void onTrustManagedChanged(final int n) {
            KeyguardStateControllerImpl.this.update(false);
        }
        
        @Override
        public void onUserSwitchComplete(final int n) {
            KeyguardStateControllerImpl.this.update(false);
        }
    }
}
