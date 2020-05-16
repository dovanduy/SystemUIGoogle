// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf.nano;

import java.io.IOException;
import java.nio.ReadOnlyBufferException;
import java.nio.BufferOverflowException;
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
    
    public static int computeBoolSize(final int n, final boolean b) {
        return computeTagSize(n) + computeBoolSizeNoTag(b);
    }
    
    public static int computeBoolSizeNoTag(final boolean b) {
        return 1;
    }
    
    public static int computeBytesSize(final int n, final byte[] array) {
        return computeTagSize(n) + computeBytesSizeNoTag(array);
    }
    
    public static int computeBytesSizeNoTag(final byte[] array) {
        return computeRawVarint32Size(array.length) + array.length;
    }
    
    public static int computeEnumSize(final int n, final int n2) {
        return computeTagSize(n) + computeEnumSizeNoTag(n2);
    }
    
    public static int computeEnumSizeNoTag(final int n) {
        return computeRawVarint32Size(n);
    }
    
    public static int computeFixed64Size(final int n, final long n2) {
        return computeTagSize(n) + computeFixed64SizeNoTag(n2);
    }
    
    public static int computeFixed64SizeNoTag(final long n) {
        return 8;
    }
    
    public static int computeFloatSize(final int n, final float n2) {
        return computeTagSize(n) + computeFloatSizeNoTag(n2);
    }
    
    public static int computeFloatSizeNoTag(final float n) {
        return 4;
    }
    
    public static int computeInt32Size(final int n, final int n2) {
        return computeTagSize(n) + computeInt32SizeNoTag(n2);
    }
    
    public static int computeInt32SizeNoTag(final int n) {
        if (n >= 0) {
            return computeRawVarint32Size(n);
        }
        return 10;
    }
    
    public static int computeInt64Size(final int n, final long n2) {
        return computeTagSize(n) + computeInt64SizeNoTag(n2);
    }
    
    public static int computeInt64SizeNoTag(final long n) {
        return computeRawVarint64Size(n);
    }
    
    public static int computeMessageSize(final int n, final MessageNano messageNano) {
        return computeTagSize(n) + computeMessageSizeNoTag(messageNano);
    }
    
    public static int computeMessageSizeNoTag(final MessageNano messageNano) {
        final int serializedSize = messageNano.getSerializedSize();
        return computeRawVarint32Size(serializedSize) + serializedSize;
    }
    
    public static int computeRawVarint32Size(final int n) {
        if ((n & 0xFFFFFF80) == 0x0) {
            return 1;
        }
        if ((n & 0xFFFFC000) == 0x0) {
            return 2;
        }
        if ((0xFFE00000 & n) == 0x0) {
            return 3;
        }
        if ((n & 0xF0000000) == 0x0) {
            return 4;
        }
        return 5;
    }
    
    public static int computeRawVarint64Size(final long n) {
        if ((0xFFFFFFFFFFFFFF80L & n) == 0x0L) {
            return 1;
        }
        if ((0xFFFFFFFFFFFFC000L & n) == 0x0L) {
            return 2;
        }
        if ((0xFFFFFFFFFFE00000L & n) == 0x0L) {
            return 3;
        }
        if ((0xFFFFFFFFF0000000L & n) == 0x0L) {
            return 4;
        }
        if ((0xFFFFFFF800000000L & n) == 0x0L) {
            return 5;
        }
        if ((0xFFFFFC0000000000L & n) == 0x0L) {
            return 6;
        }
        if ((0xFFFE000000000000L & n) == 0x0L) {
            return 7;
        }
        if ((0xFF00000000000000L & n) == 0x0L) {
            return 8;
        }
        if ((n & Long.MIN_VALUE) == 0x0L) {
            return 9;
        }
        return 10;
    }
    
    public static int computeStringSize(final int n, final String s) {
        return computeTagSize(n) + computeStringSizeNoTag(s);
    }
    
    public static int computeStringSizeNoTag(final String s) {
        final int encodedLength = encodedLength(s);
        return computeRawVarint32Size(encodedLength) + encodedLength;
    }
    
    public static int computeTagSize(final int n) {
        return computeRawVarint32Size(WireFormatNano.makeTag(n, 0));
    }
    
    public static int computeUInt32Size(final int n, final int n2) {
        return computeTagSize(n) + computeUInt32SizeNoTag(n2);
    }
    
    public static int computeUInt32SizeNoTag(final int n) {
        return computeRawVarint32Size(n);
    }
    
    public static int computeUInt64Size(final int n, final long n2) {
        return computeTagSize(n) + computeUInt64SizeNoTag(n2);
    }
    
    public static int computeUInt64SizeNoTag(final long n) {
        return computeRawVarint64Size(n);
    }
    
    private static int encode(final CharSequence charSequence, final byte[] array, int i, int j) {
        final int length = charSequence.length();
        final int n = j + i;
        int n2;
        char char1;
        for (j = 0; j < length; ++j) {
            n2 = j + i;
            if (n2 >= n) {
                break;
            }
            char1 = charSequence.charAt(j);
            if (char1 >= '\u0080') {
                break;
            }
            array[n2] = (byte)char1;
        }
        if (j == length) {
            return i + length;
        }
        int k = i + j;
        char char2;
        int n3;
        int n4;
        char char3;
        int n5;
        int n6;
        int n7;
        int n8;
        StringBuilder sb;
        StringBuilder sb2;
        for (i = j; i < length; ++i, k = j) {
            char2 = charSequence.charAt(i);
            if (char2 < '\u0080' && k < n) {
                j = k + 1;
                array[k] = (byte)char2;
            }
            else if (char2 < '\u0800' && k <= n - 2) {
                n3 = k + 1;
                array[k] = (byte)(char2 >>> 6 | 0x3C0);
                j = n3 + 1;
                array[n3] = (byte)((char2 & '?') | 0x80);
            }
            else if ((char2 < '\ud800' || '\udfff' < char2) && k <= n - 3) {
                j = k + 1;
                array[k] = (byte)(char2 >>> 12 | 0x1E0);
                n4 = j + 1;
                array[j] = (byte)((char2 >>> 6 & 0x3F) | 0x80);
                j = n4 + 1;
                array[n4] = (byte)((char2 & '?') | 0x80);
            }
            else {
                if (k <= n - 4) {
                    j = i + 1;
                    if (j != charSequence.length()) {
                        char3 = charSequence.charAt(j);
                        if (Character.isSurrogatePair(char2, char3)) {
                            i = Character.toCodePoint(char2, char3);
                            n5 = k + 1;
                            array[k] = (byte)(i >>> 18 | 0xF0);
                            n6 = n5 + 1;
                            array[n5] = (byte)((i >>> 12 & 0x3F) | 0x80);
                            n7 = n6 + 1;
                            array[n6] = (byte)((i >>> 6 & 0x3F) | 0x80);
                            n8 = n7 + 1;
                            array[n7] = (byte)((i & 0x3F) | 0x80);
                            i = j;
                            j = n8;
                            continue;
                        }
                        i = j;
                    }
                    sb = new StringBuilder();
                    sb.append("Unpaired surrogate at index ");
                    sb.append(i - 1);
                    throw new IllegalArgumentException(sb.toString());
                }
                sb2 = new StringBuilder();
                sb2.append("Failed writing ");
                sb2.append(char2);
                sb2.append(" at index ");
                sb2.append(k);
                throw new ArrayIndexOutOfBoundsException(sb2.toString());
            }
        }
        return k;
    }
    
    private static void encode(final CharSequence charSequence, final ByteBuffer byteBuffer) {
        if (!byteBuffer.isReadOnly()) {
            if (byteBuffer.hasArray()) {
                try {
                    byteBuffer.position(encode(charSequence, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining()) - byteBuffer.arrayOffset());
                    return;
                }
                catch (ArrayIndexOutOfBoundsException cause) {
                    final BufferOverflowException ex = new BufferOverflowException();
                    ex.initCause(cause);
                    throw ex;
                }
            }
            encodeDirect(charSequence, byteBuffer);
            return;
        }
        throw new ReadOnlyBufferException();
    }
    
    private static void encodeDirect(final CharSequence charSequence, final ByteBuffer byteBuffer) {
        for (int length = charSequence.length(), i = 0; i < length; ++i) {
            final char char1 = charSequence.charAt(i);
            if (char1 < '\u0080') {
                byteBuffer.put((byte)char1);
            }
            else if (char1 < '\u0800') {
                byteBuffer.put((byte)(char1 >>> 6 | 0x3C0));
                byteBuffer.put((byte)((char1 & '?') | 0x80));
            }
            else {
                if (char1 >= '\ud800' && '\udfff' >= char1) {
                    final int n = i + 1;
                    if (n != charSequence.length()) {
                        final char char2 = charSequence.charAt(n);
                        if (Character.isSurrogatePair(char1, char2)) {
                            final int codePoint = Character.toCodePoint(char1, char2);
                            byteBuffer.put((byte)(codePoint >>> 18 | 0xF0));
                            byteBuffer.put((byte)((codePoint >>> 12 & 0x3F) | 0x80));
                            byteBuffer.put((byte)((codePoint >>> 6 & 0x3F) | 0x80));
                            byteBuffer.put((byte)((codePoint & 0x3F) | 0x80));
                            i = n;
                            continue;
                        }
                        i = n;
                    }
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Unpaired surrogate at index ");
                    sb.append(i - 1);
                    throw new IllegalArgumentException(sb.toString());
                }
                byteBuffer.put((byte)(char1 >>> 12 | 0x1E0));
                byteBuffer.put((byte)((char1 >>> 6 & 0x3F) | 0x80));
                byteBuffer.put((byte)((char1 & '?') | 0x80));
            }
        }
    }
    
    private static int encodedLength(final CharSequence charSequence) {
        int length;
        int n;
        for (length = charSequence.length(), n = 0; n < length && charSequence.charAt(n) < '\u0080'; ++n) {}
        int n2 = length;
        int n3;
        while (true) {
            n3 = n2;
            if (n >= length) {
                break;
            }
            final char char1 = charSequence.charAt(n);
            if (char1 >= '\u0800') {
                n3 = n2 + encodedLengthGeneral(charSequence, n);
                break;
            }
            n2 += '\u007f' - char1 >>> 31;
            ++n;
        }
        if (n3 >= length) {
            return n3;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("UTF-8 length does not fit in int: ");
        sb.append(n3 + 4294967296L);
        throw new IllegalArgumentException(sb.toString());
    }
    
    private static int encodedLengthGeneral(final CharSequence seq, int i) {
        final int length = seq.length();
        int n = 0;
        while (i < length) {
            final char char1 = seq.charAt(i);
            int n2;
            if (char1 < '\u0800') {
                n += '\u007f' - char1 >>> 31;
                n2 = i;
            }
            else {
                final int n3 = n += 2;
                n2 = i;
                if ('\ud800' <= char1) {
                    n = n3;
                    n2 = i;
                    if (char1 <= '\udfff') {
                        if (Character.codePointAt(seq, i) < 65536) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("Unpaired surrogate at index ");
                            sb.append(i);
                            throw new IllegalArgumentException(sb.toString());
                        }
                        n2 = i + 1;
                        n = n3;
                    }
                }
            }
            i = n2 + 1;
        }
        return n;
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
    
    public void writeBool(final int n, final boolean b) throws IOException {
        this.writeTag(n, 0);
        this.writeBoolNoTag(b);
    }
    
    public void writeBoolNoTag(final boolean b) throws IOException {
        this.writeRawByte(b ? 1 : 0);
    }
    
    public void writeBytes(final int n, final byte[] array) throws IOException {
        this.writeTag(n, 2);
        this.writeBytesNoTag(array);
    }
    
    public void writeBytesNoTag(final byte[] array) throws IOException {
        this.writeRawVarint32(array.length);
        this.writeRawBytes(array);
    }
    
    public void writeEnum(final int n, final int n2) throws IOException {
        this.writeTag(n, 0);
        this.writeEnumNoTag(n2);
    }
    
    public void writeEnumNoTag(final int n) throws IOException {
        this.writeRawVarint32(n);
    }
    
    public void writeFixed64(final int n, final long n2) throws IOException {
        this.writeTag(n, 1);
        this.writeFixed64NoTag(n2);
    }
    
    public void writeFixed64NoTag(final long n) throws IOException {
        this.writeRawLittleEndian64(n);
    }
    
    public void writeFloat(final int n, final float n2) throws IOException {
        this.writeTag(n, 5);
        this.writeFloatNoTag(n2);
    }
    
    public void writeFloatNoTag(final float value) throws IOException {
        this.writeRawLittleEndian32(Float.floatToIntBits(value));
    }
    
    public void writeInt32(final int n, final int n2) throws IOException {
        this.writeTag(n, 0);
        this.writeInt32NoTag(n2);
    }
    
    public void writeInt32NoTag(final int n) throws IOException {
        if (n >= 0) {
            this.writeRawVarint32(n);
        }
        else {
            this.writeRawVarint64(n);
        }
    }
    
    public void writeInt64(final int n, final long n2) throws IOException {
        this.writeTag(n, 0);
        this.writeInt64NoTag(n2);
    }
    
    public void writeInt64NoTag(final long n) throws IOException {
        this.writeRawVarint64(n);
    }
    
    public void writeMessage(final int n, final MessageNano messageNano) throws IOException {
        this.writeTag(n, 2);
        this.writeMessageNoTag(messageNano);
    }
    
    public void writeMessageNoTag(final MessageNano messageNano) throws IOException {
        this.writeRawVarint32(messageNano.getCachedSize());
        messageNano.writeTo(this);
    }
    
    public void writeRawByte(final byte b) throws IOException {
        if (this.buffer.hasRemaining()) {
            this.buffer.put(b);
            return;
        }
        throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
    }
    
    public void writeRawByte(final int n) throws IOException {
        this.writeRawByte((byte)n);
    }
    
    public void writeRawBytes(final byte[] array) throws IOException {
        this.writeRawBytes(array, 0, array.length);
    }
    
    public void writeRawBytes(final byte[] src, final int offset, final int length) throws IOException {
        if (this.buffer.remaining() >= length) {
            this.buffer.put(src, offset, length);
            return;
        }
        throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
    }
    
    public void writeRawLittleEndian32(final int n) throws IOException {
        if (this.buffer.remaining() >= 4) {
            this.buffer.putInt(n);
            return;
        }
        throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
    }
    
    public void writeRawLittleEndian64(final long n) throws IOException {
        if (this.buffer.remaining() >= 8) {
            this.buffer.putLong(n);
            return;
        }
        throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
    }
    
    public void writeRawVarint32(int n) throws IOException {
        while ((n & 0xFFFFFF80) != 0x0) {
            this.writeRawByte((n & 0x7F) | 0x80);
            n >>>= 7;
        }
        this.writeRawByte(n);
    }
    
    public void writeRawVarint64(long n) throws IOException {
        while ((0xFFFFFFFFFFFFFF80L & n) != 0x0L) {
            this.writeRawByte(((int)n & 0x7F) | 0x80);
            n >>>= 7;
        }
        this.writeRawByte((int)n);
    }
    
    public void writeString(final int n, final String s) throws IOException {
        this.writeTag(n, 2);
        this.writeStringNoTag(s);
    }
    
    public void writeStringNoTag(final String s) throws IOException {
        try {
            final int computeRawVarint32Size = computeRawVarint32Size(s.length());
            if (computeRawVarint32Size == computeRawVarint32Size(s.length() * 3)) {
                final int position = this.buffer.position();
                if (this.buffer.remaining() < computeRawVarint32Size) {
                    throw new OutOfSpaceException(position + computeRawVarint32Size, this.buffer.limit());
                }
                this.buffer.position(position + computeRawVarint32Size);
                encode(s, this.buffer);
                final int position2 = this.buffer.position();
                this.buffer.position(position);
                this.writeRawVarint32(position2 - position - computeRawVarint32Size);
                this.buffer.position(position2);
            }
            else {
                this.writeRawVarint32(encodedLength(s));
                encode(s, this.buffer);
            }
        }
        catch (BufferOverflowException cause) {
            final OutOfSpaceException ex = new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
            ex.initCause(cause);
            throw ex;
        }
    }
    
    public void writeTag(final int n, final int n2) throws IOException {
        this.writeRawVarint32(WireFormatNano.makeTag(n, n2));
    }
    
    public void writeUInt32(final int n, final int n2) throws IOException {
        this.writeTag(n, 0);
        this.writeUInt32NoTag(n2);
    }
    
    public void writeUInt32NoTag(final int n) throws IOException {
        this.writeRawVarint32(n);
    }
    
    public void writeUInt64(final int n, final long n2) throws IOException {
        this.writeTag(n, 0);
        this.writeUInt64NoTag(n2);
    }
    
    public void writeUInt64NoTag(final long n) throws IOException {
        this.writeRawVarint64(n);
    }
    
    public static class OutOfSpaceException extends IOException
    {
        private static final long serialVersionUID = -6947486886997889499L;
        
        OutOfSpaceException(final int i, final int j) {
            final StringBuilder sb = new StringBuilder();
            sb.append("CodedOutputStream was writing to a flat byte array and ran out of space (pos ");
            sb.append(i);
            sb.append(" limit ");
            sb.append(j);
            sb.append(").");
            super(sb.toString());
        }
    }
}
