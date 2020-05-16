// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.logging;

import com.android.systemui.statusbar.notification.logging.nano.Notifications$NotificationList;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.google.protobuf.nano.MessageNano;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.List;

public class NotificationPanelLoggerImpl implements NotificationPanelLogger
{
    @Override
    public void logPanelShown(final boolean b, final List<NotificationEntry> list) {
        final Notifications$NotificationList notificationProto = NotificationPanelLogger.toNotificationProto(list);
        SysUiStatsLog.write(245, NotificationPanelEvent.fromLockscreen(b).getId(), notificationProto.notifications.length, MessageNano.toByteArray(notificationProto));
    }
}
