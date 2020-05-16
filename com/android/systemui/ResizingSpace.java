// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.view.ViewGroup$LayoutParams;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.view.View$MeasureSpec;
import android.content.res.TypedArray;
import android.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;

public class ResizingSpace extends View
{
    private final int mHeight;
    private final int mWidth;
    
    public ResizingSpace(final Context context, final AttributeSet set) {
        super(context, set);
        if (this.getVisibility() == 0) {
            this.setVisibility(4);
        }
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.ViewGroup_Layout);
        this.mWidth = obtainStyledAttributes.getResourceId(0, 0);
        this.mHeight = obtainStyledAttributes.getResourceId(1, 0);
    }
    
    private static int getDefaultSize2(int min, int size) {
        final int mode = View$MeasureSpec.getMode(size);
        size = View$MeasureSpec.getSize(size);
        if (mode != Integer.MIN_VALUE) {
            if (mode == 1073741824) {
                min = size;
            }
        }
        else {
            min = Math.min(min, size);
        }
        return min;
    }
    
    public void draw(final Canvas canvas) {
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final ViewGroup$LayoutParams layoutParams = this.getLayoutParams();
        final int mWidth = this.mWidth;
        final int n = 1;
        int n2 = 0;
        Label_0056: {
            if (mWidth > 0) {
                final int dimensionPixelOffset = this.getContext().getResources().getDimensionPixelOffset(this.mWidth);
                if (dimensionPixelOffset != layoutParams.width) {
                    layoutParams.width = dimensionPixelOffset;
                    n2 = 1;
                    break Label_0056;
                }
            }
            n2 = 0;
        }
        if (this.mHeight > 0) {
            final int dimensionPixelOffset2 = this.getContext().getResources().getDimensionPixelOffset(this.mHeight);
            if (dimensionPixelOffset2 != layoutParams.height) {
                layoutParams.height = dimensionPixelOffset2;
                n2 = n;
            }
        }
        if (n2 != 0) {
            this.setLayoutParams(layoutParams);
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        this.setMeasuredDimension(getDefaultSize2(this.getSuggestedMinimumWidth(), n), getDefaultSize2(this.getSuggestedMinimumHeight(), n2));
    }
}
