// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.functions.Function1;

public final class TransformingSequence<T, R> implements Sequence<R>
{
    private final Sequence<T> sequence;
    private final Function1<T, R> transformer;
    
    public TransformingSequence(final Sequence<? extends T> sequence, final Function1<? super T, ? extends R> transformer) {
        Intrinsics.checkParameterIsNotNull(sequence, "sequence");
        Intrinsics.checkParameterIsNotNull(transformer, "transformer");
        this.sequence = (Sequence<T>)sequence;
        this.transformer = (Function1<T, R>)transformer;
    }
    
    @Override
    public Iterator<R> iterator() {
        return (Iterator<R>)new TransformingSequence$iterator.TransformingSequence$iterator$1(this);
    }
}
