// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf.nano;

import java.io.IOException;

public final class WireFormatNano
{
    public static final byte[] EMPTY_BYTES;
    public static final float[] EMPTY_FLOAT_ARRAY;
    
    static {
        EMPTY_FLOAT_ARRAY = new float[0];
        EMPTY_BYTES = new byte[0];
    }
    
    public static final int getRepeatedFieldArrayLength(final CodedInputByteBufferNano codedInputByteBufferNano, final int n) throws IOException {
        final int position = codedInputByteBufferNano.getPosition();
        codedInputByteBufferNano.skipField(n);
        int n2 = 1;
        while (codedInputByteBufferNano.readTag() == n) {
            codedInputByteBufferNano.skipField(n);
            ++n2;
        }
        codedInputByteBufferNano.rewindToPosition(position);
        return n2;
    }
    
    public static int getTagFieldNumber(final int n) {
        return n >>> 3;
    }
    
    static int getTagWireType(final int n) {
        return n & 0x7;
    }
    
    static int makeTag(final int n, final int n2) {
        return n << 3 | n2;
    }
    
    public static boolean parseUnknownField(final CodedInputByteBufferNano codedInputByteBufferNano, final int n) throws IOException {
        return codedInputByteBufferNano.skipField(n);
    }
}
