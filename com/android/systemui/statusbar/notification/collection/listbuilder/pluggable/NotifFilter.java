// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.listbuilder.pluggable;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public abstract class NotifFilter extends Pluggable<NotifFilter>
{
    protected NotifFilter(final String s) {
        super(s);
    }
    
    public abstract boolean shouldFilterOut(final NotificationEntry p0, final long p1);
}
