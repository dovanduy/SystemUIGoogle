// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.qs.QSHost;
import com.android.systemui.plugins.ActivityStarter;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class WifiTile_Factory implements Factory<WifiTile>
{
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<QSHost> hostProvider;
    private final Provider<NetworkController> networkControllerProvider;
    
    public WifiTile_Factory(final Provider<QSHost> hostProvider, final Provider<NetworkController> networkControllerProvider, final Provider<ActivityStarter> activityStarterProvider) {
        this.hostProvider = hostProvider;
        this.networkControllerProvider = networkControllerProvider;
        this.activityStarterProvider = activityStarterProvider;
    }
    
    public static WifiTile_Factory create(final Provider<QSHost> provider, final Provider<NetworkController> provider2, final Provider<ActivityStarter> provider3) {
        return new WifiTile_Factory(provider, provider2, provider3);
    }
    
    public static WifiTile provideInstance(final Provider<QSHost> provider, final Provider<NetworkController> provider2, final Provider<ActivityStarter> provider3) {
        return new WifiTile(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public WifiTile get() {
        return provideInstance(this.hostProvider, this.networkControllerProvider, this.activityStarterProvider);
    }
}
