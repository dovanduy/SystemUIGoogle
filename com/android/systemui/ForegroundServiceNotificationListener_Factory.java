// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import android.content.Context;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ForegroundServiceNotificationListener_Factory implements Factory<ForegroundServiceNotificationListener>
{
    private final Provider<Context> contextProvider;
    private final Provider<ForegroundServiceController> foregroundServiceControllerProvider;
    private final Provider<NotifPipeline> notifPipelineProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    
    public ForegroundServiceNotificationListener_Factory(final Provider<Context> contextProvider, final Provider<ForegroundServiceController> foregroundServiceControllerProvider, final Provider<NotificationEntryManager> notificationEntryManagerProvider, final Provider<NotifPipeline> notifPipelineProvider) {
        this.contextProvider = contextProvider;
        this.foregroundServiceControllerProvider = foregroundServiceControllerProvider;
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.notifPipelineProvider = notifPipelineProvider;
    }
    
    public static ForegroundServiceNotificationListener_Factory create(final Provider<Context> provider, final Provider<ForegroundServiceController> provider2, final Provider<NotificationEntryManager> provider3, final Provider<NotifPipeline> provider4) {
        return new ForegroundServiceNotificationListener_Factory(provider, provider2, provider3, provider4);
    }
    
    public static ForegroundServiceNotificationListener provideInstance(final Provider<Context> provider, final Provider<ForegroundServiceController> provider2, final Provider<NotificationEntryManager> provider3, final Provider<NotifPipeline> provider4) {
        return new ForegroundServiceNotificationListener(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public ForegroundServiceNotificationListener get() {
        return provideInstance(this.contextProvider, this.foregroundServiceControllerProvider, this.notificationEntryManagerProvider, this.notifPipelineProvider);
    }
}
