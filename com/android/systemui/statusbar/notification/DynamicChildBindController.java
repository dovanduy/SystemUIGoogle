// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.row.RowContentBindParams;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;

public class DynamicChildBindController
{
    private final int mChildBindCutoff;
    private final RowContentBindStage mStage;
    
    public DynamicChildBindController(final RowContentBindStage rowContentBindStage) {
        this(rowContentBindStage, 9);
    }
    
    DynamicChildBindController(final RowContentBindStage mStage, final int mChildBindCutoff) {
        this.mStage = mStage;
        this.mChildBindCutoff = mChildBindCutoff;
    }
    
    private void bindChildContent(final NotificationEntry notificationEntry) {
        final RowContentBindParams rowContentBindParams = this.mStage.getStageParams(notificationEntry);
        rowContentBindParams.requireContentViews(1);
        rowContentBindParams.requireContentViews(2);
        this.mStage.requestRebind(notificationEntry, null);
    }
    
    private void freeChildContent(final NotificationEntry notificationEntry) {
        final RowContentBindParams rowContentBindParams = this.mStage.getStageParams(notificationEntry);
        rowContentBindParams.markContentViewsFreeable(1);
        rowContentBindParams.markContentViewsFreeable(2);
        this.mStage.requestRebind(notificationEntry, null);
    }
    
    private boolean hasChildContent(final NotificationEntry notificationEntry) {
        final ExpandableNotificationRow row = notificationEntry.getRow();
        return row.getPrivateLayout().getContractedChild() != null || row.getPrivateLayout().getExpandedChild() != null;
    }
    
    public void updateChildContentViews(final Map<NotificationEntry, List<NotificationEntry>> map) {
        final Iterator<NotificationEntry> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            final List<NotificationEntry> list = map.get(iterator.next());
            for (int i = 0; i < list.size(); ++i) {
                final NotificationEntry notificationEntry = list.get(i);
                if (i >= this.mChildBindCutoff) {
                    if (this.hasChildContent(notificationEntry)) {
                        this.freeChildContent(notificationEntry);
                    }
                }
                else if (!this.hasChildContent(notificationEntry)) {
                    this.bindChildContent(notificationEntry);
                }
            }
        }
    }
}
