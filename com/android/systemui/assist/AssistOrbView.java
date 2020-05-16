// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import com.android.systemui.Interpolators;
import com.android.systemui.R$id;
import android.graphics.Canvas;
import android.animation.TimeInterpolator;
import android.animation.Animator$AnimatorListener;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.animation.Animator;
import android.view.animation.OvershootInterpolator;
import android.util.AttributeSet;
import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.graphics.Rect;
import android.animation.ValueAnimator;
import android.graphics.Paint;
import android.widget.FrameLayout;

public class AssistOrbView extends FrameLayout
{
    private final Paint mBackgroundPaint;
    private final int mBaseMargin;
    private float mCircleAnimationEndValue;
    private ValueAnimator mCircleAnimator;
    private final int mCircleMinSize;
    private final Rect mCircleRect;
    private float mCircleSize;
    private ValueAnimator$AnimatorUpdateListener mCircleUpdateListener;
    private AnimatorListenerAdapter mClearAnimatorListener;
    private boolean mClipToOutline;
    private ImageView mLogo;
    private final int mMaxElevation;
    private float mOffset;
    private ValueAnimator mOffsetAnimator;
    private ValueAnimator$AnimatorUpdateListener mOffsetUpdateListener;
    private float mOutlineAlpha;
    private final Interpolator mOvershootInterpolator;
    private final int mStaticOffset;
    private final Rect mStaticRect;
    
    public AssistOrbView(final Context context) {
        this(context, null);
    }
    
