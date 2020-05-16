// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.NotificationPresenter;
import android.os.SystemClock;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.os.Handler;
import java.util.ArrayList;
import android.view.View;
import androidx.collection.ArraySet;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;

public class VisualStabilityManager implements OnHeadsUpChangedListener, Dumpable
{
    private ArraySet<View> mAddedChildren;
    private ArraySet<View> mAllowedReorderViews;
    private boolean mGroupChangedAllowed;
    private final ArrayList<Callback> mGroupChangesAllowedCallbacks;
    private final Handler mHandler;
    private boolean mIsTemporaryReorderingAllowed;
    private ArraySet<NotificationEntry> mLowPriorityReorderingViews;
    private final Runnable mOnTemporaryReorderingExpired;
    private boolean mPanelExpanded;
    private boolean mPulsing;
    private boolean mReorderingAllowed;
    private final ArrayList<Callback> mReorderingAllowedCallbacks;
    private boolean mScreenOn;
    private long mTemporaryReorderingStart;
    private VisibilityLocationProvider mVisibilityLocationProvider;
    
    public VisualStabilityManager(final NotificationEntryManager notificationEntryManager, final Handler mHandler) {
        this.mReorderingAllowedCallbacks = new ArrayList<Callback>();
        this.mGroupChangesAllowedCallbacks = new ArrayList<Callback>();
        this.mAllowedReorderViews = new ArraySet<View>();
        this.mLowPriorityReorderingViews = new ArraySet<NotificationEntry>();
        this.mAddedChildren = new ArraySet<View>();
        this.mOnTemporaryReorderingExpired = new _$$Lambda$VisualStabilityManager$6rf_6W4K3PrMdhwP_O1LDBveJ6k(this);
        this.mHandler = mHandler;
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            @Override
            public void onPreEntryUpdated(final NotificationEntry notificationEntry) {
                if (notificationEntry.isAmbient() != notificationEntry.getRow().isLowPriority()) {
                    VisualStabilityManager.this.mLowPriorityReorderingViews.add(notificationEntry);
                }
            }
        });
    }
    
    private void notifyChangeAllowed(final ArrayList<Callback> list) {
        for (int i = 0; i < list.size(); ++i) {
            list.get(i).onChangeAllowed();
        }
        list.clear();
    }
    
    private void updateAllowedStates() {
        final boolean mScreenOn = this.mScreenOn;
        final int n = 1;
        final boolean mReorderingAllowed = (!mScreenOn || !this.mPanelExpanded || this.mIsTemporaryReorderingAllowed) && !this.mPulsing;
        final boolean b = mReorderingAllowed && !this.mReorderingAllowed;
        this.mReorderingAllowed = mReorderingAllowed;
        if (b) {
            this.notifyChangeAllowed(this.mReorderingAllowedCallbacks);
        }
        final boolean mGroupChangedAllowed = (!this.mScreenOn || !this.mPanelExpanded) && !this.mPulsing;
        int n2;
        if (mGroupChangedAllowed && !this.mGroupChangedAllowed) {
            n2 = n;
        }
        else {
            n2 = 0;
        }
        this.mGroupChangedAllowed = mGroupChangedAllowed;
        if (n2 != 0) {
            this.notifyChangeAllowed(this.mGroupChangesAllowedCallbacks);
        }
    }
    
    public void addGroupChangesAllowedCallback(final Callback callback) {
        if (this.mGroupChangesAllowedCallbacks.contains(callback)) {
            return;
        }
        this.mGroupChangesAllowedCallbacks.add(callback);
    }
    
    public void addReorderingAllowedCallback(final Callback callback) {
        if (this.mReorderingAllowedCallbacks.contains(callback)) {
            return;
        }
        this.mReorderingAllowedCallbacks.add(callback);
    }
    
    public boolean areGroupChangesAllowed() {
        return this.mGroupChangedAllowed;
    }
    
    public boolean canReorderNotification(final ExpandableNotificationRow expandableNotificationRow) {
        return this.mReorderingAllowed || this.mAddedChildren.contains(expandableNotificationRow) || this.mLowPriorityReorderingViews.contains(expandableNotificationRow.getEntry()) || (this.mAllowedReorderViews.contains(expandableNotificationRow) && !this.mVisibilityLocationProvider.isInVisibleLocation(expandableNotificationRow.getEntry()));
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("VisualStabilityManager state:");
        printWriter.print("  mIsTemporaryReorderingAllowed=");
        printWriter.println(this.mIsTemporaryReorderingAllowed);
        printWriter.print("  mTemporaryReorderingStart=");
        printWriter.println(this.mTemporaryReorderingStart);
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        printWriter.print("    Temporary reordering window has been open for ");
        long mTemporaryReorderingStart;
        if (this.mIsTemporaryReorderingAllowed) {
            mTemporaryReorderingStart = this.mTemporaryReorderingStart;
        }
        else {
            mTemporaryReorderingStart = elapsedRealtime;
        }
        printWriter.print(elapsedRealtime - mTemporaryReorderingStart);
        printWriter.println("ms");
        printWriter.println();
    }
    
    public boolean isReorderingAllowed() {
        return this.mReorderingAllowed;
    }
    
    public void notifyViewAddition(final View view) {
        this.mAddedChildren.add(view);
    }
    
    @Override
    public void onHeadsUpStateChanged(final NotificationEntry notificationEntry, final boolean b) {
        if (b) {
            this.mAllowedReorderViews.add((View)notificationEntry.getRow());
        }
    }
    
    public void onReorderingFinished() {
        this.mAllowedReorderViews.clear();
        this.mAddedChildren.clear();
        this.mLowPriorityReorderingViews.clear();
    }
    
    public void setPanelExpanded(final boolean mPanelExpanded) {
        this.mPanelExpanded = mPanelExpanded;
        this.updateAllowedStates();
    }
    
    public void setPulsing(final boolean mPulsing) {
        if (this.mPulsing == mPulsing) {
            return;
        }
        this.mPulsing = mPulsing;
        this.updateAllowedStates();
    }
    
    public void setScreenOn(final boolean mScreenOn) {
        this.mScreenOn = mScreenOn;
        this.updateAllowedStates();
    }
    
    public void setUpWithPresenter(final NotificationPresenter notificationPresenter) {
    }
    
    public void setVisibilityLocationProvider(final VisibilityLocationProvider mVisibilityLocationProvider) {
        this.mVisibilityLocationProvider = mVisibilityLocationProvider;
    }
    
    public void temporarilyAllowReordering() {
        this.mHandler.removeCallbacks(this.mOnTemporaryReorderingExpired);
        this.mHandler.postDelayed(this.mOnTemporaryReorderingExpired, 1000L);
        if (!this.mIsTemporaryReorderingAllowed) {
            this.mTemporaryReorderingStart = SystemClock.elapsedRealtime();
        }
        this.mIsTemporaryReorderingAllowed = true;
        this.updateAllowedStates();
    }
    
    public interface Callback
    {
        void onChangeAllowed();
    }
}
