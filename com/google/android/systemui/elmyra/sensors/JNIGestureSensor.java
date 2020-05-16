// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.sensors;

import java.util.Arrays;
import java.io.InputStream;
import android.provider.Settings$Secure;
import android.content.res.Resources$NotFoundException;
import java.io.IOException;
import com.android.systemui.R$raw;
import com.google.android.systemui.elmyra.sensors.config.SensorCalibration;
import com.google.protobuf.nano.MessageNano;
import com.google.android.systemui.elmyra.proto.nano.ChassisProtos$Chassis;
import com.android.systemui.Dependency;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.R$string;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import android.content.Context;

public class JNIGestureSensor implements GestureSensor
{
    private static final String DISABLE_SETTING = "com.google.android.systemui.elmyra.disable_jni";
    private static final int SENSOR_RATE = 20000;
    private static final String TAG = "Elmyra/JNIGestureSensor";
    private static boolean sLibraryLoaded;
    private final Context mContext;
    private final AssistGestureController mController;
    private final GestureConfiguration mGestureConfiguration;
    private boolean mIsListening;
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback;
    private long mNativeService;
    private int mSensorCount;
    private final String mSensorStringType;
    
    static {
        try {
            System.loadLibrary("elmyra");
            JNIGestureSensor.sLibraryLoaded = true;
        }
        finally {
            final StringBuilder sb = new StringBuilder();
            sb.append("Could not load JNI component: ");
            final Throwable obj;
            sb.append(obj);
            Log.w("Elmyra/JNIGestureSensor", sb.toString());
            JNIGestureSensor.sLibraryLoaded = false;
        }
    }
    
    public JNIGestureSensor(final Context mContext, final GestureConfiguration mGestureConfiguration) {
        this.mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            private boolean mWasListening;
            
            @Override
            public void onFinishedGoingToSleep(final int n) {
                JNIGestureSensor.this.mController.onGestureProgress(0.0f);
                this.mWasListening = JNIGestureSensor.this.isListening();
                JNIGestureSensor.this.stopListening();
            }
            
            @Override
            public void onStartedWakingUp() {
                JNIGestureSensor.this.mController.onGestureProgress(0.0f);
                if (this.mWasListening) {
                    JNIGestureSensor.this.startListening();
                }
            }
        };
        this.mContext = mContext;
        this.mController = new AssistGestureController(mContext, this, mGestureConfiguration);
        this.mSensorStringType = mContext.getResources().getString(R$string.elmyra_raw_sensor_string_type);
        (this.mGestureConfiguration = mGestureConfiguration).setListener((GestureConfiguration.Listener)new _$$Lambda$JNIGestureSensor$_LNLV8OrdpJRbyOEiZGkaj6wYCk(this));
        Dependency.get(KeyguardUpdateMonitor.class).registerCallback(this.mKeyguardUpdateMonitorCallback);
        final byte[] chassisAsset = getChassisAsset(mContext);
        if (chassisAsset != null && chassisAsset.length != 0) {
            try {
                final ChassisProtos$Chassis chassisProtos$Chassis = new ChassisProtos$Chassis();
                MessageNano.mergeFrom(chassisProtos$Chassis, chassisAsset);
                this.mSensorCount = chassisProtos$Chassis.sensors.length;
                for (int i = 0; i < this.mSensorCount; ++i) {
                    final SensorCalibration calibration = SensorCalibration.getCalibration(i);
                    if (calibration != null && calibration.contains("touch_2_sensitivity")) {
                        chassisProtos$Chassis.sensors[i].sensitivity = 1.0f / calibration.get("touch_2_sensitivity");
                    }
                    else {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Error reading calibration for sensor ");
                        sb.append(i);
                        Log.w("Elmyra/JNIGestureSensor", sb.toString());
                    }
                }
                this.createNativeService(chassisAsset);
            }
            catch (Exception ex) {
                Log.e("Elmyra/JNIGestureSensor", "Error reading chassis file", (Throwable)ex);
                this.mSensorCount = 0;
            }
        }
    }
    
    private native boolean createNativeService(final byte[] p0);
    
    private native void destroyNativeService();
    
    private static byte[] getChassisAsset(final Context context) {
        try {
            return readAllBytes(context.getResources().openRawResource(R$raw.elmyra_chassis));
        }
        catch (IOException | Resources$NotFoundException ex) {
            final Throwable t;
            Log.e("Elmyra/JNIGestureSensor", "Could not load chassis resource", t);
            return null;
        }
    }
    
    public static boolean isAvailable(final Context context) {
        if (Settings$Secure.getInt(context.getContentResolver(), "com.google.android.systemui.elmyra.disable_jni", 0) == 1) {
            return false;
        }
        if (!JNIGestureSensor.sLibraryLoaded) {
            return false;
        }
        final byte[] chassisAsset = getChassisAsset(context);
        return chassisAsset != null && chassisAsset.length != 0;
    }
    
    private void onGestureDetected() {
        this.mController.onGestureDetected(null);
    }
    
    private void onGestureProgress(final float n) {
        this.mController.onGestureProgress(n);
    }
    
    private static byte[] readAllBytes(final InputStream inputStream) throws IOException {
        int newLength = 1024;
        byte[] original = new byte[1024];
        int n = 0;
        while (true) {
            final int read = inputStream.read(original, n, newLength - n);
            if (read > 0) {
                n += read;
            }
            else {
                if (read < 0) {
                    break;
                }
                newLength <<= 1;
                original = Arrays.copyOf(original, newLength);
            }
        }
        if (newLength != n) {
            original = Arrays.copyOf(original, n);
        }
        return original;
    }
    
    private native boolean setGestureDetector(final byte[] p0);
    
    private native boolean startListeningNative(final String p0, final int p1);
    
    private native void stopListeningNative();
    
    private void updateConfiguration() {
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.destroyNativeService();
    }
    
    @Override
    public boolean isListening() {
        return this.mIsListening;
    }
    
    @Override
    public void setGestureListener(final Listener gestureListener) {
        this.mController.setGestureListener(gestureListener);
    }
    
    @Override
    public void startListening() {
        if (!this.mIsListening && this.startListeningNative(this.mSensorStringType, 20000)) {
            this.updateConfiguration();
            this.mIsListening = true;
        }
    }
    
    @Override
    public void stopListening() {
        if (this.mIsListening) {
            this.stopListeningNative();
            this.mIsListening = false;
        }
    }
}
