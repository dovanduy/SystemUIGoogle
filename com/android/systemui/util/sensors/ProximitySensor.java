// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.sensors;

import java.util.Locale;
import android.os.Handler;
import java.util.function.Consumer;
import android.content.res.Resources$NotFoundException;
import com.android.systemui.R$dimen;
import java.util.Iterator;
import com.android.systemui.R$string;
import android.hardware.SensorEvent;
import java.util.ArrayList;
import android.content.res.Resources;
import android.util.Log;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import java.util.List;
import com.android.internal.annotations.VisibleForTesting;

public class ProximitySensor
{
    private static final boolean DEBUG;
    @VisibleForTesting
    ProximityEvent mLastEvent;
    private List<ProximitySensorListener> mListeners;
    private boolean mPaused;
    private boolean mRegistered;
    private final Sensor mSensor;
    private int mSensorDelay;
    private SensorEventListener mSensorEventListener;
    private final AsyncSensorManager mSensorManager;
    private String mTag;
    private final float mThreshold;
    
    static {
        DEBUG = Log.isLoggable("ProxSensor", 3);
    }
    
    public ProximitySensor(final Resources resources, final AsyncSensorManager mSensorManager) {
        this.mListeners = new ArrayList<ProximitySensorListener>();
        final Sensor sensor = null;
        this.mTag = null;
        this.mSensorDelay = 3;
        this.mSensorEventListener = (SensorEventListener)new SensorEventListener() {
            public void onAccuracyChanged(final Sensor sensor, final int n) {
            }
            
            public void onSensorChanged(final SensorEvent sensorEvent) {
                synchronized (this) {
                    ProximitySensor.this.onSensorEvent(sensorEvent);
                }
            }
        };
        this.mSensorManager = mSensorManager;
        final Sensor customProxSensor = this.findCustomProxSensor(resources);
        final float n = 0.0f;
        float customProxThreshold = 0.0f;
        Sensor sensor2 = null;
        Label_0094: {
            if (customProxSensor != null) {
                try {
                    customProxThreshold = this.getCustomProxThreshold(resources);
                }
                catch (IllegalStateException ex) {
                    Log.e("ProxSensor", "Can not load custom proximity sensor.", (Throwable)ex);
                    sensor2 = sensor;
                    customProxThreshold = n;
                    break Label_0094;
                }
            }
            sensor2 = customProxSensor;
        }
        Sensor defaultSensor = sensor2;
        float maximumRange = customProxThreshold;
        if (sensor2 == null) {
            final Sensor sensor3 = defaultSensor = mSensorManager.getDefaultSensor(8);
            maximumRange = customProxThreshold;
            if (sensor3 != null) {
                maximumRange = sensor3.getMaximumRange();
                defaultSensor = sensor3;
            }
        }
        this.mThreshold = maximumRange;
        this.mSensor = defaultSensor;
    }
    
    private Sensor findCustomProxSensor(final Resources resources) {
        final String string = resources.getString(R$string.proximity_sensor_type);
        final boolean empty = string.isEmpty();
        final Sensor sensor = null;
        if (empty) {
            return null;
        }
        final Iterator iterator = this.mSensorManager.getSensorList(-1).iterator();
        Sensor sensor2;
        do {
            sensor2 = sensor;
            if (!iterator.hasNext()) {
                break;
            }
            sensor2 = iterator.next();
        } while (!string.equals(sensor2.getStringType()));
        return sensor2;
    }
    
    private float getCustomProxThreshold(final Resources resources) {
        try {
            return resources.getFloat(R$dimen.proximity_sensor_threshold);
        }
        catch (Resources$NotFoundException ex) {
            throw new IllegalStateException("R.dimen.proximity_sensor_threshold must be set.");
        }
    }
    
