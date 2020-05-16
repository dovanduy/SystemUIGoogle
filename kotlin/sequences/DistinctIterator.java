// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.sequences;

import kotlin.jvm.internal.Intrinsics;
import java.util.Iterator;
import java.util.HashSet;
import kotlin.jvm.functions.Function1;
import kotlin.collections.AbstractIterator;

final class DistinctIterator<T, K> extends AbstractIterator<T>
{
    private final Function1<T, K> keySelector;
    private final HashSet<K> observed;
    private final Iterator<T> source;
    
    public DistinctIterator(final Iterator<? extends T> source, final Function1<? super T, ? extends K> keySelector) {
        Intrinsics.checkParameterIsNotNull(source, "source");
        Intrinsics.checkParameterIsNotNull(keySelector, "keySelector");
        this.source = (Iterator<T>)source;
        this.keySelector = (Function1<T, K>)keySelector;
        this.observed = new HashSet<K>();
    }
    
    @Override
    protected void computeNext() {
        while (this.source.hasNext()) {
            final T next = this.source.next();
            if (this.observed.add(this.keySelector.invoke(next))) {
                this.setNext(next);
                return;
            }
        }
        this.done();
    }
}
