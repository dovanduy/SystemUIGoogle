// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.provider;

import android.app.Notification$MessagingStyle;
import java.util.Iterator;
import com.android.systemui.statusbar.notification.collection.GroupEntry;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;

public class HighPriorityProvider
{
    private final PeopleNotificationIdentifier mPeopleNotificationIdentifier;
    
    public HighPriorityProvider(final PeopleNotificationIdentifier mPeopleNotificationIdentifier) {
        this.mPeopleNotificationIdentifier = mPeopleNotificationIdentifier;
    }
    
    private boolean hasHighPriorityCharacteristics(final NotificationEntry notificationEntry) {
        return !this.hasUserSetImportance(notificationEntry) && (this.isImportantOngoing(notificationEntry) || notificationEntry.getSbn().getNotification().hasMediaSession() || this.isPeopleNotification(notificationEntry) || this.isMessagingStyle(notificationEntry));
    }
    
    private boolean hasHighPriorityChild(final ListEntry listEntry) {
        if (listEntry instanceof GroupEntry) {
            final Iterator<NotificationEntry> iterator = ((GroupEntry)listEntry).getChildren().iterator();
            while (iterator.hasNext()) {
                if (this.isHighPriority(iterator.next())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean hasUserSetImportance(final NotificationEntry notificationEntry) {
        return notificationEntry.getRanking().getChannel() != null && notificationEntry.getRanking().getChannel().hasUserSetImportance();
    }
    
    private boolean isImportantOngoing(final NotificationEntry notificationEntry) {
        return notificationEntry.getSbn().getNotification().isForegroundService() && notificationEntry.getRanking().getImportance() >= 2;
    }
    
    private boolean isMessagingStyle(final NotificationEntry notificationEntry) {
        return Notification$MessagingStyle.class.equals(notificationEntry.getSbn().getNotification().getNotificationStyle());
    }
    
    private boolean isPeopleNotification(final NotificationEntry notificationEntry) {
        return this.mPeopleNotificationIdentifier.getPeopleNotificationType(notificationEntry.getSbn(), notificationEntry.getRanking()) != 0;
    }
    
    public boolean isHighPriority(final ListEntry listEntry) {
        boolean b = false;
        if (listEntry == null) {
            return false;
        }
        final NotificationEntry representativeEntry = listEntry.getRepresentativeEntry();
        if (representativeEntry == null) {
            return false;
        }
        if (representativeEntry.getRanking().getImportance() >= 3 || this.hasHighPriorityCharacteristics(representativeEntry) || this.hasHighPriorityChild(listEntry)) {
            b = true;
        }
        return b;
    }
}
