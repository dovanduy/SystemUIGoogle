// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.dagger;

import dagger.internal.Preconditions;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import javax.inject.Provider;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import dagger.internal.Factory;

public final class NotificationsModule_ProvideCommonNotifCollectionFactory implements Factory<CommonNotifCollection>
{
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<NotifPipeline> pipelineProvider;
    
    public NotificationsModule_ProvideCommonNotifCollectionFactory(final Provider<FeatureFlags> featureFlagsProvider, final Provider<NotifPipeline> pipelineProvider, final Provider<NotificationEntryManager> entryManagerProvider) {
        this.featureFlagsProvider = featureFlagsProvider;
        this.pipelineProvider = pipelineProvider;
        this.entryManagerProvider = entryManagerProvider;
    }
    
    public static NotificationsModule_ProvideCommonNotifCollectionFactory create(final Provider<FeatureFlags> provider, final Provider<NotifPipeline> provider2, final Provider<NotificationEntryManager> provider3) {
        return new NotificationsModule_ProvideCommonNotifCollectionFactory(provider, provider2, provider3);
    }
    
    public static CommonNotifCollection provideInstance(final Provider<FeatureFlags> provider, final Provider<NotifPipeline> provider2, final Provider<NotificationEntryManager> provider3) {
        return proxyProvideCommonNotifCollection(provider.get(), DoubleCheck.lazy(provider2), provider3.get());
    }
    
    public static CommonNotifCollection proxyProvideCommonNotifCollection(final FeatureFlags featureFlags, final Lazy<NotifPipeline> lazy, final NotificationEntryManager notificationEntryManager) {
        final CommonNotifCollection provideCommonNotifCollection = NotificationsModule.provideCommonNotifCollection(featureFlags, lazy, notificationEntryManager);
        Preconditions.checkNotNull(provideCommonNotifCollection, "Cannot return null from a non-@Nullable @Provides method");
        return provideCommonNotifCollection;
    }
    
    @Override
    public CommonNotifCollection get() {
        return provideInstance(this.featureFlagsProvider, this.pipelineProvider, this.entryManagerProvider);
    }
}
