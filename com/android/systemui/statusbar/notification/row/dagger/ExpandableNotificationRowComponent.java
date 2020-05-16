// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row.dagger;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import com.android.systemui.statusbar.phone.StatusBar;
import android.service.notification.StatusBarNotification;
import android.content.Context;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRowController;

public interface ExpandableNotificationRowComponent
{
    ExpandableNotificationRowController getExpandableNotificationRowController();
    
    public interface Builder
    {
        ExpandableNotificationRowComponent build();
        
        Builder expandableNotificationRow(final ExpandableNotificationRow p0);
        
        Builder inflationCallback(final NotificationRowContentBinder.InflationCallback p0);
        
        Builder notificationEntry(final NotificationEntry p0);
        
        Builder onDismissRunnable(final Runnable p0);
        
        Builder onExpandClickListener(final ExpandableNotificationRow.OnExpandClickListener p0);
        
        Builder rowContentBindStage(final RowContentBindStage p0);
    }
    
    public abstract static class ExpandableNotificationRowModule
    {
        static String provideAppName(Context packageName, final StatusBarNotification statusBarNotification) {
            final PackageManager packageManagerForUser = StatusBar.getPackageManagerForUser(packageName, statusBarNotification.getUser().getIdentifier());
            packageName = (Context)statusBarNotification.getPackageName();
            try {
                final ApplicationInfo applicationInfo = packageManagerForUser.getApplicationInfo((String)packageName, 8704);
                if (applicationInfo != null) {
                    return String.valueOf(packageManagerForUser.getApplicationLabel(applicationInfo));
                }
                return (String)packageName;
            }
            catch (PackageManager$NameNotFoundException ex) {
                return (String)packageName;
            }
        }
        
        static String provideNotificationKey(final StatusBarNotification statusBarNotification) {
            return statusBarNotification.getKey();
        }
        
        static StatusBarNotification provideStatusBarNotification(final NotificationEntry notificationEntry) {
            return notificationEntry.getSbn();
        }
    }
}
