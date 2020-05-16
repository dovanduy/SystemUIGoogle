// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.coroutines.intrinsics;

import kotlin.coroutines.CoroutineContext;
import kotlin.TypeCastException;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.coroutines.jvm.internal.BaseContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;

class IntrinsicsKt__IntrinsicsJvmKt
{
    public static <R, T> Continuation<Unit> createCoroutineUnintercepted(final Function2<? super R, ? super Continuation<? super T>, ?> function2, final R r, final Continuation<? super T> continuation) {
        Intrinsics.checkParameterIsNotNull(function2, "$this$createCoroutineUnintercepted");
        Intrinsics.checkParameterIsNotNull(continuation, "completion");
        DebugProbesKt.probeCoroutineCreated((Continuation<? super Object>)continuation);
        Object create;
        if (function2 instanceof BaseContinuationImpl) {
            create = ((BaseContinuationImpl)function2).create(r, continuation);
        }
        else {
            final CoroutineContext context = continuation.getContext();
            if (context == EmptyCoroutineContext.INSTANCE) {
                if (continuation == null) {
                    throw new TypeCastException("null cannot be cast to non-null type kotlin.coroutines.Continuation<kotlin.Any?>");
                }
                create = new IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt.IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$3((Continuation)continuation, (Continuation)continuation, (Function2)function2, (Object)r);
            }
            else {
                if (continuation == null) {
                    throw new TypeCastException("null cannot be cast to non-null type kotlin.coroutines.Continuation<kotlin.Any?>");
                }
                create = new IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt.IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$4((Continuation)continuation, context, (Continuation)continuation, context, (Function2)function2, (Object)r);
            }
        }
        return (Continuation<Unit>)create;
    }
}
