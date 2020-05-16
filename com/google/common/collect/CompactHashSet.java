// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.io.ObjectOutputStream;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.common.base.Objects;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.io.Serializable;
import java.util.AbstractSet;

class CompactHashSet<E> extends AbstractSet<E> implements Serializable
{
    transient Object[] elements;
    private transient long[] entries;
    transient float loadFactor;
    transient int modCount;
    private transient int size;
    private transient int[] table;
    private transient int threshold;
    
    CompactHashSet() {
        this.init(3, 1.0f);
    }
    
    CompactHashSet(final int n) {
        this.init(n, 1.0f);
    }
    
    private static int getHash(final long n) {
        return (int)(n >>> 32);
    }
    
    private static int getNext(final long n) {
        return (int)n;
    }
    
    private int hashTableMask() {
        return this.table.length - 1;
    }
    
    private static long[] newEntries(final int n) {
        final long[] a = new long[n];
        Arrays.fill(a, -1L);
        return a;
    }
    
    private static int[] newTable(final int n) {
        final int[] a = new int[n];
        Arrays.fill(a, -1);
        return a;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.init(3, 1.0f);
        int int1 = objectInputStream.readInt();
        while (--int1 >= 0) {
            this.add(objectInputStream.readObject());
        }
    }
    
    @CanIgnoreReturnValue
    private boolean remove(final Object o, final int n) {
        final int n2 = this.hashTableMask() & n;
        int n3 = this.table[n2];
        if (n3 == -1) {
            return false;
        }
        int n4 = -1;
        while (getHash(this.entries[n3]) != n || !Objects.equal(o, this.elements[n3])) {
            final int next = getNext(this.entries[n3]);
            if (next == -1) {
                return false;
            }
            n4 = n3;
            n3 = next;
        }
        if (n4 == -1) {
            this.table[n2] = getNext(this.entries[n3]);
        }
        else {
            final long[] entries = this.entries;
            entries[n4] = swapNext(entries[n4], getNext(entries[n3]));
        }
        this.moveEntry(n3);
        --this.size;
        ++this.modCount;
        return true;
    }
    
    private void resizeMeMaybe(int n) {
        final int length = this.entries.length;
        if (n > length) {
            if ((n = Math.max(1, length >>> 1) + length) < 0) {
                n = Integer.MAX_VALUE;
            }
            if (n != length) {
                this.resizeEntries(n);
            }
        }
    }
    
    private void resizeTable(int i) {
        if (this.table.length >= 1073741824) {
            this.threshold = Integer.MAX_VALUE;
            return;
        }
        final int n = (int)(i * this.loadFactor);
        final int[] table = newTable(i);
        final long[] entries = this.entries;
        final int length = table.length;
        int hash;
        int n2;
        for (i = 0; i < this.size; ++i) {
            hash = getHash(entries[i]);
            n2 = (hash & length - 1);
            entries[table[n2] = i] = ((long)hash << 32 | ((long)table[n2] & 0xFFFFFFFFL));
        }
        this.threshold = n + 1;
        this.table = table;
    }
    
    private static long swapNext(final long n, final int n2) {
        return (n & 0xFFFFFFFF00000000L) | ((long)n2 & 0xFFFFFFFFL);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.size);
        final Iterator<Object> iterator = this.iterator();
        while (iterator.hasNext()) {
            objectOutputStream.writeObject(iterator.next());
        }
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean add(final E e) {
        final long[] entries = this.entries;
        final Object[] elements = this.elements;
        final int smearedHash = Hashing.smearedHash(e);
        final int n = this.hashTableMask() & smearedHash;
        final int size = this.size;
        final int[] table = this.table;
        int n2;
        if ((n2 = table[n]) == -1) {
            table[n] = size;
        }
        else {
            while (true) {
                final long n3 = entries[n2];
                if (getHash(n3) == smearedHash && Objects.equal(e, elements[n2])) {
                    return false;
                }
                final int next = getNext(n3);
                if (next == -1) {
                    entries[n2] = swapNext(n3, size);
                    break;
                }
                n2 = next;
            }
        }
        if (size != Integer.MAX_VALUE) {
            final int size2 = size + 1;
            this.resizeMeMaybe(size2);
            this.insertEntry(size, e, smearedHash);
            this.size = size2;
            if (size >= this.threshold) {
                this.resizeTable(this.table.length * 2);
            }
            ++this.modCount;
            return true;
        }
        throw new IllegalStateException("Cannot contain more than Integer.MAX_VALUE elements!");
    }
    
    int adjustAfterRemove(final int n, final int n2) {
        return n - 1;
    }
    
    @Override
    public void clear() {
        ++this.modCount;
        Arrays.fill(this.elements, 0, this.size, null);
        Arrays.fill(this.table, -1);
        Arrays.fill(this.entries, -1L);
        this.size = 0;
    }
    
