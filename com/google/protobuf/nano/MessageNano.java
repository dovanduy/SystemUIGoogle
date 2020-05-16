// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf.nano;

import java.io.IOException;

public abstract class MessageNano
{
    protected volatile int cachedSize;
    
    public MessageNano() {
        this.cachedSize = -1;
    }
    
    public static final <T extends MessageNano> T mergeFrom(final T t, final byte[] array) throws InvalidProtocolBufferNanoException {
        mergeFrom(t, array, 0, array.length);
        return t;
    }
    
    public static final <T extends MessageNano> T mergeFrom(final T t, final byte[] array, final int n, final int n2) throws InvalidProtocolBufferNanoException {
        try {
            final CodedInputByteBufferNano instance = CodedInputByteBufferNano.newInstance(array, n, n2);
            t.mergeFrom(instance);
            instance.checkLastTagWas(0);
            return t;
        }
        catch (IOException ex2) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
        }
        catch (InvalidProtocolBufferNanoException ex) {
            throw ex;
        }
    }
    
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
    
    public int getCachedSize() {
        if (this.cachedSize < 0) {
            this.getSerializedSize();
        }
        return this.cachedSize;
    }
    
    public int getSerializedSize() {
        return this.cachedSize = this.computeSerializedSize();
    }
    
    public abstract MessageNano mergeFrom(final CodedInputByteBufferNano p0) throws IOException;
    
    @Override
    public String toString() {
        return MessageNanoPrinter.print(this);
    }
    
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
    }
}
