// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import android.app.ActivityManager$RunningTaskInfo;
import android.content.res.Configuration;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.NotificationRemoveInterceptor;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import android.util.ArraySet;
import android.view.ViewGroup;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import android.widget.FrameLayout$LayoutParams;
import com.android.systemui.R$id;
import android.app.PendingIntent;
import android.content.pm.ActivityInfo;
import com.android.systemui.statusbar.phone.StatusBar;
import android.util.Log;
import android.service.notification.NotificationListenerService$RankingMap;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.internal.statusbar.IStatusBarService$Stub;
import android.os.ServiceManager;
import com.android.systemui.shared.system.PinnedStackListenerForwarder;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import android.service.notification.ZenModeConfig;
import android.app.Notification;
import java.util.Iterator;
import android.os.RemoteException;
import java.util.Collection;
import android.util.Pair;
import java.util.ArrayList;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.ZenModeController;
import java.util.HashSet;
import android.service.notification.NotificationListenerService$Ranking;
import android.graphics.Rect;
import com.android.systemui.model.SysUiState;
import android.util.SparseSetArray;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.util.FloatingContentCoordinator;
import android.content.Context;
import java.util.List;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.policy.ConfigurationController;

public class BubbleController implements ConfigurationListener, Dumpable
{
    private IStatusBarService mBarService;
    private BubbleData mBubbleData;
    private final BubbleData.Listener mBubbleDataListener;
    private BubbleIconFactory mBubbleIconFactory;
    private final List<NotifCallback> mCallbacks;
    private final Context mContext;
    private int mCurrentUserId;
    private BubbleExpandListener mExpandListener;
    private final FloatingContentCoordinator mFloatingContentCoordinator;
    private boolean mInflateSynchronously;
    private final NotifPipeline mNotifPipeline;
    private final NotificationLockscreenUserManager mNotifUserManager;
    private final NotificationEntryManager mNotificationEntryManager;
    private final NotificationGroupManager mNotificationGroupManager;
    private final NotificationInterruptStateProvider mNotificationInterruptStateProvider;
    private final NotificationShadeWindowController mNotificationShadeWindowController;
    private int mOrientation;
    private Runnable mOverflowCallback;
    private final SparseSetArray<String> mSavedBubbleKeysPerUser;
    private BubbleStackView mStackView;
    private BubbleStateChangeListener mStateChangeListener;
    private StatusBarStateListener mStatusBarStateListener;
    private BubbleStackView.SurfaceSynchronizer mSurfaceSynchronizer;
    private SysUiState mSysUiState;
    private final BubbleTaskStackListener mTaskStackListener;
    private Rect mTempRect;
    private NotificationListenerService$Ranking mTmpRanking;
    private final HashSet<String> mUserBlockedBubbles;
    private final HashSet<String> mUserCreatedBubbles;
    private final ZenModeController mZenModeController;
    
