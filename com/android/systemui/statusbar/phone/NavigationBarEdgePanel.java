// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.ViewGroup$LayoutParams;
import android.graphics.Canvas;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;
import com.android.settingslib.Utils;
import com.android.systemui.R$attr;
import androidx.core.graphics.ColorUtils;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.util.MathUtils;
import com.android.systemui.R$dimen;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.graphics.Paint$Join;
import android.graphics.Paint$Style;
import android.graphics.Paint$Cap;
import com.android.systemui.Dependency;
import android.content.Context;
import android.view.animation.PathInterpolator;
import android.view.WindowManager;
import com.android.systemui.statusbar.VibratorHelper;
import android.view.VelocityTracker;
import androidx.dynamicanimation.animation.DynamicAnimation;
import android.graphics.Rect;
import android.graphics.Paint;
import android.view.WindowManager$LayoutParams;
import android.graphics.Point;
import android.graphics.Path;
import android.animation.ValueAnimator;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.dynamicanimation.animation.SpringAnimation;
import android.view.animation.Interpolator;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import com.android.systemui.plugins.NavigationEdgeBackPlugin;
import android.view.View;

public class NavigationBarEdgePanel extends View implements NavigationEdgeBackPlugin
{
    private static final FloatPropertyCompat<NavigationBarEdgePanel> CURRENT_ANGLE;
    private static final FloatPropertyCompat<NavigationBarEdgePanel> CURRENT_TRANSLATION;
    private static final FloatPropertyCompat<NavigationBarEdgePanel> CURRENT_VERTICAL_TRANSLATION;
    private static final Interpolator RUBBER_BAND_INTERPOLATOR;
    private static final Interpolator RUBBER_BAND_INTERPOLATOR_APPEAR;
    private final SpringAnimation mAngleAnimation;
    private final SpringForce mAngleAppearForce;
    private final SpringForce mAngleDisappearForce;
    private float mAngleOffset;
    private int mArrowColor;
    private final ValueAnimator mArrowColorAnimator;
    private int mArrowColorDark;
    private int mArrowColorLight;
    private final ValueAnimator mArrowDisappearAnimation;
    private final float mArrowLength;
    private int mArrowPaddingEnd;
    private final Path mArrowPath;
    private int mArrowStartColor;
    private final float mArrowThickness;
    private boolean mArrowsPointLeft;
    private BackCallback mBackCallback;
    private final float mBaseTranslation;
    private float mCurrentAngle;
    private int mCurrentArrowColor;
    private float mCurrentTranslation;
    private final float mDensity;
    private float mDesiredAngle;
    private float mDesiredTranslation;
    private float mDesiredVerticalTranslation;
    private float mDisappearAmount;
    private final Point mDisplaySize;
    private boolean mDragSlopPassed;
    private int mFingerOffset;
    private boolean mIsDark;
    private boolean mIsLeftPanel;
    private WindowManager$LayoutParams mLayoutParams;
    private int mLeftInset;
    private float mMaxTranslation;
    private int mMinArrowPosition;
    private final float mMinDeltaForSwitch;
    private final Paint mPaint;
    private float mPreviousTouchTranslation;
    private int mProtectionColor;
    private int mProtectionColorDark;
    private int mProtectionColorLight;
    private final Paint mProtectionPaint;
    private RegionSamplingHelper mRegionSamplingHelper;
    private final SpringForce mRegularTranslationSpring;
    private int mRightInset;
    private final Rect mSamplingRect;
    private int mScreenSize;
    private DynamicAnimation.OnAnimationEndListener mSetGoneEndListener;
    private boolean mShowProtection;
    private float mStartX;
    private float mStartY;
    private final float mSwipeThreshold;
    private float mTotalTouchDelta;
    private final SpringAnimation mTranslationAnimation;
    private boolean mTriggerBack;
    private final SpringForce mTriggerBackSpring;
    private VelocityTracker mVelocityTracker;
    private float mVerticalTranslation;
    private final SpringAnimation mVerticalTranslationAnimation;
    private long mVibrationTime;
    private final VibratorHelper mVibratorHelper;
    private final WindowManager mWindowManager;
    
