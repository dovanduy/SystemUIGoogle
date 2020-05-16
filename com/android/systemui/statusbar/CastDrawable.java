// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.content.res.Resources$Theme;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;

public class CastDrawable extends DrawableWrapper
{
    private Drawable mFillDrawable;
    private int mHorizontalPadding;
    
    public CastDrawable() {
        super((Drawable)null);
    }
    
    public void applyTheme(final Resources$Theme resources$Theme) {
        super.applyTheme(resources$Theme);
        this.mFillDrawable.applyTheme(resources$Theme);
    }
    
    public boolean canApplyTheme() {
        return this.mFillDrawable.canApplyTheme() || super.canApplyTheme();
    }
    
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        this.mFillDrawable.draw(canvas);
    }
    
    public boolean getPadding(final Rect rect) {
        final int left = rect.left;
        final int mHorizontalPadding = this.mHorizontalPadding;
        rect.left = left + mHorizontalPadding;
        rect.right += mHorizontalPadding;
        return true;
    }
    
    public void inflate(final Resources resources, final XmlPullParser xmlPullParser, final AttributeSet set, final Resources$Theme resources$Theme) throws XmlPullParserException, IOException {
        super.inflate(resources, xmlPullParser, set, resources$Theme);
        this.setDrawable(resources.getDrawable(R$drawable.ic_cast, resources$Theme).mutate());
        this.mFillDrawable = resources.getDrawable(R$drawable.ic_cast_connected_fill, resources$Theme).mutate();
        this.mHorizontalPadding = resources.getDimensionPixelSize(R$dimen.status_bar_horizontal_padding);
    }
    
    public Drawable mutate() {
        this.mFillDrawable.mutate();
        return super.mutate();
    }
    
    protected void onBoundsChange(final Rect bounds) {
        super.onBoundsChange(bounds);
        this.mFillDrawable.setBounds(bounds);
    }
    
    public boolean onLayoutDirectionChanged(final int layoutDirection) {
        this.mFillDrawable.setLayoutDirection(layoutDirection);
        return super.onLayoutDirectionChanged(layoutDirection);
    }
    
    public void setAlpha(final int n) {
        super.setAlpha(n);
        this.mFillDrawable.setAlpha(n);
    }
    
    public boolean setVisible(final boolean b, final boolean b2) {
        this.mFillDrawable.setVisible(b, b2);
        return super.setVisible(b, b2);
    }
}
