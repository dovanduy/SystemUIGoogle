// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import dagger.internal.Preconditions;
import android.content.Context;
import javax.inject.Provider;
import android.app.NotificationManager;
import dagger.internal.Factory;

public final class SystemServicesModule_ProvideNotificationManagerFactory implements Factory<NotificationManager>
{
    private final Provider<Context> contextProvider;
    
    public SystemServicesModule_ProvideNotificationManagerFactory(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }
    
    public static SystemServicesModule_ProvideNotificationManagerFactory create(final Provider<Context> provider) {
        return new SystemServicesModule_ProvideNotificationManagerFactory(provider);
    }
    
    public static NotificationManager provideInstance(final Provider<Context> provider) {
        return proxyProvideNotificationManager(provider.get());
    }
    
    public static NotificationManager proxyProvideNotificationManager(final Context context) {
        final NotificationManager provideNotificationManager = SystemServicesModule.provideNotificationManager(context);
        Preconditions.checkNotNull(provideNotificationManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationManager;
    }
    
    @Override
    public NotificationManager get() {
        return provideInstance(this.contextProvider);
    }
}
