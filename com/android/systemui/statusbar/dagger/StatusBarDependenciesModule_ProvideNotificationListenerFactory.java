// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.dagger;

import dagger.internal.Preconditions;
import android.app.NotificationManager;
import android.os.Handler;
import android.content.Context;
import javax.inject.Provider;
import com.android.systemui.statusbar.NotificationListener;
import dagger.internal.Factory;

public final class StatusBarDependenciesModule_ProvideNotificationListenerFactory implements Factory<NotificationListener>
{
    private final Provider<Context> contextProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<NotificationManager> notificationManagerProvider;
    
    public StatusBarDependenciesModule_ProvideNotificationListenerFactory(final Provider<Context> contextProvider, final Provider<NotificationManager> notificationManagerProvider, final Provider<Handler> mainHandlerProvider) {
        this.contextProvider = contextProvider;
        this.notificationManagerProvider = notificationManagerProvider;
        this.mainHandlerProvider = mainHandlerProvider;
    }
    
    public static StatusBarDependenciesModule_ProvideNotificationListenerFactory create(final Provider<Context> provider, final Provider<NotificationManager> provider2, final Provider<Handler> provider3) {
        return new StatusBarDependenciesModule_ProvideNotificationListenerFactory(provider, provider2, provider3);
    }
    
    public static NotificationListener provideInstance(final Provider<Context> provider, final Provider<NotificationManager> provider2, final Provider<Handler> provider3) {
        return proxyProvideNotificationListener(provider.get(), provider2.get(), provider3.get());
    }
    
    public static NotificationListener proxyProvideNotificationListener(final Context context, final NotificationManager notificationManager, final Handler handler) {
        final NotificationListener provideNotificationListener = StatusBarDependenciesModule.provideNotificationListener(context, notificationManager, handler);
        Preconditions.checkNotNull(provideNotificationListener, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationListener;
    }
    
    @Override
    public NotificationListener get() {
        return provideInstance(this.contextProvider, this.notificationManagerProvider, this.mainHandlerProvider);
    }
}
