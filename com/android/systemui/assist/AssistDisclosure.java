// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import android.graphics.Canvas;
import com.android.systemui.R$dimen;
import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff$Mode;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.graphics.Paint;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.view.ViewGroup$LayoutParams;
import android.view.WindowManager$LayoutParams;
import android.view.View;
import android.view.WindowManager;
import android.os.Handler;
import android.content.Context;

public class AssistDisclosure
{
    private final Context mContext;
    private final Handler mHandler;
    private Runnable mShowRunnable;
    private AssistDisclosureView mView;
    private boolean mViewAdded;
    private final WindowManager mWm;
    
    public AssistDisclosure(final Context mContext, final Handler mHandler) {
        this.mShowRunnable = new Runnable() {
            @Override
            public void run() {
                AssistDisclosure.this.show();
            }
        };
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.mWm = (WindowManager)mContext.getSystemService((Class)WindowManager.class);
    }
    
    private void hide() {
        if (this.mViewAdded) {
            this.mWm.removeView((View)this.mView);
            this.mViewAdded = false;
        }
    }
    
    private void show() {
        if (this.mView == null) {
            this.mView = new AssistDisclosureView(this.mContext);
        }
        if (!this.mViewAdded) {
            final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(2015, 525576, -3);
            windowManager$LayoutParams.setTitle((CharSequence)"AssistDisclosure");
            windowManager$LayoutParams.setFitInsetsTypes(0);
            this.mWm.addView((View)this.mView, (ViewGroup$LayoutParams)windowManager$LayoutParams);
            this.mViewAdded = true;
        }
    }
    
    public void postShow() {
        this.mHandler.removeCallbacks(this.mShowRunnable);
        this.mHandler.post(this.mShowRunnable);
    }
    
    private class AssistDisclosureView extends View implements ValueAnimator$AnimatorUpdateListener
    {
        private int mAlpha;
        private final ValueAnimator mAlphaInAnimator;
        private final ValueAnimator mAlphaOutAnimator;
        private final AnimatorSet mAnimator;
        private final Paint mPaint;
        private final Paint mShadowPaint;
        private float mShadowThickness;
        private float mThickness;
        
        public AssistDisclosureView(final Context context) {
            super(context);
            this.mPaint = new Paint();
            this.mShadowPaint = new Paint();
            this.mAlpha = 0;
            (this.mAlphaInAnimator = ValueAnimator.ofInt(new int[] { 0, 222 }).setDuration(400L)).addUpdateListener((ValueAnimator$AnimatorUpdateListener)this);
            this.mAlphaInAnimator.setInterpolator((TimeInterpolator)Interpolators.CUSTOM_40_40);
            (this.mAlphaOutAnimator = ValueAnimator.ofInt(new int[] { 222, 0 }).setDuration(300L)).addUpdateListener((ValueAnimator$AnimatorUpdateListener)this);
            this.mAlphaOutAnimator.setInterpolator((TimeInterpolator)Interpolators.CUSTOM_40_40);
            final AnimatorSet mAnimator = new AnimatorSet();
            this.mAnimator = mAnimator;
            mAnimator.play((Animator)this.mAlphaInAnimator).before((Animator)this.mAlphaOutAnimator);
            this.mAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter(AssistDisclosure.this) {
                boolean mCancelled;
                
                public void onAnimationCancel(final Animator animator) {
                    this.mCancelled = true;
                }
                
                public void onAnimationEnd(final Animator animator) {
                    if (!this.mCancelled) {
                        AssistDisclosure.this.hide();
                    }
                }
                
                public void onAnimationStart(final Animator animator) {
                    this.mCancelled = false;
                }
            });
            final PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff$Mode.SRC);
            this.mPaint.setColor(-1);
            this.mPaint.setXfermode((Xfermode)porterDuffXfermode);
            this.mShadowPaint.setColor(-12303292);
            this.mShadowPaint.setXfermode((Xfermode)porterDuffXfermode);
            this.mThickness = this.getResources().getDimension(R$dimen.assist_disclosure_thickness);
            this.mShadowThickness = this.getResources().getDimension(R$dimen.assist_disclosure_shadow_thickness);
        }
        
        private void drawBeam(final Canvas canvas, final float n, final float n2, final float n3, final float n4, final Paint paint, final float n5) {
            canvas.drawRect(n - n5, n2 - n5, n3 + n5, n4 + n5, paint);
        }
        
        private void drawGeometry(final Canvas canvas, final Paint paint, final float n) {
            final int width = this.getWidth();
            final int height = this.getHeight();
            final float mThickness = this.mThickness;
            final float n2 = (float)height;
            final float n3 = n2 - mThickness;
            final float n4 = (float)width;
            this.drawBeam(canvas, 0.0f, n3, n4, n2, paint, n);
            this.drawBeam(canvas, 0.0f, 0.0f, mThickness, n3, paint, n);
            final float n5 = n4 - mThickness;
            this.drawBeam(canvas, n5, 0.0f, n4, n3, paint, n);
            this.drawBeam(canvas, mThickness, 0.0f, n5, mThickness, paint, n);
        }
        
        private void startAnimation() {
            this.mAnimator.cancel();
            this.mAnimator.start();
        }
        
        public void onAnimationUpdate(final ValueAnimator valueAnimator) {
            final ValueAnimator mAlphaOutAnimator = this.mAlphaOutAnimator;
            if (valueAnimator == mAlphaOutAnimator) {
                this.mAlpha = (int)mAlphaOutAnimator.getAnimatedValue();
            }
            else {
                final ValueAnimator mAlphaInAnimator = this.mAlphaInAnimator;
                if (valueAnimator == mAlphaInAnimator) {
                    this.mAlpha = (int)mAlphaInAnimator.getAnimatedValue();
                }
            }
            this.invalidate();
        }
        
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.startAnimation();
            this.sendAccessibilityEvent(16777216);
        }
        
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.mAnimator.cancel();
            this.mAlpha = 0;
        }
        
        protected void onDraw(final Canvas canvas) {
            this.mPaint.setAlpha(this.mAlpha);
            this.mShadowPaint.setAlpha(this.mAlpha / 4);
            this.drawGeometry(canvas, this.mShadowPaint, this.mShadowThickness);
            this.drawGeometry(canvas, this.mPaint, 0.0f);
        }
    }
}
