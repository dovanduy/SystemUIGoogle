// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.NotificationUiAdjustment;
import java.io.FileDescriptor;
import java.util.Collection;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.internal.statusbar.NotificationVisibility;
import java.io.PrintWriter;
import android.service.notification.NotificationListenerService$Ranking;
import com.android.systemui.util.Assert;
import java.util.Iterator;
import android.service.notification.StatusBarNotification;
import java.util.Collections;
import android.util.ArraySet;
import android.util.Log;
import java.util.Map;
import com.android.systemui.statusbar.NotificationRemoveInterceptor;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.collection.NotificationRankingManager;
import com.android.systemui.statusbar.NotificationPresenter;
import java.util.HashMap;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinder;
import dagger.Lazy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.NotificationLifetimeExtender;
import java.util.ArrayList;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import java.util.List;
import com.android.systemui.util.leak.LeakDetector;
import android.service.notification.NotificationListenerService$RankingMap;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.FeatureFlags;
import java.util.Set;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.util.ArrayMap;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;

public class NotificationEntryManager implements CommonNotifCollection, Dumpable, InflationCallback, Callback
{
    private static final boolean DEBUG;
    private final ArrayMap<String, NotificationEntry> mActiveNotifications;
    private final Set<NotificationEntry> mAllNotifications;
    private final FeatureFlags mFeatureFlags;
    private final ForegroundServiceDismissalFeatureController mFgsFeatureController;
    private final NotificationGroupManager mGroupManager;
    private final KeyguardEnvironment mKeyguardEnvironment;
    private NotificationListenerService$RankingMap mLatestRankingMap;
    private final LeakDetector mLeakDetector;
    private final NotificationEntryManagerLogger mLogger;
    private final List<NotifCollectionListener> mNotifCollectionListeners;
    private final NotificationListener.NotificationHandler mNotifListener;
    private final List<NotificationEntryListener> mNotificationEntryListeners;
    @VisibleForTesting
    final ArrayList<NotificationLifetimeExtender> mNotificationLifetimeExtenders;
    private final Lazy<NotificationRowBinder> mNotificationRowBinderLazy;
    @VisibleForTesting
    protected final HashMap<String, NotificationEntry> mPendingNotifications;
    private NotificationPresenter mPresenter;
    private final NotificationRankingManager mRankingManager;
    private final Set<NotificationEntry> mReadOnlyAllNotifications;
    private final List<NotificationEntry> mReadOnlyNotifications;
    private final Lazy<NotificationRemoteInputManager> mRemoteInputManagerLazy;
    private final List<NotificationRemoveInterceptor> mRemoveInterceptors;
    private final Map<NotificationEntry, NotificationLifetimeExtender> mRetainedNotifications;
    @VisibleForTesting
    protected final ArrayList<NotificationEntry> mSortedAndFiltered;
    
    static {
        DEBUG = Log.isLoggable("NotificationEntryMgr", 3);
    }
    
