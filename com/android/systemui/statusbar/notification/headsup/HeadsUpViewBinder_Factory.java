// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.headsup;

import com.android.internal.util.NotificationMessagingUtil;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class HeadsUpViewBinder_Factory implements Factory<HeadsUpViewBinder>
{
    private final Provider<RowContentBindStage> bindStageProvider;
    private final Provider<NotificationMessagingUtil> notificationMessagingUtilProvider;
    
    public HeadsUpViewBinder_Factory(final Provider<NotificationMessagingUtil> notificationMessagingUtilProvider, final Provider<RowContentBindStage> bindStageProvider) {
        this.notificationMessagingUtilProvider = notificationMessagingUtilProvider;
        this.bindStageProvider = bindStageProvider;
    }
    
    public static HeadsUpViewBinder_Factory create(final Provider<NotificationMessagingUtil> provider, final Provider<RowContentBindStage> provider2) {
        return new HeadsUpViewBinder_Factory(provider, provider2);
    }
    
    public static HeadsUpViewBinder provideInstance(final Provider<NotificationMessagingUtil> provider, final Provider<RowContentBindStage> provider2) {
        return new HeadsUpViewBinder(provider.get(), provider2.get());
    }
    
    @Override
    public HeadsUpViewBinder get() {
        return provideInstance(this.notificationMessagingUtilProvider, this.bindStageProvider);
    }
}
