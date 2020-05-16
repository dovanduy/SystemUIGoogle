// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.dagger;

import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationViewController;

public interface NotificationRowComponent
{
    ActivatableNotificationViewController getActivatableNotificationViewController();
    
    public interface Builder
    {
        Builder activatableNotificationView(final ActivatableNotificationView p0);
        
        NotificationRowComponent build();
    }
}
