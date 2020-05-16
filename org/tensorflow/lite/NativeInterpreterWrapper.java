// 
// Decompiled by Procyon v0.5.36
// 

package org.tensorflow.lite;

import java.util.Map;
import java.io.PrintStream;
import java.util.Iterator;
import org.tensorflow.lite.nnapi.NnApiDelegate;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.util.List;

final class NativeInterpreterWrapper implements AutoCloseable
{
    private final List<Delegate> delegates;
    private long errorHandle;
    private Tensor[] inputTensors;
    private long interpreterHandle;
    private boolean isMemoryAllocated;
    private ByteBuffer modelByteBuffer;
    private long modelHandle;
    private Tensor[] outputTensors;
    private final List<AutoCloseable> ownedDelegates;
    
    NativeInterpreterWrapper(final ByteBuffer modelByteBuffer, final Interpreter.Options options) {
        this.isMemoryAllocated = false;
        this.delegates = new ArrayList<Delegate>();
        this.ownedDelegates = new ArrayList<AutoCloseable>();
        TensorFlowLite.init();
        if (modelByteBuffer != null && (modelByteBuffer instanceof MappedByteBuffer || (modelByteBuffer.isDirect() && modelByteBuffer.order() == ByteOrder.nativeOrder()))) {
            this.modelByteBuffer = modelByteBuffer;
            final long errorReporter = createErrorReporter(512);
            this.init(errorReporter, createModelWithBuffer(this.modelByteBuffer, errorReporter), options);
            return;
        }
        throw new IllegalArgumentException("Model ByteBuffer should be either a MappedByteBuffer of the model file, or a direct ByteBuffer using ByteOrder.nativeOrder() which contains bytes of model content.");
    }
    
    private static native long allocateTensors(final long p0, final long p1);
    
    private static native void allowBufferHandleOutput(final long p0, final boolean p1);
    
    private static native void allowFp16PrecisionForFp32(final long p0, final boolean p1);
    
    private static native void applyDelegate(final long p0, final long p1, final long p2);
    
    private void applyDelegates(final Interpreter.Options options) {
        final boolean hasUnresolvedFlexOp = hasUnresolvedFlexOp(this.interpreterHandle);
        if (hasUnresolvedFlexOp) {
            final Delegate maybeCreateFlexDelegate = maybeCreateFlexDelegate(options.delegates);
            if (maybeCreateFlexDelegate != null) {
                this.ownedDelegates.add((AutoCloseable)maybeCreateFlexDelegate);
                applyDelegate(this.interpreterHandle, this.errorHandle, maybeCreateFlexDelegate.getNativeHandle());
            }
        }
        try {
            for (final Delegate delegate : options.delegates) {
                applyDelegate(this.interpreterHandle, this.errorHandle, delegate.getNativeHandle());
                this.delegates.add(delegate);
            }
            if (options.useNNAPI != null && options.useNNAPI) {
                final NnApiDelegate nnApiDelegate = new NnApiDelegate();
                this.ownedDelegates.add(nnApiDelegate);
                applyDelegate(this.interpreterHandle, this.errorHandle, nnApiDelegate.getNativeHandle());
            }
        }
        catch (IllegalArgumentException obj) {
            if (!hasUnresolvedFlexOp || hasUnresolvedFlexOp(this.interpreterHandle)) {
                throw obj;
            }
            final PrintStream err = System.err;
            final StringBuilder sb = new StringBuilder();
            sb.append("Ignoring failed delegate application: ");
            sb.append(obj);
            err.println(sb.toString());
        }
    }
    
    private static native long createErrorReporter(final int p0);
    
    private static native long createInterpreter(final long p0, final long p1, final int p2);
    
    private static native long createModelWithBuffer(final ByteBuffer p0, final long p1);
    
    private static native void delete(final long p0, final long p1, final long p2);
    
    private static native int getInputCount(final long p0);
    
    private static native int getInputTensorIndex(final long p0, final int p1);
    
    private static native int getOutputCount(final long p0);
    
    private static native int getOutputTensorIndex(final long p0, final int p1);
    
    private static native boolean hasUnresolvedFlexOp(final long p0);
    
    private void init(final long errorHandle, long interpreter, final Interpreter.Options options) {
        Interpreter.Options options2 = options;
        if (options == null) {
            options2 = new Interpreter.Options();
        }
        this.errorHandle = errorHandle;
        this.modelHandle = interpreter;
        interpreter = createInterpreter(interpreter, errorHandle, options2.numThreads);
        this.interpreterHandle = interpreter;
        this.inputTensors = new Tensor[getInputCount(interpreter)];
        this.outputTensors = new Tensor[getOutputCount(this.interpreterHandle)];
        final Boolean allowFp16PrecisionForFp32 = options2.allowFp16PrecisionForFp32;
        if (allowFp16PrecisionForFp32 != null) {
            allowFp16PrecisionForFp32(this.interpreterHandle, allowFp16PrecisionForFp32);
        }
        final Boolean allowBufferHandleOutput = options2.allowBufferHandleOutput;
        if (allowBufferHandleOutput != null) {
            allowBufferHandleOutput(this.interpreterHandle, allowBufferHandleOutput);
        }
        this.applyDelegates(options2);
        allocateTensors(this.interpreterHandle, errorHandle);
        this.isMemoryAllocated = true;
    }
    
