// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.notifcollection;

import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public final class InitEntryEvent extends NotifEvent
{
    private final NotificationEntry entry;
    
    public InitEntryEvent(final NotificationEntry entry) {
        Intrinsics.checkParameterIsNotNull(entry, "entry");
        super(null);
        this.entry = entry;
    }
    
    @Override
    public void dispatchToListener(final NotifCollectionListener notifCollectionListener) {
        Intrinsics.checkParameterIsNotNull(notifCollectionListener, "listener");
        notifCollectionListener.onEntryInit(this.entry);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof InitEntryEvent && Intrinsics.areEqual(this.entry, ((InitEntryEvent)o).entry));
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
        sb.append("InitEntryEvent(entry=");
        sb.append(this.entry);
        sb.append(")");
        return sb.toString();
    }
}
