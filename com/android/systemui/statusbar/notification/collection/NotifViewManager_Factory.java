// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.FeatureFlags;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotifViewManager_Factory implements Factory<NotifViewManager>
{
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<NotifViewBarn> rowRegistryProvider;
    private final Provider<VisualStabilityManager> stabilityManagerProvider;
    
    public NotifViewManager_Factory(final Provider<NotifViewBarn> rowRegistryProvider, final Provider<VisualStabilityManager> stabilityManagerProvider, final Provider<FeatureFlags> featureFlagsProvider) {
        this.rowRegistryProvider = rowRegistryProvider;
        this.stabilityManagerProvider = stabilityManagerProvider;
        this.featureFlagsProvider = featureFlagsProvider;
    }
    
    public static NotifViewManager_Factory create(final Provider<NotifViewBarn> provider, final Provider<VisualStabilityManager> provider2, final Provider<FeatureFlags> provider3) {
        return new NotifViewManager_Factory(provider, provider2, provider3);
    }
    
    public static NotifViewManager provideInstance(final Provider<NotifViewBarn> provider, final Provider<VisualStabilityManager> provider2, final Provider<FeatureFlags> provider3) {
        return new NotifViewManager(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public NotifViewManager get() {
        return provideInstance(this.rowRegistryProvider, this.stabilityManagerProvider, this.featureFlagsProvider);
    }
}
