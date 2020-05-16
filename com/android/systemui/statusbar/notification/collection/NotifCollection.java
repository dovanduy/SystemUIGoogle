// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.util.Pair;
import android.os.RemoteException;
import com.android.systemui.statusbar.notification.collection.notifcollection.CleanUpEntryEvent;
import com.android.systemui.statusbar.notification.collection.notifcollection.EntryRemovedEvent;
import com.android.systemui.statusbar.notification.collection.notifcollection.EntryUpdatedEvent;
import com.android.systemui.statusbar.notification.collection.notifcollection.EntryAddedEvent;
import com.android.systemui.statusbar.notification.collection.notifcollection.InitEntryEvent;
import com.android.systemui.statusbar.notification.collection.notifcollection.RankingUpdatedEvent;
import com.android.systemui.statusbar.notification.collection.notifcollection.DismissedByUserStats;
import java.util.Iterator;
import com.android.systemui.statusbar.notification.collection.notifcollection.RankingAppliedEvent;
import java.util.Objects;
import android.service.notification.NotificationListenerService$Ranking;
import com.android.systemui.util.Assert;
import android.service.notification.NotificationListenerService$RankingMap;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.coalescer.CoalescedEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import android.util.ArrayMap;
import com.android.systemui.dump.DumpManager;
import com.android.internal.statusbar.IStatusBarService;
import java.util.Collection;
import java.util.Map;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescer;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionLogger;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifEvent;
import java.util.Queue;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifDismissInterceptor;
import java.util.List;
import com.android.systemui.statusbar.notification.collection.notifcollection.CollectionReadyForBuildListener;
import com.android.systemui.Dumpable;

public class NotifCollection implements Dumpable
{
    private boolean mAmDispatchingToOtherCode;
    private boolean mAttached;
    private CollectionReadyForBuildListener mBuildListener;
    private final List<NotifDismissInterceptor> mDismissInterceptors;
    private Queue<NotifEvent> mEventQueue;
    private final FeatureFlags mFeatureFlags;
    private final List<NotifLifetimeExtender> mLifetimeExtenders;
    private final NotifCollectionLogger mLogger;
    private final List<NotifCollectionListener> mNotifCollectionListeners;
    private final GroupCoalescer.BatchableNotificationHandler mNotifHandler;
    private final Map<String, NotificationEntry> mNotificationSet;
    private final Collection<NotificationEntry> mReadOnlyNotificationSet;
    private final IStatusBarService mStatusBarService;
    
    public NotifCollection(final IStatusBarService mStatusBarService, final DumpManager dumpManager, final FeatureFlags mFeatureFlags, final NotifCollectionLogger mLogger) {
        final ArrayMap mNotificationSet = new ArrayMap();
        this.mNotificationSet = (Map<String, NotificationEntry>)mNotificationSet;
        this.mReadOnlyNotificationSet = (Collection<NotificationEntry>)Collections.unmodifiableCollection((Collection<?>)((Map<String, NotificationEntry>)mNotificationSet).values());
        this.mNotifCollectionListeners = new ArrayList<NotifCollectionListener>();
        this.mLifetimeExtenders = new ArrayList<NotifLifetimeExtender>();
        this.mDismissInterceptors = new ArrayList<NotifDismissInterceptor>();
        this.mEventQueue = new ArrayDeque<NotifEvent>();
        this.mAttached = false;
        this.mNotifHandler = new GroupCoalescer.BatchableNotificationHandler() {
            @Override
            public void onNotificationBatchPosted(final List<CoalescedEvent> list) {
                NotifCollection.this.onNotificationGroupPosted(list);
            }
            
            @Override
            public void onNotificationPosted(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
                NotifCollection.this.onNotificationPosted(statusBarNotification, notificationListenerService$RankingMap);
            }
            
            @Override
            public void onNotificationRankingUpdate(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
                NotifCollection.this.onNotificationRankingUpdate(notificationListenerService$RankingMap);
            }
            
            @Override
            public void onNotificationRemoved(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap, final int n) {
                NotifCollection.this.onNotificationRemoved(statusBarNotification, notificationListenerService$RankingMap, n);
            }
        };
        Assert.isMainThread();
        this.mStatusBarService = mStatusBarService;
        this.mLogger = mLogger;
        dumpManager.registerDumpable("NotifCollection", this);
        this.mFeatureFlags = mFeatureFlags;
    }
    
