// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.notifcollection;

import android.service.notification.NotificationListenerService$RankingMap;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotifCollectionListener
{
    default void onEntryAdded(final NotificationEntry notificationEntry) {
    }
    
    default void onEntryCleanUp(final NotificationEntry notificationEntry) {
    }
    
    default void onEntryInit(final NotificationEntry notificationEntry) {
    }
    
    default void onEntryRemoved(final NotificationEntry notificationEntry, final int n) {
    }
    
    default void onEntryUpdated(final NotificationEntry notificationEntry) {
    }
    
    default void onRankingApplied() {
    }
    
    default void onRankingUpdate(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
    }
}
