// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.sequences;

import kotlin.Unit;
import kotlin.coroutines.Continuation;

public abstract class SequenceScope<T>
{
    public abstract Object yield(final T p0, final Continuation<? super Unit> p1);
}
