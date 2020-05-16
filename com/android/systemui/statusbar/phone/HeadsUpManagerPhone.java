// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.R$dimen;
import android.util.ArrayMap;
import android.content.res.Resources;
import com.android.systemui.R$integer;
import java.util.Iterator;
import com.android.systemui.statusbar.AlertingNotificationManager;
import java.util.Stack;
import java.util.ArrayList;
import com.android.systemui.statusbar.policy.ConfigurationController;
import android.content.Context;
import android.graphics.Region;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import java.util.List;
import com.android.internal.annotations.VisibleForTesting;
import android.util.Pools$Pool;
import androidx.collection.ArraySet;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.HashSet;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.policy.HeadsUpManager;

public class HeadsUpManagerPhone extends HeadsUpManager implements Dumpable, Callback, OnHeadsUpChangedListener
{
    private AnimationStateHandler mAnimationStateHandler;
    private final int mAutoHeadsUpNotificationDecay;
    private final KeyguardBypassController mBypassController;
    private HashSet<NotificationEntry> mEntriesToRemoveAfterExpand;
    private ArraySet<NotificationEntry> mEntriesToRemoveWhenReorderingAllowed;
    private final Pools$Pool<HeadsUpEntryPhone> mEntryPool;
    @VisibleForTesting
    final int mExtensionTime;
    private final NotificationGroupManager mGroupManager;
    private boolean mHeadsUpGoingAway;
    private int mHeadsUpInset;
    private final List<OnHeadsUpPhoneListenerChange> mHeadsUpPhoneListeners;
    private boolean mIsExpanded;
    private HashSet<String> mKeysToRemoveWhenLeavingKeyguard;
    private boolean mReleaseOnExpandFinish;
    private int mStatusBarState;
    private final StatusBarStateController.StateListener mStatusBarStateListener;
    private HashSet<String> mSwipedOutKeys;
    private final Region mTouchableRegion;
    private boolean mTrackingHeadsUp;
    private VisualStabilityManager mVisualStabilityManager;
    
    public HeadsUpManagerPhone(final Context context, final StatusBarStateController statusBarStateController, final KeyguardBypassController mBypassController, final NotificationGroupManager mGroupManager, final ConfigurationController configurationController) {
        super(context);
        this.mHeadsUpPhoneListeners = new ArrayList<OnHeadsUpPhoneListenerChange>();
        this.mSwipedOutKeys = new HashSet<String>();
        this.mEntriesToRemoveAfterExpand = new HashSet<NotificationEntry>();
        this.mKeysToRemoveWhenLeavingKeyguard = new HashSet<String>();
        this.mEntriesToRemoveWhenReorderingAllowed = new ArraySet<NotificationEntry>();
        this.mTouchableRegion = new Region();
        this.mEntryPool = (Pools$Pool<HeadsUpEntryPhone>)new Pools$Pool<HeadsUpEntryPhone>() {
            private Stack<HeadsUpEntryPhone> mPoolObjects = new Stack<HeadsUpEntryPhone>();
            
            public HeadsUpEntryPhone acquire() {
                if (!this.mPoolObjects.isEmpty()) {
                    return this.mPoolObjects.pop();
                }
                return new HeadsUpEntryPhone();
            }
            
            public boolean release(final HeadsUpEntryPhone item) {
                this.mPoolObjects.push(item);
                return true;
            }
        };
        this.mStatusBarStateListener = new StatusBarStateController.StateListener() {
            @Override
            public void onDozingChanged(final boolean b) {
                if (!b) {
                    final Iterator<AlertEntry> iterator = HeadsUpManagerPhone.this.mAlertEntries.values().iterator();
                    while (iterator.hasNext()) {
                        iterator.next().updateEntry(true);
                    }
                }
            }
            
            @Override
            public void onStateChanged(int i) {
                final int access$1000 = HeadsUpManagerPhone.this.mStatusBarState;
                final int n = 0;
                boolean b = true;
                final boolean b2 = access$1000 == 1;
                if (i != 1) {
                    b = false;
                }
                HeadsUpManagerPhone.this.mStatusBarState = i;
                if (b2 && !b && HeadsUpManagerPhone.this.mKeysToRemoveWhenLeavingKeyguard.size() != 0) {
                    final String[] array = (String[])HeadsUpManagerPhone.this.mKeysToRemoveWhenLeavingKeyguard.toArray(new String[0]);
                    int length;
                    for (length = array.length, i = n; i < length; ++i) {
                        AlertingNotificationManager.this.removeAlertEntry(array[i]);
                    }
                    HeadsUpManagerPhone.this.mKeysToRemoveWhenLeavingKeyguard.clear();
                }
            }
        };
        final Resources resources = super.mContext.getResources();
        this.mExtensionTime = resources.getInteger(R$integer.ambient_notification_extension_time);
        this.mAutoHeadsUpNotificationDecay = resources.getInteger(R$integer.auto_heads_up_notification_decay);
        statusBarStateController.addCallback(this.mStatusBarStateListener);
        this.mBypassController = mBypassController;
        this.mGroupManager = mGroupManager;
        this.updateResources();
        configurationController.addCallback((ConfigurationController.ConfigurationListener)new ConfigurationController.ConfigurationListener() {
            @Override
            public void onDensityOrFontScaleChanged() {
                HeadsUpManagerPhone.this.updateResources();
            }
            
            @Override
            public void onOverlayChanged() {
                HeadsUpManagerPhone.this.updateResources();
            }
        });
    }
    
