// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.BatteryController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class UiModeNightTile_Factory implements Factory<UiModeNightTile>
{
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<QSHost> hostProvider;
    
    public UiModeNightTile_Factory(final Provider<QSHost> hostProvider, final Provider<ConfigurationController> configurationControllerProvider, final Provider<BatteryController> batteryControllerProvider) {
        this.hostProvider = hostProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.batteryControllerProvider = batteryControllerProvider;
    }
    
    public static UiModeNightTile_Factory create(final Provider<QSHost> provider, final Provider<ConfigurationController> provider2, final Provider<BatteryController> provider3) {
        return new UiModeNightTile_Factory(provider, provider2, provider3);
    }
    
    public static UiModeNightTile provideInstance(final Provider<QSHost> provider, final Provider<ConfigurationController> provider2, final Provider<BatteryController> provider3) {
        return new UiModeNightTile(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public UiModeNightTile get() {
        return provideInstance(this.hostProvider, this.configurationControllerProvider, this.batteryControllerProvider);
    }
}
