// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.biometrics;

import android.view.View$OnKeyListener;
import com.android.systemui.R$id;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.View;
import com.android.internal.widget.LockPatternChecker$OnVerifyCallback;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockscreenCredential;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.EditText;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView$OnEditorActionListener;

public class AuthCredentialPasswordView extends AuthCredentialView implements TextView$OnEditorActionListener
{
    private final InputMethodManager mImm;
    private EditText mPasswordField;
    
    public AuthCredentialPasswordView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mImm = (InputMethodManager)super.mContext.getSystemService((Class)InputMethodManager.class);
    }
    
    private void checkPasswordAndUnlock() {
        LockscreenCredential lockscreenCredential;
        if (super.mCredentialType == 1) {
            lockscreenCredential = LockscreenCredential.createPinOrNone((CharSequence)this.mPasswordField.getText());
        }
        else {
            lockscreenCredential = LockscreenCredential.createPasswordOrNone((CharSequence)this.mPasswordField.getText());
        }
        try {
            if (lockscreenCredential.isNone()) {
                if (lockscreenCredential != null) {
                    lockscreenCredential.close();
                }
                return;
            }
            super.mPendingLockCheck = LockPatternChecker.verifyCredential(super.mLockPatternUtils, lockscreenCredential, super.mOperationId, super.mEffectiveUserId, (LockPatternChecker$OnVerifyCallback)new _$$Lambda$bUxMWJPKQYxZ29Sl7YhLi_sbYZU(this));
            if (lockscreenCredential != null) {
                lockscreenCredential.close();
            }
        }
        finally {
            if (lockscreenCredential != null) {
                try {
                    lockscreenCredential.close();
                }
                finally {
                    final Throwable t;
                    final Throwable exception;
                    t.addSuppressed(exception);
                }
            }
        }
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (super.mCredentialType == 1) {
            this.mPasswordField.setInputType(18);
        }
        this.postDelayed((Runnable)new _$$Lambda$AuthCredentialPasswordView$SrJUZA3LjIQN_kd53ey7sYUE8ZM(this), 100L);
    }
    
    @Override
    protected void onCredentialVerified(final byte[] array, int n) {
        super.onCredentialVerified(array, n);
        if (array != null) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n != 0) {
            this.mImm.hideSoftInputFromWindow(this.getWindowToken(), 0);
        }
        else {
            this.mPasswordField.setText((CharSequence)"");
        }
    }
    
    public boolean onEditorAction(final TextView textView, int n, final KeyEvent keyEvent) {
        if (keyEvent == null && (n == 0 || n == 6 || n == 5)) {
            n = 1;
        }
        else {
            n = 0;
        }
        final boolean b = keyEvent != null && KeyEvent.isConfirmKey(keyEvent.getKeyCode()) && keyEvent.getAction() == 0;
        if (n == 0 && !b) {
            return false;
        }
        this.checkPasswordAndUnlock();
        return true;
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        (this.mPasswordField = (EditText)this.findViewById(R$id.lockPassword)).setOnEditorActionListener((TextView$OnEditorActionListener)this);
        this.mPasswordField.setOnKeyListener((View$OnKeyListener)new _$$Lambda$AuthCredentialPasswordView$uBlA6RM9f63nDB7lqx6FDe1sZjk(this));
    }
}
