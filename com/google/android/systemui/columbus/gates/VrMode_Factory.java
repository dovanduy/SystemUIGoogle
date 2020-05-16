// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class VrMode_Factory implements Factory<VrMode>
{
    private final Provider<Context> contextProvider;
    
    public VrMode_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static VrMode_Factory create(final Provider<Context> provider) {
        return new VrMode_Factory(provider);
    }
    
    public static VrMode provideInstance(final Provider<Context> provider) {
        return new VrMode(provider.get());
    }
    
    @Override
    public VrMode get() {
        return provideInstance(this.contextProvider);
    }
}
