// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.os.RemoteException;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import android.app.Notification$Action;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.util.ArraySet;
import java.util.Set;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.internal.statusbar.IStatusBarService;

public class SmartReplyController
{
    private final IStatusBarService mBarService;
    private Callback mCallback;
    private final NotificationEntryManager mEntryManager;
    private Set<String> mSendingKeys;
    
    public SmartReplyController(final NotificationEntryManager mEntryManager, final IStatusBarService mBarService) {
        this.mSendingKeys = (Set<String>)new ArraySet();
        this.mBarService = mBarService;
        this.mEntryManager = mEntryManager;
    }
    
    public boolean isSendingSmartReply(final String s) {
        return this.mSendingKeys.contains(s);
    }
    
    public void setCallback(final Callback mCallback) {
        this.mCallback = mCallback;
    }
    
    public void smartActionClicked(final NotificationEntry notificationEntry, final int n, final Notification$Action notification$Action, final boolean b) {
        final NotificationVisibility obtain = NotificationVisibility.obtain(notificationEntry.getKey(), notificationEntry.getRanking().getRank(), this.mEntryManager.getActiveNotificationsCount(), true, NotificationLogger.getNotificationLocation(notificationEntry));
        try {
            this.mBarService.onNotificationActionClick(notificationEntry.getKey(), n, notification$Action, obtain, b);
        }
        catch (RemoteException ex) {}
    }
    
    public void smartReplySent(final NotificationEntry notificationEntry, final int n, final CharSequence charSequence, final int n2, final boolean b) {
        this.mCallback.onSmartReplySent(notificationEntry, charSequence);
        this.mSendingKeys.add(notificationEntry.getKey());
        try {
            this.mBarService.onNotificationSmartReplySent(notificationEntry.getSbn().getKey(), n, charSequence, n2, b);
        }
        catch (RemoteException ex) {}
    }
    
    public void smartSuggestionsAdded(final NotificationEntry notificationEntry, final int n, final int n2, final boolean b, final boolean b2) {
        try {
            this.mBarService.onNotificationSmartSuggestionsAdded(notificationEntry.getSbn().getKey(), n, n2, b, b2);
        }
        catch (RemoteException ex) {}
    }
    
    public void stopSending(final NotificationEntry notificationEntry) {
        if (notificationEntry != null) {
            this.mSendingKeys.remove(notificationEntry.getSbn().getKey());
        }
    }
    
    public interface Callback
    {
        void onSmartReplySent(final NotificationEntry p0, final CharSequence p1);
    }
}
