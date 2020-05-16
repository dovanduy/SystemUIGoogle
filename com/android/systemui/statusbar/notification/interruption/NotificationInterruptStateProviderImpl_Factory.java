// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.interruption;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import android.os.PowerManager;
import com.android.systemui.statusbar.notification.NotificationFilter;
import android.os.Handler;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import android.service.dreams.IDreamManager;
import android.content.ContentResolver;
import com.android.systemui.statusbar.policy.BatteryController;
import android.hardware.display.AmbientDisplayConfiguration;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotificationInterruptStateProviderImpl_Factory implements Factory<NotificationInterruptStateProviderImpl>
{
    private final Provider<AmbientDisplayConfiguration> ambientDisplayConfigurationProvider;
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<ContentResolver> contentResolverProvider;
    private final Provider<IDreamManager> dreamManagerProvider;
    private final Provider<HeadsUpManager> headsUpManagerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<NotificationFilter> notificationFilterProvider;
    private final Provider<PowerManager> powerManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    
    public NotificationInterruptStateProviderImpl_Factory(final Provider<ContentResolver> contentResolverProvider, final Provider<PowerManager> powerManagerProvider, final Provider<IDreamManager> dreamManagerProvider, final Provider<AmbientDisplayConfiguration> ambientDisplayConfigurationProvider, final Provider<NotificationFilter> notificationFilterProvider, final Provider<BatteryController> batteryControllerProvider, final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<HeadsUpManager> headsUpManagerProvider, final Provider<Handler> mainHandlerProvider) {
        this.contentResolverProvider = contentResolverProvider;
        this.powerManagerProvider = powerManagerProvider;
        this.dreamManagerProvider = dreamManagerProvider;
        this.ambientDisplayConfigurationProvider = ambientDisplayConfigurationProvider;
        this.notificationFilterProvider = notificationFilterProvider;
        this.batteryControllerProvider = batteryControllerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.headsUpManagerProvider = headsUpManagerProvider;
        this.mainHandlerProvider = mainHandlerProvider;
    }
    
    public static NotificationInterruptStateProviderImpl_Factory create(final Provider<ContentResolver> provider, final Provider<PowerManager> provider2, final Provider<IDreamManager> provider3, final Provider<AmbientDisplayConfiguration> provider4, final Provider<NotificationFilter> provider5, final Provider<BatteryController> provider6, final Provider<StatusBarStateController> provider7, final Provider<HeadsUpManager> provider8, final Provider<Handler> provider9) {
        return new NotificationInterruptStateProviderImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
    }
    
    public static NotificationInterruptStateProviderImpl provideInstance(final Provider<ContentResolver> provider, final Provider<PowerManager> provider2, final Provider<IDreamManager> provider3, final Provider<AmbientDisplayConfiguration> provider4, final Provider<NotificationFilter> provider5, final Provider<BatteryController> provider6, final Provider<StatusBarStateController> provider7, final Provider<HeadsUpManager> provider8, final Provider<Handler> provider9) {
        return new NotificationInterruptStateProviderImpl(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get());
    }
    
    @Override
    public NotificationInterruptStateProviderImpl get() {
        return provideInstance(this.contentResolverProvider, this.powerManagerProvider, this.dreamManagerProvider, this.ambientDisplayConfigurationProvider, this.notificationFilterProvider, this.batteryControllerProvider, this.statusBarStateControllerProvider, this.headsUpManagerProvider, this.mainHandlerProvider);
    }
}
