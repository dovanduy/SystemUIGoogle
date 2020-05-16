// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.function.Consumer;
import android.os.Trace;
import com.android.systemui.Dumpable;

public class ScreenLifecycle extends Lifecycle<Observer> implements Dumpable
{
    private int mScreenState;
    
    public ScreenLifecycle() {
        this.mScreenState = 0;
    }
    
    private void setScreenState(final int mScreenState) {
        Trace.traceCounter(4096L, "screenState", this.mScreenState = mScreenState);
    }
    
    public void dispatchScreenTurnedOff() {
        this.setScreenState(0);
        this.dispatch((Consumer<Observer>)_$$Lambda$K8LiTMkPknhhclqjA2eboLxaGEU.INSTANCE);
    }
    
    public void dispatchScreenTurnedOn() {
        this.setScreenState(2);
        this.dispatch((Consumer<Observer>)_$$Lambda$n4aPxVrHdTzFo5NE6H_ILivOadQ.INSTANCE);
    }
    
    public void dispatchScreenTurningOff() {
        this.setScreenState(3);
        this.dispatch((Consumer<Observer>)_$$Lambda$DmSZzOb4vxXoGU7unAMsJYIcFwE.INSTANCE);
    }
    
    public void dispatchScreenTurningOn() {
        this.setScreenState(1);
        this.dispatch((Consumer<Observer>)_$$Lambda$w9PiqN50NESCg48fJRhE_dJBSdc.INSTANCE);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("ScreenLifecycle:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mScreenState=");
        sb.append(this.mScreenState);
        printWriter.println(sb.toString());
    }
    
    public int getScreenState() {
        return this.mScreenState;
    }
    
    public interface Observer
    {
        default void onScreenTurnedOff() {
        }
        
        default void onScreenTurnedOn() {
        }
        
        default void onScreenTurningOff() {
        }
        
        default void onScreenTurningOn() {
        }
    }
}
