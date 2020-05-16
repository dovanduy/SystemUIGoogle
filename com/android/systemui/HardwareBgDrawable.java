// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.Canvas;
import com.android.settingslib.Utils;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

public class HardwareBgDrawable extends LayerDrawable
{
    private final Drawable[] mLayers;
    private final Paint mPaint;
    private int mPoint;
    private boolean mRotatedBackground;
    private final boolean mRoundTop;
    
    public HardwareBgDrawable(final boolean b, final boolean b2, final Context context) {
        this(b, getLayers(context, b, b2));
    }
    
    public HardwareBgDrawable(final boolean mRoundTop, final Drawable[] mLayers) {
        super(mLayers);
        this.mPaint = new Paint();
        if (mLayers.length == 2) {
            this.mRoundTop = mRoundTop;
            this.mLayers = mLayers;
            return;
        }
        throw new IllegalArgumentException("Need 2 layers");
    }
    
    private static Drawable[] getLayers(final Context context, final boolean b, final boolean b2) {
        int n;
        if (b2) {
            n = R$drawable.rounded_bg_full;
        }
        else {
            n = R$drawable.rounded_bg;
        }
        Drawable[] array;
        if (b) {
            array = new Drawable[] { context.getDrawable(n).mutate(), context.getDrawable(n).mutate() };
        }
        else {
            array = new Drawable[] { context.getDrawable(n).mutate(), null };
            int n2;
            if (b2) {
                n2 = R$drawable.rounded_full_bg_bottom;
            }
            else {
                n2 = R$drawable.rounded_bg_bottom;
            }
            array[1] = context.getDrawable(n2).mutate();
        }
        array[1].setTintList(Utils.getColorAttr(context, 16843827));
        return array;
    }
    
    public void draw(final Canvas canvas) {
        if (this.mPoint >= 0 && !this.mRotatedBackground) {
            final Rect bounds = this.getBounds();
            final int n = bounds.top + this.mPoint;
            final int bottom = bounds.bottom;
            int n2;
            if ((n2 = n) > bottom) {
                n2 = bottom;
            }
            if (this.mRoundTop) {
                this.mLayers[0].setBounds(bounds.left, bounds.top, bounds.right, n2);
            }
            else {
                this.mLayers[1].setBounds(bounds.left, n2, bounds.right, bounds.bottom);
            }
            if (this.mRoundTop) {
                this.mLayers[1].draw(canvas);
                this.mLayers[0].draw(canvas);
            }
            else {
                this.mLayers[0].draw(canvas);
                this.mLayers[1].draw(canvas);
            }
        }
        else {
            this.mLayers[0].draw(canvas);
        }
    }
    
    public int getOpacity() {
        return -1;
    }
    
    public void setAlpha(final int alpha) {
        this.mPaint.setAlpha(alpha);
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }
}