    public NotificationEntryManager(final NotificationEntryManagerLogger mLogger, final NotificationGroupManager mGroupManager, final NotificationRankingManager mRankingManager, final KeyguardEnvironment mKeyguardEnvironment, final FeatureFlags mFeatureFlags, final Lazy<NotificationRowBinder> mNotificationRowBinderLazy, final Lazy<NotificationRemoteInputManager> mRemoteInputManagerLazy, final LeakDetector mLeakDetector, final ForegroundServiceDismissalFeatureController mFgsFeatureController) {
        final ArraySet set = new ArraySet();
        this.mAllNotifications = (Set<NotificationEntry>)set;
        this.mReadOnlyAllNotifications = Collections.unmodifiableSet((Set<? extends NotificationEntry>)set);
        this.mPendingNotifications = new HashMap<String, NotificationEntry>();
        this.mActiveNotifications = (ArrayMap<String, NotificationEntry>)new ArrayMap();
        final ArrayList<NotificationEntry> list = new ArrayList<NotificationEntry>();
        this.mSortedAndFiltered = list;
        this.mReadOnlyNotifications = (List<NotificationEntry>)Collections.unmodifiableList((List<?>)list);
        this.mRetainedNotifications = (Map<NotificationEntry, NotificationLifetimeExtender>)new ArrayMap();
        this.mNotifCollectionListeners = new ArrayList<NotifCollectionListener>();
        this.mNotificationLifetimeExtenders = new ArrayList<NotificationLifetimeExtender>();
        this.mNotificationEntryListeners = new ArrayList<NotificationEntryListener>();
        this.mRemoveInterceptors = new ArrayList<NotificationRemoveInterceptor>();
        this.mNotifListener = new NotificationListener.NotificationHandler() {
            @Override
            public void onNotificationPosted(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
                if (NotificationEntryManager.this.mActiveNotifications.containsKey((Object)statusBarNotification.getKey())) {
                    NotificationEntryManager.this.updateNotification(statusBarNotification, notificationListenerService$RankingMap);
                }
                else {
                    NotificationEntryManager.this.addNotification(statusBarNotification, notificationListenerService$RankingMap);
                }
            }
            
            @Override
            public void onNotificationRankingUpdate(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
                NotificationEntryManager.this.updateNotificationRanking(notificationListenerService$RankingMap);
            }
            
            @Override
            public void onNotificationRemoved(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap, final int n) {
                NotificationEntryManager.this.removeNotification(statusBarNotification.getKey(), notificationListenerService$RankingMap, n);
            }
        };
        this.mLogger = mLogger;
        this.mGroupManager = mGroupManager;
        this.mRankingManager = mRankingManager;
        this.mKeyguardEnvironment = mKeyguardEnvironment;
        this.mFeatureFlags = mFeatureFlags;
        this.mNotificationRowBinderLazy = mNotificationRowBinderLazy;
        this.mRemoteInputManagerLazy = mRemoteInputManagerLazy;
        this.mLeakDetector = mLeakDetector;
        this.mFgsFeatureController = mFgsFeatureController;
    }
    
