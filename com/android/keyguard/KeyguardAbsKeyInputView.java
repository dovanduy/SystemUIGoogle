// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternChecker$OnCheckCallback;
import com.android.internal.util.LatencyTracker;
import android.content.res.ColorStateList;
import android.view.KeyEvent;
import com.android.systemui.R$id;
import com.android.systemui.R$plurals;
import android.os.SystemClock;
import com.android.systemui.R$string;
import com.android.internal.widget.LockscreenCredential;
import com.android.systemui.Dependency;
import android.util.AttributeSet;
import android.content.Context;
import android.os.AsyncTask;
import com.android.internal.widget.LockPatternUtils;
import android.view.View;
import android.os.CountDownTimer;
import android.widget.LinearLayout;

public abstract class KeyguardAbsKeyInputView extends LinearLayout implements KeyguardSecurityView, EmergencyButtonCallback
{
    protected KeyguardSecurityCallback mCallback;
    private CountDownTimer mCountdownTimer;
    private boolean mDismissing;
    protected View mEcaView;
    protected boolean mEnableHaptics;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    protected LockPatternUtils mLockPatternUtils;
    protected AsyncTask<?, ?, ?> mPendingLockCheck;
    protected boolean mResumed;
    protected SecurityMessageDisplay mSecurityMessageDisplay;
    
    public KeyguardAbsKeyInputView(final Context context) {
        this(context, null);
    }
    
