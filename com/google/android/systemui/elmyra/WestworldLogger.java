// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra;

import java.util.concurrent.Executor;
import android.app.StatsManager$PullAtomMetadata;
import java.util.concurrent.Executors;
import android.app.StatsManager;
import com.google.protobuf.nano.MessageNano;
import com.google.android.systemui.elmyra.proto.nano.ElmyraAtoms$ElmyraSnapshot;
import java.util.concurrent.TimeUnit;
import com.android.systemui.shared.system.SysUiStatsLog;
import android.util.StatsEvent;
import android.util.Log;
import java.util.List;
import android.content.Context;
import android.app.StatsManager$StatsPullAtomCallback;
import com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$Snapshot;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import java.util.concurrent.CountDownLatch;
import com.google.android.systemui.elmyra.proto.nano.ChassisProtos$Chassis;
import com.google.android.systemui.elmyra.sensors.GestureSensor;

public class WestworldLogger implements Listener
{
    private ChassisProtos$Chassis mChassis;
    private CountDownLatch mCountDownLatch;
    private GestureConfiguration mGestureConfiguration;
    private Object mMutex;
    private SnapshotProtos$Snapshot mSnapshot;
    private SnapshotController mSnapshotController;
    private final StatsManager$StatsPullAtomCallback mWestworldCallback;
    
    public WestworldLogger(final Context context, final GestureConfiguration mGestureConfiguration, final SnapshotController mSnapshotController) {
        this.mWestworldCallback = (StatsManager$StatsPullAtomCallback)new _$$Lambda$WestworldLogger$04xnEXtd_K3rnum_G_UincGVn0Y(this);
        this.mChassis = null;
        this.mGestureConfiguration = mGestureConfiguration;
        this.mSnapshotController = mSnapshotController;
        this.mSnapshot = null;
        this.mMutex = new Object();
        this.registerWithWestworld(context);
    }
    
    public void didReceiveChassis(final ChassisProtos$Chassis mChassis) {
        this.mChassis = mChassis;
    }
    
    public void didReceiveSnapshot(final SnapshotProtos$Snapshot mSnapshot) {
        synchronized (this.mMutex) {
            this.mSnapshot = mSnapshot;
            if (this.mCountDownLatch != null) {
                this.mCountDownLatch.countDown();
            }
        }
    }
    
    @Override
    public void onGestureDetected(final GestureSensor gestureSensor, final DetectionProperties detectionProperties) {
        SysUiStatsLog.write(174, 3);
    }
    
    @Override
    public void onGestureProgress(final GestureSensor gestureSensor, final float n, final int n2) {
        SysUiStatsLog.write(176, (int)(n * 100.0f));
        SysUiStatsLog.write(174, n2);
    }
    
    public int pull(final int atomId, final List<StatsEvent> list) {
        if (this.mSnapshotController == null) {
            Log.d("Elmyra/Logger", "Snapshot Controller is null, returning.");
            return 1;
        }
        Object mMutex = this.mMutex;
        synchronized (mMutex) {
            if (this.mCountDownLatch != null) {
                return 1;
            }
            this.mCountDownLatch = new CountDownLatch(1);
            // monitorexit(mMutex)
            this.mSnapshotController.onWestworldPull();
            Label_0284: {
                try {
                    final long currentTimeMillis = System.currentTimeMillis();
                    this.mCountDownLatch.await(50L, TimeUnit.MILLISECONDS);
                    mMutex = new StringBuilder();
                    ((StringBuilder)mMutex).append("Snapshot took ");
                    ((StringBuilder)mMutex).append(Long.toString(System.currentTimeMillis() - currentTimeMillis));
                    ((StringBuilder)mMutex).append(" milliseconds.");
                    Log.d("Elmyra/Logger", ((StringBuilder)mMutex).toString());
                    synchronized (this.mMutex) {
                        if (this.mSnapshot != null && this.mChassis != null) {
                            final ElmyraAtoms$ElmyraSnapshot elmyraAtoms$ElmyraSnapshot = new ElmyraAtoms$ElmyraSnapshot();
                            this.mSnapshot.sensitivitySetting = this.mGestureConfiguration.getSensitivity();
                            elmyraAtoms$ElmyraSnapshot.snapshot = this.mSnapshot;
                            elmyraAtoms$ElmyraSnapshot.chassis = this.mChassis;
                            list.add(StatsEvent.newBuilder().setAtomId(atomId).writeByteArray(MessageNano.toByteArray(elmyraAtoms$ElmyraSnapshot.snapshot)).writeByteArray(MessageNano.toByteArray(elmyraAtoms$ElmyraSnapshot.chassis)).build());
                            this.mSnapshot = null;
                            break Label_0284;
                        }
                        this.mCountDownLatch = null;
                        return 1;
                    }
                }
                catch (IllegalMonitorStateException ex) {
                    Log.d("Elmyra/Logger", ex.getMessage());
                }
                catch (InterruptedException ex2) {
                    Log.d("Elmyra/Logger", ex2.getMessage());
                }
            }
            synchronized (this.mMutex) {
                this.mCountDownLatch = null;
                this.mSnapshot = null;
                return 0;
            }
        }
    }
    
    public void querySubmitted() {
        SysUiStatsLog.write(175, 2);
    }
    
    public void registerWithWestworld(final Context context) {
        final StatsManager statsManager = (StatsManager)context.getSystemService("stats");
        if (statsManager == null) {
            Log.d("Elmyra/Logger", "Failed to get StatsManager");
        }
        try {
            statsManager.setPullAtomCallback(150000, (StatsManager$PullAtomMetadata)null, (Executor)Executors.newSingleThreadExecutor(), this.mWestworldCallback);
        }
        catch (RuntimeException ex) {
            Log.d("Elmyra/Logger", "Failed to register callback with StatsManager");
            ex.printStackTrace();
        }
    }
}
