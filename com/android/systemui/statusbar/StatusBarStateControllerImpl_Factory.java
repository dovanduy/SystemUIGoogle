// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import dagger.internal.Factory;

public final class StatusBarStateControllerImpl_Factory implements Factory<StatusBarStateControllerImpl>
{
    private static final StatusBarStateControllerImpl_Factory INSTANCE;
    
    static {
        INSTANCE = new StatusBarStateControllerImpl_Factory();
    }
    
    public static StatusBarStateControllerImpl_Factory create() {
        return StatusBarStateControllerImpl_Factory.INSTANCE;
    }
    
    public static StatusBarStateControllerImpl provideInstance() {
        return new StatusBarStateControllerImpl();
    }
    
    @Override
    public StatusBarStateControllerImpl get() {
        return provideInstance();
    }
}
