// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Comparator;
import java.util.SortedSet;

public abstract class ForwardingSortedSet<E> extends ForwardingSet<E> implements SortedSet<E>
{
    protected ForwardingSortedSet() {
    }
    
    @Override
    public Comparator<? super E> comparator() {
        return this.delegate().comparator();
    }
    
    @Override
    protected abstract SortedSet<E> delegate();
    
    @Override
    public E first() {
        return this.delegate().first();
    }
    
    @Override
    public SortedSet<E> headSet(final E e) {
        return this.delegate().headSet(e);
    }
    
    @Override
    public E last() {
        return this.delegate().last();
    }
    
    @Override
    public SortedSet<E> subSet(final E e, final E e2) {
        return this.delegate().subSet(e, e2);
    }
    
    @Override
    public SortedSet<E> tailSet(final E e) {
        return this.delegate().tailSet(e);
    }
}
