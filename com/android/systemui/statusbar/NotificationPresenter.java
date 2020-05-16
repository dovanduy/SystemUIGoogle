// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public interface NotificationPresenter extends OnExpandClickListener, OnActivatedListener
{
    int getMaxNotificationsWhileLocked(final boolean p0);
    
    boolean isCollapsing();
    
    boolean isDeviceInVrMode();
    
    boolean isPresenterFullyCollapsed();
    
    void onUpdateRowStates();
    
    void onUserSwitched(final int p0);
    
    void updateMediaMetaData(final boolean p0, final boolean p1);
    
    void updateNotificationViews();
}
