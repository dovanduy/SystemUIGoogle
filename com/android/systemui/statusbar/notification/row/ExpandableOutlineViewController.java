// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

public class ExpandableOutlineViewController
{
    private final ExpandableViewController mExpandableViewController;
    
    public ExpandableOutlineViewController(final ExpandableOutlineView expandableOutlineView, final ExpandableViewController mExpandableViewController) {
        this.mExpandableViewController = mExpandableViewController;
    }
    
    public void init() {
        this.mExpandableViewController.init();
    }
}
