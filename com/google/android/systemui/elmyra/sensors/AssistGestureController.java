// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.sensors;

import android.content.Intent;
import android.os.SystemClock;
import java.io.PrintWriter;
import java.util.List;
import java.io.IOException;
import android.util.Slog;
import android.os.Binder;
import java.io.FileOutputStream;
import com.google.protobuf.nano.MessageNano;
import com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$Snapshot;
import com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$Snapshots;
import java.io.FileDescriptor;
import android.content.res.Resources;
import com.android.systemui.R$integer;
import com.android.systemui.R$dimen;
import android.util.TypedValue;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import com.google.android.systemui.elmyra.SnapshotConfiguration;
import android.content.Context;
import com.google.android.systemui.elmyra.WestworldLogger;
import com.google.android.systemui.elmyra.SnapshotController;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import com.google.android.systemui.elmyra.SnapshotLogger;
import com.google.android.systemui.elmyra.proto.nano.ChassisProtos$Chassis;
import com.android.systemui.Dumpable;

class AssistGestureController implements Dumpable
{
    private ChassisProtos$Chassis mChassis;
    private SnapshotLogger mCompleteGestures;
    private final long mFalsePrimeWindow;
    private final GestureConfiguration mGestureConfiguration;
    private final long mGestureCooldownTime;
    private GestureSensor.Listener mGestureListener;
    private float mGestureProgress;
    private final GestureSensor mGestureSensor;
    private SnapshotLogger mIncompleteGestures;
    private boolean mIsFalsePrimed;
    private long mLastDetectionTime;
    private OPAQueryReceiver mOpaQueryReceiver;
    private final float mProgressAlpha;
    private final float mProgressReportThreshold;
    private final SnapshotController mSnapshotController;
    private WestworldLogger mWestworldLogger;
    
    AssistGestureController(final Context context, final GestureSensor gestureSensor, final GestureConfiguration gestureConfiguration) {
        this(context, gestureSensor, gestureConfiguration, null);
    }
    
    AssistGestureController(final Context context, final GestureSensor mGestureSensor, final GestureConfiguration mGestureConfiguration, final SnapshotConfiguration snapshotConfiguration) {
        this.mOpaQueryReceiver = new OPAQueryReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.google.android.systemui.OPA_ELMYRA_QUERY_SUBMITTED");
        context.registerReceiver((BroadcastReceiver)this.mOpaQueryReceiver, intentFilter);
        this.mGestureSensor = mGestureSensor;
        this.mGestureConfiguration = mGestureConfiguration;
        final Resources resources = context.getResources();
        final TypedValue typedValue = new TypedValue();
        final int n = 0;
        int completeGestures;
        if (snapshotConfiguration != null) {
            completeGestures = snapshotConfiguration.getCompleteGestures();
        }
        else {
            completeGestures = 0;
        }
        this.mCompleteGestures = new SnapshotLogger(completeGestures);
        int incompleteGestures = n;
        if (snapshotConfiguration != null) {
            incompleteGestures = snapshotConfiguration.getIncompleteGestures();
        }
        this.mIncompleteGestures = new SnapshotLogger(incompleteGestures);
        resources.getValue(R$dimen.elmyra_progress_alpha, typedValue, true);
        this.mProgressAlpha = typedValue.getFloat();
        resources.getValue(R$dimen.elmyra_progress_report_threshold, typedValue, true);
        this.mProgressReportThreshold = typedValue.getFloat();
        final long mGestureCooldownTime = resources.getInteger(R$integer.elmyra_gesture_cooldown_time);
        this.mGestureCooldownTime = mGestureCooldownTime;
        this.mFalsePrimeWindow = mGestureCooldownTime + resources.getInteger(R$integer.elmyra_false_prime_window);
        if (snapshotConfiguration != null) {
            this.mSnapshotController = new SnapshotController(snapshotConfiguration);
        }
        else {
            this.mSnapshotController = null;
        }
        this.mWestworldLogger = new WestworldLogger(context, this.mGestureConfiguration, this.mSnapshotController);
    }
    
