// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Iterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;

public abstract class ForwardingCollection<E> extends ForwardingObject implements Collection<E>
{
    protected ForwardingCollection() {
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean add(final E e) {
        return this.delegate().add(e);
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        return this.delegate().addAll(collection);
    }
    
    @Override
    public void clear() {
        this.delegate().clear();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.delegate().contains(o);
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        return this.delegate().containsAll(collection);
    }
    
    @Override
    protected abstract Collection<E> delegate();
    
    @Override
    public boolean isEmpty() {
        return this.delegate().isEmpty();
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.delegate().iterator();
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean remove(final Object o) {
        return this.delegate().remove(o);
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean removeAll(final Collection<?> collection) {
        return this.delegate().removeAll(collection);
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean retainAll(final Collection<?> collection) {
        return this.delegate().retainAll(collection);
    }
    
    @Override
    public int size() {
        return this.delegate().size();
    }
    
    @Override
    public Object[] toArray() {
        return this.delegate().toArray();
    }
    
    @CanIgnoreReturnValue
    @Override
    public <T> T[] toArray(final T[] array) {
        return this.delegate().toArray(array);
    }
}
