// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.coroutines.jvm.internal;

import kotlin.jvm.internal.Intrinsics;
import kotlin.coroutines.Continuation;

public final class DebugProbesKt
{
    public static final <T> Continuation<T> probeCoroutineCreated(final Continuation<? super T> continuation) {
        Intrinsics.checkParameterIsNotNull(continuation, "completion");
        return (Continuation<T>)continuation;
    }
    
    public static final void probeCoroutineResumed(final Continuation<?> continuation) {
        Intrinsics.checkParameterIsNotNull(continuation, "frame");
    }
    
    public static final void probeCoroutineSuspended(final Continuation<?> continuation) {
        Intrinsics.checkParameterIsNotNull(continuation, "frame");
    }
}
