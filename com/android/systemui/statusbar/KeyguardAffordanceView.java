// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.graphics.Rect;
import android.graphics.PorterDuff$Mode;
import android.graphics.Color;
import android.view.animation.Interpolator;
import android.view.ViewAnimationUtils;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.graphics.drawable.Drawable;
import android.view.RenderNodeAnimator;
import android.animation.Animator$AnimatorListener;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.graphics.RecordingCanvas;
import android.graphics.Canvas;
import android.content.res.TypedArray;
import com.android.systemui.R$dimen;
import android.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.animation.Animator;
import android.graphics.CanvasProperty;
import android.animation.ArgbEvaluator;
import android.graphics.Paint;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.widget.ImageView;

public class KeyguardAffordanceView extends ImageView
{
    private ValueAnimator mAlphaAnimator;
    private AnimatorListenerAdapter mAlphaEndListener;
    private int mCenterX;
    private int mCenterY;
    private ValueAnimator mCircleAnimator;
    private int mCircleColor;
    private AnimatorListenerAdapter mCircleEndListener;
    private final Paint mCirclePaint;
    private float mCircleRadius;
    private float mCircleStartRadius;
    private float mCircleStartValue;
    private boolean mCircleWillBeHidden;
    private AnimatorListenerAdapter mClipEndListener;
    private final ArgbEvaluator mColorInterpolator;
    protected final int mDarkIconColor;
    private boolean mFinishing;
    private final FlingAnimationUtils mFlingAnimationUtils;
    private CanvasProperty<Float> mHwCenterX;
    private CanvasProperty<Float> mHwCenterY;
    private CanvasProperty<Paint> mHwCirclePaint;
    private CanvasProperty<Float> mHwCircleRadius;
    private float mImageScale;
    private boolean mLaunchingAffordance;
    private float mMaxCircleSize;
    private final int mMinBackgroundRadius;
    protected final int mNormalColor;
    private Animator mPreviewClipper;
    private View mPreviewView;
    private float mRestingAlpha;
    private ValueAnimator mScaleAnimator;
    private AnimatorListenerAdapter mScaleEndListener;
    private boolean mShouldTint;
    private boolean mSupportHardware;
    private int[] mTempPoint;
    
    public KeyguardAffordanceView(final Context context) {
        this(context, null);
    }
    
