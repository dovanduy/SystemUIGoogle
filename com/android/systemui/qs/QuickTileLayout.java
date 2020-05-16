// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.widget.LinearLayout$LayoutParams;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public class QuickTileLayout extends LinearLayout
{
    public QuickTileLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.setGravity(17);
    }
    
    public void addView(final View view, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        final int height = viewGroup$LayoutParams.height;
        final LinearLayout$LayoutParams linearLayout$LayoutParams = new LinearLayout$LayoutParams(height, height);
        linearLayout$LayoutParams.weight = 1.0f;
        super.addView(view, n, (ViewGroup$LayoutParams)linearLayout$LayoutParams);
    }
}
