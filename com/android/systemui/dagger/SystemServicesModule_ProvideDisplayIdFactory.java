// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideDisplayIdFactory implements Factory<Integer>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideDisplayIdFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideDisplayIdFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideDisplayIdFactory(provider);
    }
    
    public static Integer provideInstance(final Provider<Context> provider) {
        return proxyProvideDisplayId(provider.get());
    }
    
    public static int proxyProvideDisplayId(final Context context) {
        return SystemServicesModule.provideDisplayId(context);
    }
    
    @Override
    public Integer get() {
        return provideInstance(this.contextProvider);
    }
}
