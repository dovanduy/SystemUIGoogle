// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface OnHeadsUpChangedListener
{
    default void onHeadsUpPinned(final NotificationEntry notificationEntry) {
    }
    
    default void onHeadsUpPinnedModeChanged(final boolean b) {
    }
    
    default void onHeadsUpStateChanged(final NotificationEntry notificationEntry, final boolean b) {
    }
    
    default void onHeadsUpUnPinned(final NotificationEntry notificationEntry) {
    }
}
