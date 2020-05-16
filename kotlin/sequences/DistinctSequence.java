// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.functions.Function1;

public final class DistinctSequence<T, K> implements Sequence<T>
{
    private final Function1<T, K> keySelector;
    private final Sequence<T> source;
    
    public DistinctSequence(final Sequence<? extends T> source, final Function1<? super T, ? extends K> keySelector) {
        Intrinsics.checkParameterIsNotNull(source, "source");
        Intrinsics.checkParameterIsNotNull(keySelector, "keySelector");
        this.source = (Sequence<T>)source;
        this.keySelector = (Function1<T, K>)keySelector;
    }
    
    @Override
    public Iterator<T> iterator() {
        return (Iterator<T>)new DistinctIterator(this.source.iterator(), (Function1<? super Object, ?>)this.keySelector);
    }
}
