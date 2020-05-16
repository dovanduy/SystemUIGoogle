// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.qs.QSHost;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DataSaverTile_Factory implements Factory<DataSaverTile>
{
    private final Provider<QSHost> hostProvider;
    private final Provider<NetworkController> networkControllerProvider;
    
    public DataSaverTile_Factory(final Provider<QSHost> hostProvider, final Provider<NetworkController> networkControllerProvider) {
        this.hostProvider = hostProvider;
        this.networkControllerProvider = networkControllerProvider;
    }
    
    public static DataSaverTile_Factory create(final Provider<QSHost> provider, final Provider<NetworkController> provider2) {
        return new DataSaverTile_Factory(provider, provider2);
    }
    
    public static DataSaverTile provideInstance(final Provider<QSHost> provider, final Provider<NetworkController> provider2) {
        return new DataSaverTile(provider.get(), provider2.get());
    }
    
    @Override
    public DataSaverTile get() {
        return provideInstance(this.hostProvider, this.networkControllerProvider);
    }
}
