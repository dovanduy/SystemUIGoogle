// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import kotlin.sequences.SequencesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;

class ArraysKt___ArraysKt extends ArraysKt___ArraysJvmKt
{
    public static <T> Sequence<T> asSequence(final T[] array) {
        Intrinsics.checkParameterIsNotNull(array, "$this$asSequence");
        if (array.length == 0) {
            return SequencesKt.emptySequence();
        }
        return (Sequence<T>)new ArraysKt___ArraysKt$asSequence$$inlined$Sequence.ArraysKt___ArraysKt$asSequence$$inlined$Sequence$1((Object[])array);
    }
    
    public static <T> boolean contains(final T[] array, final T t) {
        Intrinsics.checkParameterIsNotNull(array, "$this$contains");
        return indexOf(array, t) >= 0;
    }
    
    public static <T> List<T> filterNotNull(final T[] array) {
        Intrinsics.checkParameterIsNotNull(array, "$this$filterNotNull");
        final ArrayList<T> list = new ArrayList<T>();
        filterNotNullTo(array, list);
        return list;
    }
    
    public static final <C extends Collection<? super T>, T> C filterNotNullTo(final T[] array, final C c) {
        Intrinsics.checkParameterIsNotNull(array, "$this$filterNotNullTo");
        Intrinsics.checkParameterIsNotNull(c, "destination");
        for (final T t : array) {
            if (t != null) {
                c.add((Object)t);
            }
        }
        return c;
    }
    
    public static final <T> int indexOf(final T[] array, final T t) {
        Intrinsics.checkParameterIsNotNull(array, "$this$indexOf");
        final int n = 0;
        int i = 0;
        if (t == null) {
            while (i < array.length) {
                if (array[i] == null) {
                    return i;
                }
                ++i;
            }
        }
        else {
            for (int length = array.length, j = n; j < length; ++j) {
                if (Intrinsics.areEqual(t, array[j])) {
                    return j;
                }
            }
        }
        return -1;
    }
    
    public static char single(final char[] array) {
        Intrinsics.checkParameterIsNotNull(array, "$this$single");
        final int length = array.length;
        if (length == 0) {
            throw new NoSuchElementException("Array is empty.");
        }
        if (length == 1) {
            return array[0];
        }
        throw new IllegalArgumentException("Array has more than one element.");
    }
    
    public static <T> T singleOrNull(final T[] array) {
        Intrinsics.checkParameterIsNotNull(array, "$this$singleOrNull");
        T t;
        if (array.length == 1) {
            t = array[0];
        }
        else {
            t = null;
        }
        return t;
    }
    
    public static final <T, C extends Collection<? super T>> C toCollection(final T[] array, final C c) {
        Intrinsics.checkParameterIsNotNull(array, "$this$toCollection");
        Intrinsics.checkParameterIsNotNull(c, "destination");
        for (int length = array.length, i = 0; i < length; ++i) {
            c.add((Object)array[i]);
        }
        return c;
    }
    
    public static <T> List<T> toMutableList(final T[] array) {
        Intrinsics.checkParameterIsNotNull(array, "$this$toMutableList");
        return new ArrayList<T>((Collection<? extends T>)CollectionsKt__CollectionsKt.asCollection((E[])array));
    }
    
    public static <T> Set<T> toSet(final T[] array) {
        Intrinsics.checkParameterIsNotNull(array, "$this$toSet");
        final int length = array.length;
        Set<T> set2;
        if (length != 0) {
            if (length != 1) {
                final LinkedHashSet<T> set = new LinkedHashSet<T>(MapsKt.mapCapacity(array.length));
                toCollection(array, set);
                set2 = set;
            }
            else {
                set2 = SetsKt__SetsJVMKt.setOf(array[0]);
            }
        }
        else {
            set2 = SetsKt.emptySet();
        }
        return set2;
    }
}
