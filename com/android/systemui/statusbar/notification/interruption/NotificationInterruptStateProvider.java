// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.interruption;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotificationInterruptStateProvider
{
    void addSuppressor(final NotificationInterruptSuppressor p0);
    
    boolean shouldBubbleUp(final NotificationEntry p0);
    
    boolean shouldHeadsUp(final NotificationEntry p0);
    
    boolean shouldLaunchFullScreenIntentWhenAdded(final NotificationEntry p0);
}
