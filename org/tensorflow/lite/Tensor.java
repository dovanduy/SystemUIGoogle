// 
// Decompiled by Procyon v0.5.36
// 

package org.tensorflow.lite;

import java.util.Arrays;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.FloatBuffer;
import java.nio.Buffer;
import java.lang.reflect.Array;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

public final class Tensor
{
    private final DataType dtype;
    private long nativeHandle;
    private int[] shapeCopy;
    
    private Tensor(final long nativeHandle) {
        this.nativeHandle = nativeHandle;
        this.dtype = DataType.fromC(dtype(nativeHandle));
        this.shapeCopy = shape(nativeHandle);
    }
    
    private ByteBuffer buffer() {
        return buffer(this.nativeHandle).order(ByteOrder.nativeOrder());
    }
    
    private static native ByteBuffer buffer(final long p0);
    
    static int computeNumDimensions(final Object o) {
        if (o == null || !o.getClass().isArray()) {
            return 0;
        }
        if (Array.getLength(o) != 0) {
            return computeNumDimensions(Array.get(o, 0)) + 1;
        }
        throw new IllegalArgumentException("Array lengths cannot be 0.");
    }
    
    static int[] computeShapeOf(final Object o) {
        final int[] array = new int[computeNumDimensions(o)];
        fillShape(o, 0, array);
        return array;
    }
    
    private void copyTo(final Buffer obj) {
        if (obj instanceof ByteBuffer) {
            ((ByteBuffer)obj).put(this.buffer());
        }
        else if (obj instanceof FloatBuffer) {
            ((FloatBuffer)obj).put(this.buffer().asFloatBuffer());
        }
        else if (obj instanceof LongBuffer) {
            ((LongBuffer)obj).put(this.buffer().asLongBuffer());
        }
        else {
            if (!(obj instanceof IntBuffer)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unexpected output buffer type: ");
                sb.append(obj);
                throw new IllegalArgumentException(sb.toString());
            }
            ((IntBuffer)obj).put(this.buffer().asIntBuffer());
        }
    }
    
    private static native long create(final long p0, final int p1);
    
