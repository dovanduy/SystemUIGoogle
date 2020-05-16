// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.MotionEvent;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.R$dimen;
import android.view.ViewConfiguration;
import android.animation.Animator$AnimatorListener;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.ValueAnimator;
import android.view.VelocityTracker;
import android.view.View;
import android.animation.Animator;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import android.animation.AnimatorListenerAdapter;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.plugins.FalsingManager;
import android.content.Context;

public class KeyguardAffordanceHelper
{
    private Runnable mAnimationEndRunnable;
    private final Callback mCallback;
    private final Context mContext;
    private final FalsingManager mFalsingManager;
    private FlingAnimationUtils mFlingAnimationUtils;
    private AnimatorListenerAdapter mFlingEndListener;
    private int mHintGrowAmount;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private KeyguardAffordanceView mLeftIcon;
    private int mMinBackgroundRadius;
    private int mMinFlingVelocity;
    private int mMinTranslationAmount;
    private boolean mMotionCancelled;
    private KeyguardAffordanceView mRightIcon;
    private Animator mSwipeAnimator;
    private boolean mSwipingInProgress;
    private View mTargetedView;
    private int mTouchSlop;
    private boolean mTouchSlopExeeded;
    private int mTouchTargetSize;
    private float mTranslation;
    private float mTranslationOnDown;
    private VelocityTracker mVelocityTracker;
    
