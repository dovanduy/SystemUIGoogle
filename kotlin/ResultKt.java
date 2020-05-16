// 
// Decompiled by Procyon v0.5.36
// 

package kotlin;

import kotlin.jvm.internal.Intrinsics;

public final class ResultKt
{
    public static final Object createFailure(final Throwable t) {
        Intrinsics.checkParameterIsNotNull(t, "exception");
        return new Result.Failure(t);
    }
    
    public static final void throwOnFailure(final Object o) {
        if (!(o instanceof Result.Failure)) {
            return;
        }
        throw ((Result.Failure)o).exception;
    }
}
