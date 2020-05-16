// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.accessibility;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SystemActions_Factory implements Factory<SystemActions>
{
    private final Provider<Context> contextProvider;
    
    public SystemActions_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemActions_Factory create(final Provider<Context> provider) {
        return new SystemActions_Factory(provider);
    }
    
    public static SystemActions provideInstance(final Provider<Context> provider) {
        return new SystemActions(provider.get());
    }
    
    @Override
    public SystemActions get() {
        return provideInstance(this.contextProvider);
    }
}
