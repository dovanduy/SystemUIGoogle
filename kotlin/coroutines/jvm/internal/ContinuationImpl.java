// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.coroutines.jvm.internal;

import kotlin.coroutines.ContinuationInterceptor;
import kotlin.jvm.internal.Intrinsics;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;

public abstract class ContinuationImpl extends BaseContinuationImpl
{
    private final CoroutineContext _context;
    private transient Continuation<Object> intercepted;
    
    public ContinuationImpl(final Continuation<Object> continuation, final CoroutineContext context) {
        super(continuation);
        this._context = context;
    }
    
    @Override
    public CoroutineContext getContext() {
        final CoroutineContext context = this._context;
        if (context != null) {
            return context;
        }
        Intrinsics.throwNpe();
        throw null;
    }
    
    @Override
    protected void releaseIntercepted() {
        final Continuation<Object> intercepted = this.intercepted;
        if (intercepted != null && intercepted != this) {
            final ContinuationInterceptor value = this.getContext().get((CoroutineContext.Key<ContinuationInterceptor>)ContinuationInterceptor.Key);
            if (value == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            value.releaseInterceptedContinuation(intercepted);
        }
        this.intercepted = CompletedContinuation.INSTANCE;
    }
}