    public KeyguardAffordanceView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public KeyguardAffordanceView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public KeyguardAffordanceView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mTempPoint = new int[2];
        this.mImageScale = 1.0f;
        this.mRestingAlpha = 1.0f;
        this.mShouldTint = true;
        this.mClipEndListener = new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                KeyguardAffordanceView.this.mPreviewClipper = null;
            }
        };
        this.mCircleEndListener = new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                KeyguardAffordanceView.this.mCircleAnimator = null;
            }
        };
        this.mScaleEndListener = new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                KeyguardAffordanceView.this.mScaleAnimator = null;
            }
        };
        this.mAlphaEndListener = new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                KeyguardAffordanceView.this.mAlphaAnimator = null;
            }
        };
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.ImageView);
        (this.mCirclePaint = new Paint()).setAntiAlias(true);
        this.mCircleColor = -1;
        this.mCirclePaint.setColor(-1);
        this.mNormalColor = obtainStyledAttributes.getColor(5, -1);
        this.mDarkIconColor = -16777216;
        this.mMinBackgroundRadius = super.mContext.getResources().getDimensionPixelSize(R$dimen.keyguard_affordance_min_background_radius);
        this.mColorInterpolator = new ArgbEvaluator();
        this.mFlingAnimationUtils = new FlingAnimationUtils(super.mContext.getResources().getDisplayMetrics(), 0.3f);
        obtainStyledAttributes.recycle();
    }
    
    private void cancelAnimator(final Animator animator) {
        if (animator != null) {
            animator.cancel();
        }
    }
    
    private void drawBackgroundCircle(final Canvas canvas) {
        if (this.mCircleRadius > 0.0f || this.mFinishing) {
            if (this.mFinishing && this.mSupportHardware) {
                final CanvasProperty<Float> mHwCenterX = this.mHwCenterX;
                if (mHwCenterX != null) {
                    ((RecordingCanvas)canvas).drawCircle((CanvasProperty)mHwCenterX, (CanvasProperty)this.mHwCenterY, (CanvasProperty)this.mHwCircleRadius, (CanvasProperty)this.mHwCirclePaint);
                    return;
                }
            }
            this.updateCircleColor();
            canvas.drawCircle((float)this.mCenterX, (float)this.mCenterY, this.mCircleRadius, this.mCirclePaint);
        }
    }
    
    private ValueAnimator getAnimatorToRadius(final float n) {
        final float mCircleRadius = this.mCircleRadius;
        boolean mCircleWillBeHidden = false;
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { mCircleRadius, n });
        this.mCircleAnimator = ofFloat;
        this.mCircleStartValue = this.mCircleRadius;
        if (n == 0.0f) {
            mCircleWillBeHidden = true;
        }
        this.mCircleWillBeHidden = mCircleWillBeHidden;
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                KeyguardAffordanceView.this.mCircleRadius = (float)valueAnimator.getAnimatedValue();
                KeyguardAffordanceView.this.updateIconColor();
                KeyguardAffordanceView.this.invalidate();
            }
        });
        ofFloat.addListener((Animator$AnimatorListener)this.mCircleEndListener);
        return ofFloat;
    }
    
    private Animator$AnimatorListener getEndListener(final Runnable runnable) {
        return (Animator$AnimatorListener)new AnimatorListenerAdapter(this) {
            boolean mCancelled;
            
            public void onAnimationCancel(final Animator animator) {
                this.mCancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                if (!this.mCancelled) {
                    runnable.run();
                }
            }
        };
    }
    
    private float getMaxCircleSize() {
        this.getLocationInWindow(this.mTempPoint);
        final float n = (float)this.getRootView().getWidth();
        final float b = (float)(this.mTempPoint[0] + this.mCenterX);
        return (float)Math.hypot(Math.max(n - b, b), (float)(this.mTempPoint[1] + this.mCenterY));
    }
    
    private Animator getRtAnimatorToRadius(final float n) {
        final RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator((CanvasProperty)this.mHwCircleRadius, n);
        renderNodeAnimator.setTarget((View)this);
        return (Animator)renderNodeAnimator;
    }
    
    private void initHwProperties() {
        this.mHwCenterX = (CanvasProperty<Float>)CanvasProperty.createFloat((float)this.mCenterX);
        this.mHwCenterY = (CanvasProperty<Float>)CanvasProperty.createFloat((float)this.mCenterY);
        this.mHwCirclePaint = (CanvasProperty<Paint>)CanvasProperty.createPaint(this.mCirclePaint);
        this.mHwCircleRadius = (CanvasProperty<Float>)CanvasProperty.createFloat(this.mCircleRadius);
    }
    
    private void setCircleRadius(final float mCircleRadius, final boolean b, final boolean b2) {
        final boolean b3 = (this.mCircleAnimator != null && this.mCircleWillBeHidden) || (this.mCircleAnimator == null && this.mCircleRadius == 0.0f);
        final float n = fcmpl(mCircleRadius, 0.0f);
        final boolean b4 = n == 0;
        if (b3 == b4 || b2) {
            final ValueAnimator mCircleAnimator = this.mCircleAnimator;
            if (mCircleAnimator == null) {
                this.mCircleRadius = mCircleRadius;
                this.updateIconColor();
                this.invalidate();
                if (b4) {
                    final View mPreviewView = this.mPreviewView;
                    if (mPreviewView != null) {
                        mPreviewView.setVisibility(4);
                    }
                }
            }
            else if (!this.mCircleWillBeHidden) {
                mCircleAnimator.getValues()[0].setFloatValues(new float[] { this.mCircleStartValue + (mCircleRadius - this.mMinBackgroundRadius), mCircleRadius });
                final ValueAnimator mCircleAnimator2 = this.mCircleAnimator;
                mCircleAnimator2.setCurrentPlayTime(mCircleAnimator2.getCurrentPlayTime());
            }
        }
        else {
            this.cancelAnimator((Animator)this.mCircleAnimator);
            this.cancelAnimator(this.mPreviewClipper);
            final ValueAnimator animatorToRadius = this.getAnimatorToRadius(mCircleRadius);
            Interpolator interpolator;
            if (n == 0) {
                interpolator = Interpolators.FAST_OUT_LINEAR_IN;
            }
            else {
                interpolator = Interpolators.LINEAR_OUT_SLOW_IN;
            }
            animatorToRadius.setInterpolator((TimeInterpolator)interpolator);
            long min = 250L;
            if (!b) {
                min = Math.min((long)(Math.abs(this.mCircleRadius - mCircleRadius) / this.mMinBackgroundRadius * 80.0f), 200L);
            }
            animatorToRadius.setDuration(min);
            animatorToRadius.start();
            final View mPreviewView2 = this.mPreviewView;
            if (mPreviewView2 != null && mPreviewView2.getVisibility() == 0) {
                this.mPreviewView.setVisibility(0);
                (this.mPreviewClipper = ViewAnimationUtils.createCircularReveal(this.mPreviewView, this.getLeft() + this.mCenterX, this.getTop() + this.mCenterY, this.mCircleRadius, mCircleRadius)).setInterpolator((TimeInterpolator)interpolator);
                this.mPreviewClipper.setDuration(min);
                this.mPreviewClipper.addListener((Animator$AnimatorListener)this.mClipEndListener);
                this.mPreviewClipper.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                    public void onAnimationEnd(final Animator animator) {
                        KeyguardAffordanceView.this.mPreviewView.setVisibility(4);
                    }
                });
                this.mPreviewClipper.start();
            }
        }
    }
    
    private void startRtAlphaFadeIn() {
        if (this.mCircleRadius == 0.0f && this.mPreviewView == null) {
            final Paint paint = new Paint(this.mCirclePaint);
            paint.setColor(this.mCircleColor);
            paint.setAlpha(0);
            this.mHwCirclePaint = (CanvasProperty<Paint>)CanvasProperty.createPaint(paint);
            final RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator((CanvasProperty)this.mHwCirclePaint, 1, 255.0f);
            renderNodeAnimator.setTarget((View)this);
            renderNodeAnimator.setInterpolator((TimeInterpolator)Interpolators.ALPHA_IN);
            renderNodeAnimator.setDuration(250L);
            renderNodeAnimator.start();
        }
    }
    
    private void startRtCircleFadeOut(final long duration) {
        final RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator((CanvasProperty)this.mHwCirclePaint, 1, 0.0f);
        renderNodeAnimator.setDuration(duration);
        renderNodeAnimator.setInterpolator((TimeInterpolator)Interpolators.ALPHA_OUT);
        renderNodeAnimator.setTarget((View)this);
        renderNodeAnimator.start();
    }
    
    private void updateCircleColor() {
        final float mCircleRadius = this.mCircleRadius;
        final int mMinBackgroundRadius = this.mMinBackgroundRadius;
        final float n = Math.max(0.0f, Math.min(1.0f, (mCircleRadius - mMinBackgroundRadius) / (mMinBackgroundRadius * 0.5f))) * 0.5f + 0.5f;
        final View mPreviewView = this.mPreviewView;
        float n2 = n;
        if (mPreviewView != null) {
            n2 = n;
            if (mPreviewView.getVisibility() == 0) {
                n2 = n * (1.0f - Math.max(0.0f, this.mCircleRadius - this.mCircleStartRadius) / (this.mMaxCircleSize - this.mCircleStartRadius));
            }
        }
        this.mCirclePaint.setColor(Color.argb((int)(Color.alpha(this.mCircleColor) * n2), Color.red(this.mCircleColor), Color.green(this.mCircleColor), Color.blue(this.mCircleColor)));
    }
    
    private void updateIconColor() {
        if (!this.mShouldTint) {
            return;
        }
        this.getDrawable().mutate().setColorFilter((int)this.mColorInterpolator.evaluate(Math.min(1.0f, this.mCircleRadius / this.mMinBackgroundRadius), (Object)this.mNormalColor, (Object)this.mDarkIconColor), PorterDuff$Mode.SRC_ATOP);
    }
    
    public void finishAnimation(final float n, final Runnable runnable) {
        this.cancelAnimator((Animator)this.mCircleAnimator);
        this.cancelAnimator(this.mPreviewClipper);
        this.mFinishing = true;
        this.mCircleStartRadius = this.mCircleRadius;
        final float maxCircleSize = this.getMaxCircleSize();
        Object o;
        if (this.mSupportHardware) {
            this.initHwProperties();
            o = this.getRtAnimatorToRadius(maxCircleSize);
            this.startRtAlphaFadeIn();
        }
        else {
            o = this.getAnimatorToRadius(maxCircleSize);
        }
        this.mFlingAnimationUtils.applyDismissing((Animator)o, this.mCircleRadius, maxCircleSize, n, maxCircleSize);
        ((Animator)o).addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                runnable.run();
                KeyguardAffordanceView.this.mFinishing = false;
                KeyguardAffordanceView.this.mCircleRadius = maxCircleSize;
                KeyguardAffordanceView.this.invalidate();
            }
        });
        ((Animator)o).start();
        this.setImageAlpha(0.0f, true);
        final View mPreviewView = this.mPreviewView;
        if (mPreviewView != null) {
            mPreviewView.setVisibility(0);
            final Animator circularReveal = ViewAnimationUtils.createCircularReveal(this.mPreviewView, this.getLeft() + this.mCenterX, this.getTop() + this.mCenterY, this.mCircleRadius, maxCircleSize);
            this.mPreviewClipper = circularReveal;
            this.mFlingAnimationUtils.applyDismissing(circularReveal, this.mCircleRadius, maxCircleSize, n, maxCircleSize);
            this.mPreviewClipper.addListener((Animator$AnimatorListener)this.mClipEndListener);
            this.mPreviewClipper.start();
            if (this.mSupportHardware) {
                this.startRtCircleFadeOut(((Animator)o).getDuration());
            }
        }
    }
    
    public float getCircleRadius() {
        return this.mCircleRadius;
    }
    
    public float getRestingAlpha() {
        return this.mRestingAlpha;
    }
    
    public void instantFinishAnimation() {
        this.cancelAnimator(this.mPreviewClipper);
        final View mPreviewView = this.mPreviewView;
        if (mPreviewView != null) {
            mPreviewView.setClipBounds((Rect)null);
            this.mPreviewView.setVisibility(0);
        }
        this.mCircleRadius = this.getMaxCircleSize();
        this.setImageAlpha(0.0f, false);
        this.invalidate();
    }
    
    protected void onDraw(final Canvas canvas) {
        this.mSupportHardware = canvas.isHardwareAccelerated();
        this.drawBackgroundCircle(canvas);
        canvas.save();
        final float mImageScale = this.mImageScale;
        canvas.scale(mImageScale, mImageScale, (float)(this.getWidth() / 2), (float)(this.getHeight() / 2));
        super.onDraw(canvas);
        canvas.restore();
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.mCenterX = this.getWidth() / 2;
        this.mCenterY = this.getHeight() / 2;
        this.mMaxCircleSize = this.getMaxCircleSize();
    }
    
    public boolean performClick() {
        return this.isClickable() && super.performClick();
    }
    
    public void setCircleRadius(final float n, final boolean b) {
        this.setCircleRadius(n, b, false);
    }
    
    public void setCircleRadiusWithoutAnimation(final float n) {
        this.cancelAnimator((Animator)this.mCircleAnimator);
        this.setCircleRadius(n, false, true);
    }
    
    public void setImageAlpha(final float n, final boolean b) {
        this.setImageAlpha(n, b, -1L, null, null);
    }
    
    public void setImageAlpha(float n, final boolean b, final long n2, Interpolator interpolator, final Runnable runnable) {
        this.cancelAnimator((Animator)this.mAlphaAnimator);
        if (this.mLaunchingAffordance) {
            n = 0.0f;
        }
        final int n3 = (int)(n * 255.0f);
        final Drawable background = this.getBackground();
        if (!b) {
            if (background != null) {
                background.mutate().setAlpha(n3);
            }
            this.setImageAlpha(n3);
        }
        else {
            final int imageAlpha = this.getImageAlpha();
            final ValueAnimator ofInt = ValueAnimator.ofInt(new int[] { imageAlpha, n3 });
            (this.mAlphaAnimator = ofInt).addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$KeyguardAffordanceView$GLahQCZQtxFHfhh52YPyKQ2f5GE(this, background));
            ofInt.addListener((Animator$AnimatorListener)this.mAlphaEndListener);
            Interpolator interpolator2;
            if ((interpolator2 = interpolator) == null) {
                if (n == 0.0f) {
                    interpolator = Interpolators.FAST_OUT_LINEAR_IN;
                }
                else {
                    interpolator = Interpolators.LINEAR_OUT_SLOW_IN;
                }
                interpolator2 = interpolator;
            }
            ofInt.setInterpolator((TimeInterpolator)interpolator2);
            long duration = n2;
            if (n2 == -1L) {
                duration = (long)(Math.min(1.0f, Math.abs(imageAlpha - n3) / 255.0f) * 200.0f);
            }
            ofInt.setDuration(duration);
            if (runnable != null) {
                ofInt.addListener(this.getEndListener(runnable));
            }
            ofInt.start();
        }
    }
    
    public void setImageDrawable(final Drawable imageDrawable, final boolean mShouldTint) {
        super.setImageDrawable(imageDrawable);
        this.mShouldTint = mShouldTint;
        this.updateIconColor();
    }
    
    public void setImageScale(final float n, final boolean b) {
        this.setImageScale(n, b, -1L, null);
    }
    
    public void setImageScale(final float mImageScale, final boolean b, final long n, final Interpolator interpolator) {
        this.cancelAnimator((Animator)this.mScaleAnimator);
        if (!b) {
            this.mImageScale = mImageScale;
            this.invalidate();
        }
        else {
            final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { this.mImageScale, mImageScale });
            (this.mScaleAnimator = ofFloat).addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    KeyguardAffordanceView.this.mImageScale = (float)valueAnimator.getAnimatedValue();
                    KeyguardAffordanceView.this.invalidate();
                }
            });
            ofFloat.addListener((Animator$AnimatorListener)this.mScaleEndListener);
            Interpolator interpolator2;
            if ((interpolator2 = interpolator) == null) {
                if (mImageScale == 0.0f) {
                    interpolator2 = Interpolators.FAST_OUT_LINEAR_IN;
                }
                else {
                    interpolator2 = Interpolators.LINEAR_OUT_SLOW_IN;
                }
            }
            ofFloat.setInterpolator((TimeInterpolator)interpolator2);
            long duration = n;
            if (n == -1L) {
                duration = (long)(Math.min(1.0f, Math.abs(this.mImageScale - mImageScale) / 0.19999999f) * 200.0f);
            }
            ofFloat.setDuration(duration);
            ofFloat.start();
        }
    }
    
    public void setLaunchingAffordance(final boolean mLaunchingAffordance) {
        this.mLaunchingAffordance = mLaunchingAffordance;
    }
    
    public void setPreviewView(final View mPreviewView) {
        final View mPreviewView2 = this.mPreviewView;
        if (mPreviewView2 == mPreviewView) {
            return;
        }
        if ((this.mPreviewView = mPreviewView) != null) {
            int visibility;
            if (this.mLaunchingAffordance) {
                visibility = mPreviewView2.getVisibility();
            }
            else {
                visibility = 4;
            }
            mPreviewView.setVisibility(visibility);
        }
    }
    
    public boolean shouldTint() {
        return this.mShouldTint;
    }
}
