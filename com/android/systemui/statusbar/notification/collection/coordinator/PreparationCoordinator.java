// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.GroupEntry;
import java.util.List;
import java.util.Objects;
import android.service.notification.StatusBarNotification;
import android.os.RemoteException;
import com.android.systemui.statusbar.notification.stack.NotificationListItem;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import android.util.ArrayMap;
import com.android.systemui.statusbar.notification.collection.NotifInflaterImpl;
import com.android.systemui.statusbar.notification.collection.NotifViewBarn;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.notification.collection.listbuilder.OnBeforeFinalizeFilterListener;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Map;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager;
import com.android.systemui.statusbar.notification.collection.inflation.NotifInflater;

public class PreparationCoordinator implements Coordinator
{
    private final NotifInflater.InflationCallback mInflationCallback;
    private final NotifInflationErrorManager.NotifInflationErrorListener mInflationErrorListener;
    private final Map<NotificationEntry, Integer> mInflationStates;
    private final PreparationCoordinatorLogger mLogger;
    private final NotifCollectionListener mNotifCollectionListener;
    private final NotifInflationErrorManager mNotifErrorManager;
    private final NotifInflater mNotifInflater;
    private final NotifFilter mNotifInflatingFilter;
    private final NotifFilter mNotifInflationErrorFilter;
    private final OnBeforeFinalizeFilterListener mOnBeforeFinalizeFilterListener;
    private final IStatusBarService mStatusBarService;
    private final NotifViewBarn mViewBarn;
    
