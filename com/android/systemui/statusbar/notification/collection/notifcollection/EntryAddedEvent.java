// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.notifcollection;

import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public final class EntryAddedEvent extends NotifEvent
{
    private final NotificationEntry entry;
    
    public EntryAddedEvent(final NotificationEntry entry) {
        Intrinsics.checkParameterIsNotNull(entry, "entry");
        super(null);
        this.entry = entry;
    }
    
    @Override
    public void dispatchToListener(final NotifCollectionListener notifCollectionListener) {
        Intrinsics.checkParameterIsNotNull(notifCollectionListener, "listener");
        notifCollectionListener.onEntryAdded(this.entry);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof EntryAddedEvent && Intrinsics.areEqual(this.entry, ((EntryAddedEvent)o).entry));
    }
    
    @Override
    public int hashCode() {
        final NotificationEntry entry = this.entry;
        int hashCode;
        if (entry != null) {
            hashCode = entry.hashCode();
        }
        else {
            hashCode = 0;
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("EntryAddedEvent(entry=");
        sb.append(this.entry);
        sb.append(")");
        return sb.toString();
    }
}
