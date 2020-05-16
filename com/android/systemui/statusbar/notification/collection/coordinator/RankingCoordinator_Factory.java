// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class RankingCoordinator_Factory implements Factory<RankingCoordinator>
{
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    
    public RankingCoordinator_Factory(final Provider<StatusBarStateController> statusBarStateControllerProvider) {
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
    }
    
    public static RankingCoordinator_Factory create(final Provider<StatusBarStateController> provider) {
        return new RankingCoordinator_Factory(provider);
    }
    
    public static RankingCoordinator provideInstance(final Provider<StatusBarStateController> provider) {
        return new RankingCoordinator(provider.get());
    }
    
    @Override
    public RankingCoordinator get() {
        return provideInstance(this.statusBarStateControllerProvider);
    }
}
