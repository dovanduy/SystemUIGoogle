// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import android.provider.DeviceConfig;
import android.provider.DeviceConfig$OnPropertiesChangedListener;
import java.util.concurrent.Executor;

public class DeviceConfigProxy
{
    public void addOnPropertiesChangedListener(final String s, final Executor executor, final DeviceConfig$OnPropertiesChangedListener deviceConfig$OnPropertiesChangedListener) {
        DeviceConfig.addOnPropertiesChangedListener(s, executor, deviceConfig$OnPropertiesChangedListener);
    }
    
    public boolean getBoolean(final String s, final String s2, final boolean b) {
        return DeviceConfig.getBoolean(s, s2, b);
    }
    
    public float getFloat(final String s, final String s2, final float n) {
        return DeviceConfig.getFloat(s, s2, n);
    }
    
    public int getInt(final String s, final String s2, final int n) {
        return DeviceConfig.getInt(s, s2, n);
    }
    
    public String getProperty(final String s, final String s2) {
        return DeviceConfig.getProperty(s, s2);
    }
    
    public void removeOnPropertiesChangedListener(final DeviceConfig$OnPropertiesChangedListener deviceConfig$OnPropertiesChangedListener) {
        DeviceConfig.removeOnPropertiesChangedListener(deviceConfig$OnPropertiesChangedListener);
    }
}
