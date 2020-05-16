// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import java.util.Map;
import com.android.systemui.util.Utils;
import com.android.systemui.util.Assert;
import java.util.Stack;
import android.os.Trace;
import java.util.Iterator;
import com.android.systemui.statusbar.notification.stack.NotificationListItem;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.ArrayList;
import com.android.systemui.R$bool;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import java.util.List;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.HashMap;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import android.os.Handler;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.notification.stack.ForegroundServiceSectionController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.DynamicChildBindController;
import android.content.Context;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;

public class NotificationViewHierarchyManager implements Listener
{
    private final boolean mAlwaysExpandNonGroupedNotification;
    private final BubbleController mBubbleController;
    private final KeyguardBypassController mBypassController;
    private final Context mContext;
    private final DynamicChildBindController mDynamicChildBindController;
    private final DynamicPrivacyController mDynamicPrivacyController;
    private final NotificationEntryManager mEntryManager;
    private final ForegroundServiceSectionController mFgsSectionController;
    protected final NotificationGroupManager mGroupManager;
    private final Handler mHandler;
    private boolean mIsHandleDynamicPrivacyChangeScheduled;
    private NotificationListContainer mListContainer;
    protected final NotificationLockscreenUserManager mLockscreenUserManager;
    private boolean mPerformingUpdate;
    private NotificationPresenter mPresenter;
    private final SysuiStatusBarStateController mStatusBarStateController;
    private final HashMap<NotificationEntry, List<NotificationEntry>> mTmpChildOrderMap;
    protected final VisualStabilityManager mVisualStabilityManager;
    
    public NotificationViewHierarchyManager(final Context mContext, final Handler mHandler, final NotificationLockscreenUserManager mLockscreenUserManager, final NotificationGroupManager mGroupManager, final VisualStabilityManager mVisualStabilityManager, final StatusBarStateController statusBarStateController, final NotificationEntryManager mEntryManager, final KeyguardBypassController mBypassController, final BubbleController mBubbleController, final DynamicPrivacyController mDynamicPrivacyController, final ForegroundServiceSectionController mFgsSectionController, final DynamicChildBindController mDynamicChildBindController) {
        this.mTmpChildOrderMap = new HashMap<NotificationEntry, List<NotificationEntry>>();
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.mLockscreenUserManager = mLockscreenUserManager;
        this.mBypassController = mBypassController;
        this.mGroupManager = mGroupManager;
        this.mVisualStabilityManager = mVisualStabilityManager;
        this.mStatusBarStateController = (SysuiStatusBarStateController)statusBarStateController;
        this.mEntryManager = mEntryManager;
        this.mFgsSectionController = mFgsSectionController;
        this.mAlwaysExpandNonGroupedNotification = mContext.getResources().getBoolean(R$bool.config_alwaysExpandNonGroupedNotifications);
        this.mBubbleController = mBubbleController;
        (this.mDynamicPrivacyController = mDynamicPrivacyController).addListener((DynamicPrivacyController.Listener)this);
        this.mDynamicChildBindController = mDynamicChildBindController;
    }
    
