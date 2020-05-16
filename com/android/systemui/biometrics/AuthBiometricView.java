// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.biometrics;

import java.util.Collection;
import java.util.ArrayList;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import android.view.View$MeasureSpec;
import com.android.systemui.R$string;
import android.text.TextUtils;
import android.animation.ValueAnimator;
import android.view.ViewGroup;
import android.util.Log;
import android.view.View;
import com.android.systemui.R$color;
import android.os.Looper;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.Button;
import com.android.internal.annotations.VisibleForTesting;
import android.widget.ImageView;
import android.os.Handler;
import android.widget.TextView;
import android.os.Bundle;
import android.view.View$OnClickListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;

public abstract class AuthBiometricView extends LinearLayout
{
    private final AccessibilityManager mAccessibilityManager;
    private final View$OnClickListener mBackgroundClickListener;
    private Bundle mBiometricPromptBundle;
    private Callback mCallback;
    private TextView mDescriptionView;
    protected boolean mDialogSizeAnimating;
    private int mEffectiveUserId;
    private final Handler mHandler;
    private float mIconOriginalY;
    protected ImageView mIconView;
    @VisibleForTesting
    protected TextView mIndicatorView;
    private final Injector mInjector;
    private int mMediumHeight;
    private int mMediumWidth;
    @VisibleForTesting
    Button mNegativeButton;
    private AuthPanelController mPanelController;
    @VisibleForTesting
    Button mPositiveButton;
    private boolean mRequireConfirmation;
    private final Runnable mResetErrorRunnable;
    private final Runnable mResetHelpRunnable;
    protected Bundle mSavedState;
    int mSize;
    protected int mState;
    private TextView mSubtitleView;
    private final int mTextColorError;
    private final int mTextColorHint;
    private TextView mTitleView;
    @VisibleForTesting
    Button mTryAgainButton;
    
    public AuthBiometricView(final Context context) {
        this(context, null);
    }
    
    public AuthBiometricView(final Context context, final AttributeSet set) {
        this(context, set, new Injector());
    }
    
    @VisibleForTesting
    AuthBiometricView(final Context context, final AttributeSet set, final Injector mInjector) {
        super(context, set);
        this.mSize = 0;
        this.mBackgroundClickListener = (View$OnClickListener)new _$$Lambda$AuthBiometricView$74Ox_j14CYJ3ddBOXoxeI_wTUBk(this);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mTextColorError = this.getResources().getColor(R$color.biometric_dialog_error, context.getTheme());
        this.mTextColorHint = this.getResources().getColor(R$color.biometric_dialog_gray, context.getTheme());
        this.mInjector = mInjector;
        mInjector.mBiometricView = this;
        this.mAccessibilityManager = (AccessibilityManager)context.getSystemService((Class)AccessibilityManager.class);
        this.mResetErrorRunnable = new _$$Lambda$AuthBiometricView$2drOaNVaSONPnaFzaOUoYj_j85g(this);
        this.mResetHelpRunnable = new _$$Lambda$AuthBiometricView$h7WED3KSGw20PO7Z91wwxRtsrCg(this);
    }
    
    private boolean isDeviceCredentialAllowed() {
        return Utils.isDeviceCredentialAllowed(this.mBiometricPromptBundle);
    }
    
    private void removePendingAnimations() {
        this.mHandler.removeCallbacks(this.mResetHelpRunnable);
        this.mHandler.removeCallbacks(this.mResetErrorRunnable);
    }
    
    private void setText(final TextView textView, final String text) {
        textView.setText((CharSequence)text);
    }
    
    private void setTextOrHide(final TextView textView, final String text) {
        if (TextUtils.isEmpty((CharSequence)text)) {
            textView.setVisibility(8);
        }
        else {
            textView.setText((CharSequence)text);
        }
        Utils.notifyAccessibilityContentChanged(this.mAccessibilityManager, (ViewGroup)this);
    }
    
    private void showTemporaryMessage(final String text, final Runnable runnable) {
        this.removePendingAnimations();
        this.mIndicatorView.setText((CharSequence)text);
        this.mIndicatorView.setTextColor(this.mTextColorError);
        this.mIndicatorView.setVisibility(0);
        this.mHandler.postDelayed(runnable, 2000L);
        Utils.notifyAccessibilityContentChanged(this.mAccessibilityManager, (ViewGroup)this);
    }
    
