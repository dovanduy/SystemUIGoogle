// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.notifcollection;

import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public final class EntryRemovedEvent extends NotifEvent
{
    private final NotificationEntry entry;
    private final int reason;
    
    public EntryRemovedEvent(final NotificationEntry entry, final int reason) {
        Intrinsics.checkParameterIsNotNull(entry, "entry");
        super(null);
        this.entry = entry;
        this.reason = reason;
    }
    
    @Override
    public void dispatchToListener(final NotifCollectionListener notifCollectionListener) {
        Intrinsics.checkParameterIsNotNull(notifCollectionListener, "listener");
        notifCollectionListener.onEntryRemoved(this.entry, this.reason);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof EntryRemovedEvent) {
                final EntryRemovedEvent entryRemovedEvent = (EntryRemovedEvent)o;
                if (Intrinsics.areEqual(this.entry, entryRemovedEvent.entry) && this.reason == entryRemovedEvent.reason) {
                    return true;
                }
            }
            return false;
        }
        return true;
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
        return hashCode * 31 + Integer.hashCode(this.reason);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("EntryRemovedEvent(entry=");
        sb.append(this.entry);
        sb.append(", reason=");
        sb.append(this.reason);
        sb.append(")");
        return sb.toString();
    }
}
