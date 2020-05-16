// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import kotlin.TypeCastException;
import kotlin.jvm.internal.TypeIntrinsics;
import java.util.RandomAccess;
import java.util.List;
import kotlin.jvm.functions.Function1;
import kotlin.sequences.Sequence;
import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import java.util.Collection;

class CollectionsKt__MutableCollectionsKt extends CollectionsKt__MutableCollectionsJVMKt
{
    public static <T> boolean addAll(final Collection<? super T> collection, final Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(collection, "$this$addAll");
        Intrinsics.checkParameterIsNotNull(iterable, "elements");
        if (iterable instanceof Collection) {
            return collection.addAll((Collection<? extends T>)iterable);
        }
        boolean b = false;
        final Iterator<Object> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            if (collection.add((Object)iterator.next())) {
                b = true;
            }
        }
        return b;
    }
    
    public static <T> boolean addAll(final Collection<? super T> collection, final Sequence<? extends T> sequence) {
        Intrinsics.checkParameterIsNotNull(collection, "$this$addAll");
        Intrinsics.checkParameterIsNotNull(sequence, "elements");
        final Iterator<? extends T> iterator = sequence.iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            if (collection.add((Object)iterator.next())) {
                b = true;
            }
        }
        return b;
    }
    
    private static final <T> boolean filterInPlace$CollectionsKt__MutableCollectionsKt(final Iterable<? extends T> iterable, final Function1<? super T, Boolean> function1, final boolean b) {
        final Iterator<? extends T> iterator = iterable.iterator();
        boolean b2 = false;
        while (iterator.hasNext()) {
            if (function1.invoke((Object)iterator.next()) == b) {
                iterator.remove();
                b2 = true;
            }
        }
        return b2;
    }
    
    private static final <T> boolean filterInPlace$CollectionsKt__MutableCollectionsKt(final List<T> list, final Function1<? super T, Boolean> function1, final boolean b) {
        if (!(list instanceof RandomAccess)) {
            if (list != null) {
                return filterInPlace$CollectionsKt__MutableCollectionsKt((Iterable<?>)TypeIntrinsics.asMutableIterable(list), (Function1<? super Object, Boolean>)function1, b);
            }
            throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.MutableIterable<T>");
        }
        else {
            final int lastIndex = CollectionsKt__CollectionsKt.getLastIndex((List<?>)list);
            int n3;
            if (lastIndex >= 0) {
                int n2;
                int n = n2 = 0;
                while (true) {
                    final T value = (T)list.get(n);
                    if (function1.invoke(value) != b) {
                        if (n2 != n) {
                            list.set(n2, (T)value);
                        }
                        ++n2;
                    }
                    n3 = n2;
                    if (n == lastIndex) {
                        break;
                    }
                    ++n;
                }
            }
            else {
                n3 = 0;
            }
            if (n3 < list.size()) {
                int lastIndex2 = CollectionsKt__CollectionsKt.getLastIndex((List<?>)list);
                if (lastIndex2 >= n3) {
                    while (true) {
                        list.remove(lastIndex2);
                        if (lastIndex2 == n3) {
                            break;
                        }
                        --lastIndex2;
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    public static final <T> boolean removeAll(final Collection<? super T> collection, final Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(collection, "$this$removeAll");
        Intrinsics.checkParameterIsNotNull(iterable, "elements");
        return TypeIntrinsics.asMutableCollection(collection).removeAll(CollectionsKt__IterablesKt.convertToSetForSetOperationWith((Iterable<?>)iterable, (Iterable<?>)collection));
    }
    
    public static <T> boolean removeAll(final List<T> list, final Function1<? super T, Boolean> function1) {
        Intrinsics.checkParameterIsNotNull(list, "$this$removeAll");
        Intrinsics.checkParameterIsNotNull(function1, "predicate");
        return filterInPlace$CollectionsKt__MutableCollectionsKt(list, function1, true);
    }
    
    public static final <T> boolean retainAll(final Collection<? super T> collection, final Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(collection, "$this$retainAll");
        Intrinsics.checkParameterIsNotNull(iterable, "elements");
        return TypeIntrinsics.asMutableCollection(collection).retainAll(CollectionsKt__IterablesKt.convertToSetForSetOperationWith((Iterable<?>)iterable, (Iterable<?>)collection));
    }
}
