// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ConcurrentModificationException;
import java.util.Arrays;
import com.google.common.base.Objects;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Iterator;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Collection;
import java.io.ObjectInputStream;

public final class LinkedHashMultimap<K, V> extends LinkedHashMultimapGwtSerializationDependencies<K, V>
{
    static final double VALUE_SET_LOAD_FACTOR = 1.0;
    private static final long serialVersionUID = 1L;
    private transient ValueEntry<K, V> multimapHeaderEntry;
    transient int valueSetCapacity;
    
    private static <K, V> void deleteFromMultimap(final ValueEntry<K, V> valueEntry) {
        succeedsInMultimap(valueEntry.getPredecessorInMultimap(), valueEntry.getSuccessorInMultimap());
    }
    
    private static <K, V> void deleteFromValueSet(final ValueSetLink<K, V> valueSetLink) {
        succeedsInValueSet(valueSetLink.getPredecessorInValueSet(), valueSetLink.getSuccessorInValueSet());
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final int n = 0;
        final ValueEntry<K, V> multimapHeaderEntry = new ValueEntry<K, V>(null, null, 0, null);
        succeedsInMultimap(this.multimapHeaderEntry = multimapHeaderEntry, multimapHeaderEntry);
        this.valueSetCapacity = 2;
        final int int1 = objectInputStream.readInt();
        final Map<K, Collection<V>> linkedHashMapWithExpectedSize = Platform.newLinkedHashMapWithExpectedSize(12);
        for (int i = 0; i < int1; ++i) {
            final Object object = objectInputStream.readObject();
            linkedHashMapWithExpectedSize.put((K)object, this.createCollection((K)object));
        }
        for (int int2 = objectInputStream.readInt(), j = n; j < int2; ++j) {
            linkedHashMapWithExpectedSize.get(objectInputStream.readObject()).add((V)objectInputStream.readObject());
        }
        this.setMap(linkedHashMapWithExpectedSize);
    }
    
    private static <K, V> void succeedsInMultimap(final ValueEntry<K, V> predecessorInMultimap, final ValueEntry<K, V> successorInMultimap) {
        predecessorInMultimap.setSuccessorInMultimap(successorInMultimap);
        successorInMultimap.setPredecessorInMultimap(predecessorInMultimap);
    }
    
