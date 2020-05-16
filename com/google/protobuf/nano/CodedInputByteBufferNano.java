// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf.nano;

import java.io.IOException;

public final class CodedInputByteBufferNano
{
    private final byte[] buffer;
    private int bufferPos;
    private int bufferSize;
    private int bufferSizeAfterLimit;
    private int bufferStart;
    private int currentLimit;
    private int lastTag;
    private int recursionDepth;
    private int recursionLimit;
    
    private CodedInputByteBufferNano(final byte[] buffer, final int n, final int n2) {
        this.currentLimit = Integer.MAX_VALUE;
        this.recursionLimit = 64;
        this.buffer = buffer;
        this.bufferStart = n;
        this.bufferSize = n2 + n;
        this.bufferPos = n;
    }
    
    public static CodedInputByteBufferNano newInstance(final byte[] array, final int n, final int n2) {
        return new CodedInputByteBufferNano(array, n, n2);
    }
    
    private void recomputeBufferSizeAfterLimit() {
        final int bufferSize = this.bufferSize + this.bufferSizeAfterLimit;
        this.bufferSize = bufferSize;
        final int currentLimit = this.currentLimit;
        if (bufferSize > currentLimit) {
            final int bufferSizeAfterLimit = bufferSize - currentLimit;
            this.bufferSizeAfterLimit = bufferSizeAfterLimit;
            this.bufferSize = bufferSize - bufferSizeAfterLimit;
        }
        else {
            this.bufferSizeAfterLimit = 0;
        }
    }
    
    public void checkLastTagWas(final int n) throws InvalidProtocolBufferNanoException {
        if (this.lastTag == n) {
            return;
        }
        throw InvalidProtocolBufferNanoException.invalidEndTag();
    }
    
    public int getPosition() {
        return this.bufferPos - this.bufferStart;
    }
    
    public boolean isAtEnd() {
        return this.bufferPos == this.bufferSize;
    }
    
    public void popLimit(final int currentLimit) {
        this.currentLimit = currentLimit;
        this.recomputeBufferSizeAfterLimit();
    }
    
    public int pushLimit(int currentLimit) throws InvalidProtocolBufferNanoException {
        if (currentLimit < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        }
        final int currentLimit2 = currentLimit + this.bufferPos;
        currentLimit = this.currentLimit;
        if (currentLimit2 <= currentLimit) {
            this.currentLimit = currentLimit2;
            this.recomputeBufferSizeAfterLimit();
            return currentLimit;
        }
        throw InvalidProtocolBufferNanoException.truncatedMessage();
    }
    
    public boolean readBool() throws IOException {
        return this.readRawVarint32() != 0;
    }
    
    public byte[] readBytes() throws IOException {
        final int rawVarint32 = this.readRawVarint32();
        final int bufferSize = this.bufferSize;
        final int bufferPos = this.bufferPos;
        if (rawVarint32 <= bufferSize - bufferPos && rawVarint32 > 0) {
            final byte[] array = new byte[rawVarint32];
            System.arraycopy(this.buffer, bufferPos, array, 0, rawVarint32);
            this.bufferPos += rawVarint32;
            return array;
        }
        if (rawVarint32 == 0) {
            return WireFormatNano.EMPTY_BYTES;
        }
        return this.readRawBytes(rawVarint32);
    }
    
    public int readEnum() throws IOException {
        return this.readRawVarint32();
    }
    
