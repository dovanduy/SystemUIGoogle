// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.os.Handler;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.appops.AppOpsController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ForegroundServiceController_Factory implements Factory<ForegroundServiceController>
{
    private final Provider<AppOpsController> appOpsControllerProvider;
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<Handler> mainHandlerProvider;
    
    public ForegroundServiceController_Factory(final Provider<NotificationEntryManager> entryManagerProvider, final Provider<AppOpsController> appOpsControllerProvider, final Provider<Handler> mainHandlerProvider) {
        this.entryManagerProvider = entryManagerProvider;
        this.appOpsControllerProvider = appOpsControllerProvider;
        this.mainHandlerProvider = mainHandlerProvider;
    }
    
    public static ForegroundServiceController_Factory create(final Provider<NotificationEntryManager> provider, final Provider<AppOpsController> provider2, final Provider<Handler> provider3) {
        return new ForegroundServiceController_Factory(provider, provider2, provider3);
    }
    
    public static ForegroundServiceController provideInstance(final Provider<NotificationEntryManager> provider, final Provider<AppOpsController> provider2, final Provider<Handler> provider3) {
        return new ForegroundServiceController(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public ForegroundServiceController get() {
        return provideInstance(this.entryManagerProvider, this.appOpsControllerProvider, this.mainHandlerProvider);
    }
}
