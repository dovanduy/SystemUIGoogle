// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.icon;

import com.android.systemui.R$id;
import android.widget.ImageView$ScaleType;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import android.util.Log;
import com.android.systemui.statusbar.StatusBarIconView;
import kotlin.Pair;
import android.os.UserHandle;
import com.android.internal.statusbar.StatusBarIcon;
import android.app.Notification;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.app.NotificationChannel;
import android.service.notification.NotificationListenerService$Ranking;
import com.android.systemui.statusbar.notification.InflationException;
import android.app.Person;
import android.app.Notification$MessagingStyle$Message;
import android.content.pm.ShortcutInfo;
import java.util.List;
import kotlin.collections.CollectionsKt;
import android.content.pm.LauncherApps$ShortcutQuery;
import android.graphics.drawable.Icon;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import android.content.pm.LauncherApps;

public final class IconManager
{
    private final IconManager$entryListener.IconManager$entryListener$1 entryListener;
    private final IconBuilder iconBuilder;
    private final LauncherApps launcherApps;
    private final CommonNotifCollection notifCollection;
    private final NotificationEntry.OnSensitivityChangedListener sensitivityListener;
    
    public IconManager(final CommonNotifCollection notifCollection, final LauncherApps launcherApps, final IconBuilder iconBuilder) {
        Intrinsics.checkParameterIsNotNull(notifCollection, "notifCollection");
        Intrinsics.checkParameterIsNotNull(launcherApps, "launcherApps");
        Intrinsics.checkParameterIsNotNull(iconBuilder, "iconBuilder");
        this.notifCollection = notifCollection;
        this.launcherApps = launcherApps;
        this.iconBuilder = iconBuilder;
        this.entryListener = new IconManager$entryListener.IconManager$entryListener$1(this);
        this.sensitivityListener = (NotificationEntry.OnSensitivityChangedListener)new IconManager$sensitivityListener.IconManager$sensitivityListener$1(this);
    }
    