    public BubbleController(final Context mContext, final NotificationShadeWindowController mNotificationShadeWindowController, final StatusBarStateController statusBarStateController, final ShadeController shadeController, final BubbleData mBubbleData, final BubbleStackView.SurfaceSynchronizer mSurfaceSynchronizer, final ConfigurationController configurationController, final NotificationInterruptStateProvider mNotificationInterruptStateProvider, final ZenModeController mZenModeController, final NotificationLockscreenUserManager mNotifUserManager, final NotificationGroupManager mNotificationGroupManager, final NotificationEntryManager mNotificationEntryManager, final NotifPipeline mNotifPipeline, final FeatureFlags featureFlags, final DumpManager dumpManager, final FloatingContentCoordinator mFloatingContentCoordinator, final SysUiState mSysUiState) {
        this.mOverflowCallback = null;
        this.mTempRect = new Rect();
        this.mOrientation = 0;
        this.mCallbacks = new ArrayList<NotifCallback>();
        this.mBubbleDataListener = new BubbleData.Listener() {
            @Override
            public void applyUpdate(Update iterator) {
                if (BubbleController.this.mOverflowCallback != null) {
                    BubbleController.this.mOverflowCallback.run();
                }
                if (iterator.expandedChanged && !iterator.expanded) {
                    BubbleController.this.mStackView.setExpanded(false);
                }
                final Iterator<Pair> iterator2 = new ArrayList<Pair>(iterator.removedBubbles).iterator();
            Label_0234_Outer:
                while (true) {
                    Label_0307: {
                        if (!iterator2.hasNext()) {
                            break Label_0307;
                        }
                        final Pair pair = iterator2.next();
                        final Bubble bubble = (Bubble)pair.first;
                        final int intValue = (int)pair.second;
                        BubbleController.this.mStackView.removeBubble(bubble);
                        if (intValue == 8) {
                            continue Label_0234_Outer;
                        }
                    Label_0453_Outer:
                        while (true) {
                            if (!BubbleController.this.mBubbleData.hasBubbleWithKey(bubble.getKey()) && !bubble.showInShade()) {
                                final Iterator<NotifCallback> iterator3 = (Iterator<NotifCallback>)BubbleController.this.mCallbacks.iterator();
                                while (iterator3.hasNext()) {
                                    iterator3.next().removeNotification(bubble.getEntry(), 2);
                                }
                                break Label_0234;
                            }
                            final Notification notification = bubble.getEntry().getSbn().getNotification();
                            notification.flags &= 0xFFFFEFFF;
                            try {
                                BubbleController.this.mBarService.onNotificationBubbleChanged(bubble.getKey(), false);
                                if (BubbleController.this.mBubbleData.getBubblesInGroup(bubble.getEntry().getSbn().getGroupKey()).isEmpty()) {
                                    final Iterator<NotifCallback> iterator4 = (Iterator<NotifCallback>)BubbleController.this.mCallbacks.iterator();
                                    while (iterator4.hasNext()) {
                                        iterator4.next().maybeCancelSummary(bubble.getEntry());
                                    }
                                    continue Label_0234_Outer;
                                }
                                continue Label_0234_Outer;
                                // iftrue(Label_0440:, !iterator.expandedChanged || !iterator.expanded)
                                // iftrue(Label_0370:, !iterator.orderChanged)
                                // iftrue(Label_0415:, !iterator.selectionChanged)
                                // iftrue(Label_0328:, iterator.addedBubble == null)
                                // iftrue(Label_0481:, !iterator.hasNext())
                                // iftrue(Label_0349:, iterator.updatedBubble == null)
                            Block_17_Outer:
                                while (true) {
                                    Block_20: {
                                        while (true) {
                                            Block_16: {
                                                Block_19:Label_0349_Outer:Label_0370_Outer:Block_14_Outer:
                                                while (true) {
                                                    break Block_19;
                                                    while (true) {
                                                    Block_13:
                                                        while (true) {
                                                        Block_15:
                                                            while (true) {
                                                                break Block_15;
                                                                break Block_16;
                                                                break Block_13;
                                                                BubbleController.this.mStackView.updateBubble(iterator.updatedBubble);
                                                                continue Label_0370_Outer;
                                                            }
                                                            BubbleController.this.mStackView.updateBubbleOrder(iterator.bubbles);
                                                            continue Block_14_Outer;
                                                        }
                                                        BubbleController.this.mStackView.addBubble(iterator.addedBubble);
                                                        Label_0328: {
                                                            break Label_0328;
                                                            break Block_20;
                                                        }
                                                        continue Label_0453_Outer;
                                                    }
                                                    BubbleController.this.mNotificationGroupManager.updateSuppression(iterator.selectedBubble.getEntry());
                                                    continue Label_0349_Outer;
                                                }
                                                BubbleController.this.mStackView.setExpanded(true);
                                                Label_0440: {
                                                    break Label_0440;
                                                    Label_0481: {
                                                        BubbleController.this.updateStack();
                                                    }
                                                    return;
                                                }
                                                iterator = (Update)BubbleController.this.mCallbacks.iterator();
                                                continue Block_17_Outer;
                                            }
                                            BubbleController.this.mStackView.setSelectedBubble(iterator.selectedBubble);
                                            continue;
                                        }
                                    }
                                    ((NotifCallback)((Iterator<NotifCallback>)iterator).next()).invalidateNotifications("BubbleData.Listener.applyUpdate");
                                    continue;
                                }
                            }
                            // iftrue(Label_0415:, iterator.selectedBubble == null)
                            catch (RemoteException ex) {
                                continue;
                            }
                            break;
                        }
                    }
                }
            }
        };
        dumpManager.registerDumpable("Bubbles", this);
        this.mContext = mContext;
        this.mNotificationInterruptStateProvider = mNotificationInterruptStateProvider;
        this.mNotifUserManager = mNotifUserManager;
        this.mZenModeController = mZenModeController;
        this.mFloatingContentCoordinator = mFloatingContentCoordinator;
        mZenModeController.addCallback((ZenModeController.Callback)new ZenModeController.Callback() {
            @Override
            public void onConfigChanged(final ZenModeConfig zenModeConfig) {
                for (final Bubble bubble : BubbleController.this.mBubbleData.getBubbles()) {
                    bubble.setShowDot(bubble.showInShade());
                }
            }
            
            @Override
            public void onZenChanged(final int n) {
                for (final Bubble bubble : BubbleController.this.mBubbleData.getBubbles()) {
                    bubble.setShowDot(bubble.showInShade());
                }
            }
        });
        configurationController.addCallback((ConfigurationController.ConfigurationListener)this);
        this.mSysUiState = mSysUiState;
        (this.mBubbleData = mBubbleData).setListener(this.mBubbleDataListener);
        this.mBubbleData.setSuppressionChangedListener(new NotificationSuppressionChangedListener() {
            @Override
            public void onBubbleNotificationSuppressionChange(final Bubble bubble) {
                try {
                    BubbleController.this.mBarService.onBubbleNotificationSuppressionChanged(bubble.getKey(), !bubble.showInShade());
                }
                catch (RemoteException ex) {}
            }
        });
        this.mNotificationEntryManager = mNotificationEntryManager;
        this.mNotificationGroupManager = mNotificationGroupManager;
        this.mNotifPipeline = mNotifPipeline;
        if (!featureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.setupNEM();
        }
        else {
            this.setupNotifPipeline();
        }
        this.mNotificationShadeWindowController = mNotificationShadeWindowController;
        statusBarStateController.addCallback((StatusBarStateController.StateListener)(this.mStatusBarStateListener = new StatusBarStateListener()));
        this.mTaskStackListener = new BubbleTaskStackListener();
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
        try {
            WindowManagerWrapper.getInstance().addPinnedStackListener(new BubblesImeListener());
        }
        catch (RemoteException ex) {
            ex.printStackTrace();
        }
        this.mSurfaceSynchronizer = mSurfaceSynchronizer;
        this.mBarService = IStatusBarService$Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mSavedBubbleKeysPerUser = (SparseSetArray<String>)new SparseSetArray();
        this.mCurrentUserId = this.mNotifUserManager.getCurrentUserId();
        this.mNotifUserManager.addUserChangedListener((NotificationLockscreenUserManager.UserChangedListener)new NotificationLockscreenUserManager.UserChangedListener() {
            @Override
            public void onUserChanged(final int n) {
                final BubbleController this$0 = BubbleController.this;
                this$0.saveBubbles(this$0.mCurrentUserId);
                BubbleController.this.mBubbleData.dismissAll(8);
                BubbleController.this.restoreBubbles(n);
                BubbleController.this.mCurrentUserId = n;
            }
        });
        this.mUserCreatedBubbles = new HashSet<String>();
        this.mUserBlockedBubbles = new HashSet<String>();
        this.mBubbleIconFactory = new BubbleIconFactory(mContext);
    }
    
