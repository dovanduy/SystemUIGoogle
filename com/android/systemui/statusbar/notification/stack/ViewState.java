// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import com.android.systemui.statusbar.policy.HeadsUpUtil;
import android.view.animation.Interpolator;
import android.animation.PropertyValuesHolder;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Paint;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.util.Property;
import com.android.systemui.R$id;
import com.android.systemui.statusbar.notification.AnimatableProperty;
import com.android.systemui.Dumpable;

public class ViewState implements Dumpable
{
    protected static final AnimationProperties NO_NEW_ANIMATIONS;
    private static final AnimatableProperty SCALE_X_PROPERTY;
    private static final AnimatableProperty SCALE_Y_PROPERTY;
    private static final int TAG_ANIMATOR_ALPHA;
    private static final int TAG_ANIMATOR_TRANSLATION_X;
    private static final int TAG_ANIMATOR_TRANSLATION_Y;
    private static final int TAG_ANIMATOR_TRANSLATION_Z;
    private static final int TAG_END_ALPHA;
    private static final int TAG_END_TRANSLATION_X;
    private static final int TAG_END_TRANSLATION_Y;
    private static final int TAG_END_TRANSLATION_Z;
    private static final int TAG_START_ALPHA;
    private static final int TAG_START_TRANSLATION_X;
    private static final int TAG_START_TRANSLATION_Y;
    private static final int TAG_START_TRANSLATION_Z;
    public float alpha;
    public boolean gone;
    public boolean hidden;
    public float scaleX;
    public float scaleY;
    public float xTranslation;
    public float yTranslation;
    public float zTranslation;
    
    static {
        NO_NEW_ANIMATIONS = new AnimationProperties() {
            AnimationFilter mAnimationFilter = new AnimationFilter();
            
            @Override
            public AnimationFilter getAnimationFilter() {
                return this.mAnimationFilter;
            }
        };
        TAG_ANIMATOR_TRANSLATION_X = R$id.translation_x_animator_tag;
        TAG_ANIMATOR_TRANSLATION_Y = R$id.translation_y_animator_tag;
        TAG_ANIMATOR_TRANSLATION_Z = R$id.translation_z_animator_tag;
        TAG_ANIMATOR_ALPHA = R$id.alpha_animator_tag;
        TAG_END_TRANSLATION_X = R$id.translation_x_animator_end_value_tag;
        TAG_END_TRANSLATION_Y = R$id.translation_y_animator_end_value_tag;
        TAG_END_TRANSLATION_Z = R$id.translation_z_animator_end_value_tag;
        TAG_END_ALPHA = R$id.alpha_animator_end_value_tag;
        TAG_START_TRANSLATION_X = R$id.translation_x_animator_start_value_tag;
        TAG_START_TRANSLATION_Y = R$id.translation_y_animator_start_value_tag;
        TAG_START_TRANSLATION_Z = R$id.translation_z_animator_start_value_tag;
        TAG_START_ALPHA = R$id.alpha_animator_start_value_tag;
        SCALE_X_PROPERTY = new AnimatableProperty() {
            @Override
            public int getAnimationEndTag() {
                return R$id.scale_x_animator_end_value_tag;
            }
            
            @Override
            public int getAnimationStartTag() {
                return R$id.scale_x_animator_start_value_tag;
            }
            
            @Override
            public int getAnimatorTag() {
                return R$id.scale_x_animator_tag;
            }
            
            @Override
            public Property getProperty() {
                return View.SCALE_X;
            }
        };
        SCALE_Y_PROPERTY = new AnimatableProperty() {
            @Override
            public int getAnimationEndTag() {
                return R$id.scale_y_animator_end_value_tag;
            }
            
            @Override
            public int getAnimationStartTag() {
                return R$id.scale_y_animator_start_value_tag;
            }
            
            @Override
            public int getAnimatorTag() {
                return R$id.scale_y_animator_tag;
            }
            
            @Override
            public Property getProperty() {
                return View.SCALE_Y;
            }
        };
    }
    
    public ViewState() {
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
    }
    
