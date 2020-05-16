// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.app;

import android.view.ViewGroup;
import androidx.leanback.widget.Util;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

class GuidedStepRootLayout extends LinearLayout
{
    private boolean mFocusOutEnd;
    private boolean mFocusOutStart;
    
    public GuidedStepRootLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.mFocusOutStart = false;
        this.mFocusOutEnd = false;
    }
    
    public GuidedStepRootLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mFocusOutStart = false;
        this.mFocusOutEnd = false;
    }
    
    public View focusSearch(final View view, final int n) {
        final View focusSearch = super.focusSearch(view, n);
        if (n == 17 || n == 66) {
            if (Util.isDescendant((ViewGroup)this, focusSearch)) {
                return focusSearch;
            }
            Label_0060: {
                if (this.getLayoutDirection() == 0) {
                    if (n != 17) {
                        break Label_0060;
                    }
                }
                else if (n != 66) {
                    break Label_0060;
                }
                if (!this.mFocusOutStart) {
                    return view;
                }
                return focusSearch;
            }
            if (!this.mFocusOutEnd) {
                return view;
            }
        }
        return focusSearch;
    }
}
