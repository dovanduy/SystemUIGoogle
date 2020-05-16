// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import javax.inject.Provider;
import dagger.internal.Factory;

public final class RowContentBindStage_Factory implements Factory<RowContentBindStage>
{
    private final Provider<NotificationRowContentBinder> binderProvider;
    private final Provider<NotifInflationErrorManager> errorManagerProvider;
    private final Provider<RowContentBindStageLogger> loggerProvider;
    
    public RowContentBindStage_Factory(final Provider<NotificationRowContentBinder> binderProvider, final Provider<NotifInflationErrorManager> errorManagerProvider, final Provider<RowContentBindStageLogger> loggerProvider) {
        this.binderProvider = binderProvider;
        this.errorManagerProvider = errorManagerProvider;
        this.loggerProvider = loggerProvider;
    }
    
    public static RowContentBindStage_Factory create(final Provider<NotificationRowContentBinder> provider, final Provider<NotifInflationErrorManager> provider2, final Provider<RowContentBindStageLogger> provider3) {
        return new RowContentBindStage_Factory(provider, provider2, provider3);
    }
    
    public static RowContentBindStage provideInstance(final Provider<NotificationRowContentBinder> provider, final Provider<NotifInflationErrorManager> provider2, final Provider<RowContentBindStageLogger> provider3) {
        return new RowContentBindStage(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public RowContentBindStage get() {
        return provideInstance(this.binderProvider, this.errorManagerProvider, this.loggerProvider);
    }
}
