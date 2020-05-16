// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.proto.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.MessageNano;

public final class ChassisProtos$Sensor extends MessageNano
{
    private static volatile ChassisProtos$Sensor[] _emptyArray;
    public ElmyraFilters$Filter[] filters;
    public int gain;
    public float sensitivity;
    public int source;
    
    public ChassisProtos$Sensor() {
        this.clear();
    }
    
    public static ChassisProtos$Sensor[] emptyArray() {
        if (ChassisProtos$Sensor._emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (ChassisProtos$Sensor._emptyArray == null) {
                    ChassisProtos$Sensor._emptyArray = new ChassisProtos$Sensor[0];
                }
            }
        }
        return ChassisProtos$Sensor._emptyArray;
    }
    
    public ChassisProtos$Sensor clear() {
        this.source = 0;
        this.gain = 0;
        this.sensitivity = 0.0f;
        this.filters = ElmyraFilters$Filter.emptyArray();
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final int source = this.source;
        int n = computeSerializedSize;
        if (source != 0) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeUInt32Size(1, source);
        }
        final int gain = this.gain;
        int n2 = n;
        if (gain != 0) {
            n2 = n + CodedOutputByteBufferNano.computeInt32Size(2, gain);
        }
        int n3 = n2;
        if (Float.floatToIntBits(this.sensitivity) != Float.floatToIntBits(0.0f)) {
            n3 = n2 + CodedOutputByteBufferNano.computeFloatSize(3, this.sensitivity);
        }
        final ElmyraFilters$Filter[] filters = this.filters;
        int n4 = n3;
        if (filters != null) {
            n4 = n3;
            if (filters.length > 0) {
                int n5 = 0;
                while (true) {
                    final ElmyraFilters$Filter[] filters2 = this.filters;
                    n4 = n3;
                    if (n5 >= filters2.length) {
                        break;
                    }
                    final ElmyraFilters$Filter elmyraFilters$Filter = filters2[n5];
                    int n6 = n3;
                    if (elmyraFilters$Filter != null) {
                        n6 = n3 + CodedOutputByteBufferNano.computeMessageSize(4, elmyraFilters$Filter);
                    }
                    ++n5;
                    n3 = n6;
                }
            }
        }
        return n4;
    }
    
    @Override
    public ChassisProtos$Sensor mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 8) {
                if (tag != 16) {
                    if (tag != 29) {
                        if (tag != 34) {
                            if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            continue;
                        }
                        else {
                            final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 34);
                            final ElmyraFilters$Filter[] filters = this.filters;
                            int length;
                            if (filters == null) {
                                length = 0;
                            }
                            else {
                                length = filters.length;
                            }
                            final int n = repeatedFieldArrayLength + length;
                            final ElmyraFilters$Filter[] filters2 = new ElmyraFilters$Filter[n];
                            int i = length;
                            if (length != 0) {
                                System.arraycopy(this.filters, 0, filters2, 0, length);
                                i = length;
                            }
                            while (i < n - 1) {
                                codedInputByteBufferNano.readMessage(filters2[i] = new ElmyraFilters$Filter());
                                codedInputByteBufferNano.readTag();
                                ++i;
                            }
                            codedInputByteBufferNano.readMessage(filters2[i] = new ElmyraFilters$Filter());
                            this.filters = filters2;
                        }
                    }
                    else {
                        this.sensitivity = codedInputByteBufferNano.readFloat();
                    }
                }
                else {
                    this.gain = codedInputByteBufferNano.readInt32();
                }
            }
            else {
                this.source = codedInputByteBufferNano.readUInt32();
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final int source = this.source;
        if (source != 0) {
            codedOutputByteBufferNano.writeUInt32(1, source);
        }
        final int gain = this.gain;
        if (gain != 0) {
            codedOutputByteBufferNano.writeInt32(2, gain);
        }
        if (Float.floatToIntBits(this.sensitivity) != Float.floatToIntBits(0.0f)) {
            codedOutputByteBufferNano.writeFloat(3, this.sensitivity);
        }
        final ElmyraFilters$Filter[] filters = this.filters;
        if (filters != null && filters.length > 0) {
            int n = 0;
            while (true) {
                final ElmyraFilters$Filter[] filters2 = this.filters;
                if (n >= filters2.length) {
                    break;
                }
                final ElmyraFilters$Filter elmyraFilters$Filter = filters2[n];
                if (elmyraFilters$Filter != null) {
                    codedOutputByteBufferNano.writeMessage(4, elmyraFilters$Filter);
                }
                ++n;
            }
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
