// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Collection;
import java.util.Arrays;

class CompactLinkedHashSet<E> extends CompactHashSet<E>
{
    private transient int firstEntry;
    private transient int lastEntry;
    private transient int[] predecessor;
    private transient int[] successor;
    
    CompactLinkedHashSet() {
    }
    
    CompactLinkedHashSet(final int n) {
        super(n);
    }
    
    public static <E> CompactLinkedHashSet<E> createWithExpectedSize(final int n) {
        return new CompactLinkedHashSet<E>(n);
    }
    
    private void succeeds(final int lastEntry, final int firstEntry) {
        if (lastEntry == -2) {
            this.firstEntry = firstEntry;
        }
        else {
            this.successor[lastEntry] = firstEntry;
        }
        if (firstEntry == -2) {
            this.lastEntry = lastEntry;
        }
        else {
            this.predecessor[firstEntry] = lastEntry;
        }
    }
    
    @Override
    int adjustAfterRemove(final int n, final int n2) {
        int n3 = n;
        if (n == this.size()) {
            n3 = n2;
        }
        return n3;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.firstEntry = -2;
        this.lastEntry = -2;
        Arrays.fill(this.predecessor, -1);
        Arrays.fill(this.successor, -1);
    }
    
    @Override
    int firstEntryIndex() {
        return this.firstEntry;
    }
    
    @Override
    int getSuccessor(final int n) {
        return this.successor[n];
    }
    
    @Override
    void init(final int n, final float n2) {
        super.init(n, n2);
        final int[] array = new int[n];
        this.predecessor = array;
        this.successor = new int[n];
        Arrays.fill(array, -1);
        Arrays.fill(this.successor, -1);
        this.firstEntry = -2;
        this.lastEntry = -2;
    }
    
    @Override
    void insertEntry(final int n, final E e, final int n2) {
        super.insertEntry(n, e, n2);
        this.succeeds(this.lastEntry, n);
        this.succeeds(n, -2);
    }
    
    @Override
    void moveEntry(final int n) {
        final int n2 = this.size() - 1;
        super.moveEntry(n);
        this.succeeds(this.predecessor[n], this.successor[n]);
        if (n2 != n) {
            this.succeeds(this.predecessor[n2], n);
            this.succeeds(n, this.successor[n2]);
        }
        this.predecessor[n2] = -1;
        this.successor[n2] = -1;
    }
    
    @Override
    void resizeEntries(final int n) {
        super.resizeEntries(n);
        final int[] predecessor = this.predecessor;
        final int length = predecessor.length;
        this.predecessor = Arrays.copyOf(predecessor, n);
        this.successor = Arrays.copyOf(this.successor, n);
        if (length < n) {
            Arrays.fill(this.predecessor, length, n, -1);
            Arrays.fill(this.successor, length, n, -1);
        }
    }
    
    @Override
    public Object[] toArray() {
        return ObjectArrays.toArrayImpl(this);
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        return ObjectArrays.toArrayImpl(this, array);
    }
}
