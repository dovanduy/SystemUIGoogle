// 
// Decompiled by Procyon v0.5.36
// 

package org.tensorflow.lite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.support.annotation.NonNull;
import java.nio.ByteBuffer;

public final class Interpreter implements AutoCloseable
{
    NativeInterpreterWrapper wrapper;
    
    public Interpreter(@NonNull final ByteBuffer byteBuffer) {
        this(byteBuffer, null);
    }
    
    public Interpreter(@NonNull final ByteBuffer byteBuffer, final Options options) {
        this.wrapper = new NativeInterpreterWrapper(byteBuffer, options);
    }
    
    private void checkNotClosed() {
        if (this.wrapper != null) {
            return;
        }
        throw new IllegalStateException("Internal error: The Interpreter has already been closed.");
    }
    
    @Override
    public void close() {
        final NativeInterpreterWrapper wrapper = this.wrapper;
        if (wrapper != null) {
            wrapper.close();
            this.wrapper = null;
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            this.close();
        }
        finally {
            super.finalize();
        }
    }
    
    public void runForMultipleInputsOutputs(@NonNull final Object[] array, @NonNull final Map<Integer, Object> map) {
        this.checkNotClosed();
        this.wrapper.run(array, map);
    }
    
    public static class Options
    {
        Boolean allowBufferHandleOutput;
        Boolean allowFp16PrecisionForFp32;
        final List<Delegate> delegates;
        int numThreads;
        Boolean useNNAPI;
        
        public Options() {
            this.numThreads = -1;
            this.delegates = new ArrayList<Delegate>();
        }
    }
}
