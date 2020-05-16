// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import dagger.internal.DoubleCheck;
import android.os.Handler;
import com.android.systemui.assist.DeviceConfigHelper;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ColumbusServiceWrapper_Factory implements Factory<ColumbusServiceWrapper>
{
    private final Provider<ColumbusService> columbusServiceProvider;
    private final Provider<Boolean> debugBuildTypeProvider;
    private final Provider<DeviceConfigHelper> deviceConfigHelperProvider;
    private final Provider<Handler> handlerProvider;
    
    public ColumbusServiceWrapper_Factory(final Provider<Boolean> debugBuildTypeProvider, final Provider<ColumbusService> columbusServiceProvider, final Provider<DeviceConfigHelper> deviceConfigHelperProvider, final Provider<Handler> handlerProvider) {
        this.debugBuildTypeProvider = debugBuildTypeProvider;
        this.columbusServiceProvider = columbusServiceProvider;
        this.deviceConfigHelperProvider = deviceConfigHelperProvider;
        this.handlerProvider = handlerProvider;
    }
    
    public static ColumbusServiceWrapper_Factory create(final Provider<Boolean> provider, final Provider<ColumbusService> provider2, final Provider<DeviceConfigHelper> provider3, final Provider<Handler> provider4) {
        return new ColumbusServiceWrapper_Factory(provider, provider2, provider3, provider4);
    }
    
    public static ColumbusServiceWrapper provideInstance(final Provider<Boolean> provider, final Provider<ColumbusService> provider2, final Provider<DeviceConfigHelper> provider3, final Provider<Handler> provider4) {
        return new ColumbusServiceWrapper(provider.get(), DoubleCheck.lazy(provider2), provider3.get(), provider4.get());
    }
    
    @Override
    public ColumbusServiceWrapper get() {
        return provideInstance(this.debugBuildTypeProvider, this.columbusServiceProvider, this.deviceConfigHelperProvider, this.handlerProvider);
    }
}
