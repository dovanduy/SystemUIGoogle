// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Arrays;
import java.util.Iterator;
import java.util.AbstractMap;
import java.util.Collection;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.SortedMap;
import java.util.Set;
import java.util.NavigableSet;
import java.io.Serializable;
import java.util.Map;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.NavigableMap;

public final class ImmutableSortedMap<K, V> extends ImmutableSortedMapFauxverideShim<K, V> implements NavigableMap<K, V>
{
    private static final ImmutableSortedMap<Comparable, Object> NATURAL_EMPTY_MAP;
    private static final long serialVersionUID = 0L;
    private transient ImmutableSortedMap<K, V> descendingMap;
    private final transient RegularImmutableSortedSet<K> keySet;
    private final transient ImmutableList<V> valueList;
    
    static {
        Ordering.natural();
        NATURAL_EMPTY_MAP = new ImmutableSortedMap<Comparable, Object>((RegularImmutableSortedSet<Comparable>)ImmutableSortedSet.emptySet((Comparator<? super Comparable>)Ordering.natural()), ImmutableList.of());
    }
    
    ImmutableSortedMap(final RegularImmutableSortedSet<K> set, final ImmutableList<V> list) {
        this(set, list, null);
    }
    
    ImmutableSortedMap(final RegularImmutableSortedSet<K> keySet, final ImmutableList<V> valueList, final ImmutableSortedMap<K, V> descendingMap) {
        this.keySet = keySet;
        this.valueList = valueList;
        this.descendingMap = descendingMap;
    }
    
    static <K, V> ImmutableSortedMap<K, V> emptyMap(final Comparator<? super K> obj) {
        if (Ordering.natural().equals(obj)) {
            return of();
        }
        return new ImmutableSortedMap<K, V>(ImmutableSortedSet.emptySet(obj), ImmutableList.of());
    }
    
    private ImmutableSortedMap<K, V> getSubMap(final int n, final int n2) {
        if (n == 0 && n2 == this.size()) {
            return this;
        }
        if (n == n2) {
            return emptyMap(this.comparator());
        }
        return new ImmutableSortedMap<K, V>(this.keySet.getSubSet(n, n2), this.valueList.subList(n, n2));
    }
    
    public static <K, V> ImmutableSortedMap<K, V> of() {
        return (ImmutableSortedMap<K, V>)ImmutableSortedMap.NATURAL_EMPTY_MAP;
    }
    
    private static <K, V> ImmutableSortedMap<K, V> of(final Comparator<? super K> comparator, final K k, final V v) {
        final ImmutableList<K> of = ImmutableList.of(k);
        Preconditions.checkNotNull(comparator);
        return new ImmutableSortedMap<K, V>(new RegularImmutableSortedSet<Object>((ImmutableList<Object>)of, (Comparator<? super Object>)comparator), (ImmutableList<Object>)ImmutableList.of(v));
    }
    
    @Override
    public Entry<K, V> ceilingEntry(final K k) {
        return this.tailMap(k, true).firstEntry();
    }
    
