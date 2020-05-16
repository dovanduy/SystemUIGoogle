// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageView;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.Animator$AnimatorListener;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.animation.ValueAnimator;
import java.util.function.Consumer;
import com.android.systemui.R$id;
import android.graphics.ColorMatrix;

public class NotificationDozeHelper
{
    private static final int DOZE_ANIMATOR_TAG;
    private final ColorMatrix mGrayscaleColorMatrix;
    
    static {
        DOZE_ANIMATOR_TAG = R$id.doze_intensity_tag;
    }
    
    public NotificationDozeHelper() {
        this.mGrayscaleColorMatrix = new ColorMatrix();
    }
    
    public void setDozing(final Consumer<Float> consumer, final boolean b, final boolean b2, final long n, final View view) {
        if (b2) {
            this.startIntensityAnimation((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$NotificationDozeHelper$VENFYNxPWcqtSl2MMr8F4aMPH78(consumer), b, n, (Animator$AnimatorListener)new AnimatorListenerAdapter(this) {
                public void onAnimationEnd(final Animator animator) {
                    view.setTag(NotificationDozeHelper.DOZE_ANIMATOR_TAG, (Object)null);
                }
                
                public void onAnimationStart(final Animator animator) {
                    view.setTag(NotificationDozeHelper.DOZE_ANIMATOR_TAG, (Object)animator);
                }
            });
        }
        else {
            final Animator animator = (Animator)view.getTag(NotificationDozeHelper.DOZE_ANIMATOR_TAG);
            if (animator != null) {
                animator.cancel();
            }
            float f;
            if (b) {
                f = 1.0f;
            }
            else {
                f = 0.0f;
            }
            consumer.accept(f);
        }
    }
    
    public void startIntensityAnimation(final ValueAnimator$AnimatorUpdateListener valueAnimator$AnimatorUpdateListener, final boolean b, final long startDelay, final Animator$AnimatorListener animator$AnimatorListener) {
        float n = 0.0f;
        float n2;
        if (b) {
            n2 = 0.0f;
        }
        else {
            n2 = 1.0f;
        }
        if (b) {
            n = 1.0f;
        }
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { n2, n });
        ofFloat.addUpdateListener(valueAnimator$AnimatorUpdateListener);
        ofFloat.setDuration(500L);
        ofFloat.setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN);
        ofFloat.setStartDelay(startDelay);
        if (animator$AnimatorListener != null) {
            ofFloat.addListener(animator$AnimatorListener);
        }
        ofFloat.start();
    }
    
    public void updateGrayscale(final ImageView imageView, final float n) {
        if (n > 0.0f) {
            this.updateGrayscaleMatrix(n);
            imageView.setColorFilter((ColorFilter)new ColorMatrixColorFilter(this.mGrayscaleColorMatrix));
        }
        else {
            imageView.setColorFilter((ColorFilter)null);
        }
    }
    
    public void updateGrayscaleMatrix(final float n) {
        this.mGrayscaleColorMatrix.setSaturation(1.0f - n);
    }
}
