// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.util.AttributeSet;
import android.content.Context;
import android.view.View;

public class AlphaOptimizedView extends View
{
    public AlphaOptimizedView(final Context context) {
        super(context);
    }
    
    public AlphaOptimizedView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public AlphaOptimizedView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public AlphaOptimizedView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
}
