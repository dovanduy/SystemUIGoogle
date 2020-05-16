// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import androidx.core.view.OneShotPreDrawListener;
import android.view.animation.Transformation;
import android.view.animation.AnimationSet;
import androidx.fragment.R$anim;
import android.animation.AnimatorInflater;
import android.content.res.Resources$NotFoundException;
import android.view.animation.AnimationUtils;
import androidx.fragment.R$id;
import android.content.Context;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.view.animation.Animation;
import android.view.ViewGroup;
import android.view.animation.Animation$AnimationListener;
import android.animation.Animator;
import android.view.View;
import androidx.core.os.CancellationSignal;

class FragmentAnim
{
    static void animateRemoveFragment(final Fragment fragment, final AnimationOrAnimator animationOrAnimator, final FragmentTransition.Callback callback) {
        final View mView = fragment.mView;
        final ViewGroup mContainer = fragment.mContainer;
        mContainer.startViewTransition(mView);
        final CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener((CancellationSignal.OnCancelListener)new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                if (fragment.getAnimatingAway() != null) {
                    final View animatingAway = fragment.getAnimatingAway();
                    fragment.setAnimatingAway(null);
                    animatingAway.clearAnimation();
                }
                fragment.setAnimator(null);
            }
        });
        callback.onStart(fragment, cancellationSignal);
        if (animationOrAnimator.animation != null) {
            final EndViewTransitionAnimation endViewTransitionAnimation = new EndViewTransitionAnimation(animationOrAnimator.animation, mContainer, mView);
            fragment.setAnimatingAway(fragment.mView);
            ((Animation)endViewTransitionAnimation).setAnimationListener((Animation$AnimationListener)new Animation$AnimationListener() {
                public void onAnimationEnd(final Animation animation) {
                    mContainer.post((Runnable)new Runnable() {
                        @Override
                        public void run() {
                            if (fragment.getAnimatingAway() != null) {
                                fragment.setAnimatingAway(null);
                                final Animation$AnimationListener this$0 = (Animation$AnimationListener)Animation$AnimationListener.this;
                                callback.onComplete(fragment, cancellationSignal);
                            }
                        }
                    });
                }
                
                public void onAnimationRepeat(final Animation animation) {
                }
                
                public void onAnimationStart(final Animation animation) {
                }
            });
            fragment.mView.startAnimation((Animation)endViewTransitionAnimation);
        }
        else {
            final Animator animator = animationOrAnimator.animator;
            fragment.setAnimator(animator);
            animator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    mContainer.endViewTransition(mView);
                    animator = fragment.getAnimator();
                    fragment.setAnimator(null);
                    if (animator != null && mContainer.indexOfChild(mView) < 0) {
                        callback.onComplete(fragment, cancellationSignal);
                    }
                }
            });
            animator.setTarget((Object)fragment.mView);
            animator.start();
        }
    }
    
    static AnimationOrAnimator loadAnimation(final Context context, final Fragment fragment, final boolean b) {
        final int nextTransition = fragment.getNextTransition();
        final int nextAnim = fragment.getNextAnim();
        fragment.setNextAnim(0);
        final ViewGroup mContainer = fragment.mContainer;
        if (mContainer != null && mContainer.getTag(R$id.visible_removing_fragment_view_tag) != null) {
            fragment.mContainer.setTag(R$id.visible_removing_fragment_view_tag, (Object)null);
        }
        final ViewGroup mContainer2 = fragment.mContainer;
        if (mContainer2 != null && mContainer2.getLayoutTransition() != null) {
            return null;
        }
        final Animation onCreateAnimation = fragment.onCreateAnimation(nextTransition, b, nextAnim);
        if (onCreateAnimation != null) {
            return new AnimationOrAnimator(onCreateAnimation);
        }
        final Animator onCreateAnimator = fragment.onCreateAnimator(nextTransition, b, nextAnim);
        if (onCreateAnimator != null) {
            return new AnimationOrAnimator(onCreateAnimator);
        }
        if (nextAnim == 0 || !"anim".equals(context.getResources().getResourceTypeName(nextAnim))) {
            goto Label_0236;
        }
        try {
            final Animation loadAnimation = AnimationUtils.loadAnimation(context, nextAnim);
            if (loadAnimation != null) {
                return new AnimationOrAnimator(loadAnimation);
            }
            goto Label_0181;
        }
        catch (Resources$NotFoundException ex) {
            throw ex;
        }
        catch (RuntimeException ex2) {
            goto Label_0181;
        }
        try {
            final Animator loadAnimator = AnimatorInflater.loadAnimator(context, nextAnim);
            if (loadAnimator != null) {
                return new AnimationOrAnimator(loadAnimator);
            }
            goto Label_0236;
        }
        catch (RuntimeException ex3) {}
    }
    
    private static int transitToAnimResourceId(int n, final boolean b) {
        if (n != 4097) {
            if (n != 4099) {
                if (n != 8194) {
                    n = -1;
                }
                else if (b) {
                    n = R$anim.fragment_close_enter;
                }
                else {
                    n = R$anim.fragment_close_exit;
                }
            }
            else if (b) {
                n = R$anim.fragment_fade_enter;
            }
            else {
                n = R$anim.fragment_fade_exit;
            }
        }
        else if (b) {
            n = R$anim.fragment_open_enter;
        }
        else {
            n = R$anim.fragment_open_exit;
        }
        return n;
    }
    
    static class AnimationOrAnimator
    {
        public final Animation animation;
        public final Animator animator;
        
        AnimationOrAnimator(final Animator animator) {
            this.animation = null;
            this.animator = animator;
            if (animator != null) {
                return;
            }
            throw new IllegalStateException("Animator cannot be null");
        }
        
        AnimationOrAnimator(final Animation animation) {
            this.animation = animation;
            this.animator = null;
            if (animation != null) {
                return;
            }
            throw new IllegalStateException("Animation cannot be null");
        }
    }
    
    static class EndViewTransitionAnimation extends AnimationSet implements Runnable
    {
        private boolean mAnimating;
        private final View mChild;
        private boolean mEnded;
        private final ViewGroup mParent;
        private boolean mTransitionEnded;
        
        EndViewTransitionAnimation(final Animation animation, final ViewGroup mParent, final View mChild) {
            super(false);
            this.mAnimating = true;
            this.mParent = mParent;
            this.mChild = mChild;
            this.addAnimation(animation);
            this.mParent.post((Runnable)this);
        }
        
        public boolean getTransformation(final long n, final Transformation transformation) {
            this.mAnimating = true;
            if (this.mEnded) {
                return this.mTransitionEnded ^ true;
            }
            if (!super.getTransformation(n, transformation)) {
                this.mEnded = true;
                OneShotPreDrawListener.add((View)this.mParent, this);
            }
            return true;
        }
        
        public boolean getTransformation(final long n, final Transformation transformation, final float n2) {
            this.mAnimating = true;
            if (this.mEnded) {
                return this.mTransitionEnded ^ true;
            }
            if (!super.getTransformation(n, transformation, n2)) {
                this.mEnded = true;
                OneShotPreDrawListener.add((View)this.mParent, this);
            }
            return true;
        }
        
        public void run() {
            if (!this.mEnded && this.mAnimating) {
                this.mAnimating = false;
                this.mParent.post((Runnable)this);
            }
            else {
                this.mParent.endViewTransition(this.mChild);
                this.mTransitionEnded = true;
            }
        }
    }
}
