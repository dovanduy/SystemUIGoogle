// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import android.util.Log;
import java.io.PrintWriter;
import com.android.systemui.dock.DockManager;
import android.hardware.display.AmbientDisplayConfiguration;

public class DozeDockHandler implements Part
{
    private static final boolean DEBUG;
    private final AmbientDisplayConfiguration mConfig;
    private final DockEventListener mDockEventListener;
    private final DockManager mDockManager;
    private int mDockState;
    private final DozeMachine mMachine;
    
    static {
        DEBUG = DozeService.DEBUG;
    }
    
    DozeDockHandler(final AmbientDisplayConfiguration mConfig, final DozeMachine mMachine, final DockManager mDockManager) {
        this.mDockState = 0;
        this.mMachine = mMachine;
        this.mConfig = mConfig;
        this.mDockManager = mDockManager;
        this.mDockEventListener = new DockEventListener();
    }
    
    @Override
    public void dump(final PrintWriter printWriter) {
        printWriter.println("DozeDockHandler:");
        final StringBuilder sb = new StringBuilder();
        sb.append(" dockState=");
        sb.append(this.mDockState);
        printWriter.println(sb.toString());
    }
    
    @Override
    public void transitionTo(final State state, final State state2) {
        final int n = DozeDockHandler$1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state2.ordinal()];
        if (n != 1) {
            if (n == 2) {
                this.mDockEventListener.unregister();
            }
        }
        else {
            this.mDockEventListener.register();
        }
    }
    
    private class DockEventListener implements DockManager.DockEventListener
    {
        private boolean mRegistered;
        
        private boolean isPulsing() {
            final DozeMachine.State state = DozeDockHandler.this.mMachine.getState();
            return state == State.DOZE_REQUEST_PULSE || state == State.DOZE_PULSING || state == State.DOZE_PULSING_BRIGHT;
        }
        
        @Override
        public void onEvent(int access$200) {
            if (DozeDockHandler.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("dock event = ");
                sb.append(access$200);
                Log.d("DozeDockHandler", sb.toString());
            }
            DozeDockHandler.this.mDockState = access$200;
            if (this.isPulsing()) {
                return;
            }
            access$200 = DozeDockHandler.this.mDockState;
            DozeMachine.State state;
            if (access$200 != 0) {
                if (access$200 != 1) {
                    if (access$200 != 2) {
                        return;
                    }
                    state = State.DOZE;
                }
                else {
                    state = State.DOZE_AOD_DOCKED;
                }
            }
            else if (DozeDockHandler.this.mConfig.alwaysOnEnabled(-2)) {
                state = State.DOZE_AOD;
            }
            else {
                state = State.DOZE;
            }
            DozeDockHandler.this.mMachine.requestState(state);
        }
        
        void register() {
            if (this.mRegistered) {
                return;
            }
            if (DozeDockHandler.this.mDockManager != null) {
                DozeDockHandler.this.mDockManager.addListener((DockManager.DockEventListener)this);
            }
            this.mRegistered = true;
        }
        
        void unregister() {
            if (!this.mRegistered) {
                return;
            }
            if (DozeDockHandler.this.mDockManager != null) {
                DozeDockHandler.this.mDockManager.removeListener((DockManager.DockEventListener)this);
            }
            this.mRegistered = false;
        }
    }
}
