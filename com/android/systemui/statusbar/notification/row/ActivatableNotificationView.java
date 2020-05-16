// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.R$id;
import com.android.systemui.R$drawable;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.graphics.Color;
import com.android.systemui.R$color;
import android.util.MathUtils;
import android.view.ViewPropertyAnimator;
import android.view.ViewAnimationUtils;
import android.graphics.Paint;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.view.View;
import com.android.systemui.statusbar.notification.NotificationUtils;
import android.util.AttributeSet;
import android.content.Context;
import android.view.animation.PathInterpolator;
import com.android.systemui.Gefingerpoken;
import com.android.systemui.statusbar.notification.FakeShadowView;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.RectF;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;

public abstract class ActivatableNotificationView extends ExpandableOutlineView
{
    private static final Interpolator ACTIVATE_INVERSE_ALPHA_INTERPOLATOR;
    private static final Interpolator ACTIVATE_INVERSE_INTERPOLATOR;
    private AccessibilityManager mAccessibilityManager;
    private boolean mActivated;
    private float mAnimationTranslationY;
    private float mAppearAnimationFraction;
    private RectF mAppearAnimationRect;
    private float mAppearAnimationTranslation;
    private ValueAnimator mAppearAnimator;
    private ObjectAnimator mBackgroundAnimator;
    private ValueAnimator mBackgroundColorAnimator;
    private NotificationBackgroundView mBackgroundDimmed;
    NotificationBackgroundView mBackgroundNormal;
    private ValueAnimator$AnimatorUpdateListener mBackgroundVisibilityUpdater;
    int mBgTint;
    private Interpolator mCurrentAlphaInterpolator;
    private Interpolator mCurrentAppearInterpolator;
    private int mCurrentBackgroundTint;
    private boolean mDimmed;
    private int mDimmedAlpha;
    private float mDimmedBackgroundFadeInAmount;
    private boolean mDismissed;
    private boolean mDrawingAppearAnimation;
    private FakeShadowView mFakeShadow;
    private boolean mFirstInSection;
    private int mHeadsUpAddStartLocation;
    private float mHeadsUpLocation;
    private boolean mIsAppearing;
    private boolean mIsBelowSpeedBump;
    private boolean mIsHeadsUpAnimation;
    private boolean mLastInSection;
    private boolean mNeedsDimming;
    private float mNormalBackgroundVisibilityAmount;
    private int mNormalColor;
    private int mNormalRippleColor;
    private OnActivatedListener mOnActivatedListener;
    private OnDimmedListener mOnDimmedListener;
    private float mOverrideAmount;
    private int mOverrideTint;
    private boolean mRefocusOnDismiss;
    private boolean mShadowHidden;
    private final Interpolator mSlowOutFastInInterpolator;
    private final Interpolator mSlowOutLinearInInterpolator;
    private int mStartTint;
    private int mTargetTint;
    private int mTintedRippleColor;
    private Gefingerpoken mTouchHandler;
    
    static {
        ACTIVATE_INVERSE_INTERPOLATOR = (Interpolator)new PathInterpolator(0.6f, 0.0f, 0.5f, 1.0f);
        ACTIVATE_INVERSE_ALPHA_INTERPOLATOR = (Interpolator)new PathInterpolator(0.0f, 0.0f, 0.5f, 1.0f);
    }
    