    private final Icon createPeopleAvatar(final NotificationEntry notificationEntry) throws InflationException {
        final NotificationListenerService$Ranking ranking = notificationEntry.getRanking();
        Intrinsics.checkExpressionValueIsNotNull(ranking, "entry.ranking");
        final NotificationChannel channel = ranking.getChannel();
        Intrinsics.checkExpressionValueIsNotNull(channel, "entry.ranking.channel");
        final String conversationId = channel.getConversationId();
        final LauncherApps$ShortcutQuery launcherApps$ShortcutQuery = new LauncherApps$ShortcutQuery();
        final StatusBarNotification sbn = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn, "entry.sbn");
        final LauncherApps$ShortcutQuery setShortcutIds = launcherApps$ShortcutQuery.setPackage(sbn.getPackageName()).setQueryFlags(3).setShortcutIds((List)CollectionsKt.listOf(conversationId));
        final LauncherApps launcherApps = this.launcherApps;
        final StatusBarNotification sbn2 = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn2, "entry.sbn");
        final List shortcuts = launcherApps.getShortcuts(setShortcutIds, sbn2.getUser());
        Icon icon;
        if (shortcuts != null && (shortcuts.isEmpty() ^ true)) {
            final ShortcutInfo value = shortcuts.get(0);
            Intrinsics.checkExpressionValueIsNotNull(value, "shortcuts[0]");
            icon = value.getIcon();
        }
        else {
            icon = null;
        }
        Icon largeIcon = icon;
        if (icon == null) {
            final StatusBarNotification sbn3 = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn3, "entry.sbn");
            largeIcon = sbn3.getNotification().getLargeIcon();
        }
        Icon icon2;
        if ((icon2 = largeIcon) == null) {
            final StatusBarNotification sbn4 = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn4, "entry.sbn");
            final Bundle extras = sbn4.getNotification().extras;
            Intrinsics.checkExpressionValueIsNotNull(extras, "entry.sbn.notification.extras");
            final List messagesFromBundleArray = Notification$MessagingStyle$Message.getMessagesFromBundleArray(extras.getParcelableArray("android.messages"));
            final Person person = (Person)extras.getParcelable("android.messagingUser");
            Intrinsics.checkExpressionValueIsNotNull(messagesFromBundleArray, "messages");
            int size = messagesFromBundleArray.size();
            while (true) {
                final int n = size - 1;
                icon2 = largeIcon;
                if (n < 0) {
                    break;
                }
                final Notification$MessagingStyle$Message notification$MessagingStyle$Message = messagesFromBundleArray.get(n);
                Intrinsics.checkExpressionValueIsNotNull(notification$MessagingStyle$Message, "message");
                final Person senderPerson = notification$MessagingStyle$Message.getSenderPerson();
                size = n;
                if (senderPerson == null) {
                    continue;
                }
                size = n;
                if (senderPerson == person) {
                    continue;
                }
                final Person senderPerson2 = notification$MessagingStyle$Message.getSenderPerson();
                if (senderPerson2 != null) {
                    icon2 = senderPerson2.getIcon();
                    break;
                }
                Intrinsics.throwNpe();
                throw null;
            }
        }
        Icon smallIcon;
        if ((smallIcon = icon2) == null) {
            final StatusBarNotification sbn5 = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn5, "entry.sbn");
            final Notification notification = sbn5.getNotification();
            Intrinsics.checkExpressionValueIsNotNull(notification, "entry.sbn.notification");
            smallIcon = notification.getSmallIcon();
        }
        if (smallIcon != null) {
            return smallIcon;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("No icon in notification from ");
        final StatusBarNotification sbn6 = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn6, "entry.sbn");
        sb.append(sbn6.getPackageName());
        throw new InflationException(sb.toString());
    }
    
    private final StatusBarIcon getIconDescriptor(final NotificationEntry notificationEntry, final boolean b) throws InflationException {
        final StatusBarNotification sbn = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn, "entry.sbn");
        final Notification notification = sbn.getNotification();
        final boolean b2 = this.isImportantConversation(notificationEntry) && !b;
        final IconPack icons = notificationEntry.getIcons();
        Intrinsics.checkExpressionValueIsNotNull(icons, "entry.icons");
        final StatusBarIcon peopleAvatarDescriptor = icons.getPeopleAvatarDescriptor();
        final IconPack icons2 = notificationEntry.getIcons();
        Intrinsics.checkExpressionValueIsNotNull(icons2, "entry.icons");
        final StatusBarIcon smallIconDescriptor = icons2.getSmallIconDescriptor();
        if (b2 && peopleAvatarDescriptor != null) {
            return peopleAvatarDescriptor;
        }
        if (!b2 && smallIconDescriptor != null) {
            return smallIconDescriptor;
        }
        Icon icon;
        if (b2) {
            icon = this.createPeopleAvatar(notificationEntry);
        }
        else {
            Intrinsics.checkExpressionValueIsNotNull(notification, "n");
            icon = notification.getSmallIcon();
        }
        if (icon != null) {
            final StatusBarNotification sbn2 = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn2, "entry.sbn");
            final UserHandle user = sbn2.getUser();
            final StatusBarNotification sbn3 = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn3, "entry.sbn");
            final String packageName = sbn3.getPackageName();
            final int iconLevel = notification.iconLevel;
            final int number = notification.number;
            final IconBuilder iconBuilder = this.iconBuilder;
            Intrinsics.checkExpressionValueIsNotNull(notification, "n");
            final StatusBarIcon statusBarIcon = new StatusBarIcon(user, packageName, icon, iconLevel, number, iconBuilder.getIconContentDescription(notification));
            if (this.isImportantConversation(notificationEntry)) {
                if (b2) {
                    final IconPack icons3 = notificationEntry.getIcons();
                    Intrinsics.checkExpressionValueIsNotNull(icons3, "entry.icons");
                    icons3.setPeopleAvatarDescriptor(statusBarIcon);
                }
                else {
                    final IconPack icons4 = notificationEntry.getIcons();
                    Intrinsics.checkExpressionValueIsNotNull(icons4, "entry.icons");
                    icons4.setSmallIconDescriptor(statusBarIcon);
                }
            }
            return statusBarIcon;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("No icon in notification from ");
        final StatusBarNotification sbn4 = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn4, "entry.sbn");
        sb.append(sbn4.getPackageName());
        throw new InflationException(sb.toString());
    }
    
    private final Pair<StatusBarIcon, StatusBarIcon> getIconDescriptors(final NotificationEntry notificationEntry) throws InflationException {
        final StatusBarIcon iconDescriptor = this.getIconDescriptor(notificationEntry, false);
        StatusBarIcon iconDescriptor2;
        if (notificationEntry.isSensitive()) {
            iconDescriptor2 = this.getIconDescriptor(notificationEntry, true);
        }
        else {
            iconDescriptor2 = iconDescriptor;
        }
        return new Pair<StatusBarIcon, StatusBarIcon>(iconDescriptor, iconDescriptor2);
    }
    
    private final boolean isImportantConversation(final NotificationEntry notificationEntry) {
        final NotificationListenerService$Ranking ranking = notificationEntry.getRanking();
        Intrinsics.checkExpressionValueIsNotNull(ranking, "entry.ranking");
        if (ranking.getChannel() != null) {
            final NotificationListenerService$Ranking ranking2 = notificationEntry.getRanking();
            Intrinsics.checkExpressionValueIsNotNull(ranking2, "entry.ranking");
            final NotificationChannel channel = ranking2.getChannel();
            Intrinsics.checkExpressionValueIsNotNull(channel, "entry.ranking.channel");
            if (channel.isImportantConversation()) {
                return true;
            }
        }
        return false;
    }
    
    private final void setIcon(final NotificationEntry notificationEntry, final StatusBarIcon obj, final StatusBarIconView statusBarIconView) throws InflationException {
        statusBarIconView.setShowsConversation(this.showsConversation(notificationEntry, statusBarIconView, obj));
        if (statusBarIconView.set(obj)) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Couldn't create icon ");
        sb.append(obj);
        throw new InflationException(sb.toString());
    }
    
    private final void setTagOnIconViews(final IconPack iconPack, final int n, final Object o) {
        final StatusBarIconView statusBarIcon = iconPack.getStatusBarIcon();
        if (statusBarIcon != null) {
            statusBarIcon.setTag(n, o);
        }
        final StatusBarIconView shelfIcon = iconPack.getShelfIcon();
        if (shelfIcon != null) {
            shelfIcon.setTag(n, o);
        }
        final StatusBarIconView aodIcon = iconPack.getAodIcon();
        if (aodIcon != null) {
            aodIcon.setTag(n, o);
        }
        final StatusBarIconView centeredIcon = iconPack.getCenteredIcon();
        if (centeredIcon != null) {
            centeredIcon.setTag(n, o);
        }
    }
    
    private final boolean showsConversation(final NotificationEntry notificationEntry, final StatusBarIconView statusBarIconView, final StatusBarIcon statusBarIcon) {
        final IconPack icons = notificationEntry.getIcons();
        Intrinsics.checkExpressionValueIsNotNull(icons, "entry.icons");
        final StatusBarIconView shelfIcon = icons.getShelfIcon();
        final boolean b = false;
        boolean b2 = false;
        Label_0063: {
            if (statusBarIconView != shelfIcon) {
                final IconPack icons2 = notificationEntry.getIcons();
                Intrinsics.checkExpressionValueIsNotNull(icons2, "entry.icons");
                if (statusBarIconView != icons2.getAodIcon()) {
                    b2 = false;
                    break Label_0063;
                }
            }
            b2 = true;
        }
        final Icon icon = statusBarIcon.icon;
        final StatusBarNotification sbn = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn, "entry.sbn");
        final Notification notification = sbn.getNotification();
        Intrinsics.checkExpressionValueIsNotNull(notification, "entry.sbn.notification");
        final boolean equals = icon.equals((Object)notification.getSmallIcon());
        boolean b3 = b;
        if (this.isImportantConversation(notificationEntry)) {
            b3 = b;
            if (!equals) {
                if (b2) {
                    b3 = b;
                    if (notificationEntry.isSensitive()) {
                        return b3;
                    }
                }
                b3 = true;
            }
        }
        return b3;
    }
    
    private final void updateIconsSafe(final NotificationEntry notificationEntry) {
        try {
            this.updateIcons(notificationEntry);
        }
        catch (InflationException ex) {
            Log.e("IconManager", "Unable to update icon", (Throwable)ex);
        }
    }
    
    public final void attach() {
        this.notifCollection.addCollectionListener((NotifCollectionListener)this.entryListener);
    }
    
    public final void createIcons(final NotificationEntry notificationEntry) throws InflationException {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        final StatusBarIconView iconView = this.iconBuilder.createIconView(notificationEntry);
        iconView.setScaleType(ImageView$ScaleType.CENTER_INSIDE);
        final StatusBarIconView iconView2 = this.iconBuilder.createIconView(notificationEntry);
        iconView2.setScaleType(ImageView$ScaleType.CENTER_INSIDE);
        iconView2.setVisibility(4);
        iconView2.setOnVisibilityChangedListener((StatusBarIconView.OnVisibilityChangedListener)new IconManager$createIcons.IconManager$createIcons$1(notificationEntry));
        final StatusBarIconView iconView3 = this.iconBuilder.createIconView(notificationEntry);
        iconView3.setScaleType(ImageView$ScaleType.CENTER_INSIDE);
        iconView3.setIncreasedSize(true);
        final StatusBarNotification sbn = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn, "entry.sbn");
        final Notification notification = sbn.getNotification();
        Intrinsics.checkExpressionValueIsNotNull(notification, "entry.sbn.notification");
        StatusBarIconView iconView4;
        if (notification.isMediaNotification()) {
            iconView4 = this.iconBuilder.createIconView(notificationEntry);
            iconView4.setScaleType(ImageView$ScaleType.CENTER_INSIDE);
        }
        else {
            iconView4 = null;
        }
        final Pair<StatusBarIcon, StatusBarIcon> iconDescriptors = this.getIconDescriptors(notificationEntry);
        final StatusBarIcon statusBarIcon = iconDescriptors.component1();
        final StatusBarIcon statusBarIcon2 = iconDescriptors.component2();
        try {
            this.setIcon(notificationEntry, statusBarIcon, iconView);
            this.setIcon(notificationEntry, statusBarIcon2, iconView2);
            this.setIcon(notificationEntry, statusBarIcon2, iconView3);
            if (iconView4 != null) {
                this.setIcon(notificationEntry, statusBarIcon, iconView4);
            }
            notificationEntry.setIcons(IconPack.buildPack(iconView, iconView2, iconView3, iconView4, notificationEntry.getIcons()));
        }
        catch (InflationException ex) {
            notificationEntry.setIcons(IconPack.buildEmptyPack(notificationEntry.getIcons()));
            throw ex;
        }
    }
    
    public final void updateIconTags(final NotificationEntry notificationEntry, final int n) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        final IconPack icons = notificationEntry.getIcons();
        Intrinsics.checkExpressionValueIsNotNull(icons, "entry.icons");
        this.setTagOnIconViews(icons, R$id.icon_is_pre_L, n < 21);
    }
    
    public final void updateIcons(final NotificationEntry notificationEntry) throws InflationException {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        final IconPack icons = notificationEntry.getIcons();
        Intrinsics.checkExpressionValueIsNotNull(icons, "entry.icons");
        if (!icons.getAreIconsAvailable()) {
            return;
        }
        final IconPack icons2 = notificationEntry.getIcons();
        Intrinsics.checkExpressionValueIsNotNull(icons2, "entry.icons");
        icons2.setSmallIconDescriptor(null);
        final IconPack icons3 = notificationEntry.getIcons();
        Intrinsics.checkExpressionValueIsNotNull(icons3, "entry.icons");
        icons3.setPeopleAvatarDescriptor(null);
        final Pair<StatusBarIcon, StatusBarIcon> iconDescriptors = this.getIconDescriptors(notificationEntry);
        final StatusBarIcon statusBarIcon = iconDescriptors.component1();
        final StatusBarIcon statusBarIcon2 = iconDescriptors.component2();
        final IconPack icons4 = notificationEntry.getIcons();
        Intrinsics.checkExpressionValueIsNotNull(icons4, "entry.icons");
        final StatusBarIconView statusBarIcon3 = icons4.getStatusBarIcon();
        if (statusBarIcon3 != null) {
            Intrinsics.checkExpressionValueIsNotNull(statusBarIcon3, "it");
            statusBarIcon3.setNotification(notificationEntry.getSbn());
            this.setIcon(notificationEntry, statusBarIcon, statusBarIcon3);
        }
        final IconPack icons5 = notificationEntry.getIcons();
        Intrinsics.checkExpressionValueIsNotNull(icons5, "entry.icons");
        final StatusBarIconView shelfIcon = icons5.getShelfIcon();
        if (shelfIcon != null) {
            Intrinsics.checkExpressionValueIsNotNull(shelfIcon, "it");
            shelfIcon.setNotification(notificationEntry.getSbn());
            this.setIcon(notificationEntry, statusBarIcon, shelfIcon);
        }
        final IconPack icons6 = notificationEntry.getIcons();
        Intrinsics.checkExpressionValueIsNotNull(icons6, "entry.icons");
        final StatusBarIconView aodIcon = icons6.getAodIcon();
        if (aodIcon != null) {
            Intrinsics.checkExpressionValueIsNotNull(aodIcon, "it");
            aodIcon.setNotification(notificationEntry.getSbn());
            this.setIcon(notificationEntry, statusBarIcon2, aodIcon);
        }
        final IconPack icons7 = notificationEntry.getIcons();
        Intrinsics.checkExpressionValueIsNotNull(icons7, "entry.icons");
        final StatusBarIconView centeredIcon = icons7.getCenteredIcon();
        if (centeredIcon != null) {
            Intrinsics.checkExpressionValueIsNotNull(centeredIcon, "it");
            centeredIcon.setNotification(notificationEntry.getSbn());
            this.setIcon(notificationEntry, statusBarIcon2, centeredIcon);
        }
    }
}
