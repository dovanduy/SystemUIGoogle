// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.listbuilder.pluggable;

import com.android.systemui.statusbar.notification.collection.ListEntry;
import java.util.Comparator;

public abstract class NotifComparator extends Pluggable<NotifComparator> implements Comparator<ListEntry>
{
    @Override
    public abstract int compare(final ListEntry p0, final ListEntry p1);
}
