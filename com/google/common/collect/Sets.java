// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Collections;
import java.util.SortedSet;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.NavigableSet;
import com.google.common.base.Preconditions;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;

public final class Sets
{
    static boolean equalsImpl(final Set<?> set, final Object o) {
        boolean b = true;
        if (set == o) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        final Set set2 = (Set)o;
        try {
            if (set.size() != set2.size() || !set.containsAll(set2)) {
                b = false;
            }
            return b;
        }
        catch (NullPointerException | ClassCastException ex) {
            return false;
        }
    }
    
    static int hashCodeImpl(final Set<?> set) {
        final Iterator<?> iterator = set.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final Object next = iterator.next();
            int hashCode;
            if (next != null) {
                hashCode = next.hashCode();
            }
            else {
                hashCode = 0;
            }
            n += hashCode;
        }
        return n;
    }
    
    public static <E> HashSet<E> newHashSet() {
        return new HashSet<E>();
    }
    
    public static <E> HashSet<E> newHashSetWithExpectedSize(final int n) {
        return new HashSet<E>(Maps.capacity(n));
    }
    
    static boolean removeAllImpl(final Set<?> set, final Collection<?> collection) {
        Preconditions.checkNotNull(collection);
        Object elementSet = collection;
        if (collection instanceof Multiset) {
            elementSet = ((Multiset<?>)collection).elementSet();
        }
        if (elementSet instanceof Set && ((Collection)elementSet).size() > set.size()) {
            return Iterators.removeAll(set.iterator(), (Collection<?>)elementSet);
        }
        return removeAllImpl(set, ((Collection<?>)elementSet).iterator());
    }
    
    static boolean removeAllImpl(final Set<?> set, final Iterator<?> iterator) {
        boolean b = false;
        while (iterator.hasNext()) {
            b |= set.remove(iterator.next());
        }
        return b;
    }
    
    public static <E> NavigableSet<E> unmodifiableNavigableSet(final NavigableSet<E> set) {
        if (!(set instanceof ImmutableCollection) && !(set instanceof UnmodifiableNavigableSet)) {
            return new UnmodifiableNavigableSet<E>(set);
        }
        return set;
    }
    
    abstract static class ImprovedAbstractSet<E> extends AbstractSet<E>
    {
        @Override
        public boolean removeAll(final Collection<?> collection) {
            return Sets.removeAllImpl(this, collection);
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            Preconditions.checkNotNull(collection);
            return super.retainAll(collection);
        }
    }
    
    static final class UnmodifiableNavigableSet<E> extends ForwardingSortedSet<E> implements NavigableSet<E>, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final NavigableSet<E> delegate;
        private transient UnmodifiableNavigableSet<E> descendingSet;
        private final SortedSet<E> unmodifiableDelegate;
        
        UnmodifiableNavigableSet(final NavigableSet<E> s) {
            Preconditions.checkNotNull(s);
            this.delegate = s;
            this.unmodifiableDelegate = Collections.unmodifiableSortedSet(s);
        }
        
        @Override
        public E ceiling(final E e) {
            return this.delegate.ceiling(e);
        }
        
        @Override
        protected SortedSet<E> delegate() {
            return this.unmodifiableDelegate;
        }
        
        @Override
        public Iterator<E> descendingIterator() {
            return (Iterator<E>)Iterators.unmodifiableIterator((Iterator<?>)this.delegate.descendingIterator());
        }
        
        @Override
        public NavigableSet<E> descendingSet() {
            UnmodifiableNavigableSet<E> descendingSet;
            if ((descendingSet = this.descendingSet) == null) {
                descendingSet = new UnmodifiableNavigableSet<E>(this.delegate.descendingSet());
                this.descendingSet = descendingSet;
                descendingSet.descendingSet = this;
            }
            return descendingSet;
        }
        
        @Override
        public E floor(final E e) {
            return this.delegate.floor(e);
        }
        
        @Override
        public NavigableSet<E> headSet(final E e, final boolean b) {
            return Sets.unmodifiableNavigableSet(this.delegate.headSet(e, b));
        }
        
        @Override
        public E higher(final E e) {
            return this.delegate.higher(e);
        }
        
        @Override
        public E lower(final E e) {
            return this.delegate.lower(e);
        }
        
        @Override
        public E pollFirst() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public E pollLast() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public NavigableSet<E> subSet(final E e, final boolean b, final E e2, final boolean b2) {
            return Sets.unmodifiableNavigableSet(this.delegate.subSet(e, b, e2, b2));
        }
        
        @Override
        public NavigableSet<E> tailSet(final E e, final boolean b) {
            return Sets.unmodifiableNavigableSet(this.delegate.tailSet(e, b));
        }
    }
}
