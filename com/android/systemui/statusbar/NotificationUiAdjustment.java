// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.app.RemoteInput;
import java.util.Objects;
import android.text.TextUtils;
import android.graphics.drawable.Icon;
import java.util.Collections;
import android.app.Notification$Action;
import java.util.List;

public class NotificationUiAdjustment
{
    public final boolean isConversation;
    public final List<Notification$Action> smartActions;
    public final List<CharSequence> smartReplies;
    
    NotificationUiAdjustment(final String s, final List<Notification$Action> list, final List<CharSequence> list2, final boolean isConversation) {
        List<Notification$Action> emptyList = list;
        if (list == null) {
            emptyList = Collections.emptyList();
        }
        this.smartActions = emptyList;
        List<CharSequence> emptyList2;
        if ((emptyList2 = list2) == null) {
            emptyList2 = Collections.emptyList();
        }
        this.smartReplies = emptyList2;
        this.isConversation = isConversation;
    }
    
    private static boolean areDifferent(final Icon icon, final Icon icon2) {
        return icon != icon2 && (icon == null || icon2 == null || (icon.sameAs(icon2) ^ true));
    }
    
    public static boolean areDifferent(final List<Notification$Action> list, final List<Notification$Action> list2) {
        if (list == list2) {
            return false;
        }
        if (list == null || list2 == null) {
            return true;
        }
        if (list.size() != list2.size()) {
            return true;
        }
        for (int i = 0; i < list.size(); ++i) {
            final Notification$Action notification$Action = list.get(i);
            final Notification$Action notification$Action2 = list2.get(i);
            if (!TextUtils.equals(notification$Action.title, notification$Action2.title)) {
                return true;
            }
            if (areDifferent(notification$Action.getIcon(), notification$Action2.getIcon())) {
                return true;
            }
            if (!Objects.equals(notification$Action.actionIntent, notification$Action2.actionIntent)) {
                return true;
            }
            if (areDifferent(notification$Action.getRemoteInputs(), notification$Action2.getRemoteInputs())) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean areDifferent(final RemoteInput[] array, final RemoteInput[] array2) {
        if (array == array2) {
            return false;
        }
        if (array == null || array2 == null) {
            return true;
        }
        if (array.length != array2.length) {
            return true;
        }
        for (int i = 0; i < array.length; ++i) {
            final RemoteInput remoteInput = array[i];
            final RemoteInput remoteInput2 = array2[i];
            if (!TextUtils.equals(remoteInput.getLabel(), remoteInput2.getLabel())) {
                return true;
            }
            if (areDifferent(remoteInput.getChoices(), remoteInput2.getChoices())) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean areDifferent(final CharSequence[] array, final CharSequence[] array2) {
        if (array == array2) {
            return false;
        }
        if (array == null || array2 == null) {
            return true;
        }
        if (array.length != array2.length) {
            return true;
        }
        for (int i = 0; i < array.length; ++i) {
            if (!TextUtils.equals(array[i], array2[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static NotificationUiAdjustment extractFromNotificationEntry(final NotificationEntry notificationEntry) {
        return new NotificationUiAdjustment(notificationEntry.getKey(), notificationEntry.getSmartActions(), notificationEntry.getSmartReplies(), notificationEntry.getRanking().isConversation());
    }
    
    public static boolean needReinflate(final NotificationUiAdjustment notificationUiAdjustment, final NotificationUiAdjustment notificationUiAdjustment2) {
        return notificationUiAdjustment != notificationUiAdjustment2 && (notificationUiAdjustment.isConversation != notificationUiAdjustment2.isConversation || areDifferent(notificationUiAdjustment.smartActions, notificationUiAdjustment2.smartActions) || !notificationUiAdjustment2.smartReplies.equals(notificationUiAdjustment.smartReplies));
    }
}