    private void applyRanking(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        for (final NotificationEntry notificationEntry : this.mNotificationSet.values()) {
            if (!isCanceled(notificationEntry)) {
                final NotificationListenerService$Ranking ranking = new NotificationListenerService$Ranking();
                if (notificationListenerService$RankingMap.getRanking(notificationEntry.getKey(), ranking)) {
                    notificationEntry.setRanking(ranking);
                    if (!this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
                        continue;
                    }
                    final String overrideGroupKey = ranking.getOverrideGroupKey();
                    if (Objects.equals(notificationEntry.getSbn().getOverrideGroupKey(), overrideGroupKey)) {
                        continue;
                    }
                    notificationEntry.getSbn().setOverrideGroupKey(overrideGroupKey);
                }
                else {
                    this.mLogger.logRankingMissing(notificationEntry.getKey(), notificationListenerService$RankingMap);
                }
            }
        }
        this.mEventQueue.add(new RankingAppliedEvent());
    }
    
    private void cancelDismissInterception(final NotificationEntry notificationEntry) {
        this.mAmDispatchingToOtherCode = true;
        final Iterator<NotifDismissInterceptor> iterator = notificationEntry.mDismissInterceptors.iterator();
        while (iterator.hasNext()) {
            iterator.next().cancelDismissInterception(notificationEntry);
        }
        this.mAmDispatchingToOtherCode = false;
        notificationEntry.mDismissInterceptors.clear();
    }
    
    private void cancelLifetimeExtension(final NotificationEntry notificationEntry) {
        this.mAmDispatchingToOtherCode = true;
        final Iterator<NotifLifetimeExtender> iterator = notificationEntry.mLifetimeExtenders.iterator();
        while (iterator.hasNext()) {
            iterator.next().cancelLifetimeExtension(notificationEntry);
        }
        this.mAmDispatchingToOtherCode = false;
        notificationEntry.mLifetimeExtenders.clear();
    }
    
    private void cancelLocalDismissal(final NotificationEntry notificationEntry) {
        if (isDismissedByUser(notificationEntry)) {
            notificationEntry.setDismissState(NotificationEntry.DismissState.NOT_DISMISSED);
            if (notificationEntry.getSbn().getNotification().isGroupSummary()) {
                for (final NotificationEntry notificationEntry2 : this.mNotificationSet.values()) {
                    if (notificationEntry2.getSbn().getGroupKey().equals(notificationEntry.getSbn().getGroupKey()) && notificationEntry2.getDismissState() == NotificationEntry.DismissState.PARENT_DISMISSED) {
                        notificationEntry2.setDismissState(NotificationEntry.DismissState.NOT_DISMISSED);
                    }
                }
            }
        }
    }
    
    private void checkForReentrantCall() {
        if (!this.mAmDispatchingToOtherCode) {
            return;
        }
        throw new IllegalStateException("Reentrant call detected");
    }
    
    private void dispatchEventsAndRebuildList() {
        this.mAmDispatchingToOtherCode = true;
        while (!this.mEventQueue.isEmpty()) {
            this.mEventQueue.remove().dispatchTo(this.mNotifCollectionListeners);
        }
        this.mAmDispatchingToOtherCode = false;
        final CollectionReadyForBuildListener mBuildListener = this.mBuildListener;
        if (mBuildListener != null) {
            mBuildListener.onBuildList(this.mReadOnlyNotificationSet);
        }
    }
    
    private static boolean hasFlag(final NotificationEntry notificationEntry, final int n) {
        return (notificationEntry.getSbn().getNotification().flags & n) != 0x0;
    }
    
