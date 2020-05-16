// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import android.view.View;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import java.util.List;

public interface NotificationListItem
{
    void addChildNotification(final NotificationListItem p0, final int p1);
    
    boolean applyChildOrder(final List<? extends NotificationListItem> p0, final VisualStabilityManager p1, final VisualStabilityManager.Callback p2);
    
    NotificationEntry getEntry();
    
    List<? extends NotificationListItem> getNotificationChildren();
    
    View getView();
    
    boolean isBlockingHelperShowing();
    
    boolean isSummaryWithChildren();
    
    void removeAllChildren();
    
    void removeChildNotification(final NotificationListItem p0);
}
