// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.dump.DumpManager;
import android.hardware.Sensor;
import android.hardware.TriggerEventListener;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.android.systemui.Dumpable;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.keyguard.KeyguardUpdateMonitorCallback;

public final class KeyguardLiftController extends KeyguardUpdateMonitorCallback implements StateListener, Dumpable
{
    private final AsyncSensorManager asyncSensorManager;
    private boolean bouncerVisible;
    private boolean isListening;
    private final KeyguardUpdateMonitor keyguardUpdateMonitor;
    private final TriggerEventListener listener;
    private final Sensor pickupSensor;
    private final StatusBarStateController statusBarStateController;
    
    public KeyguardLiftController(final StatusBarStateController statusBarStateController, final AsyncSensorManager asyncSensorManager, final KeyguardUpdateMonitor keyguardUpdateMonitor, final DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(statusBarStateController, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(asyncSensorManager, "asyncSensorManager");
        Intrinsics.checkParameterIsNotNull(keyguardUpdateMonitor, "keyguardUpdateMonitor");
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        this.statusBarStateController = statusBarStateController;
        this.asyncSensorManager = asyncSensorManager;
        this.keyguardUpdateMonitor = keyguardUpdateMonitor;
        this.pickupSensor = asyncSensorManager.getDefaultSensor(25);
        final String name = KeyguardLiftController.class.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, "javaClass.name");
        dumpManager.registerDumpable(name, this);
        this.statusBarStateController.addCallback((StatusBarStateController.StateListener)this);
        this.keyguardUpdateMonitor.registerCallback(this);
        this.updateListeningState();
        this.listener = (TriggerEventListener)new KeyguardLiftController$listener.KeyguardLiftController$listener$1(this);
    }
    
    private final void updateListeningState() {
        if (this.pickupSensor == null) {
            return;
        }
        final boolean keyguardVisible = this.keyguardUpdateMonitor.isKeyguardVisible();
        final boolean b = true;
        final boolean b2 = keyguardVisible && !this.statusBarStateController.isDozing();
        boolean isListening = b;
        if (!b2) {
            isListening = (this.bouncerVisible && b);
        }
        if (isListening != this.isListening) {
            this.isListening = isListening;
            if (isListening) {
                this.asyncSensorManager.requestTriggerSensor(this.listener, this.pickupSensor);
            }
            else {
                this.asyncSensorManager.cancelTriggerSensor(this.listener, this.pickupSensor);
            }
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(array, "args");
        printWriter.println("KeyguardLiftController:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  pickupSensor: ");
        sb.append(this.pickupSensor);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  isListening: ");
        sb2.append(this.isListening);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  bouncerVisible: ");
        sb3.append(this.bouncerVisible);
        printWriter.println(sb3.toString());
    }
    
    @Override
    public void onDozingChanged(final boolean b) {
        this.updateListeningState();
    }
    
    @Override
    public void onKeyguardBouncerChanged(final boolean bouncerVisible) {
        this.bouncerVisible = bouncerVisible;
        this.updateListeningState();
    }
    
    @Override
    public void onKeyguardVisibilityChanged(final boolean b) {
        this.updateListeningState();
    }
}
