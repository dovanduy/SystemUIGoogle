// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.settingslib.bluetooth.LocalBluetoothManager;
import android.content.Context;
import android.os.Looper;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class BluetoothControllerImpl_Factory implements Factory<BluetoothControllerImpl>
{
    private final Provider<Looper> bgLooperProvider;
    private final Provider<Context> contextProvider;
    private final Provider<LocalBluetoothManager> localBluetoothManagerProvider;
    private final Provider<Looper> mainLooperProvider;
    
    public BluetoothControllerImpl_Factory(final Provider<Context> contextProvider, final Provider<Looper> bgLooperProvider, final Provider<Looper> mainLooperProvider, final Provider<LocalBluetoothManager> localBluetoothManagerProvider) {
        this.contextProvider = contextProvider;
        this.bgLooperProvider = bgLooperProvider;
        this.mainLooperProvider = mainLooperProvider;
        this.localBluetoothManagerProvider = localBluetoothManagerProvider;
    }
    
    public static BluetoothControllerImpl_Factory create(final Provider<Context> provider, final Provider<Looper> provider2, final Provider<Looper> provider3, final Provider<LocalBluetoothManager> provider4) {
        return new BluetoothControllerImpl_Factory(provider, provider2, provider3, provider4);
    }
    
    public static BluetoothControllerImpl provideInstance(final Provider<Context> provider, final Provider<Looper> provider2, final Provider<Looper> provider3, final Provider<LocalBluetoothManager> provider4) {
        return new BluetoothControllerImpl(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public BluetoothControllerImpl get() {
        return provideInstance(this.contextProvider, this.bgLooperProvider, this.mainLooperProvider, this.localBluetoothManagerProvider);
    }
}
