// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.util.ArraySet;
import android.os.Bundle;
import android.app.Notification$Builder;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.NotificationLifetimeExtender;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import android.content.Context;

public class ForegroundServiceNotificationListener
{
    private final Context mContext;
    private final NotificationEntryManager mEntryManager;
    private final ForegroundServiceController mForegroundServiceController;
    
    public ForegroundServiceNotificationListener(final Context mContext, final ForegroundServiceController mForegroundServiceController, final NotificationEntryManager mEntryManager, final NotifPipeline notifPipeline) {
        this.mContext = mContext;
        this.mForegroundServiceController = mForegroundServiceController;
        (this.mEntryManager = mEntryManager).addNotificationEntryListener(new NotificationEntryListener() {
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final NotificationVisibility notificationVisibility, final boolean b, final int n) {
                ForegroundServiceNotificationListener.this.removeNotification(notificationEntry.getSbn());
            }
            
            @Override
            public void onPendingEntryAdded(final NotificationEntry notificationEntry) {
                ForegroundServiceNotificationListener.this.addNotification(notificationEntry, notificationEntry.getImportance());
            }
            
            @Override
            public void onPreEntryUpdated(final NotificationEntry notificationEntry) {
                ForegroundServiceNotificationListener.this.updateNotification(notificationEntry, notificationEntry.getImportance());
            }
        });
        this.mEntryManager.addNotificationLifetimeExtender(new ForegroundServiceLifetimeExtender());
        notifPipeline.addCollectionListener(new NotifCollectionListener() {
            @Override
            public void onEntryAdded(final NotificationEntry notificationEntry) {
                ForegroundServiceNotificationListener.this.addNotification(notificationEntry, notificationEntry.getImportance());
            }
            
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final int n) {
                ForegroundServiceNotificationListener.this.removeNotification(notificationEntry.getSbn());
            }
            
            @Override
            public void onEntryUpdated(final NotificationEntry notificationEntry) {
                ForegroundServiceNotificationListener.this.updateNotification(notificationEntry, notificationEntry.getImportance());
            }
        });
    }
    
    private void addNotification(final NotificationEntry notificationEntry, final int n) {
        this.updateNotification(notificationEntry, n);
    }
    
    private void removeNotification(final StatusBarNotification statusBarNotification) {
        this.mForegroundServiceController.updateUserState(statusBarNotification.getUserId(), (ForegroundServiceController.UserStateUpdateCallback)new ForegroundServiceController.UserStateUpdateCallback() {
            @Override
            public boolean updateUserState(final ForegroundServicesUserState foregroundServicesUserState) {
                if (ForegroundServiceNotificationListener.this.mForegroundServiceController.isDisclosureNotification(statusBarNotification)) {
                    foregroundServicesUserState.setRunningServices(null, 0L);
                    return true;
                }
                return foregroundServicesUserState.removeNotification(statusBarNotification.getPackageName(), statusBarNotification.getKey());
            }
        }, false);
    }
    
    private void tagForeground(final NotificationEntry notificationEntry) {
        final StatusBarNotification sbn = notificationEntry.getSbn();
        final ArraySet<Integer> appOps = this.mForegroundServiceController.getAppOps(sbn.getUserId(), sbn.getPackageName());
        if (appOps != null) {
            synchronized (notificationEntry.mActiveAppOps) {
                notificationEntry.mActiveAppOps.clear();
                notificationEntry.mActiveAppOps.addAll((ArraySet)appOps);
            }
        }
    }
    
    private void updateNotification(final NotificationEntry notificationEntry, final int n) {
        final StatusBarNotification sbn = notificationEntry.getSbn();
        this.mForegroundServiceController.updateUserState(sbn.getUserId(), (ForegroundServiceController.UserStateUpdateCallback)new _$$Lambda$ForegroundServiceNotificationListener$bKAGLLFV59EYZBLeV36rpndtUhU(this, sbn, n, notificationEntry), true);
    }
}
