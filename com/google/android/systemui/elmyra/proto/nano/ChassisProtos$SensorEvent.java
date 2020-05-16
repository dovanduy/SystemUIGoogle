// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.proto.nano;

import java.io.IOException;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.MessageNano;

public final class ChassisProtos$SensorEvent extends MessageNano
{
    public long timestamp;
    public float[] values;
    
    public ChassisProtos$SensorEvent() {
        this.clear();
    }
    
    public ChassisProtos$SensorEvent clear() {
        this.timestamp = 0L;
        this.values = WireFormatNano.EMPTY_FLOAT_ARRAY;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final long timestamp = this.timestamp;
        int n = computeSerializedSize;
        if (timestamp != 0L) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeUInt64Size(1, timestamp);
        }
        final float[] values = this.values;
        int n2 = n;
        if (values != null) {
            n2 = n;
            if (values.length > 0) {
                n2 = n + values.length * 4 + values.length * 1;
            }
        }
        return n2;
    }
    
    @Override
    public ChassisProtos$SensorEvent mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 8) {
                if (tag != 18) {
                    if (tag != 21) {
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                        continue;
                    }
                    else {
                        final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 21);
                        final float[] values = this.values;
                        int length;
                        if (values == null) {
                            length = 0;
                        }
                        else {
                            length = values.length;
                        }
                        final int n = repeatedFieldArrayLength + length;
                        final float[] values2 = new float[n];
                        int i = length;
                        if (length != 0) {
                            System.arraycopy(this.values, 0, values2, 0, length);
                            i = length;
                        }
                        while (i < n - 1) {
                            values2[i] = codedInputByteBufferNano.readFloat();
                            codedInputByteBufferNano.readTag();
                            ++i;
                        }
                        values2[i] = codedInputByteBufferNano.readFloat();
                        this.values = values2;
                    }
                }
                else {
                    final int rawVarint32 = codedInputByteBufferNano.readRawVarint32();
                    final int pushLimit = codedInputByteBufferNano.pushLimit(rawVarint32);
                    final int n2 = rawVarint32 / 4;
                    final float[] values3 = this.values;
                    int length2;
                    if (values3 == null) {
                        length2 = 0;
                    }
                    else {
                        length2 = values3.length;
                    }
                    final int n3 = n2 + length2;
                    final float[] values4 = new float[n3];
                    int j = length2;
                    if (length2 != 0) {
                        System.arraycopy(this.values, 0, values4, 0, length2);
                        j = length2;
                    }
                    while (j < n3) {
                        values4[j] = codedInputByteBufferNano.readFloat();
                        ++j;
                    }
                    this.values = values4;
                    codedInputByteBufferNano.popLimit(pushLimit);
                }
            }
            else {
                this.timestamp = codedInputByteBufferNano.readUInt64();
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final long timestamp = this.timestamp;
        if (timestamp != 0L) {
            codedOutputByteBufferNano.writeUInt64(1, timestamp);
        }
        final float[] values = this.values;
        if (values != null && values.length > 0) {
            int n = 0;
            while (true) {
                final float[] values2 = this.values;
                if (n >= values2.length) {
                    break;
                }
                codedOutputByteBufferNano.writeFloat(2, values2[n]);
                ++n;
            }
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
