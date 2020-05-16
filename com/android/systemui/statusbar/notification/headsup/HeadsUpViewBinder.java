// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.headsup;

import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.row.RowContentBindParams;
import android.util.ArrayMap;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import androidx.core.os.CancellationSignal;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Map;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.internal.util.NotificationMessagingUtil;

public class HeadsUpViewBinder
{
    private final NotificationMessagingUtil mNotificationMessagingUtil;
    private NotificationPresenter mNotificationPresenter;
    private final Map<NotificationEntry, CancellationSignal> mOngoingBindCallbacks;
    private final RowContentBindStage mStage;
    
    HeadsUpViewBinder(final NotificationMessagingUtil mNotificationMessagingUtil, final RowContentBindStage mStage) {
        this.mOngoingBindCallbacks = (Map<NotificationEntry, CancellationSignal>)new ArrayMap();
        this.mNotificationMessagingUtil = mNotificationMessagingUtil;
        this.mStage = mStage;
    }
    
    public void abortBindCallback(final NotificationEntry notificationEntry) {
        final CancellationSignal cancellationSignal = this.mOngoingBindCallbacks.remove(notificationEntry);
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
    }
    
    public void bindHeadsUpView(final NotificationEntry notificationEntry, final NotifBindPipeline.BindCallback bindCallback) {
        final RowContentBindParams rowContentBindParams = this.mStage.getStageParams(notificationEntry);
        rowContentBindParams.setUseIncreasedHeadsUpHeight(this.mNotificationMessagingUtil.isImportantMessaging(notificationEntry.getSbn(), notificationEntry.getImportance()) && !this.mNotificationPresenter.isPresenterFullyCollapsed());
        rowContentBindParams.requireContentViews(4);
        final CancellationSignal requestRebind = this.mStage.requestRebind(notificationEntry, new _$$Lambda$HeadsUpViewBinder$B_uJuDkCVSlJGG99HdbkSW_h3ZY(rowContentBindParams, bindCallback));
        this.abortBindCallback(notificationEntry);
        this.mOngoingBindCallbacks.put(notificationEntry, requestRebind);
    }
    
    public void setPresenter(final NotificationPresenter mNotificationPresenter) {
        this.mNotificationPresenter = mNotificationPresenter;
    }
    
    public void unbindHeadsUpView(final NotificationEntry notificationEntry) {
        this.abortBindCallback(notificationEntry);
        this.mStage.getStageParams(notificationEntry).markContentViewsFreeable(4);
        this.mStage.requestRebind(notificationEntry, null);
    }
}
