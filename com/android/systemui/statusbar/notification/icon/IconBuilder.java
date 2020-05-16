// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.icon;

import android.app.Notification;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;

public final class IconBuilder
{
    private final Context context;
    
    public IconBuilder(final Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.context = context;
    }
    
    public final StatusBarIconView createIconView(final NotificationEntry notificationEntry) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        final Context context = this.context;
        final StringBuilder sb = new StringBuilder();
        final StatusBarNotification sbn = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn, "entry.sbn");
        sb.append(sbn.getPackageName());
        sb.append("/0x");
        final StatusBarNotification sbn2 = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn2, "entry.sbn");
        sb.append(Integer.toHexString(sbn2.getId()));
        return new StatusBarIconView(context, sb.toString(), notificationEntry.getSbn());
    }
    
    public final CharSequence getIconContentDescription(final Notification notification) {
        Intrinsics.checkParameterIsNotNull(notification, "n");
        final String contentDescForNotification = StatusBarIconView.contentDescForNotification(this.context, notification);
        Intrinsics.checkExpressionValueIsNotNull(contentDescForNotification, "StatusBarIconView.conten\u2026rNotification(context, n)");
        return contentDescForNotification;
    }
}
