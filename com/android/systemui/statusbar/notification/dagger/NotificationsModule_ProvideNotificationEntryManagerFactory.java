// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.dagger;

import dagger.internal.Preconditions;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.notification.collection.NotificationRankingManager;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinder;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.NotificationEntryManagerLogger;
import com.android.systemui.util.leak.LeakDetector;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController;
import com.android.systemui.statusbar.FeatureFlags;
import javax.inject.Provider;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import dagger.internal.Factory;

public final class NotificationsModule_ProvideNotificationEntryManagerFactory implements Factory<NotificationEntryManager>
{
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<ForegroundServiceDismissalFeatureController> fgsFeatureControllerProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<NotificationEntryManager.KeyguardEnvironment> keyguardEnvironmentProvider;
    private final Provider<LeakDetector> leakDetectorProvider;
    private final Provider<NotificationEntryManagerLogger> loggerProvider;
    private final Provider<NotificationRemoteInputManager> notificationRemoteInputManagerLazyProvider;
    private final Provider<NotificationRowBinder> notificationRowBinderLazyProvider;
    private final Provider<NotificationRankingManager> rankingManagerProvider;
    
    public NotificationsModule_ProvideNotificationEntryManagerFactory(final Provider<NotificationEntryManagerLogger> loggerProvider, final Provider<NotificationGroupManager> groupManagerProvider, final Provider<NotificationRankingManager> rankingManagerProvider, final Provider<NotificationEntryManager.KeyguardEnvironment> keyguardEnvironmentProvider, final Provider<FeatureFlags> featureFlagsProvider, final Provider<NotificationRowBinder> notificationRowBinderLazyProvider, final Provider<NotificationRemoteInputManager> notificationRemoteInputManagerLazyProvider, final Provider<LeakDetector> leakDetectorProvider, final Provider<ForegroundServiceDismissalFeatureController> fgsFeatureControllerProvider) {
        this.loggerProvider = loggerProvider;
        this.groupManagerProvider = groupManagerProvider;
        this.rankingManagerProvider = rankingManagerProvider;
        this.keyguardEnvironmentProvider = keyguardEnvironmentProvider;
        this.featureFlagsProvider = featureFlagsProvider;
        this.notificationRowBinderLazyProvider = notificationRowBinderLazyProvider;
        this.notificationRemoteInputManagerLazyProvider = notificationRemoteInputManagerLazyProvider;
        this.leakDetectorProvider = leakDetectorProvider;
        this.fgsFeatureControllerProvider = fgsFeatureControllerProvider;
    }
    
    public static NotificationsModule_ProvideNotificationEntryManagerFactory create(final Provider<NotificationEntryManagerLogger> provider, final Provider<NotificationGroupManager> provider2, final Provider<NotificationRankingManager> provider3, final Provider<NotificationEntryManager.KeyguardEnvironment> provider4, final Provider<FeatureFlags> provider5, final Provider<NotificationRowBinder> provider6, final Provider<NotificationRemoteInputManager> provider7, final Provider<LeakDetector> provider8, final Provider<ForegroundServiceDismissalFeatureController> provider9) {
        return new NotificationsModule_ProvideNotificationEntryManagerFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
    }
    
    public static NotificationEntryManager provideInstance(final Provider<NotificationEntryManagerLogger> provider, final Provider<NotificationGroupManager> provider2, final Provider<NotificationRankingManager> provider3, final Provider<NotificationEntryManager.KeyguardEnvironment> provider4, final Provider<FeatureFlags> provider5, final Provider<NotificationRowBinder> provider6, final Provider<NotificationRemoteInputManager> provider7, final Provider<LeakDetector> provider8, final Provider<ForegroundServiceDismissalFeatureController> provider9) {
        return proxyProvideNotificationEntryManager(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), DoubleCheck.lazy(provider6), DoubleCheck.lazy(provider7), provider8.get(), provider9.get());
    }
    
    public static NotificationEntryManager proxyProvideNotificationEntryManager(final NotificationEntryManagerLogger notificationEntryManagerLogger, final NotificationGroupManager notificationGroupManager, final NotificationRankingManager notificationRankingManager, final NotificationEntryManager.KeyguardEnvironment keyguardEnvironment, final FeatureFlags featureFlags, final Lazy<NotificationRowBinder> lazy, final Lazy<NotificationRemoteInputManager> lazy2, final LeakDetector leakDetector, final ForegroundServiceDismissalFeatureController foregroundServiceDismissalFeatureController) {
        final NotificationEntryManager provideNotificationEntryManager = NotificationsModule.provideNotificationEntryManager(notificationEntryManagerLogger, notificationGroupManager, notificationRankingManager, keyguardEnvironment, featureFlags, lazy, lazy2, leakDetector, foregroundServiceDismissalFeatureController);
        Preconditions.checkNotNull(provideNotificationEntryManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationEntryManager;
    }
    
    @Override
    public NotificationEntryManager get() {
        return provideInstance(this.loggerProvider, this.groupManagerProvider, this.rankingManagerProvider, this.keyguardEnvironmentProvider, this.featureFlagsProvider, this.notificationRowBinderLazyProvider, this.notificationRemoteInputManagerLazyProvider, this.leakDetectorProvider, this.fgsFeatureControllerProvider);
    }
}
