// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import android.content.pm.UserInfo;
import android.util.SparseArray;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;

public class HideNotifsForOtherUsersCoordinator implements Coordinator
{
    private final NotifFilter mFilter;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private final NotificationLockscreenUserManager.UserChangedListener mUserChangedListener;
    
    public HideNotifsForOtherUsersCoordinator(final NotificationLockscreenUserManager mLockscreenUserManager) {
        this.mFilter = new NotifFilter("NotCurrentUserFilter") {
            @Override
            public boolean shouldFilterOut(final NotificationEntry notificationEntry, final long n) {
                return HideNotifsForOtherUsersCoordinator.this.mLockscreenUserManager.isCurrentProfile(notificationEntry.getSbn().getUser().getIdentifier()) ^ true;
            }
        };
        this.mUserChangedListener = new NotificationLockscreenUserManager.UserChangedListener() {
            @Override
            public void onCurrentProfilesChanged(final SparseArray<UserInfo> sparseArray) {
                HideNotifsForOtherUsersCoordinator.this.mFilter.invalidateList();
            }
        };
        this.mLockscreenUserManager = mLockscreenUserManager;
    }
    
    @Override
    public void attach(final NotifPipeline notifPipeline) {
        notifPipeline.addPreGroupFilter(this.mFilter);
        this.mLockscreenUserManager.addUserChangedListener(this.mUserChangedListener);
    }
}
