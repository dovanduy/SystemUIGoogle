// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import java.util.Objects;
import com.android.internal.annotations.VisibleForTesting;
import android.util.Log;
import android.text.format.Formatter;
import android.os.SystemClock;
import java.util.Calendar;
import android.app.AlarmManager$OnAlarmListener;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.app.AlarmManager;
import com.android.systemui.util.wakelock.WakeLock;
import com.android.systemui.util.AlarmTimeout;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import android.os.Handler;
import com.android.systemui.statusbar.phone.DozeParameters;
import android.content.Context;

public class DozeUi implements Part
{
    private final boolean mCanAnimateTransition;
    private final Context mContext;
    private final DozeLog mDozeLog;
    private final DozeParameters mDozeParameters;
    private final Handler mHandler;
    private final DozeHost mHost;
    private boolean mKeyguardShowing;
    private final KeyguardUpdateMonitorCallback mKeyguardVisibilityCallback;
    private long mLastTimeTickElapsed;
    private final DozeMachine mMachine;
    private final AlarmTimeout mTimeTicker;
    private final WakeLock mWakeLock;
    
    public DozeUi(final Context mContext, final AlarmManager alarmManager, final DozeMachine mMachine, final WakeLock mWakeLock, final DozeHost mHost, final Handler mHandler, final DozeParameters mDozeParameters, final KeyguardUpdateMonitor keyguardUpdateMonitor, final DozeLog mDozeLog) {
        this.mKeyguardVisibilityCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onKeyguardVisibilityChanged(final boolean b) {
                DozeUi.this.mKeyguardShowing = b;
                DozeUi.this.updateAnimateScreenOff();
            }
        };
        this.mLastTimeTickElapsed = 0L;
        this.mContext = mContext;
        this.mMachine = mMachine;
        this.mWakeLock = mWakeLock;
        this.mHost = mHost;
        this.mHandler = mHandler;
        this.mCanAnimateTransition = (mDozeParameters.getDisplayNeedsBlanking() ^ true);
        this.mDozeParameters = mDozeParameters;
        this.mTimeTicker = new AlarmTimeout(alarmManager, (AlarmManager$OnAlarmListener)new _$$Lambda$DozeUi$FO90hbI6xqXYUh2DtwuwM_uzJzs(this), "doze_time_tick", mHandler);
        keyguardUpdateMonitor.registerCallback(this.mKeyguardVisibilityCallback);
        this.mDozeLog = mDozeLog;
    }
    
    private void onTimeTick() {
        this.verifyLastTimeTick();
        this.mHost.dozeTimeTick();
        this.mHandler.post(this.mWakeLock.wrap((Runnable)_$$Lambda$DozeUi$lHTcknku1GKi6pFF17CHlz1K3H8.INSTANCE));
        this.scheduleTimeTick();
    }
    
    private void pulseWhileDozing(final int n) {
        this.mHost.pulseWhileDozing((DozeHost.PulseCallback)new DozeHost.PulseCallback() {
            @Override
            public void onPulseFinished() {
                DozeUi.this.mMachine.requestState(State.DOZE_PULSE_DONE);
            }
            
            @Override
            public void onPulseStarted() {
                try {
                    final DozeMachine access$200 = DozeUi.this.mMachine;
                    DozeMachine.State state;
                    if (n == 8) {
                        state = State.DOZE_PULSING_BRIGHT;
                    }
                    else {
                        state = State.DOZE_PULSING;
                    }
                    access$200.requestState(state);
                }
                catch (IllegalStateException ex) {}
            }
        }, n);
    }
    
    private long roundToNextMinute(final long timeInMillis) {
        final Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(timeInMillis);
        instance.set(14, 0);
        instance.set(13, 0);
        instance.add(12, 1);
        return instance.getTimeInMillis();
    }
    
    private void scheduleTimeTick() {
        if (this.mTimeTicker.isScheduled()) {
            return;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        final long n = this.roundToNextMinute(currentTimeMillis) - System.currentTimeMillis();
        if (this.mTimeTicker.schedule(n, 1)) {
            this.mDozeLog.traceTimeTickScheduled(currentTimeMillis, n + currentTimeMillis);
        }
        this.mLastTimeTickElapsed = SystemClock.elapsedRealtime();
    }
    
    private void unscheduleTimeTick() {
        if (!this.mTimeTicker.isScheduled()) {
            return;
        }
        this.verifyLastTimeTick();
        this.mTimeTicker.cancel();
    }
    
    private void updateAnimateScreenOff() {
        if (this.mCanAnimateTransition) {
            final boolean b = this.mDozeParameters.getAlwaysOn() && this.mKeyguardShowing && !this.mHost.isPowerSaveActive();
            this.mDozeParameters.setControlScreenOffAnimation(b);
            this.mHost.setAnimateScreenOff(b);
        }
    }
    
    private void updateAnimateWakeup(final State state) {
        final int n = DozeUi$3.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state.ordinal()];
        boolean animateWakeup = true;
        switch (n) {
            default: {
                final DozeHost mHost = this.mHost;
                if (!this.mCanAnimateTransition || !this.mDozeParameters.getAlwaysOn()) {
                    animateWakeup = false;
                }
                mHost.setAnimateWakeup(animateWakeup);
            }
            case 6:
            case 9:
            case 10:
            case 11: {
                this.mHost.setAnimateWakeup(true);
            }
            case 8: {}
        }
    }
    
    private void verifyLastTimeTick() {
        final long n = SystemClock.elapsedRealtime() - this.mLastTimeTickElapsed;
        if (n > 90000L) {
            final String formatShortElapsedTime = Formatter.formatShortElapsedTime(this.mContext, n);
            this.mDozeLog.traceMissedTick(formatShortElapsedTime);
            final StringBuilder sb = new StringBuilder();
            sb.append("Missed AOD time tick by ");
            sb.append(formatShortElapsedTime);
            Log.e("DozeMachine", sb.toString());
        }
    }
    
    @VisibleForTesting
    KeyguardUpdateMonitorCallback getKeyguardCallback() {
        return this.mKeyguardVisibilityCallback;
    }
    
    @Override
    public void transitionTo(final State state, final State state2) {
        switch (DozeUi$3.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state2.ordinal()]) {
            case 8: {
                this.mHost.stopDozing();
                this.unscheduleTimeTick();
                break;
            }
            case 7: {
                this.mHost.startDozing();
                break;
            }
            case 6: {
                this.scheduleTimeTick();
                this.pulseWhileDozing(this.mMachine.getPulseReason());
                break;
            }
            case 4:
            case 5: {
                this.unscheduleTimeTick();
                break;
            }
            case 3: {
                this.scheduleTimeTick();
                break;
            }
            case 1:
            case 2: {
                if (state == State.DOZE_AOD_PAUSED || state == State.DOZE) {
                    this.mHost.dozeTimeTick();
                    final Handler mHandler = this.mHandler;
                    final WakeLock mWakeLock = this.mWakeLock;
                    final DozeHost mHost = this.mHost;
                    Objects.requireNonNull(mHost);
                    mHandler.postDelayed(mWakeLock.wrap(new _$$Lambda$TvDuFxrq6WnRSNRP7k8oBY4uOBc(mHost)), 500L);
                }
                this.scheduleTimeTick();
                break;
            }
        }
        this.updateAnimateWakeup(state2);
    }
}
