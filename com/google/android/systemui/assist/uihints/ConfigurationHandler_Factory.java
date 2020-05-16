// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ConfigurationHandler_Factory implements Factory<ConfigurationHandler>
{
    private final Provider<Context> contextProvider;
    
    public ConfigurationHandler_Factory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static ConfigurationHandler_Factory create(final Provider<Context> provider) {
        return new ConfigurationHandler_Factory(provider);
    }
    
    public static ConfigurationHandler provideInstance(final Provider<Context> provider) {
        return new ConfigurationHandler(provider.get());
    }
    
    @Override
    public ConfigurationHandler get() {
        return provideInstance(this.contextProvider);
    }
}
