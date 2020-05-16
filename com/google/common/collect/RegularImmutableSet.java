// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Iterator;

final class RegularImmutableSet<E> extends ImmutableSet<E>
{
    static final RegularImmutableSet<Object> EMPTY;
    final transient Object[] elements;
    private final transient int hashCode;
    private final transient int mask;
    private final transient int size;
    final transient Object[] table;
    
    static {
        EMPTY = new RegularImmutableSet<Object>(new Object[0], 0, null, 0, 0);
    }
    
    RegularImmutableSet(final Object[] elements, final int hashCode, final Object[] table, final int mask, final int size) {
        this.elements = elements;
        this.table = table;
        this.mask = mask;
        this.hashCode = hashCode;
        this.size = size;
    }
    
    @Override
    public boolean contains(final Object obj) {
        final Object[] table = this.table;
        if (obj == null || table == null) {
            return false;
        }
        int smearedHash = Hashing.smearedHash(obj);
        while (true) {
            smearedHash &= this.mask;
            final Object o = table[smearedHash];
            if (o == null) {
                return false;
            }
            if (o.equals(obj)) {
                return true;
            }
            ++smearedHash;
        }
    }
    
    @Override
    int copyIntoArray(final Object[] array, final int n) {
        System.arraycopy(this.elements, 0, array, n, this.size);
        return n + this.size;
    }
    
    @Override
    ImmutableList<E> createAsList() {
        return ImmutableList.asImmutableList(this.elements, this.size);
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    @Override
    Object[] internalArray() {
        return this.elements;
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
    boolean isHashCodeFast() {
        return true;
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    @Override
    public UnmodifiableIterator<E> iterator() {
        return this.asList().iterator();
    }
    
    @Override
    public int size() {
        return this.size;
    }
}