    public KeyguardAbsKeyInputView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mCountdownTimer = null;
        this.mKeyguardUpdateMonitor = Dependency.get(KeyguardUpdateMonitor.class);
    }
    
    private void onPasswordChecked(final int n, final boolean b, final int n2, final boolean b2) {
        final boolean b3 = KeyguardUpdateMonitor.getCurrentUser() == n;
        if (b) {
            this.mCallback.reportUnlockAttempt(n, true, 0);
            if (b3) {
                this.mDismissing = true;
                this.mCallback.dismiss(true, n);
            }
        }
        else {
            if (b2) {
                this.mCallback.reportUnlockAttempt(n, false, n2);
                if (n2 > 0) {
                    this.handleAttemptLockout(this.mLockPatternUtils.setLockoutAttemptDeadline(n, n2));
                }
            }
            if (n2 == 0) {
                this.mSecurityMessageDisplay.setMessage(this.getWrongPasswordStringId());
            }
        }
        this.resetPasswordText(true, b ^ true);
    }
    
    public void doHapticKeyClick() {
        if (this.mEnableHaptics) {
            this.performHapticFeedback(1, 3);
        }
    }
    
    protected abstract LockscreenCredential getEnteredCredential();
    
    protected abstract int getPasswordTextViewId();
    
    protected abstract int getPromptReasonStringRes(final int p0);
    
    protected int getWrongPasswordStringId() {
        return R$string.kg_wrong_password;
    }
    
    protected void handleAttemptLockout(final long n) {
        this.setPasswordEntryEnabled(false);
        this.mCountdownTimer = new CountDownTimer((long)Math.ceil((n - SystemClock.elapsedRealtime()) / 1000.0) * 1000L, 1000L) {
            public void onFinish() {
                KeyguardAbsKeyInputView.this.mSecurityMessageDisplay.setMessage("");
                KeyguardAbsKeyInputView.this.resetState();
            }
            
            public void onTick(final long n) {
                final int i = (int)Math.round(n / 1000.0);
                final KeyguardAbsKeyInputView this$0 = KeyguardAbsKeyInputView.this;
                this$0.mSecurityMessageDisplay.setMessage(this$0.mContext.getResources().getQuantityString(R$plurals.kg_too_many_failed_attempts_countdown, i, new Object[] { i }));
            }
        }.start();
    }
    
    public boolean needsInput() {
        return false;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSecurityMessageDisplay = KeyguardMessageArea.findSecurityMessageDisplay((View)this);
    }
    
    public void onEmergencyButtonClickedWhenInCall() {
        this.mCallback.reset();
    }
    
    protected void onFinishInflate() {
        this.mLockPatternUtils = new LockPatternUtils(super.mContext);
        this.mEcaView = this.findViewById(R$id.keyguard_selector_fade_container);
        final EmergencyButton emergencyButton = (EmergencyButton)this.findViewById(R$id.emergency_call_button);
        if (emergencyButton != null) {
            emergencyButton.setCallback((EmergencyButton.EmergencyButtonCallback)this);
        }
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        if (n != 0) {
            this.onUserInput();
        }
        return false;
    }
    
    public void onPause() {
        this.mResumed = false;
        final CountDownTimer mCountdownTimer = this.mCountdownTimer;
        if (mCountdownTimer != null) {
            mCountdownTimer.cancel();
            this.mCountdownTimer = null;
        }
        final AsyncTask<?, ?, ?> mPendingLockCheck = this.mPendingLockCheck;
        if (mPendingLockCheck != null) {
            mPendingLockCheck.cancel(false);
            this.mPendingLockCheck = null;
        }
        this.reset();
    }
    
    public void onResume(final int n) {
        this.mResumed = true;
    }
    
    protected void onUserInput() {
        final KeyguardSecurityCallback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.userActivity();
            this.mCallback.onUserInput();
        }
        this.mSecurityMessageDisplay.setMessage("");
    }
    
    public void reset() {
        this.resetPasswordText(this.mDismissing = false, false);
        final long lockoutAttemptDeadline = this.mLockPatternUtils.getLockoutAttemptDeadline(KeyguardUpdateMonitor.getCurrentUser());
        if (this.shouldLockout(lockoutAttemptDeadline)) {
            this.handleAttemptLockout(lockoutAttemptDeadline);
        }
        else {
            this.resetState();
        }
    }
    
    protected abstract void resetPasswordText(final boolean p0, final boolean p1);
    
    protected abstract void resetState();
    
    public void setKeyguardCallback(final KeyguardSecurityCallback mCallback) {
        this.mCallback = mCallback;
    }
    
    public void setLockPatternUtils(final LockPatternUtils mLockPatternUtils) {
        this.mLockPatternUtils = mLockPatternUtils;
        this.mEnableHaptics = mLockPatternUtils.isTactileFeedbackEnabled();
    }
    
    protected abstract void setPasswordEntryEnabled(final boolean p0);
    
    protected abstract void setPasswordEntryInputEnabled(final boolean p0);
    
    protected boolean shouldLockout(final long n) {
        return n != 0L;
    }
    
    public void showMessage(final CharSequence message, final ColorStateList nextMessageColor) {
        if (nextMessageColor != null) {
            this.mSecurityMessageDisplay.setNextMessageColor(nextMessageColor);
        }
        this.mSecurityMessageDisplay.setMessage(message);
    }
    
    public void showPromptReason(int promptReasonStringRes) {
        if (promptReasonStringRes != 0) {
            promptReasonStringRes = this.getPromptReasonStringRes(promptReasonStringRes);
            if (promptReasonStringRes != 0) {
                this.mSecurityMessageDisplay.setMessage(promptReasonStringRes);
            }
        }
    }
    
    public boolean startDisappearAnimation(final Runnable runnable) {
        return false;
    }
    
    protected void verifyPasswordAndUnlock() {
        if (this.mDismissing) {
            return;
        }
        final LockscreenCredential enteredCredential = this.getEnteredCredential();
        this.setPasswordEntryInputEnabled(false);
        final AsyncTask<?, ?, ?> mPendingLockCheck = this.mPendingLockCheck;
        if (mPendingLockCheck != null) {
            mPendingLockCheck.cancel(false);
        }
        final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        if (enteredCredential.size() <= 3) {
            this.setPasswordEntryInputEnabled(true);
            this.onPasswordChecked(currentUser, false, 0, false);
            enteredCredential.zeroize();
            return;
        }
        if (LatencyTracker.isEnabled(super.mContext)) {
            LatencyTracker.getInstance(super.mContext).onActionStart(3);
            LatencyTracker.getInstance(super.mContext).onActionStart(4);
        }
        this.mKeyguardUpdateMonitor.setCredentialAttempted();
        this.mPendingLockCheck = (AsyncTask<?, ?, ?>)LockPatternChecker.checkCredential(this.mLockPatternUtils, enteredCredential, currentUser, (LockPatternChecker$OnCheckCallback)new LockPatternChecker$OnCheckCallback() {
            public void onCancelled() {
                if (LatencyTracker.isEnabled(KeyguardAbsKeyInputView.this.mContext)) {
                    LatencyTracker.getInstance(KeyguardAbsKeyInputView.this.mContext).onActionEnd(4);
                }
                enteredCredential.zeroize();
            }
            
            public void onChecked(final boolean b, final int n) {
                if (LatencyTracker.isEnabled(KeyguardAbsKeyInputView.this.mContext)) {
                    LatencyTracker.getInstance(KeyguardAbsKeyInputView.this.mContext).onActionEnd(4);
                }
                KeyguardAbsKeyInputView.this.setPasswordEntryInputEnabled(true);
                final KeyguardAbsKeyInputView this$0 = KeyguardAbsKeyInputView.this;
                this$0.mPendingLockCheck = null;
                if (!b) {
                    this$0.onPasswordChecked(currentUser, false, n, true);
                }
                enteredCredential.zeroize();
            }
            
            public void onEarlyMatched() {
                if (LatencyTracker.isEnabled(KeyguardAbsKeyInputView.this.mContext)) {
                    LatencyTracker.getInstance(KeyguardAbsKeyInputView.this.mContext).onActionEnd(3);
                }
                KeyguardAbsKeyInputView.this.onPasswordChecked(currentUser, true, 0, true);
                enteredCredential.zeroize();
            }
        });
    }
}
