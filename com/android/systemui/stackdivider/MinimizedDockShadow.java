// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.stackdivider;

import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.LinearGradient;
import android.graphics.Shader$TileMode;
import android.graphics.Color;
import android.content.res.Resources$Theme;
import com.android.systemui.R$color;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Paint;
import android.view.View;

public class MinimizedDockShadow extends View
{
    private int mDockSide;
    private final Paint mShadowPaint;
    
    public MinimizedDockShadow(final Context context, final AttributeSet set) {
        super(context, set);
        this.mShadowPaint = new Paint();
        this.mDockSide = -1;
    }
    
    private void updatePaint(final int n, final int n2, final int n3, final int n4) {
        final int color = super.mContext.getResources().getColor(R$color.minimize_dock_shadow_start, (Resources$Theme)null);
        final int color2 = super.mContext.getResources().getColor(R$color.minimize_dock_shadow_end, (Resources$Theme)null);
        final int argb = Color.argb((Color.alpha(color) + Color.alpha(color2)) / 2, 0, 0, 0);
        final int argb2 = Color.argb((int)(Color.alpha(color) * 0.25f + Color.alpha(color2) * 0.75f), 0, 0, 0);
        final int mDockSide = this.mDockSide;
        if (mDockSide == 2) {
            this.mShadowPaint.setShader((Shader)new LinearGradient(0.0f, 0.0f, 0.0f, (float)(n4 - n2), new int[] { color, argb, argb2, color2 }, new float[] { 0.0f, 0.35f, 0.6f, 1.0f }, Shader$TileMode.CLAMP));
        }
        else if (mDockSide == 1) {
            this.mShadowPaint.setShader((Shader)new LinearGradient(0.0f, 0.0f, (float)(n3 - n), 0.0f, new int[] { color, argb, argb2, color2 }, new float[] { 0.0f, 0.35f, 0.6f, 1.0f }, Shader$TileMode.CLAMP));
        }
        else if (mDockSide == 3) {
            this.mShadowPaint.setShader((Shader)new LinearGradient((float)(n3 - n), 0.0f, 0.0f, 0.0f, new int[] { color, argb, argb2, color2 }, new float[] { 0.0f, 0.35f, 0.6f, 1.0f }, Shader$TileMode.CLAMP));
        }
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void onDraw(final Canvas canvas) {
        canvas.drawRect(0.0f, 0.0f, (float)this.getWidth(), (float)this.getHeight(), this.mShadowPaint);
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        if (b) {
            this.updatePaint(n, n2, n3, n4);
            this.invalidate();
        }
    }
    
    public void setDockSide(final int mDockSide) {
        if (mDockSide != this.mDockSide) {
            this.mDockSide = mDockSide;
            this.updatePaint(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
            this.invalidate();
        }
    }
}
