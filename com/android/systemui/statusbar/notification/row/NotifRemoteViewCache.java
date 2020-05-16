// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.widget.RemoteViews;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotifRemoteViewCache
{
    void clearCache(final NotificationEntry p0);
    
    RemoteViews getCachedView(final NotificationEntry p0, final int p1);
    
    boolean hasCachedView(final NotificationEntry p0, final int p1);
    
    void putCachedView(final NotificationEntry p0, final int p1, final RemoteViews p2);
    
    void removeCachedView(final NotificationEntry p0, final int p1);
}
