// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class HideNotifsForOtherUsersCoordinator_Factory implements Factory<HideNotifsForOtherUsersCoordinator>
{
    private final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider;
    
    public HideNotifsForOtherUsersCoordinator_Factory(final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider) {
        this.lockscreenUserManagerProvider = lockscreenUserManagerProvider;
    }
    
    public static HideNotifsForOtherUsersCoordinator_Factory create(final Provider<NotificationLockscreenUserManager> provider) {
        return new HideNotifsForOtherUsersCoordinator_Factory(provider);
    }
    
    public static HideNotifsForOtherUsersCoordinator provideInstance(final Provider<NotificationLockscreenUserManager> provider) {
        return new HideNotifsForOtherUsersCoordinator(provider.get());
    }
    
    @Override
    public HideNotifsForOtherUsersCoordinator get() {
        return provideInstance(this.lockscreenUserManagerProvider);
    }
}
