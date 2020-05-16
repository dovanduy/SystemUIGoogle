// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.biometrics;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.Log;
import com.android.systemui.R$color;
import com.android.systemui.R$string;
import com.android.systemui.R$drawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.content.Context;

public class AuthBiometricFingerprintView extends AuthBiometricView
{
    public AuthBiometricFingerprintView(final Context context) {
        this(context, null);
    }
    
    public AuthBiometricFingerprintView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    private Drawable getAnimationForTransition(int n, final int n2) {
        if (n2 != 1 && n2 != 2) {
            if (n2 != 3 && n2 != 4) {
                if (n2 != 6) {
                    return null;
                }
                n = R$drawable.fingerprint_dialog_fp_to_error;
            }
            else {
                n = R$drawable.fingerprint_dialog_fp_to_error;
            }
        }
        else if (n != 4 && n != 3) {
            n = R$drawable.fingerprint_dialog_fp_to_error;
        }
        else {
            n = R$drawable.fingerprint_dialog_error_to_fp;
        }
        return super.mContext.getDrawable(n);
    }
    
    private boolean shouldAnimateForTransition(final int n, final int n2) {
        if (n2 != 1 && n2 != 2) {
            return n2 == 3 || n2 == 4;
        }
        return n == 4 || n == 3;
    }
    
    private void showTouchSensorString() {
        super.mIndicatorView.setText(R$string.fingerprint_dialog_touch_sensor);
        super.mIndicatorView.setTextColor(R$color.biometric_dialog_gray);
    }
    
    private void updateIcon(final int i, final int j) {
        final Drawable animationForTransition = this.getAnimationForTransition(i, j);
        if (animationForTransition == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Animation not found, ");
            sb.append(i);
            sb.append(" -> ");
            sb.append(j);
            Log.e("BiometricPrompt/AuthBiometricFingerprintView", sb.toString());
            return;
        }
        AnimatedVectorDrawable animatedVectorDrawable;
        if (animationForTransition instanceof AnimatedVectorDrawable) {
            animatedVectorDrawable = (AnimatedVectorDrawable)animationForTransition;
        }
        else {
            animatedVectorDrawable = null;
        }
        super.mIconView.setImageDrawable(animationForTransition);
        if (animatedVectorDrawable != null && this.shouldAnimateForTransition(i, j)) {
            animatedVectorDrawable.forceAnimationOnUI();
            animatedVectorDrawable.start();
        }
    }
    
    @Override
    protected int getDelayAfterAuthenticatedDurationMs() {
        return 0;
    }
    
    @Override
    protected int getStateForAfterError() {
        return 2;
    }
    
    @Override
    protected void handleResetAfterError() {
        this.showTouchSensorString();
    }
    
    @Override
    protected void handleResetAfterHelp() {
        this.showTouchSensorString();
    }
    
    @Override
    void onAttachedToWindowInternal() {
        super.onAttachedToWindowInternal();
        this.showTouchSensorString();
    }
    
    @Override
    protected boolean supportsSmallDialog() {
        return false;
    }
    
    @Override
    public void updateState(final int n) {
        this.updateIcon(super.mState, n);
        super.updateState(n);
    }
}
