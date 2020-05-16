// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.sequences;

import kotlin.jvm.internal.Intrinsics;
import java.util.Iterator;
import kotlin.jvm.functions.Function1;

public final class FlatteningSequence<T, R, E> implements Sequence<E>
{
    private final Function1<R, Iterator<E>> iterator;
    private final Sequence<T> sequence;
    private final Function1<T, R> transformer;
    
    public FlatteningSequence(final Sequence<? extends T> sequence, final Function1<? super T, ? extends R> transformer, final Function1<? super R, ? extends Iterator<? extends E>> iterator) {
        Intrinsics.checkParameterIsNotNull(sequence, "sequence");
        Intrinsics.checkParameterIsNotNull(transformer, "transformer");
        Intrinsics.checkParameterIsNotNull(iterator, "iterator");
        this.sequence = (Sequence<T>)sequence;
        this.transformer = (Function1<T, R>)transformer;
        this.iterator = (Function1<R, Iterator<E>>)iterator;
    }
    
    @Override
    public Iterator<E> iterator() {
        return (Iterator<E>)new FlatteningSequence$iterator.FlatteningSequence$iterator$1(this);
    }
}
