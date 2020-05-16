// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.app.ActivityManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideActivityManagerFactory implements Factory<ActivityManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideActivityManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideActivityManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideActivityManagerFactory(provider);
    }
    
    public static ActivityManager provideInstance(final Provider<Context> provider) {
        return proxyProvideActivityManager(provider.get());
    }
    
    public static ActivityManager proxyProvideActivityManager(final Context context) {
        final ActivityManager provideActivityManager = SystemServicesModule.provideActivityManager(context);
        Preconditions.checkNotNull(provideActivityManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideActivityManager;
    }
    
    @Override
    public ActivityManager get() {
        return provideInstance(this.contextProvider);
    }
}
