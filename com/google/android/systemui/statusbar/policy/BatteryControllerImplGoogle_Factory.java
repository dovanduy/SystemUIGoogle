// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.statusbar.policy;

import android.content.SharedPreferences;
import com.google.android.systemui.reversecharging.ReverseWirelessCharger;
import java.util.Optional;
import android.os.PowerManager;
import android.app.NotificationManager;
import com.android.systemui.power.EnhancedEstimates;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.os.Handler;
import android.app.AlarmManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class BatteryControllerImplGoogle_Factory implements Factory<BatteryControllerImplGoogle>
{
    private final Provider<AlarmManager> alarmManagerProvider;
    private final Provider<Handler> bgHandlerProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<EnhancedEstimates> enhancedEstimatesProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<NotificationManager> notificationManagerProvider;
    private final Provider<PowerManager> powerManagerProvider;
    private final Provider<Optional<ReverseWirelessCharger>> rtxChargerManagerProvider;
    private final Provider<SharedPreferences> sharedPreferencesProvider;
    
    public BatteryControllerImplGoogle_Factory(final Provider<Optional<ReverseWirelessCharger>> rtxChargerManagerProvider, final Provider<AlarmManager> alarmManagerProvider, final Provider<Context> contextProvider, final Provider<EnhancedEstimates> enhancedEstimatesProvider, final Provider<PowerManager> powerManagerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<Handler> mainHandlerProvider, final Provider<Handler> bgHandlerProvider, final Provider<NotificationManager> notificationManagerProvider, final Provider<SharedPreferences> sharedPreferencesProvider) {
        this.rtxChargerManagerProvider = rtxChargerManagerProvider;
        this.alarmManagerProvider = alarmManagerProvider;
        this.contextProvider = contextProvider;
        this.enhancedEstimatesProvider = enhancedEstimatesProvider;
        this.powerManagerProvider = powerManagerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.bgHandlerProvider = bgHandlerProvider;
        this.notificationManagerProvider = notificationManagerProvider;
        this.sharedPreferencesProvider = sharedPreferencesProvider;
    }
    
    public static BatteryControllerImplGoogle_Factory create(final Provider<Optional<ReverseWirelessCharger>> provider, final Provider<AlarmManager> provider2, final Provider<Context> provider3, final Provider<EnhancedEstimates> provider4, final Provider<PowerManager> provider5, final Provider<BroadcastDispatcher> provider6, final Provider<Handler> provider7, final Provider<Handler> provider8, final Provider<NotificationManager> provider9, final Provider<SharedPreferences> provider10) {
        return new BatteryControllerImplGoogle_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10);
    }
    
    public static BatteryControllerImplGoogle provideInstance(final Provider<Optional<ReverseWirelessCharger>> provider, final Provider<AlarmManager> provider2, final Provider<Context> provider3, final Provider<EnhancedEstimates> provider4, final Provider<PowerManager> provider5, final Provider<BroadcastDispatcher> provider6, final Provider<Handler> provider7, final Provider<Handler> provider8, final Provider<NotificationManager> provider9, final Provider<SharedPreferences> provider10) {
        return new BatteryControllerImplGoogle(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get());
    }
    
    @Override
    public BatteryControllerImplGoogle get() {
        return provideInstance(this.rtxChargerManagerProvider, this.alarmManagerProvider, this.contextProvider, this.enhancedEstimatesProvider, this.powerManagerProvider, this.broadcastDispatcherProvider, this.mainHandlerProvider, this.bgHandlerProvider, this.notificationManagerProvider, this.sharedPreferencesProvider);
    }
}
