// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.sequences;

import java.util.ArrayList;
import kotlin.collections.CollectionsKt;
import java.util.List;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import kotlin.TypeCastException;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

class SequencesKt___SequencesKt extends SequencesKt___SequencesJvmKt
{
    public static <T> Iterable<T> asIterable(final Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$asIterable");
        return (Iterable<T>)new SequencesKt___SequencesKt$asIterable$$inlined$Iterable.SequencesKt___SequencesKt$asIterable$$inlined$Iterable$1((Sequence)sequence);
    }
    
    public static <T> Sequence<T> distinct(final Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$distinct");
        return distinctBy(sequence, (Function1<? super T, ?>)SequencesKt___SequencesKt$distinct.SequencesKt___SequencesKt$distinct$1.INSTANCE);
    }
    
    public static final <T, K> Sequence<T> distinctBy(final Sequence<? extends T> sequence, final Function1<? super T, ? extends K> function1) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$distinctBy");
        Intrinsics.checkParameterIsNotNull(function1, "selector");
        return new DistinctSequence<T, Object>(sequence, function1);
    }
    
    public static <T> Sequence<T> filter(final Sequence<? extends T> sequence, final Function1<? super T, Boolean> function1) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$filter");
        Intrinsics.checkParameterIsNotNull(function1, "predicate");
        return new FilteringSequence<T>(sequence, true, function1);
    }
    
    public static <T> Sequence<T> filterNot(final Sequence<? extends T> sequence, final Function1<? super T, Boolean> function1) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$filterNot");
        Intrinsics.checkParameterIsNotNull(function1, "predicate");
        return new FilteringSequence<T>(sequence, false, function1);
    }
    
    public static final <T> Sequence<T> filterNotNull(final Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$filterNotNull");
        final Sequence<T> filterNot = SequencesKt.filterNot(sequence, (Function1<? super T, Boolean>)SequencesKt___SequencesKt$filterNotNull.SequencesKt___SequencesKt$filterNotNull$1.INSTANCE);
        if (filterNot != null) {
            return filterNot;
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.sequences.Sequence<T>");
    }
    
    public static <T> T firstOrNull(final Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$firstOrNull");
        final Iterator<? extends T> iterator = sequence.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        return (T)iterator.next();
    }
    
    public static <T, R> Sequence<R> flatMap(final Sequence<? extends T> sequence, final Function1<? super T, ? extends Sequence<? extends R>> function1) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$flatMap");
        Intrinsics.checkParameterIsNotNull(function1, "transform");
        return new FlatteningSequence<Object, Object, R>(sequence, function1, (Function1<?, ? extends Iterator<? extends R>>)SequencesKt___SequencesKt$flatMap.SequencesKt___SequencesKt$flatMap$1.INSTANCE);
    }
    
    public static <T> T lastOrNull(final Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$lastOrNull");
        final Iterator<? extends T> iterator = sequence.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        T t = (T)iterator.next();
        while (iterator.hasNext()) {
            t = (T)iterator.next();
        }
        return t;
    }
    
    public static <T, R> Sequence<R> map(final Sequence<? extends T> sequence, final Function1<? super T, ? extends R> function1) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$map");
        Intrinsics.checkParameterIsNotNull(function1, "transform");
        return new TransformingSequence<Object, R>(sequence, function1);
    }
    
    public static <T, R> Sequence<R> mapNotNull(final Sequence<? extends T> sequence, final Function1<? super T, ? extends R> function1) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$mapNotNull");
        Intrinsics.checkParameterIsNotNull(function1, "transform");
        return filterNotNull((Sequence<? extends R>)new TransformingSequence<Object, R>(sequence, function1));
    }
    
    public static <T> Sequence<T> sortedWith(final Sequence<? extends T> sequence, final Comparator<? super T> comparator) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$sortedWith");
        Intrinsics.checkParameterIsNotNull(comparator, "comparator");
        return (Sequence<T>)new SequencesKt___SequencesKt$sortedWith.SequencesKt___SequencesKt$sortedWith$1((Sequence)sequence, (Comparator)comparator);
    }
    
    public static <T> Sequence<T> take(final Sequence<? extends T> sequence, final int i) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$take");
        if (i >= 0) {
            Sequence<T> sequence2;
            if (i == 0) {
                sequence2 = SequencesKt.emptySequence();
            }
            else if (sequence instanceof DropTakeSequence) {
                sequence2 = ((DropTakeSequence<T>)sequence).take(i);
            }
            else {
                sequence2 = new TakeSequence<T>(sequence, i);
            }
            return sequence2;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Requested element count ");
        sb.append(i);
        sb.append(" is less than zero.");
        throw new IllegalArgumentException(sb.toString().toString());
    }
    
    public static final <T, C extends Collection<? super T>> C toCollection(final Sequence<? extends T> sequence, final C c) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$toCollection");
        Intrinsics.checkParameterIsNotNull(c, "destination");
        final Iterator<? extends T> iterator = sequence.iterator();
        while (iterator.hasNext()) {
            c.add((Object)iterator.next());
        }
        return c;
    }
    
    public static <T> List<T> toList(final Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$toList");
        return CollectionsKt.optimizeReadOnlyList(toMutableList((Sequence<? extends T>)sequence));
    }
    
    public static final <T> List<T> toMutableList(final Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(sequence, "$this$toMutableList");
        final ArrayList<T> list = new ArrayList<T>();
        toCollection((Sequence<?>)sequence, list);
        return list;
    }
}
