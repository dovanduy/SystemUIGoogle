// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.globalactions;

import android.view.View;
import com.android.systemui.R$dimen;
import com.android.internal.annotations.VisibleForTesting;
import android.util.AttributeSet;
import android.content.Context;

public class GlobalActionsColumnLayout extends GlobalActionsLayout
{
    private boolean mLastSnap;
    
    public GlobalActionsColumnLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    private void updateChildOrdering() {
        if (this.shouldReverseListItems()) {
            this.getListView().bringToFront();
        }
        else {
            this.getSeparatedView().bringToFront();
        }
    }
    
    @VisibleForTesting
    protected void centerAlongEdge() {
        final int currentRotation = this.getCurrentRotation();
        if (currentRotation != 1) {
            if (currentRotation != 2) {
                this.setPadding(0, 0, 0, 0);
                this.setGravity(21);
            }
            else {
                this.setPadding(0, 0, 0, 0);
                this.setGravity(81);
            }
        }
        else {
            this.setPadding(0, 0, 0, 0);
            this.setGravity(49);
        }
    }
    
    @VisibleForTesting
    protected float getAnimationDistance() {
        return this.getGridItemSize() / 2.0f;
    }
    
    @Override
    public float getAnimationOffsetX() {
        if (this.getCurrentRotation() == 0) {
            return this.getAnimationDistance();
        }
        return 0.0f;
    }
    
    @Override
    public float getAnimationOffsetY() {
        final int currentRotation = this.getCurrentRotation();
        if (currentRotation == 1) {
            return -this.getAnimationDistance();
        }
        if (currentRotation != 2) {
            return 0.0f;
        }
        return this.getAnimationDistance();
    }
    
    @VisibleForTesting
    protected float getGridItemSize() {
        return this.getContext().getResources().getDimension(R$dimen.global_actions_grid_item_height);
    }
    
    @VisibleForTesting
    protected int getPowerButtonOffsetDistance() {
        return Math.round(this.getContext().getResources().getDimension(R$dimen.global_actions_top_padding));
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.post((Runnable)new _$$Lambda$GlobalActionsColumnLayout$iug9piEk_yt27o1Db7MoL30coo4(this));
    }
    
    @Override
    protected void onMeasure(final int n, final int n2) {
        super.onMeasure(n, n2);
    }
    
    @Override
    public void onUpdateList() {
        super.onUpdateList();
        this.updateChildOrdering();
    }
    
    @VisibleForTesting
    @Override
    protected boolean shouldReverseListItems() {
        final int currentRotation = this.getCurrentRotation();
        final boolean b = false;
        boolean b2 = false;
        if (currentRotation == 0) {
            return false;
        }
        if (this.getCurrentLayoutDirection() == 1) {
            if (currentRotation == 1) {
                b2 = true;
            }
            return b2;
        }
        boolean b3 = b;
        if (currentRotation == 2) {
            b3 = true;
        }
        return b3;
    }
    
    @VisibleForTesting
    protected boolean shouldSnapToPowerButton() {
        final int powerButtonOffsetDistance = this.getPowerButtonOffsetDistance();
        final View wrapper = this.getWrapper();
        int n;
        int n2;
        if (this.getCurrentRotation() == 0) {
            n = wrapper.getMeasuredHeight();
            n2 = this.getMeasuredHeight();
        }
        else {
            n = wrapper.getMeasuredWidth();
            n2 = this.getMeasuredWidth();
        }
        return n + powerButtonOffsetDistance < n2;
    }
    
    @VisibleForTesting
    protected void snapToPowerButton() {
        final int powerButtonOffsetDistance = this.getPowerButtonOffsetDistance();
        final int currentRotation = this.getCurrentRotation();
        if (currentRotation != 1) {
            if (currentRotation != 2) {
                this.setPadding(0, powerButtonOffsetDistance, 0, 0);
                this.setGravity(53);
            }
            else {
                this.setPadding(0, 0, powerButtonOffsetDistance, 0);
                this.setGravity(85);
            }
        }
        else {
            this.setPadding(powerButtonOffsetDistance, 0, 0, 0);
            this.setGravity(51);
        }
    }
    
    @VisibleForTesting
    protected void updateSnap() {
        final boolean shouldSnapToPowerButton = this.shouldSnapToPowerButton();
        if (shouldSnapToPowerButton != this.mLastSnap) {
            if (shouldSnapToPowerButton) {
                this.snapToPowerButton();
            }
            else {
                this.centerAlongEdge();
            }
        }
        this.mLastSnap = shouldSnapToPowerButton;
    }
}
