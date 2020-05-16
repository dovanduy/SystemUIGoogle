// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.globalactions;

import com.android.systemui.R$dimen;
import com.android.systemui.HardwareBgDrawable;
import com.android.internal.annotations.VisibleForTesting;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;

public class GlobalActionsFlatLayout extends GlobalActionsLayout
{
    public GlobalActionsFlatLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    @Override
    protected void addToListView(final View view, final boolean b) {
        if (this.getListView().getChildCount() < 4) {
            super.addToListView(view, b);
        }
    }
    
    @VisibleForTesting
    protected float getAnimationDistance() {
        return this.getGridItemSize() / 2.0f;
    }
    
    @Override
    public float getAnimationOffsetX() {
        return 0.0f;
    }
    
    @Override
    public float getAnimationOffsetY() {
        return -this.getAnimationDistance();
    }
    
    @Override
    protected HardwareBgDrawable getBackgroundDrawable(final int n) {
        return null;
    }
    
    @VisibleForTesting
    protected float getGridItemSize() {
        return this.getContext().getResources().getDimension(R$dimen.global_actions_grid_item_height);
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
}