    private static Delegate maybeCreateFlexDelegate(final List<Delegate> list) {
        try {
            final Class<?> forName = Class.forName("org.tensorflow.lite.flex.FlexDelegate");
            final Iterator<Delegate> iterator = list.iterator();
            while (iterator.hasNext()) {
                if (forName.isInstance(iterator.next())) {
                    return null;
                }
            }
            return (Delegate)forName.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    private static native boolean resizeInput(final long p0, final long p1, final int p2, final int[] p3);
    
    private static native void run(final long p0, final long p1);
    
    @Override
    public void close() {
        int n = 0;
        while (true) {
            final Tensor[] inputTensors = this.inputTensors;
            if (n >= inputTensors.length) {
                break;
            }
            if (inputTensors[n] != null) {
                inputTensors[n].close();
                this.inputTensors[n] = null;
            }
            ++n;
        }
        int n2 = 0;
        while (true) {
            final Tensor[] outputTensors = this.outputTensors;
            if (n2 >= outputTensors.length) {
                break;
            }
            if (outputTensors[n2] != null) {
                outputTensors[n2].close();
                this.outputTensors[n2] = null;
            }
            ++n2;
        }
        delete(this.errorHandle, this.modelHandle, this.interpreterHandle);
        this.errorHandle = 0L;
        this.modelHandle = 0L;
        this.interpreterHandle = 0L;
        this.modelByteBuffer = null;
        this.isMemoryAllocated = false;
        this.delegates.clear();
        for (final AutoCloseable autoCloseable : this.ownedDelegates) {
            try {
                autoCloseable.close();
            }
            catch (Exception obj) {
                final PrintStream err = System.err;
                final StringBuilder sb = new StringBuilder();
                sb.append("Failed to close flex delegate: ");
                sb.append(obj);
                err.println(sb.toString());
            }
        }
        this.ownedDelegates.clear();
    }
    
    Tensor getInputTensor(final int i) {
        if (i >= 0) {
            final Tensor[] inputTensors = this.inputTensors;
            if (i < inputTensors.length) {
                Tensor fromIndex;
                if ((fromIndex = inputTensors[i]) == null) {
                    final long interpreterHandle = this.interpreterHandle;
                    fromIndex = Tensor.fromIndex(interpreterHandle, getInputTensorIndex(interpreterHandle, i));
                    inputTensors[i] = fromIndex;
                }
                return fromIndex;
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid input Tensor index: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }
    
    Tensor getOutputTensor(final int i) {
        if (i >= 0) {
            final Tensor[] outputTensors = this.outputTensors;
            if (i < outputTensors.length) {
                Tensor fromIndex;
                if ((fromIndex = outputTensors[i]) == null) {
                    final long interpreterHandle = this.interpreterHandle;
                    fromIndex = Tensor.fromIndex(interpreterHandle, getOutputTensorIndex(interpreterHandle, i));
                    outputTensors[i] = fromIndex;
                }
                return fromIndex;
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid output Tensor index: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }
    
    void resizeInput(final int n, final int[] array) {
        if (resizeInput(this.interpreterHandle, this.errorHandle, n, array)) {
            this.isMemoryAllocated = false;
            final Tensor[] inputTensors = this.inputTensors;
            if (inputTensors[n] != null) {
                inputTensors[n].refreshShape();
            }
        }
    }
    
    void run(final Object[] array, final Map<Integer, Object> map) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Input error: Inputs should not be null or empty.");
        }
        if (map != null && !map.isEmpty()) {
            final int n = 0;
            for (int i = 0; i < array.length; ++i) {
                final int[] inputShapeIfDifferent = this.getInputTensor(i).getInputShapeIfDifferent(array[i]);
                if (inputShapeIfDifferent != null) {
                    this.resizeInput(i, inputShapeIfDifferent);
                }
            }
            final boolean b = this.isMemoryAllocated ^ true;
            if (b) {
                allocateTensors(this.interpreterHandle, this.errorHandle);
                this.isMemoryAllocated = true;
            }
            for (int j = 0; j < array.length; ++j) {
                this.getInputTensor(j).setTo(array[j]);
            }
            System.nanoTime();
            run(this.interpreterHandle, this.errorHandle);
            System.nanoTime();
            if (b) {
                int n2 = n;
                while (true) {
                    final Tensor[] outputTensors = this.outputTensors;
                    if (n2 >= outputTensors.length) {
                        break;
                    }
                    if (outputTensors[n2] != null) {
                        outputTensors[n2].refreshShape();
                    }
                    ++n2;
                }
            }
            for (final Map.Entry<Integer, Object> entry : map.entrySet()) {
                this.getOutputTensor(entry.getKey()).copyTo(entry.getValue());
            }
            return;
        }
        throw new IllegalArgumentException("Input error: Outputs should not be null or empty.");
    }
}
