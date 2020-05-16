// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import com.android.systemui.statusbar.phone.DozeParameters;
import java.io.PrintWriter;
import com.android.internal.util.Preconditions;
import android.os.Trace;
import android.util.Log;
import com.android.systemui.util.Assert;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.util.wakelock.WakeLock;
import java.util.ArrayList;
import com.android.systemui.dock.DockManager;
import android.hardware.display.AmbientDisplayConfiguration;
import com.android.systemui.statusbar.policy.BatteryController;

public class DozeMachine
{
    static final boolean DEBUG;
    private final BatteryController mBatteryController;
    private final AmbientDisplayConfiguration mConfig;
    private DockManager mDockManager;
    private final DozeHost mDozeHost;
    private final DozeLog mDozeLog;
    private final Service mDozeService;
    private Part[] mParts;
    private int mPulseReason;
    private final ArrayList<State> mQueuedRequests;
    private State mState;
    private final WakeLock mWakeLock;
    private boolean mWakeLockHeldForCurrentState;
    private final WakefulnessLifecycle mWakefulnessLifecycle;
    
    static {
        DEBUG = DozeService.DEBUG;
    }
    
    public DozeMachine(final Service mDozeService, final AmbientDisplayConfiguration mConfig, final WakeLock mWakeLock, final WakefulnessLifecycle mWakefulnessLifecycle, final BatteryController mBatteryController, final DozeLog mDozeLog, final DockManager mDockManager, final DozeHost mDozeHost) {
        this.mQueuedRequests = new ArrayList<State>();
        this.mState = State.UNINITIALIZED;
        this.mWakeLockHeldForCurrentState = false;
        this.mDozeService = mDozeService;
        this.mConfig = mConfig;
        this.mWakefulnessLifecycle = mWakefulnessLifecycle;
        this.mWakeLock = mWakeLock;
        this.mBatteryController = mBatteryController;
        this.mDozeLog = mDozeLog;
        this.mDockManager = mDockManager;
        this.mDozeHost = mDozeHost;
    }
    
