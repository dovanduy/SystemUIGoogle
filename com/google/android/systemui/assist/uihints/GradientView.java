// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.LinearGradient;
import android.graphics.Shader$TileMode;
import com.android.internal.graphics.ColorUtils;
import android.util.AttributeSet;
import android.content.Context;
import android.view.animation.PathInterpolator;
import android.graphics.Paint;
import android.view.View;

public final class GradientView extends View
{
    private int mBottomColor;
    private int[] mColors;
    private final Paint mGradientPaint;
    private final PathInterpolator mInterpolator;
    private final float[] mStops;
    private int mTopColor;
    
    public GradientView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public GradientView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public GradientView(final Context context, final AttributeSet set, int i, final int n) {
        super(context, set, i, n);
        this.mInterpolator = new PathInterpolator(0.5f, 0.5f, 0.7f, 1.0f);
        this.mColors = new int[100];
        i = 0;
        this.mTopColor = 0;
        this.mBottomColor = 0;
        (this.mGradientPaint = new Paint()).setDither(true);
        this.mStops = new float[100];
        while (i < 100) {
            this.mStops[i] = i / 100.0f;
            ++i;
        }
        this.updateGradient();
    }
    
    private void updateGradient() {
        for (int i = 0; i < 100; ++i) {
            this.mColors[i] = ColorUtils.blendARGB(this.mBottomColor, this.mTopColor, this.mInterpolator.getInterpolation(this.mStops[i]));
        }
        this.mGradientPaint.setShader((Shader)new LinearGradient(0.0f, (float)this.getBottom(), 0.0f, (float)this.getTop(), this.mColors, this.mStops, Shader$TileMode.CLAMP));
        this.invalidate();
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect((float)this.getLeft(), (float)this.getTop(), (float)this.getWidth(), (float)this.getHeight(), this.mGradientPaint);
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        this.updateGradient();
    }
}
