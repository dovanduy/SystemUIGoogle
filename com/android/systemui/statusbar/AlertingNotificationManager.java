// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.os.SystemClock;
import java.util.Iterator;
import java.util.Collection;
import android.util.Log;
import java.util.function.Function;
import java.util.stream.Stream;
import android.os.Looper;
import com.android.internal.annotations.VisibleForTesting;
import android.os.Handler;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.util.ArraySet;
import android.util.ArrayMap;

public abstract class AlertingNotificationManager implements NotificationLifetimeExtender
{
    protected final ArrayMap<String, AlertEntry> mAlertEntries;
    protected int mAutoDismissNotificationDecay;
    protected final Clock mClock;
    protected final ArraySet<NotificationEntry> mExtendedLifetimeAlertEntries;
    @VisibleForTesting
    public Handler mHandler;
    protected int mMinimumDisplayTime;
    protected NotificationSafeToRemoveCallback mNotificationLifetimeFinishedCallback;
    
    public AlertingNotificationManager() {
        this.mClock = new Clock();
        this.mAlertEntries = (ArrayMap<String, AlertEntry>)new ArrayMap();
        this.mExtendedLifetimeAlertEntries = (ArraySet<NotificationEntry>)new ArraySet();
        this.mHandler = new Handler(Looper.getMainLooper());
    }
    
    protected final void addAlertEntry(final NotificationEntry entry) {
        final AlertEntry alertEntry = this.createAlertEntry();
        alertEntry.setEntry(entry);
        this.mAlertEntries.put((Object)entry.getKey(), (Object)alertEntry);
        this.onAlertEntryAdded(alertEntry);
        entry.sendAccessibilityEvent(2048);
    }
    
    protected boolean canRemoveImmediately(final String s) {
        final AlertEntry alertEntry = (AlertEntry)this.mAlertEntries.get((Object)s);
        return alertEntry == null || alertEntry.wasShownLongEnough() || alertEntry.mEntry.isRowDismissed();
    }
    
    protected AlertEntry createAlertEntry() {
        return new AlertEntry();
    }
    
    public Stream<NotificationEntry> getAllEntries() {
        return this.mAlertEntries.values().stream().map((Function<? super Object, ? extends NotificationEntry>)_$$Lambda$AlertingNotificationManager$p_A8_yzC_BK0PtkudKAmBZE_xfo.INSTANCE);
    }
    
    public abstract int getContentFlag();
    
    public boolean hasNotifications() {
        return this.mAlertEntries.isEmpty() ^ true;
    }
    
    public boolean isAlerting(final String s) {
        return this.mAlertEntries.containsKey((Object)s);
    }
    
    protected abstract void onAlertEntryAdded(final AlertEntry p0);
    
    protected abstract void onAlertEntryRemoved(final AlertEntry p0);
    
    public void releaseAllImmediately() {
        if (Log.isLoggable("AlertNotifManager", 2)) {
            Log.v("AlertNotifManager", "releaseAllImmediately");
        }
        final Iterator iterator = new ArraySet((Collection)this.mAlertEntries.keySet()).iterator();
        while (iterator.hasNext()) {
            this.removeAlertEntry(iterator.next());
        }
    }
    
    protected final void removeAlertEntry(final String s) {
        final AlertEntry alertEntry = (AlertEntry)this.mAlertEntries.get((Object)s);
        if (alertEntry == null) {
            return;
        }
        final NotificationEntry mEntry = alertEntry.mEntry;
        this.mAlertEntries.remove((Object)s);
        this.onAlertEntryRemoved(alertEntry);
        mEntry.sendAccessibilityEvent(2048);
        alertEntry.reset();
        if (this.mExtendedLifetimeAlertEntries.contains((Object)mEntry)) {
            final NotificationSafeToRemoveCallback mNotificationLifetimeFinishedCallback = this.mNotificationLifetimeFinishedCallback;
            if (mNotificationLifetimeFinishedCallback != null) {
                mNotificationLifetimeFinishedCallback.onSafeToRemove(s);
            }
            this.mExtendedLifetimeAlertEntries.remove((Object)mEntry);
        }
    }
    
    public boolean removeNotification(final String s, final boolean b) {
        if (Log.isLoggable("AlertNotifManager", 2)) {
            Log.v("AlertNotifManager", "removeNotification");
        }
        final AlertEntry alertEntry = (AlertEntry)this.mAlertEntries.get((Object)s);
        if (alertEntry == null) {
            return true;
        }
        if (!b && !this.canRemoveImmediately(s)) {
            alertEntry.removeAsSoonAsPossible();
            return false;
        }
        this.removeAlertEntry(s);
        return true;
    }
    
    @Override
    public void setCallback(final NotificationSafeToRemoveCallback mNotificationLifetimeFinishedCallback) {
        this.mNotificationLifetimeFinishedCallback = mNotificationLifetimeFinishedCallback;
    }
    
