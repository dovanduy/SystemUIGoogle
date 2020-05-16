// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;

public interface SwipeableView
{
    NotificationMenuRowPlugin createMenu();
    
    float getTranslation();
    
    boolean hasFinishedInitialization();
    
    boolean isRemoved();
    
    void resetTranslation();
    
    void setTranslation(final float p0);
}
