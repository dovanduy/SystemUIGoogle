// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.view.View;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.internal.annotations.VisibleForTesting;
import android.view.ViewGroup;

public class AboveShelfObserver implements AboveShelfChangedListener
{
    private boolean mHasViewsAboveShelf;
    private final ViewGroup mHostLayout;
    private HasViewAboveShelfChangedListener mListener;
    
    public AboveShelfObserver(final ViewGroup mHostLayout) {
        this.mHasViewsAboveShelf = false;
        this.mHostLayout = mHostLayout;
    }
    
    @VisibleForTesting
    boolean hasViewsAboveShelf() {
        return this.mHasViewsAboveShelf;
    }
    
    @Override
    public void onAboveShelfStateChanged(final boolean b) {
        boolean mHasViewsAboveShelf = b;
        if (!b) {
            final ViewGroup mHostLayout = this.mHostLayout;
            mHasViewsAboveShelf = b;
            if (mHostLayout != null) {
                final int childCount = mHostLayout.getChildCount();
                int n = 0;
                while (true) {
                    mHasViewsAboveShelf = b;
                    if (n >= childCount) {
                        break;
                    }
                    final View child = this.mHostLayout.getChildAt(n);
                    if (child instanceof ExpandableNotificationRow && ((ExpandableNotificationRow)child).isAboveShelf()) {
                        mHasViewsAboveShelf = true;
                        break;
                    }
                    ++n;
                }
            }
        }
        if (this.mHasViewsAboveShelf != mHasViewsAboveShelf) {
            this.mHasViewsAboveShelf = mHasViewsAboveShelf;
            final HasViewAboveShelfChangedListener mListener = this.mListener;
            if (mListener != null) {
                mListener.onHasViewsAboveShelfChanged(mHasViewsAboveShelf);
            }
        }
    }
    
    public void setListener(final HasViewAboveShelfChangedListener mListener) {
        this.mListener = mListener;
    }
    
    public interface HasViewAboveShelfChangedListener
    {
        void onHasViewsAboveShelfChanged(final boolean p0);
    }
}
