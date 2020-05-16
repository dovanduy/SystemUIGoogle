// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

import com.google.android.systemui.columbus.proto.nano.ColumbusProto$SensitivityUpdate;
import com.google.android.systemui.columbus.proto.nano.ColumbusProto$ScreenStateUpdate;
import com.google.protobuf.nano.MessageNano;
import com.google.android.systemui.columbus.proto.nano.ColumbusProto$RecognizerStart;
import android.hardware.location.NanoAppMessage;
import java.util.List;
import android.hardware.location.ContextHubClientCallback;
import android.hardware.location.ContextHubInfo;
import android.util.Log;
import android.hardware.location.ContextHubManager;
import com.google.android.systemui.columbus.proto.nano.ColumbusProto$GestureDetected;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.google.android.systemui.columbus.sensors.config.GestureConfiguration;
import android.hardware.location.ContextHubClient;
import android.content.Context;

public final class CHREGestureSensor implements GestureSensor
{
    private final Context context;
    private ContextHubClient contextHubClient;
    private final CHREGestureSensor$contextHubClientCallback.CHREGestureSensor$contextHubClientCallback$1 contextHubClientCallback;
    private final GestureConfiguration gestureConfiguration;
    private final GestureController gestureController;
    private boolean isAwake;
    private boolean isDozing;
    private boolean isListening;
    private boolean screenOn;
    private boolean screenStateUpdated;
    private final CHREGestureSensor$statusBarStateListener.CHREGestureSensor$statusBarStateListener$1 statusBarStateListener;
    private final CHREGestureSensor$wakefulnessLifecycleObserver.CHREGestureSensor$wakefulnessLifecycleObserver$1 wakefulnessLifecycleObserver;
    
    public CHREGestureSensor(final Context context, final GestureConfiguration gestureConfiguration, final GestureController gestureController, final StatusBarStateController statusBarStateController, final WakefulnessLifecycle wakefulnessLifecycle) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(gestureConfiguration, "gestureConfiguration");
        Intrinsics.checkParameterIsNotNull(gestureController, "gestureController");
        Intrinsics.checkParameterIsNotNull(statusBarStateController, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(wakefulnessLifecycle, "wakefulnessLifecycle");
        this.context = context;
        this.gestureConfiguration = gestureConfiguration;
        this.gestureController = gestureController;
        this.contextHubClientCallback = new CHREGestureSensor$contextHubClientCallback.CHREGestureSensor$contextHubClientCallback$1(this);
        this.statusBarStateListener = new CHREGestureSensor$statusBarStateListener.CHREGestureSensor$statusBarStateListener$1(this);
        this.wakefulnessLifecycleObserver = new CHREGestureSensor$wakefulnessLifecycleObserver.CHREGestureSensor$wakefulnessLifecycleObserver$1(this);
        this.isDozing = statusBarStateController.isDozing();
        final int wakefulness = wakefulnessLifecycle.getWakefulness();
        final boolean b = false;
        final boolean isAwake = wakefulness == 2;
        this.isAwake = isAwake;
        boolean screenOn = b;
        if (isAwake) {
            screenOn = b;
            if (!this.isDozing) {
                screenOn = true;
            }
        }
        this.screenOn = screenOn;
        this.screenStateUpdated = true;
        this.gestureConfiguration.setListener((GestureConfiguration.Listener)new GestureConfiguration.Listener() {
            @Override
            public void onGestureConfigurationChanged(final GestureConfiguration gestureConfiguration) {
                Intrinsics.checkParameterIsNotNull(gestureConfiguration, "configuration");
                CHREGestureSensor.this.updateSensitivity(gestureConfiguration);
            }
        });
        statusBarStateController.addCallback((StatusBarStateController.StateListener)this.statusBarStateListener);
        wakefulnessLifecycle.addObserver((WakefulnessLifecycle.Observer)this.wakefulnessLifecycleObserver);
        this.initializeContextHubClientIfNull();
    }
    
    private final void handleDozingChanged(final boolean isDozing) {
        if (this.isDozing != isDozing) {
            this.isDozing = isDozing;
            this.updateScreenState();
        }
    }
    
    private final void handleGestureDetection(final ColumbusProto$GestureDetected columbusProto$GestureDetected) {
        if (columbusProto$GestureDetected.gestureType == 1) {
            this.gestureController.onGestureProgress(this, 3, new DetectionProperties(false, false));
        }
    }
    
