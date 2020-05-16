// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.notification.collection.notifcollection.DismissedByUserStats;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder;
import com.android.systemui.statusbar.notification.collection.inflation.NotifInflater;

public class NotifInflaterImpl implements NotifInflater
{
    private InflationCallback mExternalInflationCallback;
    private final NotificationRowContentBinder.InflationCallback mInflationCallback;
    private final NotifCollection mNotifCollection;
    private final NotifInflationErrorManager mNotifErrorManager;
    private final NotifPipeline mNotifPipeline;
    private NotificationRowBinderImpl mNotificationRowBinder;
    
    public NotifInflaterImpl(final IStatusBarService statusBarService, final NotifCollection mNotifCollection, final NotifInflationErrorManager mNotifErrorManager, final NotifPipeline mNotifPipeline) {
        this.mInflationCallback = new NotificationRowContentBinder.InflationCallback() {
            @Override
            public void handleInflationException(final NotificationEntry notificationEntry, final Exception ex) {
                NotifInflaterImpl.this.mNotifErrorManager.setInflationError(notificationEntry, ex);
            }
            
            @Override
            public void onAsyncInflationFinished(final NotificationEntry notificationEntry) {
                NotifInflaterImpl.this.mNotifErrorManager.clearInflationError(notificationEntry);
                if (NotifInflaterImpl.this.mExternalInflationCallback != null) {
                    NotifInflaterImpl.this.mExternalInflationCallback.onInflationFinished(notificationEntry);
                }
            }
        };
        this.mNotifCollection = mNotifCollection;
        this.mNotifErrorManager = mNotifErrorManager;
        this.mNotifPipeline = mNotifPipeline;
    }
    
    private Runnable getDismissCallback(final NotificationEntry notificationEntry) {
        return new Runnable() {
            @Override
            public void run() {
                final NotifCollection access$100 = NotifInflaterImpl.this.mNotifCollection;
                final NotificationEntry val$entry = notificationEntry;
                access$100.dismissNotification(val$entry, new DismissedByUserStats(3, 1, NotificationVisibility.obtain(val$entry.getKey(), notificationEntry.getRanking().getRank(), NotifInflaterImpl.this.mNotifPipeline.getShadeListCount(), true, NotificationLogger.getNotificationLocation(notificationEntry))));
            }
        };
    }
    
    private NotificationRowBinderImpl requireBinder() {
        final NotificationRowBinderImpl mNotificationRowBinder = this.mNotificationRowBinder;
        if (mNotificationRowBinder != null) {
            return mNotificationRowBinder;
        }
        throw new RuntimeException("NotificationRowBinder must be attached before using NotifInflaterImpl.");
    }
    
    @Override
    public void inflateViews(final NotificationEntry notificationEntry) {
        try {
            this.requireBinder().inflateViews(notificationEntry, this.getDismissCallback(notificationEntry));
        }
        catch (InflationException ex) {}
    }
    
    @Override
    public void rebindViews(final NotificationEntry notificationEntry) {
        this.inflateViews(notificationEntry);
    }
    
    @Override
    public void setInflationCallback(final InflationCallback mExternalInflationCallback) {
        this.mExternalInflationCallback = mExternalInflationCallback;
    }
    
    public void setRowBinder(final NotificationRowBinderImpl mNotificationRowBinder) {
        (this.mNotificationRowBinder = mNotificationRowBinder).setInflationCallback(this.mInflationCallback);
    }
}
