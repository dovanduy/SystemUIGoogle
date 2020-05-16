// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.proto.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.MessageNano;

public final class SnapshotProtos$Snapshot extends MessageNano
{
    private static volatile SnapshotProtos$Snapshot[] _emptyArray;
    public SnapshotProtos$Event[] events;
    public SnapshotProtos$SnapshotHeader header;
    public float sensitivitySetting;
    
    public SnapshotProtos$Snapshot() {
        this.clear();
    }
    
    public static SnapshotProtos$Snapshot[] emptyArray() {
        if (SnapshotProtos$Snapshot._emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (SnapshotProtos$Snapshot._emptyArray == null) {
                    SnapshotProtos$Snapshot._emptyArray = new SnapshotProtos$Snapshot[0];
                }
            }
        }
        return SnapshotProtos$Snapshot._emptyArray;
    }
    
    public static SnapshotProtos$Snapshot parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
        final SnapshotProtos$Snapshot snapshotProtos$Snapshot = new SnapshotProtos$Snapshot();
        MessageNano.mergeFrom(snapshotProtos$Snapshot, array);
        return snapshotProtos$Snapshot;
    }
    
    public SnapshotProtos$Snapshot clear() {
        this.header = null;
        this.events = SnapshotProtos$Event.emptyArray();
        this.sensitivitySetting = 0.0f;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final SnapshotProtos$SnapshotHeader header = this.header;
        int n = computeSerializedSize;
        if (header != null) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, header);
        }
        final SnapshotProtos$Event[] events = this.events;
        int n2 = n;
        if (events != null) {
            n2 = n;
            if (events.length > 0) {
                int n3 = 0;
                while (true) {
                    final SnapshotProtos$Event[] events2 = this.events;
                    n2 = n;
                    if (n3 >= events2.length) {
                        break;
                    }
                    final SnapshotProtos$Event snapshotProtos$Event = events2[n3];
                    int n4 = n;
                    if (snapshotProtos$Event != null) {
                        n4 = n + CodedOutputByteBufferNano.computeMessageSize(2, snapshotProtos$Event);
                    }
                    ++n3;
                    n = n4;
                }
            }
        }
        int n5 = n2;
        if (Float.floatToIntBits(this.sensitivitySetting) != Float.floatToIntBits(0.0f)) {
            n5 = n2 + CodedOutputByteBufferNano.computeFloatSize(3, this.sensitivitySetting);
        }
        return n5;
    }
    
    @Override
    public SnapshotProtos$Snapshot mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 10) {
                if (tag != 18) {
                    if (tag != 29) {
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                        continue;
                    }
                    else {
                        this.sensitivitySetting = codedInputByteBufferNano.readFloat();
                    }
                }
                else {
                    final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                    final SnapshotProtos$Event[] events = this.events;
                    int length;
                    if (events == null) {
                        length = 0;
                    }
                    else {
                        length = events.length;
                    }
                    final int n = repeatedFieldArrayLength + length;
                    final SnapshotProtos$Event[] events2 = new SnapshotProtos$Event[n];
                    int i = length;
                    if (length != 0) {
                        System.arraycopy(this.events, 0, events2, 0, length);
                        i = length;
                    }
                    while (i < n - 1) {
                        codedInputByteBufferNano.readMessage(events2[i] = new SnapshotProtos$Event());
                        codedInputByteBufferNano.readTag();
                        ++i;
                    }
                    codedInputByteBufferNano.readMessage(events2[i] = new SnapshotProtos$Event());
                    this.events = events2;
                }
            }
            else {
                if (this.header == null) {
                    this.header = new SnapshotProtos$SnapshotHeader();
                }
                codedInputByteBufferNano.readMessage(this.header);
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final SnapshotProtos$SnapshotHeader header = this.header;
        if (header != null) {
            codedOutputByteBufferNano.writeMessage(1, header);
        }
        final SnapshotProtos$Event[] events = this.events;
        if (events != null && events.length > 0) {
            int n = 0;
            while (true) {
                final SnapshotProtos$Event[] events2 = this.events;
                if (n >= events2.length) {
                    break;
                }
                final SnapshotProtos$Event snapshotProtos$Event = events2[n];
                if (snapshotProtos$Event != null) {
                    codedOutputByteBufferNano.writeMessage(2, snapshotProtos$Event);
                }
                ++n;
            }
        }
        if (Float.floatToIntBits(this.sensitivitySetting) != Float.floatToIntBits(0.0f)) {
            codedOutputByteBufferNano.writeFloat(3, this.sensitivitySetting);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
