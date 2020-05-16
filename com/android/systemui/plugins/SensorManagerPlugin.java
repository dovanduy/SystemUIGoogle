// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_SENSOR_MANAGER", version = 1)
public interface SensorManagerPlugin extends Plugin
{
    public static final String ACTION = "com.android.systemui.action.PLUGIN_SENSOR_MANAGER";
    public static final int VERSION = 1;
    
    void registerListener(final Sensor p0, final SensorEventListener p1);
    
    void unregisterListener(final Sensor p0, final SensorEventListener p1);
    
    public static class Sensor
    {
        public static final int TYPE_SKIP_STATUS = 4;
        public static final int TYPE_SWIPE = 3;
        public static final int TYPE_WAKE_DISPLAY = 2;
        public static final int TYPE_WAKE_LOCK_SCREEN = 1;
        private int mType;
        
        public Sensor(final int mType) {
            this.mType = mType;
        }
        
        public int getType() {
            return this.mType;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("{PluginSensor type=\"");
            sb.append(this.mType);
            sb.append("\"}");
            return sb.toString();
        }
    }
    
    public static class SensorEvent
    {
        Sensor mSensor;
        float[] mValues;
        int mVendorType;
        
        public SensorEvent(final Sensor sensor, final int n) {
            this(sensor, n, null);
        }
        
        public SensorEvent(final Sensor mSensor, final int mVendorType, final float[] mValues) {
            this.mSensor = mSensor;
            this.mVendorType = mVendorType;
            this.mValues = mValues;
        }
        
        public Sensor getSensor() {
            return this.mSensor;
        }
        
        public float[] getValues() {
            return this.mValues;
        }
        
        public int getVendorType() {
            return this.mVendorType;
        }
    }
    
    public interface SensorEventListener
    {
        void onSensorChanged(final SensorEvent p0);
    }
}
