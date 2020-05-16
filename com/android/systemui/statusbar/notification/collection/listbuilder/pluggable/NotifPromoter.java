// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.listbuilder.pluggable;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public abstract class NotifPromoter extends Pluggable<NotifPromoter>
{
    protected NotifPromoter(final String s) {
        super(s);
    }
    
    public abstract boolean shouldPromoteToTopLevel(final NotificationEntry p0);
}
