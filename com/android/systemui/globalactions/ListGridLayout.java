// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.globalactions;

import com.android.internal.annotations.VisibleForTesting;
import android.view.ViewGroup;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public class ListGridLayout extends LinearLayout
{
    private final int[][] mConfigs;
    private int mCurrentCount;
    private int mExpectedCount;
    private boolean mReverseItems;
    private boolean mReverseSublists;
    private boolean mSwapRowsAndColumns;
    
    public ListGridLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.mCurrentCount = 0;
        this.mConfigs = new int[][] { { 0, 0 }, { 1, 1 }, { 1, 2 }, { 1, 3 }, { 2, 2 }, { 2, 3 }, { 2, 3 }, { 3, 3 }, { 3, 3 }, { 3, 3 } };
    }
    
    private int[] getConfig() {
        if (this.mExpectedCount < 0) {
            return this.mConfigs[0];
        }
        return this.mConfigs[Math.min(this.getMaxElementCount(), this.mExpectedCount)];
    }
    
    private int getMaxElementCount() {
        return this.mConfigs.length - 1;
    }
    
    private int getParentViewIndex(int n, final boolean b, final boolean b2) {
        final int rowCount = this.getRowCount();
        if (b2) {
            n = (int)Math.floor(n / rowCount);
        }
        else {
            n %= rowCount;
        }
        int reverseSublistIndex = n;
        if (b) {
            reverseSublistIndex = this.reverseSublistIndex(n);
        }
        return reverseSublistIndex;
    }
    
    private int reverseSublistIndex(final int n) {
        return this.getChildCount() - (n + 1);
    }
    
    public void addItem(final View view) {
        final ViewGroup parentView = this.getParentView(this.mCurrentCount, this.mReverseSublists, this.mSwapRowsAndColumns);
        if (this.mReverseItems) {
            parentView.addView(view, 0);
        }
        else {
            parentView.addView(view);
        }
        parentView.setVisibility(0);
        ++this.mCurrentCount;
    }
    
    @VisibleForTesting
    protected ViewGroup getParentView(final int a, final boolean b, final boolean b2) {
        if (this.getRowCount() != 0 && a >= 0) {
            return this.getSublist(this.getParentViewIndex(Math.min(a, this.getMaxElementCount() - 1), b, b2));
        }
        return null;
    }
    
    public int getRowCount() {
        return this.getConfig()[0];
    }
    
    @VisibleForTesting
    protected ViewGroup getSublist(final int n) {
        return (ViewGroup)this.getChildAt(n);
    }
    
    public void removeAllItems() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final ViewGroup sublist = this.getSublist(i);
            if (sublist != null) {
                sublist.removeAllViews();
                sublist.setVisibility(8);
            }
        }
        this.mCurrentCount = 0;
    }
    
    public void setExpectedCount(final int mExpectedCount) {
        this.mExpectedCount = mExpectedCount;
    }
    
    public void setReverseItems(final boolean mReverseItems) {
        this.mReverseItems = mReverseItems;
    }
    
    public void setReverseSublists(final boolean mReverseSublists) {
        this.mReverseSublists = mReverseSublists;
    }
    
    public void setSwapRowsAndColumns(final boolean mSwapRowsAndColumns) {
        this.mSwapRowsAndColumns = mSwapRowsAndColumns;
    }
}
