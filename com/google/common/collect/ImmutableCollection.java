// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Arrays;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.Collection;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.AbstractCollection;

public abstract class ImmutableCollection<E> extends AbstractCollection<E> implements Serializable
{
    private static final Object[] EMPTY_ARRAY;
    
    static {
        EMPTY_ARRAY = new Object[0];
    }
    
    ImmutableCollection() {
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final boolean add(final E e) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final boolean addAll(final Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public abstract boolean contains(final Object p0);
    
    @CanIgnoreReturnValue
    int copyIntoArray(final Object[] array, int n) {
        final UnmodifiableIterator<Object> iterator = this.iterator();
        while (iterator.hasNext()) {
            array[n] = iterator.next();
            ++n;
        }
        return n;
    }
    
    Object[] internalArray() {
        return null;
    }
    
    int internalArrayEnd() {
        throw new UnsupportedOperationException();
    }
    
    int internalArrayStart() {
        throw new UnsupportedOperationException();
    }
    
    abstract boolean isPartialView();
    
    @Override
    public abstract UnmodifiableIterator<E> iterator();
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final boolean removeAll(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final boolean retainAll(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final Object[] toArray() {
        return this.toArray(ImmutableCollection.EMPTY_ARRAY);
    }
    
    @CanIgnoreReturnValue
    @Override
    public final <T> T[] toArray(final T[] array) {
        Preconditions.checkNotNull(array);
        final int size = this.size();
        T[] array2;
        if (array.length < size) {
            final Object[] internalArray = this.internalArray();
            if (internalArray != null) {
                return Platform.copy(internalArray, this.internalArrayStart(), this.internalArrayEnd(), array);
            }
            array2 = ObjectArrays.newArray(array, size);
        }
        else {
            array2 = array;
            if (array.length > size) {
                array[size] = null;
                array2 = array;
            }
        }
        this.copyIntoArray(array2, 0);
        return array2;
    }
    
    Object writeReplace() {
        return new ImmutableList.SerializedForm(this.toArray());
    }
    
    abstract static class ArrayBasedBuilder<E> extends Builder<E>
    {
        Object[] contents;
        boolean forceCopy;
        int size;
        
        ArrayBasedBuilder(final int n) {
            CollectPreconditions.checkNonnegative(n, "initialCapacity");
            this.contents = new Object[n];
            this.size = 0;
        }
        
        private void getReadyToExpandTo(final int n) {
            final Object[] contents = this.contents;
            if (contents.length < n) {
                this.contents = Arrays.copyOf(contents, Builder.expandedCapacity(contents.length, n));
                this.forceCopy = false;
            }
            else if (this.forceCopy) {
                this.contents = contents.clone();
                this.forceCopy = false;
            }
        }
        
        @CanIgnoreReturnValue
        public ArrayBasedBuilder<E> add(final E e) {
            Preconditions.checkNotNull(e);
            this.getReadyToExpandTo(this.size + 1);
            this.contents[this.size++] = e;
            return this;
        }
        
        @CanIgnoreReturnValue
        public Builder<E> add(final E... array) {
            ObjectArrays.checkElementsNotNull((Object[])array);
            this.getReadyToExpandTo(this.size + array.length);
            System.arraycopy(array, 0, this.contents, this.size, array.length);
            this.size += array.length;
            return this;
        }
    }
    
    public abstract static class Builder<E>
    {
        Builder() {
        }
        
        static int expandedCapacity(int n, int n2) {
            if (n2 >= 0) {
                if ((n = n + (n >> 1) + 1) < n2) {
                    n = Integer.highestOneBit(n2 - 1) << 1;
                }
                if ((n2 = n) < 0) {
                    n2 = Integer.MAX_VALUE;
                }
                return n2;
            }
            throw new AssertionError((Object)"cannot store more than MAX_VALUE elements");
        }
    }
}
