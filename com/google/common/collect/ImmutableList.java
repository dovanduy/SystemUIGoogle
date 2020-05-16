// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.io.Serializable;
import java.util.ListIterator;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.RandomAccess;
import java.util.List;

public abstract class ImmutableList<E> extends ImmutableCollection<E> implements List<E>, RandomAccess
{
    private static final UnmodifiableListIterator<Object> EMPTY_ITR;
    
    static {
        EMPTY_ITR = new Itr<Object>(RegularImmutableList.EMPTY, 0);
    }
    
    ImmutableList() {
    }
    
    static <E> ImmutableList<E> asImmutableList(final Object[] array) {
        return asImmutableList(array, array.length);
    }
    
    static <E> ImmutableList<E> asImmutableList(final Object[] array, final int n) {
        if (n == 0) {
            return of();
        }
        return new RegularImmutableList<E>(array, n);
    }
    
    public static <E> Builder<E> builder() {
        return new Builder<E>();
    }
    
    private static <E> ImmutableList<E> construct(final Object... array) {
        ObjectArrays.checkElementsNotNull(array);
        return (ImmutableList<E>)asImmutableList(array);
    }
    
    public static <E> ImmutableList<E> copyOf(final E[] array) {
        List<E> list;
        if (array.length == 0) {
            list = (List<E>)of();
        }
        else {
            list = (List<E>)construct((Object[])array.clone());
        }
        return (ImmutableList<E>)list;
    }
    
    public static <E> ImmutableList<E> of() {
        return (ImmutableList<E>)RegularImmutableList.EMPTY;
    }
    
