// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.graphics.ColorFilter;
import android.view.View;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.RadialGradient;
import android.graphics.Shader$TileMode;
import android.graphics.Color;
import com.android.systemui.R$color;
import android.content.Context;
import android.graphics.Paint;
import android.view.View$OnLayoutChangeListener;
import android.graphics.drawable.Drawable;

public class KeyguardUserSwitcherScrim extends Drawable implements View$OnLayoutChangeListener
{
    private int mAlpha;
    private int mDarkColor;
    private int mLayoutWidth;
    private Paint mRadialGradientPaint;
    private int mTop;
    
    public KeyguardUserSwitcherScrim(final Context context) {
        this.mAlpha = 255;
        this.mRadialGradientPaint = new Paint();
        this.mDarkColor = context.getColor(R$color.keyguard_user_switcher_background_gradient_color);
    }
    
    private void updatePaint() {
        final int mLayoutWidth = this.mLayoutWidth;
        if (mLayoutWidth == 0) {
            return;
        }
        final float n = mLayoutWidth * 2.5f;
        final boolean b = this.getLayoutDirection() == 0;
        final Paint mRadialGradientPaint = this.mRadialGradientPaint;
        float n2;
        if (b) {
            n2 = (float)this.mLayoutWidth;
        }
        else {
            n2 = 0.0f;
        }
        mRadialGradientPaint.setShader((Shader)new RadialGradient(n2, 0.0f, n, new int[] { Color.argb((int)(Color.alpha(this.mDarkColor) * this.mAlpha / 255.0f), 0, 0, 0), 0 }, new float[] { Math.max(0.0f, this.mLayoutWidth * 0.75f / n), 1.0f }, Shader$TileMode.CLAMP));
    }
    
    public void draw(final Canvas canvas) {
        final boolean b = this.getLayoutDirection() == 0;
        final Rect bounds = this.getBounds();
        final float n = bounds.width() * 2.5f;
        final float n2 = (float)(this.mTop + bounds.height());
        canvas.translate(0.0f, (float)(-this.mTop));
        canvas.scale(1.0f, n2 * 2.5f / n);
        float n3;
        if (b) {
            n3 = bounds.right - n;
        }
        else {
            n3 = 0.0f;
        }
        float n4;
        if (b) {
            n4 = (float)bounds.right;
        }
        else {
            n4 = bounds.left + n;
        }
        canvas.drawRect(n3, 0.0f, n4, n, this.mRadialGradientPaint);
    }
    
    public int getAlpha() {
        return this.mAlpha;
    }
    
    public int getOpacity() {
        return -3;
    }
    
    public void onLayoutChange(final View view, final int n, final int mTop, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        if (n != n4 || mTop != n5 || n2 != n6 || n3 != n7) {
            this.mLayoutWidth = n2 - n;
            this.mTop = mTop;
            this.updatePaint();
        }
    }
    
    public void setAlpha(final int mAlpha) {
        this.mAlpha = mAlpha;
        this.updatePaint();
        this.invalidateSelf();
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
    }
}
