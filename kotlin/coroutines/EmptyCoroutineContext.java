// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.coroutines;

import kotlin.jvm.internal.Intrinsics;
import java.io.Serializable;

public final class EmptyCoroutineContext implements CoroutineContext, Serializable
{
    public static final EmptyCoroutineContext INSTANCE;
    private static final long serialVersionUID = 0L;
    
    static {
        INSTANCE = new EmptyCoroutineContext();
    }
    
    private EmptyCoroutineContext() {
    }
    
    private final Object readResolve() {
        return EmptyCoroutineContext.INSTANCE;
    }
    
    @Override
    public <E extends Element> E get(final Key<E> key) {
        Intrinsics.checkParameterIsNotNull(key, "key");
        return null;
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    @Override
    public String toString() {
        return "EmptyCoroutineContext";
    }
}