    @Override
    public K ceilingKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.ceilingEntry((K)k));
    }
    
    @Override
    public Comparator<? super K> comparator() {
        return this.keySet().comparator();
    }
    
    @Override
    ImmutableSet<Entry<K, V>> createEntrySet() {
        Serializable of;
        if (this.isEmpty()) {
            of = ImmutableSet.of();
        }
        else {
            of = new EntrySet();
        }
        return (ImmutableSet<Entry<K, V>>)of;
    }
    
    @Override
    ImmutableSet<K> createKeySet() {
        throw new AssertionError((Object)"should never be called");
    }
    
    @Override
    ImmutableCollection<V> createValues() {
        throw new AssertionError((Object)"should never be called");
    }
    
    @Override
    public ImmutableSortedSet<K> descendingKeySet() {
        return this.keySet.descendingSet();
    }
    
    @Override
    public ImmutableSortedMap<K, V> descendingMap() {
        ImmutableSortedMap<K, V> descendingMap;
        if ((descendingMap = this.descendingMap) == null) {
            if (this.isEmpty()) {
                return emptyMap((Comparator<? super K>)Ordering.from(this.comparator()).reverse());
            }
            descendingMap = new ImmutableSortedMap<K, V>((RegularImmutableSortedSet)this.keySet.descendingSet(), this.valueList.reverse(), this);
        }
        return descendingMap;
    }
    
    @Override
    public ImmutableSet<Entry<K, V>> entrySet() {
        return super.entrySet();
    }
    
    @Override
    public Entry<K, V> firstEntry() {
        Entry<K, V> entry;
        if (this.isEmpty()) {
            entry = null;
        }
        else {
            entry = (Entry<K, V>)this.entrySet().asList().get(0);
        }
        return entry;
    }
    
    @Override
    public K firstKey() {
        return this.keySet().first();
    }
    
    @Override
    public Entry<K, V> floorEntry(final K k) {
        return this.headMap(k, true).lastEntry();
    }
    
    @Override
    public K floorKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.floorEntry((K)k));
    }
    
    @Override
    public V get(Object value) {
        final int index = this.keySet.indexOf(value);
        if (index == -1) {
            value = null;
        }
        else {
            value = this.valueList.get(index);
        }
        return (V)value;
    }
    
    @Override
    public ImmutableSortedMap<K, V> headMap(final K k) {
        return this.headMap(k, false);
    }
    
    @Override
    public ImmutableSortedMap<K, V> headMap(final K k, final boolean b) {
        final RegularImmutableSortedSet<K> keySet = this.keySet;
        Preconditions.checkNotNull(k);
        return this.getSubMap(0, keySet.headIndex(k, b));
    }
    
    @Override
    public Entry<K, V> higherEntry(final K k) {
        return this.tailMap(k, false).firstEntry();
    }
    
    @Override
    public K higherKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.higherEntry((K)k));
    }
    
    @Override
    boolean isPartialView() {
        return this.keySet.isPartialView() || this.valueList.isPartialView();
    }
    
    @Override
    public ImmutableSortedSet<K> keySet() {
        return this.keySet;
    }
    
    @Override
    public Entry<K, V> lastEntry() {
        Entry<K, V> entry;
        if (this.isEmpty()) {
            entry = null;
        }
        else {
            entry = (Entry<K, V>)this.entrySet().asList().get(this.size() - 1);
        }
        return entry;
    }
    
    @Override
    public K lastKey() {
        return this.keySet().last();
    }
    
    @Override
    public Entry<K, V> lowerEntry(final K k) {
        return this.headMap(k, false).lastEntry();
    }
    
    @Override
    public K lowerKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.lowerEntry((K)k));
    }
    
    @Override
    public ImmutableSortedSet<K> navigableKeySet() {
        return this.keySet;
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final Entry<K, V> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final Entry<K, V> pollLastEntry() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int size() {
        return this.valueList.size();
    }
    
    @Override
    public ImmutableSortedMap<K, V> subMap(final K k, final K i) {
        return this.subMap(k, true, i, false);
    }
    
    @Override
    public ImmutableSortedMap<K, V> subMap(final K k, final boolean b, final K i, final boolean b2) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(i);
        Preconditions.checkArgument(this.comparator().compare((Object)k, (Object)i) <= 0, "expected fromKey <= toKey but %s > %s", k, i);
        return this.headMap(i, b2).tailMap(k, b);
    }
    
    @Override
    public ImmutableSortedMap<K, V> tailMap(final K k) {
        return this.tailMap(k, true);
    }
    
    @Override
    public ImmutableSortedMap<K, V> tailMap(final K k, final boolean b) {
        final RegularImmutableSortedSet<K> keySet = this.keySet;
        Preconditions.checkNotNull(k);
        return this.getSubMap(keySet.tailIndex(k, b), this.size());
    }
    
    @Override
    public ImmutableCollection<V> values() {
        return this.valueList;
    }
    
    @Override
    Object writeReplace() {
        return new SerializedForm(this);
    }
    
    class EntrySet extends ImmutableMapEntrySet<K, V>
    {
        @Override
        ImmutableList<Entry<K, V>> createAsList() {
            return new ImmutableList<Entry<K, V>>() {
                @Override
                public Entry<K, V> get(final int n) {
                    return (Entry<K, V>)new AbstractMap.SimpleImmutableEntry(ImmutableSortedMap.this.keySet.asList().get(n), ImmutableSortedMap.this.valueList.get(n));
                }
                
                @Override
                boolean isPartialView() {
                    return true;
                }
                
                @Override
                public int size() {
                    return ImmutableSortedMap.this.size();
                }
            };
        }
        
        @Override
        public UnmodifiableIterator<Entry<K, V>> iterator() {
            return (UnmodifiableIterator<Entry<K, V>>)this.asList().iterator();
        }
        
        @Override
        ImmutableMap<K, V> map() {
            return (ImmutableMap<K, V>)ImmutableSortedMap.this;
        }
    }
    
    public static class Builder<K, V> extends ImmutableMap.Builder<K, V>
    {
        private final Comparator<? super K> comparator;
        private transient Object[] keys;
        private transient Object[] values;
        
        public Builder(final Comparator<? super K> comparator) {
            this(comparator, 4);
        }
        
        private Builder(final Comparator<? super K> comparator, final int n) {
            Preconditions.checkNotNull(comparator);
            this.comparator = comparator;
            this.keys = new Object[n];
            this.values = new Object[n];
        }
        
        private void ensureCapacity(int expandedCapacity) {
            final Object[] keys = this.keys;
            if (expandedCapacity > keys.length) {
                expandedCapacity = ImmutableCollection.Builder.expandedCapacity(keys.length, expandedCapacity);
                this.keys = Arrays.copyOf(this.keys, expandedCapacity);
                this.values = Arrays.copyOf(this.values, expandedCapacity);
            }
        }
        
        public ImmutableSortedMap<K, V> build() {
            final int size = super.size;
            if (size == 0) {
                return ImmutableSortedMap.emptyMap(this.comparator);
            }
            int i = 0;
            if (size != 1) {
                final Object[] copy = Arrays.copyOf(this.keys, size);
                Arrays.sort(copy, (Comparator<? super Object>)this.comparator);
                final Object[] array = new Object[super.size];
                while (i < super.size) {
                    if (i > 0) {
                        final Comparator<? super K> comparator = this.comparator;
                        final int n = i - 1;
                        if (comparator.compare(copy[n], copy[i]) == 0) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("keys required to be distinct but compared as equal: ");
                            sb.append(copy[n]);
                            sb.append(" and ");
                            sb.append(copy[i]);
                            throw new IllegalArgumentException(sb.toString());
                        }
                    }
                    array[Arrays.binarySearch(copy, this.keys[i], (Comparator<? super Object>)this.comparator)] = this.values[i];
                    ++i;
                }
                return new ImmutableSortedMap<K, V>(new RegularImmutableSortedSet<Object>(ImmutableList.asImmutableList(copy), (Comparator<? super Object>)this.comparator), ImmutableList.asImmutableList(array));
            }
            return (ImmutableSortedMap<K, V>)of(this.comparator, this.keys[0], this.values[0]);
        }
        
        @CanIgnoreReturnValue
        public Builder<K, V> put(final K k, final V v) {
            this.ensureCapacity(super.size + 1);
            CollectPreconditions.checkEntryNotNull(k, v);
            final Object[] keys = this.keys;
            final int size = super.size;
            keys[size] = k;
            this.values[size] = v;
            super.size = size + 1;
            return this;
        }
        
        @CanIgnoreReturnValue
        public Builder<K, V> put(final Entry<? extends K, ? extends V> entry) {
            super.put(entry);
            return this;
        }
        
        @CanIgnoreReturnValue
        public Builder<K, V> putAll(final Iterable<? extends Entry<? extends K, ? extends V>> iterable) {
            super.putAll(iterable);
            return this;
        }
    }
    
    private static class SerializedForm extends ImmutableMap.SerializedForm
    {
        private static final long serialVersionUID = 0L;
        private final Comparator<Object> comparator;
        
        SerializedForm(final ImmutableSortedMap<?, ?> immutableSortedMap) {
            super(immutableSortedMap);
            this.comparator = (Comparator<Object>)immutableSortedMap.comparator();
        }
        
        @Override
        Object readResolve() {
            return ((ImmutableMap.SerializedForm)this).createMap(new ImmutableSortedMap.Builder<Object, Object>(this.comparator));
        }
    }
}