    public AssistOrbView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public AssistOrbView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public AssistOrbView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mBackgroundPaint = new Paint();
        this.mCircleRect = new Rect();
        this.mStaticRect = new Rect();
        this.mOvershootInterpolator = (Interpolator)new OvershootInterpolator();
        this.mCircleUpdateListener = (ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                AssistOrbView.this.applyCircleSize((float)valueAnimator.getAnimatedValue());
                AssistOrbView.this.updateElevation();
            }
        };
        this.mClearAnimatorListener = new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                AssistOrbView.this.mCircleAnimator = null;
            }
        };
        this.mOffsetUpdateListener = (ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                AssistOrbView.this.mOffset = (float)valueAnimator.getAnimatedValue();
                AssistOrbView.this.updateLayout();
            }
        };
        this.setOutlineProvider((ViewOutlineProvider)new ViewOutlineProvider() {
            public void getOutline(final View view, final Outline outline) {
                if (AssistOrbView.this.mCircleSize > 0.0f) {
                    outline.setOval(AssistOrbView.this.mCircleRect);
                }
                else {
                    outline.setEmpty();
                }
                outline.setAlpha(AssistOrbView.this.mOutlineAlpha);
            }
        });
        this.setWillNotDraw(false);
        this.mCircleMinSize = context.getResources().getDimensionPixelSize(R$dimen.assist_orb_size);
        this.mBaseMargin = context.getResources().getDimensionPixelSize(R$dimen.assist_orb_base_margin);
        this.mStaticOffset = context.getResources().getDimensionPixelSize(R$dimen.assist_orb_travel_distance);
        this.mMaxElevation = context.getResources().getDimensionPixelSize(R$dimen.assist_orb_elevation);
        this.mBackgroundPaint.setAntiAlias(true);
        this.mBackgroundPaint.setColor(this.getResources().getColor(R$color.assist_orb_color));
    }
    
    private void animateOffset(final float n, final long duration, final long startDelay, final Interpolator interpolator) {
        final ValueAnimator mOffsetAnimator = this.mOffsetAnimator;
        if (mOffsetAnimator != null) {
            mOffsetAnimator.removeAllListeners();
            this.mOffsetAnimator.cancel();
        }
        (this.mOffsetAnimator = ValueAnimator.ofFloat(new float[] { this.mOffset, n })).addUpdateListener(this.mOffsetUpdateListener);
        this.mOffsetAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                AssistOrbView.this.mOffsetAnimator = null;
            }
        });
        this.mOffsetAnimator.setInterpolator((TimeInterpolator)interpolator);
        this.mOffsetAnimator.setStartDelay(startDelay);
        this.mOffsetAnimator.setDuration(duration);
        this.mOffsetAnimator.start();
    }
    
    private void applyCircleSize(final float mCircleSize) {
        this.mCircleSize = mCircleSize;
        this.updateLayout();
    }
    
    private void drawBackground(final Canvas canvas) {
        canvas.drawCircle((float)this.mCircleRect.centerX(), (float)this.mCircleRect.centerY(), this.mCircleSize / 2.0f, this.mBackgroundPaint);
    }
    
    private void updateCircleRect() {
        this.updateCircleRect(this.mCircleRect, this.mOffset, false);
    }
    
    private void updateCircleRect(final Rect rect, final float n, final boolean b) {
        float mCircleSize;
        if (b) {
            mCircleSize = (float)this.mCircleMinSize;
        }
        else {
            mCircleSize = this.mCircleSize;
        }
        final int n2 = (int)(this.getWidth() - mCircleSize) / 2;
        final int n3 = (int)(this.getHeight() - mCircleSize / 2.0f - this.mBaseMargin - n);
        rect.set(n2, n3, (int)(n2 + mCircleSize), (int)(n3 + mCircleSize));
    }
    
    private void updateClipping() {
        final boolean b = this.mCircleSize < this.mCircleMinSize;
        if (b != this.mClipToOutline) {
            this.setClipToOutline(b);
            this.mClipToOutline = b;
        }
    }
    
    private void updateElevation() {
        final int mStaticOffset = this.mStaticOffset;
        this.setElevation((1.0f - Math.max((mStaticOffset - this.mOffset) / mStaticOffset, 0.0f)) * this.mMaxElevation);
    }
    
    private void updateLayout() {
        this.updateCircleRect();
        this.updateLogo();
        this.invalidateOutline();
        this.invalidate();
        this.updateClipping();
    }
    
    private void updateLogo() {
        final Rect mCircleRect = this.mCircleRect;
        final float n = (mCircleRect.left + mCircleRect.right) / 2.0f;
        final float n2 = this.mLogo.getWidth() / 2.0f;
        final Rect mCircleRect2 = this.mCircleRect;
        final float n3 = (mCircleRect2.top + mCircleRect2.bottom) / 2.0f;
        final float n4 = this.mLogo.getHeight() / 2.0f;
        final float n5 = this.mCircleMinSize / 7.0f;
        final int mStaticOffset = this.mStaticOffset;
        final float n6 = (mStaticOffset - this.mOffset) / mStaticOffset;
        final float n7 = (float)mStaticOffset;
        this.mLogo.setImageAlpha((int)(Math.max((1.0f - n6 - 0.5f) * 2.0f, 0.0f) * 255.0f));
        this.mLogo.setTranslationX(n - n2);
        this.mLogo.setTranslationY(n3 - n4 - n5 + n7 * n6 * 0.1f);
    }
    
    public void animateCircleSize(final float mCircleAnimationEndValue, final long duration, final long startDelay, final Interpolator interpolator) {
        if (mCircleAnimationEndValue == this.mCircleAnimationEndValue) {
            return;
        }
        final ValueAnimator mCircleAnimator = this.mCircleAnimator;
        if (mCircleAnimator != null) {
            mCircleAnimator.cancel();
        }
        (this.mCircleAnimator = ValueAnimator.ofFloat(new float[] { this.mCircleSize, mCircleAnimationEndValue })).addUpdateListener(this.mCircleUpdateListener);
        this.mCircleAnimator.addListener((Animator$AnimatorListener)this.mClearAnimatorListener);
        this.mCircleAnimator.setInterpolator((TimeInterpolator)interpolator);
        this.mCircleAnimator.setDuration(duration);
        this.mCircleAnimator.setStartDelay(startDelay);
        this.mCircleAnimator.start();
        this.mCircleAnimationEndValue = mCircleAnimationEndValue;
    }
    
    public ImageView getLogo() {
        return this.mLogo;
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        this.drawBackground(canvas);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mLogo = (ImageView)this.findViewById(R$id.search_logo);
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        final ImageView mLogo = this.mLogo;
        mLogo.layout(0, 0, mLogo.getMeasuredWidth(), this.mLogo.getMeasuredHeight());
        if (b) {
            this.updateCircleRect(this.mStaticRect, (float)this.mStaticOffset, true);
        }
    }
    
    public void reset() {
        this.mClipToOutline = false;
        this.mBackgroundPaint.setAlpha(255);
        this.mOutlineAlpha = 1.0f;
    }
    
    public void startEnterAnimation() {
        this.applyCircleSize(0.0f);
        this.post((Runnable)new Runnable() {
            @Override
            public void run() {
                final AssistOrbView this$0 = AssistOrbView.this;
                this$0.animateCircleSize((float)this$0.mCircleMinSize, 300L, 0L, AssistOrbView.this.mOvershootInterpolator);
                final AssistOrbView this$2 = AssistOrbView.this;
                this$2.animateOffset((float)this$2.mStaticOffset, 400L, 0L, Interpolators.LINEAR_OUT_SLOW_IN);
            }
        });
    }
    
    public void startExitAnimation(final long n) {
        this.animateCircleSize(0.0f, 200L, n, Interpolators.FAST_OUT_LINEAR_IN);
        this.animateOffset(0.0f, 200L, n, Interpolators.FAST_OUT_LINEAR_IN);
    }
}
