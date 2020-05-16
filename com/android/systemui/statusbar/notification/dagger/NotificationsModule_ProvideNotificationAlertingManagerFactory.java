// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import javax.inject.Provider;
import com.android.systemui.statusbar.notification.interruption.NotificationAlertingManager;
import dagger.internal.Factory;

public final class NotificationsModule_ProvideNotificationAlertingManagerFactory implements Factory<NotificationAlertingManager>
{
    private final Provider<HeadsUpManager> headsUpManagerProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationInterruptStateProvider> notificationInterruptStateProvider;
    private final Provider<NotificationListener> notificationListenerProvider;
    private final Provider<NotificationRemoteInputManager> remoteInputManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<VisualStabilityManager> visualStabilityManagerProvider;
    
    public NotificationsModule_ProvideNotificationAlertingManagerFactory(final Provider<NotificationEntryManager> notificationEntryManagerProvider, final Provider<NotificationRemoteInputManager> remoteInputManagerProvider, final Provider<VisualStabilityManager> visualStabilityManagerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<NotificationInterruptStateProvider> notificationInterruptStateProvider, final Provider<NotificationListener> notificationListenerProvider, final Provider<HeadsUpManager> headsUpManagerProvider) {
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.remoteInputManagerProvider = remoteInputManagerProvider;
        this.visualStabilityManagerProvider = visualStabilityManagerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.notificationInterruptStateProvider = notificationInterruptStateProvider;
        this.notificationListenerProvider = notificationListenerProvider;
        this.headsUpManagerProvider = headsUpManagerProvider;
    }
    
    public static NotificationsModule_ProvideNotificationAlertingManagerFactory create(final Provider<NotificationEntryManager> provider, final Provider<NotificationRemoteInputManager> provider2, final Provider<VisualStabilityManager> provider3, final Provider<StatusBarStateController> provider4, final Provider<NotificationInterruptStateProvider> provider5, final Provider<NotificationListener> provider6, final Provider<HeadsUpManager> provider7) {
        return new NotificationsModule_ProvideNotificationAlertingManagerFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7);
    }
    
    public static NotificationAlertingManager provideInstance(final Provider<NotificationEntryManager> provider, final Provider<NotificationRemoteInputManager> provider2, final Provider<VisualStabilityManager> provider3, final Provider<StatusBarStateController> provider4, final Provider<NotificationInterruptStateProvider> provider5, final Provider<NotificationListener> provider6, final Provider<HeadsUpManager> provider7) {
        return proxyProvideNotificationAlertingManager(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get());
    }
    
    public static NotificationAlertingManager proxyProvideNotificationAlertingManager(final NotificationEntryManager notificationEntryManager, final NotificationRemoteInputManager notificationRemoteInputManager, final VisualStabilityManager visualStabilityManager, final StatusBarStateController statusBarStateController, final NotificationInterruptStateProvider notificationInterruptStateProvider, final NotificationListener notificationListener, final HeadsUpManager headsUpManager) {
        final NotificationAlertingManager provideNotificationAlertingManager = NotificationsModule.provideNotificationAlertingManager(notificationEntryManager, notificationRemoteInputManager, visualStabilityManager, statusBarStateController, notificationInterruptStateProvider, notificationListener, headsUpManager);
        Preconditions.checkNotNull(provideNotificationAlertingManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationAlertingManager;
    }
    
    @Override
    public NotificationAlertingManager get() {
        return provideInstance(this.notificationEntryManagerProvider, this.remoteInputManagerProvider, this.visualStabilityManagerProvider, this.statusBarStateControllerProvider, this.notificationInterruptStateProvider, this.notificationListenerProvider, this.headsUpManagerProvider);
    }
}
