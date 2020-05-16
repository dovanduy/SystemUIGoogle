// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist;

import android.widget.ImageView$ScaleType;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.view.View$OnTouchListener;
import android.view.View$AccessibilityDelegate;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import android.view.ViewConfiguration;
import android.os.SystemClock;
import com.android.systemui.shared.system.QuickStepContract;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff$Mode;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.Color;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import com.android.systemui.R$drawable;
import android.view.ContextThemeWrapper;
import com.android.systemui.R$style;
import com.android.systemui.R$id;
import android.content.res.Configuration;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.assist.AssistManager;
import android.view.View$OnLongClickListener;
import android.animation.ObjectAnimator;
import com.android.systemui.R$dimen;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import com.android.systemui.Interpolators;
import android.view.animation.PathInterpolator;
import android.util.AttributeSet;
import android.content.Context;
import android.content.res.Resources;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.statusbar.policy.KeyButtonView;
import android.widget.ImageView;
import android.animation.AnimatorSet;
import android.animation.Animator;
import android.util.ArraySet;
import android.view.View;
import java.util.ArrayList;
import android.view.animation.Interpolator;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.android.systemui.statusbar.phone.ButtonInterface;
import android.widget.FrameLayout;

public class OpaLayout extends FrameLayout implements ButtonInterface, FeedbackEffect
{
    private final Interpolator HOME_DISAPPEAR_INTERPOLATOR;
    private final ArrayList<View> mAnimatedViews;
    private int mAnimationState;
    private View mBlue;
    private View mBottom;
    private final ArraySet<Animator> mCurrentAnimators;
    private boolean mDelayTouchFeedback;
    private final Runnable mDiamondAnimation;
    private boolean mDiamondAnimationDelayed;
    private final Interpolator mDiamondInterpolator;
    private long mGestureAnimationSetDuration;
    private AnimatorSet mGestureAnimatorSet;
    private AnimatorSet mGestureLineSet;
    private int mGestureState;
    private View mGreen;
    private ImageView mHalo;
    private KeyButtonView mHome;
    private int mHomeDiameter;
    private boolean mIsPressed;
    private boolean mIsVertical;
    private View mLeft;
    private boolean mOpaEnabled;
    private boolean mOpaEnabledNeedsUpdate;
    private final OverviewProxyService.OverviewProxyListener mOverviewProxyListener;
    private OverviewProxyService mOverviewProxyService;
    private View mRed;
    private Resources mResources;
    private final Runnable mRetract;
    private View mRight;
    private long mStartTime;
    private View mTop;
    private int mTouchDownX;
    private int mTouchDownY;
    private ImageView mWhite;
    private ImageView mWhiteCutout;
    private boolean mWindowVisible;
    private View mYellow;
    
    public OpaLayout(final Context context) {
        this(context, null);
    }
    
