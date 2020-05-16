// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Comparator;
import java.io.IOException;
import java.io.ObjectOutputStream;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.io.Serializable;
import java.util.SortedMap;
import java.util.Set;
import java.util.NavigableSet;
import java.util.NavigableMap;
import java.util.Map;
import java.util.Collection;
import java.util.SortedSet;

final class Synchronized
{
    private static <E> Collection<E> collection(final Collection<E> collection, final Object o) {
        return new SynchronizedCollection<E>((Collection)collection, o);
    }
    
    static <K, V> Map<K, V> map(final Map<K, V> map, final Object o) {
        return new SynchronizedMap<K, V>(map, o);
    }
    
    static <K, V> NavigableMap<K, V> navigableMap(final NavigableMap<K, V> navigableMap, final Object o) {
        return new SynchronizedNavigableMap<K, V>(navigableMap, o);
    }
    
    static <E> NavigableSet<E> navigableSet(final NavigableSet<E> set, final Object o) {
        return new SynchronizedNavigableSet<E>(set, o);
    }
    
    private static <K, V> Map.Entry<K, V> nullableSynchronizedEntry(final Map.Entry<K, V> entry, final Object o) {
        if (entry == null) {
            return null;
        }
        return new SynchronizedEntry<K, V>(entry, o);
    }
    
    static <E> Set<E> set(final Set<E> set, final Object o) {
        return new SynchronizedSet<E>(set, o);
    }
    
    static <K, V> SortedMap<K, V> sortedMap(final SortedMap<K, V> sortedMap, final Object o) {
        return new SynchronizedSortedMap<K, V>(sortedMap, o);
    }
    
    private static <E> SortedSet<E> sortedSet(final SortedSet<E> set, final Object o) {
        return new SynchronizedSortedSet<E>(set, o);
    }
    
    static class SynchronizedBiMap<K, V> extends SynchronizedMap<K, V> implements BiMap<K, V>, Serializable
    {
        private static final long serialVersionUID = 0L;
        private transient Set<V> valueSet;
        
        BiMap<K, V> delegate() {
            return (BiMap<K, V>)(BiMap)super.delegate();
        }
        
        @Override
        public Set<V> values() {
            synchronized (super.mutex) {
                if (this.valueSet == null) {
                    this.valueSet = Synchronized.set(this.delegate().values(), super.mutex);
                }
                return this.valueSet;
            }
        }
    }
    
    static class SynchronizedCollection<E> extends SynchronizedObject implements Collection<E>
    {
        private static final long serialVersionUID = 0L;
        
        private SynchronizedCollection(final Collection<E> collection, final Object o) {
            super(collection, o);
        }
        
        @Override
        public boolean add(final E e) {
            synchronized (super.mutex) {
                return this.delegate().add(e);
            }
        }
        
        @Override
        public boolean addAll(final Collection<? extends E> collection) {
            synchronized (super.mutex) {
                return this.delegate().addAll(collection);
            }
        }
        
        @Override
        public void clear() {
            synchronized (super.mutex) {
                this.delegate().clear();
            }
        }
        
        @Override
        public boolean contains(final Object o) {
            synchronized (super.mutex) {
                return this.delegate().contains(o);
            }
        }
        
        @Override
        public boolean containsAll(final Collection<?> collection) {
            synchronized (super.mutex) {
                return this.delegate().containsAll(collection);
            }
        }
        
        Collection<E> delegate() {
            return (Collection<E>)super.delegate();
        }
        
        @Override
        public boolean isEmpty() {
            synchronized (super.mutex) {
                return this.delegate().isEmpty();
            }
        }
        
        @Override
        public Iterator<E> iterator() {
            return this.delegate().iterator();
        }
        
        @Override
        public boolean remove(final Object o) {
            synchronized (super.mutex) {
                return this.delegate().remove(o);
            }
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            synchronized (super.mutex) {
                return this.delegate().removeAll(collection);
            }
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            synchronized (super.mutex) {
                return this.delegate().retainAll(collection);
            }
        }
        
        @Override
        public int size() {
            synchronized (super.mutex) {
                return this.delegate().size();
            }
        }
        
        @Override
        public Object[] toArray() {
            synchronized (super.mutex) {
                return this.delegate().toArray();
            }
        }
        
        @Override
        public <T> T[] toArray(final T[] array) {
            synchronized (super.mutex) {
                return this.delegate().toArray(array);
            }
        }
    }
    
    private static class SynchronizedEntry<K, V> extends SynchronizedObject implements Entry<K, V>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedEntry(final Entry<K, V> entry, final Object o) {
            super(entry, o);
        }
        
        Entry<K, V> delegate() {
            return (Entry<K, V>)super.delegate();
        }
        
        @Override
        public boolean equals(final Object o) {
            synchronized (super.mutex) {
                return this.delegate().equals(o);
            }
        }
        
