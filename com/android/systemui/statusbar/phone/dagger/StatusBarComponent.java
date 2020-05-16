// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone.dagger;

import com.android.systemui.statusbar.phone.NotificationShadeWindowView;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowViewController;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;

public interface StatusBarComponent
{
    NotificationPanelViewController getNotificationPanelViewController();
    
    NotificationShadeWindowViewController getNotificationShadeWindowViewController();
    
    StatusBarWindowController getStatusBarWindowController();
    
    public interface Builder
    {
        StatusBarComponent build();
        
        Builder statusBarWindowView(final NotificationShadeWindowView p0);
    }
}
