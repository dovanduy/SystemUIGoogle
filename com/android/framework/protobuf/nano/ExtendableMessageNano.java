// 
// Decompiled by Procyon v0.5.36
// 

package com.android.framework.protobuf.nano;

import java.io.IOException;

public abstract class ExtendableMessageNano<M extends ExtendableMessageNano<M>> extends MessageNano
{
    protected FieldArray unknownFieldData;
    
    @Override
    public M clone() throws CloneNotSupportedException {
        final ExtendableMessageNano extendableMessageNano = (ExtendableMessageNano)super.clone();
        InternalNano.cloneUnknownFieldData(this, extendableMessageNano);
        return (M)extendableMessageNano;
    }
    
    @Override
    protected int computeSerializedSize() {
        final FieldArray unknownFieldData = this.unknownFieldData;
        int n = 0;
        int i = 0;
        if (unknownFieldData != null) {
            n = 0;
            while (i < this.unknownFieldData.size()) {
                n += this.unknownFieldData.dataAt(i).computeSerializedSize();
                ++i;
            }
        }
        return n;
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (this.unknownFieldData == null) {
            return;
        }
        for (int i = 0; i < this.unknownFieldData.size(); ++i) {
            this.unknownFieldData.dataAt(i).writeTo(codedOutputByteBufferNano);
        }
    }
}
