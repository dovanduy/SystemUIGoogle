// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coalescer;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.Objects;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import android.service.notification.NotificationListenerService$Ranking;
import android.service.notification.NotificationListenerService$RankingMap;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.statusbar.NotificationListener;
import java.util.Comparator;
import com.android.systemui.util.time.SystemClock;
import java.util.Map;
import com.android.systemui.Dumpable;

public class GroupCoalescer implements Dumpable
{
    private final Map<String, EventBatch> mBatches;
    private final SystemClock mClock;
    private final Map<String, CoalescedEvent> mCoalescedEvents;
    private final Comparator<CoalescedEvent> mEventComparator;
    private BatchableNotificationHandler mHandler;
    private final NotificationListener.NotificationHandler mListener;
    private final GroupCoalescerLogger mLogger;
    private final DelayableExecutor mMainExecutor;
    private final long mMaxGroupLingerDuration;
    private final long mMinGroupLingerDuration;
    
    public GroupCoalescer(final DelayableExecutor delayableExecutor, final SystemClock systemClock, final GroupCoalescerLogger groupCoalescerLogger) {
        this(delayableExecutor, systemClock, groupCoalescerLogger, 50L, 500L);
    }
    
    GroupCoalescer(final DelayableExecutor mMainExecutor, final SystemClock mClock, final GroupCoalescerLogger mLogger, final long mMinGroupLingerDuration, final long mMaxGroupLingerDuration) {
        this.mCoalescedEvents = (Map<String, CoalescedEvent>)new ArrayMap();
        this.mBatches = (Map<String, EventBatch>)new ArrayMap();
        this.mListener = new NotificationListener.NotificationHandler() {
            @Override
            public void onNotificationPosted(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
                GroupCoalescer.this.maybeEmitBatch(statusBarNotification);
                GroupCoalescer.this.applyRanking(notificationListenerService$RankingMap);
                if (GroupCoalescer.this.handleNotificationPosted(statusBarNotification, notificationListenerService$RankingMap)) {
                    GroupCoalescer.this.mLogger.logEventCoalesced(statusBarNotification.getKey());
                    ((NotificationListener.NotificationHandler)GroupCoalescer.this.mHandler).onNotificationRankingUpdate(notificationListenerService$RankingMap);
                }
                else {
                    ((NotificationListener.NotificationHandler)GroupCoalescer.this.mHandler).onNotificationPosted(statusBarNotification, notificationListenerService$RankingMap);
                }
            }
            
            @Override
            public void onNotificationRankingUpdate(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
                GroupCoalescer.this.applyRanking(notificationListenerService$RankingMap);
                ((NotificationListener.NotificationHandler)GroupCoalescer.this.mHandler).onNotificationRankingUpdate(notificationListenerService$RankingMap);
            }
            
            @Override
            public void onNotificationRemoved(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap, final int n) {
                GroupCoalescer.this.maybeEmitBatch(statusBarNotification);
                GroupCoalescer.this.applyRanking(notificationListenerService$RankingMap);
                ((NotificationListener.NotificationHandler)GroupCoalescer.this.mHandler).onNotificationRemoved(statusBarNotification, notificationListenerService$RankingMap, n);
            }
        };
        this.mEventComparator = (Comparator<CoalescedEvent>)_$$Lambda$GroupCoalescer$M7iIsb_J8YQ8wPCcv2h3sqACpyk.INSTANCE;
        this.mMainExecutor = mMainExecutor;
        this.mClock = mClock;
        this.mLogger = mLogger;
        this.mMinGroupLingerDuration = mMinGroupLingerDuration;
        this.mMaxGroupLingerDuration = mMaxGroupLingerDuration;
    }
    
    private void applyRanking(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        for (final CoalescedEvent coalescedEvent : this.mCoalescedEvents.values()) {
            final NotificationListenerService$Ranking ranking = new NotificationListenerService$Ranking();
            if (notificationListenerService$RankingMap.getRanking(coalescedEvent.getKey(), ranking)) {
                coalescedEvent.setRanking(ranking);
            }
            else {
                this.mLogger.logMissingRanking(coalescedEvent.getKey());
            }
        }
    }
    
    private void emitBatch(final EventBatch eventBatch) {
        if (eventBatch != this.mBatches.get(eventBatch.mGroupKey)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Cannot emit out-of-date batch ");
            sb.append(eventBatch.mGroupKey);
            throw new IllegalStateException(sb.toString());
        }
        if (!eventBatch.mMembers.isEmpty()) {
            final Runnable mCancelShortTimeout = eventBatch.mCancelShortTimeout;
            if (mCancelShortTimeout != null) {
                mCancelShortTimeout.run();
                eventBatch.mCancelShortTimeout = null;
            }
            this.mBatches.remove(eventBatch.mGroupKey);
            final ArrayList<CoalescedEvent> list = new ArrayList<CoalescedEvent>(eventBatch.mMembers);
            for (final CoalescedEvent coalescedEvent : list) {
                this.mCoalescedEvents.remove(coalescedEvent.getKey());
                coalescedEvent.setBatch(null);
            }
            list.sort(this.mEventComparator);
            this.mLogger.logEmitBatch(eventBatch.mGroupKey);
            this.mHandler.onNotificationBatchPosted(list);
            return;
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("Batch ");
        sb2.append(eventBatch.mGroupKey);
        sb2.append(" cannot be empty");
        throw new IllegalStateException(sb2.toString());
    }
    
    private EventBatch getOrBuildBatch(final String s) {
        EventBatch eventBatch;
        if ((eventBatch = this.mBatches.get(s)) == null) {
            eventBatch = new EventBatch(this.mClock.uptimeMillis(), s);
            this.mBatches.put(s, eventBatch);
        }
        return eventBatch;
    }
    
    private boolean handleNotificationPosted(final StatusBarNotification statusBarNotification, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        if (this.mCoalescedEvents.containsKey(statusBarNotification.getKey())) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Notification has already been coalesced: ");
            sb.append(statusBarNotification.getKey());
            throw new IllegalStateException(sb.toString());
        }
        if (statusBarNotification.isGroup()) {
            final EventBatch orBuildBatch = this.getOrBuildBatch(statusBarNotification.getGroupKey());
            final CoalescedEvent coalescedEvent = new CoalescedEvent(statusBarNotification.getKey(), orBuildBatch.mMembers.size(), statusBarNotification, this.requireRanking(notificationListenerService$RankingMap, statusBarNotification.getKey()), orBuildBatch);
            this.mCoalescedEvents.put(coalescedEvent.getKey(), coalescedEvent);
            orBuildBatch.mMembers.add(coalescedEvent);
            this.resetShortTimeout(orBuildBatch);
            return true;
        }
        return false;
    }
    
