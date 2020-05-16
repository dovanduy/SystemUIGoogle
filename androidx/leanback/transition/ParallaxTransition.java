// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.transition;

import android.transition.TransitionValues;
import android.view.ViewGroup;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import androidx.leanback.R$id;
import androidx.leanback.widget.Parallax;
import android.animation.Animator;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.view.animation.LinearInterpolator;
import android.view.animation.Interpolator;
import android.transition.Visibility;

public class ParallaxTransition extends Visibility
{
    static Interpolator sInterpolator;
    
    static {
        ParallaxTransition.sInterpolator = (Interpolator)new LinearInterpolator();
    }
    
    public ParallaxTransition() {
    }
    
    public ParallaxTransition(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    Animator createAnimator(final View view) {
        final Parallax parallax = (Parallax)view.getTag(R$id.lb_parallax_source);
        if (parallax == null) {
            return null;
        }
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
        ofFloat.setInterpolator((TimeInterpolator)ParallaxTransition.sInterpolator);
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener(this) {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                parallax.updateValues();
            }
        });
        return (Animator)ofFloat;
    }
    
    public Animator onAppear(final ViewGroup viewGroup, final View view, final TransitionValues transitionValues, final TransitionValues transitionValues2) {
        if (transitionValues2 == null) {
            return null;
        }
        return this.createAnimator(view);
    }
    
    public Animator onDisappear(final ViewGroup viewGroup, final View view, final TransitionValues transitionValues, final TransitionValues transitionValues2) {
        if (transitionValues == null) {
            return null;
        }
        return this.createAnimator(view);
    }
}