    private static <K, V> void succeedsInValueSet(final ValueSetLink<K, V> predecessorInValueSet, final ValueSetLink<K, V> successorInValueSet) {
        predecessorInValueSet.setSuccessorInValueSet(successorInValueSet);
        successorInValueSet.setPredecessorInValueSet(predecessorInValueSet);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.keySet().size());
        final Iterator<K> iterator = this.keySet().iterator();
        while (iterator.hasNext()) {
            objectOutputStream.writeObject(iterator.next());
        }
        objectOutputStream.writeInt(this.size());
        for (final Map.Entry<Object, V> entry : this.entries()) {
            objectOutputStream.writeObject(entry.getKey());
            objectOutputStream.writeObject(entry.getValue());
        }
    }
    
    @Override
    public void clear() {
        super.clear();
        final ValueEntry<K, V> multimapHeaderEntry = this.multimapHeaderEntry;
        succeedsInMultimap((ValueEntry<Object, Object>)multimapHeaderEntry, (ValueEntry<Object, Object>)multimapHeaderEntry);
    }
    
    @Override
    Collection<V> createCollection(final K k) {
        return new ValueSet(k, this.valueSetCapacity);
    }
    
    @Override
    Set<V> createCollection() {
        return Platform.newLinkedHashSetWithExpectedSize(this.valueSetCapacity);
    }
    
    @Override
    public Set<Map.Entry<K, V>> entries() {
        return super.entries();
    }
    
    @Override
    Iterator<Map.Entry<K, V>> entryIterator() {
        return new Iterator<Map.Entry<K, V>>() {
            ValueEntry<K, V> nextEntry = LinkedHashMultimap.this.multimapHeaderEntry.successorInMultimap;
            ValueEntry<K, V> toRemove;
            
            @Override
            public boolean hasNext() {
                return this.nextEntry != LinkedHashMultimap.this.multimapHeaderEntry;
            }
            
            @Override
            public Map.Entry<K, V> next() {
                if (this.hasNext()) {
                    final ValueEntry<K, V> nextEntry = this.nextEntry;
                    this.toRemove = nextEntry;
                    this.nextEntry = nextEntry.successorInMultimap;
                    return nextEntry;
                }
                throw new NoSuchElementException();
            }
            
            @Override
            public void remove() {
                CollectPreconditions.checkRemove(this.toRemove != null);
                LinkedHashMultimap.this.remove(this.toRemove.getKey(), this.toRemove.getValue());
                this.toRemove = null;
            }
        };
    }
    
    @Override
    public Set<K> keySet() {
        return super.keySet();
    }
    
    static final class ValueEntry<K, V> extends ImmutableEntry<K, V> implements ValueSetLink<K, V>
    {
        ValueEntry<K, V> nextInValueBucket;
        ValueEntry<K, V> predecessorInMultimap;
        ValueSetLink<K, V> predecessorInValueSet;
        final int smearedValueHash;
        ValueEntry<K, V> successorInMultimap;
        ValueSetLink<K, V> successorInValueSet;
        
        ValueEntry(final K k, final V v, final int smearedValueHash, final ValueEntry<K, V> nextInValueBucket) {
            super(k, v);
            this.smearedValueHash = smearedValueHash;
            this.nextInValueBucket = nextInValueBucket;
        }
        
        public ValueEntry<K, V> getPredecessorInMultimap() {
            return this.predecessorInMultimap;
        }
        
        @Override
        public ValueSetLink<K, V> getPredecessorInValueSet() {
            return this.predecessorInValueSet;
        }
        
        public ValueEntry<K, V> getSuccessorInMultimap() {
            return this.successorInMultimap;
        }
        
        @Override
        public ValueSetLink<K, V> getSuccessorInValueSet() {
            return this.successorInValueSet;
        }
        
        boolean matchesValue(final Object o, final int n) {
            return this.smearedValueHash == n && Objects.equal(this.getValue(), o);
        }
        
        public void setPredecessorInMultimap(final ValueEntry<K, V> predecessorInMultimap) {
            this.predecessorInMultimap = predecessorInMultimap;
        }
        
        @Override
        public void setPredecessorInValueSet(final ValueSetLink<K, V> predecessorInValueSet) {
            this.predecessorInValueSet = predecessorInValueSet;
        }
        
        public void setSuccessorInMultimap(final ValueEntry<K, V> successorInMultimap) {
            this.successorInMultimap = successorInMultimap;
        }
        
        @Override
        public void setSuccessorInValueSet(final ValueSetLink<K, V> successorInValueSet) {
            this.successorInValueSet = successorInValueSet;
        }
    }
    
    final class ValueSet extends ImprovedAbstractSet<V> implements ValueSetLink<K, V>
    {
        private ValueSetLink<K, V> firstEntry;
        ValueEntry<K, V>[] hashTable;
        private final K key;
        private ValueSetLink<K, V> lastEntry;
        private int modCount;
        private int size;
        
        ValueSet(final K key, final int n) {
            this.size = 0;
            this.modCount = 0;
            this.key = key;
            this.firstEntry = this;
            this.lastEntry = this;
            this.hashTable = (ValueEntry<K, V>[])new ValueEntry[Hashing.closedTableSize(n, 1.0)];
        }
        
        private int mask() {
            return this.hashTable.length - 1;
        }
        
        private void rehashIfNecessary() {
            if (Hashing.needsResizing(this.size, this.hashTable.length, 1.0)) {
                final int n = this.hashTable.length * 2;
                final ValueEntry[] hashTable = new ValueEntry[n];
                this.hashTable = (ValueEntry<K, V>[])hashTable;
                for (Object o = this.firstEntry; o != this; o = ((ValueSetLink<Object, Object>)o).getSuccessorInValueSet()) {
                    final ValueEntry valueEntry = (ValueEntry)o;
                    final int n2 = valueEntry.smearedValueHash & n - 1;
                    valueEntry.nextInValueBucket = hashTable[n2];
                    hashTable[n2] = valueEntry;
                }
            }
        }
        
        @Override
        public boolean add(final V v) {
            final int smearedHash = Hashing.smearedHash(v);
            final int n = this.mask() & smearedHash;
            ValueSetLink<K, V> nextInValueBucket;
            ValueEntry<K, V> valueEntry;
            for (valueEntry = (ValueEntry<K, V>)(nextInValueBucket = (ValueSetLink<K, V>)this.hashTable[n]); nextInValueBucket != null; nextInValueBucket = ((ValueEntry)nextInValueBucket).nextInValueBucket) {
                if (((ValueEntry)nextInValueBucket).matchesValue(v, smearedHash)) {
                    return false;
                }
            }
            final ValueEntry valueEntry2 = new ValueEntry(this.key, v, smearedHash, (ValueEntry<Object, Object>)valueEntry);
            succeedsInValueSet((ValueSetLink)this.lastEntry, (ValueSetLink<Object, Object>)valueEntry2);
            succeedsInValueSet((ValueSetLink)valueEntry2, (ValueSetLink<Object, Object>)this);
            succeedsInMultimap(LinkedHashMultimap.this.multimapHeaderEntry.getPredecessorInMultimap(), (ValueEntry<Object, Object>)valueEntry2);
            succeedsInMultimap(valueEntry2, (ValueEntry<Object, Object>)LinkedHashMultimap.this.multimapHeaderEntry);
            this.hashTable[n] = (ValueEntry<K, V>)valueEntry2;
            ++this.size;
            ++this.modCount;
            this.rehashIfNecessary();
            return true;
        }
        
        @Override
        public void clear() {
            Arrays.fill(this.hashTable, null);
            this.size = 0;
            for (Object o = this.firstEntry; o != this; o = ((ValueSetLink<K, V>)o).getSuccessorInValueSet()) {
                deleteFromMultimap((ValueEntry<Object, Object>)o);
            }
            succeedsInValueSet((ValueSetLink)this, (ValueSetLink<Object, Object>)this);
            ++this.modCount;
        }
        
        @Override
        public boolean contains(final Object o) {
            final int smearedHash = Hashing.smearedHash(o);
            for (ValueSetLink<K, V> nextInValueBucket = (ValueSetLink<K, V>)this.hashTable[this.mask() & smearedHash]; nextInValueBucket != null; nextInValueBucket = ((ValueEntry)nextInValueBucket).nextInValueBucket) {
                if (((ValueEntry)nextInValueBucket).matchesValue(o, smearedHash)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public ValueSetLink<K, V> getPredecessorInValueSet() {
            return this.lastEntry;
        }
        
        @Override
        public ValueSetLink<K, V> getSuccessorInValueSet() {
            return this.firstEntry;
        }
        
        @Override
        public Iterator<V> iterator() {
            return new Iterator<V>() {
                int expectedModCount = ValueSet.this.modCount;
                ValueSetLink<K, V> nextEntry = ValueSet.this.firstEntry;
                ValueEntry<K, V> toRemove;
                
                private void checkForComodification() {
                    if (ValueSet.this.modCount == this.expectedModCount) {
                        return;
                    }
                    throw new ConcurrentModificationException();
                }
                
                @Override
                public boolean hasNext() {
                    this.checkForComodification();
                    return this.nextEntry != ValueSet.this;
                }
                
                @Override
                public V next() {
                    if (this.hasNext()) {
                        final ValueEntry toRemove = (ValueEntry)this.nextEntry;
                        final V value = toRemove.getValue();
                        this.toRemove = (ValueEntry<K, V>)toRemove;
                        this.nextEntry = toRemove.getSuccessorInValueSet();
                        return value;
                    }
                    throw new NoSuchElementException();
                }
                
                @Override
                public void remove() {
                    this.checkForComodification();
                    CollectPreconditions.checkRemove(this.toRemove != null);
                    ValueSet.this.remove(this.toRemove.getValue());
                    this.expectedModCount = ValueSet.this.modCount;
                    this.toRemove = null;
                }
            };
        }
        
        @CanIgnoreReturnValue
        @Override
        public boolean remove(final Object o) {
            final int smearedHash = Hashing.smearedHash(o);
            final int n = this.mask() & smearedHash;
            ValueEntry<K, V> valueEntry = this.hashTable[n];
            ValueEntry<K, V> valueEntry2 = null;
            while (valueEntry != null) {
                if (valueEntry.matchesValue(o, smearedHash)) {
                    if (valueEntry2 == null) {
                        this.hashTable[n] = valueEntry.nextInValueBucket;
                    }
                    else {
                        valueEntry2.nextInValueBucket = valueEntry.nextInValueBucket;
                    }
                    deleteFromValueSet((ValueSetLink<Object, Object>)(ValueSetLink)valueEntry);
                    deleteFromMultimap((ValueEntry<Object, Object>)valueEntry);
                    --this.size;
                    ++this.modCount;
                    return true;
                }
                final ValueEntry<K, V> nextInValueBucket = valueEntry.nextInValueBucket;
                valueEntry2 = valueEntry;
                valueEntry = nextInValueBucket;
            }
            return false;
        }
        
        @Override
        public void setPredecessorInValueSet(final ValueSetLink<K, V> lastEntry) {
            this.lastEntry = lastEntry;
        }
        
        @Override
        public void setSuccessorInValueSet(final ValueSetLink<K, V> firstEntry) {
            this.firstEntry = firstEntry;
        }
        
        @Override
        public int size() {
            return this.size;
        }
    }
    
    private interface ValueSetLink<K, V>
    {
        ValueSetLink<K, V> getPredecessorInValueSet();
        
        ValueSetLink<K, V> getSuccessorInValueSet();
        
        void setPredecessorInValueSet(final ValueSetLink<K, V> p0);
        
        void setSuccessorInValueSet(final ValueSetLink<K, V> p0);
    }
}
