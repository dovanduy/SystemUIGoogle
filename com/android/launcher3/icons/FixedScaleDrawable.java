// 
// Decompiled by Procyon v0.5.36
// 

package com.android.launcher3.icons;

import android.content.res.Resources$Theme;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.DrawableWrapper;

public class FixedScaleDrawable extends DrawableWrapper
{
    private float mScaleX;
    private float mScaleY;
    
    public FixedScaleDrawable() {
        super((Drawable)new ColorDrawable());
        this.mScaleX = 0.46669f;
        this.mScaleY = 0.46669f;
    }
    
    public void draw(final Canvas canvas) {
        final int save = canvas.save();
        canvas.scale(this.mScaleX, this.mScaleY, this.getBounds().exactCenterX(), this.getBounds().exactCenterY());
        super.draw(canvas);
        canvas.restoreToCount(save);
    }
    
    public void inflate(final Resources resources, final XmlPullParser xmlPullParser, final AttributeSet set) {
    }
    
    public void inflate(final Resources resources, final XmlPullParser xmlPullParser, final AttributeSet set, final Resources$Theme resources$Theme) {
    }
    
    public void setScale(float n) {
        final float n2 = (float)this.getIntrinsicHeight();
        final float n3 = (float)this.getIntrinsicWidth();
        n *= 0.46669f;
        this.mScaleX = n;
        this.mScaleY = n;
        if (n2 > n3 && n3 > 0.0f) {
            this.mScaleX = n * (n3 / n2);
        }
        else if (n3 > n2 && n2 > 0.0f) {
            this.mScaleY *= n2 / n3;
        }
    }
}
