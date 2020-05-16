// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.common.base.Function;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Comparator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Set;
import java.util.SortedMap;
import java.util.Collection;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.Serializable;
import java.util.Map;

public abstract class ImmutableMap<K, V> implements Map<K, V>, Serializable
{
    @LazyInit
    private transient ImmutableSet<Entry<K, V>> entrySet;
    @LazyInit
    private transient ImmutableSet<K> keySet;
    @LazyInit
    private transient ImmutableCollection<V> values;
    
    ImmutableMap() {
    }
    
    public static <K, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }
    
    public static <K, V> ImmutableMap<K, V> copyOf(final Iterable<? extends Entry<? extends K, ? extends V>> iterable) {
        int size;
        if (iterable instanceof Collection) {
            size = ((Collection<? extends Entry<?, ?>>)iterable).size();
        }
        else {
            size = 4;
        }
        final Builder builder = new Builder<K, V>(size);
        builder.putAll(iterable);
        return builder.build();
    }
    
    public static <K, V> ImmutableMap<K, V> copyOf(final Map<? extends K, ? extends V> map) {
        if (map instanceof ImmutableMap && !(map instanceof SortedMap)) {
            final ImmutableMap<K, V> immutableMap = (ImmutableMap<K, V>)map;
            if (!immutableMap.isPartialView()) {
                return immutableMap;
            }
        }
        return copyOf((Iterable<? extends Entry<? extends K, ? extends V>>)map.entrySet());
    }
    
    @Deprecated
    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return this.get(o) != null;
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return this.values().contains(o);
    }
    
    abstract ImmutableSet<Entry<K, V>> createEntrySet();
    
    abstract ImmutableSet<K> createKeySet();
    
    abstract ImmutableCollection<V> createValues();
    
    @Override
    public ImmutableSet<Entry<K, V>> entrySet() {
        ImmutableSet<Entry<K, V>> entrySet;
        if ((entrySet = this.entrySet) == null) {
            entrySet = this.createEntrySet();
            this.entrySet = entrySet;
        }
        return entrySet;
    }
    
    @Override
    public boolean equals(final Object o) {
        return Maps.equalsImpl(this, o);
    }
    
    @Override
    public abstract V get(final Object p0);
    
    @Override
    public final V getOrDefault(Object value, V v) {
        value = this.get(value);
        if (value != null) {
            v = (V)value;
        }
        return v;
    }
    
    @Override
    public int hashCode() {
        return Sets.hashCodeImpl(this.entrySet());
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    boolean isHashCodeFast() {
        return false;
    }
    
    abstract boolean isPartialView();
    
    @Override
    public ImmutableSet<K> keySet() {
        ImmutableSet<K> keySet;
        if ((keySet = this.keySet) == null) {
            keySet = this.createKeySet();
            this.keySet = keySet;
        }
        return keySet;
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final V put(final K k, final V v) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public final void putAll(final Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final V remove(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        return Maps.toStringImpl(this);
    }
    
    @Override
    public ImmutableCollection<V> values() {
        ImmutableCollection<V> values;
        if ((values = this.values) == null) {
            values = this.createValues();
            this.values = values;
        }
        return values;
    }
    
    Object writeReplace() {
        return new SerializedForm(this);
    }
    
    public static class Builder<K, V>
    {
        Object[] alternatingKeysAndValues;
        boolean entriesUsed;
        int size;
        Comparator<? super V> valueComparator;
        
        public Builder() {
            this(4);
        }
        
        Builder(final int n) {
            this.alternatingKeysAndValues = new Object[n * 2];
            this.size = 0;
            this.entriesUsed = false;
        }
        
        private void ensureCapacity(int n) {
            n *= 2;
            final Object[] alternatingKeysAndValues = this.alternatingKeysAndValues;
            if (n > alternatingKeysAndValues.length) {
                this.alternatingKeysAndValues = Arrays.copyOf(alternatingKeysAndValues, ImmutableCollection.Builder.expandedCapacity(alternatingKeysAndValues.length, n));
                this.entriesUsed = false;
            }
        }
        
        public ImmutableMap<K, V> build() {
            this.sortEntries();
            this.entriesUsed = true;
            return (ImmutableMap<K, V>)RegularImmutableMap.create(this.size, this.alternatingKeysAndValues);
        }
        
        @CanIgnoreReturnValue
        public Builder<K, V> put(final K k, final V v) {
            this.ensureCapacity(this.size + 1);
            CollectPreconditions.checkEntryNotNull(k, v);
            final Object[] alternatingKeysAndValues = this.alternatingKeysAndValues;
            final int size = this.size;
            alternatingKeysAndValues[size * 2] = k;
            alternatingKeysAndValues[size * 2 + 1] = v;
            this.size = size + 1;
            return this;
        }
        
        @CanIgnoreReturnValue
        public Builder<K, V> put(final Entry<? extends K, ? extends V> entry) {
            return (Builder<K, V>)this.put(entry.getKey(), entry.getValue());
        }
        
        @CanIgnoreReturnValue
        public Builder<K, V> putAll(final Iterable<? extends Entry<? extends K, ? extends V>> iterable) {
            if (iterable instanceof Collection) {
                this.ensureCapacity(this.size + ((Collection<Entry>)iterable).size());
            }
            final Iterator<Entry> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                this.put(iterator.next());
            }
            return this;
        }
        
        void sortEntries() {
            if (this.valueComparator != null) {
                if (this.entriesUsed) {
                    this.alternatingKeysAndValues = Arrays.copyOf(this.alternatingKeysAndValues, this.size * 2);
                }
                final Entry[] a = new Entry[this.size];
                final int n = 0;
                int n2 = 0;
                int size;
                while (true) {
                    size = this.size;
                    if (n2 >= size) {
                        break;
                    }
                    final Object[] alternatingKeysAndValues = this.alternatingKeysAndValues;
                    final int n3 = n2 * 2;
                    a[n2] = (Entry)new AbstractMap.SimpleImmutableEntry(alternatingKeysAndValues[n3], alternatingKeysAndValues[n3 + 1]);
                    ++n2;
                }
                Arrays.sort((Entry[])a, 0, size, (Comparator<? super Entry>)Ordering.from(this.valueComparator).onResultOf((Function<Entry<?, ? extends V>, ? extends V>)Maps.valueFunction()));
                for (int i = n; i < this.size; ++i) {
                    final Object[] alternatingKeysAndValues2 = this.alternatingKeysAndValues;
                    final int n4 = i * 2;
                    alternatingKeysAndValues2[n4] = a[i].getKey();
                    this.alternatingKeysAndValues[n4 + 1] = a[i].getValue();
                }
            }
        }
    }
    
    static class SerializedForm implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Object[] keys;
        private final Object[] values;
        
        SerializedForm(final ImmutableMap<?, ?> immutableMap) {
            this.keys = new Object[immutableMap.size()];
            this.values = new Object[immutableMap.size()];
            final UnmodifiableIterator<Entry<Object, ?>> iterator = immutableMap.entrySet().iterator();
            int n = 0;
            while (iterator.hasNext()) {
                final Entry<Object, V> entry = iterator.next();
                this.keys[n] = entry.getKey();
                this.values[n] = entry.getValue();
                ++n;
            }
        }
        
        Object createMap(final Builder<Object, Object> builder) {
            int n = 0;
            while (true) {
                final Object[] keys = this.keys;
                if (n >= keys.length) {
                    break;
                }
                builder.put(keys[n], this.values[n]);
                ++n;
            }
            return builder.build();
        }
        
        Object readResolve() {
            return this.createMap((Builder<Object, Object>)new Builder(this.keys.length));
        }
    }
}
