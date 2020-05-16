// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import android.content.IntentFilter;
import android.content.Intent;
import android.content.BroadcastReceiver;
import com.android.internal.annotations.VisibleForTesting;
import android.text.format.Formatter;
import java.io.PrintWriter;
import com.android.systemui.util.Assert;
import android.util.Log;
import android.os.SystemClock;
import android.metrics.LogMaker;
import java.util.function.Consumer;
import com.android.systemui.Dependency;
import android.os.Handler;
import android.app.AlarmManager;
import com.android.systemui.util.wakelock.WakeLock;
import android.app.UiModeManager;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.android.systemui.util.sensors.ProximitySensor;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.dock.DockManager;
import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;
import com.android.systemui.broadcast.BroadcastDispatcher;

public class DozeTriggers implements Part
{
    private static final boolean DEBUG;
    private static boolean sWakeDisplaySensorState;
    private final boolean mAllowPulseTriggers;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final TriggerReceiver mBroadcastReceiver;
    private final AmbientDisplayConfiguration mConfig;
    private final Context mContext;
    private final DockEventListener mDockEventListener;
    private final DockManager mDockManager;
    private final DozeHost mDozeHost;
    private final DozeLog mDozeLog;
    private final DozeParameters mDozeParameters;
    private final DozeSensors mDozeSensors;
    private DozeHost.Callback mHostCallback;
    private final DozeMachine mMachine;
    private final MetricsLogger mMetricsLogger;
    private long mNotificationPulseTime;
    private final ProximitySensor.ProximityCheck mProxCheck;
    private boolean mPulsePending;
    private final AsyncSensorManager mSensorManager;
    private final UiModeManager mUiModeManager;
    private final WakeLock mWakeLock;
    
    static {
        DEBUG = DozeService.DEBUG;
        DozeTriggers.sWakeDisplaySensorState = true;
    }
    
    public DozeTriggers(final Context mContext, final DozeMachine mMachine, final DozeHost mDozeHost, final AlarmManager alarmManager, final AmbientDisplayConfiguration mConfig, final DozeParameters mDozeParameters, final AsyncSensorManager mSensorManager, final Handler handler, final WakeLock mWakeLock, final boolean mAllowPulseTriggers, final DockManager mDockManager, final ProximitySensor proximitySensor, final DozeLog mDozeLog, final BroadcastDispatcher mBroadcastDispatcher) {
        this.mBroadcastReceiver = new TriggerReceiver();
        this.mDockEventListener = new DockEventListener();
        this.mMetricsLogger = Dependency.get(MetricsLogger.class);
        this.mHostCallback = new DozeHost.Callback() {
            @Override
            public void onDozeSuppressedChanged(final boolean b) {
                DozeMachine.State state;
                if (DozeTriggers.this.mConfig.alwaysOnEnabled(-2) && !b) {
                    state = State.DOZE_AOD;
                }
                else {
                    state = State.DOZE;
                }
                DozeTriggers.this.mMachine.requestState(state);
            }
            
            @Override
            public void onNotificationAlerted(final Runnable runnable) {
                DozeTriggers.this.onNotification(runnable);
            }
            
            @Override
            public void onPowerSaveChanged(final boolean b) {
                if (DozeTriggers.this.mDozeHost.isPowerSaveActive()) {
                    DozeTriggers.this.mMachine.requestState(State.DOZE);
                }
                else if (DozeTriggers.this.mMachine.getState() == State.DOZE && DozeTriggers.this.mConfig.alwaysOnEnabled(-2)) {
                    DozeTriggers.this.mMachine.requestState(State.DOZE_AOD);
                }
            }
        };
        this.mContext = mContext;
        this.mMachine = mMachine;
        this.mDozeHost = mDozeHost;
        this.mConfig = mConfig;
        this.mDozeParameters = mDozeParameters;
        this.mSensorManager = mSensorManager;
        this.mWakeLock = mWakeLock;
        this.mAllowPulseTriggers = mAllowPulseTriggers;
        this.mDozeSensors = new DozeSensors(mContext, alarmManager, this.mSensorManager, mDozeParameters, mConfig, mWakeLock, (DozeSensors.Callback)new _$$Lambda$XuSeOmLZ56lHJGoIP26_sIwbcBM(this), new _$$Lambda$DozeTriggers$ulqUMEXi8OgK7771oZ9BOr21BBk(this), mDozeLog);
        this.mUiModeManager = (UiModeManager)this.mContext.getSystemService((Class)UiModeManager.class);
        this.mDockManager = mDockManager;
        this.mProxCheck = new ProximitySensor.ProximityCheck(proximitySensor, handler);
        this.mDozeLog = mDozeLog;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
    }
    
