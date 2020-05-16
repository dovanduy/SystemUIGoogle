// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public class AlphaOptimizedLinearLayout extends LinearLayout
{
    public AlphaOptimizedLinearLayout(final Context context) {
        super(context);
    }
    
    public AlphaOptimizedLinearLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public AlphaOptimizedLinearLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public AlphaOptimizedLinearLayout(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
}
