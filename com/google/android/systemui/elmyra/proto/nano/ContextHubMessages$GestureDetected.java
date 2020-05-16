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

public final class ContextHubMessages$GestureDetected extends MessageNano
{
    public boolean hapticConsumed;
    public boolean hostSuspended;
    
    public ContextHubMessages$GestureDetected() {
        this.clear();
    }
    
    public static ContextHubMessages$GestureDetected parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
        final ContextHubMessages$GestureDetected contextHubMessages$GestureDetected = new ContextHubMessages$GestureDetected();
        MessageNano.mergeFrom(contextHubMessages$GestureDetected, array);
        return contextHubMessages$GestureDetected;
    }
    
    public ContextHubMessages$GestureDetected clear() {
        this.hostSuspended = false;
        this.hapticConsumed = false;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final boolean hostSuspended = this.hostSuspended;
        int n = computeSerializedSize;
        if (hostSuspended) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(1, hostSuspended);
        }
        final boolean hapticConsumed = this.hapticConsumed;
        int n2 = n;
        if (hapticConsumed) {
            n2 = n + CodedOutputByteBufferNano.computeBoolSize(2, hapticConsumed);
        }
        return n2;
    }
    
    @Override
    public ContextHubMessages$GestureDetected mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 8) {
                if (tag != 16) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                    continue;
                }
                else {
                    this.hapticConsumed = codedInputByteBufferNano.readBool();
                }
            }
            else {
                this.hostSuspended = codedInputByteBufferNano.readBool();
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final boolean hostSuspended = this.hostSuspended;
        if (hostSuspended) {
            codedOutputByteBufferNano.writeBool(1, hostSuspended);
        }
        final boolean hapticConsumed = this.hapticConsumed;
        if (hapticConsumed) {
            codedOutputByteBufferNano.writeBool(2, hapticConsumed);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
