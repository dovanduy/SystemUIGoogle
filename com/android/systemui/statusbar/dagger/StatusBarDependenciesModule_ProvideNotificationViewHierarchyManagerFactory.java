// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import android.os.Handler;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.notification.stack.ForegroundServiceSectionController;
import com.android.systemui.statusbar.notification.DynamicChildBindController;
import android.content.Context;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.bubbles.BubbleController;
import javax.inject.Provider;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import dagger.internal.Factory;

public final class StatusBarDependenciesModule_ProvideNotificationViewHierarchyManagerFactory implements Factory<NotificationViewHierarchyManager>
{
    private final Provider<BubbleController> bubbleControllerProvider;
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DynamicChildBindController> dynamicChildBindControllerProvider;
    private final Provider<ForegroundServiceSectionController> fgsSectionControllerProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider;
    private final Provider<DynamicPrivacyController> privacyControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<VisualStabilityManager> visualStabilityManagerProvider;
    
    public StatusBarDependenciesModule_ProvideNotificationViewHierarchyManagerFactory(final Provider<Context> contextProvider, final Provider<Handler> mainHandlerProvider, final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, final Provider<NotificationGroupManager> groupManagerProvider, final Provider<VisualStabilityManager> visualStabilityManagerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<NotificationEntryManager> notificationEntryManagerProvider, final Provider<KeyguardBypassController> bypassControllerProvider, final Provider<BubbleController> bubbleControllerProvider, final Provider<DynamicPrivacyController> privacyControllerProvider, final Provider<ForegroundServiceSectionController> fgsSectionControllerProvider, final Provider<DynamicChildBindController> dynamicChildBindControllerProvider) {
        this.contextProvider = contextProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.notificationLockscreenUserManagerProvider = notificationLockscreenUserManagerProvider;
        this.groupManagerProvider = groupManagerProvider;
        this.visualStabilityManagerProvider = visualStabilityManagerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.bypassControllerProvider = bypassControllerProvider;
        this.bubbleControllerProvider = bubbleControllerProvider;
        this.privacyControllerProvider = privacyControllerProvider;
        this.fgsSectionControllerProvider = fgsSectionControllerProvider;
        this.dynamicChildBindControllerProvider = dynamicChildBindControllerProvider;
    }
    
    public static StatusBarDependenciesModule_ProvideNotificationViewHierarchyManagerFactory create(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<NotificationLockscreenUserManager> provider3, final Provider<NotificationGroupManager> provider4, final Provider<VisualStabilityManager> provider5, final Provider<StatusBarStateController> provider6, final Provider<NotificationEntryManager> provider7, final Provider<KeyguardBypassController> provider8, final Provider<BubbleController> provider9, final Provider<DynamicPrivacyController> provider10, final Provider<ForegroundServiceSectionController> provider11, final Provider<DynamicChildBindController> provider12) {
        return new StatusBarDependenciesModule_ProvideNotificationViewHierarchyManagerFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12);
    }
    
    public static NotificationViewHierarchyManager provideInstance(final Provider<Context> provider, final Provider<Handler> provider2, final Provider<NotificationLockscreenUserManager> provider3, final Provider<NotificationGroupManager> provider4, final Provider<VisualStabilityManager> provider5, final Provider<StatusBarStateController> provider6, final Provider<NotificationEntryManager> provider7, final Provider<KeyguardBypassController> provider8, final Provider<BubbleController> provider9, final Provider<DynamicPrivacyController> provider10, final Provider<ForegroundServiceSectionController> provider11, final Provider<DynamicChildBindController> provider12) {
        return proxyProvideNotificationViewHierarchyManager(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get());
    }
    
    public static NotificationViewHierarchyManager proxyProvideNotificationViewHierarchyManager(final Context context, final Handler handler, final NotificationLockscreenUserManager notificationLockscreenUserManager, final NotificationGroupManager notificationGroupManager, final VisualStabilityManager visualStabilityManager, final StatusBarStateController statusBarStateController, final NotificationEntryManager notificationEntryManager, final KeyguardBypassController keyguardBypassController, final BubbleController bubbleController, final DynamicPrivacyController dynamicPrivacyController, final ForegroundServiceSectionController foregroundServiceSectionController, final DynamicChildBindController dynamicChildBindController) {
        final NotificationViewHierarchyManager provideNotificationViewHierarchyManager = StatusBarDependenciesModule.provideNotificationViewHierarchyManager(context, handler, notificationLockscreenUserManager, notificationGroupManager, visualStabilityManager, statusBarStateController, notificationEntryManager, keyguardBypassController, bubbleController, dynamicPrivacyController, foregroundServiceSectionController, dynamicChildBindController);
        Preconditions.checkNotNull(provideNotificationViewHierarchyManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationViewHierarchyManager;
    }
    
    @Override
    public NotificationViewHierarchyManager get() {
        return provideInstance(this.contextProvider, this.mainHandlerProvider, this.notificationLockscreenUserManagerProvider, this.groupManagerProvider, this.visualStabilityManagerProvider, this.statusBarStateControllerProvider, this.notificationEntryManagerProvider, this.bypassControllerProvider, this.bubbleControllerProvider, this.privacyControllerProvider, this.fgsSectionControllerProvider, this.dynamicChildBindControllerProvider);
    }
}