    private static boolean isCanceled(final NotificationEntry notificationEntry) {
        return notificationEntry.mCancellationReason != -1;
    }
    
    private boolean isDismissIntercepted(final NotificationEntry notificationEntry) {
        return notificationEntry.mDismissInterceptors.size() > 0;
    }
    
    private static boolean isDismissedByUser(final NotificationEntry notificationEntry) {
        return notificationEntry.getDismissState() != NotificationEntry.DismissState.NOT_DISMISSED;
    }
    
    private boolean isLifetimeExtended(final NotificationEntry notificationEntry) {
        return notificationEntry.mLifetimeExtenders.size() > 0;
    }
    
    private void locallyDismissNotifications(final List<NotificationEntry> list) {
        final ArrayList<NotificationEntry> list2 = new ArrayList<NotificationEntry>();
        for (int i = 0; i < list.size(); ++i) {
            final NotificationEntry notificationEntry = list.get(i);
            notificationEntry.setDismissState(NotificationEntry.DismissState.DISMISSED);
            this.mLogger.logNotifDismissed(notificationEntry.getKey());
            if (isCanceled(notificationEntry)) {
                list2.add(notificationEntry);
            }
            else if (notificationEntry.getSbn().getNotification().isGroupSummary()) {
                for (final NotificationEntry notificationEntry2 : this.mNotificationSet.values()) {
                    if (shouldAutoDismissChildren(notificationEntry2, notificationEntry.getSbn().getGroupKey())) {
                        notificationEntry2.setDismissState(NotificationEntry.DismissState.PARENT_DISMISSED);
                        if (!isCanceled(notificationEntry2)) {
                            continue;
                        }
                        list2.add(notificationEntry2);
                    }
                }
            }
        }
        final Iterator<Object> iterator2 = list2.iterator();
        while (iterator2.hasNext()) {
            this.tryRemoveNotification(iterator2.next());
        }
    }
    
    private void onEndDismissInterception(final NotifDismissInterceptor notifDismissInterceptor, final NotificationEntry notificationEntry, final DismissedByUserStats dismissedByUserStats) {
        Assert.isMainThread();
        if (!this.mAttached) {
            return;
        }
        this.checkForReentrantCall();
        if (notificationEntry.mDismissInterceptors.remove(notifDismissInterceptor)) {
            if (!this.isDismissIntercepted(notificationEntry)) {
                this.dismissNotification(notificationEntry, dismissedByUserStats);
            }
            return;
        }
        throw new IllegalStateException(String.format("Cannot end dismiss interceptor for interceptor \"%s\" (%s)", notifDismissInterceptor.getName(), notifDismissInterceptor));
    }
    
    private void onEndLifetimeExtension(final NotifLifetimeExtender notifLifetimeExtender, final NotificationEntry notificationEntry) {
        Assert.isMainThread();
        if (!this.mAttached) {
            return;
        }
        this.checkForReentrantCall();
        if (notificationEntry.mLifetimeExtenders.remove(notifLifetimeExtender)) {
            this.mLogger.logLifetimeExtensionEnded(notificationEntry.getKey(), notifLifetimeExtender, notificationEntry.mLifetimeExtenders.size());
            if (!this.isLifetimeExtended(notificationEntry) && this.tryRemoveNotification(notificationEntry)) {
                this.dispatchEventsAndRebuildList();
            }
            return;
        }
        throw new IllegalStateException(String.format("Cannot end lifetime extension for extender \"%s\" (%s)", notifLifetimeExtender.getName(), notifLifetimeExtender));
    }
    
    private void onNotificationGroupPosted(final List<CoalescedEvent> list) {
        Assert.isMainThread();
        this.mLogger.logNotifGroupPosted(list.get(0).getSbn().getGroupKey(), list.size());
        for (final CoalescedEvent coalescedEvent : list) {
            this.postNotification(coalescedEvent.getSbn(), coalescedEvent.getRanking());
        }
        this.dispatchEventsAndRebuildList();
    }
    
