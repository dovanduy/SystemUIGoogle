// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import javax.inject.Provider;
import dagger.internal.Factory;

public final class TunablePadding_TunablePaddingService_Factory implements Factory<TunablePadding.TunablePaddingService>
{
    private final Provider<TunerService> tunerServiceProvider;
    
    public TunablePadding_TunablePaddingService_Factory(final Provider<TunerService> tunerServiceProvider) {
        this.tunerServiceProvider = tunerServiceProvider;
    }
    
    public static TunablePadding_TunablePaddingService_Factory create(final Provider<TunerService> provider) {
        return new TunablePadding_TunablePaddingService_Factory(provider);
    }
    
    public static TunablePadding.TunablePaddingService provideInstance(final Provider<TunerService> provider) {
        return new TunablePadding.TunablePaddingService(provider.get());
    }
    
    @Override
    public TunablePadding.TunablePaddingService get() {
        return provideInstance(this.tunerServiceProvider);
    }
}
