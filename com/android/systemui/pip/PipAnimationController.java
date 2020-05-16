// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip;

import com.android.internal.annotations.VisibleForTesting;
import android.animation.Animator;
import android.animation.Animator$AnimatorListener;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.ValueAnimator;
import android.view.SurfaceControl$Transaction;
import android.graphics.Rect;
import android.view.SurfaceControl;
import android.animation.TimeInterpolator;
import android.view.animation.AnimationUtils;
import android.content.Context;
import android.view.animation.Interpolator;

public class PipAnimationController
{
    private PipTransitionAnimator mCurrentAnimator;
    private final Interpolator mFastOutSlowInInterpolator;
    private final PipSurfaceTransactionHelper mSurfaceTransactionHelper;
    
    PipAnimationController(final Context context, final PipSurfaceTransactionHelper mSurfaceTransactionHelper) {
        this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563661);
        this.mSurfaceTransactionHelper = mSurfaceTransactionHelper;
    }
    
    private PipTransitionAnimator setupPipTransitionAnimator(final PipTransitionAnimator pipTransitionAnimator) {
        pipTransitionAnimator.setSurfaceTransactionHelper(this.mSurfaceTransactionHelper);
        pipTransitionAnimator.setInterpolator((TimeInterpolator)this.mFastOutSlowInInterpolator);
        pipTransitionAnimator.setFloatValues(new float[] { 0.0f, 1.0f });
        return pipTransitionAnimator;
    }
    
    PipTransitionAnimator getAnimator(final SurfaceControl surfaceControl, final Rect rect, final float n, final float f) {
        final PipTransitionAnimator mCurrentAnimator = this.mCurrentAnimator;
        if (mCurrentAnimator == null) {
            final PipTransitionAnimator<Float> ofAlpha = PipTransitionAnimator.ofAlpha(surfaceControl, rect, n, f);
            this.setupPipTransitionAnimator(ofAlpha);
            this.mCurrentAnimator = ofAlpha;
        }
        else if (mCurrentAnimator.getAnimationType() == 1 && this.mCurrentAnimator.isRunning()) {
            this.mCurrentAnimator.updateEndValue(f);
        }
        else {
            this.mCurrentAnimator.cancel();
            final PipTransitionAnimator<Float> ofAlpha2 = PipTransitionAnimator.ofAlpha(surfaceControl, rect, n, f);
            this.setupPipTransitionAnimator(ofAlpha2);
            this.mCurrentAnimator = ofAlpha2;
        }
        return this.mCurrentAnimator;
    }
    
    PipTransitionAnimator getAnimator(final SurfaceControl surfaceControl, final Rect rect, final Rect destinationBounds) {
        final PipTransitionAnimator mCurrentAnimator = this.mCurrentAnimator;
        if (mCurrentAnimator == null) {
            final PipTransitionAnimator<Rect> ofBounds = PipTransitionAnimator.ofBounds(surfaceControl, rect, destinationBounds);
            this.setupPipTransitionAnimator(ofBounds);
            this.mCurrentAnimator = ofBounds;
        }
        else if (mCurrentAnimator.getAnimationType() == 0 && this.mCurrentAnimator.isRunning()) {
            this.mCurrentAnimator.setDestinationBounds(destinationBounds);
            this.mCurrentAnimator.updateEndValue(new Rect(destinationBounds));
        }
        else {
            this.mCurrentAnimator.cancel();
            final PipTransitionAnimator<Rect> ofBounds2 = PipTransitionAnimator.ofBounds(surfaceControl, rect, destinationBounds);
            this.setupPipTransitionAnimator(ofBounds2);
            this.mCurrentAnimator = ofBounds2;
        }
        return this.mCurrentAnimator;
    }
    
    PipTransitionAnimator getCurrentAnimator() {
        return this.mCurrentAnimator;
    }
    
    public static class PipAnimationCallback
    {
        public abstract void onPipAnimationCancel(final PipTransitionAnimator p0);
        
        public abstract void onPipAnimationEnd(final SurfaceControl$Transaction p0, final PipTransitionAnimator p1);
        
        public abstract void onPipAnimationStart(final PipTransitionAnimator p0);
    }
    
    public abstract static class PipTransitionAnimator<T> extends ValueAnimator implements ValueAnimator$AnimatorUpdateListener, Animator$AnimatorListener
    {
        private final int mAnimationType;
        private T mCurrentValue;
        private final Rect mDestinationBounds;
        private T mEndValue;
        private final SurfaceControl mLeash;
        private PipAnimationCallback mPipAnimationCallback;
        private T mStartValue;
        private PipSurfaceTransactionHelper.SurfaceControlTransactionFactory mSurfaceControlTransactionFactory;
        private PipSurfaceTransactionHelper mSurfaceTransactionHelper;
        private int mTransitionDirection;
        
        private PipTransitionAnimator(final SurfaceControl mLeash, final int mAnimationType, final Rect rect, final T mStartValue, final T mEndValue) {
            final Rect mDestinationBounds = new Rect();
            this.mDestinationBounds = mDestinationBounds;
            this.mLeash = mLeash;
            this.mAnimationType = mAnimationType;
            mDestinationBounds.set(rect);
            this.mStartValue = mStartValue;
            this.mEndValue = mEndValue;
            this.addListener((Animator$AnimatorListener)this);
            this.addUpdateListener((ValueAnimator$AnimatorUpdateListener)this);
            this.mSurfaceControlTransactionFactory = (PipSurfaceTransactionHelper.SurfaceControlTransactionFactory)_$$Lambda$0FLZQAxNoOm85ohJ3bgjkYQDWsU.INSTANCE;
            this.mTransitionDirection = 0;
        }
        
        static PipTransitionAnimator<Float> ofAlpha(final SurfaceControl surfaceControl, final Rect rect, final float f, final float f2) {
            return new PipTransitionAnimator<Float>(surfaceControl, 1, rect, Float.valueOf(f), Float.valueOf(f2)) {
                @Override
                void applySurfaceControlTransaction(final SurfaceControl surfaceControl, final SurfaceControl$Transaction surfaceControl$Transaction, float f) {
                    f = this.getStartValue() * (1.0f - f) + this.getEndValue() * f;
                    this.setCurrentValue(f);
                    this.getSurfaceTransactionHelper().alpha(surfaceControl$Transaction, surfaceControl, f);
                    surfaceControl$Transaction.apply();
                }
                
                @Override
                void onStartTransaction(final SurfaceControl surfaceControl, final SurfaceControl$Transaction surfaceControl$Transaction) {
                    final PipSurfaceTransactionHelper surfaceTransactionHelper = this.getSurfaceTransactionHelper();
                    surfaceTransactionHelper.crop(surfaceControl$Transaction, surfaceControl, this.getDestinationBounds());
                    surfaceTransactionHelper.round(surfaceControl$Transaction, surfaceControl, this.shouldApplyCornerRadius());
                    surfaceControl$Transaction.apply();
                }
            };
        }
        
        static PipTransitionAnimator<Rect> ofBounds(final SurfaceControl surfaceControl, final Rect rect, final Rect rect2) {
            return new PipTransitionAnimator<Rect>(surfaceControl, 0, rect2, new Rect(rect), new Rect(rect2)) {
                private final Rect mTmpRect = new Rect();
                
                private int getCastedFractionValue(final float n, final float n2, final float n3) {
                    return (int)(n * (1.0f - n3) + n2 * n3 + 0.5f);
                }
                
                @Override
                void applySurfaceControlTransaction(final SurfaceControl surfaceControl, final SurfaceControl$Transaction surfaceControl$Transaction, final float n) {
                    final Rect rect = this.getStartValue();
                    final Rect rect2 = this.getEndValue();
                    this.mTmpRect.set(this.getCastedFractionValue((float)rect.left, (float)rect2.left, n), this.getCastedFractionValue((float)rect.top, (float)rect2.top, n), this.getCastedFractionValue((float)rect.right, (float)rect2.right, n), this.getCastedFractionValue((float)rect.bottom, (float)rect2.bottom, n));
                    this.setCurrentValue(this.mTmpRect);
                    if (this.inScaleTransition()) {
                        this.getSurfaceTransactionHelper().scale(surfaceControl$Transaction, surfaceControl, rect, this.mTmpRect);
                    }
                    else {
                        this.getSurfaceTransactionHelper().crop(surfaceControl$Transaction, surfaceControl, this.mTmpRect);
                    }
                    surfaceControl$Transaction.apply();
                }
                
                @Override
                void onEndTransaction(final SurfaceControl surfaceControl, final SurfaceControl$Transaction surfaceControl$Transaction) {
                    if (!this.inScaleTransition()) {
                        return;
                    }
                    this.getSurfaceTransactionHelper().resetScale(surfaceControl$Transaction, surfaceControl, this.getDestinationBounds());
                }
                
                @Override
                void onStartTransaction(final SurfaceControl surfaceControl, final SurfaceControl$Transaction surfaceControl$Transaction) {
                    final PipSurfaceTransactionHelper surfaceTransactionHelper = this.getSurfaceTransactionHelper();
                    surfaceTransactionHelper.alpha(surfaceControl$Transaction, surfaceControl, 1.0f);
                    surfaceTransactionHelper.round(surfaceControl$Transaction, surfaceControl, this.shouldApplyCornerRadius());
                    surfaceControl$Transaction.apply();
                }
            };
        }
        
        abstract void applySurfaceControlTransaction(final SurfaceControl p0, final SurfaceControl$Transaction p1, final float p2);
        
        int getAnimationType() {
            return this.mAnimationType;
        }
        
        Rect getDestinationBounds() {
            return this.mDestinationBounds;
        }
        
        T getEndValue() {
            return this.mEndValue;
        }
        
        T getStartValue() {
            return this.mStartValue;
        }
        
        PipSurfaceTransactionHelper getSurfaceTransactionHelper() {
            return this.mSurfaceTransactionHelper;
        }
        
        int getTransitionDirection() {
            return this.mTransitionDirection;
        }
        
        boolean inScaleTransition() {
            final int mAnimationType = this.mAnimationType;
            final boolean b = false;
            if (mAnimationType != 0) {
                return false;
            }
            final int transitionDirection = this.getTransitionDirection();
            boolean b2 = b;
            if (transitionDirection != 3) {
                b2 = b;
                if (transitionDirection != 2) {
                    b2 = true;
                }
            }
            return b2;
        }
        
        SurfaceControl$Transaction newSurfaceControlTransaction() {
            return this.mSurfaceControlTransactionFactory.getTransaction();
        }
        
        public void onAnimationCancel(final Animator animator) {
            final PipAnimationCallback mPipAnimationCallback = this.mPipAnimationCallback;
            if (mPipAnimationCallback != null) {
                mPipAnimationCallback.onPipAnimationCancel(this);
            }
        }
        
        public void onAnimationEnd(final Animator animator) {
            this.mCurrentValue = this.mEndValue;
            final SurfaceControl$Transaction surfaceControlTransaction = this.newSurfaceControlTransaction();
            this.onEndTransaction(this.mLeash, surfaceControlTransaction);
            final PipAnimationCallback mPipAnimationCallback = this.mPipAnimationCallback;
            if (mPipAnimationCallback != null) {
                mPipAnimationCallback.onPipAnimationEnd(surfaceControlTransaction, this);
            }
        }
        
        public void onAnimationRepeat(final Animator animator) {
        }
        
        public void onAnimationStart(final Animator animator) {
            this.mCurrentValue = this.mStartValue;
            this.onStartTransaction(this.mLeash, this.newSurfaceControlTransaction());
            final PipAnimationCallback mPipAnimationCallback = this.mPipAnimationCallback;
            if (mPipAnimationCallback != null) {
                mPipAnimationCallback.onPipAnimationStart(this);
            }
        }
        
        public void onAnimationUpdate(final ValueAnimator valueAnimator) {
            this.applySurfaceControlTransaction(this.mLeash, this.newSurfaceControlTransaction(), valueAnimator.getAnimatedFraction());
        }
        
        void onEndTransaction(final SurfaceControl surfaceControl, final SurfaceControl$Transaction surfaceControl$Transaction) {
        }
        
        void onStartTransaction(final SurfaceControl surfaceControl, final SurfaceControl$Transaction surfaceControl$Transaction) {
        }
        
        void setCurrentValue(final T mCurrentValue) {
            this.mCurrentValue = mCurrentValue;
        }
        
        void setDestinationBounds(final Rect rect) {
            this.mDestinationBounds.set(rect);
            if (this.mAnimationType == 1) {
                this.onStartTransaction(this.mLeash, this.newSurfaceControlTransaction());
            }
        }
        
        PipTransitionAnimator<T> setPipAnimationCallback(final PipAnimationCallback mPipAnimationCallback) {
            this.mPipAnimationCallback = mPipAnimationCallback;
            return this;
        }
        
        @VisibleForTesting
        void setSurfaceControlTransactionFactory(final PipSurfaceTransactionHelper.SurfaceControlTransactionFactory mSurfaceControlTransactionFactory) {
            this.mSurfaceControlTransactionFactory = mSurfaceControlTransactionFactory;
        }
        
        void setSurfaceTransactionHelper(final PipSurfaceTransactionHelper mSurfaceTransactionHelper) {
            this.mSurfaceTransactionHelper = mSurfaceTransactionHelper;
        }
        
        PipTransitionAnimator<T> setTransitionDirection(final int mTransitionDirection) {
            if (mTransitionDirection != 1) {
                this.mTransitionDirection = mTransitionDirection;
            }
            return this;
        }
        
        boolean shouldApplyCornerRadius() {
            return this.mTransitionDirection != 3;
        }
        
        void updateEndValue(final T mEndValue) {
            this.mEndValue = mEndValue;
            this.mStartValue = this.mCurrentValue;
        }
    }
}
