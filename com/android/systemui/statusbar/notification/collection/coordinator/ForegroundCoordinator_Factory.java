// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.ForegroundServiceController;
import com.android.systemui.appops.AppOpsController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ForegroundCoordinator_Factory implements Factory<ForegroundCoordinator>
{
    private final Provider<AppOpsController> appOpsControllerProvider;
    private final Provider<ForegroundServiceController> foregroundServiceControllerProvider;
    private final Provider<DelayableExecutor> mainExecutorProvider;
    
    public ForegroundCoordinator_Factory(final Provider<ForegroundServiceController> foregroundServiceControllerProvider, final Provider<AppOpsController> appOpsControllerProvider, final Provider<DelayableExecutor> mainExecutorProvider) {
        this.foregroundServiceControllerProvider = foregroundServiceControllerProvider;
        this.appOpsControllerProvider = appOpsControllerProvider;
        this.mainExecutorProvider = mainExecutorProvider;
    }
    
    public static ForegroundCoordinator_Factory create(final Provider<ForegroundServiceController> provider, final Provider<AppOpsController> provider2, final Provider<DelayableExecutor> provider3) {
        return new ForegroundCoordinator_Factory(provider, provider2, provider3);
    }
    
    public static ForegroundCoordinator provideInstance(final Provider<ForegroundServiceController> provider, final Provider<AppOpsController> provider2, final Provider<DelayableExecutor> provider3) {
        return new ForegroundCoordinator(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public ForegroundCoordinator get() {
        return provideInstance(this.foregroundServiceControllerProvider, this.appOpsControllerProvider, this.mainExecutorProvider);
    }
}
