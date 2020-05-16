// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.util.ArrayMap;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Map;

public abstract class BindStage<Params> extends BindRequester
{
    private Map<NotificationEntry, Params> mContentParams;
    
    public BindStage() {
        this.mContentParams = (Map<NotificationEntry, Params>)new ArrayMap();
    }
    
    protected abstract void abortStage(final NotificationEntry p0, final ExpandableNotificationRow p1);
    
    final void createStageParams(final NotificationEntry notificationEntry) {
        this.mContentParams.put(notificationEntry, this.newStageParams());
    }
    
    final void deleteStageParams(final NotificationEntry notificationEntry) {
        this.mContentParams.remove(notificationEntry);
    }
    
    protected abstract void executeStage(final NotificationEntry p0, final ExpandableNotificationRow p1, final StageCallback p2);
    
    public final Params getStageParams(final NotificationEntry notificationEntry) {
        final Params value = this.mContentParams.get(notificationEntry);
        if (value != null) {
            return value;
        }
        throw new IllegalStateException(String.format("Entry does not have any stage parameters. key: %s", notificationEntry.getKey()));
    }
    
    protected abstract Params newStageParams();
    
    interface StageCallback
    {
        void onStageFinished(final NotificationEntry p0);
    }
}
