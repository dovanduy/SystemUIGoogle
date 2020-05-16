// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.View$OnHoverListener;
import android.view.View$OnClickListener;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import android.view.View;

interface RotationButton
{
    default boolean acceptRotationProposal() {
        return this.getCurrentView() != null;
    }
    
    View getCurrentView();
    
    KeyButtonDrawable getImageDrawable();
    
    boolean hide();
    
    boolean isVisible();
    
    default void setCanShowRotationButton(final boolean b) {
    }
    
    void setDarkIntensity(final float p0);
    
    void setOnClickListener(final View$OnClickListener p0);
    
    void setOnHoverListener(final View$OnHoverListener p0);
    
    void setRotationButtonController(final RotationButtonController p0);
    
    boolean show();
    
    void updateIcon();
}
