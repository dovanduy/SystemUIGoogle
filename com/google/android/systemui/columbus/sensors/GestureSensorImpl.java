// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

import android.hardware.SensorEvent;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import android.hardware.SensorEventListener;
import kotlin.TypeCastException;
import android.os.Build;
import android.os.Looper;
import kotlin.jvm.internal.Intrinsics;
import com.google.android.systemui.columbus.sensors.config.GestureConfiguration;
import android.content.Context;
import java.util.concurrent.TimeUnit;
import android.hardware.SensorManager;
import android.os.Handler;
import android.hardware.Sensor;

public final class GestureSensorImpl implements GestureSensor
{
    private static final long TIMEOUT_MS;
    private final android.hardware.Sensor accelerometer;
    private final String deviceName;
    private final android.hardware.Sensor gyroscope;
    private final Handler handler;
    private boolean isListening;
    private final boolean isRunningInLowSamplingRate;
    private Listener listener;
    private final long samplingIntervalNs;
    private final GestureSensorEventListener sensorEventListener;
    private final SensorManager sensorManager;
    private final TapRT tap;
    
    static {
        TIMEOUT_MS = TimeUnit.SECONDS.toMillis(5L);
    }
    
    public GestureSensorImpl(final Context context, final GestureConfiguration gestureConfiguration) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(gestureConfiguration, "gestureConfiguration");
        this.handler = new Handler(Looper.getMainLooper());
        final Object systemService = context.getSystemService("sensor");
        if (systemService != null) {
            final SensorManager sensorManager = (SensorManager)systemService;
            this.sensorManager = sensorManager;
            this.accelerometer = sensorManager.getDefaultSensor(1);
            this.gyroscope = this.sensorManager.getDefaultSensor(4);
            this.sensorEventListener = new GestureSensorEventListener();
            this.deviceName = Build.MODEL;
            this.samplingIntervalNs = 2500000L;
            this.tap = new TapRT(160000000L, context.getAssets(), this.deviceName);
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.hardware.SensorManager");
    }
    
    public static final /* synthetic */ android.hardware.Sensor access$getAccelerometer$p(final GestureSensorImpl gestureSensorImpl) {
        return gestureSensorImpl.accelerometer;
    }
    
    public static final /* synthetic */ android.hardware.Sensor access$getGyroscope$p(final GestureSensorImpl gestureSensorImpl) {
        return gestureSensorImpl.gyroscope;
    }
    
    public static final /* synthetic */ Handler access$getHandler$p(final GestureSensorImpl gestureSensorImpl) {
        return gestureSensorImpl.handler;
    }
    
    public static final /* synthetic */ Listener access$getListener$p(final GestureSensorImpl gestureSensorImpl) {
        return gestureSensorImpl.listener;
    }
    
    public static final /* synthetic */ long access$getSamplingIntervalNs$p(final GestureSensorImpl gestureSensorImpl) {
        return gestureSensorImpl.samplingIntervalNs;
    }
    
    public static final /* synthetic */ GestureSensorEventListener access$getSensorEventListener$p(final GestureSensorImpl gestureSensorImpl) {
        return gestureSensorImpl.sensorEventListener;
    }
    
    public static final /* synthetic */ SensorManager access$getSensorManager$p(final GestureSensorImpl gestureSensorImpl) {
        return gestureSensorImpl.sensorManager;
    }
    
    public static final /* synthetic */ TapRT access$getTap$p(final GestureSensorImpl gestureSensorImpl) {
        return gestureSensorImpl.tap;
    }
    
    public static final /* synthetic */ boolean access$isRunningInLowSamplingRate$p(final GestureSensorImpl gestureSensorImpl) {
        return gestureSensorImpl.isRunningInLowSamplingRate;
    }
    
    @Override
    public boolean isListening() {
        return this.isListening;
    }
    
    @Override
    public void setGestureListener(final Listener listener) {
        this.listener = listener;
    }
    
    public void setListening(final boolean isListening) {
        this.isListening = isListening;
    }
    
    @Override
    public void startListening(final boolean b) {
        if (b) {
            this.sensorEventListener.setListening$vendor__unbundled_google__packages__SystemUIGoogle__android_common__sysuig(true, 0);
            this.tap.getLowpassKey().setPara(0.2f);
            this.tap.getHighpassKey().setPara(0.2f);
            this.tap.getPositivePeakDetector().setMinNoiseTolerate(0.05f);
            this.tap.getPositivePeakDetector().setWindowSize(64);
            this.tap.reset(false);
        }
        else {
            this.sensorEventListener.setListening$vendor__unbundled_google__packages__SystemUIGoogle__android_common__sysuig(true, 21000);
            this.tap.getLowpassKey().setPara(1.0f);
            this.tap.getHighpassKey().setPara(0.3f);
            this.tap.getPositivePeakDetector().setMinNoiseTolerate(0.02f);
            this.tap.getPositivePeakDetector().setWindowSize(8);
            this.tap.getNegativePeakDetection().setMinNoiseTolerate(0.02f);
            this.tap.getNegativePeakDetection().setWindowSize(8);
            this.tap.reset(true);
        }
    }
    
