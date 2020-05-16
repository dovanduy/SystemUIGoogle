// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.drawable.Drawable;

public interface ButtonInterface
{
    void abortCurrentGesture();
    
    void setDarkIntensity(final float p0);
    
    void setDelayTouchFeedback(final boolean p0);
    
    void setImageDrawable(final Drawable p0);
    
    void setVertical(final boolean p0);
}
