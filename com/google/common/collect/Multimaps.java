// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.AbstractCollection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.NavigableSet;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import com.google.common.base.Preconditions;
import java.util.Set;
import com.google.common.base.Supplier;
import java.util.Collection;
import java.util.Map;

public final class Multimaps
{
    static boolean equalsImpl(final Multimap<?, ?> multimap, final Object o) {
        return o == multimap || (o instanceof Multimap && multimap.asMap().equals(((Multimap)o).asMap()));
    }
    
    public static <K, V> SetMultimap<K, V> newSetMultimap(final Map<K, Collection<V>> map, final Supplier<? extends Set<V>> supplier) {
        return new CustomSetMultimap<K, V>(map, supplier);
    }
    
    private static class CustomSetMultimap<K, V> extends AbstractSetMultimap<K, V>
    {
        private static final long serialVersionUID = 0L;
        transient Supplier<? extends Set<V>> factory;
        
        CustomSetMultimap(final Map<K, Collection<V>> map, final Supplier<? extends Set<V>> supplier) {
            super(map);
            Preconditions.checkNotNull(supplier);
            this.factory = supplier;
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            this.factory = (Supplier<? extends Set<V>>)objectInputStream.readObject();
            this.setMap((Map<K, Collection<V>>)objectInputStream.readObject());
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            objectOutputStream.writeObject(this.factory);
            objectOutputStream.writeObject(this.backingMap());
        }
        
        @Override
        Map<K, Collection<V>> createAsMap() {
            return this.createMaybeNavigableAsMap();
        }
        
        protected Set<V> createCollection() {
            return (Set<V>)this.factory.get();
        }
        
        @Override
        Set<K> createKeySet() {
            return this.createMaybeNavigableKeySet();
        }
        
        @Override
         <E> Collection<E> unmodifiableCollectionSubclass(final Collection<E> collection) {
            if (collection instanceof NavigableSet) {
                return (Collection<E>)Sets.unmodifiableNavigableSet((NavigableSet<Object>)collection);
            }
            if (collection instanceof SortedSet) {
                return (Collection<E>)Collections.unmodifiableSortedSet((SortedSet<E>)collection);
            }
            return (Collection<E>)Collections.unmodifiableSet((Set<?>)collection);
        }
        
        @Override
        Collection<V> wrapCollection(final K k, final Collection<V> collection) {
            if (collection instanceof NavigableSet) {
                return (Collection<V>)new WrappedNavigableSet((K)k, (NavigableSet<V>)collection, null);
            }
            if (collection instanceof SortedSet) {
                return (Collection<V>)new WrappedSortedSet((K)k, (SortedSet<V>)collection, null);
            }
            return (Collection<V>)new WrappedSet((K)k, (Set<V>)collection);
        }
    }
    
    abstract static class Entries<K, V> extends AbstractCollection<Map.Entry<K, V>>
    {
        @Override
        public void clear() {
            this.multimap().clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (o instanceof Map.Entry) {
                final Map.Entry entry = (Map.Entry)o;
                return this.multimap().containsEntry(entry.getKey(), entry.getValue());
            }
            return false;
        }
        
        abstract Multimap<K, V> multimap();
        
        @Override
        public boolean remove(final Object o) {
            if (o instanceof Map.Entry) {
                final Map.Entry entry = (Map.Entry)o;
                return this.multimap().remove(entry.getKey(), entry.getValue());
            }
            return false;
        }
        
        @Override
        public int size() {
            return this.multimap().size();
        }
    }
}
