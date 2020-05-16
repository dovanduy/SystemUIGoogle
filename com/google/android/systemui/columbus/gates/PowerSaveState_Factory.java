// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PowerSaveState_Factory implements Factory<PowerSaveState>
{
    private final Provider<Context> contextProvider;
    
    public PowerSaveState_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static PowerSaveState_Factory create(final Provider<Context> provider) {
        return new PowerSaveState_Factory(provider);
    }
    
    public static PowerSaveState provideInstance(final Provider<Context> provider) {
        return new PowerSaveState(provider.get());
    }
    
    @Override
    public PowerSaveState get() {
        return provideInstance(this.contextProvider);
    }
}
