// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.view.ViewGroup;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;

class GuidedActionItemContainer extends NonOverlappingLinearLayoutWithForeground
{
    private boolean mFocusOutAllowed;
    
    public GuidedActionItemContainer(final Context context) {
        this(context, null);
    }
    
    public GuidedActionItemContainer(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public GuidedActionItemContainer(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mFocusOutAllowed = true;
    }
    
    public View focusSearch(View focusSearch, final int n) {
        if (this.mFocusOutAllowed || !Util.isDescendant((ViewGroup)this, focusSearch)) {
            return super.focusSearch(focusSearch, n);
        }
        focusSearch = super.focusSearch(focusSearch, n);
        if (Util.isDescendant((ViewGroup)this, focusSearch)) {
            return focusSearch;
        }
        return null;
    }
}
