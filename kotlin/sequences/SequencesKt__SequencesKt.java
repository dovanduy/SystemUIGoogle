// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.sequences;

import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import java.util.Iterator;

class SequencesKt__SequencesKt extends SequencesKt__SequencesJVMKt
{
    public static <T> Sequence<T> asSequence(final Iterator<? extends T> iterator) {
        Intrinsics.checkParameterIsNotNull(iterator, "$this$asSequence");
        return constrainOnce((Sequence<? extends T>)new SequencesKt__SequencesKt$asSequence$$inlined$Sequence.SequencesKt__SequencesKt$asSequence$$inlined$Sequence$1((Iterator)iterator));
    }
    
    public static final <T> Sequence<T> constrainOnce(Sequence<? extends T> constrainedOnceSequence) {
        Intrinsics.checkParameterIsNotNull(constrainedOnceSequence, "$this$constrainOnce");
        if (!(constrainedOnceSequence instanceof ConstrainedOnceSequence)) {
            constrainedOnceSequence = new ConstrainedOnceSequence<Object>(constrainedOnceSequence);
        }
        return (Sequence<T>)constrainedOnceSequence;
    }
    
    public static <T> Sequence<T> emptySequence() {
        return (Sequence<T>)EmptySequence.INSTANCE;
    }
    
    public static <T> Sequence<T> sequenceOf(final T... array) {
        Intrinsics.checkParameterIsNotNull(array, "elements");
        Sequence<T> sequence;
        if (array.length == 0) {
            sequence = SequencesKt.emptySequence();
        }
        else {
            sequence = ArraysKt.asSequence(array);
        }
        return sequence;
    }
}
