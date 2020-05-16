// 
// Decompiled by Procyon v0.5.36
// 

package androidx.swiperefreshlayout.widget;

import android.graphics.Path$FillType;
import android.graphics.Paint$Style;
import android.graphics.Paint$Cap;
import android.graphics.RectF;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.animation.Animator$AnimatorListener;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.ValueAnimator;
import androidx.core.util.Preconditions;
import android.content.Context;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.view.animation.LinearInterpolator;
import android.content.res.Resources;
import android.animation.Animator;
import android.view.animation.Interpolator;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

public class CircularProgressDrawable extends Drawable implements Animatable
{
    private static final int[] COLORS;
    private static final Interpolator LINEAR_INTERPOLATOR;
    private static final Interpolator MATERIAL_INTERPOLATOR;
    private Animator mAnimator;
    boolean mFinishing;
    private Resources mResources;
    private final Ring mRing;
    private float mRotation;
    float mRotationCount;
    
    static {
        LINEAR_INTERPOLATOR = (Interpolator)new LinearInterpolator();
        MATERIAL_INTERPOLATOR = (Interpolator)new FastOutSlowInInterpolator();
        COLORS = new int[] { -16777216 };
    }
    
    public CircularProgressDrawable(final Context context) {
        Preconditions.checkNotNull(context);
        this.mResources = context.getResources();
        (this.mRing = new Ring()).setColors(CircularProgressDrawable.COLORS);
        this.setStrokeWidth(2.5f);
        this.setupAnimators();
    }
    
    private void applyFinishTranslation(final float n, final Ring ring) {
        this.updateRingColor(n, ring);
        final float n2 = (float)(Math.floor(ring.getStartingRotation() / 0.8f) + 1.0);
        ring.setStartTrim(ring.getStartingStartTrim() + (ring.getStartingEndTrim() - 0.01f - ring.getStartingStartTrim()) * n);
        ring.setEndTrim(ring.getStartingEndTrim());
        ring.setRotation(ring.getStartingRotation() + (n2 - ring.getStartingRotation()) * n);
    }
    
    private int evaluateColorChange(final float n, int n2, final int n3) {
        final int n4 = n2 >> 24 & 0xFF;
        final int n5 = n2 >> 16 & 0xFF;
        final int n6 = n2 >> 8 & 0xFF;
        n2 &= 0xFF;
        return n4 + (int)(((n3 >> 24 & 0xFF) - n4) * n) << 24 | n5 + (int)(((n3 >> 16 & 0xFF) - n5) * n) << 16 | n6 + (int)(((n3 >> 8 & 0xFF) - n6) * n) << 8 | n2 + (int)(n * ((n3 & 0xFF) - n2));
    }
    
    private void setRotation(final float mRotation) {
        this.mRotation = mRotation;
    }
    
    private void setSizeParameters(final float n, final float n2, final float n3, final float n4) {
        final Ring mRing = this.mRing;
        final float density = this.mResources.getDisplayMetrics().density;
        mRing.setStrokeWidth(n2 * density);
        mRing.setCenterRadius(n * density);
        mRing.setColorIndex(0);
        mRing.setArrowDimensions(n3 * density, n4 * density);
    }
    
