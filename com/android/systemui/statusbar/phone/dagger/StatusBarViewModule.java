// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone.dagger;

import com.android.systemui.statusbar.phone.NotificationPanelView;
import com.android.systemui.statusbar.phone.NotificationShadeWindowView;

public abstract class StatusBarViewModule
{
    public static NotificationPanelView getNotificationPanelView(final NotificationShadeWindowView notificationShadeWindowView) {
        return notificationShadeWindowView.getNotificationPanelView();
    }
}
