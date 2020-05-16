// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tracing.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.MessageNano;

public final class SystemUiTraceEntryProto extends MessageNano
{
    private static volatile SystemUiTraceEntryProto[] _emptyArray;
    public long elapsedRealtimeNanos;
    public SystemUiTraceProto systemUi;
    
    public SystemUiTraceEntryProto() {
        this.clear();
    }
    
    public static SystemUiTraceEntryProto[] emptyArray() {
        if (SystemUiTraceEntryProto._emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (SystemUiTraceEntryProto._emptyArray == null) {
                    SystemUiTraceEntryProto._emptyArray = new SystemUiTraceEntryProto[0];
                }
            }
        }
        return SystemUiTraceEntryProto._emptyArray;
    }
    
    public SystemUiTraceEntryProto clear() {
        this.elapsedRealtimeNanos = 0L;
        this.systemUi = null;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final long elapsedRealtimeNanos = this.elapsedRealtimeNanos;
        int n = computeSerializedSize;
        if (elapsedRealtimeNanos != 0L) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeFixed64Size(1, elapsedRealtimeNanos);
        }
        final SystemUiTraceProto systemUi = this.systemUi;
        int n2 = n;
        if (systemUi != null) {
            n2 = n + CodedOutputByteBufferNano.computeMessageSize(3, systemUi);
        }
        return n2;
    }
    
    @Override
    public SystemUiTraceEntryProto mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 9) {
                if (tag != 26) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                    continue;
                }
                else {
                    if (this.systemUi == null) {
                        this.systemUi = new SystemUiTraceProto();
                    }
                    codedInputByteBufferNano.readMessage(this.systemUi);
                }
            }
            else {
                this.elapsedRealtimeNanos = codedInputByteBufferNano.readFixed64();
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final long elapsedRealtimeNanos = this.elapsedRealtimeNanos;
        if (elapsedRealtimeNanos != 0L) {
            codedOutputByteBufferNano.writeFixed64(1, elapsedRealtimeNanos);
        }
        final SystemUiTraceProto systemUi = this.systemUi;
        if (systemUi != null) {
            codedOutputByteBufferNano.writeMessage(3, systemUi);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
