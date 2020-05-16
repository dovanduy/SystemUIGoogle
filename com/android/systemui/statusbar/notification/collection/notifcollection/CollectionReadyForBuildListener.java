// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Collection;

public interface CollectionReadyForBuildListener
{
    void onBuildList(final Collection<NotificationEntry> p0);
}