    public static <E> ImmutableList<E> of(final E e) {
        return construct(e);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws InvalidObjectException {
        throw new InvalidObjectException("Use SerializedForm");
    }
    
    @Deprecated
    @Override
    public final void add(final int n, final E e) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final boolean addAll(final int n, final Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.indexOf(o) >= 0;
    }
    
    @Override
    int copyIntoArray(final Object[] array, final int n) {
        final int size = this.size();
        for (int i = 0; i < size; ++i) {
            array[n + i] = this.get(i);
        }
        return n + size;
    }
    
    @Override
    public boolean equals(final Object o) {
        return Lists.equalsImpl(this, o);
    }
    
    @Override
    public int hashCode() {
        final int size = this.size();
        int n = 1;
        for (int i = 0; i < size; ++i) {
            n = n * 31 + this.get(i).hashCode();
        }
        return n;
    }
    
    @Override
    public int indexOf(final Object o) {
        int indexOfImpl;
        if (o == null) {
            indexOfImpl = -1;
        }
        else {
            indexOfImpl = Lists.indexOfImpl(this, o);
        }
        return indexOfImpl;
    }
    
    @Override
    public UnmodifiableIterator<E> iterator() {
        return this.listIterator();
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        int lastIndexOfImpl;
        if (o == null) {
            lastIndexOfImpl = -1;
        }
        else {
            lastIndexOfImpl = Lists.lastIndexOfImpl(this, o);
        }
        return lastIndexOfImpl;
    }
    
    @Override
    public UnmodifiableListIterator<E> listIterator() {
        return this.listIterator(0);
    }
    
    @Override
    public UnmodifiableListIterator<E> listIterator(final int n) {
        Preconditions.checkPositionIndex(n, this.size());
        if (this.isEmpty()) {
            return (UnmodifiableListIterator<E>)ImmutableList.EMPTY_ITR;
        }
        return new Itr<E>(this, n);
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final E remove(final int n) {
        throw new UnsupportedOperationException();
    }
    
    public ImmutableList<E> reverse() {
        ImmutableList list;
        if (this.size() <= 1) {
            list = this;
        }
        else {
            list = new ReverseImmutableList<E>(this);
        }
        return list;
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final E set(final int n, final E e) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ImmutableList<E> subList(final int n, final int n2) {
        Preconditions.checkPositionIndexes(n, n2, this.size());
        final int n3 = n2 - n;
        if (n3 == this.size()) {
            return this;
        }
        if (n3 == 0) {
            return of();
        }
        return this.subListUnchecked(n, n2);
    }
    
    ImmutableList<E> subListUnchecked(final int n, final int n2) {
        return new SubList(n, n2 - n);
    }
    
    @Override
    Object writeReplace() {
        return new SerializedForm(this.toArray());
    }
    
    public static final class Builder<E> extends ArrayBasedBuilder<E>
    {
        public Builder() {
            this(4);
        }
        
        Builder(final int n) {
            super(n);
        }
        
        @CanIgnoreReturnValue
        public Builder<E> add(final E e) {
            super.add(e);
            return this;
        }
        
        public ImmutableList<E> build() {
            super.forceCopy = true;
            return ImmutableList.asImmutableList(super.contents, super.size);
        }
    }
    
    static class Itr<E> extends AbstractIndexedListIterator<E>
    {
        private final ImmutableList<E> list;
        
        Itr(final ImmutableList<E> list, final int n) {
            super(list.size(), n);
            this.list = list;
        }
        
        @Override
        protected E get(final int n) {
            return this.list.get(n);
        }
    }
    
    private static class ReverseImmutableList<E> extends ImmutableList<E>
    {
        private final transient ImmutableList<E> forwardList;
        
        ReverseImmutableList(final ImmutableList<E> forwardList) {
            this.forwardList = forwardList;
        }
        
        private int reverseIndex(final int n) {
            return this.size() - 1 - n;
        }
        
        private int reversePosition(final int n) {
            return this.size() - n;
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.forwardList.contains(o);
        }
        
        @Override
        public E get(final int n) {
            Preconditions.checkElementIndex(n, this.size());
            return this.forwardList.get(this.reverseIndex(n));
        }
        
        @Override
        public int indexOf(final Object o) {
            final int lastIndex = this.forwardList.lastIndexOf(o);
            int reverseIndex;
            if (lastIndex >= 0) {
                reverseIndex = this.reverseIndex(lastIndex);
            }
            else {
                reverseIndex = -1;
            }
            return reverseIndex;
        }
        
        @Override
        boolean isPartialView() {
            return this.forwardList.isPartialView();
        }
        
        @Override
        public int lastIndexOf(final Object o) {
            final int index = this.forwardList.indexOf(o);
            int reverseIndex;
            if (index >= 0) {
                reverseIndex = this.reverseIndex(index);
            }
            else {
                reverseIndex = -1;
            }
            return reverseIndex;
        }
        
        @Override
        public ImmutableList<E> reverse() {
            return this.forwardList;
        }
        
        @Override
        public int size() {
            return this.forwardList.size();
        }
        
        @Override
        public ImmutableList<E> subList(final int n, final int n2) {
            Preconditions.checkPositionIndexes(n, n2, this.size());
            return this.forwardList.subList(this.reversePosition(n2), this.reversePosition(n)).reverse();
        }
    }
    
    static class SerializedForm implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final Object[] elements;
        
        SerializedForm(final Object[] elements) {
            this.elements = elements;
        }
        
        Object readResolve() {
            return ImmutableList.copyOf(this.elements);
        }
    }
    
    class SubList extends ImmutableList<E>
    {
        final transient int length;
        final transient int offset;
        
        SubList(final int offset, final int length) {
            this.offset = offset;
            this.length = length;
        }
        
        @Override
        public E get(final int n) {
            Preconditions.checkElementIndex(n, this.length);
            return ImmutableList.this.get(n + this.offset);
        }
        
        @Override
        Object[] internalArray() {
            return ImmutableList.this.internalArray();
        }
        
        @Override
        int internalArrayEnd() {
            return ImmutableList.this.internalArrayStart() + this.offset + this.length;
        }
        
        @Override
        int internalArrayStart() {
            return ImmutableList.this.internalArrayStart() + this.offset;
        }
        
        @Override
        boolean isPartialView() {
            return true;
        }
        
        @Override
        public int size() {
            return this.length;
        }
        
        @Override
        public ImmutableList<E> subList(final int n, final int n2) {
            Preconditions.checkPositionIndexes(n, n2, this.length);
            final ImmutableList this$0 = ImmutableList.this;
            final int offset = this.offset;
            return this$0.subList(n + offset, n2 + offset);
        }
    }
}
