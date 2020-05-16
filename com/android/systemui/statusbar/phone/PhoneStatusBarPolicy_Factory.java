// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.policy.ZenModeController;
import android.os.UserManager;
import com.android.systemui.statusbar.policy.UserInfoController;
import java.util.concurrent.Executor;
import android.telecom.TelecomManager;
import android.content.SharedPreferences;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.RotationLockController;
import android.content.res.Resources;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.app.IActivityManager;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.util.time.DateFormatUtil;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.policy.BluetoothController;
import android.media.AudioManager;
import android.app.AlarmManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class PhoneStatusBarPolicy_Factory implements Factory<PhoneStatusBarPolicy>
{
    private final Provider<AlarmManager> alarmManagerProvider;
    private final Provider<AudioManager> audioManagerProvider;
    private final Provider<BluetoothController> bluetoothControllerProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<CastController> castControllerProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<DataSaverController> dataSaverControllerProvider;
    private final Provider<DateFormatUtil> dateFormatUtilProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<Integer> displayIdProvider;
    private final Provider<HotspotController> hotspotControllerProvider;
    private final Provider<IActivityManager> iActivityManagerProvider;
    private final Provider<StatusBarIconController> iconControllerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<LocationController> locationControllerProvider;
    private final Provider<NextAlarmController> nextAlarmControllerProvider;
    private final Provider<RecordingController> recordingControllerProvider;
    private final Provider<Resources> resourcesProvider;
    private final Provider<RotationLockController> rotationLockControllerProvider;
    private final Provider<SensorPrivacyController> sensorPrivacyControllerProvider;
    private final Provider<SharedPreferences> sharedPreferencesProvider;
    private final Provider<TelecomManager> telecomManagerProvider;
    private final Provider<Executor> uiBgExecutorProvider;
    private final Provider<UserInfoController> userInfoControllerProvider;
    private final Provider<UserManager> userManagerProvider;
    private final Provider<ZenModeController> zenModeControllerProvider;
    
    public PhoneStatusBarPolicy_Factory(final Provider<StatusBarIconController> iconControllerProvider, final Provider<CommandQueue> commandQueueProvider, final Provider<BroadcastDispatcher> broadcastDispatcherProvider, final Provider<Executor> uiBgExecutorProvider, final Provider<Resources> resourcesProvider, final Provider<CastController> castControllerProvider, final Provider<HotspotController> hotspotControllerProvider, final Provider<BluetoothController> bluetoothControllerProvider, final Provider<NextAlarmController> nextAlarmControllerProvider, final Provider<UserInfoController> userInfoControllerProvider, final Provider<RotationLockController> rotationLockControllerProvider, final Provider<DataSaverController> dataSaverControllerProvider, final Provider<ZenModeController> zenModeControllerProvider, final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider, final Provider<KeyguardStateController> keyguardStateControllerProvider, final Provider<LocationController> locationControllerProvider, final Provider<SensorPrivacyController> sensorPrivacyControllerProvider, final Provider<IActivityManager> iActivityManagerProvider, final Provider<AlarmManager> alarmManagerProvider, final Provider<UserManager> userManagerProvider, final Provider<AudioManager> audioManagerProvider, final Provider<RecordingController> recordingControllerProvider, final Provider<TelecomManager> telecomManagerProvider, final Provider<Integer> displayIdProvider, final Provider<SharedPreferences> sharedPreferencesProvider, final Provider<DateFormatUtil> dateFormatUtilProvider) {
        this.iconControllerProvider = iconControllerProvider;
        this.commandQueueProvider = commandQueueProvider;
        this.broadcastDispatcherProvider = broadcastDispatcherProvider;
        this.uiBgExecutorProvider = uiBgExecutorProvider;
        this.resourcesProvider = resourcesProvider;
        this.castControllerProvider = castControllerProvider;
        this.hotspotControllerProvider = hotspotControllerProvider;
        this.bluetoothControllerProvider = bluetoothControllerProvider;
        this.nextAlarmControllerProvider = nextAlarmControllerProvider;
        this.userInfoControllerProvider = userInfoControllerProvider;
        this.rotationLockControllerProvider = rotationLockControllerProvider;
        this.dataSaverControllerProvider = dataSaverControllerProvider;
        this.zenModeControllerProvider = zenModeControllerProvider;
        this.deviceProvisionedControllerProvider = deviceProvisionedControllerProvider;
        this.keyguardStateControllerProvider = keyguardStateControllerProvider;
        this.locationControllerProvider = locationControllerProvider;
        this.sensorPrivacyControllerProvider = sensorPrivacyControllerProvider;
        this.iActivityManagerProvider = iActivityManagerProvider;
        this.alarmManagerProvider = alarmManagerProvider;
        this.userManagerProvider = userManagerProvider;
        this.audioManagerProvider = audioManagerProvider;
        this.recordingControllerProvider = recordingControllerProvider;
        this.telecomManagerProvider = telecomManagerProvider;
        this.displayIdProvider = displayIdProvider;
        this.sharedPreferencesProvider = sharedPreferencesProvider;
        this.dateFormatUtilProvider = dateFormatUtilProvider;
    }
    
    public static PhoneStatusBarPolicy_Factory create(final Provider<StatusBarIconController> provider, final Provider<CommandQueue> provider2, final Provider<BroadcastDispatcher> provider3, final Provider<Executor> provider4, final Provider<Resources> provider5, final Provider<CastController> provider6, final Provider<HotspotController> provider7, final Provider<BluetoothController> provider8, final Provider<NextAlarmController> provider9, final Provider<UserInfoController> provider10, final Provider<RotationLockController> provider11, final Provider<DataSaverController> provider12, final Provider<ZenModeController> provider13, final Provider<DeviceProvisionedController> provider14, final Provider<KeyguardStateController> provider15, final Provider<LocationController> provider16, final Provider<SensorPrivacyController> provider17, final Provider<IActivityManager> provider18, final Provider<AlarmManager> provider19, final Provider<UserManager> provider20, final Provider<AudioManager> provider21, final Provider<RecordingController> provider22, final Provider<TelecomManager> provider23, final Provider<Integer> provider24, final Provider<SharedPreferences> provider25, final Provider<DateFormatUtil> provider26) {
        return new PhoneStatusBarPolicy_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20, provider21, provider22, provider23, provider24, provider25, provider26);
    }
    
    public static PhoneStatusBarPolicy provideInstance(final Provider<StatusBarIconController> provider, final Provider<CommandQueue> provider2, final Provider<BroadcastDispatcher> provider3, final Provider<Executor> provider4, final Provider<Resources> provider5, final Provider<CastController> provider6, final Provider<HotspotController> provider7, final Provider<BluetoothController> provider8, final Provider<NextAlarmController> provider9, final Provider<UserInfoController> provider10, final Provider<RotationLockController> provider11, final Provider<DataSaverController> provider12, final Provider<ZenModeController> provider13, final Provider<DeviceProvisionedController> provider14, final Provider<KeyguardStateController> provider15, final Provider<LocationController> provider16, final Provider<SensorPrivacyController> provider17, final Provider<IActivityManager> provider18, final Provider<AlarmManager> provider19, final Provider<UserManager> provider20, final Provider<AudioManager> provider21, final Provider<RecordingController> provider22, final Provider<TelecomManager> provider23, final Provider<Integer> provider24, final Provider<SharedPreferences> provider25, final Provider<DateFormatUtil> provider26) {
        return new PhoneStatusBarPolicy(provider.get(), provider2.get(), provider3.get(), provider4.get(), provider5.get(), provider6.get(), provider7.get(), provider8.get(), provider9.get(), provider10.get(), provider11.get(), provider12.get(), provider13.get(), provider14.get(), provider15.get(), provider16.get(), provider17.get(), provider18.get(), provider19.get(), provider20.get(), provider21.get(), provider22.get(), provider23.get(), provider24.get(), provider25.get(), provider26.get());
    }
    
    @Override
    public PhoneStatusBarPolicy get() {
        return provideInstance(this.iconControllerProvider, this.commandQueueProvider, this.broadcastDispatcherProvider, this.uiBgExecutorProvider, this.resourcesProvider, this.castControllerProvider, this.hotspotControllerProvider, this.bluetoothControllerProvider, this.nextAlarmControllerProvider, this.userInfoControllerProvider, this.rotationLockControllerProvider, this.dataSaverControllerProvider, this.zenModeControllerProvider, this.deviceProvisionedControllerProvider, this.keyguardStateControllerProvider, this.locationControllerProvider, this.sensorPrivacyControllerProvider, this.iActivityManagerProvider, this.alarmManagerProvider, this.userManagerProvider, this.audioManagerProvider, this.recordingControllerProvider, this.telecomManagerProvider, this.displayIdProvider, this.sharedPreferencesProvider, this.dateFormatUtilProvider);
    }
}
