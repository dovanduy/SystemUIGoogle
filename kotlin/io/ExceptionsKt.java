// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.io;

import kotlin.jvm.internal.Intrinsics;
import java.io.File;

public final class ExceptionsKt
{
    private static final String constructMessage(final File file, final File obj, final String str) {
        final StringBuilder sb = new StringBuilder(file.toString());
        if (obj != null) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(" -> ");
            sb2.append(obj);
            sb.append(sb2.toString());
        }
        if (str != null) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append(": ");
            sb3.append(str);
            sb.append(sb3.toString());
        }
        final String string = sb.toString();
        Intrinsics.checkExpressionValueIsNotNull(string, "sb.toString()");
        return string;
    }
}
