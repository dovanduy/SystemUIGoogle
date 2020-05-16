// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.io;

import kotlin.jvm.internal.Intrinsics;
import java.io.OutputStream;
import java.io.InputStream;

public final class ByteStreamsKt
{
    public static final long copyTo(final InputStream inputStream, final OutputStream outputStream, int i) {
        Intrinsics.checkParameterIsNotNull(inputStream, "$this$copyTo");
        Intrinsics.checkParameterIsNotNull(outputStream, "out");
        final byte[] b = new byte[i];
        i = inputStream.read(b);
        long n = 0L;
        while (i >= 0) {
            outputStream.write(b, 0, i);
            n += i;
            i = inputStream.read(b);
        }
        return n;
    }
}
