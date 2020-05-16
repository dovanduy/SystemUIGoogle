// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.os.HandlerThread;
import android.content.Intent;
import android.provider.Settings$Secure;
import android.text.TextUtils;
import android.util.Log;
import android.hardware.camera2.CameraManager$TorchCallback;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import android.os.Handler;
import android.content.Context;
import android.hardware.camera2.CameraManager;

public class FlashlightControllerImpl implements FlashlightController
{
    private static final boolean DEBUG;
    private String mCameraId;
    private final CameraManager mCameraManager;
    private final Context mContext;
    private boolean mFlashlightEnabled;
    private Handler mHandler;
    private final ArrayList<WeakReference<FlashlightListener>> mListeners;
    private boolean mTorchAvailable;
    private final CameraManager$TorchCallback mTorchCallback;
    
    static {
        DEBUG = Log.isLoggable("FlashlightController", 3);
    }
    
    public FlashlightControllerImpl(final Context mContext) {
        this.mListeners = new ArrayList<WeakReference<FlashlightListener>>(1);
        this.mTorchCallback = new CameraManager$TorchCallback() {
            private void setCameraAvailable(final boolean b) {
                Object this$0 = FlashlightControllerImpl.this;
                synchronized (this$0) {
                    final boolean b2 = FlashlightControllerImpl.this.mTorchAvailable != b;
                    FlashlightControllerImpl.this.mTorchAvailable = b;
                    // monitorexit(this$0)
                    if (b2) {
                        if (FlashlightControllerImpl.DEBUG) {
                            this$0 = new StringBuilder();
                            ((StringBuilder)this$0).append("dispatchAvailabilityChanged(");
                            ((StringBuilder)this$0).append(b);
                            ((StringBuilder)this$0).append(")");
                            Log.d("FlashlightController", ((StringBuilder)this$0).toString());
                        }
                        FlashlightControllerImpl.this.dispatchAvailabilityChanged(b);
                    }
                }
            }
            
            private void setTorchMode(final boolean b) {
                synchronized (FlashlightControllerImpl.this) {
                    final boolean b2 = FlashlightControllerImpl.this.mFlashlightEnabled != b;
                    FlashlightControllerImpl.this.mFlashlightEnabled = b;
                    // monitorexit(this.this$0)
                    if (b2) {
                        if (FlashlightControllerImpl.DEBUG) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("dispatchModeChanged(");
                            sb.append(b);
                            sb.append(")");
                            Log.d("FlashlightController", sb.toString());
                        }
                        FlashlightControllerImpl.this.dispatchModeChanged(b);
                    }
                }
            }
            
            public void onTorchModeChanged(final String s, final boolean torchMode) {
                if (TextUtils.equals((CharSequence)s, (CharSequence)FlashlightControllerImpl.this.mCameraId)) {
                    this.setCameraAvailable(true);
                    this.setTorchMode(torchMode);
                    Settings$Secure.putInt(FlashlightControllerImpl.this.mContext.getContentResolver(), "flashlight_available", 1);
                    Settings$Secure.putInt(FlashlightControllerImpl.this.mContext.getContentResolver(), "flashlight_enabled", (int)(torchMode ? 1 : 0));
                    FlashlightControllerImpl.this.mContext.sendBroadcast(new Intent("com.android.settings.flashlight.action.FLASHLIGHT_CHANGED"));
                }
            }
            
            public void onTorchModeUnavailable(final String s) {
                if (TextUtils.equals((CharSequence)s, (CharSequence)FlashlightControllerImpl.this.mCameraId)) {
                    this.setCameraAvailable(false);
                    Settings$Secure.putInt(FlashlightControllerImpl.this.mContext.getContentResolver(), "flashlight_available", 0);
                }
            }
        };
        this.mContext = mContext;
        this.mCameraManager = (CameraManager)mContext.getSystemService("camera");
        this.tryInitCamera();
    }
    
    private void cleanUpListenersLocked(final FlashlightListener flashlightListener) {
        for (int i = this.mListeners.size() - 1; i >= 0; --i) {
            final FlashlightListener flashlightListener2 = this.mListeners.get(i).get();
            if (flashlightListener2 == null || flashlightListener2 == flashlightListener) {
                this.mListeners.remove(i);
            }
        }
    }
    
    private void dispatchAvailabilityChanged(final boolean b) {
        this.dispatchListeners(2, b);
    }
    
    private void dispatchError() {
        this.dispatchListeners(1, false);
    }
    
    private void dispatchListeners(final int n, final boolean b) {
        synchronized (this.mListeners) {
            final int size = this.mListeners.size();
            int i = 0;
            int n2 = 0;
            while (i < size) {
                final FlashlightListener flashlightListener = this.mListeners.get(i).get();
                int n3;
                if (flashlightListener != null) {
                    if (n == 0) {
                        flashlightListener.onFlashlightError();
                        n3 = n2;
                    }
                    else if (n == 1) {
                        flashlightListener.onFlashlightChanged(b);
                        n3 = n2;
                    }
                    else {
                        n3 = n2;
                        if (n == 2) {
                            flashlightListener.onFlashlightAvailabilityChanged(b);
                            n3 = n2;
                        }
                    }
                }
                else {
                    n3 = 1;
                }
                ++i;
                n2 = n3;
            }
            if (n2 != 0) {
                this.cleanUpListenersLocked(null);
            }
        }
    }
    
    private void dispatchModeChanged(final boolean b) {
        this.dispatchListeners(1, b);
    }
    
    private void ensureHandler() {
        synchronized (this) {
            if (this.mHandler == null) {
                final HandlerThread handlerThread = new HandlerThread("FlashlightController", 10);
                handlerThread.start();
                this.mHandler = new Handler(handlerThread.getLooper());
            }
        }
    }
    
    private String getCameraId() throws CameraAccessException {
        for (final String s : this.mCameraManager.getCameraIdList()) {
            final CameraCharacteristics cameraCharacteristics = this.mCameraManager.getCameraCharacteristics(s);
            final Boolean b = (Boolean)cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            final Integer n = (Integer)cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
            if (b != null && b && n != null && n == 1) {
                return s;
            }
        }
        return null;
    }
    
    private void tryInitCamera() {
        try {
            final String cameraId = this.getCameraId();
            this.mCameraId = cameraId;
            if (cameraId != null) {
                this.ensureHandler();
                this.mCameraManager.registerTorchCallback(this.mTorchCallback, this.mHandler);
            }
        }
        finally {
            final Throwable t;
            Log.e("FlashlightController", "Couldn't initialize.", t);
        }
    }
    
    @Override
    public void addCallback(final FlashlightListener referent) {
        synchronized (this.mListeners) {
            if (this.mCameraId == null) {
                this.tryInitCamera();
            }
            this.cleanUpListenersLocked(referent);
            this.mListeners.add(new WeakReference<FlashlightListener>(referent));
            referent.onFlashlightAvailabilityChanged(this.mTorchAvailable);
            referent.onFlashlightChanged(this.mFlashlightEnabled);
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("FlashlightController state:");
        printWriter.print("  mCameraId=");
        printWriter.println(this.mCameraId);
        printWriter.print("  mFlashlightEnabled=");
        printWriter.println(this.mFlashlightEnabled);
        printWriter.print("  mTorchAvailable=");
        printWriter.println(this.mTorchAvailable);
    }
    
    @Override
    public boolean hasFlashlight() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.camera.flash");
    }
    
    @Override
    public boolean isAvailable() {
        synchronized (this) {
            return this.mTorchAvailable;
        }
    }
    
    @Override
    public boolean isEnabled() {
        synchronized (this) {
            return this.mFlashlightEnabled;
        }
    }
    
    @Override
    public void removeCallback(final FlashlightListener flashlightListener) {
        synchronized (this.mListeners) {
            this.cleanUpListenersLocked(flashlightListener);
        }
    }
    
    @Override
    public void setFlashlight(final boolean mFlashlightEnabled) {
        synchronized (this) {
            if (this.mCameraId == null) {
                return;
            }
            final boolean mFlashlightEnabled2 = this.mFlashlightEnabled;
            int n = 0;
            if (mFlashlightEnabled2 != mFlashlightEnabled) {
                this.mFlashlightEnabled = mFlashlightEnabled;
                try {
                    this.mCameraManager.setTorchMode(this.mCameraId, mFlashlightEnabled);
                    n = n;
                }
                catch (CameraAccessException ex) {
                    Log.e("FlashlightController", "Couldn't set torch mode", (Throwable)ex);
                    this.mFlashlightEnabled = false;
                    n = 1;
                }
            }
            // monitorexit(this)
            this.dispatchModeChanged(this.mFlashlightEnabled);
            if (n != 0) {
                this.dispatchError();
            }
        }
    }
}