    public PreparationCoordinator(final PreparationCoordinatorLogger mLogger, final NotifInflaterImpl mNotifInflater, final NotifInflationErrorManager mNotifErrorManager, final NotifViewBarn mViewBarn, final IStatusBarService mStatusBarService) {
        this.mInflationStates = (Map<NotificationEntry, Integer>)new ArrayMap();
        this.mNotifCollectionListener = new NotifCollectionListener() {
            @Override
            public void onEntryCleanUp(final NotificationEntry notificationEntry) {
                PreparationCoordinator.this.mInflationStates.remove(notificationEntry);
                PreparationCoordinator.this.mViewBarn.removeViewForEntry(notificationEntry);
            }
            
            @Override
            public void onEntryInit(final NotificationEntry notificationEntry) {
                PreparationCoordinator.this.mInflationStates.put(notificationEntry, 0);
            }
            
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final int i) {
                final PreparationCoordinator this$0 = PreparationCoordinator.this;
                final StringBuilder sb = new StringBuilder();
                sb.append("entryRemoved reason=");
                sb.append(i);
                this$0.abortInflation(notificationEntry, sb.toString());
            }
            
            @Override
            public void onEntryUpdated(final NotificationEntry notificationEntry) {
                final int access$100 = PreparationCoordinator.this.getInflationState(notificationEntry);
                if (access$100 == 1) {
                    PreparationCoordinator.this.mInflationStates.put(notificationEntry, 2);
                }
                else if (access$100 == -1) {
                    PreparationCoordinator.this.mInflationStates.put(notificationEntry, 0);
                }
            }
        };
        this.mOnBeforeFinalizeFilterListener = new _$$Lambda$PreparationCoordinator$O_kUtuWqdigLEcSds1YW_FvJtNg(this);
        this.mNotifInflationErrorFilter = new NotifFilter("PreparationCoordinatorInflationError") {
            @Override
            public boolean shouldFilterOut(final NotificationEntry notificationEntry, final long n) {
                return PreparationCoordinator.this.getInflationState(notificationEntry) == -1;
            }
        };
        this.mNotifInflatingFilter = new NotifFilter("PreparationCoordinatorInflating") {
            @Override
            public boolean shouldFilterOut(final NotificationEntry notificationEntry, final long n) {
                final int access$100 = PreparationCoordinator.this.getInflationState(notificationEntry);
                boolean b = true;
                if (access$100 == 1 || access$100 == 2) {
                    b = false;
                }
                return b;
            }
        };
        this.mInflationCallback = new NotifInflater.InflationCallback() {
            @Override
            public void onInflationFinished(final NotificationEntry notificationEntry) {
                PreparationCoordinator.this.mLogger.logNotifInflated(notificationEntry.getKey());
                PreparationCoordinator.this.mViewBarn.registerViewForEntry(notificationEntry, notificationEntry.getRow());
                PreparationCoordinator.this.mInflationStates.put(notificationEntry, 1);
                PreparationCoordinator.this.mNotifInflatingFilter.invalidateList();
            }
        };
        this.mInflationErrorListener = new NotifInflationErrorManager.NotifInflationErrorListener() {
            @Override
            public void onNotifInflationError(final NotificationEntry notificationEntry, final Exception ex) {
                PreparationCoordinator.this.mViewBarn.removeViewForEntry(notificationEntry);
                PreparationCoordinator.this.mInflationStates.put(notificationEntry, -1);
                while (true) {
                    try {
                        final StatusBarNotification sbn = notificationEntry.getSbn();
                        PreparationCoordinator.this.mStatusBarService.onNotificationError(sbn.getPackageName(), sbn.getTag(), sbn.getId(), sbn.getUid(), sbn.getInitialPid(), ex.getMessage(), sbn.getUserId());
                        PreparationCoordinator.this.mNotifInflationErrorFilter.invalidateList();
                    }
                    catch (RemoteException ex2) {
                        continue;
                    }
                    break;
                }
            }
            
            @Override
            public void onNotifInflationErrorCleared(final NotificationEntry notificationEntry) {
                PreparationCoordinator.this.mNotifInflationErrorFilter.invalidateList();
            }
        };
        this.mLogger = mLogger;
        (this.mNotifInflater = mNotifInflater).setInflationCallback(this.mInflationCallback);
        (this.mNotifErrorManager = mNotifErrorManager).addInflationErrorListener(this.mInflationErrorListener);
        this.mViewBarn = mViewBarn;
        this.mStatusBarService = mStatusBarService;
    }
    
    private void abortInflation(final NotificationEntry notificationEntry, final String s) {
        this.mLogger.logInflationAborted(notificationEntry.getKey(), s);
        notificationEntry.abortTask();
    }
    
    private int getInflationState(final NotificationEntry notificationEntry) {
        final Integer obj = this.mInflationStates.get(notificationEntry);
        Objects.requireNonNull(obj, "Asking state of a notification preparation coordinator doesn't know about");
        return obj;
    }
    
    private void inflateAllRequiredViews(final List<ListEntry> list) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            final ListEntry listEntry = list.get(i);
            if (listEntry instanceof GroupEntry) {
                final GroupEntry groupEntry = (GroupEntry)listEntry;
                this.inflateNotifRequiredViews(groupEntry.getSummary());
                final List<NotificationEntry> children = groupEntry.getChildren();
                for (int size2 = children.size(), j = 0; j < size2; ++j) {
                    this.inflateNotifRequiredViews(children.get(j));
                }
            }
            else {
                this.inflateNotifRequiredViews((NotificationEntry)listEntry);
            }
        }
    }
    
    private void inflateEntry(final NotificationEntry notificationEntry, final String s) {
        this.abortInflation(notificationEntry, s);
        this.mNotifInflater.inflateViews(notificationEntry);
    }
    
    private void inflateNotifRequiredViews(final NotificationEntry notificationEntry) {
        final int intValue = this.mInflationStates.get(notificationEntry);
        if (intValue != 0) {
            if (intValue == 2) {
                this.rebind(notificationEntry, "entryUpdated");
            }
        }
        else {
            this.inflateEntry(notificationEntry, "entryAdded");
        }
    }
    
    private void rebind(final NotificationEntry notificationEntry, final String s) {
        this.mNotifInflater.rebindViews(notificationEntry);
    }
    
    @Override
    public void attach(final NotifPipeline notifPipeline) {
        notifPipeline.addCollectionListener(this.mNotifCollectionListener);
        notifPipeline.addOnBeforeFinalizeFilterListener(this.mOnBeforeFinalizeFilterListener);
        notifPipeline.addFinalizeFilter(this.mNotifInflationErrorFilter);
        notifPipeline.addFinalizeFilter(this.mNotifInflatingFilter);
    }
}
