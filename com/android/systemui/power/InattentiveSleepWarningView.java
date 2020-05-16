// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.power;

import android.view.ViewGroup$LayoutParams;
import java.util.Objects;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager$LayoutParams;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorInflater;
import android.view.View$OnKeyListener;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.os.Binder;
import android.content.Context;
import android.os.IBinder;
import android.view.WindowManager;
import android.animation.Animator;
import android.widget.FrameLayout;

public class InattentiveSleepWarningView extends FrameLayout
{
    private boolean mDismissing;
    private Animator mFadeOutAnimator;
    private final WindowManager mWindowManager;
    private final IBinder mWindowToken;
    
    InattentiveSleepWarningView(final Context context) {
        super(context);
        this.mWindowToken = (IBinder)new Binder();
        this.mWindowManager = (WindowManager)super.mContext.getSystemService((Class)WindowManager.class);
        LayoutInflater.from(super.mContext).inflate(R$layout.inattentive_sleep_warning, (ViewGroup)this, true);
        this.setFocusable(true);
        this.setOnKeyListener((View$OnKeyListener)_$$Lambda$InattentiveSleepWarningView$TZ7t_oJYmI3UsEhfACXbN6lQYjI.INSTANCE);
        (this.mFadeOutAnimator = AnimatorInflater.loadAnimator(this.getContext(), 17498113)).setTarget((Object)this);
        this.mFadeOutAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationCancel(final Animator animator) {
                InattentiveSleepWarningView.this.mDismissing = false;
                InattentiveSleepWarningView.this.setAlpha(1.0f);
                InattentiveSleepWarningView.this.setVisibility(0);
            }
            
            public void onAnimationEnd(final Animator animator) {
                InattentiveSleepWarningView.this.removeView();
            }
        });
    }
    
    private WindowManager$LayoutParams getLayoutParams(final IBinder token) {
        final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(-1, -1, 2038, 256, -3);
        windowManager$LayoutParams.privateFlags |= 0x10;
        windowManager$LayoutParams.setTitle((CharSequence)"InattentiveSleepWarning");
        windowManager$LayoutParams.token = token;
        return windowManager$LayoutParams;
    }
    
    private void removeView() {
        if (this.mDismissing) {
            this.setVisibility(4);
            this.mWindowManager.removeView((View)this);
        }
    }
    
    public void dismiss(final boolean b) {
        if (this.getParent() == null) {
            return;
        }
        this.mDismissing = true;
        if (b) {
            final Animator mFadeOutAnimator = this.mFadeOutAnimator;
            Objects.requireNonNull(mFadeOutAnimator);
            this.postOnAnimation((Runnable)new _$$Lambda$VKaan4AksvR9EA2Slt2S3X5pVOI(mFadeOutAnimator));
        }
        else {
            this.removeView();
        }
    }
    
    public void show() {
        if (this.getParent() != null) {
            if (this.mFadeOutAnimator.isStarted()) {
                this.mFadeOutAnimator.cancel();
            }
            return;
        }
        this.setAlpha(1.0f);
        this.setVisibility(0);
        this.mWindowManager.addView((View)this, (ViewGroup$LayoutParams)this.getLayoutParams(this.mWindowToken));
    }
}
