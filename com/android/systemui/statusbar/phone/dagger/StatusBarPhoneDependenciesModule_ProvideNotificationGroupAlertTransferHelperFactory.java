// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone.dagger;

import dagger.internal.Preconditions;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import javax.inject.Provider;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import dagger.internal.Factory;

public final class StatusBarPhoneDependenciesModule_ProvideNotificationGroupAlertTransferHelperFactory implements Factory<NotificationGroupAlertTransferHelper>
{
    private final Provider<RowContentBindStage> bindStageProvider;
    
    public StatusBarPhoneDependenciesModule_ProvideNotificationGroupAlertTransferHelperFactory(final Provider<RowContentBindStage> bindStageProvider) {
        this.bindStageProvider = bindStageProvider;
    }
    
    public static StatusBarPhoneDependenciesModule_ProvideNotificationGroupAlertTransferHelperFactory create(final Provider<RowContentBindStage> provider) {
        return new StatusBarPhoneDependenciesModule_ProvideNotificationGroupAlertTransferHelperFactory(provider);
    }
    
    public static NotificationGroupAlertTransferHelper provideInstance(final Provider<RowContentBindStage> provider) {
        return proxyProvideNotificationGroupAlertTransferHelper(provider.get());
    }
    
    public static NotificationGroupAlertTransferHelper proxyProvideNotificationGroupAlertTransferHelper(final RowContentBindStage rowContentBindStage) {
        final NotificationGroupAlertTransferHelper provideNotificationGroupAlertTransferHelper = StatusBarPhoneDependenciesModule.provideNotificationGroupAlertTransferHelper(rowContentBindStage);
        Preconditions.checkNotNull(provideNotificationGroupAlertTransferHelper, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationGroupAlertTransferHelper;
    }
    
    @Override
    public NotificationGroupAlertTransferHelper get() {
        return provideInstance(this.bindStageProvider);
    }
}
