// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.os.Looper;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.util.ArraySet;
import android.os.Handler;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.NotificationLifetimeExtender;

public class ForegroundServiceLifetimeExtender implements NotificationLifetimeExtender
{
    @VisibleForTesting
    static final int MIN_FGS_TIME_MS = 5000;
    private Handler mHandler;
    private ArraySet<NotificationEntry> mManagedEntries;
    private NotificationSafeToRemoveCallback mNotificationSafeToRemoveCallback;
    
    public ForegroundServiceLifetimeExtender() {
        this.mManagedEntries = (ArraySet<NotificationEntry>)new ArraySet();
        this.mHandler = new Handler(Looper.getMainLooper());
    }
    
    @Override
    public void setCallback(final NotificationSafeToRemoveCallback mNotificationSafeToRemoveCallback) {
        this.mNotificationSafeToRemoveCallback = mNotificationSafeToRemoveCallback;
    }
    
    @Override
    public void setShouldManageLifetime(final NotificationEntry notificationEntry, final boolean b) {
        if (!b) {
            this.mManagedEntries.remove((Object)notificationEntry);
            return;
        }
        this.mManagedEntries.add((Object)notificationEntry);
        this.mHandler.postDelayed((Runnable)new _$$Lambda$ForegroundServiceLifetimeExtender$_eZMtetouaKnxc7j2jqc6zpz_AA(this, notificationEntry), 5000L - (System.currentTimeMillis() - notificationEntry.getSbn().getPostTime()));
    }
    
    @Override
    public boolean shouldExtendLifetime(final NotificationEntry notificationEntry) {
        final int flags = notificationEntry.getSbn().getNotification().flags;
        boolean b = false;
        if ((flags & 0x40) == 0x0) {
            return false;
        }
        if (System.currentTimeMillis() - notificationEntry.getSbn().getPostTime() < 5000L) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean shouldExtendLifetimeForPendingNotification(final NotificationEntry notificationEntry) {
        return this.shouldExtendLifetime(notificationEntry);
    }
}
