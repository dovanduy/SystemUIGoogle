// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.util.time.SystemClock;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ExpandableNotificationRowController_Factory implements Factory<ExpandableNotificationRowController>
{
    private final Provider<ActivatableNotificationViewController> activatableNotificationViewControllerProvider;
    private final Provider<Boolean> allowLongPressProvider;
    private final Provider<String> appNameProvider;
    private final Provider<SystemClock> clockProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<HeadsUpManager> headsUpManagerProvider;
    private final Provider<NotificationRowContentBinder.InflationCallback> inflationCallbackProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<NotificationMediaManager> mediaManagerProvider;
    private final Provider<NotificationGroupManager> notificationGroupManagerProvider;
    private final Provider<NotificationGutsManager> notificationGutsManagerProvider;
    private final Provider<String> notificationKeyProvider;
    private final Provider<NotificationLogger> notificationLoggerProvider;
    private final Provider<Runnable> onDismissRunnableProvider;
    private final Provider<ExpandableNotificationRow.OnExpandClickListener> onExpandClickListenerProvider;
    private final Provider<PeopleNotificationIdentifier> peopleNotificationIdentifierProvider;
    private final Provider<PluginManager> pluginManagerProvider;
    private final Provider<RowContentBindStage> rowContentBindStageProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<ExpandableNotificationRow> viewProvider;
    
    public ExpandableNotificationRowController_Factory(final Provider<ExpandableNotificationRow> viewProvider, final Provider<ActivatableNotificationViewController> activatableNotificationViewControllerProvider, final Provider<NotificationMediaManager> mediaManagerProvider, final Provider<PluginManager> pluginManagerProvider, final Provider<SystemClock> clockProvider, final Provider<String> appNameProvider, final Provider<String> notificationKeyProvider, final Provider<KeyguardBypassController> keyguardBypassControllerProvider, final Provider<NotificationGroupManager> notificationGroupManagerProvider, final Provider<RowContentBindStage> rowContentBindStageProvider, final Provider<NotificationLogger> notificationLoggerProvider, final Provider<HeadsUpManager> headsUpManagerProvider, final Provider<ExpandableNotificationRow.OnExpandClickListener> onExpandClickListenerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<NotificationRowContentBinder.InflationCallback> inflationCallbackProvider, final Provider<NotificationGutsManager> notificationGutsManagerProvider, final Provider<Boolean> allowLongPressProvider, final Provider<Runnable> onDismissRunnableProvider, final Provider<FalsingManager> falsingManagerProvider, final Provider<PeopleNotificationIdentifier> peopleNotificationIdentifierProvider) {
        this.viewProvider = viewProvider;
        this.activatableNotificationViewControllerProvider = activatableNotificationViewControllerProvider;
        this.mediaManagerProvider = mediaManagerProvider;
        this.pluginManagerProvider = pluginManagerProvider;
        this.clockProvider = clockProvider;
        this.appNameProvider = appNameProvider;
        this.notificationKeyProvider = notificationKeyProvider;
        this.keyguardBypassControllerProvider = keyguardBypassControllerProvider;
        this.notificationGroupManagerProvider = notificationGroupManagerProvider;
        this.rowContentBindStageProvider = rowContentBindStageProvider;
        this.notificationLoggerProvider = notificationLoggerProvider;
        this.headsUpManagerProvider = headsUpManagerProvider;
        this.onExpandClickListenerProvider = onExpandClickListenerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.inflationCallbackProvider = inflationCallbackProvider;
        this.notificationGutsManagerProvider = notificationGutsManagerProvider;
        this.allowLongPressProvider = allowLongPressProvider;
        this.onDismissRunnableProvider = onDismissRunnableProvider;
        this.falsingManagerProvider = falsingManagerProvider;
        this.peopleNotificationIdentifierProvider = peopleNotificationIdentifierProvider;
    }
    
    public static ExpandableNotificationRowController_Factory create(final Provider<ExpandableNotificationRow> provider, final Provider<ActivatableNotificationViewController> provider2, final Provider<NotificationMediaManager> provider3, final Provider<PluginManager> provider4, final Provider<SystemClock> provider5, final Provider<String> provider6, final Provider<String> provider7, final Provider<KeyguardBypassController> provider8, final Provider<NotificationGroupManager> provider9, final Provider<RowContentBindStage> provider10, final Provider<NotificationLogger> provider11, final Provider<HeadsUpManager> provider12, final Provider<ExpandableNotificationRow.OnExpandClickListener> provider13, final Provider<StatusBarStateController> provider14, final Provider<NotificationRowContentBinder.InflationCallback> provider15, final Provider<NotificationGutsManager> provider16, final Provider<Boolean> provider17, final Provider<Runnable> provider18, final Provider<FalsingManager> provider19, final Provider<PeopleNotificationIdentifier> provider20) {
        return new ExpandableNotificationRowController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20);
    }
    
    public static ExpandableNotificationRowController provideInstance(final Provider<ExpandableNotificationRow> provider, final Provider<ActivatableNotificationViewController> provider2, final Provider<NotificationMediaManager> provider3, final Provider<PluginManager> provider4, final Provider<SystemClock> provider5, final Provider<String> provider6, final Provider<String> provider7, final Provider<KeyguardBypassController> provider8, final Provider<NotificationGroupManager> provider9, final Provider<RowContentBindStage> provider10, final Provider<NotificationLogger> provider11, final Provider<HeadsUpManager> provider12, final Provider<ExpandableNotificationRow.OnExpandClickListener> provider13, final Provider<StatusBarStateController> provider14, final Provider<NotificationRowContentBinder.InflationCallback> provider15, final Provider<NotificationGutsManager> provider16, final Provider<Boolean> provider17, final Provider<Runnable> provider18, final Provider<FalsingManager> provider19, final Provider<PeopleNotificationIdentifier> provider20) {
        return new ExpandableNotificationRowController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get(), provider14.get(), provider15.get(), provider16.get(), provider17.get(), provider18.get(), provider19.get(), provider20.get());
    }
    
    @Override
    public ExpandableNotificationRowController get() {
        return provideInstance(this.viewProvider, this.activatableNotificationViewControllerProvider, this.mediaManagerProvider, this.pluginManagerProvider, this.clockProvider, this.appNameProvider, this.notificationKeyProvider, this.keyguardBypassControllerProvider, this.notificationGroupManagerProvider, this.rowContentBindStageProvider, this.notificationLoggerProvider, this.headsUpManagerProvider, this.onExpandClickListenerProvider, this.statusBarStateControllerProvider, this.inflationCallbackProvider, this.notificationGutsManagerProvider, this.allowLongPressProvider, this.onDismissRunnableProvider, this.falsingManagerProvider, this.peopleNotificationIdentifierProvider);
    }
}
