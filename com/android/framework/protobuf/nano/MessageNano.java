// 
// Decompiled by Procyon v0.5.36
// 

package com.android.framework.protobuf.nano;

import java.io.IOException;

public abstract class MessageNano
{
    public static final void toByteArray(final MessageNano messageNano, final byte[] array, final int n, final int n2) {
        try {
            final CodedOutputByteBufferNano instance = CodedOutputByteBufferNano.newInstance(array, n, n2);
            messageNano.writeTo(instance);
            instance.checkNoSpaceLeft();
        }
        catch (IOException cause) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", cause);
        }
    }
    
    public static final byte[] toByteArray(final MessageNano messageNano) {
        final int serializedSize = messageNano.getSerializedSize();
        final byte[] array = new byte[serializedSize];
        toByteArray(messageNano, array, 0, serializedSize);
        return array;
    }
    
    public MessageNano clone() throws CloneNotSupportedException {
        return (MessageNano)super.clone();
    }
    
    protected int computeSerializedSize() {
        return 0;
    }
    
    public int getSerializedSize() {
        return this.computeSerializedSize();
    }
    
    @Override
    public String toString() {
        return MessageNanoPrinter.print(this);
    }
    
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
    }
}