    public long readFixed64() throws IOException {
        return this.readRawLittleEndian64();
    }
    
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readRawLittleEndian32());
    }
    
    public int readInt32() throws IOException {
        return this.readRawVarint32();
    }
    
    public long readInt64() throws IOException {
        return this.readRawVarint64();
    }
    
    public void readMessage(final MessageNano messageNano) throws IOException {
        final int rawVarint32 = this.readRawVarint32();
        if (this.recursionDepth < this.recursionLimit) {
            final int pushLimit = this.pushLimit(rawVarint32);
            ++this.recursionDepth;
            messageNano.mergeFrom(this);
            this.checkLastTagWas(0);
            --this.recursionDepth;
            this.popLimit(pushLimit);
            return;
        }
        throw InvalidProtocolBufferNanoException.recursionLimitExceeded();
    }
    
    public byte readRawByte() throws IOException {
        final int bufferPos = this.bufferPos;
        if (bufferPos != this.bufferSize) {
            final byte[] buffer = this.buffer;
            this.bufferPos = bufferPos + 1;
            return buffer[bufferPos];
        }
        throw InvalidProtocolBufferNanoException.truncatedMessage();
    }
    
    public byte[] readRawBytes(final int n) throws IOException {
        if (n < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        }
        final int bufferPos = this.bufferPos;
        final int currentLimit = this.currentLimit;
        if (bufferPos + n > currentLimit) {
            this.skipRawBytes(currentLimit - bufferPos);
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
        if (n <= this.bufferSize - bufferPos) {
            final byte[] array = new byte[n];
            System.arraycopy(this.buffer, bufferPos, array, 0, n);
            this.bufferPos += n;
            return array;
        }
        throw InvalidProtocolBufferNanoException.truncatedMessage();
    }
    
    public int readRawLittleEndian32() throws IOException {
        return (this.readRawByte() & 0xFF) << 24 | ((this.readRawByte() & 0xFF) | (this.readRawByte() & 0xFF) << 8 | (this.readRawByte() & 0xFF) << 16);
    }
    
    public long readRawLittleEndian64() throws IOException {
        return ((long)this.readRawByte() & 0xFFL) << 8 | ((long)this.readRawByte() & 0xFFL) | ((long)this.readRawByte() & 0xFFL) << 16 | ((long)this.readRawByte() & 0xFFL) << 24 | ((long)this.readRawByte() & 0xFFL) << 32 | ((long)this.readRawByte() & 0xFFL) << 40 | ((long)this.readRawByte() & 0xFFL) << 48 | ((long)this.readRawByte() & 0xFFL) << 56;
    }
    
    public int readRawVarint32() throws IOException {
        final byte rawByte = this.readRawByte();
        if (rawByte >= 0) {
            return rawByte;
        }
        int n = rawByte & 0x7F;
        final byte rawByte2 = this.readRawByte();
        int n2;
        if (rawByte2 >= 0) {
            n2 = rawByte2 << 7;
        }
        else {
            n |= (rawByte2 & 0x7F) << 7;
            final byte rawByte3 = this.readRawByte();
            if (rawByte3 >= 0) {
                n2 = rawByte3 << 14;
            }
            else {
                n |= (rawByte3 & 0x7F) << 14;
                final byte rawByte4 = this.readRawByte();
                if (rawByte4 >= 0) {
                    n2 = rawByte4 << 21;
                }
                else {
                    final byte rawByte5 = this.readRawByte();
                    final int n3 = n | (rawByte4 & 0x7F) << 21 | rawByte5 << 28;
                    if (rawByte5 < 0) {
                        for (int i = 0; i < 5; ++i) {
                            if (this.readRawByte() >= 0) {
                                return n3;
                            }
                        }
                        throw InvalidProtocolBufferNanoException.malformedVarint();
                    }
                    return n3;
                }
            }
        }
        return n2 | n;
    }
    
    public long readRawVarint64() throws IOException {
        int i = 0;
        long n = 0L;
        while (i < 64) {
            final byte rawByte = this.readRawByte();
            n |= (long)(rawByte & 0x7F) << i;
            if ((rawByte & 0x80) == 0x0) {
                return n;
            }
            i += 7;
        }
        throw InvalidProtocolBufferNanoException.malformedVarint();
    }
    
    public String readString() throws IOException {
        final int rawVarint32 = this.readRawVarint32();
        if (rawVarint32 <= this.bufferSize - this.bufferPos && rawVarint32 > 0) {
            final String s = new String(this.buffer, this.bufferPos, rawVarint32, InternalNano.UTF_8);
            this.bufferPos += rawVarint32;
            return s;
        }
        return new String(this.readRawBytes(rawVarint32), InternalNano.UTF_8);
    }
    
    public int readTag() throws IOException {
        if (this.isAtEnd()) {
            return this.lastTag = 0;
        }
        final int rawVarint32 = this.readRawVarint32();
        if ((this.lastTag = rawVarint32) != 0) {
            return rawVarint32;
        }
        throw InvalidProtocolBufferNanoException.invalidTag();
    }
    
    public int readUInt32() throws IOException {
        return this.readRawVarint32();
    }
    
    public long readUInt64() throws IOException {
        return this.readRawVarint64();
    }
    
    public void rewindToPosition(final int n) {
        final int bufferPos = this.bufferPos;
        final int bufferStart = this.bufferStart;
        if (n > bufferPos - bufferStart) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Position ");
            sb.append(n);
            sb.append(" is beyond current ");
            sb.append(this.bufferPos - this.bufferStart);
            throw new IllegalArgumentException(sb.toString());
        }
        if (n >= 0) {
            this.bufferPos = bufferStart + n;
            return;
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("Bad position ");
        sb2.append(n);
        throw new IllegalArgumentException(sb2.toString());
    }
    
    public boolean skipField(final int n) throws IOException {
        final int tagWireType = WireFormatNano.getTagWireType(n);
        if (tagWireType == 0) {
            this.readInt32();
            return true;
        }
        if (tagWireType == 1) {
            this.readRawLittleEndian64();
            return true;
        }
        if (tagWireType == 2) {
            this.skipRawBytes(this.readRawVarint32());
            return true;
        }
        if (tagWireType == 3) {
            this.skipMessage();
            this.checkLastTagWas(WireFormatNano.makeTag(WireFormatNano.getTagFieldNumber(n), 4));
            return true;
        }
        if (tagWireType == 4) {
            return false;
        }
        if (tagWireType == 5) {
            this.readRawLittleEndian32();
            return true;
        }
        throw InvalidProtocolBufferNanoException.invalidWireType();
    }
    
    public void skipMessage() throws IOException {
        int tag;
        do {
            tag = this.readTag();
        } while (tag != 0 && this.skipField(tag));
    }
    
    public void skipRawBytes(final int n) throws IOException {
        if (n < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        }
        final int bufferPos = this.bufferPos;
        final int currentLimit = this.currentLimit;
        if (bufferPos + n > currentLimit) {
            this.skipRawBytes(currentLimit - bufferPos);
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
        if (n <= this.bufferSize - bufferPos) {
            this.bufferPos = bufferPos + n;
            return;
        }
        throw InvalidProtocolBufferNanoException.truncatedMessage();
    }
}
