// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.io.Serializable;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import java.util.Arrays;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.Set;

public abstract class ImmutableSet<E> extends ImmutableCollection<E> implements Set<E>
{
    @LazyInit
    private transient ImmutableList<E> asList;
    
    ImmutableSet() {
    }
    
    public static <E> Builder<E> builder() {
        return new Builder<E>();
    }
    
    static int chooseTableSize(int a) {
        final int max = Math.max(a, 2);
        boolean b = true;
        if (max < 751619276) {
            for (a = Integer.highestOneBit(max - 1) << 1; a * 0.7 < max; a <<= 1) {}
            return a;
        }
        if (max >= 1073741824) {
            b = false;
        }
        Preconditions.checkArgument(b, "collection too large");
        return 1073741824;
    }
    
    private static <E> ImmutableSet<E> construct(final int toIndex, final Object... array) {
        if (toIndex == 0) {
            return of();
        }
        if (toIndex == 1) {
            return of(array[0]);
        }
        final int chooseTableSize = chooseTableSize(toIndex);
        final Object[] array2 = new Object[chooseTableSize];
        final int n = chooseTableSize - 1;
        final int n2 = 0;
        int n4;
        int n3 = n4 = n2;
        for (int i = n2; i < toIndex; ++i) {
            final Object obj = array[i];
            ObjectArrays.checkElementNotNull(obj, i);
            final int hashCode = obj.hashCode();
            int smear = Hashing.smear(hashCode);
            while (true) {
                final int n5 = smear & n;
                final Object o = array2[n5];
                if (o == null) {
                    array2[n5] = (array[n3] = obj);
                    n4 += hashCode;
                    ++n3;
                    break;
                }
                if (o.equals(obj)) {
                    break;
                }
                ++smear;
            }
        }
        Arrays.fill(array, n3, toIndex, null);
        if (n3 == 1) {
            return new SingletonImmutableSet<E>(array[0], n4);
        }
        if (chooseTableSize(n3) < chooseTableSize / 2) {
            return (ImmutableSet<E>)construct(n3, array);
        }
        Object[] copy = array;
        if (shouldTrim(n3, array.length)) {
            copy = Arrays.copyOf(array, n3);
        }
        return new RegularImmutableSet<E>(copy, n4, array2, n, n3);
    }
    
    public static <E> ImmutableSet<E> copyOf(final E[] array) {
        final int length = array.length;
        if (length == 0) {
            return of();
        }
        if (length != 1) {
            return construct(array.length, (Object[])array.clone());
        }
        return of(array[0]);
    }
    
    public static <E> ImmutableSet<E> of() {
        return (ImmutableSet<E>)RegularImmutableSet.EMPTY;
    }
    
    public static <E> ImmutableSet<E> of(final E e) {
        return new SingletonImmutableSet<E>(e);
    }
    
    private static boolean shouldTrim(final int n, final int n2) {
        return n < (n2 >> 1) + (n2 >> 2);
    }
    
    public ImmutableList<E> asList() {
        ImmutableList<E> asList;
        if ((asList = this.asList) == null) {
            asList = this.createAsList();
            this.asList = asList;
        }
        return asList;
    }
    
    ImmutableList<E> createAsList() {
        return ImmutableList.asImmutableList(this.toArray());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || ((!(o instanceof ImmutableSet) || !this.isHashCodeFast() || !((ImmutableSet)o).isHashCodeFast() || this.hashCode() == o.hashCode()) && Sets.equalsImpl(this, o));
    }
    
    @Override
    public int hashCode() {
        return Sets.hashCodeImpl(this);
    }
    
    boolean isHashCodeFast() {
        return false;
    }
    
    @Override
    Object writeReplace() {
        return new SerializedForm(this.toArray());
    }
    
    public static class Builder<E> extends ArrayBasedBuilder<E>
    {
        private int hashCode;
        Object[] hashTable;
        
        public Builder() {
            super(4);
        }
        
        private void addDeduping(final E obj) {
            final int length = this.hashTable.length;
            final int hashCode = obj.hashCode();
            int smear = Hashing.smear(hashCode);
            while (true) {
                smear &= length - 1;
                final Object[] hashTable = this.hashTable;
                final Object o = hashTable[smear];
                if (o == null) {
                    hashTable[smear] = obj;
                    this.hashCode += hashCode;
                    super.add(obj);
                    return;
                }
                if (o.equals(obj)) {
                    return;
                }
                ++smear;
            }
        }
        
        @CanIgnoreReturnValue
        public Builder<E> add(final E e) {
            Preconditions.checkNotNull(e);
            if (this.hashTable != null && ImmutableSet.chooseTableSize(super.size) <= this.hashTable.length) {
                this.addDeduping(e);
                return this;
            }
            this.hashTable = null;
            super.add(e);
            return this;
        }
        
        @CanIgnoreReturnValue
        public Builder<E> add(final E... array) {
            if (this.hashTable != null) {
                for (int length = array.length, i = 0; i < length; ++i) {
                    this.add(array[i]);
                }
            }
            else {
                super.add(array);
            }
            return this;
        }
        
        public ImmutableSet<E> build() {
            final int size = super.size;
            if (size == 0) {
                return ImmutableSet.of();
            }
            if (size != 1) {
                ImmutableSet<Object> access$100;
                if (this.hashTable != null && ImmutableSet.chooseTableSize(size) == this.hashTable.length) {
                    Object[] array;
                    if (shouldTrim(super.size, super.contents.length)) {
                        array = Arrays.copyOf(super.contents, super.size);
                    }
                    else {
                        array = super.contents;
                    }
                    final int hashCode = this.hashCode;
                    final Object[] hashTable = this.hashTable;
                    access$100 = new RegularImmutableSet<Object>(array, hashCode, hashTable, hashTable.length - 1, super.size);
                }
                else {
                    access$100 = construct(super.size, super.contents);
                    super.size = access$100.size();
                }
                super.forceCopy = true;
                this.hashTable = null;
                return (ImmutableSet<E>)access$100;
            }
            return ImmutableSet.of(super.contents[0]);
        }
    }
    
    private static class SerializedForm implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final Object[] elements;
        
        SerializedForm(final Object[] elements) {
            this.elements = elements;
        }
        
        Object readResolve() {
            return ImmutableSet.copyOf(this.elements);
        }
    }
}
