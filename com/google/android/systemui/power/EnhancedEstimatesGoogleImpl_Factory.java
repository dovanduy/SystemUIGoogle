// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.power;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class EnhancedEstimatesGoogleImpl_Factory implements Factory<EnhancedEstimatesGoogleImpl>
{
    private final Provider<Context> contextProvider;
    
    public EnhancedEstimatesGoogleImpl_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static EnhancedEstimatesGoogleImpl_Factory create(final Provider<Context> provider) {
        return new EnhancedEstimatesGoogleImpl_Factory(provider);
    }
    
    public static EnhancedEstimatesGoogleImpl provideInstance(final Provider<Context> provider) {
        return new EnhancedEstimatesGoogleImpl(provider.get());
    }
    
    @Override
    public EnhancedEstimatesGoogleImpl get() {
        return provideInstance(this.contextProvider);
    }
}
