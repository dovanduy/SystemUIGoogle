// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.DarkIconDispatcher;
import android.content.Context;
import com.android.systemui.statusbar.policy.BatteryController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class LightBarController_Factory implements Factory<LightBarController>
{
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<Context> ctxProvider;
    private final Provider<DarkIconDispatcher> darkIconDispatcherProvider;
    
    public LightBarController_Factory(final Provider<Context> ctxProvider, final Provider<DarkIconDispatcher> darkIconDispatcherProvider, final Provider<BatteryController> batteryControllerProvider) {
        this.ctxProvider = ctxProvider;
        this.darkIconDispatcherProvider = darkIconDispatcherProvider;
        this.batteryControllerProvider = batteryControllerProvider;
    }
    
    public static LightBarController_Factory create(final Provider<Context> provider, final Provider<DarkIconDispatcher> provider2, final Provider<BatteryController> provider3) {
        return new LightBarController_Factory(provider, provider2, provider3);
    }
    
    public static LightBarController provideInstance(final Provider<Context> provider, final Provider<DarkIconDispatcher> provider2, final Provider<BatteryController> provider3) {
        return new LightBarController(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public LightBarController get() {
        return provideInstance(this.ctxProvider, this.darkIconDispatcherProvider, this.batteryControllerProvider);
    }
}
