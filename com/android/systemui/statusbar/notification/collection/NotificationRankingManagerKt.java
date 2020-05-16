// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import android.service.notification.StatusBarNotification;
import kotlin.jvm.internal.Intrinsics;

public final class NotificationRankingManagerKt
{
    private static final boolean isSystemMax(final NotificationEntry notificationEntry) {
        if (notificationEntry.getImportance() >= 4) {
            final StatusBarNotification sbn = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn, "sbn");
            if (isSystemNotification(sbn)) {
                return true;
            }
        }
        return false;
    }
    
    private static final boolean isSystemNotification(final StatusBarNotification statusBarNotification) {
        return Intrinsics.areEqual("android", statusBarNotification.getPackageName()) || Intrinsics.areEqual("com.android.systemui", statusBarNotification.getPackageName());
    }
}
