// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.proto.nano;

import com.google.protobuf.nano.CodedOutputByteBufferNano;
import java.io.IOException;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.MessageNano;

public final class ElmyraFilters$FIRFilter extends MessageNano
{
    public float[] coefficients;
    
    public ElmyraFilters$FIRFilter() {
        this.clear();
    }
    
    public ElmyraFilters$FIRFilter clear() {
        this.coefficients = WireFormatNano.EMPTY_FLOAT_ARRAY;
        super.cachedSize = -1;
        return this;
    }
    
    @Override
    protected int computeSerializedSize() {
        final int computeSerializedSize = super.computeSerializedSize();
        final float[] coefficients = this.coefficients;
        int n = computeSerializedSize;
        if (coefficients != null) {
            n = computeSerializedSize;
            if (coefficients.length > 0) {
                n = computeSerializedSize + coefficients.length * 4 + coefficients.length * 1;
            }
        }
        return n;
    }
    
    @Override
    public ElmyraFilters$FIRFilter mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 10) {
                if (tag != 13) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                    continue;
                }
                else {
                    final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 13);
                    final float[] coefficients = this.coefficients;
                    int length;
                    if (coefficients == null) {
                        length = 0;
                    }
                    else {
                        length = coefficients.length;
                    }
                    final int n = repeatedFieldArrayLength + length;
                    final float[] coefficients2 = new float[n];
                    int i = length;
                    if (length != 0) {
                        System.arraycopy(this.coefficients, 0, coefficients2, 0, length);
                        i = length;
                    }
                    while (i < n - 1) {
                        coefficients2[i] = codedInputByteBufferNano.readFloat();
                        codedInputByteBufferNano.readTag();
                        ++i;
                    }
                    coefficients2[i] = codedInputByteBufferNano.readFloat();
                    this.coefficients = coefficients2;
                }
            }
            else {
                final int rawVarint32 = codedInputByteBufferNano.readRawVarint32();
                final int pushLimit = codedInputByteBufferNano.pushLimit(rawVarint32);
                final int n2 = rawVarint32 / 4;
                final float[] coefficients3 = this.coefficients;
                int length2;
                if (coefficients3 == null) {
                    length2 = 0;
                }
                else {
                    length2 = coefficients3.length;
                }
                final int n3 = n2 + length2;
                final float[] coefficients4 = new float[n3];
                int j = length2;
                if (length2 != 0) {
                    System.arraycopy(this.coefficients, 0, coefficients4, 0, length2);
                    j = length2;
                }
                while (j < n3) {
                    coefficients4[j] = codedInputByteBufferNano.readFloat();
                    ++j;
                }
                this.coefficients = coefficients4;
                codedInputByteBufferNano.popLimit(pushLimit);
            }
        }
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        final float[] coefficients = this.coefficients;
        if (coefficients != null && coefficients.length > 0) {
            int n = 0;
            while (true) {
                final float[] coefficients2 = this.coefficients;
                if (n >= coefficients2.length) {
                    break;
                }
                codedOutputByteBufferNano.writeFloat(1, coefficients2[n]);
                ++n;
            }
        }
        super.writeTo(codedOutputByteBufferNano);
    }
}
