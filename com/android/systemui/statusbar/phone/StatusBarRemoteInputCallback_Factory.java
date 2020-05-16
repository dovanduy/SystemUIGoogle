// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.plugins.ActivityStarter;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class StatusBarRemoteInputCallback_Factory implements Factory<StatusBarRemoteInputCallback>
{
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<StatusBarKeyguardViewManager> statusBarKeyguardViewManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    
    public StatusBarRemoteInputCallback_Factory(final Provider<Context> contextProvider, final Provider<NotificationGroupManager> groupManagerProvider, final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<StatusBarKeyguardViewManager> statusBarKeyguardViewManagerProvider, final Provider<ActivityStarter> activityStarterProvider, final Provider<ShadeController> shadeControllerProvider, final Provider<CommandQueue> commandQueueProvider) {
        this.contextProvider = contextProvider;
        this.groupManagerProvider = groupManagerProvider;
        this.notificationLockscreenUserManagerProvider = notificationLockscreenUserManagerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.statusBarKeyguardViewManagerProvider = statusBarKeyguardViewManagerProvider;
        this.activityStarterProvider = activityStarterProvider;
        this.shadeControllerProvider = shadeControllerProvider;
        this.commandQueueProvider = commandQueueProvider;
    }
    
    public static StatusBarRemoteInputCallback_Factory create(final Provider<Context> provider, final Provider<NotificationGroupManager> provider2, final Provider<NotificationLockscreenUserManager> provider3, final Provider<KeyguardStateController> provider4, final Provider<StatusBarStateController> provider5, final Provider<StatusBarKeyguardViewManager> provider6, final Provider<ActivityStarter> provider7, final Provider<ShadeController> provider8, final Provider<CommandQueue> provider9) {
        return new StatusBarRemoteInputCallback_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
    }
    
    public static StatusBarRemoteInputCallback provideInstance(final Provider<Context> provider, final Provider<NotificationGroupManager> provider2, final Provider<NotificationLockscreenUserManager> provider3, final Provider<KeyguardStateController> provider4, final Provider<StatusBarStateController> provider5, final Provider<StatusBarKeyguardViewManager> provider6, final Provider<ActivityStarter> provider7, final Provider<ShadeController> provider8, final Provider<CommandQueue> provider9) {
        return new StatusBarRemoteInputCallback(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get());
    }
    
    @Override
    public StatusBarRemoteInputCallback get() {
        return provideInstance(this.contextProvider, this.groupManagerProvider, this.notificationLockscreenUserManagerProvider, this.keyguardStateControllerProvider, this.statusBarStateControllerProvider, this.statusBarKeyguardViewManagerProvider, this.activityStarterProvider, this.shadeControllerProvider, this.commandQueueProvider);
    }
}
