// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.util.AttributeSet;
import android.content.Context;
import android.widget.Button;

public class AlphaOptimizedButton extends Button
{
    public AlphaOptimizedButton(final Context context) {
        super(context);
    }
    
    public AlphaOptimizedButton(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public AlphaOptimizedButton(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public AlphaOptimizedButton(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
}