    private HeadsUpEntryPhone getHeadsUpEntryPhone(final String s) {
        return (HeadsUpEntryPhone)super.mAlertEntries.get((Object)s);
    }
    
    private HeadsUpEntryPhone getTopHeadsUpEntryPhone() {
        return (HeadsUpEntryPhone)this.getTopHeadsUpEntry();
    }
    
    private void updateResources() {
        final Resources resources = super.mContext.getResources();
        this.mHeadsUpInset = resources.getDimensionPixelSize(17105471) + resources.getDimensionPixelSize(R$dimen.heads_up_status_bar_padding);
    }
    
    void addHeadsUpPhoneListener(final OnHeadsUpPhoneListenerChange onHeadsUpPhoneListenerChange) {
        this.mHeadsUpPhoneListeners.add(onHeadsUpPhoneListenerChange);
    }
    
    public void addSwipedOutNotification(final String e) {
        this.mSwipedOutKeys.add(e);
    }
    
    @Override
    protected boolean canRemoveImmediately(final String s) {
        final boolean contains = this.mSwipedOutKeys.contains(s);
        final boolean b = true;
        if (contains) {
            this.mSwipedOutKeys.remove(s);
            return true;
        }
        final HeadsUpEntryPhone headsUpEntryPhone = this.getHeadsUpEntryPhone(s);
        final HeadsUpEntryPhone topHeadsUpEntryPhone = this.getTopHeadsUpEntryPhone();
        boolean b2 = b;
        if (headsUpEntryPhone != null) {
            b2 = b;
            if (headsUpEntryPhone == topHeadsUpEntryPhone) {
                b2 = (super.canRemoveImmediately(s) && b);
            }
        }
        return b2;
    }
    
    @Override
    protected HeadsUpEntry createAlertEntry() {
        return (HeadsUpEntry)this.mEntryPool.acquire();
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("HeadsUpManagerPhone state:");
        this.dumpInternal(fileDescriptor, printWriter, array);
    }
    
    @Override
    protected void dumpInternal(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        super.dumpInternal(fileDescriptor, printWriter, array);
        printWriter.print("  mBarState=");
        printWriter.println(this.mStatusBarState);
        printWriter.print("  mTouchableRegion=");
        printWriter.println(this.mTouchableRegion);
    }
    
    public void extendHeadsUp() {
        final HeadsUpEntryPhone topHeadsUpEntryPhone = this.getTopHeadsUpEntryPhone();
        if (topHeadsUpEntryPhone == null) {
            return;
        }
        topHeadsUpEntryPhone.extendPulse();
    }
    
    Region getTouchableRegion() {
        final NotificationEntry topEntry = this.getTopEntry();
        if (this.hasPinnedHeadsUp() && topEntry != null) {
            NotificationEntry notificationEntry = topEntry;
            if (topEntry.isChildInGroup()) {
                final NotificationEntry groupSummary = this.mGroupManager.getGroupSummary(topEntry.getSbn());
                notificationEntry = topEntry;
                if (groupSummary != null) {
                    notificationEntry = groupSummary;
                }
            }
            final ExpandableNotificationRow row = notificationEntry.getRow();
            final int[] array = new int[2];
            row.getLocationOnScreen(array);
            this.mTouchableRegion.set(array[0], 0, array[0] + row.getWidth(), this.mHeadsUpInset + row.getIntrinsicHeight());
            return this.mTouchableRegion;
        }
        return null;
    }
    
    @Override
    public boolean isEntryAutoHeadsUpped(final String s) {
        final HeadsUpEntryPhone headsUpEntryPhone = this.getHeadsUpEntryPhone(s);
        return headsUpEntryPhone != null && headsUpEntryPhone.isAutoHeadsUp();
    }
    
    boolean isHeadsUpGoingAway() {
        return this.mHeadsUpGoingAway;
    }
    
