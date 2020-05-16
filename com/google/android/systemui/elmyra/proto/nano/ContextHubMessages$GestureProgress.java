// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.proto.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;

public final class ContextHubMessages$GestureProgress extends MessageNano
{
    public float progress;
    
    public ContextHubMessages$GestureProgress() {
        this.clear();
    }
    
    public static ContextHubMessages$GestureProgress parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
        final ContextHubMessages$GestureProgress contextHubMessages$GestureProgress = new ContextHubMessages$GestureProgress();
        MessageNano.mergeFrom(contextHubMessages$GestureProgress, array);
        return contextHubMessages$GestureProgress;
    }
    
    public ContextHubMessages$GestureProgress clear() {
        this.progress = 0.0f;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        if (Float.floatToIntBits(this.progress) != Float.floatToIntBits(0.0f)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.progress);
        }
        return computeSerializedSize;
    }
    
    @Override
    public ContextHubMessages$GestureProgress mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 13) {
                if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                    return this;
                }
                continue;
            }
            else {
                this.progress = codedInputByteBufferNano.readFloat();
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (Float.floatToIntBits(this.progress) != Float.floatToIntBits(0.0f)) {
            codedOutputByteBufferNano.writeFloat(1, this.progress);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
