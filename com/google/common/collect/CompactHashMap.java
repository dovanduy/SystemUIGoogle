// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.AbstractCollection;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.util.AbstractSet;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.io.ObjectOutputStream;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import com.google.common.base.Objects;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;
import java.util.AbstractMap;

class CompactHashMap<K, V> extends AbstractMap<K, V> implements Serializable
{
    transient long[] entries;
    private transient Set<Entry<K, V>> entrySetView;
    private transient Set<K> keySetView;
    transient Object[] keys;
    transient float loadFactor;
    transient int modCount;
    private transient int size;
    private transient int[] table;
    private transient int threshold;
    transient Object[] values;
    private transient Collection<V> valuesView;
    
    CompactHashMap() {
        this.init(3, 1.0f);
    }
    
    CompactHashMap(final int n) {
        this(n, 1.0f);
    }
    
    CompactHashMap(final int n, final float n2) {
        this.init(n, n2);
    }
    
    public static <K, V> CompactHashMap<K, V> createWithExpectedSize(final int n) {
        return new CompactHashMap<K, V>(n);
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
    
    private int indexOf(final Object o) {
        final int smearedHash = Hashing.smearedHash(o);
        long n;
        for (int i = this.table[this.hashTableMask() & smearedHash]; i != -1; i = getNext(n)) {
            n = this.entries[i];
            if (getHash(n) == smearedHash && Objects.equal(o, this.keys[i])) {
                return i;
            }
        }
        return -1;
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
            this.put(objectInputStream.readObject(), objectInputStream.readObject());
        }
    }
    
    private V remove(Object o, final int n) {
        final int n2 = this.hashTableMask() & n;
        int n3 = this.table[n2];
        if (n3 == -1) {
            return null;
        }
        int n4 = -1;
        while (getHash(this.entries[n3]) != n || !Objects.equal(o, this.keys[n3])) {
            final int next = getNext(this.entries[n3]);
            if (next == -1) {
                return null;
            }
            n4 = n3;
            n3 = next;
        }
        o = this.values[n3];
        if (n4 == -1) {
            this.table[n2] = getNext(this.entries[n3]);
        }
        else {
            final long[] entries = this.entries;
            entries[n4] = swapNext(entries[n4], getNext(entries[n3]));
        }
        this.moveLastEntry(n3);
        --this.size;
        ++this.modCount;
        return (V)o;
    }
    
    @CanIgnoreReturnValue
    private V removeEntry(final int n) {
        return this.remove(this.keys[n], getHash(this.entries[n]));
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
        for (int i = 0; i < this.size; ++i) {
            objectOutputStream.writeObject(this.keys[i]);
            objectOutputStream.writeObject(this.values[i]);
        }
    }
    
    void accessEntry(final int n) {
    }
    
    int adjustAfterRemove(final int n, final int n2) {
        return n - 1;
    }
    
    @Override
    public void clear() {
        ++this.modCount;
        Arrays.fill(this.keys, 0, this.size, null);
        Arrays.fill(this.values, 0, this.size, null);
        Arrays.fill(this.table, -1);
        Arrays.fill(this.entries, -1L);
        this.size = 0;
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return this.indexOf(o) != -1;
    }
    
    @Override
    public boolean containsValue(final Object o) {
        for (int i = 0; i < this.size; ++i) {
            if (Objects.equal(o, this.values[i])) {
                return true;
            }
        }
        return false;
    }
    
    Set<Entry<K, V>> createEntrySet() {
        return new EntrySetView();
    }
    
    Set<K> createKeySet() {
        return new KeySetView();
    }
    
    Collection<V> createValues() {
        return new ValuesView();
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entrySetView;
        if ((entrySetView = this.entrySetView) == null) {
            entrySetView = this.createEntrySet();
            this.entrySetView = entrySetView;
        }
        return entrySetView;
    }
    