    public ActivatableNotificationView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mBgTint = 0;
        this.mAppearAnimationRect = new RectF();
        this.mAppearAnimationFraction = -1.0f;
        this.mDimmedBackgroundFadeInAmount = -1.0f;
        this.mBackgroundVisibilityUpdater = (ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                final ActivatableNotificationView this$0 = ActivatableNotificationView.this;
                this$0.setNormalBackgroundVisibilityAmount(this$0.mBackgroundNormal.getAlpha());
                final ActivatableNotificationView this$2 = ActivatableNotificationView.this;
                this$2.mDimmedBackgroundFadeInAmount = this$2.mBackgroundDimmed.getAlpha();
            }
        };
        this.mSlowOutFastInInterpolator = (Interpolator)new PathInterpolator(0.8f, 0.0f, 0.6f, 1.0f);
        this.mSlowOutLinearInInterpolator = (Interpolator)new PathInterpolator(0.8f, 0.0f, 1.0f, 1.0f);
        this.setClipChildren(false);
        this.setClipToPadding(false);
        this.updateColors();
        this.initDimens();
    }
    
    private void applyBackgroundRoundness(final float n, final float n2) {
        this.mBackgroundDimmed.setRoundness(n, n2);
        this.mBackgroundNormal.setRoundness(n, n2);
    }
    
    private int calculateBgColor(final boolean b, final boolean b2) {
        if (b2 && this.mOverrideTint != 0) {
            return NotificationUtils.interpolateColors(this.calculateBgColor(b, false), this.mOverrideTint, this.mOverrideAmount);
        }
        if (b) {
            final int mBgTint = this.mBgTint;
            if (mBgTint != 0) {
                return mBgTint;
            }
        }
        return this.mNormalColor;
    }
    
    private void cancelAppearAnimation() {
        final ValueAnimator mAppearAnimator = this.mAppearAnimator;
        if (mAppearAnimator != null) {
            mAppearAnimator.cancel();
            this.mAppearAnimator = null;
        }
    }
    
    private void cancelFadeAnimations() {
        final ObjectAnimator mBackgroundAnimator = this.mBackgroundAnimator;
        if (mBackgroundAnimator != null) {
            mBackgroundAnimator.cancel();
        }
        this.mBackgroundDimmed.animate().cancel();
        this.mBackgroundNormal.animate().cancel();
    }
    
    private void enableAppearDrawing(final boolean mDrawingAppearAnimation) {
        if (mDrawingAppearAnimation != this.mDrawingAppearAnimation) {
            if (!(this.mDrawingAppearAnimation = mDrawingAppearAnimation)) {
                this.setContentAlpha(1.0f);
                this.mAppearAnimationFraction = -1.0f;
                this.setOutlineRect(null);
            }
            this.invalidate();
        }
    }
    
    private void fadeDimmedBackground() {
        this.mBackgroundDimmed.animate().cancel();
        this.mBackgroundNormal.animate().cancel();
        if (this.mActivated) {
            this.updateBackground();
            return;
        }
        if (!this.shouldHideBackground()) {
            if (this.mDimmed) {
                this.mBackgroundDimmed.setVisibility(0);
            }
            else {
                this.mBackgroundNormal.setVisibility(0);
            }
        }
        final boolean mDimmed = this.mDimmed;
        float n = 1.0f;
        float floatValue;
        if (mDimmed) {
            floatValue = 1.0f;
        }
        else {
            floatValue = 0.0f;
        }
        if (this.mDimmed) {
            n = 0.0f;
        }
        int n2 = 220;
        final ObjectAnimator mBackgroundAnimator = this.mBackgroundAnimator;
        if (mBackgroundAnimator != null) {
            floatValue = (float)mBackgroundAnimator.getAnimatedValue();
            final int n3 = (int)this.mBackgroundAnimator.getCurrentPlayTime();
            this.mBackgroundAnimator.removeAllListeners();
            this.mBackgroundAnimator.cancel();
            if ((n2 = n3) <= 0) {
                this.updateBackground();
                return;
            }
        }
        this.mBackgroundNormal.setAlpha(floatValue);
        (this.mBackgroundAnimator = ObjectAnimator.ofFloat((Object)this.mBackgroundNormal, View.ALPHA, new float[] { floatValue, n })).setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        this.mBackgroundAnimator.setDuration((long)n2);
        this.mBackgroundAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                ActivatableNotificationView.this.updateBackground();
                ActivatableNotificationView.this.mBackgroundAnimator = null;
                ActivatableNotificationView.this.mDimmedBackgroundFadeInAmount = -1.0f;
            }
        });
        this.mBackgroundAnimator.addUpdateListener(this.mBackgroundVisibilityUpdater);
        this.mBackgroundAnimator.start();
    }
    
    private int getRippleColor() {
        if (this.mBgTint != 0) {
            return this.mTintedRippleColor;
        }
        return this.mNormalRippleColor;
    }
    
    private void initDimens() {
        this.mHeadsUpAddStartLocation = this.getResources().getDimensionPixelSize(17105344);
    }
    
    private void setContentAlpha(final float alpha) {
        final View contentView = this.getContentView();
        if (contentView.hasOverlappingRendering()) {
            int n;
            if (alpha != 0.0f && alpha != 1.0f) {
                n = 2;
            }
            else {
                n = 0;
            }
            if (contentView.getLayerType() != n) {
                contentView.setLayerType(n, (Paint)null);
            }
        }
        contentView.setAlpha(alpha);
    }
    
    private void setNormalBackgroundVisibilityAmount(final float mNormalBackgroundVisibilityAmount) {
        this.mNormalBackgroundVisibilityAmount = mNormalBackgroundVisibilityAmount;
        this.updateOutlineAlpha();
    }
    
    private void startActivateAnimation(final boolean b) {
        if (!this.isAttachedToWindow()) {
            return;
        }
        if (!this.isDimmable()) {
            return;
        }
        final int n = this.mBackgroundNormal.getWidth() / 2;
        final int n2 = this.mBackgroundNormal.getActualHeight() / 2;
        final float n3 = (float)Math.sqrt(n * n + n2 * n2);
        float n4 = 0.0f;
        Animator animator;
        if (b) {
            animator = ViewAnimationUtils.createCircularReveal((View)this.mBackgroundNormal, n, n2, n3, 0.0f);
        }
        else {
            animator = ViewAnimationUtils.createCircularReveal((View)this.mBackgroundNormal, n, n2, 0.0f, n3);
        }
        this.mBackgroundNormal.setVisibility(0);
        Interpolator interpolator;
        Interpolator activate_INVERSE_INTERPOLATOR;
        if (!b) {
            activate_INVERSE_INTERPOLATOR = (interpolator = Interpolators.LINEAR_OUT_SLOW_IN);
        }
        else {
            activate_INVERSE_INTERPOLATOR = ActivatableNotificationView.ACTIVATE_INVERSE_INTERPOLATOR;
            interpolator = ActivatableNotificationView.ACTIVATE_INVERSE_ALPHA_INTERPOLATOR;
        }
        animator.setInterpolator((TimeInterpolator)activate_INVERSE_INTERPOLATOR);
        animator.setDuration(220L);
        if (b) {
            this.mBackgroundNormal.setAlpha(1.0f);
            animator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    ActivatableNotificationView.this.updateBackground();
                }
            });
            animator.start();
        }
        else {
            this.mBackgroundNormal.setAlpha(0.4f);
            animator.start();
        }
        final ViewPropertyAnimator animate = this.mBackgroundNormal.animate();
        if (!b) {
            n4 = 1.0f;
        }
        animate.alpha(n4).setInterpolator((TimeInterpolator)interpolator).setUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$ActivatableNotificationView$OICE3JOCIwmVbbHi746Bfw_wQKU(this, b)).setDuration(220L);
    }
    
    private void startAppearAnimation(final boolean mIsAppearing, float n, final long startDelay, final long n2, final Runnable runnable, final AnimatorListenerAdapter animatorListenerAdapter) {
        this.cancelAppearAnimation();
        final float n3 = n * this.getActualHeight();
        this.mAnimationTranslationY = n3;
        final float mAppearAnimationFraction = this.mAppearAnimationFraction;
        n = 1.0f;
        if (mAppearAnimationFraction == -1.0f) {
            if (mIsAppearing) {
                this.mAppearAnimationFraction = 0.0f;
                this.mAppearAnimationTranslation = n3;
            }
            else {
                this.mAppearAnimationFraction = 1.0f;
                this.mAppearAnimationTranslation = 0.0f;
            }
        }
        this.mIsAppearing = mIsAppearing;
        if (mIsAppearing) {
            this.mCurrentAppearInterpolator = this.mSlowOutFastInInterpolator;
            this.mCurrentAlphaInterpolator = Interpolators.LINEAR_OUT_SLOW_IN;
        }
        else {
            this.mCurrentAppearInterpolator = Interpolators.FAST_OUT_SLOW_IN;
            this.mCurrentAlphaInterpolator = this.mSlowOutLinearInInterpolator;
            n = 0.0f;
        }
        (this.mAppearAnimator = ValueAnimator.ofFloat(new float[] { this.mAppearAnimationFraction, n })).setInterpolator((TimeInterpolator)Interpolators.LINEAR);
        this.mAppearAnimator.setDuration((long)(n2 * Math.abs(this.mAppearAnimationFraction - n)));
        this.mAppearAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$ActivatableNotificationView$zXl7sYwVdaC__0arcFQWEk_iqbo(this));
        if (animatorListenerAdapter != null) {
            this.mAppearAnimator.addListener((Animator$AnimatorListener)animatorListenerAdapter);
        }
        if (startDelay > 0L) {
            this.updateAppearAnimationAlpha();
            this.updateAppearRect();
            this.mAppearAnimator.setStartDelay(startDelay);
        }
        this.mAppearAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            private boolean mWasCancelled;
            
            public void onAnimationCancel(final Animator animator) {
                this.mWasCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                final Runnable val$onFinishedRunnable = runnable;
                if (val$onFinishedRunnable != null) {
                    val$onFinishedRunnable.run();
                }
                if (!this.mWasCancelled) {
                    ActivatableNotificationView.this.enableAppearDrawing(false);
                    ActivatableNotificationView.this.onAppearAnimationFinished(mIsAppearing);
                }
            }
            
            public void onAnimationStart(final Animator animator) {
                this.mWasCancelled = false;
            }
        });
        this.mAppearAnimator.start();
    }
    
    private void updateAppearAnimationAlpha() {
        this.setContentAlpha(this.mCurrentAlphaInterpolator.getInterpolation(Math.min(1.0f, this.mAppearAnimationFraction / 1.0f)));
    }
    
    private void updateAppearRect() {
        final float n = 1.0f - this.mAppearAnimationFraction;
        final float mAppearAnimationTranslation = this.mCurrentAppearInterpolator.getInterpolation(n) * this.mAnimationTranslationY;
        this.mAppearAnimationTranslation = mAppearAnimationTranslation;
        final float n2 = n - 0.0f;
        final float interpolation = this.mCurrentAppearInterpolator.getInterpolation(Math.min(1.0f, Math.max(0.0f, n2 / 0.8f)));
        float n3;
        if (this.mIsHeadsUpAnimation && !this.mIsAppearing) {
            n3 = 0.0f;
        }
        else {
            n3 = 0.05f;
        }
        final float n4 = 1.0f - interpolation;
        final float n5 = MathUtils.lerp(n3, 1.0f, n4) * this.getWidth();
        float lerp;
        float n6;
        if (this.mIsHeadsUpAnimation) {
            lerp = MathUtils.lerp(this.mHeadsUpLocation, 0.0f, n4);
            n6 = n5 + lerp;
        }
        else {
            lerp = this.getWidth() * 0.5f - n5 / 2.0f;
            n6 = this.getWidth() - lerp;
        }
        final float interpolation2 = this.mCurrentAppearInterpolator.getInterpolation(Math.max(0.0f, n2 / 1.0f));
        final int actualHeight = this.getActualHeight();
        final float mAnimationTranslationY = this.mAnimationTranslationY;
        float n7;
        float n8;
        if (mAnimationTranslationY > 0.0f) {
            n7 = actualHeight - mAnimationTranslationY * interpolation2 * 0.1f - mAppearAnimationTranslation;
            n8 = interpolation2 * n7;
        }
        else {
            final float n9 = (float)actualHeight;
            n8 = (mAnimationTranslationY + n9) * interpolation2 * 0.1f - mAppearAnimationTranslation;
            n7 = n9 * (1.0f - interpolation2) + interpolation2 * n8;
        }
        this.mAppearAnimationRect.set(lerp, n8, n6, n7);
        final float mAppearAnimationTranslation2 = this.mAppearAnimationTranslation;
        this.setOutlineRect(lerp, n8 + mAppearAnimationTranslation2, n6, n7 + mAppearAnimationTranslation2);
    }
    
    private void updateBackgroundTint(final boolean b) {
        final ValueAnimator mBackgroundColorAnimator = this.mBackgroundColorAnimator;
        if (mBackgroundColorAnimator != null) {
            mBackgroundColorAnimator.cancel();
        }
        final int rippleColor = this.getRippleColor();
        this.mBackgroundDimmed.setRippleColor(rippleColor);
        this.mBackgroundNormal.setRippleColor(rippleColor);
        final int calculateBgColor = this.calculateBgColor();
        if (!b) {
            this.setBackgroundTintColor(calculateBgColor);
        }
        else {
            final int mCurrentBackgroundTint = this.mCurrentBackgroundTint;
            if (calculateBgColor != mCurrentBackgroundTint) {
                this.mStartTint = mCurrentBackgroundTint;
                this.mTargetTint = calculateBgColor;
                (this.mBackgroundColorAnimator = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f })).addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$ActivatableNotificationView$RyrpIvGKNair8LtB_lYHJy_dGQs(this));
                this.mBackgroundColorAnimator.setDuration(360L);
                this.mBackgroundColorAnimator.setInterpolator((TimeInterpolator)Interpolators.LINEAR);
                this.mBackgroundColorAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                    public void onAnimationEnd(final Animator animator) {
                        ActivatableNotificationView.this.mBackgroundColorAnimator = null;
                    }
                });
                this.mBackgroundColorAnimator.start();
            }
        }
    }
    
    private void updateColors() {
        this.mNormalColor = super.mContext.getColor(R$color.notification_material_background_color);
        this.mTintedRippleColor = super.mContext.getColor(R$color.notification_ripple_tinted_color);
        this.mNormalRippleColor = super.mContext.getColor(R$color.notification_ripple_untinted_color);
        this.mDimmedAlpha = Color.alpha(super.mContext.getColor(R$color.notification_material_background_dimmed_color));
    }
    
    private void updateOutlineAlpha() {
        this.setOutlineAlpha(0.3f * this.mNormalBackgroundVisibilityAmount + 0.7f);
    }
    
    @Override
    protected void applyRoundness() {
        super.applyRoundness();
        this.applyBackgroundRoundness(this.getCurrentBackgroundRadiusTop(), this.getCurrentBackgroundRadiusBottom());
    }
    
    public int calculateBgColor() {
        return this.calculateBgColor(true, true);
    }
    
    public void cancelAppearDrawing() {
        this.cancelAppearAnimation();
        this.enableAppearDrawing(false);
    }
    
    @Override
    protected boolean childNeedsClipping(final View view) {
        return (view instanceof NotificationBackgroundView && this.isClippingNeeded()) || super.childNeedsClipping(view);
    }
    
    protected boolean disallowSingleClick(final MotionEvent motionEvent) {
        return false;
    }
    
    public void dismiss(final boolean mRefocusOnDismiss) {
        this.mDismissed = true;
        this.mRefocusOnDismiss = mRefocusOnDismiss;
    }
    
    protected void dispatchDraw(final Canvas canvas) {
        if (this.mDrawingAppearAnimation) {
            canvas.save();
            canvas.translate(0.0f, this.mAppearAnimationTranslation);
        }
        super.dispatchDraw(canvas);
        if (this.mDrawingAppearAnimation) {
            canvas.restore();
        }
    }
    
    public void drawableHotspotChanged(final float n, final float n2) {
        if (!this.mDimmed) {
            this.mBackgroundNormal.drawableHotspotChanged(n, n2);
        }
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mDimmed) {
            this.mBackgroundDimmed.setState(this.getDrawableState());
        }
        else {
            this.mBackgroundNormal.setState(this.getDrawableState());
        }
    }
    
    public int getBackgroundColorWithoutTint() {
        return this.calculateBgColor(false, false);
    }
    
    protected abstract View getContentView();
    
    public int getCurrentBackgroundTint() {
        return this.mCurrentBackgroundTint;
    }
    
    public int getHeadsUpHeightWithoutHeader() {
        return this.getHeight();
    }
    
    protected boolean handleSlideBack() {
        return false;
    }
    
    protected void initBackground() {
        this.mBackgroundNormal.setCustomBackground(R$drawable.notification_material_bg);
        this.mBackgroundDimmed.setCustomBackground(R$drawable.notification_material_bg_dim);
    }
    
    public boolean isActive() {
        return this.mActivated;
    }
    
    public boolean isDimmable() {
        return true;
    }
    
    public boolean isDimmed() {
        return this.mDimmed;
    }
    
    public boolean isDismissed() {
        return this.mDismissed;
    }
    
    public boolean isDrawingAppearAnimation() {
        return this.mDrawingAppearAnimation;
    }
    
    public boolean isFirstInSection() {
        return this.mFirstInSection;
    }
    
    protected boolean isInteractive() {
        return true;
    }
    
    public boolean isLastInSection() {
        return this.mLastInSection;
    }
    
    void makeActive() {
        this.startActivateAnimation(false);
        this.mActivated = true;
        final OnActivatedListener mOnActivatedListener = this.mOnActivatedListener;
        if (mOnActivatedListener != null) {
            mOnActivatedListener.onActivated(this);
        }
    }
    
    public void makeInactive(final boolean b) {
        if (this.mActivated) {
            this.mActivated = false;
            if (this.mDimmed) {
                if (b) {
                    this.startActivateAnimation(true);
                }
                else {
                    this.updateBackground();
                }
            }
        }
        final OnActivatedListener mOnActivatedListener = this.mOnActivatedListener;
        if (mOnActivatedListener != null) {
            mOnActivatedListener.onActivationReset(this);
        }
    }
    
    protected void onAppearAnimationFinished(final boolean b) {
    }
    
    protected void onBelowSpeedBumpChanged() {
    }
    
    @Override
    public void onDensityOrFontScaleChanged() {
        super.onDensityOrFontScaleChanged();
        this.initDimens();
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mBackgroundNormal = (NotificationBackgroundView)this.findViewById(R$id.backgroundNormal);
        final FakeShadowView mFakeShadow = (FakeShadowView)this.findViewById(R$id.fake_shadow);
        this.mFakeShadow = mFakeShadow;
        this.mShadowHidden = (mFakeShadow.getVisibility() != 0);
        this.mBackgroundDimmed = (NotificationBackgroundView)this.findViewById(R$id.backgroundDimmed);
        this.initBackground();
        this.updateBackground();
        this.updateBackgroundTint();
        this.updateOutlineAlpha();
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final Gefingerpoken mTouchHandler = this.mTouchHandler;
        return (mTouchHandler != null && mTouchHandler.onInterceptTouchEvent(motionEvent)) || super.onInterceptTouchEvent(motionEvent);
    }
    
    @Override
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.setPivotX((float)(this.getWidth() / 2));
    }
    
    @Override
    public void performAddAnimation(final long n, final long n2, final boolean mIsHeadsUpAnimation) {
        this.enableAppearDrawing(true);
        this.mIsHeadsUpAnimation = mIsHeadsUpAnimation;
        this.mHeadsUpLocation = (float)this.mHeadsUpAddStartLocation;
        if (this.mDrawingAppearAnimation) {
            float n3;
            if (mIsHeadsUpAnimation) {
                n3 = 0.0f;
            }
            else {
                n3 = -1.0f;
            }
            this.startAppearAnimation(true, n3, n, n2, null, null);
        }
    }
    
    public boolean performClick() {
        if (this.mNeedsDimming) {
            final AccessibilityManager mAccessibilityManager = this.mAccessibilityManager;
            if (mAccessibilityManager == null || !mAccessibilityManager.isTouchExplorationEnabled()) {
                return false;
            }
        }
        return super.performClick();
    }
    
    @Override
    public long performRemoveAnimation(final long n, final long n2, final float n3, final boolean mIsHeadsUpAnimation, final float mHeadsUpLocation, final Runnable runnable, final AnimatorListenerAdapter animatorListenerAdapter) {
        this.enableAppearDrawing(true);
        this.mIsHeadsUpAnimation = mIsHeadsUpAnimation;
        this.mHeadsUpLocation = mHeadsUpLocation;
        if (this.mDrawingAppearAnimation) {
            this.startAppearAnimation(false, n3, n2, n, runnable, animatorListenerAdapter);
        }
        else if (runnable != null) {
            runnable.run();
        }
        return 0L;
    }
    
    protected void resetBackgroundAlpha() {
        this.updateBackgroundAlpha(0.0f);
    }
    
    public void setAccessibilityManager(final AccessibilityManager mAccessibilityManager) {
        this.mAccessibilityManager = mAccessibilityManager;
    }
    
    @Override
    public void setActualHeight(final int n, final boolean b) {
        super.setActualHeight(n, b);
        this.setPivotY((float)(n / 2));
        this.mBackgroundNormal.setActualHeight(n);
        this.mBackgroundDimmed.setActualHeight(n);
    }
    
    protected void setBackgroundTintColor(final int mCurrentBackgroundTint) {
        if (mCurrentBackgroundTint != this.mCurrentBackgroundTint) {
            this.mCurrentBackgroundTint = mCurrentBackgroundTint;
            int n;
            if ((n = mCurrentBackgroundTint) == this.mNormalColor) {
                n = 0;
            }
            this.mBackgroundDimmed.setTint(n);
            this.mBackgroundNormal.setTint(n);
        }
    }
    
    protected void setBackgroundTop(final int n) {
        this.mBackgroundDimmed.setBackgroundTop(n);
        this.mBackgroundNormal.setBackgroundTop(n);
    }
    
    @Override
    public void setBelowSpeedBump(final boolean b) {
        super.setBelowSpeedBump(b);
        if (b != this.mIsBelowSpeedBump) {
            this.mIsBelowSpeedBump = b;
            this.updateBackgroundTint();
            this.onBelowSpeedBumpChanged();
        }
    }
    
    @Override
    public void setClipBottomAmount(final int clipBottomAmount) {
        super.setClipBottomAmount(clipBottomAmount);
        this.mBackgroundNormal.setClipBottomAmount(clipBottomAmount);
        this.mBackgroundDimmed.setClipBottomAmount(clipBottomAmount);
    }
    
    @Override
    public void setClipTopAmount(final int clipTopAmount) {
        super.setClipTopAmount(clipTopAmount);
        this.mBackgroundNormal.setClipTopAmount(clipTopAmount);
        this.mBackgroundDimmed.setClipTopAmount(clipTopAmount);
    }
    
    @Override
    public void setDimmed(final boolean mNeedsDimming, final boolean b) {
        this.mNeedsDimming = mNeedsDimming;
        final OnDimmedListener mOnDimmedListener = this.mOnDimmedListener;
        if (mOnDimmedListener != null) {
            mOnDimmedListener.onSetDimmed(mNeedsDimming);
        }
        final boolean mDimmed = mNeedsDimming & this.isDimmable();
        if (this.mDimmed != mDimmed) {
            this.mDimmed = mDimmed;
            this.resetBackgroundAlpha();
            if (b) {
                this.fadeDimmedBackground();
            }
            else {
                this.updateBackground();
            }
        }
    }
    
    @Override
    public void setDistanceToTopRoundness(final float distanceToTopRoundness) {
        super.setDistanceToTopRoundness(distanceToTopRoundness);
        this.mBackgroundNormal.setDistanceToTopRoundness(distanceToTopRoundness);
        this.mBackgroundDimmed.setDistanceToTopRoundness(distanceToTopRoundness);
    }
    
    @Override
    public void setFakeShadowIntensity(final float n, final float n2, final int n3, final int n4) {
        final boolean mShadowHidden = this.mShadowHidden;
        final boolean mShadowHidden2 = n == 0.0f;
        this.mShadowHidden = mShadowHidden2;
        if (!mShadowHidden2 || !mShadowHidden) {
            this.mFakeShadow.setFakeShadowTranslationZ(n * (this.getTranslationZ() + 0.1f), n2, n3, n4);
        }
    }
    
    public void setFirstInSection(final boolean firstInSection) {
        if (firstInSection != this.mFirstInSection) {
            this.mFirstInSection = firstInSection;
            this.mBackgroundNormal.setFirstInSection(firstInSection);
            this.mBackgroundDimmed.setFirstInSection(firstInSection);
        }
    }
    
    public void setLastInSection(final boolean lastInSection) {
        if (lastInSection != this.mLastInSection) {
            this.mLastInSection = lastInSection;
            this.mBackgroundNormal.setLastInSection(lastInSection);
            this.mBackgroundDimmed.setLastInSection(lastInSection);
        }
    }
    
    public void setOnActivatedListener(final OnActivatedListener mOnActivatedListener) {
        this.mOnActivatedListener = mOnActivatedListener;
    }
    
    void setOnDimmedListener(final OnDimmedListener mOnDimmedListener) {
        this.mOnDimmedListener = mOnDimmedListener;
    }
    
    public void setOverrideTintColor(final int mOverrideTint, final float mOverrideAmount) {
        this.mOverrideTint = mOverrideTint;
        this.mOverrideAmount = mOverrideAmount;
        this.setBackgroundTintColor(this.calculateBgColor());
        if (!this.isDimmable() && this.mNeedsDimming) {
            this.mBackgroundNormal.setDrawableAlpha((int)NotificationUtils.interpolate(255.0f, (float)this.mDimmedAlpha, mOverrideAmount));
        }
        else {
            this.mBackgroundNormal.setDrawableAlpha(255);
        }
    }
    
    void setRippleAllowed(final boolean pressedAllowed) {
        this.mBackgroundNormal.setPressedAllowed(pressedAllowed);
    }
    
    protected void setTintColor(final int n) {
        this.setTintColor(n, false);
    }
    
    void setTintColor(final int mBgTint, final boolean b) {
        if (mBgTint != this.mBgTint) {
            this.mBgTint = mBgTint;
            this.updateBackgroundTint(b);
        }
    }
    
    void setTouchHandler(final Gefingerpoken mTouchHandler) {
        this.mTouchHandler = mTouchHandler;
    }
    
    protected boolean shouldHideBackground() {
        return false;
    }
    
    public boolean shouldRefocusOnDismiss() {
        return this.mRefocusOnDismiss || this.isAccessibilityFocused();
    }
    
    boolean superPerformClick() {
        return super.performClick();
    }
    
    public void unDismiss() {
        this.mDismissed = false;
    }
    
    protected void updateBackground() {
        this.cancelFadeAnimations();
        final boolean shouldHideBackground = this.shouldHideBackground();
        float normalBackgroundVisibilityAmount = 1.0f;
        int n = 4;
        if (shouldHideBackground) {
            this.mBackgroundDimmed.setVisibility(4);
            final NotificationBackgroundView mBackgroundNormal = this.mBackgroundNormal;
            if (this.mActivated) {
                n = 0;
            }
            mBackgroundNormal.setVisibility(n);
        }
        else if (this.mDimmed) {
            final boolean b = this.isGroupExpansionChanging() && this.isChildInGroup();
            final NotificationBackgroundView mBackgroundDimmed = this.mBackgroundDimmed;
            int visibility;
            if (b) {
                visibility = 4;
            }
            else {
                visibility = 0;
            }
            mBackgroundDimmed.setVisibility(visibility);
            final NotificationBackgroundView mBackgroundNormal2 = this.mBackgroundNormal;
            if (this.mActivated || b) {
                n = 0;
            }
            mBackgroundNormal2.setVisibility(n);
        }
        else {
            this.mBackgroundDimmed.setVisibility(4);
            this.mBackgroundNormal.setVisibility(0);
            this.mBackgroundNormal.setAlpha(1.0f);
            this.makeInactive(false);
        }
        if (this.mBackgroundNormal.getVisibility() != 0) {
            normalBackgroundVisibilityAmount = 0.0f;
        }
        this.setNormalBackgroundVisibilityAmount(normalBackgroundVisibilityAmount);
    }
    
    protected void updateBackgroundAlpha(float n) {
        if (!this.isChildInGroup() || !this.mDimmed) {
            n = 1.0f;
        }
        final float mDimmedBackgroundFadeInAmount = this.mDimmedBackgroundFadeInAmount;
        float alpha = n;
        if (mDimmedBackgroundFadeInAmount != -1.0f) {
            alpha = n * mDimmedBackgroundFadeInAmount;
        }
        this.mBackgroundDimmed.setAlpha(alpha);
    }
    
    protected void updateBackgroundClipping() {
        this.mBackgroundNormal.setBottomAmountClips(this.isChildInGroup() ^ true);
        this.mBackgroundDimmed.setBottomAmountClips(this.isChildInGroup() ^ true);
    }
    
    protected void updateBackgroundColors() {
        this.updateColors();
        this.initBackground();
        this.updateBackgroundTint();
    }
    
    protected void updateBackgroundTint() {
        this.updateBackgroundTint(false);
    }
    
    public interface OnActivatedListener
    {
        void onActivated(final ActivatableNotificationView p0);
        
        void onActivationReset(final ActivatableNotificationView p0);
    }
    
    interface OnDimmedListener
    {
        void onSetDimmed(final boolean p0);
    }
}