    @Override
    public boolean isTrackingHeadsUp() {
        return this.mTrackingHeadsUp;
    }
    
    @Override
    protected void onAlertEntryRemoved(final AlertEntry alertEntry) {
        this.mKeysToRemoveWhenLeavingKeyguard.remove(alertEntry.mEntry.getKey());
        super.onAlertEntryRemoved(alertEntry);
        this.mEntryPool.release((Object)alertEntry);
    }
    
    @Override
    public void onChangeAllowed() {
        this.mAnimationStateHandler.setHeadsUpGoingAwayAnimationsAllowed(false);
        for (final NotificationEntry notificationEntry : this.mEntriesToRemoveWhenReorderingAllowed) {
            if (this.isAlerting(notificationEntry.getKey())) {
                this.removeAlertEntry(notificationEntry.getKey());
            }
        }
        this.mEntriesToRemoveWhenReorderingAllowed.clear();
        this.mAnimationStateHandler.setHeadsUpGoingAwayAnimationsAllowed(true);
    }
    
    public void onExpandingFinished() {
        if (this.mReleaseOnExpandFinish) {
            this.releaseAllImmediately();
            this.mReleaseOnExpandFinish = false;
        }
        else {
            for (final NotificationEntry notificationEntry : this.mEntriesToRemoveAfterExpand) {
                if (this.isAlerting(notificationEntry.getKey())) {
                    this.removeAlertEntry(notificationEntry.getKey());
                }
            }
        }
        this.mEntriesToRemoveAfterExpand.clear();
    }
    
    public void setAnimationStateHandler(final AnimationStateHandler mAnimationStateHandler) {
        this.mAnimationStateHandler = mAnimationStateHandler;
    }
    