    protected abstract int getDelayAfterAuthenticatedDurationMs();
    
    protected abstract int getStateForAfterError();
    
    protected abstract void handleResetAfterError();
    
    protected abstract void handleResetAfterHelp();
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.onAttachedToWindowInternal();
    }
    
    @VisibleForTesting
    void onAttachedToWindowInternal() {
        this.setText(this.mTitleView, this.mBiometricPromptBundle.getString("title"));
        String s;
        if (this.isDeviceCredentialAllowed()) {
            final int credentialType = Utils.getCredentialType(super.mContext, this.mEffectiveUserId);
            if (credentialType != 1) {
                if (credentialType != 2) {
                    if (credentialType != 3) {
                        s = this.getResources().getString(R$string.biometric_dialog_use_password);
                    }
                    else {
                        s = this.getResources().getString(R$string.biometric_dialog_use_password);
                    }
                }
                else {
                    s = this.getResources().getString(R$string.biometric_dialog_use_pattern);
                }
            }
            else {
                s = this.getResources().getString(R$string.biometric_dialog_use_pin);
            }
        }
        else {
            s = this.mBiometricPromptBundle.getString("negative_text");
        }
        this.setText((TextView)this.mNegativeButton, s);
        this.setTextOrHide(this.mSubtitleView, this.mBiometricPromptBundle.getString("subtitle"));
        this.setTextOrHide(this.mDescriptionView, this.mBiometricPromptBundle.getString("description"));
        final Bundle mSavedState = this.mSavedState;
        if (mSavedState == null) {
            this.updateState(1);
        }
        else {
            this.updateState(mSavedState.getInt("state"));
            this.mTryAgainButton.setVisibility(this.mSavedState.getInt("try_agian_visibility"));
        }
    }
    
    public void onAuthenticationFailed(final String s) {
        this.showTemporaryMessage(s, this.mResetErrorRunnable);
        this.updateState(4);
    }
    
    public void onAuthenticationSucceeded() {
        this.removePendingAnimations();
        if (this.mRequireConfirmation) {
            this.updateState(5);
        }
        else {
            this.updateState(6);
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mHandler.removeCallbacksAndMessages((Object)null);
    }
    
    public void onDialogAnimatedIn() {
        this.updateState(2);
    }
    
    public void onError(final String s) {
        this.showTemporaryMessage(s, this.mResetErrorRunnable);
        this.updateState(4);
        this.mHandler.postDelayed((Runnable)new _$$Lambda$AuthBiometricView$IjJmXNzRMZpZA04YZLx9v3gpf7E(this), (long)this.mInjector.getDelayAfterError());
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.onFinishInflateInternal();
    }
    
    @VisibleForTesting
    void onFinishInflateInternal() {
        this.mTitleView = this.mInjector.getTitleView();
        this.mSubtitleView = this.mInjector.getSubtitleView();
        this.mDescriptionView = this.mInjector.getDescriptionView();
        this.mIconView = this.mInjector.getIconView();
        this.mIndicatorView = this.mInjector.getIndicatorView();
        this.mNegativeButton = this.mInjector.getNegativeButton();
        this.mPositiveButton = this.mInjector.getPositiveButton();
        this.mTryAgainButton = this.mInjector.getTryAgainButton();
        this.mNegativeButton.setOnClickListener((View$OnClickListener)new _$$Lambda$AuthBiometricView$qlVsSDplrDVUHj3VMy1YMdB9Z2Q(this));
        this.mPositiveButton.setOnClickListener((View$OnClickListener)new _$$Lambda$AuthBiometricView$qLp3TPGAuJEy2AApoHqHuLR3prY(this));
        this.mTryAgainButton.setOnClickListener((View$OnClickListener)new _$$Lambda$AuthBiometricView$qQg25Tq_M8BNbfsr_x1MChyC8F0(this));
    }
    
    public void onHelp(final String s) {
        if (this.mSize != 2) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Help received in size: ");
            sb.append(this.mSize);
            Log.w("BiometricPrompt/AuthBiometricView", sb.toString());
            return;
        }
        this.showTemporaryMessage(s, this.mResetHelpRunnable);
        this.updateState(3);
    }
    
    public void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.onLayoutInternal();
    }
    
    @VisibleForTesting
    void onLayoutInternal() {
        if (this.mIconOriginalY == 0.0f) {
            this.mIconOriginalY = this.mIconView.getY();
            final Bundle mSavedState = this.mSavedState;
            if (mSavedState == null) {
                int n;
                if (!this.mRequireConfirmation && this.supportsSmallDialog()) {
                    n = 1;
                }
                else {
                    n = 2;
                }
                this.updateSize(n);
            }
            else {
                this.updateSize(mSavedState.getInt("size"));
                final String string = this.mSavedState.getString("indicator_string");
                if (this.mSavedState.getBoolean("hint_is_temporary")) {
                    this.onHelp(string);
                }
                else if (this.mSavedState.getBoolean("error_is_temporary")) {
                    this.onAuthenticationFailed(string);
                }
            }
        }
    }
    
    protected void onMeasure(int i, int n) {
        i = View$MeasureSpec.getSize(i);
        final int size = View$MeasureSpec.getSize(n);
        final int min = Math.min(i, size);
        final int childCount = this.getChildCount();
        i = 0;
        int mMediumHeight = 0;
        while (i < childCount) {
            final View child = this.getChildAt(i);
            if (child.getId() == R$id.biometric_icon) {
                child.measure(View$MeasureSpec.makeMeasureSpec(min, Integer.MIN_VALUE), View$MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE));
            }
            else if (child.getId() == R$id.button_bar) {
                child.measure(View$MeasureSpec.makeMeasureSpec(min, 1073741824), View$MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, 1073741824));
            }
            else {
                child.measure(View$MeasureSpec.makeMeasureSpec(min, 1073741824), View$MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE));
            }
            n = mMediumHeight;
            if (child.getVisibility() != 8) {
                n = mMediumHeight + child.getMeasuredHeight();
            }
            ++i;
            mMediumHeight = n;
        }
        this.setMeasuredDimension(min, mMediumHeight);
        this.mMediumHeight = mMediumHeight;
        this.mMediumWidth = this.getMeasuredWidth();
    }
    
    public void onSaveState(final Bundle bundle) {
        bundle.putInt("try_agian_visibility", this.mTryAgainButton.getVisibility());
        bundle.putInt("state", this.mState);
        bundle.putString("indicator_string", this.mIndicatorView.getText().toString());
        bundle.putBoolean("error_is_temporary", this.mHandler.hasCallbacks(this.mResetErrorRunnable));
        bundle.putBoolean("hint_is_temporary", this.mHandler.hasCallbacks(this.mResetHelpRunnable));
        bundle.putInt("size", this.mSize);
    }
    
    public void restoreState(final Bundle mSavedState) {
        this.mSavedState = mSavedState;
    }
    
    public void setBackgroundView(final View view) {
        view.setOnClickListener(this.mBackgroundClickListener);
    }
    
    public void setBiometricPromptBundle(final Bundle mBiometricPromptBundle) {
        this.mBiometricPromptBundle = mBiometricPromptBundle;
    }
    
    public void setCallback(final Callback mCallback) {
        this.mCallback = mCallback;
    }
    
    public void setEffectiveUserId(final int mEffectiveUserId) {
        this.mEffectiveUserId = mEffectiveUserId;
    }
    
    public void setPanelController(final AuthPanelController mPanelController) {
        this.mPanelController = mPanelController;
    }
    
    public void setRequireConfirmation(final boolean mRequireConfirmation) {
        this.mRequireConfirmation = mRequireConfirmation;
    }
    
    public void setUserId(final int n) {
    }
    
    void startTransitionToCredentialUI() {
        this.updateSize(3);
        this.mCallback.onAction(6);
    }
    
    protected abstract boolean supportsSmallDialog();
    
    @VisibleForTesting
    void updateSize(final int n) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Current size: ");
        sb.append(this.mSize);
        sb.append(" New size: ");
        sb.append(n);
        Log.v("BiometricPrompt/AuthBiometricView", sb.toString());
        if (n == 1) {
            this.mTitleView.setVisibility(8);
            this.mSubtitleView.setVisibility(8);
            this.mDescriptionView.setVisibility(8);
            this.mIndicatorView.setVisibility(8);
            this.mNegativeButton.setVisibility(8);
            final float dimension = this.getResources().getDimension(R$dimen.biometric_dialog_icon_padding);
            this.mIconView.setY(this.getHeight() - this.mIconView.getHeight() - dimension);
            this.mPanelController.updateForContentDimensions(this.mMediumWidth, this.mIconView.getHeight() + (int)dimension * 2 - this.mIconView.getPaddingTop() - this.mIconView.getPaddingBottom(), 0);
            this.mSize = n;
        }
        else if (this.mSize == 1 && n == 2) {
            if (this.mDialogSizeAnimating) {
                return;
            }
            this.mDialogSizeAnimating = true;
            final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { this.mIconView.getY(), this.mIconOriginalY });
            ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$AuthBiometricView$Wj3pIUGv2yvV3z4ykqi4KllVNJU(this));
            final ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
            ofFloat2.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$AuthBiometricView$wnwcoDTpdgktx5JVpsJj4HSA0jk(this));
            final AnimatorSet set = new AnimatorSet();
            set.setDuration(150L);
            set.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    super.onAnimationEnd(animator);
                    final AuthBiometricView this$0 = AuthBiometricView.this;
                    this$0.mSize = n;
                    this$0.mDialogSizeAnimating = false;
                    Utils.notifyAccessibilityContentChanged(this$0.mAccessibilityManager, (ViewGroup)AuthBiometricView.this);
                }
                
                public void onAnimationStart(final Animator animator) {
                    super.onAnimationStart(animator);
                    AuthBiometricView.this.mTitleView.setVisibility(0);
                    AuthBiometricView.this.mIndicatorView.setVisibility(0);
                    AuthBiometricView.this.mNegativeButton.setVisibility(0);
                    AuthBiometricView.this.mTryAgainButton.setVisibility(0);
                    if (!TextUtils.isEmpty(AuthBiometricView.this.mSubtitleView.getText())) {
                        AuthBiometricView.this.mSubtitleView.setVisibility(0);
                    }
                    if (!TextUtils.isEmpty(AuthBiometricView.this.mDescriptionView.getText())) {
                        AuthBiometricView.this.mDescriptionView.setVisibility(0);
                    }
                }
            });
            set.play((Animator)ofFloat).with((Animator)ofFloat2);
            set.start();
            this.mPanelController.updateForContentDimensions(this.mMediumWidth, this.mMediumHeight, 150);
        }
        else if (n == 2) {
            this.mPanelController.updateForContentDimensions(this.mMediumWidth, this.mMediumHeight, 0);
            this.mSize = n;
        }
        else if (n == 3) {
            final ValueAnimator ofFloat3 = ValueAnimator.ofFloat(new float[] { this.getY(), this.getY() - this.getResources().getDimension(R$dimen.biometric_dialog_medium_to_large_translation_offset) });
            ofFloat3.setDuration((long)this.mInjector.getMediumToLargeAnimationDurationMs());
            ofFloat3.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$AuthBiometricView$DNZGqOzv_lXEbjrYTngC9OQfLl4(this));
            ofFloat3.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    super.onAnimationEnd(animator);
                    if (AuthBiometricView.this.getParent() != null) {
                        ((ViewGroup)AuthBiometricView.this.getParent()).removeView((View)AuthBiometricView.this);
                    }
                    AuthBiometricView.this.mSize = n;
                }
            });
            final ValueAnimator ofFloat4 = ValueAnimator.ofFloat(new float[] { 1.0f, 0.0f });
            ofFloat4.setDuration((long)(this.mInjector.getMediumToLargeAnimationDurationMs() / 2));
            ofFloat4.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$AuthBiometricView$NITDpz2_CemnJIsSGRaKPYHZqW4(this));
            this.mPanelController.setUseFullScreen(true);
            final AuthPanelController mPanelController = this.mPanelController;
            mPanelController.updateForContentDimensions(mPanelController.getContainerWidth(), this.mPanelController.getContainerHeight(), this.mInjector.getMediumToLargeAnimationDurationMs());
            final AnimatorSet set2 = new AnimatorSet();
            final ArrayList<ValueAnimator> list = new ArrayList<ValueAnimator>();
            list.add(ofFloat3);
            list.add(ofFloat4);
            set2.playTogether((Collection)list);
            set2.setDuration((long)(this.mInjector.getMediumToLargeAnimationDurationMs() * 2 / 3));
            set2.start();
        }
        else {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Unknown transition from: ");
            sb2.append(this.mSize);
            sb2.append(" to: ");
            sb2.append(n);
            Log.e("BiometricPrompt/AuthBiometricView", sb2.toString());
        }
        Utils.notifyAccessibilityContentChanged(this.mAccessibilityManager, (ViewGroup)this);
    }
    
    public void updateState(final int mState) {
        final StringBuilder sb = new StringBuilder();
        sb.append("newState: ");
        sb.append(mState);
        Log.v("BiometricPrompt/AuthBiometricView", sb.toString());
        if (mState != 1 && mState != 2) {
            if (mState != 4) {
                if (mState != 5) {
                    if (mState != 6) {
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append("Unhandled state: ");
                        sb2.append(mState);
                        Log.w("BiometricPrompt/AuthBiometricView", sb2.toString());
                    }
                    else {
                        if (this.mSize != 1) {
                            this.mPositiveButton.setVisibility(8);
                            this.mNegativeButton.setVisibility(8);
                            this.mIndicatorView.setVisibility(4);
                        }
                        this.announceForAccessibility((CharSequence)this.getResources().getString(R$string.biometric_dialog_authenticated));
                        this.mHandler.postDelayed((Runnable)new _$$Lambda$AuthBiometricView$A6c9EVpo4leekZpDntHzHp57vns(this), (long)this.getDelayAfterAuthenticatedDurationMs());
                    }
                }
                else {
                    this.removePendingAnimations();
                    this.mNegativeButton.setText(R$string.cancel);
                    this.mNegativeButton.setContentDescription((CharSequence)this.getResources().getString(R$string.cancel));
                    this.mPositiveButton.setEnabled(true);
                    this.mPositiveButton.setVisibility(0);
                    this.mIndicatorView.setTextColor(this.mTextColorHint);
                    this.mIndicatorView.setText(R$string.biometric_dialog_tap_confirm);
                    this.mIndicatorView.setVisibility(0);
                }
            }
            else if (this.mSize == 1) {
                this.updateSize(2);
            }
        }
        else {
            this.removePendingAnimations();
            if (this.mRequireConfirmation) {
                this.mPositiveButton.setEnabled(false);
                this.mPositiveButton.setVisibility(0);
            }
        }
        Utils.notifyAccessibilityContentChanged(this.mAccessibilityManager, (ViewGroup)this);
        this.mState = mState;
    }
    
    interface Callback
    {
        void onAction(final int p0);
    }
    
    @VisibleForTesting
    static class Injector
    {
        AuthBiometricView mBiometricView;
        
        public int getDelayAfterError() {
            return 2000;
        }
        
        public TextView getDescriptionView() {
            return (TextView)this.mBiometricView.findViewById(R$id.description);
        }
        
        public ImageView getIconView() {
            return (ImageView)this.mBiometricView.findViewById(R$id.biometric_icon);
        }
        
        public TextView getIndicatorView() {
            return (TextView)this.mBiometricView.findViewById(R$id.indicator);
        }
        
        public int getMediumToLargeAnimationDurationMs() {
            return 450;
        }
        
        public Button getNegativeButton() {
            return (Button)this.mBiometricView.findViewById(R$id.button_negative);
        }
        
        public Button getPositiveButton() {
            return (Button)this.mBiometricView.findViewById(R$id.button_positive);
        }
        
        public TextView getSubtitleView() {
            return (TextView)this.mBiometricView.findViewById(R$id.subtitle);
        }
        
        public TextView getTitleView() {
            return (TextView)this.mBiometricView.findViewById(R$id.title);
        }
        
        public Button getTryAgainButton() {
            return (Button)this.mBiometricView.findViewById(R$id.button_try_again);
        }
    }
}
