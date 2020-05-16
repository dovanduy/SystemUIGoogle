// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.inflation;

import com.android.systemui.statusbar.NotificationUiAdjustment;
import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotificationRowBinder
{
    void inflateViews(final NotificationEntry p0, final Runnable p1) throws InflationException;
    
    void onNotificationRankingUpdated(final NotificationEntry p0, final Integer p1, final NotificationUiAdjustment p2, final NotificationUiAdjustment p3);
}
