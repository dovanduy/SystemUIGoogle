// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.os.Handler;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ChargingState_Factory implements Factory<ChargingState>
{
    private final Provider<Context> contextProvider;
    private final Provider<Long> gateDurationProvider;
    private final Provider<Handler> handlerProvider;
    
    public ChargingState_Factory(final Provider<Context> contextProvider, final Provider<Handler> handlerProvider, final Provider<Long> gateDurationProvider) {
        this.contextProvider = contextProvider;
        this.handlerProvider = handlerProvider;
        this.gateDurationProvider = gateDurationProvider;
    }
    
    public static ChargingState_Factory create(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<Long> provider3) {
        return new ChargingState_Factory(provider, provider2, provider3);
    }
    
    public static ChargingState provideInstance(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<Long> provider3) {
        return new ChargingState(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public ChargingState get() {
        return provideInstance(this.contextProvider, this.handlerProvider, this.gateDurationProvider);
    }
}
