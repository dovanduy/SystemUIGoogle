// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.proto.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;

public final class ElmyraFilters$MedianFilter extends MessageNano
{
    public int windowSize;
    
    public ElmyraFilters$MedianFilter() {
        this.clear();
    }
    
    public ElmyraFilters$MedianFilter clear() {
        this.windowSize = 0;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final int windowSize = this.windowSize;
        int n = computeSerializedSize;
        if (windowSize != 0) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeUInt32Size(1, windowSize);
        }
        return n;
    }
    
    @Override
    public ElmyraFilters$MedianFilter mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                this.windowSize = codedInputByteBufferNano.readUInt32();
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final int windowSize = this.windowSize;
        if (windowSize != 0) {
            codedOutputByteBufferNano.writeUInt32(1, windowSize);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
