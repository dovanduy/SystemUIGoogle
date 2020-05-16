// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.smartspace.nano;

import java.io.IOException;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import java.util.Arrays;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.MessageNano;

public final class SmartspaceProto$CardWrapper extends MessageNano
{
    public SmartspaceProto$SmartspaceUpdate.SmartspaceCard card;
    public long gsaUpdateTime;
    public int gsaVersionCode;
    public byte[] icon;
    public boolean isIconGrayscale;
    public long publishTime;
    
    public SmartspaceProto$CardWrapper() {
        this.clear();
    }
    
    public SmartspaceProto$CardWrapper clear() {
        this.card = null;
        this.publishTime = 0L;
        this.gsaUpdateTime = 0L;
        this.gsaVersionCode = 0;
        this.icon = WireFormatNano.EMPTY_BYTES;
        this.isIconGrayscale = false;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final SmartspaceProto$SmartspaceUpdate.SmartspaceCard card = this.card;
        int n = computeSerializedSize;
        if (card != null) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, card);
        }
        final long publishTime = this.publishTime;
        int n2 = n;
        if (publishTime != 0L) {
            n2 = n + CodedOutputByteBufferNano.computeInt64Size(2, publishTime);
        }
        final long gsaUpdateTime = this.gsaUpdateTime;
        int n3 = n2;
        if (gsaUpdateTime != 0L) {
            n3 = n2 + CodedOutputByteBufferNano.computeInt64Size(3, gsaUpdateTime);
        }
        final int gsaVersionCode = this.gsaVersionCode;
        int n4 = n3;
        if (gsaVersionCode != 0) {
            n4 = n3 + CodedOutputByteBufferNano.computeInt32Size(4, gsaVersionCode);
        }
        int n5 = n4;
        if (!Arrays.equals(this.icon, WireFormatNano.EMPTY_BYTES)) {
            n5 = n4 + CodedOutputByteBufferNano.computeBytesSize(5, this.icon);
        }
        final boolean isIconGrayscale = this.isIconGrayscale;
        int n6 = n5;
        if (isIconGrayscale) {
            n6 = n5 + CodedOutputByteBufferNano.computeBoolSize(6, isIconGrayscale);
        }
        return n6;
    }
    
    @Override
    public SmartspaceProto$CardWrapper mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 10) {
                if (tag != 16) {
                    if (tag != 24) {
                        if (tag != 32) {
                            if (tag != 42) {
                                if (tag != 48) {
                                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                        return this;
                                    }
                                    continue;
                                }
                                else {
                                    this.isIconGrayscale = codedInputByteBufferNano.readBool();
                                }
                            }
                            else {
                                this.icon = codedInputByteBufferNano.readBytes();
                            }
                        }
                        else {
                            this.gsaVersionCode = codedInputByteBufferNano.readInt32();
                        }
                    }
                    else {
                        this.gsaUpdateTime = codedInputByteBufferNano.readInt64();
                    }
                }
                else {
                    this.publishTime = codedInputByteBufferNano.readInt64();
                }
            }
            else {
                if (this.card == null) {
                    this.card = new SmartspaceProto$SmartspaceUpdate.SmartspaceCard();
                }
                codedInputByteBufferNano.readMessage(this.card);
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final SmartspaceProto$SmartspaceUpdate.SmartspaceCard card = this.card;
        if (card != null) {
            codedOutputByteBufferNano.writeMessage(1, card);
        }
        final long publishTime = this.publishTime;
        if (publishTime != 0L) {
            codedOutputByteBufferNano.writeInt64(2, publishTime);
        }
        final long gsaUpdateTime = this.gsaUpdateTime;
        if (gsaUpdateTime != 0L) {
            codedOutputByteBufferNano.writeInt64(3, gsaUpdateTime);
        }
        final int gsaVersionCode = this.gsaVersionCode;
        if (gsaVersionCode != 0) {
            codedOutputByteBufferNano.writeInt32(4, gsaVersionCode);
        }
        if (!Arrays.equals(this.icon, WireFormatNano.EMPTY_BYTES)) {
            codedOutputByteBufferNano.writeBytes(5, this.icon);
        }
        final boolean isIconGrayscale = this.isIconGrayscale;
        if (isIconGrayscale) {
            codedOutputByteBufferNano.writeBool(6, isIconGrayscale);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
