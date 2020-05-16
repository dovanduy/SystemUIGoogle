// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;
import java.util.List;
import com.google.common.base.Function;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Comparator;

public abstract class Ordering<T> implements Comparator<T>
{
    protected Ordering() {
    }
    
    public static <T> Ordering<T> from(final Comparator<T> comparator) {
        Ordering<T> ordering;
        if (comparator instanceof Ordering) {
            ordering = (Ordering<T>)comparator;
        }
        else {
            ordering = new ComparatorOrdering<T>(comparator);
        }
        return ordering;
    }
    
    public static <C extends Comparable> Ordering<C> natural() {
        return (Ordering<C>)NaturalOrdering.INSTANCE;
    }
    
    @CanIgnoreReturnValue
    @Override
    public abstract int compare(final T p0, final T p1);
    
    public <F> Ordering<F> onResultOf(final Function<F, ? extends T> function) {
        return (Ordering<F>)new ByFunctionOrdering((Function<Object, ?>)function, (Ordering<Object>)this);
    }
    
    public <S extends T> Ordering<S> reverse() {
        return new ReverseOrdering<S>(this);
    }
    
    public <E extends T> List<E> sortedCopy(final Iterable<E> iterable) {
        final Object[] array = Iterables.toArray(iterable);
        Arrays.sort(array, (Comparator<? super Object>)this);
        return (List<E>)Lists.newArrayList((Iterable<?>)Arrays.asList(array));
    }
    
    static class ArbitraryOrdering extends Ordering<Object>
    {
        private final AtomicInteger counter;
        private final ConcurrentMap<Object, Integer> uids;
        
        ArbitraryOrdering() {
            this.counter = new AtomicInteger(0);
            final MapMaker mapMaker = new MapMaker();
            Platform.tryWeakKeys(mapMaker);
            this.uids = (ConcurrentMap<Object, Integer>)mapMaker.makeMap();
        }
        
        private Integer getUid(final Object o) {
            Integer value;
            if ((value = this.uids.get(o)) == null) {
                value = this.counter.getAndIncrement();
                final Integer n = this.uids.putIfAbsent(o, value);
                if (n != null) {
                    value = n;
                }
            }
            return value;
        }
        
        @Override
        public int compare(final Object o, final Object o2) {
            if (o == o2) {
                return 0;
            }
            int n = -1;
            if (o == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            final int identityHashCode = this.identityHashCode(o);
            final int identityHashCode2 = this.identityHashCode(o2);
            if (identityHashCode != identityHashCode2) {
                if (identityHashCode >= identityHashCode2) {
                    n = 1;
                }
                return n;
            }
            final int compareTo = this.getUid(o).compareTo(this.getUid(o2));
            if (compareTo != 0) {
                return compareTo;
            }
            throw new AssertionError();
        }
        
        int identityHashCode(final Object o) {
            return System.identityHashCode(o);
        }
        
        @Override
        public String toString() {
            return "Ordering.arbitrary()";
        }
    }
    
    static class IncomparableValueException extends ClassCastException
    {
        private static final long serialVersionUID = 0L;
        final Object value;
    }
}
