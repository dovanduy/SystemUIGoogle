// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.biometrics;

import android.os.UserManager;
import com.android.systemui.R$id;
import android.content.Context;
import android.view.ViewGroup$LayoutParams;
import android.os.Bundle;
import android.view.KeyEvent;
import android.animation.TimeInterpolator;
import android.view.WindowInsets$Type;
import android.view.WindowManager$LayoutParams;
import android.view.View$OnClickListener;
import android.view.View$OnKeyListener;
import android.util.Log;
import com.android.systemui.R$layout;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.android.systemui.Interpolators;
import com.android.systemui.R$dimen;
import com.android.systemui.Dependency;
import android.os.Looper;
import android.os.Binder;
import android.os.IBinder;
import android.view.WindowManager;
import android.view.View;
import android.view.animation.Interpolator;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import com.android.internal.annotations.VisibleForTesting;
import android.widget.ImageView;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import android.widget.LinearLayout;

public class AuthContainerView extends LinearLayout implements AuthDialog, Observer
{
    @VisibleForTesting
    final ImageView mBackgroundView;
    @VisibleForTesting
    final BiometricCallback mBiometricCallback;
    @VisibleForTesting
    final ScrollView mBiometricScrollView;
    @VisibleForTesting
    AuthBiometricView mBiometricView;
    final Config mConfig;
    private int mContainerState;
    byte[] mCredentialAttestation;
    private final CredentialCallback mCredentialCallback;
    @VisibleForTesting
    AuthCredentialView mCredentialView;
    final int mEffectiveUserId;
    @VisibleForTesting
    final FrameLayout mFrameLayout;
    private final Handler mHandler;
    private final Injector mInjector;
    private final Interpolator mLinearOutSlowIn;
    private final AuthPanelController mPanelController;
    private final View mPanelView;
    Integer mPendingCallbackReason;
    private final float mTranslationY;
    @VisibleForTesting
    final WakefulnessLifecycle mWakefulnessLifecycle;
    private final WindowManager mWindowManager;
    private final IBinder mWindowToken;
    
