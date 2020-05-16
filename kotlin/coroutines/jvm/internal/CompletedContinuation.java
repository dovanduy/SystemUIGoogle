// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.coroutines.jvm.internal;

import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.Continuation;

public final class CompletedContinuation implements Continuation<Object>
{
    public static final CompletedContinuation INSTANCE;
    
    static {
        INSTANCE = new CompletedContinuation();
    }
    
    private CompletedContinuation() {
    }
    
    @Override
    public CoroutineContext getContext() {
        throw new IllegalStateException("This continuation is already complete".toString());
    }
    
    @Override
    public void resumeWith(final Object o) {
        throw new IllegalStateException("This continuation is already complete".toString());
    }
    
    @Override
    public String toString() {
        return "This continuation is already complete";
    }
}