    private void logDebug(final String str) {
        if (ProximitySensor.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            String string;
            if (this.mTag != null) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("[");
                sb2.append(this.mTag);
                sb2.append("] ");
                string = sb2.toString();
            }
            else {
                string = "";
            }
            sb.append(string);
            sb.append(str);
            Log.d("ProxSensor", sb.toString());
        }
    }
    
    private void onSensorEvent(final SensorEvent sensorEvent) {
        final float[] values = sensorEvent.values;
        boolean b = false;
        if (values[0] < this.mThreshold) {
            b = true;
        }
        this.mLastEvent = new ProximityEvent(b, sensorEvent.timestamp);
        this.alertListeners();
    }
    
    public void alertListeners() {
        this.mListeners.forEach(new _$$Lambda$ProximitySensor$ghFL_7mqmC5TPLUcAxsPY_h6a_M(this));
    }
    
    public boolean getSensorAvailable() {
        return this.mSensor != null;
    }
    
    public Boolean isNear() {
        if (this.getSensorAvailable()) {
            final ProximityEvent mLastEvent = this.mLastEvent;
            if (mLastEvent != null) {
                return mLastEvent.getNear();
            }
        }
        return null;
    }
    
    public boolean isRegistered() {
        return this.mRegistered;
    }
    
    public void pause() {
        this.mPaused = true;
        this.unregisterInternal();
    }
    
    public boolean register(final ProximitySensorListener obj) {
        if (!this.getSensorAvailable()) {
            return false;
        }
        if (this.mListeners.contains(obj)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("ProxListener registered multiple times: ");
            sb.append(obj);
            Log.d("ProxSensor", sb.toString());
        }
        else {
            this.mListeners.add(obj);
        }
        this.registerInternal();
        return true;
    }
    
    protected void registerInternal() {
        if (!this.mRegistered && !this.mPaused) {
            if (!this.mListeners.isEmpty()) {
                this.logDebug("Registering sensor listener");
                this.mRegistered = true;
                this.mSensorManager.registerListener(this.mSensorEventListener, this.mSensor, this.mSensorDelay);
            }
        }
    }
    
    public void resume() {
        this.mPaused = false;
        this.registerInternal();
    }
    
    public void setSensorDelay(final int mSensorDelay) {
        this.mSensorDelay = mSensorDelay;
    }
    
    public void setTag(final String mTag) {
        this.mTag = mTag;
    }
    
    @Override
    public String toString() {
        return String.format("{registered=%s, paused=%s, near=%s, sensor=%s}", this.isRegistered(), this.mPaused, this.isNear(), this.mSensor);
    }
    
    public void unregister(final ProximitySensorListener proximitySensorListener) {
        this.mListeners.remove(proximitySensorListener);
        if (this.mListeners.size() == 0) {
            this.unregisterInternal();
        }
    }
    
    protected void unregisterInternal() {
        if (!this.mRegistered) {
            return;
        }
        this.logDebug("unregistering sensor listener");
        this.mSensorManager.unregisterListener(this.mSensorEventListener);
        this.mRegistered = false;
    }
    
    public static class ProximityCheck implements Runnable
    {
        private List<Consumer<Boolean>> mCallbacks;
        private final Handler mHandler;
        private final ProximitySensor mSensor;
        
        public ProximityCheck(final ProximitySensor mSensor, final Handler mHandler) {
            this.mCallbacks = new ArrayList<Consumer<Boolean>>();
            (this.mSensor = mSensor).setTag("prox_check");
            this.mHandler = mHandler;
            this.mSensor.pause();
            this.mSensor.register((ProximitySensorListener)new _$$Lambda$ProximitySensor$ProximityCheck$EpeBSbiQOkialkiHGaahifHqSB4(this));
        }
        
        public void check(final long n, final Consumer<Boolean> consumer) {
            if (!this.mSensor.getSensorAvailable()) {
                consumer.accept(null);
            }
            this.mCallbacks.add(consumer);
            if (!this.mSensor.isRegistered()) {
                this.mSensor.resume();
                this.mHandler.postDelayed((Runnable)this, n);
            }
        }
        
        @Override
        public void run() {
            this.mSensor.pause();
            this.mSensor.alertListeners();
        }
    }
    
    public static class ProximityEvent
    {
        private final boolean mNear;
        private final long mTimestampNs;
        
        public ProximityEvent(final boolean mNear, final long mTimestampNs) {
            this.mNear = mNear;
            this.mTimestampNs = mTimestampNs;
        }
        
        public boolean getNear() {
            return this.mNear;
        }
        
        public long getTimestampNs() {
            return this.mTimestampNs;
        }
        
        @Override
        public String toString() {
            return String.format(null, "{near=%s, timestamp_ns=%d}", this.mNear, this.mTimestampNs);
        }
    }
    
    public interface ProximitySensorListener
    {
        void onSensorEvent(final ProximityEvent p0);
    }
}
