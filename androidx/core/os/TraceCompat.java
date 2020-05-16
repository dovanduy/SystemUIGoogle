// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.os;

import android.util.Log;
import android.os.Trace;
import android.os.Build$VERSION;

public final class TraceCompat
{
    static {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 18 && sdk_INT < 29) {
            try {
                Trace.class.getField("TRACE_TAG_APP").getLong(null);
                Trace.class.getMethod("isTagEnabled", Long.TYPE);
                Trace.class.getMethod("asyncTraceBegin", Long.TYPE, String.class, Integer.TYPE);
                Trace.class.getMethod("asyncTraceEnd", Long.TYPE, String.class, Integer.TYPE);
                Trace.class.getMethod("traceCounter", Long.TYPE, String.class, Integer.TYPE);
            }
            catch (Exception ex) {
                Log.i("TraceCompat", "Unable to initialize via reflection.", (Throwable)ex);
            }
        }
    }
    
    public static void beginSection(final String s) {
        if (Build$VERSION.SDK_INT >= 18) {
            Trace.beginSection(s);
        }
    }
    
    public static void endSection() {
        if (Build$VERSION.SDK_INT >= 18) {
            Trace.endSection();
        }
    }
}
