// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.content.pm.ShortcutInfo;
import android.app.NotificationChannel;
import android.service.notification.NotificationListenerService$Ranking;
import android.app.Notification$MessagingStyle;
import android.app.Notification$Builder;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;
import android.content.pm.LauncherApps;

public final class ConversationNotificationProcessor
{
    private final ConversationNotificationManager conversationNotificationManager;
    private final LauncherApps launcherApps;
    
    public ConversationNotificationProcessor(final LauncherApps launcherApps, final ConversationNotificationManager conversationNotificationManager) {
        Intrinsics.checkParameterIsNotNull(launcherApps, "launcherApps");
        Intrinsics.checkParameterIsNotNull(conversationNotificationManager, "conversationNotificationManager");
        this.launcherApps = launcherApps;
        this.conversationNotificationManager = conversationNotificationManager;
    }
    
    public final void processNotification(final NotificationEntry notificationEntry, final Notification$Builder notification$Builder) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        Intrinsics.checkParameterIsNotNull(notification$Builder, "recoveredBuilder");
        Object style;
        if (!((style = notification$Builder.getStyle()) instanceof Notification$MessagingStyle)) {
            style = null;
        }
        final Notification$MessagingStyle notification$MessagingStyle = (Notification$MessagingStyle)style;
        if (notification$MessagingStyle != null) {
            final NotificationListenerService$Ranking ranking = notificationEntry.getRanking();
            Intrinsics.checkExpressionValueIsNotNull(ranking, "entry.ranking");
            final NotificationChannel channel = ranking.getChannel();
            Intrinsics.checkExpressionValueIsNotNull(channel, "entry.ranking.channel");
            int conversationType;
            if (channel.isImportantConversation()) {
                conversationType = 2;
            }
            else {
                conversationType = 1;
            }
            notification$MessagingStyle.setConversationType(conversationType);
            final NotificationListenerService$Ranking ranking2 = notificationEntry.getRanking();
            Intrinsics.checkExpressionValueIsNotNull(ranking2, "entry.ranking");
            final ShortcutInfo shortcutInfo = ranking2.getShortcutInfo();
            if (shortcutInfo != null) {
                notification$MessagingStyle.setShortcutIcon(this.launcherApps.getShortcutIcon(shortcutInfo));
                final CharSequence shortLabel = shortcutInfo.getShortLabel();
                if (shortLabel != null) {
                    notification$MessagingStyle.setConversationTitle(shortLabel);
                }
            }
            notification$MessagingStyle.setUnreadMessageCount(this.conversationNotificationManager.getUnreadCount(notificationEntry, notification$Builder));
        }
    }
}
