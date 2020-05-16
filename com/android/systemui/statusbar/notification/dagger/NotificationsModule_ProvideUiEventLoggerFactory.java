// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.dagger;

import dagger.internal.Preconditions;
import com.android.internal.logging.UiEventLogger;
import dagger.internal.Factory;

public final class NotificationsModule_ProvideUiEventLoggerFactory implements Factory<UiEventLogger>
{
    private static final NotificationsModule_ProvideUiEventLoggerFactory INSTANCE;
    
    static {
        INSTANCE = new NotificationsModule_ProvideUiEventLoggerFactory();
    }
    
    public static NotificationsModule_ProvideUiEventLoggerFactory create() {
        return NotificationsModule_ProvideUiEventLoggerFactory.INSTANCE;
    }
    
    public static UiEventLogger provideInstance() {
        return proxyProvideUiEventLogger();
    }
    
    public static UiEventLogger proxyProvideUiEventLogger() {
        final UiEventLogger provideUiEventLogger = NotificationsModule.provideUiEventLogger();
        Preconditions.checkNotNull(provideUiEventLogger, "Cannot return null from a non-@Nullable @Provides method");
        return provideUiEventLogger;
    }
    
    @Override
    public UiEventLogger get() {
        return provideInstance();
    }
}
