// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import dagger.internal.DoubleCheck;
import java.util.concurrent.Executor;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotifCollection;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.app.KeyguardManager;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import android.service.dreams.IDreamManager;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.bubbles.BubbleController;
import android.os.Handler;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.ActivityIntentHelper;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class StatusBarNotificationActivityStarter_Builder_Factory implements Factory<StatusBarNotificationActivityStarter.Builder>
{
    private final Provider<ActivityIntentHelper> activityIntentHelperProvider;
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<AssistManager> assistManagerLazyProvider;
    private final Provider<Handler> backgroundHandlerProvider;
    private final Provider<BubbleController> bubbleControllerProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<IDreamManager> dreamManagerProvider;
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerProvider;
    private final Provider<KeyguardManager> keyguardManagerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<LockPatternUtils> lockPatternUtilsProvider;
    private final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider;
    private final Provider<Handler> mainThreadHandlerProvider;
    private final Provider<MetricsLogger> metricsLoggerProvider;
    private final Provider<NotifCollection> notifCollectionProvider;
    private final Provider<NotifPipeline> notifPipelineProvider;
    private final Provider<NotificationInterruptStateProvider> notificationInterruptStateProvider;
    private final Provider<StatusBarRemoteInputCallback> remoteInputCallbackProvider;
    private final Provider<NotificationRemoteInputManager> remoteInputManagerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<StatusBarKeyguardViewManager> statusBarKeyguardViewManagerProvider;
    private final Provider<IStatusBarService> statusBarServiceProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<Executor> uiBgExecutorProvider;
    
    public StatusBarNotificationActivityStarter_Builder_Factory(final Provider<Context> contextProvider, final Provider<CommandQueue> commandQueueProvider, final Provider<AssistManager> assistManagerLazyProvider, final Provider<NotificationEntryManager> entryManagerProvider, final Provider<HeadsUpManagerPhone> headsUpManagerProvider, final Provider<ActivityStarter> activityStarterProvider, final Provider<IStatusBarService> statusBarServiceProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<StatusBarKeyguardViewManager> statusBarKeyguardViewManagerProvider, final Provider<KeyguardManager> keyguardManagerProvider, final Provider<IDreamManager> dreamManagerProvider, final Provider<NotificationRemoteInputManager> remoteInputManagerProvider, final Provider<StatusBarRemoteInputCallback> remoteInputCallbackProvider, final Provider<NotificationGroupManager> groupManagerProvider, final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<NotificationInterruptStateProvider> notificationInterruptStateProvider, final Provider<MetricsLogger> metricsLoggerProvider, final Provider<LockPatternUtils> lockPatternUtilsProvider, final Provider<Handler> mainThreadHandlerProvider, final Provider<Handler> backgroundHandlerProvider, final Provider<Executor> uiBgExecutorProvider, final Provider<ActivityIntentHelper> activityIntentHelperProvider, final Provider<BubbleController> bubbleControllerProvider, final Provider<ShadeController> shadeControllerProvider, final Provider<FeatureFlags> featureFlagsProvider, final Provider<NotifPipeline> notifPipelineProvider, final Provider<NotifCollection> notifCollectionProvider) {
        this.contextProvider = contextProvider;
        this.commandQueueProvider = commandQueueProvider;
        this.assistManagerLazyProvider = assistManagerLazyProvider;
        this.entryManagerProvider = entryManagerProvider;
        this.headsUpManagerProvider = headsUpManagerProvider;
        this.activityStarterProvider = activityStarterProvider;
        this.statusBarServiceProvider = statusBarServiceProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.statusBarKeyguardViewManagerProvider = statusBarKeyguardViewManagerProvider;
        this.keyguardManagerProvider = keyguardManagerProvider;
        this.dreamManagerProvider = dreamManagerProvider;
        this.remoteInputManagerProvider = remoteInputManagerProvider;
        this.remoteInputCallbackProvider = remoteInputCallbackProvider;
        this.groupManagerProvider = groupManagerProvider;
        this.lockscreenUserManagerProvider = lockscreenUserManagerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.notificationInterruptStateProvider = notificationInterruptStateProvider;
        this.metricsLoggerProvider = metricsLoggerProvider;
        this.lockPatternUtilsProvider = lockPatternUtilsProvider;
        this.mainThreadHandlerProvider = mainThreadHandlerProvider;
        this.backgroundHandlerProvider = backgroundHandlerProvider;
        this.uiBgExecutorProvider = uiBgExecutorProvider;
        this.activityIntentHelperProvider = activityIntentHelperProvider;
        this.bubbleControllerProvider = bubbleControllerProvider;
        this.shadeControllerProvider = shadeControllerProvider;
        this.featureFlagsProvider = featureFlagsProvider;
        this.notifPipelineProvider = notifPipelineProvider;
        this.notifCollectionProvider = notifCollectionProvider;
    }
    
    public static StatusBarNotificationActivityStarter_Builder_Factory create(final Provider<Context> provider, final Provider<CommandQueue> provider2, final Provider<AssistManager> provider3, final Provider<NotificationEntryManager> provider4, final Provider<HeadsUpManagerPhone> provider5, final Provider<ActivityStarter> provider6, final Provider<IStatusBarService> provider7, final Provider<StatusBarStateController> provider8, final Provider<StatusBarKeyguardViewManager> provider9, final Provider<KeyguardManager> provider10, final Provider<IDreamManager> provider11, final Provider<NotificationRemoteInputManager> provider12, final Provider<StatusBarRemoteInputCallback> provider13, final Provider<NotificationGroupManager> provider14, final Provider<NotificationLockscreenUserManager> provider15, final Provider<KeyguardStateController> provider16, final Provider<NotificationInterruptStateProvider> provider17, final Provider<MetricsLogger> provider18, final Provider<LockPatternUtils> provider19, final Provider<Handler> provider20, final Provider<Handler> provider21, final Provider<Executor> provider22, final Provider<ActivityIntentHelper> provider23, final Provider<BubbleController> provider24, final Provider<ShadeController> provider25, final Provider<FeatureFlags> provider26, final Provider<NotifPipeline> provider27, final Provider<NotifCollection> provider28) {
        return new StatusBarNotificationActivityStarter_Builder_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20, provider21, provider22, provider23, provider24, provider25, provider26, provider27, provider28);
    }
    
    public static StatusBarNotificationActivityStarter.Builder provideInstance(final Provider<Context> provider, final Provider<CommandQueue> provider2, final Provider<AssistManager> provider3, final Provider<NotificationEntryManager> provider4, final Provider<HeadsUpManagerPhone> provider5, final Provider<ActivityStarter> provider6, final Provider<IStatusBarService> provider7, final Provider<StatusBarStateController> provider8, final Provider<StatusBarKeyguardViewManager> provider9, final Provider<KeyguardManager> provider10, final Provider<IDreamManager> provider11, final Provider<NotificationRemoteInputManager> provider12, final Provider<StatusBarRemoteInputCallback> provider13, final Provider<NotificationGroupManager> provider14, final Provider<NotificationLockscreenUserManager> provider15, final Provider<KeyguardStateController> provider16, final Provider<NotificationInterruptStateProvider> provider17, final Provider<MetricsLogger> provider18, final Provider<LockPatternUtils> provider19, final Provider<Handler> provider20, final Provider<Handler> provider21, final Provider<Executor> provider22, final Provider<ActivityIntentHelper> provider23, final Provider<BubbleController> provider24, final Provider<ShadeController> provider25, final Provider<FeatureFlags> provider26, final Provider<NotifPipeline> provider27, final Provider<NotifCollection> provider28) {
        return new StatusBarNotificationActivityStarter.Builder(provider.get(), provider2.get(), DoubleCheck.lazy(provider3), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get(), provider14.get(), provider15.get(), provider16.get(), provider17.get(), provider18.get(), provider19.get(), provider20.get(), provider21.get(), provider22.get(), provider23.get(), provider24.get(), provider25.get(), provider26.get(), provider27.get(), provider28.get());
    }
    
    @Override
    public StatusBarNotificationActivityStarter.Builder get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider, this.assistManagerLazyProvider, this.entryManagerProvider, this.headsUpManagerProvider, this.activityStarterProvider, this.statusBarServiceProvider, this.statusBarStateControllerProvider, this.statusBarKeyguardViewManagerProvider, this.keyguardManagerProvider, this.dreamManagerProvider, this.remoteInputManagerProvider, this.remoteInputCallbackProvider, this.groupManagerProvider, this.lockscreenUserManagerProvider, this.keyguardStateControllerProvider, this.notificationInterruptStateProvider, this.metricsLoggerProvider, this.lockPatternUtilsProvider, this.mainThreadHandlerProvider, this.backgroundHandlerProvider, this.uiBgExecutorProvider, this.activityIntentHelperProvider, this.bubbleControllerProvider, this.shadeControllerProvider, this.featureFlagsProvider, this.notifPipelineProvider, this.notifCollectionProvider);
    }
}