    private final void handleWakefullnessChanged(final boolean isAwake) {
        if (this.isAwake != isAwake) {
            this.isAwake = isAwake;
            this.updateScreenState();
        }
    }
    
    private final void initializeContextHubClientIfNull() {
        if (this.contextHubClient == null) {
            final ContextHubManager contextHubManager = (ContextHubManager)this.context.getSystemService("contexthub");
            List<ContextHubInfo> contextHubs;
            if (contextHubManager != null) {
                contextHubs = (List<ContextHubInfo>)contextHubManager.getContextHubs();
            }
            else {
                contextHubs = null;
            }
            int size;
            if (contextHubs != null) {
                size = contextHubs.size();
            }
            else {
                size = 0;
            }
            if (size == 0) {
                Log.e("Columbus/GestureSensor", "No context hubs found");
                return;
            }
            if (contextHubs != null) {
                this.contextHubClient = contextHubManager.createClient((ContextHubInfo)contextHubs.get(0), (ContextHubClientCallback)this.contextHubClientCallback);
            }
        }
    }
    
    private final boolean sendMessageToNanoApp(final int i, final byte[] array) {
        this.initializeContextHubClientIfNull();
        if (this.contextHubClient == null) {
            Log.e("Columbus/GestureSensor", "ContextHubClient null");
            return false;
        }
        final NanoAppMessage messageToNanoApp = NanoAppMessage.createMessageToNanoApp(5147455389092024345L, i, array);
        final ContextHubClient contextHubClient = this.contextHubClient;
        Integer value;
        if (contextHubClient != null) {
            value = contextHubClient.sendMessageToNanoApp(messageToNanoApp);
        }
        else {
            value = null;
        }
        if (value != null) {
            if (value == 0) {
                return true;
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Unable to send message ");
        sb.append(i);
        sb.append(" to nanoapp, error code ");
        sb.append(value);
        Log.e("Columbus/GestureSensor", sb.toString());
        return false;
    }
    
    private final void startRecognizer() {
        final ColumbusProto$RecognizerStart columbusProto$RecognizerStart = new ColumbusProto$RecognizerStart();
        columbusProto$RecognizerStart.sensitivity = this.gestureConfiguration.getSensitivity();
        final byte[] byteArray = MessageNano.toByteArray(columbusProto$RecognizerStart);
        Intrinsics.checkExpressionValueIsNotNull(byteArray, "MessageNano.toByteArray(recognizerStart)");
        this.sendMessageToNanoApp(100, byteArray);
    }
    
    private final void updateScreenState() {
        final boolean isAwake = this.isAwake;
        int screenState = 1;
        final boolean screenOn = isAwake && !this.isDozing;
        if (this.screenOn != screenOn || !this.screenStateUpdated) {
            this.screenOn = screenOn;
            final ColumbusProto$ScreenStateUpdate columbusProto$ScreenStateUpdate = new ColumbusProto$ScreenStateUpdate();
            if (!this.screenOn) {
                screenState = 2;
            }
            columbusProto$ScreenStateUpdate.screenState = screenState;
            final byte[] byteArray = MessageNano.toByteArray(columbusProto$ScreenStateUpdate);
            Intrinsics.checkExpressionValueIsNotNull(byteArray, "MessageNano.toByteArray(screenStateUpdate)");
            this.screenStateUpdated = this.sendMessageToNanoApp(400, byteArray);
        }
    }
    
    private final void updateSensitivity(final GestureConfiguration gestureConfiguration) {
        final ColumbusProto$SensitivityUpdate columbusProto$SensitivityUpdate = new ColumbusProto$SensitivityUpdate();
        columbusProto$SensitivityUpdate.sensitivity = gestureConfiguration.getSensitivity();
        final byte[] byteArray = MessageNano.toByteArray(columbusProto$SensitivityUpdate);
        Intrinsics.checkExpressionValueIsNotNull(byteArray, "MessageNano.toByteArray(sensitivityUpdate)");
        this.sendMessageToNanoApp(200, byteArray);
    }
    
    @Override
    public boolean isListening() {
        return this.isListening;
    }
    
    @Override
    public void setGestureListener(final Listener gestureListener) {
        this.gestureController.setGestureListener(gestureListener);
    }
    
    public void setListening(final boolean isListening) {
        this.isListening = isListening;
    }
    
    @Override
    public void startListening(final boolean b) {
        this.setListening(true);
        this.startRecognizer();
    }
    
    @Override
    public void stopListening() {
        this.sendMessageToNanoApp(101, new byte[0]);
        this.setListening(false);
    }
}
