// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.animation.TimeInterpolator;
import android.graphics.Rect;
import android.view.View$OnClickListener;
import android.text.method.KeyListener;
import android.text.method.TextKeyListener;
import android.os.UserHandle;
import android.view.KeyEvent;
import com.android.systemui.R$string;
import com.android.systemui.R$id;
import com.android.internal.widget.LockscreenCredential;
import android.text.TextUtils;
import android.text.Editable;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup$MarginLayoutParams;
import java.util.List;
import java.util.Iterator;
import android.view.inputmethod.InputMethodSubtype;
import android.view.inputmethod.InputMethodInfo;
import android.view.animation.AnimationUtils;
import com.android.systemui.R$dimen;
import com.android.systemui.R$bool;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import com.android.internal.widget.TextViewInputDisabler;
import android.widget.TextView;
import android.view.inputmethod.InputMethodManager;
import android.view.animation.Interpolator;
import android.text.TextWatcher;
import android.widget.TextView$OnEditorActionListener;

public class KeyguardPasswordView extends KeyguardAbsKeyInputView implements KeyguardSecurityView, TextView$OnEditorActionListener, TextWatcher
{
    private final int mDisappearYTranslation;
    private Interpolator mFastOutLinearInInterpolator;
    InputMethodManager mImm;
    private Interpolator mLinearOutSlowInInterpolator;
    private TextView mPasswordEntry;
    private TextViewInputDisabler mPasswordEntryDisabler;
    private final boolean mShowImeAtScreenOn;
    private View mSwitchImeButton;
    
    public KeyguardPasswordView(final Context context) {
        this(context, null);
    }
    
