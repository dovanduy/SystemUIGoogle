// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.app.AppGlobals;
import com.android.internal.annotations.VisibleForTesting;
import android.service.notification.StatusBarNotification;
import android.os.RemoteException;
import android.content.pm.IPackageManager;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.ForegroundServiceController;

public class NotificationFilter
{
    private NotificationEntryManager.KeyguardEnvironment mEnvironment;
    private ForegroundServiceController mFsc;
    private final StatusBarStateController mStatusBarStateController;
    private NotificationLockscreenUserManager mUserManager;
    
    public NotificationFilter(final StatusBarStateController mStatusBarStateController) {
        final NotificationGroupManager notificationGroupManager = Dependency.get(NotificationGroupManager.class);
        this.mStatusBarStateController = mStatusBarStateController;
    }
    
    private static int checkUidPermission(final IPackageManager packageManager, final String s, int checkUidPermission) {
        try {
            checkUidPermission = packageManager.checkUidPermission(s, checkUidPermission);
            return checkUidPermission;
        }
        catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }
    
    private NotificationEntryManager.KeyguardEnvironment getEnvironment() {
        if (this.mEnvironment == null) {
            this.mEnvironment = Dependency.get(NotificationEntryManager.KeyguardEnvironment.class);
        }
        return this.mEnvironment;
    }
    
    private ForegroundServiceController getFsc() {
        if (this.mFsc == null) {
            this.mFsc = Dependency.get(ForegroundServiceController.class);
        }
        return this.mFsc;
    }
    
    private NotificationLockscreenUserManager getUserManager() {
        if (this.mUserManager == null) {
            this.mUserManager = Dependency.get(NotificationLockscreenUserManager.class);
        }
        return this.mUserManager;
    }
    
    @VisibleForTesting
    static boolean showNotificationEvenIfUnprovisioned(final IPackageManager packageManager, final StatusBarNotification statusBarNotification) {
        return checkUidPermission(packageManager, "android.permission.NOTIFICATION_DURING_SETUP", statusBarNotification.getUid()) == 0 && statusBarNotification.getNotification().extras.getBoolean("android.allowDuringSetup");
    }
    
    private static boolean showNotificationEvenIfUnprovisioned(final StatusBarNotification statusBarNotification) {
        return showNotificationEvenIfUnprovisioned(AppGlobals.getPackageManager(), statusBarNotification);
    }
    
    public boolean shouldFilterOut(final NotificationEntry notificationEntry) {
        final StatusBarNotification sbn = notificationEntry.getSbn();
        if (!this.getEnvironment().isDeviceProvisioned() && !showNotificationEvenIfUnprovisioned(sbn)) {
            return true;
        }
        if (!this.getEnvironment().isNotificationForCurrentProfiles(sbn)) {
            return true;
        }
        if (this.getUserManager().isLockscreenPublicMode(sbn.getUserId()) && (sbn.getNotification().visibility == -1 || this.getUserManager().shouldHideNotifications(sbn.getUserId()) || this.getUserManager().shouldHideNotifications(sbn.getKey()))) {
            return true;
        }
        if (this.mStatusBarStateController.isDozing() && notificationEntry.shouldSuppressAmbient()) {
            return true;
        }
        if (!this.mStatusBarStateController.isDozing() && notificationEntry.shouldSuppressNotificationList()) {
            return true;
        }
        if (notificationEntry.getRanking().isSuspended()) {
            return true;
        }
        if (this.getFsc().isDisclosureNotification(sbn) && !this.getFsc().isDisclosureNeededForUser(sbn.getUserId())) {
            return true;
        }
        if (this.getFsc().isSystemAlertNotification(sbn)) {
            final String[] stringArray = sbn.getNotification().extras.getStringArray("android.foregroundApps");
            if (stringArray != null && stringArray.length >= 1 && !this.getFsc().isSystemAlertWarningNeeded(sbn.getUserId(), stringArray[0])) {
                return true;
            }
        }
        return false;
    }
}