        @Override
        public K getKey() {
            synchronized (super.mutex) {
                return this.delegate().getKey();
            }
        }
        
        @Override
        public V getValue() {
            synchronized (super.mutex) {
                return this.delegate().getValue();
            }
        }
        
        @Override
        public int hashCode() {
            synchronized (super.mutex) {
                return this.delegate().hashCode();
            }
        }
        
        @Override
        public V setValue(final V value) {
            synchronized (super.mutex) {
                return this.delegate().setValue(value);
            }
        }
    }
    
    private static class SynchronizedMap<K, V> extends SynchronizedObject implements Map<K, V>
    {
        private static final long serialVersionUID = 0L;
        transient Set<Entry<K, V>> entrySet;
        transient Set<K> keySet;
        transient Collection<V> values;
        
        SynchronizedMap(final Map<K, V> map, final Object o) {
            super(map, o);
        }
        
        @Override
        public void clear() {
            synchronized (super.mutex) {
                this.delegate().clear();
            }
        }
        
        @Override
        public boolean containsKey(final Object o) {
            synchronized (super.mutex) {
                return this.delegate().containsKey(o);
            }
        }
        
        @Override
        public boolean containsValue(final Object o) {
            synchronized (super.mutex) {
                return this.delegate().containsValue(o);
            }
        }
        
        Map<K, V> delegate() {
            return (Map<K, V>)super.delegate();
        }
        
        @Override
        public Set<Entry<K, V>> entrySet() {
            synchronized (super.mutex) {
                if (this.entrySet == null) {
                    this.entrySet = Synchronized.set(this.delegate().entrySet(), super.mutex);
                }
                return this.entrySet;
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            synchronized (super.mutex) {
                return this.delegate().equals(o);
            }
        }
        
        @Override
        public V get(Object value) {
            synchronized (super.mutex) {
                value = this.delegate().get(value);
                return (V)value;
            }
        }
        
        @Override
        public int hashCode() {
            synchronized (super.mutex) {
                return this.delegate().hashCode();
            }
        }
        
        @Override
        public boolean isEmpty() {
            synchronized (super.mutex) {
                return this.delegate().isEmpty();
            }
        }
        
        @Override
        public Set<K> keySet() {
            synchronized (super.mutex) {
                if (this.keySet == null) {
                    this.keySet = Synchronized.set(this.delegate().keySet(), super.mutex);
                }
                return this.keySet;
            }
        }
        
        @Override
        public V put(final K k, final V v) {
            synchronized (super.mutex) {
                return this.delegate().put(k, v);
            }
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends V> map) {
            synchronized (super.mutex) {
                this.delegate().putAll(map);
            }
        }
        
        @Override
        public V remove(Object remove) {
            synchronized (super.mutex) {
                remove = this.delegate().remove(remove);
                return (V)remove;
            }
        }
        
        @Override
        public int size() {
            synchronized (super.mutex) {
                return this.delegate().size();
            }
        }
        
        @Override
        public Collection<V> values() {
            synchronized (super.mutex) {
                if (this.values == null) {
                    this.values = (Collection<V>)collection((Collection<Object>)this.delegate().values(), super.mutex);
                }
                return this.values;
            }
        }
    }
    
    static class SynchronizedNavigableMap<K, V> extends SynchronizedSortedMap<K, V> implements NavigableMap<K, V>
    {
        private static final long serialVersionUID = 0L;
        transient NavigableSet<K> descendingKeySet;
        transient NavigableMap<K, V> descendingMap;
        transient NavigableSet<K> navigableKeySet;
        
        SynchronizedNavigableMap(final NavigableMap<K, V> navigableMap, final Object o) {
            super(navigableMap, o);
        }
        
        @Override
        public Entry<K, V> ceilingEntry(final K k) {
            synchronized (super.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().ceilingEntry(k), super.mutex);
            }
        }
        
        @Override
        public K ceilingKey(final K k) {
            synchronized (super.mutex) {
                return this.delegate().ceilingKey(k);
            }
        }
        
        NavigableMap<K, V> delegate() {
            return (NavigableMap<K, V>)(NavigableMap)super.delegate();
        }
        
        @Override
        public NavigableSet<K> descendingKeySet() {
            synchronized (super.mutex) {
                if (this.descendingKeySet == null) {
                    return this.descendingKeySet = Synchronized.navigableSet(this.delegate().descendingKeySet(), super.mutex);
                }
                return this.descendingKeySet;
            }
        }
        
        @Override
        public NavigableMap<K, V> descendingMap() {
            synchronized (super.mutex) {
                if (this.descendingMap == null) {
                    return this.descendingMap = Synchronized.navigableMap(this.delegate().descendingMap(), super.mutex);
                }
                return this.descendingMap;
            }
        }
        
        @Override
        public Entry<K, V> firstEntry() {
            synchronized (super.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().firstEntry(), super.mutex);
            }
        }
        
        @Override
        public Entry<K, V> floorEntry(final K k) {
            synchronized (super.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().floorEntry(k), super.mutex);
            }
        }
        
        @Override
        public K floorKey(final K k) {
            synchronized (super.mutex) {
                return this.delegate().floorKey(k);
            }
        }
        
        @Override
        public NavigableMap<K, V> headMap(final K k, final boolean b) {
            synchronized (super.mutex) {
                return Synchronized.navigableMap(this.delegate().headMap(k, b), super.mutex);
            }
        }
        
        @Override
        public SortedMap<K, V> headMap(final K k) {
            return this.headMap(k, false);
        }
        
        @Override
        public Entry<K, V> higherEntry(final K k) {
            synchronized (super.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().higherEntry(k), super.mutex);
            }
        }
        
        @Override
        public K higherKey(final K k) {
            synchronized (super.mutex) {
                return this.delegate().higherKey(k);
            }
        }
        
        @Override
        public Set<K> keySet() {
            return this.navigableKeySet();
        }
        
        @Override
        public Entry<K, V> lastEntry() {
            synchronized (super.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().lastEntry(), super.mutex);
            }
        }
        
        @Override
        public Entry<K, V> lowerEntry(final K k) {
            synchronized (super.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().lowerEntry(k), super.mutex);
            }
        }
        
        @Override
        public K lowerKey(final K k) {
            synchronized (super.mutex) {
                return this.delegate().lowerKey(k);
            }
        }
        
        @Override
        public NavigableSet<K> navigableKeySet() {
            synchronized (super.mutex) {
                if (this.navigableKeySet == null) {
                    return this.navigableKeySet = Synchronized.navigableSet(this.delegate().navigableKeySet(), super.mutex);
                }
                return this.navigableKeySet;
            }
        }
        
        @Override
        public Entry<K, V> pollFirstEntry() {
            synchronized (super.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().pollFirstEntry(), super.mutex);
            }
        }
        
        @Override
        public Entry<K, V> pollLastEntry() {
            synchronized (super.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().pollLastEntry(), super.mutex);
            }
        }
        
        @Override
        public NavigableMap<K, V> subMap(final K k, final boolean b, final K i, final boolean b2) {
            synchronized (super.mutex) {
                return Synchronized.navigableMap(this.delegate().subMap(k, b, i, b2), super.mutex);
            }
        }
        
        @Override
        public SortedMap<K, V> subMap(final K k, final K i) {
            return this.subMap(k, true, i, false);
        }
        
        @Override
        public NavigableMap<K, V> tailMap(final K k, final boolean b) {
            synchronized (super.mutex) {
                return Synchronized.navigableMap(this.delegate().tailMap(k, b), super.mutex);
            }
        }
        
        @Override
        public SortedMap<K, V> tailMap(final K k) {
            return this.tailMap(k, true);
        }
    }
    
    static class SynchronizedNavigableSet<E> extends SynchronizedSortedSet<E> implements NavigableSet<E>
    {
        private static final long serialVersionUID = 0L;
        transient NavigableSet<E> descendingSet;
        
        SynchronizedNavigableSet(final NavigableSet<E> set, final Object o) {
            super(set, o);
        }
        
        @Override
        public E ceiling(final E e) {
            synchronized (super.mutex) {
                return this.delegate().ceiling(e);
            }
        }
        
        NavigableSet<E> delegate() {
            return (NavigableSet<E>)(NavigableSet)super.delegate();
        }
        
        @Override
        public Iterator<E> descendingIterator() {
            return this.delegate().descendingIterator();
        }
        
        @Override
        public NavigableSet<E> descendingSet() {
            synchronized (super.mutex) {
                if (this.descendingSet == null) {
                    return this.descendingSet = Synchronized.navigableSet(this.delegate().descendingSet(), super.mutex);
                }
                return this.descendingSet;
            }
        }
        
        @Override
        public E floor(final E e) {
            synchronized (super.mutex) {
                return this.delegate().floor(e);
            }
        }
        
        @Override
        public NavigableSet<E> headSet(final E e, final boolean b) {
            synchronized (super.mutex) {
                return Synchronized.navigableSet(this.delegate().headSet(e, b), super.mutex);
            }
        }
        
        @Override
        public SortedSet<E> headSet(final E e) {
            return this.headSet(e, false);
        }
        
        @Override
        public E higher(final E e) {
            synchronized (super.mutex) {
                return this.delegate().higher(e);
            }
        }
        
        @Override
        public E lower(final E e) {
            synchronized (super.mutex) {
                return this.delegate().lower(e);
            }
        }
        
        @Override
        public E pollFirst() {
            synchronized (super.mutex) {
                return this.delegate().pollFirst();
            }
        }
        
        @Override
        public E pollLast() {
            synchronized (super.mutex) {
                return this.delegate().pollLast();
            }
        }
        
        @Override
        public NavigableSet<E> subSet(final E e, final boolean b, final E e2, final boolean b2) {
            synchronized (super.mutex) {
                return Synchronized.navigableSet(this.delegate().subSet(e, b, e2, b2), super.mutex);
            }
        }
        
        @Override
        public SortedSet<E> subSet(final E e, final E e2) {
            return this.subSet(e, true, e2, false);
        }
        
        @Override
        public NavigableSet<E> tailSet(final E e, final boolean b) {
            synchronized (super.mutex) {
                return Synchronized.navigableSet(this.delegate().tailSet(e, b), super.mutex);
            }
        }
        
        @Override
        public SortedSet<E> tailSet(final E e) {
            return this.tailSet(e, true);
        }
    }
    
    static class SynchronizedObject implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final Object delegate;
        final Object mutex;
        
        SynchronizedObject(Object o, final Object o2) {
            Preconditions.checkNotNull(o);
            this.delegate = o;
            o = o2;
            if (o2 == null) {
                o = this;
            }
            this.mutex = o;
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            synchronized (this.mutex) {
                objectOutputStream.defaultWriteObject();
            }
        }
        
        Object delegate() {
            return this.delegate;
        }
        
        @Override
        public String toString() {
            synchronized (this.mutex) {
                return this.delegate.toString();
            }
        }
    }
    
    static class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedSet(final Set<E> set, final Object o) {
            super((Collection)set, o);
        }
        
        Set<E> delegate() {
            return (Set<E>)(Set)super.delegate();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            synchronized (super.mutex) {
                return this.delegate().equals(o);
            }
        }
        
        @Override
        public int hashCode() {
            synchronized (super.mutex) {
                return this.delegate().hashCode();
            }
        }
    }
    
    static class SynchronizedSortedMap<K, V> extends SynchronizedMap<K, V> implements SortedMap<K, V>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedSortedMap(final SortedMap<K, V> sortedMap, final Object o) {
            super(sortedMap, o);
        }
        
        @Override
        public Comparator<? super K> comparator() {
            synchronized (super.mutex) {
                return this.delegate().comparator();
            }
        }
        
        SortedMap<K, V> delegate() {
            return (SortedMap<K, V>)(SortedMap)super.delegate();
        }
        
        @Override
        public K firstKey() {
            synchronized (super.mutex) {
                return this.delegate().firstKey();
            }
        }
        
        @Override
        public SortedMap<K, V> headMap(final K k) {
            synchronized (super.mutex) {
                return Synchronized.sortedMap(this.delegate().headMap(k), super.mutex);
            }
        }
        
        @Override
        public K lastKey() {
            synchronized (super.mutex) {
                return this.delegate().lastKey();
            }
        }
        
        @Override
        public SortedMap<K, V> subMap(final K k, final K i) {
            synchronized (super.mutex) {
                return Synchronized.sortedMap(this.delegate().subMap(k, i), super.mutex);
            }
        }
        
        @Override
        public SortedMap<K, V> tailMap(final K k) {
            synchronized (super.mutex) {
                return Synchronized.sortedMap(this.delegate().tailMap(k), super.mutex);
            }
        }
    }
    
    static class SynchronizedSortedSet<E> extends SynchronizedSet<E> implements SortedSet<E>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedSortedSet(final SortedSet<E> set, final Object o) {
            super(set, o);
        }
        
        @Override
        public Comparator<? super E> comparator() {
            synchronized (super.mutex) {
                return this.delegate().comparator();
            }
        }
        
        SortedSet<E> delegate() {
            return (SortedSet<E>)(SortedSet)super.delegate();
        }
        
        @Override
        public E first() {
            synchronized (super.mutex) {
                return this.delegate().first();
            }
        }
        
        @Override
        public SortedSet<E> headSet(final E e) {
            synchronized (super.mutex) {
                return (SortedSet<E>)sortedSet((SortedSet<Object>)this.delegate().headSet(e), super.mutex);
            }
        }
        
        @Override
        public E last() {
            synchronized (super.mutex) {
                return this.delegate().last();
            }
        }
        
        @Override
        public SortedSet<E> subSet(final E e, final E e2) {
            synchronized (super.mutex) {
                return (SortedSet<E>)sortedSet((SortedSet<Object>)this.delegate().subSet(e, e2), super.mutex);
            }
        }
        
        @Override
        public SortedSet<E> tailSet(final E e) {
            synchronized (super.mutex) {
                return (SortedSet<E>)sortedSet((SortedSet<Object>)this.delegate().tailSet(e), super.mutex);
            }
        }
    }
}
