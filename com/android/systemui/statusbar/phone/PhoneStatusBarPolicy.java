// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.qs.tiles.RotationLockTile;
import android.app.IUserSwitchObserver;
import android.content.IntentFilter;
import android.service.notification.ZenModeConfig;
import com.android.systemui.qs.tiles.DndTile;
import java.util.Iterator;
import android.app.ActivityTaskManager;
import com.android.systemui.R$drawable;
import android.text.format.DateFormat;
import java.util.Locale;
import com.android.systemui.R$string;
import android.content.Intent;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.app.SynchronousUserSwitchObserver;
import android.os.UserManager;
import com.android.systemui.statusbar.policy.UserInfoController;
import java.util.concurrent.Executor;
import android.telecom.TelecomManager;
import android.content.SharedPreferences;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import android.content.res.Resources;
import com.android.systemui.statusbar.policy.NextAlarmController;
import android.app.AlarmManager$AlarmClockInfo;
import android.content.BroadcastReceiver;
import android.app.IActivityManager;
import com.android.systemui.statusbar.policy.HotspotController;
import android.os.Handler;
import com.android.systemui.util.time.DateFormatUtil;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.media.AudioManager;
import android.app.AlarmManager;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.BluetoothController;

public class PhoneStatusBarPolicy implements BluetoothController.Callback, Callbacks, RotationLockControllerCallback, Listener, ZenModeController.Callback, DeviceProvisionedListener, KeyguardStateController.Callback, LocationChangeCallback, RecordingStateChangeCallback
{
    private static final boolean DEBUG;
    private final AlarmManager mAlarmManager;
    private final AudioManager mAudioManager;
    private BluetoothController mBluetooth;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final CastController mCast;
    private final CastController.Callback mCastCallback;
    private final CommandQueue mCommandQueue;
    private boolean mCurrentUserSetup;
    private final DataSaverController mDataSaver;
    private final DateFormatUtil mDateFormatUtil;
    private final int mDisplayId;
    private final Handler mHandler;
    private final HotspotController mHotspot;
    private final HotspotController.Callback mHotspotCallback;
    private final IActivityManager mIActivityManager;
    private final StatusBarIconController mIconController;
    private BroadcastReceiver mIntentReceiver;
    private final KeyguardStateController mKeyguardStateController;
    private final LocationController mLocationController;
    private boolean mManagedProfileIconVisible;
    private AlarmManager$AlarmClockInfo mNextAlarm;
    private final NextAlarmController.NextAlarmChangeCallback mNextAlarmCallback;
    private final NextAlarmController mNextAlarmController;
    private final DeviceProvisionedController mProvisionedController;
    private final RecordingController mRecordingController;
    private Runnable mRemoveCastIconRunnable;
    private final Resources mResources;
    private final RotationLockController mRotationLockController;
    private final SensorPrivacyController mSensorPrivacyController;
    private final SensorPrivacyController.OnSensorPrivacyChangedListener mSensorPrivacyListener;
    private final SharedPreferences mSharedPreferences;
    private final String mSlotAlarmClock;
    private final String mSlotBluetooth;
    private final String mSlotCast;
    private final String mSlotDataSaver;
    private final String mSlotHeadset;
    private final String mSlotHotspot;
    private final String mSlotLocation;
    private final String mSlotManagedProfile;
    private final String mSlotRotate;
    private final String mSlotScreenRecord;
    private final String mSlotSensorsOff;
    private final String mSlotTty;
    private final String mSlotVolume;
    private final String mSlotZen;
    private final TelecomManager mTelecomManager;
    private final Executor mUiBgExecutor;
    private final UserInfoController mUserInfoController;
    private final UserManager mUserManager;
    private final SynchronousUserSwitchObserver mUserSwitchListener;
    private boolean mVolumeVisible;
    private final ZenModeController mZenController;
    private boolean mZenVisible;
    
    static {
        DEBUG = Log.isLoggable("PhoneStatusBarPolicy", 3);
    }
    