    static {
        RUBBER_BAND_INTERPOLATOR = (Interpolator)new PathInterpolator(0.2f, 1.0f, 1.0f, 1.0f);
        RUBBER_BAND_INTERPOLATOR_APPEAR = (Interpolator)new PathInterpolator(0.25f, 1.0f, 1.0f, 1.0f);
        CURRENT_ANGLE = new FloatPropertyCompat<NavigationBarEdgePanel>() {
            @Override
            public float getValue(final NavigationBarEdgePanel navigationBarEdgePanel) {
                return navigationBarEdgePanel.getCurrentAngle();
            }
            
            @Override
            public void setValue(final NavigationBarEdgePanel navigationBarEdgePanel, final float n) {
                navigationBarEdgePanel.setCurrentAngle(n);
            }
        };
        CURRENT_TRANSLATION = new FloatPropertyCompat<NavigationBarEdgePanel>() {
            @Override
            public float getValue(final NavigationBarEdgePanel navigationBarEdgePanel) {
                return navigationBarEdgePanel.getCurrentTranslation();
            }
            
            @Override
            public void setValue(final NavigationBarEdgePanel navigationBarEdgePanel, final float n) {
                navigationBarEdgePanel.setCurrentTranslation(n);
            }
        };
        CURRENT_VERTICAL_TRANSLATION = new FloatPropertyCompat<NavigationBarEdgePanel>() {
            @Override
            public float getValue(final NavigationBarEdgePanel navigationBarEdgePanel) {
                return navigationBarEdgePanel.getVerticalTranslation();
            }
            
            @Override
            public void setValue(final NavigationBarEdgePanel navigationBarEdgePanel, final float n) {
                navigationBarEdgePanel.setVerticalTranslation(n);
            }
        };
    }
    
