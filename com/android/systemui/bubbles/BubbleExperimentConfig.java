// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import java.util.Arrays;
import android.content.pm.LauncherApps$ShortcutQuery;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;
import java.util.Iterator;
import android.os.Parcelable;
import android.os.Bundle;
import android.app.Notification$MessagingStyle$Message;
import com.android.internal.util.ArrayUtils;
import java.util.Collection;
import java.util.ArrayList;
import android.graphics.drawable.Drawable;
import java.util.List;
import android.app.PendingIntent;
import android.app.Notification;
import com.android.internal.util.ContrastColorUtil;
import com.android.internal.graphics.ColorUtils;
import android.graphics.drawable.Icon;
import android.graphics.drawable.BitmapDrawable;
import com.android.systemui.statusbar.notification.people.PeopleHubNotificationListenerKt;
import android.app.Person;
import android.app.Notification$BubbleMetadata$Builder;
import android.content.ContentResolver;
import android.provider.Settings$Secure;
import android.app.Notification$BubbleMetadata;
import android.util.Log;
import android.app.Notification$MessagingStyle;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.content.Context;

public class BubbleExperimentConfig
{
    static boolean adjustForExperiments(final Context context, final NotificationEntry notificationEntry, final boolean b, final boolean b2) {
        final boolean packageWhitelistedToAutoBubble = isPackageWhitelistedToAutoBubble(context, notificationEntry.getSbn().getPackageName());
        final boolean equals = Notification$MessagingStyle.class.equals(notificationEntry.getSbn().getNotification().getNotificationStyle());
        final int n = 0;
        final boolean b3 = (equals && allowMessageNotifsToBubble(context)) || allowAnyNotifToBubble(context);
        final boolean useShortcutInfoToBubble = useShortcutInfoToBubble(context);
        final String shortcutId = notificationEntry.getSbn().getNotification().getShortcutId();
        final boolean b4 = notificationEntry.getBubbleMetadata() != null;
        if ((!b4 && (b || b3)) || useShortcutInfoToBubble) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Adjusting ");
            sb.append(notificationEntry.getKey());
            sb.append(" for bubble experiment. allowMessages=");
            sb.append(allowMessageNotifsToBubble(context));
            sb.append(" isMessage=");
            sb.append(equals);
            sb.append(" allowNotifs=");
            sb.append(allowAnyNotifToBubble(context));
            sb.append(" useShortcutInfo=");
            sb.append(useShortcutInfoToBubble);
            sb.append(" previouslyUserCreated=");
            sb.append(b);
            Log.d("Bubbles", sb.toString());
        }
        boolean b5 = false;
        Label_0372: {
            if (useShortcutInfoToBubble && shortcutId != null) {
                Notification$BubbleMetadata forShortcut;
                if (getShortcutInfo(context, notificationEntry.getSbn().getPackageName(), notificationEntry.getSbn().getUser(), shortcutId) != null) {
                    forShortcut = createForShortcut(shortcutId);
                }
                else {
                    forShortcut = null;
                }
                if ((notificationEntry.getBubbleMetadata() != null || b3 || b) && forShortcut != null) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Adding experimental shortcut bubble for: ");
                    sb2.append(notificationEntry.getKey());
                    Log.d("Bubbles", sb2.toString());
                    notificationEntry.setBubbleMetadata(forShortcut);
                    b5 = true;
                    break Label_0372;
                }
            }
            b5 = false;
        }
        boolean b6 = b5;
        Label_0456: {
            if (notificationEntry.getBubbleMetadata() == null) {
                if (!b3) {
                    b6 = b5;
                    if (!b) {
                        break Label_0456;
                    }
                }
                final Notification$BubbleMetadata fromNotif = createFromNotif(context, notificationEntry);
                b6 = b5;
                if (fromNotif != null) {
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append("Adding experimental notification bubble for: ");
                    sb3.append(notificationEntry.getKey());
                    Log.d("Bubbles", sb3.toString());
                    notificationEntry.setBubbleMetadata(fromNotif);
                    b6 = true;
                }
            }
        }
        int n2 = n;
        Label_0490: {
            if (!b2) {
                n2 = n;
                if (packageWhitelistedToAutoBubble) {
                    if (!b6) {
                        n2 = n;
                        if (!b4) {
                            break Label_0490;
                        }
                    }
                    n2 = 1;
                }
            }
        }
        if ((b && b6) || n2 != 0) {
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("Setting FLAG_BUBBLE for: ");
            sb4.append(notificationEntry.getKey());
            Log.d("Bubbles", sb4.toString());
            notificationEntry.setFlagBubble(true);
            return true;
        }
        return b6;
    }
    
    static boolean allowAnyNotifToBubble(final Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        boolean b = false;
        if (Settings$Secure.getInt(contentResolver, "allow_any_notif_to_bubble", 0) != 0) {
            b = true;
        }
        return b;
    }
    
    static boolean allowBubbleOverflow(final Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        boolean b = false;
        if (Settings$Secure.getInt(contentResolver, "allow_bubble_overflow", 0) != 0) {
            b = true;
        }
        return b;
    }
    
    static boolean allowMessageNotifsToBubble(final Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        boolean b = true;
        if (Settings$Secure.getInt(contentResolver, "allow_message_notifs_to_bubble", 1) == 0) {
            b = false;
        }
        return b;
    }
    
    static Notification$BubbleMetadata createForShortcut(final String s) {
        return new Notification$BubbleMetadata$Builder(s).setDesiredHeight(10000).build();
    }
    
    static Notification$BubbleMetadata createFromNotif(final Context context, final NotificationEntry notificationEntry) {
        final Notification notification = notificationEntry.getSbn().getNotification();
        final PendingIntent contentIntent = notification.contentIntent;
        final List<Person> peopleFromNotification = getPeopleFromNotification(notificationEntry);
        final int size = peopleFromNotification.size();
        boolean b = false;
        Icon withBitmap = null;
        Label_0097: {
            if (size > 0) {
                final Person person = peopleFromNotification.get(0);
                if (person != null) {
                    final Icon icon = person.getIcon();
                    if ((withBitmap = icon) != null) {
                        break Label_0097;
                    }
                    final Drawable avatarFromRow = PeopleHubNotificationListenerKt.extractAvatarFromRow(notificationEntry);
                    withBitmap = icon;
                    if (avatarFromRow instanceof BitmapDrawable) {
                        withBitmap = Icon.createWithBitmap(((BitmapDrawable)avatarFromRow).getBitmap());
                    }
                    break Label_0097;
                }
            }
            withBitmap = null;
        }
        Icon icon2;
        if ((icon2 = withBitmap) == null) {
            if (notification.getLargeIcon() == null) {
                b = true;
            }
            Icon icon3;
            if (b) {
                icon3 = notification.getSmallIcon();
            }
            else {
                icon3 = notification.getLargeIcon();
            }
            icon2 = icon3;
            if (b) {
                icon3.setTint(ContrastColorUtil.findContrastColor(ColorUtils.setAlphaComponent(notificationEntry.getSbn().getNotification().color, 255), -1, true, 3.0));
                icon2 = icon3;
            }
        }
        if (contentIntent != null) {
            return new Notification$BubbleMetadata$Builder(contentIntent, icon2).setDesiredHeight(10000).build();
        }
        return null;
    }
    
    static List<Person> getPeopleFromNotification(final NotificationEntry notificationEntry) {
        final Bundle extras = notificationEntry.getSbn().getNotification().extras;
        final ArrayList<Person> list = new ArrayList<Person>();
        if (extras == null) {
            return list;
        }
        final ArrayList parcelableArrayList = extras.getParcelableArrayList("android.people.list");
        if (parcelableArrayList != null) {
            list.addAll(parcelableArrayList);
        }
        if (Notification$MessagingStyle.class.equals(notificationEntry.getSbn().getNotification().getNotificationStyle())) {
            final Parcelable[] parcelableArray = extras.getParcelableArray("android.messages");
            if (!ArrayUtils.isEmpty((Object[])parcelableArray)) {
                final Iterator iterator = Notification$MessagingStyle$Message.getMessagesFromBundleArray(parcelableArray).iterator();
                while (iterator.hasNext()) {
                    list.add(iterator.next().getSenderPerson());
                }
            }
        }
        return list;
    }
    
    static ShortcutInfo getShortcutInfo(final Context context, final String package1, final UserHandle userHandle, final String s) {
        final LauncherApps launcherApps = (LauncherApps)context.getSystemService("launcherapps");
        final LauncherApps$ShortcutQuery launcherApps$ShortcutQuery = new LauncherApps$ShortcutQuery();
        if (package1 != null) {
            launcherApps$ShortcutQuery.setPackage(package1);
        }
        if (s != null) {
            launcherApps$ShortcutQuery.setShortcutIds((List)Arrays.asList(s));
        }
        launcherApps$ShortcutQuery.setQueryFlags(11);
        final List shortcuts = launcherApps.getShortcuts(launcherApps$ShortcutQuery, userHandle);
        ShortcutInfo shortcutInfo;
        if (shortcuts != null && shortcuts.size() > 0) {
            shortcutInfo = shortcuts.get(0);
        }
        else {
            shortcutInfo = null;
        }
        return shortcutInfo;
    }
    
    static boolean isPackageWhitelistedToAutoBubble(final Context context, final String anObject) {
        final String string = Settings$Secure.getString(context.getContentResolver(), "whitelisted_auto_bubble_apps");
        if (string != null) {
            final String[] split = string.split(",");
            for (int i = 0; i < split.length; ++i) {
                if (split[i].trim().equals(anObject)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static boolean useShortcutInfoToBubble(final Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        boolean b = false;
        if (Settings$Secure.getInt(contentResolver, "allow_shortcuts_to_bubble", 0) != 0) {
            b = true;
        }
        return b;
    }
}
