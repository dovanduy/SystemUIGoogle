// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.NotifViewBarn;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.notification.collection.NotifInflaterImpl;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PreparationCoordinator_Factory implements Factory<PreparationCoordinator>
{
    private final Provider<NotifInflationErrorManager> errorManagerProvider;
    private final Provider<PreparationCoordinatorLogger> loggerProvider;
    private final Provider<NotifInflaterImpl> notifInflaterProvider;
    private final Provider<IStatusBarService> serviceProvider;
    private final Provider<NotifViewBarn> viewBarnProvider;
    
    public PreparationCoordinator_Factory(final Provider<PreparationCoordinatorLogger> loggerProvider, final Provider<NotifInflaterImpl> notifInflaterProvider, final Provider<NotifInflationErrorManager> errorManagerProvider, final Provider<NotifViewBarn> viewBarnProvider, final Provider<IStatusBarService> serviceProvider) {
        this.loggerProvider = loggerProvider;
        this.notifInflaterProvider = notifInflaterProvider;
        this.errorManagerProvider = errorManagerProvider;
        this.viewBarnProvider = viewBarnProvider;
        this.serviceProvider = serviceProvider;
    }
    
    public static PreparationCoordinator_Factory create(final Provider<PreparationCoordinatorLogger> provider, final Provider<NotifInflaterImpl> provider2, final Provider<NotifInflationErrorManager> provider3, final Provider<NotifViewBarn> provider4, final Provider<IStatusBarService> provider5) {
        return new PreparationCoordinator_Factory(provider, provider2, provider3, provider4, provider5);
    }
    
    public static PreparationCoordinator provideInstance(final Provider<PreparationCoordinatorLogger> provider, final Provider<NotifInflaterImpl> provider2, final Provider<NotifInflationErrorManager> provider3, final Provider<NotifViewBarn> provider4, final Provider<IStatusBarService> provider5) {
        return new PreparationCoordinator(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get());
    }
    
    @Override
    public PreparationCoordinator get() {
        return provideInstance(this.loggerProvider, this.notifInflaterProvider, this.errorManagerProvider, this.viewBarnProvider, this.serviceProvider);
    }
}
