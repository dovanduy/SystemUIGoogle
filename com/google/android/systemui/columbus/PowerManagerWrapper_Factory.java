// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PowerManagerWrapper_Factory implements Factory<PowerManagerWrapper>
{
    private final Provider<Context> contextProvider;
    
    public PowerManagerWrapper_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static PowerManagerWrapper_Factory create(final Provider<Context> provider) {
        return new PowerManagerWrapper_Factory(provider);
    }
    
    public static PowerManagerWrapper provideInstance(final Provider<Context> provider) {
        return new PowerManagerWrapper(provider.get());
    }
    
    @Override
    public PowerManagerWrapper get() {
        return provideInstance(this.contextProvider);
    }
}
