// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.util.ArraySet;
import android.os.Message;
import java.util.Collection;
import java.util.Set;
import androidx.core.os.CancellationSignal;
import java.util.ArrayList;
import android.util.ArrayMap;
import android.os.Looper;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import java.util.List;
import android.os.Handler;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Map;

public final class NotifBindPipeline
{
    private final Map<NotificationEntry, BindEntry> mBindEntries;
    private final NotifCollectionListener mCollectionListener;
    private final NotifBindPipelineLogger mLogger;
    private final Handler mMainHandler;
    private final List<BindCallback> mScratchCallbacksList;
    private BindStage mStage;
    
    NotifBindPipeline(final CommonNotifCollection collection, final NotifBindPipelineLogger mLogger, final Looper looper) {
        this.mBindEntries = (Map<NotificationEntry, BindEntry>)new ArrayMap();
        this.mScratchCallbacksList = new ArrayList<BindCallback>();
        collection.addCollectionListener(this.mCollectionListener = new NotifCollectionListener() {
            @Override
            public void onEntryCleanUp(final NotificationEntry notificationEntry) {
                final ExpandableNotificationRow row = NotifBindPipeline.this.mBindEntries.remove(notificationEntry).row;
                if (row != null) {
                    NotifBindPipeline.this.mStage.abortStage(notificationEntry, row);
                }
                NotifBindPipeline.this.mStage.deleteStageParams(notificationEntry);
                NotifBindPipeline.this.mMainHandler.removeMessages(1, (Object)notificationEntry);
            }
            
            @Override
            public void onEntryInit(final NotificationEntry notificationEntry) {
                NotifBindPipeline.this.mBindEntries.put(notificationEntry, new BindEntry());
                NotifBindPipeline.this.mStage.createStageParams(notificationEntry);
            }
        });
        this.mLogger = mLogger;
        this.mMainHandler = new NotifBindPipelineHandler(looper);
    }
    
    private BindEntry getBindEntry(final NotificationEntry notificationEntry) {
        final BindEntry bindEntry = this.mBindEntries.get(notificationEntry);
        if (bindEntry != null) {
            return bindEntry;
        }
        throw new IllegalStateException(String.format("Attempting bind on an inactive notification. key: %s", notificationEntry.getKey()));
    }
    
    private void onBindRequested(final NotificationEntry notificationEntry, final CancellationSignal cancellationSignal, final BindCallback bindCallback) {
        final BindEntry bindEntry = this.getBindEntry(notificationEntry);
        if (bindEntry == null) {
            return;
        }
        bindEntry.invalidated = true;
        if (bindCallback != null) {
            final Set<BindCallback> callbacks = bindEntry.callbacks;
            callbacks.add(bindCallback);
            cancellationSignal.setOnCancelListener((CancellationSignal.OnCancelListener)new _$$Lambda$NotifBindPipeline$AMh7TPDBgF4c0_yw5t_ceqMMco8(callbacks, bindCallback));
        }
        this.requestPipelineRun(notificationEntry);
    }
    
    private void onPipelineComplete(final NotificationEntry notificationEntry) {
        final BindEntry bindEntry = this.getBindEntry(notificationEntry);
        final Set<BindCallback> callbacks = bindEntry.callbacks;
        this.mLogger.logFinishedPipeline(notificationEntry.getKey(), callbacks.size());
        int i = 0;
        bindEntry.invalidated = false;
        this.mScratchCallbacksList.addAll((Collection<? extends BindCallback>)callbacks);
        callbacks.clear();
        while (i < this.mScratchCallbacksList.size()) {
            this.mScratchCallbacksList.get(i).onBindFinished(notificationEntry);
            ++i;
        }
        this.mScratchCallbacksList.clear();
    }
    
    private void requestPipelineRun(final NotificationEntry notificationEntry) {
        final BindEntry bindEntry = this.getBindEntry(notificationEntry);
        if (bindEntry.row == null) {
            return;
        }
        this.mLogger.logRequestPipelineRun(notificationEntry.getKey());
        this.mStage.abortStage(notificationEntry, bindEntry.row);
        if (!this.mMainHandler.hasMessages(1, (Object)notificationEntry)) {
            this.mMainHandler.sendMessage(Message.obtain(this.mMainHandler, 1, (Object)notificationEntry));
        }
    }
    
    private void startPipeline(final NotificationEntry notificationEntry) {
        this.mLogger.logStartPipeline(notificationEntry.getKey());
        if (this.mStage != null) {
            this.mStage.executeStage(notificationEntry, this.mBindEntries.get(notificationEntry).row, (BindStage.StageCallback)new _$$Lambda$NotifBindPipeline$3PgyvwQAf2ulPzCouWU3DaPqlCo(this));
            return;
        }
        throw new IllegalStateException("No stage was ever set on the pipeline");
    }
    
    public void manageRow(final NotificationEntry notificationEntry, final ExpandableNotificationRow row) {
        this.mLogger.logManagedRow(notificationEntry.getKey());
        final BindEntry bindEntry = this.getBindEntry(notificationEntry);
        bindEntry.row = row;
        if (bindEntry.invalidated) {
            this.requestPipelineRun(notificationEntry);
        }
    }
    
    public void setStage(final BindStage mStage) {
        this.mLogger.logStageSet(mStage.getClass().getName());
        (this.mStage = mStage).setBindRequestListener((BindRequester.BindRequestListener)new _$$Lambda$NotifBindPipeline$Ub0DE4cDhgUUqolBUMUBBCvUjYs(this));
    }
    
    public interface BindCallback
    {
        void onBindFinished(final NotificationEntry p0);
    }
    
    private class BindEntry
    {
        public final Set<BindCallback> callbacks;
        public boolean invalidated;
        public ExpandableNotificationRow row;
        
        private BindEntry(final NotifBindPipeline notifBindPipeline) {
            this.callbacks = (Set<BindCallback>)new ArraySet();
        }
    }
    
    private class NotifBindPipelineHandler extends Handler
    {
        NotifBindPipelineHandler(final Looper looper) {
            super(looper);
        }
        
        public void handleMessage(final Message message) {
            if (message.what == 1) {
                NotifBindPipeline.this.startPipeline((NotificationEntry)message.obj);
                return;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("Unknown message type: ");
            sb.append(message.what);
            throw new IllegalArgumentException(sb.toString());
        }
    }
}
