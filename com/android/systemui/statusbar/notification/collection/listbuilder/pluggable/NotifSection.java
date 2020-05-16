// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.listbuilder.pluggable;

import com.android.systemui.statusbar.notification.collection.ListEntry;

public abstract class NotifSection extends Pluggable<NotifSection>
{
    protected NotifSection(final String s) {
        super(s);
    }
    
    public abstract boolean isInSection(final ListEntry p0);
}
