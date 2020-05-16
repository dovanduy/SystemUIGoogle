// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.notifcollection;

import kotlin.jvm.internal.Intrinsics;
import java.util.List;

public abstract class NotifEvent
{
    private NotifEvent() {
    }
    
    public final void dispatchTo(final List<? extends NotifCollectionListener> list) {
        Intrinsics.checkParameterIsNotNull(list, "listeners");
        for (int size = list.size(), i = 0; i < size; ++i) {
            this.dispatchToListener((NotifCollectionListener)list.get(i));
        }
    }
    
    public abstract void dispatchToListener(final NotifCollectionListener p0);
}
