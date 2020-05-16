// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.os.Handler;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class UsbState_Factory implements Factory<UsbState>
{
    private final Provider<Context> contextProvider;
    private final Provider<Long> gateDurationProvider;
    private final Provider<Handler> handlerProvider;
    
    public UsbState_Factory(final Provider<Context> contextProvider, final Provider<Handler> handlerProvider, final Provider<Long> gateDurationProvider) {
        this.contextProvider = contextProvider;
        this.handlerProvider = handlerProvider;
        this.gateDurationProvider = gateDurationProvider;
    }
    
    public static UsbState_Factory create(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<Long> provider3) {
        return new UsbState_Factory(provider, provider2, provider3);
    }
    
    public static UsbState provideInstance(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<Long> provider3) {
        return new UsbState(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public UsbState get() {
        return provideInstance(this.contextProvider, this.handlerProvider, this.gateDurationProvider);
    }
}
