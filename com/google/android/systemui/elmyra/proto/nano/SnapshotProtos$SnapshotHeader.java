// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.proto.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;

public final class SnapshotProtos$SnapshotHeader extends MessageNano
{
    public int feedback;
    public int gestureType;
    public long identifier;
    
    public SnapshotProtos$SnapshotHeader() {
        this.clear();
    }
    
    public SnapshotProtos$SnapshotHeader clear() {
        this.identifier = 0L;
        this.gestureType = 0;
        this.feedback = 0;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final long identifier = this.identifier;
        int n = computeSerializedSize;
        if (identifier != 0L) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(1, identifier);
        }
        final int gestureType = this.gestureType;
        int n2 = n;
        if (gestureType != 0) {
            n2 = n + CodedOutputByteBufferNano.computeInt32Size(2, gestureType);
        }
        final int feedback = this.feedback;
        int n3 = n2;
        if (feedback != 0) {
            n3 = n2 + CodedOutputByteBufferNano.computeInt32Size(3, feedback);
        }
        return n3;
    }
    
    @Override
    public SnapshotProtos$SnapshotHeader mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 8) {
                if (tag != 16) {
                    if (tag != 24) {
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                        continue;
                    }
                    else {
                        final int int32 = codedInputByteBufferNano.readInt32();
                        if (int32 != 0 && int32 != 1 && int32 != 2) {
                            continue;
                        }
                        this.feedback = int32;
                    }
                }
                else {
                    final int int33 = codedInputByteBufferNano.readInt32();
                    if (int33 != 0 && int33 != 1 && int33 != 2 && int33 != 3 && int33 != 4) {
                        continue;
                    }
                    this.gestureType = int33;
                }
            }
            else {
                this.identifier = codedInputByteBufferNano.readInt64();
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final long identifier = this.identifier;
        if (identifier != 0L) {
            codedOutputByteBufferNano.writeInt64(1, identifier);
        }
        final int gestureType = this.gestureType;
        if (gestureType != 0) {
            codedOutputByteBufferNano.writeInt32(2, gestureType);
        }
        final int feedback = this.feedback;
        if (feedback != 0) {
            codedOutputByteBufferNano.writeInt32(3, feedback);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
