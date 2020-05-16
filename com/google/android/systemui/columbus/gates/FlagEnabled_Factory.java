// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.os.Handler;
import com.android.systemui.assist.DeviceConfigHelper;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class FlagEnabled_Factory implements Factory<FlagEnabled>
{
    private final Provider<Context> contextProvider;
    private final Provider<Boolean> debugBuildTypeProvider;
    private final Provider<DeviceConfigHelper> deviceConfigHelperProvider;
    private final Provider<Handler> handlerProvider;
    
    public FlagEnabled_Factory(final Provider<Boolean> debugBuildTypeProvider, final Provider<Context> contextProvider, final Provider<Handler> handlerProvider, final Provider<DeviceConfigHelper> deviceConfigHelperProvider) {
        this.debugBuildTypeProvider = debugBuildTypeProvider;
        this.contextProvider = contextProvider;
        this.handlerProvider = handlerProvider;
        this.deviceConfigHelperProvider = deviceConfigHelperProvider;
    }
    
    public static FlagEnabled_Factory create(final Provider<Boolean> provider, final Provider<Context> provider2, final Provider<Handler> provider3, final Provider<DeviceConfigHelper> provider4) {
        return new FlagEnabled_Factory(provider, provider2, provider3, provider4);
    }
    
    public static FlagEnabled provideInstance(final Provider<Boolean> provider, final Provider<Context> provider2, final Provider<Handler> provider3, final Provider<DeviceConfigHelper> provider4) {
        return new FlagEnabled(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public FlagEnabled get() {
        return provideInstance(this.debugBuildTypeProvider, this.contextProvider, this.handlerProvider, this.deviceConfigHelperProvider);
    }
}
