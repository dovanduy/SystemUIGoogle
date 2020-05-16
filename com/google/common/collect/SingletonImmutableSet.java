// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Iterator;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.LazyInit;

final class SingletonImmutableSet<E> extends ImmutableSet<E>
{
    @LazyInit
    private transient int cachedHashCode;
    final transient E element;
    
    SingletonImmutableSet(final E element) {
        Preconditions.checkNotNull(element);
        this.element = element;
    }
    
    SingletonImmutableSet(final E element, final int cachedHashCode) {
        this.element = element;
        this.cachedHashCode = cachedHashCode;
    }
    
    @Override
    public boolean contains(final Object obj) {
        return this.element.equals(obj);
    }
    
    @Override
    int copyIntoArray(final Object[] array, final int n) {
        array[n] = this.element;
        return n + 1;
    }
    
    @Override
    ImmutableList<E> createAsList() {
        return ImmutableList.of(this.element);
    }
    
    @Override
    public final int hashCode() {
        int cachedHashCode;
        if ((cachedHashCode = this.cachedHashCode) == 0) {
            cachedHashCode = this.element.hashCode();
            this.cachedHashCode = cachedHashCode;
        }
        return cachedHashCode;
    }
    
    @Override
    boolean isHashCodeFast() {
        return this.cachedHashCode != 0;
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    @Override
    public UnmodifiableIterator<E> iterator() {
        return Iterators.singletonIterator(this.element);
    }
    
    @Override
    public int size() {
        return 1;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(this.element.toString());
        sb.append(']');
        return sb.toString();
    }
}
