// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;

public class RankingCoordinator implements Coordinator
{
    private final NotifFilter mDozingFilter;
    private final StatusBarStateController.StateListener mStatusBarStateCallback;
    private final StatusBarStateController mStatusBarStateController;
    private final NotifFilter mSuspendedFilter;
    
    public RankingCoordinator(final StatusBarStateController mStatusBarStateController) {
        this.mSuspendedFilter = new NotifFilter("IsSuspendedFilter") {
            @Override
            public boolean shouldFilterOut(final NotificationEntry notificationEntry, final long n) {
                return notificationEntry.getRanking().isSuspended();
            }
        };
        this.mDozingFilter = new NotifFilter("IsDozingFilter") {
            @Override
            public boolean shouldFilterOut(final NotificationEntry notificationEntry, final long n) {
                final boolean dozing = RankingCoordinator.this.mStatusBarStateController.isDozing();
                boolean b = true;
                if (dozing && notificationEntry.shouldSuppressAmbient()) {
                    return true;
                }
                if (RankingCoordinator.this.mStatusBarStateController.isDozing() || !notificationEntry.shouldSuppressNotificationList()) {
                    b = false;
                }
                return b;
            }
        };
        this.mStatusBarStateCallback = new StatusBarStateController.StateListener() {
            @Override
            public void onDozingChanged(final boolean b) {
                RankingCoordinator.this.mDozingFilter.invalidateList();
            }
        };
        this.mStatusBarStateController = mStatusBarStateController;
    }
    
    @Override
    public void attach(final NotifPipeline notifPipeline) {
        this.mStatusBarStateController.addCallback(this.mStatusBarStateCallback);
        notifPipeline.addPreGroupFilter(this.mSuspendedFilter);
        notifPipeline.addPreGroupFilter(this.mDozingFilter);
    }
}
