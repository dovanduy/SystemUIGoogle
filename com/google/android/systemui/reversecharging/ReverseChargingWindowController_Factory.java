// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.reversecharging;

import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.phone.StatusBar;
import android.content.Context;
import com.android.systemui.statusbar.policy.BatteryController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ReverseChargingWindowController_Factory implements Factory<ReverseChargingWindowController>
{
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<StatusBar> statusBarLazyProvider;
    
    public ReverseChargingWindowController_Factory(final Provider<Context> contextProvider, final Provider<BatteryController> batteryControllerProvider, final Provider<StatusBar> statusBarLazyProvider) {
        this.contextProvider = contextProvider;
        this.batteryControllerProvider = batteryControllerProvider;
        this.statusBarLazyProvider = statusBarLazyProvider;
    }
    
    public static ReverseChargingWindowController_Factory create(final Provider<Context> provider, final Provider<BatteryController> provider2, final Provider<StatusBar> provider3) {
        return new ReverseChargingWindowController_Factory(provider, provider2, provider3);
    }
    
    public static ReverseChargingWindowController provideInstance(final Provider<Context> provider, final Provider<BatteryController> provider2, final Provider<StatusBar> provider3) {
        return new ReverseChargingWindowController(provider.get(), provider2.get(), DoubleCheck.lazy(provider3));
    }
    
    @Override
    public ReverseChargingWindowController get() {
        return provideInstance(this.contextProvider, this.batteryControllerProvider, this.statusBarLazyProvider);
    }
}
