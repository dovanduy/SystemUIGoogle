// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import android.util.Log;
import com.android.systemui.util.wakelock.WakeLock;
import com.android.systemui.util.wakelock.SettableWakeLock;
import com.android.systemui.statusbar.phone.DozeParameters;
import android.os.Handler;

public class DozeScreenState implements Part
{
    private static final boolean DEBUG;
    private final Runnable mApplyPendingScreenState;
    private final DozeHost mDozeHost;
    private final Service mDozeService;
    private final Handler mHandler;
    private final DozeParameters mParameters;
    private int mPendingScreenState;
    private SettableWakeLock mWakeLock;
    
    static {
        DEBUG = DozeService.DEBUG;
    }
    
    public DozeScreenState(final Service mDozeService, final Handler mHandler, final DozeHost mDozeHost, final DozeParameters mParameters, final WakeLock wakeLock) {
        this.mApplyPendingScreenState = new _$$Lambda$DozeScreenState$eRrLSFQgxPfG2I_jJDfdCLwKzVE(this);
        this.mPendingScreenState = 0;
        this.mDozeService = mDozeService;
        this.mHandler = mHandler;
        this.mParameters = mParameters;
        this.mDozeHost = mDozeHost;
        this.mWakeLock = new SettableWakeLock(wakeLock, "DozeScreenState");
    }
    
    private void applyPendingScreenState() {
        this.applyScreenState(this.mPendingScreenState);
        this.mPendingScreenState = 0;
    }
    
    private void applyScreenState(final int n) {
        if (n != 0) {
            if (DozeScreenState.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("setDozeScreenState(");
                sb.append(n);
                sb.append(")");
                Log.d("DozeScreenState", sb.toString());
            }
            this.mDozeService.setDozeScreenState(n);
            this.mPendingScreenState = 0;
            this.mWakeLock.setAcquired(false);
        }
    }
    
    @Override
    public void transitionTo(final State state, final State state2) {
        final int screenState = state2.screenState(this.mParameters);
        this.mDozeHost.cancelGentleSleep();
        final DozeMachine.State finish = State.FINISH;
        final int n = 0;
        if (state2 == finish) {
            this.mPendingScreenState = 0;
            this.mHandler.removeCallbacks(this.mApplyPendingScreenState);
            this.applyScreenState(screenState);
            this.mWakeLock.setAcquired(false);
            return;
        }
        if (screenState == 0) {
            return;
        }
        final boolean hasCallbacks = this.mHandler.hasCallbacks(this.mApplyPendingScreenState);
        final DozeMachine.State doze_PULSE_DONE = State.DOZE_PULSE_DONE;
        final int n2 = 1;
        final boolean b = state == doze_PULSE_DONE && state2.isAlwaysOn();
        final boolean b2 = (state == State.DOZE_AOD_PAUSED || state == State.DOZE) && state2.isAlwaysOn();
        final boolean b3 = (state.isAlwaysOn() && state2 == State.DOZE) || (state == State.DOZE_AOD_PAUSING && state2 == State.DOZE_AOD_PAUSED);
        final boolean b4 = state == State.INITIALIZED;
        if (!hasCallbacks && !b4 && !b && !b2) {
            if (b3) {
                this.mDozeHost.prepareForGentleSleep(new _$$Lambda$DozeScreenState$Q7dH1ne3iIuPaAubBMflNieFfwI(this, screenState));
            }
            else {
                this.applyScreenState(screenState);
            }
        }
        else {
            this.mPendingScreenState = screenState;
            int n3 = n;
            if (state2 == State.DOZE_AOD) {
                n3 = n;
                if (this.mParameters.shouldControlScreenOff()) {
                    n3 = n;
                    if (!b2) {
                        n3 = 1;
                    }
                }
            }
            if (n3 != 0) {
                this.mWakeLock.setAcquired(true);
            }
            if (!hasCallbacks) {
                if (DozeScreenState.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Display state changed to ");
                    sb.append(screenState);
                    sb.append(" delayed by ");
                    int i = n2;
                    if (n3 != 0) {
                        i = 4000;
                    }
                    sb.append(i);
                    Log.d("DozeScreenState", sb.toString());
                }
                if (n3 != 0) {
                    this.mHandler.postDelayed(this.mApplyPendingScreenState, 4000L);
                }
                else {
                    this.mHandler.post(this.mApplyPendingScreenState);
                }
            }
            else if (DozeScreenState.DEBUG) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Pending display state change to ");
                sb2.append(screenState);
                Log.d("DozeScreenState", sb2.toString());
            }
        }
    }
}
