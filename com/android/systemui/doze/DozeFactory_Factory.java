// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import android.app.IWallpaperManager;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.util.sensors.ProximitySensor;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.os.Handler;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.phone.DozeServiceHost;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.dock.DockManager;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.util.sensors.AsyncSensorManager;
import android.app.AlarmManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DozeFactory_Factory implements Factory<DozeFactory>
{
    private final Provider<AlarmManager> alarmManagerProvider;
    private final Provider<AsyncSensorManager> asyncSensorManagerProvider;
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<BiometricUnlockController> biometricUnlockControllerProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<DelayedWakeLock.Builder> delayedWakeLockBuilderProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<DozeLog> dozeLogProvider;
    private final Provider<DozeParameters> dozeParametersProvider;
    private final Provider<DozeServiceHost> dozeServiceHostProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<ProximitySensor> proximitySensorProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;
    private final Provider<IWallpaperManager> wallpaperManagerProvider;
    
    public DozeFactory_Factory(final Provider<FalsingManager> falsingManagerProvider, final Provider<DozeLog> dozeLogProvider, final Provider<DozeParameters> dozeParametersProvider, final Provider<BatteryController> batteryControllerProvider, final Provider<AsyncSensorManager> asyncSensorManagerProvider, final Provider<AlarmManager> alarmManagerProvider, final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider, final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider, final Provider<DockManager> dockManagerProvider, final Provider<IWallpaperManager> wallpaperManagerProvider, final Provider<ProximitySensor> proximitySensorProvider, final Provider<DelayedWakeLock.Builder> delayedWakeLockBuilderProvider, final Provider<Handler> handlerProvider, final Provider<BiometricUnlockController> biometricUnlockControllerProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<DozeServiceHost> dozeServiceHostProvider) {
        this.falsingManagerProvider = falsingManagerProvider;
        this.dozeLogProvider = dozeLogProvider;
        this.dozeParametersProvider = dozeParametersProvider;
        this.batteryControllerProvider = batteryControllerProvider;
        this.asyncSensorManagerProvider = asyncSensorManagerProvider;
        this.alarmManagerProvider = alarmManagerProvider;
        this.wakefulnessLifecycleProvider = wakefulnessLifecycleProvider;
        this.keyguardUpdateMonitorProvider = keyguardUpdateMonitorProvider;
        this.dockManagerProvider = dockManagerProvider;
        this.wallpaperManagerProvider = wallpaperManagerProvider;
        this.proximitySensorProvider = proximitySensorProvider;
        this.delayedWakeLockBuilderProvider = delayedWakeLockBuilderProvider;
        this.handlerProvider = handlerProvider;
        this.biometricUnlockControllerProvider = biometricUnlockControllerProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.dozeServiceHostProvider = dozeServiceHostProvider;
    }
    
    public static DozeFactory_Factory create(final Provider<FalsingManager> provider, final Provider<DozeLog> provider2, final Provider<DozeParameters> provider3, final Provider<BatteryController> provider4, final Provider<AsyncSensorManager> provider5, final Provider<AlarmManager> provider6, final Provider<WakefulnessLifecycle> provider7, final Provider<KeyguardUpdateMonitor> provider8, final Provider<DockManager> provider9, final Provider<IWallpaperManager> provider10, final Provider<ProximitySensor> provider11, final Provider<DelayedWakeLock.Builder> provider12, final Provider<Handler> provider13, final Provider<BiometricUnlockController> provider14, final Provider<BroadcastDispatcher> provider15, final Provider<DozeServiceHost> provider16) {
        return new DozeFactory_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16);
    }
    
    public static DozeFactory provideInstance(final Provider<FalsingManager> provider, final Provider<DozeLog> provider2, final Provider<DozeParameters> provider3, final Provider<BatteryController> provider4, final Provider<AsyncSensorManager> provider5, final Provider<AlarmManager> provider6, final Provider<WakefulnessLifecycle> provider7, final Provider<KeyguardUpdateMonitor> provider8, final Provider<DockManager> provider9, final Provider<IWallpaperManager> provider10, final Provider<ProximitySensor> provider11, final Provider<DelayedWakeLock.Builder> provider12, final Provider<Handler> provider13, final Provider<BiometricUnlockController> provider14, final Provider<BroadcastDispatcher> provider15, final Provider<DozeServiceHost> provider16) {
        return new DozeFactory(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get(), provider14.get(), provider15.get(), provider16.get());
    }
    
    @Override
    public DozeFactory get() {
        return provideInstance(this.falsingManagerProvider, this.dozeLogProvider, this.dozeParametersProvider, this.batteryControllerProvider, this.asyncSensorManagerProvider, this.alarmManagerProvider, this.wakefulnessLifecycleProvider, this.keyguardUpdateMonitorProvider, this.dockManagerProvider, this.wallpaperManagerProvider, this.proximitySensorProvider, this.delayedWakeLockBuilderProvider, this.handlerProvider, this.biometricUnlockControllerProvider, this.broadcastDispatcherProvider, this.dozeServiceHostProvider);
    }
}
