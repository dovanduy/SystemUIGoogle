// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import java.util.Objects;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;

public class NotificationListController
{
    private final DeviceProvisionedController mDeviceProvisionedController;
    private final DeviceProvisionedController.DeviceProvisionedListener mDeviceProvisionedListener;
    private final NotificationEntryListener mEntryListener;
    private final NotificationEntryManager mEntryManager;
    private final NotificationListContainer mListContainer;
    
    public NotificationListController(final NotificationEntryManager obj, final NotificationListContainer obj2, final DeviceProvisionedController obj3) {
        this.mEntryListener = new NotificationEntryListener() {
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final NotificationVisibility notificationVisibility, final boolean b, final int n) {
                NotificationListController.this.mListContainer.cleanUpViewStateForEntry(notificationEntry);
            }
        };
        this.mDeviceProvisionedListener = new DeviceProvisionedController.DeviceProvisionedListener() {
            @Override
            public void onDeviceProvisionedChanged() {
                NotificationListController.this.mEntryManager.updateNotifications("device provisioned changed");
            }
        };
        Objects.requireNonNull(obj);
        this.mEntryManager = obj;
        Objects.requireNonNull(obj2);
        this.mListContainer = obj2;
        Objects.requireNonNull(obj3);
        this.mDeviceProvisionedController = obj3;
    }
    
    public void bind() {
        this.mEntryManager.addNotificationEntryListener(this.mEntryListener);
        this.mDeviceProvisionedController.addCallback(this.mDeviceProvisionedListener);
    }
}
