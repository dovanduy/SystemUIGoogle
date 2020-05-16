// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.function.Consumer;
import android.os.Trace;
import com.android.systemui.Dumpable;

public class WakefulnessLifecycle extends Lifecycle<Observer> implements Dumpable
{
    private int mWakefulness;
    
    public WakefulnessLifecycle() {
        this.mWakefulness = 0;
    }
    
    private void setWakefulness(final int mWakefulness) {
        Trace.traceCounter(4096L, "wakefulness", this.mWakefulness = mWakefulness);
    }
    
    public void dispatchFinishedGoingToSleep() {
        if (this.getWakefulness() == 0) {
            return;
        }
        this.setWakefulness(0);
        this.dispatch((Consumer<Observer>)_$$Lambda$AKoGNPXjF07Pzc3_fzdQTCHgk6E.INSTANCE);
    }
    
    public void dispatchFinishedWakingUp() {
        if (this.getWakefulness() == 2) {
            return;
        }
        this.setWakefulness(2);
        this.dispatch((Consumer<Observer>)_$$Lambda$v8UUYbN3IpgugNoVVCKp_k3ABDI.INSTANCE);
    }
    
    public void dispatchStartedGoingToSleep() {
        if (this.getWakefulness() == 3) {
            return;
        }
        this.setWakefulness(3);
        this.dispatch((Consumer<Observer>)_$$Lambda$ASgSeR7gTZT1Q2JGNWCU20EppLY.INSTANCE);
    }
    
    public void dispatchStartedWakingUp() {
        if (this.getWakefulness() == 1) {
            return;
        }
        this.setWakefulness(1);
        this.dispatch((Consumer<Observer>)_$$Lambda$TPhVA13qrDBGFKbgQpRNBPBvAqI.INSTANCE);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("WakefulnessLifecycle:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mWakefulness=");
        sb.append(this.mWakefulness);
        printWriter.println(sb.toString());
    }
    
    public int getWakefulness() {
        return this.mWakefulness;
    }
    
    public interface Observer
    {
        default void onFinishedGoingToSleep() {
        }
        
        default void onFinishedWakingUp() {
        }
        
        default void onStartedGoingToSleep() {
        }
        
        default void onStartedWakingUp() {
        }
    }
}
