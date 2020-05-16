// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.view.ViewGroup$MarginLayoutParams;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;

public class BrowseRowsFrameLayout extends FrameLayout
{
    public BrowseRowsFrameLayout(final Context context) {
        this(context, null);
    }
    
    public BrowseRowsFrameLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public BrowseRowsFrameLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    protected void measureChildWithMargins(final View view, final int n, final int n2, final int n3, final int n4) {
        final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams = (ViewGroup$MarginLayoutParams)view.getLayoutParams();
        view.measure(FrameLayout.getChildMeasureSpec(n, this.getPaddingLeft() + this.getPaddingRight() + n2, viewGroup$MarginLayoutParams.width), FrameLayout.getChildMeasureSpec(n3, this.getPaddingTop() + this.getPaddingBottom() + n4, viewGroup$MarginLayoutParams.height));
    }
}
