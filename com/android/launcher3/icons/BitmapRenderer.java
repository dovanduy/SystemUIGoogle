// 
// Decompiled by Procyon v0.5.36
// 

package com.android.launcher3.icons;

import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import android.annotation.TargetApi;
import android.graphics.Picture;
import android.graphics.Bitmap;
import android.os.Build$VERSION;

public interface BitmapRenderer
{
    public static final boolean USE_HARDWARE_BITMAP = Build$VERSION.SDK_INT >= 28;
    
    @TargetApi(28)
    default Bitmap createHardwareBitmap(final int n, final int n2, final BitmapRenderer bitmapRenderer) {
        if (!BitmapRenderer.USE_HARDWARE_BITMAP) {
            return createSoftwareBitmap(n, n2, bitmapRenderer);
        }
        GraphicsUtils.noteNewBitmapCreated();
        final Picture picture = new Picture();
        bitmapRenderer.draw(picture.beginRecording(n, n2));
        picture.endRecording();
        return Bitmap.createBitmap(picture);
    }
    
    default Bitmap createSoftwareBitmap(final int n, final int n2, final BitmapRenderer bitmapRenderer) {
        GraphicsUtils.noteNewBitmapCreated();
        final Bitmap bitmap = Bitmap.createBitmap(n, n2, Bitmap$Config.ARGB_8888);
        bitmapRenderer.draw(new Canvas(bitmap));
        return bitmap;
    }
    
    void draw(final Canvas p0);
}
