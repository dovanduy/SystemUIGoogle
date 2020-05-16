// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import java.util.ArrayList;
import androidx.collection.ArraySet;
import java.util.List;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Set;

public class NotifInflationErrorManager
{
    Set<NotificationEntry> mErroredNotifs;
    List<NotifInflationErrorListener> mListeners;
    
    public NotifInflationErrorManager() {
        this.mErroredNotifs = new ArraySet<NotificationEntry>();
        this.mListeners = new ArrayList<NotifInflationErrorListener>();
    }
    
    public void addInflationErrorListener(final NotifInflationErrorListener notifInflationErrorListener) {
        this.mListeners.add(notifInflationErrorListener);
    }
    
    public void clearInflationError(final NotificationEntry notificationEntry) {
        if (this.mErroredNotifs.contains(notificationEntry)) {
            this.mErroredNotifs.remove(notificationEntry);
            for (int i = 0; i < this.mListeners.size(); ++i) {
                this.mListeners.get(i).onNotifInflationErrorCleared(notificationEntry);
            }
        }
    }
    
    public void setInflationError(final NotificationEntry notificationEntry, final Exception ex) {
        this.mErroredNotifs.add(notificationEntry);
        for (int i = 0; i < this.mListeners.size(); ++i) {
            this.mListeners.get(i).onNotifInflationError(notificationEntry, ex);
        }
    }
    
    public interface NotifInflationErrorListener
    {
        void onNotifInflationError(final NotificationEntry p0, final Exception p1);
        
        default void onNotifInflationErrorCleared(final NotificationEntry notificationEntry) {
        }
    }
}
