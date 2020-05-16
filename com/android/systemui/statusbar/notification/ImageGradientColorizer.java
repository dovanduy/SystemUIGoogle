// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff$Mode;
import android.graphics.Shader;
import android.graphics.LinearGradient;
import android.graphics.Shader$TileMode;
import android.graphics.Paint;
import android.graphics.ColorMatrix;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ImageGradientColorizer
{
    public Bitmap colorize(final Drawable drawable, int n, final boolean b) {
        final int intrinsicWidth = drawable.getIntrinsicWidth();
        final int intrinsicHeight = drawable.getIntrinsicHeight();
        final int min = Math.min(intrinsicWidth, intrinsicHeight);
        final int n2 = (intrinsicWidth - min) / 2;
        final int n3 = (intrinsicHeight - min) / 2;
        final Drawable mutate = drawable.mutate();
        mutate.setBounds(-n2, -n3, intrinsicWidth - n2, intrinsicHeight - n3);
        final Bitmap bitmap = Bitmap.createBitmap(min, min, Bitmap$Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        final int red = Color.red(n);
        final int green = Color.green(n);
        n = Color.blue(n);
        final float n4 = (float)red;
        final float n5 = n4 / 255.0f;
        final float n6 = (float)green;
        final float n7 = n6 / 255.0f;
        final float n8 = (float)n;
        final float n9 = (n5 * 0.2126f + n7 * 0.7152f + n8 / 255.0f * 0.0722f) * 255.0f;
        final ColorMatrix colorMatrix = new ColorMatrix(new float[] { 0.2126f, 0.7152f, 0.0722f, 0.0f, n4 - n9, 0.2126f, 0.7152f, 0.0722f, 0.0f, n6 - n9, 0.2126f, 0.7152f, 0.0722f, 0.0f, n8 - n9, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f });
        final Paint paint = new Paint(1);
        final float n10 = (float)min;
        n = Color.argb(0.5f, 1.0f, 1.0f, 1.0f);
        paint.setShader((Shader)new LinearGradient(0.0f, 0.0f, n10, 0.0f, new int[] { 0, n, -16777216 }, new float[] { 0.0f, 0.4f, 1.0f }, Shader$TileMode.CLAMP));
        final Bitmap bitmap2 = Bitmap.createBitmap(min, min, Bitmap$Config.ARGB_8888);
        final Canvas canvas2 = new Canvas(bitmap2);
        mutate.clearColorFilter();
        mutate.draw(canvas2);
        if (b) {
            canvas2.translate(n10, 0.0f);
            canvas2.scale(-1.0f, 1.0f);
        }
        paint.setXfermode((Xfermode)new PorterDuffXfermode(PorterDuff$Mode.DST_IN));
        canvas2.drawPaint(paint);
        final Paint paint2 = new Paint(1);
        paint2.setColorFilter((ColorFilter)new ColorMatrixColorFilter(colorMatrix));
        paint2.setAlpha(127);
        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint2);
        n = Color.argb(0.5f, 1.0f, 1.0f, 1.0f);
        paint.setShader((Shader)new LinearGradient(0.0f, 0.0f, n10, 0.0f, new int[] { 0, n, -16777216 }, new float[] { 0.0f, 0.6f, 1.0f }, Shader$TileMode.CLAMP));
        canvas2.drawPaint(paint);
        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, (Paint)null);
        return bitmap;
    }
}
