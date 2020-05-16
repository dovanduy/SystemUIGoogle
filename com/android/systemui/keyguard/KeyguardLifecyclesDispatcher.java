// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import android.os.Message;
import android.os.Handler;

public class KeyguardLifecyclesDispatcher
{
    private Handler mHandler;
    private final ScreenLifecycle mScreenLifecycle;
    private final WakefulnessLifecycle mWakefulnessLifecycle;
    
    public KeyguardLifecyclesDispatcher(final ScreenLifecycle mScreenLifecycle, final WakefulnessLifecycle mWakefulnessLifecycle) {
        this.mHandler = new Handler() {
            public void handleMessage(final Message obj) {
                switch (obj.what) {
                    default: {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Unknown message: ");
                        sb.append(obj);
                        throw new IllegalArgumentException(sb.toString());
                    }
                    case 7: {
                        KeyguardLifecyclesDispatcher.this.mWakefulnessLifecycle.dispatchFinishedGoingToSleep();
                        break;
                    }
                    case 6: {
                        KeyguardLifecyclesDispatcher.this.mWakefulnessLifecycle.dispatchStartedGoingToSleep();
                        break;
                    }
                    case 5: {
                        KeyguardLifecyclesDispatcher.this.mWakefulnessLifecycle.dispatchFinishedWakingUp();
                        break;
                    }
                    case 4: {
                        KeyguardLifecyclesDispatcher.this.mWakefulnessLifecycle.dispatchStartedWakingUp();
                        break;
                    }
                    case 3: {
                        KeyguardLifecyclesDispatcher.this.mScreenLifecycle.dispatchScreenTurnedOff();
                        break;
                    }
                    case 2: {
                        KeyguardLifecyclesDispatcher.this.mScreenLifecycle.dispatchScreenTurningOff();
                        break;
                    }
                    case 1: {
                        KeyguardLifecyclesDispatcher.this.mScreenLifecycle.dispatchScreenTurnedOn();
                        break;
                    }
                    case 0: {
                        KeyguardLifecyclesDispatcher.this.mScreenLifecycle.dispatchScreenTurningOn();
                        break;
                    }
                }
            }
        };
        this.mScreenLifecycle = mScreenLifecycle;
        this.mWakefulnessLifecycle = mWakefulnessLifecycle;
    }
    
    void dispatch(final int n) {
        this.mHandler.obtainMessage(n).sendToTarget();
    }
}
