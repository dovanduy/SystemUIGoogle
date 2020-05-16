// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.util.Property;
import android.animation.ObjectAnimator;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.Path$Direction;
import android.graphics.Matrix;
import android.graphics.Canvas;
import android.animation.ValueAnimator;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.graphics.RectF;
import android.util.FloatProperty;
import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class SlashDrawable extends Drawable
{
    private boolean mAnimationEnabled;
    private float mCurrentSlashLength;
    private Drawable mDrawable;
    private final Paint mPaint;
    private final Path mPath;
    private float mRotation;
    private final FloatProperty mSlashLengthProp;
    private final RectF mSlashRect;
    private boolean mSlashed;
    private ColorStateList mTintList;
    private PorterDuff$Mode mTintMode;
    
    public SlashDrawable(final Drawable mDrawable) {
        this.mPath = new Path();
        this.mPaint = new Paint(1);
        this.mSlashRect = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
        this.mAnimationEnabled = true;
        this.mSlashLengthProp = new FloatProperty<SlashDrawable>("slashLength") {
            public Float get(final SlashDrawable slashDrawable) {
                return slashDrawable.mCurrentSlashLength;
            }
            
            public void setValue(final SlashDrawable slashDrawable, final float n) {
                slashDrawable.mCurrentSlashLength = n;
            }
        };
        this.mDrawable = mDrawable;
    }
    
    private float scale(final float n, final int n2) {
        return n * n2;
    }
    
    private void updateRect(final float left, final float top, final float right, final float bottom) {
        final RectF mSlashRect = this.mSlashRect;
        mSlashRect.left = left;
        mSlashRect.top = top;
        mSlashRect.right = right;
        mSlashRect.bottom = bottom;
    }
    
    public void draw(final Canvas canvas) {
        canvas.save();
        final Matrix matrix = new Matrix();
        final int width = this.getBounds().width();
        final int height = this.getBounds().height();
        final float scale = this.scale(1.0f, width);
        final float scale2 = this.scale(1.0f, height);
        this.updateRect(this.scale(0.40544835f, width), this.scale(-0.088781714f, height), this.scale(0.4820516f, width), this.scale(this.mCurrentSlashLength - 0.088781714f, height));
        this.mPath.reset();
        this.mPath.addRoundRect(this.mSlashRect, scale, scale2, Path$Direction.CW);
        final float mRotation = this.mRotation;
        final float n = (float)(width / 2);
        final float n2 = (float)(height / 2);
        matrix.setRotate(mRotation - 45.0f, n, n2);
        this.mPath.transform(matrix);
        canvas.drawPath(this.mPath, this.mPaint);
        matrix.setRotate(-this.mRotation + 45.0f, n, n2);
        this.mPath.transform(matrix);
        matrix.setTranslate(this.mSlashRect.width(), 0.0f);
        this.mPath.transform(matrix);
        this.mPath.addRoundRect(this.mSlashRect, width * 1.0f, height * 1.0f, Path$Direction.CW);
        matrix.setRotate(this.mRotation - 45.0f, n, n2);
        this.mPath.transform(matrix);
        canvas.clipOutPath(this.mPath);
        this.mDrawable.draw(canvas);
        canvas.restore();
    }
    
    public int getIntrinsicHeight() {
        final Drawable mDrawable = this.mDrawable;
        int intrinsicHeight;
        if (mDrawable != null) {
            intrinsicHeight = mDrawable.getIntrinsicHeight();
        }
        else {
            intrinsicHeight = 0;
        }
        return intrinsicHeight;
    }
    
    public int getIntrinsicWidth() {
        final Drawable mDrawable = this.mDrawable;
        int intrinsicWidth;
        if (mDrawable != null) {
            intrinsicWidth = mDrawable.getIntrinsicWidth();
        }
        else {
            intrinsicWidth = 0;
        }
        return intrinsicWidth;
    }
    
    public int getOpacity() {
        return 255;
    }
    
    protected void onBoundsChange(final Rect bounds) {
        super.onBoundsChange(bounds);
        this.mDrawable.setBounds(bounds);
    }
    
    public void setAlpha(final int n) {
        this.mDrawable.setAlpha(n);
        this.mPaint.setAlpha(n);
    }
    
    public void setAnimationEnabled(final boolean mAnimationEnabled) {
        this.mAnimationEnabled = mAnimationEnabled;
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
        this.mDrawable.setColorFilter(colorFilter);
        this.mPaint.setColorFilter(colorFilter);
    }
    
    public void setDrawable(final Drawable mDrawable) {
        (this.mDrawable = mDrawable).setCallback(this.getCallback());
        this.mDrawable.setBounds(this.getBounds());
        final PorterDuff$Mode mTintMode = this.mTintMode;
        if (mTintMode != null) {
            this.mDrawable.setTintMode(mTintMode);
        }
        final ColorStateList mTintList = this.mTintList;
        if (mTintList != null) {
            this.mDrawable.setTintList(mTintList);
        }
        this.invalidateSelf();
    }
    
    protected void setDrawableTintList(final ColorStateList tintList) {
        this.mDrawable.setTintList(tintList);
    }
    
    public void setRotation(final float mRotation) {
        if (this.mRotation == mRotation) {
            return;
        }
        this.mRotation = mRotation;
        this.invalidateSelf();
    }
    
    public void setSlashed(final boolean mSlashed) {
        if (this.mSlashed == mSlashed) {
            return;
        }
        this.mSlashed = mSlashed;
        float n = 1.1666666f;
        float mCurrentSlashLength;
        if (mSlashed) {
            mCurrentSlashLength = 1.1666666f;
        }
        else {
            mCurrentSlashLength = 0.0f;
        }
        if (this.mSlashed) {
            n = 0.0f;
        }
        if (this.mAnimationEnabled) {
            final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)this, (Property)this.mSlashLengthProp, new float[] { n, mCurrentSlashLength });
            ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$SlashDrawable$d6ImpYshN38WeANK1PRMKepeaRo(this));
            ofFloat.setDuration(350L);
            ofFloat.start();
        }
        else {
            this.mCurrentSlashLength = mCurrentSlashLength;
            this.invalidateSelf();
        }
    }
    
    public void setTint(final int color) {
        super.setTint(color);
        this.mDrawable.setTint(color);
        this.mPaint.setColor(color);
    }
    
    public void setTintList(final ColorStateList list) {
        super.setTintList(this.mTintList = list);
        this.setDrawableTintList(list);
        this.mPaint.setColor(list.getDefaultColor());
        this.invalidateSelf();
    }
    
    public void setTintMode(final PorterDuff$Mode porterDuff$Mode) {
        super.setTintMode(this.mTintMode = porterDuff$Mode);
        this.mDrawable.setTintMode(porterDuff$Mode);
    }
}
