// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import java.util.List;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifDismissInterceptor;
import java.util.Arrays;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender;

public class ListDumper
{
    private static void dumpEntry(final ListEntry listEntry, String key, final String s, final StringBuilder sb, final boolean b, final boolean b2) {
        sb.append(s);
        sb.append("[");
        sb.append(key);
        sb.append("] ");
        sb.append(listEntry.getKey());
        if (b) {
            sb.append(" (parent=");
            if (listEntry.getParent() != null) {
                key = listEntry.getParent().getKey();
            }
            else {
                key = null;
            }
            sb.append(key);
            sb.append(")");
        }
        if (listEntry.getNotifSection() != null) {
            sb.append(" sectionIndex=");
            sb.append(listEntry.getSection());
            sb.append(" sectionName=");
            sb.append(listEntry.getNotifSection().getName());
        }
        if (b2) {
            final NotificationEntry representativeEntry = listEntry.getRepresentativeEntry();
            final StringBuilder sb2 = new StringBuilder();
            final boolean empty = representativeEntry.mLifetimeExtenders.isEmpty();
            final int n = 0;
            if (!empty) {
                final int size = representativeEntry.mLifetimeExtenders.size();
                final String[] a = new String[size];
                for (int i = 0; i < size; ++i) {
                    a[i] = representativeEntry.mLifetimeExtenders.get(i).getName();
                }
                sb2.append("lifetimeExtenders=");
                sb2.append(Arrays.toString(a));
                sb2.append(" ");
            }
            if (!representativeEntry.mDismissInterceptors.isEmpty()) {
                final int size2 = representativeEntry.mDismissInterceptors.size();
                final String[] a2 = new String[size2];
                for (int j = n; j < size2; ++j) {
                    a2[j] = representativeEntry.mDismissInterceptors.get(j).getName();
                }
                sb2.append("dismissInterceptors=");
                sb2.append(Arrays.toString(a2));
                sb2.append(" ");
            }
            if (representativeEntry.getExcludingFilter() != null) {
                sb2.append("filter=");
                sb2.append(representativeEntry.getExcludingFilter().getName());
                sb2.append(" ");
            }
            if (representativeEntry.getNotifPromoter() != null) {
                sb2.append("promoter=");
                sb2.append(representativeEntry.getNotifPromoter().getName());
                sb2.append(" ");
            }
            if (representativeEntry.mCancellationReason != -1) {
                sb2.append("cancellationReason=");
                sb2.append(representativeEntry.mCancellationReason);
                sb2.append(" ");
            }
            if (representativeEntry.getDismissState() != NotificationEntry.DismissState.NOT_DISMISSED) {
                sb2.append("dismissState=");
                sb2.append(representativeEntry.getDismissState());
                sb2.append(" ");
            }
            final String string = sb2.toString();
            if (!string.isEmpty()) {
                sb.append("\n\t");
                sb.append(s);
                sb.append(string);
            }
        }
        sb.append("\n");
    }
    
    public static String dumpList(final List<NotificationEntry> list, final boolean b, final String s) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); ++i) {
            dumpEntry(list.get(i), Integer.toString(i), s, sb, false, b);
        }
        return sb.toString();
    }
    
    public static String dumpTree(final List<ListEntry> list, final boolean b, final String str) {
        final StringBuilder sb = new StringBuilder();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append("  ");
        final String string = sb2.toString();
        for (int i = 0; i < list.size(); ++i) {
            final ListEntry listEntry = list.get(i);
            dumpEntry(listEntry, Integer.toString(i), str, sb, true, b);
            if (listEntry instanceof GroupEntry) {
                final List<NotificationEntry> children = ((GroupEntry)listEntry).getChildren();
                for (int j = 0; j < children.size(); ++j) {
                    final NotificationEntry notificationEntry = children.get(j);
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append(Integer.toString(i));
                    sb3.append(".");
                    sb3.append(Integer.toString(j));
                    dumpEntry(notificationEntry, sb3.toString(), string, sb, true, b);
                }
            }
        }
        return sb.toString();
    }
}
