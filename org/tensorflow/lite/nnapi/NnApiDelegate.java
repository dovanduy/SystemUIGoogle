// 
// Decompiled by Procyon v0.5.36
// 

package org.tensorflow.lite.nnapi;

import org.tensorflow.lite.TensorFlowLite;
import org.tensorflow.lite.Delegate;

public class NnApiDelegate implements Delegate, AutoCloseable
{
    private long delegateHandle;
    
    public NnApiDelegate() {
        this(new Options());
    }
    
    public NnApiDelegate(final Options options) {
        TensorFlowLite.init();
        this.delegateHandle = createDelegate(options.executionPreference, options.accelerator_name, options.cache_dir, options.model_token);
    }
    
    private static native long createDelegate(final int p0, final String p1, final String p2, final String p3);
    
    private static native void deleteDelegate(final long p0);
    
    @Override
    public void close() {
        final long delegateHandle = this.delegateHandle;
        if (delegateHandle != 0L) {
            deleteDelegate(delegateHandle);
            this.delegateHandle = 0L;
        }
    }
    
    @Override
    public long getNativeHandle() {
        return this.delegateHandle;
    }
    
    public static final class Options
    {
        String accelerator_name;
        String cache_dir;
        int executionPreference;
        String model_token;
        
        public Options() {
            this.executionPreference = -1;
            this.accelerator_name = null;
            this.cache_dir = null;
            this.model_token = null;
        }
    }
}
