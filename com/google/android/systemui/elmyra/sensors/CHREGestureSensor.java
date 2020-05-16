// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.sensors;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.google.android.systemui.elmyra.proto.nano.ContextHubMessages$SensitivityUpdate;
import com.google.android.systemui.elmyra.proto.nano.ContextHubMessages$RecognizerStart;
import com.google.protobuf.nano.MessageNano;
import com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$SnapshotHeader;
import java.util.List;
import android.hardware.location.ContextHubInfo;
import android.hardware.location.ContextHubManager;
import com.google.android.systemui.elmyra.SnapshotController;
import com.android.systemui.R$dimen;
import android.util.TypedValue;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.android.systemui.elmyra.proto.nano.ContextHubMessages$GestureProgress;
import com.google.android.systemui.elmyra.proto.nano.ContextHubMessages$GestureDetected;
import com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$Snapshot;
import com.google.android.systemui.elmyra.proto.nano.ChassisProtos$Chassis;
import android.hardware.location.NanoAppMessage;
import android.util.Log;
import com.google.android.systemui.elmyra.SnapshotConfiguration;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import android.hardware.location.ContextHubClientCallback;
import android.hardware.location.ContextHubClient;
import android.content.Context;
import com.android.systemui.Dumpable;

public class CHREGestureSensor implements Dumpable, GestureSensor
{
    private final Context mContext;
    private ContextHubClient mContextHubClient;
    private final ContextHubClientCallback mContextHubClientCallback;
    private int mContextHubRetryCount;
    private final AssistGestureController mController;
    private final GestureConfiguration mGestureConfiguration;
    private boolean mIsListening;
    private final float mProgressDetectThreshold;
    
