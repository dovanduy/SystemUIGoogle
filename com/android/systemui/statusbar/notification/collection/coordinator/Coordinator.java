// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSection;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;

public interface Coordinator
{
    void attach(final NotifPipeline p0);
    
    default NotifSection getSection() {
        return null;
    }
}
