// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.biometrics;

import android.os.CountDownTimer;
import com.android.systemui.R$id;
import android.os.SystemClock;
import android.graphics.drawable.Drawable;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import android.content.DialogInterface$OnDismissListener;
import android.app.AlertDialog;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.view.ViewGroup;
import android.text.TextUtils;
import android.content.DialogInterface;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.content.pm.UserInfo;
import com.android.systemui.R$string;
import android.os.Looper;
import android.util.AttributeSet;
import android.content.Context;
import android.os.UserManager;
import android.os.AsyncTask;
import com.android.internal.widget.LockPatternUtils;
import android.widget.ImageView;
import android.os.Handler;
import android.app.admin.DevicePolicyManager;
import android.widget.TextView;
import android.os.Bundle;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;

public abstract class AuthCredentialView extends LinearLayout
{
    private final AccessibilityManager mAccessibilityManager;
    private Bundle mBiometricPromptBundle;
    protected Callback mCallback;
    protected final Runnable mClearErrorRunnable;
    protected AuthContainerView mContainerView;
    protected int mCredentialType;
    private TextView mDescriptionView;
    private final DevicePolicyManager mDevicePolicyManager;
    protected int mEffectiveUserId;
    protected ErrorTimer mErrorTimer;
    protected TextView mErrorView;
    protected final Handler mHandler;
    private ImageView mIconView;
    protected final LockPatternUtils mLockPatternUtils;
    protected long mOperationId;
    private AuthPanelController mPanelController;
    protected AsyncTask<?, ?, ?> mPendingLockCheck;
    private boolean mShouldAnimateContents;
    private boolean mShouldAnimatePanel;
    private TextView mSubtitleView;
    private TextView mTitleView;
    protected int mUserId;
    private final UserManager mUserManager;
    
