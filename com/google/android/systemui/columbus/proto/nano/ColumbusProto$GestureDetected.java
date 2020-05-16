// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.proto.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;

public final class ColumbusProto$GestureDetected extends MessageNano
{
    public int gestureType;
    
    public ColumbusProto$GestureDetected() {
        this.clear();
    }
    
    public static ColumbusProto$GestureDetected parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
        final ColumbusProto$GestureDetected columbusProto$GestureDetected = new ColumbusProto$GestureDetected();
        MessageNano.mergeFrom(columbusProto$GestureDetected, array);
        return columbusProto$GestureDetected;
    }
    
    public ColumbusProto$GestureDetected clear() {
        this.gestureType = 0;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final int gestureType = this.gestureType;
        int n = computeSerializedSize;
        if (gestureType != 0) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(1, gestureType);
        }
        return n;
    }
    
    @Override
    public ColumbusProto$GestureDetected mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 8) {
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
                this.gestureType = int32;
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final int gestureType = this.gestureType;
        if (gestureType != 0) {
            codedOutputByteBufferNano.writeInt32(1, gestureType);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
