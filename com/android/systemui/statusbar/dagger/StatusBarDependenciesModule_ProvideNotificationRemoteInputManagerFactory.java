// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.dagger;

import dagger.internal.Preconditions;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import android.os.Handler;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import android.content.Context;
import javax.inject.Provider;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import dagger.internal.Factory;

public final class StatusBarDependenciesModule_ProvideNotificationRemoteInputManagerFactory implements Factory<NotificationRemoteInputManager>
{
    private final Provider<Context> contextProvider;
    private final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<RemoteInputUriController> remoteInputUriControllerProvider;
    private final Provider<SmartReplyController> smartReplyControllerProvider;
    private final Provider<StatusBar> statusBarLazyProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    
    public StatusBarDependenciesModule_ProvideNotificationRemoteInputManagerFactory(final Provider<Context> contextProvider, final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider, final Provider<SmartReplyController> smartReplyControllerProvider, final Provider<NotificationEntryManager> notificationEntryManagerProvider, final Provider<StatusBar> statusBarLazyProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<Handler> mainHandlerProvider, final Provider<RemoteInputUriController> remoteInputUriControllerProvider) {
        this.contextProvider = contextProvider;
        this.lockscreenUserManagerProvider = lockscreenUserManagerProvider;
        this.smartReplyControllerProvider = smartReplyControllerProvider;
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.statusBarLazyProvider = statusBarLazyProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.remoteInputUriControllerProvider = remoteInputUriControllerProvider;
    }
    
    public static StatusBarDependenciesModule_ProvideNotificationRemoteInputManagerFactory create(final Provider<Context> provider, final Provider<NotificationLockscreenUserManager> provider2, final Provider<SmartReplyController> provider3, final Provider<NotificationEntryManager> provider4, final Provider<StatusBar> provider5, final Provider<StatusBarStateController> provider6, final Provider<Handler> provider7, final Provider<RemoteInputUriController> provider8) {
        return new StatusBarDependenciesModule_ProvideNotificationRemoteInputManagerFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
    }
    
    public static NotificationRemoteInputManager provideInstance(final Provider<Context> provider, final Provider<NotificationLockscreenUserManager> provider2, final Provider<SmartReplyController> provider3, final Provider<NotificationEntryManager> provider4, final Provider<StatusBar> provider5, final Provider<StatusBarStateController> provider6, final Provider<Handler> provider7, final Provider<RemoteInputUriController> provider8) {
        return proxyProvideNotificationRemoteInputManager(provider.get(), provider2.get(), provider3.get(), provider4.get(), DoubleCheck.lazy(provider5), provider6.get(), provider7.get(), provider8.get());
    }
    
    public static NotificationRemoteInputManager proxyProvideNotificationRemoteInputManager(final Context context, final NotificationLockscreenUserManager notificationLockscreenUserManager, final SmartReplyController smartReplyController, final NotificationEntryManager notificationEntryManager, final Lazy<StatusBar> lazy, final StatusBarStateController statusBarStateController, final Handler handler, final RemoteInputUriController remoteInputUriController) {
        final NotificationRemoteInputManager provideNotificationRemoteInputManager = StatusBarDependenciesModule.provideNotificationRemoteInputManager(context, notificationLockscreenUserManager, smartReplyController, notificationEntryManager, lazy, statusBarStateController, handler, remoteInputUriController);
        Preconditions.checkNotNull(provideNotificationRemoteInputManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationRemoteInputManager;
    }
    
    @Override
    public NotificationRemoteInputManager get() {
        return provideInstance(this.contextProvider, this.lockscreenUserManagerProvider, this.smartReplyControllerProvider, this.notificationEntryManagerProvider, this.statusBarLazyProvider, this.statusBarStateControllerProvider, this.mainHandlerProvider, this.remoteInputUriControllerProvider);
    }
}
