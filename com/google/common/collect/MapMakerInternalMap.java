// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.AbstractCollection;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.lang.ref.Reference;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.AbstractSet;
import java.util.NoSuchElementException;
import java.lang.ref.WeakReference;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.common.base.Preconditions;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.Iterator;
import java.util.ArrayList;
import java.lang.ref.ReferenceQueue;
import java.util.Collection;
import com.google.common.base.Equivalence;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;
import java.util.AbstractMap;

class MapMakerInternalMap<K, V, E extends InternalEntry<K, V, E>, S extends Segment<K, V, E, S>> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable
{
    static final WeakValueReference<Object, Object, DummyInternalEntry> UNSET_WEAK_VALUE_REFERENCE;
    private static final long serialVersionUID = 5L;
    final int concurrencyLevel;
    final transient InternalEntryHelper<K, V, E, S> entryHelper;
    transient Set<Entry<K, V>> entrySet;
    final Equivalence<Object> keyEquivalence;
    transient Set<K> keySet;
    final transient int segmentMask;
    final transient int segmentShift;
    final transient Segment<K, V, E, S>[] segments;
    transient Collection<V> values;
    
    static {
        UNSET_WEAK_VALUE_REFERENCE = (WeakValueReference)new WeakValueReference<Object, Object, DummyInternalEntry>() {
            @Override
            public void clear() {
            }
            
            public WeakValueReference<Object, Object, DummyInternalEntry> copyFor(final ReferenceQueue<Object> referenceQueue, final DummyInternalEntry dummyInternalEntry) {
                return this;
            }
            
            @Override
            public Object get() {
                return null;
            }
            
            public DummyInternalEntry getEntry() {
                return null;
            }
        };
    }
    
    private MapMakerInternalMap(final MapMaker mapMaker, final InternalEntryHelper<K, V, E, S> entryHelper) {
        this.concurrencyLevel = Math.min(mapMaker.getConcurrencyLevel(), 65536);
        this.keyEquivalence = mapMaker.getKeyEquivalence();
        this.entryHelper = entryHelper;
        final int min = Math.min(mapMaker.getInitialCapacity(), 1073741824);
        final int n = 0;
        final int n2 = 1;
        int n3 = 0;
        int i;
        for (i = 1; i < this.concurrencyLevel; i <<= 1) {
            ++n3;
        }
        this.segmentShift = 32 - n3;
        this.segmentMask = i - 1;
        this.segments = this.newSegmentArray(i);
        final int n4 = min / i;
        int n5 = n2;
        int n6 = n4;
        if (i * n4 < min) {
            n6 = n4 + 1;
            n5 = n2;
        }
        int n7;
        while (true) {
            n7 = n;
            if (n5 >= n6) {
                break;
            }
            n5 <<= 1;
        }
        while (true) {
            final Segment<K, V, E, S>[] segments = this.segments;
            if (n7 >= segments.length) {
                break;
            }
            segments[n7] = this.createSegment(n5, -1);
            ++n7;
        }
    }
    
    static <K, V> MapMakerInternalMap<K, V, ? extends InternalEntry<K, V, ?>, ?> create(final MapMaker mapMaker) {
        if (mapMaker.getKeyStrength() == Strength.STRONG && mapMaker.getValueStrength() == Strength.STRONG) {
            return new MapMakerInternalMap<K, V, InternalEntry<K, V, ?>, Object>(mapMaker, (InternalEntryHelper<K, V, ? extends InternalEntry<K, V, ?>, ?>)StrongKeyStrongValueEntry.Helper.instance());
        }
        if (mapMaker.getKeyStrength() == Strength.STRONG && mapMaker.getValueStrength() == Strength.WEAK) {
            return new MapMakerInternalMap<K, V, InternalEntry<K, V, ?>, Object>(mapMaker, (InternalEntryHelper<K, V, ? extends InternalEntry<K, V, ?>, ?>)StrongKeyWeakValueEntry.Helper.instance());
        }
        if (mapMaker.getKeyStrength() == Strength.WEAK && mapMaker.getValueStrength() == Strength.STRONG) {
            return new MapMakerInternalMap<K, V, InternalEntry<K, V, ?>, Object>(mapMaker, (InternalEntryHelper<K, V, ? extends InternalEntry<K, V, ?>, ?>)WeakKeyStrongValueEntry.Helper.instance());
        }
        if (mapMaker.getKeyStrength() == Strength.WEAK && mapMaker.getValueStrength() == Strength.WEAK) {
            return new MapMakerInternalMap<K, V, InternalEntry<K, V, ?>, Object>(mapMaker, (InternalEntryHelper<K, V, ? extends InternalEntry<K, V, ?>, ?>)WeakKeyWeakValueEntry.Helper.instance());
        }
        throw new AssertionError();
    }
    
    static int rehash(int n) {
        n += (n << 15 ^ 0xFFFFCD7D);
        n ^= n >>> 10;
        n += n << 3;
        n ^= n >>> 6;
        n += (n << 2) + (n << 14);
        return n ^ n >>> 16;
    }
    
    private static <E> ArrayList<E> toArrayList(final Collection<E> collection) {
        final ArrayList<Object> list = new ArrayList<Object>(collection.size());
        Iterators.addAll(list, collection.iterator());
        return (ArrayList<E>)list;
    }
    
    static <K, V, E extends InternalEntry<K, V, E>> WeakValueReference<K, V, E> unsetWeakValueReference() {
        return (WeakValueReference<K, V, E>)MapMakerInternalMap.UNSET_WEAK_VALUE_REFERENCE;
    }
    
    @Override
    public void clear() {
        final Segment<K, V, E, S>[] segments = this.segments;
        for (int length = segments.length, i = 0; i < length; ++i) {
            segments[i].clear();
        }
    }
    
    @Override
    public boolean containsKey(final Object o) {
        if (o == null) {
            return false;
        }
        final int hash = this.hash(o);
        return this.segmentFor(hash).containsKey(o, hash);
    }
    
    @Override
    public boolean containsValue(final Object o) {
        if (o == null) {
            return false;
        }
        final Segment<K, V, E, S>[] segments = this.segments;
        long n = -1L;
        long n2;
        for (int i = 0; i < 3; ++i, n = n2) {
            n2 = 0L;
            for (final Segment<K, V, E, S> segment : segments) {
                final int count = segment.count;
                final AtomicReferenceArray<E> table = segment.table;
                for (int k = 0; k < table.length(); ++k) {
                    for (InternalEntry<K, V, E> next = table.get(k); next != null; next = next.getNext()) {
                        final V liveValue = segment.getLiveValue((E)next);
                        if (liveValue != null && this.valueEquivalence().equivalent(o, liveValue)) {
                            return true;
                        }
                    }
                }
                n2 += segment.modCount;
            }
            if (n2 == n) {
                return false;
            }
        }
        return false;
    }
    
    E copyEntry(final E e, final E e2) {
        return this.segmentFor(e.getHash()).copyEntry(e, e2);
    }
    
    Segment<K, V, E, S> createSegment(final int n, final int n2) {
        return this.entryHelper.newSegment(this, n, n2);
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entrySet = this.entrySet;
        if (entrySet == null) {
            entrySet = new EntrySet();
            this.entrySet = entrySet;
        }
        return entrySet;
    }
    
    @Override
    public V get(final Object o) {
        if (o == null) {
            return null;
        }
        final int hash = this.hash(o);
        return this.segmentFor(hash).get(o, hash);
    }
    
    V getLiveValue(final E e) {
        if (e.getKey() == null) {
            return null;
        }
        final V value = ((InternalEntry<K, V, E>)e).getValue();
        if (value == null) {
            return null;
        }
        return value;
    }
    
    int hash(final Object o) {
        return rehash(this.keyEquivalence.hash(o));
    }
    
    @Override
    public boolean isEmpty() {
        final Segment<K, V, E, S>[] segments = this.segments;
        long n = 0L;
        for (int i = 0; i < segments.length; ++i) {
            if (segments[i].count != 0) {
                return false;
            }
            n += segments[i].modCount;
        }
        if (n != 0L) {
            for (int j = 0; j < segments.length; ++j) {
                if (segments[j].count != 0) {
                    return false;
                }
                n -= segments[j].modCount;
            }
            if (n != 0L) {
                return false;
            }
        }
        return true;
    }
    
    boolean isLiveForTesting(final InternalEntry<K, V, ?> internalEntry) {
        return this.segmentFor(internalEntry.getHash()).getLiveValueForTesting(internalEntry) != null;
    }
    
