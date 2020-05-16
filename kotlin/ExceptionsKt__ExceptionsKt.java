// 
// Decompiled by Procyon v0.5.36
// 

package kotlin;

import kotlin.internal.PlatformImplementationsKt;
import kotlin.jvm.internal.Intrinsics;

class ExceptionsKt__ExceptionsKt
{
    public static void addSuppressed(final Throwable t, final Throwable t2) {
        Intrinsics.checkParameterIsNotNull(t, "$this$addSuppressed");
        Intrinsics.checkParameterIsNotNull(t2, "exception");
        PlatformImplementationsKt.IMPLEMENTATIONS.addSuppressed(t, t2);
    }
}
