// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import android.graphics.Paint$Align;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;
import android.content.res.Resources;
import android.util.Log;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.content.res.Resources$Theme;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;

public class ScreenRecordDrawable extends DrawableWrapper
{
    private Drawable mFillDrawable;
    private int mHorizontalPadding;
    private float mIconRadius;
    private int mLevel;
    private Paint mPaint;
    private float mTextSize;
    
    public ScreenRecordDrawable() {
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
        final Rect bounds = this.mFillDrawable.getBounds();
        final int mLevel = this.mLevel;
        if (mLevel > 0) {
            final String value = String.valueOf(mLevel);
            final Rect rect = new Rect();
            this.mPaint.getTextBounds(value, 0, value.length(), rect);
            canvas.drawText(value, (float)bounds.centerX(), bounds.centerY() + (float)(rect.height() / 4), this.mPaint);
        }
        else {
            final float n = (float)bounds.centerX();
            final float n2 = (float)bounds.centerY();
            final float mIconRadius = this.mIconRadius;
            canvas.drawCircle(n, n2 - mIconRadius / 2.0f, mIconRadius, this.mPaint);
        }
    }
    
    public boolean getPadding(final Rect rect) {
        final int left = rect.left;
        final int mHorizontalPadding = this.mHorizontalPadding;
        rect.left = left + mHorizontalPadding;
        rect.right += mHorizontalPadding;
        rect.top = 0;
        rect.bottom = 0;
        Log.d("ScreenRecordDrawable", "set zero top/bottom pad");
        return true;
    }
    
    public void inflate(final Resources resources, final XmlPullParser xmlPullParser, final AttributeSet set, final Resources$Theme resources$Theme) throws XmlPullParserException, IOException {
        super.inflate(resources, xmlPullParser, set, resources$Theme);
        this.setDrawable(resources.getDrawable(R$drawable.ic_screen_record_background, resources$Theme).mutate());
        this.mFillDrawable = resources.getDrawable(R$drawable.ic_screen_record_background, resources$Theme).mutate();
        this.mHorizontalPadding = resources.getDimensionPixelSize(R$dimen.status_bar_horizontal_padding);
        this.mTextSize = (float)resources.getDimensionPixelSize(R$dimen.screenrecord_status_text_size);
        this.mIconRadius = (float)resources.getDimensionPixelSize(R$dimen.screenrecord_status_icon_radius);
        this.mLevel = set.getAttributeIntValue((String)null, "level", 0);
        (this.mPaint = new Paint()).setTextAlign(Paint$Align.CENTER);
        this.mPaint.setColor(-1);
        this.mPaint.setTextSize(this.mTextSize);
        this.mPaint.setFakeBoldText(true);
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
