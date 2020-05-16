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
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.NotificationListener;

public final class NotificationsControllerStub implements NotificationsController
{
    private final NotificationListener notificationListener;
    
    public NotificationsControllerStub(final NotificationListener notificationListener) {
        Intrinsics.checkParameterIsNotNull(notificationListener, "notificationListener");
        this.notificationListener = notificationListener;
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array, final boolean b) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(array, "args");
        printWriter.println();
        printWriter.println("Notification handling disabled");
        printWriter.println();
    }
    
    @Override
    public int getActiveNotificationsCount() {
        return 0;
    }
    
    @Override
    public void initialize(final StatusBar statusBar, final NotificationPresenter notificationPresenter, final NotificationListContainer notificationListContainer, final NotificationActivityStarter notificationActivityStarter, final NotificationRowBinderImpl.BindRowCallback bindRowCallback) {
        Intrinsics.checkParameterIsNotNull(statusBar, "statusBar");
        Intrinsics.checkParameterIsNotNull(notificationPresenter, "presenter");
        Intrinsics.checkParameterIsNotNull(notificationListContainer, "listContainer");
        Intrinsics.checkParameterIsNotNull(notificationActivityStarter, "notificationActivityStarter");
        Intrinsics.checkParameterIsNotNull(bindRowCallback, "bindRowCallback");
        this.notificationListener.registerAsSystemService();
    }
    
    @Override
    public void requestNotificationUpdate(final String s) {
        Intrinsics.checkParameterIsNotNull(s, "reason");
    }
    
    @Override
    public void resetUserExpandedStates() {
    }
    
    @Override
    public void setNotificationSnoozed(final StatusBarNotification statusBarNotification, final int n) {
        Intrinsics.checkParameterIsNotNull(statusBarNotification, "sbn");
    }
    
    @Override
    public void setNotificationSnoozed(final StatusBarNotification statusBarNotification, final NotificationSwipeActionHelper.SnoozeOption snoozeOption) {
        Intrinsics.checkParameterIsNotNull(statusBarNotification, "sbn");
        Intrinsics.checkParameterIsNotNull(snoozeOption, "snoozeOption");
    }
}
