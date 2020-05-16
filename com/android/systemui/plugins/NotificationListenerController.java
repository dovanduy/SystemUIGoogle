// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.service.notification.NotificationListenerService$RankingMap;
import android.service.notification.StatusBarNotification;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;

@DependsOn(target = NotificationProvider.class)
@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_NOTIFICATION_ASSISTANT", version = 1)
public interface NotificationListenerController extends Plugin
{
    public static final String ACTION = "com.android.systemui.action.PLUGIN_NOTIFICATION_ASSISTANT";
    public static final int VERSION = 1;
    
    default StatusBarNotification[] getActiveNotifications(final StatusBarNotification[] array) {
        return array;
    }
    
    default NotificationListenerService$RankingMap getCurrentRanking(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        return notificationListenerService$RankingMap;
    }
    
    void onListenerConnected(final NotificationProvider p0);
    
    default boolean onNotificationPosted(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        return false;
    }
    
    default boolean onNotificationRemoved(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        return false;
    }
    
    @ProvidesInterface(version = 1)
    public interface NotificationProvider
    {
        public static final int VERSION = 1;
        
        void addNotification(final StatusBarNotification p0);
        
        StatusBarNotification[] getActiveNotifications();
        
        NotificationListenerService$RankingMap getRankingMap();
        
        void removeNotification(final StatusBarNotification p0);
        
        void updateRanking();
    }
}
