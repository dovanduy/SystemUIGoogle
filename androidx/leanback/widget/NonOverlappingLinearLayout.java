// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import java.util.ArrayList;
import android.widget.LinearLayout;

public class NonOverlappingLinearLayout extends LinearLayout
{
    boolean mDeferFocusableViewAvailableInLayout;
    boolean mFocusableViewAvailableFixEnabled;
    final ArrayList<ArrayList<View>> mSortedAvailableViews;
    
    public NonOverlappingLinearLayout(final Context context) {
        this(context, null);
    }
    
    public NonOverlappingLinearLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public NonOverlappingLinearLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mFocusableViewAvailableFixEnabled = false;
        this.mSortedAvailableViews = new ArrayList<ArrayList<View>>();
    }
    
    public void focusableViewAvailable(final View e) {
        if (this.mDeferFocusableViewAvailableInLayout) {
            View view = e;
            while (true) {
                while (view != this && view != null) {
                    if (view.getParent() == this) {
                        final int indexOfChild = this.indexOfChild(view);
                        if (indexOfChild != -1) {
                            this.mSortedAvailableViews.get(indexOfChild).add(e);
                        }
                        return;
                    }
                    else {
                        view = (View)view.getParent();
                    }
                }
                final int indexOfChild = -1;
                continue;
            }
        }
        super.focusableViewAvailable(e);
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void onLayout(final boolean b, int i, int j, final int n, final int n2) {
        final int n3 = 0;
        try {
            final boolean mDeferFocusableViewAvailableInLayout = this.mFocusableViewAvailableFixEnabled && this.getOrientation() == 0 && this.getLayoutDirection() == 1;
            this.mDeferFocusableViewAvailableInLayout = mDeferFocusableViewAvailableInLayout;
            if (mDeferFocusableViewAvailableInLayout) {
                while (this.mSortedAvailableViews.size() > this.getChildCount()) {
                    this.mSortedAvailableViews.remove(this.mSortedAvailableViews.size() - 1);
                }
                while (this.mSortedAvailableViews.size() < this.getChildCount()) {
                    this.mSortedAvailableViews.add(new ArrayList<View>());
                }
            }
            super.onLayout(b, i, j, n, n2);
            if (this.mDeferFocusableViewAvailableInLayout) {
                for (i = 0; i < this.mSortedAvailableViews.size(); ++i) {
                    for (j = 0; j < this.mSortedAvailableViews.get(i).size(); ++j) {
                        super.focusableViewAvailable((View)this.mSortedAvailableViews.get(i).get(j));
                    }
                }
            }
        }
        finally {
            if (this.mDeferFocusableViewAvailableInLayout) {
                this.mDeferFocusableViewAvailableInLayout = false;
                for (i = n3; i < this.mSortedAvailableViews.size(); ++i) {
                    this.mSortedAvailableViews.get(i).clear();
                }
            }
        }
    }
}
