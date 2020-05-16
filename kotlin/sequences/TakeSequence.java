// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;

public final class TakeSequence<T> implements Sequence<T>, DropTakeSequence<T>
{
    private final int count;
    private final Sequence<T> sequence;
    
    public TakeSequence(final Sequence<? extends T> sequence, int count) {
        Intrinsics.checkParameterIsNotNull(sequence, "sequence");
        this.sequence = (Sequence<T>)sequence;
        this.count = count;
        if (count >= 0) {
            count = 1;
        }
        else {
            count = 0;
        }
        if (count != 0) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("count must be non-negative, but was ");
        sb.append(this.count);
        sb.append('.');
        throw new IllegalArgumentException(sb.toString().toString());
    }
    
    @Override
    public Iterator<T> iterator() {
        return (Iterator<T>)new TakeSequence$iterator.TakeSequence$iterator$1(this);
    }
    
    @Override
    public Sequence<T> take(final int n) {
        TakeSequence takeSequence;
        if (n >= this.count) {
            takeSequence = this;
        }
        else {
            takeSequence = new TakeSequence(this.sequence, n);
        }
        return takeSequence;
    }
}
