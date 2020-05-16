// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.interruption;

import android.util.Log;
import android.service.notification.StatusBarNotification;
import android.app.Notification;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.policy.HeadsUpManager;

public class NotificationAlertingManager
{
    private HeadsUpManager mHeadsUpManager;
    private final NotificationInterruptStateProvider mNotificationInterruptStateProvider;
    private final NotificationListener mNotificationListener;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final StatusBarStateController mStatusBarStateController;
    private final VisualStabilityManager mVisualStabilityManager;
    
    public NotificationAlertingManager(final NotificationEntryManager notificationEntryManager, final NotificationRemoteInputManager mRemoteInputManager, final VisualStabilityManager mVisualStabilityManager, final StatusBarStateController mStatusBarStateController, final NotificationInterruptStateProvider mNotificationInterruptStateProvider, final NotificationListener mNotificationListener, final HeadsUpManager mHeadsUpManager) {
        this.mRemoteInputManager = mRemoteInputManager;
        this.mVisualStabilityManager = mVisualStabilityManager;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mNotificationInterruptStateProvider = mNotificationInterruptStateProvider;
        this.mNotificationListener = mNotificationListener;
        this.mHeadsUpManager = mHeadsUpManager;
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            @Override
            public void onEntryInflated(final NotificationEntry notificationEntry) {
                NotificationAlertingManager.this.showAlertingView(notificationEntry);
            }
            
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final NotificationVisibility notificationVisibility, final boolean b, final int n) {
                NotificationAlertingManager.this.stopAlerting(notificationEntry.getKey());
            }
            
            @Override
            public void onPreEntryUpdated(final NotificationEntry notificationEntry) {
                NotificationAlertingManager.this.updateAlertState(notificationEntry);
            }
        });
    }
    
    public static boolean alertAgain(final NotificationEntry notificationEntry, final Notification notification) {
        return notificationEntry == null || !notificationEntry.hasInterrupted() || (notification.flags & 0x8) == 0x0;
    }
    
    private void setNotificationShown(final StatusBarNotification statusBarNotification) {
        try {
            this.mNotificationListener.setNotificationsShown(new String[] { statusBarNotification.getKey() });
        }
        catch (RuntimeException ex) {
            Log.d("NotifAlertManager", "failed setNotificationsShown: ", (Throwable)ex);
        }
    }
    
    private void showAlertingView(final NotificationEntry notificationEntry) {
        if (notificationEntry.getRow().getPrivateLayout().getHeadsUpChild() != null) {
            this.mHeadsUpManager.showNotification(notificationEntry);
            if (!this.mStatusBarStateController.isDozing()) {
                this.setNotificationShown(notificationEntry.getSbn());
            }
        }
    }
    
    private void stopAlerting(final String s) {
        if (this.mHeadsUpManager.isAlerting(s)) {
            this.mHeadsUpManager.removeNotification(s, (this.mRemoteInputManager.getController().isSpinning(s) && !NotificationRemoteInputManager.FORCE_REMOTE_INPUT_HISTORY) || !this.mVisualStabilityManager.isReorderingAllowed());
        }
    }
    
    private void updateAlertState(final NotificationEntry notificationEntry) {
        final boolean alertAgain = alertAgain(notificationEntry, notificationEntry.getSbn().getNotification());
        final boolean shouldHeadsUp = this.mNotificationInterruptStateProvider.shouldHeadsUp(notificationEntry);
        if (this.mHeadsUpManager.isAlerting(notificationEntry.getKey())) {
            if (shouldHeadsUp) {
                this.mHeadsUpManager.updateNotification(notificationEntry.getKey(), alertAgain);
            }
            else if (!this.mHeadsUpManager.isEntryAutoHeadsUpped(notificationEntry.getKey())) {
                this.mHeadsUpManager.removeNotification(notificationEntry.getKey(), false);
            }
        }
        else if (shouldHeadsUp && alertAgain) {
            this.mHeadsUpManager.showNotification(notificationEntry);
        }
    }
}
