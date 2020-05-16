// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.functions.Function1;

public final class FilteringSequence<T> implements Sequence<T>
{
    private final Function1<T, Boolean> predicate;
    private final boolean sendWhen;
    private final Sequence<T> sequence;
    
    public FilteringSequence(final Sequence<? extends T> sequence, final boolean sendWhen, final Function1<? super T, Boolean> predicate) {
        Intrinsics.checkParameterIsNotNull(sequence, "sequence");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        this.sequence = (Sequence<T>)sequence;
        this.sendWhen = sendWhen;
        this.predicate = (Function1<T, Boolean>)predicate;
    }
    
    @Override
    public Iterator<T> iterator() {
        return (Iterator<T>)new FilteringSequence$iterator.FilteringSequence$iterator$1(this);
    }
}
