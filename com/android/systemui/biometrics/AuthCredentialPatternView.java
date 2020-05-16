// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.biometrics;

import android.os.AsyncTask;
import com.android.internal.widget.LockPatternChecker$OnVerifyCallback;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.LockPatternView$Cell;
import java.util.List;
import com.android.internal.widget.LockPatternView$OnPatternListener;
import com.android.systemui.R$id;
import android.util.AttributeSet;
import android.content.Context;
import com.android.internal.widget.LockPatternView;

public class AuthCredentialPatternView extends AuthCredentialView
{
    private LockPatternView mLockPatternView;
    
    public AuthCredentialPatternView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        (this.mLockPatternView = (LockPatternView)this.findViewById(R$id.lockPattern)).setOnPatternListener((LockPatternView$OnPatternListener)new UnlockPatternListener());
        this.mLockPatternView.setInStealthMode(super.mLockPatternUtils.isVisiblePatternEnabled(super.mUserId) ^ true);
        this.mLockPatternView.setTactileFeedbackEnabled(super.mLockPatternUtils.isTactileFeedbackEnabled());
    }
    
    @Override
    protected void onErrorTimeoutFinish() {
        super.onErrorTimeoutFinish();
        this.mLockPatternView.setEnabled(true);
    }
    
    private class UnlockPatternListener implements LockPatternView$OnPatternListener
    {
        private void onPatternVerified(final byte[] array, final int n) {
            AuthCredentialPatternView.this.onCredentialVerified(array, n);
            if (n > 0) {
                AuthCredentialPatternView.this.mLockPatternView.setEnabled(false);
            }
            else {
                AuthCredentialPatternView.this.mLockPatternView.setEnabled(true);
            }
        }
        
        public void onPatternCellAdded(final List<LockPatternView$Cell> list) {
        }
        
        public void onPatternCleared() {
        }
        
        public void onPatternDetected(List<LockPatternView$Cell> pattern) {
            final AsyncTask<?, ?, ?> mPendingLockCheck = AuthCredentialPatternView.this.mPendingLockCheck;
            if (mPendingLockCheck != null) {
                mPendingLockCheck.cancel(false);
            }
            AuthCredentialPatternView.this.mLockPatternView.setEnabled(false);
            if (((List)pattern).size() < 4) {
                this.onPatternVerified(null, 0);
                return;
            }
            pattern = LockscreenCredential.createPattern((List)pattern);
            try {
                AuthCredentialPatternView.this.mPendingLockCheck = (AsyncTask<?, ?, ?>)LockPatternChecker.verifyCredential(AuthCredentialPatternView.this.mLockPatternUtils, pattern, AuthCredentialPatternView.this.mOperationId, AuthCredentialPatternView.this.mEffectiveUserId, (LockPatternChecker$OnVerifyCallback)new _$$Lambda$AuthCredentialPatternView$UnlockPatternListener$i26rXOj6tOr6sIKp7_ro_y07Tuw(this));
                if (pattern != null) {
                    pattern.close();
                }
            }
            finally {
                if (pattern != null) {
                    try {
                        pattern.close();
                    }
                    finally {
                        final Throwable t;
                        final Throwable exception;
                        t.addSuppressed(exception);
                    }
                }
            }
        }
        
        public void onPatternStart() {
        }
    }
}
