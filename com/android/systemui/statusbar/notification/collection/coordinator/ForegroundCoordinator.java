// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import android.os.UserHandle;
import com.android.systemui.util.Assert;
import java.util.Iterator;
import android.util.ArraySet;
import java.util.HashMap;
import java.util.Map;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.ForegroundServiceController;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender;
import com.android.systemui.appops.AppOpsController;

public class ForegroundCoordinator implements Coordinator
{
    private final AppOpsController mAppOpsController;
    private final NotifLifetimeExtender mForegroundLifetimeExtender;
    private final ForegroundServiceController mForegroundServiceController;
    private final DelayableExecutor mMainExecutor;
    private NotifCollectionListener mNotifCollectionListener;
    private final NotifFilter mNotifFilter;
    private NotifPipeline mNotifPipeline;
    
    public ForegroundCoordinator(final ForegroundServiceController mForegroundServiceController, final AppOpsController mAppOpsController, final DelayableExecutor mMainExecutor) {
        this.mNotifFilter = new NotifFilter("ForegroundCoordinator") {
            @Override
            public boolean shouldFilterOut(final NotificationEntry notificationEntry, final long n) {
                final StatusBarNotification sbn = notificationEntry.getSbn();
                if (ForegroundCoordinator.this.mForegroundServiceController.isDisclosureNotification(sbn) && !ForegroundCoordinator.this.mForegroundServiceController.isDisclosureNeededForUser(sbn.getUser().getIdentifier())) {
                    return true;
                }
                if (ForegroundCoordinator.this.mForegroundServiceController.isSystemAlertNotification(sbn)) {
                    final String[] stringArray = sbn.getNotification().extras.getStringArray("android.foregroundApps");
                    if (stringArray != null && stringArray.length >= 1 && !ForegroundCoordinator.this.mForegroundServiceController.isSystemAlertWarningNeeded(sbn.getUser().getIdentifier(), stringArray[0])) {
                        return true;
                    }
                }
                return false;
            }
        };
        this.mForegroundLifetimeExtender = new NotifLifetimeExtender() {
            private OnEndLifetimeExtensionCallback mEndCallback;
            private Map<NotificationEntry, Runnable> mEndRunnables = new HashMap<NotificationEntry, Runnable>();
            
            @Override
            public void cancelLifetimeExtension(final NotificationEntry notificationEntry) {
                final Runnable runnable = this.mEndRunnables.remove(notificationEntry);
                if (runnable != null) {
                    runnable.run();
                }
            }
            
            @Override
            public String getName() {
                return "ForegroundCoordinator";
            }
            
            @Override
            public void setCallback(final OnEndLifetimeExtensionCallback mEndCallback) {
                this.mEndCallback = mEndCallback;
            }
            
            @Override
            public boolean shouldExtendLifetime(final NotificationEntry notificationEntry, int flags) {
                flags = notificationEntry.getSbn().getNotification().flags;
                boolean b = false;
                if ((flags & 0x40) == 0x0) {
                    return false;
                }
                final long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - notificationEntry.getSbn().getPostTime() < 5000L) {
                    b = true;
                }
                if (b && !this.mEndRunnables.containsKey(notificationEntry)) {
                    this.mEndRunnables.put(notificationEntry, ForegroundCoordinator.this.mMainExecutor.executeDelayed(new _$$Lambda$ForegroundCoordinator$2$G500BvG4KxBRFPQBxlQmtYzEpEc(this, notificationEntry), 5000L - (currentTimeMillis - notificationEntry.getSbn().getPostTime())));
                }
                return b;
            }
        };
        this.mNotifCollectionListener = new NotifCollectionListener() {
            private void tagForeground(final NotificationEntry notificationEntry) {
                final StatusBarNotification sbn = notificationEntry.getSbn();
                final ArraySet<Integer> appOps = ForegroundCoordinator.this.mForegroundServiceController.getAppOps(sbn.getUser().getIdentifier(), sbn.getPackageName());
                if (appOps != null) {
                    notificationEntry.mActiveAppOps.clear();
                    notificationEntry.mActiveAppOps.addAll((ArraySet)appOps);
                }
            }
            
            @Override
            public void onEntryAdded(final NotificationEntry notificationEntry) {
                this.tagForeground(notificationEntry);
            }
            
            @Override
            public void onEntryUpdated(final NotificationEntry notificationEntry) {
                this.tagForeground(notificationEntry);
            }
        };
        this.mForegroundServiceController = mForegroundServiceController;
        this.mAppOpsController = mAppOpsController;
        this.mMainExecutor = mMainExecutor;
    }
    
    private NotificationEntry findNotificationEntryWithKey(final String anObject) {
        for (final NotificationEntry notificationEntry : this.mNotifPipeline.getAllNotifs()) {
            if (notificationEntry.getKey().equals(anObject)) {
                return notificationEntry;
            }
        }
        return null;
    }
    
    private void handleAppOpsChanged(final int n, final int n2, final String s, final boolean b) {
        Assert.isMainThread();
        final String standardLayoutKey = this.mForegroundServiceController.getStandardLayoutKey(UserHandle.getUserId(n2), s);
        if (standardLayoutKey != null) {
            final NotificationEntry notificationEntryWithKey = this.findNotificationEntryWithKey(standardLayoutKey);
            if (notificationEntryWithKey != null && n2 == notificationEntryWithKey.getSbn().getUid() && s.equals(notificationEntryWithKey.getSbn().getPackageName())) {
                boolean b2;
                if (b) {
                    b2 = notificationEntryWithKey.mActiveAppOps.add((Object)n);
                }
                else {
                    b2 = notificationEntryWithKey.mActiveAppOps.remove((Object)n);
                }
                if (b2) {
                    this.mNotifFilter.invalidateList();
                }
            }
        }
    }
    
    private void onAppOpsChanged(final int n, final int n2, final String s, final boolean b) {
        this.mMainExecutor.execute(new _$$Lambda$ForegroundCoordinator$NRgUpFfXHeMTuFSuWDQ6Cgb5Biw(this, n, n2, s, b));
    }
    
    @Override
    public void attach(final NotifPipeline mNotifPipeline) {
        (this.mNotifPipeline = mNotifPipeline).addNotificationLifetimeExtender(this.mForegroundLifetimeExtender);
        this.mNotifPipeline.addCollectionListener(this.mNotifCollectionListener);
        this.mNotifPipeline.addPreGroupFilter(this.mNotifFilter);
        this.mAppOpsController.addCallback(ForegroundServiceController.APP_OPS, (AppOpsController.Callback)new _$$Lambda$ForegroundCoordinator$jdBvGLm54wVgMF6dFXwLOqaE9M4(this));
    }
}
