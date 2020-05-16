// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.statusbar.notification.logging.NotificationPanelLogger;
import dagger.internal.Factory;

public final class NotificationsModule_ProvideNotificationPanelLoggerFactory implements Factory<NotificationPanelLogger>
{
    private static final NotificationsModule_ProvideNotificationPanelLoggerFactory INSTANCE;
    
    static {
        INSTANCE = new NotificationsModule_ProvideNotificationPanelLoggerFactory();
    }
    
    public static NotificationsModule_ProvideNotificationPanelLoggerFactory create() {
        return NotificationsModule_ProvideNotificationPanelLoggerFactory.INSTANCE;
    }
    
    public static NotificationPanelLogger provideInstance() {
        return proxyProvideNotificationPanelLogger();
    }
    
    public static NotificationPanelLogger proxyProvideNotificationPanelLogger() {
        final NotificationPanelLogger provideNotificationPanelLogger = NotificationsModule.provideNotificationPanelLogger();
        Preconditions.checkNotNull(provideNotificationPanelLogger, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationPanelLogger;
    }
    
    @Override
    public NotificationPanelLogger get() {
        return provideInstance();
    }
}
