// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotifLifetimeExtender
{
    void cancelLifetimeExtension(final NotificationEntry p0);
    
    String getName();
    
    void setCallback(final OnEndLifetimeExtensionCallback p0);
    
    boolean shouldExtendLifetime(final NotificationEntry p0, final int p1);
    
    public interface OnEndLifetimeExtensionCallback
    {
        void onEndLifetimeExtension(final NotifLifetimeExtender p0, final NotificationEntry p1);
    }
}
