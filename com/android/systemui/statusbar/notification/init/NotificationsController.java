// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.init;

import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.phone.StatusBar;
import java.io.PrintWriter;
import java.io.FileDescriptor;

public interface NotificationsController
{
    void dump(final FileDescriptor p0, final PrintWriter p1, final String[] p2, final boolean p3);
    
    int getActiveNotificationsCount();
    
    void initialize(final StatusBar p0, final NotificationPresenter p1, final NotificationListContainer p2, final NotificationActivityStarter p3, final NotificationRowBinderImpl.BindRowCallback p4);
    
    void requestNotificationUpdate(final String p0);
    
    void resetUserExpandedStates();
    
    void setNotificationSnoozed(final StatusBarNotification p0, final int p1);
    
    void setNotificationSnoozed(final StatusBarNotification p0, final NotificationSwipeActionHelper.SnoozeOption p1);
}
