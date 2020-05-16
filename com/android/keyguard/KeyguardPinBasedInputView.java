// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.view.MotionEvent;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.View$OnLongClickListener;
import android.view.View$OnHoverListener;
import android.view.View$OnClickListener;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.internal.widget.LockscreenCredential;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.view.View$OnTouchListener;
import android.view.View$OnKeyListener;

public abstract class KeyguardPinBasedInputView extends KeyguardAbsKeyInputView implements View$OnKeyListener, View$OnTouchListener
{
    private View mButton0;
    private View mButton1;
    private View mButton2;
    private View mButton3;
    private View mButton4;
    private View mButton5;
    private View mButton6;
    private View mButton7;
    private View mButton8;
    private View mButton9;
    private View mDeleteButton;
    private View mOkButton;
    protected PasswordTextView mPasswordEntry;
    
    public KeyguardPinBasedInputView(final Context context) {
        this(context, null);
    }
    
    public KeyguardPinBasedInputView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    private void performClick(final View view) {
        view.performClick();
    }
    
    private void performNumberClick(final int n) {
        switch (n) {
            case 9: {
                this.performClick(this.mButton9);
                break;
            }
            case 8: {
                this.performClick(this.mButton8);
                break;
            }
            case 7: {
                this.performClick(this.mButton7);
                break;
            }
            case 6: {
                this.performClick(this.mButton6);
                break;
            }
            case 5: {
                this.performClick(this.mButton5);
                break;
            }
            case 4: {
                this.performClick(this.mButton4);
                break;
            }
            case 3: {
                this.performClick(this.mButton3);
                break;
            }
            case 2: {
                this.performClick(this.mButton2);
                break;
            }
            case 1: {
                this.performClick(this.mButton1);
                break;
            }
            case 0: {
                this.performClick(this.mButton0);
                break;
            }
        }
    }
    
    @Override
    protected LockscreenCredential getEnteredCredential() {
        return LockscreenCredential.createPinOrNone((CharSequence)this.mPasswordEntry.getText());
    }
    
    @Override
    protected int getPromptReasonStringRes(final int n) {
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return R$string.kg_prompt_reason_restart_pin;
        }
        if (n == 2) {
            return R$string.kg_prompt_reason_timeout_pin;
        }
        if (n == 3) {
            return R$string.kg_prompt_reason_device_admin;
        }
        if (n == 4) {
            return R$string.kg_prompt_reason_user_request;
        }
        if (n != 6) {
            return R$string.kg_prompt_reason_timeout_pin;
        }
        return R$string.kg_prompt_reason_prepare_for_update_pin;
    }
    
    public CharSequence getTitle() {
        return this.getContext().getString(17040358);
    }
    
    @Override
    protected void onFinishInflate() {
        (this.mPasswordEntry = (PasswordTextView)this.findViewById(this.getPasswordTextViewId())).setOnKeyListener((View$OnKeyListener)this);
        this.mPasswordEntry.setSelected(true);
        this.mPasswordEntry.setUserActivityListener((PasswordTextView.UserActivityListener)new PasswordTextView.UserActivityListener() {
            @Override
            public void onUserActivity() {
                KeyguardPinBasedInputView.this.onUserInput();
            }
        });
        final View viewById = this.findViewById(R$id.key_enter);
        this.mOkButton = viewById;
        if (viewById != null) {
            viewById.setOnTouchListener((View$OnTouchListener)this);
            this.mOkButton.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                public void onClick(final View view) {
                    if (KeyguardPinBasedInputView.this.mPasswordEntry.isEnabled()) {
                        KeyguardPinBasedInputView.this.verifyPasswordAndUnlock();
                    }
                }
            });
            this.mOkButton.setOnHoverListener((View$OnHoverListener)new LiftToActivateListener(this.getContext()));
        }
        (this.mDeleteButton = this.findViewById(R$id.delete_button)).setVisibility(0);
        this.mDeleteButton.setOnTouchListener((View$OnTouchListener)this);
        this.mDeleteButton.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                if (KeyguardPinBasedInputView.this.mPasswordEntry.isEnabled()) {
                    KeyguardPinBasedInputView.this.mPasswordEntry.deleteLastChar();
                }
            }
        });
        this.mDeleteButton.setOnLongClickListener((View$OnLongClickListener)new View$OnLongClickListener() {
            public boolean onLongClick(final View view) {
                if (KeyguardPinBasedInputView.this.mPasswordEntry.isEnabled()) {
                    KeyguardPinBasedInputView.this.resetPasswordText(true, true);
                }
                KeyguardPinBasedInputView.this.doHapticKeyClick();
                return true;
            }
        });
        this.mButton0 = this.findViewById(R$id.key0);
        this.mButton1 = this.findViewById(R$id.key1);
        this.mButton2 = this.findViewById(R$id.key2);
        this.mButton3 = this.findViewById(R$id.key3);
        this.mButton4 = this.findViewById(R$id.key4);
        this.mButton5 = this.findViewById(R$id.key5);
        this.mButton6 = this.findViewById(R$id.key6);
        this.mButton7 = this.findViewById(R$id.key7);
        this.mButton8 = this.findViewById(R$id.key8);
        this.mButton9 = this.findViewById(R$id.key9);
        this.mPasswordEntry.requestFocus();
        super.onFinishInflate();
    }
    
    public boolean onKey(final View view, final int n, final KeyEvent keyEvent) {
        return keyEvent.getAction() == 0 && this.onKeyDown(n, keyEvent);
    }
    
    @Override
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        if (KeyEvent.isConfirmKey(n)) {
            this.performClick(this.mOkButton);
            return true;
        }
        if (n == 67) {
            this.performClick(this.mDeleteButton);
            return true;
        }
        if (n >= 7 && n <= 16) {
            this.performNumberClick(n - 7);
            return true;
        }
        if (n >= 144 && n <= 153) {
            this.performNumberClick(n - 144);
            return true;
        }
        return super.onKeyDown(n, keyEvent);
    }
    
    protected boolean onRequestFocusInDescendants(final int n, final Rect rect) {
        return this.mPasswordEntry.requestFocus(n, rect);
    }
    
    @Override
    public void onResume(final int n) {
        super.onResume(n);
        this.mPasswordEntry.requestFocus();
    }
    
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            this.doHapticKeyClick();
        }
        return false;
    }
    
    @Override
    protected void resetPasswordText(final boolean b, final boolean b2) {
        this.mPasswordEntry.reset(b, b2);
    }
    
    @Override
    protected void resetState() {
        this.setPasswordEntryEnabled(true);
    }
    
    @Override
    protected void setPasswordEntryEnabled(final boolean b) {
        this.mPasswordEntry.setEnabled(b);
        this.mOkButton.setEnabled(b);
        if (b && !this.mPasswordEntry.hasFocus()) {
            this.mPasswordEntry.requestFocus();
        }
    }
    
    @Override
    protected void setPasswordEntryInputEnabled(final boolean b) {
        this.mPasswordEntry.setEnabled(b);
        this.mOkButton.setEnabled(b);
        if (b && !this.mPasswordEntry.hasFocus()) {
            this.mPasswordEntry.requestFocus();
        }
    }
}
