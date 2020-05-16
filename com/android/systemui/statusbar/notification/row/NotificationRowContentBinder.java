// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotificationRowContentBinder
{
    void bindContent(final NotificationEntry p0, final ExpandableNotificationRow p1, final int p2, final BindParams p3, final boolean p4, final InflationCallback p5);
    
    void cancelBind(final NotificationEntry p0, final ExpandableNotificationRow p1);
    
    void unbindContent(final NotificationEntry p0, final ExpandableNotificationRow p1, final int p2);
    
    public static class BindParams
    {
        public boolean isChildInGroup;
        public boolean isLowPriority;
        public boolean usesIncreasedHeadsUpHeight;
        public boolean usesIncreasedHeight;
    }
    
    public interface InflationCallback
    {
        void handleInflationException(final NotificationEntry p0, final Exception p1);
        
        void onAsyncInflationFinished(final NotificationEntry p0);
    }
}