    private void setupAnimators() {
        final Ring mRing = this.mRing;
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
        ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                final float floatValue = (float)valueAnimator.getAnimatedValue();
                CircularProgressDrawable.this.updateRingColor(floatValue, mRing);
                CircularProgressDrawable.this.applyTransformation(floatValue, mRing, false);
                CircularProgressDrawable.this.invalidateSelf();
            }
        });
        ofFloat.setRepeatCount(-1);
        ofFloat.setRepeatMode(1);
        ofFloat.setInterpolator((TimeInterpolator)CircularProgressDrawable.LINEAR_INTERPOLATOR);
        ofFloat.addListener((Animator$AnimatorListener)new Animator$AnimatorListener() {
            public void onAnimationCancel(final Animator animator) {
            }
            
            public void onAnimationEnd(final Animator animator) {
            }
            
            public void onAnimationRepeat(final Animator animator) {
                CircularProgressDrawable.this.applyTransformation(1.0f, mRing, true);
                mRing.storeOriginals();
                mRing.goToNextColor();
                final CircularProgressDrawable this$0 = CircularProgressDrawable.this;
                if (this$0.mFinishing) {
                    this$0.mFinishing = false;
                    animator.cancel();
                    animator.setDuration(1332L);
                    animator.start();
                    mRing.setShowArrow(false);
                }
                else {
                    ++this$0.mRotationCount;
                }
            }
            
            public void onAnimationStart(final Animator animator) {
                CircularProgressDrawable.this.mRotationCount = 0.0f;
            }
        });
        this.mAnimator = (Animator)ofFloat;
    }
    
    void applyTransformation(final float n, final Ring ring, final boolean b) {
        if (this.mFinishing) {
            this.applyFinishTranslation(n, ring);
        }
        else if (n != 1.0f || b) {
            final float startingRotation = ring.getStartingRotation();
            float startingStartTrim;
            float endTrim;
            if (n < 0.5f) {
                final float n2 = n / 0.5f;
                startingStartTrim = ring.getStartingStartTrim();
                endTrim = CircularProgressDrawable.MATERIAL_INTERPOLATOR.getInterpolation(n2) * 0.79f + 0.01f + startingStartTrim;
            }
            else {
                final float n3 = (n - 0.5f) / 0.5f;
                endTrim = ring.getStartingStartTrim() + 0.79f;
                startingStartTrim = endTrim - ((1.0f - CircularProgressDrawable.MATERIAL_INTERPOLATOR.getInterpolation(n3)) * 0.79f + 0.01f);
            }
            final float mRotationCount = this.mRotationCount;
            ring.setStartTrim(startingStartTrim);
            ring.setEndTrim(endTrim);
            ring.setRotation(startingRotation + 0.20999998f * n);
            this.setRotation((n + mRotationCount) * 216.0f);
        }
    }
    
    public void draw(final Canvas canvas) {
        final Rect bounds = this.getBounds();
        canvas.save();
        canvas.rotate(this.mRotation, bounds.exactCenterX(), bounds.exactCenterY());
        this.mRing.draw(canvas, bounds);
        canvas.restore();
    }
    
    public int getAlpha() {
        return this.mRing.getAlpha();
    }
    
    public int getOpacity() {
        return -3;
    }
    
    public boolean isRunning() {
        return this.mAnimator.isRunning();
    }
    
    public void setAlpha(final int alpha) {
        this.mRing.setAlpha(alpha);
        this.invalidateSelf();
    }
    
    public void setArrowEnabled(final boolean showArrow) {
        this.mRing.setShowArrow(showArrow);
        this.invalidateSelf();
    }
    
    public void setArrowScale(final float arrowScale) {
        this.mRing.setArrowScale(arrowScale);
        this.invalidateSelf();
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
        this.mRing.setColorFilter(colorFilter);
        this.invalidateSelf();
    }
    
    public void setProgressRotation(final float rotation) {
        this.mRing.setRotation(rotation);
        this.invalidateSelf();
    }
    
    public void setStartEndTrim(final float startTrim, final float endTrim) {
        this.mRing.setStartTrim(startTrim);
        this.mRing.setEndTrim(endTrim);
        this.invalidateSelf();
    }
    
    public void setStrokeWidth(final float strokeWidth) {
        this.mRing.setStrokeWidth(strokeWidth);
        this.invalidateSelf();
    }
    
    public void setStyle(final int n) {
        if (n == 0) {
            this.setSizeParameters(11.0f, 3.0f, 12.0f, 6.0f);
        }
        else {
            this.setSizeParameters(7.5f, 2.5f, 10.0f, 5.0f);
        }
        this.invalidateSelf();
    }
    
    public void start() {
        this.mAnimator.cancel();
        this.mRing.storeOriginals();
        if (this.mRing.getEndTrim() != this.mRing.getStartTrim()) {
            this.mFinishing = true;
            this.mAnimator.setDuration(666L);
            this.mAnimator.start();
        }
        else {
            this.mRing.setColorIndex(0);
            this.mRing.resetOriginals();
            this.mAnimator.setDuration(1332L);
            this.mAnimator.start();
        }
    }
    
    public void stop() {
        this.mAnimator.cancel();
        this.setRotation(0.0f);
        this.mRing.setShowArrow(false);
        this.mRing.setColorIndex(0);
        this.mRing.resetOriginals();
        this.invalidateSelf();
    }
    
    void updateRingColor(final float n, final Ring ring) {
        if (n > 0.75f) {
            ring.setColor(this.evaluateColorChange((n - 0.75f) / 0.25f, ring.getStartingColor(), ring.getNextColor()));
        }
        else {
            ring.setColor(ring.getStartingColor());
        }
    }
    
    private static class Ring
    {
        int mAlpha;
        Path mArrow;
        int mArrowHeight;
        final Paint mArrowPaint;
        float mArrowScale;
        int mArrowWidth;
        final Paint mCirclePaint;
        int mColorIndex;
        int[] mColors;
        int mCurrentColor;
        float mEndTrim;
        final Paint mPaint;
        float mRingCenterRadius;
        float mRotation;
        boolean mShowArrow;
        float mStartTrim;
        float mStartingEndTrim;
        float mStartingRotation;
        float mStartingStartTrim;
        float mStrokeWidth;
        final RectF mTempBounds;
        
        Ring() {
            this.mTempBounds = new RectF();
            this.mPaint = new Paint();
            this.mArrowPaint = new Paint();
            this.mCirclePaint = new Paint();
            this.mStartTrim = 0.0f;
            this.mEndTrim = 0.0f;
            this.mRotation = 0.0f;
            this.mStrokeWidth = 5.0f;
            this.mArrowScale = 1.0f;
            this.mAlpha = 255;
            this.mPaint.setStrokeCap(Paint$Cap.SQUARE);
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStyle(Paint$Style.STROKE);
            this.mArrowPaint.setStyle(Paint$Style.FILL);
            this.mArrowPaint.setAntiAlias(true);
            this.mCirclePaint.setColor(0);
        }
        
        void draw(final Canvas canvas, final Rect rect) {
            final RectF mTempBounds = this.mTempBounds;
            final float mRingCenterRadius = this.mRingCenterRadius;
            float n = this.mStrokeWidth / 2.0f + mRingCenterRadius;
            if (mRingCenterRadius <= 0.0f) {
                n = Math.min(rect.width(), rect.height()) / 2.0f - Math.max(this.mArrowWidth * this.mArrowScale / 2.0f, this.mStrokeWidth / 2.0f);
            }
            mTempBounds.set(rect.centerX() - n, rect.centerY() - n, rect.centerX() + n, rect.centerY() + n);
            final float mStartTrim = this.mStartTrim;
            final float mRotation = this.mRotation;
            final float n2 = (mStartTrim + mRotation) * 360.0f;
            final float n3 = (this.mEndTrim + mRotation) * 360.0f - n2;
            this.mPaint.setColor(this.mCurrentColor);
            this.mPaint.setAlpha(this.mAlpha);
            final float n4 = this.mStrokeWidth / 2.0f;
            mTempBounds.inset(n4, n4);
            canvas.drawCircle(mTempBounds.centerX(), mTempBounds.centerY(), mTempBounds.width() / 2.0f, this.mCirclePaint);
            final float n5 = -n4;
            mTempBounds.inset(n5, n5);
            canvas.drawArc(mTempBounds, n2, n3, false, this.mPaint);
            this.drawTriangle(canvas, n2, n3, mTempBounds);
        }
        
        void drawTriangle(final Canvas canvas, final float n, final float n2, final RectF rectF) {
            if (this.mShowArrow) {
                final Path mArrow = this.mArrow;
                if (mArrow == null) {
                    (this.mArrow = new Path()).setFillType(Path$FillType.EVEN_ODD);
                }
                else {
                    mArrow.reset();
                }
                final float n3 = Math.min(rectF.width(), rectF.height()) / 2.0f;
                final float n4 = this.mArrowWidth * this.mArrowScale / 2.0f;
                this.mArrow.moveTo(0.0f, 0.0f);
                this.mArrow.lineTo(this.mArrowWidth * this.mArrowScale, 0.0f);
                final Path mArrow2 = this.mArrow;
                final float n5 = (float)this.mArrowWidth;
                final float mArrowScale = this.mArrowScale;
                mArrow2.lineTo(n5 * mArrowScale / 2.0f, this.mArrowHeight * mArrowScale);
                this.mArrow.offset(n3 + rectF.centerX() - n4, rectF.centerY() + this.mStrokeWidth / 2.0f);
                this.mArrow.close();
                this.mArrowPaint.setColor(this.mCurrentColor);
                this.mArrowPaint.setAlpha(this.mAlpha);
                canvas.save();
                canvas.rotate(n + n2, rectF.centerX(), rectF.centerY());
                canvas.drawPath(this.mArrow, this.mArrowPaint);
                canvas.restore();
            }
        }
        
        int getAlpha() {
            return this.mAlpha;
        }
        
        float getEndTrim() {
            return this.mEndTrim;
        }
        
        int getNextColor() {
            return this.mColors[this.getNextColorIndex()];
        }
        
        int getNextColorIndex() {
            return (this.mColorIndex + 1) % this.mColors.length;
        }
        
        float getStartTrim() {
            return this.mStartTrim;
        }
        
        int getStartingColor() {
            return this.mColors[this.mColorIndex];
        }
        
        float getStartingEndTrim() {
            return this.mStartingEndTrim;
        }
        
        float getStartingRotation() {
            return this.mStartingRotation;
        }
        
        float getStartingStartTrim() {
            return this.mStartingStartTrim;
        }
        
        void goToNextColor() {
            this.setColorIndex(this.getNextColorIndex());
        }
        
        void resetOriginals() {
            this.mStartingStartTrim = 0.0f;
            this.mStartingEndTrim = 0.0f;
            this.setStartTrim(this.mStartingRotation = 0.0f);
            this.setEndTrim(0.0f);
            this.setRotation(0.0f);
        }
        
        void setAlpha(final int mAlpha) {
            this.mAlpha = mAlpha;
        }
        
        void setArrowDimensions(final float n, final float n2) {
            this.mArrowWidth = (int)n;
            this.mArrowHeight = (int)n2;
        }
        
        void setArrowScale(final float mArrowScale) {
            if (mArrowScale != this.mArrowScale) {
                this.mArrowScale = mArrowScale;
            }
        }
        
        void setCenterRadius(final float mRingCenterRadius) {
            this.mRingCenterRadius = mRingCenterRadius;
        }
        
        void setColor(final int mCurrentColor) {
            this.mCurrentColor = mCurrentColor;
        }
        
        void setColorFilter(final ColorFilter colorFilter) {
            this.mPaint.setColorFilter(colorFilter);
        }
        
        void setColorIndex(final int mColorIndex) {
            this.mColorIndex = mColorIndex;
            this.mCurrentColor = this.mColors[mColorIndex];
        }
        
        void setColors(final int[] mColors) {
            this.mColors = mColors;
            this.setColorIndex(0);
        }
        
        void setEndTrim(final float mEndTrim) {
            this.mEndTrim = mEndTrim;
        }
        
        void setRotation(final float mRotation) {
            this.mRotation = mRotation;
        }
        
        void setShowArrow(final boolean mShowArrow) {
            if (this.mShowArrow != mShowArrow) {
                this.mShowArrow = mShowArrow;
            }
        }
        
        void setStartTrim(final float mStartTrim) {
            this.mStartTrim = mStartTrim;
        }
        
        void setStrokeWidth(final float n) {
            this.mStrokeWidth = n;
            this.mPaint.setStrokeWidth(n);
        }
        
        void storeOriginals() {
            this.mStartingStartTrim = this.mStartTrim;
            this.mStartingEndTrim = this.mEndTrim;
            this.mStartingRotation = this.mRotation;
        }
    }
}
