// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.dagger;

import com.android.internal.logging.UiEventLoggerImpl;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.R$bool;
import com.android.systemui.statusbar.notification.init.NotificationsController;
import com.android.systemui.statusbar.notification.init.NotificationsControllerStub;
import com.android.systemui.statusbar.notification.init.NotificationsControllerImpl;
import com.android.systemui.statusbar.notification.logging.NotificationPanelLoggerImpl;
import com.android.systemui.statusbar.notification.logging.NotificationPanelLogger;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import java.util.concurrent.Executor;
import android.content.pm.ShortcutManager;
import android.content.pm.LauncherApps;
import android.app.INotificationManager;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import android.view.accessibility.AccessibilityManager;
import android.os.Handler;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController;
import com.android.systemui.util.leak.LeakDetector;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinder;
import com.android.systemui.statusbar.notification.collection.NotificationRankingManager;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.notification.NotificationEntryManagerLogger;
import com.android.systemui.statusbar.notification.row.NotificationBlockingHelperManager;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import android.content.Context;
import com.android.systemui.statusbar.notification.interruption.NotificationAlertingManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import dagger.Lazy;
import com.android.systemui.statusbar.FeatureFlags;

public interface NotificationsModule
{
    default CommonNotifCollection provideCommonNotifCollection(final FeatureFlags featureFlags, final Lazy<NotifPipeline> lazy, NotificationEntryManager notificationEntryManager) {
        if (featureFlags.isNewNotifPipelineRenderingEnabled()) {
            notificationEntryManager = (NotificationEntryManager)lazy.get();
        }
        return notificationEntryManager;
    }
    
    default NotificationAlertingManager provideNotificationAlertingManager(final NotificationEntryManager notificationEntryManager, final NotificationRemoteInputManager notificationRemoteInputManager, final VisualStabilityManager visualStabilityManager, final StatusBarStateController statusBarStateController, final NotificationInterruptStateProvider notificationInterruptStateProvider, final NotificationListener notificationListener, final HeadsUpManager headsUpManager) {
        return new NotificationAlertingManager(notificationEntryManager, notificationRemoteInputManager, visualStabilityManager, statusBarStateController, notificationInterruptStateProvider, notificationListener, headsUpManager);
    }
    
    default NotificationBlockingHelperManager provideNotificationBlockingHelperManager(final Context context, final NotificationGutsManager notificationGutsManager, final NotificationEntryManager notificationEntryManager, final MetricsLogger metricsLogger) {
        return new NotificationBlockingHelperManager(context, notificationGutsManager, notificationEntryManager, metricsLogger);
    }
    
    default NotificationEntryManager provideNotificationEntryManager(final NotificationEntryManagerLogger notificationEntryManagerLogger, final NotificationGroupManager notificationGroupManager, final NotificationRankingManager notificationRankingManager, final NotificationEntryManager.KeyguardEnvironment keyguardEnvironment, final FeatureFlags featureFlags, final Lazy<NotificationRowBinder> lazy, final Lazy<NotificationRemoteInputManager> lazy2, final LeakDetector leakDetector, final ForegroundServiceDismissalFeatureController foregroundServiceDismissalFeatureController) {
        return new NotificationEntryManager(notificationEntryManagerLogger, notificationGroupManager, notificationRankingManager, keyguardEnvironment, featureFlags, lazy, lazy2, leakDetector, foregroundServiceDismissalFeatureController);
    }
    
    default NotificationGutsManager provideNotificationGutsManager(final Context context, final VisualStabilityManager visualStabilityManager, final Lazy<StatusBar> lazy, final Handler handler, final AccessibilityManager accessibilityManager, final HighPriorityProvider highPriorityProvider, final INotificationManager notificationManager, final LauncherApps launcherApps, final ShortcutManager shortcutManager) {
        return new NotificationGutsManager(context, visualStabilityManager, lazy, handler, accessibilityManager, highPriorityProvider, notificationManager, launcherApps, shortcutManager);
    }
    
    default NotificationLogger provideNotificationLogger(final NotificationListener notificationListener, final Executor executor, final NotificationEntryManager notificationEntryManager, final StatusBarStateController statusBarStateController, final NotificationLogger.ExpansionStateLogger expansionStateLogger, final NotificationPanelLogger notificationPanelLogger) {
        return new NotificationLogger(notificationListener, executor, notificationEntryManager, statusBarStateController, expansionStateLogger, notificationPanelLogger);
    }
    
    default NotificationPanelLogger provideNotificationPanelLogger() {
        return new NotificationPanelLoggerImpl();
    }
    
    default NotificationsController provideNotificationsController(final Context context, final Lazy<NotificationsControllerImpl> lazy, final Lazy<NotificationsControllerStub> lazy2) {
        if (context.getResources().getBoolean(R$bool.config_renderNotifications)) {
            return lazy.get();
        }
        return lazy2.get();
    }
    
    default UiEventLogger provideUiEventLogger() {
        return (UiEventLogger)new UiEventLoggerImpl();
    }
    
    default VisualStabilityManager provideVisualStabilityManager(final NotificationEntryManager notificationEntryManager, final Handler handler) {
        return new VisualStabilityManager(notificationEntryManager, handler);
    }
}
