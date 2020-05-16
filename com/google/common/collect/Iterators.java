// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.primitives.Ints;
import java.util.NoSuchElementException;
import com.google.common.base.Objects;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.Collection;

public final class Iterators
{
    @CanIgnoreReturnValue
    public static <T> boolean addAll(final Collection<T> collection, final Iterator<? extends T> iterator) {
        Preconditions.checkNotNull(collection);
        Preconditions.checkNotNull(iterator);
        boolean b = false;
        while (iterator.hasNext()) {
            b |= collection.add((T)iterator.next());
        }
        return b;
    }
    
    static void clear(final Iterator<?> iterator) {
        Preconditions.checkNotNull(iterator);
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }
    
    public static boolean elementsEqual(final Iterator<?> iterator, final Iterator<?> iterator2) {
        while (iterator.hasNext()) {
            if (!iterator2.hasNext()) {
                return false;
            }
            if (!Objects.equal(iterator.next(), iterator2.next())) {
                return false;
            }
        }
        return iterator2.hasNext() ^ true;
    }
    
    static <T> Iterator<T> emptyModifiableIterator() {
        return (Iterator<T>)EmptyModifiableIterator.INSTANCE;
    }
    
    public static <T> T getNext(final Iterator<? extends T> iterator, T next) {
        if (iterator.hasNext()) {
            next = (T)iterator.next();
        }
        return next;
    }
    
    public static <T> PeekingIterator<T> peekingIterator(final Iterator<? extends T> iterator) {
        if (iterator instanceof PeekingImpl) {
            return (PeekingImpl<T>)iterator;
        }
        return new PeekingImpl<T>(iterator);
    }
    
    static <T> T pollNext(final Iterator<T> iterator) {
        if (iterator.hasNext()) {
            final T next = iterator.next();
            iterator.remove();
            return next;
        }
        return null;
    }
    
    @CanIgnoreReturnValue
    public static boolean removeAll(final Iterator<?> iterator, final Collection<?> collection) {
        Preconditions.checkNotNull(collection);
        boolean b = false;
        while (iterator.hasNext()) {
            if (collection.contains(iterator.next())) {
                iterator.remove();
                b = true;
            }
        }
        return b;
    }
    
    public static <T> UnmodifiableIterator<T> singletonIterator(final T t) {
        return new UnmodifiableIterator<T>() {
            boolean done;
            
            @Override
            public boolean hasNext() {
                return this.done ^ true;
            }
            
            @Override
            public T next() {
                if (!this.done) {
                    this.done = true;
                    return t;
                }
                throw new NoSuchElementException();
            }
        };
    }
    
    public static int size(final Iterator<?> iterator) {
        long n = 0L;
        while (iterator.hasNext()) {
            iterator.next();
            ++n;
        }
        return Ints.saturatedCast(n);
    }
    
    public static <T> UnmodifiableIterator<T> unmodifiableIterator(final Iterator<? extends T> iterator) {
        Preconditions.checkNotNull(iterator);
        if (iterator instanceof UnmodifiableIterator) {
            return (UnmodifiableIterator<T>)iterator;
        }
        return new UnmodifiableIterator<T>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
            
            @Override
            public T next() {
                return iterator.next();
            }
        };
    }
    
    private enum EmptyModifiableIterator implements Iterator<Object>
    {
        INSTANCE;
        
        @Override
        public boolean hasNext() {
            return false;
        }
        
        @Override
        public Object next() {
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            CollectPreconditions.checkRemove(false);
        }
    }
    
    private static class PeekingImpl<E> implements PeekingIterator<E>
    {
        private boolean hasPeeked;
        private final Iterator<? extends E> iterator;
        private E peekedElement;
        
        public PeekingImpl(final Iterator<? extends E> iterator) {
            Preconditions.checkNotNull(iterator);
            this.iterator = iterator;
        }
        
        @Override
        public boolean hasNext() {
            return this.hasPeeked || this.iterator.hasNext();
        }
        
        @Override
        public E next() {
            if (!this.hasPeeked) {
                return (E)this.iterator.next();
            }
            final E peekedElement = this.peekedElement;
            this.hasPeeked = false;
            this.peekedElement = null;
            return peekedElement;
        }
        
        @Override
        public E peek() {
            if (!this.hasPeeked) {
                this.peekedElement = (E)this.iterator.next();
                this.hasPeeked = true;
            }
            return this.peekedElement;
        }
        
        @Override
        public void remove() {
            Preconditions.checkState(this.hasPeeked ^ true, "Can't remove after you've peeked at next");
            this.iterator.remove();
        }
    }
}
