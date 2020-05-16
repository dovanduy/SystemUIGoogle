// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotificationRemoveInterceptor
{
    boolean onNotificationRemoveRequested(final String p0, final NotificationEntry p1, final int p2);
}
