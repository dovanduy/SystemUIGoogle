// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.view.View$OnClickListener;

public interface QSFooter
{
    default void disable(final int n, final int n2, final boolean b) {
    }
    
    int getHeight();
    
    void setExpandClickListener(final View$OnClickListener p0);
    
    void setExpanded(final boolean p0);
    
    void setExpansion(final float p0);
    
    void setKeyguardShowing(final boolean p0);
    
    void setListening(final boolean p0);
    
    void setQSPanel(final QSPanel p0);
    
    void setVisibility(final int p0);
}
