// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import android.content.pm.IPackageManager;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;

public class DeviceProvisionedCoordinator implements Coordinator
{
    private final DeviceProvisionedController mDeviceProvisionedController;
    private final DeviceProvisionedController.DeviceProvisionedListener mDeviceProvisionedListener;
    private final IPackageManager mIPackageManager;
    private final NotifFilter mNotifFilter;
    
    public DeviceProvisionedCoordinator(final DeviceProvisionedController mDeviceProvisionedController, final IPackageManager miPackageManager) {
        this.mNotifFilter = new NotifFilter("DeviceProvisionedCoordinator") {
            @Override
            public boolean shouldFilterOut(final NotificationEntry notificationEntry, final long n) {
                return !DeviceProvisionedCoordinator.this.mDeviceProvisionedController.isDeviceProvisioned() && !DeviceProvisionedCoordinator.this.showNotificationEvenIfUnprovisioned(notificationEntry.getSbn());
            }
        };
        this.mDeviceProvisionedListener = new DeviceProvisionedController.DeviceProvisionedListener() {
            @Override
            public void onDeviceProvisionedChanged() {
                DeviceProvisionedCoordinator.this.mNotifFilter.invalidateList();
            }
        };
        this.mDeviceProvisionedController = mDeviceProvisionedController;
        this.mIPackageManager = miPackageManager;
    }
    
    private int checkUidPermission(final String s, int checkUidPermission) {
        try {
            checkUidPermission = this.mIPackageManager.checkUidPermission(s, checkUidPermission);
            return checkUidPermission;
        }
        catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }
    
    private boolean showNotificationEvenIfUnprovisioned(final StatusBarNotification statusBarNotification) {
        final int checkUidPermission = this.checkUidPermission("android.permission.NOTIFICATION_DURING_SETUP", statusBarNotification.getUid());
        boolean b = true;
        if (checkUidPermission != 0 || !statusBarNotification.getNotification().extras.getBoolean("android.allowDuringSetup")) {
            b = false;
        }
        return b;
    }
    
    @Override
    public void attach(final NotifPipeline notifPipeline) {
        this.mDeviceProvisionedController.addCallback(this.mDeviceProvisionedListener);
        notifPipeline.addPreGroupFilter(this.mNotifFilter);
    }
}
