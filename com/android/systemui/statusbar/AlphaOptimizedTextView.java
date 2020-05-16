// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;

public class AlphaOptimizedTextView extends TextView
{
    public AlphaOptimizedTextView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
}
