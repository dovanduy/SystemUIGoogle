// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.util.ArrayMap;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import android.widget.RemoteViews;
import android.util.SparseArray;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Map;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;

public class NotifRemoteViewCacheImpl implements NotifRemoteViewCache
{
    private final NotifCollectionListener mCollectionListener;
    private final Map<NotificationEntry, SparseArray<RemoteViews>> mNotifCachedContentViews;
    
    NotifRemoteViewCacheImpl(final CommonNotifCollection collection) {
        this.mNotifCachedContentViews = (Map<NotificationEntry, SparseArray<RemoteViews>>)new ArrayMap();
        collection.addCollectionListener(this.mCollectionListener = new NotifCollectionListener() {
            @Override
            public void onEntryCleanUp(final NotificationEntry notificationEntry) {
                NotifRemoteViewCacheImpl.this.mNotifCachedContentViews.remove(notificationEntry);
            }
            
            @Override
            public void onEntryInit(final NotificationEntry notificationEntry) {
                NotifRemoteViewCacheImpl.this.mNotifCachedContentViews.put(notificationEntry, new SparseArray());
            }
        });
    }
    
    @Override
    public void clearCache(final NotificationEntry notificationEntry) {
        final SparseArray<RemoteViews> sparseArray = this.mNotifCachedContentViews.get(notificationEntry);
        if (sparseArray == null) {
            return;
        }
        sparseArray.clear();
    }
    
    @Override
    public RemoteViews getCachedView(final NotificationEntry notificationEntry, final int n) {
        final SparseArray<RemoteViews> sparseArray = this.mNotifCachedContentViews.get(notificationEntry);
        if (sparseArray == null) {
            return null;
        }
        return (RemoteViews)sparseArray.get(n);
    }
    
    @Override
    public boolean hasCachedView(final NotificationEntry notificationEntry, final int n) {
        return this.getCachedView(notificationEntry, n) != null;
    }
    
    @Override
    public void putCachedView(final NotificationEntry notificationEntry, final int n, final RemoteViews remoteViews) {
        final SparseArray<RemoteViews> sparseArray = this.mNotifCachedContentViews.get(notificationEntry);
        if (sparseArray == null) {
            return;
        }
        sparseArray.put(n, (Object)remoteViews);
    }
    
    @Override
    public void removeCachedView(final NotificationEntry notificationEntry, final int n) {
        final SparseArray<RemoteViews> sparseArray = this.mNotifCachedContentViews.get(notificationEntry);
        if (sparseArray == null) {
            return;
        }
        sparseArray.remove(n);
    }
}
