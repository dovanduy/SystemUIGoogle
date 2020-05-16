// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.model.SysUiState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.util.FloatingContentCoordinator;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.bubbles.BubbleData;
import android.content.Context;
import com.android.systemui.statusbar.policy.ConfigurationController;
import javax.inject.Provider;
import com.android.systemui.bubbles.BubbleController;
import dagger.internal.Factory;

public final class BubbleModule_NewBubbleControllerFactory implements Factory<BubbleController>
{
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<BubbleData> dataProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<FloatingContentCoordinator> floatingContentCoordinatorProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<NotificationInterruptStateProvider> interruptionStateProvider;
    private final Provider<NotifPipeline> notifPipelineProvider;
    private final Provider<NotificationLockscreenUserManager> notifUserManagerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<SysUiState> sysUiStateProvider;
    private final Provider<ZenModeController> zenModeControllerProvider;
    
    public BubbleModule_NewBubbleControllerFactory(final Provider<Context> contextProvider, final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<ShadeController> shadeControllerProvider, final Provider<BubbleData> dataProvider, final Provider<ConfigurationController> configurationControllerProvider, final Provider<NotificationInterruptStateProvider> interruptionStateProvider, final Provider<ZenModeController> zenModeControllerProvider, final Provider<NotificationLockscreenUserManager> notifUserManagerProvider, final Provider<NotificationGroupManager> groupManagerProvider, final Provider<NotificationEntryManager> entryManagerProvider, final Provider<NotifPipeline> notifPipelineProvider, final Provider<FeatureFlags> featureFlagsProvider, final Provider<DumpManager> dumpManagerProvider, final Provider<FloatingContentCoordinator> floatingContentCoordinatorProvider, final Provider<SysUiState> sysUiStateProvider) {
        this.contextProvider = contextProvider;
        this.notificationShadeWindowControllerProvider = notificationShadeWindowControllerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.shadeControllerProvider = shadeControllerProvider;
        this.dataProvider = dataProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.interruptionStateProvider = interruptionStateProvider;
        this.zenModeControllerProvider = zenModeControllerProvider;
        this.notifUserManagerProvider = notifUserManagerProvider;
        this.groupManagerProvider = groupManagerProvider;
        this.entryManagerProvider = entryManagerProvider;
        this.notifPipelineProvider = notifPipelineProvider;
        this.featureFlagsProvider = featureFlagsProvider;
        this.dumpManagerProvider = dumpManagerProvider;
        this.floatingContentCoordinatorProvider = floatingContentCoordinatorProvider;
        this.sysUiStateProvider = sysUiStateProvider;
    }
    
    public static BubbleModule_NewBubbleControllerFactory create(final Provider<Context> provider, final Provider<NotificationShadeWindowController> provider2, final Provider<StatusBarStateController> provider3, final Provider<ShadeController> provider4, final Provider<BubbleData> provider5, final Provider<ConfigurationController> provider6, final Provider<NotificationInterruptStateProvider> provider7, final Provider<ZenModeController> provider8, final Provider<NotificationLockscreenUserManager> provider9, final Provider<NotificationGroupManager> provider10, final Provider<NotificationEntryManager> provider11, final Provider<NotifPipeline> provider12, final Provider<FeatureFlags> provider13, final Provider<DumpManager> provider14, final Provider<FloatingContentCoordinator> provider15, final Provider<SysUiState> provider16) {
        return new BubbleModule_NewBubbleControllerFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16);
    }
    
    public static BubbleController provideInstance(final Provider<Context> provider, final Provider<NotificationShadeWindowController> provider2, final Provider<StatusBarStateController> provider3, final Provider<ShadeController> provider4, final Provider<BubbleData> provider5, final Provider<ConfigurationController> provider6, final Provider<NotificationInterruptStateProvider> provider7, final Provider<ZenModeController> provider8, final Provider<NotificationLockscreenUserManager> provider9, final Provider<NotificationGroupManager> provider10, final Provider<NotificationEntryManager> provider11, final Provider<NotifPipeline> provider12, final Provider<FeatureFlags> provider13, final Provider<DumpManager> provider14, final Provider<FloatingContentCoordinator> provider15, final Provider<SysUiState> provider16) {
        return proxyNewBubbleController(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get(), provider14.get(), provider15.get(), provider16.get());
    }
    
    public static BubbleController proxyNewBubbleController(final Context context, final NotificationShadeWindowController notificationShadeWindowController, final StatusBarStateController statusBarStateController, final ShadeController shadeController, final BubbleData bubbleData, final ConfigurationController configurationController, final NotificationInterruptStateProvider notificationInterruptStateProvider, final ZenModeController zenModeController, final NotificationLockscreenUserManager notificationLockscreenUserManager, final NotificationGroupManager notificationGroupManager, final NotificationEntryManager notificationEntryManager, final NotifPipeline notifPipeline, final FeatureFlags featureFlags, final DumpManager dumpManager, final FloatingContentCoordinator floatingContentCoordinator, final SysUiState sysUiState) {
        final BubbleController bubbleController = BubbleModule.newBubbleController(context, notificationShadeWindowController, statusBarStateController, shadeController, bubbleData, configurationController, notificationInterruptStateProvider, zenModeController, notificationLockscreenUserManager, notificationGroupManager, notificationEntryManager, notifPipeline, featureFlags, dumpManager, floatingContentCoordinator, sysUiState);
        Preconditions.checkNotNull(bubbleController, "Cannot return null from a non-@Nullable @Provides method");
        return bubbleController;
    }
    
    @Override
    public BubbleController get() {
        return provideInstance(this.contextProvider, this.notificationShadeWindowControllerProvider, this.statusBarStateControllerProvider, this.shadeControllerProvider, this.dataProvider, this.configurationControllerProvider, this.interruptionStateProvider, this.zenModeControllerProvider, this.notifUserManagerProvider, this.groupManagerProvider, this.entryManagerProvider, this.notifPipelineProvider, this.featureFlagsProvider, this.dumpManagerProvider, this.floatingContentCoordinatorProvider, this.sysUiStateProvider);
    }
}
