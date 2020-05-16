// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.proto.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;

public final class SnapshotProtos$Snapshots extends MessageNano
{
    public SnapshotProtos$Snapshot[] snapshots;
    
    public SnapshotProtos$Snapshots() {
        this.clear();
    }
    
    public SnapshotProtos$Snapshots clear() {
        this.snapshots = SnapshotProtos$Snapshot.emptyArray();
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        final SnapshotProtos$Snapshot[] snapshots = this.snapshots;
        int n = computeSerializedSize;
        if (snapshots != null) {
            n = computeSerializedSize;
            if (snapshots.length > 0) {
                int n2 = 0;
                while (true) {
                    final SnapshotProtos$Snapshot[] snapshots2 = this.snapshots;
                    n = computeSerializedSize;
                    if (n2 >= snapshots2.length) {
                        break;
                    }
                    final SnapshotProtos$Snapshot snapshotProtos$Snapshot = snapshots2[n2];
                    int n3 = computeSerializedSize;
                    if (snapshotProtos$Snapshot != null) {
                        n3 = computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, snapshotProtos$Snapshot);
                    }
                    ++n2;
                    computeSerializedSize = n3;
                }
            }
        }
        return n;
    }
    
    @Override
    public SnapshotProtos$Snapshots mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 10) {
                if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                    return this;
                }
                continue;
            }
            else {
                final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 10);
                final SnapshotProtos$Snapshot[] snapshots = this.snapshots;
                int length;
                if (snapshots == null) {
                    length = 0;
                }
                else {
                    length = snapshots.length;
                }
                final int n = repeatedFieldArrayLength + length;
                final SnapshotProtos$Snapshot[] snapshots2 = new SnapshotProtos$Snapshot[n];
                int i = length;
                if (length != 0) {
                    System.arraycopy(this.snapshots, 0, snapshots2, 0, length);
                    i = length;
                }
                while (i < n - 1) {
                    codedInputByteBufferNano.readMessage(snapshots2[i] = new SnapshotProtos$Snapshot());
                    codedInputByteBufferNano.readTag();
                    ++i;
                }
                codedInputByteBufferNano.readMessage(snapshots2[i] = new SnapshotProtos$Snapshot());
                this.snapshots = snapshots2;
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final SnapshotProtos$Snapshot[] snapshots = this.snapshots;
        if (snapshots != null && snapshots.length > 0) {
            int n = 0;
            while (true) {
                final SnapshotProtos$Snapshot[] snapshots2 = this.snapshots;
                if (n >= snapshots2.length) {
                    break;
                }
                final SnapshotProtos$Snapshot snapshotProtos$Snapshot = snapshots2[n];
                if (snapshotProtos$Snapshot != null) {
                    codedOutputByteBufferNano.writeMessage(1, snapshotProtos$Snapshot);
                }
                ++n;
            }
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
