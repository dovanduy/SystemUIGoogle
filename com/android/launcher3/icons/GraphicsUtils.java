// 
// Decompiled by Procyon v0.5.36
// 

package com.android.launcher3.icons;

import android.graphics.Rect;
import android.graphics.RegionIterator;
import android.graphics.Region;

public class GraphicsUtils
{
    public static Runnable sOnNewBitmapRunnable;
    
    static {
        GraphicsUtils.sOnNewBitmapRunnable = (Runnable)_$$Lambda$GraphicsUtils$W6f4e52z7SPvYCk05ydbedScRFQ.INSTANCE;
    }
    
    public static int getArea(final Region region) {
        final RegionIterator regionIterator = new RegionIterator(region);
        final Rect rect = new Rect();
        int n = 0;
        while (regionIterator.next(rect)) {
            n += rect.width() * rect.height();
        }
        return n;
    }
    
    public static void noteNewBitmapCreated() {
        GraphicsUtils.sOnNewBitmapRunnable.run();
    }
    
    public static int setColorAlphaBound(final int n, final int n2) {
        int n3;
        if (n2 < 0) {
            n3 = 0;
        }
        else if ((n3 = n2) > 255) {
            n3 = 255;
        }
        return (n & 0xFFFFFF) | n3 << 24;
    }
}
