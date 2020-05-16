// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.BatteryController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class BatterySaverTile_Factory implements Factory<BatterySaverTile>
{
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<QSHost> hostProvider;
    
    public BatterySaverTile_Factory(final Provider<QSHost> hostProvider, final Provider<BatteryController> batteryControllerProvider) {
        this.hostProvider = hostProvider;
        this.batteryControllerProvider = batteryControllerProvider;
    }
    
    public static BatterySaverTile_Factory create(final Provider<QSHost> provider, final Provider<BatteryController> provider2) {
        return new BatterySaverTile_Factory(provider, provider2);
    }
    
    public static BatterySaverTile provideInstance(final Provider<QSHost> provider, final Provider<BatteryController> provider2) {
        return new BatterySaverTile(provider.get(), provider2.get());
    }
    
    @Override
    public BatterySaverTile get() {
        return provideInstance(this.hostProvider, this.batteryControllerProvider);
    }
}
