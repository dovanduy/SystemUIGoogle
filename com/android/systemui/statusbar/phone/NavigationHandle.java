// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.drawable.Drawable;
import android.animation.ArgbEvaluator;
import android.graphics.Canvas;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;
import com.android.settingslib.Utils;
import com.android.systemui.R$attr;
import com.android.systemui.R$dimen;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Paint;
import android.view.View;

public class NavigationHandle extends View implements ButtonInterface
{
    protected final int mBottom;
    private final int mDarkColor;
    private final int mLightColor;
    protected final Paint mPaint;
    protected final int mRadius;
    private boolean mRequiresInvalidate;
    
    public NavigationHandle(final Context context) {
        this(context, null);
    }
    
    public NavigationHandle(final Context context, final AttributeSet set) {
        super(context, set);
        this.mPaint = new Paint();
        final Resources resources = context.getResources();
        this.mRadius = resources.getDimensionPixelSize(R$dimen.navigation_handle_radius);
        this.mBottom = resources.getDimensionPixelSize(R$dimen.navigation_handle_bottom);
        final int themeAttr = Utils.getThemeAttr(context, R$attr.darkIconTheme);
        final ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, Utils.getThemeAttr(context, R$attr.lightIconTheme));
        final ContextThemeWrapper contextThemeWrapper2 = new ContextThemeWrapper(context, themeAttr);
        this.mLightColor = Utils.getColorAttrDefaultColor((Context)contextThemeWrapper, R$attr.homeHandleColor);
        this.mDarkColor = Utils.getColorAttrDefaultColor((Context)contextThemeWrapper2, R$attr.homeHandleColor);
        this.mPaint.setAntiAlias(true);
        this.setFocusable(false);
    }
    
    public void abortCurrentGesture() {
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        final int height = this.getHeight();
        final int n = this.mRadius * 2;
        final int width = this.getWidth();
        final int n2 = height - this.mBottom - n;
        final float n3 = (float)n2;
        final float n4 = (float)width;
        final float n5 = (float)(n2 + n);
        final int mRadius = this.mRadius;
        canvas.drawRoundRect(0.0f, n3, n4, n5, (float)mRadius, (float)mRadius, this.mPaint);
    }
    
    public void setAlpha(final float alpha) {
        super.setAlpha(alpha);
        if (alpha > 0.0f && this.mRequiresInvalidate) {
            this.mRequiresInvalidate = false;
            this.invalidate();
        }
    }
    
    public void setDarkIntensity(final float n) {
        final int intValue = (int)ArgbEvaluator.getInstance().evaluate(n, (Object)this.mLightColor, (Object)this.mDarkColor);
        if (this.mPaint.getColor() != intValue) {
            this.mPaint.setColor(intValue);
            if (this.getVisibility() == 0 && this.getAlpha() > 0.0f) {
                this.invalidate();
            }
            else {
                this.mRequiresInvalidate = true;
            }
        }
    }
    
    public void setDelayTouchFeedback(final boolean b) {
    }
    
    public void setImageDrawable(final Drawable drawable) {
    }
    
    public void setVertical(final boolean b) {
    }
}
