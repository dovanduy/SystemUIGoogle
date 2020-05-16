// 
// Decompiled by Procyon v0.5.36
// 

package com.android.framework.protobuf.nano;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;

public final class CodedOutputByteBufferNano
{
    private final ByteBuffer buffer;
    
    private CodedOutputByteBufferNano(final ByteBuffer buffer) {
        (this.buffer = buffer).order(ByteOrder.LITTLE_ENDIAN);
    }
    
    private CodedOutputByteBufferNano(final byte[] array, final int offset, final int length) {
        this(ByteBuffer.wrap(array, offset, length));
    }
    
    public static CodedOutputByteBufferNano newInstance(final byte[] array, final int n, final int n2) {
        return new CodedOutputByteBufferNano(array, n, n2);
    }
    
    public void checkNoSpaceLeft() {
        if (this.spaceLeft() == 0) {
            return;
        }
        throw new IllegalStateException("Did not write as much data as expected.");
    }
    
    public int spaceLeft() {
        return this.buffer.remaining();
    }
}
