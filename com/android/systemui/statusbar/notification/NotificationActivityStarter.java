// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.content.Intent;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.service.notification.StatusBarNotification;

public interface NotificationActivityStarter
{
    default boolean isCollapsingToShowActivityOverLockscreen() {
        return false;
    }
    
    void onNotificationClicked(final StatusBarNotification p0, final ExpandableNotificationRow p1);
    
    void startNotificationGutsIntent(final Intent p0, final int p1, final ExpandableNotificationRow p2);
}
