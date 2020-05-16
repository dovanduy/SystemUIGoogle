// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationGroupManager_Factory implements Factory<NotificationGroupManager>
{
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    
    public NotificationGroupManager_Factory(final Provider<StatusBarStateController> statusBarStateControllerProvider) {
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
    }
    
    public static NotificationGroupManager_Factory create(final Provider<StatusBarStateController> provider) {
        return new NotificationGroupManager_Factory(provider);
    }
    
    public static NotificationGroupManager provideInstance(final Provider<StatusBarStateController> provider) {
        return new NotificationGroupManager(provider.get());
    }
    
    @Override
    public NotificationGroupManager get() {
        return provideInstance(this.statusBarStateControllerProvider);
    }
}
