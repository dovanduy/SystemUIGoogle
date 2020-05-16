// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.plugins.ActivityStarter;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class BluetoothTile_Factory implements Factory<BluetoothTile>
{
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<BluetoothController> bluetoothControllerProvider;
    private final Provider<QSHost> hostProvider;
    
    public BluetoothTile_Factory(final Provider<QSHost> hostProvider, final Provider<BluetoothController> bluetoothControllerProvider, final Provider<ActivityStarter> activityStarterProvider) {
        this.hostProvider = hostProvider;
        this.bluetoothControllerProvider = bluetoothControllerProvider;
        this.activityStarterProvider = activityStarterProvider;
    }
    
    public static BluetoothTile_Factory create(final Provider<QSHost> provider, final Provider<BluetoothController> provider2, final Provider<ActivityStarter> provider3) {
        return new BluetoothTile_Factory(provider, provider2, provider3);
    }
    
    public static BluetoothTile provideInstance(final Provider<QSHost> provider, final Provider<BluetoothController> provider2, final Provider<ActivityStarter> provider3) {
        return new BluetoothTile(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public BluetoothTile get() {
        return provideInstance(this.hostProvider, this.bluetoothControllerProvider, this.activityStarterProvider);
    }
}
