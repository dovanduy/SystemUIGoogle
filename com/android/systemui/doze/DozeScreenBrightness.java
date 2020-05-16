// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import android.os.Trace;
import android.hardware.SensorEvent;
import android.content.Intent;
import android.provider.Settings$System;
import com.android.internal.annotations.VisibleForTesting;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.os.Handler;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.hardware.SensorEventListener;
import android.content.BroadcastReceiver;

public class DozeScreenBrightness extends BroadcastReceiver implements Part, SensorEventListener
{
    private static final boolean DEBUG_AOD_BRIGHTNESS;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final Context mContext;
    private int mDebugBrightnessBucket;
    private final boolean mDebuggable;
    private int mDefaultDozeBrightness;
    private final DozeHost mDozeHost;
    private final Service mDozeService;
    private final Handler mHandler;
    private int mLastSensorValue;
    private final Sensor mLightSensor;
    private boolean mPaused;
    private boolean mRegistered;
    private boolean mScreenOff;
    private final SensorManager mSensorManager;
    private final int[] mSensorToBrightness;
    private final int[] mSensorToScrimOpacity;
    
    static {
        DEBUG_AOD_BRIGHTNESS = SystemProperties.getBoolean("debug.aod_brightness", false);
    }
    
    @VisibleForTesting
    public DozeScreenBrightness(final Context mContext, final Service mDozeService, final SensorManager mSensorManager, final Sensor mLightSensor, final BroadcastDispatcher mBroadcastDispatcher, final DozeHost mDozeHost, final Handler mHandler, final int mDefaultDozeBrightness, final int[] mSensorToBrightness, final int[] mSensorToScrimOpacity, final boolean mDebuggable) {
        this.mPaused = false;
        this.mScreenOff = false;
        this.mLastSensorValue = -1;
        this.mDebugBrightnessBucket = -1;
        this.mContext = mContext;
        this.mDozeService = mDozeService;
        this.mSensorManager = mSensorManager;
        this.mLightSensor = mLightSensor;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        this.mDozeHost = mDozeHost;
        this.mHandler = mHandler;
        this.mDebuggable = mDebuggable;
        this.mDefaultDozeBrightness = mDefaultDozeBrightness;
        this.mSensorToBrightness = mSensorToBrightness;
        this.mSensorToScrimOpacity = mSensorToScrimOpacity;
        if (mDebuggable) {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.android.systemui.doze.AOD_BRIGHTNESS");
            this.mBroadcastDispatcher.registerReceiverWithHandler(this, intentFilter, mHandler, UserHandle.ALL);
        }
    }
    
    public DozeScreenBrightness(final Context context, final Service service, final SensorManager sensorManager, final Sensor sensor, final BroadcastDispatcher broadcastDispatcher, final DozeHost dozeHost, final Handler handler, final AlwaysOnDisplayPolicy alwaysOnDisplayPolicy) {
        this(context, service, sensorManager, sensor, broadcastDispatcher, dozeHost, handler, context.getResources().getInteger(17694884), alwaysOnDisplayPolicy.screenBrightnessArray, alwaysOnDisplayPolicy.dimmingScrimArray, DozeScreenBrightness.DEBUG_AOD_BRIGHTNESS);
    }
    
