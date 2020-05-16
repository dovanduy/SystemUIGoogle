// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.init;

import com.android.systemui.statusbar.NotificationListener;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationsControllerStub_Factory implements Factory<NotificationsControllerStub>
{
    private final Provider<NotificationListener> notificationListenerProvider;
    
    public NotificationsControllerStub_Factory(final Provider<NotificationListener> notificationListenerProvider) {
        this.notificationListenerProvider = notificationListenerProvider;
    }
    
    public static NotificationsControllerStub_Factory create(final Provider<NotificationListener> provider) {
        return new NotificationsControllerStub_Factory(provider);
    }
    
    public static NotificationsControllerStub provideInstance(final Provider<NotificationListener> provider) {
        return new NotificationsControllerStub(provider.get());
    }
    
    @Override
    public NotificationsControllerStub get() {
        return provideInstance(this.notificationListenerProvider);
    }
}
