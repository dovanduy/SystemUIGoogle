// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;

public class HideLocallyDismissedNotifsCoordinator implements Coordinator
{
    private final NotifFilter mFilter;
    
    public HideLocallyDismissedNotifsCoordinator() {
        this.mFilter = new NotifFilter("HideLocallyDismissedNotifsFilter") {
            @Override
            public boolean shouldFilterOut(final NotificationEntry notificationEntry, final long n) {
                return notificationEntry.getDismissState() != NotificationEntry.DismissState.NOT_DISMISSED;
            }
        };
    }
    
    @Override
    public void attach(final NotifPipeline notifPipeline) {
        notifPipeline.addPreGroupFilter(this.mFilter);
    }
}
