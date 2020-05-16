// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DynamicPrivacyController_Factory implements Factory<DynamicPrivacyController>
{
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider;
    private final Provider<StatusBarStateController> stateControllerProvider;
    
    public DynamicPrivacyController_Factory(final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<StatusBarStateController> stateControllerProvider) {
        this.notificationLockscreenUserManagerProvider = notificationLockscreenUserManagerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.stateControllerProvider = stateControllerProvider;
    }
    
    public static DynamicPrivacyController_Factory create(final Provider<NotificationLockscreenUserManager> provider, final Provider<KeyguardStateController> provider2, final Provider<StatusBarStateController> provider3) {
        return new DynamicPrivacyController_Factory(provider, provider2, provider3);
    }
    
    public static DynamicPrivacyController provideInstance(final Provider<NotificationLockscreenUserManager> provider, final Provider<KeyguardStateController> provider2, final Provider<StatusBarStateController> provider3) {
        return new DynamicPrivacyController(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public DynamicPrivacyController get() {
        return provideInstance(this.notificationLockscreenUserManagerProvider, this.keyguardStateControllerProvider, this.stateControllerProvider);
    }
}
