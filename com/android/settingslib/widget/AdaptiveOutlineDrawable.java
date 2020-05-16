// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget;

import android.graphics.Rect;
import android.graphics.Canvas;
import android.graphics.Paint$Style;
import android.content.res.Resources$Theme;
import android.util.PathParser;
import android.graphics.drawable.Drawable;
import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.drawable.DrawableWrapper;

public class AdaptiveOutlineDrawable extends DrawableWrapper
{
    private final Bitmap mBitmap;
    private final int mInsetPx;
    final Paint mOutlinePaint;
    private Path mPath;
    
    public AdaptiveOutlineDrawable(final Resources resources, final Bitmap mBitmap) {
        super((Drawable)new AdaptiveIconShapeDrawable(resources));
        this.getDrawable().setTint(-1);
        this.mPath = new Path(PathParser.createPathFromPathData(resources.getString(17039911)));
        (this.mOutlinePaint = new Paint()).setColor(resources.getColor(R$color.bt_outline_color, (Resources$Theme)null));
        this.mOutlinePaint.setStyle(Paint$Style.STROKE);
        this.mOutlinePaint.setStrokeWidth(resources.getDimension(R$dimen.adaptive_outline_stroke));
        this.mOutlinePaint.setAntiAlias(true);
        this.mInsetPx = resources.getDimensionPixelSize(R$dimen.dashboard_tile_foreground_image_inset);
        this.mBitmap = mBitmap;
    }
    
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        final Rect bounds = this.getBounds();
        final float n = (bounds.right - bounds.left) / 100.0f;
        final float n2 = (bounds.bottom - bounds.top) / 100.0f;
        final int save = canvas.save();
        canvas.scale(n, n2);
        canvas.drawPath(this.mPath, this.mOutlinePaint);
        canvas.restoreToCount(save);
        final Bitmap mBitmap = this.mBitmap;
        final int left = bounds.left;
        final int mInsetPx = this.mInsetPx;
        canvas.drawBitmap(mBitmap, (float)(left + mInsetPx), (float)(bounds.top + mInsetPx), (Paint)null);
    }
    
    public int getIntrinsicHeight() {
        return this.mBitmap.getHeight() + this.mInsetPx * 2;
    }
    
    public int getIntrinsicWidth() {
        return this.mBitmap.getWidth() + this.mInsetPx * 2;
    }
}
