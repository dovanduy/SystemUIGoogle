// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.util.AttributeSet;
import android.content.Context;
import android.view.View;

class NonOverlappingView extends View
{
    public NonOverlappingView(final Context context) {
        this(context, null);
    }
    
    public NonOverlappingView(final Context context, final AttributeSet set) {
        super(context, set, 0);
    }
    
    public NonOverlappingView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
}
