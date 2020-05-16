// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.headsup;

import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;

public class HeadsUpBindController
{
    private NotifCollectionListener mCollectionListener;
    private final HeadsUpViewBinder mHeadsUpViewBinder;
    private final NotificationInterruptStateProvider mInterruptStateProvider;
    private OnHeadsUpChangedListener mOnHeadsUpChangedListener;
    
    HeadsUpBindController(final HeadsUpViewBinder mHeadsUpViewBinder, final NotificationInterruptStateProvider mInterruptStateProvider) {
        this.mCollectionListener = new NotifCollectionListener() {
            @Override
            public void onEntryAdded(final NotificationEntry notificationEntry) {
                if (HeadsUpBindController.this.mInterruptStateProvider.shouldHeadsUp(notificationEntry)) {
                    HeadsUpBindController.this.mHeadsUpViewBinder.bindHeadsUpView(notificationEntry, null);
                }
            }
            
            @Override
            public void onEntryCleanUp(final NotificationEntry notificationEntry) {
                HeadsUpBindController.this.mHeadsUpViewBinder.abortBindCallback(notificationEntry);
            }
            
            @Override
            public void onEntryUpdated(final NotificationEntry notificationEntry) {
                if (HeadsUpBindController.this.mInterruptStateProvider.shouldHeadsUp(notificationEntry)) {
                    HeadsUpBindController.this.mHeadsUpViewBinder.bindHeadsUpView(notificationEntry, null);
                }
            }
        };
        this.mOnHeadsUpChangedListener = new OnHeadsUpChangedListener() {
            @Override
            public void onHeadsUpStateChanged(final NotificationEntry notificationEntry, final boolean b) {
                if (!b) {
                    HeadsUpBindController.this.mHeadsUpViewBinder.unbindHeadsUpView(notificationEntry);
                }
            }
        };
        this.mInterruptStateProvider = mInterruptStateProvider;
        this.mHeadsUpViewBinder = mHeadsUpViewBinder;
    }
    
    public void attach(final NotificationEntryManager notificationEntryManager, final HeadsUpManager headsUpManager) {
        notificationEntryManager.addCollectionListener(this.mCollectionListener);
        headsUpManager.addListener(this.mOnHeadsUpChangedListener);
    }
}
