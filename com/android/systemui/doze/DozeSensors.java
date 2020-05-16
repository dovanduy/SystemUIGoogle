// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import android.provider.Settings$Secure;
import com.android.internal.logging.MetricsLogger;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.util.Log;
import android.os.SystemClock;
import java.io.PrintWriter;
import java.util.Iterator;
import android.text.TextUtils;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import com.android.systemui.plugins.SensorManagerPlugin;
import android.app.ActivityManager;
import android.net.Uri;
import java.util.Collection;
import com.android.systemui.statusbar.phone.DozeParameters;
import android.app.AlarmManager;
import com.android.systemui.util.wakelock.WakeLock;
import android.database.ContentObserver;
import com.android.systemui.util.sensors.AsyncSensorManager;
import android.content.ContentResolver;
import com.android.systemui.util.sensors.ProximitySensor;
import java.util.function.Consumer;
import android.os.Handler;
import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;

public class DozeSensors
{
    private static final boolean DEBUG;
    private final Callback mCallback;
    private final AmbientDisplayConfiguration mConfig;
    private final Context mContext;
    private long mDebounceFrom;
    private final Handler mHandler;
    private boolean mListening;
    private boolean mPaused;
    private final Consumer<Boolean> mProxCallback;
    private final ProximitySensor mProximitySensor;
    private final ContentResolver mResolver;
    private final AsyncSensorManager mSensorManager;
    protected TriggerSensor[] mSensors;
    private boolean mSettingRegistered;
    private final ContentObserver mSettingsObserver;
    private final WakeLock mWakeLock;
    
    static {
        DEBUG = DozeService.DEBUG;
    }
    
    public DozeSensors(final Context mContext, final AlarmManager alarmManager, final AsyncSensorManager mSensorManager, final DozeParameters dozeParameters, final AmbientDisplayConfiguration mConfig, final WakeLock mWakeLock, final Callback mCallback, final Consumer<Boolean> mProxCallback, final DozeLog dozeLog) {
        this.mHandler = new Handler();
        this.mSettingsObserver = new ContentObserver(this.mHandler) {
            public void onChange(final boolean b, final Collection<Uri> collection, int i, int length) {
                if (length != ActivityManager.getCurrentUser()) {
                    return;
                }
                final TriggerSensor[] mSensors = DozeSensors.this.mSensors;
                for (length = mSensors.length, i = 0; i < length; ++i) {
                    mSensors[i].updateListening();
                }
            }
        };
        this.mContext = mContext;
        this.mSensorManager = mSensorManager;
        this.mConfig = mConfig;
        this.mWakeLock = mWakeLock;
        this.mProxCallback = mProxCallback;
        this.mResolver = mContext.getContentResolver();
        this.mCallback = mCallback;
        final boolean alwaysOnEnabled = this.mConfig.alwaysOnEnabled(-2);
        this.mSensors = new TriggerSensor[] { new TriggerSensor(this.mSensorManager.getDefaultSensor(17), null, dozeParameters.getPulseOnSigMotion(), 2, false, false, dozeLog), new TriggerSensor(this.mSensorManager.getDefaultSensor(25), "doze_pulse_on_pick_up", true, mConfig.dozePickupSensorAvailable(), 3, false, false, false, dozeLog), new TriggerSensor(this.findSensorWithType(mConfig.doubleTapSensorType()), "doze_pulse_on_double_tap", true, 4, dozeParameters.doubleTapReportsTouchCoordinates(), true, dozeLog), new TriggerSensor(this.findSensorWithType(mConfig.tapSensorType()), "doze_tap_gesture", true, 9, false, true, dozeLog), new TriggerSensor(this.findSensorWithType(mConfig.longPressSensorType()), "doze_pulse_on_long_press", false, true, 5, true, true, dozeLog), (TriggerSensor)new PluginSensor(new SensorManagerPlugin.Sensor(2), "doze_wake_display_gesture", this.mConfig.wakeScreenGestureAvailable() && alwaysOnEnabled, 7, false, false, dozeLog), (TriggerSensor)new PluginSensor(new SensorManagerPlugin.Sensor(1), "doze_wake_screen_gesture", this.mConfig.wakeScreenGestureAvailable(), 8, false, false, this.mConfig.getWakeLockScreenDebounce(), dozeLog) };
        this.mProximitySensor = new ProximitySensor(mContext.getResources(), mSensorManager);
        this.setProxListening(false);
        this.mProximitySensor.register((ProximitySensor.ProximitySensorListener)new _$$Lambda$DozeSensors$eWcsfaBj95QArTbTaV_jJjjsPh4(this));
    }
    
