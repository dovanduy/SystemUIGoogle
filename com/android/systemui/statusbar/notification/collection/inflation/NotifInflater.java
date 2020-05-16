// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.inflation;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotifInflater
{
    void inflateViews(final NotificationEntry p0);
    
    void rebindViews(final NotificationEntry p0);
    
    void setInflationCallback(final InflationCallback p0);
    
    public interface InflationCallback
    {
        void onInflationFinished(final NotificationEntry p0);
    }
}
