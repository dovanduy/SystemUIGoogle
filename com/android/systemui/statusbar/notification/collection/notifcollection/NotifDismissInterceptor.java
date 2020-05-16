// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotifDismissInterceptor
{
    void cancelDismissInterception(final NotificationEntry p0);
    
    String getName();
    
    void setCallback(final OnEndDismissInterception p0);
    
    boolean shouldInterceptDismissal(final NotificationEntry p0);
    
    public interface OnEndDismissInterception
    {
        void onEndDismissInterception(final NotifDismissInterceptor p0, final NotificationEntry p1, final DismissedByUserStats p2);
    }
}
