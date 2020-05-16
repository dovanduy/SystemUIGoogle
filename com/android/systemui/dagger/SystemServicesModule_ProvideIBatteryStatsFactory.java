// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import com.android.internal.app.IBatteryStats;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideIBatteryStatsFactory implements Factory<IBatteryStats>
{
    private static final SystemServicesModule_ProvideIBatteryStatsFactory INSTANCE;
    
    static {
        INSTANCE = new SystemServicesModule_ProvideIBatteryStatsFactory();
    }
    
    public static SystemServicesModule_ProvideIBatteryStatsFactory create() {
        return SystemServicesModule_ProvideIBatteryStatsFactory.INSTANCE;
    }
    
    public static IBatteryStats provideInstance() {
        return proxyProvideIBatteryStats();
    }
    
    public static IBatteryStats proxyProvideIBatteryStats() {
        final IBatteryStats provideIBatteryStats = SystemServicesModule.provideIBatteryStats();
        Preconditions.checkNotNull(provideIBatteryStats, "Cannot return null from a non-@Nullable @Provides method");
        return provideIBatteryStats;
    }
    
    @Override
    public IBatteryStats get() {
        return provideInstance();
    }
}
