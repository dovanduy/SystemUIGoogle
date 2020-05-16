// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import dagger.internal.Factory;

public final class DeviceConfigHelper_Factory implements Factory<DeviceConfigHelper>
{
    private static final DeviceConfigHelper_Factory INSTANCE;
    
    static {
        INSTANCE = new DeviceConfigHelper_Factory();
    }
    
    public static DeviceConfigHelper_Factory create() {
        return DeviceConfigHelper_Factory.INSTANCE;
    }
    
    public static DeviceConfigHelper provideInstance() {
        return new DeviceConfigHelper();
    }
    
    @Override
    public DeviceConfigHelper get() {
        return provideInstance();
    }
}
