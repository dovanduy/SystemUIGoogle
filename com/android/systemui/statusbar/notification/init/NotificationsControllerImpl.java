// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.init;

import android.service.notification.SnoozeCriterion;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import android.service.notification.StatusBarNotification;
import java.util.Iterator;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder;
import com.android.systemui.statusbar.notification.NotificationClicker;
import java.util.Optional;
import com.android.systemui.statusbar.notification.NotificationListController;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.phone.StatusBar;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineInitializer;
import com.android.systemui.statusbar.notification.collection.init.NotifPipelineInitializer;
import dagger.Lazy;
import com.android.systemui.statusbar.notification.headsup.HeadsUpViewBinder;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.notification.headsup.HeadsUpBindController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.bubbles.BubbleController;

public final class NotificationsControllerImpl implements NotificationsController
{
    private final BubbleController bubbleController;
    private final DeviceProvisionedController deviceProvisionedController;
    private final NotificationEntryManager entryManager;
    private final FeatureFlags featureFlags;
    private final NotificationGroupAlertTransferHelper groupAlertTransferHelper;
    private final NotificationGroupManager groupManager;
    private final HeadsUpBindController headsUpBindController;
    private final HeadsUpManager headsUpManager;
    private final HeadsUpViewBinder headsUpViewBinder;
    private final Lazy<NotifPipelineInitializer> newNotifPipeline;
    private final NotifBindPipelineInitializer notifBindPipelineInitializer;
    private final NotificationListener notificationListener;
    private final NotificationRowBinderImpl notificationRowBinder;
    private final RemoteInputUriController remoteInputUriController;
    
