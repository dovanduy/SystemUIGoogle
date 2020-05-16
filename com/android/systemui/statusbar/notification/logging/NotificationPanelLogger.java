// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.logging;

import com.android.internal.logging.UiEventLogger$UiEventEnum;
import android.service.notification.StatusBarNotification;
import java.util.Iterator;
import com.android.systemui.statusbar.notification.logging.nano.Notifications$Notification;
import com.android.systemui.statusbar.notification.logging.nano.Notifications$NotificationList;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.List;

public interface NotificationPanelLogger
{
    default Notifications$NotificationList toNotificationProto(final List<NotificationEntry> list) {
        final Notifications$NotificationList list2 = new Notifications$NotificationList();
        if (list == null) {
            return list2;
        }
        final Notifications$Notification[] notifications = new Notifications$Notification[list.size()];
        int n = 0;
        for (final NotificationEntry notificationEntry : list) {
            final StatusBarNotification sbn = notificationEntry.getSbn();
            if (sbn != null) {
                final Notifications$Notification notifications$Notification = new Notifications$Notification();
                notifications$Notification.uid = sbn.getUid();
                notifications$Notification.packageName = sbn.getPackageName();
                if (sbn.getInstanceId() != null) {
                    notifications$Notification.instanceId = sbn.getInstanceId().getId();
                }
                if (sbn.getNotification() != null) {
                    notifications$Notification.isGroupSummary = sbn.getNotification().isGroupSummary();
                }
                notifications$Notification.section = notificationEntry.getBucket() + 1;
                notifications[n] = notifications$Notification;
            }
            ++n;
        }
        list2.notifications = notifications;
        return list2;
    }
    
    void logPanelShown(final boolean p0, final List<NotificationEntry> p1);
    
    public enum NotificationPanelEvent implements UiEventLogger$UiEventEnum
    {
        NOTIFICATION_PANEL_OPEN_LOCKSCREEN(201), 
        NOTIFICATION_PANEL_OPEN_STATUS_BAR(200);
        
        private final int mId;
        
        private NotificationPanelEvent(final int mId) {
            this.mId = mId;
        }
        
        public static NotificationPanelEvent fromLockscreen(final boolean b) {
            NotificationPanelEvent notificationPanelEvent;
            if (b) {
                notificationPanelEvent = NotificationPanelEvent.NOTIFICATION_PANEL_OPEN_LOCKSCREEN;
            }
            else {
                notificationPanelEvent = NotificationPanelEvent.NOTIFICATION_PANEL_OPEN_STATUS_BAR;
            }
            return notificationPanelEvent;
        }
        
        public int getId() {
            return this.mId;
        }
    }
}
