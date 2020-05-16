// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.BatteryController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ReverseChargingTile_Factory implements Factory<ReverseChargingTile>
{
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<QSHost> hostProvider;
    
    public ReverseChargingTile_Factory(final Provider<QSHost> hostProvider, final Provider<BatteryController> batteryControllerProvider) {
        this.hostProvider = hostProvider;
        this.batteryControllerProvider = batteryControllerProvider;
    }
    
    public static ReverseChargingTile_Factory create(final Provider<QSHost> provider, final Provider<BatteryController> provider2) {
        return new ReverseChargingTile_Factory(provider, provider2);
    }
    
    public static ReverseChargingTile provideInstance(final Provider<QSHost> provider, final Provider<BatteryController> provider2) {
        return new ReverseChargingTile(provider.get(), provider2.get());
    }
    
    @Override
    public ReverseChargingTile get() {
        return provideInstance(this.hostProvider, this.batteryControllerProvider);
    }
}
