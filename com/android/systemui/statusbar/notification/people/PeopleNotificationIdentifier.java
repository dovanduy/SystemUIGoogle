// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.people;

import android.service.notification.NotificationListenerService$Ranking;
import android.service.notification.StatusBarNotification;

public interface PeopleNotificationIdentifier
{
    default static {
        final Companion $$INSTANCE = Companion.$$INSTANCE;
    }
    
    int getPeopleNotificationType(final StatusBarNotification p0, final NotificationListenerService$Ranking p1);
    
    public static final class Companion
    {
        static final /* synthetic */ Companion $$INSTANCE;
        
        static {
            $$INSTANCE = new Companion();
        }
        
        private Companion() {
        }
    }
}
