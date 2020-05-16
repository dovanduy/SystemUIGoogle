// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.internal.logging.MetricsLogger;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Iterator;
import android.content.res.Resources;
import android.util.Log;
import android.os.Handler;
import android.database.ContentObserver;
import android.provider.Settings$Global;
import com.android.systemui.R$integer;
import com.android.systemui.Dependency;
import android.util.ArrayMap;
import java.util.HashSet;
import android.content.Context;
import com.android.systemui.statusbar.AlertingNotificationManager;

public abstract class HeadsUpManager extends AlertingNotificationManager
{
    private final AccessibilityManagerWrapper mAccessibilityMgr;
    protected final Context mContext;
    protected boolean mHasPinnedNotification;
    protected final HashSet<OnHeadsUpChangedListener> mListeners;
    protected int mSnoozeLengthMs;
    private final ArrayMap<String, Long> mSnoozedPackages;
    protected int mTouchAcceptanceDelay;
    protected int mUser;
    
    public HeadsUpManager(final Context mContext) {
        this.mListeners = new HashSet<OnHeadsUpChangedListener>();
        this.mContext = mContext;
        this.mAccessibilityMgr = Dependency.get(AccessibilityManagerWrapper.class);
        final Resources resources = mContext.getResources();
        super.mMinimumDisplayTime = resources.getInteger(R$integer.heads_up_notification_minimum_time);
        super.mAutoDismissNotificationDecay = resources.getInteger(R$integer.heads_up_notification_decay);
        this.mTouchAcceptanceDelay = resources.getInteger(R$integer.touch_acceptance_delay);
        this.mSnoozedPackages = (ArrayMap<String, Long>)new ArrayMap();
        this.mSnoozeLengthMs = Settings$Global.getInt(mContext.getContentResolver(), "heads_up_snooze_length_ms", resources.getInteger(R$integer.heads_up_default_snooze_length_ms));
        mContext.getContentResolver().registerContentObserver(Settings$Global.getUriFor("heads_up_snooze_length_ms"), false, (ContentObserver)new ContentObserver(super.mHandler) {
            public void onChange(final boolean b) {
                final int int1 = Settings$Global.getInt(mContext.getContentResolver(), "heads_up_snooze_length_ms", -1);
                if (int1 > -1) {
                    final HeadsUpManager this$0 = HeadsUpManager.this;
                    if (int1 != this$0.mSnoozeLengthMs) {
                        this$0.mSnoozeLengthMs = int1;
                        if (Log.isLoggable("HeadsUpManager", 2)) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("mSnoozeLengthMs = ");
                            sb.append(HeadsUpManager.this.mSnoozeLengthMs);
                            Log.v("HeadsUpManager", sb.toString());
                        }
                    }
                }
            }
        });
    }
    
    private boolean hasPinnedNotificationInternal() {
        final Iterator<String> iterator = super.mAlertEntries.keySet().iterator();
        while (iterator.hasNext()) {
            if (this.getHeadsUpEntry(iterator.next()).mEntry.isRowPinned()) {
                return true;
            }
        }
        return false;
    }
    
    private static String snoozeKey(final String str, final int i) {
        final StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append(",");
        sb.append(str);
        return sb.toString();
    }
    
    public void addListener(final OnHeadsUpChangedListener e) {
        this.mListeners.add(e);
    }
    
    public int compare(final NotificationEntry notificationEntry, final NotificationEntry notificationEntry2) {
        final HeadsUpEntry headsUpEntry = this.getHeadsUpEntry(notificationEntry.getKey());
        final HeadsUpEntry headsUpEntry2 = this.getHeadsUpEntry(notificationEntry2.getKey());
        if (headsUpEntry != null && headsUpEntry2 != null) {
            return ((AlertEntry)headsUpEntry).compareTo((AlertEntry)headsUpEntry2);
        }
        int n;
        if (headsUpEntry == null) {
            n = 1;
        }
        else {
            n = -1;
        }
        return n;
    }
    
    protected HeadsUpEntry createAlertEntry() {
        return new HeadsUpEntry();
    }
    
    protected void dumpInternal(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.print("  mTouchAcceptanceDelay=");
        printWriter.println(this.mTouchAcceptanceDelay);
        printWriter.print("  mSnoozeLengthMs=");
        printWriter.println(this.mSnoozeLengthMs);
        printWriter.print("  now=");
        printWriter.println(super.mClock.currentTimeMillis());
        printWriter.print("  mUser=");
        printWriter.println(this.mUser);
        for (final AlertEntry alertEntry : super.mAlertEntries.values()) {
            printWriter.print("  HeadsUpEntry=");
            printWriter.println(alertEntry.mEntry);
        }
        final int size = this.mSnoozedPackages.size();
        final StringBuilder sb = new StringBuilder();
        sb.append("  snoozed packages: ");
        sb.append(size);
        printWriter.println(sb.toString());
        for (int i = 0; i < size; ++i) {
            printWriter.print("    ");
            printWriter.print(this.mSnoozedPackages.valueAt(i));
            printWriter.print(", ");
            printWriter.println((String)this.mSnoozedPackages.keyAt(i));
        }
    }
    
    @Override
    public int getContentFlag() {
        return 4;
    }
    
    protected HeadsUpEntry getHeadsUpEntry(final String s) {
        return (HeadsUpEntry)super.mAlertEntries.get((Object)s);
    }
    
    public NotificationEntry getTopEntry() {
        final HeadsUpEntry topHeadsUpEntry = this.getTopHeadsUpEntry();
        NotificationEntry mEntry;
        if (topHeadsUpEntry != null) {
            mEntry = topHeadsUpEntry.mEntry;
        }
        else {
            mEntry = null;
        }
        return mEntry;
    }
    
    protected HeadsUpEntry getTopHeadsUpEntry() {
        final boolean empty = super.mAlertEntries.isEmpty();
        Comparable<AlertEntry> comparable = null;
        if (empty) {
            return null;
        }
        for (final AlertEntry alertEntry : super.mAlertEntries.values()) {
            if (comparable == null || alertEntry.compareTo((AlertEntry)comparable) < 0) {
                comparable = alertEntry;
            }
        }
        return (HeadsUpEntry)comparable;
    }
    
    protected boolean hasFullScreenIntent(final NotificationEntry notificationEntry) {
        return notificationEntry.getSbn().getNotification().fullScreenIntent != null;
    }
    
    public boolean hasPinnedHeadsUp() {
        return this.mHasPinnedNotification;
    }
    
    public boolean isEntryAutoHeadsUpped(final String s) {
        return false;
    }
    
    public boolean isSnoozed(final String s) {
        final String snoozeKey = snoozeKey(s, this.mUser);
        final Long n = (Long)this.mSnoozedPackages.get((Object)snoozeKey);
        if (n != null) {
            if (n > super.mClock.currentTimeMillis()) {
                if (Log.isLoggable("HeadsUpManager", 2)) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(snoozeKey);
                    sb.append(" snoozed");
                    Log.v("HeadsUpManager", sb.toString());
                }
                return true;
            }
            this.mSnoozedPackages.remove((Object)s);
        }
        return false;
    }
    
    public boolean isTrackingHeadsUp() {
        return false;
    }
    
    @Override
    protected void onAlertEntryAdded(final AlertEntry alertEntry) {
        final NotificationEntry mEntry = alertEntry.mEntry;
        mEntry.setHeadsUp(true);
        this.setEntryPinned((HeadsUpEntry)alertEntry, this.shouldHeadsUpBecomePinned(mEntry));
        final Iterator<OnHeadsUpChangedListener> iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onHeadsUpStateChanged(mEntry, true);
        }
    }
    
    @Override
    protected void onAlertEntryRemoved(final AlertEntry alertEntry) {
        final NotificationEntry mEntry = alertEntry.mEntry;
        mEntry.setHeadsUp(false);
        this.setEntryPinned((HeadsUpEntry)alertEntry, false);
        final Iterator<OnHeadsUpChangedListener> iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onHeadsUpStateChanged(mEntry, false);
        }
    }
    
    public void onDensityOrFontScaleChanged() {
    }
    
    public void removeListener(final OnHeadsUpChangedListener o) {
        this.mListeners.remove(o);
    }
    
    protected void setEntryPinned(final HeadsUpEntry headsUpEntry, final boolean b) {
        if (Log.isLoggable("HeadsUpManager", 2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("setEntryPinned: ");
            sb.append(b);
            Log.v("HeadsUpManager", sb.toString());
        }
        final NotificationEntry mEntry = headsUpEntry.mEntry;
        if (mEntry.isRowPinned() != b) {
            mEntry.setRowPinned(b);
            this.updatePinnedMode();
            for (final OnHeadsUpChangedListener onHeadsUpChangedListener : this.mListeners) {
                if (b) {
                    onHeadsUpChangedListener.onHeadsUpPinned(mEntry);
                }
                else {
                    onHeadsUpChangedListener.onHeadsUpUnPinned(mEntry);
                }
            }
        }
    }
    
    public void setExpanded(final NotificationEntry notificationEntry, final boolean expanded) {
        final HeadsUpEntry headsUpEntry = this.getHeadsUpEntry(notificationEntry.getKey());
        if (headsUpEntry != null && notificationEntry.isRowPinned()) {
            headsUpEntry.setExpanded(expanded);
        }
    }
    
    public void setUser(final int mUser) {
        this.mUser = mUser;
    }
    
    protected boolean shouldHeadsUpBecomePinned(final NotificationEntry notificationEntry) {
        return this.hasFullScreenIntent(notificationEntry);
    }
    
    public void snooze() {
        final Iterator<String> iterator = super.mAlertEntries.keySet().iterator();
        while (iterator.hasNext()) {
            this.mSnoozedPackages.put((Object)snoozeKey(this.getHeadsUpEntry(iterator.next()).mEntry.getSbn().getPackageName(), this.mUser), (Object)(super.mClock.currentTimeMillis() + this.mSnoozeLengthMs));
        }
    }
    
    public void unpinAll(final boolean b) {
        final Iterator<String> iterator = super.mAlertEntries.keySet().iterator();
        while (iterator.hasNext()) {
            final HeadsUpEntry headsUpEntry = this.getHeadsUpEntry(iterator.next());
            this.setEntryPinned(headsUpEntry, false);
            ((AlertEntry)headsUpEntry).updateEntry(false);
            if (b) {
                final NotificationEntry mEntry = headsUpEntry.mEntry;
                if (mEntry == null || !mEntry.mustStayOnScreen()) {
                    continue;
                }
                headsUpEntry.mEntry.setHeadsUpIsVisible();
            }
        }
    }
    
    @Override
    public void updateNotification(final String s, final boolean b) {
        super.updateNotification(s, b);
        final HeadsUpEntry headsUpEntry = this.getHeadsUpEntry(s);
        if (b && headsUpEntry != null) {
            this.setEntryPinned(headsUpEntry, this.shouldHeadsUpBecomePinned(headsUpEntry.mEntry));
        }
    }
    
    protected void updatePinnedMode() {
        final boolean hasPinnedNotificationInternal = this.hasPinnedNotificationInternal();
        if (hasPinnedNotificationInternal == this.mHasPinnedNotification) {
            return;
        }
        if (Log.isLoggable("HeadsUpManager", 2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Pinned mode changed: ");
            sb.append(this.mHasPinnedNotification);
            sb.append(" -> ");
            sb.append(hasPinnedNotificationInternal);
            Log.v("HeadsUpManager", sb.toString());
        }
        this.mHasPinnedNotification = hasPinnedNotificationInternal;
        if (hasPinnedNotificationInternal) {
            MetricsLogger.count(this.mContext, "note_peek", 1);
        }
        final Iterator<OnHeadsUpChangedListener> iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onHeadsUpPinnedModeChanged(hasPinnedNotificationInternal);
        }
    }
    
    protected class HeadsUpEntry extends AlertEntry
    {
        protected boolean expanded;
        public boolean remoteInputActive;
        
        @Override
        protected long calculateFinishTime() {
            return super.mPostTime + this.getRecommendedHeadsUpTimeoutMs(HeadsUpManager.this.mAutoDismissNotificationDecay);
        }
        
        @Override
        protected long calculatePostTime() {
            return super.calculatePostTime() + HeadsUpManager.this.mTouchAcceptanceDelay;
        }
        
        @Override
        public int compareTo(final AlertEntry alertEntry) {
            final HeadsUpEntry headsUpEntry = (HeadsUpEntry)alertEntry;
            final boolean rowPinned = super.mEntry.isRowPinned();
            final boolean rowPinned2 = headsUpEntry.mEntry.isRowPinned();
            if (rowPinned && !rowPinned2) {
                return -1;
            }
            if (!rowPinned && rowPinned2) {
                return 1;
            }
            final boolean hasFullScreenIntent = HeadsUpManager.this.hasFullScreenIntent(super.mEntry);
            final boolean hasFullScreenIntent2 = HeadsUpManager.this.hasFullScreenIntent(headsUpEntry.mEntry);
            if (hasFullScreenIntent && !hasFullScreenIntent2) {
                return -1;
            }
            if (!hasFullScreenIntent && hasFullScreenIntent2) {
                return 1;
            }
            if (this.remoteInputActive && !headsUpEntry.remoteInputActive) {
                return -1;
            }
            if (!this.remoteInputActive && headsUpEntry.remoteInputActive) {
                return 1;
            }
            return super.compareTo((AlertEntry)headsUpEntry);
        }
        
        protected int getRecommendedHeadsUpTimeoutMs(final int n) {
            return HeadsUpManager.this.mAccessibilityMgr.getRecommendedTimeoutMillis(n, 7);
        }
        
        @Override
        protected boolean isSticky() {
            return (super.mEntry.isRowPinned() && this.expanded) || this.remoteInputActive || HeadsUpManager.this.hasFullScreenIntent(super.mEntry);
        }
        
        @Override
        public void reset() {
            super.reset();
            this.expanded = false;
            this.remoteInputActive = false;
        }
        
        public void setExpanded(final boolean expanded) {
            this.expanded = expanded;
        }
    }
}
