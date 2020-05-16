// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Collections;
import java.util.Set;
import java.util.Collection;
import java.util.Map;

abstract class AbstractSetMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements SetMultimap<K, V>
{
    private static final long serialVersionUID = 7431625294878419160L;
    
    protected AbstractSetMultimap(final Map<K, Collection<V>> map) {
        super(map);
    }
    
    @Override
    public Map<K, Collection<V>> asMap() {
        return super.asMap();
    }
    
    @Override
    public Set<Map.Entry<K, V>> entries() {
        return (Set<Map.Entry<K, V>>)(Set)super.entries();
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }
    
    @Override
    public Set<V> get(final K k) {
        return (Set<V>)(Set)super.get(k);
    }
    
    @Override
     <E> Collection<E> unmodifiableCollectionSubclass(final Collection<E> collection) {
        return (Collection<E>)Collections.unmodifiableSet((Set<?>)collection);
    }
    
    @Override
    Collection<V> wrapCollection(final K k, final Collection<V> collection) {
        return (Collection<V>)new WrappedSet((K)k, (Set<V>)collection);
    }
}
