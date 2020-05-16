// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.logging;

import android.util.Log;
import android.util.ArrayMap;
import java.util.Map;
import java.util.Collections;
import com.android.internal.annotations.VisibleForTesting;
import android.os.RemoteException;
import com.android.internal.statusbar.NotificationVisibility$NotificationLocation;
import java.util.Iterator;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.internal.statusbar.IStatusBarService$Stub;
import android.os.ServiceManager;
import java.util.List;
import java.util.Collection;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.os.SystemClock;
import com.android.systemui.statusbar.NotificationListener;
import java.util.concurrent.Executor;
import android.service.notification.NotificationListenerService;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import android.os.Handler;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.internal.statusbar.NotificationVisibility;
import android.util.ArraySet;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.plugins.statusbar.StatusBarStateController;

public class NotificationLogger implements StateListener
{
    protected IStatusBarService mBarService;
    private final ArraySet<NotificationVisibility> mCurrentlyVisibleNotifications;
    private boolean mDozing;
    private final Object mDozingLock;
    private final NotificationEntryManager mEntryManager;
    private final ExpansionStateLogger mExpansionStateLogger;
    protected Handler mHandler;
    private HeadsUpManager mHeadsUpManager;
    private long mLastVisibilityReportUptimeMs;
    private NotificationListContainer mListContainer;
    private final NotificationListenerService mNotificationListener;
    protected final OnChildLocationsChangedListener mNotificationLocationsChangedListener;
    private final NotificationPanelLogger mNotificationPanelLogger;
    private final Executor mUiBgExecutor;
    protected Runnable mVisibilityReporter;
    
    public NotificationLogger(final NotificationListener mNotificationListener, final Executor mUiBgExecutor, final NotificationEntryManager mEntryManager, final StatusBarStateController statusBarStateController, final ExpansionStateLogger mExpansionStateLogger, final NotificationPanelLogger mNotificationPanelLogger) {
        this.mCurrentlyVisibleNotifications = (ArraySet<NotificationVisibility>)new ArraySet();
        this.mHandler = new Handler();
        this.mDozingLock = new Object();
        this.mNotificationLocationsChangedListener = (OnChildLocationsChangedListener)new OnChildLocationsChangedListener() {
            @Override
            public void onChildLocationsChanged() {
                final NotificationLogger this$0 = NotificationLogger.this;
                if (this$0.mHandler.hasCallbacks(this$0.mVisibilityReporter)) {
                    return;
                }
                final long access$000 = NotificationLogger.this.mLastVisibilityReportUptimeMs;
                final NotificationLogger this$2 = NotificationLogger.this;
                this$2.mHandler.postAtTime(this$2.mVisibilityReporter, access$000 + 500L);
            }
        };
        this.mVisibilityReporter = new Runnable() {
            private final ArraySet<NotificationVisibility> mTmpCurrentlyVisibleNotifications = new ArraySet();
            private final ArraySet<NotificationVisibility> mTmpNewlyVisibleNotifications = new ArraySet();
            private final ArraySet<NotificationVisibility> mTmpNoLongerVisibleNotifications = new ArraySet();
            
            @Override
            public void run() {
                NotificationLogger.this.mLastVisibilityReportUptimeMs = SystemClock.uptimeMillis();
                final List<NotificationEntry> visibleNotifications = NotificationLogger.this.mEntryManager.getVisibleNotifications();
                for (int size = visibleNotifications.size(), i = 0; i < size; ++i) {
                    final NotificationEntry notificationEntry = visibleNotifications.get(i);
                    final String key = notificationEntry.getSbn().getKey();
                    final boolean inVisibleLocation = NotificationLogger.this.mListContainer.isInVisibleLocation(notificationEntry);
                    final NotificationVisibility obtain = NotificationVisibility.obtain(key, i, size, inVisibleLocation, NotificationLogger.getNotificationLocation(notificationEntry));
                    final boolean contains = NotificationLogger.this.mCurrentlyVisibleNotifications.contains((Object)obtain);
                    if (inVisibleLocation) {
                        this.mTmpCurrentlyVisibleNotifications.add((Object)obtain);
                        if (!contains) {
                            this.mTmpNewlyVisibleNotifications.add((Object)obtain);
                        }
                    }
                    else {
                        obtain.recycle();
                    }
                }
                this.mTmpNoLongerVisibleNotifications.addAll(NotificationLogger.this.mCurrentlyVisibleNotifications);
                this.mTmpNoLongerVisibleNotifications.removeAll((ArraySet)this.mTmpCurrentlyVisibleNotifications);
                NotificationLogger.this.logNotificationVisibilityChanges((Collection)this.mTmpNewlyVisibleNotifications, (Collection)this.mTmpNoLongerVisibleNotifications);
                final NotificationLogger this$0 = NotificationLogger.this;
                this$0.recycleAllVisibilityObjects(this$0.mCurrentlyVisibleNotifications);
                NotificationLogger.this.mCurrentlyVisibleNotifications.addAll((ArraySet)this.mTmpCurrentlyVisibleNotifications);
                final ExpansionStateLogger access$600 = NotificationLogger.this.mExpansionStateLogger;
                final ArraySet<NotificationVisibility> mTmpCurrentlyVisibleNotifications = this.mTmpCurrentlyVisibleNotifications;
                access$600.onVisibilityChanged((Collection<NotificationVisibility>)mTmpCurrentlyVisibleNotifications, (Collection<NotificationVisibility>)mTmpCurrentlyVisibleNotifications);
                NotificationLogger.this.recycleAllVisibilityObjects(this.mTmpNoLongerVisibleNotifications);
                this.mTmpCurrentlyVisibleNotifications.clear();
                this.mTmpNewlyVisibleNotifications.clear();
                this.mTmpNoLongerVisibleNotifications.clear();
            }
        };
        this.mNotificationListener = mNotificationListener;
        this.mUiBgExecutor = mUiBgExecutor;
        this.mEntryManager = mEntryManager;
        this.mBarService = IStatusBarService$Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mExpansionStateLogger = mExpansionStateLogger;
        this.mNotificationPanelLogger = mNotificationPanelLogger;
        statusBarStateController.addCallback((StatusBarStateController.StateListener)this);
        mEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final NotificationVisibility notificationVisibility, final boolean b, final int n) {
                if (b && notificationVisibility != null) {
                    NotificationLogger.this.logNotificationClear(notificationEntry.getKey(), notificationEntry.getSbn(), notificationVisibility);
                }
                NotificationLogger.this.mExpansionStateLogger.onEntryRemoved(notificationEntry.getKey());
            }
            
