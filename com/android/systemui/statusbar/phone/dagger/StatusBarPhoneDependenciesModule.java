// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone.dagger;

import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;

public interface StatusBarPhoneDependenciesModule
{
    default NotificationGroupAlertTransferHelper provideNotificationGroupAlertTransferHelper(final RowContentBindStage rowContentBindStage) {
        return new NotificationGroupAlertTransferHelper(rowContentBindStage);
    }
}
