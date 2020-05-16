// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.statusbar.phone;

import dagger.internal.Preconditions;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.policy.BatteryController;
import javax.inject.Provider;
import com.google.android.systemui.reversecharging.ReverseChargingWindowController;
import java.util.Optional;
import dagger.internal.Factory;

public final class StatusBarGoogleModule_ProvideReverseChargingWindowControllerOptionalFactory implements Factory<Optional<ReverseChargingWindowController>>
{
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<ReverseChargingWindowController> reverseChargingWindowControllerLazyProvider;
    
    public StatusBarGoogleModule_ProvideReverseChargingWindowControllerOptionalFactory(final Provider<BatteryController> batteryControllerProvider, final Provider<ReverseChargingWindowController> reverseChargingWindowControllerLazyProvider) {
        this.batteryControllerProvider = batteryControllerProvider;
        this.reverseChargingWindowControllerLazyProvider = reverseChargingWindowControllerLazyProvider;
    }
    
    public static StatusBarGoogleModule_ProvideReverseChargingWindowControllerOptionalFactory create(final Provider<BatteryController> provider, final Provider<ReverseChargingWindowController> provider2) {
        return new StatusBarGoogleModule_ProvideReverseChargingWindowControllerOptionalFactory(provider, provider2);
    }
    
    public static Optional<ReverseChargingWindowController> provideInstance(final Provider<BatteryController> provider, final Provider<ReverseChargingWindowController> provider2) {
        return proxyProvideReverseChargingWindowControllerOptional(provider.get(), DoubleCheck.lazy(provider2));
    }
    
    public static Optional<ReverseChargingWindowController> proxyProvideReverseChargingWindowControllerOptional(final BatteryController batteryController, final Lazy<ReverseChargingWindowController> lazy) {
        final Optional<ReverseChargingWindowController> provideReverseChargingWindowControllerOptional = StatusBarGoogleModule.provideReverseChargingWindowControllerOptional(batteryController, lazy);
        Preconditions.checkNotNull(provideReverseChargingWindowControllerOptional, "Cannot return null from a non-@Nullable @Provides method");
        return provideReverseChargingWindowControllerOptional;
    }
    
    @Override
    public Optional<ReverseChargingWindowController> get() {
        return provideInstance(this.batteryControllerProvider, this.reverseChargingWindowControllerLazyProvider);
    }
}
