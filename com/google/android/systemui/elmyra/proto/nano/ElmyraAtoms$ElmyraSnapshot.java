// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.proto.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;

public final class ElmyraAtoms$ElmyraSnapshot extends MessageNano
{
    public ChassisProtos$Chassis chassis;
    public SnapshotProtos$Snapshot snapshot;
    
    public ElmyraAtoms$ElmyraSnapshot() {
        this.clear();
    }
    
    public ElmyraAtoms$ElmyraSnapshot clear() {
        this.snapshot = null;
        this.chassis = null;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final SnapshotProtos$Snapshot snapshot = this.snapshot;
        int n = computeSerializedSize;
        if (snapshot != null) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, snapshot);
        }
        final ChassisProtos$Chassis chassis = this.chassis;
        int n2 = n;
        if (chassis != null) {
            n2 = n + CodedOutputByteBufferNano.computeMessageSize(2, chassis);
        }
        return n2;
    }
    
    @Override
    public ElmyraAtoms$ElmyraSnapshot mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 10) {
                if (tag != 18) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                    continue;
                }
                else {
                    if (this.chassis == null) {
                        this.chassis = new ChassisProtos$Chassis();
                    }
                    codedInputByteBufferNano.readMessage(this.chassis);
                }
            }
            else {
                if (this.snapshot == null) {
                    this.snapshot = new SnapshotProtos$Snapshot();
                }
                codedInputByteBufferNano.readMessage(this.snapshot);
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final SnapshotProtos$Snapshot snapshot = this.snapshot;
        if (snapshot != null) {
            codedOutputByteBufferNano.writeMessage(1, snapshot);
        }
        final ChassisProtos$Chassis chassis = this.chassis;
        if (chassis != null) {
            codedOutputByteBufferNano.writeMessage(2, chassis);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
