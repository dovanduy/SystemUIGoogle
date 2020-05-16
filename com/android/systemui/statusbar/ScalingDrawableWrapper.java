// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;

public class ScalingDrawableWrapper extends DrawableWrapper
{
    private float mScaleFactor;
    
    public ScalingDrawableWrapper(final Drawable drawable, final float mScaleFactor) {
        super(drawable);
        this.mScaleFactor = mScaleFactor;
    }
    
    public int getIntrinsicHeight() {
        return (int)(super.getIntrinsicHeight() * this.mScaleFactor);
    }
    
    public int getIntrinsicWidth() {
        return (int)(super.getIntrinsicWidth() * this.mScaleFactor);
    }
}
