// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.io.Serializable;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.SortedSet;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.Comparator;
import java.util.NavigableSet;

public abstract class ImmutableSortedSet<E> extends ImmutableSortedSetFauxverideShim<E> implements NavigableSet<E>, SortedIterable<E>
{
    final transient Comparator<? super E> comparator;
    @LazyInit
    transient ImmutableSortedSet<E> descendingSet;
    
    ImmutableSortedSet(final Comparator<? super E> comparator) {
        this.comparator = comparator;
    }
    
    static <E> ImmutableSortedSet<E> construct(final Comparator<? super E> c, final int n, final E... original) {
        if (n == 0) {
            return (ImmutableSortedSet<E>)emptySet((Comparator<? super Object>)c);
        }
        ObjectArrays.checkElementsNotNull(original, n);
        Arrays.sort(original, 0, n, c);
        int i = 1;
        int n2 = 1;
        while (i < n) {
            final E e = original[i];
            int n3 = n2;
            if (c.compare(e, original[n2 - 1]) != 0) {
                original[n2] = e;
                n3 = n2 + 1;
            }
            ++i;
            n2 = n3;
        }
        Arrays.fill(original, n2, n, null);
        E[] copy = original;
        if (n2 < original.length / 2) {
            copy = Arrays.copyOf(original, n2);
        }
        return new RegularImmutableSortedSet<E>(ImmutableList.asImmutableList(copy, n2), (Comparator<? super Object>)c);
    }
    
    static <E> RegularImmutableSortedSet<E> emptySet(final Comparator<? super E> obj) {
        if (Ordering.natural().equals(obj)) {
            return (RegularImmutableSortedSet<E>)RegularImmutableSortedSet.NATURAL_EMPTY_SET;
        }
        return new RegularImmutableSortedSet<E>(ImmutableList.of(), obj);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws InvalidObjectException {
        throw new InvalidObjectException("Use SerializedForm");
    }
    
    static int unsafeCompare(final Comparator<?> comparator, final Object o, final Object o2) {
        return comparator.compare(o, o2);
    }
    
    @Override
    public E ceiling(final E e) {
        return Iterables.getFirst((Iterable<? extends E>)this.tailSet(e, true), (E)null);
    }
    
    @Override
    public Comparator<? super E> comparator() {
        return this.comparator;
    }
    
    abstract ImmutableSortedSet<E> createDescendingSet();
    
    @Override
    public abstract UnmodifiableIterator<E> descendingIterator();
    
    @Override
    public ImmutableSortedSet<E> descendingSet() {
        ImmutableSortedSet<E> descendingSet;
        if ((descendingSet = this.descendingSet) == null) {
            descendingSet = this.createDescendingSet();
            this.descendingSet = descendingSet;
            descendingSet.descendingSet = this;
        }
        return descendingSet;
    }
    
    @Override
    public E first() {
        return this.iterator().next();
    }
    
    @Override
    public E floor(final E e) {
        return Iterators.getNext((Iterator<? extends E>)this.headSet(e, true).descendingIterator(), (E)null);
    }
    
    @Override
    public ImmutableSortedSet<E> headSet(final E e) {
        return this.headSet(e, false);
    }
    
    @Override
    public ImmutableSortedSet<E> headSet(final E e, final boolean b) {
        Preconditions.checkNotNull(e);
        return this.headSetImpl(e, b);
    }
    
    abstract ImmutableSortedSet<E> headSetImpl(final E p0, final boolean p1);
    
    @Override
    public E higher(final E e) {
        return Iterables.getFirst((Iterable<? extends E>)this.tailSet(e, false), (E)null);
    }
    
    @Override
    public E last() {
        return this.descendingIterator().next();
    }
    
    @Override
    public E lower(final E e) {
        return Iterators.getNext((Iterator<? extends E>)this.headSet(e, false).descendingIterator(), (E)null);
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final E pollFirst() {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final E pollLast() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ImmutableSortedSet<E> subSet(final E e, final E e2) {
        return this.subSet(e, true, e2, false);
    }
    
    @Override
    public ImmutableSortedSet<E> subSet(final E e, final boolean b, final E e2, final boolean b2) {
        Preconditions.checkNotNull(e);
        Preconditions.checkNotNull(e2);
        Preconditions.checkArgument(this.comparator.compare((Object)e, (Object)e2) <= 0);
        return this.subSetImpl(e, b, e2, b2);
    }
    
    abstract ImmutableSortedSet<E> subSetImpl(final E p0, final boolean p1, final E p2, final boolean p3);
    
    @Override
    public ImmutableSortedSet<E> tailSet(final E e) {
        return this.tailSet(e, true);
    }
    
    @Override
    public ImmutableSortedSet<E> tailSet(final E e, final boolean b) {
        Preconditions.checkNotNull(e);
        return this.tailSetImpl(e, b);
    }
    
    abstract ImmutableSortedSet<E> tailSetImpl(final E p0, final boolean p1);
    
    int unsafeCompare(final Object o, final Object o2) {
        return unsafeCompare(this.comparator, o, o2);
    }
    
    @Override
    Object writeReplace() {
        return new SerializedForm((Comparator<? super Object>)this.comparator, this.toArray());
    }
    
    public static final class Builder<E> extends ImmutableSet.Builder<E>
    {
        private final Comparator<? super E> comparator;
        
        public Builder(final Comparator<? super E> comparator) {
            Preconditions.checkNotNull(comparator);
            this.comparator = comparator;
        }
        
        @CanIgnoreReturnValue
        public Builder<E> add(final E e) {
            super.add(e);
            return this;
        }
        
        @CanIgnoreReturnValue
        public Builder<E> add(final E... array) {
            super.add(array);
            return this;
        }
        
        public ImmutableSortedSet<E> build() {
            final ImmutableSortedSet<E> construct = ImmutableSortedSet.construct(this.comparator, super.size, (E[])super.contents);
            super.size = construct.size();
            super.forceCopy = true;
            return construct;
        }
    }
    
    private static class SerializedForm<E> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final Comparator<? super E> comparator;
        final Object[] elements;
        
        public SerializedForm(final Comparator<? super E> comparator, final Object[] elements) {
            this.comparator = comparator;
            this.elements = elements;
        }
        
        Object readResolve() {
            final Builder<Object> builder = new Builder<Object>((Comparator<? super Object>)this.comparator);
            builder.add(this.elements);
            return builder.build();
        }
    }
}
