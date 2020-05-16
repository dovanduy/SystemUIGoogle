// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PipSnapAlgorithm_Factory implements Factory<PipSnapAlgorithm>
{
    private final Provider<Context> contextProvider;
    
    public PipSnapAlgorithm_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static PipSnapAlgorithm_Factory create(final Provider<Context> provider) {
        return new PipSnapAlgorithm_Factory(provider);
    }
    
    public static PipSnapAlgorithm provideInstance(final Provider<Context> provider) {
        return new PipSnapAlgorithm(provider.get());
    }
    
    @Override
    public PipSnapAlgorithm get() {
        return provideInstance(this.contextProvider);
    }
}
