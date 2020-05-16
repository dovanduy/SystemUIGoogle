// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PipSurfaceTransactionHelper_Factory implements Factory<PipSurfaceTransactionHelper>
{
    private final Provider<Context> contextProvider;
    
    public PipSurfaceTransactionHelper_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static PipSurfaceTransactionHelper_Factory create(final Provider<Context> provider) {
        return new PipSurfaceTransactionHelper_Factory(provider);
    }
    
    public static PipSurfaceTransactionHelper provideInstance(final Provider<Context> provider) {
        return new PipSurfaceTransactionHelper(provider.get());
    }
    
    @Override
    public PipSurfaceTransactionHelper get() {
        return provideInstance(this.contextProvider);
    }
}
