// 
// Decompiled by Procyon v0.5.36
// 

package com.android.launcher3.icons;

import android.graphics.Bitmap$Config;
import android.graphics.Bitmap;

public class BitmapInfo
{
    public static final Bitmap LOW_RES_ICON;
    public final int color;
    public final Bitmap icon;
    
    static {
        fromBitmap(LOW_RES_ICON = Bitmap.createBitmap(1, 1, Bitmap$Config.ALPHA_8));
    }
    
    public BitmapInfo(final Bitmap icon, final int color) {
        this.icon = icon;
        this.color = color;
    }
    
    public static BitmapInfo fromBitmap(final Bitmap bitmap) {
        return of(bitmap, 0);
    }
    
    public static BitmapInfo of(final Bitmap bitmap, final int n) {
        return new BitmapInfo(bitmap, n);
    }
    
    public interface Extender
    {
        default BitmapInfo getExtendedInfo(final Bitmap bitmap, final int n, final BaseIconFactory baseIconFactory) {
            return BitmapInfo.of(bitmap, n);
        }
    }
}