    private void maybeEmitBatch(final StatusBarNotification statusBarNotification) {
        final CoalescedEvent coalescedEvent = this.mCoalescedEvents.get(statusBarNotification.getKey());
        final EventBatch eventBatch = this.mBatches.get(statusBarNotification.getGroupKey());
        if (coalescedEvent != null) {
            final GroupCoalescerLogger mLogger = this.mLogger;
            final String key = statusBarNotification.getKey();
            final EventBatch batch = coalescedEvent.getBatch();
            Objects.requireNonNull(batch);
            mLogger.logEarlyEmit(key, batch.mGroupKey);
            final EventBatch batch2 = coalescedEvent.getBatch();
            Objects.requireNonNull(batch2);
            this.emitBatch(batch2);
        }
        else if (eventBatch != null && this.mClock.uptimeMillis() - eventBatch.mCreatedTimestamp >= this.mMaxGroupLingerDuration) {
            this.mLogger.logMaxBatchTimeout(statusBarNotification.getKey(), eventBatch.mGroupKey);
            this.emitBatch(eventBatch);
        }
    }
    
    private NotificationListenerService$Ranking requireRanking(final NotificationListenerService$RankingMap notificationListenerService$RankingMap, final String str) {
        final NotificationListenerService$Ranking notificationListenerService$Ranking = new NotificationListenerService$Ranking();
        if (notificationListenerService$RankingMap.getRanking(str, notificationListenerService$Ranking)) {
            return notificationListenerService$Ranking;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Ranking map does not contain key ");
        sb.append(str);
        throw new IllegalArgumentException(sb.toString());
    }
    
    private void resetShortTimeout(final EventBatch eventBatch) {
        final Runnable mCancelShortTimeout = eventBatch.mCancelShortTimeout;
        if (mCancelShortTimeout != null) {
            mCancelShortTimeout.run();
        }
        eventBatch.mCancelShortTimeout = this.mMainExecutor.executeDelayed(new _$$Lambda$GroupCoalescer$CkC530E2KSp8Q8dstQvPigtYz5M(this, eventBatch), this.mMinGroupLingerDuration);
    }
    
    public void attach(final NotificationListener notificationListener) {
        notificationListener.addNotificationHandler(this.mListener);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final long uptimeMillis = this.mClock.uptimeMillis();
        printWriter.println();
        printWriter.println("Coalesced notifications:");
        final Iterator<EventBatch> iterator = this.mBatches.values().iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final EventBatch eventBatch = iterator.next();
            final StringBuilder sb = new StringBuilder();
            sb.append("   Batch ");
            sb.append(eventBatch.mGroupKey);
            sb.append(":");
            printWriter.println(sb.toString());
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("       Created ");
            sb2.append(uptimeMillis - eventBatch.mCreatedTimestamp);
            sb2.append("ms ago");
            printWriter.println(sb2.toString());
            final Iterator<CoalescedEvent> iterator2 = eventBatch.mMembers.iterator();
            int n2 = n;
            while (true) {
                n = n2;
                if (!iterator2.hasNext()) {
                    break;
                }
                final CoalescedEvent coalescedEvent = iterator2.next();
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("       ");
                sb3.append(coalescedEvent.getKey());
                printWriter.println(sb3.toString());
                ++n2;
            }
        }
        if (n != this.mCoalescedEvents.size()) {
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("    ERROR: batches contain ");
            sb4.append(this.mCoalescedEvents.size());
            sb4.append(" events but am tracking ");
            sb4.append(this.mCoalescedEvents.size());
            sb4.append(" total events");
            printWriter.println(sb4.toString());
            printWriter.println("    All tracked events:");
            for (final CoalescedEvent coalescedEvent2 : this.mCoalescedEvents.values()) {
                final StringBuilder sb5 = new StringBuilder();
                sb5.append("        ");
                sb5.append(coalescedEvent2.getKey());
                printWriter.println(sb5.toString());
            }
        }
    }
    
    public void setNotificationHandler(final BatchableNotificationHandler mHandler) {
        this.mHandler = mHandler;
    }
    
    public interface BatchableNotificationHandler extends NotificationHandler
    {
        void onNotificationBatchPosted(final List<CoalescedEvent> p0);
    }
}