    public NavigationBarEdgePanel(final Context context) {
        super(context);
        this.mPaint = new Paint();
        this.mArrowPath = new Path();
        this.mDisplaySize = new Point();
        this.mIsDark = false;
        this.mShowProtection = false;
        this.mSamplingRect = new Rect();
        this.mSetGoneEndListener = new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(final DynamicAnimation dynamicAnimation, final boolean b, final float n, final float n2) {
                dynamicAnimation.removeEndListener((DynamicAnimation.OnAnimationEndListener)this);
                if (!b) {
                    NavigationBarEdgePanel.this.setVisibility(8);
                }
            }
        };
        this.mWindowManager = (WindowManager)context.getSystemService((Class)WindowManager.class);
        this.mVibratorHelper = Dependency.get(VibratorHelper.class);
        this.mDensity = context.getResources().getDisplayMetrics().density;
        this.mBaseTranslation = this.dp(32.0f);
        this.mArrowLength = this.dp(18.0f);
        this.mArrowThickness = this.dp(2.5f);
        this.mMinDeltaForSwitch = this.dp(32.0f);
        this.mPaint.setStrokeWidth(this.mArrowThickness);
        this.mPaint.setStrokeCap(Paint$Cap.ROUND);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint$Style.STROKE);
        this.mPaint.setStrokeJoin(Paint$Join.ROUND);
        (this.mArrowColorAnimator = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f })).setDuration(120L);
        this.mArrowColorAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$NavigationBarEdgePanel$bOecFcR5bBF6RggHYoy3PBO7S7o(this));
        (this.mArrowDisappearAnimation = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f })).setDuration(100L);
        this.mArrowDisappearAnimation.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        this.mArrowDisappearAnimation.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$NavigationBarEdgePanel$dGzlaDOJBgEiptNKFEADYL6K5TY(this));
        this.mAngleAnimation = new SpringAnimation((K)this, (FloatPropertyCompat<K>)NavigationBarEdgePanel.CURRENT_ANGLE);
        final SpringForce mAngleAppearForce = new SpringForce();
        mAngleAppearForce.setStiffness(500.0f);
        mAngleAppearForce.setDampingRatio(0.5f);
        this.mAngleAppearForce = mAngleAppearForce;
        final SpringForce mAngleDisappearForce = new SpringForce();
        mAngleDisappearForce.setStiffness(1500.0f);
        mAngleDisappearForce.setDampingRatio(0.5f);
        mAngleDisappearForce.setFinalPosition(90.0f);
        this.mAngleDisappearForce = mAngleDisappearForce;
        final SpringAnimation mAngleAnimation = this.mAngleAnimation;
        mAngleAnimation.setSpring(this.mAngleAppearForce);
        mAngleAnimation.setMaxValue(90.0f);
        this.mTranslationAnimation = new SpringAnimation((K)this, (FloatPropertyCompat<K>)NavigationBarEdgePanel.CURRENT_TRANSLATION);
        final SpringForce mRegularTranslationSpring = new SpringForce();
        mRegularTranslationSpring.setStiffness(1500.0f);
        mRegularTranslationSpring.setDampingRatio(0.75f);
        this.mRegularTranslationSpring = mRegularTranslationSpring;
        final SpringForce mTriggerBackSpring = new SpringForce();
        mTriggerBackSpring.setStiffness(450.0f);
        mTriggerBackSpring.setDampingRatio(0.75f);
        this.mTriggerBackSpring = mTriggerBackSpring;
        this.mTranslationAnimation.setSpring(this.mRegularTranslationSpring);
        final SpringAnimation mVerticalTranslationAnimation = new SpringAnimation((K)this, (FloatPropertyCompat<K>)NavigationBarEdgePanel.CURRENT_VERTICAL_TRANSLATION);
        this.mVerticalTranslationAnimation = mVerticalTranslationAnimation;
        final SpringForce spring = new SpringForce();
        spring.setStiffness(1500.0f);
        spring.setDampingRatio(0.75f);
        mVerticalTranslationAnimation.setSpring(spring);
        (this.mProtectionPaint = new Paint(this.mPaint)).setStrokeWidth(this.mArrowThickness + 2.0f);
        this.loadDimens();
        this.loadColors(context);
        this.updateArrowDirection();
        this.mSwipeThreshold = context.getResources().getDimension(R$dimen.navigation_edge_action_drag_threshold);
        this.setVisibility(8);
        (this.mRegionSamplingHelper = new RegionSamplingHelper(this, (RegionSamplingHelper.SamplingCallback)new RegionSamplingHelper.SamplingCallback() {
            @Override
            public Rect getSampledRegion(final View view) {
                return NavigationBarEdgePanel.this.mSamplingRect;
            }
            
            @Override
            public void onRegionDarknessChanged(final boolean b) {
                NavigationBarEdgePanel.this.setIsDark(b ^ true, true);
            }
        })).setWindowVisible(true);
    }
    
    private void adjustSamplingRectToBoundingBox() {
        float mDesiredTranslation = this.mDesiredTranslation;
        Label_0056: {
            if (!this.mTriggerBack) {
                final float mBaseTranslation = this.mBaseTranslation;
                if (!this.mIsLeftPanel || !this.mArrowsPointLeft) {
                    mDesiredTranslation = mBaseTranslation;
                    if (this.mIsLeftPanel) {
                        break Label_0056;
                    }
                    mDesiredTranslation = mBaseTranslation;
                    if (this.mArrowsPointLeft) {
                        break Label_0056;
                    }
                }
                mDesiredTranslation = mBaseTranslation - this.getStaticArrowWidth();
            }
        }
        float n = mDesiredTranslation - this.mArrowThickness / 2.0f;
        if (!this.mIsLeftPanel) {
            n = this.mSamplingRect.width() - n;
        }
        final float staticArrowWidth = this.getStaticArrowWidth();
        final float n2 = this.polarToCartY(56.0f) * this.mArrowLength * 2.0f;
        float n3 = n;
        if (!this.mArrowsPointLeft) {
            n3 = n - staticArrowWidth;
        }
        this.mSamplingRect.offset((int)n3, (int)(this.getHeight() * 0.5f + this.mDesiredVerticalTranslation - n2 / 2.0f));
        final Rect mSamplingRect = this.mSamplingRect;
        final int left = mSamplingRect.left;
        final int top = mSamplingRect.top;
        mSamplingRect.set(left, top, (int)(left + staticArrowWidth), (int)(top + n2));
        this.mRegionSamplingHelper.updateSamplingRect();
    }
    
    private Path calculatePath(float n, float n2) {
        float n3 = n;
        if (!this.mArrowsPointLeft) {
            n3 = -n;
        }
        final float lerp = MathUtils.lerp(1.0f, 0.75f, this.mDisappearAmount);
        n = n3 * lerp;
        n2 *= lerp;
        this.mArrowPath.reset();
        this.mArrowPath.moveTo(n, n2);
        this.mArrowPath.lineTo(0.0f, 0.0f);
        this.mArrowPath.lineTo(n, -n2);
        return this.mArrowPath;
    }
    
    private void cancelBack() {
        this.mBackCallback.cancelBack();
        if (this.mTranslationAnimation.isRunning()) {
            this.mTranslationAnimation.addEndListener(this.mSetGoneEndListener);
        }
        else {
            this.setVisibility(8);
        }
    }
    
    private float dp(final float n) {
        return this.mDensity * n;
    }
    
    private float getCurrentAngle() {
        return this.mCurrentAngle;
    }
    
    private float getCurrentTranslation() {
        return this.mCurrentTranslation;
    }
    
    private float getStaticArrowWidth() {
        return this.polarToCartX(56.0f) * this.mArrowLength;
    }
    
    private float getVerticalTranslation() {
        return this.mVerticalTranslation;
    }
    
    private void handleMoveEvent(final MotionEvent motionEvent) {
        final float x = motionEvent.getX();
        final float y = motionEvent.getY();
        final float abs = MathUtils.abs(x - this.mStartX);
        final float f = y - this.mStartY;
        final float mTotalTouchDelta = abs - this.mPreviousTouchTranslation;
        if (Math.abs(mTotalTouchDelta) > 0.0f) {
            if (Math.signum(mTotalTouchDelta) == Math.signum(this.mTotalTouchDelta)) {
                this.mTotalTouchDelta += mTotalTouchDelta;
            }
            else {
                this.mTotalTouchDelta = mTotalTouchDelta;
            }
        }
        this.mPreviousTouchTranslation = abs;
        if (!this.mDragSlopPassed && abs > this.mSwipeThreshold) {
            this.mDragSlopPassed = true;
            this.mVibratorHelper.vibrate(2);
            this.mVibrationTime = SystemClock.uptimeMillis();
            this.mDisappearAmount = 0.0f;
            this.setAlpha(1.0f);
            this.setTriggerBack(true, true);
        }
        final float mBaseTranslation = this.mBaseTranslation;
        float n;
        if (abs > mBaseTranslation) {
            final float interpolation = NavigationBarEdgePanel.RUBBER_BAND_INTERPOLATOR.getInterpolation(MathUtils.saturate((abs - mBaseTranslation) / (this.mScreenSize - mBaseTranslation)));
            final float mMaxTranslation = this.mMaxTranslation;
            final float mBaseTranslation2 = this.mBaseTranslation;
            n = mBaseTranslation2 + interpolation * (mMaxTranslation - mBaseTranslation2);
        }
        else {
            final float interpolation2 = NavigationBarEdgePanel.RUBBER_BAND_INTERPOLATOR_APPEAR.getInterpolation(MathUtils.saturate((mBaseTranslation - abs) / mBaseTranslation));
            final float mBaseTranslation3 = this.mBaseTranslation;
            n = mBaseTranslation3 - interpolation2 * (mBaseTranslation3 / 4.0f);
        }
        boolean mTriggerBack = this.mTriggerBack;
        final float abs2 = Math.abs(this.mTotalTouchDelta);
        final float mMinDeltaForSwitch = this.mMinDeltaForSwitch;
        final boolean b = false;
        if (abs2 > mMinDeltaForSwitch) {
            mTriggerBack = (this.mTotalTouchDelta > 0.0f);
        }
        this.mVelocityTracker.computeCurrentVelocity(1000);
        final float xVelocity = this.mVelocityTracker.getXVelocity();
        this.mAngleOffset = Math.min(MathUtils.mag(xVelocity, this.mVelocityTracker.getYVelocity()) / 1000.0f * 4.0f, 4.0f) * Math.signum(xVelocity);
        if ((this.mIsLeftPanel && this.mArrowsPointLeft) || (!this.mIsLeftPanel && !this.mArrowsPointLeft)) {
            this.mAngleOffset *= -1.0f;
        }
        if (Math.abs(f) > Math.abs(x - this.mStartX) * 2.0f) {
            mTriggerBack = b;
        }
        this.setTriggerBack(mTriggerBack, true);
        float n2 = 0.0f;
        Label_0492: {
            if (!this.mTriggerBack) {
                n2 = 0.0f;
            }
            else {
                if (!this.mIsLeftPanel || !this.mArrowsPointLeft) {
                    n2 = n;
                    if (this.mIsLeftPanel) {
                        break Label_0492;
                    }
                    n2 = n;
                    if (this.mArrowsPointLeft) {
                        break Label_0492;
                    }
                }
                n2 = n - this.getStaticArrowWidth();
            }
        }
        this.setDesiredTranslation(n2, true);
        this.updateAngle(true);
        final float n3 = this.getHeight() / 2.0f - this.mArrowLength;
        this.setDesiredVerticalTransition(NavigationBarEdgePanel.RUBBER_BAND_INTERPOLATOR.getInterpolation(MathUtils.constrain(Math.abs(f) / (15.0f * n3), 0.0f, 1.0f)) * n3 * Math.signum(f), true);
        this.updateSamplingRect();
    }
    
    private void loadColors(final Context context) {
        final int themeAttr = Utils.getThemeAttr(context, R$attr.darkIconTheme);
        final ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, Utils.getThemeAttr(context, R$attr.lightIconTheme));
        final ContextThemeWrapper contextThemeWrapper2 = new ContextThemeWrapper(context, themeAttr);
        this.mArrowColorLight = Utils.getColorAttrDefaultColor((Context)contextThemeWrapper, R$attr.singleToneColor);
        final int colorAttrDefaultColor = Utils.getColorAttrDefaultColor((Context)contextThemeWrapper2, R$attr.singleToneColor);
        this.mArrowColorDark = colorAttrDefaultColor;
        this.mProtectionColorDark = this.mArrowColorLight;
        this.mProtectionColorLight = colorAttrDefaultColor;
        this.updateIsDark(false);
    }
    
    private void loadDimens() {
        final Resources resources = this.getResources();
        this.mArrowPaddingEnd = resources.getDimensionPixelSize(R$dimen.navigation_edge_panel_padding);
        this.mMinArrowPosition = resources.getDimensionPixelSize(R$dimen.navigation_edge_arrow_min_y);
        this.mFingerOffset = resources.getDimensionPixelSize(R$dimen.navigation_edge_finger_offset);
    }
    
    private float polarToCartX(final float n) {
        return (float)Math.cos(Math.toRadians(n));
    }
    
    private float polarToCartY(final float n) {
        return (float)Math.sin(Math.toRadians(n));
    }
    
    private void resetOnDown() {
        this.animate().cancel();
        this.mAngleAnimation.cancel();
        this.mTranslationAnimation.cancel();
        this.mVerticalTranslationAnimation.cancel();
        this.mArrowDisappearAnimation.cancel();
        this.mAngleOffset = 0.0f;
        this.mTranslationAnimation.setSpring(this.mRegularTranslationSpring);
        this.setTriggerBack(false, false);
        this.setDesiredTranslation(0.0f, false);
        this.setCurrentTranslation(0.0f);
        this.updateAngle(false);
        this.mPreviousTouchTranslation = 0.0f;
        this.mTotalTouchDelta = 0.0f;
        this.mVibrationTime = 0L;
        this.setDesiredVerticalTransition(0.0f, false);
    }
    
    private void setCurrentAngle(final float mCurrentAngle) {
        this.mCurrentAngle = mCurrentAngle;
        this.invalidate();
    }
    
    private void setCurrentArrowColor(final int n) {
        this.mCurrentArrowColor = n;
        this.mPaint.setColor(n);
        this.invalidate();
    }
    
    private void setCurrentTranslation(final float mCurrentTranslation) {
        this.mCurrentTranslation = mCurrentTranslation;
        this.invalidate();
    }
    
    private void setDesiredTranslation(final float n, final boolean b) {
        if (this.mDesiredTranslation != n) {
            this.mDesiredTranslation = n;
            if (!b) {
                this.setCurrentTranslation(n);
            }
            else {
                this.mTranslationAnimation.animateToFinalPosition(n);
            }
        }
    }
    
    private void setDesiredVerticalTransition(final float n, final boolean b) {
        if (this.mDesiredVerticalTranslation != n) {
            this.mDesiredVerticalTranslation = n;
            if (!b) {
                this.setVerticalTranslation(n);
            }
            else {
                this.mVerticalTranslationAnimation.animateToFinalPosition(n);
            }
            this.invalidate();
        }
    }
    
    private void setIsDark(final boolean mIsDark, final boolean b) {
        this.mIsDark = mIsDark;
        this.updateIsDark(b);
    }
    
    private void setTriggerBack(final boolean mTriggerBack, final boolean b) {
        if (this.mTriggerBack != mTriggerBack) {
            this.mTriggerBack = mTriggerBack;
            this.mAngleAnimation.cancel();
            this.updateAngle(b);
            this.mTranslationAnimation.cancel();
        }
    }
    
    private void setVerticalTranslation(final float mVerticalTranslation) {
        this.mVerticalTranslation = mVerticalTranslation;
        this.invalidate();
    }
    
    private void triggerBack() {
        this.mBackCallback.triggerBack();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.computeCurrentVelocity(1000);
        if (Math.abs(this.mVelocityTracker.getXVelocity()) < 500.0f || SystemClock.uptimeMillis() - this.mVibrationTime >= 400L) {
            this.mVibratorHelper.vibrate(0);
        }
        final float mAngleOffset = this.mAngleOffset;
        if (mAngleOffset > -4.0f) {
            this.mAngleOffset = Math.max(-8.0f, mAngleOffset - 8.0f);
            this.updateAngle(true);
        }
        final _$$Lambda$NavigationBarEdgePanel$8nVYqEm2UMLvGUzkKr5IU4GQaTc $$Lambda$NavigationBarEdgePanel$8nVYqEm2UMLvGUzkKr5IU4GQaTc = new _$$Lambda$NavigationBarEdgePanel$8nVYqEm2UMLvGUzkKr5IU4GQaTc(this);
        if (this.mTranslationAnimation.isRunning()) {
            this.mTranslationAnimation.addEndListener((DynamicAnimation.OnAnimationEndListener)new DynamicAnimation.OnAnimationEndListener(this) {
                @Override
                public void onAnimationEnd(final DynamicAnimation dynamicAnimation, final boolean b, final float n, final float n2) {
                    dynamicAnimation.removeEndListener((DynamicAnimation.OnAnimationEndListener)this);
                    if (!b) {
                        $$Lambda$NavigationBarEdgePanel$8nVYqEm2UMLvGUzkKr5IU4GQaTc.run();
                    }
                }
            });
        }
        else {
            $$Lambda$NavigationBarEdgePanel$8nVYqEm2UMLvGUzkKr5IU4GQaTc.run();
        }
    }
    
    private void updateAngle(final boolean b) {
        float n;
        if (this.mTriggerBack) {
            n = this.mAngleOffset + 56.0f;
        }
        else {
            n = 90.0f;
        }
        if (n != this.mDesiredAngle) {
            if (!b) {
                this.setCurrentAngle(n);
            }
            else {
                final SpringAnimation mAngleAnimation = this.mAngleAnimation;
                SpringForce spring;
                if (this.mTriggerBack) {
                    spring = this.mAngleAppearForce;
                }
                else {
                    spring = this.mAngleDisappearForce;
                }
                mAngleAnimation.setSpring(spring);
                this.mAngleAnimation.animateToFinalPosition(n);
            }
            this.mDesiredAngle = n;
        }
    }
    
    private void updateArrowDirection() {
        this.mArrowsPointLeft = (this.getLayoutDirection() == 0);
        this.invalidate();
    }
    
    private void updateIsDark(final boolean b) {
        int n;
        if (this.mIsDark) {
            n = this.mProtectionColorDark;
        }
        else {
            n = this.mProtectionColorLight;
        }
        this.mProtectionColor = n;
        this.mProtectionPaint.setColor(n);
        int mArrowColor;
        if (this.mIsDark) {
            mArrowColor = this.mArrowColorDark;
        }
        else {
            mArrowColor = this.mArrowColorLight;
        }
        this.mArrowColor = mArrowColor;
        this.mArrowColorAnimator.cancel();
        if (!b) {
            this.setCurrentArrowColor(this.mArrowColor);
        }
        else {
            this.mArrowStartColor = this.mCurrentArrowColor;
            this.mArrowColorAnimator.start();
        }
    }
    
    private void updatePosition(float max) {
        max = Math.max(max - this.mFingerOffset, (float)this.mMinArrowPosition);
        final WindowManager$LayoutParams mLayoutParams = this.mLayoutParams;
        mLayoutParams.y = MathUtils.constrain((int)(max - mLayoutParams.height / 2.0f), 0, this.mDisplaySize.y);
        this.updateSamplingRect();
    }
    
    private void updateSamplingRect() {
        final WindowManager$LayoutParams mLayoutParams = this.mLayoutParams;
        final int y = mLayoutParams.y;
        int mLeftInset;
        if (this.mIsLeftPanel) {
            mLeftInset = this.mLeftInset;
        }
        else {
            mLeftInset = this.mDisplaySize.x - this.mRightInset - mLayoutParams.width;
        }
        final WindowManager$LayoutParams mLayoutParams2 = this.mLayoutParams;
        this.mSamplingRect.set(mLeftInset, y, mLayoutParams2.width + mLeftInset, mLayoutParams2.height + y);
        this.adjustSamplingRectToBoundingBox();
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.updateArrowDirection();
        this.loadDimens();
    }
    
    public void onDestroy() {
        this.mWindowManager.removeView((View)this);
        this.mRegionSamplingHelper.stop();
        this.mRegionSamplingHelper = null;
    }
    
    protected void onDraw(final Canvas canvas) {
        float n = this.mCurrentTranslation - this.mArrowThickness / 2.0f;
        canvas.save();
        if (!this.mIsLeftPanel) {
            n = this.getWidth() - n;
        }
        canvas.translate(n, this.getHeight() * 0.5f + this.mVerticalTranslation);
        final Path calculatePath = this.calculatePath(this.polarToCartX(this.mCurrentAngle) * this.mArrowLength, this.polarToCartY(this.mCurrentAngle) * this.mArrowLength);
        if (this.mShowProtection) {
            canvas.drawPath(calculatePath, this.mProtectionPaint);
        }
        canvas.drawPath(calculatePath, this.mPaint);
        canvas.restore();
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.mMaxTranslation = (float)(this.getWidth() - this.mArrowPaddingEnd);
    }
    
    public void onMotionEvent(final MotionEvent motionEvent) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked == 3) {
                        this.cancelBack();
                        this.mRegionSamplingHelper.stop();
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                    }
                }
                else {
                    this.handleMoveEvent(motionEvent);
                }
            }
            else {
                if (this.mTriggerBack) {
                    this.triggerBack();
                }
                else {
                    this.cancelBack();
                }
                this.mRegionSamplingHelper.stop();
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            }
        }
        else {
            this.mDragSlopPassed = false;
            this.resetOnDown();
            this.mStartX = motionEvent.getX();
            this.mStartY = motionEvent.getY();
            this.setVisibility(0);
            this.updatePosition(motionEvent.getY());
            this.mRegionSamplingHelper.start(this.mSamplingRect);
            this.mWindowManager.updateViewLayout((View)this, (ViewGroup$LayoutParams)this.mLayoutParams);
        }
    }
    
    public void setBackCallback(final BackCallback mBackCallback) {
        this.mBackCallback = mBackCallback;
    }
    
    public void setDisplaySize(Point mDisplaySize) {
        this.mDisplaySize.set(mDisplaySize.x, mDisplaySize.y);
        mDisplaySize = this.mDisplaySize;
        this.mScreenSize = Math.min(mDisplaySize.x, mDisplaySize.y);
    }
    
    public void setInsets(final int mLeftInset, final int mRightInset) {
        this.mLeftInset = mLeftInset;
        this.mRightInset = mRightInset;
    }
    
    public void setIsLeftPanel(final boolean mIsLeftPanel) {
        this.mIsLeftPanel = mIsLeftPanel;
        final WindowManager$LayoutParams mLayoutParams = this.mLayoutParams;
        int gravity;
        if (mIsLeftPanel) {
            gravity = 51;
        }
        else {
            gravity = 53;
        }
        mLayoutParams.gravity = gravity;
    }
    
    public void setLayoutParams(final WindowManager$LayoutParams mLayoutParams) {
        this.mLayoutParams = mLayoutParams;
        this.mWindowManager.addView((View)this, (ViewGroup$LayoutParams)mLayoutParams);
    }
}
