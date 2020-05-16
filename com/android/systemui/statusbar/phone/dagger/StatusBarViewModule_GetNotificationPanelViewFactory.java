// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.statusbar.phone.NotificationShadeWindowView;
import javax.inject.Provider;
import com.android.systemui.statusbar.phone.NotificationPanelView;
import dagger.internal.Factory;

public final class StatusBarViewModule_GetNotificationPanelViewFactory implements Factory<NotificationPanelView>
{
    private final Provider<NotificationShadeWindowView> notificationShadeWindowViewProvider;
    
    public StatusBarViewModule_GetNotificationPanelViewFactory(final Provider<NotificationShadeWindowView> notificationShadeWindowViewProvider) {
        this.notificationShadeWindowViewProvider = notificationShadeWindowViewProvider;
    }
    
    public static StatusBarViewModule_GetNotificationPanelViewFactory create(final Provider<NotificationShadeWindowView> provider) {
        return new StatusBarViewModule_GetNotificationPanelViewFactory(provider);
    }
    
    public static NotificationPanelView provideInstance(final Provider<NotificationShadeWindowView> provider) {
        return proxyGetNotificationPanelView(provider.get());
    }
    
    public static NotificationPanelView proxyGetNotificationPanelView(final NotificationShadeWindowView notificationShadeWindowView) {
        final NotificationPanelView notificationPanelView = StatusBarViewModule.getNotificationPanelView(notificationShadeWindowView);
        Preconditions.checkNotNull(notificationPanelView, "Cannot return null from a non-@Nullable @Provides method");
        return notificationPanelView;
    }
    
    @Override
    public NotificationPanelView get() {
        return provideInstance(this.notificationShadeWindowViewProvider);
    }
}
