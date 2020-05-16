// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import java.util.concurrent.atomic.AtomicReference;

public final class ConstrainedOnceSequence<T> implements Sequence<T>
{
    private final AtomicReference<Sequence<T>> sequenceRef;
    
    public ConstrainedOnceSequence(final Sequence<? extends T> initialValue) {
        Intrinsics.checkParameterIsNotNull(initialValue, "sequence");
        this.sequenceRef = new AtomicReference<Sequence<T>>((Sequence<T>)initialValue);
    }
    
    @Override
    public Iterator<T> iterator() {
        final Sequence<T> sequence = this.sequenceRef.getAndSet(null);
        if (sequence != null) {
            return sequence.iterator();
        }
        throw new IllegalStateException("This sequence can be consumed only once.");
    }
}