    public static long cancelAnimatorAndGetNewDuration(final long b, final ValueAnimator valueAnimator) {
        long max = b;
        if (valueAnimator != null) {
            max = Math.max(valueAnimator.getDuration() - valueAnimator.getCurrentPlayTime(), b);
            valueAnimator.cancel();
        }
        return max;
    }
    
    public static <T> T getChildTag(final View view, final int n) {
        return (T)view.getTag(n);
    }
    
    public static float getFinalTranslationX(final View view) {
        if (view == null) {
            return 0.0f;
        }
        if (getChildTag(view, ViewState.TAG_ANIMATOR_TRANSLATION_X) == null) {
            return view.getTranslationX();
        }
        return getChildTag(view, ViewState.TAG_END_TRANSLATION_X);
    }
    
    public static float getFinalTranslationY(final View view) {
        if (view == null) {
            return 0.0f;
        }
        if (getChildTag(view, ViewState.TAG_ANIMATOR_TRANSLATION_Y) == null) {
            return view.getTranslationY();
        }
        return getChildTag(view, ViewState.TAG_END_TRANSLATION_Y);
    }
    
    public static float getFinalTranslationZ(final View view) {
        if (view == null) {
            return 0.0f;
        }
        if (getChildTag(view, ViewState.TAG_ANIMATOR_TRANSLATION_Z) == null) {
            return view.getTranslationZ();
        }
        return getChildTag(view, ViewState.TAG_END_TRANSLATION_Z);
    }
    
    private static boolean isAnimating(final View view, final int n) {
        return getChildTag(view, n) != null;
    }
    
    public static boolean isAnimating(final View view, final AnimatableProperty animatableProperty) {
        return getChildTag(view, animatableProperty.getAnimatorTag()) != null;
    }
    
    public static boolean isAnimatingY(final View view) {
        return getChildTag(view, ViewState.TAG_ANIMATOR_TRANSLATION_Y) != null;
    }
    
