// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.hardware.display.NightDisplayListener;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.qs.QSTileHost;
import android.os.Handler;
import com.android.systemui.statusbar.policy.DataSaverController;
import android.content.Context;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.qs.AutoAddTracker;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AutoTileManager_Factory implements Factory<AutoTileManager>
{
    private final Provider<AutoAddTracker> autoAddTrackerProvider;
    private final Provider<CastController> castControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DataSaverController> dataSaverControllerProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<QSTileHost> hostProvider;
    private final Provider<HotspotController> hotspotControllerProvider;
    private final Provider<ManagedProfileController> managedProfileControllerProvider;
    private final Provider<NightDisplayListener> nightDisplayListenerProvider;
    
    public AutoTileManager_Factory(final Provider<Context> contextProvider, final Provider<AutoAddTracker> autoAddTrackerProvider, final Provider<QSTileHost> hostProvider, final Provider<Handler> handlerProvider, final Provider<HotspotController> hotspotControllerProvider, final Provider<DataSaverController> dataSaverControllerProvider, final Provider<ManagedProfileController> managedProfileControllerProvider, final Provider<NightDisplayListener> nightDisplayListenerProvider, final Provider<CastController> castControllerProvider) {
        this.contextProvider = contextProvider;
        this.autoAddTrackerProvider = autoAddTrackerProvider;
        this.hostProvider = hostProvider;
        this.handlerProvider = handlerProvider;
        this.hotspotControllerProvider = hotspotControllerProvider;
        this.dataSaverControllerProvider = dataSaverControllerProvider;
        this.managedProfileControllerProvider = managedProfileControllerProvider;
        this.nightDisplayListenerProvider = nightDisplayListenerProvider;
        this.castControllerProvider = castControllerProvider;
    }
    
    public static AutoTileManager_Factory create(final Provider<Context> provider, final Provider<AutoAddTracker> provider2, final Provider<QSTileHost> provider3, final Provider<Handler> provider4, final Provider<HotspotController> provider5, final Provider<DataSaverController> provider6, final Provider<ManagedProfileController> provider7, final Provider<NightDisplayListener> provider8, final Provider<CastController> provider9) {
        return new AutoTileManager_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
    }
    
    public static AutoTileManager provideInstance(final Provider<Context> provider, final Provider<AutoAddTracker> provider2, final Provider<QSTileHost> provider3, final Provider<Handler> provider4, final Provider<HotspotController> provider5, final Provider<DataSaverController> provider6, final Provider<ManagedProfileController> provider7, final Provider<NightDisplayListener> provider8, final Provider<CastController> provider9) {
        return new AutoTileManager(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get());
    }
    
    @Override
    public AutoTileManager get() {
        return provideInstance(this.contextProvider, this.autoAddTrackerProvider, this.hostProvider, this.handlerProvider, this.hotspotControllerProvider, this.dataSaverControllerProvider, this.managedProfileControllerProvider, this.nightDisplayListenerProvider, this.castControllerProvider);
    }
}