    private void abortExistingInflation(final String key, final String s) {
        if (this.mPendingNotifications.containsKey(key)) {
            final NotificationEntry notificationEntry = this.mPendingNotifications.get(key);
            notificationEntry.abortTask();
            this.mPendingNotifications.remove(key);
            final Iterator<NotifCollectionListener> iterator = this.mNotifCollectionListeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().onEntryCleanUp(notificationEntry);
            }
            this.mLogger.logInflationAborted(key, "pending", s);
        }
        final NotificationEntry activeNotificationUnfiltered = this.getActiveNotificationUnfiltered(key);
        if (activeNotificationUnfiltered != null) {
            activeNotificationUnfiltered.abortTask();
            this.mLogger.logInflationAborted(key, "active", s);
        }
    }
    
    private void addActiveNotification(final NotificationEntry notificationEntry) {
        Assert.isMainThread();
        this.mActiveNotifications.put((Object)notificationEntry.getKey(), (Object)notificationEntry);
        this.mGroupManager.onEntryAdded(notificationEntry);
        this.updateRankingAndSort(this.mRankingManager.getRankingMap(), "addEntryInternalInternal");
    }
    
    private void addNotificationInternal(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) throws InflationException {
        final String key = statusBarNotification.getKey();
        if (NotificationEntryManager.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("addNotification key=");
            sb.append(key);
            Log.d("NotificationEntryMgr", sb.toString());
        }
        this.updateRankingAndSort(notificationListenerService$RankingMap, "addNotificationInternal");
        final NotificationListenerService$Ranking notificationListenerService$Ranking = new NotificationListenerService$Ranking();
        notificationListenerService$RankingMap.getRanking(key, notificationListenerService$Ranking);
        final NotificationEntry value = new NotificationEntry(statusBarNotification, notificationListenerService$Ranking, this.mFgsFeatureController.isForegroundServiceDismissalEnabled());
        this.mAllNotifications.add(value);
        this.mLeakDetector.trackInstance(value);
        final Iterator<NotifCollectionListener> iterator = this.mNotifCollectionListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onEntryInit(value);
        }
        if (!this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.mNotificationRowBinderLazy.get().inflateViews(value, new _$$Lambda$NotificationEntryManager$lOGPG9l6kx5UZEzr26g7h2LQR6w(this, statusBarNotification));
        }
        this.abortExistingInflation(key, "addNotification");
        this.mPendingNotifications.put(key, value);
        this.mLogger.logNotifAdded(value.getKey());
        final Iterator<NotificationEntryListener> iterator2 = this.mNotificationEntryListeners.iterator();
        while (iterator2.hasNext()) {
            iterator2.next().onPendingEntryAdded(value);
        }
        final Iterator<NotifCollectionListener> iterator3 = this.mNotifCollectionListeners.iterator();
        while (iterator3.hasNext()) {
            iterator3.next().onEntryAdded(value);
        }
        final Iterator<NotifCollectionListener> iterator4 = this.mNotifCollectionListeners.iterator();
        while (iterator4.hasNext()) {
            iterator4.next().onRankingApplied();
        }
    }
    
    private void cancelLifetimeExtension(final NotificationEntry notificationEntry) {
        final NotificationLifetimeExtender notificationLifetimeExtender = this.mRetainedNotifications.remove(notificationEntry);
        if (notificationLifetimeExtender != null) {
            notificationLifetimeExtender.setShouldManageLifetime(notificationEntry, false);
        }
    }
    
    private void dumpEntry(final PrintWriter printWriter, final String s, final int i, final NotificationEntry notificationEntry) {
        printWriter.print(s);
        final StringBuilder sb = new StringBuilder();
        sb.append("  [");
        sb.append(i);
        sb.append("] key=");
        sb.append(notificationEntry.getKey());
        sb.append(" icon=");
        sb.append(notificationEntry.getIcons().getStatusBarIcon());
        printWriter.println(sb.toString());
        final StatusBarNotification sbn = notificationEntry.getSbn();
        printWriter.print(s);
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("      pkg=");
        sb2.append(sbn.getPackageName());
        sb2.append(" id=");
        sb2.append(sbn.getId());
        sb2.append(" importance=");
        sb2.append(notificationEntry.getRanking().getImportance());
        printWriter.println(sb2.toString());
        printWriter.print(s);
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("      notification=");
        sb3.append(sbn.getNotification());
        printWriter.println(sb3.toString());
    }
    
    private void extendLifetime(final NotificationEntry notificationEntry, final NotificationLifetimeExtender notificationLifetimeExtender) {
        final NotificationLifetimeExtender notificationLifetimeExtender2 = this.mRetainedNotifications.get(notificationEntry);
        if (notificationLifetimeExtender2 != null && notificationLifetimeExtender2 != notificationLifetimeExtender) {
            notificationLifetimeExtender2.setShouldManageLifetime(notificationEntry, false);
        }
        this.mRetainedNotifications.put(notificationEntry, notificationLifetimeExtender);
        notificationLifetimeExtender.setShouldManageLifetime(notificationEntry, true);
    }
    
    private void handleGroupSummaryRemoved(final String s) {
        final NotificationEntry activeNotificationUnfiltered = this.getActiveNotificationUnfiltered(s);
        if (activeNotificationUnfiltered != null && activeNotificationUnfiltered.rowExists() && activeNotificationUnfiltered.isSummaryWithChildren()) {
            if (activeNotificationUnfiltered.getSbn().getOverrideGroupKey() != null && !activeNotificationUnfiltered.isRowDismissed()) {
                return;
            }
            final List<NotificationEntry> children = activeNotificationUnfiltered.getChildren();
            if (children == null) {
                return;
            }
            for (int i = 0; i < children.size(); ++i) {
                final NotificationEntry notificationEntry = children.get(i);
                final boolean b = (activeNotificationUnfiltered.getSbn().getNotification().flags & 0x40) != 0x0;
                final boolean b2 = this.mRemoteInputManagerLazy.get().shouldKeepForRemoteInputHistory(notificationEntry) || this.mRemoteInputManagerLazy.get().shouldKeepForSmartReplyHistory(notificationEntry);
                if (!b) {
                    if (!b2) {
                        notificationEntry.setKeepInParent(true);
                        notificationEntry.removeRow();
                    }
                }
            }
        }
    }
    
    private NotificationVisibility obtainVisibility(final String s) {
        final NotificationEntry notificationEntry = (NotificationEntry)this.mActiveNotifications.get((Object)s);
        int rank;
        if (notificationEntry != null) {
            rank = notificationEntry.getRanking().getRank();
        }
        else {
            rank = 0;
        }
        return NotificationVisibility.obtain(s, rank, this.mActiveNotifications.size(), true, NotificationLogger.getNotificationLocation(this.getActiveNotificationUnfiltered(s)));
    }
    
    private void removeNotificationInternal(final String key, final NotificationListenerService$RankingMap mLatestRankingMap, final NotificationVisibility notificationVisibility, final boolean b, final boolean b2, final int n) {
        final NotificationEntry activeNotificationUnfiltered = this.getActiveNotificationUnfiltered(key);
        final Iterator<NotificationRemoveInterceptor> iterator = this.mRemoveInterceptors.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().onNotificationRemoveRequested(key, activeNotificationUnfiltered, n)) {
                this.mLogger.logRemovalIntercepted(key);
                return;
            }
        }
        final int n2 = 1;
        int n4 = 0;
        Label_0171: {
            if (activeNotificationUnfiltered == null) {
                final NotificationEntry notificationEntry = this.mPendingNotifications.get(key);
                if (notificationEntry != null) {
                    final Iterator<NotificationLifetimeExtender> iterator2 = this.mNotificationLifetimeExtenders.iterator();
                    int n3 = 0;
                    while (true) {
                        n4 = n3;
                        if (!iterator2.hasNext()) {
                            break Label_0171;
                        }
                        final NotificationLifetimeExtender notificationLifetimeExtender = iterator2.next();
                        if (!notificationLifetimeExtender.shouldExtendLifetimeForPendingNotification(notificationEntry)) {
                            continue;
                        }
                        this.extendLifetime(notificationEntry, notificationLifetimeExtender);
                        this.mLogger.logLifetimeExtended(key, notificationLifetimeExtender.getClass().getName(), "pending");
                        n3 = 1;
                    }
                }
            }
            n4 = 0;
        }
        if (n4 == 0) {
            this.abortExistingInflation(key, "removeNotification");
        }
        if (activeNotificationUnfiltered != null) {
            final boolean rowDismissed = activeNotificationUnfiltered.isRowDismissed();
            int n5 = 0;
            Label_0291: {
                if (!b && !rowDismissed) {
                    for (final NotificationLifetimeExtender notificationLifetimeExtender2 : this.mNotificationLifetimeExtenders) {
                        if (notificationLifetimeExtender2.shouldExtendLifetime(activeNotificationUnfiltered)) {
                            this.mLatestRankingMap = mLatestRankingMap;
                            this.extendLifetime(activeNotificationUnfiltered, notificationLifetimeExtender2);
                            this.mLogger.logLifetimeExtended(key, notificationLifetimeExtender2.getClass().getName(), "active");
                            n5 = n2;
                            break Label_0291;
                        }
                    }
                }
                n5 = n4;
            }
            if (n5 == 0) {
                this.cancelLifetimeExtension(activeNotificationUnfiltered);
                if (activeNotificationUnfiltered.rowExists()) {
                    activeNotificationUnfiltered.removeRow();
                }
                this.mAllNotifications.remove(activeNotificationUnfiltered);
                this.handleGroupSummaryRemoved(key);
                this.removeVisibleNotification(key);
                this.updateNotifications("removeNotificationInternal");
                this.mLeakDetector.trackGarbage(activeNotificationUnfiltered);
                final boolean b3 = b2 | rowDismissed;
                this.mLogger.logNotifRemoved(activeNotificationUnfiltered.getKey(), b3);
                final Iterator<NotificationEntryListener> iterator4 = this.mNotificationEntryListeners.iterator();
                while (iterator4.hasNext()) {
                    iterator4.next().onEntryRemoved(activeNotificationUnfiltered, notificationVisibility, b3, n);
                }
                final Iterator<NotifCollectionListener> iterator5 = this.mNotifCollectionListeners.iterator();
                while (iterator5.hasNext()) {
                    iterator5.next().onEntryRemoved(activeNotificationUnfiltered, 0);
                }
                final Iterator<NotifCollectionListener> iterator6 = this.mNotifCollectionListeners.iterator();
                while (iterator6.hasNext()) {
                    iterator6.next().onEntryCleanUp(activeNotificationUnfiltered);
                }
            }
        }
    }
    
    private void removeVisibleNotification(final String s) {
        Assert.isMainThread();
        final NotificationEntry notificationEntry = (NotificationEntry)this.mActiveNotifications.remove((Object)s);
        if (notificationEntry == null) {
            return;
        }
        this.mGroupManager.onEntryRemoved(notificationEntry);
    }
    
    private void updateNotificationInternal(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) throws InflationException {
        if (NotificationEntryManager.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("updateNotification(");
            sb.append(statusBarNotification);
            sb.append(")");
            Log.d("NotificationEntryMgr", sb.toString());
        }
        final String key = statusBarNotification.getKey();
        this.abortExistingInflation(key, "updateNotification");
        final NotificationEntry activeNotificationUnfiltered = this.getActiveNotificationUnfiltered(key);
        if (activeNotificationUnfiltered == null) {
            return;
        }
        this.cancelLifetimeExtension(activeNotificationUnfiltered);
        this.updateRankingAndSort(notificationListenerService$RankingMap, "updateNotificationInternal");
        final StatusBarNotification sbn = activeNotificationUnfiltered.getSbn();
        activeNotificationUnfiltered.setSbn(statusBarNotification);
        this.mGroupManager.onEntryUpdated(activeNotificationUnfiltered, sbn);
        this.mLogger.logNotifUpdated(activeNotificationUnfiltered.getKey());
        final Iterator<NotificationEntryListener> iterator = this.mNotificationEntryListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onPreEntryUpdated(activeNotificationUnfiltered);
        }
        final Iterator<NotifCollectionListener> iterator2 = this.mNotifCollectionListeners.iterator();
        while (iterator2.hasNext()) {
            iterator2.next().onEntryUpdated(activeNotificationUnfiltered);
        }
        if (!this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.mNotificationRowBinderLazy.get().inflateViews(activeNotificationUnfiltered, new _$$Lambda$NotificationEntryManager$RJEcTAo4cuGvAgvl2zrMgzSF4kM(this, statusBarNotification));
        }
        this.updateNotifications("updateNotificationInternal");
        if (NotificationEntryManager.DEBUG) {
            final boolean notificationForCurrentProfiles = this.mKeyguardEnvironment.isNotificationForCurrentProfiles(statusBarNotification);
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("notification is ");
            String str;
            if (notificationForCurrentProfiles) {
                str = "";
            }
            else {
                str = "not ";
            }
            sb2.append(str);
            sb2.append("for you");
            Log.d("NotificationEntryMgr", sb2.toString());
        }
        final Iterator<NotificationEntryListener> iterator3 = this.mNotificationEntryListeners.iterator();
        while (iterator3.hasNext()) {
            iterator3.next().onPostEntryUpdated(activeNotificationUnfiltered);
        }
        final Iterator<NotifCollectionListener> iterator4 = this.mNotifCollectionListeners.iterator();
        while (iterator4.hasNext()) {
            iterator4.next().onRankingApplied();
        }
    }
    
    private void updateRankingAndSort(final NotificationListenerService$RankingMap notificationListenerService$RankingMap, final String s) {
        this.mSortedAndFiltered.clear();
        this.mSortedAndFiltered.addAll(this.mRankingManager.updateRanking(notificationListenerService$RankingMap, this.mActiveNotifications.values(), s));
    }
    
    private void updateRankingOfPendingNotifications(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        if (notificationListenerService$RankingMap == null) {
            return;
        }
        for (final NotificationEntry notificationEntry : this.mPendingNotifications.values()) {
            final NotificationListenerService$Ranking ranking = new NotificationListenerService$Ranking();
            if (notificationListenerService$RankingMap.getRanking(notificationEntry.getKey(), ranking)) {
                notificationEntry.setRanking(ranking);
            }
        }
    }
    
    @VisibleForTesting
    public void addActiveNotificationForTest(final NotificationEntry notificationEntry) {
        this.mActiveNotifications.put((Object)notificationEntry.getKey(), (Object)notificationEntry);
        this.mGroupManager.onEntryAdded(notificationEntry);
        this.reapplyFilterAndSort("addVisibleNotification");
    }
    
    @Override
    public void addCollectionListener(final NotifCollectionListener notifCollectionListener) {
        this.mNotifCollectionListeners.add(notifCollectionListener);
    }
    
    public void addNotification(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        try {
            this.addNotificationInternal(statusBarNotification, notificationListenerService$RankingMap);
        }
        catch (InflationException ex) {
            this.handleInflationException(statusBarNotification, ex);
        }
    }
    
    public void addNotificationEntryListener(final NotificationEntryListener notificationEntryListener) {
        this.mNotificationEntryListeners.add(notificationEntryListener);
    }
    
    public void addNotificationLifetimeExtender(final NotificationLifetimeExtender e) {
        this.mNotificationLifetimeExtenders.add(e);
        e.setCallback((NotificationLifetimeExtender.NotificationSafeToRemoveCallback)new _$$Lambda$NotificationEntryManager$B9Rprc7VWCrqKYHxmFbKGPst6oI(this));
    }
    
    public void addNotificationLifetimeExtenders(final List<NotificationLifetimeExtender> list) {
        final Iterator<NotificationLifetimeExtender> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.addNotificationLifetimeExtender(iterator.next());
        }
    }
    
    public void addNotificationRemoveInterceptor(final NotificationRemoveInterceptor notificationRemoveInterceptor) {
        this.mRemoveInterceptors.add(notificationRemoveInterceptor);
    }
    
    public void attach(final NotificationListener notificationListener) {
        notificationListener.addNotificationHandler(this.mNotifListener);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("NotificationEntryManager state:");
        printWriter.print("  mPendingNotifications=");
        if (this.mPendingNotifications.size() == 0) {
            printWriter.println("null");
        }
        else {
            final Iterator<NotificationEntry> iterator = this.mPendingNotifications.values().iterator();
            while (iterator.hasNext()) {
                printWriter.println(iterator.next().getSbn());
            }
        }
        printWriter.println("  Remove interceptors registered:");
        for (final NotificationRemoveInterceptor notificationRemoveInterceptor : this.mRemoveInterceptors) {
            final StringBuilder sb = new StringBuilder();
            sb.append("    ");
            sb.append(notificationRemoveInterceptor.getClass().getSimpleName());
            printWriter.println(sb.toString());
        }
        printWriter.println("  Lifetime extenders registered:");
        for (final NotificationLifetimeExtender notificationLifetimeExtender : this.mNotificationLifetimeExtenders) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("    ");
            sb2.append(notificationLifetimeExtender.getClass().getSimpleName());
            printWriter.println(sb2.toString());
        }
        printWriter.println("  Lifetime-extended notifications:");
        if (this.mRetainedNotifications.isEmpty()) {
            printWriter.println("    None");
        }
        else {
            for (final Map.Entry<NotificationEntry, NotificationLifetimeExtender> entry : this.mRetainedNotifications.entrySet()) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("    ");
                sb3.append(entry.getKey().getSbn());
                sb3.append(" retained by ");
                sb3.append(entry.getValue().getClass().getName());
                printWriter.println(sb3.toString());
            }
        }
    }
    
    public void dump(final PrintWriter printWriter, final String s) {
        printWriter.println("NotificationEntryManager");
        final int size = this.mSortedAndFiltered.size();
        printWriter.print(s);
        final StringBuilder sb = new StringBuilder();
        sb.append("active notifications: ");
        sb.append(size);
        printWriter.println(sb.toString());
        int i = 0;
        int j;
        for (j = 0; j < size; ++j) {
            this.dumpEntry(printWriter, s, j, this.mSortedAndFiltered.get(j));
        }
        synchronized (this.mActiveNotifications) {
            final int size2 = this.mActiveNotifications.size();
            printWriter.print(s);
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("inactive notifications: ");
            sb2.append(size2 - j);
            printWriter.println(sb2.toString());
            int n = 0;
            while (i < size2) {
                final NotificationEntry o = (NotificationEntry)this.mActiveNotifications.valueAt(i);
                int n2 = n;
                if (!this.mSortedAndFiltered.contains(o)) {
                    this.dumpEntry(printWriter, s, n, o);
                    n2 = n + 1;
                }
                ++i;
                n = n2;
            }
        }
    }
    
    public NotificationEntry getActiveNotificationUnfiltered(final String s) {
        return (NotificationEntry)this.mActiveNotifications.get((Object)s);
    }
    
    public int getActiveNotificationsCount() {
        return this.mReadOnlyNotifications.size();
    }
    
    public List<NotificationEntry> getActiveNotificationsForCurrentUser() {
        Assert.isMainThread();
        final ArrayList<NotificationEntry> list = new ArrayList<NotificationEntry>();
        for (int size = this.mActiveNotifications.size(), i = 0; i < size; ++i) {
            final NotificationEntry e = (NotificationEntry)this.mActiveNotifications.valueAt(i);
            if (this.mKeyguardEnvironment.isNotificationForCurrentProfiles(e.getSbn())) {
                list.add(e);
            }
        }
        return list;
    }
    
    @Override
    public Collection<NotificationEntry> getAllNotifs() {
        return this.mReadOnlyAllNotifications;
    }
    
    public Iterable<NotificationEntry> getPendingNotificationsIterator() {
        return this.mPendingNotifications.values();
    }
    
    public NotificationEntry getPendingOrActiveNotif(final String s) {
        if (this.mPendingNotifications.containsKey(s)) {
            return this.mPendingNotifications.get(s);
        }
        return (NotificationEntry)this.mActiveNotifications.get((Object)s);
    }
    
    public List<NotificationEntry> getVisibleNotifications() {
        return this.mReadOnlyNotifications;
    }
    
    public void handleInflationException(final StatusBarNotification statusBarNotification, final Exception ex) {
        this.removeNotificationInternal(statusBarNotification.getKey(), null, null, true, false, 4);
        final Iterator<NotificationEntryListener> iterator = this.mNotificationEntryListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onInflationError(statusBarNotification, ex);
        }
    }
    
    @Override
    public void handleInflationException(final NotificationEntry notificationEntry, final Exception ex) {
        this.handleInflationException(notificationEntry.getSbn(), ex);
    }
    
    public boolean hasActiveNotifications() {
        return this.mReadOnlyNotifications.size() != 0;
    }
    
    @Override
    public void onAsyncInflationFinished(final NotificationEntry notificationEntry) {
        this.mPendingNotifications.remove(notificationEntry.getKey());
        if (!notificationEntry.isRowRemoved()) {
            final boolean b = this.getActiveNotificationUnfiltered(notificationEntry.getKey()) == null;
            this.mLogger.logNotifInflated(notificationEntry.getKey(), b);
            if (b) {
                final Iterator<NotificationEntryListener> iterator = this.mNotificationEntryListeners.iterator();
                while (iterator.hasNext()) {
                    iterator.next().onEntryInflated(notificationEntry);
                }
                this.addActiveNotification(notificationEntry);
                this.updateNotifications("onAsyncInflationFinished");
                final Iterator<NotificationEntryListener> iterator2 = this.mNotificationEntryListeners.iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().onNotificationAdded(notificationEntry);
                }
            }
            else {
                final Iterator<NotificationEntryListener> iterator3 = this.mNotificationEntryListeners.iterator();
                while (iterator3.hasNext()) {
                    iterator3.next().onEntryReinflated(notificationEntry);
                }
            }
        }
    }
    
    @Override
    public void onChangeAllowed() {
        this.updateNotifications("reordering is now allowed");
    }
    
    public void performRemoveNotification(final StatusBarNotification statusBarNotification, final int n) {
        this.removeNotificationInternal(statusBarNotification.getKey(), null, this.obtainVisibility(statusBarNotification.getKey()), false, true, n);
    }
    
    public void reapplyFilterAndSort(final String s) {
        this.updateRankingAndSort(this.mRankingManager.getRankingMap(), s);
    }
    
    public void removeNotification(final String s, final NotificationListenerService$RankingMap notificationListenerService$RankingMap, final int n) {
        this.removeNotificationInternal(s, notificationListenerService$RankingMap, this.obtainVisibility(s), false, false, n);
    }
    
    public void removeNotificationEntryListener(final NotificationEntryListener notificationEntryListener) {
        this.mNotificationEntryListeners.remove(notificationEntryListener);
    }
    
    public void setUpWithPresenter(final NotificationPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }
    
    public void updateNotification(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        try {
            this.updateNotificationInternal(statusBarNotification, notificationListenerService$RankingMap);
        }
        catch (InflationException ex) {
            this.handleInflationException(statusBarNotification, ex);
        }
    }
    
    public void updateNotificationRanking(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        final ArrayList<NotificationEntry> list = new ArrayList<NotificationEntry>();
        list.addAll((Collection<?>)this.getVisibleNotifications());
        list.addAll((Collection<?>)this.mPendingNotifications.values());
        final ArrayMap arrayMap = new ArrayMap();
        final ArrayMap arrayMap2 = new ArrayMap();
        for (final NotificationEntry notificationEntry : list) {
            arrayMap.put((Object)notificationEntry.getKey(), (Object)NotificationUiAdjustment.extractFromNotificationEntry(notificationEntry));
            arrayMap2.put((Object)notificationEntry.getKey(), (Object)notificationEntry.getImportance());
        }
        this.updateRankingAndSort(notificationListenerService$RankingMap, "updateNotificationRanking");
        this.updateRankingOfPendingNotifications(notificationListenerService$RankingMap);
        for (final NotificationEntry notificationEntry2 : list) {
            this.mNotificationRowBinderLazy.get().onNotificationRankingUpdated(notificationEntry2, (Integer)arrayMap2.get((Object)notificationEntry2.getKey()), (NotificationUiAdjustment)arrayMap.get((Object)notificationEntry2.getKey()), NotificationUiAdjustment.extractFromNotificationEntry(notificationEntry2));
        }
        this.updateNotifications("updateNotificationRanking");
        final Iterator<NotificationEntryListener> iterator3 = this.mNotificationEntryListeners.iterator();
        while (iterator3.hasNext()) {
            iterator3.next().onNotificationRankingUpdated(notificationListenerService$RankingMap);
        }
        final Iterator<NotifCollectionListener> iterator4 = this.mNotifCollectionListeners.iterator();
        while (iterator4.hasNext()) {
            iterator4.next().onRankingUpdate(notificationListenerService$RankingMap);
        }
        final Iterator<NotifCollectionListener> iterator5 = this.mNotifCollectionListeners.iterator();
        while (iterator5.hasNext()) {
            iterator5.next().onRankingApplied();
        }
    }
    
    public void updateNotifications(final String s) {
        this.reapplyFilterAndSort(s);
        if (this.mPresenter != null && !this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.mPresenter.updateNotificationViews();
        }
    }
    
    public interface KeyguardEnvironment
    {
        boolean isDeviceProvisioned();
        
        boolean isNotificationForCurrentProfiles(final StatusBarNotification p0);
    }
}