    public CHREGestureSensor(final Context mContext, final GestureConfiguration mGestureConfiguration, final SnapshotConfiguration snapshotConfiguration) {
        this.mContextHubClientCallback = new ContextHubClientCallback() {
            public void onHubReset(final ContextHubClient contextHubClient) {
                final StringBuilder sb = new StringBuilder();
                sb.append("HubReset: ");
                sb.append(contextHubClient.getAttachedHub().getId());
                Log.d("Elmyra/GestureSensor", sb.toString());
            }
            
            public void onMessageFromNanoApp(final ContextHubClient contextHubClient, final NanoAppMessage nanoAppMessage) {
                if (nanoAppMessage.getNanoAppId() != 5147455389092024334L) {
                    return;
                }
                try {
                    final int messageType = nanoAppMessage.getMessageType();
                    if (messageType != 1) {
                        switch (messageType) {
                            default: {
                                final StringBuilder sb = new StringBuilder();
                                sb.append("Unknown message type: ");
                                sb.append(nanoAppMessage.getMessageType());
                                Log.e("Elmyra/GestureSensor", sb.toString());
                                break;
                            }
                            case 303: {
                                CHREGestureSensor.this.mController.storeChassisConfiguration(ChassisProtos$Chassis.parseFrom(nanoAppMessage.getMessageBody()));
                                break;
                            }
                            case 302: {
                                final SnapshotProtos$Snapshot from = SnapshotProtos$Snapshot.parseFrom(nanoAppMessage.getMessageBody());
                                from.sensitivitySetting = CHREGestureSensor.this.mGestureConfiguration.getSensitivity();
                                CHREGestureSensor.this.mController.onSnapshotReceived(from);
                                break;
                            }
                            case 301: {
                                final ContextHubMessages$GestureDetected from2 = ContextHubMessages$GestureDetected.parseFrom(nanoAppMessage.getMessageBody());
                                CHREGestureSensor.this.mController.onGestureDetected(new DetectionProperties(from2.hapticConsumed, from2.hostSuspended));
                                break;
                            }
                            case 300: {
                                CHREGestureSensor.this.mController.onGestureProgress(ContextHubMessages$GestureProgress.parseFrom(nanoAppMessage.getMessageBody()).progress);
                            }
                            case 304:
                            case 305: {
                                break;
                            }
                        }
                    }
                    else if (CHREGestureSensor.this.mIsListening) {
                        CHREGestureSensor.this.startRecognizer();
                    }
                }
                catch (InvalidProtocolBufferNanoException ex) {
                    Log.e("Elmyra/GestureSensor", "Invalid protocol buffer", (Throwable)ex);
                }
            }
            
            public void onNanoAppAborted(final ContextHubClient contextHubClient, final long n, final int i) {
                if (n == 5147455389092024334L) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Nanoapp aborted, code: ");
                    sb.append(i);
                    Log.e("Elmyra/GestureSensor", sb.toString());
                }
            }
        };
        this.mContext = mContext;
        final TypedValue typedValue = new TypedValue();
        mContext.getResources().getValue(R$dimen.elmyra_progress_detect_threshold, typedValue, true);
        this.mProgressDetectThreshold = typedValue.getFloat();
        (this.mController = new AssistGestureController(mContext, this, mGestureConfiguration, snapshotConfiguration)).setSnapshotListener(new _$$Lambda$CHREGestureSensor$tgdTWD62oqqQesyxLOZGgtM6s68(this));
        (this.mGestureConfiguration = mGestureConfiguration).setListener((GestureConfiguration.Listener)new _$$Lambda$CHREGestureSensor$v5v365vvSzFymZTj0TlwjnaHmcs(this));
        this.initializeContextHubClientIfNull();
    }
    
    private void initializeContextHubClientIfNull() {
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.context_hub") && this.mContextHubClient == null) {
            final ContextHubManager contextHubManager = (ContextHubManager)this.mContext.getSystemService("contexthub");
            final List contextHubs = contextHubManager.getContextHubs();
            if (contextHubs.size() == 0) {
                Log.e("Elmyra/GestureSensor", "No context hubs found");
                return;
            }
            this.mContextHubClient = contextHubManager.createClient((ContextHubInfo)contextHubs.get(0), this.mContextHubClientCallback);
            ++this.mContextHubRetryCount;
        }
    }
    
    private void sendMessageToNanoApp(final int i, final byte[] array) {
        this.initializeContextHubClientIfNull();
        if (this.mContextHubClient == null) {
            Log.e("Elmyra/GestureSensor", "ContextHubClient null");
            return;
        }
        final int sendMessageToNanoApp = this.mContextHubClient.sendMessageToNanoApp(NanoAppMessage.createMessageToNanoApp(5147455389092024334L, i, array));
        if (sendMessageToNanoApp != 0) {
            Log.e("Elmyra/GestureSensor", String.format("Unable to send message %d to nanoapp, error code %d", i, sendMessageToNanoApp));
        }
    }
    
    private void startRecognizer() {
        final ContextHubMessages$RecognizerStart contextHubMessages$RecognizerStart = new ContextHubMessages$RecognizerStart();
        contextHubMessages$RecognizerStart.progressReportThreshold = this.mProgressDetectThreshold;
        contextHubMessages$RecognizerStart.sensitivity = this.mGestureConfiguration.getSensitivity();
        this.sendMessageToNanoApp(200, MessageNano.toByteArray(contextHubMessages$RecognizerStart));
        if (this.mController.getChassisConfiguration() == null) {
            this.sendMessageToNanoApp(204, new byte[0]);
        }
    }
    
    private void updateSensitivity(final GestureConfiguration gestureConfiguration) {
        final ContextHubMessages$SensitivityUpdate contextHubMessages$SensitivityUpdate = new ContextHubMessages$SensitivityUpdate();
        contextHubMessages$SensitivityUpdate.sensitivity = gestureConfiguration.getSensitivity();
        this.sendMessageToNanoApp(202, MessageNano.toByteArray(contextHubMessages$SensitivityUpdate));
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final StringBuilder sb = new StringBuilder();
        sb.append(CHREGestureSensor.class.getSimpleName());
        sb.append(" state:");
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  mIsListening: ");
        sb2.append(this.mIsListening);
        printWriter.println(sb2.toString());
        if (this.mContextHubClient == null) {
            printWriter.println("  mContextHubClient is null. Likely no context hubs were found");
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mContextHubRetryCount: ");
        sb3.append(this.mContextHubRetryCount);
        printWriter.println(sb3.toString());
        this.mController.dump(fileDescriptor, printWriter, array);
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
        this.mIsListening = true;
        this.startRecognizer();
    }
    
    @Override
    public void stopListening() {
        this.sendMessageToNanoApp(201, new byte[0]);
        this.mIsListening = false;
    }
}
