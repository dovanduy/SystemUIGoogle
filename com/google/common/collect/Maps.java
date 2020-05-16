// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.AbstractCollection;
import java.util.SortedSet;
import java.util.AbstractMap;
import java.util.HashSet;
import com.google.common.base.Objects;
import java.util.Collection;
import java.util.SortedMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.Comparator;
import java.util.NavigableMap;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;

public final class Maps
{
    static int capacity(final int n) {
        if (n < 3) {
            CollectPreconditions.checkNonnegative(n, "expectedSize");
            return n + 1;
        }
        if (n < 1073741824) {
            return (int)(n / 0.75f + 1.0f);
        }
        return Integer.MAX_VALUE;
    }
    
    static boolean equalsImpl(final Map<?, ?> map, final Object o) {
        return map == o || (o instanceof Map && map.entrySet().equals(((Map)o).entrySet()));
    }
    
    public static <K, V> Map.Entry<K, V> immutableEntry(final K k, final V v) {
        return new ImmutableEntry<K, V>(k, v);
    }
    
    static <K, V> Iterator<K> keyIterator(final Iterator<Map.Entry<K, V>> iterator) {
        return new TransformedIterator<Map.Entry<K, V>, K>(iterator) {
            @Override
            K transform(final Map.Entry<K, V> entry) {
                return entry.getKey();
            }
        };
    }
    
