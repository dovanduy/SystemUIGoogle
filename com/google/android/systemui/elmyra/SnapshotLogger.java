// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra;

import android.os.Binder;
import java.io.FileDescriptor;
import com.google.android.systemui.elmyra.proto.nano.ChassisProtos$SensorEvent;
import com.google.android.systemui.elmyra.proto.nano.SnapshotProtos$Snapshot;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import com.android.systemui.Dumpable;

public class SnapshotLogger implements Dumpable
{
    private final int mSnapshotCapacity;
    private List<Snapshot> mSnapshots;
    
    public SnapshotLogger(final int n) {
        this.mSnapshotCapacity = n;
        this.mSnapshots = new ArrayList<Snapshot>(n);
    }
    
    private void dumpInternal(final PrintWriter printWriter) {
        printWriter.println("Dumping Elmyra Snapshots");
        for (int i = 0; i < this.mSnapshots.size(); ++i) {
            final SnapshotProtos$Snapshot snapshot = this.mSnapshots.get(i).getSnapshot();
            final StringBuilder sb = new StringBuilder();
            sb.append("SystemTime: ");
            sb.append(this.mSnapshots.get(i).getTimestamp());
            printWriter.println(sb.toString());
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Snapshot: ");
            sb2.append(i);
            printWriter.println(sb2.toString());
            printWriter.print("header {");
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("  identifier: ");
            sb3.append(snapshot.header.identifier);
            printWriter.print(sb3.toString());
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("  gesture_type: ");
            sb4.append(snapshot.header.gestureType);
            printWriter.print(sb4.toString());
            final StringBuilder sb5 = new StringBuilder();
            sb5.append("  feedback: ");
            sb5.append(snapshot.header.feedback);
            printWriter.print(sb5.toString());
            printWriter.print("}");
            for (int j = 0; j < snapshot.events.length; ++j) {
                printWriter.print("events {");
                if (snapshot.events[j].hasGestureStage()) {
                    final StringBuilder sb6 = new StringBuilder();
                    sb6.append("  gesture_stage: ");
                    sb6.append(snapshot.events[j].getGestureStage());
                    printWriter.print(sb6.toString());
                }
                else if (snapshot.events[j].hasSensorEvent()) {
                    final ChassisProtos$SensorEvent sensorEvent = snapshot.events[j].getSensorEvent();
                    printWriter.print("  sensor_event {");
                    final StringBuilder sb7 = new StringBuilder();
                    sb7.append("    timestamp: ");
                    sb7.append(sensorEvent.timestamp);
                    printWriter.print(sb7.toString());
                    for (int k = 0; k < sensorEvent.values.length; ++k) {
                        final StringBuilder sb8 = new StringBuilder();
                        sb8.append("    values: ");
                        sb8.append(sensorEvent.values[k]);
                        printWriter.print(sb8.toString());
                    }
                    printWriter.print("  }");
                }
                printWriter.print("}");
            }
            final StringBuilder sb9 = new StringBuilder();
            sb9.append("sensitivity_setting: ");
            sb9.append(snapshot.sensitivitySetting);
            printWriter.println(sb9.toString());
            printWriter.println();
        }
        this.mSnapshots.clear();
        printWriter.println("Finished Dumping Elmyra Snapshots");
    }
    
    public void addSnapshot(final SnapshotProtos$Snapshot snapshotProtos$Snapshot, final long n) {
        if (this.mSnapshots.size() == this.mSnapshotCapacity) {
            this.mSnapshots.remove(0);
        }
        this.mSnapshots.add(new Snapshot(snapshotProtos$Snapshot, n));
    }
    
    public void didReceiveQuery() {
        if (this.mSnapshots.size() > 0) {
            final List<Snapshot> mSnapshots = this.mSnapshots;
            mSnapshots.get(mSnapshots.size() - 1).getSnapshot().header.feedback = 1;
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.dumpInternal(printWriter);
        }
        finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }
    
    public List<Snapshot> getSnapshots() {
        return this.mSnapshots;
    }
    
    public class Snapshot
    {
        final SnapshotProtos$Snapshot mSnapshot;
        final long mTimestamp;
        
        Snapshot(final SnapshotLogger snapshotLogger, final SnapshotProtos$Snapshot mSnapshot, final long mTimestamp) {
            this.mSnapshot = mSnapshot;
            this.mTimestamp = mTimestamp;
        }
        
        public SnapshotProtos$Snapshot getSnapshot() {
            return this.mSnapshot;
        }
        
        long getTimestamp() {
            return this.mTimestamp;
        }
    }
}
