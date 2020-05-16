// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.util.Objects;
import android.service.notification.StatusBarNotification;
import java.util.Iterator;
import java.util.ArrayList;
import android.os.SystemClock;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.row.RowContentBindParams;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.AlertingNotificationManager;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import android.util.ArrayMap;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;

public class NotificationGroupAlertTransferHelper implements OnHeadsUpChangedListener, StateListener
{
    private NotificationEntryManager mEntryManager;
    private final ArrayMap<String, GroupAlertEntry> mGroupAlertEntries;
    private final NotificationGroupManager mGroupManager;
    private HeadsUpManager mHeadsUpManager;
    private boolean mIsDozing;
    private final NotificationEntryListener mNotificationEntryListener;
    private final NotificationGroupManager.OnGroupChangeListener mOnGroupChangeListener;
    private final ArrayMap<String, PendingAlertInfo> mPendingAlerts;
    private final RowContentBindStage mRowContentBindStage;
    
    public NotificationGroupAlertTransferHelper(final RowContentBindStage mRowContentBindStage) {
        this.mGroupAlertEntries = (ArrayMap<String, GroupAlertEntry>)new ArrayMap();
        this.mPendingAlerts = (ArrayMap<String, PendingAlertInfo>)new ArrayMap();
        this.mGroupManager = Dependency.get(NotificationGroupManager.class);
        this.mOnGroupChangeListener = new NotificationGroupManager.OnGroupChangeListener() {
            @Override
            public void onGroupCreated(final NotificationGroup notificationGroup, final String s) {
                NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.put((Object)s, (Object)new GroupAlertEntry(notificationGroup));
            }
            
            @Override
            public void onGroupRemoved(final NotificationGroup notificationGroup, final String s) {
                NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.remove((Object)s);
            }
            
            @Override
            public void onGroupSuppressionChanged(final NotificationGroup notificationGroup, final boolean b) {
                if (b) {
                    if (NotificationGroupAlertTransferHelper.this.mHeadsUpManager.isAlerting(notificationGroup.summary.getKey())) {
                        final NotificationGroupAlertTransferHelper this$0 = NotificationGroupAlertTransferHelper.this;
                        this$0.handleSuppressedSummaryAlerted(notificationGroup.summary, this$0.mHeadsUpManager);
                    }
                }
                else {
                    if (notificationGroup.summary == null) {
                        return;
                    }
                    final GroupAlertEntry groupAlertEntry = (GroupAlertEntry)NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.get((Object)NotificationGroupAlertTransferHelper.this.mGroupManager.getGroupKey(notificationGroup.summary.getSbn()));
                    if (groupAlertEntry.mAlertSummaryOnNextAddition) {
                        if (!NotificationGroupAlertTransferHelper.this.mHeadsUpManager.isAlerting(notificationGroup.summary.getKey())) {
                            final NotificationGroupAlertTransferHelper this$2 = NotificationGroupAlertTransferHelper.this;
                            this$2.alertNotificationWhenPossible(notificationGroup.summary, this$2.mHeadsUpManager);
                        }
                        groupAlertEntry.mAlertSummaryOnNextAddition = false;
                    }
                    else {
                        NotificationGroupAlertTransferHelper.this.checkShouldTransferBack(groupAlertEntry);
                    }
                }
            }
        };
        this.mNotificationEntryListener = new NotificationEntryListener() {
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final NotificationVisibility notificationVisibility, final boolean b, final int n) {
                NotificationGroupAlertTransferHelper.this.mPendingAlerts.remove((Object)notificationEntry.getKey());
            }
            
            @Override
            public void onPendingEntryAdded(final NotificationEntry notificationEntry) {
                final GroupAlertEntry groupAlertEntry = (GroupAlertEntry)NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.get((Object)NotificationGroupAlertTransferHelper.this.mGroupManager.getGroupKey(notificationEntry.getSbn()));
                if (groupAlertEntry != null) {
                    NotificationGroupAlertTransferHelper.this.checkShouldTransferBack(groupAlertEntry);
                }
            }
        };
        Dependency.get(StatusBarStateController.class).addCallback((StatusBarStateController.StateListener)this);
        this.mRowContentBindStage = mRowContentBindStage;
    }
    
    private void alertNotificationWhenPossible(final NotificationEntry notificationEntry, final AlertingNotificationManager alertingNotificationManager) {
        final int contentFlag = alertingNotificationManager.getContentFlag();
        final RowContentBindParams rowContentBindParams = this.mRowContentBindStage.getStageParams(notificationEntry);
        if ((rowContentBindParams.getContentViews() & contentFlag) == 0x0) {
            this.mPendingAlerts.put((Object)notificationEntry.getKey(), (Object)new PendingAlertInfo(notificationEntry));
            rowContentBindParams.requireContentViews(contentFlag);
            this.mRowContentBindStage.requestRebind(notificationEntry, new _$$Lambda$NotificationGroupAlertTransferHelper$eMYMUXNB2yOw4q9wL9gYe0M0Ark(this, notificationEntry, contentFlag));
            return;
        }
        if (alertingNotificationManager.isAlerting(notificationEntry.getKey())) {
            alertingNotificationManager.updateNotification(notificationEntry.getKey(), true);
        }
        else {
            alertingNotificationManager.showNotification(notificationEntry);
        }
    }
    
    private void checkShouldTransferBack(final GroupAlertEntry groupAlertEntry) {
        if (SystemClock.elapsedRealtime() - groupAlertEntry.mLastAlertTransferTime < 300L) {
            final NotificationEntry summary = groupAlertEntry.mGroup.summary;
            if (!this.onlySummaryAlerts(summary)) {
                return;
            }
            final ArrayList<NotificationEntry> logicalChildren = this.mGroupManager.getLogicalChildren(summary.getSbn());
            final int size = logicalChildren.size();
            final int pendingChildrenNotAlerting = this.getPendingChildrenNotAlerting(groupAlertEntry.mGroup);
            final int n = size + pendingChildrenNotAlerting;
            if (n <= 1) {
                return;
            }
            final int n2 = 0;
            int n3;
            for (int i = n3 = 0; i < logicalChildren.size(); ++i) {
                final NotificationEntry notificationEntry = logicalChildren.get(i);
                int n4 = n3;
                if (this.onlySummaryAlerts(notificationEntry)) {
                    n4 = n3;
                    if (this.mHeadsUpManager.isAlerting(notificationEntry.getKey())) {
                        this.mHeadsUpManager.removeNotification(notificationEntry.getKey(), true);
                        n4 = 1;
                    }
                }
                n3 = n4;
                if (this.mPendingAlerts.containsKey((Object)notificationEntry.getKey())) {
                    ((PendingAlertInfo)this.mPendingAlerts.get((Object)notificationEntry.getKey())).mAbortOnInflation = true;
                    n3 = 1;
                }
            }
            if (n3 != 0 && !this.mHeadsUpManager.isAlerting(summary.getKey())) {
                int n5 = n2;
                if (n - pendingChildrenNotAlerting > 1) {
                    n5 = 1;
                }
                if (n5 != 0) {
                    this.alertNotificationWhenPossible(summary, this.mHeadsUpManager);
                }
                else {
                    groupAlertEntry.mAlertSummaryOnNextAddition = true;
                }
                groupAlertEntry.mLastAlertTransferTime = 0L;
            }
        }
    }
    
    private int getPendingChildrenNotAlerting(final NotificationGroupManager.NotificationGroup notificationGroup) {
        final NotificationEntryManager mEntryManager = this.mEntryManager;
        int n = 0;
        if (mEntryManager == null) {
            return 0;
        }
        for (final NotificationEntry notificationEntry : mEntryManager.getPendingNotificationsIterator()) {
            if (this.isPendingNotificationInGroup(notificationEntry, notificationGroup) && this.onlySummaryAlerts(notificationEntry)) {
                ++n;
            }
        }
        return n;
    }
    
    private void handleSuppressedSummaryAlerted(final NotificationEntry notificationEntry, final AlertingNotificationManager alertingNotificationManager) {
        final StatusBarNotification sbn = notificationEntry.getSbn();
        final GroupAlertEntry groupAlertEntry = (GroupAlertEntry)this.mGroupAlertEntries.get((Object)this.mGroupManager.getGroupKey(sbn));
        if (this.mGroupManager.isSummaryOfSuppressedGroup(notificationEntry.getSbn()) && alertingNotificationManager.isAlerting(sbn.getKey())) {
            if (groupAlertEntry != null) {
                if (this.pendingInflationsWillAddChildren(groupAlertEntry.mGroup)) {
                    return;
                }
                final NotificationEntry notificationEntry2 = this.mGroupManager.getLogicalChildren(notificationEntry.getSbn()).iterator().next();
                if (notificationEntry2 != null && !notificationEntry2.getRow().keepInParent() && !notificationEntry2.isRowRemoved()) {
                    if (!notificationEntry2.isRowDismissed()) {
                        if (!alertingNotificationManager.isAlerting(notificationEntry2.getKey()) && this.onlySummaryAlerts(notificationEntry)) {
                            groupAlertEntry.mLastAlertTransferTime = SystemClock.elapsedRealtime();
                        }
                        this.transferAlertState(notificationEntry, notificationEntry2, alertingNotificationManager);
                    }
                }
            }
        }
    }
    
    private boolean isPendingNotificationInGroup(final NotificationEntry notificationEntry, final NotificationGroupManager.NotificationGroup notificationGroup) {
        final String groupKey = this.mGroupManager.getGroupKey(notificationGroup.summary.getSbn());
        return this.mGroupManager.isGroupChild(notificationEntry.getSbn()) && Objects.equals(this.mGroupManager.getGroupKey(notificationEntry.getSbn()), groupKey) && !notificationGroup.children.containsKey(notificationEntry.getKey());
    }
    
    private void onAlertStateChanged(final NotificationEntry notificationEntry, final boolean b, final AlertingNotificationManager alertingNotificationManager) {
        if (b && this.mGroupManager.isSummaryOfSuppressedGroup(notificationEntry.getSbn())) {
            this.handleSuppressedSummaryAlerted(notificationEntry, alertingNotificationManager);
        }
    }
    
    private boolean onlySummaryAlerts(final NotificationEntry notificationEntry) {
        final int groupAlertBehavior = notificationEntry.getSbn().getNotification().getGroupAlertBehavior();
        boolean b = true;
        if (groupAlertBehavior != 1) {
            b = false;
        }
        return b;
    }
    
    private boolean pendingInflationsWillAddChildren(final NotificationGroupManager.NotificationGroup notificationGroup) {
        final NotificationEntryManager mEntryManager = this.mEntryManager;
        if (mEntryManager == null) {
            return false;
        }
        final Iterator<NotificationEntry> iterator = mEntryManager.getPendingNotificationsIterator().iterator();
        while (iterator.hasNext()) {
            if (this.isPendingNotificationInGroup(iterator.next(), notificationGroup)) {
                return true;
            }
        }
        return false;
    }
    
    private void transferAlertState(final NotificationEntry notificationEntry, final NotificationEntry notificationEntry2, final AlertingNotificationManager alertingNotificationManager) {
        alertingNotificationManager.removeNotification(notificationEntry.getKey(), true);
        this.alertNotificationWhenPossible(notificationEntry2, alertingNotificationManager);
    }
    
    public void bind(final NotificationEntryManager mEntryManager, final NotificationGroupManager notificationGroupManager) {
        if (this.mEntryManager == null) {
            (this.mEntryManager = mEntryManager).addNotificationEntryListener(this.mNotificationEntryListener);
            notificationGroupManager.addOnGroupChangeListener(this.mOnGroupChangeListener);
            return;
        }
        throw new IllegalStateException("Already bound.");
    }
    
    @Override
    public void onDozingChanged(final boolean mIsDozing) {
        if (this.mIsDozing != mIsDozing) {
            for (final GroupAlertEntry groupAlertEntry : this.mGroupAlertEntries.values()) {
                groupAlertEntry.mLastAlertTransferTime = 0L;
                groupAlertEntry.mAlertSummaryOnNextAddition = false;
            }
        }
        this.mIsDozing = mIsDozing;
    }
    
    @Override
    public void onHeadsUpStateChanged(final NotificationEntry notificationEntry, final boolean b) {
        this.onAlertStateChanged(notificationEntry, b, this.mHeadsUpManager);
    }
    
    @Override
    public void onStateChanged(final int n) {
    }
    
    public void setHeadsUpManager(final HeadsUpManager mHeadsUpManager) {
        this.mHeadsUpManager = mHeadsUpManager;
    }
    
    private static class GroupAlertEntry
    {
        boolean mAlertSummaryOnNextAddition;
        final NotificationGroupManager.NotificationGroup mGroup;
        long mLastAlertTransferTime;
        
        GroupAlertEntry(final NotificationGroupManager.NotificationGroup mGroup) {
            this.mGroup = mGroup;
        }
    }
    
    private class PendingAlertInfo
    {
        boolean mAbortOnInflation;
        final NotificationEntry mEntry;
        final StatusBarNotification mOriginalNotification;
        
        PendingAlertInfo(final NotificationGroupAlertTransferHelper notificationGroupAlertTransferHelper, final NotificationEntry mEntry) {
            this.mOriginalNotification = mEntry.getSbn();
            this.mEntry = mEntry;
        }
        
        private boolean isStillValid() {
            return !this.mAbortOnInflation && this.mEntry.getSbn().getGroupKey() == this.mOriginalNotification.getGroupKey() && this.mEntry.getSbn().getNotification().isGroupSummary() == this.mOriginalNotification.getNotification().isGroupSummary();
        }
    }
}