    public OpaLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public OpaLayout(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public OpaLayout(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.HOME_DISAPPEAR_INTERPOLATOR = (Interpolator)new PathInterpolator(0.65f, 0.0f, 1.0f, 1.0f);
        this.mDiamondInterpolator = (Interpolator)new PathInterpolator(0.2f, 0.0f, 0.2f, 1.0f);
        this.mCurrentAnimators = (ArraySet<Animator>)new ArraySet();
        this.mAnimatedViews = new ArrayList<View>();
        this.mAnimationState = 0;
        this.mGestureState = 0;
        this.mRetract = new Runnable() {
            @Override
            public void run() {
                OpaLayout.this.cancelCurrentAnimation();
                OpaLayout.this.startRetractAnimation();
            }
        };
        this.mOverviewProxyListener = new OverviewProxyService.OverviewProxyListener() {
            @Override
            public void onConnectionChanged(final boolean b) {
                OpaLayout.this.updateOpaLayout();
            }
        };
        this.mDiamondAnimation = new _$$Lambda$OpaLayout$FW1rmJcZbiemVKFJwNyO1Lz2ero(this);
    }
    
    private boolean allowAnimations() {
        return this.isAttachedToWindow() && this.mWindowVisible;
    }
    
    private void cancelCurrentAnimation() {
        if (!this.mCurrentAnimators.isEmpty()) {
            for (int i = this.mCurrentAnimators.size() - 1; i >= 0; --i) {
                final Animator animator = (Animator)this.mCurrentAnimators.valueAt(i);
                animator.removeAllListeners();
                animator.cancel();
            }
            this.mCurrentAnimators.clear();
            this.mAnimationState = 0;
        }
        final AnimatorSet mGestureAnimatorSet = this.mGestureAnimatorSet;
        if (mGestureAnimatorSet != null) {
            mGestureAnimatorSet.cancel();
            this.mGestureState = 0;
        }
    }
    
    private void endCurrentAnimation() {
        if (!this.mCurrentAnimators.isEmpty()) {
            for (int i = this.mCurrentAnimators.size() - 1; i >= 0; --i) {
                final Animator animator = (Animator)this.mCurrentAnimators.valueAt(i);
                animator.removeAllListeners();
                animator.end();
            }
            this.mCurrentAnimators.clear();
        }
        this.mAnimationState = 0;
    }
    
    private ArraySet<Animator> getCollapseAnimatorSet() {
        final ArraySet set = new ArraySet();
        Animator animator;
        if (this.mIsVertical) {
            animator = OpaUtils.getTranslationAnimatorY(this.mRed, OpaUtils.INTERPOLATOR_40_OUT, 133);
        }
        else {
            animator = OpaUtils.getTranslationAnimatorX(this.mRed, OpaUtils.INTERPOLATOR_40_OUT, 133);
        }
        set.add((Object)animator);
        set.add((Object)OpaUtils.getScaleAnimatorX(this.mRed, 1.0f, 200, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getScaleAnimatorY(this.mRed, 1.0f, 200, OpaUtils.INTERPOLATOR_40_OUT));
        Animator animator2;
        if (this.mIsVertical) {
            animator2 = OpaUtils.getTranslationAnimatorY(this.mBlue, OpaUtils.INTERPOLATOR_40_OUT, 150);
        }
        else {
            animator2 = OpaUtils.getTranslationAnimatorX(this.mBlue, OpaUtils.INTERPOLATOR_40_OUT, 150);
        }
        set.add((Object)animator2);
        set.add((Object)OpaUtils.getScaleAnimatorX(this.mBlue, 1.0f, 200, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getScaleAnimatorY(this.mBlue, 1.0f, 200, OpaUtils.INTERPOLATOR_40_OUT));
        Animator animator3;
        if (this.mIsVertical) {
            animator3 = OpaUtils.getTranslationAnimatorY(this.mYellow, OpaUtils.INTERPOLATOR_40_OUT, 133);
        }
        else {
            animator3 = OpaUtils.getTranslationAnimatorX(this.mYellow, OpaUtils.INTERPOLATOR_40_OUT, 133);
        }
        set.add((Object)animator3);
        set.add((Object)OpaUtils.getScaleAnimatorX(this.mYellow, 1.0f, 200, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getScaleAnimatorY(this.mYellow, 1.0f, 200, OpaUtils.INTERPOLATOR_40_OUT));
        Animator animator4;
        if (this.mIsVertical) {
            animator4 = OpaUtils.getTranslationAnimatorY(this.mGreen, OpaUtils.INTERPOLATOR_40_OUT, 150);
        }
        else {
            animator4 = OpaUtils.getTranslationAnimatorX(this.mGreen, OpaUtils.INTERPOLATOR_40_OUT, 150);
        }
        set.add((Object)animator4);
        set.add((Object)OpaUtils.getScaleAnimatorX(this.mGreen, 1.0f, 200, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getScaleAnimatorY(this.mGreen, 1.0f, 200, OpaUtils.INTERPOLATOR_40_OUT));
        final Animator scaleAnimatorX = OpaUtils.getScaleAnimatorX((View)this.mWhite, 1.0f, 150, Interpolators.FAST_OUT_SLOW_IN);
        final Animator scaleAnimatorY = OpaUtils.getScaleAnimatorY((View)this.mWhite, 1.0f, 150, Interpolators.FAST_OUT_SLOW_IN);
        final Animator scaleAnimatorX2 = OpaUtils.getScaleAnimatorX((View)this.mWhiteCutout, 1.0f, 150, Interpolators.FAST_OUT_SLOW_IN);
        final Animator scaleAnimatorY2 = OpaUtils.getScaleAnimatorY((View)this.mWhiteCutout, 1.0f, 150, Interpolators.FAST_OUT_SLOW_IN);
        final Animator scaleAnimatorX3 = OpaUtils.getScaleAnimatorX((View)this.mHalo, 1.0f, 150, Interpolators.FAST_OUT_SLOW_IN);
        final Animator scaleAnimatorY3 = OpaUtils.getScaleAnimatorY((View)this.mHalo, 1.0f, 150, Interpolators.FAST_OUT_SLOW_IN);
        final Animator alphaAnimator = OpaUtils.getAlphaAnimator((View)this.mHalo, 1.0f, 150, Interpolators.FAST_OUT_SLOW_IN);
        scaleAnimatorX.setStartDelay(33L);
        scaleAnimatorY.setStartDelay(33L);
        scaleAnimatorX2.setStartDelay(33L);
        scaleAnimatorY2.setStartDelay(33L);
        scaleAnimatorX3.setStartDelay(33L);
        scaleAnimatorY3.setStartDelay(33L);
        alphaAnimator.setStartDelay(33L);
        set.add((Object)scaleAnimatorX);
        set.add((Object)scaleAnimatorY);
        set.add((Object)scaleAnimatorX2);
        set.add((Object)scaleAnimatorY2);
        set.add((Object)scaleAnimatorX3);
        set.add((Object)scaleAnimatorY3);
        set.add((Object)alphaAnimator);
        this.getLongestAnim((ArraySet<Animator>)set).addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                OpaLayout.this.mCurrentAnimators.clear();
                OpaLayout.this.skipToStartingValue();
            }
        });
        return (ArraySet<Animator>)set;
    }
    
    private ArraySet<Animator> getDiamondAnimatorSet() {
        final ArraySet set = new ArraySet();
        set.add((Object)OpaUtils.getDeltaAnimatorY(this.mTop, this.mDiamondInterpolator, -OpaUtils.getPxVal(this.mResources, R$dimen.opa_diamond_translation), 200));
        set.add((Object)OpaUtils.getScaleAnimatorX(this.mTop, 0.8f, 200, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getScaleAnimatorY(this.mTop, 0.8f, 200, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getDeltaAnimatorY(this.mBottom, this.mDiamondInterpolator, OpaUtils.getPxVal(this.mResources, R$dimen.opa_diamond_translation), 200));
        set.add((Object)OpaUtils.getScaleAnimatorX(this.mBottom, 0.8f, 200, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getScaleAnimatorY(this.mBottom, 0.8f, 200, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getDeltaAnimatorX(this.mLeft, this.mDiamondInterpolator, -OpaUtils.getPxVal(this.mResources, R$dimen.opa_diamond_translation), 200));
        set.add((Object)OpaUtils.getScaleAnimatorX(this.mLeft, 0.8f, 200, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getScaleAnimatorY(this.mLeft, 0.8f, 200, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getDeltaAnimatorX(this.mRight, this.mDiamondInterpolator, OpaUtils.getPxVal(this.mResources, R$dimen.opa_diamond_translation), 200));
        set.add((Object)OpaUtils.getScaleAnimatorX(this.mRight, 0.8f, 200, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getScaleAnimatorY(this.mRight, 0.8f, 200, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getScaleAnimatorX((View)this.mWhite, 0.625f, 200, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getScaleAnimatorY((View)this.mWhite, 0.625f, 200, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getScaleAnimatorX((View)this.mWhiteCutout, 0.625f, 200, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getScaleAnimatorY((View)this.mWhiteCutout, 0.625f, 200, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getScaleAnimatorX((View)this.mHalo, 0.47619048f, 100, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getScaleAnimatorY((View)this.mHalo, 0.47619048f, 100, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getAlphaAnimator((View)this.mHalo, 0.0f, 100, Interpolators.FAST_OUT_SLOW_IN));
        this.getLongestAnim((ArraySet<Animator>)set).addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationCancel(final Animator animator) {
                OpaLayout.this.mCurrentAnimators.clear();
            }
            
            public void onAnimationEnd(final Animator animator) {
                OpaLayout.this.startLineAnimation();
            }
        });
        return (ArraySet<Animator>)set;
    }
    
    private AnimatorSet getGestureAnimatorSet() {
        final AnimatorSet mGestureLineSet = this.mGestureLineSet;
        if (mGestureLineSet != null) {
            mGestureLineSet.removeAllListeners();
            this.mGestureLineSet.cancel();
            return this.mGestureLineSet;
        }
        this.mGestureLineSet = new AnimatorSet();
        final ObjectAnimator scaleObjectAnimator = OpaUtils.getScaleObjectAnimator((View)this.mWhite, 0.0f, 100, OpaUtils.INTERPOLATOR_40_OUT);
        final ObjectAnimator scaleObjectAnimator2 = OpaUtils.getScaleObjectAnimator((View)this.mWhiteCutout, 0.0f, 100, OpaUtils.INTERPOLATOR_40_OUT);
        final ObjectAnimator scaleObjectAnimator3 = OpaUtils.getScaleObjectAnimator((View)this.mHalo, 0.0f, 100, OpaUtils.INTERPOLATOR_40_OUT);
        scaleObjectAnimator.setStartDelay(50L);
        scaleObjectAnimator2.setStartDelay(50L);
        this.mGestureLineSet.play((Animator)scaleObjectAnimator).with((Animator)scaleObjectAnimator2).with((Animator)scaleObjectAnimator3);
        this.mGestureLineSet.play((Animator)OpaUtils.getScaleObjectAnimator(this.mTop, 0.8f, 200, Interpolators.FAST_OUT_SLOW_IN)).with((Animator)scaleObjectAnimator).with((Animator)OpaUtils.getAlphaObjectAnimator(this.mRed, 1.0f, 50, 130, Interpolators.LINEAR)).with((Animator)OpaUtils.getAlphaObjectAnimator(this.mYellow, 1.0f, 50, 130, Interpolators.LINEAR)).with((Animator)OpaUtils.getAlphaObjectAnimator(this.mBlue, 1.0f, 50, 113, Interpolators.LINEAR)).with((Animator)OpaUtils.getAlphaObjectAnimator(this.mGreen, 1.0f, 50, 113, Interpolators.LINEAR)).with((Animator)OpaUtils.getScaleObjectAnimator(this.mBottom, 0.8f, 200, Interpolators.FAST_OUT_SLOW_IN)).with((Animator)OpaUtils.getScaleObjectAnimator(this.mLeft, 0.8f, 200, Interpolators.FAST_OUT_SLOW_IN)).with((Animator)OpaUtils.getScaleObjectAnimator(this.mRight, 0.8f, 200, Interpolators.FAST_OUT_SLOW_IN));
        if (this.mIsVertical) {
            final ObjectAnimator translationObjectAnimatorY = OpaUtils.getTranslationObjectAnimatorY(this.mRed, OpaUtils.INTERPOLATOR_40_40, OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_ry), this.mRed.getY() + OpaUtils.getDeltaDiamondPositionLeftY(), 350);
            ((Animator)translationObjectAnimatorY).addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    OpaLayout.this.startCollapseAnimation();
                }
            });
            this.mGestureLineSet.play((Animator)translationObjectAnimatorY).with((Animator)scaleObjectAnimator3).with((Animator)OpaUtils.getTranslationObjectAnimatorY(this.mBlue, OpaUtils.INTERPOLATOR_40_40, OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_bg), this.mBlue.getY() + OpaUtils.getDeltaDiamondPositionBottomY(this.mResources), 350)).with((Animator)OpaUtils.getTranslationObjectAnimatorY(this.mYellow, OpaUtils.INTERPOLATOR_40_40, -OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_ry), this.mYellow.getY() + OpaUtils.getDeltaDiamondPositionRightY(), 350)).with((Animator)OpaUtils.getTranslationObjectAnimatorY(this.mGreen, OpaUtils.INTERPOLATOR_40_40, -OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_bg), this.mGreen.getY() + OpaUtils.getDeltaDiamondPositionTopY(this.mResources), 350));
        }
        else {
            final ObjectAnimator translationObjectAnimatorX = OpaUtils.getTranslationObjectAnimatorX(this.mRed, OpaUtils.INTERPOLATOR_40_40, -OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_ry), this.mRed.getX() + OpaUtils.getDeltaDiamondPositionTopX(), 350);
            ((Animator)translationObjectAnimatorX).addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    OpaLayout.this.startCollapseAnimation();
                }
            });
            this.mGestureLineSet.play((Animator)translationObjectAnimatorX).with((Animator)scaleObjectAnimator).with((Animator)OpaUtils.getTranslationObjectAnimatorX(this.mBlue, OpaUtils.INTERPOLATOR_40_40, -OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_bg), this.mBlue.getX() + OpaUtils.getDeltaDiamondPositionLeftX(this.mResources), 350)).with((Animator)OpaUtils.getTranslationObjectAnimatorX(this.mYellow, OpaUtils.INTERPOLATOR_40_40, OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_ry), this.mYellow.getX() + OpaUtils.getDeltaDiamondPositionBottomX(), 350)).with((Animator)OpaUtils.getTranslationObjectAnimatorX(this.mGreen, OpaUtils.INTERPOLATOR_40_40, OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_bg), this.mGreen.getX() + OpaUtils.getDeltaDiamondPositionRightX(this.mResources), 350));
        }
        return this.mGestureLineSet;
    }
    
