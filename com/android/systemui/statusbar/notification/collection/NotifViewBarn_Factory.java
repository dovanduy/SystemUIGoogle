// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import dagger.internal.Factory;

public final class NotifViewBarn_Factory implements Factory<NotifViewBarn>
{
    private static final NotifViewBarn_Factory INSTANCE;
    
    static {
        INSTANCE = new NotifViewBarn_Factory();
    }
    
    public static NotifViewBarn_Factory create() {
        return NotifViewBarn_Factory.INSTANCE;
    }
    
    public static NotifViewBarn provideInstance() {
        return new NotifViewBarn();
    }
    
    @Override
    public NotifViewBarn get() {
        return provideInstance();
    }
}
