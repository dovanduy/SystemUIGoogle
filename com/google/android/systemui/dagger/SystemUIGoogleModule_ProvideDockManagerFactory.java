// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import javax.inject.Provider;
import com.android.systemui.dock.DockManager;
import dagger.internal.Factory;

public final class SystemUIGoogleModule_ProvideDockManagerFactory implements Factory<DockManager>
{
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NotificationInterruptStateProvider> notificationInterruptionStateProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    
    public SystemUIGoogleModule_ProvideDockManagerFactory(final Provider<Context> contextProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<NotificationInterruptStateProvider> notificationInterruptionStateProvider) {
        this.contextProvider = contextProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.notificationInterruptionStateProvider = notificationInterruptionStateProvider;
    }
    
    public static SystemUIGoogleModule_ProvideDockManagerFactory create(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<StatusBarStateController> provider3, final Provider<NotificationInterruptStateProvider> provider4) {
        return new SystemUIGoogleModule_ProvideDockManagerFactory(provider, provider2, provider3, provider4);
    }
    
    public static DockManager provideInstance(final Provider<Context> provider, final Provider<BroadcastDispatcher> provider2, final Provider<StatusBarStateController> provider3, final Provider<NotificationInterruptStateProvider> provider4) {
        return proxyProvideDockManager(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    public static DockManager proxyProvideDockManager(final Context context, final BroadcastDispatcher broadcastDispatcher, final StatusBarStateController statusBarStateController, final NotificationInterruptStateProvider notificationInterruptStateProvider) {
        final DockManager provideDockManager = SystemUIGoogleModule.provideDockManager(context, broadcastDispatcher, statusBarStateController, notificationInterruptStateProvider);
        Preconditions.checkNotNull(provideDockManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideDockManager;
    }
    
    @Override
    public DockManager get() {
        return provideInstance(this.contextProvider, this.broadcastDispatcherProvider, this.statusBarStateControllerProvider, this.notificationInterruptionStateProvider);
    }
}
