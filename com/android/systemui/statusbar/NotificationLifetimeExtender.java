// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotificationLifetimeExtender
{
    void setCallback(final NotificationSafeToRemoveCallback p0);
    
    void setShouldManageLifetime(final NotificationEntry p0, final boolean p1);
    
    boolean shouldExtendLifetime(final NotificationEntry p0);
    
    default boolean shouldExtendLifetimeForPendingNotification(final NotificationEntry notificationEntry) {
        return false;
    }
    
    public interface NotificationSafeToRemoveCallback
    {
        void onSafeToRemove(final String p0);
    }
}