    private void addNotificationChildrenAndSort() {
        final ArrayList<ExpandableNotificationRow> list = new ArrayList<ExpandableNotificationRow>();
        boolean b;
        for (int i = (b = false) ? 1 : 0; i < this.mListContainer.getContainerChildCount(); ++i) {
            final View containerChild = this.mListContainer.getContainerChildAt(i);
            if (containerChild instanceof ExpandableNotificationRow) {
                final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)containerChild;
                final List<ExpandableNotificationRow> notificationChildren = expandableNotificationRow.getNotificationChildren();
                final List<NotificationEntry> list2 = this.mTmpChildOrderMap.get(expandableNotificationRow.getEntry());
                for (int n = 0; list2 != null && n < list2.size(); ++n) {
                    final ExpandableNotificationRow row = list2.get(n).getRow();
                    if (notificationChildren == null || !notificationChildren.contains(row)) {
                        if (row.getParent() != null) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("trying to add a notification child that already has a parent. class:");
                            sb.append(row.getParent().getClass());
                            sb.append("\n child: ");
                            sb.append(row);
                            Log.wtf("NotificationViewHierarchyManager", sb.toString());
                            ((ViewGroup)row.getParent()).removeView((View)row);
                        }
                        this.mVisualStabilityManager.notifyViewAddition((View)row);
                        expandableNotificationRow.addChildNotification(row, n);
                        this.mListContainer.notifyGroupChildAdded(row);
                    }
                    list.add(row);
                }
                b |= expandableNotificationRow.applyChildOrder(list, this.mVisualStabilityManager, this.mEntryManager);
                list.clear();
            }
        }
        if (b) {
            this.mListContainer.generateChildOrderChangedEvent();
        }
    }
    
    private void beginUpdate() {
        if (this.mPerformingUpdate) {
            Log.wtf("NotificationViewHierarchyManager", "Re-entrant code during update", (Throwable)new Exception());
        }
        this.mPerformingUpdate = true;
    }
    
    private void endUpdate() {
        if (!this.mPerformingUpdate) {
            Log.wtf("NotificationViewHierarchyManager", "Manager state has become desynced", (Throwable)new Exception());
        }
        this.mPerformingUpdate = false;
    }
    
    private void onHandleDynamicPrivacyChanged() {
        this.mIsHandleDynamicPrivacyChangeScheduled = false;
        this.updateNotificationViews();
    }
    
    private void removeNotificationChildren() {
        final ArrayList<ExpandableNotificationRow> list = new ArrayList<ExpandableNotificationRow>();
        for (int i = 0; i < this.mListContainer.getContainerChildCount(); ++i) {
            final View containerChild = this.mListContainer.getContainerChildAt(i);
            if (containerChild instanceof ExpandableNotificationRow) {
                final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)containerChild;
                final List<ExpandableNotificationRow> notificationChildren = expandableNotificationRow.getNotificationChildren();
                final List<NotificationEntry> list2 = this.mTmpChildOrderMap.get(expandableNotificationRow.getEntry());
                if (notificationChildren != null) {
                    list.clear();
                    for (final ExpandableNotificationRow e : notificationChildren) {
                        if ((list2 == null || !list2.contains(e.getEntry())) && !e.keepInParent()) {
                            list.add(e);
                        }
                    }
                    for (final ExpandableNotificationRow expandableNotificationRow2 : list) {
                        expandableNotificationRow.removeChildNotification(expandableNotificationRow2);
                        if (this.mEntryManager.getActiveNotificationUnfiltered(expandableNotificationRow2.getEntry().getSbn().getKey()) == null) {
                            this.mListContainer.notifyGroupChildRemoved(expandableNotificationRow2, expandableNotificationRow.getChildrenContainer());
                        }
                    }
                }
            }
        }
    }
    
    private void updateRowStatesInternal() {
        Trace.beginSection("NotificationViewHierarchyManager#updateRowStates");
        final int containerChildCount = this.mListContainer.getContainerChildCount();
        final boolean onKeyguard = this.mStatusBarStateController.getState() == 1;
        int maxNotificationsWhileLocked;
        if (onKeyguard && !this.mBypassController.getBypassEnabled()) {
            maxNotificationsWhileLocked = this.mPresenter.getMaxNotificationsWhileLocked(true);
        }
        else {
            maxNotificationsWhileLocked = -1;
        }
        this.mListContainer.setMaxDisplayedNotifications(maxNotificationsWhileLocked);
        final Stack<ExpandableNotificationRow> stack = new Stack<ExpandableNotificationRow>();
        for (int i = containerChildCount - 1; i >= 0; --i) {
            final View containerChild = this.mListContainer.getContainerChildAt(i);
            if (containerChild instanceof ExpandableNotificationRow) {
                stack.push((ExpandableNotificationRow)containerChild);
            }
        }
        int n = 0;
        while (!stack.isEmpty()) {
            final ExpandableNotificationRow expandableNotificationRow = stack.pop();
            final NotificationEntry entry = expandableNotificationRow.getEntry();
            final boolean childInGroupWithSummary = this.mGroupManager.isChildInGroupWithSummary(entry.getSbn());
            expandableNotificationRow.setOnKeyguard(onKeyguard);
            if (!onKeyguard) {
                expandableNotificationRow.setSystemExpanded(this.mAlwaysExpandNonGroupedNotification || (n == 0 && !childInGroupWithSummary && !expandableNotificationRow.isLowPriority()));
            }
            final int userId = entry.getSbn().getUserId();
            final boolean b = this.mGroupManager.isSummaryOfSuppressedGroup(entry.getSbn()) && !entry.isRowRemoved();
            boolean shouldShowOnKeyguard;
            final boolean b2 = shouldShowOnKeyguard = this.mLockscreenUserManager.shouldShowOnKeyguard(entry);
            if (!b2) {
                shouldShowOnKeyguard = b2;
                if (this.mGroupManager.isChildInGroupWithSummary(entry.getSbn())) {
                    final NotificationEntry logicalGroupSummary = this.mGroupManager.getLogicalGroupSummary(entry.getSbn());
                    shouldShowOnKeyguard = b2;
                    if (logicalGroupSummary != null) {
                        shouldShowOnKeyguard = b2;
                        if (this.mLockscreenUserManager.shouldShowOnKeyguard(logicalGroupSummary)) {
                            shouldShowOnKeyguard = true;
                        }
                    }
                }
            }
            int n2;
            if (!b && !this.mLockscreenUserManager.shouldHideNotifications(userId) && (!onKeyguard || shouldShowOnKeyguard)) {
                final boolean b3 = entry.getRow().getVisibility() == 8;
                if (b3) {
                    entry.getRow().setVisibility(0);
                }
                n2 = n;
                if (!childInGroupWithSummary) {
                    n2 = n;
                    if (!entry.getRow().isRemoved()) {
                        if (b3) {
                            this.mListContainer.generateAddAnimation(entry.getRow(), shouldShowOnKeyguard ^ true);
                        }
                        n2 = n + 1;
                    }
                }
            }
            else {
                entry.getRow().setVisibility(8);
                n2 = n;
            }
            if (expandableNotificationRow.isSummaryWithChildren()) {
                final List<ExpandableNotificationRow> notificationChildren = expandableNotificationRow.getNotificationChildren();
                for (int j = notificationChildren.size() - 1; j >= 0; --j) {
                    stack.push(notificationChildren.get(j));
                }
            }
            expandableNotificationRow.showAppOpsIcons(entry.mActiveAppOps);
            expandableNotificationRow.setLastAudiblyAlertedMs(entry.getLastAudiblyAlertedMs());
            n = n2;
        }
        Trace.beginSection("NotificationPresenter#onUpdateRowStates");
        this.mPresenter.onUpdateRowStates();
        Trace.endSection();
        Trace.endSection();
    }
    
    @Override
    public void onDynamicPrivacyChanged() {
        if (this.mPerformingUpdate) {
            Log.w("NotificationViewHierarchyManager", "onDynamicPrivacyChanged made a re-entrant call");
        }
        if (!this.mIsHandleDynamicPrivacyChangeScheduled) {
            this.mIsHandleDynamicPrivacyChangeScheduled = true;
            this.mHandler.post((Runnable)new _$$Lambda$NotificationViewHierarchyManager$VZHW9NMJkqBLUXo3lkuiamxmEXo(this));
        }
    }
    
    public void setUpWithPresenter(final NotificationPresenter mPresenter, final NotificationListContainer mListContainer) {
        this.mPresenter = mPresenter;
        this.mListContainer = mListContainer;
    }
    
    public void updateNotificationViews() {
        Assert.isMainThread();
        this.beginUpdate();
        final List<NotificationEntry> visibleNotifications = this.mEntryManager.getVisibleNotifications();
        final ArrayList list = new ArrayList<ExpandableNotificationRow>(visibleNotifications.size());
        final int size = visibleNotifications.size();
        final int n = 0;
        int n2 = 0;
        while (true) {
            final boolean b = true;
            if (n2 >= size) {
                break;
            }
            final NotificationEntry notificationEntry = visibleNotifications.get(n2);
            final boolean useQsMediaPlayer = Utils.useQsMediaPlayer(this.mContext);
            if (!notificationEntry.isRowDismissed() && !notificationEntry.isRowRemoved() && (!notificationEntry.isMediaNotification() || !useQsMediaPlayer) && !this.mBubbleController.isBubbleNotificationSuppressedFromShade(notificationEntry)) {
                if (!this.mFgsSectionController.hasEntry(notificationEntry)) {
                    final int userId = notificationEntry.getSbn().getUserId();
                    final int currentUserId = this.mLockscreenUserManager.getCurrentUserId();
                    final boolean lockscreenPublicMode = this.mLockscreenUserManager.isLockscreenPublicMode(currentUserId);
                    boolean b3;
                    final boolean b2 = b3 = (lockscreenPublicMode || this.mLockscreenUserManager.isLockscreenPublicMode(userId));
                    Label_0251: {
                        if (b2) {
                            b3 = b2;
                            if (this.mDynamicPrivacyController.isDynamicallyUnlocked()) {
                                if (userId != currentUserId && userId != -1) {
                                    b3 = b2;
                                    if (this.mLockscreenUserManager.needsSeparateWorkChallenge(userId)) {
                                        break Label_0251;
                                    }
                                }
                                b3 = false;
                            }
                        }
                    }
                    final boolean needsRedaction = this.mLockscreenUserManager.needsRedaction(notificationEntry);
                    notificationEntry.setSensitive(b3 && needsRedaction, lockscreenPublicMode && !this.mLockscreenUserManager.userAllowsPrivateNotificationsInPublic(currentUserId));
                    notificationEntry.getRow().setNeedsRedaction(needsRedaction);
                    final boolean childInGroupWithSummary = this.mGroupManager.isChildInGroupWithSummary(notificationEntry.getSbn());
                    int n3 = b ? 1 : 0;
                    if (!this.mVisualStabilityManager.areGroupChangesAllowed()) {
                        if (!notificationEntry.hasFinishedInitialization()) {
                            n3 = (b ? 1 : 0);
                        }
                        else {
                            n3 = 0;
                        }
                    }
                    final NotificationEntry groupSummary = this.mGroupManager.getGroupSummary(notificationEntry.getSbn());
                    boolean b4 = childInGroupWithSummary;
                    NotificationEntry entry = groupSummary;
                    Label_0518: {
                        if (n3 == 0) {
                            final boolean childInGroup = notificationEntry.isChildInGroup();
                            if (childInGroupWithSummary && !childInGroup) {
                                this.mVisualStabilityManager.addGroupChangesAllowedCallback((VisualStabilityManager.Callback)this.mEntryManager);
                                entry = groupSummary;
                            }
                            else {
                                b4 = childInGroupWithSummary;
                                entry = groupSummary;
                                if (childInGroupWithSummary) {
                                    break Label_0518;
                                }
                                b4 = childInGroupWithSummary;
                                entry = groupSummary;
                                if (!childInGroup) {
                                    break Label_0518;
                                }
                                b4 = childInGroupWithSummary;
                                entry = groupSummary;
                                if (!this.mGroupManager.isLogicalGroupExpanded(notificationEntry.getSbn())) {
                                    break Label_0518;
                                }
                                entry = notificationEntry.getRow().getNotificationParent().getEntry();
                                this.mVisualStabilityManager.addGroupChangesAllowedCallback((VisualStabilityManager.Callback)this.mEntryManager);
                            }
                            b4 = childInGroup;
                        }
                    }
                    if (b4) {
                        List<NotificationEntry> value;
                        if ((value = this.mTmpChildOrderMap.get(entry)) == null) {
                            value = new ArrayList<NotificationEntry>();
                            this.mTmpChildOrderMap.put(entry, value);
                        }
                        value.add(notificationEntry);
                    }
                    else {
                        list.add(notificationEntry.getRow());
                    }
                }
            }
            ++n2;
        }
        final ArrayList<ExpandableNotificationRow> list2 = new ArrayList<ExpandableNotificationRow>();
        for (int i = 0; i < this.mListContainer.getContainerChildCount(); ++i) {
            final View containerChild = this.mListContainer.getContainerChildAt(i);
            if (!list.contains(containerChild) && containerChild instanceof ExpandableNotificationRow) {
                final ExpandableNotificationRow e = (ExpandableNotificationRow)containerChild;
                if (!e.isBlockingHelperShowing()) {
                    list2.add(e);
                }
            }
        }
        for (final ExpandableNotificationRow expandableNotificationRow : list2) {
            if (this.mEntryManager.getPendingOrActiveNotif(expandableNotificationRow.getEntry().getKey()) != null) {
                this.mListContainer.setChildTransferInProgress(true);
            }
            if (expandableNotificationRow.isSummaryWithChildren()) {
                expandableNotificationRow.removeAllChildren();
            }
            this.mListContainer.removeContainerView((View)expandableNotificationRow);
            this.mListContainer.setChildTransferInProgress(false);
        }
        this.removeNotificationChildren();
        int n4;
        for (int j = 0; j < list.size(); j = n4 + 1) {
            final View o = (View)list.get(j);
            if (o.getParent() == null) {
                this.mVisualStabilityManager.notifyViewAddition(o);
                this.mListContainer.addContainerView(o);
                n4 = j;
            }
            else {
                n4 = j;
                if (!this.mListContainer.containsView(o)) {
                    list.remove(o);
                    n4 = j - 1;
                }
            }
        }
        this.addNotificationChildrenAndSort();
        int index = 0;
        for (int k = n; k < this.mListContainer.getContainerChildCount(); ++k) {
            final View containerChild2 = this.mListContainer.getContainerChildAt(k);
            if (containerChild2 instanceof ExpandableNotificationRow) {
                if (!((ExpandableNotificationRow)containerChild2).isBlockingHelperShowing()) {
                    final ExpandableNotificationRow expandableNotificationRow2 = list.get(index);
                    if (containerChild2 != expandableNotificationRow2) {
                        if (this.mVisualStabilityManager.canReorderNotification(expandableNotificationRow2)) {
                            this.mListContainer.changeViewPosition(expandableNotificationRow2, k);
                        }
                        else {
                            this.mVisualStabilityManager.addReorderingAllowedCallback((VisualStabilityManager.Callback)this.mEntryManager);
                        }
                    }
                    ++index;
                }
            }
        }
        this.mDynamicChildBindController.updateChildContentViews(this.mTmpChildOrderMap);
        this.mVisualStabilityManager.onReorderingFinished();
        this.mTmpChildOrderMap.clear();
        this.updateRowStatesInternal();
        this.mListContainer.onNotificationViewUpdateFinished();
        this.endUpdate();
    }
    
    public void updateRowStates() {
        Assert.isMainThread();
        this.beginUpdate();
        this.updateRowStatesInternal();
        this.endUpdate();
    }
}
