// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.util.AttributeSet;
import android.content.Context;
import android.widget.RelativeLayout;

public class AlphaOptimizedRelativeLayout extends RelativeLayout
{
    public AlphaOptimizedRelativeLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
}
