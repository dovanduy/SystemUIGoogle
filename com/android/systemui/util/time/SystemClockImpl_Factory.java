// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.time;

import dagger.internal.Factory;

public final class SystemClockImpl_Factory implements Factory<SystemClockImpl>
{
    private static final SystemClockImpl_Factory INSTANCE;
    
    static {
        INSTANCE = new SystemClockImpl_Factory();
    }
    
    public static SystemClockImpl_Factory create() {
        return SystemClockImpl_Factory.INSTANCE;
    }
    
    public static SystemClockImpl provideInstance() {
        return new SystemClockImpl();
    }
    
    @Override
    public SystemClockImpl get() {
        return provideInstance();
    }
}
