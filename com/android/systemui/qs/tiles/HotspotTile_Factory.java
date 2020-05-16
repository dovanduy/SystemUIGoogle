// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.DataSaverController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class HotspotTile_Factory implements Factory<HotspotTile>
{
    private final Provider<DataSaverController> dataSaverControllerProvider;
    private final Provider<QSHost> hostProvider;
    private final Provider<HotspotController> hotspotControllerProvider;
    
    public HotspotTile_Factory(final Provider<QSHost> hostProvider, final Provider<HotspotController> hotspotControllerProvider, final Provider<DataSaverController> dataSaverControllerProvider) {
        this.hostProvider = hostProvider;
        this.hotspotControllerProvider = hotspotControllerProvider;
        this.dataSaverControllerProvider = dataSaverControllerProvider;
    }
    
    public static HotspotTile_Factory create(final Provider<QSHost> provider, final Provider<HotspotController> provider2, final Provider<DataSaverController> provider3) {
        return new HotspotTile_Factory(provider, provider2, provider3);
    }
    
    public static HotspotTile provideInstance(final Provider<QSHost> provider, final Provider<HotspotController> provider2, final Provider<DataSaverController> provider3) {
        return new HotspotTile(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public HotspotTile get() {
        return provideInstance(this.hostProvider, this.hotspotControllerProvider, this.dataSaverControllerProvider);
    }
}
