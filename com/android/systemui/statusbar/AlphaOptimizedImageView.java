// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.util.AttributeSet;
import android.content.Context;
import android.widget.ImageView;

public class AlphaOptimizedImageView extends ImageView
{
    public AlphaOptimizedImageView(final Context context) {
        this(context, null);
    }
    
    public AlphaOptimizedImageView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public AlphaOptimizedImageView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public AlphaOptimizedImageView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
}