    private void startAlphaAnimation(final View view, final AnimationProperties animationProperties) {
        final Float n = getChildTag(view, ViewState.TAG_START_ALPHA);
        final Float n2 = getChildTag(view, ViewState.TAG_END_ALPHA);
        final float alpha = this.alpha;
        if (n2 != null && n2 == alpha) {
            return;
        }
        final ObjectAnimator objectAnimator = getChildTag(view, ViewState.TAG_ANIMATOR_ALPHA);
        if (!animationProperties.getAnimationFilter().animateAlpha) {
            if (objectAnimator != null) {
                final PropertyValuesHolder[] values = objectAnimator.getValues();
                final float f = n + (alpha - n2);
                values[0].setFloatValues(new float[] { f, alpha });
                view.setTag(ViewState.TAG_START_ALPHA, (Object)f);
                view.setTag(ViewState.TAG_END_ALPHA, (Object)alpha);
                objectAnimator.setCurrentPlayTime(objectAnimator.getCurrentPlayTime());
                return;
            }
            view.setAlpha(alpha);
            if (alpha == 0.0f) {
                view.setVisibility(4);
            }
        }
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)view, View.ALPHA, new float[] { view.getAlpha(), alpha });
        ofFloat.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        view.setLayerType(2, (Paint)null);
        ofFloat.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter(this) {
            public boolean mWasCancelled;
            
            public void onAnimationCancel(final Animator animator) {
                this.mWasCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                view.setLayerType(0, (Paint)null);
                if (alpha == 0.0f && !this.mWasCancelled) {
                    view.setVisibility(4);
                }
                view.setTag(ViewState.TAG_ANIMATOR_ALPHA, (Object)null);
                view.setTag(ViewState.TAG_START_ALPHA, (Object)null);
                view.setTag(ViewState.TAG_END_ALPHA, (Object)null);
            }
            
            public void onAnimationStart(final Animator animator) {
                this.mWasCancelled = false;
            }
        });
        ofFloat.setDuration(cancelAnimatorAndGetNewDuration(animationProperties.duration, (ValueAnimator)objectAnimator));
        if (animationProperties.delay > 0L && (objectAnimator == null || objectAnimator.getAnimatedFraction() == 0.0f)) {
            ofFloat.setStartDelay(animationProperties.delay);
        }
        final AnimatorListenerAdapter animationFinishListener = animationProperties.getAnimationFinishListener(View.ALPHA);
        if (animationFinishListener != null) {
            ofFloat.addListener((Animator$AnimatorListener)animationFinishListener);
        }
        startAnimator((Animator)ofFloat, animationFinishListener);
        view.setTag(ViewState.TAG_ANIMATOR_ALPHA, (Object)ofFloat);
        view.setTag(ViewState.TAG_START_ALPHA, (Object)view.getAlpha());
        view.setTag(ViewState.TAG_END_ALPHA, (Object)alpha);
    }
    
    public static void startAnimator(final Animator animator, final AnimatorListenerAdapter animatorListenerAdapter) {
        if (animatorListenerAdapter != null) {
            animatorListenerAdapter.onAnimationStart(animator);
        }
        animator.start();
    }
    
    private void startXTranslationAnimation(final View view, final AnimationProperties animationProperties) {
        final Float n = getChildTag(view, ViewState.TAG_START_TRANSLATION_X);
        final Float n2 = getChildTag(view, ViewState.TAG_END_TRANSLATION_X);
        final float xTranslation = this.xTranslation;
        if (n2 != null && n2 == xTranslation) {
            return;
        }
        final ObjectAnimator objectAnimator = getChildTag(view, ViewState.TAG_ANIMATOR_TRANSLATION_X);
        if (animationProperties.getAnimationFilter().animateX) {
            final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)view, View.TRANSLATION_X, new float[] { view.getTranslationX(), xTranslation });
            Interpolator interpolator = animationProperties.getCustomInterpolator(view, View.TRANSLATION_X);
            if (interpolator == null) {
                interpolator = Interpolators.FAST_OUT_SLOW_IN;
            }
            ofFloat.setInterpolator((TimeInterpolator)interpolator);
            ofFloat.setDuration(cancelAnimatorAndGetNewDuration(animationProperties.duration, (ValueAnimator)objectAnimator));
            if (animationProperties.delay > 0L && (objectAnimator == null || objectAnimator.getAnimatedFraction() == 0.0f)) {
                ofFloat.setStartDelay(animationProperties.delay);
            }
            final AnimatorListenerAdapter animationFinishListener = animationProperties.getAnimationFinishListener(View.TRANSLATION_X);
            if (animationFinishListener != null) {
                ofFloat.addListener((Animator$AnimatorListener)animationFinishListener);
            }
            ofFloat.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter(this) {
                public void onAnimationEnd(final Animator animator) {
                    view.setTag(ViewState.TAG_ANIMATOR_TRANSLATION_X, (Object)null);
                    view.setTag(ViewState.TAG_START_TRANSLATION_X, (Object)null);
                    view.setTag(ViewState.TAG_END_TRANSLATION_X, (Object)null);
                }
            });
            startAnimator((Animator)ofFloat, animationFinishListener);
            view.setTag(ViewState.TAG_ANIMATOR_TRANSLATION_X, (Object)ofFloat);
            view.setTag(ViewState.TAG_START_TRANSLATION_X, (Object)view.getTranslationX());
            view.setTag(ViewState.TAG_END_TRANSLATION_X, (Object)xTranslation);
            return;
        }
        if (objectAnimator != null) {
            final PropertyValuesHolder[] values = objectAnimator.getValues();
            final float f = n + (xTranslation - n2);
            values[0].setFloatValues(new float[] { f, xTranslation });
            view.setTag(ViewState.TAG_START_TRANSLATION_X, (Object)f);
            view.setTag(ViewState.TAG_END_TRANSLATION_X, (Object)xTranslation);
            objectAnimator.setCurrentPlayTime(objectAnimator.getCurrentPlayTime());
            return;
        }
        view.setTranslationX(xTranslation);
    }
    
    private void startYTranslationAnimation(final View view, final AnimationProperties animationProperties) {
        final Float n = getChildTag(view, ViewState.TAG_START_TRANSLATION_Y);
        final Float n2 = getChildTag(view, ViewState.TAG_END_TRANSLATION_Y);
        final float yTranslation = this.yTranslation;
        if (n2 != null && n2 == yTranslation) {
            return;
        }
        final ObjectAnimator objectAnimator = getChildTag(view, ViewState.TAG_ANIMATOR_TRANSLATION_Y);
        if (animationProperties.getAnimationFilter().shouldAnimateY(view)) {
            final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)view, View.TRANSLATION_Y, new float[] { view.getTranslationY(), yTranslation });
            Interpolator interpolator = animationProperties.getCustomInterpolator(view, View.TRANSLATION_Y);
            if (interpolator == null) {
                interpolator = Interpolators.FAST_OUT_SLOW_IN;
            }
            ofFloat.setInterpolator((TimeInterpolator)interpolator);
            ofFloat.setDuration(cancelAnimatorAndGetNewDuration(animationProperties.duration, (ValueAnimator)objectAnimator));
            if (animationProperties.delay > 0L && (objectAnimator == null || objectAnimator.getAnimatedFraction() == 0.0f)) {
                ofFloat.setStartDelay(animationProperties.delay);
            }
            final AnimatorListenerAdapter animationFinishListener = animationProperties.getAnimationFinishListener(View.TRANSLATION_Y);
            if (animationFinishListener != null) {
                ofFloat.addListener((Animator$AnimatorListener)animationFinishListener);
            }
            ofFloat.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    HeadsUpUtil.setIsClickedHeadsUpNotification(view, false);
                    view.setTag(ViewState.TAG_ANIMATOR_TRANSLATION_Y, (Object)null);
                    view.setTag(ViewState.TAG_START_TRANSLATION_Y, (Object)null);
                    view.setTag(ViewState.TAG_END_TRANSLATION_Y, (Object)null);
                    ViewState.this.onYTranslationAnimationFinished(view);
                }
            });
            startAnimator((Animator)ofFloat, animationFinishListener);
            view.setTag(ViewState.TAG_ANIMATOR_TRANSLATION_Y, (Object)ofFloat);
            view.setTag(ViewState.TAG_START_TRANSLATION_Y, (Object)view.getTranslationY());
            view.setTag(ViewState.TAG_END_TRANSLATION_Y, (Object)yTranslation);
            return;
        }
        if (objectAnimator != null) {
            final PropertyValuesHolder[] values = objectAnimator.getValues();
            final float f = n + (yTranslation - n2);
            values[0].setFloatValues(new float[] { f, yTranslation });
            view.setTag(ViewState.TAG_START_TRANSLATION_Y, (Object)f);
            view.setTag(ViewState.TAG_END_TRANSLATION_Y, (Object)yTranslation);
            objectAnimator.setCurrentPlayTime(objectAnimator.getCurrentPlayTime());
            return;
        }
        view.setTranslationY(yTranslation);
    }
    
    private void startZTranslationAnimation(final View view, final AnimationProperties animationProperties) {
        final Float n = getChildTag(view, ViewState.TAG_START_TRANSLATION_Z);
        final Float n2 = getChildTag(view, ViewState.TAG_END_TRANSLATION_Z);
        final float zTranslation = this.zTranslation;
        if (n2 != null && n2 == zTranslation) {
            return;
        }
        final ObjectAnimator objectAnimator = getChildTag(view, ViewState.TAG_ANIMATOR_TRANSLATION_Z);
        if (!animationProperties.getAnimationFilter().animateZ) {
            if (objectAnimator != null) {
                final PropertyValuesHolder[] values = objectAnimator.getValues();
                final float f = n + (zTranslation - n2);
                values[0].setFloatValues(new float[] { f, zTranslation });
                view.setTag(ViewState.TAG_START_TRANSLATION_Z, (Object)f);
                view.setTag(ViewState.TAG_END_TRANSLATION_Z, (Object)zTranslation);
                objectAnimator.setCurrentPlayTime(objectAnimator.getCurrentPlayTime());
                return;
            }
            view.setTranslationZ(zTranslation);
        }
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)view, View.TRANSLATION_Z, new float[] { view.getTranslationZ(), zTranslation });
        ofFloat.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        ofFloat.setDuration(cancelAnimatorAndGetNewDuration(animationProperties.duration, (ValueAnimator)objectAnimator));
        if (animationProperties.delay > 0L && (objectAnimator == null || objectAnimator.getAnimatedFraction() == 0.0f)) {
            ofFloat.setStartDelay(animationProperties.delay);
        }
        final AnimatorListenerAdapter animationFinishListener = animationProperties.getAnimationFinishListener(View.TRANSLATION_Z);
        if (animationFinishListener != null) {
            ofFloat.addListener((Animator$AnimatorListener)animationFinishListener);
        }
        ofFloat.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter(this) {
            public void onAnimationEnd(final Animator animator) {
                view.setTag(ViewState.TAG_ANIMATOR_TRANSLATION_Z, (Object)null);
                view.setTag(ViewState.TAG_START_TRANSLATION_Z, (Object)null);
                view.setTag(ViewState.TAG_END_TRANSLATION_Z, (Object)null);
            }
        });
        startAnimator((Animator)ofFloat, animationFinishListener);
        view.setTag(ViewState.TAG_ANIMATOR_TRANSLATION_Z, (Object)ofFloat);
        view.setTag(ViewState.TAG_START_TRANSLATION_Z, (Object)view.getTranslationZ());
        view.setTag(ViewState.TAG_END_TRANSLATION_Z, (Object)zTranslation);
    }
    
    private void updateAlphaAnimation(final View view) {
        this.startAlphaAnimation(view, ViewState.NO_NEW_ANIMATIONS);
    }
    
    private void updateAnimation(final View view, final AnimatableProperty animatableProperty, final float n) {
        PropertyAnimator.startAnimation(view, animatableProperty, n, ViewState.NO_NEW_ANIMATIONS);
    }
    
    private void updateAnimationX(final View view) {
        this.startXTranslationAnimation(view, ViewState.NO_NEW_ANIMATIONS);
    }
    
    private void updateAnimationY(final View view) {
        this.startYTranslationAnimation(view, ViewState.NO_NEW_ANIMATIONS);
    }
    
    private void updateAnimationZ(final View view) {
        this.startZTranslationAnimation(view, ViewState.NO_NEW_ANIMATIONS);
    }
    
    protected void abortAnimation(final View view, final int n) {
        final Animator animator = getChildTag(view, n);
        if (animator != null) {
            animator.cancel();
        }
    }
    
    public void animateTo(final View view, final AnimationProperties animationProperties) {
        final int visibility = view.getVisibility();
        final int n = 0;
        final boolean b = visibility == 0;
        final float alpha = this.alpha;
        if (!b && (alpha != 0.0f || view.getAlpha() != 0.0f) && !this.gone && !this.hidden) {
            view.setVisibility(0);
        }
        final float alpha2 = view.getAlpha();
        int n2 = n;
        if (this.alpha != alpha2) {
            n2 = 1;
        }
        int n3 = n2;
        if (view instanceof ExpandableView) {
            n3 = (n2 & ((((ExpandableView)view).willBeGone() ^ true) ? 1 : 0));
        }
        if (view.getTranslationX() != this.xTranslation) {
            this.startXTranslationAnimation(view, animationProperties);
        }
        else {
            this.abortAnimation(view, ViewState.TAG_ANIMATOR_TRANSLATION_X);
        }
        if (view.getTranslationY() != this.yTranslation) {
            this.startYTranslationAnimation(view, animationProperties);
        }
        else {
            this.abortAnimation(view, ViewState.TAG_ANIMATOR_TRANSLATION_Y);
        }
        if (view.getTranslationZ() != this.zTranslation) {
            this.startZTranslationAnimation(view, animationProperties);
        }
        else {
            this.abortAnimation(view, ViewState.TAG_ANIMATOR_TRANSLATION_Z);
        }
        final float scaleX = view.getScaleX();
        final float scaleX2 = this.scaleX;
        if (scaleX != scaleX2) {
            PropertyAnimator.startAnimation(view, ViewState.SCALE_X_PROPERTY, scaleX2, animationProperties);
        }
        else {
            this.abortAnimation(view, ViewState.SCALE_X_PROPERTY.getAnimatorTag());
        }
        final float scaleY = view.getScaleY();
        final float scaleY2 = this.scaleY;
        if (scaleY != scaleY2) {
            PropertyAnimator.startAnimation(view, ViewState.SCALE_Y_PROPERTY, scaleY2, animationProperties);
        }
        else {
            this.abortAnimation(view, ViewState.SCALE_Y_PROPERTY.getAnimatorTag());
        }
        if (n3 != 0) {
            this.startAlphaAnimation(view, animationProperties);
        }
        else {
            this.abortAnimation(view, ViewState.TAG_ANIMATOR_ALPHA);
        }
    }
    
    public void applyToView(final View view) {
        if (this.gone) {
            return;
        }
        if (isAnimating(view, ViewState.TAG_ANIMATOR_TRANSLATION_X)) {
            this.updateAnimationX(view);
        }
        else {
            final float translationX = view.getTranslationX();
            final float xTranslation = this.xTranslation;
            if (translationX != xTranslation) {
                view.setTranslationX(xTranslation);
            }
        }
        if (isAnimating(view, ViewState.TAG_ANIMATOR_TRANSLATION_Y)) {
            this.updateAnimationY(view);
        }
        else {
            final float translationY = view.getTranslationY();
            final float yTranslation = this.yTranslation;
            if (translationY != yTranslation) {
                view.setTranslationY(yTranslation);
            }
        }
        if (isAnimating(view, ViewState.TAG_ANIMATOR_TRANSLATION_Z)) {
            this.updateAnimationZ(view);
        }
        else {
            final float translationZ = view.getTranslationZ();
            final float zTranslation = this.zTranslation;
            if (translationZ != zTranslation) {
                view.setTranslationZ(zTranslation);
            }
        }
        if (isAnimating(view, ViewState.SCALE_X_PROPERTY)) {
            this.updateAnimation(view, ViewState.SCALE_X_PROPERTY, this.scaleX);
        }
        else {
            final float scaleX = view.getScaleX();
            final float scaleX2 = this.scaleX;
            if (scaleX != scaleX2) {
                view.setScaleX(scaleX2);
            }
        }
        if (isAnimating(view, ViewState.SCALE_Y_PROPERTY)) {
            this.updateAnimation(view, ViewState.SCALE_Y_PROPERTY, this.scaleY);
        }
        else {
            final float scaleY = view.getScaleY();
            final float scaleY2 = this.scaleY;
            if (scaleY != scaleY2) {
                view.setScaleY(scaleY2);
            }
        }
        final int visibility = view.getVisibility();
        final float alpha = this.alpha;
        final int n = 1;
        final int n2 = 0;
        boolean b = false;
        Label_0272: {
            Label_0269: {
                if (alpha != 0.0f) {
                    if (this.hidden) {
                        if (!this.isAnimating(view)) {
                            break Label_0269;
                        }
                        if (visibility != 0) {
                            break Label_0269;
                        }
                    }
                    b = false;
                    break Label_0272;
                }
            }
            b = true;
        }
        if (isAnimating(view, ViewState.TAG_ANIMATOR_ALPHA)) {
            this.updateAlphaAnimation(view);
        }
        else {
            final float alpha2 = view.getAlpha();
            final float alpha3 = this.alpha;
            if (alpha2 != alpha3) {
                final boolean b2 = alpha3 == 1.0f;
                int n3;
                if (!b && !b2 && view.hasOverlappingRendering()) {
                    n3 = n;
                }
                else {
                    n3 = 0;
                }
                final int layerType = view.getLayerType();
                int n4;
                if (n3 != 0) {
                    n4 = 2;
                }
                else {
                    n4 = 0;
                }
                if (layerType != n4) {
                    view.setLayerType(n4, (Paint)null);
                }
                view.setAlpha(this.alpha);
            }
        }
        int visibility2 = n2;
        if (b) {
            visibility2 = 4;
        }
        if (visibility2 != visibility && (!(view instanceof ExpandableView) || !((ExpandableView)view).willBeGone())) {
            view.setVisibility(visibility2);
        }
    }
    
    public void cancelAnimations(final View view) {
        final Animator animator = getChildTag(view, ViewState.TAG_ANIMATOR_TRANSLATION_X);
        if (animator != null) {
            animator.cancel();
        }
        final Animator animator2 = getChildTag(view, ViewState.TAG_ANIMATOR_TRANSLATION_Y);
        if (animator2 != null) {
            animator2.cancel();
        }
        final Animator animator3 = getChildTag(view, ViewState.TAG_ANIMATOR_TRANSLATION_Z);
        if (animator3 != null) {
            animator3.cancel();
        }
        final Animator animator4 = getChildTag(view, ViewState.TAG_ANIMATOR_ALPHA);
        if (animator4 != null) {
            animator4.cancel();
        }
    }
    
    public void copyFrom(final ViewState viewState) {
        this.alpha = viewState.alpha;
        this.xTranslation = viewState.xTranslation;
        this.yTranslation = viewState.yTranslation;
        this.zTranslation = viewState.zTranslation;
        this.gone = viewState.gone;
        this.hidden = viewState.hidden;
        this.scaleX = viewState.scaleX;
        this.scaleY = viewState.scaleY;
    }
    
    @Override
    public void dump(FileDescriptor fileDescriptor, final PrintWriter printWriter, String[] obj) {
        obj = (String[])(Object)new StringBuilder();
        ((StringBuilder)(Object)obj).append("ViewState { ");
        fileDescriptor = (FileDescriptor)this.getClass();
        int n = 1;
        while (true) {
            Label_0171: {
                if (fileDescriptor == null) {
                    break Label_0171;
                }
                final Field[] declaredFields = ((Class)fileDescriptor).getDeclaredFields();
                final int length = declaredFields.length;
                int n2 = 0;
            Label_0150_Outer:
                while (true) {
                    Label_0163: {
                        if (n2 >= length) {
                            break Label_0163;
                        }
                        final Field field = declaredFields[n2];
                        final int modifiers = field.getModifiers();
                        int n3 = n;
                        Label_0153: {
                            if (Modifier.isStatic(modifiers)) {
                                break Label_0153;
                            }
                            n3 = n;
                            if (field.isSynthetic()) {
                                break Label_0153;
                            }
                            if (Modifier.isTransient(modifiers)) {
                                n3 = n;
                                break Label_0153;
                            }
                            if (n == 0) {
                                ((StringBuilder)(Object)obj).append(", ");
                            }
                            while (true) {
                                try {
                                    ((StringBuilder)(Object)obj).append(field.getName());
                                    ((StringBuilder)(Object)obj).append(": ");
                                    field.setAccessible(true);
                                    ((StringBuilder)(Object)obj).append(field.get(this));
                                    n3 = 0;
                                    ++n2;
                                    n = n3;
                                    continue Label_0150_Outer;
                                    ((StringBuilder)(Object)obj).append(" }");
                                    printWriter.print(obj);
                                    return;
                                    fileDescriptor = (FileDescriptor)((Class<Object>)fileDescriptor).getSuperclass();
                                }
                                catch (IllegalAccessException ex) {
                                    continue;
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public void initFrom(final View view) {
        this.alpha = view.getAlpha();
        this.xTranslation = view.getTranslationX();
        this.yTranslation = view.getTranslationY();
        this.zTranslation = view.getTranslationZ();
        final int visibility = view.getVisibility();
        final boolean b = true;
        this.gone = (visibility == 8);
        this.hidden = (view.getVisibility() == 4 && b);
        this.scaleX = view.getScaleX();
        this.scaleY = view.getScaleY();
    }
    
    public boolean isAnimating(final View view) {
        return isAnimating(view, ViewState.TAG_ANIMATOR_TRANSLATION_X) || isAnimating(view, ViewState.TAG_ANIMATOR_TRANSLATION_Y) || isAnimating(view, ViewState.TAG_ANIMATOR_TRANSLATION_Z) || isAnimating(view, ViewState.TAG_ANIMATOR_ALPHA) || isAnimating(view, ViewState.SCALE_X_PROPERTY) || isAnimating(view, ViewState.SCALE_Y_PROPERTY);
    }
    
    protected void onYTranslationAnimationFinished(final View view) {
        if (this.hidden && !this.gone) {
            view.setVisibility(4);
        }
    }
}