    public NotificationsControllerImpl(final FeatureFlags featureFlags, final NotificationListener notificationListener, final NotificationEntryManager entryManager, final Lazy<NotifPipelineInitializer> newNotifPipeline, final NotifBindPipelineInitializer notifBindPipelineInitializer, final DeviceProvisionedController deviceProvisionedController, final NotificationRowBinderImpl notificationRowBinder, final RemoteInputUriController remoteInputUriController, final BubbleController bubbleController, final NotificationGroupManager groupManager, final NotificationGroupAlertTransferHelper groupAlertTransferHelper, final HeadsUpManager headsUpManager, final HeadsUpBindController headsUpBindController, final HeadsUpViewBinder headsUpViewBinder) {
        Intrinsics.checkParameterIsNotNull(featureFlags, "featureFlags");
        Intrinsics.checkParameterIsNotNull(notificationListener, "notificationListener");
        Intrinsics.checkParameterIsNotNull(entryManager, "entryManager");
        Intrinsics.checkParameterIsNotNull(newNotifPipeline, "newNotifPipeline");
        Intrinsics.checkParameterIsNotNull(notifBindPipelineInitializer, "notifBindPipelineInitializer");
        Intrinsics.checkParameterIsNotNull(deviceProvisionedController, "deviceProvisionedController");
        Intrinsics.checkParameterIsNotNull(notificationRowBinder, "notificationRowBinder");
        Intrinsics.checkParameterIsNotNull(remoteInputUriController, "remoteInputUriController");
        Intrinsics.checkParameterIsNotNull(bubbleController, "bubbleController");
        Intrinsics.checkParameterIsNotNull(groupManager, "groupManager");
        Intrinsics.checkParameterIsNotNull(groupAlertTransferHelper, "groupAlertTransferHelper");
        Intrinsics.checkParameterIsNotNull(headsUpManager, "headsUpManager");
        Intrinsics.checkParameterIsNotNull(headsUpBindController, "headsUpBindController");
        Intrinsics.checkParameterIsNotNull(headsUpViewBinder, "headsUpViewBinder");
        this.featureFlags = featureFlags;
        this.notificationListener = notificationListener;
        this.entryManager = entryManager;
        this.newNotifPipeline = newNotifPipeline;
        this.notifBindPipelineInitializer = notifBindPipelineInitializer;
        this.deviceProvisionedController = deviceProvisionedController;
        this.notificationRowBinder = notificationRowBinder;
        this.remoteInputUriController = remoteInputUriController;
        this.bubbleController = bubbleController;
        this.groupManager = groupManager;
        this.groupAlertTransferHelper = groupAlertTransferHelper;
        this.headsUpManager = headsUpManager;
        this.headsUpBindController = headsUpBindController;
        this.headsUpViewBinder = headsUpViewBinder;
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array, final boolean b) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(array, "args");
        if (b) {
            this.entryManager.dump(printWriter, "  ");
        }
        this.groupManager.dump(fileDescriptor, printWriter, array);
    }
    
    @Override
    public int getActiveNotificationsCount() {
        return this.entryManager.getActiveNotificationsCount();
    }
    
    @Override
    public void initialize(final StatusBar value, final NotificationPresenter presenter, final NotificationListContainer notificationListContainer, final NotificationActivityStarter notificationActivityStarter, final NotificationRowBinderImpl.BindRowCallback bindRowCallback) {
        Intrinsics.checkParameterIsNotNull(value, "statusBar");
        Intrinsics.checkParameterIsNotNull(presenter, "presenter");
        Intrinsics.checkParameterIsNotNull(notificationListContainer, "listContainer");
        Intrinsics.checkParameterIsNotNull(notificationActivityStarter, "notificationActivityStarter");
        Intrinsics.checkParameterIsNotNull(bindRowCallback, "bindRowCallback");
        this.notificationListener.registerAsSystemService();
        new NotificationListController(this.entryManager, notificationListContainer, this.deviceProvisionedController).bind();
        this.notificationRowBinder.setNotificationClicker(new NotificationClicker(Optional.of(value), this.bubbleController, notificationActivityStarter));
        this.notificationRowBinder.setUpWithPresenter(presenter, notificationListContainer, bindRowCallback);
        this.headsUpViewBinder.setPresenter(presenter);
        this.notifBindPipelineInitializer.initialize();
        if (this.featureFlags.isNewNotifPipelineEnabled()) {
            this.newNotifPipeline.get().initialize(this.notificationListener, this.notificationRowBinder, notificationListContainer);
        }
        if (!this.featureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.notificationRowBinder.setInflationCallback(this.entryManager);
            this.remoteInputUriController.attach(this.entryManager);
            this.groupAlertTransferHelper.bind(this.entryManager, this.groupManager);
            this.headsUpManager.addListener(this.groupManager);
            this.headsUpManager.addListener(this.groupAlertTransferHelper);
            this.headsUpBindController.attach(this.entryManager, this.headsUpManager);
            this.groupManager.setHeadsUpManager(this.headsUpManager);
            this.groupAlertTransferHelper.setHeadsUpManager(this.headsUpManager);
            this.entryManager.attach(this.notificationListener);
        }
    }
    
    @Override
    public void requestNotificationUpdate(final String s) {
        Intrinsics.checkParameterIsNotNull(s, "reason");
        this.entryManager.updateNotifications(s);
    }
    
    @Override
    public void resetUserExpandedStates() {
        final Iterator<NotificationEntry> iterator = this.entryManager.getVisibleNotifications().iterator();
        while (iterator.hasNext()) {
            iterator.next().resetUserExpansion();
        }
    }
    
    @Override
    public void setNotificationSnoozed(final StatusBarNotification statusBarNotification, final int n) {
        Intrinsics.checkParameterIsNotNull(statusBarNotification, "sbn");
        this.notificationListener.snoozeNotification(statusBarNotification.getKey(), n * 60 * 60 * (long)1000);
    }
    
    @Override
    public void setNotificationSnoozed(final StatusBarNotification statusBarNotification, final NotificationSwipeActionHelper.SnoozeOption snoozeOption) {
        Intrinsics.checkParameterIsNotNull(statusBarNotification, "sbn");
        Intrinsics.checkParameterIsNotNull(snoozeOption, "snoozeOption");
        if (snoozeOption.getSnoozeCriterion() != null) {
            final NotificationListener notificationListener = this.notificationListener;
            final String key = statusBarNotification.getKey();
            final SnoozeCriterion snoozeCriterion = snoozeOption.getSnoozeCriterion();
            Intrinsics.checkExpressionValueIsNotNull(snoozeCriterion, "snoozeOption.snoozeCriterion");
            notificationListener.snoozeNotification(key, snoozeCriterion.getId());
        }
        else {
            this.notificationListener.snoozeNotification(statusBarNotification.getKey(), snoozeOption.getMinutesToSnoozeFor() * 60 * (long)1000);
        }
    }
}