    static boolean canLaunchInActivityView(final Context context, final NotificationEntry notificationEntry) {
        PendingIntent intent;
        if (notificationEntry.getBubbleMetadata() != null) {
            intent = notificationEntry.getBubbleMetadata().getIntent();
        }
        else {
            intent = null;
        }
        if (notificationEntry.getBubbleMetadata() != null && notificationEntry.getBubbleMetadata().getShortcutId() != null) {
            return true;
        }
        if (intent == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unable to create bubble -- no intent: ");
            sb.append(notificationEntry.getKey());
            Log.w("Bubbles", sb.toString());
            return false;
        }
        final ActivityInfo resolveActivityInfo = intent.getIntent().resolveActivityInfo(StatusBar.getPackageManagerForUser(context, notificationEntry.getSbn().getUser().getIdentifier()), 0);
        if (resolveActivityInfo == null) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Unable to send as bubble, ");
            sb2.append(notificationEntry.getKey());
            sb2.append(" couldn't find activity info for intent: ");
            sb2.append(intent);
            Log.w("Bubbles", sb2.toString());
            return false;
        }
        if (!ActivityInfo.isResizeableMode(resolveActivityInfo.resizeMode)) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("Unable to send as bubble, ");
            sb3.append(notificationEntry.getKey());
            sb3.append(" activity is not resizable for intent: ");
            sb3.append(intent);
            Log.w("Bubbles", sb3.toString());
            return false;
        }
        return true;
    }
    
    private void ensureStackViewCreated() {
        if (this.mStackView == null) {
            this.mStackView = new BubbleStackView(this.mContext, this.mBubbleData, this.mSurfaceSynchronizer, this.mFloatingContentCoordinator, this.mSysUiState);
            final ViewGroup notificationShadeView = this.mNotificationShadeWindowController.getNotificationShadeView();
            notificationShadeView.addView((View)this.mStackView, notificationShadeView.indexOfChild(notificationShadeView.findViewById(R$id.scrim_for_bubble)) + 1, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
            final BubbleExpandListener mExpandListener = this.mExpandListener;
            if (mExpandListener != null) {
                this.mStackView.setExpandListener(mExpandListener);
            }
        }
    }
    
    private void handleSummaryDismissalInterception(final NotificationEntry notificationEntry) {
        final List<NotificationEntry> children = notificationEntry.getChildren();
        if (children != null) {
            for (int i = 0; i < children.size(); ++i) {
                final NotificationEntry notificationEntry2 = children.get(i);
                if (this.mBubbleData.hasBubbleWithKey(notificationEntry2.getKey())) {
                    final Bubble bubbleWithKey = this.mBubbleData.getBubbleWithKey(notificationEntry2.getKey());
                    this.mNotificationGroupManager.onEntryRemoved(bubbleWithKey.getEntry());
                    bubbleWithKey.setSuppressNotification(true);
                    bubbleWithKey.setShowDot(false);
                }
                else {
                    final Iterator<NotifCallback> iterator = this.mCallbacks.iterator();
                    while (iterator.hasNext()) {
                        iterator.next().removeNotification(notificationEntry2, 12);
                    }
                }
            }
        }
        this.mNotificationGroupManager.onEntryRemoved(notificationEntry);
        this.mBubbleData.addSummaryToSuppress(notificationEntry.getSbn().getGroupKey(), notificationEntry.getKey());
    }
    
    private boolean isSummaryOfBubbles(final NotificationEntry notificationEntry) {
        final boolean b = false;
        if (notificationEntry == null) {
            return false;
        }
        final String groupKey = notificationEntry.getSbn().getGroupKey();
        final ArrayList<Bubble> bubblesInGroup = this.mBubbleData.getBubblesInGroup(groupKey);
        final boolean b2 = this.mBubbleData.isSummarySuppressed(groupKey) && this.mBubbleData.getSummaryKey(groupKey).equals(notificationEntry.getKey());
        final boolean groupSummary = notificationEntry.getSbn().getNotification().isGroupSummary();
        if (!b2) {
            final boolean b3 = b;
            if (!groupSummary) {
                return b3;
            }
        }
        boolean b3 = b;
        if (bubblesInGroup != null) {
            b3 = b;
            if (!bubblesInGroup.isEmpty()) {
                b3 = true;
            }
        }
        return b3;
    }
    
    private void onEntryAdded(final NotificationEntry notificationEntry) {
        final boolean contains = this.mUserCreatedBubbles.contains(notificationEntry.getKey());
        final boolean adjustForExperiments = BubbleExperimentConfig.adjustForExperiments(this.mContext, notificationEntry, contains, this.mUserBlockedBubbles.contains(notificationEntry.getKey()));
        if (this.mNotificationInterruptStateProvider.shouldBubbleUp(notificationEntry) && (canLaunchInActivityView(this.mContext, notificationEntry) || adjustForExperiments)) {
            if (adjustForExperiments && !contains) {
                this.mUserCreatedBubbles.add(notificationEntry.getKey());
            }
            this.updateBubble(notificationEntry);
        }
    }
    
    private void onEntryRemoved(final NotificationEntry notificationEntry) {
        if (this.isSummaryOfBubbles(notificationEntry)) {
            final String groupKey = notificationEntry.getSbn().getGroupKey();
            this.mBubbleData.removeSuppressedSummary(groupKey);
            final ArrayList<Bubble> bubblesInGroup = this.mBubbleData.getBubblesInGroup(groupKey);
            for (int i = 0; i < bubblesInGroup.size(); ++i) {
                this.removeBubble(((Bubble)bubblesInGroup.get(i)).getEntry(), 9);
            }
        }
        else {
            this.removeBubble(notificationEntry, 5);
        }
    }
    
    private void onEntryUpdated(final NotificationEntry notificationEntry) {
        final boolean contains = this.mUserCreatedBubbles.contains(notificationEntry.getKey());
        final boolean adjustForExperiments = BubbleExperimentConfig.adjustForExperiments(this.mContext, notificationEntry, contains, this.mUserBlockedBubbles.contains(notificationEntry.getKey()));
        final boolean b = this.mNotificationInterruptStateProvider.shouldBubbleUp(notificationEntry) && (canLaunchInActivityView(this.mContext, notificationEntry) || adjustForExperiments);
        if (!b && this.mBubbleData.hasBubbleWithKey(notificationEntry.getKey())) {
            this.removeBubble(notificationEntry, 7);
        }
        else if (b) {
            if (adjustForExperiments && !contains) {
                this.mUserCreatedBubbles.add(notificationEntry.getKey());
            }
            this.updateBubble(notificationEntry);
        }
    }
    
    private void onRankingUpdated(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        if (this.mTmpRanking == null) {
            this.mTmpRanking = new NotificationListenerService$Ranking();
        }
        final String[] orderedKeys = notificationListenerService$RankingMap.getOrderedKeys();
        for (int i = 0; i < orderedKeys.length; ++i) {
            final String s = orderedKeys[i];
            final NotificationEntry pendingOrActiveNotif = this.mNotificationEntryManager.getPendingOrActiveNotif(s);
            notificationListenerService$RankingMap.getRanking(s, this.mTmpRanking);
            final boolean hasBubbleWithKey = this.mBubbleData.hasBubbleWithKey(s);
            if (hasBubbleWithKey && !this.mTmpRanking.canBubble()) {
                this.mBubbleData.notificationEntryRemoved(pendingOrActiveNotif, 4);
            }
            else if (pendingOrActiveNotif != null && this.mTmpRanking.isBubble() && !hasBubbleWithKey) {
                pendingOrActiveNotif.setFlagBubble(true);
                this.onEntryUpdated(pendingOrActiveNotif);
            }
        }
    }
    
    private void restoreBubbles(final int n) {
        final ArraySet value = this.mSavedBubbleKeysPerUser.get(n);
        if (value == null) {
            return;
        }
        for (final NotificationEntry notificationEntry : this.mNotificationEntryManager.getActiveNotificationsForCurrentUser()) {
            if (value.contains((Object)notificationEntry.getKey()) && this.mNotificationInterruptStateProvider.shouldBubbleUp(notificationEntry) && canLaunchInActivityView(this.mContext, notificationEntry)) {
                this.updateBubble(notificationEntry, true);
            }
        }
        this.mSavedBubbleKeysPerUser.remove(this.mCurrentUserId);
    }
    
    private void saveBubbles(final int n) {
        this.mSavedBubbleKeysPerUser.remove(n);
        final Iterator<Bubble> iterator = this.mBubbleData.getBubbles().iterator();
        while (iterator.hasNext()) {
            this.mSavedBubbleKeysPerUser.add(n, (Object)iterator.next().getKey());
        }
    }
    
    private void setupNEM() {
        this.mNotificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final NotificationVisibility notificationVisibility, final boolean b, final int n) {
                BubbleController.this.onEntryRemoved(notificationEntry);
            }
            
            @Override
            public void onNotificationRankingUpdated(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
                BubbleController.this.onRankingUpdated(notificationListenerService$RankingMap);
            }
            
            @Override
            public void onPendingEntryAdded(final NotificationEntry notificationEntry) {
                BubbleController.this.onEntryAdded(notificationEntry);
            }
            
            @Override
            public void onPreEntryUpdated(final NotificationEntry notificationEntry) {
                BubbleController.this.onEntryUpdated(notificationEntry);
            }
        });
        this.mNotificationEntryManager.addNotificationRemoveInterceptor(new NotificationRemoveInterceptor() {
            @Override
            public boolean onNotificationRemoveRequested(final String s, final NotificationEntry notificationEntry, int n) {
                final int n2 = 1;
                final boolean b = n == 3;
                final boolean b2 = n == 2 || n == 1;
                final boolean b3 = n == 8 || n == 9;
                if (n == 12) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                if (notificationEntry != null && notificationEntry.isRowDismissed()) {
                    final int n3 = n2;
                    if (!b3) {
                        return (n3 != 0 || BubbleController.this.isUserCreatedBubble(s) || BubbleController.this.isSummaryOfUserCreatedBubble(notificationEntry)) && BubbleController.this.handleDismissalInterception(notificationEntry);
                    }
                }
                int n3 = n2;
                if (!b) {
                    n3 = n2;
                    if (!b2) {
                        if (n != 0) {
                            n3 = n2;
                        }
                        else {
                            n3 = 0;
                        }
                    }
                }
                return (n3 != 0 || BubbleController.this.isUserCreatedBubble(s) || BubbleController.this.isSummaryOfUserCreatedBubble(notificationEntry)) && BubbleController.this.handleDismissalInterception(notificationEntry);
            }
        });
        this.mNotificationGroupManager.addOnGroupChangeListener((NotificationGroupManager.OnGroupChangeListener)new NotificationGroupManager.OnGroupChangeListener() {
            @Override
            public void onGroupSuppressionChanged(final NotificationGroup notificationGroup, final boolean b) {
                final NotificationEntry summary = notificationGroup.summary;
                String groupKey;
                if (summary != null) {
                    groupKey = summary.getSbn().getGroupKey();
                }
                else {
                    groupKey = null;
                }
                if (!b && groupKey != null && BubbleController.this.mBubbleData.isSummarySuppressed(groupKey)) {
                    BubbleController.this.mBubbleData.removeSuppressedSummary(groupKey);
                }
            }
        });
        this.addNotifCallback((NotifCallback)new NotifCallback() {
            @Override
            public void invalidateNotifications(final String s) {
                BubbleController.this.mNotificationEntryManager.updateNotifications(s);
            }
            
            @Override
            public void maybeCancelSummary(final NotificationEntry notificationEntry) {
                final String groupKey = notificationEntry.getSbn().getGroupKey();
                if (BubbleController.this.mBubbleData.isSummarySuppressed(groupKey)) {
                    BubbleController.this.mBubbleData.removeSuppressedSummary(groupKey);
                    final NotificationEntry activeNotificationUnfiltered = BubbleController.this.mNotificationEntryManager.getActiveNotificationUnfiltered(BubbleController.this.mBubbleData.getSummaryKey(groupKey));
                    if (activeNotificationUnfiltered != null) {
                        BubbleController.this.mNotificationEntryManager.performRemoveNotification(activeNotificationUnfiltered.getSbn(), 0);
                    }
                }
                final NotificationEntry logicalGroupSummary = BubbleController.this.mNotificationGroupManager.getLogicalGroupSummary(notificationEntry.getSbn());
                if (logicalGroupSummary != null) {
                    final ArrayList<NotificationEntry> logicalChildren = BubbleController.this.mNotificationGroupManager.getLogicalChildren(logicalGroupSummary.getSbn());
                    if (!logicalGroupSummary.getKey().equals(notificationEntry.getKey()) && (logicalChildren == null || logicalChildren.isEmpty())) {
                        BubbleController.this.mNotificationEntryManager.performRemoveNotification(logicalGroupSummary.getSbn(), 0);
                    }
                }
            }
            
            @Override
            public void removeNotification(final NotificationEntry notificationEntry, final int n) {
                BubbleController.this.mNotificationEntryManager.performRemoveNotification(notificationEntry.getSbn(), n);
            }
        });
    }
    
    private void setupNotifPipeline() {
        this.mNotifPipeline.addCollectionListener(new NotifCollectionListener() {
            @Override
            public void onEntryAdded(final NotificationEntry notificationEntry) {
                BubbleController.this.onEntryAdded(notificationEntry);
            }
            
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final int n) {
                BubbleController.this.onEntryRemoved(notificationEntry);
            }
            
            @Override
            public void onEntryUpdated(final NotificationEntry notificationEntry) {
                BubbleController.this.onEntryUpdated(notificationEntry);
            }
            
            @Override
            public void onRankingUpdate(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
                BubbleController.this.onRankingUpdated(notificationListenerService$RankingMap);
            }
        });
    }
    
    private void updateForThemeChanges() {
        final BubbleStackView mStackView = this.mStackView;
        if (mStackView != null) {
            mStackView.onThemeChanged();
        }
        this.mBubbleIconFactory = new BubbleIconFactory(this.mContext);
        final Iterator<Bubble> iterator = this.mBubbleData.getBubbles().iterator();
        while (iterator.hasNext()) {
            iterator.next().inflate(null, this.mContext, this.mStackView, this.mBubbleIconFactory);
        }
    }
    
    public void addNotifCallback(final NotifCallback notifCallback) {
        this.mCallbacks.add(notifCallback);
    }
    
    public void collapseStack() {
        this.mBubbleData.setExpanded(false);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("BubbleController state:");
        this.mBubbleData.dump(fileDescriptor, printWriter, array);
        printWriter.println();
        final BubbleStackView mStackView = this.mStackView;
        if (mStackView != null) {
            mStackView.dump(fileDescriptor, printWriter, array);
        }
        printWriter.println();
    }
    
    public void expandStackAndSelectBubble(final String s) {
        final Bubble bubbleWithKey = this.mBubbleData.getBubbleWithKey(s);
        if (bubbleWithKey != null) {
            this.mBubbleData.setSelectedBubble(bubbleWithKey);
            this.mBubbleData.setExpanded(true);
        }
    }
    
    public int getExpandedDisplayId(final Context context) {
        if (this.mStackView == null) {
            return -1;
        }
        final boolean b = context.getDisplay() != null && context.getDisplay().getDisplayId() == 0;
        final BubbleViewProvider expandedBubble = this.mStackView.getExpandedBubble();
        if (b && expandedBubble != null && this.isStackExpanded() && !this.mNotificationShadeWindowController.getPanelExpanded()) {
            return expandedBubble.getDisplayId();
        }
        return -1;
    }
    
    List<Bubble> getOverflowBubbles() {
        return this.mBubbleData.getOverflowBubbles();
    }
    
    @VisibleForTesting
    BubbleStackView getStackView() {
        return this.mStackView;
    }
    
    public Rect getTouchableRegion() {
        final BubbleStackView mStackView = this.mStackView;
        if (mStackView != null && mStackView.getVisibility() == 0) {
            this.mStackView.getBoundsOnScreen(this.mTempRect);
            return this.mTempRect;
        }
        return null;
    }
    
    public boolean handleDismissalInterception(final NotificationEntry notificationEntry) {
        if (notificationEntry == null) {
            return false;
        }
        final boolean b = this.mBubbleData.hasBubbleWithKey(notificationEntry.getKey()) && notificationEntry.isBubble();
        if (this.isSummaryOfBubbles(notificationEntry)) {
            this.handleSummaryDismissalInterception(notificationEntry);
        }
        else {
            if (!b) {
                return false;
            }
            final Bubble bubbleWithKey = this.mBubbleData.getBubbleWithKey(notificationEntry.getKey());
            bubbleWithKey.setSuppressNotification(true);
            bubbleWithKey.setShowDot(false);
        }
        final Iterator<NotifCallback> iterator = this.mCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().invalidateNotifications("BubbleController.handleDismissalInterception");
        }
        return true;
    }
    
    public boolean hasBubbles() {
        return this.mStackView != null && this.mBubbleData.hasBubbles();
    }
    
    public boolean isBubbleNotificationSuppressedFromShade(final NotificationEntry notificationEntry) {
        final String key = notificationEntry.getKey();
        final boolean hasBubbleWithKey = this.mBubbleData.hasBubbleWithKey(key);
        final boolean b = true;
        final boolean b2 = hasBubbleWithKey && !this.mBubbleData.getBubbleWithKey(key).showInShade();
        final String groupKey = notificationEntry.getSbn().getGroupKey();
        final boolean summarySuppressed = this.mBubbleData.isSummarySuppressed(groupKey);
        if (key.equals(this.mBubbleData.getSummaryKey(groupKey))) {
            final boolean b3 = b;
            if (summarySuppressed) {
                return b3;
            }
        }
        return b2 && b;
    }
    
    public boolean isStackExpanded() {
        return this.mBubbleData.isExpanded();
    }
    
    boolean isSummaryOfUserCreatedBubble(final NotificationEntry notificationEntry) {
        if (this.isSummaryOfBubbles(notificationEntry)) {
            final ArrayList<Bubble> bubblesInGroup = this.mBubbleData.getBubblesInGroup(notificationEntry.getSbn().getGroupKey());
            for (int i = 0; i < bubblesInGroup.size(); ++i) {
                if (this.isUserCreatedBubble(((Bubble)bubblesInGroup.get(i)).getKey())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    boolean isUserCreatedBubble(final String o) {
        return this.mUserCreatedBubbles.contains(o);
    }
    
    @Override
    public void onConfigChanged(final Configuration configuration) {
        final BubbleStackView mStackView = this.mStackView;
        if (mStackView != null && configuration != null) {
            final int orientation = configuration.orientation;
            if (orientation != this.mOrientation) {
                mStackView.onOrientationChanged(this.mOrientation = orientation);
            }
        }
    }
    
    @Override
    public void onOverlayChanged() {
        this.updateForThemeChanges();
    }
    
    @Override
    public void onUiModeChanged() {
        this.updateForThemeChanges();
    }
    
    public void performBackPressIfNeeded() {
        final BubbleStackView mStackView = this.mStackView;
        if (mStackView != null) {
            mStackView.performBackPressIfNeeded();
        }
    }
    
    void promoteBubbleFromOverflow(final Bubble bubble) {
        bubble.setInflateSynchronously(this.mInflateSynchronously);
        this.mBubbleData.promoteBubbleFromOverflow(bubble, this.mStackView, this.mBubbleIconFactory);
    }
    
    void removeBubble(final NotificationEntry notificationEntry, final int n) {
        if (this.mBubbleData.hasBubbleWithKey(notificationEntry.getKey())) {
            this.mBubbleData.notificationEntryRemoved(notificationEntry, n);
        }
    }
    
    public void setBubbleStateChangeListener(final BubbleStateChangeListener mStateChangeListener) {
        this.mStateChangeListener = mStateChangeListener;
    }
    
    public void setExpandListener(final BubbleExpandListener bubbleExpandListener) {
        final _$$Lambda$BubbleController$B9Rf_8Lqgsvsjhuncdnt9rJlYfA $$Lambda$BubbleController$B9Rf_8Lqgsvsjhuncdnt9rJlYfA = new _$$Lambda$BubbleController$B9Rf_8Lqgsvsjhuncdnt9rJlYfA(this, bubbleExpandListener);
        this.mExpandListener = (BubbleExpandListener)$$Lambda$BubbleController$B9Rf_8Lqgsvsjhuncdnt9rJlYfA;
        final BubbleStackView mStackView = this.mStackView;
        if (mStackView != null) {
            mStackView.setExpandListener($$Lambda$BubbleController$B9Rf_8Lqgsvsjhuncdnt9rJlYfA);
        }
    }
    
    @VisibleForTesting
    void setInflateSynchronously(final boolean mInflateSynchronously) {
        this.mInflateSynchronously = mInflateSynchronously;
    }
    
    void setOverflowCallback(final Runnable mOverflowCallback) {
        this.mOverflowCallback = mOverflowCallback;
    }
    
    void updateBubble(final NotificationEntry notificationEntry) {
        this.updateBubble(notificationEntry, false);
    }
    
    void updateBubble(final NotificationEntry notificationEntry, final boolean b) {
        this.updateBubble(notificationEntry, b, true);
    }
    
    void updateBubble(final NotificationEntry notificationEntry, final boolean b, final boolean b2) {
        if (this.mStackView == null) {
            this.ensureStackViewCreated();
        }
        if (notificationEntry.getImportance() >= 4) {
            notificationEntry.setInterruption();
        }
        final Bubble orCreateBubble = this.mBubbleData.getOrCreateBubble(notificationEntry);
        orCreateBubble.setInflateSynchronously(this.mInflateSynchronously);
        orCreateBubble.inflate(new _$$Lambda$BubbleController$7NR5gsflTPkZLdJ6E4NbUe6AOWk(this, b, b2), this.mContext, this.mStackView, this.mBubbleIconFactory);
    }
    
    public void updateStack() {
        if (this.mStackView == null) {
            return;
        }
        final int currentState = this.mStatusBarStateListener.getCurrentState();
        final boolean b = false;
        int visibility = 4;
        if (currentState == 0 && this.hasBubbles()) {
            final BubbleStackView mStackView = this.mStackView;
            if (this.hasBubbles()) {
                visibility = 0;
            }
            mStackView.setVisibility(visibility);
        }
        else {
            final BubbleStackView mStackView2 = this.mStackView;
            if (mStackView2 != null) {
                mStackView2.setVisibility(4);
            }
        }
        final boolean bubblesShowing = this.mNotificationShadeWindowController.getBubblesShowing();
        int bubblesShowing2 = b ? 1 : 0;
        if (this.hasBubbles()) {
            bubblesShowing2 = (b ? 1 : 0);
            if (this.mStackView.getVisibility() == 0) {
                bubblesShowing2 = 1;
            }
        }
        this.mNotificationShadeWindowController.setBubblesShowing((boolean)(bubblesShowing2 != 0));
        final BubbleStateChangeListener mStateChangeListener = this.mStateChangeListener;
        if (mStateChangeListener != null && (bubblesShowing ? 1 : 0) != bubblesShowing2) {
            mStateChangeListener.onHasBubblesChanged((boolean)(bubblesShowing2 != 0));
        }
        this.mStackView.updateContentDescription();
    }
    
    public interface BubbleExpandListener
    {
        void onBubbleExpandChanged(final boolean p0, final String p1);
    }
    
    public interface BubbleStateChangeListener
    {
        void onHasBubblesChanged(final boolean p0);
    }
    
    private class BubbleTaskStackListener extends TaskStackChangeListener
    {
        @Override
        public void onActivityLaunchOnSecondaryDisplayRerouted() {
            if (BubbleController.this.mStackView != null) {
                BubbleController.this.mBubbleData.setExpanded(false);
            }
        }
        
        @Override
        public void onActivityRestartAttempt(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo, final boolean b, final boolean b2) {
            for (final Bubble bubble : BubbleController.this.mBubbleData.getBubbles()) {
                if (bubble.getDisplayId() == activityManager$RunningTaskInfo.displayId) {
                    BubbleController.this.expandStackAndSelectBubble(bubble.getKey());
                    break;
                }
            }
        }
        
        @Override
        public void onBackPressedOnTaskRoot(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
            if (BubbleController.this.mStackView != null) {
                final int displayId = activityManager$RunningTaskInfo.displayId;
                final BubbleController this$0 = BubbleController.this;
                if (displayId == this$0.getExpandedDisplayId(this$0.mContext)) {
                    BubbleController.this.mBubbleData.setExpanded(false);
                }
            }
        }
        
        @Override
        public void onSingleTaskDisplayDrawn(final int n) {
            if (BubbleController.this.mStackView == null) {
                return;
            }
            BubbleController.this.mStackView.showExpandedViewContents(n);
        }
        
        @Override
        public void onSingleTaskDisplayEmpty(final int n) {
            BubbleViewProvider expandedBubble;
            if (BubbleController.this.mStackView != null) {
                expandedBubble = BubbleController.this.mStackView.getExpandedBubble();
            }
            else {
                expandedBubble = null;
            }
            int displayId;
            if (expandedBubble != null) {
                displayId = expandedBubble.getDisplayId();
            }
            else {
                displayId = -1;
            }
            if (BubbleController.this.mStackView != null && BubbleController.this.mStackView.isExpanded() && displayId == n) {
                BubbleController.this.mBubbleData.setExpanded(false);
            }
            BubbleController.this.mBubbleData.notifyDisplayEmpty(n);
        }
        
        @Override
        public void onTaskMovedToFront(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
            if (BubbleController.this.mStackView != null && activityManager$RunningTaskInfo.displayId == 0 && !BubbleController.this.mStackView.isExpansionAnimating()) {
                BubbleController.this.mBubbleData.setExpanded(false);
            }
        }
    }
    
    private class BubblesImeListener extends PinnedStackListener
    {
        @Override
        public void onImeVisibilityChanged(final boolean b, final int n) {
            if (BubbleController.this.mStackView != null) {
                BubbleController.this.mStackView.post((Runnable)new _$$Lambda$BubbleController$BubblesImeListener$k3Ccv_01hiK8jFFaKEuMmcHqId4(this, b, n));
            }
        }
    }
    
    public interface NotifCallback
    {
        void invalidateNotifications(final String p0);
        
        void maybeCancelSummary(final NotificationEntry p0);
        
        void removeNotification(final NotificationEntry p0, final int p1);
    }
    
    public interface NotificationSuppressionChangedListener
    {
        void onBubbleNotificationSuppressionChange(final Bubble p0);
    }
    
    private class StatusBarStateListener implements StateListener
    {
        private int mState;
        
        public int getCurrentState() {
            return this.mState;
        }
        
        @Override
        public void onStateChanged(int mState) {
            this.mState = mState;
            if (mState != 0) {
                mState = 1;
            }
            else {
                mState = 0;
            }
            if (mState != 0) {
                BubbleController.this.collapseStack();
            }
            BubbleController.this.updateStack();
        }
    }
}
