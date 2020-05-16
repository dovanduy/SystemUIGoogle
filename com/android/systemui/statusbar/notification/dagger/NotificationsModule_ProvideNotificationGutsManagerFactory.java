// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.dagger;

import dagger.internal.Preconditions;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.phone.StatusBar;
import android.content.pm.ShortcutManager;
import android.app.INotificationManager;
import android.os.Handler;
import android.content.pm.LauncherApps;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import javax.inject.Provider;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import dagger.internal.Factory;

public final class NotificationsModule_ProvideNotificationGutsManagerFactory implements Factory<NotificationGutsManager>
{
    private final Provider<AccessibilityManager> accessibilityManagerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<HighPriorityProvider> highPriorityProvider;
    private final Provider<LauncherApps> launcherAppsProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<INotificationManager> notificationManagerProvider;
    private final Provider<ShortcutManager> shortcutManagerProvider;
    private final Provider<StatusBar> statusBarLazyProvider;
    private final Provider<VisualStabilityManager> visualStabilityManagerProvider;
    
    public NotificationsModule_ProvideNotificationGutsManagerFactory(final Provider<Context> contextProvider, final Provider<VisualStabilityManager> visualStabilityManagerProvider, final Provider<StatusBar> statusBarLazyProvider, final Provider<Handler> mainHandlerProvider, final Provider<AccessibilityManager> accessibilityManagerProvider, final Provider<HighPriorityProvider> highPriorityProvider, final Provider<INotificationManager> notificationManagerProvider, final Provider<LauncherApps> launcherAppsProvider, final Provider<ShortcutManager> shortcutManagerProvider) {
        this.contextProvider = contextProvider;
        this.visualStabilityManagerProvider = visualStabilityManagerProvider;
        this.statusBarLazyProvider = statusBarLazyProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.accessibilityManagerProvider = accessibilityManagerProvider;
        this.highPriorityProvider = highPriorityProvider;
        this.notificationManagerProvider = notificationManagerProvider;
        this.launcherAppsProvider = launcherAppsProvider;
        this.shortcutManagerProvider = shortcutManagerProvider;
    }
    
    public static NotificationsModule_ProvideNotificationGutsManagerFactory create(final Provider<Context> provider, final Provider<VisualStabilityManager> provider2, final Provider<StatusBar> provider3, final Provider<Handler> provider4, final Provider<AccessibilityManager> provider5, final Provider<HighPriorityProvider> provider6, final Provider<INotificationManager> provider7, final Provider<LauncherApps> provider8, final Provider<ShortcutManager> provider9) {
        return new NotificationsModule_ProvideNotificationGutsManagerFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
    }
    
    public static NotificationGutsManager provideInstance(final Provider<Context> provider, final Provider<VisualStabilityManager> provider2, final Provider<StatusBar> provider3, final Provider<Handler> provider4, final Provider<AccessibilityManager> provider5, final Provider<HighPriorityProvider> provider6, final Provider<INotificationManager> provider7, final Provider<LauncherApps> provider8, final Provider<ShortcutManager> provider9) {
        return proxyProvideNotificationGutsManager(provider.get(), provider2.get(), DoubleCheck.lazy(provider3), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get());
    }
    
    public static NotificationGutsManager proxyProvideNotificationGutsManager(final Context context, final VisualStabilityManager visualStabilityManager, final Lazy<StatusBar> lazy, final Handler handler, final AccessibilityManager accessibilityManager, final HighPriorityProvider highPriorityProvider, final INotificationManager notificationManager, final LauncherApps launcherApps, final ShortcutManager shortcutManager) {
        final NotificationGutsManager provideNotificationGutsManager = NotificationsModule.provideNotificationGutsManager(context, visualStabilityManager, lazy, handler, accessibilityManager, highPriorityProvider, notificationManager, launcherApps, shortcutManager);
        Preconditions.checkNotNull(provideNotificationGutsManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationGutsManager;
    }
    
    @Override
    public NotificationGutsManager get() {
        return provideInstance(this.contextProvider, this.visualStabilityManagerProvider, this.statusBarLazyProvider, this.mainHandlerProvider, this.accessibilityManagerProvider, this.highPriorityProvider, this.notificationManagerProvider, this.launcherAppsProvider, this.shortcutManagerProvider);
    }
}
