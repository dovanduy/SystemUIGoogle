// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.internal.logging.MetricsLogger;
import android.content.Context;
import javax.inject.Provider;
import com.android.systemui.statusbar.notification.row.NotificationBlockingHelperManager;
import dagger.internal.Factory;

public final class NotificationsModule_ProvideNotificationBlockingHelperManagerFactory implements Factory<NotificationBlockingHelperManager>
{
    private final Provider<Context> contextProvider;
    private final Provider<MetricsLogger> metricsLoggerProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationGutsManager> notificationGutsManagerProvider;
    
    public NotificationsModule_ProvideNotificationBlockingHelperManagerFactory(final Provider<Context> contextProvider, final Provider<NotificationGutsManager> notificationGutsManagerProvider, final Provider<NotificationEntryManager> notificationEntryManagerProvider, final Provider<MetricsLogger> metricsLoggerProvider) {
        this.contextProvider = contextProvider;
        this.notificationGutsManagerProvider = notificationGutsManagerProvider;
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.metricsLoggerProvider = metricsLoggerProvider;
    }
    
    public static NotificationsModule_ProvideNotificationBlockingHelperManagerFactory create(final Provider<Context> provider, final Provider<NotificationGutsManager> provider2, final Provider<NotificationEntryManager> provider3, final Provider<MetricsLogger> provider4) {
        return new NotificationsModule_ProvideNotificationBlockingHelperManagerFactory(provider, provider2, provider3, provider4);
    }
    
    public static NotificationBlockingHelperManager provideInstance(final Provider<Context> provider, final Provider<NotificationGutsManager> provider2, final Provider<NotificationEntryManager> provider3, final Provider<MetricsLogger> provider4) {
        return proxyProvideNotificationBlockingHelperManager(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    public static NotificationBlockingHelperManager proxyProvideNotificationBlockingHelperManager(final Context context, final NotificationGutsManager notificationGutsManager, final NotificationEntryManager notificationEntryManager, final MetricsLogger metricsLogger) {
        final NotificationBlockingHelperManager provideNotificationBlockingHelperManager = NotificationsModule.provideNotificationBlockingHelperManager(context, notificationGutsManager, notificationEntryManager, metricsLogger);
        Preconditions.checkNotNull(provideNotificationBlockingHelperManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationBlockingHelperManager;
    }
    
    @Override
    public NotificationBlockingHelperManager get() {
        return provideInstance(this.contextProvider, this.notificationGutsManagerProvider, this.notificationEntryManagerProvider, this.metricsLoggerProvider);
    }
}
