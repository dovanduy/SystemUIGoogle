// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.content.pm.LauncherApps;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideLauncherAppsFactory implements Factory<LauncherApps>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideLauncherAppsFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideLauncherAppsFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideLauncherAppsFactory(provider);
    }
    
    public static LauncherApps provideInstance(final Provider<Context> provider) {
        return proxyProvideLauncherApps(provider.get());
    }
    
    public static LauncherApps proxyProvideLauncherApps(final Context context) {
        final LauncherApps provideLauncherApps = SystemServicesModule.provideLauncherApps(context);
        Preconditions.checkNotNull(provideLauncherApps, "Cannot return null from a non-@Nullable @Provides method");
        return provideLauncherApps;
    }
    
    @Override
    public LauncherApps get() {
        return provideInstance(this.contextProvider);
    }
}
