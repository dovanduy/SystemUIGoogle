// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.interruption;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotificationInterruptSuppressor
{
    default String getName() {
        return this.getClass().getName();
    }
    
    default boolean suppressAwakeHeadsUp(final NotificationEntry notificationEntry) {
        return false;
    }
    
    default boolean suppressAwakeInterruptions(final NotificationEntry notificationEntry) {
        return false;
    }
    
    default boolean suppressInterruptions(final NotificationEntry notificationEntry) {
        return false;
    }
}
