// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import android.view.ViewGroup;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import android.view.View;
import com.android.systemui.statusbar.notification.collection.SimpleNotificationListContainer;
import com.android.systemui.statusbar.notification.VisibilityLocationProvider;
import com.android.systemui.statusbar.notification.row.ExpandableView;

public interface NotificationListContainer extends OnHeightChangedListener, VisibilityLocationProvider, SimpleNotificationListContainer
{
    void addContainerView(final View p0);
    
    default void applyExpandAnimationParams(final ActivityLaunchAnimator.ExpandAnimationParameters expandAnimationParameters) {
    }
    
    default void bindRow(final ExpandableNotificationRow expandableNotificationRow) {
    }
    
    void changeViewPosition(final ExpandableView p0, final int p1);
    
    void cleanUpViewStateForEntry(final NotificationEntry p0);
    
    default boolean containsView(final View view) {
        return true;
    }
    
    void generateAddAnimation(final ExpandableView p0, final boolean p1);
    
    void generateChildOrderChangedEvent();
    
    View getContainerChildAt(final int p0);
    
    int getContainerChildCount();
    
    NotificationSwipeActionHelper getSwipeActionHelper();
    
    ViewGroup getViewParentForNotification(final NotificationEntry p0);
    
    boolean hasPulsingNotifications();
    
    void notifyGroupChildAdded(final ExpandableView p0);
    
    void notifyGroupChildRemoved(final ExpandableView p0, final ViewGroup p1);
    
    default void onNotificationViewUpdateFinished() {
    }
    
    void removeContainerView(final View p0);
    
    void resetExposedMenuView(final boolean p0, final boolean p1);
    
    void setChildLocationsChangedListener(final NotificationLogger.OnChildLocationsChangedListener p0);
    
    void setChildTransferInProgress(final boolean p0);
    
    default void setExpandingNotification(final ExpandableNotificationRow expandableNotificationRow) {
    }
    
    void setMaxDisplayedNotifications(final int p0);
    
    default void setWillExpand(final boolean b) {
    }
}
