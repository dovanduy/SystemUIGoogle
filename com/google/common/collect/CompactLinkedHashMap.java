// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Arrays;

class CompactLinkedHashMap<K, V> extends CompactHashMap<K, V>
{
    private final boolean accessOrder;
    private transient int firstEntry;
    private transient int lastEntry;
    transient long[] links;
    
    CompactLinkedHashMap() {
        this(3);
    }
    
    CompactLinkedHashMap(final int n) {
        this(n, 1.0f, false);
    }
    
    CompactLinkedHashMap(final int n, final float n2, final boolean accessOrder) {
        super(n, n2);
        this.accessOrder = accessOrder;
    }
    
    public static <K, V> CompactLinkedHashMap<K, V> createWithExpectedSize(final int n) {
        return new CompactLinkedHashMap<K, V>(n);
    }
    
    private int getPredecessor(final int n) {
        return (int)(this.links[n] >>> 32);
    }
    
    private void setPredecessor(final int n, final int n2) {
        final long[] links = this.links;
        links[n] = ((links[n] & 0xFFFFFFFFL) | (long)n2 << 32);
    }
    
    private void setSucceeds(final int lastEntry, final int firstEntry) {
        if (lastEntry == -2) {
            this.firstEntry = firstEntry;
        }
        else {
            this.setSuccessor(lastEntry, firstEntry);
        }
        if (firstEntry == -2) {
            this.lastEntry = lastEntry;
        }
        else {
            this.setPredecessor(firstEntry, lastEntry);
        }
    }
    
    private void setSuccessor(final int n, final int n2) {
        final long[] links = this.links;
        links[n] = ((links[n] & 0xFFFFFFFF00000000L) | ((long)n2 & 0xFFFFFFFFL));
    }
    
    @Override
    void accessEntry(final int n) {
        if (this.accessOrder) {
            this.setSucceeds(this.getPredecessor(n), this.getSuccessor(n));
            this.setSucceeds(this.lastEntry, n);
            this.setSucceeds(n, -2);
            ++super.modCount;
        }
    }
    
    @Override
    int adjustAfterRemove(final int n, final int n2) {
        int n3 = n;
        if (n >= this.size()) {
            n3 = n2;
        }
        return n3;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.firstEntry = -2;
        this.lastEntry = -2;
    }
    
    @Override
    int firstEntryIndex() {
        return this.firstEntry;
    }
    
    @Override
    int getSuccessor(final int n) {
        return (int)this.links[n];
    }
    
    @Override
    void init(final int n, final float n2) {
        super.init(n, n2);
        this.firstEntry = -2;
        this.lastEntry = -2;
        Arrays.fill(this.links = new long[n], -1L);
    }
    
    @Override
    void insertEntry(final int n, final K k, final V v, final int n2) {
        super.insertEntry(n, k, v, n2);
        this.setSucceeds(this.lastEntry, n);
        this.setSucceeds(n, -2);
    }
    
    @Override
    void moveLastEntry(final int n) {
        final int n2 = this.size() - 1;
        this.setSucceeds(this.getPredecessor(n), this.getSuccessor(n));
        if (n < n2) {
            this.setSucceeds(this.getPredecessor(n2), n);
            this.setSucceeds(n, this.getSuccessor(n2));
        }
        super.moveLastEntry(n);
    }
    
    @Override
    void resizeEntries(final int newLength) {
        super.resizeEntries(newLength);
        this.links = Arrays.copyOf(this.links, newLength);
    }
}
