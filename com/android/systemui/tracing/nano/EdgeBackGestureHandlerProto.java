// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tracing.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;

public final class EdgeBackGestureHandlerProto extends MessageNano
{
    public boolean allowGesture;
    
    public EdgeBackGestureHandlerProto() {
        this.clear();
    }
    
    public EdgeBackGestureHandlerProto clear() {
        this.allowGesture = false;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final boolean allowGesture = this.allowGesture;
        int n = computeSerializedSize;
        if (allowGesture) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(1, allowGesture);
        }
        return n;
    }
    
    @Override
    public EdgeBackGestureHandlerProto mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                this.allowGesture = codedInputByteBufferNano.readBool();
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final boolean allowGesture = this.allowGesture;
        if (allowGesture) {
            codedOutputByteBufferNano.writeBool(1, allowGesture);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
