// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionLogger;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.dump.DumpManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotifCollection_Factory implements Factory<NotifCollection>
{
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<NotifCollectionLogger> loggerProvider;
    private final Provider<IStatusBarService> statusBarServiceProvider;
    
    public NotifCollection_Factory(final Provider<IStatusBarService> statusBarServiceProvider, final Provider<DumpManager> dumpManagerProvider, final Provider<FeatureFlags> featureFlagsProvider, final Provider<NotifCollectionLogger> loggerProvider) {
        this.statusBarServiceProvider = statusBarServiceProvider;
        this.dumpManagerProvider = dumpManagerProvider;
        this.featureFlagsProvider = featureFlagsProvider;
        this.loggerProvider = loggerProvider;
    }
    
    public static NotifCollection_Factory create(final Provider<IStatusBarService> provider, final Provider<DumpManager> provider2, final Provider<FeatureFlags> provider3, final Provider<NotifCollectionLogger> provider4) {
        return new NotifCollection_Factory(provider, provider2, provider3, provider4);
    }
    
    public static NotifCollection provideInstance(final Provider<IStatusBarService> provider, final Provider<DumpManager> provider2, final Provider<FeatureFlags> provider3, final Provider<NotifCollectionLogger> provider4) {
        return new NotifCollection(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public NotifCollection get() {
        return provideInstance(this.statusBarServiceProvider, this.dumpManagerProvider, this.featureFlagsProvider, this.loggerProvider);
    }
}
