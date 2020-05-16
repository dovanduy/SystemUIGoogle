// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.transition;

import android.animation.AnimatorListenerAdapter;
import android.animation.Animator$AnimatorPauseListener;
import android.animation.Animator$AnimatorListener;
import android.transition.Transition$TransitionListener;
import android.animation.ObjectAnimator;
import android.graphics.Path;
import androidx.leanback.R$id;
import android.animation.Animator;
import android.transition.Transition;
import android.animation.TimeInterpolator;
import android.transition.TransitionValues;
import android.view.View;

class TranslationAnimationCreator
{
    static Animator createAnimation(final View view, final TransitionValues transitionValues, final int n, final int n2, float translationX, float translationY, final float n3, final float n4, final TimeInterpolator interpolator, final Transition transition) {
        final float translationX2 = view.getTranslationX();
        final float translationY2 = view.getTranslationY();
        final int[] array = (int[])transitionValues.view.getTag(R$id.transitionPosition);
        if (array != null) {
            translationX = array[0] - n + translationX2;
            translationY = array[1] - n2 + translationY2;
        }
        final int round = Math.round(translationX - translationX2);
        final int round2 = Math.round(translationY - translationY2);
        view.setTranslationX(translationX);
        view.setTranslationY(translationY);
        if (translationX == n3 && translationY == n4) {
            return null;
        }
        final Path path = new Path();
        path.moveTo(translationX, translationY);
        path.lineTo(n3, n4);
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)view, View.TRANSLATION_X, View.TRANSLATION_Y, path);
        final TransitionPositionListener transitionPositionListener = new TransitionPositionListener(view, transitionValues.view, round + n, n2 + round2, translationX2, translationY2);
        transition.addListener((Transition$TransitionListener)transitionPositionListener);
        ofFloat.addListener((Animator$AnimatorListener)transitionPositionListener);
        ofFloat.addPauseListener((Animator$AnimatorPauseListener)transitionPositionListener);
        ofFloat.setInterpolator(interpolator);
        return (Animator)ofFloat;
    }
    
    private static class TransitionPositionListener extends AnimatorListenerAdapter implements Transition$TransitionListener
    {
        private final View mMovingView;
        private float mPausedX;
        private float mPausedY;
        private final int mStartX;
        private final int mStartY;
        private final float mTerminalX;
        private final float mTerminalY;
        private int[] mTransitionPosition;
        private final View mViewInHierarchy;
        
        TransitionPositionListener(final View mMovingView, final View mViewInHierarchy, final int n, final int n2, final float mTerminalX, final float mTerminalY) {
            this.mMovingView = mMovingView;
            this.mViewInHierarchy = mViewInHierarchy;
            this.mStartX = n - Math.round(mMovingView.getTranslationX());
            this.mStartY = n2 - Math.round(this.mMovingView.getTranslationY());
            this.mTerminalX = mTerminalX;
            this.mTerminalY = mTerminalY;
            final int[] mTransitionPosition = (int[])this.mViewInHierarchy.getTag(R$id.transitionPosition);
            this.mTransitionPosition = mTransitionPosition;
            if (mTransitionPosition != null) {
                this.mViewInHierarchy.setTag(R$id.transitionPosition, (Object)null);
            }
        }
        
        public void onAnimationCancel(final Animator animator) {
            if (this.mTransitionPosition == null) {
                this.mTransitionPosition = new int[2];
            }
            this.mTransitionPosition[0] = Math.round(this.mStartX + this.mMovingView.getTranslationX());
            this.mTransitionPosition[1] = Math.round(this.mStartY + this.mMovingView.getTranslationY());
            this.mViewInHierarchy.setTag(R$id.transitionPosition, (Object)this.mTransitionPosition);
        }
        
        public void onAnimationEnd(final Animator animator) {
        }
        
        public void onAnimationPause(final Animator animator) {
            this.mPausedX = this.mMovingView.getTranslationX();
            this.mPausedY = this.mMovingView.getTranslationY();
            this.mMovingView.setTranslationX(this.mTerminalX);
            this.mMovingView.setTranslationY(this.mTerminalY);
        }
        
        public void onAnimationResume(final Animator animator) {
            this.mMovingView.setTranslationX(this.mPausedX);
            this.mMovingView.setTranslationY(this.mPausedY);
        }
        
        public void onTransitionCancel(final Transition transition) {
        }
        
        public void onTransitionEnd(final Transition transition) {
            this.mMovingView.setTranslationX(this.mTerminalX);
            this.mMovingView.setTranslationY(this.mTerminalY);
        }
        
        public void onTransitionPause(final Transition transition) {
        }
        
        public void onTransitionResume(final Transition transition) {
        }
        
        public void onTransitionStart(final Transition transition) {
        }
    }
}
