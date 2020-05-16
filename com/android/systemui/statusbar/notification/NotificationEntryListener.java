// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.service.notification.NotificationListenerService$RankingMap;
import android.service.notification.StatusBarNotification;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotificationEntryListener
{
    default void onEntryInflated(final NotificationEntry notificationEntry) {
    }
    
    default void onEntryReinflated(final NotificationEntry notificationEntry) {
    }
    
    default void onEntryRemoved(final NotificationEntry notificationEntry, final NotificationVisibility notificationVisibility, final boolean b, final int n) {
    }
    
    default void onInflationError(final StatusBarNotification statusBarNotification, final Exception ex) {
    }
    
    default void onNotificationAdded(final NotificationEntry notificationEntry) {
    }
    
    default void onNotificationRankingUpdated(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
    }
    
    default void onPendingEntryAdded(final NotificationEntry notificationEntry) {
    }
    
    default void onPostEntryUpdated(final NotificationEntry notificationEntry) {
    }
    
    default void onPreEntryUpdated(final NotificationEntry notificationEntry) {
    }
}
