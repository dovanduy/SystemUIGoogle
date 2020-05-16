// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Iterator;
import java.util.Collection;

public final class Iterables
{
    private static <E> Collection<E> castOrCopyToCollection(final Iterable<E> iterable) {
        Object arrayList;
        if (iterable instanceof Collection) {
            arrayList = iterable;
        }
        else {
            arrayList = Lists.newArrayList((Iterator<?>)iterable.iterator());
        }
        return (Collection<E>)arrayList;
    }
    
    public static <T> T getFirst(final Iterable<? extends T> iterable, final T t) {
        return Iterators.getNext(iterable.iterator(), t);
    }
    
    static Object[] toArray(final Iterable<?> iterable) {
        return castOrCopyToCollection(iterable).toArray();
    }
}
