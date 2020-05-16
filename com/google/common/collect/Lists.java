// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Collection;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Iterator;
import com.google.common.base.Objects;
import java.util.RandomAccess;
import com.google.common.base.Preconditions;
import java.util.List;
import com.google.common.primitives.Ints;

public final class Lists
{
    static int computeArrayListCapacity(final int n) {
        CollectPreconditions.checkNonnegative(n, "arraySize");
        return Ints.saturatedCast(n + 5L + n / 10);
    }
    
    static boolean equalsImpl(final List<?> list, final Object o) {
        Preconditions.checkNotNull(list);
        if (o == list) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        final List list2 = (List)o;
        final int size = list.size();
        if (size != list2.size()) {
            return false;
        }
        if (list instanceof RandomAccess && list2 instanceof RandomAccess) {
            for (int i = 0; i < size; ++i) {
                if (!Objects.equal(list.get(i), list2.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return Iterators.elementsEqual(list.iterator(), list2.iterator());
    }
    
    static int indexOfImpl(final List<?> list, final Object o) {
        if (list instanceof RandomAccess) {
            return indexOfRandomAccess(list, o);
        }
        final ListIterator<?> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            if (Objects.equal(o, listIterator.next())) {
                return listIterator.previousIndex();
            }
        }
        return -1;
    }
    
    private static int indexOfRandomAccess(final List<?> list, final Object o) {
        final int size = list.size();
        int i = 0;
        final int n = 0;
        if (o == null) {
            for (int j = n; j < size; ++j) {
                if (list.get(j) == null) {
                    return j;
                }
            }
        }
        else {
            while (i < size) {
                if (o.equals(list.get(i))) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }
    
    static int lastIndexOfImpl(final List<?> list, final Object o) {
        if (list instanceof RandomAccess) {
            return lastIndexOfRandomAccess(list, o);
        }
        final ListIterator<?> listIterator = list.listIterator(list.size());
        while (listIterator.hasPrevious()) {
            if (Objects.equal(o, listIterator.previous())) {
                return listIterator.nextIndex();
            }
        }
        return -1;
    }
    
    private static int lastIndexOfRandomAccess(final List<?> list, final Object o) {
        if (o == null) {
            for (int i = list.size() - 1; i >= 0; --i) {
                if (list.get(i) == null) {
                    return i;
                }
            }
        }
        else {
            for (int j = list.size() - 1; j >= 0; --j) {
                if (o.equals(list.get(j))) {
                    return j;
                }
            }
        }
        return -1;
    }
    
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }
    
    public static <E> ArrayList<E> newArrayList(final Iterable<? extends E> iterable) {
        Preconditions.checkNotNull(iterable);
        ArrayList<E> arrayList;
        if (iterable instanceof Collection) {
            arrayList = new ArrayList<E>((Collection<? extends E>)Collections2.cast((Iterable<? extends E>)iterable));
        }
        else {
            arrayList = newArrayList((Iterator<? extends E>)iterable.iterator());
        }
        return arrayList;
    }
    
    public static <E> ArrayList<E> newArrayList(final Iterator<? extends E> iterator) {
        final ArrayList<Object> arrayList = newArrayList();
        Iterators.addAll(arrayList, iterator);
        return (ArrayList<E>)arrayList;
    }
}
