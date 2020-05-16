// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

public interface AutoHideUiElement
{
    void hide();
    
    boolean isVisible();
    
    default boolean shouldHideOnTouch() {
        return true;
    }
    
    void synchronizeState();
}
