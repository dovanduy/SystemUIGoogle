// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.View;

public interface ShadeController
{
    void addPostCollapseAction(final Runnable p0);
    
    void animateCollapsePanels();
    
    void animateCollapsePanels(final int p0);
    
    void animateCollapsePanels(final int p0, final boolean p1);
    
    void animateCollapsePanels(final int p0, final boolean p1, final boolean p2);
    
    void animateCollapsePanels(final int p0, final boolean p1, final boolean p2, final float p3);
    
    boolean closeShadeIfOpen();
    
    void collapsePanel(final boolean p0);
    
    boolean collapsePanel();
    
    void goToLockedShade(final View p0);
    
    void instantExpandNotificationsPanel();
    
    void postOnShadeExpanded(final Runnable p0);
    
    void runPostCollapseRunnables();
}
