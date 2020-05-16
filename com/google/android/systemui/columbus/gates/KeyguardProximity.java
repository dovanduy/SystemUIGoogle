// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.util.Log;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.util.sensors.AsyncSensorManager;
import android.content.Context;
import android.hardware.SensorManager;
import android.hardware.Sensor;

public final class KeyguardProximity extends Gate
{
    private final KeyguardProximity$gateListener.KeyguardProximity$gateListener$1 gateListener;
    private boolean isListening;
    private final KeyguardVisibility keyguardGate;
    private boolean proximityBlocked;
    private final float proximityThreshold;
    private final Sensor sensor;
    private final KeyguardProximity$sensorListener.KeyguardProximity$sensorListener$1 sensorListener;
    private final SensorManager sensorManager;
    
    public KeyguardProximity(final Context context, final AsyncSensorManager sensorManager, final KeyguardVisibility keyguardGate) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(sensorManager, "asyncSensorManager");
        Intrinsics.checkParameterIsNotNull(keyguardGate, "keyguardGate");
        super(context);
        this.keyguardGate = keyguardGate;
        this.sensorManager = sensorManager;
        this.sensor = sensorManager.getDefaultSensor(8);
        this.sensorListener = new KeyguardProximity$sensorListener.KeyguardProximity$sensorListener$1(this);
        this.gateListener = new KeyguardProximity$gateListener.KeyguardProximity$gateListener$1(this);
        final Sensor sensor = this.sensor;
        if (sensor == null) {
            this.proximityThreshold = 0.0f;
            Log.e("Columbus/KeyguardProximity", "Could not find any Sensor.TYPE_PROXIMITY");
        }
        else {
            this.proximityThreshold = Math.min(sensor.getMaximumRange(), 5.0f);
            this.keyguardGate.setListener((Listener)this.gateListener);
            this.updateProximityListener();
        }
    }
    
    private final void handleSensorEvent(final SensorEvent sensorEvent) {
        final float[] values = sensorEvent.values;
        boolean proximityBlocked = false;
        if (values[0] < this.proximityThreshold) {
            proximityBlocked = true;
        }
        if (this.isListening && proximityBlocked != this.proximityBlocked) {
            this.proximityBlocked = proximityBlocked;
            this.notifyListener();
        }
    }
    
    private final void updateProximityListener() {
        if (this.proximityBlocked) {
            this.proximityBlocked = false;
            this.notifyListener();
        }
        if (this.getActive() && this.keyguardGate.isKeyguardShowing() && !this.keyguardGate.isKeyguardOccluded()) {
            if (!this.isListening) {
                final Sensor sensor = this.sensor;
                if (sensor != null) {
                    this.sensorManager.registerListener((SensorEventListener)this.sensorListener, sensor, 3);
                    this.isListening = true;
                }
            }
        }
        else {
            this.sensorManager.unregisterListener((SensorEventListener)this.sensorListener);
            this.isListening = false;
        }
    }
    
    @Override
    protected boolean isBlocked() {
        return this.isListening && this.proximityBlocked;
    }
    
    @Override
    protected void onActivate() {
        if (this.sensor != null) {
            this.keyguardGate.activate();
            this.updateProximityListener();
        }
    }
    
    @Override
    protected void onDeactivate() {
        if (this.sensor != null) {
            this.keyguardGate.deactivate();
            this.updateProximityListener();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [isListening -> ");
        sb.append(this.isListening);
        sb.append("]");
        return sb.toString();
    }
}