    private int clampToUserSetting(final int a) {
        return Math.min(a, Settings$System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness", Integer.MAX_VALUE, -2));
    }
    
    private int computeBrightness(final int n) {
        if (n >= 0) {
            final int[] mSensorToBrightness = this.mSensorToBrightness;
            if (n < mSensorToBrightness.length) {
                return mSensorToBrightness[n];
            }
        }
        return -1;
    }
    
    private int computeScrimOpacity(final int n) {
        if (n >= 0) {
            final int[] mSensorToScrimOpacity = this.mSensorToScrimOpacity;
            if (n < mSensorToScrimOpacity.length) {
                return mSensorToScrimOpacity[n];
            }
        }
        return -1;
    }
    
    private void onDestroy() {
        this.setLightSensorEnabled(false);
        if (this.mDebuggable) {
            this.mBroadcastDispatcher.unregisterReceiver(this);
        }
    }
    
    private void resetBrightnessToDefault() {
        this.mDozeService.setDozeScreenBrightness(this.clampToUserSetting(this.mDefaultDozeBrightness));
        this.mDozeHost.setAodDimmingScrim(0.0f);
    }
    
    private void setLightSensorEnabled(final boolean b) {
        if (b && !this.mRegistered) {
            final Sensor mLightSensor = this.mLightSensor;
            if (mLightSensor != null) {
                this.mRegistered = this.mSensorManager.registerListener((SensorEventListener)this, mLightSensor, 3, this.mHandler);
                this.mLastSensorValue = -1;
                return;
            }
        }
        if (!b && this.mRegistered) {
            this.mSensorManager.unregisterListener((SensorEventListener)this);
            this.mRegistered = false;
            this.mLastSensorValue = -1;
        }
    }
    
    private void setPaused(final boolean mPaused) {
        if (this.mPaused != mPaused) {
            this.mPaused = mPaused;
            this.updateBrightnessAndReady(false);
        }
    }
    
    private void setScreenOff(final boolean mScreenOff) {
        if (this.mScreenOff != mScreenOff) {
            this.mScreenOff = mScreenOff;
            this.updateBrightnessAndReady(true);
        }
    }
    
    private void updateBrightnessAndReady(final boolean b) {
        int computeScrimOpacity = -1;
        if (b || this.mRegistered || this.mDebugBrightnessBucket != -1) {
            int n;
            if ((n = this.mDebugBrightnessBucket) == -1) {
                n = this.mLastSensorValue;
            }
            final int computeBrightness = this.computeBrightness(n);
            final boolean b2 = computeBrightness > 0;
            if (b2) {
                this.mDozeService.setDozeScreenBrightness(this.clampToUserSetting(computeBrightness));
            }
            if (this.mLightSensor == null) {
                computeScrimOpacity = 0;
            }
            else if (b2) {
                computeScrimOpacity = this.computeScrimOpacity(n);
            }
            if (computeScrimOpacity >= 0) {
                this.mDozeHost.setAodDimmingScrim(computeScrimOpacity / 255.0f);
            }
        }
    }
    
    public void onAccuracyChanged(final Sensor sensor, final int n) {
    }
    
    public void onReceive(final Context context, final Intent intent) {
        this.mDebugBrightnessBucket = intent.getIntExtra("brightness_bucket", -1);
        this.updateBrightnessAndReady(false);
    }
    
    public void onSensorChanged(final SensorEvent sensorEvent) {
        final StringBuilder sb = new StringBuilder();
        sb.append("DozeScreenBrightness.onSensorChanged");
        sb.append(sensorEvent.values[0]);
        Trace.beginSection(sb.toString());
        try {
            if (this.mRegistered) {
                this.mLastSensorValue = (int)sensorEvent.values[0];
                this.updateBrightnessAndReady(false);
            }
        }
        finally {
            Trace.endSection();
        }
    }
    
    public void transitionTo(final State state, final State state2) {
        final int n = DozeScreenBrightness$1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state2.ordinal()];
        final boolean b = false;
        switch (n) {
            case 6: {
                this.onDestroy();
                break;
            }
            case 5: {
                this.setLightSensorEnabled(false);
                this.resetBrightnessToDefault();
                break;
            }
            case 2:
            case 3:
            case 4: {
                this.setLightSensorEnabled(true);
                break;
            }
            case 1: {
                this.resetBrightnessToDefault();
                break;
            }
        }
        if (state2 != State.FINISH) {
            this.setScreenOff(state2 == State.DOZE);
            boolean paused = b;
            if (state2 == State.DOZE_AOD_PAUSED) {
                paused = true;
            }
            this.setPaused(paused);
        }
    }
}