    @VisibleForTesting
    AuthContainerView(final Config mConfig, final Injector mInjector) {
        super(mConfig.mContext);
        this.mWindowToken = (IBinder)new Binder();
        this.mContainerState = 0;
        this.mConfig = mConfig;
        this.mInjector = mInjector;
        this.mEffectiveUserId = mInjector.getUserManager(super.mContext).getCredentialOwnerProfile(this.mConfig.mUserId);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mWindowManager = (WindowManager)super.mContext.getSystemService((Class)WindowManager.class);
        this.mWakefulnessLifecycle = Dependency.get(WakefulnessLifecycle.class);
        this.mTranslationY = this.getResources().getDimension(R$dimen.biometric_dialog_animation_translation_offset);
        this.mLinearOutSlowIn = Interpolators.LINEAR_OUT_SLOW_IN;
        this.mBiometricCallback = new BiometricCallback();
        this.mCredentialCallback = new CredentialCallback();
        final LayoutInflater from = LayoutInflater.from(super.mContext);
        final FrameLayout inflateContainerView = this.mInjector.inflateContainerView(from, (ViewGroup)this);
        this.mFrameLayout = inflateContainerView;
        final View panelView = this.mInjector.getPanelView(inflateContainerView);
        this.mPanelView = panelView;
        this.mPanelController = this.mInjector.getPanelController(super.mContext, panelView);
        if (Utils.isBiometricAllowed(this.mConfig.mBiometricPromptBundle)) {
            final int mModalityMask = mConfig.mModalityMask;
            if (mModalityMask == 2) {
                this.mBiometricView = (AuthBiometricFingerprintView)from.inflate(R$layout.auth_biometric_fingerprint_view, (ViewGroup)null, false);
            }
            else {
                if (mModalityMask != 8) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Unsupported biometric modality: ");
                    sb.append(mConfig.mModalityMask);
                    Log.e("BiometricPrompt/AuthContainerView", sb.toString());
                    this.mBiometricView = null;
                    this.mBackgroundView = null;
                    this.mBiometricScrollView = null;
                    return;
                }
                this.mBiometricView = (AuthBiometricFaceView)from.inflate(R$layout.auth_biometric_face_view, (ViewGroup)null, false);
            }
        }
        this.mBiometricScrollView = this.mInjector.getBiometricScrollView(this.mFrameLayout);
        this.mBackgroundView = this.mInjector.getBackgroundView(this.mFrameLayout);
        this.addView((View)this.mFrameLayout);
        this.setOnKeyListener((View$OnKeyListener)new _$$Lambda$AuthContainerView$DgtzYoQDVOv5iHGCr90WcpJUnck(this));
        this.setFocusableInTouchMode(true);
        this.requestFocus();
    }
    
    private void addBiometricView() {
        this.mBiometricView.setRequireConfirmation(this.mConfig.mRequireConfirmation);
        this.mBiometricView.setPanelController(this.mPanelController);
        this.mBiometricView.setBiometricPromptBundle(this.mConfig.mBiometricPromptBundle);
        this.mBiometricView.setCallback((AuthBiometricView.Callback)this.mBiometricCallback);
        this.mBiometricView.setBackgroundView((View)this.mBackgroundView);
        this.mBiometricView.setUserId(this.mConfig.mUserId);
        this.mBiometricView.setEffectiveUserId(this.mEffectiveUserId);
        this.mBiometricScrollView.addView((View)this.mBiometricView);
    }
    
    private void addCredentialView(final boolean b, final boolean shouldAnimateContents) {
        final LayoutInflater from = LayoutInflater.from(super.mContext);
        final int credentialType = this.mInjector.getCredentialType(super.mContext, this.mEffectiveUserId);
        Label_0116: {
            if (credentialType != 1) {
                if (credentialType == 2) {
                    this.mCredentialView = (AuthCredentialView)from.inflate(R$layout.auth_credential_pattern_view, (ViewGroup)null, false);
                    break Label_0116;
                }
                if (credentialType != 3) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Unknown credential type: ");
                    sb.append(credentialType);
                    throw new IllegalStateException(sb.toString());
                }
            }
            this.mCredentialView = (AuthCredentialView)from.inflate(R$layout.auth_credential_password_view, (ViewGroup)null, false);
        }
        this.mBackgroundView.setOnClickListener((View$OnClickListener)null);
        this.mBackgroundView.setImportantForAccessibility(2);
        this.mCredentialView.setContainerView(this);
        this.mCredentialView.setUserId(this.mConfig.mUserId);
        this.mCredentialView.setOperationId(this.mConfig.mOperationId);
        this.mCredentialView.setEffectiveUserId(this.mEffectiveUserId);
        this.mCredentialView.setCredentialType(credentialType);
        this.mCredentialView.setCallback((AuthCredentialView.Callback)this.mCredentialCallback);
        this.mCredentialView.setBiometricPromptBundle(this.mConfig.mBiometricPromptBundle);
        this.mCredentialView.setPanelController(this.mPanelController, b);
        this.mCredentialView.setShouldAnimateContents(shouldAnimateContents);
        this.mFrameLayout.addView((View)this.mCredentialView);
    }
    
    private void animateAway(final boolean b, final int n) {
        final int mContainerState = this.mContainerState;
        if (mContainerState == 1) {
            Log.w("BiometricPrompt/AuthContainerView", "startDismiss(): waiting for onDialogAnimatedIn");
            this.mContainerState = 2;
            return;
        }
        if (mContainerState == 4) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Already dismissing, sendReason: ");
            sb.append(b);
            sb.append(" reason: ");
            sb.append(n);
            Log.w("BiometricPrompt/AuthContainerView", sb.toString());
            return;
        }
        this.mContainerState = 4;
        if (b) {
            this.mPendingCallbackReason = n;
        }
        else {
            this.mPendingCallbackReason = null;
        }
        this.postOnAnimation((Runnable)new _$$Lambda$AuthContainerView$hmGjJ8kJosRRIFfZpgTQM0R9XiM(this, new _$$Lambda$AuthContainerView$vtbE2_wtRO04iHv7UirSn4_zgk0(this)));
    }
    
    public static WindowManager$LayoutParams getLayoutParams(final IBinder token) {
        final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(-1, -1, 2017, 16785408, -3);
        windowManager$LayoutParams.privateFlags |= 0x10;
        windowManager$LayoutParams.setTitle((CharSequence)"BiometricPrompt");
        windowManager$LayoutParams.token = token;
        windowManager$LayoutParams.setFitInsetsTypes(windowManager$LayoutParams.getFitInsetsTypes() & WindowInsets$Type.statusBars());
        return windowManager$LayoutParams;
    }
    
    private void onDialogAnimatedIn() {
        if (this.mContainerState == 2) {
            Log.d("BiometricPrompt/AuthContainerView", "onDialogAnimatedIn(): mPendingDismissDialog=true, dismissing now");
            this.animateAway(false, 0);
            return;
        }
        this.mContainerState = 3;
        final AuthBiometricView mBiometricView = this.mBiometricView;
        if (mBiometricView != null) {
            mBiometricView.onDialogAnimatedIn();
        }
    }
    
    private void removeWindowIfAttached() {
        this.sendPendingCallbackIfNotNull();
        if (this.mContainerState == 5) {
            return;
        }
        this.mContainerState = 5;
        this.mWindowManager.removeView((View)this);
    }
    
    private void sendPendingCallbackIfNotNull() {
        final StringBuilder sb = new StringBuilder();
        sb.append("pendingCallback: ");
        sb.append(this.mPendingCallbackReason);
        Log.d("BiometricPrompt/AuthContainerView", sb.toString());
        final Integer mPendingCallbackReason = this.mPendingCallbackReason;
        if (mPendingCallbackReason != null) {
            this.mConfig.mCallback.onDismissed(mPendingCallbackReason, this.mCredentialAttestation);
            this.mPendingCallbackReason = null;
        }
    }
    
    @VisibleForTesting
    void animateAway(final int n) {
        this.animateAway(true, n);
    }
    
    public void animateToCredentialUI() {
        this.mBiometricView.startTransitionToCredentialUI();
    }
    
    public void dismissFromSystemServer() {
        this.removeWindowIfAttached();
    }
    
    public void dismissWithoutCallback(final boolean b) {
        if (b) {
            this.animateAway(false, 0);
        }
        else {
            this.removeWindowIfAttached();
        }
    }
    
    public String getOpPackageName() {
        return this.mConfig.mOpPackageName;
    }
    
    public boolean isAllowDeviceCredentials() {
        return Utils.isDeviceCredentialAllowed(this.mConfig.mBiometricPromptBundle);
    }
    
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.onAttachedToWindowInternal();
    }
    
    @VisibleForTesting
    void onAttachedToWindowInternal() {
        this.mWakefulnessLifecycle.addObserver((WakefulnessLifecycle.Observer)this);
        if (Utils.isBiometricAllowed(this.mConfig.mBiometricPromptBundle)) {
            this.addBiometricView();
        }
        else {
            if (!Utils.isDeviceCredentialAllowed(this.mConfig.mBiometricPromptBundle)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unknown configuration: ");
                sb.append(Utils.getAuthenticators(this.mConfig.mBiometricPromptBundle));
                throw new IllegalStateException(sb.toString());
            }
            this.addCredentialView(true, false);
        }
        if (this.mConfig.mSkipIntro) {
            this.mContainerState = 3;
        }
        else {
            this.mContainerState = 1;
            this.mPanelView.setY(this.mTranslationY);
            this.mBiometricScrollView.setY(this.mTranslationY);
            this.setAlpha(0.0f);
            this.postOnAnimation((Runnable)new _$$Lambda$AuthContainerView$9SlioxBspj7c8LZaxiUDurCbgao(this));
        }
    }
    
    public void onAuthenticationFailed(final String s) {
        this.mBiometricView.onAuthenticationFailed(s);
    }
    
    public void onAuthenticationSucceeded() {
        this.mBiometricView.onAuthenticationSucceeded();
    }
    
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mWakefulnessLifecycle.removeObserver((WakefulnessLifecycle.Observer)this);
    }
    
    public void onError(final String s) {
        this.mBiometricView.onError(s);
    }
    
    public void onHelp(final String s) {
        this.mBiometricView.onHelp(s);
    }
    
    protected void onMeasure(final int n, final int n2) {
        super.onMeasure(n, n2);
        this.mPanelController.setContainerDimensions(this.getMeasuredWidth(), this.getMeasuredHeight());
    }
    
    public void onSaveState(final Bundle bundle) {
        bundle.putInt("container_state", this.mContainerState);
        final AuthBiometricView mBiometricView = this.mBiometricView;
        final boolean b = true;
        bundle.putBoolean("biometric_showing", mBiometricView != null && this.mCredentialView == null);
        bundle.putBoolean("credential_showing", this.mCredentialView != null && b);
        final AuthBiometricView mBiometricView2 = this.mBiometricView;
        if (mBiometricView2 != null) {
            mBiometricView2.onSaveState(bundle);
        }
    }
    
    public void onStartedGoingToSleep() {
        this.animateAway(1);
    }
    
    void sendEarlyUserCanceled() {
        this.mConfig.mCallback.onSystemEvent(1);
    }
    
    public void show(final WindowManager windowManager, final Bundle bundle) {
        final AuthBiometricView mBiometricView = this.mBiometricView;
        if (mBiometricView != null) {
            mBiometricView.restoreState(bundle);
        }
        windowManager.addView((View)this, (ViewGroup$LayoutParams)getLayoutParams(this.mWindowToken));
    }
    
    @VisibleForTesting
    final class BiometricCallback implements Callback
    {
        @Override
        public void onAction(final int i) {
            switch (i) {
                default: {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Unhandled action: ");
                    sb.append(i);
                    Log.e("BiometricPrompt/AuthContainerView", sb.toString());
                    break;
                }
                case 6: {
                    AuthContainerView.this.mConfig.mCallback.onDeviceCredentialPressed();
                    AuthContainerView.this.mHandler.postDelayed((Runnable)new _$$Lambda$AuthContainerView$BiometricCallback$_PVe7wil4E5Jd70vq9AnXnFCWJk(this), (long)AuthContainerView.this.mInjector.getAnimateCredentialStartDelayMs());
                    break;
                }
                case 5: {
                    AuthContainerView.this.animateAway(5);
                    break;
                }
                case 4: {
                    AuthContainerView.this.mConfig.mCallback.onTryAgainPressed();
                    break;
                }
                case 3: {
                    AuthContainerView.this.animateAway(2);
                    break;
                }
                case 2: {
                    AuthContainerView.this.sendEarlyUserCanceled();
                    AuthContainerView.this.animateAway(1);
                    break;
                }
                case 1: {
                    AuthContainerView.this.animateAway(4);
                    break;
                }
            }
        }
    }
    
    public static class Builder
    {
        Config mConfig;
        
        public Builder(final Context mContext) {
            final Config mConfig = new Config();
            this.mConfig = mConfig;
            mConfig.mContext = mContext;
        }
        
        public AuthContainerView build(final int mModalityMask) {
            this.mConfig.mModalityMask = mModalityMask;
            return new AuthContainerView(this.mConfig, new Injector());
        }
        
        public Builder setBiometricPromptBundle(final Bundle mBiometricPromptBundle) {
            this.mConfig.mBiometricPromptBundle = mBiometricPromptBundle;
            return this;
        }
        
        public Builder setCallback(final AuthDialogCallback mCallback) {
            this.mConfig.mCallback = mCallback;
            return this;
        }
        
        public Builder setOpPackageName(final String mOpPackageName) {
            this.mConfig.mOpPackageName = mOpPackageName;
            return this;
        }
        
        public Builder setOperationId(final long mOperationId) {
            this.mConfig.mOperationId = mOperationId;
            return this;
        }
        
        public Builder setRequireConfirmation(final boolean mRequireConfirmation) {
            this.mConfig.mRequireConfirmation = mRequireConfirmation;
            return this;
        }
        
        public Builder setSkipIntro(final boolean mSkipIntro) {
            this.mConfig.mSkipIntro = mSkipIntro;
            return this;
        }
        
        public Builder setUserId(final int mUserId) {
            this.mConfig.mUserId = mUserId;
            return this;
        }
    }
    
    static class Config
    {
        Bundle mBiometricPromptBundle;
        AuthDialogCallback mCallback;
        Context mContext;
        int mModalityMask;
        String mOpPackageName;
        long mOperationId;
        boolean mRequireConfirmation;
        boolean mSkipIntro;
        int mUserId;
    }
    
    final class CredentialCallback implements Callback
    {
        @Override
        public void onCredentialMatched(final byte[] mCredentialAttestation) {
            final AuthContainerView this$0 = AuthContainerView.this;
            this$0.mCredentialAttestation = mCredentialAttestation;
            this$0.animateAway(7);
        }
    }
    
    public static class Injector
    {
        int getAnimateCredentialStartDelayMs() {
            return 300;
        }
        
        ImageView getBackgroundView(final FrameLayout frameLayout) {
            return (ImageView)frameLayout.findViewById(R$id.background);
        }
        
        ScrollView getBiometricScrollView(final FrameLayout frameLayout) {
            return (ScrollView)frameLayout.findViewById(R$id.biometric_scrollview);
        }
        
        int getCredentialType(final Context context, final int n) {
            return Utils.getCredentialType(context, n);
        }
        
        AuthPanelController getPanelController(final Context context, final View view) {
            return new AuthPanelController(context, view);
        }
        
        View getPanelView(final FrameLayout frameLayout) {
            return frameLayout.findViewById(R$id.panel);
        }
        
        UserManager getUserManager(final Context context) {
            return UserManager.get(context);
        }
        
        FrameLayout inflateContainerView(final LayoutInflater layoutInflater, final ViewGroup viewGroup) {
            return (FrameLayout)layoutInflater.inflate(R$layout.auth_container_view, viewGroup, false);
        }
    }
}