    void setHeadsUpGoingAway(final boolean mHeadsUpGoingAway) {
        if (mHeadsUpGoingAway != this.mHeadsUpGoingAway) {
            this.mHeadsUpGoingAway = mHeadsUpGoingAway;
            final Iterator<OnHeadsUpPhoneListenerChange> iterator = this.mHeadsUpPhoneListeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().onHeadsUpGoingAwayStateChanged(mHeadsUpGoingAway);
            }
        }
    }
    
    void setIsPanelExpanded(final boolean mIsExpanded) {
        if (mIsExpanded != this.mIsExpanded && (this.mIsExpanded = mIsExpanded)) {
            this.mHeadsUpGoingAway = false;
        }
    }
    
    public void setMenuShown(final NotificationEntry notificationEntry, final boolean menuShownPinned) {
        final HeadsUpEntry headsUpEntry = this.getHeadsUpEntry(notificationEntry.getKey());
        if (headsUpEntry instanceof HeadsUpEntryPhone && notificationEntry.isRowPinned()) {
            ((HeadsUpEntryPhone)headsUpEntry).setMenuShownPinned(menuShownPinned);
        }
    }
    
    public void setRemoteInputActive(final NotificationEntry notificationEntry, final boolean remoteInputActive) {
        final HeadsUpEntryPhone headsUpEntryPhone = this.getHeadsUpEntryPhone(notificationEntry.getKey());
        if (headsUpEntryPhone != null && headsUpEntryPhone.remoteInputActive != remoteInputActive) {
            headsUpEntryPhone.remoteInputActive = remoteInputActive;
            if (remoteInputActive) {
                ((AlertEntry)headsUpEntryPhone).removeAutoRemovalCallbacks();
            }
            else {
                headsUpEntryPhone.updateEntry(false);
            }
        }
    }
    
    public void setTrackingHeadsUp(final boolean mTrackingHeadsUp) {
        this.mTrackingHeadsUp = mTrackingHeadsUp;
    }
    
    void setup(final VisualStabilityManager mVisualStabilityManager) {
        this.mVisualStabilityManager = mVisualStabilityManager;
    }
    
    @Override
    public boolean shouldExtendLifetime(final NotificationEntry notificationEntry) {
        return this.mVisualStabilityManager.isReorderingAllowed() && super.shouldExtendLifetime(notificationEntry);
    }
    
    @Override
    protected boolean shouldHeadsUpBecomePinned(final NotificationEntry notificationEntry) {
        final int mStatusBarState = this.mStatusBarState;
        boolean b = false;
        boolean b2 = mStatusBarState == 0 && !this.mIsExpanded;
        if (this.mBypassController.getBypassEnabled()) {
            b2 |= (this.mStatusBarState == 1);
        }
        if (b2 || super.shouldHeadsUpBecomePinned(notificationEntry)) {
            b = true;
        }
        return b;
    }
    
    boolean shouldSwallowClick(final String s) {
        final HeadsUpEntry headsUpEntry = this.getHeadsUpEntry(s);
        return headsUpEntry != null && super.mClock.currentTimeMillis() < headsUpEntry.mPostTime;
    }
    
    @Override
    public void snooze() {
        super.snooze();
        this.mReleaseOnExpandFinish = true;
    }
    
    public interface AnimationStateHandler
    {
        void setHeadsUpGoingAwayAnimationsAllowed(final boolean p0);
    }
    
    protected class HeadsUpEntryPhone extends HeadsUpEntry
    {
        private boolean extended;
        private boolean mIsAutoHeadsUp;
        private boolean mMenuShownPinned;
        
        private void extendPulse() {
            if (!this.extended) {
                this.extended = true;
                this.updateEntry(false);
            }
        }
        
        private int getDecayDuration() {
            if (this.isAutoHeadsUp()) {
                return ((HeadsUpEntry)this).getRecommendedHeadsUpTimeoutMs(HeadsUpManagerPhone.this.mAutoHeadsUpNotificationDecay);
            }
            return ((HeadsUpEntry)this).getRecommendedHeadsUpTimeoutMs(HeadsUpManagerPhone.this.mAutoDismissNotificationDecay);
        }
        
        private boolean isAutoHeadsUp() {
            return this.mIsAutoHeadsUp;
        }
        
        @Override
        protected long calculateFinishTime() {
            final long mPostTime = super.mPostTime;
            final long n = this.getDecayDuration();
            int mExtensionTime;
            if (this.extended) {
                mExtensionTime = HeadsUpManagerPhone.this.mExtensionTime;
            }
            else {
                mExtensionTime = 0;
            }
            return mPostTime + n + mExtensionTime;
        }
        
        @Override
        public int compareTo(final AlertEntry alertEntry) {
            final HeadsUpEntryPhone headsUpEntryPhone = (HeadsUpEntryPhone)alertEntry;
            final boolean autoHeadsUp = this.isAutoHeadsUp();
            final boolean autoHeadsUp2 = headsUpEntryPhone.isAutoHeadsUp();
            if (autoHeadsUp && !autoHeadsUp2) {
                return 1;
            }
            if (!autoHeadsUp && autoHeadsUp2) {
                return -1;
            }
            return super.compareTo(alertEntry);
        }
        
        @Override
        protected boolean isSticky() {
            return super.isSticky() || this.mMenuShownPinned;
        }
        
        @Override
        public void reset() {
            super.reset();
            this.mMenuShownPinned = false;
            this.extended = false;
            this.mIsAutoHeadsUp = false;
        }
        
        @Override
        public void setEntry(final NotificationEntry notificationEntry) {
            ((AlertEntry)this).setEntry(notificationEntry, new _$$Lambda$HeadsUpManagerPhone$HeadsUpEntryPhone$adyrhF30JE9Yr0JaVKYkiAV0Clw(this, notificationEntry));
        }
        
        @Override
        public void setExpanded(final boolean expanded) {
            if (super.expanded == expanded) {
                return;
            }
            super.expanded = expanded;
            if (expanded) {
                ((AlertEntry)this).removeAutoRemovalCallbacks();
            }
            else {
                this.updateEntry(false);
            }
        }
        
        public void setMenuShownPinned(final boolean mMenuShownPinned) {
            if (this.mMenuShownPinned == mMenuShownPinned) {
                return;
            }
            this.mMenuShownPinned = mMenuShownPinned;
            if (mMenuShownPinned) {
                ((AlertEntry)this).removeAutoRemovalCallbacks();
            }
            else {
                this.updateEntry(false);
            }
        }
        
        @Override
        public void updateEntry(final boolean b) {
            this.mIsAutoHeadsUp = super.mEntry.isAutoHeadsUp();
            super.updateEntry(b);
            if (HeadsUpManagerPhone.this.mEntriesToRemoveAfterExpand.contains(super.mEntry)) {
                HeadsUpManagerPhone.this.mEntriesToRemoveAfterExpand.remove(super.mEntry);
            }
            if (HeadsUpManagerPhone.this.mEntriesToRemoveWhenReorderingAllowed.contains(super.mEntry)) {
                HeadsUpManagerPhone.this.mEntriesToRemoveWhenReorderingAllowed.remove(super.mEntry);
            }
            HeadsUpManagerPhone.this.mKeysToRemoveWhenLeavingKeyguard.remove(super.mEntry.getKey());
        }
    }
    
    public interface OnHeadsUpPhoneListenerChange
    {
        void onHeadsUpGoingAwayStateChanged(final boolean p0);
    }
}
