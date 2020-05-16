// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.gates;

import com.android.systemui.R$integer;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.util.sensors.AsyncSensorManager;
import android.hardware.SensorEvent;
import android.content.Context;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;

public class KeyguardProximity extends Gate
{
    private final Listener mGateListener;
    private boolean mIsListening;
    private final KeyguardVisibility mKeyguardGate;
    private boolean mProximityBlocked;
    private final float mProximityThreshold;
    private final Sensor mSensor;
    private final SensorEventListener mSensorListener;
    private final SensorManager mSensorManager;
    
    public KeyguardProximity(final Context context) {
        super(context);
        this.mGateListener = new Listener() {
            @Override
            public void onGateChanged(final Gate gate) {
                KeyguardProximity.this.updateProximityListener();
            }
        };
        this.mSensorListener = (SensorEventListener)new SensorEventListener() {
            public void onAccuracyChanged(final Sensor sensor, final int n) {
            }
            
            public void onSensorChanged(final SensorEvent sensorEvent) {
                final float[] values = sensorEvent.values;
                boolean b = false;
                if (values[0] < KeyguardProximity.this.mProximityThreshold) {
                    b = true;
                }
                if (KeyguardProximity.this.mIsListening && b != KeyguardProximity.this.mProximityBlocked) {
                    KeyguardProximity.this.mProximityBlocked = b;
                    KeyguardProximity.this.notifyListener();
                }
            }
        };
        this.mIsListening = false;
        this.mProximityBlocked = false;
        final AsyncSensorManager mSensorManager = Dependency.get(AsyncSensorManager.class);
        this.mSensorManager = mSensorManager;
        final Sensor defaultSensor = mSensorManager.getDefaultSensor(8);
        this.mSensor = defaultSensor;
        if (defaultSensor == null) {
            this.mProximityThreshold = 0.0f;
            this.mKeyguardGate = null;
            Log.e("Elmyra/KeyguardProximity", "Could not find any Sensor.TYPE_PROXIMITY");
        }
        else {
            this.mProximityThreshold = Math.min(defaultSensor.getMaximumRange(), (float)context.getResources().getInteger(R$integer.elmyra_keyguard_proximity_threshold));
            (this.mKeyguardGate = new KeyguardVisibility(context)).setListener(this.mGateListener);
            this.updateProximityListener();
        }
    }
    
    private void updateProximityListener() {
        if (this.mProximityBlocked) {
            this.mProximityBlocked = false;
            this.notifyListener();
        }
        if (this.isActive() && this.mKeyguardGate.isKeyguardShowing() && !this.mKeyguardGate.isKeyguardOccluded()) {
            if (!this.mIsListening) {
                this.mSensorManager.registerListener(this.mSensorListener, this.mSensor, 3);
                this.mIsListening = true;
            }
        }
        else {
            this.mSensorManager.unregisterListener(this.mSensorListener);
            this.mIsListening = false;
        }
    }
    
    @Override
    protected boolean isBlocked() {
        return this.mIsListening && this.mProximityBlocked;
    }
    
    @Override
    protected void onActivate() {
        if (this.mSensor != null) {
            this.mKeyguardGate.activate();
            this.updateProximityListener();
        }
    }
    
    @Override
    protected void onDeactivate() {
        if (this.mSensor != null) {
            this.mKeyguardGate.deactivate();
            this.updateProximityListener();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mIsListening -> ");
        sb.append(this.mIsListening);
        sb.append("]");
        return sb.toString();
    }
}
