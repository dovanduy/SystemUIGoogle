// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra;

import java.util.Random;
import com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$SnapshotHeader;
import android.os.Message;
import android.os.Looper;
import android.os.Handler;
import com.google.android.systemui.elmyra.sensors.GestureSensor;

public final class SnapshotController implements GestureSensor.Listener
{
    private final Handler mHandler;
    private int mLastGestureStage;
    private final int mSnapshotDelayAfterGesture;
    private Listener mSnapshotListener;
    
    public SnapshotController(final SnapshotConfiguration snapshotConfiguration) {
        this.mLastGestureStage = 0;
        this.mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(final Message message) {
                if (message.what == 1) {
                    SnapshotController.this.requestSnapshot((SnapshotProtos$SnapshotHeader)message.obj);
                }
            }
        };
        this.mSnapshotDelayAfterGesture = snapshotConfiguration.getSnapshotDelayAfterGesture();
    }
    
    private void requestSnapshot(final SnapshotProtos$SnapshotHeader snapshotProtos$SnapshotHeader) {
        final Listener mSnapshotListener = this.mSnapshotListener;
        if (mSnapshotListener != null) {
            mSnapshotListener.onSnapshotRequested(snapshotProtos$SnapshotHeader);
        }
    }
    
    @Override
    public void onGestureDetected(final GestureSensor gestureSensor, final DetectionProperties detectionProperties) {
        final SnapshotProtos$SnapshotHeader snapshotProtos$SnapshotHeader = new SnapshotProtos$SnapshotHeader();
        snapshotProtos$SnapshotHeader.gestureType = 1;
        long actionId;
        if (detectionProperties != null) {
            actionId = detectionProperties.getActionId();
        }
        else {
            actionId = 0L;
        }
        snapshotProtos$SnapshotHeader.identifier = actionId;
        this.mLastGestureStage = 0;
        final Handler mHandler = this.mHandler;
        mHandler.sendMessageDelayed(mHandler.obtainMessage(1, (Object)snapshotProtos$SnapshotHeader), (long)this.mSnapshotDelayAfterGesture);
    }
    
    @Override
    public void onGestureProgress(final GestureSensor gestureSensor, final float n, final int mLastGestureStage) {
        if (this.mLastGestureStage == 2 && mLastGestureStage != 2) {
            final SnapshotProtos$SnapshotHeader snapshotProtos$SnapshotHeader = new SnapshotProtos$SnapshotHeader();
            snapshotProtos$SnapshotHeader.identifier = new Random().nextLong();
            snapshotProtos$SnapshotHeader.gestureType = 2;
            this.requestSnapshot(snapshotProtos$SnapshotHeader);
        }
        this.mLastGestureStage = mLastGestureStage;
    }
    
    public void onWestworldPull() {
        final SnapshotProtos$SnapshotHeader snapshotProtos$SnapshotHeader = new SnapshotProtos$SnapshotHeader();
        snapshotProtos$SnapshotHeader.gestureType = 4;
        snapshotProtos$SnapshotHeader.identifier = 0L;
        final Handler mHandler = this.mHandler;
        mHandler.sendMessage(mHandler.obtainMessage(1, (Object)snapshotProtos$SnapshotHeader));
    }
    
    public void setListener(final Listener mSnapshotListener) {
        this.mSnapshotListener = mSnapshotListener;
    }
    
    public interface Listener
    {
        void onSnapshotRequested(final SnapshotProtos$SnapshotHeader p0);
    }
}