    private void dumpProto(final FileDescriptor fdObj) {
        final List<SnapshotLogger.Snapshot> snapshots = this.mIncompleteGestures.getSnapshots();
        final List<SnapshotLogger.Snapshot> snapshots2 = this.mCompleteGestures.getSnapshots();
        if (snapshots.size() + snapshots2.size() == 0) {
            return;
        }
        final SnapshotProtos$Snapshots snapshotProtos$Snapshots = new SnapshotProtos$Snapshots();
        snapshotProtos$Snapshots.snapshots = new SnapshotProtos$Snapshot[snapshots.size() + snapshots2.size()];
        final int n = 0;
        int n2 = 0;
        int i;
        while (true) {
            i = n;
            if (n2 >= snapshots.size()) {
                break;
            }
            snapshotProtos$Snapshots.snapshots[n2] = ((SnapshotLogger.Snapshot)snapshots.get(n2)).getSnapshot();
            ++n2;
        }
        while (i < snapshots2.size()) {
            snapshotProtos$Snapshots.snapshots[n2 + i] = ((SnapshotLogger.Snapshot)snapshots2.get(i)).getSnapshot();
            ++i;
        }
        final byte[] byteArray = MessageNano.toByteArray(snapshotProtos$Snapshots);
        final FileOutputStream fileOutputStream = new FileOutputStream(fdObj);
        final long clearCallingIdentity = Binder.clearCallingIdentity();
        while (true) {
            try {
                try {
                    fileOutputStream.write(byteArray);
                    fileOutputStream.flush();
                    this.mCompleteGestures.getSnapshots().clear();
                    this.mIncompleteGestures.getSnapshots().clear();
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
                finally {}
            }
            catch (IOException ex) {
                Slog.e("Elmyra/AssistGestureController", "Error writing to output stream");
                continue;
            }
            break;
        }
        return;
        this.mCompleteGestures.getSnapshots().clear();
        this.mIncompleteGestures.getSnapshots().clear();
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }
    
    private void sendGestureProgress(final GestureSensor gestureSensor, final float n, final int n2) {
        final GestureSensor.Listener mGestureListener = this.mGestureListener;
        if (mGestureListener != null) {
            mGestureListener.onGestureProgress(gestureSensor, n, n2);
        }
        final SnapshotController mSnapshotController = this.mSnapshotController;
        if (mSnapshotController != null) {
            mSnapshotController.onGestureProgress(gestureSensor, n, n2);
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final ChassisProtos$Chassis mChassis = this.mChassis;
        final int n = 0;
        if (mChassis != null) {
            for (int i = 0; i < this.mChassis.sensors.length; ++i) {
                printWriter.print("sensors {");
                final StringBuilder sb = new StringBuilder();
                sb.append("  source: ");
                sb.append(this.mChassis.sensors[i].source);
                printWriter.print(sb.toString());
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("  gain: ");
                sb2.append(this.mChassis.sensors[i].gain);
                printWriter.print(sb2.toString());
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("  sensitivity: ");
                sb3.append(this.mChassis.sensors[i].sensitivity);
                printWriter.print(sb3.toString());
                printWriter.print("}");
            }
            printWriter.println();
        }
        int n3;
        int n2 = n3 = 0;
        int n4;
        for (int j = n; j < array.length; ++j, n2 = n4) {
            final String s = array[j];
            if (s.equals("GoogleServices")) {
                n4 = 1;
            }
            else {
                n4 = n2;
                if (s.equals("proto")) {
                    n3 = 1;
                    n4 = n2;
                }
            }
        }
        if (n2 != 0 && n3 != 0) {
            this.dumpProto(fileDescriptor);
        }
        else {
            this.mCompleteGestures.dump(fileDescriptor, printWriter, array);
            this.mIncompleteGestures.dump(fileDescriptor, printWriter, array);
        }
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("user_sensitivity: ");
        sb4.append(this.mGestureConfiguration.getSensitivity());
        printWriter.println(sb4.toString());
    }
    
    public ChassisProtos$Chassis getChassisConfiguration() {
        return this.mChassis;
    }
    
    public void onGestureDetected(final GestureSensor.DetectionProperties detectionProperties) {
        final long uptimeMillis = SystemClock.uptimeMillis();
        if (uptimeMillis - this.mLastDetectionTime >= this.mGestureCooldownTime) {
            if (!this.mIsFalsePrimed) {
                final GestureSensor.Listener mGestureListener = this.mGestureListener;
                if (mGestureListener != null) {
                    mGestureListener.onGestureDetected(this.mGestureSensor, detectionProperties);
                }
                final SnapshotController mSnapshotController = this.mSnapshotController;
                if (mSnapshotController != null) {
                    mSnapshotController.onGestureDetected(this.mGestureSensor, detectionProperties);
                }
                this.mWestworldLogger.onGestureDetected(this.mGestureSensor, detectionProperties);
                this.mLastDetectionTime = uptimeMillis;
            }
        }
    }
    
    public void onGestureProgress(final float n) {
        if (n == 0.0f) {
            this.mGestureProgress = 0.0f;
            this.mIsFalsePrimed = false;
        }
        else {
            final float mProgressAlpha = this.mProgressAlpha;
            this.mGestureProgress = mProgressAlpha * n + (1.0f - mProgressAlpha) * this.mGestureProgress;
        }
        final long uptimeMillis = SystemClock.uptimeMillis();
        final long mLastDetectionTime = this.mLastDetectionTime;
        if (uptimeMillis - mLastDetectionTime >= this.mGestureCooldownTime) {
            if (!this.mIsFalsePrimed) {
                final long mFalsePrimeWindow = this.mFalsePrimeWindow;
                int n2 = 1;
                if (uptimeMillis - mLastDetectionTime < mFalsePrimeWindow && n == 1.0f) {
                    this.mIsFalsePrimed = true;
                    return;
                }
                final float mGestureProgress = this.mGestureProgress;
                final float mProgressReportThreshold = this.mProgressReportThreshold;
                if (mGestureProgress < mProgressReportThreshold) {
                    this.sendGestureProgress(this.mGestureSensor, 0.0f, 0);
                    this.mWestworldLogger.onGestureProgress(this.mGestureSensor, 0.0f, 0);
                }
                else {
                    final float n3 = (mGestureProgress - mProgressReportThreshold) / (1.0f - mProgressReportThreshold);
                    if (n == 1.0f) {
                        n2 = 2;
                    }
                    this.sendGestureProgress(this.mGestureSensor, n3, n2);
                    this.mWestworldLogger.onGestureProgress(this.mGestureSensor, n3, n2);
                }
            }
        }
    }
    
    public void onSnapshotReceived(final SnapshotProtos$Snapshot snapshotProtos$Snapshot) {
        final int gestureType = snapshotProtos$Snapshot.header.gestureType;
        if (gestureType == 4) {
            this.mWestworldLogger.didReceiveSnapshot(snapshotProtos$Snapshot);
        }
        else if (gestureType == 1) {
            this.mCompleteGestures.addSnapshot(snapshotProtos$Snapshot, System.currentTimeMillis());
        }
        else {
            this.mIncompleteGestures.addSnapshot(snapshotProtos$Snapshot, System.currentTimeMillis());
        }
    }
    
    public void setGestureListener(final GestureSensor.Listener mGestureListener) {
        this.mGestureListener = mGestureListener;
    }
    
    public void setSnapshotListener(final SnapshotController.Listener listener) {
        final SnapshotController mSnapshotController = this.mSnapshotController;
        if (mSnapshotController != null) {
            mSnapshotController.setListener(listener);
        }
    }
    
    public void storeChassisConfiguration(final ChassisProtos$Chassis mChassis) {
        this.mChassis = mChassis;
        this.mWestworldLogger.didReceiveChassis(mChassis);
    }
    
    private class OPAQueryReceiver extends BroadcastReceiver
    {
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals("com.google.android.systemui.OPA_ELMYRA_QUERY_SUBMITTED")) {
                AssistGestureController.this.mCompleteGestures.didReceiveQuery();
                AssistGestureController.this.mWestworldLogger.querySubmitted();
            }
        }
    }
}
