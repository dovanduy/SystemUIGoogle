// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;

class RegularImmutableList<E> extends ImmutableList<E>
{
    static final ImmutableList<Object> EMPTY;
    final transient Object[] array;
    private final transient int size;
    
    static {
        EMPTY = new RegularImmutableList<Object>(new Object[0], 0);
    }
    
    RegularImmutableList(final Object[] array, final int size) {
        this.array = array;
        this.size = size;
    }
    
    @Override
    int copyIntoArray(final Object[] array, final int n) {
        System.arraycopy(this.array, 0, array, n, this.size);
        return n + this.size;
    }
    
    @Override
    public E get(final int n) {
        Preconditions.checkElementIndex(n, this.size);
        return (E)this.array[n];
    }
    
    @Override
    Object[] internalArray() {
        return this.array;
    }
    
    @Override
    int internalArrayEnd() {
        return this.size;
    }
    
    @Override
    int internalArrayStart() {
        return 0;
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    @Override
    public int size() {
        return this.size;
    }
}