    @Override
    public boolean contains(final Object o) {
        final int smearedHash = Hashing.smearedHash(o);
        long n;
        for (int i = this.table[this.hashTableMask() & smearedHash]; i != -1; i = getNext(n)) {
            n = this.entries[i];
            if (getHash(n) == smearedHash && Objects.equal(o, this.elements[i])) {
                return true;
            }
        }
        return false;
    }
    
    int firstEntryIndex() {
        int n;
        if (this.isEmpty()) {
            n = -1;
        }
        else {
            n = 0;
        }
        return n;
    }
    
    int getSuccessor(int n) {
        if (++n >= this.size) {
            n = -1;
        }
        return n;
    }
    
    void init(final int n, final float loadFactor) {
        final boolean b = false;
        Preconditions.checkArgument(n >= 0, "Initial capacity must be non-negative");
        boolean b2 = b;
        if (loadFactor > 0.0f) {
            b2 = true;
        }
        Preconditions.checkArgument(b2, "Illegal load factor");
        final int closedTableSize = Hashing.closedTableSize(n, loadFactor);
        this.table = newTable(closedTableSize);
        this.loadFactor = loadFactor;
        this.elements = new Object[n];
        this.entries = newEntries(n);
        this.threshold = Math.max(1, (int)(closedTableSize * loadFactor));
    }
    
    void insertEntry(final int n, final E e, final int n2) {
        this.entries[n] = ((long)n2 << 32 | 0xFFFFFFFFL);
        this.elements[n] = e;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int expectedModCount;
            int index;
            int indexToRemove;
            
            {
                CompactHashSet.this = CompactHashSet.this;
                this.expectedModCount = CompactHashSet.this.modCount;
                this.index = CompactHashSet.this.firstEntryIndex();
                this.indexToRemove = -1;
            }
            
            private void checkForConcurrentModification() {
                if (CompactHashSet.this.modCount == this.expectedModCount) {
                    return;
                }
                throw new ConcurrentModificationException();
            }
            
            @Override
            public boolean hasNext() {
                return this.index >= 0;
            }
            
            @Override
            public E next() {
                this.checkForConcurrentModification();
                if (this.hasNext()) {
                    final int index = this.index;
                    this.indexToRemove = index;
                    final CompactHashSet this$0 = CompactHashSet.this;
                    final Object o = this$0.elements[index];
                    this.index = this$0.getSuccessor(index);
                    return (E)o;
                }
                throw new NoSuchElementException();
            }
            
            @Override
            public void remove() {
                this.checkForConcurrentModification();
                CollectPreconditions.checkRemove(this.indexToRemove >= 0);
                ++this.expectedModCount;
                final CompactHashSet this$0 = CompactHashSet.this;
                this$0.remove(this$0.elements[this.indexToRemove], getHash(this$0.entries[this.indexToRemove]));
                this.index = CompactHashSet.this.adjustAfterRemove(this.index, this.indexToRemove);
                this.indexToRemove = -1;
            }
        };
    }
    
    void moveEntry(final int n) {
        final int n2 = this.size() - 1;
        if (n < n2) {
            final Object[] elements = this.elements;
            elements[n] = elements[n2];
            elements[n2] = null;
            final long[] entries = this.entries;
            final long n3 = entries[n2];
            entries[n] = n3;
            entries[n2] = -1L;
            final int n4 = getHash(n3) & this.hashTableMask();
            final int[] table = this.table;
            int n5;
            if ((n5 = table[n4]) == n2) {
                table[n4] = n;
            }
            else {
                long n6;
                while (true) {
                    n6 = this.entries[n5];
                    final int next = getNext(n6);
                    if (next == n2) {
                        break;
                    }
                    n5 = next;
                }
                this.entries[n5] = swapNext(n6, n);
            }
        }
        else {
            this.elements[n] = null;
            this.entries[n] = -1L;
        }
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean remove(final Object o) {
        return this.remove(o, Hashing.smearedHash(o));
    }
    
    void resizeEntries(final int toIndex) {
        this.elements = Arrays.copyOf(this.elements, toIndex);
        final long[] entries = this.entries;
        final int length = entries.length;
        final long[] copy = Arrays.copyOf(entries, toIndex);
        if (toIndex > length) {
            Arrays.fill(copy, length, toIndex, -1L);
        }
        this.entries = copy;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public Object[] toArray() {
        return Arrays.copyOf(this.elements, this.size);
    }
    
    @CanIgnoreReturnValue
    @Override
    public <T> T[] toArray(final T[] array) {
        return ObjectArrays.toArrayImpl(this.elements, 0, this.size, array);
    }
}
