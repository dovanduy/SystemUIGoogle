// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.plugins.DarkIconDispatcher;

public interface StatusIconDisplayable extends DarkReceiver
{
    String getSlot();
    
    int getVisibleState();
    
    default boolean isIconBlocked() {
        return false;
    }
    
    boolean isIconVisible();
    
    void setDecorColor(final int p0);
    
    void setStaticDrawableColor(final int p0);
    
    default void setVisibleState(final int n) {
        this.setVisibleState(n, false);
    }
    
    void setVisibleState(final int p0, final boolean p1);
}
