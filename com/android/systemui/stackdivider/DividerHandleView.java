// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.stackdivider;

import android.graphics.Canvas;
import android.view.animation.Interpolator;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import com.android.systemui.R$dimen;
import android.content.res.Resources$Theme;
import com.android.systemui.R$color;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Paint;
import android.animation.AnimatorSet;
import android.util.Property;
import android.view.View;

public class DividerHandleView extends View
{
    private static final Property<DividerHandleView, Integer> HEIGHT_PROPERTY;
    private static final Property<DividerHandleView, Integer> WIDTH_PROPERTY;
    private AnimatorSet mAnimator;
    private final int mCircleDiameter;
    private int mCurrentHeight;
    private int mCurrentWidth;
    private final int mHeight;
    private final Paint mPaint;
    private boolean mTouching;
    private final int mWidth;
    
    static {
        WIDTH_PROPERTY = new Property<DividerHandleView, Integer>("width") {
            public Integer get(final DividerHandleView dividerHandleView) {
                return dividerHandleView.mCurrentWidth;
            }
            
            public void set(final DividerHandleView dividerHandleView, final Integer n) {
                dividerHandleView.mCurrentWidth = n;
                dividerHandleView.invalidate();
            }
        };
        HEIGHT_PROPERTY = new Property<DividerHandleView, Integer>("height") {
            public Integer get(final DividerHandleView dividerHandleView) {
                return dividerHandleView.mCurrentHeight;
            }
            
            public void set(final DividerHandleView dividerHandleView, final Integer n) {
                dividerHandleView.mCurrentHeight = n;
                dividerHandleView.invalidate();
            }
        };
    }
    
    public DividerHandleView(final Context context, final AttributeSet set) {
        super(context, set);
        (this.mPaint = new Paint()).setColor(this.getResources().getColor(R$color.docked_divider_handle, (Resources$Theme)null));
        this.mPaint.setAntiAlias(true);
        this.mWidth = this.getResources().getDimensionPixelSize(R$dimen.docked_divider_handle_width);
        final int dimensionPixelSize = this.getResources().getDimensionPixelSize(R$dimen.docked_divider_handle_height);
        this.mHeight = dimensionPixelSize;
        final int mWidth = this.mWidth;
        this.mCurrentWidth = mWidth;
        this.mCurrentHeight = dimensionPixelSize;
        this.mCircleDiameter = (mWidth + dimensionPixelSize) / 3;
    }
    
    private void animateToTarget(final int n, final int n2, final boolean b) {
        (this.mAnimator = new AnimatorSet()).playTogether(new Animator[] { (Animator)ObjectAnimator.ofInt((Object)this, (Property)DividerHandleView.WIDTH_PROPERTY, new int[] { this.mCurrentWidth, n }), (Animator)ObjectAnimator.ofInt((Object)this, (Property)DividerHandleView.HEIGHT_PROPERTY, new int[] { this.mCurrentHeight, n2 }) });
        final AnimatorSet mAnimator = this.mAnimator;
        long duration;
        if (b) {
            duration = 150L;
        }
        else {
            duration = 200L;
        }
        mAnimator.setDuration(duration);
        final AnimatorSet mAnimator2 = this.mAnimator;
        Interpolator interpolator;
        if (b) {
            interpolator = Interpolators.TOUCH_RESPONSE;
        }
        else {
            interpolator = Interpolators.FAST_OUT_SLOW_IN;
        }
        mAnimator2.setInterpolator((TimeInterpolator)interpolator);
        this.mAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                DividerHandleView.this.mAnimator = null;
            }
        });
        this.mAnimator.start();
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void onDraw(final Canvas canvas) {
        final int n = this.getWidth() / 2 - this.mCurrentWidth / 2;
        final int n2 = this.getHeight() / 2;
        final int mCurrentHeight = this.mCurrentHeight;
        final int n3 = n2 - mCurrentHeight / 2;
        final int n4 = Math.min(this.mCurrentWidth, mCurrentHeight) / 2;
        final float n5 = (float)n;
        final float n6 = (float)n3;
        final float n7 = (float)(n + this.mCurrentWidth);
        final float n8 = (float)(n3 + this.mCurrentHeight);
        final float n9 = (float)n4;
        canvas.drawRoundRect(n5, n6, n7, n8, n9, n9, this.mPaint);
    }
    
    public void setTouching(final boolean mTouching, final boolean b) {
        if (mTouching == this.mTouching) {
            return;
        }
        final AnimatorSet mAnimator = this.mAnimator;
        if (mAnimator != null) {
            mAnimator.cancel();
            this.mAnimator = null;
        }
        if (!b) {
            if (mTouching) {
                final int mCircleDiameter = this.mCircleDiameter;
                this.mCurrentWidth = mCircleDiameter;
                this.mCurrentHeight = mCircleDiameter;
            }
            else {
                this.mCurrentWidth = this.mWidth;
                this.mCurrentHeight = this.mHeight;
            }
            this.invalidate();
        }
        else {
            int n;
            if (mTouching) {
                n = this.mCircleDiameter;
            }
            else {
                n = this.mWidth;
            }
            int n2;
            if (mTouching) {
                n2 = this.mCircleDiameter;
            }
            else {
                n2 = this.mHeight;
            }
            this.animateToTarget(n, n2, mTouching);
        }
        this.mTouching = mTouching;
    }
}