    public AuthCredentialView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mClearErrorRunnable = new Runnable() {
            @Override
            public void run() {
                final TextView mErrorView = AuthCredentialView.this.mErrorView;
                if (mErrorView != null) {
                    mErrorView.setText((CharSequence)"");
                }
            }
        };
        this.mLockPatternUtils = new LockPatternUtils(super.mContext);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mAccessibilityManager = (AccessibilityManager)super.mContext.getSystemService((Class)AccessibilityManager.class);
        this.mUserManager = (UserManager)super.mContext.getSystemService((Class)UserManager.class);
        this.mDevicePolicyManager = (DevicePolicyManager)super.mContext.getSystemService((Class)DevicePolicyManager.class);
    }
    
    private static CharSequence getDescription(final Bundle bundle) {
        final CharSequence charSequence = bundle.getCharSequence("device_credential_description");
        CharSequence charSequence2;
        if (charSequence != null) {
            charSequence2 = charSequence;
        }
        else {
            charSequence2 = bundle.getCharSequence("description");
        }
        return charSequence2;
    }
    
    private static int getLastAttemptBeforeWipeDeviceMessageRes(final int n) {
        if (n == 1) {
            return R$string.biometric_dialog_last_pin_attempt_before_wipe_device;
        }
        if (n != 2) {
            return R$string.biometric_dialog_last_password_attempt_before_wipe_device;
        }
        return R$string.biometric_dialog_last_pattern_attempt_before_wipe_device;
    }
    
    private static int getLastAttemptBeforeWipeMessageRes(final int i, final int n) {
        if (i == 1) {
            return getLastAttemptBeforeWipeDeviceMessageRes(n);
        }
        if (i == 2) {
            return getLastAttemptBeforeWipeProfileMessageRes(n);
        }
        if (i == 3) {
            return getLastAttemptBeforeWipeUserMessageRes(n);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Unrecognized user type:");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }
    
    private static int getLastAttemptBeforeWipeProfileMessageRes(final int n) {
        if (n == 1) {
            return R$string.biometric_dialog_last_pin_attempt_before_wipe_profile;
        }
        if (n != 2) {
            return R$string.biometric_dialog_last_password_attempt_before_wipe_profile;
        }
        return R$string.biometric_dialog_last_pattern_attempt_before_wipe_profile;
    }
    
    private static int getLastAttemptBeforeWipeUserMessageRes(final int n) {
        if (n == 1) {
            return R$string.biometric_dialog_last_pin_attempt_before_wipe_user;
        }
        if (n != 2) {
            return R$string.biometric_dialog_last_password_attempt_before_wipe_user;
        }
        return R$string.biometric_dialog_last_pattern_attempt_before_wipe_user;
    }
    
    private static int getNowWipingMessageRes(final int i) {
        if (i == 1) {
            return R$string.biometric_dialog_failed_attempts_now_wiping_device;
        }
        if (i == 2) {
            return R$string.biometric_dialog_failed_attempts_now_wiping_profile;
        }
        if (i == 3) {
            return R$string.biometric_dialog_failed_attempts_now_wiping_user;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Unrecognized user type:");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }
    
    private static CharSequence getSubtitle(final Bundle bundle) {
        final CharSequence charSequence = bundle.getCharSequence("device_credential_subtitle");
        CharSequence charSequence2;
        if (charSequence != null) {
            charSequence2 = charSequence;
        }
        else {
            charSequence2 = bundle.getCharSequence("subtitle");
        }
        return charSequence2;
    }
    
    private static CharSequence getTitle(final Bundle bundle) {
        final CharSequence charSequence = bundle.getCharSequence("device_credential_title");
        CharSequence charSequence2;
        if (charSequence != null) {
            charSequence2 = charSequence;
        }
        else {
            charSequence2 = bundle.getCharSequence("title");
        }
        return charSequence2;
    }
    
    private int getUserTypeForWipe() {
        final UserInfo userInfo = this.mUserManager.getUserInfo(this.mDevicePolicyManager.getProfileWithMinimumFailedPasswordsForWipe(this.mEffectiveUserId));
        if (userInfo == null || userInfo.isPrimary()) {
            return 1;
        }
        if (userInfo.isManagedProfile()) {
            return 2;
        }
        return 3;
    }
    
    private boolean reportFailedAttempt() {
        final boolean updateErrorMessage = this.updateErrorMessage(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(this.mEffectiveUserId) + 1);
        this.mLockPatternUtils.reportFailedPasswordAttempt(this.mEffectiveUserId);
        return updateErrorMessage;
    }
    
    private void setText(final TextView textView, final CharSequence text) {
        textView.setText(text);
    }
    
    private void setTextOrHide(final TextView textView, final CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(8);
        }
        else {
            textView.setText(text);
        }
        Utils.notifyAccessibilityContentChanged(this.mAccessibilityManager, (ViewGroup)this);
    }
    
    private void showLastAttemptBeforeWipeDialog() {
        final AlertDialog create = new AlertDialog$Builder(super.mContext).setTitle(R$string.biometric_dialog_last_attempt_before_wipe_dialog_title).setMessage(getLastAttemptBeforeWipeMessageRes(this.getUserTypeForWipe(), this.mCredentialType)).setPositiveButton(17039370, (DialogInterface$OnClickListener)null).create();
        create.getWindow().setType(2017);
        create.show();
    }
    
    private void showNowWipingDialog() {
        final AlertDialog create = new AlertDialog$Builder(super.mContext).setMessage(getNowWipingMessageRes(this.getUserTypeForWipe())).setPositiveButton(R$string.biometric_dialog_now_wiping_dialog_dismiss, (DialogInterface$OnClickListener)null).setOnDismissListener((DialogInterface$OnDismissListener)new _$$Lambda$AuthCredentialView$BXXne_WVQqWIG1IKb_D_tTyLfJQ(this)).create();
        create.getWindow().setType(2017);
        create.show();
    }
    
    private boolean updateErrorMessage(int i) {
        final int maximumFailedPasswordsForWipe = this.mLockPatternUtils.getMaximumFailedPasswordsForWipe(this.mEffectiveUserId);
        if (maximumFailedPasswordsForWipe > 0 && i > 0) {
            if (this.mErrorView != null) {
                this.showError(this.getResources().getString(R$string.biometric_dialog_credential_attempts_before_wipe, new Object[] { i, maximumFailedPasswordsForWipe }));
            }
            i = maximumFailedPasswordsForWipe - i;
            if (i == 1) {
                this.showLastAttemptBeforeWipeDialog();
            }
            else if (i <= 0) {
                this.showNowWipingDialog();
            }
            return true;
        }
        return false;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final CharSequence title = getTitle(this.mBiometricPromptBundle);
        this.setText(this.mTitleView, title);
        this.setTextOrHide(this.mSubtitleView, getSubtitle(this.mBiometricPromptBundle));
        this.setTextOrHide(this.mDescriptionView, getDescription(this.mBiometricPromptBundle));
        this.announceForAccessibility(title);
        Drawable imageDrawable;
        if (Utils.isManagedProfile(super.mContext, this.mEffectiveUserId)) {
            imageDrawable = this.getResources().getDrawable(R$drawable.auth_dialog_enterprise, super.mContext.getTheme());
        }
        else {
            imageDrawable = this.getResources().getDrawable(R$drawable.auth_dialog_lock, super.mContext.getTheme());
        }
        this.mIconView.setImageDrawable(imageDrawable);
        if (this.mShouldAnimateContents) {
            this.setTranslationY(this.getResources().getDimension(R$dimen.biometric_dialog_credential_translation_offset));
            this.setAlpha(0.0f);
            this.postOnAnimation((Runnable)new _$$Lambda$AuthCredentialView$KVtRMfNSJ6YMQd7FjO_ZTh576v4(this));
        }
    }
    
    protected void onCredentialVerified(final byte[] array, int n) {
        if (array != null) {
            this.mClearErrorRunnable.run();
            this.mCallback.onCredentialMatched(array);
        }
        else if (n > 0) {
            this.mHandler.removeCallbacks(this.mClearErrorRunnable);
            (this.mErrorTimer = (ErrorTimer)new ErrorTimer(super.mContext, this.mLockPatternUtils.setLockoutAttemptDeadline(this.mEffectiveUserId, n) - SystemClock.elapsedRealtime(), 1000L, this.mErrorView) {
                public void onFinish() {
                    AuthCredentialView.this.onErrorTimeoutFinish();
                    AuthCredentialView.this.mClearErrorRunnable.run();
                }
            }).start();
        }
        else if (!this.reportFailedAttempt()) {
            n = this.mCredentialType;
            if (n != 1) {
                if (n != 2) {
                    n = R$string.biometric_dialog_wrong_password;
                }
                else {
                    n = R$string.biometric_dialog_wrong_pattern;
                }
            }
            else {
                n = R$string.biometric_dialog_wrong_pin;
            }
            this.showError(this.getResources().getString(n));
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final ErrorTimer mErrorTimer = this.mErrorTimer;
        if (mErrorTimer != null) {
            mErrorTimer.cancel();
        }
    }
    
    protected void onErrorTimeoutFinish() {
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTitleView = (TextView)this.findViewById(R$id.title);
        this.mSubtitleView = (TextView)this.findViewById(R$id.subtitle);
        this.mDescriptionView = (TextView)this.findViewById(R$id.description);
        this.mIconView = (ImageView)this.findViewById(R$id.icon);
        this.mErrorView = (TextView)this.findViewById(R$id.error);
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        if (this.mShouldAnimatePanel) {
            this.mPanelController.setUseFullScreen(true);
            final AuthPanelController mPanelController = this.mPanelController;
            mPanelController.updateForContentDimensions(mPanelController.getContainerWidth(), this.mPanelController.getContainerHeight(), 0);
            this.mShouldAnimatePanel = false;
        }
    }
    
    void setBiometricPromptBundle(final Bundle mBiometricPromptBundle) {
        this.mBiometricPromptBundle = mBiometricPromptBundle;
    }
    
    void setCallback(final Callback mCallback) {
        this.mCallback = mCallback;
    }
    
    void setContainerView(final AuthContainerView mContainerView) {
        this.mContainerView = mContainerView;
    }
    
    void setCredentialType(final int mCredentialType) {
        this.mCredentialType = mCredentialType;
    }
    
    void setEffectiveUserId(final int mEffectiveUserId) {
        this.mEffectiveUserId = mEffectiveUserId;
    }
    
    void setOperationId(final long mOperationId) {
        this.mOperationId = mOperationId;
    }
    
    void setPanelController(final AuthPanelController mPanelController, final boolean mShouldAnimatePanel) {
        this.mPanelController = mPanelController;
        this.mShouldAnimatePanel = mShouldAnimatePanel;
    }
    
    void setShouldAnimateContents(final boolean mShouldAnimateContents) {
        this.mShouldAnimateContents = mShouldAnimateContents;
    }
    
    void setUserId(final int mUserId) {
        this.mUserId = mUserId;
    }
    
    protected void showError(final String text) {
        final Handler mHandler = this.mHandler;
        if (mHandler != null) {
            mHandler.removeCallbacks(this.mClearErrorRunnable);
            this.mHandler.postDelayed(this.mClearErrorRunnable, 3000L);
        }
        final TextView mErrorView = this.mErrorView;
        if (mErrorView != null) {
            mErrorView.setText((CharSequence)text);
        }
    }
    
    interface Callback
    {
        void onCredentialMatched(final byte[] p0);
    }
    
    protected static class ErrorTimer extends CountDownTimer
    {
        private final Context mContext;
        private final TextView mErrorView;
        
        public ErrorTimer(final Context mContext, final long n, final long n2, final TextView mErrorView) {
            super(n, n2);
            this.mErrorView = mErrorView;
            this.mContext = mContext;
        }
        
        public void onTick(final long n) {
            this.mErrorView.setText((CharSequence)this.mContext.getString(R$string.biometric_dialog_credential_too_many_attempts, new Object[] { (int)(n / 1000L) }));
        }
    }
}
