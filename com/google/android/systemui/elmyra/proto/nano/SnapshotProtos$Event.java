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

public final class SnapshotProtos$Event extends MessageNano
{
    private static volatile SnapshotProtos$Event[] _emptyArray;
    private int typesCase_;
    private Object types_;
    
    public SnapshotProtos$Event() {
        this.typesCase_ = 0;
        this.clear();
    }
    
    public static SnapshotProtos$Event[] emptyArray() {
        if (SnapshotProtos$Event._emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (SnapshotProtos$Event._emptyArray == null) {
                    SnapshotProtos$Event._emptyArray = new SnapshotProtos$Event[0];
                }
            }
        }
        return SnapshotProtos$Event._emptyArray;
    }
    
    public SnapshotProtos$Event clear() {
        this.clearTypes();
        super.cachedSize = -1;
        return this;
    }
    
    public SnapshotProtos$Event clearTypes() {
        this.typesCase_ = 0;
        this.types_ = null;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        int computeSerializedSize;
        final int n = computeSerializedSize = super.computeSerializedSize();
        if (this.typesCase_ == 1) {
            computeSerializedSize = n + CodedOutputByteBufferNano.computeMessageSize(1, (MessageNano)this.types_);
        }
        int n2 = computeSerializedSize;
        if (this.typesCase_ == 2) {
            n2 = computeSerializedSize + CodedOutputByteBufferNano.computeEnumSize(2, (int)this.types_);
        }
        return n2;
    }
    
    public int getGestureStage() {
        if (this.typesCase_ == 2) {
            return (int)this.types_;
        }
        return 0;
    }
    
    public ChassisProtos$SensorEvent getSensorEvent() {
        if (this.typesCase_ == 1) {
            return (ChassisProtos$SensorEvent)this.types_;
        }
        return null;
    }
    
    public boolean hasGestureStage() {
        return this.typesCase_ == 2;
    }
    
    public boolean hasSensorEvent() {
        final int typesCase_ = this.typesCase_;
        boolean b = true;
        if (typesCase_ != 1) {
            b = false;
        }
        return b;
    }
    
    @Override
    public SnapshotProtos$Event mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 10) {
                if (tag != 16) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                    continue;
                }
                else {
                    this.types_ = codedInputByteBufferNano.readEnum();
                    this.typesCase_ = 2;
                }
            }
            else {
                if (this.typesCase_ != 1) {
                    this.types_ = new ChassisProtos$SensorEvent();
                }
                codedInputByteBufferNano.readMessage((MessageNano)this.types_);
                this.typesCase_ = 1;
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (this.typesCase_ == 1) {
            codedOutputByteBufferNano.writeMessage(1, (MessageNano)this.types_);
        }
        if (this.typesCase_ == 2) {
            codedOutputByteBufferNano.writeEnum(2, (int)this.types_);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