    public PhoneStatusBarPolicy(final StatusBarIconController mIconController, final CommandQueue mCommandQueue, final BroadcastDispatcher mBroadcastDispatcher, final Executor mUiBgExecutor, final Resources mResources, final CastController mCast, final HotspotController mHotspot, final BluetoothController mBluetooth, final NextAlarmController mNextAlarmController, final UserInfoController mUserInfoController, final RotationLockController mRotationLockController, final DataSaverController mDataSaver, final ZenModeController mZenController, final DeviceProvisionedController mProvisionedController, final KeyguardStateController mKeyguardStateController, final LocationController mLocationController, final SensorPrivacyController mSensorPrivacyController, final IActivityManager miActivityManager, final AlarmManager mAlarmManager, final UserManager mUserManager, final AudioManager mAudioManager, final RecordingController mRecordingController, final TelecomManager mTelecomManager, final int mDisplayId, final SharedPreferences mSharedPreferences, final DateFormatUtil mDateFormatUtil) {
        this.mHandler = new Handler();
        this.mManagedProfileIconVisible = false;
        this.mUserSwitchListener = new SynchronousUserSwitchObserver() {
            public void onUserSwitchComplete(final int n) throws RemoteException {
                PhoneStatusBarPolicy.this.mHandler.post((Runnable)new _$$Lambda$PhoneStatusBarPolicy$1$lONTSmykfPe64DIHRuLayVCRwlI(this));
            }
            
            public void onUserSwitching(final int n) throws RemoteException {
                PhoneStatusBarPolicy.this.mHandler.post((Runnable)new _$$Lambda$PhoneStatusBarPolicy$1$4_BI5ieR2ylfAj9z5SwNfbqaqk4(this));
            }
        };
        this.mHotspotCallback = new HotspotController.Callback() {
            @Override
            public void onHotspotChanged(final boolean b, final int n) {
                PhoneStatusBarPolicy.this.mIconController.setIconVisibility(PhoneStatusBarPolicy.this.mSlotHotspot, b);
            }
        };
        this.mCastCallback = new CastController.Callback() {
            @Override
            public void onCastDevicesChanged() {
                PhoneStatusBarPolicy.this.updateCast();
            }
        };
        this.mNextAlarmCallback = new NextAlarmController.NextAlarmChangeCallback() {
            @Override
            public void onNextAlarmChanged(final AlarmManager$AlarmClockInfo alarmManager$AlarmClockInfo) {
                PhoneStatusBarPolicy.this.mNextAlarm = alarmManager$AlarmClockInfo;
                PhoneStatusBarPolicy.this.updateAlarm();
            }
        };
        this.mSensorPrivacyListener = new SensorPrivacyController.OnSensorPrivacyChangedListener() {
            @Override
            public void onSensorPrivacyChanged(final boolean b) {
                PhoneStatusBarPolicy.this.mHandler.post((Runnable)new _$$Lambda$PhoneStatusBarPolicy$5$UApHxsPG0BIvDnX5FCFYX6op1Fs(this, b));
            }
        };
        this.mIntentReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                int n = 0;
                Label_0203: {
                    switch (action.hashCode()) {
                        case 2070024785: {
                            if (action.equals("android.media.RINGER_MODE_CHANGED")) {
                                n = 0;
                                break Label_0203;
                            }
                            break;
                        }
                        case 1051477093: {
                            if (action.equals("android.intent.action.MANAGED_PROFILE_REMOVED")) {
                                n = 6;
                                break Label_0203;
                            }
                            break;
                        }
                        case 1051344550: {
                            if (action.equals("android.telecom.action.CURRENT_TTY_MODE_CHANGED")) {
                                n = 3;
                                break Label_0203;
                            }
                            break;
                        }
                        case 100931828: {
                            if (action.equals("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION")) {
                                n = 1;
                                break Label_0203;
                            }
                            break;
                        }
                        case -229777127: {
                            if (action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                                n = 2;
                                break Label_0203;
                            }
                            break;
                        }
                        case -864107122: {
                            if (action.equals("android.intent.action.MANAGED_PROFILE_AVAILABLE")) {
                                n = 4;
                                break Label_0203;
                            }
                            break;
                        }
                        case -1238404651: {
                            if (action.equals("android.intent.action.MANAGED_PROFILE_UNAVAILABLE")) {
                                n = 5;
                                break Label_0203;
                            }
                            break;
                        }
                        case -1676458352: {
                            if (action.equals("android.intent.action.HEADSET_PLUG")) {
                                n = 7;
                                break Label_0203;
                            }
                            break;
                        }
                    }
                    n = -1;
                }
                switch (n) {
                    case 7: {
                        PhoneStatusBarPolicy.this.updateHeadsetPlug(intent);
                        break;
                    }
                    case 4:
                    case 5:
                    case 6: {
                        PhoneStatusBarPolicy.this.updateManagedProfile();
                        break;
                    }
                    case 3: {
                        PhoneStatusBarPolicy.this.updateTTY(intent.getIntExtra("android.telecom.extra.CURRENT_TTY_MODE", 0));
                        break;
                    }
                    case 2: {
                        intent.getBooleanExtra("rebroadcastOnUnlock", false);
                        break;
                    }
                    case 0:
                    case 1: {
                        PhoneStatusBarPolicy.this.updateVolumeZen();
                        break;
                    }
                }
            }
        };
        this.mRemoveCastIconRunnable = new Runnable() {
            @Override
            public void run() {
                if (PhoneStatusBarPolicy.DEBUG) {
                    Log.v("PhoneStatusBarPolicy", "updateCast: hiding icon NOW");
                }
                PhoneStatusBarPolicy.this.mIconController.setIconVisibility(PhoneStatusBarPolicy.this.mSlotCast, false);
            }
        };
        this.mIconController = mIconController;
        this.mCommandQueue = mCommandQueue;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mResources = mResources;
        this.mCast = mCast;
        this.mHotspot = mHotspot;
        this.mBluetooth = mBluetooth;
        this.mNextAlarmController = mNextAlarmController;
        this.mAlarmManager = mAlarmManager;
        this.mUserInfoController = mUserInfoController;
        this.mIActivityManager = miActivityManager;
        this.mUserManager = mUserManager;
        this.mRotationLockController = mRotationLockController;
        this.mDataSaver = mDataSaver;
        this.mZenController = mZenController;
        this.mProvisionedController = mProvisionedController;
        this.mKeyguardStateController = mKeyguardStateController;
        this.mLocationController = mLocationController;
        this.mSensorPrivacyController = mSensorPrivacyController;
        this.mRecordingController = mRecordingController;
        this.mUiBgExecutor = mUiBgExecutor;
        this.mAudioManager = mAudioManager;
        this.mTelecomManager = mTelecomManager;
        this.mSlotCast = mResources.getString(17041284);
        this.mSlotHotspot = mResources.getString(17041291);
        this.mSlotBluetooth = mResources.getString(17041282);
        this.mSlotTty = mResources.getString(17041308);
        this.mSlotZen = mResources.getString(17041312);
        this.mSlotVolume = mResources.getString(17041309);
        this.mSlotAlarmClock = mResources.getString(17041280);
        this.mSlotManagedProfile = mResources.getString(17041294);
        this.mSlotRotate = mResources.getString(17041301);
        this.mSlotHeadset = mResources.getString(17041290);
        this.mSlotDataSaver = mResources.getString(17041288);
        this.mSlotLocation = mResources.getString(17041293);
        this.mSlotSensorsOff = mResources.getString(17041304);
        this.mSlotScreenRecord = mResources.getString(17041302);
        this.mDisplayId = mDisplayId;
        this.mSharedPreferences = mSharedPreferences;
        this.mDateFormatUtil = mDateFormatUtil;
    }
    
    private String buildAlarmContentDescription() {
        if (this.mNextAlarm == null) {
            return this.mResources.getString(R$string.status_bar_alarm);
        }
        String s;
        if (this.mDateFormatUtil.is24HourFormat()) {
            s = "EHm";
        }
        else {
            s = "Ehma";
        }
        return this.mResources.getString(R$string.accessibility_quick_settings_alarm, new Object[] { DateFormat.format((CharSequence)DateFormat.getBestDateTimePattern(Locale.getDefault(), s), this.mNextAlarm.getTriggerTime()).toString() });
    }
    
    private void updateAlarm() {
        final AlarmManager$AlarmClockInfo nextAlarmClock = this.mAlarmManager.getNextAlarmClock(-2);
        boolean b = true;
        final boolean b2 = nextAlarmClock != null && nextAlarmClock.getTriggerTime() > 0L;
        final boolean b3 = this.mZenController.getZen() == 2;
        final StatusBarIconController mIconController = this.mIconController;
        final String mSlotAlarmClock = this.mSlotAlarmClock;
        int n;
        if (b3) {
            n = R$drawable.stat_sys_alarm_dim;
        }
        else {
            n = R$drawable.stat_sys_alarm;
        }
        mIconController.setIcon(mSlotAlarmClock, n, this.buildAlarmContentDescription());
        final StatusBarIconController mIconController2 = this.mIconController;
        final String mSlotAlarmClock2 = this.mSlotAlarmClock;
        if (!this.mCurrentUserSetup || !b2) {
            b = false;
        }
        mIconController2.setIconVisibility(mSlotAlarmClock2, b);
    }
    
    private final void updateBluetooth() {
        final int stat_sys_data_bluetooth_connected = R$drawable.stat_sys_data_bluetooth_connected;
        String s = this.mResources.getString(R$string.accessibility_quick_settings_bluetooth_on);
        final BluetoothController mBluetooth = this.mBluetooth;
        boolean bluetoothEnabled;
        if (mBluetooth != null && mBluetooth.isBluetoothConnected() && (this.mBluetooth.isBluetoothAudioActive() || !this.mBluetooth.isBluetoothAudioProfileOnly())) {
            s = this.mResources.getString(R$string.accessibility_bluetooth_connected);
            bluetoothEnabled = this.mBluetooth.isBluetoothEnabled();
        }
        else {
            bluetoothEnabled = false;
        }
        this.mIconController.setIcon(this.mSlotBluetooth, stat_sys_data_bluetooth_connected, s);
        this.mIconController.setIconVisibility(this.mSlotBluetooth, bluetoothEnabled);
    }
    
    private void updateCast() {
        final Iterator<CastController.CastDevice> iterator = this.mCast.getCastDevices().iterator();
        while (true) {
            while (iterator.hasNext()) {
                final int state = ((CastController.CastDevice)iterator.next()).state;
                if (state == 1 || state == 2) {
                    final boolean b = true;
                    if (PhoneStatusBarPolicy.DEBUG) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("updateCast: isCasting: ");
                        sb.append(b);
                        Log.v("PhoneStatusBarPolicy", sb.toString());
                    }
                    this.mHandler.removeCallbacks(this.mRemoveCastIconRunnable);
                    if (b && !this.mRecordingController.isRecording()) {
                        this.mIconController.setIcon(this.mSlotCast, R$drawable.stat_sys_cast, this.mResources.getString(R$string.accessibility_casting));
                        this.mIconController.setIconVisibility(this.mSlotCast, true);
                    }
                    else {
                        if (PhoneStatusBarPolicy.DEBUG) {
                            Log.v("PhoneStatusBarPolicy", "updateCast: hiding icon in 3 sec...");
                        }
                        this.mHandler.postDelayed(this.mRemoveCastIconRunnable, 3000L);
                    }
                    return;
                }
            }
            final boolean b = false;
            continue;
        }
    }
    
    private void updateHeadsetPlug(final Intent intent) {
        final boolean b = intent.getIntExtra("state", 0) != 0;
        final boolean b2 = intent.getIntExtra("microphone", 0) != 0;
        if (b) {
            final Resources mResources = this.mResources;
            int n;
            if (b2) {
                n = R$string.accessibility_status_bar_headset;
            }
            else {
                n = R$string.accessibility_status_bar_headphones;
            }
            final String string = mResources.getString(n);
            final StatusBarIconController mIconController = this.mIconController;
            final String mSlotHeadset = this.mSlotHeadset;
            int n2;
            if (b2) {
                n2 = R$drawable.stat_sys_headset_mic;
            }
            else {
                n2 = R$drawable.stat_sys_headset;
            }
            mIconController.setIcon(mSlotHeadset, n2, string);
            this.mIconController.setIconVisibility(this.mSlotHeadset, true);
        }
        else {
            this.mIconController.setIconVisibility(this.mSlotHeadset, false);
        }
    }
    
    private void updateLocation() {
        if (this.mLocationController.isLocationActive()) {
            this.mIconController.setIconVisibility(this.mSlotLocation, true);
        }
        else {
            this.mIconController.setIconVisibility(this.mSlotLocation, false);
        }
    }
    
    private void updateManagedProfile() {
        this.mUiBgExecutor.execute(new _$$Lambda$PhoneStatusBarPolicy$0YjhmxnSstzZ2dpboZJyd_6m3ZY(this));
    }
    
    private final void updateTTY() {
        final TelecomManager mTelecomManager = this.mTelecomManager;
        if (mTelecomManager == null) {
            this.updateTTY(0);
        }
        else {
            this.updateTTY(mTelecomManager.getCurrentTtyMode());
        }
    }
    
    private final void updateTTY(final int n) {
        final boolean b = n != 0;
        if (PhoneStatusBarPolicy.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("updateTTY: enabled: ");
            sb.append(b);
            Log.v("PhoneStatusBarPolicy", sb.toString());
        }
        if (b) {
            if (PhoneStatusBarPolicy.DEBUG) {
                Log.v("PhoneStatusBarPolicy", "updateTTY: set TTY on");
            }
            this.mIconController.setIcon(this.mSlotTty, R$drawable.stat_sys_tty_mode, this.mResources.getString(R$string.accessibility_tty_enabled));
            this.mIconController.setIconVisibility(this.mSlotTty, true);
        }
        else {
            if (PhoneStatusBarPolicy.DEBUG) {
                Log.v("PhoneStatusBarPolicy", "updateTTY: set TTY off");
            }
            this.mIconController.setIconVisibility(this.mSlotTty, false);
        }
    }
    
    private final void updateVolumeZen() {
        final int zen = this.mZenController.getZen();
        final boolean visible = DndTile.isVisible(this.mSharedPreferences);
        CharSequence charSequence = null;
        boolean mVolumeVisible = false;
        String s = null;
        int stat_sys_dnd = 0;
        int mZenVisible = 0;
        Label_0140: {
            if (!visible && !DndTile.isCombinedIcon(this.mSharedPreferences)) {
                int n;
                if (zen == 2) {
                    n = R$drawable.stat_sys_dnd;
                    s = this.mResources.getString(R$string.interruption_level_none);
                }
                else {
                    if (zen != 1) {
                        s = null;
                        mZenVisible = (stat_sys_dnd = 0);
                        break Label_0140;
                    }
                    n = R$drawable.stat_sys_dnd;
                    s = this.mResources.getString(R$string.interruption_level_priority);
                }
                stat_sys_dnd = n;
                mZenVisible = 1;
            }
            else {
                if (zen != 0) {
                    mZenVisible = 1;
                }
                else {
                    mZenVisible = 0;
                }
                stat_sys_dnd = R$drawable.stat_sys_dnd;
                s = this.mResources.getString(R$string.quick_settings_dnd_label);
            }
        }
        int n2 = 0;
        Label_0218: {
            Label_0216: {
                if (!ZenModeConfig.isZenOverridingRinger(zen, this.mZenController.getConsolidatedPolicy())) {
                    if (this.mAudioManager.getRingerModeInternal() == 1) {
                        n2 = R$drawable.stat_sys_ringer_vibrate;
                        charSequence = this.mResources.getString(R$string.accessibility_ringer_vibrate);
                    }
                    else {
                        if (this.mAudioManager.getRingerModeInternal() != 0) {
                            break Label_0216;
                        }
                        n2 = R$drawable.stat_sys_ringer_silent;
                        charSequence = this.mResources.getString(R$string.accessibility_ringer_silent);
                    }
                    mVolumeVisible = true;
                    break Label_0218;
                }
            }
            n2 = 0;
        }
        if (mZenVisible != 0) {
            this.mIconController.setIcon(this.mSlotZen, stat_sys_dnd, s);
        }
        if (mZenVisible != (this.mZenVisible ? 1 : 0)) {
            this.mIconController.setIconVisibility(this.mSlotZen, (boolean)(mZenVisible != 0));
            this.mZenVisible = (mZenVisible != 0);
        }
        if (mVolumeVisible) {
            this.mIconController.setIcon(this.mSlotVolume, n2, charSequence);
        }
        if (mVolumeVisible != this.mVolumeVisible) {
            this.mIconController.setIconVisibility(this.mSlotVolume, mVolumeVisible);
            this.mVolumeVisible = mVolumeVisible;
        }
        this.updateAlarm();
    }
    
    @Override
    public void appTransitionStarting(final int n, final long n2, final long n3, final boolean b) {
        if (this.mDisplayId == n) {
            this.updateManagedProfile();
        }
    }
    
    public void init() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.media.RINGER_MODE_CHANGED");
        intentFilter.addAction("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        intentFilter.addAction("android.telecom.action.CURRENT_TTY_MODE_CHANGED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this.mIntentReceiver, intentFilter, this.mHandler);
        while (true) {
            try {
                this.mIActivityManager.registerUserSwitchObserver((IUserSwitchObserver)this.mUserSwitchListener, "PhoneStatusBarPolicy");
                this.updateTTY();
                this.updateBluetooth();
                this.mIconController.setIcon(this.mSlotAlarmClock, R$drawable.stat_sys_alarm, null);
                this.mIconController.setIconVisibility(this.mSlotAlarmClock, false);
                this.mIconController.setIcon(this.mSlotZen, R$drawable.stat_sys_dnd, null);
                this.mIconController.setIconVisibility(this.mSlotZen, false);
                this.mIconController.setIcon(this.mSlotVolume, R$drawable.stat_sys_ringer_vibrate, null);
                this.mIconController.setIconVisibility(this.mSlotVolume, false);
                this.updateVolumeZen();
                this.mIconController.setIcon(this.mSlotCast, R$drawable.stat_sys_cast, null);
                this.mIconController.setIconVisibility(this.mSlotCast, false);
                this.mIconController.setIcon(this.mSlotHotspot, R$drawable.stat_sys_hotspot, this.mResources.getString(R$string.accessibility_status_bar_hotspot));
                this.mIconController.setIconVisibility(this.mSlotHotspot, this.mHotspot.isHotspotEnabled());
                this.mIconController.setIcon(this.mSlotManagedProfile, R$drawable.stat_sys_managed_profile_status, this.mResources.getString(R$string.accessibility_managed_profile));
                this.mIconController.setIconVisibility(this.mSlotManagedProfile, this.mManagedProfileIconVisible);
                this.mIconController.setIcon(this.mSlotDataSaver, R$drawable.stat_sys_data_saver, this.mResources.getString(R$string.accessibility_data_saver_on));
                this.mIconController.setIconVisibility(this.mSlotDataSaver, false);
                this.mIconController.setIcon(this.mSlotLocation, 17303123, this.mResources.getString(R$string.accessibility_location_active));
                this.mIconController.setIconVisibility(this.mSlotLocation, false);
                this.mIconController.setIcon(this.mSlotSensorsOff, R$drawable.stat_sys_sensors_off, this.mResources.getString(R$string.accessibility_sensors_off_active));
                this.mIconController.setIconVisibility(this.mSlotSensorsOff, this.mSensorPrivacyController.isSensorPrivacyEnabled());
                this.mIconController.setIcon(this.mSlotScreenRecord, R$drawable.stat_sys_screen_record, null);
                this.mIconController.setIconVisibility(this.mSlotScreenRecord, false);
                this.mRotationLockController.addCallback((RotationLockController.RotationLockControllerCallback)this);
                this.mBluetooth.addCallback((BluetoothController.Callback)this);
                this.mProvisionedController.addCallback((DeviceProvisionedController.DeviceProvisionedListener)this);
                this.mZenController.addCallback((ZenModeController.Callback)this);
                this.mCast.addCallback(this.mCastCallback);
                this.mHotspot.addCallback(this.mHotspotCallback);
                this.mNextAlarmController.addCallback(this.mNextAlarmCallback);
                this.mDataSaver.addCallback((DataSaverController.Listener)this);
                this.mKeyguardStateController.addCallback((KeyguardStateController.Callback)this);
                this.mSensorPrivacyController.addCallback(this.mSensorPrivacyListener);
                this.mLocationController.addCallback((LocationController.LocationChangeCallback)this);
                this.mRecordingController.addCallback((RecordingController.RecordingStateChangeCallback)this);
                this.mCommandQueue.addCallback((CommandQueue.Callbacks)this);
            }
            catch (RemoteException ex) {
                continue;
            }
            break;
        }
    }
    
    @Override
    public void onBluetoothDevicesChanged() {
        this.updateBluetooth();
    }
    
    @Override
    public void onBluetoothStateChange(final boolean b) {
        this.updateBluetooth();
    }
    
    @Override
    public void onConfigChanged(final ZenModeConfig zenModeConfig) {
        this.updateVolumeZen();
    }
    
    @Override
    public void onCountdown(final long lng) {
        if (PhoneStatusBarPolicy.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("screenrecord: countdown ");
            sb.append(lng);
            Log.d("PhoneStatusBarPolicy", sb.toString());
        }
        final int n = (int)Math.floorDiv(lng + 500L, 1000L);
        int n2 = R$drawable.stat_sys_screen_record;
        if (n != 1) {
            if (n != 2) {
                if (n == 3) {
                    n2 = R$drawable.stat_sys_screen_record_3;
                }
            }
            else {
                n2 = R$drawable.stat_sys_screen_record_2;
            }
        }
        else {
            n2 = R$drawable.stat_sys_screen_record_1;
        }
        this.mIconController.setIcon(this.mSlotScreenRecord, n2, null);
        this.mIconController.setIconVisibility(this.mSlotScreenRecord, true);
    }
    
    @Override
    public void onCountdownEnd() {
        if (PhoneStatusBarPolicy.DEBUG) {
            Log.d("PhoneStatusBarPolicy", "screenrecord: hiding icon during countdown");
        }
        this.mHandler.post((Runnable)new _$$Lambda$PhoneStatusBarPolicy$vDie88xyfybv0fbD81ATYspGS9w(this));
    }
    
    @Override
    public void onDataSaverChanged(final boolean b) {
        this.mIconController.setIconVisibility(this.mSlotDataSaver, b);
    }
    
    @Override
    public void onKeyguardShowingChanged() {
        this.updateManagedProfile();
    }
    
    @Override
    public void onLocationActiveChanged(final boolean b) {
        this.updateLocation();
    }
    
    @Override
    public void onRecordingEnd() {
        if (PhoneStatusBarPolicy.DEBUG) {
            Log.d("PhoneStatusBarPolicy", "screenrecord: hiding icon");
        }
        this.mHandler.post((Runnable)new _$$Lambda$PhoneStatusBarPolicy$dpyAA_eIm3FRkAJRBmEHDHOqEAk(this));
    }
    
    @Override
    public void onRecordingStart() {
        if (PhoneStatusBarPolicy.DEBUG) {
            Log.d("PhoneStatusBarPolicy", "screenrecord: showing icon");
        }
        this.mIconController.setIcon(this.mSlotScreenRecord, R$drawable.stat_sys_screen_record, null);
        this.mIconController.setIconVisibility(this.mSlotScreenRecord, true);
    }
    
    @Override
    public void onRotationLockStateChanged(final boolean b, final boolean b2) {
        final boolean currentOrientationLockPortrait = RotationLockTile.isCurrentOrientationLockPortrait(this.mRotationLockController, this.mResources);
        if (b) {
            if (currentOrientationLockPortrait) {
                this.mIconController.setIcon(this.mSlotRotate, R$drawable.stat_sys_rotate_portrait, this.mResources.getString(R$string.accessibility_rotation_lock_on_portrait));
            }
            else {
                this.mIconController.setIcon(this.mSlotRotate, R$drawable.stat_sys_rotate_landscape, this.mResources.getString(R$string.accessibility_rotation_lock_on_landscape));
            }
            this.mIconController.setIconVisibility(this.mSlotRotate, true);
        }
        else {
            this.mIconController.setIconVisibility(this.mSlotRotate, false);
        }
    }
    
    @Override
    public void onUserSetupChanged() {
        final DeviceProvisionedController mProvisionedController = this.mProvisionedController;
        final boolean userSetup = mProvisionedController.isUserSetup(mProvisionedController.getCurrentUser());
        if (this.mCurrentUserSetup == userSetup) {
            return;
        }
        this.mCurrentUserSetup = userSetup;
        this.updateAlarm();
    }
    
    @Override
    public void onZenChanged(final int n) {
        this.updateVolumeZen();
    }
}
