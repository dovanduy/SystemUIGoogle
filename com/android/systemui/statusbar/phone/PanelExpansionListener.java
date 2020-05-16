// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

public interface PanelExpansionListener
{
    void onPanelExpansionChanged(final float p0, final boolean p1);
    
    default void onQsExpansionChanged(final float n) {
    }
}
