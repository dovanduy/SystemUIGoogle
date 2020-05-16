// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.dagger;

import dagger.internal.Preconditions;
import java.util.concurrent.Executor;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.logging.NotificationPanelLogger;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import javax.inject.Provider;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import dagger.internal.Factory;

public final class NotificationsModule_ProvideNotificationLoggerFactory implements Factory<NotificationLogger>
{
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<NotificationLogger.ExpansionStateLogger> expansionStateLoggerProvider;
    private final Provider<NotificationListener> notificationListenerProvider;
    private final Provider<NotificationPanelLogger> notificationPanelLoggerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<Executor> uiBgExecutorProvider;
    
    public NotificationsModule_ProvideNotificationLoggerFactory(final Provider<NotificationListener> notificationListenerProvider, final Provider<Executor> uiBgExecutorProvider, final Provider<NotificationEntryManager> entryManagerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<NotificationLogger.ExpansionStateLogger> expansionStateLoggerProvider, final Provider<NotificationPanelLogger> notificationPanelLoggerProvider) {
        this.notificationListenerProvider = notificationListenerProvider;
        this.uiBgExecutorProvider = uiBgExecutorProvider;
        this.entryManagerProvider = entryManagerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.expansionStateLoggerProvider = expansionStateLoggerProvider;
        this.notificationPanelLoggerProvider = notificationPanelLoggerProvider;
    }
    
    public static NotificationsModule_ProvideNotificationLoggerFactory create(final Provider<NotificationListener> provider, final Provider<Executor> provider2, final Provider<NotificationEntryManager> provider3, final Provider<StatusBarStateController> provider4, final Provider<NotificationLogger.ExpansionStateLogger> provider5, final Provider<NotificationPanelLogger> provider6) {
        return new NotificationsModule_ProvideNotificationLoggerFactory(provider, provider2, provider3, provider4, provider5, provider6);
    }
    
    public static NotificationLogger provideInstance(final Provider<NotificationListener> provider, final Provider<Executor> provider2, final Provider<NotificationEntryManager> provider3, final Provider<StatusBarStateController> provider4, final Provider<NotificationLogger.ExpansionStateLogger> provider5, final Provider<NotificationPanelLogger> provider6) {
        return proxyProvideNotificationLogger(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get());
    }
    
    public static NotificationLogger proxyProvideNotificationLogger(final NotificationListener notificationListener, final Executor executor, final NotificationEntryManager notificationEntryManager, final StatusBarStateController statusBarStateController, final NotificationLogger.ExpansionStateLogger expansionStateLogger, final NotificationPanelLogger notificationPanelLogger) {
        final NotificationLogger provideNotificationLogger = NotificationsModule.provideNotificationLogger(notificationListener, executor, notificationEntryManager, statusBarStateController, expansionStateLogger, notificationPanelLogger);
        Preconditions.checkNotNull(provideNotificationLogger, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationLogger;
    }
    
    @Override
    public NotificationLogger get() {
        return provideInstance(this.notificationListenerProvider, this.uiBgExecutorProvider, this.entryManagerProvider, this.statusBarStateControllerProvider, this.expansionStateLoggerProvider, this.notificationPanelLoggerProvider);
    }
}
