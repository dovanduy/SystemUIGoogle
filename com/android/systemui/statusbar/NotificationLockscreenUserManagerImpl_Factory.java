// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.os.UserManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.os.Handler;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.app.KeyguardManager;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationLockscreenUserManagerImpl_Factory implements Factory<NotificationLockscreenUserManagerImpl>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DevicePolicyManager> devicePolicyManagerProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<IStatusBarService> iStatusBarServiceProvider;
    private final Provider<KeyguardManager> keyguardManagerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<UserManager> userManagerProvider;
    
    public NotificationLockscreenUserManagerImpl_Factory(final Provider<Context> contextProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<DevicePolicyManager> devicePolicyManagerProvider, final Provider<UserManager> userManagerProvider, final Provider<IStatusBarService> iStatusBarServiceProvider, final Provider<KeyguardManager> keyguardManagerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<Handler> mainHandlerProvider, final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider) {
        this.contextProvider = contextProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.devicePolicyManagerProvider = devicePolicyManagerProvider;
        this.userManagerProvider = userManagerProvider;
        this.iStatusBarServiceProvider = iStatusBarServiceProvider;
        this.keyguardManagerProvider = keyguardManagerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.deviceProvisionedControllerProvider = deviceProvisionedControllerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
    }
    
    public static NotificationLockscreenUserManagerImpl_Factory create(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<DevicePolicyManager> provider3, final Provider<UserManager> provider4, final Provider<IStatusBarService> provider5, final Provider<KeyguardManager> provider6, final Provider<StatusBarStateController> provider7, final Provider<Handler> provider8, final Provider<DeviceProvisionedController> provider9, final Provider<KeyguardStateController> provider10) {
        return new NotificationLockscreenUserManagerImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10);
    }
    
    public static NotificationLockscreenUserManagerImpl provideInstance(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<DevicePolicyManager> provider3, final Provider<UserManager> provider4, final Provider<IStatusBarService> provider5, final Provider<KeyguardManager> provider6, final Provider<StatusBarStateController> provider7, final Provider<Handler> provider8, final Provider<DeviceProvisionedController> provider9, final Provider<KeyguardStateController> provider10) {
        return new NotificationLockscreenUserManagerImpl(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get());
    }
    
    @Override
    public NotificationLockscreenUserManagerImpl get() {
        return provideInstance(this.contextProvider, this.broadcastDispatcherProvider, this.devicePolicyManagerProvider, this.userManagerProvider, this.iStatusBarServiceProvider, this.keyguardManagerProvider, this.statusBarStateControllerProvider, this.mainHandlerProvider, this.deviceProvisionedControllerProvider, this.keyguardStateControllerProvider);
    }
}
