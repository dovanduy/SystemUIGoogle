// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.collection.notifcollection.DismissedByUserStats;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.HashSet;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import com.android.systemui.statusbar.notification.collection.NotifCollection;
import java.util.Set;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifDismissInterceptor;
import com.android.systemui.bubbles.BubbleController;

public class BubbleCoordinator implements Coordinator
{
    private final BubbleController mBubbleController;
    private final NotifDismissInterceptor mDismissInterceptor;
    private final Set<String> mInterceptedDismissalEntries;
    private final BubbleController.NotifCallback mNotifCallback;
    private final NotifCollection mNotifCollection;
    private final NotifFilter mNotifFilter;
    private NotifPipeline mNotifPipeline;
    private NotifDismissInterceptor.OnEndDismissInterception mOnEndDismissInterception;
    
    public BubbleCoordinator(final BubbleController mBubbleController, final NotifCollection mNotifCollection) {
        this.mInterceptedDismissalEntries = new HashSet<String>();
        this.mNotifFilter = new NotifFilter("BubbleCoordinator") {
            @Override
            public boolean shouldFilterOut(final NotificationEntry notificationEntry, final long n) {
                return BubbleCoordinator.this.mBubbleController.isBubbleNotificationSuppressedFromShade(notificationEntry);
            }
        };
        this.mDismissInterceptor = new NotifDismissInterceptor() {
            @Override
            public void cancelDismissInterception(final NotificationEntry notificationEntry) {
                BubbleCoordinator.this.mInterceptedDismissalEntries.remove(notificationEntry.getKey());
            }
            
            @Override
            public String getName() {
                return "BubbleCoordinator";
            }
            
            @Override
            public void setCallback(final OnEndDismissInterception onEndDismissInterception) {
                BubbleCoordinator.this.mOnEndDismissInterception = onEndDismissInterception;
            }
            
            @Override
            public boolean shouldInterceptDismissal(final NotificationEntry notificationEntry) {
                if (BubbleCoordinator.this.mBubbleController.handleDismissalInterception(notificationEntry)) {
                    BubbleCoordinator.this.mInterceptedDismissalEntries.add(notificationEntry.getKey());
                    return true;
                }
                BubbleCoordinator.this.mInterceptedDismissalEntries.remove(notificationEntry.getKey());
                return false;
            }
        };
        this.mNotifCallback = new BubbleController.NotifCallback() {
            @Override
            public void invalidateNotifications(final String s) {
                BubbleCoordinator.this.mNotifFilter.invalidateList();
            }
            
            @Override
            public void maybeCancelSummary(final NotificationEntry notificationEntry) {
            }
            
            @Override
            public void removeNotification(final NotificationEntry notificationEntry, final int n) {
                if (BubbleCoordinator.this.isInterceptingDismissal(notificationEntry)) {
                    BubbleCoordinator.this.mInterceptedDismissalEntries.remove(notificationEntry.getKey());
                    BubbleCoordinator.this.mOnEndDismissInterception.onEndDismissInterception(BubbleCoordinator.this.mDismissInterceptor, notificationEntry, BubbleCoordinator.this.createDismissedByUserStats(notificationEntry));
                }
                else if (BubbleCoordinator.this.mNotifPipeline.getAllNotifs().contains(notificationEntry)) {
                    BubbleCoordinator.this.mNotifCollection.dismissNotification(notificationEntry, BubbleCoordinator.this.createDismissedByUserStats(notificationEntry));
                }
            }
        };
        this.mBubbleController = mBubbleController;
        this.mNotifCollection = mNotifCollection;
    }
    
    private DismissedByUserStats createDismissedByUserStats(final NotificationEntry notificationEntry) {
        return new DismissedByUserStats(0, 1, NotificationVisibility.obtain(notificationEntry.getKey(), notificationEntry.getRanking().getRank(), this.mNotifPipeline.getShadeListCount(), true, NotificationLogger.getNotificationLocation(notificationEntry)));
    }
    
    private boolean isInterceptingDismissal(final NotificationEntry notificationEntry) {
        return this.mInterceptedDismissalEntries.contains(notificationEntry.getKey());
    }
    
    @Override
    public void attach(final NotifPipeline mNotifPipeline) {
        (this.mNotifPipeline = mNotifPipeline).addNotificationDismissInterceptor(this.mDismissInterceptor);
        this.mNotifPipeline.addFinalizeFilter(this.mNotifFilter);
        this.mBubbleController.addNotifCallback(this.mNotifCallback);
    }
}
