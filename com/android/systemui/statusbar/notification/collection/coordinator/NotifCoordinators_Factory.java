// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.dump.DumpManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotifCoordinators_Factory implements Factory<NotifCoordinators>
{
    private final Provider<BubbleCoordinator> bubbleCoordinatorProvider;
    private final Provider<ConversationCoordinator> conversationCoordinatorProvider;
    private final Provider<DeviceProvisionedCoordinator> deviceProvisionedCoordinatorProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<ForegroundCoordinator> foregroundCoordinatorProvider;
    private final Provider<HeadsUpCoordinator> headsUpCoordinatorProvider;
    private final Provider<HideNotifsForOtherUsersCoordinator> hideNotifsForOtherUsersCoordinatorProvider;
    private final Provider<KeyguardCoordinator> keyguardCoordinatorProvider;
    private final Provider<PreparationCoordinator> preparationCoordinatorProvider;
    private final Provider<RankingCoordinator> rankingCoordinatorProvider;
    
    public NotifCoordinators_Factory(final Provider<DumpManager> dumpManagerProvider, final Provider<FeatureFlags> featureFlagsProvider, final Provider<HideNotifsForOtherUsersCoordinator> hideNotifsForOtherUsersCoordinatorProvider, final Provider<KeyguardCoordinator> keyguardCoordinatorProvider, final Provider<RankingCoordinator> rankingCoordinatorProvider, final Provider<ForegroundCoordinator> foregroundCoordinatorProvider, final Provider<DeviceProvisionedCoordinator> deviceProvisionedCoordinatorProvider, final Provider<BubbleCoordinator> bubbleCoordinatorProvider, final Provider<HeadsUpCoordinator> headsUpCoordinatorProvider, final Provider<ConversationCoordinator> conversationCoordinatorProvider, final Provider<PreparationCoordinator> preparationCoordinatorProvider) {
        this.dumpManagerProvider = dumpManagerProvider;
        this.featureFlagsProvider = featureFlagsProvider;
        this.hideNotifsForOtherUsersCoordinatorProvider = hideNotifsForOtherUsersCoordinatorProvider;
        this.keyguardCoordinatorProvider = keyguardCoordinatorProvider;
        this.rankingCoordinatorProvider = rankingCoordinatorProvider;
        this.foregroundCoordinatorProvider = foregroundCoordinatorProvider;
        this.deviceProvisionedCoordinatorProvider = deviceProvisionedCoordinatorProvider;
        this.bubbleCoordinatorProvider = bubbleCoordinatorProvider;
        this.headsUpCoordinatorProvider = headsUpCoordinatorProvider;
        this.conversationCoordinatorProvider = conversationCoordinatorProvider;
        this.preparationCoordinatorProvider = preparationCoordinatorProvider;
    }
    
    public static NotifCoordinators_Factory create(final Provider<DumpManager> provider, final Provider<FeatureFlags> provider2, final Provider<HideNotifsForOtherUsersCoordinator> provider3, final Provider<KeyguardCoordinator> provider4, final Provider<RankingCoordinator> provider5, final Provider<ForegroundCoordinator> provider6, final Provider<DeviceProvisionedCoordinator> provider7, final Provider<BubbleCoordinator> provider8, final Provider<HeadsUpCoordinator> provider9, final Provider<ConversationCoordinator> provider10, final Provider<PreparationCoordinator> provider11) {
        return new NotifCoordinators_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11);
    }
    
    public static NotifCoordinators provideInstance(final Provider<DumpManager> provider, final Provider<FeatureFlags> provider2, final Provider<HideNotifsForOtherUsersCoordinator> provider3, final Provider<KeyguardCoordinator> provider4, final Provider<RankingCoordinator> provider5, final Provider<ForegroundCoordinator> provider6, final Provider<DeviceProvisionedCoordinator> provider7, final Provider<BubbleCoordinator> provider8, final Provider<HeadsUpCoordinator> provider9, final Provider<ConversationCoordinator> provider10, final Provider<PreparationCoordinator> provider11) {
        return new NotifCoordinators(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get());
    }
    
    @Override
    public NotifCoordinators get() {
        return provideInstance(this.dumpManagerProvider, this.featureFlagsProvider, this.hideNotifsForOtherUsersCoordinatorProvider, this.keyguardCoordinatorProvider, this.rankingCoordinatorProvider, this.foregroundCoordinatorProvider, this.deviceProvisionedCoordinatorProvider, this.bubbleCoordinatorProvider, this.headsUpCoordinatorProvider, this.conversationCoordinatorProvider, this.preparationCoordinatorProvider);
    }
}
