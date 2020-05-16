// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.service.notification.StatusBarNotification;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;

public class KeyguardEnvironmentImpl implements KeyguardEnvironment
{
    private final DeviceProvisionedController mDeviceProvisionedController;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    
    public KeyguardEnvironmentImpl() {
        this.mLockscreenUserManager = Dependency.get(NotificationLockscreenUserManager.class);
        this.mDeviceProvisionedController = Dependency.get(DeviceProvisionedController.class);
    }
    
    @Override
    public boolean isDeviceProvisioned() {
        return this.mDeviceProvisionedController.isDeviceProvisioned();
    }
    
    @Override
    public boolean isNotificationForCurrentProfiles(final StatusBarNotification statusBarNotification) {
        return this.mLockscreenUserManager.isCurrentProfile(statusBarNotification.getUserId());
    }
}