    private boolean canPulse() {
        return this.mMachine.getState() == State.DOZE || this.mMachine.getState() == State.DOZE_AOD || this.mMachine.getState() == State.DOZE_AOD_DOCKED;
    }
    
    private void checkTriggersAtInit() {
        if (this.mUiModeManager.getCurrentModeType() == 3 || this.mDozeHost.isBlockingDoze() || !this.mDozeHost.isProvisioned()) {
            this.mMachine.requestState(State.FINISH);
        }
    }
    
    private void continuePulseRequest(final int n) {
        this.mPulsePending = false;
        if (!this.mDozeHost.isPulsingBlocked() && this.canPulse()) {
            this.mMachine.requestPulse(n);
            return;
        }
        this.mDozeLog.tracePulseDropped(this.mPulsePending, this.mMachine.getState(), this.mDozeHost.isPulsingBlocked());
    }
    
    private void gentleWakeUp(final int subtype) {
        this.mMetricsLogger.write(new LogMaker(223).setType(6).setSubtype(subtype));
        if (this.mDozeParameters.getDisplayNeedsBlanking()) {
            this.mDozeHost.setAodDimmingScrim(1.0f);
        }
        this.mMachine.wakeUp();
    }
    
    private void onNotification(final Runnable runnable) {
        if (DozeMachine.DEBUG) {
            Log.d("DozeTriggers", "requestNotificationPulse");
        }
        if (!DozeTriggers.sWakeDisplaySensorState) {
            Log.d("DozeTriggers", "Wake display false. Pulse denied.");
            runIfNotNull(runnable);
            this.mDozeLog.tracePulseDropped("wakeDisplaySensor");
            return;
        }
        this.mNotificationPulseTime = SystemClock.elapsedRealtime();
        if (!this.mConfig.pulseOnNotificationEnabled(-2)) {
            runIfNotNull(runnable);
            this.mDozeLog.tracePulseDropped("pulseOnNotificationsDisabled");
            return;
        }
        if (this.mDozeHost.isDozeSuppressed()) {
            runIfNotNull(runnable);
            this.mDozeLog.tracePulseDropped("dozeSuppressed");
            return;
        }
        this.requestPulse(1, false, runnable);
        this.mDozeLog.traceNotificationPulse();
    }
    
    private void onProximityFar(final boolean b) {
        if (this.mMachine.isExecutingTransition()) {
            Log.w("DozeTriggers", "onProximityFar called during transition. Ignoring sensor response.");
            return;
        }
        final boolean b2 = b ^ true;
        final DozeMachine.State state = this.mMachine.getState();
        final DozeMachine.State doze_AOD_PAUSED = State.DOZE_AOD_PAUSED;
        boolean b3 = false;
        final boolean b4 = state == doze_AOD_PAUSED;
        final boolean b5 = state == State.DOZE_AOD_PAUSING;
        if (state == State.DOZE_AOD) {
            b3 = true;
        }
        if (state == State.DOZE_PULSING || state == State.DOZE_PULSING_BRIGHT) {
            if (DozeTriggers.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Prox changed, ignore touch = ");
                sb.append(b2);
                Log.i("DozeTriggers", sb.toString());
            }
            this.mDozeHost.onIgnoreTouchWhilePulsing(b2);
        }
        if (b && (b4 || b5)) {
            if (DozeTriggers.DEBUG) {
                Log.i("DozeTriggers", "Prox FAR, unpausing AOD");
            }
            this.mMachine.requestState(State.DOZE_AOD);
        }
        else if (b2 && b3) {
            if (DozeTriggers.DEBUG) {
                Log.i("DozeTriggers", "Prox NEAR, pausing AOD");
            }
            this.mMachine.requestState(State.DOZE_AOD_PAUSING);
        }
    }
    
