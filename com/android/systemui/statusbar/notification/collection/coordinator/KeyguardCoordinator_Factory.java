// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.os.Handler;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class KeyguardCoordinator_Factory implements Factory<KeyguardCoordinator>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<HighPriorityProvider> highPriorityProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider;
    private final Provider<Handler> mainThreadHandlerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    
    public KeyguardCoordinator_Factory(final Provider<Context> contextProvider, final Provider<Handler> mainThreadHandlerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<HighPriorityProvider> highPriorityProvider) {
        this.contextProvider = contextProvider;
        this.mainThreadHandlerProvider = mainThreadHandlerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.lockscreenUserManagerProvider = lockscreenUserManagerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.highPriorityProvider = highPriorityProvider;
    }
    
    public static KeyguardCoordinator_Factory create(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<KeyguardStateController> provider3, final Provider<NotificationLockscreenUserManager> provider4, final Provider<BroadcastDispatcher> provider5, final Provider<StatusBarStateController> provider6, final Provider<KeyguardUpdateMonitor> provider7, final Provider<HighPriorityProvider> provider8) {
        return new KeyguardCoordinator_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
    }
    
    public static KeyguardCoordinator provideInstance(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<KeyguardStateController> provider3, final Provider<NotificationLockscreenUserManager> provider4, final Provider<BroadcastDispatcher> provider5, final Provider<StatusBarStateController> provider6, final Provider<KeyguardUpdateMonitor> provider7, final Provider<HighPriorityProvider> provider8) {
        return new KeyguardCoordinator(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get());
    }
    
    @Override
    public KeyguardCoordinator get() {
        return provideInstance(this.contextProvider, this.mainThreadHandlerProvider, this.keyguardStateControllerProvider, this.lockscreenUserManagerProvider, this.broadcastDispatcherProvider, this.statusBarStateControllerProvider, this.keyguardUpdateMonitorProvider, this.highPriorityProvider);
    }
}
