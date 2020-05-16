// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.coroutines.jvm.internal;

import kotlin.ResultKt;
import kotlin.Result;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.Unit;
import java.io.Serializable;
import kotlin.coroutines.Continuation;

public abstract class BaseContinuationImpl implements Continuation<Object>, Object, Serializable
{
    private final Continuation<Object> completion;
    
    public BaseContinuationImpl(final Continuation<Object> completion) {
        this.completion = completion;
    }
    
    public Continuation<Unit> create(final Object o, final Continuation<?> continuation) {
        Intrinsics.checkParameterIsNotNull(continuation, "completion");
        throw new UnsupportedOperationException("create(Any?;Continuation) has not been overridden");
    }
    
    public final Continuation<Object> getCompletion() {
        return this.completion;
    }
    
    public StackTraceElement getStackTraceElement() {
        return DebugMetadataKt.getStackTraceElement(this);
    }
    
    protected abstract Object invokeSuspend(final Object p0);
    
    protected void releaseIntercepted() {
    }
    
    @Override
    public final void resumeWith(Object o) {
        BaseContinuationImpl baseContinuationImpl = this;
        while (true) {
            DebugProbesKt.probeCoroutineResumed(baseContinuationImpl);
            final Continuation<Object> completion = baseContinuationImpl.completion;
            if (completion == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            try {
                o = baseContinuationImpl.invokeSuspend(o);
                if (o == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                    return;
                }
                final Result.Companion companion = Result.Companion;
                Result.constructor-impl(o);
            }
            finally {
                final Result.Companion companion2 = Result.Companion;
                final Throwable t;
                o = ResultKt.createFailure(t);
                Result.constructor-impl(o);
            }
            baseContinuationImpl.releaseIntercepted();
            if (!(completion instanceof BaseContinuationImpl)) {
                completion.resumeWith(o);
                return;
            }
            baseContinuationImpl = (BaseContinuationImpl)completion;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Continuation at ");
        Serializable obj = this.getStackTraceElement();
        if (obj == null) {
            obj = this.getClass().getName();
        }
        sb.append(obj);
        return sb.toString();
    }
}
