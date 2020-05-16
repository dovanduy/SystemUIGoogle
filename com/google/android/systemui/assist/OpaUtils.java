// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist;

import android.content.ComponentName;
import com.android.internal.app.AssistUtils;
import android.content.Context;
import android.animation.PropertyValuesHolder;
import android.util.ArraySet;
import com.android.systemui.R$dimen;
import android.content.res.Resources;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.RenderNodeAnimator;
import android.animation.Animator;
import android.view.View;
import android.view.animation.PathInterpolator;
import android.view.animation.Interpolator;

public final class OpaUtils
{
    static final Interpolator INTERPOLATOR_40_40;
    static final Interpolator INTERPOLATOR_40_OUT;
    
    static {
        INTERPOLATOR_40_40 = (Interpolator)new PathInterpolator(0.4f, 0.0f, 0.6f, 1.0f);
        INTERPOLATOR_40_OUT = (Interpolator)new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
    }
    
    static Animator getAlphaAnimator(final View target, final float n, final int n2, final int n3, final Interpolator interpolator) {
        final RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(11, n);
        renderNodeAnimator.setTarget(target);
        renderNodeAnimator.setInterpolator((TimeInterpolator)interpolator);
        renderNodeAnimator.setDuration((long)n2);
        renderNodeAnimator.setStartDelay((long)n3);
        return (Animator)renderNodeAnimator;
    }
    
    static Animator getAlphaAnimator(final View view, final float n, final int n2, final Interpolator interpolator) {
        return getAlphaAnimator(view, n, n2, 0, interpolator);
    }
    
    static ObjectAnimator getAlphaObjectAnimator(final View view, final float n, final int n2, final int n3, final Interpolator interpolator) {
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)view, View.ALPHA, new float[] { n });
        ofFloat.setInterpolator((TimeInterpolator)interpolator);
        ofFloat.setDuration((long)n2);
        ofFloat.setStartDelay((long)n3);
        return ofFloat;
    }
    
    static Animator getDeltaAnimatorX(final View target, final Interpolator interpolator, final float n, final int n2) {
        final RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(8, target.getX() + n);
        renderNodeAnimator.setTarget(target);
        renderNodeAnimator.setInterpolator((TimeInterpolator)interpolator);
        renderNodeAnimator.setDuration((long)n2);
        return (Animator)renderNodeAnimator;
    }
    
    static Animator getDeltaAnimatorY(final View target, final Interpolator interpolator, final float n, final int n2) {
        final RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(9, target.getY() + n);
        renderNodeAnimator.setTarget(target);
        renderNodeAnimator.setInterpolator((TimeInterpolator)interpolator);
        renderNodeAnimator.setDuration((long)n2);
        return (Animator)renderNodeAnimator;
    }
    
    static float getDeltaDiamondPositionBottomX() {
        return 0.0f;
    }
    
    static float getDeltaDiamondPositionBottomY(final Resources resources) {
        return getPxVal(resources, R$dimen.opa_diamond_translation);
    }
    
    static float getDeltaDiamondPositionLeftX(final Resources resources) {
        return -getPxVal(resources, R$dimen.opa_diamond_translation);
    }
    
    static float getDeltaDiamondPositionLeftY() {
        return 0.0f;
    }
    
    static float getDeltaDiamondPositionRightX(final Resources resources) {
        return getPxVal(resources, R$dimen.opa_diamond_translation);
    }
    
    static float getDeltaDiamondPositionRightY() {
        return 0.0f;
    }
    
    static float getDeltaDiamondPositionTopX() {
        return 0.0f;
    }
    
    static float getDeltaDiamondPositionTopY(final Resources resources) {
        return -getPxVal(resources, R$dimen.opa_diamond_translation);
    }
    
    static Animator getLongestAnim(final ArraySet<Animator> set) {
        int i = set.size() - 1;
        long n = Long.MIN_VALUE;
        Animator animator = null;
        while (i >= 0) {
            final Animator animator2 = (Animator)set.valueAt(i);
            long totalDuration = n;
            if (animator2.getTotalDuration() > n) {
                totalDuration = animator2.getTotalDuration();
                animator = animator2;
            }
            --i;
            n = totalDuration;
        }
        return animator;
    }
    
    static float getPxVal(final Resources resources, final int n) {
        return (float)resources.getDimensionPixelOffset(n);
    }
    
    static Animator getScaleAnimatorX(final View target, final float n, final int n2, final Interpolator interpolator) {
        final RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(3, n);
        renderNodeAnimator.setTarget(target);
        renderNodeAnimator.setInterpolator((TimeInterpolator)interpolator);
        renderNodeAnimator.setDuration((long)n2);
        return (Animator)renderNodeAnimator;
    }
    
    static Animator getScaleAnimatorY(final View target, final float n, final int n2, final Interpolator interpolator) {
        final RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(4, n);
        renderNodeAnimator.setTarget(target);
        renderNodeAnimator.setInterpolator((TimeInterpolator)interpolator);
        renderNodeAnimator.setDuration((long)n2);
        return (Animator)renderNodeAnimator;
    }
    
    static ObjectAnimator getScaleObjectAnimator(final View view, final float n, final int n2, final Interpolator interpolator) {
        final ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder((Object)view, new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat(View.SCALE_X, new float[] { n }), PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[] { n }) });
        ofPropertyValuesHolder.setDuration((long)n2);
        ofPropertyValuesHolder.setInterpolator((TimeInterpolator)interpolator);
        return ofPropertyValuesHolder;
    }
    
    static Animator getTranslationAnimatorX(final View target, final Interpolator interpolator, final int n) {
        final RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(0, 0.0f);
        renderNodeAnimator.setTarget(target);
        renderNodeAnimator.setInterpolator((TimeInterpolator)interpolator);
        renderNodeAnimator.setDuration((long)n);
        return (Animator)renderNodeAnimator;
    }
    
    static Animator getTranslationAnimatorY(final View target, final Interpolator interpolator, final int n) {
        final RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(1, 0.0f);
        renderNodeAnimator.setTarget(target);
        renderNodeAnimator.setInterpolator((TimeInterpolator)interpolator);
        renderNodeAnimator.setDuration((long)n);
        return (Animator)renderNodeAnimator;
    }
    
    static ObjectAnimator getTranslationObjectAnimatorX(final View view, final Interpolator interpolator, final float n, final float n2, final int n3) {
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)view, View.X, new float[] { n2, n2 + n });
        ofFloat.setInterpolator((TimeInterpolator)interpolator);
        ofFloat.setDuration((long)n3);
        return ofFloat;
    }
    
    static ObjectAnimator getTranslationObjectAnimatorY(final View view, final Interpolator interpolator, final float n, final float n2, final int n3) {
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)view, View.Y, new float[] { n2, n2 + n });
        ofFloat.setInterpolator((TimeInterpolator)interpolator);
        ofFloat.setDuration((long)n3);
        return ofFloat;
    }
    
    static boolean isAGSACurrentAssistant(final Context context) {
        final ComponentName assistComponentForUser = new AssistUtils(context).getAssistComponentForUser(-2);
        return assistComponentForUser != null && "com.google.android.googlequicksearchbox/com.google.android.voiceinteraction.GsaVoiceInteractionService".equals(assistComponentForUser.flattenToString());
    }
}
