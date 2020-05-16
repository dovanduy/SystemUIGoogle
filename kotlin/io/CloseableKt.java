// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.io;

import kotlin.ExceptionsKt;
import java.io.Closeable;

public final class CloseableKt
{
    public static final void closeFinally(final Closeable closeable, final Throwable t) {
        if (closeable != null) {
            if (t == null) {
                closeable.close();
            }
            else {
                try {
                    closeable.close();
                }
                finally {
                    final Throwable t2;
                    ExceptionsKt.addSuppressed(t, t2);
                }
            }
        }
    }
}
