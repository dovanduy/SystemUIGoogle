// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.dagger;

import dagger.internal.Preconditions;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.MediaArtworkProcessor;
import java.util.concurrent.Executor;
import com.android.keyguard.KeyguardMediaPlayer;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.util.DeviceConfigProxy;
import android.content.Context;
import javax.inject.Provider;
import com.android.systemui.statusbar.NotificationMediaManager;
import dagger.internal.Factory;

public final class StatusBarDependenciesModule_ProvideNotificationMediaManagerFactory implements Factory<NotificationMediaManager>
{
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigProxy> deviceConfigProxyProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<KeyguardMediaPlayer> keyguardMediaPlayerProvider;
    private final Provider<Executor> mainExecutorProvider;
    private final Provider<MediaArtworkProcessor> mediaArtworkProcessorProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<StatusBar> statusBarLazyProvider;
    
    public StatusBarDependenciesModule_ProvideNotificationMediaManagerFactory(final Provider<Context> contextProvider, final Provider<StatusBar> statusBarLazyProvider, final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider, final Provider<NotificationEntryManager> notificationEntryManagerProvider, final Provider<MediaArtworkProcessor> mediaArtworkProcessorProvider, final Provider<KeyguardBypassController> keyguardBypassControllerProvider, final Provider<KeyguardMediaPlayer> keyguardMediaPlayerProvider, final Provider<Executor> mainExecutorProvider, final Provider<DeviceConfigProxy> deviceConfigProxyProvider) {
        this.contextProvider = contextProvider;
        this.statusBarLazyProvider = statusBarLazyProvider;
        this.notificationShadeWindowControllerProvider = notificationShadeWindowControllerProvider;
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.mediaArtworkProcessorProvider = mediaArtworkProcessorProvider;
        this.keyguardBypassControllerProvider = keyguardBypassControllerProvider;
        this.keyguardMediaPlayerProvider = keyguardMediaPlayerProvider;
        this.mainExecutorProvider = mainExecutorProvider;
        this.deviceConfigProxyProvider = deviceConfigProxyProvider;
    }
    
    public static StatusBarDependenciesModule_ProvideNotificationMediaManagerFactory create(final Provider<Context> provider, final Provider<StatusBar> provider2, final Provider<NotificationShadeWindowController> provider3, final Provider<NotificationEntryManager> provider4, final Provider<MediaArtworkProcessor> provider5, final Provider<KeyguardBypassController> provider6, final Provider<KeyguardMediaPlayer> provider7, final Provider<Executor> provider8, final Provider<DeviceConfigProxy> provider9) {
        return new StatusBarDependenciesModule_ProvideNotificationMediaManagerFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
    }
    
    public static NotificationMediaManager provideInstance(final Provider<Context> provider, final Provider<StatusBar> provider2, final Provider<NotificationShadeWindowController> provider3, final Provider<NotificationEntryManager> provider4, final Provider<MediaArtworkProcessor> provider5, final Provider<KeyguardBypassController> provider6, final Provider<KeyguardMediaPlayer> provider7, final Provider<Executor> provider8, final Provider<DeviceConfigProxy> provider9) {
        return proxyProvideNotificationMediaManager(provider.get(), DoubleCheck.lazy(provider2), DoubleCheck.lazy(provider3), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get());
    }
    
    public static NotificationMediaManager proxyProvideNotificationMediaManager(final Context context, final Lazy<StatusBar> lazy, final Lazy<NotificationShadeWindowController> lazy2, final NotificationEntryManager notificationEntryManager, final MediaArtworkProcessor mediaArtworkProcessor, final KeyguardBypassController keyguardBypassController, final KeyguardMediaPlayer keyguardMediaPlayer, final Executor executor, final DeviceConfigProxy deviceConfigProxy) {
        final NotificationMediaManager provideNotificationMediaManager = StatusBarDependenciesModule.provideNotificationMediaManager(context, lazy, lazy2, notificationEntryManager, mediaArtworkProcessor, keyguardBypassController, keyguardMediaPlayer, executor, deviceConfigProxy);
        Preconditions.checkNotNull(provideNotificationMediaManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationMediaManager;
    }
    
    @Override
    public NotificationMediaManager get() {
        return provideInstance(this.contextProvider, this.statusBarLazyProvider, this.notificationShadeWindowControllerProvider, this.notificationEntryManagerProvider, this.mediaArtworkProcessorProvider, this.keyguardBypassControllerProvider, this.keyguardMediaPlayerProvider, this.mainExecutorProvider, this.deviceConfigProxyProvider);
    }
}