    static <K> K keyOrNull(final Map.Entry<K, ?> entry) {
        K key;
        if (entry == null) {
            key = null;
        }
        else {
            key = entry.getKey();
        }
        return key;
    }
    
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<K, V>();
    }
    
    static boolean safeContainsKey(final Map<?, ?> map, final Object o) {
        Preconditions.checkNotNull(map);
        try {
            return map.containsKey(o);
        }
        catch (ClassCastException | NullPointerException ex) {
            return false;
        }
    }
    
    static <V> V safeGet(final Map<?, V> map, final Object o) {
        Preconditions.checkNotNull(map);
        try {
            return map.get(o);
        }
        catch (ClassCastException | NullPointerException ex) {
            return null;
        }
    }
    
    static <V> V safeRemove(final Map<?, V> map, final Object o) {
        Preconditions.checkNotNull(map);
        try {
            return map.remove(o);
        }
        catch (ClassCastException | NullPointerException ex) {
            return null;
        }
    }
    
    static String toStringImpl(final Map<?, ?> map) {
        final StringBuilder stringBuilderForCollection = Collections2.newStringBuilderForCollection(map.size());
        stringBuilderForCollection.append('{');
        final Iterator<Map.Entry<?, ?>> iterator = map.entrySet().iterator();
        int n = 1;
        while (iterator.hasNext()) {
            final Map.Entry<Object, V> entry = iterator.next();
            if (n == 0) {
                stringBuilderForCollection.append(", ");
            }
            n = 0;
            stringBuilderForCollection.append(entry.getKey());
            stringBuilderForCollection.append('=');
            stringBuilderForCollection.append(entry.getValue());
        }
        stringBuilderForCollection.append('}');
        return stringBuilderForCollection.toString();
    }
    
    static <V> Function<Map.Entry<?, V>, V> valueFunction() {
        return (Function<Map.Entry<?, V>, V>)EntryFunction.VALUE;
    }
    
    static <K, V> Iterator<V> valueIterator(final Iterator<Map.Entry<K, V>> iterator) {
        return new TransformedIterator<Map.Entry<K, V>, V>(iterator) {
            @Override
            V transform(final Map.Entry<K, V> entry) {
                return entry.getValue();
            }
        };
    }
    
    abstract static class DescendingMap<K, V> extends ForwardingMap<K, V> implements NavigableMap<K, V>
    {
        private transient Comparator<? super K> comparator;
        private transient Set<Entry<K, V>> entrySet;
        private transient NavigableSet<K> navigableKeySet;
        
        private static <T> Ordering<T> reverse(final Comparator<T> comparator) {
            return Ordering.from(comparator).reverse();
        }
        
        @Override
        public Entry<K, V> ceilingEntry(final K k) {
            return this.forward().floorEntry(k);
        }
        
        @Override
        public K ceilingKey(final K k) {
            return this.forward().floorKey(k);
        }
        
        @Override
        public Comparator<? super K> comparator() {
            Comparator<? super Object> comparator;
            if ((comparator = (Comparator<? super Object>)this.comparator) == null) {
                Object o;
                if ((o = this.forward().comparator()) == null) {
                    o = Ordering.natural();
                }
                comparator = reverse((Comparator<Object>)o);
                this.comparator = comparator;
            }
            return comparator;
        }
        
        Set<Entry<K, V>> createEntrySet() {
            return (Set<Entry<K, V>>)new EntrySetImpl();
        }
        
        @Override
        protected final Map<K, V> delegate() {
            return this.forward();
        }
        
        @Override
        public NavigableSet<K> descendingKeySet() {
            return this.forward().navigableKeySet();
        }
        
        @Override
        public NavigableMap<K, V> descendingMap() {
            return this.forward();
        }
        
        abstract Iterator<Entry<K, V>> entryIterator();
        
        @Override
        public Set<Entry<K, V>> entrySet() {
            Set<Entry<K, V>> entrySet;
            if ((entrySet = this.entrySet) == null) {
                entrySet = this.createEntrySet();
                this.entrySet = entrySet;
            }
            return entrySet;
        }
        
        @Override
        public Entry<K, V> firstEntry() {
            return this.forward().lastEntry();
        }
        
        @Override
        public K firstKey() {
            return this.forward().lastKey();
        }
        
        @Override
        public Entry<K, V> floorEntry(final K k) {
            return this.forward().ceilingEntry(k);
        }
        
        @Override
        public K floorKey(final K k) {
            return this.forward().ceilingKey(k);
        }
        
        abstract NavigableMap<K, V> forward();
        
        @Override
        public NavigableMap<K, V> headMap(final K k, final boolean b) {
            return this.forward().tailMap(k, b).descendingMap();
        }
        
        @Override
        public SortedMap<K, V> headMap(final K k) {
            return this.headMap(k, false);
        }
        
        @Override
        public Entry<K, V> higherEntry(final K k) {
            return this.forward().lowerEntry(k);
        }
        
        @Override
        public K higherKey(final K k) {
            return this.forward().lowerKey(k);
        }
        
        @Override
        public Set<K> keySet() {
            return this.navigableKeySet();
        }
        
        @Override
        public Entry<K, V> lastEntry() {
            return this.forward().firstEntry();
        }
        
        @Override
        public K lastKey() {
            return this.forward().firstKey();
        }
        
        @Override
        public Entry<K, V> lowerEntry(final K k) {
            return this.forward().higherEntry(k);
        }
        
        @Override
        public K lowerKey(final K k) {
            return this.forward().higherKey(k);
        }
        
        @Override
        public NavigableSet<K> navigableKeySet() {
            NavigableSet<K> navigableKeySet;
            if ((navigableKeySet = this.navigableKeySet) == null) {
                navigableKeySet = new NavigableKeySet<K, Object>(this);
                this.navigableKeySet = navigableKeySet;
            }
            return navigableKeySet;
        }
        
        @Override
        public Entry<K, V> pollFirstEntry() {
            return this.forward().pollLastEntry();
        }
        
        @Override
        public Entry<K, V> pollLastEntry() {
            return this.forward().pollFirstEntry();
        }
        
        @Override
        public NavigableMap<K, V> subMap(final K k, final boolean b, final K i, final boolean b2) {
            return this.forward().subMap(i, b2, k, b).descendingMap();
        }
        
        @Override
        public SortedMap<K, V> subMap(final K k, final K i) {
            return this.subMap(k, true, i, false);
        }
        
        @Override
        public NavigableMap<K, V> tailMap(final K k, final boolean b) {
            return this.forward().headMap(k, b).descendingMap();
        }
        
        @Override
        public SortedMap<K, V> tailMap(final K k) {
            return this.tailMap(k, true);
        }
        
        @Override
        public String toString() {
            return this.standardToString();
        }
        
        @Override
        public Collection<V> values() {
            return (Collection<V>)new Values((Map<Object, Object>)this);
        }
        
        class EntrySetImpl extends EntrySet<K, V>
        {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return DescendingMap.this.entryIterator();
            }
            
            @Override
            Map<K, V> map() {
                return (Map<K, V>)DescendingMap.this;
            }
        }
    }
    
    private enum EntryFunction implements Function<Map.Entry<?, ?>, Object>
    {
        KEY(0) {
            @Override
            public Object apply(final Map.Entry<?, ?> entry) {
                return entry.getKey();
            }
        }, 
        VALUE(1) {
            @Override
            public Object apply(final Map.Entry<?, ?> entry) {
                return entry.getValue();
            }
        };
    }
    
    abstract static class EntrySet<K, V> extends ImprovedAbstractSet<Map.Entry<K, V>>
    {
        @Override
        public void clear() {
            this.map().clear();
        }
        
        @Override
        public boolean contains(Object safeGet) {
            final boolean b = safeGet instanceof Map.Entry;
            boolean b3;
            final boolean b2 = b3 = false;
            if (b) {
                final Map.Entry entry = (Map.Entry)safeGet;
                final Object key = entry.getKey();
                safeGet = Maps.safeGet(this.map(), key);
                b3 = b2;
                if (Objects.equal(safeGet, entry.getValue())) {
                    if (safeGet == null) {
                        b3 = b2;
                        if (!this.map().containsKey(key)) {
                            return b3;
                        }
                    }
                    b3 = true;
                }
            }
            return b3;
        }
        
        @Override
        public boolean isEmpty() {
            return this.map().isEmpty();
        }
        
        abstract Map<K, V> map();
        
        @Override
        public boolean remove(final Object o) {
            return this.contains(o) && this.map().keySet().remove(((Map.Entry)o).getKey());
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            try {
                Preconditions.checkNotNull(collection);
                return super.removeAll(collection);
            }
            catch (UnsupportedOperationException ex) {
                return Sets.removeAllImpl(this, collection.iterator());
            }
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            try {
                Preconditions.checkNotNull(collection);
                return super.retainAll(collection);
            }
            catch (UnsupportedOperationException ex) {
                final HashSet<Object> hashSetWithExpectedSize = Sets.newHashSetWithExpectedSize(collection.size());
                for (final Object next : collection) {
                    if (this.contains(next)) {
                        hashSetWithExpectedSize.add(((Map.Entry<Object, V>)next).getKey());
                    }
                }
                return this.map().keySet().retainAll(hashSetWithExpectedSize);
            }
        }
        
        @Override
        public int size() {
            return this.map().size();
        }
    }
    
    abstract static class IteratorBasedAbstractMap<K, V> extends AbstractMap<K, V>
    {
        @Override
        public void clear() {
            Iterators.clear(this.entryIterator());
        }
        
        abstract Iterator<Entry<K, V>> entryIterator();
        
        @Override
        public Set<Entry<K, V>> entrySet() {
            return (Set<Entry<K, V>>)new EntrySet<K, V>() {
                @Override
                public Iterator<Entry<K, V>> iterator() {
                    return IteratorBasedAbstractMap.this.entryIterator();
                }
                
                @Override
                Map<K, V> map() {
                    return (Map<K, V>)IteratorBasedAbstractMap.this;
                }
            };
        }
    }
    
    static class KeySet<K, V> extends ImprovedAbstractSet<K>
    {
        final Map<K, V> map;
        
        KeySet(final Map<K, V> map) {
            Preconditions.checkNotNull(map);
            this.map = map;
        }
        
        @Override
        public void clear() {
            this.map().clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.map().containsKey(o);
        }
        
        @Override
        public boolean isEmpty() {
            return this.map().isEmpty();
        }
        
        @Override
        public Iterator<K> iterator() {
            return Maps.keyIterator(this.map().entrySet().iterator());
        }
        
        Map<K, V> map() {
            return this.map;
        }
        
        @Override
        public boolean remove(final Object o) {
            if (this.contains(o)) {
                this.map().remove(o);
                return true;
            }
            return false;
        }
        
        @Override
        public int size() {
            return this.map().size();
        }
    }
    
    static class NavigableKeySet<K, V> extends SortedKeySet<K, V> implements NavigableSet<K>
    {
        NavigableKeySet(final NavigableMap<K, V> navigableMap) {
            super(navigableMap);
        }
        
        @Override
        public K ceiling(final K k) {
            return this.map().ceilingKey(k);
        }
        
        @Override
        public Iterator<K> descendingIterator() {
            return this.descendingSet().iterator();
        }
        
        @Override
        public NavigableSet<K> descendingSet() {
            return this.map().descendingKeySet();
        }
        
        @Override
        public K floor(final K k) {
            return this.map().floorKey(k);
        }
        
        @Override
        public NavigableSet<K> headSet(final K k, final boolean b) {
            return this.map().headMap(k, b).navigableKeySet();
        }
        
        @Override
        public SortedSet<K> headSet(final K k) {
            return this.headSet(k, false);
        }
        
        @Override
        public K higher(final K k) {
            return this.map().higherKey(k);
        }
        
        @Override
        public K lower(final K k) {
            return this.map().lowerKey(k);
        }
        
        NavigableMap<K, V> map() {
            return (NavigableMap<K, V>)super.map;
        }
        
        @Override
        public K pollFirst() {
            return Maps.keyOrNull((Map.Entry<K, ?>)this.map().pollFirstEntry());
        }
        
        @Override
        public K pollLast() {
            return Maps.keyOrNull((Map.Entry<K, ?>)this.map().pollLastEntry());
        }
        
        @Override
        public NavigableSet<K> subSet(final K k, final boolean b, final K i, final boolean b2) {
            return this.map().subMap(k, b, i, b2).navigableKeySet();
        }
        
        @Override
        public SortedSet<K> subSet(final K k, final K i) {
            return this.subSet(k, true, i, false);
        }
        
        @Override
        public NavigableSet<K> tailSet(final K k, final boolean b) {
            return this.map().tailMap(k, b).navigableKeySet();
        }
        
        @Override
        public SortedSet<K> tailSet(final K k) {
            return this.tailSet(k, true);
        }
    }
    
    static class SortedKeySet<K, V> extends KeySet<K, V> implements SortedSet<K>
    {
        SortedKeySet(final SortedMap<K, V> sortedMap) {
            super(sortedMap);
        }
        
        @Override
        public Comparator<? super K> comparator() {
            return this.map().comparator();
        }
        
        @Override
        public K first() {
            return this.map().firstKey();
        }
        
        @Override
        public K last() {
            return this.map().lastKey();
        }
        
        abstract SortedMap<K, V> map();
    }
    
    static class Values<K, V> extends AbstractCollection<V>
    {
        final Map<K, V> map;
        
        Values(final Map<K, V> map) {
            Preconditions.checkNotNull(map);
            this.map = map;
        }
        
        @Override
        public void clear() {
            this.map().clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.map().containsValue(o);
        }
        
        @Override
        public boolean isEmpty() {
            return this.map().isEmpty();
        }
        
        @Override
        public Iterator<V> iterator() {
            return Maps.valueIterator(this.map().entrySet().iterator());
        }
        
        final Map<K, V> map() {
            return this.map;
        }
        
        @Override
        public boolean remove(final Object o) {
            try {
                return super.remove(o);
            }
            catch (UnsupportedOperationException ex) {
                for (final Map.Entry<K, V> entry : this.map().entrySet()) {
                    if (Objects.equal(o, entry.getValue())) {
                        this.map().remove(entry.getKey());
                        return true;
                    }
                }
                return false;
            }
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            try {
                Preconditions.checkNotNull(collection);
                return super.removeAll(collection);
            }
            catch (UnsupportedOperationException ex) {
                final HashSet<K> hashSet = Sets.newHashSet();
                for (final Map.Entry<K, V> entry : this.map().entrySet()) {
                    if (collection.contains(entry.getValue())) {
                        hashSet.add((K)entry.getKey());
                    }
                }
                return this.map().keySet().removeAll(hashSet);
            }
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            try {
                Preconditions.checkNotNull(collection);
                return super.retainAll(collection);
            }
            catch (UnsupportedOperationException ex) {
                final HashSet<K> hashSet = Sets.newHashSet();
                for (final Map.Entry<K, V> entry : this.map().entrySet()) {
                    if (collection.contains(entry.getValue())) {
                        hashSet.add((K)entry.getKey());
                    }
                }
                return this.map().keySet().retainAll(hashSet);
            }
        }
        
        @Override
        public int size() {
            return this.map().size();
        }
    }
    
    abstract static class ViewCachingAbstractMap<K, V> extends AbstractMap<K, V>
    {
        private transient Set<Entry<K, V>> entrySet;
        private transient Collection<V> values;
        
        abstract Set<Entry<K, V>> createEntrySet();
        
        Collection<V> createValues() {
            return (Collection<V>)new Values((Map<Object, Object>)this);
        }
        
        @Override
        public Set<Entry<K, V>> entrySet() {
            Set<Entry<K, V>> entrySet;
            if ((entrySet = this.entrySet) == null) {
                entrySet = this.createEntrySet();
                this.entrySet = entrySet;
            }
            return entrySet;
        }
        
        @Override
        public Collection<V> values() {
            Collection<V> values;
            if ((values = this.values) == null) {
                values = this.createValues();
                this.values = values;
            }
            return values;
        }
    }
}
