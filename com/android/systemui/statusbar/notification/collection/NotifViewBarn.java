// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import android.view.textclassifier.Log;
import kotlin.jvm.internal.Intrinsics;
import java.util.LinkedHashMap;
import com.android.systemui.statusbar.notification.stack.NotificationListItem;
import java.util.Map;

public final class NotifViewBarn
{
    private final boolean DEBUG;
    private final Map<String, NotificationListItem> rowMap;
    
    public NotifViewBarn() {
        this.rowMap = new LinkedHashMap<String, NotificationListItem>();
    }
    
    public final void registerViewForEntry(final ListEntry obj, final NotificationListItem notificationListItem) {
        Intrinsics.checkParameterIsNotNull(obj, "entry");
        Intrinsics.checkParameterIsNotNull(notificationListItem, "view");
        if (this.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("registerViewForEntry: ");
            sb.append(obj);
            sb.append(".key");
            Log.d("NotifViewBarn", sb.toString());
        }
        final Map<String, NotificationListItem> rowMap = this.rowMap;
        final String key = obj.getKey();
        Intrinsics.checkExpressionValueIsNotNull(key, "entry.key");
        rowMap.put(key, notificationListItem);
    }
    
    public final void removeViewForEntry(final ListEntry obj) {
        Intrinsics.checkParameterIsNotNull(obj, "entry");
        if (this.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("removeViewForEntry: ");
            sb.append(obj);
            sb.append(".key");
            Log.d("NotifViewBarn", sb.toString());
        }
        this.rowMap.remove(obj.getKey());
    }
    
    public final NotificationListItem requireView(final ListEntry listEntry) {
        Intrinsics.checkParameterIsNotNull(listEntry, "forEntry");
        if (this.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("requireView: ");
            sb.append(listEntry);
            sb.append(".key");
            Log.d("NotifViewBarn", sb.toString());
        }
        final NotificationListItem notificationListItem = this.rowMap.get(listEntry.getKey());
        if (notificationListItem != null) {
            return notificationListItem;
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("No view has been registered for entry: ");
        sb2.append(listEntry);
        throw new IllegalStateException(sb2.toString());
    }
}