    @Override
    public void setShouldManageLifetime(final NotificationEntry notificationEntry, final boolean b) {
        if (b) {
            this.mExtendedLifetimeAlertEntries.add((Object)notificationEntry);
            ((AlertEntry)this.mAlertEntries.get((Object)notificationEntry.getKey())).removeAsSoonAsPossible();
        }
        else {
            this.mExtendedLifetimeAlertEntries.remove((Object)notificationEntry);
        }
    }
    
    @Override
    public boolean shouldExtendLifetime(final NotificationEntry notificationEntry) {
        return this.canRemoveImmediately(notificationEntry.getKey()) ^ true;
    }
    
    public void showNotification(final NotificationEntry notificationEntry) {
        if (Log.isLoggable("AlertNotifManager", 2)) {
            Log.v("AlertNotifManager", "showNotification");
        }
        this.addAlertEntry(notificationEntry);
        this.updateNotification(notificationEntry.getKey(), true);
        notificationEntry.setInterruption();
    }
    
    public void updateNotification(final String s, final boolean b) {
        if (Log.isLoggable("AlertNotifManager", 2)) {
            Log.v("AlertNotifManager", "updateNotification");
        }
        final AlertEntry alertEntry = (AlertEntry)this.mAlertEntries.get((Object)s);
        if (alertEntry == null) {
            return;
        }
        alertEntry.mEntry.sendAccessibilityEvent(2048);
        if (b) {
            alertEntry.updateEntry(true);
        }
    }
    
    protected class AlertEntry implements Comparable<AlertEntry>
    {
        public long mEarliestRemovaltime;
        public NotificationEntry mEntry;
        public long mPostTime;
        protected Runnable mRemoveAlertRunnable;
        
        protected long calculateFinishTime() {
            return this.mPostTime + AlertingNotificationManager.this.mAutoDismissNotificationDecay;
        }
        
        protected long calculatePostTime() {
            return AlertingNotificationManager.this.mClock.currentTimeMillis();
        }
        
        @Override
        public int compareTo(final AlertEntry alertEntry) {
            final long mPostTime = this.mPostTime;
            final long mPostTime2 = alertEntry.mPostTime;
            int compareTo;
            if (mPostTime < mPostTime2) {
                compareTo = 1;
            }
            else if (mPostTime == mPostTime2) {
                compareTo = this.mEntry.getKey().compareTo(alertEntry.mEntry.getKey());
            }
            else {
                compareTo = -1;
            }
            return compareTo;
        }
        
        protected boolean isSticky() {
            return false;
        }
        
        public void removeAsSoonAsPossible() {
            if (this.mRemoveAlertRunnable != null) {
                this.removeAutoRemovalCallbacks();
                final AlertingNotificationManager this$0 = AlertingNotificationManager.this;
                this$0.mHandler.postDelayed(this.mRemoveAlertRunnable, this.mEarliestRemovaltime - this$0.mClock.currentTimeMillis());
            }
        }
        
        public void removeAutoRemovalCallbacks() {
            final Runnable mRemoveAlertRunnable = this.mRemoveAlertRunnable;
            if (mRemoveAlertRunnable != null) {
                AlertingNotificationManager.this.mHandler.removeCallbacks(mRemoveAlertRunnable);
            }
        }
        
        public void reset() {
            this.mEntry = null;
            this.removeAutoRemovalCallbacks();
            this.mRemoveAlertRunnable = null;
        }
        
        public void setEntry(final NotificationEntry notificationEntry) {
            this.setEntry(notificationEntry, new _$$Lambda$AlertingNotificationManager$AlertEntry$H0BO9fDKgUoiMeVuexcatZzpMyY(this, notificationEntry));
        }
        
        public void setEntry(final NotificationEntry mEntry, final Runnable mRemoveAlertRunnable) {
            this.mEntry = mEntry;
            this.mRemoveAlertRunnable = mRemoveAlertRunnable;
            this.mPostTime = this.calculatePostTime();
            this.updateEntry(true);
        }
        
        public void updateEntry(final boolean b) {
            if (Log.isLoggable("AlertNotifManager", 2)) {
                Log.v("AlertNotifManager", "updateEntry");
            }
            final long currentTimeMillis = AlertingNotificationManager.this.mClock.currentTimeMillis();
            this.mEarliestRemovaltime = AlertingNotificationManager.this.mMinimumDisplayTime + currentTimeMillis;
            if (b) {
                this.mPostTime = Math.max(this.mPostTime, currentTimeMillis);
            }
            this.removeAutoRemovalCallbacks();
            if (!this.isSticky()) {
                AlertingNotificationManager.this.mHandler.postDelayed(this.mRemoveAlertRunnable, Math.max(this.calculateFinishTime() - currentTimeMillis, AlertingNotificationManager.this.mMinimumDisplayTime));
            }
        }
        
        public boolean wasShownLongEnough() {
            return this.mEarliestRemovaltime < AlertingNotificationManager.this.mClock.currentTimeMillis();
        }
    }
    
    protected static final class Clock
    {
        public long currentTimeMillis() {
            return SystemClock.elapsedRealtime();
        }
    }
}
