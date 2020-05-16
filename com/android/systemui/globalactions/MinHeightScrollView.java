// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.globalactions;

import android.view.View;
import android.view.View$MeasureSpec;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.ScrollView;

public class MinHeightScrollView extends ScrollView
{
    public MinHeightScrollView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public void onMeasure(final int n, final int n2) {
        final View child = this.getChildAt(0);
        if (child != null) {
            child.setMinimumHeight(View$MeasureSpec.getSize(n2));
        }
        super.onMeasure(n, n2);
    }
}