    private void onWakeScreen(final boolean sWakeDisplaySensorState, final State state) {
        this.mDozeLog.traceWakeDisplay(sWakeDisplaySensorState);
        DozeTriggers.sWakeDisplaySensorState = sWakeDisplaySensorState;
        boolean b = true;
        if (sWakeDisplaySensorState) {
            this.proximityCheckThenCall(new _$$Lambda$DozeTriggers$HZx5UzHarvs5L6_DXQmh_vvZFRQ(this, state), true, 7);
        }
        else {
            final boolean b2 = state == State.DOZE_AOD_PAUSED;
            if (state != State.DOZE_AOD_PAUSING) {
                b = false;
            }
            if (!b && !b2) {
                this.mMachine.requestState(State.DOZE);
                this.mMetricsLogger.write(new LogMaker(223).setType(2).setSubtype(7));
            }
        }
    }
    
    private void proximityCheckThenCall(final Consumer<Boolean> consumer, final boolean b, final int n) {
        final Boolean proximityCurrentlyNear = this.mDozeSensors.isProximityCurrentlyNear();
        if (b) {
            consumer.accept(null);
        }
        else if (proximityCurrentlyNear != null) {
            consumer.accept(proximityCurrentlyNear);
        }
        else {
            this.mProxCheck.check(500L, new _$$Lambda$DozeTriggers$7dHaL16_QO2EYQ_3R1TKZzEi3lA(this, SystemClock.uptimeMillis(), n, consumer));
            this.mWakeLock.acquire("DozeTriggers");
        }
    }
    
    private void requestPulse(final int subtype, final boolean b, final Runnable runnable) {
        Assert.isMainThread();
        this.mDozeHost.extendPulse(subtype);
        if (this.mMachine.getState() == State.DOZE_PULSING && subtype == 8) {
            this.mMachine.requestState(State.DOZE_PULSING_BRIGHT);
            return;
        }
        if (!this.mPulsePending && this.mAllowPulseTriggers && this.canPulse()) {
            final boolean b2 = true;
            this.mPulsePending = true;
            final _$$Lambda$DozeTriggers$7efrn9gY_OB_Pbk9skV2oR0_AOE $$Lambda$DozeTriggers$7efrn9gY_OB_Pbk9skV2oR0_AOE = new _$$Lambda$DozeTriggers$7efrn9gY_OB_Pbk9skV2oR0_AOE(this, runnable, subtype);
            boolean b3 = b2;
            if (this.mDozeParameters.getProxCheckBeforePulse()) {
                b3 = (b && b2);
            }
            this.proximityCheckThenCall($$Lambda$DozeTriggers$7efrn9gY_OB_Pbk9skV2oR0_AOE, b3, subtype);
            this.mMetricsLogger.write(new LogMaker(223).setType(6).setSubtype(subtype));
            return;
        }
        if (this.mAllowPulseTriggers) {
            this.mDozeLog.tracePulseDropped(this.mPulsePending, this.mMachine.getState(), this.mDozeHost.isPulsingBlocked());
        }
        runIfNotNull(runnable);
    }
    
    private static void runIfNotNull(final Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }
    
    @Override
    public void dump(final PrintWriter printWriter) {
        printWriter.print(" notificationPulseTime=");
        printWriter.println(Formatter.formatShortElapsedTime(this.mContext, this.mNotificationPulseTime));
        final StringBuilder sb = new StringBuilder();
        sb.append(" pulsePending=");
        sb.append(this.mPulsePending);
        printWriter.println(sb.toString());
        printWriter.println("DozeSensors:");
        this.mDozeSensors.dump(printWriter);
    }
    
    @VisibleForTesting
    void onSensor(final int n, final float n2, final float n3, final float[] array) {
        final boolean b = false;
        final boolean b2 = n == 4;
        final boolean b3 = n == 9;
        final boolean b4 = n == 3;
        final boolean b5 = n == 5;
        final boolean b6 = n == 7;
        final boolean b7 = n == 8;
        final boolean b8 = array != null && array.length > 0 && array[0] != 0.0f;
        Enum<DozeMachine.State> state = null;
        if (b6) {
            if (!this.mMachine.isExecutingTransition()) {
                state = this.mMachine.getState();
            }
            this.onWakeScreen(b8, (State)state);
        }
        else if (b5) {
            this.requestPulse(n, true, null);
        }
        else if (b7) {
            if (b8) {
                this.requestPulse(n, true, null);
            }
        }
        else {
            this.proximityCheckThenCall(new _$$Lambda$DozeTriggers$_9uGVeOllRSk5IFkZMhDAbIz6Gw(this, b2, b3, n2, n3, n, b4), true, n);
        }
        if (b4) {
            boolean b9 = b;
            if (SystemClock.elapsedRealtime() - this.mNotificationPulseTime < this.mDozeParameters.getPickupVibrationThreshold()) {
                b9 = true;
            }
            this.mDozeLog.tracePickupWakeUp(b9);
        }
    }
    
