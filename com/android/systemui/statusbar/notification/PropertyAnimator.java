// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.animation.PropertyValuesHolder;
import android.view.animation.Interpolator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator$AnimatorListener;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import com.android.systemui.statusbar.notification.stack.ViewState;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import android.util.Property;
import android.animation.ValueAnimator;
import android.view.View;

public class PropertyAnimator
{
    public static <T extends View> void applyImmediately(final T t, final AnimatableProperty animatableProperty, final float f) {
        cancelAnimation(t, animatableProperty);
        animatableProperty.getProperty().set((Object)t, (Object)f);
    }
    
    public static void cancelAnimation(final View view, final AnimatableProperty animatableProperty) {
        final ValueAnimator valueAnimator = (ValueAnimator)view.getTag(animatableProperty.getAnimatorTag());
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }
    
    public static <T extends View> void setProperty(final T t, final AnimatableProperty animatableProperty, final float f, final AnimationProperties animationProperties, final boolean b) {
        if (ViewState.getChildTag(t, animatableProperty.getAnimatorTag()) == null && !b) {
            animatableProperty.getProperty().set((Object)t, (Object)f);
        }
        else {
            startAnimation(t, animatableProperty, f, animationProperties);
        }
    }
    
    public static <T extends View> void startAnimation(final T t, final AnimatableProperty animatableProperty, final float f, final AnimationProperties animationProperties) {
        final Property property = animatableProperty.getProperty();
        final int animationStartTag = animatableProperty.getAnimationStartTag();
        final int animationEndTag = animatableProperty.getAnimationEndTag();
        final Float n = ViewState.getChildTag(t, animationStartTag);
        final Float n2 = ViewState.getChildTag(t, animationEndTag);
        if (n2 != null && n2 == f) {
            return;
        }
        final int animatorTag = animatableProperty.getAnimatorTag();
        final ValueAnimator valueAnimator = ViewState.getChildTag(t, animatorTag);
        if (animationProperties.getAnimationFilter().shouldAnimateProperty(property)) {
            final Float n3 = (Float)property.get((Object)t);
            final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { n3, f });
            ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$PropertyAnimator$VEXcQp_kY9kIrKbFhOrW7gy9zN4(property, t));
            Interpolator interpolator = animationProperties.getCustomInterpolator(t, property);
            if (interpolator == null) {
                interpolator = Interpolators.FAST_OUT_SLOW_IN;
            }
            ofFloat.setInterpolator((TimeInterpolator)interpolator);
            ofFloat.setDuration(ViewState.cancelAnimatorAndGetNewDuration(animationProperties.duration, valueAnimator));
            if (animationProperties.delay > 0L && (valueAnimator == null || valueAnimator.getAnimatedFraction() == 0.0f)) {
                ofFloat.setStartDelay(animationProperties.delay);
            }
            final AnimatorListenerAdapter animationFinishListener = animationProperties.getAnimationFinishListener(property);
            if (animationFinishListener != null) {
                ofFloat.addListener((Animator$AnimatorListener)animationFinishListener);
            }
            ofFloat.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    t.setTag(animatorTag, (Object)null);
                    t.setTag(animationStartTag, (Object)null);
                    t.setTag(animationEndTag, (Object)null);
                }
            });
            ViewState.startAnimator((Animator)ofFloat, animationFinishListener);
            t.setTag(animatorTag, (Object)ofFloat);
            t.setTag(animationStartTag, (Object)n3);
            t.setTag(animationEndTag, (Object)f);
            return;
        }
        if (valueAnimator != null) {
            final PropertyValuesHolder[] values = valueAnimator.getValues();
            final float f2 = n + (f - n2);
            values[0].setFloatValues(new float[] { f2, f });
            t.setTag(animationStartTag, (Object)f2);
            t.setTag(animationEndTag, (Object)f);
            valueAnimator.setCurrentPlayTime(valueAnimator.getCurrentPlayTime());
            return;
        }
        property.set((Object)t, (Object)f);
    }
}
