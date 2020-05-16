// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.collections;

import java.util.LinkedHashSet;
import java.util.HashSet;
import kotlin.TypeCastException;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;

class CollectionsKt___CollectionsKt extends CollectionsKt___CollectionsJvmKt
{
    public static <T> Sequence<T> asSequence(final Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$asSequence");
        return (Sequence<T>)new CollectionsKt___CollectionsKt$asSequence$$inlined$Sequence.CollectionsKt___CollectionsKt$asSequence$$inlined$Sequence$1((Iterable)iterable);
    }
    
    public static <T> boolean contains(final Iterable<? extends T> iterable, final T t) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$contains");
        if (iterable instanceof Collection) {
            return ((Collection)iterable).contains(t);
        }
        return indexOf(iterable, t) >= 0;
    }
    
    public static final <T> int indexOf(final Iterable<? extends T> iterable, final T t) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$indexOf");
        if (iterable instanceof List) {
            return ((List<Object>)iterable).indexOf(t);
        }
        int n = 0;
        for (final Object next : iterable) {
            if (n < 0) {
                CollectionsKt__CollectionsKt.throwIndexOverflow();
                throw null;
            }
            if (Intrinsics.areEqual(t, next)) {
                return n;
            }
            ++n;
        }
        return -1;
    }
    
    public static <T> Set<T> intersect(final Iterable<? extends T> iterable, final Iterable<? extends T> iterable2) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$intersect");
        Intrinsics.checkParameterIsNotNull(iterable2, "other");
        final Set<? super Object> mutableSet = (Set<? super Object>)toMutableSet(iterable);
        CollectionsKt__MutableCollectionsKt.retainAll((Collection<? super Object>)mutableSet, (Iterable<?>)iterable2);
        return (Set<T>)mutableSet;
    }
    
    public static <T> List<T> minus(final Iterable<? extends T> iterable, final T t) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$minus");
        final ArrayList<T> list = new ArrayList<T>(CollectionsKt.collectionSizeOrDefault((Iterable<?>)iterable, 10));
        final Iterator<? extends T> iterator = iterable.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final T next = (T)iterator.next();
            final boolean b = true;
            int n2 = n;
            int n3 = b ? 1 : 0;
            if (n == 0) {
                n2 = n;
                n3 = (b ? 1 : 0);
                if (Intrinsics.areEqual(next, t)) {
                    n2 = 1;
                    n3 = 0;
                }
            }
            n = n2;
            if (n3 != 0) {
                list.add((T)next);
                n = n2;
            }
        }
        return list;
    }
    
    public static <T> List<T> plus(final Collection<? extends T> collection, final Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(collection, "$this$plus");
        Intrinsics.checkParameterIsNotNull(iterable, "elements");
        if (iterable instanceof Collection) {
            final int size = collection.size();
            final Collection<? extends T> c = (Collection<? extends T>)iterable;
            final ArrayList list = new ArrayList<Object>(size + c.size());
            list.addAll(collection);
            list.addAll(c);
            return (List<T>)list;
        }
        final ArrayList<Object> list2 = new ArrayList<Object>(collection);
        CollectionsKt.addAll((Collection<? super Object>)list2, (Iterable<?>)iterable);
        return (List<T>)list2;
    }
    
    public static <T> List<T> plus(final Collection<? extends T> c, final T e) {
        Intrinsics.checkParameterIsNotNull(c, "$this$plus");
        final ArrayList list = new ArrayList(c.size() + 1);
        list.addAll(c);
        list.add(e);
        return (List<T>)list;
    }
    
    public static <T> List<T> sortedWith(final Iterable<? extends T> iterable, final Comparator<? super T> comparator) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$sortedWith");
        Intrinsics.checkParameterIsNotNull(comparator, "comparator");
        if (!(iterable instanceof Collection)) {
            final List<T> mutableList = toMutableList(iterable);
            CollectionsKt.sortWith(mutableList, comparator);
            return mutableList;
        }
        final Collection<? extends T> collection = (Collection<? extends T>)iterable;
        if (collection.size() <= 1) {
            return CollectionsKt.toList(iterable);
        }
        final T[] array = collection.toArray(new Object[0]);
        if (array == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
        }
        if (array != null) {
            ArraysKt___ArraysJvmKt.sortWith(array, comparator);
            return ArraysKt___ArraysJvmKt.asList(array);
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
    }
    
    public static <T> Set<T> subtract(final Iterable<? extends T> iterable, final Iterable<? extends T> iterable2) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$subtract");
        Intrinsics.checkParameterIsNotNull(iterable2, "other");
        final Set<? super Object> mutableSet = (Set<? super Object>)toMutableSet(iterable);
        CollectionsKt__MutableCollectionsKt.removeAll((Collection<? super Object>)mutableSet, (Iterable<?>)iterable2);
        return (Set<T>)mutableSet;
    }
    
    public static final <T, C extends Collection<? super T>> C toCollection(final Iterable<? extends T> iterable, final C c) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$toCollection");
        Intrinsics.checkParameterIsNotNull(c, "destination");
        final Iterator<? extends T> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            c.add((Object)iterator.next());
        }
        return c;
    }
    
    public static final <T> HashSet<T> toHashSet(final Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$toHashSet");
        final HashSet set = new HashSet(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault((Iterable<?>)iterable, 12)));
        toCollection((Iterable<?>)iterable, (HashSet<T>)set);
        return (HashSet<T>)set;
    }
    
    public static <T> List<T> toList(final Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$toList");
        if (iterable instanceof Collection) {
            final Collection<? extends T> collection = (Collection<? extends T>)iterable;
            final int size = collection.size();
            List<T> list;
            if (size != 0) {
                if (size != 1) {
                    list = CollectionsKt.toMutableList((Collection<? extends T>)collection);
                }
                else {
                    Object o;
                    if (iterable instanceof List) {
                        o = ((List<Object>)iterable).get(0);
                    }
                    else {
                        o = iterable.iterator().next();
                    }
                    list = CollectionsKt.listOf(o);
                }
            }
            else {
                list = CollectionsKt.emptyList();
            }
            return list;
        }
        return CollectionsKt.optimizeReadOnlyList(toMutableList((Iterable<? extends T>)iterable));
    }
    
    public static final <T> List<T> toMutableList(final Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$toMutableList");
        if (iterable instanceof Collection) {
            return CollectionsKt.toMutableList((Collection<? extends T>)iterable);
        }
        final ArrayList<T> list = new ArrayList<T>();
        toCollection((Iterable<?>)iterable, list);
        return list;
    }
    
    public static <T> List<T> toMutableList(final Collection<? extends T> c) {
        Intrinsics.checkParameterIsNotNull(c, "$this$toMutableList");
        return new ArrayList<T>(c);
    }
    
    public static final <T> Set<T> toMutableSet(final Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$toMutableSet");
        LinkedHashSet<T> set;
        if (iterable instanceof Collection) {
            set = new LinkedHashSet<T>((Collection<? extends T>)iterable);
        }
        else {
            final LinkedHashSet<T> set2 = new LinkedHashSet<T>();
            toCollection((Iterable<?>)iterable, set2);
            set = set2;
        }
        return set;
    }
    
    public static <T> Set<T> toSet(final Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$toSet");
        if (iterable instanceof Collection) {
            final Collection collection = (Collection)iterable;
            final int size = collection.size();
            Set<T> set2;
            if (size != 0) {
                if (size != 1) {
                    final LinkedHashSet set = new LinkedHashSet<T>(MapsKt.mapCapacity(collection.size()));
                    toCollection((Iterable<?>)iterable, (LinkedHashSet<T>)set);
                    set2 = (LinkedHashSet<T>)set;
                }
                else {
                    Object of;
                    if (iterable instanceof List) {
                        of = ((List<Object>)iterable).get(0);
                    }
                    else {
                        of = iterable.iterator().next();
                    }
                    set2 = SetsKt__SetsJVMKt.setOf(of);
                }
            }
            else {
                set2 = SetsKt.emptySet();
            }
            return set2;
        }
        final LinkedHashSet<Object> set3 = new LinkedHashSet<Object>();
        toCollection((Iterable<?>)iterable, (LinkedHashSet<? extends T>)set3);
        return (Set<T>)SetsKt__SetsKt.optimizeReadOnlySet((Set<?>)set3);
    }
    
    public static <T> Set<T> union(final Iterable<? extends T> iterable, final Iterable<? extends T> iterable2) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$union");
        Intrinsics.checkParameterIsNotNull(iterable2, "other");
        final Set<? super Object> mutableSet = (Set<? super Object>)toMutableSet(iterable);
        CollectionsKt.addAll((Collection<? super Object>)mutableSet, (Iterable<?>)iterable2);
        return (Set<T>)mutableSet;
    }
}
