// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.Map;

abstract class AbstractMultimap<K, V> implements Multimap<K, V>
{
    private transient Map<K, Collection<V>> asMap;
    private transient Collection<Map.Entry<K, V>> entries;
    private transient Set<K> keySet;
    
    @Override
    public Map<K, Collection<V>> asMap() {
        Map<K, Collection<V>> asMap;
        if ((asMap = this.asMap) == null) {
            asMap = this.createAsMap();
            this.asMap = asMap;
        }
        return asMap;
    }
    
    @Override
    public boolean containsEntry(final Object o, final Object o2) {
        final Collection<V> collection = this.asMap().get(o);
        return collection != null && collection.contains(o2);
    }
    
    abstract Map<K, Collection<V>> createAsMap();
    
    abstract Collection<Map.Entry<K, V>> createEntries();
    
    abstract Set<K> createKeySet();
    
    public Collection<Map.Entry<K, V>> entries() {
        Collection<Map.Entry<K, V>> entries;
        if ((entries = this.entries) == null) {
            entries = this.createEntries();
            this.entries = entries;
        }
        return entries;
    }
    
    abstract Iterator<Map.Entry<K, V>> entryIterator();
    
    @Override
    public boolean equals(final Object o) {
        return Multimaps.equalsImpl(this, o);
    }
    
    @Override
    public int hashCode() {
        return this.asMap().hashCode();
    }
    
    public Set<K> keySet() {
        Set<K> keySet;
        if ((keySet = this.keySet) == null) {
            keySet = this.createKeySet();
            this.keySet = keySet;
        }
        return keySet;
    }
    
    @CanIgnoreReturnValue
    @Override
    public boolean remove(final Object o, final Object o2) {
        final Collection<V> collection = this.asMap().get(o);
        return collection != null && collection.remove(o2);
    }
    
    @Override
    public String toString() {
        return this.asMap().toString();
    }
    
    class Entries extends Multimaps.Entries<K, V>
    {
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return AbstractMultimap.this.entryIterator();
        }
        
        @Override
        Multimap<K, V> multimap() {
            return (Multimap<K, V>)AbstractMultimap.this;
        }
    }
    
    class EntrySet extends Entries implements Set<Map.Entry<K, V>>
    {
        EntrySet(final AbstractMultimap abstractMultimap) {
            abstractMultimap.super();
        }
        
        @Override
        public boolean equals(final Object o) {
            return Sets.equalsImpl(this, o);
        }
        
        @Override
        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }
    }
}
