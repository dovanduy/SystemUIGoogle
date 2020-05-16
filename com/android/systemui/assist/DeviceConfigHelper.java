// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import java.util.function.Supplier;
import com.android.systemui.DejankUtils;
import android.provider.DeviceConfig$OnPropertiesChangedListener;
import java.util.concurrent.Executor;
import android.provider.DeviceConfig;

public class DeviceConfigHelper
{
    public void addOnPropertiesChangedListener(final Executor executor, final DeviceConfig$OnPropertiesChangedListener deviceConfig$OnPropertiesChangedListener) {
        DeviceConfig.addOnPropertiesChangedListener("systemui", executor, deviceConfig$OnPropertiesChangedListener);
    }
    
    public boolean getBoolean(final String s, final boolean b) {
        return DejankUtils.whitelistIpcs((Supplier<Boolean>)new _$$Lambda$DeviceConfigHelper$HWniMUF9Jobip6r9UKC_XeuOiT4(s, b));
    }
    
    public int getInt(final String s, final int n) {
        return DejankUtils.whitelistIpcs((Supplier<Integer>)new _$$Lambda$DeviceConfigHelper$_Ng8xYHPOvZ_2ultguhmGQJUI2A(s, n));
    }
    
    public long getLong(final String s, final long n) {
        return DejankUtils.whitelistIpcs((Supplier<Long>)new _$$Lambda$DeviceConfigHelper$3aQUQDpT19LyipkVjVVewd3Du_U(s, n));
    }
    
    public String getString(final String s, final String s2) {
        return DejankUtils.whitelistIpcs((Supplier<String>)new _$$Lambda$DeviceConfigHelper$3D4OB5zAUMlCtZQpKS6FfDrXEDI(s, s2));
    }
    
    public void removeOnPropertiesChangedListener(final DeviceConfig$OnPropertiesChangedListener deviceConfig$OnPropertiesChangedListener) {
        DeviceConfig.removeOnPropertiesChangedListener(deviceConfig$OnPropertiesChangedListener);
    }
}
