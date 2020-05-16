// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.init;

import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotifViewManager;
import com.android.systemui.statusbar.notification.collection.NotifInflaterImpl;
import com.android.systemui.statusbar.notification.collection.coordinator.NotifCoordinators;
import com.android.systemui.statusbar.notification.collection.NotifCollection;
import com.android.systemui.statusbar.notification.collection.ShadeListBuilder;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescer;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.dump.DumpManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotifPipelineInitializer_Factory implements Factory<NotifPipelineInitializer>
{
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<GroupCoalescer> groupCoalescerProvider;
    private final Provider<ShadeListBuilder> listBuilderProvider;
    private final Provider<NotifCollection> notifCollectionProvider;
    private final Provider<NotifCoordinators> notifCoordinatorsProvider;
    private final Provider<NotifInflaterImpl> notifInflaterProvider;
    private final Provider<NotifViewManager> notifViewManagerProvider;
    private final Provider<NotifPipeline> pipelineWrapperProvider;
    
    public NotifPipelineInitializer_Factory(final Provider<NotifPipeline> pipelineWrapperProvider, final Provider<GroupCoalescer> groupCoalescerProvider, final Provider<NotifCollection> notifCollectionProvider, final Provider<ShadeListBuilder> listBuilderProvider, final Provider<NotifCoordinators> notifCoordinatorsProvider, final Provider<NotifInflaterImpl> notifInflaterProvider, final Provider<DumpManager> dumpManagerProvider, final Provider<FeatureFlags> featureFlagsProvider, final Provider<NotifViewManager> notifViewManagerProvider) {
        this.pipelineWrapperProvider = pipelineWrapperProvider;
        this.groupCoalescerProvider = groupCoalescerProvider;
        this.notifCollectionProvider = notifCollectionProvider;
        this.listBuilderProvider = listBuilderProvider;
        this.notifCoordinatorsProvider = notifCoordinatorsProvider;
        this.notifInflaterProvider = notifInflaterProvider;
        this.dumpManagerProvider = dumpManagerProvider;
        this.featureFlagsProvider = featureFlagsProvider;
        this.notifViewManagerProvider = notifViewManagerProvider;
    }
    
    public static NotifPipelineInitializer_Factory create(final Provider<NotifPipeline> provider, final Provider<GroupCoalescer> provider2, final Provider<NotifCollection> provider3, final Provider<ShadeListBuilder> provider4, final Provider<NotifCoordinators> provider5, final Provider<NotifInflaterImpl> provider6, final Provider<DumpManager> provider7, final Provider<FeatureFlags> provider8, final Provider<NotifViewManager> provider9) {
        return new NotifPipelineInitializer_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
    }
    
    public static NotifPipelineInitializer provideInstance(final Provider<NotifPipeline> provider, final Provider<GroupCoalescer> provider2, final Provider<NotifCollection> provider3, final Provider<ShadeListBuilder> provider4, final Provider<NotifCoordinators> provider5, final Provider<NotifInflaterImpl> provider6, final Provider<DumpManager> provider7, final Provider<FeatureFlags> provider8, final Provider<NotifViewManager> provider9) {
        return new NotifPipelineInitializer(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get());
    }
    
    @Override
    public NotifPipelineInitializer get() {
        return provideInstance(this.pipelineWrapperProvider, this.groupCoalescerProvider, this.notifCollectionProvider, this.listBuilderProvider, this.notifCoordinatorsProvider, this.notifInflaterProvider, this.dumpManagerProvider, this.featureFlagsProvider, this.notifViewManagerProvider);
    }
}