    static DataType dataTypeOf(final Object o) {
        if (o != null) {
            Class<?> obj;
            for (obj = o.getClass(); obj.isArray(); obj = obj.getComponentType()) {}
            if (Float.TYPE.equals(obj) || o instanceof FloatBuffer) {
                return DataType.FLOAT32;
            }
            if (Integer.TYPE.equals(obj) || o instanceof IntBuffer) {
                return DataType.INT32;
            }
            if (Byte.TYPE.equals(obj)) {
                return DataType.UINT8;
            }
            if (Long.TYPE.equals(obj) || o instanceof LongBuffer) {
                return DataType.INT64;
            }
            if (String.class.equals(obj)) {
                return DataType.STRING;
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("DataType error: cannot resolve DataType of ");
        sb.append(o.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
    }
    
    private static native void delete(final long p0);
    
    private static native int dtype(final long p0);
    
    static void fillShape(final Object o, final int i, final int[] array) {
        if (array != null && i != array.length) {
            final int length = Array.getLength(o);
            final int n = array[i];
            int j = 0;
            if (n == 0) {
                array[i] = length;
            }
            else if (array[i] != length) {
                throw new IllegalArgumentException(String.format("Mismatched lengths (%d and %d) in dimension %d", array[i], length, i));
            }
            while (j < length) {
                fillShape(Array.get(o, j), i + 1, array);
                ++j;
            }
        }
    }
    
    static Tensor fromIndex(final long n, final int n2) {
        return new Tensor(create(n, n2));
    }
    
    private static native boolean hasDelegateBufferHandle(final long p0);
    
    private static boolean isBuffer(final Object o) {
        return o instanceof Buffer;
    }
    
    private static boolean isByteBuffer(final Object o) {
        return o instanceof ByteBuffer;
    }
    
    private static native int numBytes(final long p0);
    
    private static native void readMultiDimensionalArray(final long p0, final Object p1);
    
    private void setTo(final Buffer obj) {
        if (obj instanceof ByteBuffer) {
            final ByteBuffer src = (ByteBuffer)obj;
            if (src.isDirect() && src.order() == ByteOrder.nativeOrder()) {
                writeDirectBuffer(this.nativeHandle, obj);
            }
            else {
                this.buffer().put(src);
            }
        }
        else if (obj instanceof LongBuffer) {
            final LongBuffer src2 = (LongBuffer)obj;
            if (src2.isDirect() && src2.order() == ByteOrder.nativeOrder()) {
                writeDirectBuffer(this.nativeHandle, obj);
            }
            else {
                this.buffer().asLongBuffer().put(src2);
            }
        }
        else if (obj instanceof FloatBuffer) {
            final FloatBuffer src3 = (FloatBuffer)obj;
            if (src3.isDirect() && src3.order() == ByteOrder.nativeOrder()) {
                writeDirectBuffer(this.nativeHandle, obj);
            }
            else {
                this.buffer().asFloatBuffer().put(src3);
            }
        }
        else {
            if (!(obj instanceof IntBuffer)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unexpected input buffer type: ");
                sb.append(obj);
                throw new IllegalArgumentException(sb.toString());
            }
            final IntBuffer src4 = (IntBuffer)obj;
            if (src4.isDirect() && src4.order() == ByteOrder.nativeOrder()) {
                writeDirectBuffer(this.nativeHandle, obj);
            }
            else {
                this.buffer().asIntBuffer().put(src4);
            }
        }
    }
    
    private static native int[] shape(final long p0);
    
    private void throwIfDataIsIncompatible(final Object o) {
        this.throwIfTypeIsIncompatible(o);
        this.throwIfShapeIsIncompatible(o);
    }
    
    private void throwIfShapeIsIncompatible(final Object o) {
        if (isBuffer(o)) {
            final Buffer buffer = (Buffer)o;
            final int numBytes = this.numBytes();
            int capacity;
            if (isByteBuffer(o)) {
                capacity = buffer.capacity();
            }
            else {
                capacity = this.dtype.byteSize() * buffer.capacity();
            }
            if (numBytes == capacity) {
                return;
            }
            throw new IllegalArgumentException(String.format("Cannot convert between a TensorFlowLite buffer with %d bytes and a Java Buffer with %d bytes.", numBytes, capacity));
        }
        else {
            final int[] computeShape = computeShapeOf(o);
            if (Arrays.equals(computeShape, this.shapeCopy)) {
                return;
            }
            throw new IllegalArgumentException(String.format("Cannot copy between a TensorFlowLite tensor with shape %s and a Java object with shape %s.", Arrays.toString(this.shapeCopy), Arrays.toString(computeShape)));
        }
    }
    
    private void throwIfTypeIsIncompatible(final Object o) {
        if (isByteBuffer(o)) {
            return;
        }
        final DataType dataType = dataTypeOf(o);
        if (dataType == this.dtype) {
            return;
        }
        throw new IllegalArgumentException(String.format("Cannot convert between a TensorFlowLite tensor with type %s and a Java object of type %s (which is compatible with the TensorFlowLite type %s).", this.dtype, o.getClass().getName(), dataType));
    }
    
    private static native void writeDirectBuffer(final long p0, final Buffer p1);
    
    private static native void writeMultiDimensionalArray(final long p0, final Object p1);
    
    void close() {
        delete(this.nativeHandle);
        this.nativeHandle = 0L;
    }
    
    Object copyTo(final Object o) {
        if (o != null) {
            this.throwIfDataIsIncompatible(o);
            if (isBuffer(o)) {
                this.copyTo((Buffer)o);
            }
            else {
                readMultiDimensionalArray(this.nativeHandle, o);
            }
            return o;
        }
        if (hasDelegateBufferHandle(this.nativeHandle)) {
            return o;
        }
        throw new IllegalArgumentException("Null outputs are allowed only if the Tensor is bound to a buffer handle.");
    }
    
    int[] getInputShapeIfDifferent(final Object o) {
        if (o == null) {
            return null;
        }
        if (isBuffer(o)) {
            return null;
        }
        this.throwIfTypeIsIncompatible(o);
        final int[] computeShape = computeShapeOf(o);
        if (Arrays.equals(this.shapeCopy, computeShape)) {
            return null;
        }
        return computeShape;
    }
    
    public int numBytes() {
        return numBytes(this.nativeHandle);
    }
    
    void refreshShape() {
        this.shapeCopy = shape(this.nativeHandle);
    }
    
    void setTo(final Object o) {
        if (o != null) {
            this.throwIfDataIsIncompatible(o);
            if (isBuffer(o)) {
                this.setTo((Buffer)o);
            }
            else {
                writeMultiDimensionalArray(this.nativeHandle, o);
            }
            return;
        }
        if (hasDelegateBufferHandle(this.nativeHandle)) {
            return;
        }
        throw new IllegalArgumentException("Null inputs are allowed only if the Tensor is bound to a buffer handle.");
    }
}
