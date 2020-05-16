// 
// Decompiled by Procyon v0.5.36
// 

package org.tensorflow.lite;

public final class TensorFlowLite
{
    private static final Throwable LOAD_LIBRARY_EXCEPTION;
    private static volatile boolean isInit = false;
    
    static {
        Throwable load_LIBRARY_EXCEPTION = null;
        try {
            System.loadLibrary("tensorflowlite_jni");
            load_LIBRARY_EXCEPTION = null;
        }
        catch (UnsatisfiedLinkError unsatisfiedLinkError) {}
        LOAD_LIBRARY_EXCEPTION = load_LIBRARY_EXCEPTION;
    }
    
    public static void init() {
        if (TensorFlowLite.isInit) {
            return;
        }
        try {
            nativeRuntimeVersion();
            TensorFlowLite.isInit = true;
        }
        catch (UnsatisfiedLinkError obj) {
            final Throwable load_LIBRARY_EXCEPTION = TensorFlowLite.LOAD_LIBRARY_EXCEPTION;
            if (load_LIBRARY_EXCEPTION != null) {
                obj = load_LIBRARY_EXCEPTION;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("Failed to load native TensorFlow Lite methods. Check that the correct native libraries are present, and, if using a custom native library, have been properly loaded via System.loadLibrary():\n  ");
            sb.append(obj);
            throw new UnsatisfiedLinkError(sb.toString());
        }
    }
    
    public static native String nativeRuntimeVersion();
    
    public static String runtimeVersion() {
        init();
        return nativeRuntimeVersion();
    }
}
