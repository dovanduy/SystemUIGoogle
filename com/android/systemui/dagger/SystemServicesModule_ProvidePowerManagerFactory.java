// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.os.PowerManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvidePowerManagerFactory implements Factory<PowerManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvidePowerManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvidePowerManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvidePowerManagerFactory(provider);
    }
    
    public static PowerManager provideInstance(final Provider<Context> provider) {
        return proxyProvidePowerManager(provider.get());
    }
    
    public static PowerManager proxyProvidePowerManager(final Context context) {
        final PowerManager providePowerManager = SystemServicesModule.providePowerManager(context);
        Preconditions.checkNotNull(providePowerManager, "Cannot return null from a non-@Nullable @Provides method");
        return providePowerManager;
    }
    
    @Override
    public PowerManager get() {
        return provideInstance(this.contextProvider);
    }
}
