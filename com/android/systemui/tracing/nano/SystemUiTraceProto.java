// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tracing.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;

public final class SystemUiTraceProto extends MessageNano
{
    public EdgeBackGestureHandlerProto edgeBackGestureHandler;
    
    public SystemUiTraceProto() {
        this.clear();
    }
    
    public SystemUiTraceProto clear() {
        this.edgeBackGestureHandler = null;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final EdgeBackGestureHandlerProto edgeBackGestureHandler = this.edgeBackGestureHandler;
        int n = computeSerializedSize;
        if (edgeBackGestureHandler != null) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, edgeBackGestureHandler);
        }
        return n;
    }
    
    @Override
    public SystemUiTraceProto mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                if (this.edgeBackGestureHandler == null) {
                    this.edgeBackGestureHandler = new EdgeBackGestureHandlerProto();
                }
                codedInputByteBufferNano.readMessage(this.edgeBackGestureHandler);
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final EdgeBackGestureHandlerProto edgeBackGestureHandler = this.edgeBackGestureHandler;
        if (edgeBackGestureHandler != null) {
            codedOutputByteBufferNano.writeMessage(1, edgeBackGestureHandler);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
