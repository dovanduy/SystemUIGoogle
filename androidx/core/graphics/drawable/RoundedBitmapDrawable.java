// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.graphics.drawable;

import android.graphics.Shader;
import android.graphics.ColorFilter;
import android.graphics.Canvas;
import android.graphics.Shader$TileMode;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Rect;
import android.graphics.BitmapShader;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public abstract class RoundedBitmapDrawable extends Drawable
{
    private boolean mApplyGravity;
    final Bitmap mBitmap;
    private int mBitmapHeight;
    private final BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private float mCornerRadius;
    final Rect mDstRect;
    private final RectF mDstRectF;
    private int mGravity;
    private boolean mIsCircular;
    private final Paint mPaint;
    private final Matrix mShaderMatrix;
    private int mTargetDensity;
    
    RoundedBitmapDrawable(final Resources resources, Bitmap mBitmap) {
        this.mTargetDensity = 160;
        this.mGravity = 119;
        this.mPaint = new Paint(3);
        this.mShaderMatrix = new Matrix();
        this.mDstRect = new Rect();
        this.mDstRectF = new RectF();
        this.mApplyGravity = true;
        if (resources != null) {
            this.mTargetDensity = resources.getDisplayMetrics().densityDpi;
        }
        if ((this.mBitmap = mBitmap) != null) {
            this.computeBitmapSize();
            mBitmap = this.mBitmap;
            final Shader$TileMode clamp = Shader$TileMode.CLAMP;
            this.mBitmapShader = new BitmapShader(mBitmap, clamp, clamp);
        }
        else {
            this.mBitmapHeight = -1;
            this.mBitmapWidth = -1;
            this.mBitmapShader = null;
        }
    }
    
    private void computeBitmapSize() {
        this.mBitmapWidth = this.mBitmap.getScaledWidth(this.mTargetDensity);
        this.mBitmapHeight = this.mBitmap.getScaledHeight(this.mTargetDensity);
    }
    
    private static boolean isGreaterThanZero(final float n) {
        return n > 0.05f;
    }
    
    private void updateCircularCornerRadius() {
        this.mCornerRadius = (float)(Math.min(this.mBitmapHeight, this.mBitmapWidth) / 2);
    }
    
    public void draw(final Canvas canvas) {
        final Bitmap mBitmap = this.mBitmap;
        if (mBitmap == null) {
            return;
        }
        this.updateDstRect();
        if (this.mPaint.getShader() == null) {
            canvas.drawBitmap(mBitmap, (Rect)null, this.mDstRect, this.mPaint);
        }
        else {
            final RectF mDstRectF = this.mDstRectF;
            final float mCornerRadius = this.mCornerRadius;
            canvas.drawRoundRect(mDstRectF, mCornerRadius, mCornerRadius, this.mPaint);
        }
    }
    
    public int getAlpha() {
        return this.mPaint.getAlpha();
    }
    
    public ColorFilter getColorFilter() {
        return this.mPaint.getColorFilter();
    }
    
    public float getCornerRadius() {
        return this.mCornerRadius;
    }
    
    public int getIntrinsicHeight() {
        return this.mBitmapHeight;
    }
    
    public int getIntrinsicWidth() {
        return this.mBitmapWidth;
    }
    
    public int getOpacity() {
        final int mGravity = this.mGravity;
        int n2;
        final int n = n2 = -3;
        if (mGravity == 119) {
            if (this.mIsCircular) {
                n2 = n;
            }
            else {
                final Bitmap mBitmap = this.mBitmap;
                n2 = n;
                if (mBitmap != null) {
                    n2 = n;
                    if (!mBitmap.hasAlpha()) {
                        n2 = n;
                        if (this.mPaint.getAlpha() >= 255) {
                            if (isGreaterThanZero(this.mCornerRadius)) {
                                n2 = n;
                            }
                            else {
                                n2 = -1;
                            }
                        }
                    }
                }
            }
        }
        return n2;
    }
    
    abstract void gravityCompatApply(final int p0, final int p1, final int p2, final Rect p3, final Rect p4);
    
    protected void onBoundsChange(final Rect rect) {
        super.onBoundsChange(rect);
        if (this.mIsCircular) {
            this.updateCircularCornerRadius();
        }
        this.mApplyGravity = true;
    }
    
    public void setAlpha(final int alpha) {
        if (alpha != this.mPaint.getAlpha()) {
            this.mPaint.setAlpha(alpha);
            this.invalidateSelf();
        }
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
        this.invalidateSelf();
    }
    
    public void setCornerRadius(final float mCornerRadius) {
        if (this.mCornerRadius == mCornerRadius) {
            return;
        }
        this.mIsCircular = false;
        if (isGreaterThanZero(mCornerRadius)) {
            this.mPaint.setShader((Shader)this.mBitmapShader);
        }
        else {
            this.mPaint.setShader((Shader)null);
        }
        this.mCornerRadius = mCornerRadius;
        this.invalidateSelf();
    }
    
    public void setDither(final boolean dither) {
        this.mPaint.setDither(dither);
        this.invalidateSelf();
    }
    
    public void setFilterBitmap(final boolean filterBitmap) {
        this.mPaint.setFilterBitmap(filterBitmap);
        this.invalidateSelf();
    }
    
    void updateDstRect() {
        if (this.mApplyGravity) {
            if (this.mIsCircular) {
                final int min = Math.min(this.mBitmapWidth, this.mBitmapHeight);
                this.gravityCompatApply(this.mGravity, min, min, this.getBounds(), this.mDstRect);
                final int min2 = Math.min(this.mDstRect.width(), this.mDstRect.height());
                this.mDstRect.inset(Math.max(0, (this.mDstRect.width() - min2) / 2), Math.max(0, (this.mDstRect.height() - min2) / 2));
                this.mCornerRadius = min2 * 0.5f;
            }
            else {
                this.gravityCompatApply(this.mGravity, this.mBitmapWidth, this.mBitmapHeight, this.getBounds(), this.mDstRect);
            }
            this.mDstRectF.set(this.mDstRect);
            if (this.mBitmapShader != null) {
                final Matrix mShaderMatrix = this.mShaderMatrix;
                final RectF mDstRectF = this.mDstRectF;
                mShaderMatrix.setTranslate(mDstRectF.left, mDstRectF.top);
                this.mShaderMatrix.preScale(this.mDstRectF.width() / this.mBitmap.getWidth(), this.mDstRectF.height() / this.mBitmap.getHeight());
                this.mBitmapShader.setLocalMatrix(this.mShaderMatrix);
                this.mPaint.setShader((Shader)this.mBitmapShader);
            }
            this.mApplyGravity = false;
        }
    }
}