    KeyguardAffordanceHelper(final Callback mCallback, final Context mContext, final FalsingManager mFalsingManager) {
        this.mFlingEndListener = new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                KeyguardAffordanceHelper.this.mSwipeAnimator = null;
                KeyguardAffordanceHelper.this.mSwipingInProgress = false;
                KeyguardAffordanceHelper.this.mTargetedView = null;
            }
        };
        this.mAnimationEndRunnable = new Runnable() {
            @Override
            public void run() {
                KeyguardAffordanceHelper.this.mCallback.onAnimationToSideEnded();
            }
        };
        this.mContext = mContext;
        this.mCallback = mCallback;
        this.initIcons();
        final KeyguardAffordanceView mLeftIcon = this.mLeftIcon;
        this.updateIcon(mLeftIcon, 0.0f, mLeftIcon.getRestingAlpha(), false, false, true, false);
        final KeyguardAffordanceView mRightIcon = this.mRightIcon;
        this.updateIcon(mRightIcon, 0.0f, mRightIcon.getRestingAlpha(), false, false, true, false);
        this.mFalsingManager = mFalsingManager;
        this.initDimens();
    }
    
    private void cancelAnimation() {
        final Animator mSwipeAnimator = this.mSwipeAnimator;
        if (mSwipeAnimator != null) {
            mSwipeAnimator.cancel();
        }
    }
    
    private void endMotion(final boolean b, final float n, final float n2) {
        if (this.mSwipingInProgress) {
            this.flingWithCurrentVelocity(b, n, n2);
        }
        else {
            this.mTargetedView = null;
        }
        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }
    
    private void fling(final float n, final boolean b, final boolean b2) {
        float maxTranslationDistance;
        if (b2) {
            maxTranslationDistance = -this.mCallback.getMaxTranslationDistance();
        }
        else {
            maxTranslationDistance = this.mCallback.getMaxTranslationDistance();
        }
        if (b) {
            maxTranslationDistance = 0.0f;
        }
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { this.mTranslation, maxTranslationDistance });
        this.mFlingAnimationUtils.apply((Animator)ofFloat, this.mTranslation, maxTranslationDistance, n);
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                KeyguardAffordanceHelper.this.mTranslation = (float)valueAnimator.getAnimatedValue();
            }
        });
        ofFloat.addListener((Animator$AnimatorListener)this.mFlingEndListener);
        if (!b) {
            this.startFinishingCircleAnimation(0.375f * n, this.mAnimationEndRunnable, b2);
            this.mCallback.onAnimationToSideStarted(b2, this.mTranslation, n);
        }
        else {
            this.reset(true);
        }
        ofFloat.start();
        this.mSwipeAnimator = (Animator)ofFloat;
        if (b) {
            this.mCallback.onSwipingAborted();
        }
    }
    
    private void flingWithCurrentVelocity(final boolean b, float currentVelocity, final float n) {
        currentVelocity = this.getCurrentVelocity(currentVelocity, n);
        final boolean needsAntiFalsing = this.mCallback.needsAntiFalsing();
        boolean b2 = true;
        final boolean b3 = (needsAntiFalsing && this.mFalsingManager.isFalseTouch()) || this.isBelowFalsingThreshold();
        final boolean b4 = this.mTranslation * currentVelocity < 0.0f;
        final boolean b5 = b3 | (Math.abs(currentVelocity) > this.mMinFlingVelocity && b4);
        if (b4 ^ b5) {
            currentVelocity = 0.0f;
        }
        final boolean b6 = b5 || b;
        if (this.mTranslation >= 0.0f) {
            b2 = false;
        }
        this.fling(currentVelocity, b6, b2);
    }
    
    private ValueAnimator getAnimatorToRadius(final boolean b, final int n) {
        KeyguardAffordanceView keyguardAffordanceView;
        if (b) {
            keyguardAffordanceView = this.mRightIcon;
        }
        else {
            keyguardAffordanceView = this.mLeftIcon;
        }
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { keyguardAffordanceView.getCircleRadius(), (float)n });
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                final float floatValue = (float)valueAnimator.getAnimatedValue();
                keyguardAffordanceView.setCircleRadiusWithoutAnimation(floatValue);
                final float access$500 = KeyguardAffordanceHelper.this.getTranslationFromRadius(floatValue);
                final KeyguardAffordanceHelper this$0 = KeyguardAffordanceHelper.this;
                float n = access$500;
                if (b) {
                    n = -access$500;
                }
                this$0.mTranslation = n;
                KeyguardAffordanceHelper.this.updateIconsFromTranslation(keyguardAffordanceView);
            }
        });
        return ofFloat;
    }
    
    private float getCurrentVelocity(float n, float n2) {
        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
        if (mVelocityTracker == null) {
            return 0.0f;
        }
        mVelocityTracker.computeCurrentVelocity(1000);
        final float xVelocity = this.mVelocityTracker.getXVelocity();
        final float yVelocity = this.mVelocityTracker.getYVelocity();
        n -= this.mInitialTouchX;
        n2 -= this.mInitialTouchY;
        n2 = (n = (xVelocity * n + yVelocity * n2) / (float)Math.hypot(n, n2));
        if (this.mTargetedView == this.mRightIcon) {
            n = -n2;
        }
        return n;
    }
    
    private View getIconAtPosition(final float n, final float n2) {
        if (this.leftSwipePossible() && this.isOnIcon((View)this.mLeftIcon, n, n2)) {
            return (View)this.mLeftIcon;
        }
        if (this.rightSwipePossible() && this.isOnIcon((View)this.mRightIcon, n, n2)) {
            return (View)this.mRightIcon;
        }
        return null;
    }
    
    private int getMinTranslationAmount() {
        return (int)(this.mMinTranslationAmount * this.mCallback.getAffordanceFalsingFactor());
    }
    
    private float getRadiusFromTranslation(final float n) {
        final int mTouchSlop = this.mTouchSlop;
        if (n <= mTouchSlop) {
            return 0.0f;
        }
        return (n - mTouchSlop) * 0.25f + this.mMinBackgroundRadius;
    }
    
    private float getScale(final float n, final KeyguardAffordanceView keyguardAffordanceView) {
        return Math.min(n / keyguardAffordanceView.getRestingAlpha() * 0.2f + 0.8f, 1.5f);
    }
    
    private float getTranslationFromRadius(float n) {
        final float n2 = (n - this.mMinBackgroundRadius) / 0.25f;
        n = 0.0f;
        if (n2 > 0.0f) {
            n = n2 + this.mTouchSlop;
        }
        return n;
    }
    
    private void initDimens() {
        final ViewConfiguration value = ViewConfiguration.get(this.mContext);
        this.mTouchSlop = value.getScaledPagingTouchSlop();
        this.mMinFlingVelocity = value.getScaledMinimumFlingVelocity();
        this.mMinTranslationAmount = this.mContext.getResources().getDimensionPixelSize(R$dimen.keyguard_min_swipe_amount);
        this.mMinBackgroundRadius = this.mContext.getResources().getDimensionPixelSize(R$dimen.keyguard_affordance_min_background_radius);
        this.mTouchTargetSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.keyguard_affordance_touch_target_size);
        this.mHintGrowAmount = this.mContext.getResources().getDimensionPixelSize(R$dimen.hint_grow_amount_sideways);
        this.mFlingAnimationUtils = new FlingAnimationUtils(this.mContext.getResources().getDisplayMetrics(), 0.4f);
    }
    
    private void initIcons() {
        this.mLeftIcon = this.mCallback.getLeftIcon();
        this.mRightIcon = this.mCallback.getRightIcon();
        this.updatePreviews();
    }
    
    private void initVelocityTracker() {
        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
        }
        this.mVelocityTracker = VelocityTracker.obtain();
    }
    
    private boolean isBelowFalsingThreshold() {
        return Math.abs(this.mTranslation) < Math.abs(this.mTranslationOnDown) + this.getMinTranslationAmount();
    }
    
    private boolean isOnIcon(final View view, final float n, final float n2) {
        return Math.hypot(n - (view.getX() + view.getWidth() / 2.0f), n2 - (view.getY() + view.getHeight() / 2.0f)) <= this.mTouchTargetSize / 2;
    }
    
    private boolean leftSwipePossible() {
        return this.mLeftIcon.getVisibility() == 0;
    }
    
    private boolean rightSwipePossible() {
        return this.mRightIcon.getVisibility() == 0;
    }
    
    private void setTranslation(float n, final boolean b, final boolean b2) {
        if (!this.rightSwipePossible()) {
            n = Math.max(0.0f, n);
        }
        if (!this.leftSwipePossible()) {
            n = Math.min(0.0f, n);
        }
        final float abs = Math.abs(n);
        if (n != this.mTranslation || b) {
            final float n2 = fcmpl(n, 0.0f);
            KeyguardAffordanceView keyguardAffordanceView;
            if (n2 > 0) {
                keyguardAffordanceView = this.mLeftIcon;
            }
            else {
                keyguardAffordanceView = this.mRightIcon;
            }
            KeyguardAffordanceView keyguardAffordanceView2;
            if (n2 > 0) {
                keyguardAffordanceView2 = this.mRightIcon;
            }
            else {
                keyguardAffordanceView2 = this.mLeftIcon;
            }
            final float n3 = abs / this.getMinTranslationAmount();
            final float max = Math.max(1.0f - n3, 0.0f);
            final boolean b3 = b && b2;
            final boolean b4 = b && !b2;
            final float radiusFromTranslation = this.getRadiusFromTranslation(abs);
            final boolean b5 = b && this.isBelowFalsingThreshold();
            if (!b) {
                this.updateIcon(keyguardAffordanceView, radiusFromTranslation, n3 + keyguardAffordanceView.getRestingAlpha() * max, false, false, false, false);
            }
            else {
                this.updateIcon(keyguardAffordanceView, 0.0f, max * keyguardAffordanceView.getRestingAlpha(), b3, b5, true, b4);
            }
            this.updateIcon(keyguardAffordanceView2, 0.0f, max * keyguardAffordanceView2.getRestingAlpha(), b3, b5, b, b4);
            this.mTranslation = n;
        }
    }
    
    private void startFinishingCircleAnimation(final float n, final Runnable runnable, final boolean b) {
        KeyguardAffordanceView keyguardAffordanceView;
        if (b) {
            keyguardAffordanceView = this.mRightIcon;
        }
        else {
            keyguardAffordanceView = this.mLeftIcon;
        }
        keyguardAffordanceView.finishAnimation(n, runnable);
    }
    
    private void startHintAnimationPhase1(final boolean b, final Runnable runnable) {
        KeyguardAffordanceView mTargetedView;
        if (b) {
            mTargetedView = this.mRightIcon;
        }
        else {
            mTargetedView = this.mLeftIcon;
        }
        final ValueAnimator animatorToRadius = this.getAnimatorToRadius(b, this.mHintGrowAmount);
        animatorToRadius.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            private boolean mCancelled;
            
            public void onAnimationCancel(final Animator animator) {
                this.mCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                if (this.mCancelled) {
                    KeyguardAffordanceHelper.this.mSwipeAnimator = null;
                    KeyguardAffordanceHelper.this.mTargetedView = null;
                    runnable.run();
                }
                else {
                    KeyguardAffordanceHelper.this.startUnlockHintAnimationPhase2(b, runnable);
                }
            }
        });
        animatorToRadius.setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN);
        animatorToRadius.setDuration(200L);
        animatorToRadius.start();
        this.mSwipeAnimator = (Animator)animatorToRadius;
        this.mTargetedView = (View)mTargetedView;
    }
    
    private void startSwiping(final View mTargetedView) {
        this.mCallback.onSwipingStarted(mTargetedView == this.mRightIcon);
        this.mSwipingInProgress = true;
        this.mTargetedView = mTargetedView;
    }
    
    private void startUnlockHintAnimationPhase2(final boolean b, final Runnable runnable) {
        final ValueAnimator animatorToRadius = this.getAnimatorToRadius(b, 0);
        animatorToRadius.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                KeyguardAffordanceHelper.this.mSwipeAnimator = null;
                KeyguardAffordanceHelper.this.mTargetedView = null;
                runnable.run();
            }
        });
        animatorToRadius.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_LINEAR_IN);
        animatorToRadius.setDuration(350L);
        animatorToRadius.setStartDelay(500L);
        animatorToRadius.start();
        this.mSwipeAnimator = (Animator)animatorToRadius;
    }
    
    private void trackMovement(final MotionEvent motionEvent) {
        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(motionEvent);
        }
    }
    
    private void updateIcon(final KeyguardAffordanceView keyguardAffordanceView, final float circleRadiusWithoutAnimation, final float n, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        if (keyguardAffordanceView.getVisibility() != 0 && !b3) {
            return;
        }
        if (b4) {
            keyguardAffordanceView.setCircleRadiusWithoutAnimation(circleRadiusWithoutAnimation);
        }
        else {
            keyguardAffordanceView.setCircleRadius(circleRadiusWithoutAnimation, b2);
        }
        this.updateIconAlpha(keyguardAffordanceView, n, b);
    }
    
    private void updateIconAlpha(final KeyguardAffordanceView keyguardAffordanceView, final float b, final boolean b2) {
        final float scale = this.getScale(b, keyguardAffordanceView);
        keyguardAffordanceView.setImageAlpha(Math.min(1.0f, b), b2);
        keyguardAffordanceView.setImageScale(scale, b2);
    }
    
    private void updateIconsFromTranslation(final KeyguardAffordanceView keyguardAffordanceView) {
        final float n = Math.abs(this.mTranslation) / this.getMinTranslationAmount();
        final float max = Math.max(0.0f, 1.0f - n);
        KeyguardAffordanceView keyguardAffordanceView2;
        if (keyguardAffordanceView == (keyguardAffordanceView2 = this.mRightIcon)) {
            keyguardAffordanceView2 = this.mLeftIcon;
        }
        this.updateIconAlpha(keyguardAffordanceView, n + keyguardAffordanceView.getRestingAlpha() * max, false);
        this.updateIconAlpha(keyguardAffordanceView2, max * keyguardAffordanceView2.getRestingAlpha(), false);
    }
    
    public void animateHideLeftRightIcon() {
        this.cancelAnimation();
        this.updateIcon(this.mRightIcon, 0.0f, 0.0f, true, false, false, false);
        this.updateIcon(this.mLeftIcon, 0.0f, 0.0f, true, false, false, false);
    }
    
    public boolean isOnAffordanceIcon(final float n, final float n2) {
        return this.isOnIcon((View)this.mLeftIcon, n, n2) || this.isOnIcon((View)this.mRightIcon, n, n2);
    }
    
    public boolean isSwipingInProgress() {
        return this.mSwipingInProgress;
    }
    
    public void launchAffordance(boolean b, final boolean b2) {
        if (this.mSwipingInProgress) {
            return;
        }
        KeyguardAffordanceView keyguardAffordanceView;
        if (b2) {
            keyguardAffordanceView = this.mLeftIcon;
        }
        else {
            keyguardAffordanceView = this.mRightIcon;
        }
        KeyguardAffordanceView keyguardAffordanceView2;
        if (b2) {
            keyguardAffordanceView2 = this.mRightIcon;
        }
        else {
            keyguardAffordanceView2 = this.mLeftIcon;
        }
        this.startSwiping((View)keyguardAffordanceView);
        if (keyguardAffordanceView.getVisibility() != 0) {
            b = false;
        }
        if (b) {
            this.fling(0.0f, false, b2 ^ true);
            this.updateIcon(keyguardAffordanceView2, 0.0f, 0.0f, true, false, true, false);
        }
        else {
            this.mCallback.onAnimationToSideStarted(b2 ^ true, this.mTranslation, 0.0f);
            float mTranslation;
            if (b2) {
                mTranslation = this.mCallback.getMaxTranslationDistance();
            }
            else {
                mTranslation = this.mCallback.getMaxTranslationDistance();
            }
            this.mTranslation = mTranslation;
            this.updateIcon(keyguardAffordanceView2, 0.0f, 0.0f, false, false, true, false);
            keyguardAffordanceView.instantFinishAnimation();
            this.mFlingEndListener.onAnimationEnd((Animator)null);
            this.mAnimationEndRunnable.run();
        }
    }
    
    public void onConfigurationChanged() {
        this.initDimens();
        this.initIcons();
    }
    
    public void onRtlPropertiesChanged() {
        this.initIcons();
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        final boolean mMotionCancelled = this.mMotionCancelled;
        boolean b = false;
        if (mMotionCancelled && actionMasked != 0) {
            return false;
        }
        final float y = motionEvent.getY();
        final float x = motionEvent.getX();
        if (actionMasked == 0) {
            final View iconAtPosition = this.getIconAtPosition(x, y);
            if (iconAtPosition != null) {
                final View mTargetedView = this.mTargetedView;
                if (mTargetedView == null || mTargetedView == iconAtPosition) {
                    if (this.mTargetedView != null) {
                        this.cancelAnimation();
                    }
                    else {
                        this.mTouchSlopExeeded = false;
                    }
                    this.startSwiping(iconAtPosition);
                    this.mInitialTouchX = x;
                    this.mInitialTouchY = y;
                    this.mTranslationOnDown = this.mTranslation;
                    this.initVelocityTracker();
                    this.trackMovement(motionEvent);
                    this.mMotionCancelled = false;
                    return true;
                }
            }
            this.mMotionCancelled = true;
            return false;
        }
        boolean b2;
        if (actionMasked != 1) {
            if (actionMasked != 2) {
                if (actionMasked != 3) {
                    if (actionMasked != 5) {
                        return true;
                    }
                    this.endMotion(this.mMotionCancelled = true, x, y);
                    return true;
                }
                else {
                    b2 = false;
                }
            }
            else {
                this.trackMovement(motionEvent);
                final float n = (float)Math.hypot(x - this.mInitialTouchX, y - this.mInitialTouchY);
                if (!this.mTouchSlopExeeded && n > this.mTouchSlop) {
                    this.mTouchSlopExeeded = true;
                }
                if (this.mSwipingInProgress) {
                    float n2;
                    if (this.mTargetedView == this.mRightIcon) {
                        n2 = Math.min(0.0f, this.mTranslationOnDown - n);
                    }
                    else {
                        n2 = Math.max(0.0f, this.mTranslationOnDown + n);
                    }
                    this.setTranslation(n2, false, false);
                    return true;
                }
                return true;
            }
        }
        else {
            b2 = true;
        }
        if (this.mTargetedView == this.mRightIcon) {
            b = true;
        }
        this.trackMovement(motionEvent);
        this.endMotion(b2 ^ true, x, y);
        if (!this.mTouchSlopExeeded && b2) {
            this.mCallback.onIconClicked(b);
        }
        return true;
    }
    
    public void reset(final boolean b) {
        this.cancelAnimation();
        this.setTranslation(0.0f, true, b);
        this.mMotionCancelled = true;
        if (this.mSwipingInProgress) {
            this.mCallback.onSwipingAborted();
            this.mSwipingInProgress = false;
        }
    }
    
    public void startHintAnimation(final boolean b, final Runnable runnable) {
        this.cancelAnimation();
        this.startHintAnimationPhase1(b, runnable);
    }
    
    public void updatePreviews() {
        this.mLeftIcon.setPreviewView(this.mCallback.getLeftPreview());
        this.mRightIcon.setPreviewView(this.mCallback.getRightPreview());
    }
    
    public interface Callback
    {
        float getAffordanceFalsingFactor();
        
        KeyguardAffordanceView getLeftIcon();
        
        View getLeftPreview();
        
        float getMaxTranslationDistance();
        
        KeyguardAffordanceView getRightIcon();
        
        View getRightPreview();
        
        boolean needsAntiFalsing();
        
        void onAnimationToSideEnded();
        
        void onAnimationToSideStarted(final boolean p0, final float p1, final float p2);
        
        void onIconClicked(final boolean p0);
        
        void onSwipingAborted();
        
        void onSwipingStarted(final boolean p0);
    }
}
