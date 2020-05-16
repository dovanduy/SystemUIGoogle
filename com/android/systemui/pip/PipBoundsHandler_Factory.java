// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PipBoundsHandler_Factory implements Factory<PipBoundsHandler>
{
    private final Provider<Context> contextProvider;
    private final Provider<PipSnapAlgorithm> pipSnapAlgorithmProvider;
    
    public PipBoundsHandler_Factory(final Provider<Context> contextProvider, final Provider<PipSnapAlgorithm> pipSnapAlgorithmProvider) {
        this.contextProvider = contextProvider;
        this.pipSnapAlgorithmProvider = pipSnapAlgorithmProvider;
    }
    
    public static PipBoundsHandler_Factory create(final Provider<Context> provider, final Provider<PipSnapAlgorithm> provider2) {
        return new PipBoundsHandler_Factory(provider, provider2);
    }
    
    public static PipBoundsHandler provideInstance(final Provider<Context> provider, final Provider<PipSnapAlgorithm> provider2) {
        return new PipBoundsHandler(provider.get(), provider2.get());
    }
    
    @Override
    public PipBoundsHandler get() {
        return provideInstance(this.contextProvider, this.pipSnapAlgorithmProvider);
    }
}