    Iterator<Entry<K, V>> entrySetIterator() {
        return new Itr<Entry<K, V>>() {
            Entry<K, V> getOutput(final int n) {
                return new MapEntry(n);
            }
        };
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
    
    @Override
    public V get(Object o) {
        final int index = this.indexOf(o);
        this.accessEntry(index);
        if (index == -1) {
            o = null;
        }
        else {
            o = this.values[index];
        }
        return (V)o;
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
        this.keys = new Object[n];
        this.values = new Object[n];
        this.entries = newEntries(n);
        this.threshold = Math.max(1, (int)(closedTableSize * loadFactor));
    }
    
    void insertEntry(final int n, final K k, final V v, final int n2) {
        this.entries[n] = ((long)n2 << 32 | 0xFFFFFFFFL);
        this.keys[n] = k;
        this.values[n] = v;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public Set<K> keySet() {
        Set<K> keySetView;
        if ((keySetView = this.keySetView) == null) {
            keySetView = this.createKeySet();
            this.keySetView = keySetView;
        }
        return keySetView;
    }
    
    Iterator<K> keySetIterator() {
        return new Itr<K>() {
            @Override
            K getOutput(final int n) {
                return (K)CompactHashMap.this.keys[n];
            }
        };
    }
    
    void moveLastEntry(final int n) {
        final int n2 = this.size() - 1;
        if (n < n2) {
            final Object[] keys = this.keys;
            keys[n] = keys[n2];
            final Object[] values = this.values;
            values[n] = values[n2];
            values[n2] = (keys[n2] = null);
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
            this.keys[n] = null;
            this.values[n] = null;
            this.entries[n] = -1L;
        }
    }
    
    @CanIgnoreReturnValue
    @Override
    public V put(final K k, final V v) {
        final long[] entries = this.entries;
        final Object[] keys = this.keys;
        final Object[] values = this.values;
        final int smearedHash = Hashing.smearedHash(k);
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
                if (getHash(n3) == smearedHash && Objects.equal(k, keys[n2])) {
                    final Object o = values[n2];
                    values[n2] = v;
                    this.accessEntry(n2);
                    return (V)o;
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
            this.insertEntry(size, k, v, smearedHash);
            this.size = size2;
            if (size >= this.threshold) {
                this.resizeTable(this.table.length * 2);
            }
            ++this.modCount;
            return null;
        }
        throw new IllegalStateException("Cannot contain more than Integer.MAX_VALUE elements!");
    }
    
    @CanIgnoreReturnValue
    @Override
    public V remove(final Object o) {
        return this.remove(o, Hashing.smearedHash(o));
    }
    
    void resizeEntries(final int n) {
        this.keys = Arrays.copyOf(this.keys, n);
        this.values = Arrays.copyOf(this.values, n);
        final long[] entries = this.entries;
        final int length = entries.length;
        final long[] copy = Arrays.copyOf(entries, n);
        if (n > length) {
            Arrays.fill(copy, length, n, -1L);
        }
        this.entries = copy;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public Collection<V> values() {
        Collection<V> valuesView;
        if ((valuesView = this.valuesView) == null) {
            valuesView = this.createValues();
            this.valuesView = valuesView;
        }
        return valuesView;
    }
    
    Iterator<V> valuesIterator() {
        return new Itr<V>() {
            @Override
            V getOutput(final int n) {
                return (V)CompactHashMap.this.values[n];
            }
        };
    }
    
    class EntrySetView extends AbstractSet<Entry<K, V>>
    {
        @Override
        public void clear() {
            CompactHashMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            final boolean b = o instanceof Entry;
            boolean b3;
            final boolean b2 = b3 = false;
            if (b) {
                final Entry entry = (Entry)o;
                final int access$200 = CompactHashMap.this.indexOf(entry.getKey());
                b3 = b2;
                if (access$200 != -1) {
                    b3 = b2;
                    if (Objects.equal(CompactHashMap.this.values[access$200], entry.getValue())) {
                        b3 = true;
                    }
                }
            }
            return b3;
        }
        
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return CompactHashMap.this.entrySetIterator();
        }
        
        @Override
        public boolean remove(final Object o) {
            if (o instanceof Entry) {
                final Entry entry = (Entry)o;
                final int access$200 = CompactHashMap.this.indexOf(entry.getKey());
                if (access$200 != -1 && Objects.equal(CompactHashMap.this.values[access$200], entry.getValue())) {
                    CompactHashMap.this.removeEntry(access$200);
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public int size() {
            return CompactHashMap.this.size;
        }
    }
    
    private abstract class Itr<T> implements Iterator<T>
    {
        int currentIndex;
        int expectedModCount;
        int indexToRemove;
        
        private Itr() {
            CompactHashMap.this = CompactHashMap.this;
            this.expectedModCount = CompactHashMap.this.modCount;
            this.currentIndex = CompactHashMap.this.firstEntryIndex();
            this.indexToRemove = -1;
        }
        
        private void checkForConcurrentModification() {
            if (CompactHashMap.this.modCount == this.expectedModCount) {
                return;
            }
            throw new ConcurrentModificationException();
        }
        
        abstract T getOutput(final int p0);
        
        @Override
        public boolean hasNext() {
            return this.currentIndex >= 0;
        }
        
        @Override
        public T next() {
            this.checkForConcurrentModification();
            if (this.hasNext()) {
                final int currentIndex = this.currentIndex;
                this.indexToRemove = currentIndex;
                final T output = this.getOutput(currentIndex);
                this.currentIndex = CompactHashMap.this.getSuccessor(this.currentIndex);
                return output;
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            this.checkForConcurrentModification();
            CollectPreconditions.checkRemove(this.indexToRemove >= 0);
            ++this.expectedModCount;
            CompactHashMap.this.removeEntry(this.indexToRemove);
            this.currentIndex = CompactHashMap.this.adjustAfterRemove(this.currentIndex, this.indexToRemove);
            this.indexToRemove = -1;
        }
    }
    
    class KeySetView extends AbstractSet<K>
    {
        @Override
        public void clear() {
            CompactHashMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return CompactHashMap.this.containsKey(o);
        }
        
        @Override
        public Iterator<K> iterator() {
            return CompactHashMap.this.keySetIterator();
        }
        
        @Override
        public boolean remove(final Object o) {
            final int access$200 = CompactHashMap.this.indexOf(o);
            if (access$200 == -1) {
                return false;
            }
            CompactHashMap.this.removeEntry(access$200);
            return true;
        }
        
        @Override
        public int size() {
            return CompactHashMap.this.size;
        }
    }
    
    final class MapEntry extends AbstractMapEntry<K, V>
    {
        private final K key;
        private int lastKnownIndex;
        
        MapEntry(final int lastKnownIndex) {
            this.key = (K)CompactHashMap.this.keys[lastKnownIndex];
            this.lastKnownIndex = lastKnownIndex;
        }
        
        private void updateLastKnownIndex() {
            final int lastKnownIndex = this.lastKnownIndex;
            if (lastKnownIndex == -1 || lastKnownIndex >= CompactHashMap.this.size() || !Objects.equal(this.key, CompactHashMap.this.keys[this.lastKnownIndex])) {
                this.lastKnownIndex = CompactHashMap.this.indexOf(this.key);
            }
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public V getValue() {
            this.updateLastKnownIndex();
            final int lastKnownIndex = this.lastKnownIndex;
            Object o;
            if (lastKnownIndex == -1) {
                o = null;
            }
            else {
                o = CompactHashMap.this.values[lastKnownIndex];
            }
            return (V)o;
        }
        
        @Override
        public V setValue(final V v) {
            this.updateLastKnownIndex();
            final int lastKnownIndex = this.lastKnownIndex;
            if (lastKnownIndex == -1) {
                CompactHashMap.this.put(this.key, v);
                return null;
            }
            final Object[] values = CompactHashMap.this.values;
            final Object o = values[lastKnownIndex];
            values[lastKnownIndex] = v;
            return (V)o;
        }
    }
    
    class ValuesView extends AbstractCollection<V>
    {
        @Override
        public void clear() {
            CompactHashMap.this.clear();
        }
        
        @Override
        public Iterator<V> iterator() {
            return CompactHashMap.this.valuesIterator();
        }
        
        @Override
        public int size() {
            return CompactHashMap.this.size;
        }
    }
}
