// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.content.res.Resources;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideResourcesFactory implements Factory<Resources>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideResourcesFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideResourcesFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideResourcesFactory(provider);
    }
    
    public static Resources provideInstance(final Provider<Context> provider) {
        return proxyProvideResources(provider.get());
    }
    
    public static Resources proxyProvideResources(final Context context) {
        final Resources provideResources = SystemServicesModule.provideResources(context);
        Preconditions.checkNotNull(provideResources, "Cannot return null from a non-@Nullable @Provides method");
        return provideResources;
    }
    
    @Override
    public Resources get() {
        return provideInstance(this.contextProvider);
    }
}
