// 
// Decompiled by Procyon v0.5.36
// 

package com.android.launcher3.icons;

import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff$Mode;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.MaskFilter;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter$Blur;
import android.graphics.BlurMaskFilter;
import android.graphics.Paint;

public class ShadowGenerator
{
    private final Paint mBlurPaint;
    private final BlurMaskFilter mDefaultBlurMaskFilter;
    private final Paint mDrawPaint;
    private final int mIconSize;
    
    public ShadowGenerator(final int mIconSize) {
        this.mIconSize = mIconSize;
        this.mBlurPaint = new Paint(3);
        this.mDrawPaint = new Paint(3);
        this.mDefaultBlurMaskFilter = new BlurMaskFilter(this.mIconSize * 0.010416667f, BlurMaskFilter$Blur.NORMAL);
    }
    
    public void recreateIcon(final Bitmap bitmap, final BlurMaskFilter maskFilter, final int alpha, final int alpha2, final Canvas canvas) {
        synchronized (this) {
            final int[] array = new int[2];
            this.mBlurPaint.setMaskFilter((MaskFilter)maskFilter);
            final Bitmap alpha3 = bitmap.extractAlpha(this.mBlurPaint, array);
            this.mDrawPaint.setAlpha(alpha);
            canvas.drawBitmap(alpha3, (float)array[0], (float)array[1], this.mDrawPaint);
            this.mDrawPaint.setAlpha(alpha2);
            canvas.drawBitmap(alpha3, (float)array[0], array[1] + this.mIconSize * 0.020833334f, this.mDrawPaint);
            this.mDrawPaint.setAlpha(255);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, this.mDrawPaint);
        }
    }
    
    public void recreateIcon(final Bitmap bitmap, final Canvas canvas) {
        synchronized (this) {
            this.recreateIcon(bitmap, this.mDefaultBlurMaskFilter, 30, 61, canvas);
        }
    }
    
    public static class Builder
    {
        public int ambientShadowAlpha;
        public final RectF bounds;
        public final int color;
        public int keyShadowAlpha;
        public float keyShadowDistance;
        public float radius;
        public float shadowBlur;
        
        public Builder(final int color) {
            this.bounds = new RectF();
            this.ambientShadowAlpha = 30;
            this.keyShadowAlpha = 61;
            this.color = color;
        }
        
        public Bitmap createPill(final int n, final int n2) {
            return this.createPill(n, n2, n2 / 2.0f);
        }
        
        public Bitmap createPill(int max, final int n, float radius) {
            this.radius = radius;
            final float n2 = (float)max;
            final float n3 = n2 / 2.0f;
            max = Math.max(Math.round(this.shadowBlur + n3), Math.round(this.radius + this.shadowBlur + this.keyShadowDistance));
            final RectF bounds = this.bounds;
            radius = (float)n;
            bounds.set(0.0f, 0.0f, n2, radius);
            final RectF bounds2 = this.bounds;
            final float n4 = (float)max;
            bounds2.offsetTo(n4 - n3, n4 - radius / 2.0f);
            max *= 2;
            return BitmapRenderer.createHardwareBitmap(max, max, new _$$Lambda$OjMsHesuVZLBPdr255qG_kElFTU(this));
        }
        
        public void drawShadow(final Canvas canvas) {
            final Paint paint = new Paint(3);
            paint.setColor(this.color);
            paint.setShadowLayer(this.shadowBlur, 0.0f, this.keyShadowDistance, GraphicsUtils.setColorAlphaBound(-16777216, this.keyShadowAlpha));
            final RectF bounds = this.bounds;
            final float radius = this.radius;
            canvas.drawRoundRect(bounds, radius, radius, paint);
            paint.setShadowLayer(this.shadowBlur, 0.0f, 0.0f, GraphicsUtils.setColorAlphaBound(-16777216, this.ambientShadowAlpha));
            final RectF bounds2 = this.bounds;
            final float radius2 = this.radius;
            canvas.drawRoundRect(bounds2, radius2, radius2, paint);
            if (Color.alpha(this.color) < 255) {
                paint.setXfermode((Xfermode)new PorterDuffXfermode(PorterDuff$Mode.CLEAR));
                paint.clearShadowLayer();
                paint.setColor(-16777216);
                final RectF bounds3 = this.bounds;
                final float radius3 = this.radius;
                canvas.drawRoundRect(bounds3, radius3, radius3, paint);
                paint.setXfermode((Xfermode)null);
                paint.setColor(this.color);
                final RectF bounds4 = this.bounds;
                final float radius4 = this.radius;
                canvas.drawRoundRect(bounds4, radius4, radius4, paint);
            }
        }
        
        public Builder setupBlurForSize(final int n) {
            final float n2 = n * 1.0f;
            this.shadowBlur = n2 / 24.0f;
            this.keyShadowDistance = n2 / 16.0f;
            return this;
        }
    }
}
