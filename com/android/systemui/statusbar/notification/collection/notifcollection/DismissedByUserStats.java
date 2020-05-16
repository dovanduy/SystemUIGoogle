// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.internal.statusbar.NotificationVisibility;

public class DismissedByUserStats
{
    public final int dismissalSentiment;
    public final int dismissalSurface;
    public final NotificationVisibility notificationVisibility;
    
    public DismissedByUserStats(final int dismissalSurface, final int dismissalSentiment, final NotificationVisibility notificationVisibility) {
        this.dismissalSurface = dismissalSurface;
        this.dismissalSentiment = dismissalSentiment;
        this.notificationVisibility = notificationVisibility;
    }
}
