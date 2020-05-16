// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.app.INotificationManager;
import dagger.internal.Factory;

public final class DependencyProvider_ProvideINotificationManagerFactory implements Factory<INotificationManager>
{
    private final DependencyProvider module;
    
    public DependencyProvider_ProvideINotificationManagerFactory(final DependencyProvider module) {
        this.module = module;
    }
    
    public static DependencyProvider_ProvideINotificationManagerFactory create(final DependencyProvider dependencyProvider) {
        return new DependencyProvider_ProvideINotificationManagerFactory(dependencyProvider);
    }
    
    public static INotificationManager provideInstance(final DependencyProvider dependencyProvider) {
        return proxyProvideINotificationManager(dependencyProvider);
    }
    
    public static INotificationManager proxyProvideINotificationManager(final DependencyProvider dependencyProvider) {
        final INotificationManager provideINotificationManager = dependencyProvider.provideINotificationManager();
        Preconditions.checkNotNull(provideINotificationManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideINotificationManager;
    }
    
    @Override
    public INotificationManager get() {
        return provideInstance(this.module);
    }
}