    private void onNotificationPosted(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        Assert.isMainThread();
        this.postNotification(statusBarNotification, requireRanking(notificationListenerService$RankingMap, statusBarNotification.getKey()));
        this.applyRanking(notificationListenerService$RankingMap);
        this.dispatchEventsAndRebuildList();
    }
    
    private void onNotificationRankingUpdate(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        Assert.isMainThread();
        this.mEventQueue.add(new RankingUpdatedEvent(notificationListenerService$RankingMap));
        this.applyRanking(notificationListenerService$RankingMap);
        this.dispatchEventsAndRebuildList();
    }
    
    private void onNotificationRemoved(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap, final int mCancellationReason) {
        Assert.isMainThread();
        this.mLogger.logNotifRemoved(statusBarNotification.getKey(), mCancellationReason);
        final NotificationEntry notificationEntry = this.mNotificationSet.get(statusBarNotification.getKey());
        if (notificationEntry != null) {
            notificationEntry.mCancellationReason = mCancellationReason;
            this.tryRemoveNotification(notificationEntry);
            this.applyRanking(notificationListenerService$RankingMap);
            this.dispatchEventsAndRebuildList();
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("No notification to remove with key ");
        sb.append(statusBarNotification.getKey());
        throw new IllegalStateException(sb.toString());
    }
    
    private void postNotification(final StatusBarNotification sbn, final NotificationListenerService$Ranking notificationListenerService$Ranking) {
        final NotificationEntry notificationEntry = this.mNotificationSet.get(sbn.getKey());
        if (notificationEntry == null) {
            final NotificationEntry notificationEntry2 = new NotificationEntry(sbn, notificationListenerService$Ranking);
            this.mNotificationSet.put(sbn.getKey(), notificationEntry2);
            this.mLogger.logNotifPosted(sbn.getKey());
            this.mEventQueue.add(new InitEntryEvent(notificationEntry2));
            this.mEventQueue.add(new EntryAddedEvent(notificationEntry2));
        }
        else {
            this.cancelLocalDismissal(notificationEntry);
            this.cancelLifetimeExtension(notificationEntry);
            this.cancelDismissInterception(notificationEntry);
            notificationEntry.mCancellationReason = -1;
            notificationEntry.setSbn(sbn);
            this.mLogger.logNotifUpdated(sbn.getKey());
            this.mEventQueue.add(new EntryUpdatedEvent(notificationEntry));
        }
    }
    
    private static NotificationListenerService$Ranking requireRanking(final NotificationListenerService$RankingMap notificationListenerService$RankingMap, final String str) {
        final NotificationListenerService$Ranking notificationListenerService$Ranking = new NotificationListenerService$Ranking();
        if (notificationListenerService$RankingMap.getRanking(str, notificationListenerService$Ranking)) {
            return notificationListenerService$Ranking;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Ranking map doesn't contain key: ");
        sb.append(str);
        throw new IllegalArgumentException(sb.toString());
    }
    
    private static boolean shouldAutoDismissChildren(final NotificationEntry notificationEntry, final String anObject) {
        return notificationEntry.getSbn().getGroupKey().equals(anObject) && !notificationEntry.getSbn().getNotification().isGroupSummary() && !hasFlag(notificationEntry, 64) && !hasFlag(notificationEntry, 4096) && notificationEntry.getDismissState() != NotificationEntry.DismissState.DISMISSED;
    }
    
    private static boolean shouldDismissOnClearAll(final NotificationEntry notificationEntry, final int n) {
        return userIdMatches(notificationEntry, n) && notificationEntry.isClearable() && !hasFlag(notificationEntry, 4096) && notificationEntry.getDismissState() != NotificationEntry.DismissState.DISMISSED;
    }
    
    private boolean tryRemoveNotification(final NotificationEntry notificationEntry) {
        if (this.mNotificationSet.get(notificationEntry.getKey()) != notificationEntry) {
            final StringBuilder sb = new StringBuilder();
            sb.append("No notification to remove with key ");
            sb.append(notificationEntry.getKey());
            throw new IllegalStateException(sb.toString());
        }
        if (!isCanceled(notificationEntry)) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Cannot remove notification ");
            sb2.append(notificationEntry.getKey());
            sb2.append(": has not been marked for removal");
            throw new IllegalStateException(sb2.toString());
        }
        if (isDismissedByUser(notificationEntry)) {
            this.cancelLifetimeExtension(notificationEntry);
        }
        else {
            this.updateLifetimeExtension(notificationEntry);
        }
        if (!this.isLifetimeExtended(notificationEntry)) {
            this.mNotificationSet.remove(notificationEntry.getKey());
            this.cancelDismissInterception(notificationEntry);
            this.mEventQueue.add(new EntryRemovedEvent(notificationEntry, notificationEntry.mCancellationReason));
            this.mEventQueue.add(new CleanUpEntryEvent(notificationEntry));
            return true;
        }
        return false;
    }
    
    private void updateDismissInterceptors(final NotificationEntry notificationEntry) {
        notificationEntry.mDismissInterceptors.clear();
        this.mAmDispatchingToOtherCode = true;
        for (final NotifDismissInterceptor notifDismissInterceptor : this.mDismissInterceptors) {
            if (notifDismissInterceptor.shouldInterceptDismissal(notificationEntry)) {
                notificationEntry.mDismissInterceptors.add(notifDismissInterceptor);
            }
        }
        this.mAmDispatchingToOtherCode = false;
    }
    
    private void updateLifetimeExtension(final NotificationEntry notificationEntry) {
        notificationEntry.mLifetimeExtenders.clear();
        this.mAmDispatchingToOtherCode = true;
        for (final NotifLifetimeExtender notifLifetimeExtender : this.mLifetimeExtenders) {
            if (notifLifetimeExtender.shouldExtendLifetime(notificationEntry, notificationEntry.mCancellationReason)) {
                this.mLogger.logLifetimeExtended(notificationEntry.getKey(), notifLifetimeExtender);
                notificationEntry.mLifetimeExtenders.add(notifLifetimeExtender);
            }
        }
        this.mAmDispatchingToOtherCode = false;
    }
    
    private static boolean userIdMatches(final NotificationEntry notificationEntry, final int n) {
        return n == -1 || notificationEntry.getSbn().getUser().getIdentifier() == -1 || notificationEntry.getSbn().getUser().getIdentifier() == n;
    }
    
    void addCollectionListener(final NotifCollectionListener notifCollectionListener) {
        Assert.isMainThread();
        this.mNotifCollectionListeners.add(notifCollectionListener);
    }
    
    void addNotificationDismissInterceptor(final NotifDismissInterceptor obj) {
        Assert.isMainThread();
        this.checkForReentrantCall();
        if (!this.mDismissInterceptors.contains(obj)) {
            this.mDismissInterceptors.add(obj);
            obj.setCallback((NotifDismissInterceptor.OnEndDismissInterception)new _$$Lambda$NotifCollection$rJA7gnYxObrhTLCPUOVCEcGObt0(this));
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Interceptor ");
        sb.append(obj);
        sb.append(" already added.");
        throw new IllegalArgumentException(sb.toString());
    }
    
    void addNotificationLifetimeExtender(final NotifLifetimeExtender obj) {
        Assert.isMainThread();
        this.checkForReentrantCall();
        if (!this.mLifetimeExtenders.contains(obj)) {
            this.mLifetimeExtenders.add(obj);
            obj.setCallback((NotifLifetimeExtender.OnEndLifetimeExtensionCallback)new _$$Lambda$NotifCollection$PS_T32cqW5H2Es1H0TkJN19WLA8(this));
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Extender ");
        sb.append(obj);
        sb.append(" already added.");
        throw new IllegalArgumentException(sb.toString());
    }
    
    public void attach(final GroupCoalescer groupCoalescer) {
        Assert.isMainThread();
        if (!this.mAttached) {
            this.mAttached = true;
            groupCoalescer.setNotificationHandler(this.mNotifHandler);
            return;
        }
        throw new RuntimeException("attach() called twice");
    }
    
    public void dismissAllNotifications(final int n) {
        Assert.isMainThread();
        this.checkForReentrantCall();
        try {
            this.mStatusBarService.onClearAllNotifications(n);
        }
        catch (RemoteException ex) {
            this.mLogger.logRemoteExceptionOnClearAllNotifications(ex);
        }
        final ArrayList<NotificationEntry> list = new ArrayList<NotificationEntry>(this.getAllNotifs());
        for (int i = list.size() - 1; i >= 0; --i) {
            final NotificationEntry notificationEntry = list.get(i);
            if (!shouldDismissOnClearAll(notificationEntry, n)) {
                this.updateDismissInterceptors(notificationEntry);
                if (this.isDismissIntercepted(notificationEntry)) {
                    this.mLogger.logNotifClearAllDismissalIntercepted(notificationEntry.getKey());
                }
                list.remove(i);
            }
        }
        this.locallyDismissNotifications(list);
        this.dispatchEventsAndRebuildList();
    }
    
    public void dismissNotification(final NotificationEntry notificationEntry, final DismissedByUserStats dismissedByUserStats) {
        this.dismissNotifications(List.of(new Pair((Object)notificationEntry, (Object)dismissedByUserStats)));
    }
    
    public void dismissNotifications(final List<Pair<NotificationEntry, DismissedByUserStats>> list) {
        Assert.isMainThread();
        this.checkForReentrantCall();
        final ArrayList<NotificationEntry> list2 = new ArrayList<NotificationEntry>();
        for (int i = 0; i < list.size(); ++i) {
            final NotificationEntry notificationEntry = (NotificationEntry)list.get(i).first;
            final DismissedByUserStats obj = (DismissedByUserStats)list.get(i).second;
            Objects.requireNonNull(obj);
            if (notificationEntry != this.mNotificationSet.get(notificationEntry.getKey())) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Invalid entry: ");
                sb.append(notificationEntry.getKey());
                throw new IllegalStateException(sb.toString());
            }
            if (notificationEntry.getDismissState() != NotificationEntry.DismissState.DISMISSED) {
                this.updateDismissInterceptors(notificationEntry);
                if (this.isDismissIntercepted(notificationEntry)) {
                    this.mLogger.logNotifDismissedIntercepted(notificationEntry.getKey());
                }
                else {
                    list2.add(notificationEntry);
                    if (!isCanceled(notificationEntry)) {
                        try {
                            this.mStatusBarService.onNotificationClear(notificationEntry.getSbn().getPackageName(), notificationEntry.getSbn().getTag(), notificationEntry.getSbn().getId(), notificationEntry.getSbn().getUser().getIdentifier(), notificationEntry.getSbn().getKey(), obj.dismissalSurface, obj.dismissalSentiment, obj.notificationVisibility);
                        }
                        catch (RemoteException ex) {
                            this.mLogger.logRemoteExceptionOnNotificationClear(notificationEntry.getKey(), ex);
                        }
                    }
                }
            }
        }
        this.locallyDismissNotifications(list2);
        this.dispatchEventsAndRebuildList();
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final ArrayList<NotificationEntry> list = new ArrayList<NotificationEntry>(this.getAllNotifs());
        printWriter.println("\tNotifCollection unsorted/unfiltered notifications:");
        if (list.size() == 0) {
            printWriter.println("\t\t None");
        }
        printWriter.println(ListDumper.dumpList(list, true, "\t\t"));
    }
    
    Collection<NotificationEntry> getAllNotifs() {
        Assert.isMainThread();
        return this.mReadOnlyNotificationSet;
    }
    
    void setBuildListener(final CollectionReadyForBuildListener mBuildListener) {
        Assert.isMainThread();
        this.mBuildListener = mBuildListener;
    }
}
