// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.logging.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.MessageNano;

public final class Notifications$Notification extends MessageNano
{
    private static volatile Notifications$Notification[] _emptyArray;
    public int groupInstanceId;
    public int instanceId;
    public boolean isGroupSummary;
    public String packageName;
    public int section;
    public int uid;
    
    public Notifications$Notification() {
        this.clear();
    }
    
    public static Notifications$Notification[] emptyArray() {
        if (Notifications$Notification._emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (Notifications$Notification._emptyArray == null) {
                    Notifications$Notification._emptyArray = new Notifications$Notification[0];
                }
            }
        }
        return Notifications$Notification._emptyArray;
    }
    
    public Notifications$Notification clear() {
        this.uid = 0;
        this.packageName = "";
        this.instanceId = 0;
        this.groupInstanceId = 0;
        this.isGroupSummary = false;
        this.section = 0;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final int uid = this.uid;
        int n = computeSerializedSize;
        if (uid != 0) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(1, uid);
        }
        int n2 = n;
        if (!this.packageName.equals("")) {
            n2 = n + CodedOutputByteBufferNano.computeStringSize(2, this.packageName);
        }
        final int instanceId = this.instanceId;
        int n3 = n2;
        if (instanceId != 0) {
            n3 = n2 + CodedOutputByteBufferNano.computeInt32Size(3, instanceId);
        }
        final int groupInstanceId = this.groupInstanceId;
        int n4 = n3;
        if (groupInstanceId != 0) {
            n4 = n3 + CodedOutputByteBufferNano.computeInt32Size(4, groupInstanceId);
        }
        final boolean isGroupSummary = this.isGroupSummary;
        int n5 = n4;
        if (isGroupSummary) {
            n5 = n4 + CodedOutputByteBufferNano.computeBoolSize(5, isGroupSummary);
        }
        final int section = this.section;
        int n6 = n5;
        if (section != 0) {
            n6 = n5 + CodedOutputByteBufferNano.computeInt32Size(6, section);
        }
        return n6;
    }
    
    @Override
    public Notifications$Notification mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 8) {
                if (tag != 18) {
                    if (tag != 24) {
                        if (tag != 32) {
                            if (tag != 40) {
                                if (tag != 48) {
                                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                        return this;
                                    }
                                    continue;
                                }
                                else {
                                    final int int32 = codedInputByteBufferNano.readInt32();
                                    if (int32 != 0 && int32 != 1 && int32 != 2 && int32 != 3 && int32 != 4) {
                                        continue;
                                    }
                                    this.section = int32;
                                }
                            }
                            else {
                                this.isGroupSummary = codedInputByteBufferNano.readBool();
                            }
                        }
                        else {
                            this.groupInstanceId = codedInputByteBufferNano.readInt32();
                        }
                    }
                    else {
                        this.instanceId = codedInputByteBufferNano.readInt32();
                    }
                }
                else {
                    this.packageName = codedInputByteBufferNano.readString();
                }
            }
            else {
                this.uid = codedInputByteBufferNano.readInt32();
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final int uid = this.uid;
        if (uid != 0) {
            codedOutputByteBufferNano.writeInt32(1, uid);
        }
        if (!this.packageName.equals("")) {
            codedOutputByteBufferNano.writeString(2, this.packageName);
        }
        final int instanceId = this.instanceId;
        if (instanceId != 0) {
            codedOutputByteBufferNano.writeInt32(3, instanceId);
        }
        final int groupInstanceId = this.groupInstanceId;
        if (groupInstanceId != 0) {
            codedOutputByteBufferNano.writeInt32(4, groupInstanceId);
        }
        final boolean isGroupSummary = this.isGroupSummary;
        if (isGroupSummary) {
            codedOutputByteBufferNano.writeBool(5, isGroupSummary);
        }
        final int section = this.section;
        if (section != 0) {
            codedOutputByteBufferNano.writeInt32(6, section);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