    @Override
    public Set<K> keySet() {
        Set<K> keySet = this.keySet;
        if (keySet == null) {
            keySet = new KeySet();
            this.keySet = keySet;
        }
        return keySet;
    }
    
    Strength keyStrength() {
        return this.entryHelper.keyStrength();
    }
    
    final Segment<K, V, E, S>[] newSegmentArray(final int n) {
        return (Segment<K, V, E, S>[])new Segment[n];
    }
    
    @CanIgnoreReturnValue
    @Override
    public V put(final K k, final V v) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(v);
        final int hash = this.hash(k);
        return this.segmentFor(hash).put(k, hash, v, false);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            this.put(entry.getKey(), (V)entry.getValue());
        }
    }
    
    @CanIgnoreReturnValue
    @Override
    public V putIfAbsent(final K k, final V v) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(v);
        final int hash = this.hash(k);
        return this.segmentFor(hash).put(k, hash, v, true);
    }
    
    void reclaimKey(final E e) {
        final int hash = e.getHash();
        this.segmentFor(hash).reclaimKey(e, hash);
    }
    
    void reclaimValue(final WeakValueReference<K, V, E> weakValueReference) {
        final InternalEntry<K, V, E> entry = weakValueReference.getEntry();
        final int hash = entry.getHash();
        this.segmentFor(hash).reclaimValue(entry.getKey(), hash, weakValueReference);
    }
    
    @CanIgnoreReturnValue
    @Override
    public V remove(final Object o) {
        if (o == null) {
            return null;
        }
        final int hash = this.hash(o);
        return this.segmentFor(hash).remove(o, hash);
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean remove(final Object o, final Object o2) {
        if (o != null && o2 != null) {
            final int hash = this.hash(o);
            return this.segmentFor(hash).remove(o, hash, o2);
        }
        return false;
    }
    
    @CanIgnoreReturnValue
    @Override
    public V replace(final K k, final V v) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(v);
        final int hash = this.hash(k);
        return this.segmentFor(hash).replace(k, hash, v);
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean replace(final K k, final V v, final V v2) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(v2);
        if (v == null) {
            return false;
        }
        final int hash = this.hash(k);
        return this.segmentFor(hash).replace(k, hash, v, v2);
    }
    
    Segment<K, V, E, S> segmentFor(final int n) {
        return this.segments[this.segmentMask & n >>> this.segmentShift];
    }
    
    @Override
    public int size() {
        final Segment<K, V, E, S>[] segments = this.segments;
        long n = 0L;
        for (int i = 0; i < segments.length; ++i) {
            n += segments[i].count;
        }
        return Ints.saturatedCast(n);
    }
    
    Equivalence<Object> valueEquivalence() {
        return this.entryHelper.valueStrength().defaultEquivalence();
    }
    
    Strength valueStrength() {
        return this.entryHelper.valueStrength();
    }
    
    @Override
    public Collection<V> values() {
        Collection<V> values = this.values;
        if (values == null) {
            values = new Values();
            this.values = values;
        }
        return values;
    }
    
    Object writeReplace() {
        return new SerializationProxy(this.entryHelper.keyStrength(), this.entryHelper.valueStrength(), this.keyEquivalence, this.entryHelper.valueStrength().defaultEquivalence(), this.concurrencyLevel, (ConcurrentMap<Object, Object>)this);
    }
    
    abstract static class AbstractSerializationProxy<K, V> extends ForwardingConcurrentMap<K, V> implements Serializable
    {
        private static final long serialVersionUID = 3L;
        final int concurrencyLevel;
        transient ConcurrentMap<K, V> delegate;
        final Equivalence<Object> keyEquivalence;
        final Strength keyStrength;
        final Equivalence<Object> valueEquivalence;
        final Strength valueStrength;
        
        AbstractSerializationProxy(final Strength keyStrength, final Strength valueStrength, final Equivalence<Object> keyEquivalence, final Equivalence<Object> valueEquivalence, final int concurrencyLevel, final ConcurrentMap<K, V> delegate) {
            this.keyStrength = keyStrength;
            this.valueStrength = valueStrength;
            this.keyEquivalence = keyEquivalence;
            this.valueEquivalence = valueEquivalence;
            this.concurrencyLevel = concurrencyLevel;
            this.delegate = delegate;
        }
        
        @Override
        protected ConcurrentMap<K, V> delegate() {
            return this.delegate;
        }
        
        void readEntries(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            while (true) {
                final Object object = objectInputStream.readObject();
                if (object == null) {
                    break;
                }
                this.delegate.put((K)object, (V)objectInputStream.readObject());
            }
        }
        
        MapMaker readMapMaker(final ObjectInputStream objectInputStream) throws IOException {
            final int int1 = objectInputStream.readInt();
            final MapMaker mapMaker = new MapMaker();
            mapMaker.initialCapacity(int1);
            mapMaker.setKeyStrength(this.keyStrength);
            mapMaker.setValueStrength(this.valueStrength);
            mapMaker.keyEquivalence(this.keyEquivalence);
            mapMaker.concurrencyLevel(this.concurrencyLevel);
            return mapMaker;
        }
        
        void writeMapTo(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeInt(this.delegate.size());
            for (final Map.Entry<Object, Object> entry : this.delegate.entrySet()) {
                objectOutputStream.writeObject(entry.getKey());
                objectOutputStream.writeObject(entry.getValue());
            }
            objectOutputStream.writeObject(null);
        }
    }
    
    abstract static class AbstractStrongKeyEntry<K, V, E extends InternalEntry<K, V, E>> implements InternalEntry<K, V, E>
    {
        final int hash;
        final K key;
        final E next;
        
        AbstractStrongKeyEntry(final K key, final int hash, final E next) {
            this.key = key;
            this.hash = hash;
            this.next = next;
        }
        
        @Override
        public int getHash() {
            return this.hash;
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public E getNext() {
            return this.next;
        }
    }
    
    abstract static class AbstractWeakKeyEntry<K, V, E extends InternalEntry<K, V, E>> extends WeakReference<K> implements InternalEntry<K, V, E>
    {
        final int hash;
        final E next;
        
        AbstractWeakKeyEntry(final ReferenceQueue<K> q, final K referent, final int hash, final E next) {
            super(referent, q);
            this.hash = hash;
            this.next = next;
        }
        
        @Override
        public int getHash() {
            return this.hash;
        }
        
        @Override
        public K getKey() {
            return this.get();
        }
        
        @Override
        public E getNext() {
            return this.next;
        }
    }
    
    static final class DummyInternalEntry implements InternalEntry<Object, Object, DummyInternalEntry>
    {
        private DummyInternalEntry() {
            throw new AssertionError();
        }
        
        @Override
        public int getHash() {
            throw new AssertionError();
        }
        
        @Override
        public Object getKey() {
            throw new AssertionError();
        }
        
        public DummyInternalEntry getNext() {
            throw new AssertionError();
        }
        
        @Override
        public Object getValue() {
            throw new AssertionError();
        }
    }
    
    final class EntryIterator extends HashIterator<Entry<K, V>>
    {
        EntryIterator(final MapMakerInternalMap mapMakerInternalMap) {
            mapMakerInternalMap.super();
        }
        
        @Override
        public Entry<K, V> next() {
            return ((HashIterator)this).nextEntry();
        }
    }
    
    final class EntrySet extends SafeToArraySet<Entry<K, V>>
    {
        @Override
        public void clear() {
            MapMakerInternalMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            final boolean b = o instanceof Entry;
            final boolean b2 = false;
            if (!b) {
                return false;
            }
            final Entry entry = (Entry)o;
            final Object key = entry.getKey();
            if (key == null) {
                return false;
            }
            final V value = MapMakerInternalMap.this.get(key);
            boolean b3 = b2;
            if (value != null) {
                b3 = b2;
                if (MapMakerInternalMap.this.valueEquivalence().equivalent(entry.getValue(), value)) {
                    b3 = true;
                }
            }
            return b3;
        }
        
        @Override
        public boolean isEmpty() {
            return MapMakerInternalMap.this.isEmpty();
        }
        
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public boolean remove(final Object o) {
            final boolean b = o instanceof Entry;
            final boolean b2 = false;
            if (!b) {
                return false;
            }
            final Entry entry = (Entry)o;
            final Object key = entry.getKey();
            boolean b3 = b2;
            if (key != null) {
                b3 = b2;
                if (MapMakerInternalMap.this.remove(key, entry.getValue())) {
                    b3 = true;
                }
            }
            return b3;
        }
        
        @Override
        public int size() {
            return MapMakerInternalMap.this.size();
        }
    }
    
    abstract class HashIterator<T> implements Iterator<T>
    {
        Segment<K, V, E, S> currentSegment;
        AtomicReferenceArray<E> currentTable;
        WriteThroughEntry lastReturned;
        E nextEntry;
        WriteThroughEntry nextExternal;
        int nextSegmentIndex;
        int nextTableIndex;
        
        HashIterator() {
            this.nextSegmentIndex = MapMakerInternalMap.this.segments.length - 1;
            this.nextTableIndex = -1;
            this.advance();
        }
        
        final void advance() {
            this.nextExternal = null;
            if (this.nextInChain()) {
                return;
            }
            if (this.nextInTable()) {
                return;
            }
            while (true) {
                final int nextSegmentIndex = this.nextSegmentIndex;
                if (nextSegmentIndex < 0) {
                    break;
                }
                final Segment<K, V, E, S>[] segments = MapMakerInternalMap.this.segments;
                this.nextSegmentIndex = nextSegmentIndex - 1;
                final Segment<K, V, E, S> currentSegment = segments[nextSegmentIndex];
                this.currentSegment = currentSegment;
                if (currentSegment.count == 0) {
                    continue;
                }
                final AtomicReferenceArray<E> table = this.currentSegment.table;
                this.currentTable = table;
                this.nextTableIndex = table.length() - 1;
                if (this.nextInTable()) {
                    break;
                }
            }
        }
        
        boolean advanceTo(final E e) {
            try {
                final K key = ((InternalEntry<K, V, E>)e).getKey();
                final V liveValue = MapMakerInternalMap.this.getLiveValue(e);
                boolean b;
                if (liveValue != null) {
                    this.nextExternal = new WriteThroughEntry(key, liveValue);
                    b = true;
                }
                else {
                    b = false;
                }
                return b;
            }
            finally {
                this.currentSegment.postReadCleanup();
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.nextExternal != null;
        }
        
        WriteThroughEntry nextEntry() {
            final WriteThroughEntry nextExternal = this.nextExternal;
            if (nextExternal != null) {
                this.lastReturned = nextExternal;
                this.advance();
                return this.lastReturned;
            }
            throw new NoSuchElementException();
        }
        
        boolean nextInChain() {
            InternalEntry<K, V, E> internalEntry = this.nextEntry;
            if (internalEntry != null) {
                while (true) {
                    this.nextEntry = internalEntry.getNext();
                    final InternalEntry<K, V, E> nextEntry = this.nextEntry;
                    if (nextEntry == null) {
                        break;
                    }
                    if (this.advanceTo((E)nextEntry)) {
                        return true;
                    }
                    internalEntry = this.nextEntry;
                }
            }
            return false;
        }
        
        boolean nextInTable() {
            InternalEntry<K, V, E> nextEntry;
            do {
                final int nextTableIndex = this.nextTableIndex;
                if (nextTableIndex < 0) {
                    return false;
                }
                final AtomicReferenceArray<E> currentTable = this.currentTable;
                this.nextTableIndex = nextTableIndex - 1;
                nextEntry = currentTable.get(nextTableIndex);
                this.nextEntry = (E)nextEntry;
            } while (nextEntry == null || (!this.advanceTo((E)nextEntry) && !this.nextInChain()));
            return true;
        }
        
        @Override
        public void remove() {
            CollectPreconditions.checkRemove(this.lastReturned != null);
            MapMakerInternalMap.this.remove(this.lastReturned.getKey());
            this.lastReturned = null;
        }
    }
    
    interface InternalEntry<K, V, E extends InternalEntry<K, V, E>>
    {
        int getHash();
        
        K getKey();
        
        E getNext();
        
        V getValue();
    }
    
    interface InternalEntryHelper<K, V, E extends InternalEntry<K, V, E>, S extends Segment<K, V, E, S>>
    {
        E copy(final S p0, final E p1, final E p2);
        
        Strength keyStrength();
        
        E newEntry(final S p0, final K p1, final int p2, final E p3);
        
        S newSegment(final MapMakerInternalMap<K, V, E, S> p0, final int p1, final int p2);
        
        void setValue(final S p0, final E p1, final V p2);
        
        Strength valueStrength();
    }
    
    final class KeyIterator extends HashIterator<K>
    {
        KeyIterator(final MapMakerInternalMap mapMakerInternalMap) {
            mapMakerInternalMap.super();
        }
        
        @Override
        public K next() {
            return ((HashIterator)this).nextEntry().getKey();
        }
    }
    
    final class KeySet extends SafeToArraySet<K>
    {
        @Override
        public void clear() {
            MapMakerInternalMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return MapMakerInternalMap.this.containsKey(o);
        }
        
        @Override
        public boolean isEmpty() {
            return MapMakerInternalMap.this.isEmpty();
        }
        
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public boolean remove(final Object o) {
            return MapMakerInternalMap.this.remove(o) != null;
        }
        
        @Override
        public int size() {
            return MapMakerInternalMap.this.size();
        }
    }
    
    private abstract static class SafeToArraySet<E> extends AbstractSet<E>
    {
        @Override
        public Object[] toArray() {
            return toArrayList((Collection<Object>)this).toArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] a) {
            return toArrayList((Collection<Object>)this).toArray(a);
        }
    }
    
    abstract static class Segment<K, V, E extends InternalEntry<K, V, E>, S extends Segment<K, V, E, S>> extends ReentrantLock
    {
        volatile int count;
        final MapMakerInternalMap<K, V, E, S> map;
        final int maxSegmentSize;
        int modCount;
        final AtomicInteger readCount;
        volatile AtomicReferenceArray<E> table;
        int threshold;
        
        Segment(final MapMakerInternalMap<K, V, E, S> map, final int n, final int maxSegmentSize) {
            this.readCount = new AtomicInteger();
            this.map = map;
            this.maxSegmentSize = maxSegmentSize;
            this.initTable(this.newEntryArray(n));
        }
        
        static <K, V, E extends InternalEntry<K, V, E>> boolean isCollected(final E e) {
            return e.getValue() == null;
        }
        
        abstract E castForTesting(final InternalEntry<K, V, ?> p0);
        
        void clear() {
            if (this.count != 0) {
                this.lock();
                try {
                    final AtomicReferenceArray<E> table = this.table;
                    for (int i = 0; i < table.length(); ++i) {
                        table.set(i, null);
                    }
                    this.maybeClearReferenceQueues();
                    this.readCount.set(0);
                    ++this.modCount;
                    this.count = 0;
                }
                finally {
                    this.unlock();
                }
            }
        }
        
         <T> void clearReferenceQueue(final ReferenceQueue<T> referenceQueue) {
            while (referenceQueue.poll() != null) {}
        }
        
        boolean containsKey(Object value, final int n) {
            try {
                final int count = this.count;
                final boolean b = false;
                if (count != 0) {
                    final InternalEntry<K, Object, E> liveEntry = this.getLiveEntry(value, n);
                    boolean b2 = b;
                    if (liveEntry != null) {
                        value = liveEntry.getValue();
                        b2 = b;
                        if (value != null) {
                            b2 = true;
                        }
                    }
                    return b2;
                }
                return false;
            }
            finally {
                this.postReadCleanup();
            }
        }
        
        boolean containsValue(final Object o) {
            try {
                if (this.count != 0) {
                    final AtomicReferenceArray<E> table = this.table;
                    for (int length = table.length(), i = 0; i < length; ++i) {
                        for (Object next = table.get(i); next != null; next = ((InternalEntry<K, V, E>)next).getNext()) {
                            final V liveValue = this.getLiveValue((E)next);
                            if (liveValue != null) {
                                if (this.map.valueEquivalence().equivalent(o, liveValue)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            }
            finally {
                this.postReadCleanup();
            }
        }
        
        E copyEntry(final E e, final E e2) {
            return this.map.entryHelper.copy(this.self(), e, e2);
        }
        
        @GuardedBy("this")
        void drainKeyReferenceQueue(final ReferenceQueue<K> referenceQueue) {
            int n = 0;
            do {
                final Reference<? extends K> poll = referenceQueue.poll();
                if (poll == null) {
                    break;
                }
                this.map.reclaimKey((E)(InternalEntry)poll);
            } while (++n != 16);
        }
        
        @GuardedBy("this")
        void drainValueReferenceQueue(final ReferenceQueue<V> referenceQueue) {
            int n = 0;
            do {
                final Reference<? extends V> poll = referenceQueue.poll();
                if (poll == null) {
                    break;
                }
                this.map.reclaimValue((WeakValueReference<K, V, E>)poll);
            } while (++n != 16);
        }
        
        @GuardedBy("this")
        void expand() {
            final AtomicReferenceArray<E> table = this.table;
            final int length = table.length();
            if (length >= 1073741824) {
                return;
            }
            int count = this.count;
            final AtomicReferenceArray<E> entryArray = this.newEntryArray(length << 1);
            this.threshold = entryArray.length() * 3 / 4;
            final int n = entryArray.length() - 1;
            int n2;
            for (int i = 0; i < length; ++i, count = n2) {
                Object next = table.get(i);
                n2 = count;
                if (next != null) {
                    Object o = ((InternalEntry<K, V, InternalEntry<K, V, InternalEntry<K, V, InternalEntry>>>)next).getNext();
                    int n3 = ((InternalEntry)next).getHash() & n;
                    if (o == null) {
                        entryArray.set(n3, (E)next);
                        n2 = count;
                    }
                    else {
                        Object newValue = next;
                        while (o != null) {
                            final int n4 = ((InternalEntry)o).getHash() & n;
                            int n5;
                            if (n4 != (n5 = n3)) {
                                newValue = o;
                                n5 = n4;
                            }
                            o = ((InternalEntry<K, V, InternalEntry<K, V, InternalEntry>>)o).getNext();
                            n3 = n5;
                        }
                        entryArray.set(n3, (E)newValue);
                        while (true) {
                            n2 = count;
                            if (next == newValue) {
                                break;
                            }
                            final int n6 = ((InternalEntry)next).getHash() & n;
                            final InternalEntry<K, V, InternalEntry<K, V, InternalEntry<K, V, InternalEntry>>> copyEntry = this.copyEntry((InternalEntry<K, V, InternalEntry<K, V, InternalEntry<K, V, InternalEntry>>>)next, (InternalEntry<K, V, InternalEntry<K, V, InternalEntry<K, V, InternalEntry>>>)entryArray.get(n6));
                            if (copyEntry != null) {
                                entryArray.set(n6, (E)copyEntry);
                            }
                            else {
                                --count;
                            }
                            next = ((InternalEntry<K, V, InternalEntry<K, V, InternalEntry<K, V, InternalEntry>>>)next).getNext();
                        }
                    }
                }
            }
            this.table = entryArray;
            this.count = count;
        }
        
        V get(Object value, final int n) {
            try {
                final InternalEntry<K, Object, E> liveEntry = this.getLiveEntry(value, n);
                if (liveEntry == null) {
                    return null;
                }
                value = liveEntry.getValue();
                if (value == null) {
                    this.tryDrainReferenceQueues();
                }
                return (V)value;
            }
            finally {
                this.postReadCleanup();
            }
        }
        
        E getEntry(final Object o, final int n) {
            if (this.count != 0) {
                for (Object o2 = this.getFirst(n); o2 != null; o2 = ((InternalEntry)o2).getNext()) {
                    if (((InternalEntry)o2).getHash() == n) {
                        final Object key = ((InternalEntry<Object, V, E>)o2).getKey();
                        if (key == null) {
                            this.tryDrainReferenceQueues();
                        }
                        else if (this.map.keyEquivalence.equivalent(o, key)) {
                            return (E)o2;
                        }
                    }
                }
            }
            return null;
        }
        
        E getFirst(final int n) {
            final AtomicReferenceArray<E> table = this.table;
            return table.get(n & table.length() - 1);
        }
        
        E getLiveEntry(final Object o, final int n) {
            return this.getEntry(o, n);
        }
        
        V getLiveValue(final E e) {
            if (e.getKey() == null) {
                this.tryDrainReferenceQueues();
                return null;
            }
            final V value = ((InternalEntry<K, V, E>)e).getValue();
            if (value == null) {
                this.tryDrainReferenceQueues();
                return null;
            }
            return value;
        }
        
        V getLiveValueForTesting(final InternalEntry<K, V, ?> internalEntry) {
            return this.getLiveValue(this.castForTesting(internalEntry));
        }
        
        void initTable(final AtomicReferenceArray<E> table) {
            final int threshold = table.length() * 3 / 4;
            this.threshold = threshold;
            if (threshold == this.maxSegmentSize) {
                this.threshold = threshold + 1;
            }
            this.table = table;
        }
        
        void maybeClearReferenceQueues() {
        }
        
        @GuardedBy("this")
        void maybeDrainReferenceQueues() {
        }
        
        AtomicReferenceArray<E> newEntryArray(final int length) {
            return new AtomicReferenceArray<E>(length);
        }
        
        void postReadCleanup() {
            if ((this.readCount.incrementAndGet() & 0x3F) == 0x0) {
                this.runCleanup();
            }
        }
        
        @GuardedBy("this")
        void preWriteCleanup() {
            this.runLockedCleanup();
        }
        
        V put(final K k, final int n, final V v, final boolean b) {
            this.lock();
            try {
                this.preWriteCleanup();
                int count;
                if ((count = this.count + 1) > this.threshold) {
                    this.expand();
                    count = this.count + 1;
                }
                final AtomicReferenceArray<E> table = this.table;
                final int n2 = table.length() - 1 & n;
                Object next;
                final InternalEntry<Object, V, E> internalEntry = (InternalEntry<Object, V, E>)(next = table.get(n2));
                while (next != null) {
                    final Object key = ((InternalEntry<Object, V, E>)next).getKey();
                    if (((InternalEntry)next).getHash() == n && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        final V value = ((InternalEntry)next).getValue();
                        if (value == null) {
                            ++this.modCount;
                            this.setValue((E)next, v);
                            this.count = this.count;
                            return null;
                        }
                        if (b) {
                            return (V)value;
                        }
                        ++this.modCount;
                        this.setValue((E)next, v);
                        return (V)value;
                    }
                    else {
                        next = ((InternalEntry<Object, V, E>)next).getNext();
                    }
                }
                ++this.modCount;
                final InternalEntry<K, V, E> entry = this.map.entryHelper.newEntry(this.self(), k, n, (E)internalEntry);
                this.setValue((E)entry, v);
                table.set(n2, (E)entry);
                this.count = count;
                return null;
            }
            finally {
                this.unlock();
            }
        }
        
        @CanIgnoreReturnValue
        boolean reclaimKey(final E e, int n) {
            this.lock();
            try {
                final AtomicReferenceArray<E> table = this.table;
                n &= table.length() - 1;
                Object next;
                for (InternalEntry<K, V, E> internalEntry = (InternalEntry<K, V, E>)(next = table.get(n)); next != null; next = ((InternalEntry<K, V, InternalEntry<K, V, InternalEntry<K, V, InternalEntry>>>)next).getNext()) {
                    if (next == e) {
                        ++this.modCount;
                        final InternalEntry<K, V, InternalEntry<K, V, InternalEntry<K, V, InternalEntry>>> removeFromChain = this.removeFromChain((InternalEntry<K, V, InternalEntry<K, V, InternalEntry<K, V, InternalEntry>>>)internalEntry, (InternalEntry<K, V, InternalEntry<K, V, InternalEntry<K, V, InternalEntry>>>)next);
                        final int count = this.count;
                        table.set(n, (E)removeFromChain);
                        this.count = count - 1;
                        return true;
                    }
                }
                return false;
            }
            finally {
                this.unlock();
            }
        }
        
        @CanIgnoreReturnValue
        boolean reclaimValue(final K k, int count, final WeakValueReference<K, V, E> weakValueReference) {
            this.lock();
            try {
                final AtomicReferenceArray<E> table = this.table;
                final int n = table.length() - 1 & count;
                Object next;
                final InternalEntry<Object, V, E> internalEntry = (InternalEntry<Object, V, E>)(next = table.get(n));
                while (next != null) {
                    final Object key = ((InternalEntry<Object, V, E>)next).getKey();
                    if (((InternalEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        if (((WeakValueEntry)next).getValueReference() == weakValueReference) {
                            ++this.modCount;
                            final InternalEntry<Object, V, E> removeFromChain = this.removeFromChain(internalEntry, (InternalEntry<Object, V, E>)next);
                            count = this.count;
                            table.set(n, (E)removeFromChain);
                            this.count = count - 1;
                            return true;
                        }
                        return false;
                    }
                    else {
                        next = ((InternalEntry)next).getNext();
                    }
                }
                return false;
            }
            finally {
                this.unlock();
            }
        }
        
        @CanIgnoreReturnValue
        V remove(Object value, int count) {
            this.lock();
            try {
                this.preWriteCleanup();
                final AtomicReferenceArray<E> table = this.table;
                final int n = table.length() - 1 & count;
                Object next;
                for (InternalEntry<Object, V, E> internalEntry = (InternalEntry<Object, V, E>)(next = table.get(n)); next != null; next = ((InternalEntry<Object, Object, E>)next).getNext()) {
                    final Object key = ((InternalEntry<Object, Object, E>)next).getKey();
                    if (((InternalEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(value, key)) {
                        value = ((InternalEntry<Object, Object, E>)next).getValue();
                        if (value == null) {
                            if (!isCollected(next)) {
                                return null;
                            }
                        }
                        ++this.modCount;
                        final InternalEntry<Object, V, E> removeFromChain = this.removeFromChain(internalEntry, (InternalEntry<Object, V, E>)next);
                        count = this.count;
                        table.set(n, (E)removeFromChain);
                        this.count = count - 1;
                        return (V)value;
                    }
                }
                return null;
            }
            finally {
                this.unlock();
            }
        }
        
        boolean remove(Object value, int count, final Object o) {
            this.lock();
            try {
                this.preWriteCleanup();
                final AtomicReferenceArray<E> table = this.table;
                final int n = table.length() - 1 & count;
                Object next;
                final InternalEntry<Object, V, E> internalEntry = (InternalEntry<Object, V, E>)(next = table.get(n));
                while (true) {
                    boolean b = false;
                    if (next == null) {
                        return false;
                    }
                    final Object key = ((InternalEntry<Object, Object, E>)next).getKey();
                    if (((InternalEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(value, key)) {
                        value = ((InternalEntry<Object, Object, E>)next).getValue();
                        if (this.map.valueEquivalence().equivalent(o, value)) {
                            b = true;
                        }
                        else if (!isCollected(next)) {
                            return false;
                        }
                        ++this.modCount;
                        final InternalEntry<Object, V, E> removeFromChain = this.removeFromChain(internalEntry, (InternalEntry<Object, V, E>)next);
                        count = this.count;
                        table.set(n, (E)removeFromChain);
                        this.count = count - 1;
                        return b;
                    }
                    next = ((InternalEntry<Object, Object, E>)next).getNext();
                }
            }
            finally {
                this.unlock();
            }
        }
        
        @GuardedBy("this")
        E removeFromChain(E next, final E e) {
            int count = this.count;
            InternalEntry<K, V, InternalEntry<K, V, InternalEntry<K, V, InternalEntry>>> next2 = ((InternalEntry<K, V, InternalEntry<K, V, InternalEntry<K, V, InternalEntry<K, V, InternalEntry>>>>)e).getNext();
            while (next != e) {
                final InternalEntry<K, V, InternalEntry<K, V, InternalEntry<K, V, InternalEntry>>> copyEntry = this.copyEntry((InternalEntry<K, V, InternalEntry<K, V, InternalEntry<K, V, InternalEntry>>>)next, next2);
                if (copyEntry != null) {
                    next2 = copyEntry;
                }
                else {
                    --count;
                }
                next = ((InternalEntry<K, V, E>)next).getNext();
            }
            this.count = count;
            return (E)next2;
        }
        
        V replace(final K k, int count, final V v) {
            this.lock();
            try {
                this.preWriteCleanup();
                final AtomicReferenceArray<E> table = this.table;
                final int n = table.length() - 1 & count;
                Object next;
                final InternalEntry<Object, V, E> internalEntry = (InternalEntry<Object, V, E>)(next = table.get(n));
                while (next != null) {
                    final Object key = ((InternalEntry<Object, V, E>)next).getKey();
                    if (((InternalEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        final V value = ((InternalEntry)next).getValue();
                        if (value == null) {
                            if (isCollected(next)) {
                                ++this.modCount;
                                final InternalEntry<Object, V, E> removeFromChain = this.removeFromChain(internalEntry, (InternalEntry<Object, V, E>)next);
                                count = this.count;
                                table.set(n, (E)removeFromChain);
                                this.count = count - 1;
                            }
                            return null;
                        }
                        ++this.modCount;
                        this.setValue((E)next, v);
                        return (V)value;
                    }
                    else {
                        next = ((InternalEntry<K, V, E>)next).getNext();
                    }
                }
                return null;
            }
            finally {
                this.unlock();
            }
        }
        
        boolean replace(final K k, int count, final V v, final V v2) {
            this.lock();
            try {
                this.preWriteCleanup();
                final AtomicReferenceArray<E> table = this.table;
                final int n = table.length() - 1 & count;
                Object next;
                final InternalEntry<Object, V, E> internalEntry = (InternalEntry<Object, V, E>)(next = table.get(n));
                while (next != null) {
                    final Object key = ((InternalEntry<Object, V, E>)next).getKey();
                    if (((InternalEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        final V value = ((InternalEntry)next).getValue();
                        if (value == null) {
                            if (isCollected(next)) {
                                ++this.modCount;
                                final InternalEntry<Object, V, E> removeFromChain = this.removeFromChain(internalEntry, (InternalEntry<Object, V, E>)next);
                                count = this.count;
                                table.set(n, (E)removeFromChain);
                                this.count = count - 1;
                            }
                            return false;
                        }
                        if (this.map.valueEquivalence().equivalent(v, value)) {
                            ++this.modCount;
                            this.setValue((E)next, v2);
                            return true;
                        }
                        return false;
                    }
                    else {
                        next = ((InternalEntry<K, V, E>)next).getNext();
                    }
                }
                return false;
            }
            finally {
                this.unlock();
            }
        }
        
        void runCleanup() {
            this.runLockedCleanup();
        }
        
        void runLockedCleanup() {
            if (this.tryLock()) {
                try {
                    this.maybeDrainReferenceQueues();
                    this.readCount.set(0);
                }
                finally {
                    this.unlock();
                }
            }
        }
        
        abstract S self();
        
        void setValue(final E e, final V v) {
            this.map.entryHelper.setValue(this.self(), e, v);
        }
        
        void tryDrainReferenceQueues() {
            if (this.tryLock()) {
                try {
                    this.maybeDrainReferenceQueues();
                }
                finally {
                    this.unlock();
                }
            }
        }
    }
    
    private static final class SerializationProxy<K, V> extends AbstractSerializationProxy<K, V>
    {
        private static final long serialVersionUID = 3L;
        
        SerializationProxy(final Strength strength, final Strength strength2, final Equivalence<Object> equivalence, final Equivalence<Object> equivalence2, final int n, final ConcurrentMap<K, V> concurrentMap) {
            super(strength, strength2, equivalence, equivalence2, n, concurrentMap);
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            super.delegate = this.readMapMaker(objectInputStream).makeMap();
            this.readEntries(objectInputStream);
        }
        
        private Object readResolve() {
            return super.delegate;
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            this.writeMapTo(objectOutputStream);
        }
    }
    
    enum Strength
    {
        STRONG(0) {
            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.equals();
            }
        }, 
        WEAK(1) {
            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.identity();
            }
        };
        
        abstract Equivalence<Object> defaultEquivalence();
    }
    
    static final class StrongKeyStrongValueEntry<K, V> extends AbstractStrongKeyEntry<K, V, StrongKeyStrongValueEntry<K, V>> implements Object<K, V, StrongKeyStrongValueEntry<K, V>>
    {
        private volatile V value;
        
        StrongKeyStrongValueEntry(final K k, final int n, final StrongKeyStrongValueEntry<K, V> strongKeyStrongValueEntry) {
            super(k, n, strongKeyStrongValueEntry);
            this.value = null;
        }
        
        StrongKeyStrongValueEntry<K, V> copy(final StrongKeyStrongValueEntry<K, V> strongKeyStrongValueEntry) {
            final StrongKeyStrongValueEntry strongKeyStrongValueEntry2 = new StrongKeyStrongValueEntry((K)super.key, super.hash, strongKeyStrongValueEntry);
            strongKeyStrongValueEntry2.value = this.value;
            return strongKeyStrongValueEntry2;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
        
        void setValue(final V value) {
            this.value = value;
        }
        
        static final class Helper<K, V> implements InternalEntryHelper<K, V, StrongKeyStrongValueEntry<K, V>, StrongKeyStrongValueSegment<K, V>>
        {
            private static final Helper<?, ?> INSTANCE;
            
            static {
                INSTANCE = new Helper<Object, Object>();
            }
            
            static <K, V> Helper<K, V> instance() {
                return (Helper<K, V>)Helper.INSTANCE;
            }
            
            public StrongKeyStrongValueEntry<K, V> copy(final StrongKeyStrongValueSegment<K, V> strongKeyStrongValueSegment, final StrongKeyStrongValueEntry<K, V> strongKeyStrongValueEntry, final StrongKeyStrongValueEntry<K, V> strongKeyStrongValueEntry2) {
                return strongKeyStrongValueEntry.copy(strongKeyStrongValueEntry2);
            }
            
            @Override
            public Strength keyStrength() {
                return Strength.STRONG;
            }
            
            public StrongKeyStrongValueEntry<K, V> newEntry(final StrongKeyStrongValueSegment<K, V> strongKeyStrongValueSegment, final K k, final int n, final StrongKeyStrongValueEntry<K, V> strongKeyStrongValueEntry) {
                return new StrongKeyStrongValueEntry<K, V>(k, n, strongKeyStrongValueEntry);
            }
            
            public StrongKeyStrongValueSegment<K, V> newSegment(final MapMakerInternalMap<K, V, StrongKeyStrongValueEntry<K, V>, StrongKeyStrongValueSegment<K, V>> mapMakerInternalMap, final int n, final int n2) {
                return (StrongKeyStrongValueSegment<K, V>)new StrongKeyStrongValueSegment((MapMakerInternalMap<Object, Object, StrongKeyStrongValueEntry<Object, Object>, StrongKeyStrongValueSegment<Object, Object>>)mapMakerInternalMap, n, n2);
            }
            
            public void setValue(final StrongKeyStrongValueSegment<K, V> strongKeyStrongValueSegment, final StrongKeyStrongValueEntry<K, V> strongKeyStrongValueEntry, final V value) {
                strongKeyStrongValueEntry.setValue(value);
            }
            
            @Override
            public Strength valueStrength() {
                return Strength.STRONG;
            }
        }
    }
    
    static final class StrongKeyStrongValueSegment<K, V> extends Segment<K, V, StrongKeyStrongValueEntry<K, V>, StrongKeyStrongValueSegment<K, V>>
    {
        StrongKeyStrongValueSegment(final MapMakerInternalMap<K, V, StrongKeyStrongValueEntry<K, V>, StrongKeyStrongValueSegment<K, V>> mapMakerInternalMap, final int n, final int n2) {
            super(mapMakerInternalMap, n, n2);
        }
        
        public StrongKeyStrongValueEntry<K, V> castForTesting(final InternalEntry<K, V, ?> internalEntry) {
            return (StrongKeyStrongValueEntry<K, V>)(StrongKeyStrongValueEntry)internalEntry;
        }
        
        StrongKeyStrongValueSegment<K, V> self() {
            return this;
        }
    }
    
    static final class StrongKeyWeakValueEntry<K, V> extends AbstractStrongKeyEntry<K, V, StrongKeyWeakValueEntry<K, V>> implements WeakValueEntry<K, V, StrongKeyWeakValueEntry<K, V>>
    {
        private volatile WeakValueReference<K, V, StrongKeyWeakValueEntry<K, V>> valueReference;
        
        StrongKeyWeakValueEntry(final K k, final int n, final StrongKeyWeakValueEntry<K, V> strongKeyWeakValueEntry) {
            super(k, n, strongKeyWeakValueEntry);
            this.valueReference = MapMakerInternalMap.unsetWeakValueReference();
        }
        
        StrongKeyWeakValueEntry<K, V> copy(final ReferenceQueue<V> referenceQueue, final StrongKeyWeakValueEntry<K, V> strongKeyWeakValueEntry) {
            final StrongKeyWeakValueEntry strongKeyWeakValueEntry2 = new StrongKeyWeakValueEntry((K)super.key, super.hash, strongKeyWeakValueEntry);
            strongKeyWeakValueEntry2.valueReference = this.valueReference.copyFor(referenceQueue, strongKeyWeakValueEntry2);
            return strongKeyWeakValueEntry2;
        }
        
        @Override
        public V getValue() {
            return this.valueReference.get();
        }
        
        @Override
        public WeakValueReference<K, V, StrongKeyWeakValueEntry<K, V>> getValueReference() {
            return this.valueReference;
        }
        
        void setValue(final V p0, final ReferenceQueue<V> p1) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     4: astore_3       
            //     5: aload_0        
            //     6: new             Lcom/google/common/collect/MapMakerInternalMap$WeakValueReferenceImpl;
            //     9: dup            
            //    10: aload_2        
            //    11: aload_1        
            //    12: aload_0        
            //    13: invokespecial   com/google/common/collect/MapMakerInternalMap$WeakValueReferenceImpl.<init>:(Ljava/lang/ref/ReferenceQueue;Ljava/lang/Object;Lcom/google/common/collect/MapMakerInternalMap$InternalEntry;)V
            //    16: putfield        com/google/common/collect/MapMakerInternalMap$StrongKeyWeakValueEntry.valueReference:Lcom/google/common/collect/MapMakerInternalMap$WeakValueReference;
            //    19: aload_3        
            //    20: invokeinterface com/google/common/collect/MapMakerInternalMap$WeakValueReference.clear:()V
            //    25: return         
            //    Signature:
            //  (TV;Ljava/lang/ref/ReferenceQueue<TV;>;)V
            // 
            // The error that occurred was:
            // 
            // com.strobel.assembler.metadata.MetadataHelper$AdaptFailure
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2300)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2221)
            //     at com.strobel.assembler.metadata.GenericParameter.accept(GenericParameter.java:85)
            //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2255)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2232)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitClassType(MetadataHelper.java:2239)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitClassType(MetadataHelper.java:2221)
            //     at com.strobel.assembler.metadata.TypeDefinition.accept(TypeDefinition.java:183)
            //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2255)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2232)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2245)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2221)
            //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
            //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
            //     at com.strobel.assembler.metadata.MetadataHelper.adapt(MetadataHelper.java:1312)
            //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:932)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
            //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1061)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
            //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
            //     at com.strobel.decompiler.ast.TypeAnalysis.invalidateDependentExpressions(TypeAnalysis.java:759)
            //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1011)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:778)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2669)
            //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
            //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:586)
            //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:397)
            //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:576)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        static final class Helper<K, V> implements InternalEntryHelper<K, V, StrongKeyWeakValueEntry<K, V>, StrongKeyWeakValueSegment<K, V>>
        {
            private static final Helper<?, ?> INSTANCE;
            
            static {
                INSTANCE = new Helper<Object, Object>();
            }
            
            static <K, V> Helper<K, V> instance() {
                return (Helper<K, V>)Helper.INSTANCE;
            }
            
            public StrongKeyWeakValueEntry<K, V> copy(final StrongKeyWeakValueSegment<K, V> strongKeyWeakValueSegment, final StrongKeyWeakValueEntry<K, V> strongKeyWeakValueEntry, final StrongKeyWeakValueEntry<K, V> strongKeyWeakValueEntry2) {
                if (Segment.isCollected(strongKeyWeakValueEntry)) {
                    return null;
                }
                return strongKeyWeakValueEntry.copy(((StrongKeyWeakValueSegment<Object, Object>)strongKeyWeakValueSegment).queueForValues, strongKeyWeakValueEntry2);
            }
            
            @Override
            public Strength keyStrength() {
                return Strength.STRONG;
            }
            
            public StrongKeyWeakValueEntry<K, V> newEntry(final StrongKeyWeakValueSegment<K, V> strongKeyWeakValueSegment, final K k, final int n, final StrongKeyWeakValueEntry<K, V> strongKeyWeakValueEntry) {
                return new StrongKeyWeakValueEntry<K, V>(k, n, strongKeyWeakValueEntry);
            }
            
            public StrongKeyWeakValueSegment<K, V> newSegment(final MapMakerInternalMap<K, V, StrongKeyWeakValueEntry<K, V>, StrongKeyWeakValueSegment<K, V>> mapMakerInternalMap, final int n, final int n2) {
                return (StrongKeyWeakValueSegment<K, V>)new StrongKeyWeakValueSegment((MapMakerInternalMap<Object, Object, StrongKeyWeakValueEntry<Object, Object>, StrongKeyWeakValueSegment<Object, Object>>)mapMakerInternalMap, n, n2);
            }
            
            public void setValue(final StrongKeyWeakValueSegment<K, V> strongKeyWeakValueSegment, final StrongKeyWeakValueEntry<K, V> strongKeyWeakValueEntry, final V v) {
                strongKeyWeakValueEntry.setValue(v, ((StrongKeyWeakValueSegment<Object, Object>)strongKeyWeakValueSegment).queueForValues);
            }
            
            @Override
            public Strength valueStrength() {
                return Strength.WEAK;
            }
        }
    }
    
    static final class StrongKeyWeakValueSegment<K, V> extends Segment<K, V, StrongKeyWeakValueEntry<K, V>, StrongKeyWeakValueSegment<K, V>>
    {
        private final ReferenceQueue<V> queueForValues;
        
        StrongKeyWeakValueSegment(final MapMakerInternalMap<K, V, StrongKeyWeakValueEntry<K, V>, StrongKeyWeakValueSegment<K, V>> mapMakerInternalMap, final int n, final int n2) {
            super(mapMakerInternalMap, n, n2);
            this.queueForValues = new ReferenceQueue<V>();
        }
        
        public StrongKeyWeakValueEntry<K, V> castForTesting(final InternalEntry<K, V, ?> internalEntry) {
            return (StrongKeyWeakValueEntry<K, V>)(StrongKeyWeakValueEntry)internalEntry;
        }
        
        @Override
        void maybeClearReferenceQueues() {
            this.clearReferenceQueue(this.queueForValues);
        }
        
        @Override
        void maybeDrainReferenceQueues() {
            this.drainValueReferenceQueue(this.queueForValues);
        }
        
        StrongKeyWeakValueSegment<K, V> self() {
            return this;
        }
    }
    
    final class ValueIterator extends HashIterator<V>
    {
        ValueIterator(final MapMakerInternalMap mapMakerInternalMap) {
            mapMakerInternalMap.super();
        }
        
        @Override
        public V next() {
            return ((HashIterator)this).nextEntry().getValue();
        }
    }
    
    final class Values extends AbstractCollection<V>
    {
        @Override
        public void clear() {
            MapMakerInternalMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return MapMakerInternalMap.this.containsValue(o);
        }
        
        @Override
        public boolean isEmpty() {
            return MapMakerInternalMap.this.isEmpty();
        }
        
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
        
        @Override
        public int size() {
            return MapMakerInternalMap.this.size();
        }
        
        @Override
        public Object[] toArray() {
            return toArrayList((Collection<Object>)this).toArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] a) {
            return toArrayList((Collection<Object>)this).toArray(a);
        }
    }
    
    static final class WeakKeyStrongValueEntry<K, V> extends AbstractWeakKeyEntry<K, V, WeakKeyStrongValueEntry<K, V>> implements Object<K, V, WeakKeyStrongValueEntry<K, V>>
    {
        private volatile V value;
        
        WeakKeyStrongValueEntry(final ReferenceQueue<K> referenceQueue, final K k, final int n, final WeakKeyStrongValueEntry<K, V> weakKeyStrongValueEntry) {
            super(referenceQueue, k, n, weakKeyStrongValueEntry);
            this.value = null;
        }
        
        WeakKeyStrongValueEntry<K, V> copy(final ReferenceQueue<K> referenceQueue, final WeakKeyStrongValueEntry<K, V> weakKeyStrongValueEntry) {
            final WeakKeyStrongValueEntry<Object, Object> weakKeyStrongValueEntry2 = (WeakKeyStrongValueEntry<Object, Object>)new WeakKeyStrongValueEntry<K, Object>(referenceQueue, this.getKey(), super.hash, (WeakKeyStrongValueEntry<K, Object>)weakKeyStrongValueEntry);
            weakKeyStrongValueEntry2.setValue(this.value);
            return (WeakKeyStrongValueEntry<K, V>)weakKeyStrongValueEntry2;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
        
        void setValue(final V value) {
            this.value = value;
        }
        
        static final class Helper<K, V> implements InternalEntryHelper<K, V, WeakKeyStrongValueEntry<K, V>, WeakKeyStrongValueSegment<K, V>>
        {
            private static final Helper<?, ?> INSTANCE;
            
            static {
                INSTANCE = new Helper<Object, Object>();
            }
            
            static <K, V> Helper<K, V> instance() {
                return (Helper<K, V>)Helper.INSTANCE;
            }
            
            public WeakKeyStrongValueEntry<K, V> copy(final WeakKeyStrongValueSegment<K, V> weakKeyStrongValueSegment, final WeakKeyStrongValueEntry<K, V> weakKeyStrongValueEntry, final WeakKeyStrongValueEntry<K, V> weakKeyStrongValueEntry2) {
                if (weakKeyStrongValueEntry.getKey() == null) {
                    return null;
                }
                return weakKeyStrongValueEntry.copy(((WeakKeyStrongValueSegment<Object, Object>)weakKeyStrongValueSegment).queueForKeys, weakKeyStrongValueEntry2);
            }
            
            @Override
            public Strength keyStrength() {
                return Strength.WEAK;
            }
            
            public WeakKeyStrongValueEntry<K, V> newEntry(final WeakKeyStrongValueSegment<K, V> weakKeyStrongValueSegment, final K k, final int n, final WeakKeyStrongValueEntry<K, V> weakKeyStrongValueEntry) {
                return new WeakKeyStrongValueEntry<K, V>(((WeakKeyStrongValueSegment<Object, Object>)weakKeyStrongValueSegment).queueForKeys, k, n, weakKeyStrongValueEntry);
            }
            
            public WeakKeyStrongValueSegment<K, V> newSegment(final MapMakerInternalMap<K, V, WeakKeyStrongValueEntry<K, V>, WeakKeyStrongValueSegment<K, V>> mapMakerInternalMap, final int n, final int n2) {
                return (WeakKeyStrongValueSegment<K, V>)new WeakKeyStrongValueSegment((MapMakerInternalMap<Object, Object, WeakKeyStrongValueEntry<Object, Object>, WeakKeyStrongValueSegment<Object, Object>>)mapMakerInternalMap, n, n2);
            }
            
            public void setValue(final WeakKeyStrongValueSegment<K, V> weakKeyStrongValueSegment, final WeakKeyStrongValueEntry<K, V> weakKeyStrongValueEntry, final V value) {
                weakKeyStrongValueEntry.setValue(value);
            }
            
            @Override
            public Strength valueStrength() {
                return Strength.STRONG;
            }
        }
    }
    
    static final class WeakKeyStrongValueSegment<K, V> extends Segment<K, V, WeakKeyStrongValueEntry<K, V>, WeakKeyStrongValueSegment<K, V>>
    {
        private final ReferenceQueue<K> queueForKeys;
        
        WeakKeyStrongValueSegment(final MapMakerInternalMap<K, V, WeakKeyStrongValueEntry<K, V>, WeakKeyStrongValueSegment<K, V>> mapMakerInternalMap, final int n, final int n2) {
            super(mapMakerInternalMap, n, n2);
            this.queueForKeys = new ReferenceQueue<K>();
        }
        
        public WeakKeyStrongValueEntry<K, V> castForTesting(final InternalEntry<K, V, ?> internalEntry) {
            return (WeakKeyStrongValueEntry<K, V>)(WeakKeyStrongValueEntry)internalEntry;
        }
        
        @Override
        void maybeClearReferenceQueues() {
            this.clearReferenceQueue(this.queueForKeys);
        }
        
        @Override
        void maybeDrainReferenceQueues() {
            this.drainKeyReferenceQueue(this.queueForKeys);
        }
        
        WeakKeyStrongValueSegment<K, V> self() {
            return this;
        }
    }
    
    static final class WeakKeyWeakValueEntry<K, V> extends AbstractWeakKeyEntry<K, V, WeakKeyWeakValueEntry<K, V>> implements WeakValueEntry<K, V, WeakKeyWeakValueEntry<K, V>>
    {
        private volatile WeakValueReference<K, V, WeakKeyWeakValueEntry<K, V>> valueReference;
        
        WeakKeyWeakValueEntry(final ReferenceQueue<K> referenceQueue, final K k, final int n, final WeakKeyWeakValueEntry<K, V> weakKeyWeakValueEntry) {
            super(referenceQueue, k, n, weakKeyWeakValueEntry);
            this.valueReference = MapMakerInternalMap.unsetWeakValueReference();
        }
        
        WeakKeyWeakValueEntry<K, V> copy(final ReferenceQueue<K> referenceQueue, final ReferenceQueue<V> referenceQueue2, final WeakKeyWeakValueEntry<K, V> weakKeyWeakValueEntry) {
            final WeakKeyWeakValueEntry weakKeyWeakValueEntry2 = new WeakKeyWeakValueEntry(referenceQueue, this.getKey(), super.hash, weakKeyWeakValueEntry);
            weakKeyWeakValueEntry2.valueReference = this.valueReference.copyFor(referenceQueue2, weakKeyWeakValueEntry2);
            return weakKeyWeakValueEntry2;
        }
        
        @Override
        public V getValue() {
            return this.valueReference.get();
        }
        
        @Override
        public WeakValueReference<K, V, WeakKeyWeakValueEntry<K, V>> getValueReference() {
            return this.valueReference;
        }
        
        void setValue(final V p0, final ReferenceQueue<V> p1) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     4: astore_3       
            //     5: aload_0        
            //     6: new             Lcom/google/common/collect/MapMakerInternalMap$WeakValueReferenceImpl;
            //     9: dup            
            //    10: aload_2        
            //    11: aload_1        
            //    12: aload_0        
            //    13: invokespecial   com/google/common/collect/MapMakerInternalMap$WeakValueReferenceImpl.<init>:(Ljava/lang/ref/ReferenceQueue;Ljava/lang/Object;Lcom/google/common/collect/MapMakerInternalMap$InternalEntry;)V
            //    16: putfield        com/google/common/collect/MapMakerInternalMap$WeakKeyWeakValueEntry.valueReference:Lcom/google/common/collect/MapMakerInternalMap$WeakValueReference;
            //    19: aload_3        
            //    20: invokeinterface com/google/common/collect/MapMakerInternalMap$WeakValueReference.clear:()V
            //    25: return         
            //    Signature:
            //  (TV;Ljava/lang/ref/ReferenceQueue<TV;>;)V
            // 
            // The error that occurred was:
            // 
            // com.strobel.assembler.metadata.MetadataHelper$AdaptFailure
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2300)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2221)
            //     at com.strobel.assembler.metadata.GenericParameter.accept(GenericParameter.java:85)
            //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2255)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2232)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitClassType(MetadataHelper.java:2239)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitClassType(MetadataHelper.java:2221)
            //     at com.strobel.assembler.metadata.TypeDefinition.accept(TypeDefinition.java:183)
            //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2255)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2232)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2245)
            //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2221)
            //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
            //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
            //     at com.strobel.assembler.metadata.MetadataHelper.adapt(MetadataHelper.java:1312)
            //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:932)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
            //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1061)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
            //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
            //     at com.strobel.decompiler.ast.TypeAnalysis.invalidateDependentExpressions(TypeAnalysis.java:759)
            //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1011)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:778)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2669)
            //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
            //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
            //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:586)
            //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:397)
            //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:576)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        static final class Helper<K, V> implements InternalEntryHelper<K, V, WeakKeyWeakValueEntry<K, V>, WeakKeyWeakValueSegment<K, V>>
        {
            private static final Helper<?, ?> INSTANCE;
            
            static {
                INSTANCE = new Helper<Object, Object>();
            }
            
            static <K, V> Helper<K, V> instance() {
                return (Helper<K, V>)Helper.INSTANCE;
            }
            
            public WeakKeyWeakValueEntry<K, V> copy(final WeakKeyWeakValueSegment<K, V> weakKeyWeakValueSegment, final WeakKeyWeakValueEntry<K, V> weakKeyWeakValueEntry, final WeakKeyWeakValueEntry<K, V> weakKeyWeakValueEntry2) {
                if (weakKeyWeakValueEntry.getKey() == null) {
                    return null;
                }
                if (Segment.isCollected((AbstractWeakKeyEntry<Object, Object, InternalEntry>)weakKeyWeakValueEntry)) {
                    return null;
                }
                return weakKeyWeakValueEntry.copy(((WeakKeyWeakValueSegment<Object, Object>)weakKeyWeakValueSegment).queueForKeys, ((WeakKeyWeakValueSegment<Object, Object>)weakKeyWeakValueSegment).queueForValues, weakKeyWeakValueEntry2);
            }
            
            @Override
            public Strength keyStrength() {
                return Strength.WEAK;
            }
            
            public WeakKeyWeakValueEntry<K, V> newEntry(final WeakKeyWeakValueSegment<K, V> weakKeyWeakValueSegment, final K k, final int n, final WeakKeyWeakValueEntry<K, V> weakKeyWeakValueEntry) {
                return new WeakKeyWeakValueEntry<K, V>(((WeakKeyWeakValueSegment<Object, Object>)weakKeyWeakValueSegment).queueForKeys, k, n, weakKeyWeakValueEntry);
            }
            
            public WeakKeyWeakValueSegment<K, V> newSegment(final MapMakerInternalMap<K, V, WeakKeyWeakValueEntry<K, V>, WeakKeyWeakValueSegment<K, V>> mapMakerInternalMap, final int n, final int n2) {
                return (WeakKeyWeakValueSegment<K, V>)new WeakKeyWeakValueSegment((MapMakerInternalMap<Object, Object, WeakKeyWeakValueEntry<Object, Object>, WeakKeyWeakValueSegment<Object, Object>>)mapMakerInternalMap, n, n2);
            }
            
            public void setValue(final WeakKeyWeakValueSegment<K, V> weakKeyWeakValueSegment, final WeakKeyWeakValueEntry<K, V> weakKeyWeakValueEntry, final V v) {
                weakKeyWeakValueEntry.setValue(v, ((WeakKeyWeakValueSegment<Object, Object>)weakKeyWeakValueSegment).queueForValues);
            }
            
            @Override
            public Strength valueStrength() {
                return Strength.WEAK;
            }
        }
    }
    
    static final class WeakKeyWeakValueSegment<K, V> extends Segment<K, V, WeakKeyWeakValueEntry<K, V>, WeakKeyWeakValueSegment<K, V>>
    {
        private final ReferenceQueue<K> queueForKeys;
        private final ReferenceQueue<V> queueForValues;
        
        WeakKeyWeakValueSegment(final MapMakerInternalMap<K, V, WeakKeyWeakValueEntry<K, V>, WeakKeyWeakValueSegment<K, V>> mapMakerInternalMap, final int n, final int n2) {
            super(mapMakerInternalMap, n, n2);
            this.queueForKeys = new ReferenceQueue<K>();
            this.queueForValues = new ReferenceQueue<V>();
        }
        
        public WeakKeyWeakValueEntry<K, V> castForTesting(final InternalEntry<K, V, ?> internalEntry) {
            return (WeakKeyWeakValueEntry<K, V>)(WeakKeyWeakValueEntry)internalEntry;
        }
        
        @Override
        void maybeClearReferenceQueues() {
            this.clearReferenceQueue(this.queueForKeys);
        }
        
        @Override
        void maybeDrainReferenceQueues() {
            this.drainKeyReferenceQueue(this.queueForKeys);
            this.drainValueReferenceQueue(this.queueForValues);
        }
        
        WeakKeyWeakValueSegment<K, V> self() {
            return this;
        }
    }
    
    interface WeakValueEntry<K, V, E extends InternalEntry<K, V, E>> extends InternalEntry<K, V, E>
    {
        WeakValueReference<K, V, E> getValueReference();
    }
    
    interface WeakValueReference<K, V, E extends InternalEntry<K, V, E>>
    {
        void clear();
        
        WeakValueReference<K, V, E> copyFor(final ReferenceQueue<V> p0, final E p1);
        
        V get();
        
        E getEntry();
    }
    
    static final class WeakValueReferenceImpl<K, V, E extends InternalEntry<K, V, E>> extends WeakReference<V> implements WeakValueReference<K, V, E>
    {
        final E entry;
        
        WeakValueReferenceImpl(final ReferenceQueue<V> q, final V referent, final E entry) {
            super(referent, q);
            this.entry = entry;
        }
        
        @Override
        public WeakValueReference<K, V, E> copyFor(final ReferenceQueue<V> referenceQueue, final E e) {
            return new WeakValueReferenceImpl((ReferenceQueue<Object>)referenceQueue, this.get(), e);
        }
        
        @Override
        public E getEntry() {
            return this.entry;
        }
    }
    
    final class WriteThroughEntry extends AbstractMapEntry<K, V>
    {
        final K key;
        V value;
        
        WriteThroughEntry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public boolean equals(final Object o) {
            final boolean b = o instanceof Entry;
            boolean b3;
            final boolean b2 = b3 = false;
            if (b) {
                final Entry entry = (Entry)o;
                b3 = b2;
                if (this.key.equals(entry.getKey())) {
                    b3 = b2;
                    if (this.value.equals(entry.getValue())) {
                        b3 = true;
                    }
                }
            }
            return b3;
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
        
        @Override
        public int hashCode() {
            return this.value.hashCode() ^ this.key.hashCode();
        }
        
        @Override
        public V setValue(final V value) {
            final Object put = MapMakerInternalMap.this.put(this.key, value);
            this.value = value;
            return (V)put;
        }
    }
}
