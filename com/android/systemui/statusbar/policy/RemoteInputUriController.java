// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.util.Log;
import android.net.Uri;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import android.os.RemoteException;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.notification.NotificationEntryListener;

public class RemoteInputUriController
{
    private final NotificationEntryListener mInlineUriListener;
    private final IStatusBarService mStatusBarManagerService;
    
    public RemoteInputUriController(final IStatusBarService mStatusBarManagerService) {
        this.mInlineUriListener = new NotificationEntryListener() {
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final NotificationVisibility notificationVisibility, final boolean b, final int n) {
                try {
                    RemoteInputUriController.this.mStatusBarManagerService.clearInlineReplyUriPermissions(notificationEntry.getKey());
                }
                catch (RemoteException ex) {
                    ex.rethrowFromSystemServer();
                }
            }
        };
        this.mStatusBarManagerService = mStatusBarManagerService;
    }
    
    public void attach(final NotificationEntryManager notificationEntryManager) {
        notificationEntryManager.addNotificationEntryListener(this.mInlineUriListener);
    }
    
    public void grantInlineReplyUriPermission(final StatusBarNotification statusBarNotification, final Uri uri) {
        try {
            this.mStatusBarManagerService.grantInlineReplyUriPermission(statusBarNotification.getKey(), uri, statusBarNotification.getUser(), statusBarNotification.getPackageName());
        }
        catch (Exception ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Failed to grant URI permissions:");
            sb.append(ex.getMessage());
            Log.e("RemoteInputUriController", sb.toString(), (Throwable)ex);
        }
    }
}
