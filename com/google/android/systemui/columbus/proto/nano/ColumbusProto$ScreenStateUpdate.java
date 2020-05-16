// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.proto.nano;

import java.io.IOException;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;

public final class ColumbusProto$ScreenStateUpdate extends MessageNano
{
    public int screenState;
    
    public ColumbusProto$ScreenStateUpdate() {
        this.clear();
    }
    
    public ColumbusProto$ScreenStateUpdate clear() {
        this.screenState = 0;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final int screenState = this.screenState;
        int n = computeSerializedSize;
        if (screenState != 0) {
            n = computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(1, screenState);
        }
        return n;
    }
    
    @Override
    public ColumbusProto$ScreenStateUpdate mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 8) {
                if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                    return this;
                }
                continue;
            }
            else {
                final int int32 = codedInputByteBufferNano.readInt32();
                if (int32 != 0 && int32 != 1 && int32 != 2) {
                    continue;
                }
                this.screenState = int32;
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final int screenState = this.screenState;
        if (screenState != 0) {
            codedOutputByteBufferNano.writeInt32(1, screenState);
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
