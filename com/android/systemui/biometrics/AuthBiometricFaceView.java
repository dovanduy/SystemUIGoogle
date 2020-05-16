// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.biometrics;

import android.util.Log;
import com.android.systemui.R$string;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.AnimatedVectorDrawable;
import com.android.systemui.R$drawable;
import android.os.Looper;
import android.os.Handler;
import android.widget.ImageView;
import android.graphics.drawable.Animatable2$AnimationCallback;
import com.android.systemui.R$color;
import android.widget.TextView;
import android.util.AttributeSet;
import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;

public class AuthBiometricFaceView extends AuthBiometricView
{
    @VisibleForTesting
    IconController mIconController;
    
    public AuthBiometricFaceView(final Context context) {
        this(context, null);
    }
    
    public AuthBiometricFaceView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    static void resetErrorView(final Context context, final TextView textView) {
        textView.setTextColor(context.getResources().getColor(R$color.biometric_dialog_gray, context.getTheme()));
        textView.setVisibility(4);
    }
    
    @Override
    protected int getDelayAfterAuthenticatedDurationMs() {
        return 500;
    }
    
    @Override
    protected int getStateForAfterError() {
        return 0;
    }
    
    @Override
    protected void handleResetAfterError() {
        resetErrorView(super.mContext, super.mIndicatorView);
    }
    
    @Override
    protected void handleResetAfterHelp() {
        resetErrorView(super.mContext, super.mIndicatorView);
    }
    
    @Override
    public void onAuthenticationFailed(final String s) {
        if (super.mSize == 2) {
            super.mTryAgainButton.setVisibility(0);
            super.mPositiveButton.setVisibility(8);
        }
        super.onAuthenticationFailed(s);
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mIconController = new IconController(super.mContext, super.mIconView, super.mIndicatorView);
    }
    
    @Override
    protected boolean supportsSmallDialog() {
        return true;
    }
    
    @Override
    public void updateState(final int n) {
        this.mIconController.updateState(super.mState, n);
        if (n == 1 || (n == 2 && super.mSize == 2)) {
            resetErrorView(super.mContext, super.mIndicatorView);
        }
        super.updateState(n);
    }
    
    public static class IconController extends Animatable2$AnimationCallback
    {
        Context mContext;
        ImageView mIconView;
        boolean mLastPulseLightToDark;
        int mState;
        
        IconController(final Context mContext, final ImageView mIconView, final TextView textView) {
            this.mContext = mContext;
            this.mIconView = mIconView;
            new Handler(Looper.getMainLooper());
            this.showStaticDrawable(R$drawable.face_dialog_pulse_dark_to_light);
        }
        
        void animateIcon(final int n, final boolean b) {
            final AnimatedVectorDrawable imageDrawable = (AnimatedVectorDrawable)this.mContext.getDrawable(n);
            this.mIconView.setImageDrawable((Drawable)imageDrawable);
            imageDrawable.forceAnimationOnUI();
            if (b) {
                imageDrawable.registerAnimationCallback((Animatable2$AnimationCallback)this);
            }
            imageDrawable.start();
        }
        
        void animateOnce(final int n) {
            this.animateIcon(n, false);
        }
        
        public void onAnimationEnd(final Drawable drawable) {
            super.onAnimationEnd(drawable);
            final int mState = this.mState;
            if (mState == 2 || mState == 3) {
                this.pulseInNextDirection();
            }
        }
        
        void pulseInNextDirection() {
            int n;
            if (this.mLastPulseLightToDark) {
                n = R$drawable.face_dialog_pulse_dark_to_light;
            }
            else {
                n = R$drawable.face_dialog_pulse_light_to_dark;
            }
            this.animateIcon(n, true);
            this.mLastPulseLightToDark ^= true;
        }
        
        public void showStaticDrawable(final int n) {
            this.mIconView.setImageDrawable(this.mContext.getDrawable(n));
        }
        
        void startPulsing() {
            this.mLastPulseLightToDark = false;
            this.animateIcon(R$drawable.face_dialog_pulse_dark_to_light, true);
        }
        
        public void updateState(final int n, final int n2) {
            final boolean b = n == 4 || n == 3;
            if (n2 == 1) {
                this.showStaticDrawable(R$drawable.face_dialog_pulse_dark_to_light);
                this.mIconView.setContentDescription((CharSequence)this.mContext.getString(R$string.biometric_dialog_face_icon_description_authenticating));
            }
            else if (n2 == 2) {
                this.startPulsing();
                this.mIconView.setContentDescription((CharSequence)this.mContext.getString(R$string.biometric_dialog_face_icon_description_authenticating));
            }
            else if (n == 5 && n2 == 6) {
                this.animateOnce(R$drawable.face_dialog_dark_to_checkmark);
                this.mIconView.setContentDescription((CharSequence)this.mContext.getString(R$string.biometric_dialog_face_icon_description_confirmed));
            }
            else if (b && n2 == 0) {
                this.animateOnce(R$drawable.face_dialog_error_to_idle);
                this.mIconView.setContentDescription((CharSequence)this.mContext.getString(R$string.biometric_dialog_face_icon_description_idle));
            }
            else if (b && n2 == 6) {
                this.animateOnce(R$drawable.face_dialog_dark_to_checkmark);
                this.mIconView.setContentDescription((CharSequence)this.mContext.getString(R$string.biometric_dialog_face_icon_description_authenticated));
            }
            else if (n2 == 4 && n != 4) {
                this.animateOnce(R$drawable.face_dialog_dark_to_error);
            }
            else if (n == 2 && n2 == 6) {
                this.animateOnce(R$drawable.face_dialog_dark_to_checkmark);
                this.mIconView.setContentDescription((CharSequence)this.mContext.getString(R$string.biometric_dialog_face_icon_description_authenticated));
            }
            else if (n2 == 5) {
                this.animateOnce(R$drawable.face_dialog_wink_from_dark);
                this.mIconView.setContentDescription((CharSequence)this.mContext.getString(R$string.biometric_dialog_face_icon_description_authenticated));
            }
            else if (n2 == 0) {
                this.showStaticDrawable(R$drawable.face_dialog_idle_static);
                this.mIconView.setContentDescription((CharSequence)this.mContext.getString(R$string.biometric_dialog_face_icon_description_idle));
            }
            else {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unhandled state: ");
                sb.append(n2);
                Log.w("BiometricPrompt/AuthBiometricFaceView", sb.toString());
            }
            this.mState = n2;
        }
    }
}