    static Sensor findSensorWithType(final SensorManager sensorManager, final String s) {
        if (TextUtils.isEmpty((CharSequence)s)) {
            return null;
        }
        for (final Sensor sensor : sensorManager.getSensorList(-1)) {
            if (s.equals(sensor.getStringType())) {
                return sensor;
            }
        }
        return null;
    }
    
    private Sensor findSensorWithType(final String s) {
        return findSensorWithType(this.mSensorManager, s);
    }
    
    public void dump(final PrintWriter printWriter) {
        for (final TriggerSensor triggerSensor : this.mSensors) {
            final StringBuilder sb = new StringBuilder();
            sb.append("  Sensor: ");
            sb.append(triggerSensor.toString());
            printWriter.println(sb.toString());
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  ProxSensor: ");
        sb2.append(this.mProximitySensor.toString());
        printWriter.println(sb2.toString());
    }
    
    public void ignoreTouchScreenSensorsSettingInterferingWithDocking(final boolean b) {
        for (final TriggerSensor triggerSensor : this.mSensors) {
            if (triggerSensor.mRequiresTouchscreen) {
                triggerSensor.ignoreSetting(b);
            }
        }
    }
    
    public Boolean isProximityCurrentlyNear() {
        return this.mProximitySensor.isNear();
    }
    
    public void onUserSwitched() {
        final TriggerSensor[] mSensors = this.mSensors;
        for (int length = mSensors.length, i = 0; i < length; ++i) {
            mSensors[i].updateListening();
        }
    }
    
    public void requestTemporaryDisable() {
        this.mDebounceFrom = SystemClock.uptimeMillis();
    }
    
    public void setListening(final boolean mListening) {
        if (this.mListening == mListening) {
            return;
        }
        this.mListening = mListening;
        this.updateListening();
    }
    
    public void setPaused(final boolean mPaused) {
        if (this.mPaused == mPaused) {
            return;
        }
        this.mPaused = mPaused;
        this.updateListening();
    }
    
    public void setProxListening(final boolean b) {
        if (this.mProximitySensor.isRegistered() && b) {
            this.mProximitySensor.alertListeners();
        }
        else if (b) {
            this.mProximitySensor.resume();
        }
        else {
            this.mProximitySensor.pause();
        }
    }
    
    public void setTouchscreenSensorsListening(final boolean listening) {
        for (final TriggerSensor triggerSensor : this.mSensors) {
            if (triggerSensor.mRequiresTouchscreen) {
                triggerSensor.setListening(listening);
            }
        }
    }
    
    public void updateListening() {
        final TriggerSensor[] mSensors = this.mSensors;
        final int length = mSensors.length;
        final int n = 0;
        boolean mSettingRegistered;
        for (int i = (mSettingRegistered = false) ? 1 : 0; i < length; ++i) {
            mSensors[i].setListening(this.mListening);
            if (this.mListening) {
                mSettingRegistered = true;
            }
        }
        if (!mSettingRegistered) {
            this.mResolver.unregisterContentObserver(this.mSettingsObserver);
        }
        else if (!this.mSettingRegistered) {
            final TriggerSensor[] mSensors2 = this.mSensors;
            for (int length2 = mSensors2.length, j = n; j < length2; ++j) {
                mSensors2[j].registerSettingsObserver(this.mSettingsObserver);
            }
        }
        this.mSettingRegistered = mSettingRegistered;
    }
    
    public interface Callback
    {
        void onSensorPulse(final int p0, final float p1, final float p2, final float[] p3);
    }
    
    class PluginSensor extends TriggerSensor implements SensorEventListener
    {
        private long mDebounce;
        final Sensor mPluginSensor;
        
        PluginSensor(final Sensor mPluginSensor, final String s, final boolean b, final int n, final boolean b2, final boolean b3, final long mDebounce, final DozeLog dozeLog) {
            super(null, s, b, n, b2, b3, dozeLog);
            this.mPluginSensor = mPluginSensor;
            this.mDebounce = mDebounce;
        }
        
        PluginSensor(final DozeSensors dozeSensors, final Sensor sensor, final String s, final boolean b, final int n, final boolean b2, final boolean b3, final DozeLog dozeLog) {
            this(dozeSensors, sensor, s, b, n, b2, b3, 0L, dozeLog);
        }
        
        private String triggerEventToString(final SensorEvent sensorEvent) {
            if (sensorEvent == null) {
                return null;
            }
            final StringBuilder sb = new StringBuilder("PluginTriggerEvent[");
            sb.append(sensorEvent.getSensor());
            sb.append(',');
            sb.append(sensorEvent.getVendorType());
            if (sensorEvent.getValues() != null) {
                for (int i = 0; i < sensorEvent.getValues().length; ++i) {
                    sb.append(',');
                    sb.append(sensorEvent.getValues()[i]);
                }
            }
            sb.append(']');
            return sb.toString();
        }
        
        @Override
        public void onSensorChanged(final SensorEvent sensorEvent) {
            super.mDozeLog.traceSensor(super.mPulseReason);
            DozeSensors.this.mHandler.post(DozeSensors.this.mWakeLock.wrap(new _$$Lambda$DozeSensors$PluginSensor$EFDqlQhDL6RwEmmtbTd8M88V_8Y(this, sensorEvent)));
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("{mRegistered=");
            sb.append(super.mRegistered);
            sb.append(", mRequested=");
            sb.append(super.mRequested);
            sb.append(", mDisabled=");
            sb.append(super.mDisabled);
            sb.append(", mConfigured=");
            sb.append(super.mConfigured);
            sb.append(", mIgnoresSetting=");
            sb.append(super.mIgnoresSetting);
            sb.append(", mSensor=");
            sb.append(this.mPluginSensor);
            sb.append("}");
            return sb.toString();
        }
        
        @Override
        public void updateListening() {
            if (!super.mConfigured) {
                return;
            }
            final AsyncSensorManager access$200 = DozeSensors.this.mSensorManager;
            if (super.mRequested && !super.mDisabled && (((TriggerSensor)this).enabledBySetting() || super.mIgnoresSetting) && !super.mRegistered) {
                access$200.registerPluginListener(this.mPluginSensor, this);
                super.mRegistered = true;
                if (DozeSensors.DEBUG) {
                    Log.d("DozeSensors", "registerPluginListener");
                }
            }
            else if (super.mRegistered) {
                access$200.unregisterPluginListener(this.mPluginSensor, this);
                super.mRegistered = false;
                if (DozeSensors.DEBUG) {
                    Log.d("DozeSensors", "unregisterPluginListener");
                }
            }
        }
    }
    
    class TriggerSensor extends TriggerEventListener
    {
        final boolean mConfigured;
        protected boolean mDisabled;
        protected final DozeLog mDozeLog;
        protected boolean mIgnoresSetting;
        final int mPulseReason;
        protected boolean mRegistered;
        private final boolean mReportsTouchCoordinates;
        protected boolean mRequested;
        private final boolean mRequiresTouchscreen;
        final Sensor mSensor;
        private final String mSetting;
        private final boolean mSettingDefault;
        
        public TriggerSensor(final DozeSensors dozeSensors, final Sensor sensor, final String s, final boolean b, final int n, final boolean b2, final boolean b3, final DozeLog dozeLog) {
            this(dozeSensors, sensor, s, true, b, n, b2, b3, dozeLog);
        }
        
        public TriggerSensor(final DozeSensors dozeSensors, final Sensor sensor, final String s, final boolean b, final boolean b2, final int n, final boolean b3, final boolean b4, final DozeLog dozeLog) {
            this(dozeSensors, sensor, s, b, b2, n, b3, b4, false, dozeLog);
        }
        
        private TriggerSensor(final Sensor mSensor, final String mSetting, final boolean mSettingDefault, final boolean mConfigured, final int mPulseReason, final boolean mReportsTouchCoordinates, final boolean mRequiresTouchscreen, final boolean mIgnoresSetting, final DozeLog mDozeLog) {
            this.mSensor = mSensor;
            this.mSetting = mSetting;
            this.mSettingDefault = mSettingDefault;
            this.mConfigured = mConfigured;
            this.mPulseReason = mPulseReason;
            this.mReportsTouchCoordinates = mReportsTouchCoordinates;
            this.mRequiresTouchscreen = mRequiresTouchscreen;
            this.mIgnoresSetting = mIgnoresSetting;
            this.mDozeLog = mDozeLog;
        }
        
        protected boolean enabledBySetting() {
            final boolean enabled = DozeSensors.this.mConfig.enabled(-2);
            boolean b = false;
            if (!enabled) {
                return false;
            }
            if (TextUtils.isEmpty((CharSequence)this.mSetting)) {
                return true;
            }
            if (Settings$Secure.getIntForUser(DozeSensors.this.mResolver, this.mSetting, (int)(this.mSettingDefault ? 1 : 0), -2) != 0) {
                b = true;
            }
            return b;
        }
        
        public void ignoreSetting(final boolean mIgnoresSetting) {
            if (this.mIgnoresSetting == mIgnoresSetting) {
                return;
            }
            this.mIgnoresSetting = mIgnoresSetting;
            this.updateListening();
        }
        
        public void onTrigger(final TriggerEvent triggerEvent) {
            this.mDozeLog.traceSensor(this.mPulseReason);
            DozeSensors.this.mHandler.post(DozeSensors.this.mWakeLock.wrap(new _$$Lambda$DozeSensors$TriggerSensor$O2XJN2HKJ96bSF_1qNx6jPK_eFk(this, triggerEvent)));
        }
        
        public void registerSettingsObserver(final ContentObserver contentObserver) {
            if (this.mConfigured && !TextUtils.isEmpty((CharSequence)this.mSetting)) {
                DozeSensors.this.mResolver.registerContentObserver(Settings$Secure.getUriFor(this.mSetting), false, DozeSensors.this.mSettingsObserver, -1);
            }
        }
        
        public void setListening(final boolean mRequested) {
            if (this.mRequested == mRequested) {
                return;
            }
            this.mRequested = mRequested;
            this.updateListening();
        }
        
        public String toString() {
            final StringBuilder sb = new StringBuilder("{mRegistered=");
            sb.append(this.mRegistered);
            sb.append(", mRequested=");
            sb.append(this.mRequested);
            sb.append(", mDisabled=");
            sb.append(this.mDisabled);
            sb.append(", mConfigured=");
            sb.append(this.mConfigured);
            sb.append(", mIgnoresSetting=");
            sb.append(this.mIgnoresSetting);
            sb.append(", mSensor=");
            sb.append(this.mSensor);
            sb.append("}");
            return sb.toString();
        }
        
        protected String triggerEventToString(final TriggerEvent triggerEvent) {
            if (triggerEvent == null) {
                return null;
            }
            final StringBuilder sb = new StringBuilder("SensorEvent[");
            sb.append(triggerEvent.timestamp);
            sb.append(',');
            sb.append(triggerEvent.sensor.getName());
            if (triggerEvent.values != null) {
                for (int i = 0; i < triggerEvent.values.length; ++i) {
                    sb.append(',');
                    sb.append(triggerEvent.values[i]);
                }
            }
            sb.append(']');
            return sb.toString();
        }
        
        public void updateListening() {
            if (this.mConfigured) {
                if (this.mSensor != null) {
                    if (this.mRequested && !this.mDisabled && (this.enabledBySetting() || this.mIgnoresSetting) && !this.mRegistered) {
                        this.mRegistered = DozeSensors.this.mSensorManager.requestTriggerSensor((TriggerEventListener)this, this.mSensor);
                        if (DozeSensors.DEBUG) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("requestTriggerSensor ");
                            sb.append(this.mRegistered);
                            Log.d("DozeSensors", sb.toString());
                        }
                    }
                    else if (this.mRegistered) {
                        final boolean cancelTriggerSensor = DozeSensors.this.mSensorManager.cancelTriggerSensor((TriggerEventListener)this, this.mSensor);
                        if (DozeSensors.DEBUG) {
                            final StringBuilder sb2 = new StringBuilder();
                            sb2.append("cancelTriggerSensor ");
                            sb2.append(cancelTriggerSensor);
                            Log.d("DozeSensors", sb2.toString());
                        }
                        this.mRegistered = false;
                    }
                }
            }
        }
    }
}
