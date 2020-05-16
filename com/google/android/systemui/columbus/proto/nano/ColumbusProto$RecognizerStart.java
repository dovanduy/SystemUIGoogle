// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.proto.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;

public final class ColumbusProto$RecognizerStart extends MessageNano
{
    public float sensitivity;
    
    public ColumbusProto$RecognizerStart() {
        this.clear();
    }
    
    public ColumbusProto$RecognizerStart clear() {
        this.sensitivity = 0.0f;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        if (Float.floatToIntBits(this.sensitivity) != Float.floatToIntBits(0.0f)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.sensitivity);
        }
        return computeSerializedSize;
    }
    
    @Override
    public ColumbusProto$RecognizerStart mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                this.sensitivity = codedInputByteBufferNano.readFloat();
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (Float.floatToIntBits(this.sensitivity) != Float.floatToIntBits(0.0f)) {
            codedOutputByteBufferNano.writeFloat(1, this.sensitivity);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
