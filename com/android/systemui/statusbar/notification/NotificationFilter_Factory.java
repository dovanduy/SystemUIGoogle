// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationFilter_Factory implements Factory<NotificationFilter>
{
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    
    public NotificationFilter_Factory(final Provider<StatusBarStateController> statusBarStateControllerProvider) {
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
    }
    
    public static NotificationFilter_Factory create(final Provider<StatusBarStateController> provider) {
        return new NotificationFilter_Factory(provider);
    }
    
    public static NotificationFilter provideInstance(final Provider<StatusBarStateController> provider) {
        return new NotificationFilter(provider.get());
    }
    
    @Override
    public NotificationFilter get() {
        return provideInstance(this.statusBarStateControllerProvider);
    }
}