    public KeyguardPasswordView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mShowImeAtScreenOn = context.getResources().getBoolean(R$bool.kg_show_ime_at_screen_on);
        this.mDisappearYTranslation = this.getResources().getDimensionPixelSize(R$dimen.disappear_y_translation);
        this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563662);
        this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(context, 17563663);
    }
    
    private boolean hasMultipleEnabledIMEsOrSubtypes(final InputMethodManager inputMethodManager, final boolean b) {
        final Iterator<InputMethodInfo> iterator = (Iterator<InputMethodInfo>)inputMethodManager.getEnabledInputMethodListAsUser(KeyguardUpdateMonitor.getCurrentUser()).iterator();
        final boolean b2 = false;
        int n = 0;
        while (iterator.hasNext()) {
            final InputMethodInfo inputMethodInfo = iterator.next();
            if (n > 1) {
                return true;
            }
            final List enabledInputMethodSubtypeList = inputMethodManager.getEnabledInputMethodSubtypeList(inputMethodInfo, true);
            if (!enabledInputMethodSubtypeList.isEmpty()) {
                final Iterator<InputMethodSubtype> iterator2 = enabledInputMethodSubtypeList.iterator();
                int n2 = 0;
                while (iterator2.hasNext()) {
                    if (iterator2.next().isAuxiliary()) {
                        ++n2;
                    }
                }
                if (enabledInputMethodSubtypeList.size() - n2 <= 0) {
                    if (!b || n2 <= 1) {
                        continue;
                    }
                }
            }
            ++n;
        }
        if (n <= 1) {
            final boolean b3 = b2;
            if (inputMethodManager.getEnabledInputMethodSubtypeList((InputMethodInfo)null, false).size() <= 1) {
                return b3;
            }
        }
        return true;
    }
    
    private void updateSwitchImeButton() {
        final boolean b = this.mSwitchImeButton.getVisibility() == 0;
        final boolean hasMultipleEnabledIMEsOrSubtypes = this.hasMultipleEnabledIMEsOrSubtypes(this.mImm, false);
        if (b != hasMultipleEnabledIMEsOrSubtypes) {
            final View mSwitchImeButton = this.mSwitchImeButton;
            int visibility;
            if (hasMultipleEnabledIMEsOrSubtypes) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            mSwitchImeButton.setVisibility(visibility);
        }
        if (this.mSwitchImeButton.getVisibility() != 0) {
            final ViewGroup$LayoutParams layoutParams = this.mPasswordEntry.getLayoutParams();
            if (layoutParams instanceof ViewGroup$MarginLayoutParams) {
                ((ViewGroup$MarginLayoutParams)layoutParams).setMarginStart(0);
                this.mPasswordEntry.setLayoutParams(layoutParams);
            }
        }
    }
    
    public void afterTextChanged(final Editable editable) {
        if (!TextUtils.isEmpty((CharSequence)editable)) {
            this.onUserInput();
        }
    }
    
    public void beforeTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
        final KeyguardSecurityCallback mCallback = super.mCallback;
        if (mCallback != null) {
            mCallback.userActivity();
        }
    }
    
    @Override
    protected LockscreenCredential getEnteredCredential() {
        return LockscreenCredential.createPasswordOrNone(this.mPasswordEntry.getText());
    }
    
    @Override
    protected int getPasswordTextViewId() {
        return R$id.passwordEntry;
    }
    
    @Override
    protected int getPromptReasonStringRes(final int n) {
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return R$string.kg_prompt_reason_restart_password;
        }
        if (n == 2) {
            return R$string.kg_prompt_reason_timeout_password;
        }
        if (n == 3) {
            return R$string.kg_prompt_reason_device_admin;
        }
        if (n == 4) {
            return R$string.kg_prompt_reason_user_request;
        }
        if (n != 6) {
            return R$string.kg_prompt_reason_timeout_password;
        }
        return R$string.kg_prompt_reason_prepare_for_update_password;
    }
    
    @Override
    public CharSequence getTitle() {
        return this.getContext().getString(17040355);
    }
    
    public int getWrongPasswordStringId() {
        return R$string.kg_wrong_password;
    }
    
    @Override
    public boolean needsInput() {
        return true;
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
        this.verifyPasswordAndUnlock();
        return true;
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mImm = (InputMethodManager)this.getContext().getSystemService("input_method");
        (this.mPasswordEntry = (TextView)this.findViewById(this.getPasswordTextViewId())).setTextOperationUser(UserHandle.of(KeyguardUpdateMonitor.getCurrentUser()));
        this.mPasswordEntryDisabler = new TextViewInputDisabler(this.mPasswordEntry);
        this.mPasswordEntry.setKeyListener((KeyListener)TextKeyListener.getInstance());
        this.mPasswordEntry.setInputType(129);
        this.mPasswordEntry.setOnEditorActionListener((TextView$OnEditorActionListener)this);
        this.mPasswordEntry.addTextChangedListener((TextWatcher)this);
        this.mPasswordEntry.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                KeyguardPasswordView.this.mCallback.userActivity();
            }
        });
        this.mPasswordEntry.setSelected(true);
        (this.mSwitchImeButton = this.findViewById(R$id.switch_ime_button)).setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                KeyguardPasswordView.this.mCallback.userActivity();
                final KeyguardPasswordView this$0 = KeyguardPasswordView.this;
                this$0.mImm.showInputMethodPickerFromSystem(false, this$0.getContext().getDisplayId());
            }
        });
        final View viewById = this.findViewById(R$id.cancel_button);
        if (viewById != null) {
            viewById.setOnClickListener((View$OnClickListener)new _$$Lambda$KeyguardPasswordView$o6rdkANQuxgpLXMWWI2lzhbd_0k(this));
        }
        this.updateSwitchImeButton();
        this.postDelayed((Runnable)new Runnable() {
            @Override
            public void run() {
                KeyguardPasswordView.this.updateSwitchImeButton();
            }
        }, 500L);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        this.mImm.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }
    
    protected boolean onRequestFocusInDescendants(final int n, final Rect rect) {
        return this.mPasswordEntry.requestFocus(n, rect);
    }
    
    @Override
    public void onResume(final int n) {
        super.onResume(n);
        this.post((Runnable)new Runnable() {
            @Override
            public void run() {
                if (KeyguardPasswordView.this.isShown() && KeyguardPasswordView.this.mPasswordEntry.isEnabled()) {
                    KeyguardPasswordView.this.mPasswordEntry.requestFocus();
                    if (n != 1 || KeyguardPasswordView.this.mShowImeAtScreenOn) {
                        final KeyguardPasswordView this$0 = KeyguardPasswordView.this;
                        this$0.mImm.showSoftInput((View)this$0.mPasswordEntry, 1);
                    }
                }
            }
        });
    }
    
    public void onTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
    }
    
    @Override
    protected void resetPasswordText(final boolean b, final boolean b2) {
        this.mPasswordEntry.setText((CharSequence)"");
    }
    
    @Override
    protected void resetState() {
        this.mPasswordEntry.setTextOperationUser(UserHandle.of(KeyguardUpdateMonitor.getCurrentUser()));
        final SecurityMessageDisplay mSecurityMessageDisplay = super.mSecurityMessageDisplay;
        if (mSecurityMessageDisplay != null) {
            mSecurityMessageDisplay.setMessage("");
        }
        final boolean enabled = this.mPasswordEntry.isEnabled();
        this.setPasswordEntryEnabled(true);
        this.setPasswordEntryInputEnabled(true);
        if (super.mResumed) {
            if (this.mPasswordEntry.isVisibleToUser()) {
                if (enabled) {
                    this.mImm.showSoftInput((View)this.mPasswordEntry, 1);
                }
            }
        }
    }
    
    @Override
    protected void setPasswordEntryEnabled(final boolean enabled) {
        this.mPasswordEntry.setEnabled(enabled);
    }
    
    @Override
    protected void setPasswordEntryInputEnabled(final boolean inputEnabled) {
        this.mPasswordEntryDisabler.setInputEnabled(inputEnabled);
    }
    
    @Override
    public void startAppearAnimation() {
        this.setAlpha(0.0f);
        this.setTranslationY(0.0f);
        this.animate().alpha(1.0f).withLayer().setDuration(300L).setInterpolator((TimeInterpolator)this.mLinearOutSlowInInterpolator);
    }
    
    @Override
    public boolean startDisappearAnimation(final Runnable runnable) {
        this.animate().alpha(0.0f).translationY((float)this.mDisappearYTranslation).setInterpolator((TimeInterpolator)this.mFastOutLinearInInterpolator).setDuration(100L).withEndAction(runnable);
        return true;
    }
}
