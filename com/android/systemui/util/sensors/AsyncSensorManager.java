// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.sensors;

import android.hardware.HardwareBuffer;
import android.os.MemoryFile;
import android.hardware.SensorDirectChannel;
import com.android.internal.util.Preconditions;
import android.hardware.SensorAdditionalInfo;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager$DynamicSensorCallback;
import android.util.Log;
import android.hardware.TriggerEventListener;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.Plugin;
import java.util.ArrayList;
import android.os.HandlerThread;
import com.android.systemui.shared.plugins.PluginManager;
import android.content.Context;
import android.hardware.Sensor;
import java.util.List;
import android.os.Handler;
import com.android.systemui.plugins.SensorManagerPlugin;
import com.android.systemui.plugins.PluginListener;
import android.hardware.SensorManager;

public class AsyncSensorManager extends SensorManager implements PluginListener<SensorManagerPlugin>
{
    private final Handler mHandler;
    private final SensorManager mInner;
    private final List<SensorManagerPlugin> mPlugins;
    private final List<Sensor> mSensorCache;
    
    public AsyncSensorManager(final Context context, final PluginManager pluginManager) {
        this((SensorManager)context.getSystemService((Class)SensorManager.class), pluginManager, null);
    }
    
    @VisibleForTesting
    public AsyncSensorManager(final SensorManager mInner, final PluginManager pluginManager, final Handler mHandler) {
        this.mInner = mInner;
        if (mHandler == null) {
            final HandlerThread handlerThread = new HandlerThread("async_sensor");
            handlerThread.start();
            this.mHandler = new Handler(handlerThread.getLooper());
        }
        else {
            this.mHandler = mHandler;
        }
        this.mSensorCache = (List<Sensor>)this.mInner.getSensorList(-1);
        this.mPlugins = new ArrayList<SensorManagerPlugin>();
        if (pluginManager != null) {
            pluginManager.addPluginListener((PluginListener<Plugin>)this, SensorManagerPlugin.class, true);
        }
    }
    
    protected boolean cancelTriggerSensorImpl(final TriggerEventListener triggerEventListener, final Sensor sensor, final boolean b) {
        Preconditions.checkArgument(b);
        this.mHandler.post((Runnable)new _$$Lambda$AsyncSensorManager$rUtRem6mSTBr22Jz6SCHZv3qC7c(this, triggerEventListener, sensor));
        return true;
    }
    
    protected int configureDirectChannelImpl(final SensorDirectChannel sensorDirectChannel, final Sensor sensor, final int n) {
        throw new UnsupportedOperationException("not implemented");
    }
    
    protected SensorDirectChannel createDirectChannelImpl(final MemoryFile memoryFile, final HardwareBuffer hardwareBuffer) {
        throw new UnsupportedOperationException("not implemented");
    }
    
    protected void destroyDirectChannelImpl(final SensorDirectChannel sensorDirectChannel) {
        throw new UnsupportedOperationException("not implemented");
    }
    
    protected boolean flushImpl(final SensorEventListener sensorEventListener) {
        return this.mInner.flush(sensorEventListener);
    }
    
    protected List<Sensor> getFullDynamicSensorList() {
        return (List<Sensor>)this.mInner.getSensorList(-1);
    }
    
    protected List<Sensor> getFullSensorList() {
        return this.mSensorCache;
    }
    
    protected boolean initDataInjectionImpl(final boolean b) {
        throw new UnsupportedOperationException("not implemented");
    }
    
    protected boolean injectSensorDataImpl(final Sensor sensor, final float[] array, final int n, final long n2) {
        throw new UnsupportedOperationException("not implemented");
    }
    
    public void onPluginConnected(final SensorManagerPlugin sensorManagerPlugin, final Context context) {
        this.mPlugins.add(sensorManagerPlugin);
    }
    
    public void onPluginDisconnected(final SensorManagerPlugin sensorManagerPlugin) {
        this.mPlugins.remove(sensorManagerPlugin);
    }
    
    protected void registerDynamicSensorCallbackImpl(final SensorManager$DynamicSensorCallback sensorManager$DynamicSensorCallback, final Handler handler) {
        this.mHandler.post((Runnable)new _$$Lambda$AsyncSensorManager$F0WdeYeejMV4rm6D4L93BEBFpEM(this, sensorManager$DynamicSensorCallback, handler));
    }
    
    protected boolean registerListenerImpl(final SensorEventListener sensorEventListener, final Sensor sensor, final int n, final Handler handler, final int n2, final int n3) {
        this.mHandler.post((Runnable)new _$$Lambda$AsyncSensorManager$pme3Zcml6LetP_ijBXRDSjxUcHg(this, sensorEventListener, sensor, n, n2, handler));
        return true;
    }
    
    public boolean registerPluginListener(final SensorManagerPlugin.Sensor sensor, final SensorManagerPlugin.SensorEventListener sensorEventListener) {
        if (this.mPlugins.isEmpty()) {
            Log.w("AsyncSensorManager", "No plugins registered");
            return false;
        }
        this.mHandler.post((Runnable)new _$$Lambda$AsyncSensorManager$aB8h3ftRLBeKmBma753sk37oSSM(this, sensor, sensorEventListener));
        return true;
    }
    
    protected boolean requestTriggerSensorImpl(final TriggerEventListener triggerEventListener, final Sensor sensor) {
        if (triggerEventListener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        if (sensor != null) {
            this.mHandler.post((Runnable)new _$$Lambda$AsyncSensorManager$6e3mOsDmvWVwlltv4pnWARTR_gQ(this, triggerEventListener, sensor));
            return true;
        }
        throw new IllegalArgumentException("sensor cannot be null");
    }
    
    protected boolean setOperationParameterImpl(final SensorAdditionalInfo sensorAdditionalInfo) {
        this.mHandler.post((Runnable)new _$$Lambda$AsyncSensorManager$I0ubq9rKvg_slyfuYRTkWnHfBQA(this, sensorAdditionalInfo));
        return true;
    }
    
    protected void unregisterDynamicSensorCallbackImpl(final SensorManager$DynamicSensorCallback sensorManager$DynamicSensorCallback) {
        this.mHandler.post((Runnable)new _$$Lambda$AsyncSensorManager$jlPEINewb64o1tvevL779Llyz9o(this, sensorManager$DynamicSensorCallback));
    }
    
    protected void unregisterListenerImpl(final SensorEventListener sensorEventListener, final Sensor sensor) {
        this.mHandler.post((Runnable)new _$$Lambda$AsyncSensorManager$hqcwBQ7SIv_uRvhgQvDc2LVBJ_U(this, sensor, sensorEventListener));
    }
    
    public void unregisterPluginListener(final SensorManagerPlugin.Sensor sensor, final SensorManagerPlugin.SensorEventListener sensorEventListener) {
        this.mHandler.post((Runnable)new _$$Lambda$AsyncSensorManager$BRp9I_sao7Fg71C6pjzafSkiRQo(this, sensor, sensorEventListener));
    }
}