    @Override
    public void transitionTo(final State state, final State state2) {
        switch (DozeTriggers$2.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state2.ordinal()]) {
            case 10: {
                this.mBroadcastReceiver.unregister(this.mBroadcastDispatcher);
                this.mDozeHost.removeCallback(this.mHostCallback);
                this.mDockManager.removeListener((DockManager.DockEventListener)this.mDockEventListener);
                this.mDozeSensors.setListening(false);
                this.mDozeSensors.setProxListening(false);
                break;
            }
            case 9: {
                this.mDozeSensors.requestTemporaryDisable();
                this.mDozeSensors.updateListening();
                break;
            }
            case 6:
            case 7:
            case 8: {
                this.mDozeSensors.setTouchscreenSensorsListening(false);
                this.mDozeSensors.setProxListening(true);
                this.mDozeSensors.setPaused(false);
                break;
            }
            case 4:
            case 5: {
                this.mDozeSensors.setProxListening(true);
                this.mDozeSensors.setPaused(true);
                break;
            }
            case 2:
            case 3: {
                this.mDozeSensors.setProxListening(state2 != State.DOZE);
                this.mDozeSensors.setListening(true);
                this.mDozeSensors.setPaused(false);
                if (state2 == State.DOZE_AOD && !DozeTriggers.sWakeDisplaySensorState) {
                    this.onWakeScreen(false, state2);
                    break;
                }
                break;
            }
            case 1: {
                this.mBroadcastReceiver.register(this.mBroadcastDispatcher);
                this.mDozeHost.addCallback(this.mHostCallback);
                this.mDockManager.addListener((DockManager.DockEventListener)this.mDockEventListener);
                this.mDozeSensors.requestTemporaryDisable();
                this.checkTriggersAtInit();
                break;
            }
        }
    }
    
    private class DockEventListener implements DockManager.DockEventListener
    {
        @Override
        public void onEvent(final int i) {
            if (DozeTriggers.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("dock event = ");
                sb.append(i);
                Log.d("DozeTriggers", sb.toString());
            }
            if (i != 0) {
                if (i == 1 || i == 2) {
                    DozeTriggers.this.mDozeSensors.ignoreTouchScreenSensorsSettingInterferingWithDocking(true);
                }
            }
            else {
                DozeTriggers.this.mDozeSensors.ignoreTouchScreenSensorsSettingInterferingWithDocking(false);
            }
        }
    }
    
    private class TriggerReceiver extends BroadcastReceiver
    {
        private boolean mRegistered;
        
        public void onReceive(final Context context, final Intent intent) {
            if ("com.android.systemui.doze.pulse".equals(intent.getAction())) {
                if (DozeMachine.DEBUG) {
                    Log.d("DozeTriggers", "Received pulse intent");
                }
                DozeTriggers.this.requestPulse(0, false, null);
            }
            if (UiModeManager.ACTION_ENTER_CAR_MODE.equals(intent.getAction())) {
                DozeTriggers.this.mMachine.requestState(State.FINISH);
            }
            if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
                DozeTriggers.this.mDozeSensors.onUserSwitched();
            }
        }
        
        public void register(final BroadcastDispatcher broadcastDispatcher) {
            if (this.mRegistered) {
                return;
            }
            final IntentFilter intentFilter = new IntentFilter("com.android.systemui.doze.pulse");
            intentFilter.addAction(UiModeManager.ACTION_ENTER_CAR_MODE);
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            broadcastDispatcher.registerReceiver(this, intentFilter);
            this.mRegistered = true;
        }
        
        public void unregister(final BroadcastDispatcher broadcastDispatcher) {
            if (!this.mRegistered) {
                return;
            }
            broadcastDispatcher.unregisterReceiver(this);
            this.mRegistered = false;
        }
    }
}