    @Override
    public void stopListening() {
        this.sensorEventListener.setListening$vendor__unbundled_google__packages__SystemUIGoogle__android_common__sysuig(false, 0);
        this.sensorEventListener.reset$vendor__unbundled_google__packages__SystemUIGoogle__android_common__sysuig();
    }
    
    private final class GestureSensorEventListener implements SensorEventListener
    {
        private final Function0<Unit> onTimeout;
        
        public GestureSensorEventListener() {
            this.onTimeout = (Function0<Unit>)new GestureSensorImpl$GestureSensorEventListener$onTimeout.GestureSensorImpl$GestureSensorEventListener$onTimeout$1(this);
        }
        
        public void onAccuracyChanged(final android.hardware.Sensor sensor, final int n) {
        }
        
        public void onSensorChanged(final SensorEvent sensorEvent) {
            if (sensorEvent != null) {
                final TapRT access$getTap$p = GestureSensorImpl.access$getTap$p(GestureSensorImpl.this);
                final android.hardware.Sensor sensor = sensorEvent.sensor;
                Intrinsics.checkExpressionValueIsNotNull(sensor, "it.sensor");
                final int type = sensor.getType();
                final float[] values = sensorEvent.values;
                access$getTap$p.updateData(type, values[0], values[1], values[2], sensorEvent.timestamp, GestureSensorImpl.access$getSamplingIntervalNs$p(GestureSensorImpl.this), GestureSensorImpl.access$isRunningInLowSamplingRate$p(GestureSensorImpl.this));
                final int checkDoubleTapTiming = GestureSensorImpl.access$getTap$p(GestureSensorImpl.this).checkDoubleTapTiming(sensorEvent.timestamp);
                if (checkDoubleTapTiming != 1) {
                    if (checkDoubleTapTiming == 2) {
                        final Handler access$getHandler$p = GestureSensorImpl.access$getHandler$p(GestureSensorImpl.this);
                        final Function0<Unit> onTimeout = this.onTimeout;
                        Runnable runnable;
                        if ((runnable = (Runnable)onTimeout) != null) {
                            runnable = new GestureSensorImpl$sam$i$java_lang_Runnable$0(onTimeout);
                        }
                        access$getHandler$p.removeCallbacks((Runnable)runnable);
                        GestureSensorImpl.access$getHandler$p(GestureSensorImpl.this).post((Runnable)new GestureSensorImpl$GestureSensorEventListener$onSensorChanged$$inlined$let$lambda.GestureSensorImpl$GestureSensorEventListener$onSensorChanged$$inlined$let$lambda$2(this));
                    }
                }
                else {
                    final Handler access$getHandler$p2 = GestureSensorImpl.access$getHandler$p(GestureSensorImpl.this);
                    final Function0<Unit> onTimeout2 = this.onTimeout;
                    Runnable runnable2;
                    if ((runnable2 = (Runnable)onTimeout2) != null) {
                        runnable2 = new GestureSensorImpl$sam$i$java_lang_Runnable$0(onTimeout2);
                    }
                    access$getHandler$p2.removeCallbacks((Runnable)runnable2);
                    GestureSensorImpl.access$getHandler$p(GestureSensorImpl.this).post((Runnable)new GestureSensorImpl$GestureSensorEventListener$onSensorChanged$$inlined$let$lambda.GestureSensorImpl$GestureSensorEventListener$onSensorChanged$$inlined$let$lambda$1(this));
                }
            }
        }
        
        public final void reset$vendor__unbundled_google__packages__SystemUIGoogle__android_common__sysuig() {
            final Listener access$getListener$p = GestureSensorImpl.access$getListener$p(GestureSensorImpl.this);
            if (access$getListener$p != null) {
                Listener.DefaultImpls.onGestureProgress$default(access$getListener$p, GestureSensorImpl.this, 0, null, 4, null);
            }
        }
        
        public final void setListening$vendor__unbundled_google__packages__SystemUIGoogle__android_common__sysuig(final boolean b, final int n) {
            if (b && GestureSensorImpl.access$getAccelerometer$p(GestureSensorImpl.this) != null && GestureSensorImpl.access$getGyroscope$p(GestureSensorImpl.this) != null) {
                GestureSensorImpl.access$getSensorManager$p(GestureSensorImpl.this).registerListener((SensorEventListener)GestureSensorImpl.access$getSensorEventListener$p(GestureSensorImpl.this), GestureSensorImpl.access$getAccelerometer$p(GestureSensorImpl.this), n, GestureSensorImpl.access$getHandler$p(GestureSensorImpl.this));
                GestureSensorImpl.access$getSensorManager$p(GestureSensorImpl.this).registerListener((SensorEventListener)GestureSensorImpl.access$getSensorEventListener$p(GestureSensorImpl.this), GestureSensorImpl.access$getGyroscope$p(GestureSensorImpl.this), n, GestureSensorImpl.access$getHandler$p(GestureSensorImpl.this));
                GestureSensorImpl.this.setListening(true);
            }
            else {
                GestureSensorImpl.access$getSensorManager$p(GestureSensorImpl.this).unregisterListener((SensorEventListener)GestureSensorImpl.access$getSensorEventListener$p(GestureSensorImpl.this));
                GestureSensorImpl.this.setListening(false);
            }
        }
    }
}
