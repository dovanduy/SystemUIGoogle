// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.TransformState;

public interface TransformableView
{
    TransformState getCurrentState(final int p0);
    
    void setVisible(final boolean p0);
    
    void transformFrom(final TransformableView p0);
    
    void transformFrom(final TransformableView p0, final float p1);
    
    void transformTo(final TransformableView p0, final float p1);
    
    void transformTo(final TransformableView p0, final Runnable p1);
}
