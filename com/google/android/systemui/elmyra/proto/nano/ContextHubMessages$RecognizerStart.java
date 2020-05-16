// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.proto.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;

public final class ContextHubMessages$RecognizerStart extends MessageNano
{
    public float progressReportThreshold;
    public float sensitivity;
    
    public ContextHubMessages$RecognizerStart() {
        this.clear();
    }
    
    public ContextHubMessages$RecognizerStart clear() {
        this.progressReportThreshold = 0.0f;
        this.sensitivity = 0.0f;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        int computeSerializedSize;
        final int n = computeSerializedSize = super.computeSerializedSize();
        if (Float.floatToIntBits(this.progressReportThreshold) != Float.floatToIntBits(0.0f)) {
            computeSerializedSize = n + CodedOutputByteBufferNano.computeFloatSize(1, this.progressReportThreshold);
        }
        int n2 = computeSerializedSize;
        if (Float.floatToIntBits(this.sensitivity) != Float.floatToIntBits(0.0f)) {
            n2 = computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(2, this.sensitivity);
        }
        return n2;
    }
    
    @Override
    public ContextHubMessages$RecognizerStart mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 13) {
                if (tag != 21) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                    continue;
                }
                else {
                    this.sensitivity = codedInputByteBufferNano.readFloat();
                }
            }
            else {
                this.progressReportThreshold = codedInputByteBufferNano.readFloat();
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (Float.floatToIntBits(this.progressReportThreshold) != Float.floatToIntBits(0.0f)) {
            codedOutputByteBufferNano.writeFloat(1, this.progressReportThreshold);
        }
        if (Float.floatToIntBits(this.sensitivity) != Float.floatToIntBits(0.0f)) {
            codedOutputByteBufferNano.writeFloat(2, this.sensitivity);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
