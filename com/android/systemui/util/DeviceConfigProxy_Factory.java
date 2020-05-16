// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import dagger.internal.Factory;

public final class DeviceConfigProxy_Factory implements Factory<DeviceConfigProxy>
{
    private static final DeviceConfigProxy_Factory INSTANCE;
    
    static {
        INSTANCE = new DeviceConfigProxy_Factory();
    }
    
    public static DeviceConfigProxy_Factory create() {
        return DeviceConfigProxy_Factory.INSTANCE;
    }
    
    public static DeviceConfigProxy provideInstance() {
        return new DeviceConfigProxy();
    }
    
    @Override
    public DeviceConfigProxy get() {
        return provideInstance();
    }
}
