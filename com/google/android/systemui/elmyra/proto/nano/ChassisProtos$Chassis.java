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

public final class ChassisProtos$Chassis extends MessageNano
{
    public ElmyraFilters$Filter[] defaultFilters;
    public ChassisProtos$Sensor[] sensors;
    
    public ChassisProtos$Chassis() {
        this.clear();
    }
    
    public static ChassisProtos$Chassis parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
        final ChassisProtos$Chassis chassisProtos$Chassis = new ChassisProtos$Chassis();
        MessageNano.mergeFrom(chassisProtos$Chassis, array);
        return chassisProtos$Chassis;
    }
    
    public ChassisProtos$Chassis clear() {
        this.sensors = ChassisProtos$Sensor.emptyArray();
        this.defaultFilters = ElmyraFilters$Filter.emptyArray();
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        final ChassisProtos$Sensor[] sensors = this.sensors;
        final int n = 0;
        int n2 = computeSerializedSize;
        if (sensors != null) {
            n2 = computeSerializedSize;
            if (sensors.length > 0) {
                int n3 = 0;
                while (true) {
                    final ChassisProtos$Sensor[] sensors2 = this.sensors;
                    n2 = computeSerializedSize;
                    if (n3 >= sensors2.length) {
                        break;
                    }
                    final ChassisProtos$Sensor chassisProtos$Sensor = sensors2[n3];
                    int n4 = computeSerializedSize;
                    if (chassisProtos$Sensor != null) {
                        n4 = computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, chassisProtos$Sensor);
                    }
                    ++n3;
                    computeSerializedSize = n4;
                }
            }
        }
        final ElmyraFilters$Filter[] defaultFilters = this.defaultFilters;
        int n5 = n2;
        if (defaultFilters != null) {
            n5 = n2;
            if (defaultFilters.length > 0) {
                int n6 = n;
                while (true) {
                    final ElmyraFilters$Filter[] defaultFilters2 = this.defaultFilters;
                    n5 = n2;
                    if (n6 >= defaultFilters2.length) {
                        break;
                    }
                    final ElmyraFilters$Filter elmyraFilters$Filter = defaultFilters2[n6];
                    int n7 = n2;
                    if (elmyraFilters$Filter != null) {
                        n7 = n2 + CodedOutputByteBufferNano.computeMessageSize(2, elmyraFilters$Filter);
                    }
                    ++n6;
                    n2 = n7;
                }
            }
        }
        return n5;
    }
    
    @Override
    public ChassisProtos$Chassis mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 10) {
                if (tag != 18) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                    continue;
                }
                else {
                    final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                    final ElmyraFilters$Filter[] defaultFilters = this.defaultFilters;
                    int length;
                    if (defaultFilters == null) {
                        length = 0;
                    }
                    else {
                        length = defaultFilters.length;
                    }
                    final int n = repeatedFieldArrayLength + length;
                    final ElmyraFilters$Filter[] defaultFilters2 = new ElmyraFilters$Filter[n];
                    int i = length;
                    if (length != 0) {
                        System.arraycopy(this.defaultFilters, 0, defaultFilters2, 0, length);
                        i = length;
                    }
                    while (i < n - 1) {
                        codedInputByteBufferNano.readMessage(defaultFilters2[i] = new ElmyraFilters$Filter());
                        codedInputByteBufferNano.readTag();
                        ++i;
                    }
                    codedInputByteBufferNano.readMessage(defaultFilters2[i] = new ElmyraFilters$Filter());
                    this.defaultFilters = defaultFilters2;
                }
            }
            else {
                final int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 10);
                final ChassisProtos$Sensor[] sensors = this.sensors;
                int length2;
                if (sensors == null) {
                    length2 = 0;
                }
                else {
                    length2 = sensors.length;
                }
                final int n2 = repeatedFieldArrayLength2 + length2;
                final ChassisProtos$Sensor[] sensors2 = new ChassisProtos$Sensor[n2];
                int j = length2;
                if (length2 != 0) {
                    System.arraycopy(this.sensors, 0, sensors2, 0, length2);
                    j = length2;
                }
                while (j < n2 - 1) {
                    codedInputByteBufferNano.readMessage(sensors2[j] = new ChassisProtos$Sensor());
                    codedInputByteBufferNano.readTag();
                    ++j;
                }
                codedInputByteBufferNano.readMessage(sensors2[j] = new ChassisProtos$Sensor());
                this.sensors = sensors2;
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final ChassisProtos$Sensor[] sensors = this.sensors;
        final int n = 0;
        if (sensors != null && sensors.length > 0) {
            int n2 = 0;
            while (true) {
                final ChassisProtos$Sensor[] sensors2 = this.sensors;
                if (n2 >= sensors2.length) {
                    break;
                }
                final ChassisProtos$Sensor chassisProtos$Sensor = sensors2[n2];
                if (chassisProtos$Sensor != null) {
                    codedOutputByteBufferNano.writeMessage(1, chassisProtos$Sensor);
                }
                ++n2;
            }
        }
        final ElmyraFilters$Filter[] defaultFilters = this.defaultFilters;
        if (defaultFilters != null && defaultFilters.length > 0) {
            int n3 = n;
            while (true) {
                final ElmyraFilters$Filter[] defaultFilters2 = this.defaultFilters;
                if (n3 >= defaultFilters2.length) {
                    break;
                }
                final ElmyraFilters$Filter elmyraFilters$Filter = defaultFilters2[n3];
                if (elmyraFilters$Filter != null) {
                    codedOutputByteBufferNano.writeMessage(2, elmyraFilters$Filter);
                }
                ++n3;
            }
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
