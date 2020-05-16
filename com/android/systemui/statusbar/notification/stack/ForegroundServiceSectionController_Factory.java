// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ForegroundServiceSectionController_Factory implements Factory<ForegroundServiceSectionController>
{
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<ForegroundServiceDismissalFeatureController> featureControllerProvider;
    
    public ForegroundServiceSectionController_Factory(final Provider<NotificationEntryManager> entryManagerProvider, final Provider<ForegroundServiceDismissalFeatureController> featureControllerProvider) {
        this.entryManagerProvider = entryManagerProvider;
        this.featureControllerProvider = featureControllerProvider;
    }
    
    public static ForegroundServiceSectionController_Factory create(final Provider<NotificationEntryManager> provider, final Provider<ForegroundServiceDismissalFeatureController> provider2) {
        return new ForegroundServiceSectionController_Factory(provider, provider2);
    }
    
    public static ForegroundServiceSectionController provideInstance(final Provider<NotificationEntryManager> provider, final Provider<ForegroundServiceDismissalFeatureController> provider2) {
        return new ForegroundServiceSectionController(provider.get(), provider2.get());
    }
    
    @Override
    public ForegroundServiceSectionController get() {
        return provideInstance(this.entryManagerProvider, this.featureControllerProvider);
    }
}
