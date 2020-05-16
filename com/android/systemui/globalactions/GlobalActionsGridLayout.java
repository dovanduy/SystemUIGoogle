// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.globalactions;

import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R$dimen;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;

public class GlobalActionsGridLayout extends GlobalActionsLayout
{
    public GlobalActionsGridLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    @Override
    protected void addToListView(final View view, final boolean b) {
        final ListGridLayout listView = this.getListView();
        if (listView != null) {
            listView.addItem(view);
        }
    }
    
    @VisibleForTesting
    protected float getAnimationDistance() {
        return this.getListView().getRowCount() * this.getContext().getResources().getDimension(R$dimen.global_actions_grid_item_height) / 2.0f;
    }
    
    @Override
    public float getAnimationOffsetX() {
        final int currentRotation = this.getCurrentRotation();
        if (currentRotation == 1) {
            return this.getAnimationDistance();
        }
        if (currentRotation != 2) {
            return 0.0f;
        }
        return -this.getAnimationDistance();
    }
    
    @Override
    public float getAnimationOffsetY() {
        if (this.getCurrentRotation() == 0) {
            return this.getAnimationDistance();
        }
        return 0.0f;
    }
    
    protected ListGridLayout getListView() {
        return (ListGridLayout)super.getListView();
    }
    
    @Override
    public void onUpdateList() {
        this.setupListView();
        super.onUpdateList();
        this.updateSeparatedItemSize();
    }
    
    public void removeAllItems() {
        final ViewGroup separatedView = this.getSeparatedView();
        final ListGridLayout listView = this.getListView();
        if (separatedView != null) {
            separatedView.removeAllViews();
        }
        if (listView != null) {
            listView.removeAllItems();
        }
    }
    
    @Override
    protected void removeAllListViews() {
        final ListGridLayout listView = this.getListView();
        if (listView != null) {
            listView.removeAllItems();
        }
    }
    
    @VisibleForTesting
    protected void setupListView() {
        final ListGridLayout listView = this.getListView();
        listView.setExpectedCount(super.mAdapter.countListItems());
        listView.setReverseSublists(this.shouldReverseSublists());
        listView.setReverseItems(this.shouldReverseListItems());
        listView.setSwapRowsAndColumns(this.shouldSwapRowsAndColumns());
    }
    
    @Override
    protected boolean shouldReverseListItems() {
        final int currentRotation = this.getCurrentRotation();
        boolean b = currentRotation == 0 || currentRotation == 2;
        if (this.getCurrentLayoutDirection() == 1) {
            b ^= true;
        }
        return b;
    }
    
    @VisibleForTesting
    protected boolean shouldReverseSublists() {
        return this.getCurrentRotation() == 2;
    }
    
    @VisibleForTesting
    protected boolean shouldSwapRowsAndColumns() {
        return this.getCurrentRotation() != 0;
    }
    
    @VisibleForTesting
    protected void updateSeparatedItemSize() {
        final ViewGroup separatedView = this.getSeparatedView();
        if (separatedView.getChildCount() == 0) {
            return;
        }
        final ViewGroup$LayoutParams layoutParams = separatedView.getChildAt(0).getLayoutParams();
        if (separatedView.getChildCount() == 1) {
            layoutParams.width = -1;
            layoutParams.height = -1;
        }
        else {
            layoutParams.width = -2;
            layoutParams.height = -2;
        }
    }
}
