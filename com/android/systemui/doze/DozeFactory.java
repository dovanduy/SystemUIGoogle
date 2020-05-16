// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import com.android.systemui.util.wakelock.WakeLock;
import android.hardware.display.AmbientDisplayConfiguration;
import com.android.systemui.R$string;
import android.hardware.SensorManager;
import android.content.Context;
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

public class DozeFactory
{
    private final AlarmManager mAlarmManager;
    private final AsyncSensorManager mAsyncSensorManager;
    private final BatteryController mBatteryController;
    private final BiometricUnlockController mBiometricUnlockController;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final DelayedWakeLock.Builder mDelayedWakeLockBuilder;
    private final DockManager mDockManager;
    private final DozeLog mDozeLog;
    private final DozeParameters mDozeParameters;
    private final DozeServiceHost mDozeServiceHost;
    private final FalsingManager mFalsingManager;
    private final Handler mHandler;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final ProximitySensor mProximitySensor;
    private final WakefulnessLifecycle mWakefulnessLifecycle;
    private final IWallpaperManager mWallpaperManager;
    
    public DozeFactory(final FalsingManager mFalsingManager, final DozeLog mDozeLog, final DozeParameters mDozeParameters, final BatteryController mBatteryController, final AsyncSensorManager mAsyncSensorManager, final AlarmManager mAlarmManager, final WakefulnessLifecycle mWakefulnessLifecycle, final KeyguardUpdateMonitor mKeyguardUpdateMonitor, final DockManager mDockManager, final IWallpaperManager mWallpaperManager, final ProximitySensor mProximitySensor, final DelayedWakeLock.Builder mDelayedWakeLockBuilder, final Handler mHandler, final BiometricUnlockController mBiometricUnlockController, final BroadcastDispatcher mBroadcastDispatcher, final DozeServiceHost mDozeServiceHost) {
        this.mFalsingManager = mFalsingManager;
        this.mDozeLog = mDozeLog;
        this.mDozeParameters = mDozeParameters;
        this.mBatteryController = mBatteryController;
        this.mAsyncSensorManager = mAsyncSensorManager;
        this.mAlarmManager = mAlarmManager;
        this.mWakefulnessLifecycle = mWakefulnessLifecycle;
        this.mKeyguardUpdateMonitor = mKeyguardUpdateMonitor;
        this.mDockManager = mDockManager;
        this.mWallpaperManager = mWallpaperManager;
        this.mProximitySensor = mProximitySensor;
        this.mDelayedWakeLockBuilder = mDelayedWakeLockBuilder;
        this.mHandler = mHandler;
        this.mBiometricUnlockController = mBiometricUnlockController;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mDozeServiceHost = mDozeServiceHost;
    }
    
    private DozeMachine.Part createDozeScreenBrightness(final Context context, final DozeMachine.Service service, final SensorManager sensorManager, final DozeHost dozeHost, final DozeParameters dozeParameters, final Handler handler) {
        return new DozeScreenBrightness(context, service, sensorManager, DozeSensors.findSensorWithType(sensorManager, context.getString(R$string.doze_brightness_sensor_type)), this.mBroadcastDispatcher, dozeHost, handler, dozeParameters.getPolicy());
    }
    
    private DozeTriggers createDozeTriggers(final Context context, final AsyncSensorManager asyncSensorManager, final DozeHost dozeHost, final AlarmManager alarmManager, final AmbientDisplayConfiguration ambientDisplayConfiguration, final DozeParameters dozeParameters, final Handler handler, final WakeLock wakeLock, final DozeMachine dozeMachine, final DockManager dockManager, final DozeLog dozeLog) {
        return new DozeTriggers(context, dozeMachine, dozeHost, alarmManager, ambientDisplayConfiguration, dozeParameters, asyncSensorManager, handler, wakeLock, true, dockManager, this.mProximitySensor, dozeLog, this.mBroadcastDispatcher);
    }
    
    private DozeMachine.Part createDozeUi(final Context context, final DozeHost dozeHost, final WakeLock wakeLock, final DozeMachine dozeMachine, final Handler handler, final AlarmManager alarmManager, final DozeParameters dozeParameters, final DozeLog dozeLog) {
        return new DozeUi(context, alarmManager, dozeMachine, wakeLock, dozeHost, handler, dozeParameters, this.mKeyguardUpdateMonitor, dozeLog);
    }
    
    DozeMachine assembleMachine(final DozeService dozeService) {
        final AmbientDisplayConfiguration ambientDisplayConfiguration = new AmbientDisplayConfiguration((Context)dozeService);
        final DelayedWakeLock.Builder mDelayedWakeLockBuilder = this.mDelayedWakeLockBuilder;
        mDelayedWakeLockBuilder.setHandler(this.mHandler);
        mDelayedWakeLockBuilder.setTag("Doze");
        final DelayedWakeLock build = mDelayedWakeLockBuilder.build();
        final DozeMachine.Service wrapIfNeeded = DozeSuspendScreenStatePreventingAdapter.wrapIfNeeded(DozeScreenStatePreventingAdapter.wrapIfNeeded((DozeMachine.Service)new DozeBrightnessHostForwarder(dozeService, this.mDozeServiceHost), this.mDozeParameters), this.mDozeParameters);
        final DozeMachine dozeMachine = new DozeMachine(wrapIfNeeded, ambientDisplayConfiguration, build, this.mWakefulnessLifecycle, this.mBatteryController, this.mDozeLog, this.mDockManager, this.mDozeServiceHost);
        dozeMachine.setParts(new DozeMachine.Part[] { new DozePauser(this.mHandler, dozeMachine, this.mAlarmManager, this.mDozeParameters.getPolicy()), new DozeFalsingManagerAdapter(this.mFalsingManager), this.createDozeTriggers((Context)dozeService, this.mAsyncSensorManager, this.mDozeServiceHost, this.mAlarmManager, ambientDisplayConfiguration, this.mDozeParameters, this.mHandler, build, dozeMachine, this.mDockManager, this.mDozeLog), this.createDozeUi((Context)dozeService, this.mDozeServiceHost, build, dozeMachine, this.mHandler, this.mAlarmManager, this.mDozeParameters, this.mDozeLog), new DozeScreenState(wrapIfNeeded, this.mHandler, this.mDozeServiceHost, this.mDozeParameters, build), this.createDozeScreenBrightness((Context)dozeService, wrapIfNeeded, this.mAsyncSensorManager, this.mDozeServiceHost, this.mDozeParameters, this.mHandler), new DozeWallpaperState(this.mWallpaperManager, this.mBiometricUnlockController, this.mDozeParameters), new DozeDockHandler(ambientDisplayConfiguration, dozeMachine, this.mDockManager), new DozeAuthRemover((Context)dozeService) });
        return dozeMachine;
    }
}
