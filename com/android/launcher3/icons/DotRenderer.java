// 
// Decompiled by Procyon v0.5.36
// 

package com.android.launcher3.icons;

import android.view.ViewDebug$ExportedProperty;
import android.graphics.Rect;
import android.util.Log;
import android.graphics.Canvas;
import android.graphics.PathMeasure;
import android.graphics.Path$Op;
import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.Bitmap;

public class DotRenderer
{
    private final Bitmap mBackgroundWithShadow;
    private final float mBitmapOffset;
    private final Paint mCirclePaint;
    private final float mCircleRadius;
    private final float[] mLeftDotPosition;
    private final float[] mRightDotPosition;
    
    public DotRenderer(int round, final Path path, final int n) {
        this.mCirclePaint = new Paint(3);
        round = Math.round(round * 0.228f);
        final ShadowGenerator.Builder builder = new ShadowGenerator.Builder(0);
        builder.ambientShadowAlpha = 88;
        builder.setupBlurForSize(round);
        final Bitmap pill = builder.createPill(round, round);
        this.mBackgroundWithShadow = pill;
        this.mCircleRadius = builder.radius;
        this.mBitmapOffset = -pill.getHeight() * 0.5f;
        final float n2 = (float)n;
        this.mLeftDotPosition = getPathPoint(path, n2, -1.0f);
        this.mRightDotPosition = getPathPoint(path, n2, 1.0f);
    }
    
    private static float[] getPathPoint(final Path path, final float n, final float n2) {
        final float n3 = n / 2.0f;
        final float n4 = n2 * n3 + n3;
        final Path path2 = new Path();
        path2.moveTo(n3, n3);
        path2.lineTo(n2 * 1.0f + n4, 0.0f);
        path2.lineTo(n4, -1.0f);
        path2.close();
        path2.op(path, Path$Op.INTERSECT);
        final float[] array = new float[2];
        new PathMeasure(path2, false).getPosTan(0.0f, array, (float[])null);
        array[0] /= n;
        array[1] /= n;
        return array;
    }
    
    public void draw(final Canvas canvas, final DrawParams drawParams) {
        if (drawParams == null) {
            Log.e("DotRenderer", "Invalid null argument(s) passed in call to draw.");
            return;
        }
        canvas.save();
        final Rect iconBounds = drawParams.iconBounds;
        float[] array;
        if (drawParams.leftAlign) {
            array = this.mLeftDotPosition;
        }
        else {
            array = this.mRightDotPosition;
        }
        final float n = iconBounds.left + iconBounds.width() * array[0];
        final float n2 = iconBounds.top + iconBounds.height() * array[1];
        final Rect clipBounds = canvas.getClipBounds();
        float n3;
        if (drawParams.leftAlign) {
            n3 = Math.max(0.0f, clipBounds.left - (this.mBitmapOffset + n));
        }
        else {
            n3 = Math.min(0.0f, clipBounds.right - (n - this.mBitmapOffset));
        }
        canvas.translate(n + n3, n2 + Math.max(0.0f, clipBounds.top - (this.mBitmapOffset + n2)));
        final float scale = drawParams.scale;
        canvas.scale(scale, scale);
        this.mCirclePaint.setColor(-16777216);
        final Bitmap mBackgroundWithShadow = this.mBackgroundWithShadow;
        final float mBitmapOffset = this.mBitmapOffset;
        canvas.drawBitmap(mBackgroundWithShadow, mBitmapOffset, mBitmapOffset, this.mCirclePaint);
        this.mCirclePaint.setColor(drawParams.color);
        canvas.drawCircle(0.0f, 0.0f, this.mCircleRadius, this.mCirclePaint);
        canvas.restore();
    }
    
    public float[] getLeftDotPosition() {
        return this.mLeftDotPosition;
    }
    
    public float[] getRightDotPosition() {
        return this.mRightDotPosition;
    }
    
    public static class DrawParams
    {
        @ViewDebug$ExportedProperty(category = "notification dot", formatToHexString = true)
        public int color;
        @ViewDebug$ExportedProperty(category = "notification dot")
        public Rect iconBounds;
        @ViewDebug$ExportedProperty(category = "notification dot")
        public boolean leftAlign;
        @ViewDebug$ExportedProperty(category = "notification dot")
        public float scale;
        
        public DrawParams() {
            this.iconBounds = new Rect();
        }
    }
}
