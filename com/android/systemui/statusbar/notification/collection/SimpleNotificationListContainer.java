// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import android.view.ViewGroup;
import android.view.View;
import com.android.systemui.statusbar.notification.stack.NotificationListItem;

public interface SimpleNotificationListContainer
{
    void addListItem(final NotificationListItem p0);
    
    void generateChildOrderChangedEvent();
    
    View getContainerChildAt(final int p0);
    
    int getContainerChildCount();
    
    void notifyGroupChildAdded(final View p0);
    
    void notifyGroupChildRemoved(final View p0, final ViewGroup p1);
    
    void removeListItem(final NotificationListItem p0);
    
    void setChildTransferInProgress(final boolean p0);
}