            @Override
            public void onInflationError(final StatusBarNotification statusBarNotification, final Exception ex) {
                NotificationLogger.this.logNotificationError(statusBarNotification, ex);
            }
            
            @Override
            public void onPreEntryUpdated(final NotificationEntry notificationEntry) {
                NotificationLogger.this.mExpansionStateLogger.onEntryUpdated(notificationEntry.getKey());
            }
        });
    }
    
    private static NotificationVisibility[] cloneVisibilitiesAsArr(final Collection<NotificationVisibility> collection) {
        final NotificationVisibility[] array = new NotificationVisibility[collection.size()];
        final Iterator<NotificationVisibility> iterator = collection.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final NotificationVisibility notificationVisibility = iterator.next();
            if (notificationVisibility != null) {
                array[n] = notificationVisibility.clone();
            }
            ++n;
        }
        return array;
    }
    
    private static NotificationVisibility$NotificationLocation convertNotificationLocation(final int n) {
        if (n == 1) {
            return NotificationVisibility$NotificationLocation.LOCATION_FIRST_HEADS_UP;
        }
        if (n == 2) {
            return NotificationVisibility$NotificationLocation.LOCATION_HIDDEN_TOP;
        }
        if (n == 4) {
            return NotificationVisibility$NotificationLocation.LOCATION_MAIN_AREA;
        }
        if (n == 8) {
            return NotificationVisibility$NotificationLocation.LOCATION_BOTTOM_STACK_PEEKING;
        }
        if (n == 16) {
            return NotificationVisibility$NotificationLocation.LOCATION_BOTTOM_STACK_HIDDEN;
        }
        if (n != 64) {
            return NotificationVisibility$NotificationLocation.LOCATION_UNKNOWN;
        }
        return NotificationVisibility$NotificationLocation.LOCATION_GONE;
    }
    
    public static NotificationVisibility$NotificationLocation getNotificationLocation(final NotificationEntry notificationEntry) {
        if (notificationEntry != null && notificationEntry.getRow() != null && notificationEntry.getRow().getViewState() != null) {
            return convertNotificationLocation(notificationEntry.getRow().getViewState().location);
        }
        return NotificationVisibility$NotificationLocation.LOCATION_UNKNOWN;
    }
    
    private void logNotificationClear(final String s, final StatusBarNotification statusBarNotification, final NotificationVisibility notificationVisibility) {
        final String packageName = statusBarNotification.getPackageName();
        final String tag = statusBarNotification.getTag();
        final int id = statusBarNotification.getId();
        final int userId = statusBarNotification.getUserId();
        try {
            int n;
            if (this.mHeadsUpManager.isAlerting(s)) {
                n = 1;
            }
            else if (this.mListContainer.hasPulsingNotifications()) {
                n = 2;
            }
            else {
                n = 3;
            }
            this.mBarService.onNotificationClear(packageName, tag, id, userId, statusBarNotification.getKey(), n, 1, notificationVisibility);
        }
        catch (RemoteException ex) {}
    }
    
    private void logNotificationError(final StatusBarNotification statusBarNotification, final Exception ex) {
        try {
            this.mBarService.onNotificationError(statusBarNotification.getPackageName(), statusBarNotification.getTag(), statusBarNotification.getId(), statusBarNotification.getUid(), statusBarNotification.getInitialPid(), ex.getMessage(), statusBarNotification.getUserId());
        }
        catch (RemoteException ex2) {}
    }
    
    private void logNotificationVisibilityChanges(final Collection<NotificationVisibility> collection, final Collection<NotificationVisibility> collection2) {
        if (collection.isEmpty() && collection2.isEmpty()) {
            return;
        }
        this.mUiBgExecutor.execute(new _$$Lambda$NotificationLogger$e3uK_rBablkegG4HWqs1WzubMAs(this, cloneVisibilitiesAsArr(collection), cloneVisibilitiesAsArr(collection2)));
    }
    
    private void recycleAllVisibilityObjects(final ArraySet<NotificationVisibility> set) {
        for (int size = set.size(), i = 0; i < size; ++i) {
            ((NotificationVisibility)set.valueAt(i)).recycle();
        }
        set.clear();
    }
    
    private void recycleAllVisibilityObjects(final NotificationVisibility[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i] != null) {
                array[i].recycle();
            }
        }
    }
    
    private void setDozing(final boolean mDozing) {
        synchronized (this.mDozingLock) {
            this.mDozing = mDozing;
        }
    }
    
    @VisibleForTesting
    public Runnable getVisibilityReporter() {
        return this.mVisibilityReporter;
    }
    
    @Override
    public void onDozingChanged(final boolean dozing) {
        this.setDozing(dozing);
    }
    
    public void onExpansionChanged(final String s, final boolean b, final boolean b2) {
        this.mExpansionStateLogger.onExpansionChanged(s, b, b2, getNotificationLocation(this.mEntryManager.getActiveNotificationUnfiltered(s)));
    }
    
    @Override
    public void onStateChanged(final int n) {
    }
    
    public void setHeadsUpManager(final HeadsUpManager mHeadsUpManager) {
        this.mHeadsUpManager = mHeadsUpManager;
    }
    
    public void setUpWithContainer(final NotificationListContainer mListContainer) {
        this.mListContainer = mListContainer;
    }
    
    @VisibleForTesting
    public void setVisibilityReporter(final Runnable mVisibilityReporter) {
        this.mVisibilityReporter = mVisibilityReporter;
    }
    
    public void startNotificationLogging() {
        this.mListContainer.setChildLocationsChangedListener(this.mNotificationLocationsChangedListener);
        this.mNotificationLocationsChangedListener.onChildLocationsChanged();
        this.mNotificationPanelLogger.logPanelShown(this.mListContainer.hasPulsingNotifications(), this.mEntryManager.getVisibleNotifications());
    }
    
    public void stopNotificationLogging() {
        if (!this.mCurrentlyVisibleNotifications.isEmpty()) {
            this.logNotificationVisibilityChanges((Collection<NotificationVisibility>)Collections.emptyList(), (Collection<NotificationVisibility>)this.mCurrentlyVisibleNotifications);
            this.recycleAllVisibilityObjects(this.mCurrentlyVisibleNotifications);
        }
        this.mHandler.removeCallbacks(this.mVisibilityReporter);
        this.mListContainer.setChildLocationsChangedListener(null);
    }
    
    public static class ExpansionStateLogger
    {
        @VisibleForTesting
        IStatusBarService mBarService;
        private final Map<String, State> mExpansionStates;
        private final Map<String, Boolean> mLoggedExpansionState;
        private final Executor mUiBgExecutor;
        
        public ExpansionStateLogger(final Executor mUiBgExecutor) {
            this.mExpansionStates = (Map<String, State>)new ArrayMap();
            this.mLoggedExpansionState = (Map<String, Boolean>)new ArrayMap();
            this.mUiBgExecutor = mUiBgExecutor;
            this.mBarService = IStatusBarService$Stub.asInterface(ServiceManager.getService("statusbar"));
        }
        
        private State getState(final String s) {
            State state;
            if ((state = this.mExpansionStates.get(s)) == null) {
                state = new State();
                this.mExpansionStates.put(s, state);
            }
            return state;
        }
        
        private void maybeNotifyOnNotificationExpansionChanged(final String s, State state) {
            if (!state.isFullySet()) {
                return;
            }
            if (!state.mIsVisible) {
                return;
            }
            final Boolean b = this.mLoggedExpansionState.get(s);
            if (b == null && !state.mIsExpanded) {
                return;
            }
            if (b != null && state.mIsExpanded == b) {
                return;
            }
            this.mLoggedExpansionState.put(s, state.mIsExpanded);
            state = new State(state);
            this.mUiBgExecutor.execute(new _$$Lambda$NotificationLogger$ExpansionStateLogger$2Eiyi73G6QB8CNmBwaixENnG5Co(this, s, state));
        }
        
        @VisibleForTesting
        void onEntryRemoved(final String s) {
            this.mExpansionStates.remove(s);
            this.mLoggedExpansionState.remove(s);
        }
        
        @VisibleForTesting
        void onEntryUpdated(final String s) {
            this.mLoggedExpansionState.remove(s);
        }
        
        @VisibleForTesting
        void onExpansionChanged(final String s, final boolean b, final boolean b2, final NotificationVisibility$NotificationLocation mLocation) {
            final State state = this.getState(s);
            state.mIsUserAction = b;
            state.mIsExpanded = b2;
            state.mLocation = mLocation;
            this.maybeNotifyOnNotificationExpansionChanged(s, state);
        }
        
        @VisibleForTesting
        void onVisibilityChanged(final Collection<NotificationVisibility> collection, final Collection<NotificationVisibility> collection2) {
            final NotificationVisibility[] access$900 = cloneVisibilitiesAsArr(collection);
            final NotificationVisibility[] access$901 = cloneVisibilitiesAsArr(collection2);
            final int length = access$900.length;
            final int n = 0;
            for (final NotificationVisibility notificationVisibility : access$900) {
                final State state = this.getState(notificationVisibility.key);
                state.mIsVisible = Boolean.TRUE;
                state.mLocation = notificationVisibility.location;
                this.maybeNotifyOnNotificationExpansionChanged(notificationVisibility.key, state);
            }
            for (int length2 = access$901.length, j = n; j < length2; ++j) {
                this.getState(access$901[j].key).mIsVisible = Boolean.FALSE;
            }
        }
        
        private static class State
        {
            Boolean mIsExpanded;
            Boolean mIsUserAction;
            Boolean mIsVisible;
            NotificationVisibility$NotificationLocation mLocation;
            
            private State() {
            }
            
            private State(final State state) {
                this.mIsUserAction = state.mIsUserAction;
                this.mIsExpanded = state.mIsExpanded;
                this.mIsVisible = state.mIsVisible;
                this.mLocation = state.mLocation;
            }
            
            private boolean isFullySet() {
                return this.mIsUserAction != null && this.mIsExpanded != null && this.mIsVisible != null && this.mLocation != null;
            }
        }
    }
    
    public interface OnChildLocationsChangedListener
    {
        void onChildLocationsChanged();
    }
}
