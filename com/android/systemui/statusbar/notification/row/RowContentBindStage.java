// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public class RowContentBindStage extends BindStage<RowContentBindParams>
{
    private final NotificationRowContentBinder mBinder;
    private final RowContentBindStageLogger mLogger;
    private final NotifInflationErrorManager mNotifInflationErrorManager;
    
    RowContentBindStage(final NotificationRowContentBinder mBinder, final NotifInflationErrorManager mNotifInflationErrorManager, final RowContentBindStageLogger mLogger) {
        this.mBinder = mBinder;
        this.mNotifInflationErrorManager = mNotifInflationErrorManager;
        this.mLogger = mLogger;
    }
    
    @Override
    protected void abortStage(final NotificationEntry notificationEntry, final ExpandableNotificationRow expandableNotificationRow) {
        this.mBinder.cancelBind(notificationEntry, expandableNotificationRow);
    }
    
    @Override
    protected void executeStage(final NotificationEntry notificationEntry, final ExpandableNotificationRow expandableNotificationRow, final StageCallback stageCallback) {
        final RowContentBindParams rowContentBindParams = this.getStageParams(notificationEntry);
        this.mLogger.logStageParams(notificationEntry.getKey(), rowContentBindParams.toString());
        final int contentViews = rowContentBindParams.getContentViews();
        final int dirtyContentViews = rowContentBindParams.getDirtyContentViews();
        this.mBinder.unbindContent(notificationEntry, expandableNotificationRow, contentViews ^ 0xF);
        final NotificationRowContentBinder.BindParams bindParams = new NotificationRowContentBinder.BindParams();
        bindParams.isLowPriority = rowContentBindParams.useLowPriority();
        bindParams.isChildInGroup = rowContentBindParams.useChildInGroup();
        bindParams.usesIncreasedHeight = rowContentBindParams.useIncreasedHeight();
        bindParams.usesIncreasedHeadsUpHeight = rowContentBindParams.useIncreasedHeadsUpHeight();
        final boolean needsReinflation = rowContentBindParams.needsReinflation();
        final NotificationRowContentBinder.InflationCallback inflationCallback = new NotificationRowContentBinder.InflationCallback() {
            @Override
            public void handleInflationException(final NotificationEntry notificationEntry, final Exception ex) {
                RowContentBindStage.this.mNotifInflationErrorManager.setInflationError(notificationEntry, ex);
            }
            
            @Override
            public void onAsyncInflationFinished(final NotificationEntry notificationEntry) {
                RowContentBindStage.this.mNotifInflationErrorManager.clearInflationError(notificationEntry);
                RowContentBindStage.this.getStageParams(notificationEntry).clearDirtyContentViews();
                stageCallback.onStageFinished(notificationEntry);
            }
        };
        this.mBinder.cancelBind(notificationEntry, expandableNotificationRow);
        this.mBinder.bindContent(notificationEntry, expandableNotificationRow, dirtyContentViews & contentViews, bindParams, needsReinflation, (NotificationRowContentBinder.InflationCallback)inflationCallback);
    }
    
    @Override
    protected RowContentBindParams newStageParams() {
        return new RowContentBindParams();
    }
}
