// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import dagger.internal.Factory;

public final class NotifInflationErrorManager_Factory implements Factory<NotifInflationErrorManager>
{
    private static final NotifInflationErrorManager_Factory INSTANCE;
    
    static {
        INSTANCE = new NotifInflationErrorManager_Factory();
    }
    
    public static NotifInflationErrorManager_Factory create() {
        return NotifInflationErrorManager_Factory.INSTANCE;
    }
    
    public static NotifInflationErrorManager provideInstance() {
        return new NotifInflationErrorManager();
    }
    
    @Override
    public NotifInflationErrorManager get() {
        return provideInstance();
    }
}