    private void performTransitionOnComponents(final State state, final State state2) {
        final Part[] mParts = this.mParts;
        for (int length = mParts.length, i = 0; i < length; ++i) {
            mParts[i].transitionTo(state, state2);
        }
        if (DozeMachine$1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state2.ordinal()] == 11) {
            this.mDozeService.finish();
        }
    }
    
    private void requestState(final State state, final int n) {
        Assert.isMainThread();
        if (DozeMachine.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("request: current=");
            sb.append(this.mState);
            sb.append(" req=");
            sb.append(state);
            Log.i("DozeMachine", sb.toString(), new Throwable("here"));
        }
        final boolean executingTransition = this.isExecutingTransition();
        this.mQueuedRequests.add(state);
        if (executingTransition ^ true) {
            this.mWakeLock.acquire("DozeMachine#requestState");
            for (int i = 0; i < this.mQueuedRequests.size(); ++i) {
                this.transitionTo(this.mQueuedRequests.get(i), n);
            }
            this.mQueuedRequests.clear();
            this.mWakeLock.release("DozeMachine#requestState");
        }
    }
    
    private void resolveIntermediateState(State state) {
        final int n = DozeMachine$1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state.ordinal()];
        if (n == 10 || n == 12) {
            final int wakefulness = this.mWakefulnessLifecycle.getWakefulness();
            if (state != State.INITIALIZED && (wakefulness == 2 || wakefulness == 1)) {
                state = State.FINISH;
            }
            else if (this.mDockManager.isDocked()) {
                if (this.mDockManager.isHidden()) {
                    state = State.DOZE;
                }
                else {
                    state = State.DOZE_AOD_DOCKED;
                }
            }
            else if (this.mConfig.alwaysOnEnabled(-2)) {
                state = State.DOZE_AOD;
            }
            else {
                state = State.DOZE;
            }
            this.transitionTo(state, -1);
        }
    }
    
    private State transitionPolicy(final State obj) {
        final State mState = this.mState;
        final State finish = State.FINISH;
        if (mState == finish) {
            return finish;
        }
        if (this.mDozeHost.isDozeSuppressed() && obj.isAlwaysOn()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Doze is suppressed. Suppressing state: ");
            sb.append(obj);
            Log.i("DozeMachine", sb.toString());
            this.mDozeLog.traceDozeSuppressed(obj);
            return State.DOZE;
        }
        final State mState2 = this.mState;
        if ((mState2 == State.DOZE_AOD_PAUSED || mState2 == State.DOZE_AOD_PAUSING || mState2 == State.DOZE_AOD || mState2 == State.DOZE || mState2 == State.DOZE_AOD_DOCKED) && obj == State.DOZE_PULSE_DONE) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Dropping pulse done because current state is already done: ");
            sb2.append(this.mState);
            Log.i("DozeMachine", sb2.toString());
            return this.mState;
        }
        if (obj == State.DOZE_AOD && this.mBatteryController.isAodPowerSave()) {
            return State.DOZE;
        }
        if (obj == State.DOZE_REQUEST_PULSE && !this.mState.canPulse()) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("Dropping pulse request because current state can't pulse: ");
            sb3.append(this.mState);
            Log.i("DozeMachine", sb3.toString());
            return this.mState;
        }
        return obj;
    }
    
    private void transitionTo(State mState, final int n) {
        final State transitionPolicy = this.transitionPolicy(mState);
        if (DozeMachine.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("transition: old=");
            sb.append(this.mState);
            sb.append(" req=");
            sb.append(mState);
            sb.append(" new=");
            sb.append(transitionPolicy);
            Log.i("DozeMachine", sb.toString());
        }
        if (transitionPolicy == this.mState) {
            return;
        }
        this.validateTransition(transitionPolicy);
        mState = this.mState;
        this.mState = transitionPolicy;
        this.mDozeLog.traceState(transitionPolicy);
        Trace.traceCounter(4096L, "doze_machine_state", transitionPolicy.ordinal());
        this.updatePulseReason(transitionPolicy, mState, n);
        this.performTransitionOnComponents(mState, transitionPolicy);
        this.updateWakeLockState(transitionPolicy);
        this.resolveIntermediateState(transitionPolicy);
    }
    
    private void updatePulseReason(final State state, final State state2, final int mPulseReason) {
        if (state == State.DOZE_REQUEST_PULSE) {
            this.mPulseReason = mPulseReason;
        }
        else if (state2 == State.DOZE_PULSE_DONE) {
            this.mPulseReason = -1;
        }
    }
    
    private void updateWakeLockState(final State state) {
        final boolean staysAwake = state.staysAwake();
        if (this.mWakeLockHeldForCurrentState && !staysAwake) {
            this.mWakeLock.release("DozeMachine#heldForState");
            this.mWakeLockHeldForCurrentState = false;
        }
        else if (!this.mWakeLockHeldForCurrentState && staysAwake) {
            this.mWakeLock.acquire("DozeMachine#heldForState");
            this.mWakeLockHeldForCurrentState = true;
        }
    }
    
    private void validateTransition(final State obj) {
        try {
            final int n = DozeMachine$1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[this.mState.ordinal()];
            final boolean b = true;
            final boolean b2 = true;
            final boolean b3 = true;
            if (n != 9) {
                if (n == 11) {
                    Preconditions.checkState(obj == State.FINISH);
                }
            }
            else {
                Preconditions.checkState(obj == State.INITIALIZED);
            }
            final int n2 = DozeMachine$1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[obj.ordinal()];
            if (n2 != 7) {
                if (n2 != 12) {
                    if (n2 == 9) {
                        throw new IllegalArgumentException("can't transition to UNINITIALIZED");
                    }
                    if (n2 == 10) {
                        Preconditions.checkState(this.mState == State.UNINITIALIZED && b3);
                    }
                }
                else {
                    boolean b4 = b;
                    if (this.mState != State.DOZE_REQUEST_PULSE) {
                        b4 = b;
                        if (this.mState != State.DOZE_PULSING) {
                            b4 = (this.mState == State.DOZE_PULSING_BRIGHT && b);
                        }
                    }
                    Preconditions.checkState(b4);
                }
            }
            else {
                Preconditions.checkState(this.mState == State.DOZE_REQUEST_PULSE && b2);
            }
        }
        catch (RuntimeException cause) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Illegal Transition: ");
            sb.append(this.mState);
            sb.append(" -> ");
            sb.append(obj);
            throw new IllegalStateException(sb.toString(), cause);
        }
    }
    
    public void dump(final PrintWriter printWriter) {
        printWriter.print(" state=");
        printWriter.println(this.mState);
        printWriter.print(" wakeLockHeldForCurrentState=");
        printWriter.println(this.mWakeLockHeldForCurrentState);
        printWriter.print(" wakeLock=");
        printWriter.println(this.mWakeLock);
        printWriter.println("Parts:");
        final Part[] mParts = this.mParts;
        for (int length = mParts.length, i = 0; i < length; ++i) {
            mParts[i].dump(printWriter);
        }
    }
    
    public int getPulseReason() {
        Assert.isMainThread();
        final State mState = this.mState;
        final boolean b = mState == State.DOZE_REQUEST_PULSE || mState == State.DOZE_PULSING || mState == State.DOZE_PULSING_BRIGHT || mState == State.DOZE_PULSE_DONE;
        final StringBuilder sb = new StringBuilder();
        sb.append("must be in pulsing state, but is ");
        sb.append(this.mState);
        Preconditions.checkState(b, sb.toString());
        return this.mPulseReason;
    }
    
    public State getState() {
        Assert.isMainThread();
        if (!this.isExecutingTransition()) {
            return this.mState;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Cannot get state because there were pending transitions: ");
        sb.append(this.mQueuedRequests.toString());
        throw new IllegalStateException(sb.toString());
    }
    
    public boolean isExecutingTransition() {
        return this.mQueuedRequests.isEmpty() ^ true;
    }
    
    public void requestPulse(final int n) {
        Preconditions.checkState(this.isExecutingTransition() ^ true);
        this.requestState(State.DOZE_REQUEST_PULSE, n);
    }
    
    public void requestState(final State state) {
        Preconditions.checkArgument(state != State.DOZE_REQUEST_PULSE);
        this.requestState(state, -1);
    }
    
    public void setParts(final Part[] mParts) {
        Preconditions.checkState(this.mParts == null);
        this.mParts = mParts;
    }
    
    public void wakeUp() {
        this.mDozeService.requestWakeUp();
    }
    
    public interface Part
    {
        default void dump(final PrintWriter printWriter) {
        }
        
        void transitionTo(final State p0, final State p1);
    }
    
    public interface Service
    {
        void finish();
        
        void requestWakeUp();
        
        void setDozeScreenBrightness(final int p0);
        
        void setDozeScreenState(final int p0);
        
        public static class Delegate implements Service
        {
            private final Service mDelegate;
            
            public Delegate(final Service mDelegate) {
                this.mDelegate = mDelegate;
            }
            
            @Override
            public void finish() {
                this.mDelegate.finish();
            }
            
            @Override
            public void requestWakeUp() {
                this.mDelegate.requestWakeUp();
            }
            
            @Override
            public void setDozeScreenBrightness(final int dozeScreenBrightness) {
                this.mDelegate.setDozeScreenBrightness(dozeScreenBrightness);
            }
            
            @Override
            public void setDozeScreenState(final int dozeScreenState) {
                this.mDelegate.setDozeScreenState(dozeScreenState);
            }
        }
    }
    
    public enum State
    {
        DOZE, 
        DOZE_AOD, 
        DOZE_AOD_DOCKED, 
        DOZE_AOD_PAUSED, 
        DOZE_AOD_PAUSING, 
        DOZE_PULSE_DONE, 
        DOZE_PULSING, 
        DOZE_PULSING_BRIGHT, 
        DOZE_REQUEST_PULSE, 
        FINISH, 
        INITIALIZED, 
        UNINITIALIZED;
        
        boolean canPulse() {
            final int n = DozeMachine$1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[this.ordinal()];
            return n == 1 || n == 2 || n == 3 || n == 4 || n == 5;
        }
        
        boolean isAlwaysOn() {
            return this == State.DOZE_AOD || this == State.DOZE_AOD_DOCKED;
        }
        
        int screenState(final DozeParameters dozeParameters) {
            final int n = DozeMachine$1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[this.ordinal()];
            int n2 = 2;
            switch (n) {
                default: {
                    return 0;
                }
                case 6:
                case 9:
                case 10: {
                    if (dozeParameters.shouldControlScreenOff()) {
                        n2 = n2;
                        return n2;
                    }
                    n2 = 1;
                    return n2;
                }
                case 5:
                case 7:
                case 8: {
                    return n2;
                }
                case 2:
                case 4: {
                    return 4;
                }
                case 1:
                case 3: {
                    return 1;
                }
            }
        }
        
        boolean staysAwake() {
            final int n = DozeMachine$1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[this.ordinal()];
            return n == 5 || n == 6 || n == 7 || n == 8;
        }
    }
}