    private ArraySet<Animator> getLineAnimatorSet() {
        final ArraySet set = new ArraySet();
        if (this.mIsVertical) {
            set.add((Object)OpaUtils.getDeltaAnimatorY(this.mRed, Interpolators.FAST_OUT_SLOW_IN, OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_ry), 225));
            set.add((Object)OpaUtils.getDeltaAnimatorX(this.mRed, Interpolators.FAST_OUT_SLOW_IN, OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_y_translation), 133));
            set.add((Object)OpaUtils.getDeltaAnimatorY(this.mBlue, Interpolators.FAST_OUT_SLOW_IN, OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_bg), 225));
            set.add((Object)OpaUtils.getDeltaAnimatorY(this.mYellow, Interpolators.FAST_OUT_SLOW_IN, -OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_ry), 225));
            set.add((Object)OpaUtils.getDeltaAnimatorX(this.mYellow, Interpolators.FAST_OUT_SLOW_IN, -OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_y_translation), 133));
            set.add((Object)OpaUtils.getDeltaAnimatorY(this.mGreen, Interpolators.FAST_OUT_SLOW_IN, -OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_bg), 225));
        }
        else {
            set.add((Object)OpaUtils.getDeltaAnimatorX(this.mRed, Interpolators.FAST_OUT_SLOW_IN, -OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_ry), 225));
            set.add((Object)OpaUtils.getDeltaAnimatorY(this.mRed, Interpolators.FAST_OUT_SLOW_IN, OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_y_translation), 133));
            set.add((Object)OpaUtils.getDeltaAnimatorX(this.mBlue, Interpolators.FAST_OUT_SLOW_IN, -OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_bg), 225));
            set.add((Object)OpaUtils.getDeltaAnimatorX(this.mYellow, Interpolators.FAST_OUT_SLOW_IN, OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_ry), 225));
            set.add((Object)OpaUtils.getDeltaAnimatorY(this.mYellow, Interpolators.FAST_OUT_SLOW_IN, -OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_y_translation), 133));
            set.add((Object)OpaUtils.getDeltaAnimatorX(this.mGreen, Interpolators.FAST_OUT_SLOW_IN, OpaUtils.getPxVal(this.mResources, R$dimen.opa_line_x_trans_bg), 225));
        }
        set.add((Object)OpaUtils.getScaleAnimatorX((View)this.mWhite, 0.0f, 83, this.HOME_DISAPPEAR_INTERPOLATOR));
        set.add((Object)OpaUtils.getScaleAnimatorY((View)this.mWhite, 0.0f, 83, this.HOME_DISAPPEAR_INTERPOLATOR));
        set.add((Object)OpaUtils.getScaleAnimatorX((View)this.mWhiteCutout, 0.0f, 83, this.HOME_DISAPPEAR_INTERPOLATOR));
        set.add((Object)OpaUtils.getScaleAnimatorY((View)this.mWhiteCutout, 0.0f, 83, this.HOME_DISAPPEAR_INTERPOLATOR));
        set.add((Object)OpaUtils.getScaleAnimatorX((View)this.mHalo, 0.0f, 83, this.HOME_DISAPPEAR_INTERPOLATOR));
        set.add((Object)OpaUtils.getScaleAnimatorY((View)this.mHalo, 0.0f, 83, this.HOME_DISAPPEAR_INTERPOLATOR));
        this.getLongestAnim((ArraySet<Animator>)set).addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationCancel(final Animator animator) {
                OpaLayout.this.mCurrentAnimators.clear();
            }
            
            public void onAnimationEnd(final Animator animator) {
                OpaLayout.this.startCollapseAnimation();
            }
        });
        return (ArraySet<Animator>)set;
    }
    
    private Animator getLongestAnim(final ArraySet<Animator> set) {
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
    
    private ArraySet<Animator> getRetractAnimatorSet() {
        final ArraySet set = new ArraySet();
        set.add((Object)OpaUtils.getTranslationAnimatorX(this.mRed, OpaUtils.INTERPOLATOR_40_OUT, 190));
        set.add((Object)OpaUtils.getTranslationAnimatorY(this.mRed, OpaUtils.INTERPOLATOR_40_OUT, 190));
        set.add((Object)OpaUtils.getScaleAnimatorX(this.mRed, 1.0f, 190, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getScaleAnimatorY(this.mRed, 1.0f, 190, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getTranslationAnimatorX(this.mBlue, OpaUtils.INTERPOLATOR_40_OUT, 190));
        set.add((Object)OpaUtils.getTranslationAnimatorY(this.mBlue, OpaUtils.INTERPOLATOR_40_OUT, 190));
        set.add((Object)OpaUtils.getScaleAnimatorX(this.mBlue, 1.0f, 190, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getScaleAnimatorY(this.mBlue, 1.0f, 190, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getTranslationAnimatorX(this.mGreen, OpaUtils.INTERPOLATOR_40_OUT, 190));
        set.add((Object)OpaUtils.getTranslationAnimatorY(this.mGreen, OpaUtils.INTERPOLATOR_40_OUT, 190));
        set.add((Object)OpaUtils.getScaleAnimatorX(this.mGreen, 1.0f, 190, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getScaleAnimatorY(this.mGreen, 1.0f, 190, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getTranslationAnimatorX(this.mYellow, OpaUtils.INTERPOLATOR_40_OUT, 190));
        set.add((Object)OpaUtils.getTranslationAnimatorY(this.mYellow, OpaUtils.INTERPOLATOR_40_OUT, 190));
        set.add((Object)OpaUtils.getScaleAnimatorX(this.mYellow, 1.0f, 190, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getScaleAnimatorY(this.mYellow, 1.0f, 190, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getScaleAnimatorX((View)this.mWhite, 1.0f, 190, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getScaleAnimatorY((View)this.mWhite, 1.0f, 190, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getScaleAnimatorX((View)this.mWhiteCutout, 1.0f, 190, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getScaleAnimatorY((View)this.mWhiteCutout, 1.0f, 190, OpaUtils.INTERPOLATOR_40_OUT));
        set.add((Object)OpaUtils.getScaleAnimatorX((View)this.mHalo, 1.0f, 190, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getScaleAnimatorY((View)this.mHalo, 1.0f, 190, Interpolators.FAST_OUT_SLOW_IN));
        set.add((Object)OpaUtils.getAlphaAnimator((View)this.mHalo, 1.0f, 190, Interpolators.FAST_OUT_SLOW_IN));
        this.getLongestAnim((ArraySet<Animator>)set).addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                OpaLayout.this.mCurrentAnimators.clear();
                OpaLayout.this.skipToStartingValue();
            }
        });
        return (ArraySet<Animator>)set;
    }
    
    private void setDotsVisible() {
        for (int size = this.mAnimatedViews.size(), i = 0; i < size; ++i) {
            this.mAnimatedViews.get(i).setAlpha(1.0f);
        }
    }
    
    private void skipToStartingValue() {
        for (int size = this.mAnimatedViews.size(), i = 0; i < size; ++i) {
            final View view = this.mAnimatedViews.get(i);
            view.setScaleY(1.0f);
            view.setScaleX(1.0f);
            view.setTranslationY(0.0f);
            view.setTranslationX(0.0f);
            view.setAlpha(0.0f);
        }
        this.mHalo.setAlpha(1.0f);
        this.mWhite.setAlpha(1.0f);
        this.mWhiteCutout.setAlpha(1.0f);
        this.mAnimationState = 0;
        this.mGestureState = 0;
    }
    
    private void startAll(final ArraySet<Animator> set) {
        for (int i = set.size() - 1; i >= 0; --i) {
            ((Animator)set.valueAt(i)).start();
        }
    }
    
    private void startCollapseAnimation() {
        if (this.allowAnimations()) {
            this.mCurrentAnimators.clear();
            this.mCurrentAnimators.addAll((ArraySet)this.getCollapseAnimatorSet());
            this.mAnimationState = 3;
            this.startAll(this.mCurrentAnimators);
        }
        else {
            this.skipToStartingValue();
        }
    }
    
    private void startDiamondAnimation() {
        if (this.allowAnimations()) {
            this.mCurrentAnimators.clear();
            this.setDotsVisible();
            this.mCurrentAnimators.addAll((ArraySet)this.getDiamondAnimatorSet());
            this.mAnimationState = 1;
            this.startAll(this.mCurrentAnimators);
        }
        else {
            this.skipToStartingValue();
        }
    }
    
    private void startLineAnimation() {
        if (this.allowAnimations()) {
            this.mCurrentAnimators.clear();
            this.mCurrentAnimators.addAll((ArraySet)this.getLineAnimatorSet());
            this.mAnimationState = 3;
            this.startAll(this.mCurrentAnimators);
        }
        else {
            this.skipToStartingValue();
        }
    }
    
    private void startRetractAnimation() {
        if (this.allowAnimations()) {
            this.mCurrentAnimators.clear();
            this.mCurrentAnimators.addAll((ArraySet)this.getRetractAnimatorSet());
            this.mAnimationState = 2;
            this.startAll(this.mCurrentAnimators);
        }
        else {
            this.skipToStartingValue();
        }
    }
    
    public void abortCurrentGesture() {
        this.mHome.abortCurrentGesture();
        this.mIsPressed = false;
        this.mDiamondAnimationDelayed = false;
        this.removeCallbacks(this.mDiamondAnimation);
        this.cancelLongPress();
        final int mAnimationState = this.mAnimationState;
        if (mAnimationState == 3 || mAnimationState == 1) {
            this.mRetract.run();
        }
    }
    
    public boolean getOpaEnabled() {
        if (this.mOpaEnabledNeedsUpdate) {
            Dependency.get((Class<AssistManagerGoogle>)AssistManager.class).dispatchOpaEnabledState();
            if (this.mOpaEnabledNeedsUpdate) {
                Log.w("OpaLayout", "mOpaEnabledNeedsUpdate not cleared by AssistManagerGoogle!");
            }
        }
        return this.mOpaEnabled;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mOverviewProxyService.addCallback(this.mOverviewProxyListener);
        this.mOpaEnabledNeedsUpdate = true;
        this.post((Runnable)new _$$Lambda$qadRDAXGXctZjQf_VlEtWjxSCCE(this));
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.updateOpaLayout();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mOverviewProxyService.removeCallback(this.mOverviewProxyListener);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mResources = this.getResources();
        this.mBlue = this.findViewById(R$id.blue);
        this.mRed = this.findViewById(R$id.red);
        this.mYellow = this.findViewById(R$id.yellow);
        this.mGreen = this.findViewById(R$id.green);
        this.mWhite = (ImageView)this.findViewById(R$id.white);
        this.mWhiteCutout = (ImageView)this.findViewById(R$id.white_cutout);
        this.mHalo = (ImageView)this.findViewById(R$id.halo);
        this.mHome = (KeyButtonView)this.findViewById(R$id.home_button);
        this.mHalo.setImageDrawable((Drawable)KeyButtonDrawable.create((Context)new ContextThemeWrapper(this.getContext(), R$style.DualToneLightTheme), (Context)new ContextThemeWrapper(this.getContext(), R$style.DualToneDarkTheme), R$drawable.halo, true, null));
        this.mHomeDiameter = this.mResources.getDimensionPixelSize(R$dimen.opa_disabled_home_diameter);
        final Paint paint = new Paint();
        paint.setXfermode((Xfermode)new PorterDuffXfermode(PorterDuff$Mode.DST_OUT));
        this.mWhiteCutout.setLayerType(2, paint);
        this.mAnimatedViews.add(this.mBlue);
        this.mAnimatedViews.add(this.mRed);
        this.mAnimatedViews.add(this.mYellow);
        this.mAnimatedViews.add(this.mGreen);
        this.mAnimatedViews.add((View)this.mWhite);
        this.mAnimatedViews.add((View)this.mWhiteCutout);
        this.mAnimatedViews.add((View)this.mHalo);
        this.mOverviewProxyService = Dependency.get(OverviewProxyService.class);
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        if (this.getOpaEnabled() && ValueAnimator.areAnimatorsEnabled()) {
            if (this.mGestureState == 0) {
                final int action = motionEvent.getAction();
                if (action != 0) {
                    if (action != 1) {
                        if (action != 2) {
                            if (action != 3) {
                                return false;
                            }
                        }
                        else {
                            final float quickStepTouchSlopPx = QuickStepContract.getQuickStepTouchSlopPx(this.getContext());
                            if (Math.abs(motionEvent.getRawX() - this.mTouchDownX) > quickStepTouchSlopPx || Math.abs(motionEvent.getRawY() - this.mTouchDownY) > quickStepTouchSlopPx) {
                                this.abortCurrentGesture();
                                return false;
                            }
                            return false;
                        }
                    }
                    if (this.mDiamondAnimationDelayed) {
                        if (this.mIsPressed) {
                            this.postDelayed(this.mRetract, 200L);
                        }
                    }
                    else {
                        if (this.mAnimationState == 1) {
                            final long elapsedRealtime = SystemClock.elapsedRealtime();
                            final long mStartTime = this.mStartTime;
                            this.removeCallbacks(this.mRetract);
                            this.postDelayed(this.mRetract, 100L - (elapsedRealtime - mStartTime));
                            this.removeCallbacks(this.mDiamondAnimation);
                            this.cancelLongPress();
                            return false;
                        }
                        if (this.mIsPressed) {
                            this.mRetract.run();
                        }
                    }
                    this.mIsPressed = false;
                }
                else {
                    this.mTouchDownX = (int)motionEvent.getRawX();
                    this.mTouchDownY = (int)motionEvent.getRawY();
                    boolean b;
                    if (!this.mCurrentAnimators.isEmpty()) {
                        if (this.mAnimationState != 2) {
                            return false;
                        }
                        this.endCurrentAnimation();
                        b = true;
                    }
                    else {
                        b = false;
                    }
                    this.mStartTime = SystemClock.elapsedRealtime();
                    this.mIsPressed = true;
                    this.removeCallbacks(this.mDiamondAnimation);
                    this.removeCallbacks(this.mRetract);
                    if (this.mDelayTouchFeedback && !b) {
                        this.mDiamondAnimationDelayed = true;
                        this.postDelayed(this.mDiamondAnimation, (long)ViewConfiguration.getTapTimeout());
                    }
                    else {
                        this.mDiamondAnimationDelayed = false;
                        this.startDiamondAnimation();
                    }
                }
            }
        }
        return false;
    }
    
    public void onProgress(final float n, final int n2) {
        if (this.mGestureState != 2) {
            if (this.allowAnimations()) {
                if (this.mAnimationState == 2) {
                    this.endCurrentAnimation();
                }
                if (this.mAnimationState != 0) {
                    return;
                }
                if (this.mGestureAnimatorSet == null) {
                    final AnimatorSet gestureAnimatorSet = this.getGestureAnimatorSet();
                    this.mGestureAnimatorSet = gestureAnimatorSet;
                    this.mGestureAnimationSetDuration = gestureAnimatorSet.getTotalDuration();
                }
                this.mGestureAnimatorSet.setCurrentPlayTime((long)((this.mGestureAnimationSetDuration - 1L) * n));
                if (n == 0.0f) {
                    this.mGestureState = 0;
                }
                else {
                    this.mGestureState = 1;
                }
            }
        }
    }
    
    public void onRelease() {
        if (this.mAnimationState != 0) {
            return;
        }
        if (this.mGestureState == 1) {
            final AnimatorSet mGestureAnimatorSet = this.mGestureAnimatorSet;
            if (mGestureAnimatorSet != null) {
                mGestureAnimatorSet.cancel();
            }
            this.mGestureState = 0;
            this.startRetractAnimation();
        }
    }
    
    public void onResolve(final GestureSensor.DetectionProperties detectionProperties) {
        if (this.mAnimationState != 0) {
            return;
        }
        if (this.mGestureState == 1) {
            final AnimatorSet mGestureAnimatorSet = this.mGestureAnimatorSet;
            if (mGestureAnimatorSet != null && !mGestureAnimatorSet.isStarted()) {
                this.mGestureAnimatorSet.start();
                this.mGestureState = 2;
                return;
            }
        }
        this.skipToStartingValue();
    }
    
    public void onWindowVisibilityChanged(final int n) {
        super.onWindowVisibilityChanged(n);
        this.mWindowVisible = (n == 0);
        if (n == 0) {
            this.updateOpaLayout();
        }
        else {
            this.cancelCurrentAnimation();
            this.skipToStartingValue();
        }
    }
    
    public void setAccessibilityDelegate(final View$AccessibilityDelegate view$AccessibilityDelegate) {
        super.setAccessibilityDelegate(view$AccessibilityDelegate);
        this.mHome.setAccessibilityDelegate(view$AccessibilityDelegate);
    }
    
    public void setDarkIntensity(final float darkIntensity) {
        if (this.mWhite.getDrawable() instanceof KeyButtonDrawable) {
            ((KeyButtonDrawable)this.mWhite.getDrawable()).setDarkIntensity(darkIntensity);
        }
        ((KeyButtonDrawable)this.mHalo.getDrawable()).setDarkIntensity(darkIntensity);
        this.mWhite.invalidate();
        this.mHalo.invalidate();
        this.mHome.setDarkIntensity(darkIntensity);
    }
    
    public void setDelayTouchFeedback(final boolean b) {
        this.mHome.setDelayTouchFeedback(b);
        this.mDelayTouchFeedback = b;
    }
    
    public void setImageDrawable(final Drawable drawable) {
        this.mWhite.setImageDrawable(drawable);
        this.mWhiteCutout.setImageDrawable(drawable);
    }
    
    public void setOnLongClickListener(final View$OnLongClickListener view$OnLongClickListener) {
        this.mHome.setOnLongClickListener((View$OnLongClickListener)new _$$Lambda$OpaLayout$Z3ewuWnWUI4_KJINicZNFqRIG8M(this, view$OnLongClickListener));
    }
    
    public void setOnTouchListener(final View$OnTouchListener onTouchListener) {
        this.mHome.setOnTouchListener(onTouchListener);
    }
    
    public void setOpaEnabled(final boolean b) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Setting opa enabled to ");
        sb.append(b);
        Log.i("OpaLayout", sb.toString());
        this.mOpaEnabled = b;
        this.mOpaEnabledNeedsUpdate = false;
        this.updateOpaLayout();
    }
    
    public void setVertical(final boolean b) {
        if (this.mIsVertical != b) {
            final AnimatorSet mGestureAnimatorSet = this.mGestureAnimatorSet;
            if (mGestureAnimatorSet != null) {
                mGestureAnimatorSet.cancel();
                this.mGestureAnimatorSet = null;
                this.skipToStartingValue();
            }
        }
        this.mIsVertical = b;
        this.mHome.setVertical(b);
        if (this.mIsVertical) {
            this.mTop = this.mGreen;
            this.mBottom = this.mBlue;
            this.mRight = this.mYellow;
            this.mLeft = this.mRed;
        }
        else {
            this.mTop = this.mRed;
            this.mBottom = this.mYellow;
            this.mLeft = this.mBlue;
            this.mRight = this.mGreen;
        }
    }
    
    public void updateOpaLayout() {
        final boolean shouldShowSwipeUpUI = this.mOverviewProxyService.shouldShowSwipeUpUI();
        final boolean mOpaEnabled = this.mOpaEnabled;
        final int n = 1;
        final boolean b = mOpaEnabled && !shouldShowSwipeUpUI;
        final ImageView mHalo = this.mHalo;
        int visibility;
        if (b) {
            visibility = 0;
        }
        else {
            visibility = 4;
        }
        mHalo.setVisibility(visibility);
        final FrameLayout$LayoutParams frameLayout$LayoutParams = (FrameLayout$LayoutParams)this.mHalo.getLayoutParams();
        int n2;
        if (!b && !shouldShowSwipeUpUI) {
            n2 = n;
        }
        else {
            n2 = 0;
        }
        int mHomeDiameter;
        if (n2 != 0) {
            mHomeDiameter = this.mHomeDiameter;
        }
        else {
            mHomeDiameter = -1;
        }
        frameLayout$LayoutParams.width = mHomeDiameter;
        frameLayout$LayoutParams.height = mHomeDiameter;
        this.mWhite.setLayoutParams((ViewGroup$LayoutParams)frameLayout$LayoutParams);
        this.mWhiteCutout.setLayoutParams((ViewGroup$LayoutParams)frameLayout$LayoutParams);
        ImageView$ScaleType imageView$ScaleType;
        if (n2 != 0) {
            imageView$ScaleType = ImageView$ScaleType.FIT_CENTER;
        }
        else {
            imageView$ScaleType = ImageView$ScaleType.CENTER;
        }
        this.mWhite.setScaleType(imageView$ScaleType);
        this.mWhiteCutout.setScaleType(imageView$ScaleType);
    }
}
