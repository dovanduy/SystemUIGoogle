// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tracing.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;

public final class SystemUiTraceFileProto extends MessageNano
{
    public SystemUiTraceEntryProto[] entry;
    public long magicNumber;
    
    public SystemUiTraceFileProto() {
        this.clear();
    }
    
    public SystemUiTraceFileProto clear() {
        this.magicNumber = 0L;
        this.entry = SystemUiTraceEntryProto.emptyArray();
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final long magicNumber = this.magicNumber;
        int n = computeSerializedSize;
        if (magicNumber != 0L) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeFixed64Size(1, magicNumber);
        }
        final SystemUiTraceEntryProto[] entry = this.entry;
        int n2 = n;
        if (entry != null) {
            n2 = n;
            if (entry.length > 0) {
                int n3 = 0;
                while (true) {
                    final SystemUiTraceEntryProto[] entry2 = this.entry;
                    n2 = n;
                    if (n3 >= entry2.length) {
                        break;
                    }
                    final SystemUiTraceEntryProto systemUiTraceEntryProto = entry2[n3];
                    int n4 = n;
                    if (systemUiTraceEntryProto != null) {
                        n4 = n + CodedOutputByteBufferNano.computeMessageSize(2, systemUiTraceEntryProto);
                    }
                    ++n3;
                    n = n4;
                }
            }
        }
        return n2;
    }
    
    @Override
    public SystemUiTraceFileProto mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 9) {
                if (tag != 18) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                    continue;
                }
                else {
                    final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                    final SystemUiTraceEntryProto[] entry = this.entry;
                    int length;
                    if (entry == null) {
                        length = 0;
                    }
                    else {
                        length = entry.length;
                    }
                    final int n = repeatedFieldArrayLength + length;
                    final SystemUiTraceEntryProto[] entry2 = new SystemUiTraceEntryProto[n];
                    int i = length;
                    if (length != 0) {
                        System.arraycopy(this.entry, 0, entry2, 0, length);
                        i = length;
                    }
                    while (i < n - 1) {
                        codedInputByteBufferNano.readMessage(entry2[i] = new SystemUiTraceEntryProto());
                        codedInputByteBufferNano.readTag();
                        ++i;
                    }
                    codedInputByteBufferNano.readMessage(entry2[i] = new SystemUiTraceEntryProto());
                    this.entry = entry2;
                }
            }
            else {
                this.magicNumber = codedInputByteBufferNano.readFixed64();
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final long magicNumber = this.magicNumber;
        if (magicNumber != 0L) {
            codedOutputByteBufferNano.writeFixed64(1, magicNumber);
        }
        final SystemUiTraceEntryProto[] entry = this.entry;
        if (entry != null && entry.length > 0) {
            int n = 0;
            while (true) {
                final SystemUiTraceEntryProto[] entry2 = this.entry;
                if (n >= entry2.length) {
                    break;
                }
                final SystemUiTraceEntryProto systemUiTraceEntryProto = entry2[n];
                if (systemUiTraceEntryProto != null) {
                    codedOutputByteBufferNano.writeMessage(2, systemUiTraceEntryProto);
                }
                ++n;
            }
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
