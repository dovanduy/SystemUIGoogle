// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.system;

import android.view.InputEvent;
import android.view.Choreographer;
import android.view.BatchedInputEventReceiver;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.InputChannel;
import java.io.PrintWriter;
import android.view.WindowManagerGlobal;
import android.os.Binder;
import android.view.IWindowManager;
import android.os.IBinder;

public class InputConsumerController
{
    private static final String TAG = "InputConsumerController";
    private InputEventReceiver mInputEventReceiver;
    private InputListener mListener;
    private final String mName;
    private RegistrationListener mRegistrationListener;
    private final IBinder mToken;
    private final IWindowManager mWindowManager;
    
    public InputConsumerController(final IWindowManager mWindowManager, final String mName) {
        this.mWindowManager = mWindowManager;
        this.mToken = (IBinder)new Binder();
        this.mName = mName;
    }
    
    public static InputConsumerController getPipInputConsumer() {
        return new InputConsumerController(WindowManagerGlobal.getWindowManagerService(), "pip_input_consumer");
    }
    
    public void dump(final PrintWriter printWriter, final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("  ");
        final String string = sb.toString();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(s);
        sb2.append(InputConsumerController.TAG);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(string);
        sb3.append("registered=");
        sb3.append(this.mInputEventReceiver != null);
        printWriter.println(sb3.toString());
    }
    
    public void registerInputConsumer() {
        if (this.mInputEventReceiver == null) {
            final InputChannel inputChannel = new InputChannel();
            try {
                this.mWindowManager.destroyInputConsumer(this.mName, 0);
                this.mWindowManager.createInputConsumer(this.mToken, this.mName, 0, inputChannel);
            }
            catch (RemoteException ex) {
                Log.e(InputConsumerController.TAG, "Failed to create input consumer", (Throwable)ex);
            }
            this.mInputEventReceiver = new InputEventReceiver(inputChannel, Looper.myLooper());
            final RegistrationListener mRegistrationListener = this.mRegistrationListener;
            if (mRegistrationListener != null) {
                mRegistrationListener.onRegistrationChanged(true);
            }
        }
    }
    
    public void setInputListener(final InputListener mListener) {
        this.mListener = mListener;
    }
    
    public void setRegistrationListener(final RegistrationListener mRegistrationListener) {
        this.mRegistrationListener = mRegistrationListener;
        if (mRegistrationListener != null) {
            mRegistrationListener.onRegistrationChanged(this.mInputEventReceiver != null);
        }
    }
    
    public void unregisterInputConsumer() {
        if (this.mInputEventReceiver != null) {
            try {
                this.mWindowManager.destroyInputConsumer(this.mName, 0);
            }
            catch (RemoteException ex) {
                Log.e(InputConsumerController.TAG, "Failed to destroy input consumer", (Throwable)ex);
            }
            this.mInputEventReceiver.dispose();
            this.mInputEventReceiver = null;
            final RegistrationListener mRegistrationListener = this.mRegistrationListener;
            if (mRegistrationListener != null) {
                mRegistrationListener.onRegistrationChanged(false);
            }
        }
    }
    
    private final class InputEventReceiver extends BatchedInputEventReceiver
    {
        public InputEventReceiver(final InputChannel inputChannel, final Looper looper) {
            super(inputChannel, looper, Choreographer.getInstance());
        }
        
        public void onInputEvent(final InputEvent inputEvent) {
            boolean onInputEvent = true;
            try {
                if (InputConsumerController.this.mListener != null) {
                    onInputEvent = InputConsumerController.this.mListener.onInputEvent(inputEvent);
                }
                this.finishInputEvent(inputEvent, onInputEvent);
            }
            finally {
                this.finishInputEvent(inputEvent, true);
            }
        }
    }
    
    public interface InputListener
    {
        boolean onInputEvent(final InputEvent p0);
    }
    
    public interface RegistrationListener
    {
        void onRegistrationChanged(final boolean p0);
    }
}
