// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import android.content.Context;
import android.os.Handler;
import javax.inject.Provider;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideLocalBluetoothControllerFactory implements Factory<LocalBluetoothManager>
{
    private final Provider<Handler> bgHandlerProvider;
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideLocalBluetoothControllerFactory(final Provider<Context> contextProvider, final Provider<Handler> bgHandlerProvider) {
        this.contextProvider = contextProvider;
        this.bgHandlerProvider = bgHandlerProvider;
    }
    
    public static SystemServicesModule_ProvideLocalBluetoothControllerFactory create(final Provider<Context> provider, final Provider<Handler> provider2) {
        return new SystemServicesModule_ProvideLocalBluetoothControllerFactory(provider, provider2);
    }
    
    public static LocalBluetoothManager provideInstance(final Provider<Context> provider, final Provider<Handler> provider2) {
        return proxyProvideLocalBluetoothController(provider.get(), provider2.get());
    }
    
    public static LocalBluetoothManager proxyProvideLocalBluetoothController(final Context context, final Handler handler) {
        return SystemServicesModule.provideLocalBluetoothController(context, handler);
    }
    
    @Override
    public LocalBluetoothManager get() {
        return provideInstance(this.contextProvider, this.bgHandlerProvider);
    }
}
