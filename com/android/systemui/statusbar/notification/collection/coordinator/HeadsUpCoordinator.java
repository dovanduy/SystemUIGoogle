// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import java.util.Objects;
import com.android.systemui.statusbar.notification.interruption.NotificationAlertingManager;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSection;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifPromoter;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.headsup.HeadsUpViewBinder;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public class HeadsUpCoordinator implements Coordinator
{
    private NotificationEntry mCurrentHun;
    private NotifLifetimeExtender.OnEndLifetimeExtensionCallback mEndLifetimeExtension;
    private final HeadsUpManager mHeadsUpManager;
    private final HeadsUpViewBinder mHeadsUpViewBinder;
    private final NotifLifetimeExtender mLifetimeExtender;
    private final NotifCollectionListener mNotifCollectionListener;
    private NotificationEntry mNotifExtendingLifetime;
    private final NotifPromoter mNotifPromoter;
    private final NotifSection mNotifSection;
    private final NotificationInterruptStateProvider mNotificationInterruptStateProvider;
    private final OnHeadsUpChangedListener mOnHeadsUpChangedListener;
    private final NotificationRemoteInputManager mRemoteInputManager;
    
    public HeadsUpCoordinator(final HeadsUpManager mHeadsUpManager, final HeadsUpViewBinder mHeadsUpViewBinder, final NotificationInterruptStateProvider mNotificationInterruptStateProvider, final NotificationRemoteInputManager mRemoteInputManager) {
        this.mNotifCollectionListener = new NotifCollectionListener() {
            @Override
            public void onEntryAdded(final NotificationEntry notificationEntry) {
                if (HeadsUpCoordinator.this.mNotificationInterruptStateProvider.shouldHeadsUp(notificationEntry)) {
                    HeadsUpCoordinator.this.mHeadsUpViewBinder.bindHeadsUpView(notificationEntry, new _$$Lambda$HeadsUpCoordinator$1$ifvJDWb4RvXhnAbnBVrfGOQJUGM(HeadsUpCoordinator.this));
                }
            }
            
            @Override
            public void onEntryCleanUp(final NotificationEntry notificationEntry) {
                HeadsUpCoordinator.this.mHeadsUpViewBinder.abortBindCallback(notificationEntry);
            }
            
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final int n) {
                final String key = notificationEntry.getKey();
                if (HeadsUpCoordinator.this.mHeadsUpManager.isAlerting(key)) {
                    HeadsUpCoordinator.this.mHeadsUpManager.removeNotification(notificationEntry.getKey(), HeadsUpCoordinator.this.mRemoteInputManager.getController().isSpinning(key) && !NotificationRemoteInputManager.FORCE_REMOTE_INPUT_HISTORY);
                }
            }
            
            @Override
            public void onEntryUpdated(final NotificationEntry notificationEntry) {
                final boolean alertAgain = NotificationAlertingManager.alertAgain(notificationEntry, notificationEntry.getSbn().getNotification());
                final boolean shouldHeadsUp = HeadsUpCoordinator.this.mNotificationInterruptStateProvider.shouldHeadsUp(notificationEntry);
                if (HeadsUpCoordinator.this.mHeadsUpManager.isAlerting(notificationEntry.getKey())) {
                    if (shouldHeadsUp) {
                        HeadsUpCoordinator.this.mHeadsUpManager.updateNotification(notificationEntry.getKey(), alertAgain);
                    }
                    else if (!HeadsUpCoordinator.this.mHeadsUpManager.isEntryAutoHeadsUpped(notificationEntry.getKey())) {
                        HeadsUpCoordinator.this.mHeadsUpManager.removeNotification(notificationEntry.getKey(), false);
                    }
                }
                else if (shouldHeadsUp && alertAgain) {
                    HeadsUpCoordinator.this.mHeadsUpViewBinder.bindHeadsUpView(notificationEntry, new _$$Lambda$HeadsUpCoordinator$1$7Uuyx_f2KfHu3jWNg_GxLQ4f6Hw(HeadsUpCoordinator.this));
                }
            }
        };
        this.mLifetimeExtender = new NotifLifetimeExtender() {
            @Override
            public void cancelLifetimeExtension(final NotificationEntry b) {
                if (Objects.equals(HeadsUpCoordinator.this.mNotifExtendingLifetime, b)) {
                    HeadsUpCoordinator.this.mNotifExtendingLifetime = null;
                }
            }
            
            @Override
            public String getName() {
                return "HeadsUpCoordinator";
            }
            
            @Override
            public void setCallback(final OnEndLifetimeExtensionCallback onEndLifetimeExtensionCallback) {
                HeadsUpCoordinator.this.mEndLifetimeExtension = onEndLifetimeExtensionCallback;
            }
            
            @Override
            public boolean shouldExtendLifetime(final NotificationEntry notificationEntry, final int n) {
                final boolean access$600 = HeadsUpCoordinator.this.isCurrentlyShowingHun(notificationEntry);
                if (access$600) {
                    HeadsUpCoordinator.this.mNotifExtendingLifetime = notificationEntry;
                }
                return access$600;
            }
        };
        this.mNotifPromoter = new NotifPromoter("HeadsUpCoordinator") {
            @Override
            public boolean shouldPromoteToTopLevel(final NotificationEntry notificationEntry) {
                return HeadsUpCoordinator.this.isCurrentlyShowingHun(notificationEntry);
            }
        };
        this.mNotifSection = new NotifSection("HeadsUpCoordinator") {
            @Override
            public boolean isInSection(final ListEntry listEntry) {
                return HeadsUpCoordinator.this.isCurrentlyShowingHun(listEntry);
            }
        };
        this.mOnHeadsUpChangedListener = new OnHeadsUpChangedListener() {
            @Override
            public void onHeadsUpStateChanged(final NotificationEntry notificationEntry, final boolean b) {
                final NotificationEntry topEntry = HeadsUpCoordinator.this.mHeadsUpManager.getTopEntry();
                if (!Objects.equals(HeadsUpCoordinator.this.mCurrentHun, topEntry)) {
                    HeadsUpCoordinator.this.endNotifLifetimeExtension();
                    HeadsUpCoordinator.this.mCurrentHun = topEntry;
                    HeadsUpCoordinator.this.mNotifPromoter.invalidateList();
                    HeadsUpCoordinator.this.mNotifSection.invalidateList();
                }
                if (!b) {
                    HeadsUpCoordinator.this.mHeadsUpViewBinder.unbindHeadsUpView(notificationEntry);
                }
            }
        };
        this.mHeadsUpManager = mHeadsUpManager;
        this.mHeadsUpViewBinder = mHeadsUpViewBinder;
        this.mNotificationInterruptStateProvider = mNotificationInterruptStateProvider;
        this.mRemoteInputManager = mRemoteInputManager;
    }
    
    private void endNotifLifetimeExtension() {
        final NotificationEntry mNotifExtendingLifetime = this.mNotifExtendingLifetime;
        if (mNotifExtendingLifetime != null) {
            this.mEndLifetimeExtension.onEndLifetimeExtension(this.mLifetimeExtender, mNotifExtendingLifetime);
            this.mNotifExtendingLifetime = null;
        }
    }
    
    private boolean isCurrentlyShowingHun(final ListEntry listEntry) {
        return this.mCurrentHun == listEntry.getRepresentativeEntry();
    }
    
    private void onHeadsUpViewBound(final NotificationEntry notificationEntry) {
        this.mHeadsUpManager.showNotification(notificationEntry);
    }
    
    @Override
    public void attach(final NotifPipeline notifPipeline) {
        this.mHeadsUpManager.addListener(this.mOnHeadsUpChangedListener);
        notifPipeline.addCollectionListener(this.mNotifCollectionListener);
        notifPipeline.addPromoter(this.mNotifPromoter);
        notifPipeline.addNotificationLifetimeExtender(this.mLifetimeExtender);
    }
    
    @Override
    public NotifSection getSection() {
        return this.mNotifSection;
    }
}
